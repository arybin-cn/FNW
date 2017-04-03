package info.arybin.fearnotwords.core;


import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

public abstract class AbstractOperableQueue<T> implements OperableQueue<T> {

    public enum DataSource {
        Default, Passed, Skipped
    }

    private LoopType currentLoopType = LoopType.NoLoop;
    private DataSource lastDataSource = DataSource.Default;

    private ArrayDeque<T> mSkipped;
    private ArrayDeque<T> mPassed;
    private ArrayDeque<T> mDefault;

    private int mIntervalToLastReview = 0;
    private T mCurrent;

    protected abstract boolean shouldReview(int intervalToLastReview);

    protected AbstractOperableQueue(Collection<T> lastDataSource, Collection<T> skipped) {
        mDefault = new ArrayDeque<>(lastDataSource);
        mSkipped = new ArrayDeque<>(skipped);
        mPassed = new ArrayDeque<>();
        mCurrent = mDefault.poll();
    }


    @Override
    public T current() {
        return mCurrent;
    }

    private T next() {
        switch (currentLoopType) {
            case NoLoop:
                if (mDefault.size() == 0 && mSkipped.size() == 0) {
                    mCurrent = null;
                    break;
                }
                if (mDefault.size() == 0) {
                    mCurrent = pollFromSkipped();
                    break;
                }
                if (mSkipped.size() == 0) {
                    mCurrent = pollFromDefault();
                    break;
                }
                if (shouldReview(mIntervalToLastReview++)) {
                    mCurrent = pollFromSkipped();
                    mIntervalToLastReview = 0;
                } else {
                    mCurrent = pollFromDefault();
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

    @Override
    public T pass() {
        if (mCurrent != null) {
            mPassed.add(mCurrent);
        }
        return next();
    }

    @Override
    public T skip() {
        if (mCurrent != null) {
            mSkipped.add(mCurrent);
        }
        return next();
    }

    @Override
    public T loop() {
        switch (lastDataSource) {
            case Skipped:
                return skip();
            case Passed:
                return pass();
        }
        // lastDataSource is Default
        mDefault.addFirst(mCurrent);
        return next();
    }

    private T pollFromSkipped() {
        lastDataSource = DataSource.Skipped;
        return mSkipped.poll();
    }

    private T pollFromDefault() {
        lastDataSource = DataSource.Default;
        return mDefault.poll();
    }

    private T pollFromPassed() {
        lastDataSource = DataSource.Passed;
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

    public DataSource getLastDataSource() {
        return lastDataSource;
    }

    @Override
    public Deque<T> passedDeque() {
        return mPassed;
    }

    @Override
    public Deque<T> skippedDeque() {
        return mSkipped;
    }

    @Override
    public Deque<T> defaultDeque() {
        return mDefault;
    }


    @Override
    public String toString() {
        return String.format("current: %s\npassedDeque: %s\nskippedDeque: %s\ndefaultDeque: %s\n",
                mCurrent, mPassed, mSkipped, mDefault);
    }

}
