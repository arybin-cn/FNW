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
    private boolean consumeMotion = true;

    private ArrayList<View> onPressObservers = new ArrayList<>();
    private ArrayList<View> onHoverObservers = new ArrayList<>();
    private View currentPressedView;
    private Stack<View> currentHoveredStack = new Stack<>();
    private int state = STATE_IDLE;


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
        if (null != listener & !locked) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (STATE_IDLE == state) {
                        if (anyPressObserversIn(event)) {
                            notifyOnPress(event);
                        } else {
                            listener.onPressDown(this, event);
                            switchToStatePressed(this);
                        }
                        notifyHoverIn(event);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (STATE_PRESSED == state) {
                        notifyHoverIn(event);
                        notifyHoverOut(event);
                        listener.onPressMove(currentPressedView, event);
                        return false;
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
        if (null != listener & !locked) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return consumeMotion;

                case MotionEvent.ACTION_MOVE:
                    if (STATE_PRESSED == state) {
                        notifyHoverIn(event);
                        notifyHoverOut(event);
                        listener.onPressMove(currentPressedView, event);
                        return consumeMotion;
                    }

                case MotionEvent.ACTION_UP:
                    if (STATE_PRESSED == state) {
                        View hoveredView;
                        while (currentHoveredStack.size() > 0) {
                            hoveredView = currentHoveredStack.pop();
                            if (null == hoveredView || !listener.onHoverCancel(currentPressedView, hoveredView, event)) {
                                currentHoveredStack.clear();
                                break;
                            }
                        }
                        this.listener.onPressUp(currentPressedView, event);
                        state = STATE_IDLE;
                    }

                default:
                    break;
            }


        }
        return super.onTouchEvent(event);
    }

    private boolean anyPressObserversIn(MotionEvent event) {
        for (View observer : onPressObservers) {
            if (isPointInsideView(event.getRawX(), event.getRawY(), observer)) {
                return true;
            }
        }
        return false;
    }


    private void notifyOnPress(MotionEvent event) {
        for (View observer : onPressObservers) {
            if (isPointInsideView(event.getRawX(), event.getRawY(), observer)) {
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




    private void switchToStatePressed(View view) {
        state = STATE_PRESSED;
        currentPressedView = view;
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

        void onPressDown(View pressDownView, MotionEvent event);

        void onPressMove(View pressDownView, MotionEvent event);

        void onPressUp(View pressDownView, MotionEvent event);

        void onHoverIn(View pressDownView, View viewOnHover, MotionEvent event);

        void onHoverOut(View pressDownView, View viewOnHover, MotionEvent event);

        /**
         * @return true to notify other hovered views(order LIFO)
         */
        boolean onHoverCancel(View pressDownView, View viewOnHover, MotionEvent event);
    }

}
