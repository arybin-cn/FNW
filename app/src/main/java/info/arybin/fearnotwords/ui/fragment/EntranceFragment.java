package info.arybin.fearnotwords.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.florent37.expectanim.ExpectAnim;
import com.ldoublem.loadingviewlib.view.LVGhost;

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
import info.arybin.fearnotwords.ui.view.SlideLayout;

import static com.github.florent37.expectanim.core.Expectations.aboveOf;
import static com.github.florent37.expectanim.core.Expectations.alpha;
import static com.github.florent37.expectanim.core.Expectations.atItsOriginalPosition;
import static com.github.florent37.expectanim.core.Expectations.rotated;
import static info.arybin.fearnotwords.Utils.retrieveAllChildViews;

public class EntranceFragment extends BaseFragment implements SlideLayout.OnSlideListener {

    public static int STATE_IDLE = 0;
    public static int STATE_LOADING = 1;

    private MainActivity mainActivity;
    private Random random = new Random();
    private AtomicBoolean canSlide = new AtomicBoolean(true);

    private View currentSliding;
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

    @BindView(R.id.separatorTop)
    public View separatorTop;
    @BindView(R.id.textViewPost)
    public TextView textViewPost;
    @BindView(R.id.textViewPostTranslation)
    public TextView textViewPostTranslation;
    @BindView(R.id.textViewUser)
    public TextView textViewUser;
    @BindView(R.id.separatorBottom)
    public View separatorBottom;

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

    private void initialize() {
        loadingAnimPre = new ExpectAnim().expect(loadingGhost)
                .toBe(aboveOf(blurView).withMarginDp(36), alpha(0), rotated(180))
                .toAnimation();

        loadingAnim = new ExpectAnim().expect(loadingGhost)
                .toBe(
                        rotated(0),
                        atItsOriginalPosition(),
                        alpha(0.6f)
                ).toAnimation();
        initializedViews();
    }

    private void initializedViews() {
        blurView.setupWith((ViewGroup) mainActivity.imageView.getParent()).blurRadius(BLUR_RADIUS);
        loadingAnimPre.setNow();
        mainActivity.imageViewBlurred.setAlpha(0);
        layoutEntranceNew.setOnSlideListener(this);
        layoutEntranceOld.setOnSlideListener(this);
        layoutEntranceAll.setOnSlideListener(this);
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
        ViewGroup parent = (ViewGroup) originLayout.getParent();
        for (int i = hierarchy; i > 1; i--) {
            parent = (ViewGroup) parent.getParent();
        }
        currentSlidingChildViews = retrieveAllChildViews(originLayout);
        LinkedList<View> childViews = retrieveAllChildViews(parent, originLayout);
        Iterator<View> iterator = childViews.iterator();
        while (iterator.hasNext()) {
            View view = iterator.next();
            transitionMap.put(view, 0f);
        }
    }

    private void tryToInitTransitionMap(SlideLayout originLayout, int hierarchy) throws Exception {
        if (hierarchy < 1) {
            throw new IllegalArgumentException("hierarchy must >= 1");
        }

        if (transitionMap.size() == 0 || currentSliding != originLayout) {
            tryToPrepareTransitionMap(originLayout, hierarchy);
            currentSliding = originLayout;
        }

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

    private void switchToLoadingState() {
        layoutEntrance.setVisibility(View.INVISIBLE);
        loadingGhost.startAnim(1200);
    }


    private void switchToIdleState() {
        layoutEntrance.setVisibility(View.VISIBLE);
        loadingAnimPre.setNow();
    }


    @Override
    public void onSlide(SlideLayout layout, float rate) {
        Iterator<View> i;
        View tmpView;
        float rateAbs = Math.abs(rate);
        float conjugateRateAbs = 1 - rateAbs;
        float conjugateRateAbs3 = conjugateRateAbs * conjugateRateAbs * conjugateRateAbs;
        mainActivity.imageViewBlurred.setAlpha(1 - conjugateRateAbs3);
        loadingAnim.setPercent(rateAbs);
        i = transitionMap.keySet().iterator();
        while (i.hasNext()) {
            tmpView = i.next();
            tmpView.setTranslationY(rateAbs * transitionMap.get(tmpView));
            tmpView.setAlpha(conjugateRateAbs3);
        }
        separatorBottom.setAlpha(conjugateRateAbs3);

        i = currentSlidingChildViews.iterator();
        while (i.hasNext()) {
            tmpView = i.next();
            tmpView.setAlpha(conjugateRateAbs);
        }


    }

    @Override
    public void onSlideToLeft(SlideLayout layout) {
        switchToLoadingState();
    }

    @Override
    public void onSlideToCenter(SlideLayout layout) {
        mainActivity.imageView.resume();
        blurView.setBlurAutoUpdate(true);
        if (layoutEntranceNew != layout) {
            layoutEntranceNew.setSlidable(true);
        }
        if (layoutEntranceOld != layout) {
            layoutEntranceOld.setSlidable(true);
        }
        if (layoutEntranceAll != layout) {
            layoutEntranceAll.setSlidable(true);
        }
        canSlide.set(true);
    }

    @Override
    public void onSlideToRight(SlideLayout layout) {
    }


    @Override
    public void onStartSlide(SlideLayout layout) {
        if (canSlide.compareAndSet(true, false)) {
            if (layoutEntranceNew != layout) {
                layoutEntranceNew.setSlidable(false);
            }
            if (layoutEntranceOld != layout) {
                layoutEntranceOld.setSlidable(false);
            }
            if (layoutEntranceAll != layout) {
                layoutEntranceAll.setSlidable(false);
            }
            initTransitionMap(layout);
            mainActivity.imageView.pause();
            blurView.setBlurAutoUpdate(false);
            mainActivity.imageViewBlurred.updateBlur();
        }
    }

    @Override
    public void onFinishSlide(SlideLayout layout) {

    }


}
