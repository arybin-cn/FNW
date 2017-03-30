package info.arybin.fearnotwords.activity;

import android.os.Bundle;
import android.view.ViewGroup;

import com.flaviofaria.kenburnsview.KenBurnsView;

import butterknife.BindView;
import butterknife.ButterKnife;
import eightbitlab.com.blurview.BlurView;
import info.arybin.fearnotwords.R;
import info.arybin.fearnotwords.ui.anim.SimpleTransitionGenerator;
import info.arybin.fearnotwords.fragment.EntranceFragment;

public class MainActivity extends BaseActivity {


    @BindView(R.id.blurredLayer)
    public BlurView imageViewBlurred;

    @BindView(R.id.layer)
    public KenBurnsView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @Override
    public void initializeViews() {
        super.initializeViews();
        imageView.setTransitionGenerator(new SimpleTransitionGenerator(0.25f, 5));
        imageViewBlurred.setupWith((ViewGroup) imageView.getParent()).blurRadius(BLUR_RADIUS);
        imageViewBlurred.setBlurAutoUpdate(false);
        loadFragment(R.id.layoutFragmentContainer, EntranceFragment.class);
    }

}
