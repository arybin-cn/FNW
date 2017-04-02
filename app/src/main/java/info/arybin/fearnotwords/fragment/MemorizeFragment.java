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
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.arybin.fearnotwords.R;
import info.arybin.fearnotwords.core.OperableQueue;
import info.arybin.fearnotwords.core.SimpleOperableQueue;
import info.arybin.fearnotwords.model.Memorable;
import info.arybin.fearnotwords.model.Translatable;
import info.arybin.fearnotwords.ui.view.layout.ObservableLayout;

public class MemorizeFragment extends BaseFragment implements View.OnClickListener, ObservableLayout.EventListener {

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
    @BindView(R.id.scrollViewExample)
    public ScrollView scrollViewExample;
    @BindView(R.id.textViewExampleBody)
    public TextView textViewExampleBody;
    @BindView(R.id.textViewExampleTranslation)
    public TextView textViewExampleTranslation;

    @BindView(R.id.imageSkip)
    protected ImageView imageSkip;
    @BindView(R.id.imagePass)
    protected ImageView imagePass;
    @BindView(R.id.imagePronounce)
    protected ImageView imagePronounce;

    private OperableQueue<? extends Memorable> memorableQueue;


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


        initializedViews();
    }

    private void initializedViews() {

        layoutMain.setEventListener(this);

        layoutMain.addOnPressObserver(layoutMain, imagePronounce, imageSkip, imagePass);
        layoutMain.addOnHoverObserver(imagePronounce, imageSkip, imagePass
                , textViewTranslation, layoutExample);

        imagePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Click");
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


    @Override
    public void onClick(View v) {


    }

    @Override
    public void onPressDown(View view, MotionEvent event) {
//        new ExpectAnim().expect(imageSkip).toBe(Expectations.centerInParent(true, true)).toAnimation().setDuration(1000).start();

    }

    @Override
    public void onPressMove(View view, double distance2LastPos, double distance2AnchorPos, MotionEvent event) {
    }

    @Override
    public void onPressUp(View pressDownView, MotionEvent event) {
        boolean shouldPass = false;
        switch (pressDownView.getId()) {
            case R.id.imagePass:
                shouldPass = true;
            case R.id.imageSkip:
                Memorable memorable = memorableQueue.next(shouldPass);
                if (memorable == null) {
                    if (memorableQueue.getLoopType() != OperableQueue.LoopType.NoLoop) {
                        memorableQueue.setLoopType(OperableQueue.LoopType.NoLoop);
                        memorable = memorableQueue.next(shouldPass);
                        if (memorable == null) {
                            //end of OperableQueue
                        } else {
                            updateView(memorable);
                        }
                    } else {
                        //end of OperableQueue
                    }
                } else {
                    updateView(memorable);
                }
                break;
        }
    }

    @Override
    public void onHoverIn(View pressDownView, View viewOnHover, MotionEvent event) {
        System.out.println("HoverIn   " + viewOnHover);
        switch (viewOnHover.getId()) {
            case R.id.imagePass:
            case R.id.imagePronounce:
            case R.id.imageSkip:
                viewOnHover.startAnimation(makeHoverInAnimation(true));
                break;
            default:
                viewOnHover.startAnimation(makeHoverInAnimation(false));

        }

    }

    @Override
    public void onHoverOut(View pressDownView, View viewOnHover, MotionEvent event) {
        switch (viewOnHover.getId()) {
            case R.id.imagePass:
            case R.id.imagePronounce:
            case R.id.imageSkip:
                viewOnHover.startAnimation(makeHoverOutAnimation(true));
                break;
            default:
                viewOnHover.startAnimation(makeHoverOutAnimation(false));

        }
    }

    @Override
    public boolean onHoverCancel(View pressDownView, View viewOnHover, MotionEvent event) {
        switch (viewOnHover.getId()) {
            case R.id.imagePass:
            case R.id.imagePronounce:
            case R.id.imageSkip:
                viewOnHover.startAnimation(makeHoverOutAnimation(true));
                break;
            default:
                viewOnHover.startAnimation(makeHoverOutAnimation(false));

        }
        return true;
    }
}
