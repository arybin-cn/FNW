package info.arybin.fearnotwords.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.arybin.fearnotwords.R;
import info.arybin.fearnotwords.core.OperableQueue;
import info.arybin.fearnotwords.core.SimpleOperableQueue;
import info.arybin.fearnotwords.model.Memorable;
import info.arybin.fearnotwords.model.Translatable;
import info.arybin.fearnotwords.ui.view.layout.TripleLayout;
import info.arybin.fearnotwords.ui.view.layout.ObservableLayout;
import info.arybin.fearnotwords.ui.view.layout.SlidableLayout;

import static java.lang.Math.abs;

public class MemorizeFragment extends BaseFragment implements ObservableLayout.EventListener, SlidableLayout.OnSlideListener {

    @BindView(R.id.tripleView)
    protected TripleLayout tripleLayout;
    @BindView(R.id.layoutMain)
    protected ObservableLayout layoutMain;

    @BindView(R.id.textViewBody)
    public TextView textViewBody;
    @BindView(R.id.textViewPronounce)
    public TextView textViewPronounce;

    @BindView(R.id.layoutTranslation)
    public RelativeLayout layoutTranslation;
    @BindView(R.id.textViewTranslation)
    public TextView textViewTranslation;
    @BindView(R.id.lockerTranslation)
    public ImageView lockerTranslation;

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


    private static final int LOCK_SLOP = 100;

    private static final int PRI_STATE_NORMAL = 0x1;
    private static final int PRI_STATE_LOOP = 0x2;


    private static final int MIN_STATE_TRANSLATION_HIDE = 0x1;
    private static final int MIN_STATE_TRANSLATION_SHOW = 0x2;
    private static final int MIN_STATE_TRANSLATION_WILL_SHOW = 0x4;
    private static final int MIN_STATE_TRANSLATION_WILL_HIDE = 0x8;
    private static final int MIN_STATE_TRANSLATION_LOCKED = 0x10;

    private static final int MIN_STATE_SKIP_LOCKED = 0x40;
    private static final int MIN_STATE_WILL_LOCK_SKIP = 0x100;
    private static final int MIN_STATE_WILL_UNLOCK_SKIP = 0x200;

    private static final int MIN_STATE_PRONOUNCE_LOCKED = 0x20;
    private static final int MIN_STATE_WILL_LOCK_PRONOUNCE = 0x1000;
    private static final int MIN_STATE_WILL_UNLOCK_PRONOUNCE = 0x2000;

    private static final int MIN_STATE_PASS_LOCKED = 0x80;
    private static final int MIN_STATE_WILL_LOCK_PASS = 0x400;
    private static final int MIN_STATE_WILL_UNLOCK_PASS = 0x800;


    private int primaryState = PRI_STATE_NORMAL;
    private int minorState = MIN_STATE_TRANSLATION_HIDE;
    private ArrayList<SlidableLayout> functionViews = new ArrayList<>(2);
    private float pressDownX;
    private float pressDownY;
    private float previousX;
    private float previousY;


    private void initialize() {
        ArrayList<? extends Memorable> tmp = getArguments().getParcelableArrayList(KEY_LOADED_MEMORABLE);
        memorableQueue = SimpleOperableQueue.buildFrom(tmp);

        initializedViews();
    }

    private void initializedViews() {
        tripleLayout.lock();
        layoutMain.setEventListener(this);

        layoutMain.addOnPressObserver(layoutSkip, layoutPronounce, layoutPass);
        layoutMain.addOnHoverObserver(
                layoutSkip, layoutPronounce, layoutPass,
                layoutTranslation, lockerTranslation, layoutExample);
        functionViews.addAll(Arrays.asList(layoutSkip, layoutPass));

        layoutSkip.setSlidableOffset(0, LOCK_SLOP, 0, LOCK_SLOP);
        layoutPronounce.setSlidableOffset(0, 0, 0, LOCK_SLOP);
        layoutPass.setSlidableOffset(0, 0, 0, LOCK_SLOP);

        layoutSkip.setOnSlideListener(this);
        layoutPronounce.setOnSlideListener(this);
        layoutPass.setOnSlideListener(this);


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


    private void showTranslation() {
        layoutTranslation.setVisibility(View.VISIBLE);
    }

    private void tryToHideTranslation() {
        if (!hasMinorState(MIN_STATE_TRANSLATION_LOCKED)) {
            layoutTranslation.setVisibility(View.INVISIBLE);
        }
    }


    private void addMinorState(int state) {
        minorState |= state;
    }

    private void removeMinorState(int state) {
        minorState &= (~state);
    }

    private boolean hasMinorState(int state) {
        return (minorState & state) != 0;
    }


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

    @Override
    public void onPressDown(View pressDownView, MotionEvent event) {
        pressDownX = event.getX();
        pressDownY = event.getY();
        previousX = pressDownX;
        previousY = pressDownY;
        showTranslation();


        System.out.println("OnPressDown");
    }

    @Override
    public void onPressMove(View pressDownView, MotionEvent event) {
        float currentX = event.getX();
        float currentY = event.getY();
        float deltaX = currentX - previousX;
        float deltaY = currentY - previousY;

        if (pressDownView instanceof SlidableLayout) {
            //layoutSkip or layoutPronounce or layoutPass
            SlidableLayout slidableLayout = (SlidableLayout) pressDownView;
            if (deltaY < 0) {
                slidableLayout.cancelSlide();
            }



        }


        previousX = currentX;
        previousY = currentY;
    }

    @Override
    public void onPressUp(final View pressDownView, MotionEvent event) {
        tryToHideTranslation();
    }

    @Override
    public void onHoverIn(View pressDownView, View viewOnHover, MotionEvent event) {
        switch (viewOnHover.getId()) {
            case R.id.layoutTranslation:
                if (!functionViews.contains(pressDownView)) {
                    lockerTranslation.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.lockerTranslation:
                if (!functionViews.contains(pressDownView)) {
                    if (hasMinorState(MIN_STATE_TRANSLATION_LOCKED)) {
                        removeMinorState(MIN_STATE_TRANSLATION_LOCKED);
                        lockerTranslation.setImageResource(R.drawable.ic_unlock_white_24dp);
                    } else {
                        addMinorState(MIN_STATE_TRANSLATION_LOCKED);
                        lockerTranslation.setImageResource(R.drawable.ic_lock_white_24dp);
                    }
                }
                break;
        }
    }

    @Override
    public void onHoverOut(View pressDownView, View viewOnHover, MotionEvent event) {
        switch (viewOnHover.getId()) {
            case R.id.layoutTranslation:
                if (!(functionViews.contains(pressDownView))) {
                    lockerTranslation.setVisibility(View.INVISIBLE);
                }
                break;
        }

    }

    @Override
    public boolean onHoverCancel(View pressDownView, View viewOnHover, MotionEvent event) {
        System.out.println("OnHoverCancel-" + viewOnHover);
        switch (viewOnHover.getId()) {
            case R.id.layoutTranslation:
                lockerTranslation.setVisibility(View.INVISIBLE);
                break;
        }
        return true;
    }

    @Override
    public void onSlide(SlidableLayout layout, float rateLeftRight, float rateUpDown) {
    }

    @Override
    public void onSlideTo(SlidableLayout layout, SlidableLayout.Direction direction) {

    }

    @Override
    public void onSlideCanceled(SlidableLayout layout) {

    }

    @Override
    public void onStartSlide(SlidableLayout layout) {

    }

    @Override
    public void onCancelSlide(SlidableLayout layout) {
    }

}
