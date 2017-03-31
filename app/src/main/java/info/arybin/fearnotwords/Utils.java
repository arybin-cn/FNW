package info.arybin.fearnotwords;

import android.view.View;
import android.view.ViewGroup;

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

}
