package info.arybin.fearnotwords.application;

import android.app.Application;

import com.wonderkiln.blurkit.BlurKit;

import org.litepal.LitePal;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        BlurKit.init(this);
    }
}
