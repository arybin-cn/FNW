package info.arybin.fearnotwords.ui.view.layout;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class ElasticLayout extends RelativeLayout {

    public enum Direction {
        Up, Down, Left, Right
    }

    private final int touchSlop;

    public final int STATE_IDLE = 0;
    public final int STATE_SLIDING = 1;
    public final int STATE_CANCEL = 2;
    public final int STATE_FINISH = 3;
    private int state = STATE_IDLE;

    private boolean slidable = true;
    private boolean consumeMotion = true;
    private boolean cancelWhenSlideBack = false;
    private boolean scrollBackWhenFinish = true;

    private float speed;
    private Scroller scroller;
    private Rect slidableBound = new Rect(0, 0, 0, 0);

    private OnSlideListener onSlideListener;
    private DistanceInterpolator distanceInterpolator;

    private float previousX;
    private float previousY;

    public ElasticLayout(Context context) {
        this(context, null, 0);
    }

    public ElasticLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ElasticLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        speed = 150;
    }

    public boolean willCancelWhenSlideBack() {
        return cancelWhenSlideBack;
    }

    public void setCancelWhenSlideBack(boolean cancelWhenSlideBack) {
        this.cancelWhenSlideBack = cancelWhenSlideBack;
    }

    public void setScrollBackWhenFinish(boolean scrollBackWhenFinish) {
        this.scrollBackWhenFinish = scrollBackWhenFinish;
    }

    public boolean willScrollBackWhenFinish() {
        return scrollBackWhenFinish;
    }

    public boolean isSlidable() {
        return slidable;
    }

    public void setSlidable(boolean slidable) {
        this.slidable = slidable;
    }

    public boolean willConsumeMotion() {
        return consumeMotion;
    }

    public void setConsumeMotion(boolean consumeMotion) {
        this.consumeMotion = consumeMotion;
    }

    public void setSlidableOffset(int leftOffset, int rightOffset, int upOffset, int downOffset) {
        this.slidableBound = new Rect(-leftOffset, -upOffset, rightOffset, downOffset);
    }

    public void setSlidableOffsetLeftRight(int offset) {
        setSlidableOffset(offset, 0, offset, 0);
    }

    public void setSlidableOffsetUpDown(int offset) {
        setSlidableOffset(0, offset, 0, offset);
    }

    public void setSlidableOffsetLeft(int offset) {
        setSlidableOffset(offset, 0, 0, 0);
    }

    public void setSlidableOffsetRight(int offset) {
        setSlidableOffset(offset, 0, 0, 0);
    }

    public void setSlidableOffsetUp(int offset) {
        setSlidableOffset(0, 0, offset, 0);
    }

    public void setSlidableOffsetDown(int offset) {
        setSlidableOffset(0, 0, 0, offset);
    }


    /**
     * @param speed pixels per second(default 150)
     */
    public boolean setSpeed(float speed) {
        if (speed > 0) {
            this.speed = speed;
            return true;
        }
        return false;
    }

    public float getSpeed() {
        return speed;
    }

    public void scrollToCenter() {
        int scrolledX = getScrollX();
        int scrolledY = getScrollY();
        scroller.startScroll(scrolledX, scrolledY, -scrolledX, -scrolledY,
                (int) (sqrt(pow(scrolledX, 2) + pow(scrolledY, 2)) * 1000 / speed));
        invalidate();
    }

    public void cancelSlide() {
        if (STATE_CANCEL != state) {
            state = STATE_CANCEL;
            scrollToCenter();
            if (null != onSlideListener) {
                onSlideListener.onCancelSlide(this);
            }
        }
    }

    public void setInterpolator(Interpolator interpolator) {
        scroller = new Scroller(getContext(), interpolator);
    }

    public void setOnSlideListener(OnSlideListener onSlideListener) {
        this.onSlideListener = onSlideListener;
    }

    public DistanceInterpolator getDistanceInterpolator() {
        return distanceInterpolator;
    }

    public void setDistanceInterpolator(DistanceInterpolator distanceInterpolator) {
        this.distanceInterpolator = distanceInterpolator;
    }

    private boolean canSlide(Direction direction) {
        switch (direction) {
            case Left:
                return slidableBound.left != 0;
            case Right:
                return slidableBound.right != 0;
            case Up:
                return slidableBound.top != 0;
            case Down:
                return slidableBound.bottom != 0;
            default:
                return false;

        }
    }

    private boolean shouldStartSlide(float currentX, float currentY) {
        boolean shouldIntercept = false;
        if (canSlide(Direction.Left)) {
            shouldIntercept = (previousX - currentX >= touchSlop);
        }
        if (canSlide(Direction.Right)) {
            shouldIntercept = shouldIntercept || (currentX - previousX >= touchSlop);
        }
        if (canSlide(Direction.Up)) {
            shouldIntercept = shouldIntercept || (previousY - currentY >= touchSlop);
        }
        if (canSlide(Direction.Down)) {
            shouldIntercept = shouldIntercept || (currentY - previousY >= touchSlop);
        }
        return shouldIntercept;
    }

    private Float[] adjustToSlidableBound(float x, float y) {
        if (x < slidableBound.left) {
            x = slidableBound.left;
        }
        if (x > slidableBound.right) {
            x = slidableBound.right;
        }
        if (y < slidableBound.top) {
            y = slidableBound.top;
        }
        if (y > slidableBound.bottom) {
            y = slidableBound.bottom;
        }
        return new Float[]{x, y};
    }

    private boolean notifyIfSlideTo(Direction direction) {
        if (canSlide(direction)) {
            boolean shouldNotify = false;
            switch (direction) {
                case Left:
                    if (getScrollX() > 1) {
                        shouldNotify = abs(-slidableBound.left - getScrollX()) < 3;
                    }
                    break;
                case Right:
                    if (getScrollX() < -1) {
                        shouldNotify = abs(slidableBound.right + getScrollX()) < 3;
                    }
                    break;
                case Up:
                    if (getScrollY() > 1) {
                        shouldNotify = abs(-slidableBound.top - getScrollY()) < 3;
                    }
                    break;
                case Down:
                    if (getScrollY() < -1) {
                        shouldNotify = abs(slidableBound.bottom + getScrollY()) < 3;
                    }
                    break;
                default:
                    //will never happen
                    shouldNotify = false;
            }
            if (shouldNotify && state == STATE_SLIDING) {
                if (null != onSlideListener) {
                    onSlideListener.onSlideTo(this, direction);
                }
                finishSlide();
                return true;
            }

        }
        return false;
    }

    private void finishSlide() {
        state = STATE_FINISH;
        if (scrollBackWhenFinish) {
            scrollToCenter();
        } else {
            scrollTo(0, 0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!slidable) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                previousX = event.getX();
                previousY = event.getY();
                state = STATE_IDLE;
                if (!scroller.isFinished()) {
                    state = STATE_SLIDING;
                    scroller.abortAnimation();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (state == STATE_IDLE && shouldStartSlide(getX(), getY())) {
                    if (null != onSlideListener) {
                        onSlideListener.onStartSlide(this);
                    }
                    state = STATE_SLIDING;
                    previousX = event.getX();
                    previousY = event.getY();
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!slidable) {
            return consumeMotion;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                previousX = event.getX();
                previousY = event.getY();
                if (!scroller.isFinished()) {
                    state = STATE_SLIDING;
                    scroller.abortAnimation();
                }
                return consumeMotion;
            case MotionEvent.ACTION_MOVE:

                if (state == STATE_IDLE && shouldStartSlide(event.getX(), event.getY())) {
                    if (null != onSlideListener) {
                        onSlideListener.onStartSlide(this);
                    }
                    state = STATE_SLIDING;
                    previousX = event.getX();
                    previousY = event.getY();
                    break;
                }

                if (state == STATE_SLIDING) {

                    float oldScrollX = -getScrollX();
                    float oldScrollY = -getScrollY();


                    float deltaX = event.getX() - previousX;
                    float deltaY = event.getY() - previousY;


                    if (abs(oldScrollX) > abs(oldScrollY)) {
                        deltaY = 0;
                        if (cancelWhenSlideBack) {
                            if (deltaX < -1 && oldScrollX > 0) {
                                cancelSlide();
                            }
                            if (deltaX > 1 && oldScrollX < 0) {
                                cancelSlide();
                            }

                        }
                    } else if (abs(oldScrollX) < abs(oldScrollY)) {
                        deltaX = 0;
                        if (cancelWhenSlideBack) {
                            if (deltaY < -1 && oldScrollY > 0) {
                                cancelSlide();
                            }
                            if (deltaY > 1 && oldScrollY < 0) {
                                cancelSlide();
                            }
                        }
                    } else {
                        boolean shouldMoveInXY = abs(deltaX) > abs(deltaY);
                        deltaX *= shouldMoveInXY ? 1 : 0;
                        deltaY *= shouldMoveInXY ? 0 : 1;
                    }


                    if (null != distanceInterpolator) {
                        deltaX = distanceInterpolator.interpolate(deltaX);
                        deltaY = distanceInterpolator.interpolate(deltaY);
                    }

                    previousX = event.getX();
                    previousY = event.getY();


                    float newScrollX = deltaX + oldScrollX;
                    float newScrollY = deltaY + oldScrollY;

                    boolean notified = false;
                    for (Direction direction : Direction.values()) {
                        notified = notifyIfSlideTo(direction);
                        if (notified) {
                            break;
                        }
                    }
                    if (!notified) {
                        Float[] adjustedPoint = adjustToSlidableBound(newScrollX, newScrollY);
                        scrollTo((int) -adjustedPoint[0], (int) -adjustedPoint[1]);
                    }


                }
                return consumeMotion;

            case MotionEvent.ACTION_UP:
                cancelSlide();
                break;
        }
        return super.onTouchEvent(event);
    }


    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }

        if (state == STATE_CANCEL && abs(getScrollX()) == 0 && abs(getScrollY()) == 0) {
            if (null != onSlideListener) {
                onSlideListener.onSlideCanceled(this);
            }
            state = STATE_FINISH;
        }

        if (state == STATE_SLIDING || state == STATE_CANCEL) {
            float rateLeftRight = 0, rateUpDown = 0;
            if (getScrollX() < 0 && slidableBound.right != 0) {
                rateLeftRight = getScrollX() * -1f / slidableBound.right;
            } else if (getScrollX() >= 0 && slidableBound.left != 0) {
                rateLeftRight = getScrollX() * 1f / slidableBound.left;
            }
            if (getScrollY() < 0 && slidableBound.bottom != 0) {
                rateUpDown = getScrollY() * -1f / slidableBound.bottom;
            } else if (getScrollY() >= 0 && slidableBound.top != 0) {
                rateUpDown = getScrollY() * 1f / slidableBound.top;
            }

            if (null != onSlideListener) {
                onSlideListener.onSlide(this, rateLeftRight, rateUpDown);
            }
        }

        for (Direction direction : Direction.values()) {
            notifyIfSlideTo(direction);
        }

    }


    public interface OnSlideListener {
        void onSlide(ElasticLayout layout, float rateLeftRight, float rateUpDown);

        void onSlideTo(ElasticLayout layout, Direction direction);

        void onSlideCanceled(ElasticLayout layout);

        void onStartSlide(ElasticLayout layout);

        void onCancelSlide(ElasticLayout layout);
    }

    public interface DistanceInterpolator {
        float interpolate(float offset);
    }

}
