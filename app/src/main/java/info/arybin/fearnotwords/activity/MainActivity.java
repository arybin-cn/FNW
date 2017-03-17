package info.arybin.fearnotwords.activity;

import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.Transition;

import butterknife.BindView;
import butterknife.ButterKnife;
import eightbitlab.com.blurview.BlurView;
import info.arybin.fearnotwords.R;
import info.arybin.fearnotwords.util.SimpleTransitionGenerator;
import info.arybin.fearnotwords.view.FABRevealLayout;
import info.arybin.fearnotwords.view.SlideLayout;

public class MainActivity extends BaseActivity {

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

    @BindView(R.id.layoutMain)
    protected ViewGroup layoutMain;

    @BindView(R.id.layoutEntrance)
    protected ViewGroup layoutEntrance;

    @BindView(R.id.layoutSetting)
    protected ViewGroup layoutSetting;

    @BindView(R.id.layoutFabReveal)
    protected FABRevealLayout layoutFabReveal;

    @BindView(R.id.layoutImage)
    protected ViewGroup layoutImage;

    @BindView(R.id.textViewPost)
    protected TextView textViewPost;

    @BindView(R.id.textViewUser)
    protected TextView textViewUser;

    @BindView(R.id.blurView)
    protected BlurView blurView;

    @BindView(R.id.imageView)
    protected KenBurnsView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initializeViews();
        test();
    }

    private void test() {
    }

    @Override
    protected void initializeViews() {
        super.initializeViews();
        blurView.setupWith((ViewGroup) imageView.getParent()).blurRadius(BLUR_RADIUS);
        layoutSetting.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                layoutFabReveal.revealMainView();
                return true;
            }
        });

        imageView.setTransitionGenerator(new SimpleTransitionGenerator(0.15f, 5));


    }

}