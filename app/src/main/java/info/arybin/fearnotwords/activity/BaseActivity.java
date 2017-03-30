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
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import info.arybin.fearnotwords.Constants;
import info.arybin.fearnotwords.ui.view.TextViewNonAscii;

public abstract class BaseActivity extends FragmentActivity implements Constants, Handler.Callback {

    private boolean initialized = false;
    private Handler handler = new Handler(this);
    protected WindowManager windowManager;
    protected AssetManager assetManager;
    protected SharedPreferences configs;

    protected ArrayList<Fragment> loadedFragments = new ArrayList<>();

    private boolean initializedDatabase = false;

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
            setTextViewFont(textView, configs.getString(KEY_FONT_NON_ASCII, DEF_FONT_NON_ASCII));
        } else {
            setTextViewFont(textView, configs.getString(KEY_FONT, DEF_FONT));
        }
    }

    private void initialize() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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

    protected Fragment loadFragment(int container, Class<? extends Fragment> fragment) {
        return loadFragment(container, fragment, null);
    }

    protected Fragment loadFragment(int container, Class<? extends Fragment> fragment, Bundle args) {
        try {
            return tryToLoadFragment(container, fragment, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Fragment tryToLoadFragment(int container, Class<? extends Fragment> fragmentClass, Bundle args) throws Exception {
        Fragment fragment = fragmentClass.newInstance();
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().
                add(container, fragment).
                commit();
        loadedFragments.add(fragment);
        return fragment;
    }


    protected Fragment unloadFragment(Fragment fragment) {
        Iterator<Fragment> i = loadedFragments.iterator();
        while (i.hasNext()) {
            Fragment fragmentTmp = i.next();
            if (fragmentTmp == fragment) {
                getSupportFragmentManager().beginTransaction().
                        remove(fragment).
                        commit();
            }
        }


        return null;
    }


}
