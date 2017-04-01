package info.arybin.fearnotwords.ui.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static info.arybin.fearnotwords.Utils.isPointInsideView;

/**
 * Note: NOT THREAD SAFE(DO NOT CHANGE OBSERVERS IN OTHER THREAD)
 */
public class ObservableLayout extends RelativeLayout {

    private static final int STATE_IDLE = 0;
    private static final int STATE_PRESSED = 1;


    private EventListener listener;

    private boolean locked = false;
    private ArrayList<View> onPressObservers = new ArrayList<>();
    private ArrayList<View> onHoverObservers = new ArrayList<>();
    private View currentPressedView;
    private int state = STATE_IDLE;

    private float previousX;
    private float previousY;

    private float anchorX;
    private float anchorY;


    public ObservableLayout(Context context) {
        super(context);
    }

    public ObservableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return locked || super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (locked) {
            return true;
        }
        if (null != listener && onPressObservers.size() != 0) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (STATE_IDLE == state) {
                        for (View observer : onPressObservers) {
                            if (isPointInsideView(event.getRawX(), event.getRawY(), observer)) {
                                listener.onPressDown(observer);
                                switchToStatePressed(observer, event);
                                return true;
                            }

                        }

                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (STATE_PRESSED == state) {
                        for (View observer : onHoverObservers) {
                            if (isPointInsideView(event.getRawX(), event.getRawY(), observer)) {
                                listener.onHover(currentPressedView, observer);
                            }

                        }

                        float currentX = event.getX();
                        float currentY = event.getY();
                        listener.onPressMove(currentPressedView,
                                Math.sqrt(Math.pow(currentX - previousX, 2) + Math.pow(currentY - previousY, 2)),
                                Math.sqrt(Math.pow(currentX - anchorX, 2) + Math.pow(currentY - anchorY, 2)));

                        previousX = currentX;
                        previousY = currentY;
                        return true;
                    }

                case MotionEvent.ACTION_UP:
                    if (STATE_PRESSED == state) {
                        this.listener.onPressUp(currentPressedView, event.getRawX(), event.getRawY());
                        state = STATE_IDLE;
                        return true;
                    }

                default:
                    break;
            }


        }
        return super.onTouchEvent(event);
    }

    private void switchToStatePressed(View view, MotionEvent event) {
        state = STATE_PRESSED;
        currentPressedView = view;
        previousX = event.getX();
        previousY = event.getY();
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
        void onPressDown(View view);

        void onPressMove(View view, double distance2LastPos, double distance2AnchorPos);

        void onPressUp(View pressDownView, float xInScreen, float yInScreen);

        void onHover(View pressDownView, View viewOnHover);

    }

}
