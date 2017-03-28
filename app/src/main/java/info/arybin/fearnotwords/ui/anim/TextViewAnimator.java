package info.arybin.fearnotwords.ui.anim;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class TextViewAnimator implements Animation.AnimationListener {
    private TextView textView;
    private int animDuration;
    private AtomicBoolean canOut = new AtomicBoolean(true);
    private ArrayDeque<TextView> inQueue = new ArrayDeque<>();
    private ArrayDeque<TextView> outQueue = new ArrayDeque<>();

    TextViewAnimator(TextView textView, int animDuration) {
        this.textView = textView;
        this.animDuration = animDuration;
    }

    void setText(CharSequence text) {
        TextView textViewIn = duplicateTextView(text);
        if (canOut.compareAndSet(true, false)) {
            TextView textViewOut = duplicateTextView(null);
            textViewOut.setAnimation(makeOutAnimation());
            textView.setVisibility(View.INVISIBLE);
            parentOf(textView).addView(textViewOut, retrieveLayoutParams());
        } else {
            outQueue.add(inQueue.getLast());
        }
        inQueue.add(textViewIn);
        textViewIn.setAnimation(makeInAnimation());
        parentOf(textView).addView(textViewIn, retrieveLayoutParams());
    }


    private ViewGroup parentOf(View view) {
        return (ViewGroup) view.getParent();
    }

    private ViewGroup.LayoutParams retrieveLayoutParams() {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        return params;
    }


    private TextView duplicateTextView(CharSequence text) {
        TextView dstTextView = new TextView(textView.getContext());
        dstTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getTextSize());
        dstTextView.setTextColor(textView.getTextColors());
        dstTextView.setGravity(textView.getGravity());
        if (null == text) {
            dstTextView.setText(textView.getText());
        } else {
            dstTextView.setText(text);
        }
        return dstTextView;
    }


    private Animation makeOutAnimation() {
        TranslateAnimation tA = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.3f,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        AlphaAnimation aA = new AlphaAnimation(1, 0);

        AnimationSet aS = new AnimationSet(true);
        aS.setDuration(animDuration);
        aS.setFillAfter(true);

        aS.addAnimation(tA);
        aS.addAnimation(aA);
        return aS;
    }


    private Animation makeInAnimation() {
        TranslateAnimation tA = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -0.3f, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        AlphaAnimation aA = new AlphaAnimation(0, 1);
        AnimationSet aS = new AnimationSet(true);
        aS.setDuration(animDuration);
        aS.addAnimation(tA);
        aS.addAnimation(aA);
        aS.setAnimationListener(this);
        return aS;
    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        TextView textViewIn = inQueue.poll();
        textView.setText(textViewIn.getText());
        textViewIn.setVisibility(View.GONE);
        if (outQueue.size() == 0) {
            textView.setVisibility(View.VISIBLE);
            canOut.set(true);
        } else {
            TextView textViewOut = outQueue.poll();
            textViewOut.startAnimation(makeOutAnimation());
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}