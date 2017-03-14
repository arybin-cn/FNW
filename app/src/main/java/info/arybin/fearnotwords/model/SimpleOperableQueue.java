package info.arybin.fearnotwords.model;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;


public class SimpleOperableQueue<T> extends AbstractOperableQueue<T> {


    private SimpleOperableQueue(Collection<T> dataSource, Collection<T> skipped) {
        super(dataSource, skipped);
    }

    public static <S> SimpleOperableQueue buildFrom(Collection<S> dataSource, Collection<S> skipped) {
        if (null == dataSource || null == skipped || dataSource.size() == 0) {
            return null;
        }
        return new SimpleOperableQueue<>(dataSource, skipped);
    }

    public static <S> SimpleOperableQueue buildFrom(Collection<S> dataSource) {
        if (null == dataSource || dataSource.size() == 0) {
            return null;
        }
        return new SimpleOperableQueue<>(dataSource, new ConcurrentLinkedQueue());
    }


    @Override
    protected boolean shouldReview(ConcurrentLinkedQueue dataSource,
                                   ConcurrentLinkedQueue passed,
                                   ConcurrentLinkedQueue skipped) {
        //Words' review strategy here
        return skipped.size() > 2;
    }

}
