package info.arybin.fearnotwords.model;


import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractOperableQueue<T> implements OperableQueue<T> {

    private ConcurrentLinkedQueue<T> mSkipped;
    private ConcurrentLinkedQueue<T> mPassed;
    private ConcurrentLinkedQueue<T> mSource;

    private T mCurrent;

    protected abstract boolean shouldReview(ConcurrentLinkedQueue dataSource,
                                            ConcurrentLinkedQueue passed,
                                            ConcurrentLinkedQueue skipped);

    protected AbstractOperableQueue(Collection<T> dataSource, Collection<T> skipped) {
        mSource = new ConcurrentLinkedQueue<>(dataSource);
        mSkipped = new ConcurrentLinkedQueue<>(skipped);
        mPassed = new ConcurrentLinkedQueue<>();
        mCurrent = mSource.poll();
    }


    private T next(boolean passCurrent, boolean inLoop) {
        if (mCurrent == null) {
            return null;
        }
        (passCurrent ? mPassed : mSkipped).add(mCurrent);
        if (inLoop) {
            mCurrent = mSkipped.poll();
        } else {
            if (shouldReview(mSource, mPassed, mSkipped) || mSource.size() == 0) {
                mCurrent = mSkipped.poll();
            } else {
                mCurrent = mSource.poll();
            }
        }
        return mCurrent;
    }


    @Override
    public T current() {
        return mCurrent;
    }

    @Override
    public T pass() {
        return next(true, false);
    }

    @Override
    public T skip() {
        return next(false, false);
    }

    @Override
    public T loop() {
        return next(false, true);
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
        return String.format("current: %s\npassed: %s\nskipped: %s\nsource: %s\n", mCurrent, mPassed, mSkipped, mSource);
    }

}
