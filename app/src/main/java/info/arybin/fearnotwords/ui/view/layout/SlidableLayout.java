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

public class SlidableLayout extends RelativeLayout {

    public enum Direction {
        Up, Down, Left, Right
    }

    public final int STATE_IDLE = 0;
    public final int STATE_SLIDING = 1;
    public final int STATE_CANCEL = 2;
    public final int STATE_FINISH = 3;

    private boolean slidable = true;
    private boolean consumeMotion = true;


    private Scroller scroller;
    private final int touchSlop;

    private float previousX;
    private float previousY;
    private int state = STATE_IDLE;
    private Rect slidableBound = new Rect(0, 0, 0, 0);
    private boolean scrollBackWhenFinish = true;

    private OnSlideListener onSlideListener;


    public SlidableLayout(Context context) {
        this(context, null, 0);
    }

    public SlidableLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    public void setScrollBackWhenFinish(boolean scrollBackWhenFinish) {
        this.scrollBackWhenFinish = scrollBackWhenFinish;
    }

    public boolean getScrollBackWhenFinish() {
        return scrollBackWhenFinish;
    }

    public boolean isSlidable() {
        return slidable;
    }

    public void setSlidable(boolean slidable) {
        this.slidable = slidable;
    }

    public boolean isConsumeMotion() {
        return consumeMotion;
    }

    public void setConsumeMotion(boolean consumeMotion) {
        this.consumeMotion = consumeMotion;
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
            default:
                return slidableBound.bottom != 0;
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


    public void setSlidableOffset(int leftOffset, int rightOffset, int topOffset, int bottomOffset) {
        this.slidableBound = new Rect(-leftOffset, -topOffset, rightOffset, bottomOffset);
    }

    public void setSlidableOffsetLeftRight(int offset) {
        this.slidableBound = new Rect(-offset, 0, offset, 0);
    }

    public void setSlidableOffsetUpDown(int offset) {
        this.slidableBound = new Rect(0, -offset, 0, offset);
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

                    float deltaX = event.getX() - previousX;
                    float deltaY = event.getY() - previousY;

                    previousX = event.getX();
                    previousY = event.getY();

                    float newScrollX = deltaX - getScrollX();
                    float newScrollY = deltaY - getScrollY();

                    if (slidableBound.contains((int) newScrollX, (int) newScrollY)) {
                        scrollTo((int) -newScrollX, (int) -newScrollY);
                    } else if (canSlide(Direction.Left) && newScrollX < slidableBound.left) {
                        notifyIfSlideTo(Direction.Left);
                    } else if (canSlide(Direction.Right) && newScrollX > slidableBound.right) {
                        notifyIfSlideTo(Direction.Right);
                    } else if (canSlide(Direction.Up) && newScrollY < slidableBound.top) {
                        notifyIfSlideTo(Direction.Up);
                    } else if (canSlide(Direction.Down) && newScrollY > slidableBound.bottom) {
                        notifyIfSlideTo(Direction.Down);
                    } else {
                        Float[] adjustedPoint = adjustToSlidableBound(newScrollX, newScrollY);
                        scrollTo((int) -adjustedPoint[0], (int) -adjustedPoint[1]);
                    }

                }
                return consumeMotion;

            case MotionEvent.ACTION_UP:
                if (null != onSlideListener) {
                    onSlideListener.onFinishSlide(this);
                }
                state = STATE_CANCEL;
                scrollToCenter();
                break;
        }
        return super.onTouchEvent(event);
    }


    public void scrollToCenter() {
        int scrolledX = getScrollX();
        int scrolledY = getScrollY();
        scroller.startScroll(scrolledX, scrolledY, -scrolledX, -scrolledY,
                (int) (sqrt(pow(scrolledX, 2) + pow(scrolledY, 2)) * 4));
        invalidate();
    }

    private void notifyIfSlideTo(Direction direction) {
        if (canSlide(direction)) {

            boolean shouldNotify = false;
            switch (direction) {
                case Left:
                    if (getScrollX() > 0) {
                        shouldNotify = abs(-slidableBound.left - getScrollX()) < 3;
                    }
                    break;
                case Right:
                    if (getScrollX() < 0) {
                        shouldNotify = abs(slidableBound.right + getScrollX()) < 3;
                    }
                    break;
                case Up:
                    if (getScrollY() > 0) {
                        shouldNotify = abs(-slidableBound.top - getScrollY()) < 3;
                    }
                    break;
                case Down:
                    if (getScrollY() < 0) {
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
            }

        }
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }

        if (state == STATE_CANCEL && abs(getScrollX()) == 0 && abs(getScrollY()) == 0) {
            if (null != onSlideListener) {
                onSlideListener.onSlideCancel(this);
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


    public void cancelSlide() {
        state = STATE_CANCEL;
        scrollToCenter();
    }


    private void finishSlide() {
        state = STATE_FINISH;
        if (scrollBackWhenFinish) {
            scrollToCenter();
        } else {
            scrollTo(0, 0);
        }
    }

    public void setInterpolator(Interpolator interpolator) {
        scroller = new Scroller(getContext(), interpolator);
    }


    public void setOnSlideListener(OnSlideListener onSlideListener) {
        this.onSlideListener = onSlideListener;
    }


    public interface OnSlideListener {
        void onSlide(SlidableLayout layout, float rateLeftRight, float rateUpDown);

        void onSlideTo(SlidableLayout layout, Direction direction);

        void onSlideCancel(SlidableLayout layout);

        void onStartSlide(SlidableLayout layout);

        void onFinishSlide(SlidableLayout layout);
    }


}
