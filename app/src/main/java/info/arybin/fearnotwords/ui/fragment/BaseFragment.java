package info.arybin.fearnotwords.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import info.arybin.fearnotwords.Constants;
import info.arybin.fearnotwords.R;
import info.arybin.fearnotwords.activity.BaseActivity;


public abstract class BaseFragment extends Fragment implements Constants {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((BaseActivity) getActivity()).initializeViews(getClass(), this);
    }


    protected void loadLoadingFragment(Bundle arguments) {
        LoadingFragment fragment = new LoadingFragment();
        fragment.setArguments(arguments);
        getFragmentManager().beginTransaction().
                addToBackStack(null).
                replace(R.id.layoutFragmentContainer, fragment).
                commit();
    }


}
