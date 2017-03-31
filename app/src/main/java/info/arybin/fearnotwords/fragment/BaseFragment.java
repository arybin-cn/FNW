package info.arybin.fearnotwords.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import info.arybin.fearnotwords.Config;
import info.arybin.fearnotwords.Constants;
import info.arybin.fearnotwords.activity.BaseActivity;


public abstract class BaseFragment extends Fragment implements Constants {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((BaseActivity) getActivity()).initializeViews(getClass(), this);
    }


    /**
     * @return true if the back press event is consumed, false otherwise.
     */
    abstract public boolean onBackPressed();


    protected String readConfig(Config config) {
        return ((BaseActivity) getActivity()).readConfig(config);
    }

}
