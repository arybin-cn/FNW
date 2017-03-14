package info.arybin.fearnotwords.model;

import java.util.Collection;

public interface OperableQueue<T> {
    T current();

    T pass();

    T skip();

    T loop();

    Collection<T> passed();

    Collection<T> skipped();

    Collection<T> source();
}
