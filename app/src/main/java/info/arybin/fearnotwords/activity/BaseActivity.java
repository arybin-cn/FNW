package info.arybin.fearnotwords.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import info.arybin.fearnotwords.Constants;

public abstract class BaseActivity extends AppCompatActivity implements Constants, Handler.Callback {

    private Handler handler = new Handler(this);
    protected WindowManager windowManager;
    protected AssetManager assetManager;
    protected SharedPreferences configs;

    private boolean initializedDatabase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initialize();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_INITIALIZED_DB:
                initializedDatabase = true;
                break;
        }

        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void initializeTextView(TextView textView) {
        setTextViewFont(textView, configs.getString(KEY_FONT, DEF_FONT));
    }

    private void initialize() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        assetManager = getAssets();
        configs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        initializeDatabase();
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

    private void initializeDatabase() {
        if (databaseExist()) {
            initializedDatabase = true;
            return;
        }
        releaseDatabase();
    }

    protected boolean initializedDatabase() {
        return initializedDatabase;
    }


    protected final boolean databaseExist() {
        return getDatabasePath(DEF_DB_FILE).exists();
    }

    protected final void releaseDatabase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] buf;
                    File dbFile = getDatabasePath(DEF_DB_FILE);
                    dbFile.getParentFile().mkdirs();
                    dbFile.createNewFile();
                    InputStream inputStream = assetManager.open("databases/" + DEF_DB_FILE);
                    buf = new byte[inputStream.available()];
                    FileOutputStream fileOutputStream = new FileOutputStream(dbFile);
                    while (inputStream.read(buf) != -1) {
                        fileOutputStream.write(buf);
                    }
                    fileOutputStream.close();
                    inputStream.close();
                    handler.sendEmptyMessage(MSG_INITIALIZED_DB);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
