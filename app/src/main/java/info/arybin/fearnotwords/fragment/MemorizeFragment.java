package info.arybin.fearnotwords.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.arybin.fearnotwords.R;
import info.arybin.fearnotwords.Utils;
import info.arybin.fearnotwords.core.OperableQueue;
import info.arybin.fearnotwords.core.SimpleOperableQueue;
import info.arybin.fearnotwords.model.Memorable;
import info.arybin.fearnotwords.model.Translatable;
import info.arybin.fearnotwords.ui.view.layout.ObservableLayout;
import info.arybin.fearnotwords.ui.view.layout.SlidableLayout;

public class MemorizeFragment extends BaseFragment implements ObservableLayout.EventListener {

    @BindView(R.id.layoutMain)
    protected ObservableLayout layoutMain;

    @BindView(R.id.textViewBody)
    public TextView textViewBody;
    @BindView(R.id.textViewPronounce)
    public TextView textViewPronounce;
    @BindView(R.id.textViewTranslation)
    public TextView textViewTranslation;

    @BindView(R.id.layoutExample)
    public RelativeLayout layoutExample;
    @BindView(R.id.textViewExampleBody)
    public TextView textViewExampleBody;
    @BindView(R.id.textViewExampleTranslation)
    public TextView textViewExampleTranslation;

    @BindView(R.id.layoutSkip)
    protected SlidableLayout layoutSkip;
    @BindView(R.id.imageSkip)
    protected ImageView imageSkip;

    @BindView(R.id.layoutPronounce)
    protected SlidableLayout layoutPronounce;
    @BindView(R.id.imagePronounce)
    protected ImageView imagePronounce;

    @BindView(R.id.layoutPass)
    protected SlidableLayout layoutPass;
    @BindView(R.id.imagePass)
    protected ImageView imagePass;


    private OperableQueue<? extends Memorable> memorableQueue;


    private static final int LOCK_SLOP = 60;

    private static final int PRI_STATE_IDLE = 0;
    private static final int PRI_STATE_CONTROL_LOCKED = 1;

    private static final int MIN_STATE_TRANSLATION_HIDE = 0x0;
    private static final int MIN_STATE_TRANSLATION_SHOW = 0x1;

    private static final int MIN_STATE_TRANSLATION_WILL_SHOW = 0x2;
    private static final int MIN_STATE_TRANSLATION_WILL_HIDE = 0x4;

    private static final int MIN_STATE_TRANSLATION_LOCKED = 0x10;
    private static final int MIN_STATE_TRANSLATION_UNLOCKED = 0x00;


    private Scroller scroller;
    private int primaryState = PRI_STATE_IDLE;
    private int minorState = MIN_STATE_TRANSLATION_HIDE;


    private ArrayList<View> controlViews = new ArrayList<>(3);

    private float pressDownX;
    private float pressDownY;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memorize, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    @Override
    public boolean onBackPressed() {

        return false;
    }

    private void initialize() {
        ArrayList<? extends Memorable> tmp = getArguments().getParcelableArrayList(KEY_LOADED_MEMORABLE);
        memorableQueue = SimpleOperableQueue.buildFrom(tmp);
        scroller = new Scroller(getContext());

        initializedViews();
    }

    private void initializedViews() {

        controlViews.addAll(Arrays.asList(imageSkip, imagePass, imagePronounce));

        layoutMain.setEventListener(this);

        layoutMain.addOnPressObserver(layoutSkip, layoutPronounce, layoutPass);
        layoutMain.addOnHoverObserver(layoutSkip, layoutPronounce, layoutPass
                , textViewTranslation, layoutExample);

        layoutSkip.setSlidableOffset(0, 0, 0, LOCK_SLOP);
        layoutPronounce.setSlidableOffset(0, 0, 0, LOCK_SLOP);
        layoutPass.setSlidableOffset(0, 0, 0, LOCK_SLOP);

        layoutSkip.setOnSlideListener(new SlidableLayout.OnSlideListener() {
            @Override
            public void onSlide(SlidableLayout layout, float rateLeftRight, float rateUpDown) {
                System.out.println(rateLeftRight+"-"+rateUpDown);
            }

            @Override
            public void onSlideTo(SlidableLayout layout, SlidableLayout.Direction direction) {

            }

            @Override
            public void onSlideCancel(SlidableLayout layout) {

            }

            @Override
            public void onStartSlide(SlidableLayout layout) {

            }

            @Override
            public void onFinishSlide(SlidableLayout layout) {

            }
        });


        updateView(memorableQueue.current());
    }

    public void updateView(Memorable memorable) {
        updateView(memorable, 0);
    }

    public void updateView(Memorable memorable, int exampleIndex) {
        textViewBody.setText(memorable.getOriginal());
        textViewPronounce.setText(memorable.getPronounce());
        textViewTranslation.setText(memorable.getTranslation());
        Translatable example = memorable.getExampleAt(exampleIndex);
        textViewExampleBody.setText(example.getOriginal());
        textViewExampleTranslation.setText(example.getTranslation());
    }

    private Animation makeHoverInAnimation(boolean reverse) {
        if (reverse) {
            return AnimationUtils.loadAnimation(getContext(), R.anim.in_hover_reverse);
        }
        return AnimationUtils.loadAnimation(getContext(), R.anim.in_hover);
    }

    private Animation makeHoverOutAnimation(boolean reverse) {
        if (reverse) {
            return AnimationUtils.loadAnimation(getContext(), R.anim.out_hover_reverse);
        }
        return AnimationUtils.loadAnimation(getContext(), R.anim.out_hover);
    }

    private Animation makeFadeInAnimation(final View view) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.in_fade);
        animation.setAnimationListener(new Utils.AnimationListenerAdapter() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }
        });
        return animation;
    }

    private Animation makeFadeOutAnimation(final View view) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.out_fade);
        animation.setAnimationListener(new Utils.AnimationListenerAdapter() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }
        });
        return animation;
    }


    private void showTranslation() {


    }

    private void hideTranslation() {

    }


//    private Memorable next(boolean shouldPass) {
//        Memorable memorable = memorableQueue.next(shouldPass);
//        if (memorable == null) {
//            if (memorableQueue.getLoopType() != OperableQueue.LoopType.NoLoop) {
//                memorableQueue.setLoopType(OperableQueue.LoopType.NoLoop);
//                memorable = memorableQueue.next(shouldPass);
//                if (memorable == null) {
//                    //end of OperableQueue
//                    return null;
//                } else {
//                    return memorable;
//                }
//            } else {
//                //end of OperableQueue
//                return null;
//            }
//        }
//        return memorable;
//    }


    @Override
    public void onPressDown(View pressDownView, MotionEvent event) {

    }

    @Override
    public void onPressMove(View pressDownView, MotionEvent event) {
//        System.out.println("OnPressMove");

    }

    @Override
    public void onPressUp(final View pressDownView, MotionEvent event) {
        System.out.println("OnPressUp");
//        boolean shouldPass = false;
//        switch (pressDownView.getId()) {
//            case R.id.imagePass:
//                shouldPass = true;
//            case R.id.imageSkip:
//                updateView(next(shouldPass));
//                break;
//        }
    }

    @Override
    public void onHoverIn(View pressDownView, View viewOnHover, MotionEvent event) {
        System.out.println("HoverIn");
    }

    @Override
    public void onHoverOut(View pressDownView, View viewOnHover, MotionEvent event) {
        if (viewOnHover instanceof SlidableLayout) {
            System.out.println("SetSlidable false");
            ((SlidableLayout) viewOnHover).cancelSlide();
        }
        System.out.println("HoverOut");

    }

    @Override
    public boolean onHoverCancel(View pressDownView, View viewOnHover, MotionEvent event) {
        System.out.println("HoverCancel");

        return true;
    }
}
