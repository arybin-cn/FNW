package info.arybin.fearnotwords.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldoublem.loadingviewlib.view.LVGhost;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.arybin.fearnotwords.R;


public class LoadingFragment extends Fragment {

    @BindView(R.id.loadingGhost)
    protected LVGhost loadingGhost;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        ButterKnife.bind(this, view);


        loadingGhost.startAnim(1200);

        return view;
    }


}
