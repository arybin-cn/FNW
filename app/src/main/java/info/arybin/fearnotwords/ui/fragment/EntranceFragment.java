package info.arybin.fearnotwords.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import eightbitlab.com.blurview.BlurView;
import info.arybin.fearnotwords.R;
import info.arybin.fearnotwords.activity.MainActivity;
import info.arybin.fearnotwords.ui.view.SlideLayout;

public class EntranceFragment extends BaseFragment implements SlideLayout.OnSlideListener {
    private Random random = new Random();
    private HashMap<View, Float> transitionMap = new HashMap<>();

    private MainActivity mainActivity;
    private View transitionOriginView = null;

    @BindView(R.id.blurView)
    public BlurView blurView;

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

    @BindView(R.id.textViewPost)
    public TextView textViewPost;
    @BindView(R.id.textViewPostTranslation)
    public TextView textViewPostTranslation;
    @BindView(R.id.textViewUser)
    public TextView textViewUser;

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
        initializedViews();
    }

    private void initializedViews() {
        blurView.setupWith((ViewGroup) mainActivity.imageView.getParent()).blurRadius(BLUR_RADIUS);
        mainActivity.imageViewBlurred.setAlpha(0);

        layoutEntranceNew.setOnSlideListener(this);
        layoutEntranceOld.setOnSlideListener(this);
        layoutEntranceAll.setOnSlideListener(this);
    }

    private void initTransitionMap(ViewGroup originView) {
        initTransitionMap(originView, 3);

    }

    private void initTransitionMap(View originView, int hierarchy) {
        try {
            tryToInitTransitionMap(originView, hierarchy);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void tryToInitTransitionMap(View originView, int hierarchy) throws Exception {
        if (hierarchy < 1) {
            throw new IllegalArgumentException("hierarchy must >= 1");
        }
        if (transitionMap.size() == 0 || transitionOriginView != originView) {
            tryToPrepareTransitionMap(originView, hierarchy);
            transitionOriginView = originView;
        }

        for (View view : transitionMap.keySet()) {
            int posOriginView[] = new int[2];
            originView.getLocationOnScreen(posOriginView);
            int posView[] = new int[2];
            view.getLocationOnScreen(posView);

            float maxDistance = 100f;
            int direction = (posView[1] - posOriginView[1]);
            direction = direction > 1 ? 1 : -1;
            transitionMap.put(view, direction * maxDistance * (0.45f + 0.9f * random.nextFloat()));
        }

    }

    private void tryToPrepareTransitionMap(View originView, int hierarchy) throws Exception {
        transitionMap.clear();
        ViewGroup parent = (ViewGroup) originView.getParent();
        for (int i = hierarchy; i > 1; i--) {
            parent = (ViewGroup) parent.getParent();
        }
        ArrayDeque<View> queue = new ArrayDeque<>();
        queue.add(parent);
        while (queue.size() > 0) {
            View view = queue.poll();
            if (view instanceof ViewGroup) {
                if (view != originView) {
                    ViewGroup tmpGroup = (ViewGroup) view;
                    int childCount = tmpGroup.getChildCount();
                    for (int j = 0; j < childCount; j++) {
                        queue.add(tmpGroup.getChildAt(j));
                    }
                }
                continue;
            }
            transitionMap.put(view, 0f);
        }

    }

    @Override
    public void onSlideToLeft(SlideLayout layout) {

    }

    @Override
    public void onSlideToCenter(SlideLayout layout) {
        if (layoutEntranceNew != layout) {
            layoutEntranceNew.setSlideable(true);
        }
        if (layoutEntranceOld != layout) {
            layoutEntranceOld.setSlideable(true);
        }
        if (layoutEntranceAll != layout) {
            layoutEntranceAll.setSlideable(true);
        }
    }

    @Override
    public void onSlideToRight(SlideLayout layout) {
        System.out.println(111);
        getFragmentManager().beginTransaction().
                addToBackStack(null).
                replace(R.id.layoutFragmentContainer, new LoadingFragment()).
                commit();
    }

    @Override
    public void onSlide(SlideLayout layout, float rate) {
        float rateAbs = Math.abs(rate);
        float conjugateRateAbs = 1 - rateAbs;
        float conjugateRateAbs3 = conjugateRateAbs * conjugateRateAbs * conjugateRateAbs;
        mainActivity.imageViewBlurred.setAlpha(1 - conjugateRateAbs3);
        blurView.setAlpha(conjugateRateAbs);
        Iterator<View> i = transitionMap.keySet().iterator();
        while (i.hasNext()) {
            View view = i.next();
            view.setTranslationY(rateAbs * transitionMap.get(view));
            view.setAlpha(conjugateRateAbs3 * conjugateRateAbs);
        }
    }

    @Override
    public void onStartSlide(SlideLayout layout) {
        if (layoutEntranceNew != layout) {
            layoutEntranceNew.setSlideable(false);
        }
        if (layoutEntranceOld != layout) {
            layoutEntranceOld.setSlideable(false);
        }
        if (layoutEntranceAll != layout) {
            layoutEntranceAll.setSlideable(false);
        }
        mainActivity.imageView.pause();
        blurView.setBlurAutoUpdate(false);
        mainActivity.imageViewBlurred.updateBlur();
        initTransitionMap(layout);
    }

    @Override
    public void onFinishSlide(SlideLayout layout) {
        mainActivity.imageView.resume();
        blurView.setBlurAutoUpdate(true);
    }

}
