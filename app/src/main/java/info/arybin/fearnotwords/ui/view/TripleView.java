package info.arybin.fearnotwords.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class TripleView extends ViewGroup {

    private ActionListener actionListener;

    private VelocityTracker mVelocityTracker;
    private static final int LIMIT_VELOCITY = 1000;

    private boolean cacheFlag = false;

    private boolean mLock = false;

    private View mLeftView;
    private View mCenterView;
    private View mRightView;

    private int mLeftWidth;
    private int mCenterWidth;
    private int mRightWidth;

    private static final int TOUCH_SCROLLING = 1;
    private static final int TOUCH_IDLE = 0;
    private int mTouchState = TOUCH_IDLE;

    public static final int SCREEN_LEFT = -1;
    public static final int SCREEN_CENTER = 0;
    public static final int SCREEN_RIGHT = 1;

    private int mCurrentScreen = SCREEN_CENTER;
    private int mAimScreen = SCREEN_CENTER;

    private float mPreviousX;

    private Scroller mScroller;
    private int mTouchSlop;

    public TripleView(Context context) {
        this(context, null, 0);
    }

    public TripleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TripleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureViews(widthMeasureSpec, heightMeasureSpec);
    }

    public void measureViews(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() != 3) {
            throw new IllegalArgumentException(
                    "The number of child views must be 3!");
        }

        mLeftView = getChildAt(0);
        mCenterView = getChildAt(1);
        mRightView = getChildAt(2);

        mLeftWidth = mLeftView.getLayoutParams().width;
        mRightWidth = mRightView.getLayoutParams().width;


        mLeftView.measure(mLeftWidth, heightMeasureSpec);
        mCenterView.measure(widthMeasureSpec, heightMeasureSpec);
        mRightView.measure(mRightWidth, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (getChildCount() != 3) {
            throw new IllegalArgumentException(
                    "The number of child views must be 3!");
        }

        mCenterWidth = right - left;

        mLeftView.layout(-mLeftWidth, top, left, bottom);
        mCenterView.layout(left, top, right, bottom);
        mRightView.layout(right, top, right + mRightWidth, bottom);

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        System.out.println("I-"+event);

        if (mLock) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    mTouchState = TOUCH_SCROLLING;
                    return true;
                } else {
                    mPreviousX = event.getX();
                    mTouchState = TOUCH_IDLE;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                System.out.println(mTouchState == TOUCH_SCROLLING);

                float deltaX = event.getX() - mPreviousX;

                if (mTouchState != TOUCH_SCROLLING && Math.abs(deltaX) >= mTouchSlop && !mLock) {
                    mTouchState = TOUCH_SCROLLING;
                    mPreviousX = event.getX();
                    enableCache();
                    return true;
                }
                break;
        }
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("N-"+event);
        if (mLock) {
            return true;
        }
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    mTouchState = TOUCH_SCROLLING;
                } else {
                    mPreviousX = event.getX();
                    mTouchState = TOUCH_IDLE;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentScreen = correctScreen();

                float deltaX = event.getX() - mPreviousX;

                if (mTouchState != TOUCH_SCROLLING && Math.abs(deltaX) >= mTouchSlop && !mLock) {
                    mTouchState = TOUCH_SCROLLING;
                    mPreviousX = event.getX();
                    enableCache();
                    break;
                }


                if (mTouchState == TOUCH_SCROLLING) {
                    deltaX *= 1f;
                    mPreviousX = event.getX();
                    float newX = getScrollX() - deltaX;

                    if (newX > mRightWidth) {
                        scrollTo(mRightWidth, 0);
                    } else if (newX < -mLeftWidth) {
                        scrollTo(-mLeftWidth, 0);
                    } else {
                        scrollBy((int) -deltaX, 0);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1);
                if (mVelocityTracker.getXVelocity() >= LIMIT_VELOCITY
                        && (mAimScreen - 1) >= SCREEN_LEFT) {
                    scrollToScreen(--mAimScreen);
                } else if (mVelocityTracker.getXVelocity() < -LIMIT_VELOCITY
                        && (mAimScreen + 1) <= SCREEN_RIGHT) {
                    scrollToScreen(++mAimScreen);
                } else {
                    mAimScreen = correctScreen();
                    autoScrollToScreen();

                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                break;

        }

        return true;
    }

    private void autoScrollToScreen() {
        scrollToScreen(mAimScreen);
    }

    public void scrollToScreen(int which) {

        int deltaX = 0;
        switch (which) {
            case SCREEN_LEFT:
                deltaX = -(mLeftWidth + getScrollX());
                break;

            case SCREEN_CENTER:
                deltaX = -getScrollX();
                break;

            case SCREEN_RIGHT:
                deltaX = mRightWidth - getScrollX();
                break;

            default:
                break;
        }

        mScroller.startScroll(getScrollX(), 0, deltaX, 0, Math.abs(deltaX) * 3);
        invalidate();
    }

    @Override
    public void computeScroll() {
        mCurrentScreen = correctScreen();
        if (mScroller.computeScrollOffset()) {
            cacheFlag = true;
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        } else if (cacheFlag) {
            cacheFlag = false;
            disableCache();
        }
        if (null != actionListener) {
            int x = getScrollX();
            actionListener.onViewportChanged(x * 1f / (x < 0 ? mLeftWidth : mRightWidth));
        }

    }

    private int correctScreen() {
        if (getScrollX() <= -(mLeftWidth / 2)) {
            return SCREEN_LEFT;
        } else if (getScrollX() + mCenterWidth > (mCenterWidth + mRightWidth / 2)) {
            return SCREEN_RIGHT;
        } else {
            return SCREEN_CENTER;
        }

    }

    public int getCurrentScreen() {
        return mCurrentScreen;
    }

    public void lock() {
        mLock = true;
    }

    public void unlock() {
        mLock = false;
    }

    private void enableCache() {
        mLeftView.setDrawingCacheEnabled(true);
        mCenterView.setDrawingCacheEnabled(true);
        mRightView.setDrawingCacheEnabled(true);
    }

    private void disableCache() {
        mLeftView.setDrawingCacheEnabled(false);
        mCenterView.setDrawingCacheEnabled(false);
        mRightView.setDrawingCacheEnabled(false);
    }

    public void setOnActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public interface ActionListener {
        void onViewportChanged(float rate);
    }
}
