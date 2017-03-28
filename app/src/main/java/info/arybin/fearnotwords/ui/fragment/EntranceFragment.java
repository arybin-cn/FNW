package info.arybin.fearnotwords.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import eightbitlab.com.blurview.BlurView;
import info.arybin.fearnotwords.R;
import info.arybin.fearnotwords.activity.MainActivity;
import info.arybin.fearnotwords.ui.view.FABRevealLayout;
import info.arybin.fearnotwords.ui.view.SlideLayout;

public class EntranceFragment extends BaseFragment {

    private MainActivity mainActivity;

    @BindView(R.id.blurView)
    protected BlurView blurView;

    @BindView(R.id.floatingActionButton)
    protected FloatingActionButton floatingActionButton;

    @BindView(R.id.textViewAllCount)
    protected TextView textViewAllCount;

    @BindView(R.id.textViewEntranceAll)
    protected TextView textViewEntranceAll;

    @BindView(R.id.textViewOldCount)
    protected TextView textViewOldCount;

    @BindView(R.id.textViewEntranceOld)
    protected TextView textViewEntranceOld;

    @BindView(R.id.layoutEntranceNew)
    protected SlideLayout layoutEntranceNew;

    @BindView(R.id.textViewNewCount)
    protected TextView textViewNewCount;

    @BindView(R.id.textViewEntranceNew)
    protected TextView textViewEntranceNew;


    @BindView(R.id.layoutEntrance)
    protected ViewGroup layoutEntrance;

    @BindView(R.id.layoutSetting)
    protected ViewGroup layoutSetting;

    @BindView(R.id.layoutFabReveal)
    protected FABRevealLayout layoutFabReveal;

    @BindView(R.id.textViewPost)
    protected TextView textViewPost;

    @BindView(R.id.textViewUser)
    protected TextView textViewUser;


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
        layoutSetting.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                layoutFabReveal.revealMainView();
                return true;
            }
        });

        layoutEntranceNew.setOnSlideListener(new SlideLayout.OnSlideListener() {
            @Override
            public void onSlideToLeft(SlideLayout layout) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.layoutFragment, new MemorizeFragment());
                transaction.commit();
            }

            @Override
            public void onSlideToRight(SlideLayout layout) {
            }

            @Override
            public void onSlide(float rate) {
//                floatingActionButton.setY(floatingActionButton.getY() + 100 * rate);
                mainActivity.imageViewBlurred.setAlpha(Math.abs(rate * 1.5f));


            }

            @Override
            public void onStartSlide() {
                mainActivity.imageView.pause();
                blurView.setBlurAutoUpdate(false);
                mainActivity.imageViewBlurred.updateBlur();


            }

            @Override
            public void onFinishSlide() {
                mainActivity.imageView.resume();
                blurView.setBlurAutoUpdate(true);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        initialize();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
