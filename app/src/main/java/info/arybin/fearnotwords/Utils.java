package info.arybin.fearnotwords;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import java.util.ArrayDeque;
import java.util.LinkedList;

public class Utils {
    public static LinkedList<View> retrieveAllChildViews(ViewGroup parent, View... exceptions) {
        LinkedList<View> list = new LinkedList<>();
        ArrayDeque<View> queue = new ArrayDeque<>();
        queue.add(parent);
        while (queue.size() > 0) {
            View view = queue.poll();
            boolean shouldContinue = false;
            for (View exception : exceptions) {
                if (view == exception) {
                    shouldContinue = true;
                    break;
                }
            }
            if (shouldContinue) {
                continue;
            }
            if (view instanceof ViewGroup) {
                ViewGroup tmpGroup = (ViewGroup) view;
                int childCount = tmpGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    queue.add(tmpGroup.getChildAt(i));
                }
                continue;
            }
            list.add(view);
        }
        return list;
    }

    public static boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        return (x > viewX && x < (viewX + view.getWidth())) &&
                (y > viewY && y < (viewY + view.getHeight()));
    }

    public abstract static class AnimationListenerAdapter implements Animation.AnimationListener{
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}
