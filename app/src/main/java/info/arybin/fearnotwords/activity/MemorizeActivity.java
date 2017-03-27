package info.arybin.fearnotwords.activity;


import android.os.Bundle;
import android.os.PersistableBundle;

import butterknife.ButterKnife;
import info.arybin.fearnotwords.R;

public class MemorizeActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorize);
        ButterKnife.bind(this);
        initializeViews();

    }
}
