package info.arybin.fearnotwords.core;

import java.util.Deque;

public interface OperableQueue<T> {

    enum DataSource {
        Default, Passed, Skipped
    }


    T current();

    T pass();

    T skip();

    T startLoop(DataSource dataSource);

    T loop();

    T endLoop();


    Deque<T> passedDeque();

    Deque<T> skippedDeque();

    Deque<T> defaultDeque();
}
