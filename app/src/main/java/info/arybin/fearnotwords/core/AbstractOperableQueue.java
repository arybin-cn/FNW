package info.arybin.fearnotwords.core;


import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicBoolean;

abstract class AbstractOperableQueue<T> implements OperableQueue<T> {

    private enum DataSource {
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
    private T beforeLoop;
    private ArrayDeque<T> queueBeforeLoop;
    private DataSource dataSourceBeforeLoop;

    protected abstract boolean shouldReview(int intervalToLastReview);

    AbstractOperableQueue(Collection<T> source, Collection<T> skipped) {
        defaultQueue = new ArrayDeque<>(source);
        skippedQueue = new ArrayDeque<>(skipped);
        passedQueue = new ArrayDeque<>();
        current = defaultQueue.poll();
    }

    private T next() {
        if (inLoop.get()) {
            if (currentLoopType == LoopType.LoopInPassed) {
                return pollFromPassed();
            } else {
                //LoopInSkipped or else
                return pollFromSkipped();
            }
        } else {
            if (defaultQueue.size() == 0 && skippedQueue.size() == 0) {
                //end of queue
                current = null;
                return null;
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
    }

    private void insertToDefault() {
        if (current != null) {
            defaultQueue.addFirst(current);
        }
    }

    private void appendToPassed() {
        if (current != null) {
            passedQueue.add(current);
        }
    }

    private void appendToSkipped() {
        if (current != null) {
            skippedQueue.add(current);
        }
    }


    private T pollFromDefault() {
        return poll(defaultQueue, DataSource.Default);
    }


    private T pollFromPassed() {
        return poll(passedQueue, DataSource.Passed);
    }

    private T pollFromSkipped() {
        return poll(skippedQueue, DataSource.Skipped);
    }


    private T poll(ArrayDeque<T> queue, DataSource source) {
        current = queue.poll();
        lastDataSource = source;
        return current;
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
    public T startLoop(LoopType loopType) {
        if (inLoop.compareAndSet(false, true)) {
            currentLoopType = loopType;
            beforeLoop = current;
            dataSourceBeforeLoop = lastDataSource;
            switch (lastDataSource) {
                case Skipped:
                    queueBeforeLoop = skippedQueue;
                    if (loopType == LoopType.LoopInSkipped) {
                        return current;
                    } else {
                        return skip();
                    }
                case Passed:
                    queueBeforeLoop = passedQueue;
                    if (loopType == LoopType.LoopInPassed) {
                        return current;
                    } else {
                        return pass();
                    }
                case Default:
                default:
                    queueBeforeLoop = defaultQueue;
                    insertToDefault();
                    return next();
            }
        }
        return next();
    }

    @Override
    public T loop() {
        if (currentLoopType == LoopType.LoopInPassed) {
            appendToPassed();
        } else {
            appendToSkipped();
        }
        return next();
    }

    @Override
    public T endLoop() {
        if (inLoop.compareAndSet(true, false)) {
            if (queueBeforeLoop.contains(beforeLoop)) {
                queueBeforeLoop.remove(beforeLoop);
                lastDataSource = dataSourceBeforeLoop;
                current = beforeLoop;
                return current;
            }
        }
        return next();
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
