package info.arybin.fearnotwords.ui.view.layout;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class SlideLayout extends RelativeLayout {

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
    private Rect slidableRect = new Rect(0, 0, 0, 0);
    //    private int mOffsetMax = 360;
    private boolean scrollBackWhenFinish = false;

    private OnSlideListener mOnSlideListener;


    public SlideLayout(Context context) {
        this(context, null, 0);
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
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
                return slidableRect.left != 0;
            case Right:
                return slidableRect.right != 0;
            case Up:
                return slidableRect.top != 0;
            case Down:
            default:
                return slidableRect.bottom != 0;
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


    public void setSlidableOffset(int left, int right, int top, int bottom) {
        this.slidableRect = new Rect(-left, -top, right, bottom);
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

                    if (slidableRect.contains((int) newX, (int) newY)) {
                        scrollBy((int) -deltaX, (int) -deltaY);
                    } else if (canSlide(Direction.Left) && newX < slidableRect.left) {

                        scrollTo(abs(slidableRect.left), getScrollY());

                    } else if (canSlide(Direction.Right) && newX > slidableRect.right) {
                        scrollTo(-slidableRect.right, getScrollY());
                    } else if (canSlide(Direction.Up) && newY < slidableRect.top) {
                        scrollTo(getScrollX(), abs(slidableRect.top));
                    } else if (canSlide(Direction.Down) && newY > slidableRect.bottom) {
                        scrollTo(getScrollX(), -slidableRect.bottom);
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
                (int) (sqrt(pow(scrolledX, 2) + pow(scrolledY, 2)) * 50));
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        } else {
            if (abs(getScrollX()) == 0 && abs(getScrollY()) == 0 && mState == STATE_SLIDING) {
                if (null != mOnSlideListener) {
                    mOnSlideListener.onSlideToCenter(this);
                    mState = STATE_IDLE;
                }
            }
        }
        if (null != mOnSlideListener && mState == STATE_SLIDING) {
            float rateLeftRight, rateUpDown;
            if (getScrollX() < 0) {
                rateLeftRight = getScrollX() * -1f / slidableRect.right;
            } else {
                rateLeftRight = getScrollX() * 1f / slidableRect.left;
            }
            if (getScrollY() < 0) {
                rateUpDown = getScrollY() * -1f / slidableRect.bottom;
            } else {
                rateUpDown = getScrollY() * 1f / slidableRect.top;
            }

            mOnSlideListener.onSlide(this, rateLeftRight, rateUpDown);
        }

        if (null != mOnSlideListener) {
            if (canSlide(Direction.Left)) {
                if (abs(slidableRect.left) - getScrollX() < 1 && mState == STATE_SLIDING) {
                    mOnSlideListener.onSlideToLeft(this);
                    finishSlide();
                }
            }
            if (canSlide(Direction.Right)) {
                if (slidableRect.right + getScrollX() < 1 && mState == STATE_SLIDING) {
                    mOnSlideListener.onSlideToRight(this);
                    finishSlide();
                }
            }
            if (canSlide(Direction.Up)) {
                if (abs(slidableRect.top) - getScrollY() < 1 && mState == STATE_SLIDING) {
                    mOnSlideListener.onSlideToTop(this);
                    finishSlide();
                }
            }
            if (canSlide(Direction.Down)) {
                if (slidableRect.bottom + getScrollY() < 1 && mState == STATE_SLIDING) {
                    mOnSlideListener.onSlideToBottom(this);
                    finishSlide();
                }
            }
        }

    }


    private void finishSlide() {
        mState = STATE_FINISH;
        if (scrollBackWhenFinish) {
            scrollToCenter();
        } else {
            mOnSlideListener.onSlide(this, 0, 0);
            scrollTo(0, 0);
        }
    }


    public void setOnSlideListener(OnSlideListener onSlideListener) {
        this.mOnSlideListener = onSlideListener;
    }


    public interface OnSlideListener {
        void onSlide(SlideLayout layout, float rateLeftRight, float rateUpDown);

        void onSlideToLeft(SlideLayout layout);

        void onSlideToTop(SlideLayout layout);

        void onSlideToCenter(SlideLayout layout);

        void onSlideToRight(SlideLayout layout);

        void onSlideToBottom(SlideLayout layout);

        void onStartSlide(SlideLayout layout);

        void onFinishSlide(SlideLayout layout);

    }

}
