package info.arybin.fearnotwords.core;


import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractOperableQueue<T> implements OperableQueue<T> {

    public enum LastElementType {
        Source, Passed, Skipped
    }

    private LoopType currentLoopType = LoopType.NoLoop;
    private LastElementType lastElementType = LastElementType.Source;

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
        if (mCurrent != null) {
            (passCurrent ? mPassed : mSkipped).add(mCurrent);
        }
        switch (currentLoopType) {
            case NoLoop:
                if (mSource.size() == 0 && mSkipped.size() == 0) {
                    mCurrent = null;
                    break;
                }
                if (mSource.size() == 0) {
                    mCurrent = pollFromSkipped();
                    break;
                }
                if (mSkipped.size() == 0) {
                    mCurrent = pollFromSource();
                    break;
                }
                if (shouldReview(mIntervalToLastReview++)) {
                    mCurrent = pollFromSkipped();
                    mIntervalToLastReview = 0;
                } else {
                    mCurrent = pollFromSource();
                }
                break;
            case LoopInSkipped:
                mCurrent = pollFromSkipped();
                break;
            case LoopInPassed:
                mCurrent = pollFromPassed();
                break;
        }
        return mCurrent;
    }

    private T pollFromSkipped() {
        lastElementType = LastElementType.Skipped;
        return mSkipped.poll();
    }

    private T pollFromSource() {
        lastElementType = LastElementType.Source;
        return mSource.poll();
    }

    private T pollFromPassed() {
        lastElementType = LastElementType.Passed;
        return mPassed.poll();
    }


    @Override
    public void setLoopType(LoopType loopType) {
        currentLoopType = loopType;
    }

    @Override
    public LoopType getLoopType() {
        return currentLoopType;
    }

    public LastElementType getLastElementType() {
        return lastElementType;
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
