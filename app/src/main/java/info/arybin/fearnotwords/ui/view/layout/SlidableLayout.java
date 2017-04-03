package info.arybin.fearnotwords.ui.view.layout;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.OvershootInterpolator;
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
    public final int STATE_FINISH = 2;

    private boolean mSlidable = true;


    private final Scroller mScroller;
    private final int mTouchSlop;

    private float mPreviousX;
    private float mPreviousY;
    private int mState = STATE_IDLE;
    private Rect slidableBound = new Rect(0, 0, 0, 0);
    private boolean scrollBackWhenFinish = true;

    private OnSlideListener mOnSlideListener;


    public SlidableLayout(Context context) {
        this(context, null, 0);
    }

    public SlidableLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context, new OvershootInterpolator());
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    public void setScrollBackWhenFinish(boolean scrollBackWhenFinish) {
        this.scrollBackWhenFinish = scrollBackWhenFinish;
    }

    public boolean getScrollBackWhenFinish() {
        return scrollBackWhenFinish;
    }

    public boolean isSlidable() {
        return mSlidable;
    }

    public void setSlidable(boolean slidable) {
        this.mSlidable = slidable;
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
            shouldIntercept = (mPreviousX - currentX >= mTouchSlop);
        }
        if (canSlide(Direction.Right)) {
            shouldIntercept = shouldIntercept || (currentX - mPreviousX >= mTouchSlop);
        }
        if (canSlide(Direction.Up)) {
            shouldIntercept = shouldIntercept || (mPreviousY - currentY >= mTouchSlop);
        }
        if (canSlide(Direction.Down)) {
            shouldIntercept = shouldIntercept || (currentY - mPreviousY >= mTouchSlop);
        }
        return shouldIntercept;
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
        if (!mSlidable) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreviousX = event.getX();
                mPreviousY = event.getY();
                mState = STATE_IDLE;
                if (!mScroller.isFinished()) {
                    mState = STATE_SLIDING;
                    mScroller.abortAnimation();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mState == STATE_IDLE && shouldStartSlide(getX(), getY())) {
                    if (null != mOnSlideListener) {
                        mOnSlideListener.onStartSlide(this);
                    }
                    mState = STATE_SLIDING;
                    mPreviousX = event.getX();
                    mPreviousY = event.getY();
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mSlidable) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreviousX = event.getX();
                mPreviousY = event.getY();
                if (!mScroller.isFinished()) {
                    mState = STATE_SLIDING;
                    mScroller.abortAnimation();
                }
                return true;
            case MotionEvent.ACTION_MOVE:

                if (mState == STATE_IDLE && shouldStartSlide(event.getX(), event.getY())) {
                    if (null != mOnSlideListener) {
                        mOnSlideListener.onStartSlide(this);
                    }
                    mState = STATE_SLIDING;
                    mPreviousX = event.getX();
                    mPreviousY = event.getY();
                    break;
                }
                if (mState == STATE_SLIDING) {

                    float deltaX = event.getX() - mPreviousX;
                    float deltaY = event.getY() - mPreviousY;

                    mPreviousX = event.getX();
                    mPreviousY = event.getY();

                    float newX = deltaX - getScrollX();
                    float newY = deltaY - getScrollY();

                    if (slidableBound.contains((int) newX, (int) newY)) {
                        scrollBy((int) -deltaX, (int) -deltaY);
                    } else if (canSlide(Direction.Left) && newX < slidableBound.left) {
                        notifyIfSlideTo(Direction.Left);
                    } else if (canSlide(Direction.Right) && newX > slidableBound.right) {
                        notifyIfSlideTo(Direction.Right);
                    } else if (canSlide(Direction.Up) && newY < slidableBound.top) {
                        notifyIfSlideTo(Direction.Up);
                    } else if (canSlide(Direction.Down) && newY > slidableBound.bottom) {
                        notifyIfSlideTo(Direction.Down);
                    } else {
                        //adjust deltaX or deltaY
                        if (deltaX > 0) {
                            deltaX *= canSlide(Direction.Right) ? 1 : 0;
                        } else {
                            deltaX *= canSlide(Direction.Left) ? 1 : 0;
                        }
                        if (deltaY < 0) {
                            deltaY *= canSlide(Direction.Up) ? 1 : 0;
                        } else {
                            deltaY *= canSlide(Direction.Down) ? 1 : 0;
                        }
                        scrollBy((int) -deltaX, (int) -deltaY);
                    }

                }
                return true;

            case MotionEvent.ACTION_UP:
                if (null != mOnSlideListener) {
                    mOnSlideListener.onFinishSlide(this);
                }
                scrollToCenter();
                break;
        }
        return super.onTouchEvent(event);
    }

    public void scrollToCenter() {
        int scrolledX = getScrollX();
        int scrolledY = getScrollY();
        mScroller.startScroll(scrolledX, scrolledY, -scrolledX, -scrolledY,
                (int) (sqrt(pow(scrolledX, 2) + pow(scrolledY, 2)) * 4));
        invalidate();
    }

    private void notifyIfSlideTo(Direction direction) {
        if (canSlide(direction)) {
            if (null != mOnSlideListener) {
                boolean shouldNotify = false;
                switch (direction) {
                    case Left:
                        if (getScrollX() > 0) {
                            shouldNotify = abs(-slidableBound.left - getScrollX()) < 10;
                        }
                        break;
                    case Right:
                        if (getScrollX() < 0) {
                            shouldNotify = abs(slidableBound.right + getScrollX()) < 10;
                        }
                        break;
                    case Up:
                        if (getScrollY() > 0) {
                            shouldNotify = abs(-slidableBound.top - getScrollY()) < 10;
                        }
                        break;
                    case Down:
                        if (getScrollY() < 0) {
                            shouldNotify = abs(slidableBound.bottom + getScrollY()) < 10;
                        }
                        break;
                    default:
                        //will never happen
                        shouldNotify = false;
                }
                if (shouldNotify && mState == STATE_SLIDING) {
                    mOnSlideListener.onSlideTo(this, direction);
                    finishSlide();
                }
            }
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }

        if (mState == STATE_SLIDING && abs(getScrollX()) == 0 && abs(getScrollY()) == 0) {
            mOnSlideListener.onSlideCancel(this);
            mState = STATE_FINISH;
        }

        if (null != mOnSlideListener && mState == STATE_SLIDING) {
            float rateLeftRight, rateUpDown;
            if (getScrollX() < 0) {
                rateLeftRight = getScrollX() * -1f / slidableBound.right;
            } else {
                rateLeftRight = getScrollX() * 1f / slidableBound.left;
            }
            if (getScrollY() < 0) {
                rateUpDown = getScrollY() * -1f / slidableBound.bottom;
            } else {
                rateUpDown = getScrollY() * 1f / slidableBound.top;
            }

            mOnSlideListener.onSlide(this, rateLeftRight, rateUpDown);
        }

        for (Direction direction : Direction.values()) {
            notifyIfSlideTo(direction);
        }

    }


    private void finishSlide() {
        mState = STATE_FINISH;
        if (scrollBackWhenFinish) {
            scrollToCenter();
        } else {
            scrollTo(0, 0);
        }
    }


    public void setOnSlideListener(OnSlideListener onSlideListener) {
        this.mOnSlideListener = onSlideListener;
    }


    public interface OnSlideListener {
        void onSlide(SlidableLayout layout, float rateLeftRight, float rateUpDown);

        void onSlideTo(SlidableLayout layout, Direction direction);

        void onSlideCancel(SlidableLayout layout);

        void onStartSlide(SlidableLayout layout);

        void onFinishSlide(SlidableLayout layout);

    }

}
