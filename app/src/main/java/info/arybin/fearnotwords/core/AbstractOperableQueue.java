package info.arybin.fearnotwords.core;


import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicBoolean;

abstract class AbstractOperableQueue<T> implements OperableQueue<T> {


    private AtomicBoolean inLoop = new AtomicBoolean(false);
    private DataSource loopSource;
    private DataSource lastSource = DataSource.Default;

    private ArrayDeque<T> skippedQueue;
    private ArrayDeque<T> passedQueue;
    private ArrayDeque<T> defaultQueue;

    private int intervalToLastReview = 0;
    private T current;
    private T beforeLoop;
    private DataSource sourceBeforeLoop;

    protected abstract boolean shouldReview(int intervalToLastReview);

    AbstractOperableQueue(Collection<T> source, Collection<T> skipped) {
        defaultQueue = new ArrayDeque<>(source);
        skippedQueue = new ArrayDeque<>(skipped);
        passedQueue = new ArrayDeque<>();
        current = defaultQueue.poll();
    }

    private T next() {
        if (inLoop.get()) {
            if (loopSource == DataSource.Passed) {
                return pollFrom(DataSource.Passed);
            } else {
                //LoopInSkipped or else
                return pollFrom(DataSource.Skipped);
            }
        } else {
            if (defaultQueue.size() == 0 && skippedQueue.size() == 0) {
                //end of queue
                current = null;
                return null;
            }
            if (defaultQueue.size() == 0) {
                return pollFrom(DataSource.Skipped);
            }
            if (skippedQueue.size() == 0) {
                return pollFrom(DataSource.Default);
            }
            if (shouldReview(intervalToLastReview++)) {
                intervalToLastReview = 0;
                return pollFrom(DataSource.Skipped);
            } else {
                return pollFrom(DataSource.Default);
            }
        }
    }


    private void appendTo(ArrayDeque<T> queue) {
        if (current != null) {
            queue.add(current);
        }
    }

    private void insertTo(ArrayDeque<T> queue) {
        if (current != null) {
            queue.addFirst(current);
        }
    }

    private T pollFrom(DataSource source) {
        current = dataSource2Queue(source).poll();
        lastSource = source;
        return current;
    }

    private ArrayDeque<T> dataSource2Queue(DataSource source) {
        switch (source) {
            case Passed:
                return passedQueue;
            case Skipped:
                return skippedQueue;
            case Default:
            default:
                return defaultQueue;
        }
    }
    
    @Override
    public T current() {
        return current;
    }

    @Override
    public T pass() {
        appendTo(passedQueue);
        return next();
    }

    @Override
    public T skip() {
        appendTo(skippedQueue);
        return next();
    }


    @Override
    public T startLoop(DataSource loopSource) {
        //can not loop in default queue(meaningless)
        if (inLoop.compareAndSet(false, true) && loopSource != DataSource.Default) {
            this.loopSource = loopSource;
            beforeLoop = current;
            sourceBeforeLoop = lastSource;
            if (loopSource == lastSource) {
                //not switching here
                return current;
            } else {
                if (lastSource == DataSource.Default) {
                    insertTo(defaultQueue);
                } else {
                    appendTo(dataSource2Queue(lastSource));
                }
                return next();
            }
        }
        appendTo(skippedQueue);
        return next();
    }

    @Override
    public T loop() {
        appendTo(dataSource2Queue(loopSource));
        return next();
    }

    @Override
    public T endLoop() {
        ArrayDeque<T> queueBeforeLoop;
        if (inLoop.compareAndSet(true, false)) {
            queueBeforeLoop = dataSource2Queue(sourceBeforeLoop);
            if (queueBeforeLoop.contains(beforeLoop)) {
                queueBeforeLoop.remove(beforeLoop);
                lastSource = sourceBeforeLoop;
                current = beforeLoop;
                return current;
            }
        }
        appendTo(skippedQueue);
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
