package info.arybin.fearnotwords.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.lang.reflect.Field;

import info.arybin.fearnotwords.Constants;

public abstract class BaseActivity extends AppCompatActivity implements Constants {
    protected WindowManager windowManager;
    protected AssetManager assetManager;
    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initialize();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void initializeTextView(TextView textView) {
        setTextViewFont(textView, sharedPreferences.getString(KEY_FONT, DEF_FONT));
    }


    private void initialize() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        assetManager = getAssets();
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
    }


    protected void initializeViews() {
        try {
            tryToInitializeViews();
        } catch (Exception e) {
            //log something
            e.printStackTrace();
        }
    }

    private void tryToInitializeViews() throws Exception {
        Class<? extends BaseActivity> klass = getClass();
        for (Field field : klass.getDeclaredFields()) {
            if (TextView.class.equals(field.getType())) {
                initializeTextView((TextView) field.get(this));
            }
        }
    }

    protected void setTextViewFont(TextView textView, String fontName) {
        Typeface tf = Typeface.createFromAsset(assetManager, "fonts/" + fontName);
        textView.setTypeface(tf);
    }

    protected final int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    protected final int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /**
     * Note: Ignore when given the negative value(eg -1).
     */
    protected final void setViewBound(View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (width >= 0) {
            layoutParams.width = width;
        }
        if (height >= 0) {
            layoutParams.height = height;
        }
        view.setLayoutParams(layoutParams);
    }

}
