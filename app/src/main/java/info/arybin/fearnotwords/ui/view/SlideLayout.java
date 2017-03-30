package info.arybin.fearnotwords.ui.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class SlideLayout extends RelativeLayout {

    public final int STATE_IDLE = 0;
    public final int STATE_SLIDING = 1;
    public final int STATE_FINISH = 2;

    private boolean mSlidable = true;


    private final Scroller mScroller;
    private final int mTouchSlop;

    private float mPreviousX;
    private int mState = STATE_IDLE;
    private int mOffsetMax = 360;
    private boolean ignoreLeft = false;
    private boolean ignoreRight = false;

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


    public void setIgnoreLeft(boolean ignoreLeft) {
        this.ignoreLeft = ignoreLeft;
    }

    public void setIgnoreRight(boolean ignoreRight) {
        this.ignoreRight = ignoreRight;
    }

    public void setMaxOffset(int offsetMax) {
        this.mOffsetMax = offsetMax;
    }

    public int getMaxOffset() {
        return mOffsetMax;
    }

    public boolean isSlidable() {
        return mSlidable;
    }

    public void setSlidable(boolean slidable) {
        this.mSlidable = slidable;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!mSlidable) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreviousX = event.getX();
                mState = STATE_IDLE;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getX() - mPreviousX;

                if (mState == STATE_IDLE && Math.abs(deltaX) >= mTouchSlop) {
                    if (null != mOnSlideListener) {
                        mOnSlideListener.onStartSlide(this);
                    }
                    mState = STATE_SLIDING;
                    mPreviousX = event.getX();
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
                mState = STATE_IDLE;
                if (!mScroller.isFinished()) {
                    mState = STATE_SLIDING;
                    mScroller.abortAnimation();
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getX() - mPreviousX;
                if (mState == STATE_IDLE && Math.abs(deltaX) > mTouchSlop) {
                    if (null != mOnSlideListener) {
                        mOnSlideListener.onStartSlide(this);
                    }
                    mState = STATE_SLIDING;
                    mPreviousX = event.getX();
                }
                if (mState == STATE_SLIDING) {
                    deltaX *= 1f;
                    mPreviousX = event.getX();
                    float newX = getScrollX() - deltaX;

                    if (Math.abs(newX) > mOffsetMax) {
                        scrollTo(mOffsetMax * (newX > 0 ? 1 : -1), 0);
                    } else {
                        scrollBy((int) -deltaX, 0);
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
        mScroller.startScroll(scrolledX, 0, -scrolledX, 0, Math.abs(scrolledX) * 5);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        } else {
            if (Math.abs(getScrollX()) == 0 && mState == STATE_SLIDING) {
                if (null != mOnSlideListener) {
                    mOnSlideListener.onSlideToCenter(this);
                    mState = STATE_IDLE;
                }
            }
        }

        if (null != mOnSlideListener && mState == STATE_SLIDING) {
            mOnSlideListener.onSlide(this, getScrollX() * -1f / mOffsetMax);
        }

        if (Math.abs(Math.abs(getScrollX()) - mOffsetMax) < 1) {
            if (null != mOnSlideListener) {
                if (getScrollX() > 0) {
                    if (!ignoreLeft) {
                        mOnSlideListener.onSlideToLeft(this);
                        finishSlide();
                    }
                } else {
                    if (!ignoreRight) {
                        mOnSlideListener.onSlideToRight(this);
                        finishSlide();
                    }
                }
            }
        }
    }


    private void finishSlide() {
        mState = STATE_FINISH;
        scrollTo(0, 0);
    }


    public void setOnSlideListener(OnSlideListener onSlideListener) {
        this.mOnSlideListener = onSlideListener;
    }


    public interface OnSlideListener {
        void onSlide(SlideLayout layout, float rate);

        void onSlideToLeft(SlideLayout layout);

        void onSlideToCenter(SlideLayout layout);

        void onSlideToRight(SlideLayout layout);

        void onStartSlide(SlideLayout layout);

        void onFinishSlide(SlideLayout layout);

    }

}
