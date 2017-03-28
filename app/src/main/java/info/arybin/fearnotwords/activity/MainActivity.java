package info.arybin.fearnotwords.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.florent37.expectanim.ExpectAnim;

import butterknife.BindView;
import butterknife.ButterKnife;
import eightbitlab.com.blurview.BlurView;
import info.arybin.fearnotwords.R;
import info.arybin.fearnotwords.ui.anim.SimpleTransitionGenerator;
import info.arybin.fearnotwords.ui.view.FABRevealLayout;
import info.arybin.fearnotwords.ui.view.SlideLayout;

import static com.github.florent37.expectanim.core.Expectations.alpha;
import static com.github.florent37.expectanim.core.Expectations.centerInParent;
import static com.github.florent37.expectanim.core.Expectations.sameCenterVerticalAs;
import static com.github.florent37.expectanim.core.Expectations.toRightOf;

public class MainActivity extends BaseActivity {


    ExpectAnim anim;


    @BindView(R.id.imageViewBlurred)
    protected BlurView imageViewBlurred;

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

    @BindView(R.id.layoutMain)
    protected ViewGroup layoutMain;

    @BindView(R.id.layoutEntrance)
    protected ViewGroup layoutEntrance;

    @BindView(R.id.layoutSetting)
    protected ViewGroup layoutSetting;

    @BindView(R.id.layoutFabReveal)
    protected FABRevealLayout layoutFabReveal;

    @BindView(R.id.layoutBlur)
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
    }


    @Override
    protected void initializeViews() {
        super.initializeViews();
        imageView.setTransitionGenerator(new SimpleTransitionGenerator(0.25f, 5));
        blurView.setupWith((ViewGroup) imageView.getParent()).blurRadius(BLUR_RADIUS);
        imageViewBlurred.setupWith((ViewGroup) imageView.getParent()).blurRadius(BLUR_RADIUS);
        imageViewBlurred.setBlurAutoUpdate(false);
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

//                blurView.setBlurAutoUpdate(false);

            }

            @Override
            public void onSlideToRight(SlideLayout layout) {

//                Intent i = new Intent(MainActivity.this, MemorizeActivity.class);
//                startActivity(i);

            }

            @Override
            public void onSlide(float rate) {
//                floatingActionButton.setY(floatingActionButton.getY() + 100 * rate);
                imageViewBlurred.setAlpha(Math.abs(rate * 1.5f));

                    anim.setPercent(Math.abs(rate));



            }

            @Override
            public void onStartSlide() {
                imageView.pause();
                blurView.setBlurAutoUpdate(false);
                imageViewBlurred.updateBlur();
                anim = new ExpectAnim()
                        .expect(textViewPost)
                        .toBe(
                                centerInParent(true, true)
                        ).toAnimation();


            }

            @Override
            public void onFinishSlide() {
                imageView.resume();
                blurView.setBlurAutoUpdate(true);
            }
        });


    }

}
