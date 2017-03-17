package info.arybin.fearnotwords.core;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;


public class SimpleOperableQueue<T> extends AbstractOperableQueue<T> {


    private SimpleOperableQueue(Collection<T> source, Collection<T> skipped) {
        super(source, skipped);
    }

    public static <S> SimpleOperableQueue buildFrom(Collection<S> source,
                                                    Collection<S> skipped) {
        if (null == source || null == skipped || source.size() == 0) {
            return null;
        }
        return new SimpleOperableQueue<>(source, skipped);
    }

    public static <S> SimpleOperableQueue buildFrom(Collection<S> source) {
        if (null == source || source.size() == 0) {
            return null;
        }
        return new SimpleOperableQueue<>(source, new ConcurrentLinkedQueue());
    }


    @Override
    protected boolean shouldReview(int intervalToLastReview) {
        //Words' review strategy here
        return skipped().size() > 5 || intervalToLastReview > 3;
    }

}
