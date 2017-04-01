package info.arybin.fearnotwords.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.florent37.expectanim.ExpectAnim;
import com.github.florent37.expectanim.listener.AnimationEndListener;
import com.ldoublem.loadingviewlib.view.LVGhost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import eightbitlab.com.blurview.BlurView;
import info.arybin.fearnotwords.R;
import info.arybin.fearnotwords.activity.MainActivity;
import info.arybin.fearnotwords.model.FakeEntity;
import info.arybin.fearnotwords.ui.view.SlideLayout;

import static com.github.florent37.expectanim.core.Expectations.aboveOf;
import static com.github.florent37.expectanim.core.Expectations.alpha;
import static com.github.florent37.expectanim.core.Expectations.atItsOriginalPosition;
import static com.github.florent37.expectanim.core.Expectations.invisible;
import static com.github.florent37.expectanim.core.Expectations.rotated;
import static com.github.florent37.expectanim.core.Expectations.visible;
import static info.arybin.fearnotwords.Utils.retrieveAllChildViews;

public class EntryFragment extends BaseFragment implements
        SlideLayout.OnSlideListener, View.OnClickListener, Handler.Callback {

    public static final int STATE_IDLE = 0;
    public static final int STATE_LOADING = 1;

    private int state = 0;
    private MainActivity mainActivity;
    private Random random = new Random();

    private AtomicBoolean canSwitchSlide = new AtomicBoolean(true);

    private LinkedList<View> currentSlidingChildViews = new LinkedList<>();
    private HashMap<View, Float> transitionMap = new HashMap<>();

    private ExpectAnim loadingAnimPre;
    private ExpectAnim loadingAnim;

    @BindView(R.id.blurView)
    public BlurView blurView;

    @BindView(R.id.loadingGhost)
    protected LVGhost loadingGhost;

    @BindView(R.id.layoutEntrance)
    public LinearLayout layoutEntrance;

    @BindView(R.id.layoutEntranceNew)
    public SlideLayout layoutEntranceNew;
    @BindView(R.id.textViewNewCount)
    public TextView textViewNewCount;
    @BindView(R.id.textViewEntranceNew)
    public TextView textViewEntranceNew;

    @BindView(R.id.layoutEntranceOld)
    public SlideLayout layoutEntranceOld;
    @BindView(R.id.textViewOldCount)
    public TextView textViewOldCount;
    @BindView(R.id.textViewEntranceOld)
    public TextView textViewEntranceOld;

    @BindView(R.id.layoutEntranceAll)
    public SlideLayout layoutEntranceAll;
    @BindView(R.id.textViewAllCount)
    public TextView textViewAllCount;
    @BindView(R.id.textViewEntranceAll)
    public TextView textViewEntranceAll;

    @BindView(R.id.layoutPost)
    public RelativeLayout layoutPost;
    @BindView(R.id.textViewPost)
    public TextView textViewPost;
    @BindView(R.id.textViewPostTranslation)
    public TextView textViewPostTranslation;
    @BindView(R.id.textViewUser)
    public TextView textViewUser;
    @BindView(R.id.separatorBottom)
    public View separatorBottom;

    private Handler handler = new Handler(this);


    private void initialize() {
        loadingAnimPre = new ExpectAnim().expect(loadingGhost)
                .toBe(aboveOf(blurView).withMarginDp(36), alpha(0), rotated(180))
                .toAnimation();
        loadingAnim = new ExpectAnim().expect(loadingGhost)
                .toBe(rotated(0), atItsOriginalPosition(), alpha(0.6f))
                .toAnimation();
        initializedViews();
    }

    private void initializedViews() {

//        blurView.setOverlayColor(Color.argb(64, 0, 0, 0));

        blurView.setupWith((ViewGroup) mainActivity.imageView.getParent()).blurRadius(BLUR_RADIUS);
        loadingAnimPre.setNow();
        mainActivity.imageViewBlurred.setAlpha(0);

        layoutEntranceNew.setOnSlideListener(this);
        layoutEntranceNew.setIgnoreLeft(true);

        layoutEntranceOld.setOnSlideListener(this);
        layoutEntranceOld.setIgnoreLeft(true);

        layoutEntranceAll.setOnSlideListener(this);
        layoutEntranceAll.setIgnoreLeft(true);

        //For test
        layoutPost.setOnClickListener(this);

    }

    private void initTransitionMap(SlideLayout originLayout) {
        initTransitionMap(originLayout, 1);

    }

    private void initTransitionMap(SlideLayout originLayout, int hierarchy) {
        try {
            tryToInitTransitionMap(originLayout, hierarchy);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void tryToPrepareTransitionMap(SlideLayout originLayout, int hierarchy) throws Exception {
        transitionMap.clear();
        currentSlidingChildViews = retrieveAllChildViews(originLayout);
        ViewGroup parent = (ViewGroup) originLayout.getParent();
        for (int i = hierarchy; i > 1; i--) {
            parent = (ViewGroup) parent.getParent();
        }
        LinkedList<View> childViews = retrieveAllChildViews(parent, originLayout);
        for (View view : childViews) {
            transitionMap.put(view, 0f);
        }

    }

    private void tryToInitTransitionMap(SlideLayout originLayout, int hierarchy) throws Exception {
        if (hierarchy < 1) {
            throw new IllegalArgumentException("hierarchy must >= 1");
        }

        tryToPrepareTransitionMap(originLayout, hierarchy);

        for (View view : transitionMap.keySet()) {
            int posOriginView[] = new int[2];
            originLayout.getLocationOnScreen(posOriginView);
            int posView[] = new int[2];
            view.getLocationOnScreen(posView);

            float maxDistance = 100f;
            int direction = (posView[1] - posOriginView[1]);
            direction = direction > 1 ? 1 : -1;
            transitionMap.put(view, direction * maxDistance * (0.5f + 1.5f * random.nextFloat()));
        }

    }

    private void setTransitionMapPercentage(float percent) {
        Iterator<View> i;
        View tmpView;
        float rateAbs = Math.abs(percent);
        float conjugateRateAbs = 1 - rateAbs;
        float conjugateRateAbs3 = conjugateRateAbs * conjugateRateAbs * conjugateRateAbs;
        i = transitionMap.keySet().iterator();
        while (i.hasNext()) {
            tmpView = i.next();
            tmpView.setTranslationY(rateAbs * transitionMap.get(tmpView));
            tmpView.setAlpha(conjugateRateAbs3);
        }
        i = currentSlidingChildViews.iterator();
        while (i.hasNext()) {
            tmpView = i.next();
            tmpView.setAlpha(conjugateRateAbs);
        }
    }

    private void switchToLoadingState() {
        setSlidable(false, null);
        canSwitchSlide.set(false);
        state = STATE_LOADING;
        loadingGhost.startAnim(1200);
        new ExpectAnim()
                .expect(layoutEntrance).toBe(invisible())
                .toAnimation()
                .setDuration(300)
                .setEndListener(new AnimationEndListener() {
                    @Override
                    public void onAnimationEnd(ExpectAnim expectAnim) {
                        setTransitionMapPercentage(0);
                    }
                })
                .start();
    }

    private void abortLoading() {
        new ExpectAnim()
                .expect(loadingGhost).toBe(invisible())
                .expect(separatorBottom).toBe(alpha(1))
                .expect(mainActivity.imageViewBlurred).toBe(alpha(0))
                .expect(layoutEntrance).toBe(visible())
                .toAnimation()
                .setDuration(1000)
                .setEndListener(new AnimationEndListener() {
                    @Override
                    public void onAnimationEnd(ExpectAnim expectAnim) {
                        mainActivity.imageView.resume();
                        finishLoading();
                    }
                }).start();

    }

    private void finishLoading() {
        loadingGhost.setAlpha(0);
        loadingGhost.stopAnim();
        state = STATE_IDLE;
        setSlidable(true, null);
        canSwitchSlide.set(true);
    }


    private void setSlidable(boolean slidable, SlideLayout exception) {
        if (layoutEntranceNew != exception) {
            layoutEntranceNew.setSlidable(slidable);
        }
        if (layoutEntranceOld != exception) {
            layoutEntranceOld.setSlidable(slidable);
        }
        if (layoutEntranceAll != exception) {
            layoutEntranceAll.setSlidable(slidable);
        }
    }


    private void prepareToLoadNew() {
        switchToLoadingState();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<FakeEntity> list = new ArrayList<>(300);
                    for (int i = 0; i < 300; i++) {
                        list.add(new FakeEntity(i));
                    }
                    Thread.sleep(1000);
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putParcelableArrayList(KEY_LOADED_MEMORABLE, list);
                    msg.setData(data);
                    msg.what = MSG_LOADED;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        initialize();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrance, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public boolean onBackPressed() {
        switch (state) {
            case STATE_IDLE:
                return false;
            case STATE_LOADING:
                abortLoading();
                return true;
            default:
                return false;
        }
    }


    @Override
    public void onSlide(SlideLayout layout, float rate) {
        float rateAbs = Math.abs(rate);
        float conjugateRateAbs = 1 - rateAbs;
        float conjugateRateAbs3 = conjugateRateAbs * conjugateRateAbs * conjugateRateAbs;
        mainActivity.imageViewBlurred.setAlpha(1 - conjugateRateAbs3);
        setTransitionMapPercentage(rate);
        loadingAnim.setPercent(rateAbs);
        separatorBottom.setAlpha(conjugateRateAbs3);
    }

    @Override
    public void onSlideToLeft(SlideLayout layout) {


    }

    @Override
    public void onSlideToCenter(SlideLayout layout) {
        mainActivity.imageView.resume();
        blurView.setBlurAutoUpdate(true);
        setSlidable(true, null);
        canSwitchSlide.set(true);
    }

    @Override
    public void onSlideToRight(SlideLayout layout) {
        prepareToLoadNew();
    }

    @Override
    public void onStartSlide(SlideLayout layout) {
        if (canSwitchSlide.compareAndSet(true, false)) {
            initTransitionMap(layout);
            setSlidable(false, layout);
            mainActivity.imageView.pause();
            blurView.setBlurAutoUpdate(false);
            mainActivity.imageViewBlurred.updateBlur();
        }
    }

    @Override
    public void onFinishSlide(SlideLayout layout) {

    }


    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_LOADED:
                loadFragment(R.id.layoutFragmentContainer, MemorizeFragment.class, msg.getData());
                finishLoading();
                break;
            default:

        }
        return false;
    }
}
