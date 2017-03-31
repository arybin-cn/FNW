package info.arybin.fearnotwords.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import info.arybin.fearnotwords.Config;
import info.arybin.fearnotwords.Constants;
import info.arybin.fearnotwords.fragment.BaseFragment;
import info.arybin.fearnotwords.ui.view.TextViewNonAscii;

public abstract class BaseActivity extends FragmentActivity implements Constants, Handler.Callback {

    private boolean initialized = false;
    private Handler handler = new Handler(this);

    private BaseFragment topFragment;

    protected WindowManager windowManager;
    protected AssetManager assetManager;
    protected FragmentManager fragmentManager;

    private SharedPreferences configs;

    protected ArrayList<Fragment> loadedFragments = new ArrayList<>();

    protected boolean initializedDatabase = false;

    @Override
    protected void onStart() {
        super.onStart();
        if (!initialized) {
            initialized = true;
            this.initialize();
        }
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
        if (textView instanceof TextViewNonAscii) {
            setTextViewFont(textView, readConfig(Config.FONT_NON_ASCII));
        } else {
            setTextViewFont(textView, readConfig(Config.FONT));
        }
    }

    private void initialize() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        fragmentManager = getSupportFragmentManager();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        assetManager = getAssets();
        configs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        initializeViews();
        initializeDatabase();
    }


    public void initializeViews() {
        initializeViews(getClass(), this);
    }

    public void initializeViews(Class<?> klass, Object instance) {
        try {
            tryToInitializeViews(klass, instance);
        } catch (Exception e) {
            //log something
            e.printStackTrace();
        }
    }

    private void tryToInitializeViews(Class<?> klass, Object instance) throws Exception {
        for (Field field : klass.getDeclaredFields()) {
            try {
                Object obj = field.get(instance);
                if (obj instanceof TextView) {
                    initializeTextView((TextView) obj);
                }
            } catch (IllegalAccessException ignored) {

            }

        }
    }

    private void setTextViewFont(TextView textView, String fontName) {
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

    protected final boolean databaseExist() {
        return getDatabasePath(readConfig(Config.DB_FILE)).exists();
    }

    protected final void releaseDatabase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] buf;
                    String dbFileName = readConfig(Config.DB_FILE);
                    File dbFile = getDatabasePath(dbFileName);
                    dbFile.getParentFile().mkdirs();
                    dbFile.createNewFile();
                    InputStream inputStream = assetManager.open("databases/" + dbFileName);
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

    protected Fragment loadFragment(int container, Class<? extends BaseFragment> fragment) {
        return loadFragment(container, fragment, null);
    }

    protected Fragment loadFragment(int container, Class<? extends BaseFragment> fragment, Bundle args) {
        try {
            return tryToLoadFragment(container, fragment, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Fragment tryToLoadFragment(int container, Class<? extends BaseFragment> fragmentClass, Bundle args) throws Exception {
        BaseFragment fragment = fragmentClass.newInstance();
        topFragment = fragment;
        fragment.setArguments(args);
        fragmentManager.beginTransaction().
                replace(container, fragment).
                commit();
        loadedFragments.add(fragment);
        return fragment;
    }


    public String readConfig(Config config) {
        if (null != configs) {
            return configs.getString(config.name(), config.getDefaultValue());
        }
        return null;
    }


    @Override
    public void onBackPressed() {
        boolean consumed = false;
        if (null != topFragment) {
            consumed = topFragment.onBackPressed();
        }
        if (!consumed) {
            super.onBackPressed();
        }
    }
}
