package com.yowoo.newbuyhouse.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yowoo.newbuyhouse.Constants;
import com.yowoo.newbuyhouse.MainActivity;
import com.thinkermobile.sinyi.R;

public class MainChatFragment extends MyFragment{

	public static Fragment newInstance(Context context) {
		MainChatFragment f = new MainChatFragment();
 
        return f;
    }
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_main_price, null);
        
        
        return root;
    }
    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(Constants.FRAGMENT_ARG_POSITION));
	}
    
    
    
}
