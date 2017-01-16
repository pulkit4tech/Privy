package com.pulkit4tech.privy.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pulkit4tech.privy.R;

public class NoLocationPermission extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.error_msg_layout, container, false);
        TextView errorMsg = (TextView) view.findViewById(R.id.error_msg);
        errorMsg.setText(getString(R.string.location_permission_failed));
        return view;
    }
}
