package info.arybin.fearnotwords.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.SoundPool;
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
import java.util.HashMap;

import info.arybin.fearnotwords.Config;
import info.arybin.fearnotwords.Constants;
import info.arybin.fearnotwords.R;
import info.arybin.fearnotwords.fragment.BaseFragment;
import info.arybin.fearnotwords.ui.view.textview.TextViewAscii;
import info.arybin.fearnotwords.ui.view.textview.TextViewNonAscii;
import info.arybin.fearnotwords.ui.view.textview.TextViewPhonetic;

import static android.media.AudioManager.STREAM_MUSIC;

public abstract class BaseActivity extends FragmentActivity implements Constants, Handler.Callback {

    private boolean initialized = false;
    private Handler handler = new Handler(this);

    private BaseFragment lastLoadedFragment;

    protected WindowManager windowManager;
    protected AssetManager assetManager;
    protected FragmentManager fragmentManager;

    protected SoundPool soundPool;
    protected HashMap<String, Integer> soundMap = new HashMap<>();
    private SharedPreferences configs;
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
        } else if (textView instanceof TextViewAscii) {
            setTextViewFont(textView, readConfig(Config.FONT_ASCII));
        } else if (textView instanceof TextViewPhonetic) {
            setTextViewFont(textView, readConfig(Config.FONT_PHONETIC));
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
        initializeSounds();
    }


    private void initializeSounds() {
        try {
            tryToInitializeSounds();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tryToInitializeSounds() throws Exception {
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(new AudioAttributes.Builder().setLegacyStreamType(STREAM_MUSIC).build())
                .build();
        Class<R.raw> rawClass = R.raw.class;
        for (Field field : rawClass.getDeclaredFields()) {
            if (field.getName().startsWith(PREFIX_SOUND)) {
                soundMap.put(field.getName().substring(PREFIX_SOUND.length()),
                        soundPool.load(this, field.getInt(null), 1));
            }
        }
    }

    public boolean playSound(String name) {
        Integer soundID = soundMap.get(name);
        return null != soundID && 0 != soundPool.play(soundID, 1, 1, 1, 0, 1);
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

    public Fragment loadFragment(int container, Class<? extends BaseFragment> fragment, Bundle args) {
        try {
            return tryToLoadFragment(container, fragment, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Fragment tryToLoadFragment(int container, Class<? extends BaseFragment> fragmentClass, Bundle args) throws Exception {
        BaseFragment fragment = fragmentClass.newInstance();
        lastLoadedFragment = fragment;
        fragment.setArguments(args);
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(container, fragment)
                .commit();
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
        if (null != lastLoadedFragment) {
            consumed = lastLoadedFragment.onBackPressed();
        }
        if (!consumed) {
            super.onBackPressed();
        }
    }


}
