package info.arybin.fearnotwords.core;

import java.util.Deque;

public interface OperableQueue<T> {

    public enum LoopType {
        LoopInPassed, LoopInSkipped
    }


    T current();

    T pass();

    T skip();

    T startLoop(LoopType loopType);

    T endLoop();


    Deque<T> passedDeque();

    Deque<T> skippedDeque();

    Deque<T> defaultDeque();
}
