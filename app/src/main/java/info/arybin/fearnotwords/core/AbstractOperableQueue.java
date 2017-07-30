package info.arybin.fearnotwords.core;


import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicBoolean;

abstract class AbstractOperableQueue<T> implements OperableQueue<T> {


    private AtomicBoolean inLoop = new AtomicBoolean(false);
    //private DataSource loopSource;
    private ArrayDeque<T> loopDeque;
    //private DataSource lastSource = DataSource.Default;
    private ArrayDeque<T> lastDeque;
    private ArrayDeque<T> dequeBeforeLoop;

    private ArrayDeque<T> skippedQueue;
    private ArrayDeque<T> passedQueue;
    private ArrayDeque<T> defaultQueue;

    private int intervalToLastReview = 0;
    private T current;
    private T beforeLoop;

    protected abstract boolean shouldReview(int intervalToLastReview);

    AbstractOperableQueue(Collection<T> source, Collection<T> skipped) {
        defaultQueue = new ArrayDeque<>(source);
        skippedQueue = new ArrayDeque<>(skipped);
        passedQueue = new ArrayDeque<>();
        lastDeque = defaultQueue;
        current = defaultQueue.poll();
    }


    private void currentTo(ArrayDeque<T> queue) {
        if (current != null) {
            if (queue == defaultQueue) {
                //only use in loop
                defaultQueue.addFirst(current);
            } else {
                queue.add(current);
            }
        }

    }

    private T nextFrom(ArrayDeque<T> queue) {
        current = queue.poll();
        lastDeque=queue;
        return current;
    }

    private T next() {
        if (inLoop.get()) {
            return nextFrom(loopDeque);
        } else {
            if (defaultQueue.size() == 0 && skippedQueue.size() == 0) {
                //end of queue
                current = null;
                return null;
            }
            if (defaultQueue.size() == 0) {
                return nextFrom(skippedQueue);
            }
            if (skippedQueue.size() == 0) {
                return nextFrom(defaultQueue);
            }
            if (shouldReview(intervalToLastReview++)) {
                intervalToLastReview = 0;
                return nextFrom(skippedQueue);
            } else {
                return nextFrom(defaultQueue);
            }
        }
    }


    @Override
    public ArrayDeque<T> getRawDeque(DataSource dataSource){
        switch (dataSource) {
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
        currentTo(passedQueue);
        return next();
    }

    @Override
    public T skip() {
        currentTo(skippedQueue);
        return next();
    }


    @Override
    public T startLoop(DataSource loopSource) {
        //can not loop in default queue(meaningless)
        if (inLoop.compareAndSet(false, true) && loopSource != DataSource.Default) {
            loopDeque = getRawDeque(loopSource);
            beforeLoop = current;
            dequeBeforeLoop=lastDeque;
            if (loopDeque == lastDeque) {
                //not switching here
                return current;
            } else {
                currentTo(lastDeque);
                return next();
            }
        }
        //below should never get executed, maybe replace here with exception
        currentTo(skippedQueue);
        return next();
    }

    @Override
    public T loop() {
        currentTo(loopDeque);
        return next();
    }

    @Override
    public T endLoop() {
        if (inLoop.compareAndSet(true, false)) {
            if (current == beforeLoop) {
                return current;
            } else {
                if (dequeBeforeLoop.contains(beforeLoop)) {
                    dequeBeforeLoop.remove(beforeLoop);
                    currentTo(loopDeque);
                    lastDeque=dequeBeforeLoop;
                    current = beforeLoop;
                    return current;
                }
            }


        }
        //below should never get executed, maybe replaced by exception handler
        currentTo(skippedQueue);
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
