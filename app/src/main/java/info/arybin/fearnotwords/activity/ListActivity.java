package info.arybin.fearnotwords.activity;


import android.os.Bundle;
import android.os.PersistableBundle;

import butterknife.ButterKnife;
import info.arybin.fearnotwords.R;

public class ListActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        initializeViews();
    }
}
