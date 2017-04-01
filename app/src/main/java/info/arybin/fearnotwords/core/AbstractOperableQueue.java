package info.arybin.fearnotwords.core;


import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractOperableQueue<T> implements OperableQueue<T> {

    private LoopType currentLoopType = LoopType.NoLoop;

    private ConcurrentLinkedQueue<T> mSkipped;
    private ConcurrentLinkedQueue<T> mPassed;
    private ConcurrentLinkedQueue<T> mSource;

    private int mIntervalToLastReview = 0;
    private T mCurrent;

    protected abstract boolean shouldReview(int intervalToLastReview);

    protected AbstractOperableQueue(Collection<T> dataSource, Collection<T> skipped) {
        mSource = new ConcurrentLinkedQueue<>(dataSource);
        mSkipped = new ConcurrentLinkedQueue<>(skipped);
        mPassed = new ConcurrentLinkedQueue<>();
        mCurrent = mSource.poll();
    }


    @Override
    public T current() {
        return mCurrent;
    }

    @Override
    public T pass() {
        return next(true);
    }

    @Override
    public T skip() {
        return next(false);
    }

    @Override
    public T next(boolean passCurrent) {
        if (mCurrent == null) {
            return null;
        }
        (passCurrent ? mPassed : mSkipped).add(mCurrent);
        switch (currentLoopType) {
            case NoLoop:
                if (mSource.size() == 0 || (mSkipped.size() > 0 && shouldReview(mIntervalToLastReview++))) {
                    mIntervalToLastReview = 0;
                    mCurrent = mSkipped.poll();
                } else {
                    mCurrent = mSource.poll();
                }
                break;
            case LoopInSkipped:
                mCurrent = mSkipped.poll();
                break;
            case LoopInPassed:
                mCurrent = mPassed.poll();
                break;
        }
        return mCurrent;
    }

    @Override
    public void setLoopType(LoopType loopType) {
        currentLoopType = loopType;
    }

    @Override
    public Collection<T> passed() {
        return mPassed;
    }

    @Override
    public Collection<T> skipped() {
        return mSkipped;
    }

    @Override
    public Collection<T> source() {
        return mSource;
    }


    @Override
    public String toString() {
        return String.format("current: %s\npassed: %s\nskipped: %s\nsource: %s\n",
                mCurrent, mPassed, mSkipped, mSource);
    }

}
