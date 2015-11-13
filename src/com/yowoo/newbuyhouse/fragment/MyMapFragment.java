package com.yowoo.newbuyhouse.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

public class MyMapFragment extends SupportMapFragment{
	View mOriginalContentView;
	TouchableWrapper mTouchView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
	    mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);

	    mTouchView = new TouchableWrapper(getActivity());
	    mTouchView.addView(mOriginalContentView);

	    return mTouchView;
	}

	@Override
	public View getView() {
	    return mOriginalContentView;
	}
}
