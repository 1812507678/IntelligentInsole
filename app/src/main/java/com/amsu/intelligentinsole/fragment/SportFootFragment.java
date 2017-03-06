package com.amsu.intelligentinsole.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amsu.intelligentinsole.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SportFootFragment extends Fragment {


    public SportFootFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sport_foot, container, false);
    }

}
