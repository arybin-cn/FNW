package info.arybin.fearnotwords.ui.anim;

import android.graphics.*;
import android.view.animation.LinearInterpolator;

import com.flaviofaria.kenburnsview.Transition;
import com.flaviofaria.kenburnsview.TransitionGenerator;

import java.util.Random;

public class SimpleTransitionGenerator implements TransitionGenerator {
    private final Random mRandom;
    private final float mMaxPaddingRate;

    private int mSpeed;
    private RectF mMaxScaledViewport;
    private Transition mLastTransition;


    /**
     * @param maxPaddingRate max padding rate
     * @param speed          pixels per second
     */
    public SimpleTransitionGenerator(float maxPaddingRate, int speed) {
        mRandom = new Random();
        mMaxPaddingRate = maxPaddingRate;
        mSpeed = speed;
    }

    private RectF scaleRect(RectF raw, float scale) {
        RectF scaledRect = new RectF(raw);
        float newWidth = raw.width() * scale;
        float newHeight = raw.height() * scale;
        scaledRect.left -= (newWidth - raw.width()) / 2;
        scaledRect.top -= (newHeight - raw.height()) / 2;
        scaledRect.right = scaledRect.left + newWidth;
        scaledRect.bottom = scaledRect.top + newHeight;
        return scaledRect;
    }

    @Override
    public Transition generateNextTransition(RectF drawableBounds, RectF viewport) {
        float gapX, gapY;
        float deltaX, deltaY;
        SimplePoint fromPoint, toPoint;
        RectF srcRect, dstRect;

        if (null == mMaxScaledViewport) {
            float viewportDeltaX = (drawableBounds.width() - viewport.width()) / 2;
            float viewportDeltaY = (drawableBounds.height() - viewport.height()) / 2;
            viewport.left += viewportDeltaX;
            viewport.right += viewportDeltaX;
            viewport.top += viewportDeltaY;
            viewport.bottom += viewportDeltaY;
            mMaxScaledViewport = scaleRect(viewport, Math.min(drawableBounds.width(), drawableBounds.height())
                    / Math.max(viewport.width(), viewport.height()));
        }

        if (mLastTransition != null) {
            srcRect = mLastTransition.getDestinyRect();
        } else {
            srcRect = mMaxScaledViewport;
        }

        dstRect = scaleRect(mMaxScaledViewport, randomFloatBetween(1 - mMaxPaddingRate, 1));

        gapX = dstRect.left - drawableBounds.left;
        gapY = dstRect.top - drawableBounds.top;
        deltaX = randomFloatBetween(-gapX, gapX);
        deltaY = randomFloatBetween(-gapY, gapY);

        dstRect.left += deltaX;
        dstRect.right += deltaX;
        dstRect.top += deltaY;
        dstRect.bottom += deltaY;

        fromPoint = new SimplePoint(srcRect.left, srcRect.top);
        toPoint = new SimplePoint(dstRect.left, dstRect.top);


        mLastTransition = new Transition(srcRect, dstRect, (int) (fromPoint.distanceTo(toPoint) / mSpeed) * 1000, new LinearInterpolator());
        return mLastTransition;
    }


    private float randomFloatBetween(float a, float b) {
        return a + (b - a) * mRandom.nextFloat();
    }

}
