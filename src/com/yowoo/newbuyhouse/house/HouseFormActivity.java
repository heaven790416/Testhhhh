package com.yowoo.newbuyhouse.house;

import java.util.Calendar;
import java.util.HashMap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.network.SinyiService;
import com.yowoo.newbuyhouse.network.SinyiService.FormCallback;
import com.yowoo.newbuyhouse.util.DatePickerFragment;
import com.yowoo.newbuyhouse.util.DatePickerFragment.DatePickerListener;

public class HouseFormActivity extends BaseActivity{
	
	TextView dateTextView;
	EditText nameEditText, phoneEditText, emailEditText;
	RadioButton maleRadioButton, femaleRadioButton;
	Button sendButton;
	TextView introTextView, lawTextView;
	ImageView checkImageView;
	
	String houseNO = "";
	String houseName = "";
	String formType = "";
	
	int year = 0;
    int month = 0;
    int day = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_house_form);
		
		//set toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_selector);
        toolbar.setTitleTextColor(getResources().getColor(R.color.title_text_color));
        TextView mTitleTextView = (TextView)toolbar.findViewById(R.id.mTitleTextView);
        mTitleTextView.setVisibility(View.VISIBLE);
        ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("");
		
		//GET EXTRA
		try{
			houseNO = getIntent().getExtras().getString(BHConstants.EXTRA_HOUSE_NO);
		}catch (Exception e){}
		try{
			houseName = getIntent().getExtras().getString(BHConstants.EXTRA_HOUSE_NAME);
		}catch (Exception e){}
		try{
			formType = getIntent().getExtras().getString(BHConstants.EXTRA_FORM_TYPE);
		}catch (Exception e){}
		
		//set title
		if (formType.equals(BHConstants.FORM_MESSAGE_TYPE_RESERVATION)){
			mTitleTextView.setText(getString(R.string.reservation));
		}else if (formType.equals(BHConstants.FORM_MESSAGE_TYPE_SALESMAN)){
			mTitleTextView.setText(getString(R.string.call_salesman));
		}
		
		//set views
		nameEditText = (EditText) findViewById(R.id.nameEditText);
		dateTextView = (TextView) findViewById(R.id.dateTextView);
		phoneEditText = (EditText) findViewById(R.id.phoneEditText);
		emailEditText = (EditText) findViewById(R.id.emailEditText);
		maleRadioButton = (RadioButton) findViewById(R.id.maleRadioButton);
		femaleRadioButton = (RadioButton) findViewById(R.id.femaleRadioButton);
		sendButton = (Button) findViewById(R.id.sendButton);

		introTextView = (TextView) findViewById(R.id.introTextView);
		lawTextView = (TextView) findViewById(R.id.lawTextView);
		checkImageView = (ImageView) findViewById(R.id.checkImageView);
		
		//reload views
		reloadViews();
		
		setListener();
	}
	
	private void reloadViews(){
		//init date
		final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        
        //init views
        String today = year+"-"+(month+1)+"-"+day;
		dateTextView.setText(today);
		
		String introText = "";
		if (formType.equals(BHConstants.FORM_MESSAGE_TYPE_RESERVATION)){
			introText = 
				"<font color=#3b434a>"+getString(R.string.form_reservation_intro1)+"</font>"+
				"<font color=#009038>「"+houseName+"」</font>"+
				"<font color=#3b434a>"+getString(R.string.form_reservation_intro2)+"</font>";
		}else{
			introText = 
					"<font color=#3b434a>"+getString(R.string.form_salesman_intro)+"</font>";
		}
		introTextView.setText(Html.fromHtml(introText));
		
		
		String lawText = 
				"<font color=#3b434a>"+getString(R.string.form_law_text1)+"</font>"+
				"<font color=#009038>"+getString(R.string.form_law_text2)+"</font>";
		lawTextView.setText(Html.fromHtml(lawText));			
		
	}
	
	private void setListener(){
		
		dateTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog();
			}
		});
		
		lawTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = BHConstants.FORM_LAW_DETAIL_URL;
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
		
		checkImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (checkImageView.isSelected()){
					checkImageView.setSelected(false);
					checkImageView.setBackgroundResource(R.drawable.tick_off);
				}else{
					checkImageView.setSelected(true);
					checkImageView.setBackgroundResource(R.drawable.tick_on);
				}
			}
		});
		
		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String name = nameEditText.getText().toString();
				String sex = (maleRadioButton.isChecked())? "1":"2";
				String mobile = phoneEditText.getText().toString();
				String email = emailEditText.getText().toString();
				String date = dateTextView.getText().toString();
				
				if (name.equals("")){
					showToast(getString(R.string.please_type_in)+getString(R.string.name));
					return;
				}
				
				if (mobile.equals("")){
					showToast(getString(R.string.please_type_in)+getString(R.string.phone));
					return;
				}
				
				if (!checkImageView.isSelected()){
					showToast(R.string.please_check_law);
					return;
				}
				
				HashMap<String, Object> params = new HashMap<String, Object>();
				
				params.put(BHConstants.PARAM_HOUSE_NO, houseNO);
				params.put(BHConstants.PARAM_MESSAGE_TYPE, formType);
				params.put(BHConstants.PARAM_NAME, name);
				params.put(BHConstants.PARAM_SEX, sex);
				params.put(BHConstants.PARAM_MOBILE, mobile);
				params.put(BHConstants.PARAM_EMAIL, email);
				params.put(BHConstants.PARAM_CONTACT_START_DATE, date);
				
				SinyiService.sendForm(params, new FormCallback(){
					@Override
					public void onResult(boolean success, String message) {
						if (success){
							showToast(message);
							finish();
						}else{
							showToast(R.string.network_not_stable);
						}
					}
				});
				
			}
		});
		
	}
	
	public void showDatePickerDialog() {
		DatePickerFragment datePickerFragment = new DatePickerFragment();
	    datePickerFragment.setListener(datePickerListener);
	    datePickerFragment.initDate(year, month, day);
	    datePickerFragment.show(getSupportFragmentManager(), "datePicker");
	}
	
	
	private DatePickerListener datePickerListener = new DatePickerListener(){

		@Override
		public void onDateSet(DatePicker view, int newYear, int newMonth, int newDay) {
			//check不可選今日以前的日期
			final Calendar c = Calendar.getInstance();
	        int minYear = c.get(Calendar.YEAR);
	        int minMonth = c.get(Calendar.MONTH);
	        int minDay = c.get(Calendar.DAY_OF_MONTH);
			
	        //Log.e("test", "min: "+minYear+"-"+minMonth+"-"+minDay);
	        //Log.e("test", "new: "+newYear+"-"+newMonth+"-"+newDay);
	        
	        if ((newYear < minYear) || (newMonth < minMonth && newYear == minYear) ||
	                (newDay < minDay && newMonth == minMonth && newYear == minYear)){
	        		showToast(getString(R.string.please_choose_day_after_today));
	        		
	        		new Handler(Looper.getMainLooper()).postDelayed(new Runnable(){
	        			@Override
	        			public void run() {
	        				showDatePickerDialog();
	        			}
	        		},300);
	        		
	        		return;
	        }
	        
			year = newYear;
			month = newMonth;
			day = newDay;
		    
			String dateText = year+"-"+(month+1)+"-"+day;
			dateTextView.setText(dateText);
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home){
			finish();
		}
		
		return super.onOptionsItemSelected(item);
	}
	

}
