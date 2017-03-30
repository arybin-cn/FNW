package info.arybin.fearnotwords.ui.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import java.util.concurrent.atomic.AtomicBoolean;

public class SlideLayout extends RelativeLayout {

    public final int STATE_IDLE = 0;
    public final int STATE_SCROLLING = 1;

    private boolean slidable = true;
    private AtomicBoolean slideToSide = new AtomicBoolean(false);

    private final Scroller mScroller;
    private final int mTouchSlop;

    private float mPreviousX;
    private int mState = STATE_IDLE;
    private int mOffsetMax = 360;
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


    public void setMaxOffset(int offsetMax) {
        this.mOffsetMax = offsetMax;
    }

    public int getMaxOffset() {
        return mOffsetMax;
    }

    public boolean isSlidable() {
        return slidable;
    }

    public void setSlidable(boolean slidable) {
        this.slidable = slidable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!slidable) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (null != mOnSlideListener && mState == STATE_IDLE) {
                    mOnSlideListener.onStartSlide(this);
                }
                mPreviousX = event.getX();
                if (mScroller.isFinished()) {
                    mState = STATE_IDLE;
                } else {
                    mScroller.abortAnimation();
                    mState = STATE_SCROLLING;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getX() - mPreviousX;

                if (mState == STATE_IDLE && Math.abs(deltaX) >= mTouchSlop) {
                    mState = STATE_SCROLLING;
                    mPreviousX = event.getX();
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!slidable) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getX() - mPreviousX;
                if (mState == STATE_IDLE && Math.abs(deltaX) > mTouchSlop) {
                    mPreviousX = event.getX();
                    mState = STATE_SCROLLING;
                    break;
                }
                if (mState == STATE_SCROLLING) {
                    deltaX *= 1f;
                    mPreviousX = event.getX();
                    float newX = getScrollX() - deltaX;

                    if (Math.abs(newX) > mOffsetMax) {
                        scrollTo(mOffsetMax * (newX > 0 ? 1 : -1), 0);
                    } else {
                        scrollBy((int) -deltaX, 0);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (null != mOnSlideListener) {
                    mOnSlideListener.onFinishSlide(this);
                }
                scrollToCenter();
                break;
        }
        return true;
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
            if (Math.abs(getScrollX()) == 0 && mState == STATE_SCROLLING) {
                if (null != mOnSlideListener) {
                    mOnSlideListener.onSlideToCenter(this);
                    mState = STATE_IDLE;
                    slideToSide.set(false);
                }
            }
        }

        if (null != mOnSlideListener) {
            mOnSlideListener.onSlide(this, getScrollX() * -1f / mOffsetMax);
        }

        if (Math.abs(Math.abs(getScrollX()) - mOffsetMax) < 1) {
            if (slideToSide.compareAndSet(false, true)) {
                scrollToCenter();
                if (null != mOnSlideListener && mState == STATE_SCROLLING) {
                    mState = STATE_IDLE;
                    if (getScrollX() > 0) {
                        mOnSlideListener.onSlideToLeft(this);
                    } else {
                        mOnSlideListener.onSlideToRight(this);
                    }
                }
            }
        }
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
