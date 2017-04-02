package info.arybin.fearnotwords.ui.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import static info.arybin.fearnotwords.Utils.isPointInsideView;

/**
 * Note: NOT THREAD SAFE(DO NOT CHANGE OBSERVERS IN OTHER THREAD)
 */
public class ObservableLayout extends RelativeLayout {

    private static final int STATE_IDLE = 0;
    private static final int STATE_PRESSED = 1;
    private final int touchSlop;


    private EventListener listener;

    private boolean locked = false;
    private ArrayList<View> onPressObservers = new ArrayList<>();
    private ArrayList<View> onHoverObservers = new ArrayList<>();
    private View currentPressedView;
    private Stack<View> currentHoveredStack = new Stack<>();
    private int state = STATE_IDLE;

    private float previousX;
    private float previousY;

    private float anchorX;
    private float anchorY;


    public ObservableLayout(Context context) {
        this(context, null);
    }

    public ObservableLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ObservableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (locked) {
            return true;
        }

        if (null != listener && onPressObservers.size() != 0) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (STATE_IDLE == state) {
                        notifyOnPress(event);
                        notifyHoverIn(event);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (STATE_PRESSED == state) {
                        float deltaX = event.getX() - previousX;
                        float deltaY = event.getY() - previousY;
                        if (Math.abs(deltaX) > touchSlop || Math.abs(deltaY) > touchSlop) {
                            return true;
                        }
                    }

                    break;

                case MotionEvent.ACTION_UP:
                    onTouchEvent(event);
                    break;
            }


        }


        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (locked) {
            return true;
        }

        if (null != listener && onPressObservers.size() != 0) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (STATE_PRESSED == state) {
                        notifyHoverIn(event);
                        notifyHoverOut(event);
                        float currentX = event.getX();
                        float currentY = event.getY();
                        listener.onPressMove(currentPressedView,
                                Math.sqrt(Math.pow(currentX - previousX, 2) + Math.pow(currentY - previousY, 2)),
                                Math.sqrt(Math.pow(currentX - anchorX, 2) + Math.pow(currentY - anchorY, 2)), event);
                        previousX = currentX;
                        previousY = currentY;
                        return true;
                    }

                case MotionEvent.ACTION_UP:
                    if (STATE_PRESSED == state) {
                        View hoveredView;
                        while (currentHoveredStack.size() > 0) {
                            hoveredView = currentHoveredStack.pop();
                            if (null == hoveredView || listener.onHoverCancel(currentPressedView, hoveredView, event)) {
                                break;
                            }
                        }
                        this.listener.onPressUp(currentPressedView, event);
                        state = STATE_IDLE;
                        return true;
                    }

                default:
                    break;
            }


        }
        return super.onTouchEvent(event);
    }

    private void notifyOnPress(MotionEvent event) {
        for (View observer : onPressObservers) {
            if (isPointInsideView(event.getRawX(), event.getRawY(), observer)) {
                recordPosition(event);
                switchToStatePressed(observer);
                listener.onPressDown(observer, event);
            }
        }
    }

    private void notifyHoverIn(MotionEvent event) {
        for (View observer : onHoverObservers) {
            if (isPointInsideView(event.getRawX(), event.getRawY(), observer)) {
                if (!currentHoveredStack.contains(observer)) {
                    currentHoveredStack.push(observer);
                    this.listener.onHoverIn(currentPressedView, observer, event);
                }
            }
        }
    }

    private void notifyHoverOut(MotionEvent event) {
        for (View hoveredView : currentHoveredStack) {
            if (!isPointInsideView(event.getRawX(), event.getRawY(), hoveredView)) {
                currentHoveredStack.remove(hoveredView);
                this.listener.onHoverOut(currentPressedView, hoveredView, event);
            }
        }
    }


    private void recordPosition(MotionEvent event) {
        previousX = event.getX();
        previousY = event.getY();
    }

    private void switchToStatePressed(View view) {
        state = STATE_PRESSED;
        currentPressedView = view;
    }

    /**
     * Should only be used in callbacks of EventListener.
     */
    public void anchor(float xInLayout, float yInLayout) {
        this.anchorX = previousX;
        this.anchorY = previousY;
    }


    public void lock() {
        this.locked = true;
    }

    public void unlock() {
        this.locked = false;
    }


    public void addOnPressObserver(View... views) {
        this.onPressObservers.addAll(Arrays.asList(views));
    }

    public void removeOnPressObserver(View view) {
        this.onPressObservers.remove(view);
    }

    public ArrayList<View> getOnPressObservers() {
        return onPressObservers;
    }

    public void addOnHoverObserver(View... views) {
        this.onHoverObservers.addAll(Arrays.asList(views));
    }

    public void removeOnHoverObserver(View view) {
        this.onHoverObservers.remove(view);
    }

    public ArrayList<View> getOnHoverObservers() {
        return onHoverObservers;
    }


    public void setEventListener(EventListener listener) {
        this.listener = listener;
    }


    public interface EventListener {

        void onPressDown(View view, MotionEvent event);

        void onPressMove(View view, double distance2LastPos, double distance2AnchorPos, MotionEvent event);

        void onPressUp(View pressDownView, MotionEvent event);

        void onHoverIn(View pressDownView, View viewOnHover, MotionEvent event);

        void onHoverOut(View pressDownView, View viewOnHover, MotionEvent event);

        /**
         * @return true to notify other hovered views(order LIFO)
         */
        boolean onHoverCancel(View pressDownView, View viewOnHover, MotionEvent event);
    }

}
