package info.arybin.fearnotwords.core;


import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractOperableQueue<T> implements OperableQueue<T> {

    public enum DataSource {
        Default, Passed, Skipped
    }

    private AtomicBoolean inLoop = new AtomicBoolean(false);
    private LoopType currentLoopType;
    private DataSource lastDataSource = DataSource.Default;

    private ArrayDeque<T> skippedQueue;
    private ArrayDeque<T> passedQueue;
    private ArrayDeque<T> defaultQueue;

    private int intervalToLastReview = 0;
    private T current;

    protected abstract boolean shouldReview(int intervalToLastReview);

    protected AbstractOperableQueue(Collection<T> source, Collection<T> skipped) {
        defaultQueue = new ArrayDeque<>(source);
        skippedQueue = new ArrayDeque<>(skipped);
        passedQueue = new ArrayDeque<>();
        current = defaultQueue.poll();
    }

    private T next() {
        if (inLoop.get()) {
            switch (currentLoopType) {
                case LoopInSkipped:
                    return pollFromSkipped();
                case LoopInPassed:
                    return pollFromPassed();
            }
        } else {
            if (defaultQueue.size() == 0 && skippedQueue.size() == 0) {
                //end of queue
                current = null;
            }
            if (defaultQueue.size() == 0) {
                return pollFromSkipped();
            }
            if (skippedQueue.size() == 0) {
                return pollFromDefault();
            }
            if (shouldReview(intervalToLastReview++)) {
                intervalToLastReview = 0;
                return pollFromSkipped();
            } else {
                return pollFromDefault();
            }
        }
        return current;
    }

    private void appendToSkipped() {
        if (current != null) {
            skippedQueue.add(current);
        }
    }

    private T pollFromSkipped() {
        current = skippedQueue.poll();
        lastDataSource = DataSource.Skipped;
        return current;
    }

    private T pollFromDefault() {
        current = defaultQueue.poll();
        lastDataSource = DataSource.Default;
        return current;
    }

    private void appendToPassed() {
        if (current != null) {
            passedQueue.add(current);
        }
    }

    private T pollFromPassed() {
        current = passedQueue.poll();
        lastDataSource = DataSource.Passed;
        return current;
    }


    public DataSource getLastDataSource() {
        return lastDataSource;
    }


    @Override
    public T current() {
        return current;
    }

    @Override
    public T pass() {
        appendToPassed();
        return next();
    }

    @Override
    public T skip() {
        appendToSkipped();
        return next();
    }


    @Override
    public T endLoop() {
        return null;
    }

    @Override
    public T startLoop(LoopType loopType) {
        if (inLoop.compareAndSet(false,true)){
            currentLoopType = loopType;
            switch (lastDataSource){
            }
        }
        return null;
    }


    @Override
    public Deque<T> passedDeque() {
        return passedQueue;
    }

    @Override
    public Deque<T> skippedDeque() {
        return skippedQueue;
    }

    @Override
    public Deque<T> defaultDeque() {
        return defaultQueue;
    }


    @Override
    public String toString() {
        return String.format("current: %s\npassedDeque: %s\nskippedDeque: %s\ndefaultDeque: %s\n",
                current, passedQueue, skippedQueue, defaultQueue);
    }

}
