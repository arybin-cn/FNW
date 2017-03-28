package info.arybin.fearnotwords.ui.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class SlideLayout extends RelativeLayout {

    public final int STATE_IDLE = 0;
    public final int STATE_SCROLLING = 1;


    private final Scroller mScroller;
    private final int mTouchSlop;

    private float mPreviousX;
    private int mState = STATE_IDLE;
    private int mOffsetMax = 200;
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


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    mState = STATE_SCROLLING;
                    return true;
                } else {
                    mPreviousX = event.getX();
                    mState = STATE_IDLE;
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
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    mState = STATE_SCROLLING;
                } else {
                    mPreviousX = event.getX();
                    mState = STATE_IDLE;
                }
                break;
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
                scrollToCenter();
                break;
        }
        return true;
    }

    public void scrollToCenter(){
        int scrolledX = getScrollX();
        mScroller.startScroll(scrolledX, 0, -scrolledX, 0, Math.abs(scrolledX) *5);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        }
        if (mOnSlideListener != null) {
            if (Math.abs(Math.abs(getScrollX()) - mOffsetMax) < 1) {
                if (getScrollX() > 0) {
                    mOnSlideListener.onSlideToLeft(this);
                } else {
                    mOnSlideListener.onSlideToRight(this);
                }
                scrollToCenter();
            } else {
                mOnSlideListener.onSlide(getScrollX() * -1f / mOffsetMax);
            }
        }
    }


    public void setOnSlideListener(OnSlideListener onSlideListener) {
        this.mOnSlideListener = onSlideListener;
    }


    public interface OnSlideListener {
        void onSlideToLeft(SlideLayout layout);

        void onSlideToRight(SlideLayout layout);

        void onSlide(float rate);

    }

}
