package info.arybin.fearnotwords.core;

import java.util.Collection;

public interface OperableQueue<T> {

    public enum LoopType {
        NoLoop, LoopInPassed, LoopInSkipped
    }


    T current();

    T next(boolean passCurrent);

    T pass();

    T skip();

    void setLoopType(LoopType loopType);

    LoopType getLoopType();

    Collection<T> passed();

    Collection<T> skipped();

    Collection<T> source();
}
