package info.arybin.fearnotwords.core;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;


public class SimpleOperableQueue<T> extends AbstractOperableQueue<T> {

    private static Random random = new Random();


    private SimpleOperableQueue(Collection<T> source, Collection<T> skipped) {
        super(source, skipped);
    }

    public static <S> SimpleOperableQueue<S> buildFrom(Collection<S> source,
                                                       Collection<S> skipped) {
        if (null == source || null == skipped || source.size() == 0) {
            return null;
        }
        return new SimpleOperableQueue<>(source, skipped);
    }

    public static <S> SimpleOperableQueue<S> buildFrom(Collection<S> source) {
        if (null == source || source.size() == 0) {
            return null;
        }
        return new SimpleOperableQueue<>(source, new ConcurrentLinkedQueue<S>());
    }


    @Override
    protected boolean shouldReview(int intervalToLastReview) {
        //Words' review strategy here
        return skippedDeque().size() > 10 || intervalToLastReview > 3 || (random.nextInt(30) < 2);
    }

}
