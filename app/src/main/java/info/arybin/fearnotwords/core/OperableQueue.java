package info.arybin.fearnotwords.core;

import java.util.Deque;

public interface OperableQueue<T> {

    public enum LoopType {
        NoLoop, LoopInPassed, LoopInSkipped
    }


    T current();

    T pass();

    T skip();

    T loop();


    void setLoopType(LoopType loopType);

    LoopType getLoopType();

    Deque<T> passedDeque();

    Deque<T> skippedDeque();

    Deque<T> defaultDeque();
}
