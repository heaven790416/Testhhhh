package com.yowoo.newbuyhouse.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.Constants;
import com.yowoo.newbuyhouse.MainActivity;
import com.yowoo.newbuyhouse.SearchInfo;
import com.yowoo.newbuyhouse.network.LeaveMessageService;
import com.yowoo.newbuyhouse.network.LeaveMessageService.FormCallback;
import com.yowoo.newbuyhouse.network.SinyiService;
import com.yowoo.newbuyhouse.network.SinyiService.RoadCallback;
import com.yowoo.newbuyhouse.util.DatePickerFragment;
import com.yowoo.newbuyhouse.util.DatePickerFragment.DatePickerListener;
import com.yowoo.newbuyhouse.view.CityWheelView;
import com.yowoo.newbuyhouse.view.CityWheelView.WheelListener;
import com.yowoo.newbuyhouse.view.SingleChooseRow;
import com.yowoo.newbuyhouse.view.SingleWheelView;
import com.yowoo.newbuyhouse.view.SingleWheelView.SingleWheelListener;
//import com.yowoo.newbuyhouse.view.RoadWheelView;
//import com.yowoo.newbuyhouse.view.RoadWheelView.SingleWheelListener;

public class MainLeaveMessageFragment extends MyFragment{

	RelativeLayout shadow;
	LinearLayout areaContainer, roadContainer;

	SingleChooseRow cityRow, areaRow;
	SingleChooseRow roadRow;

	ScrollView filterScrollView;

	TextView dateTextView;
	EditText nameEditText, phoneEditText, emailEditText, demandEditText;
	RadioButton maleRadioButton, femaleRadioButton;
	Button sendButton;
	TextView lawTextView, introTextView;
	ImageView checkImageView;

	public int currentFilterMode = 0;
	//for area mode
	public int selectedCity = 0;
	public int selectedArea = 0;
	public int selectedRoad = 0;

	int year = 0;
	int month = 0;
	int day = 0;

	String zipcode;

	//wheel
	CityWheelView areaWheelView;
	//RoadWheelView roadWheelView;
	SingleWheelView singleWheelView;

	ArrayList<String> road = null;

	String areaName;
	String cityName;
	String roadName="";

	public static Fragment newInstance(Context context) {
		MainLeaveMessageFragment f = new MainLeaveMessageFragment();



		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_main_leave_message, null);

		shadow = (RelativeLayout) root.findViewById(R.id.shadow);


		areaContainer = (LinearLayout) root.findViewById(R.id.areaContainer);
		areaWheelView = (CityWheelView) root.findViewById(R.id.aeraWheelView);

		//roadContainer = (LinearLayout) root.findViewById(R.id.roadContainer);
		//roadWheelView = (RoadWheelView) root.findViewById(R.id.roadWheelView);
		singleWheelView = (SingleWheelView) root.findViewById(R.id.singleWheelView);

		cityRow = (SingleChooseRow) root.findViewById(R.id.cityRow);//縣市
		areaRow = (SingleChooseRow) root.findViewById(R.id.areaRow);//行政區
		roadRow = (SingleChooseRow) root.findViewById(R.id.roadRow);//路段

		filterScrollView = (ScrollView) root.findViewById(R.id.filterScrollView);


		//reloadSearchInfoStatus();

		//set views
		demandEditText = (EditText) root.findViewById(R.id.OtherDemandEditText);
		nameEditText = (EditText) root.findViewById(R.id.nameEditText);
		dateTextView = (TextView) root.findViewById(R.id.dateTextView);
		phoneEditText = (EditText) root.findViewById(R.id.phoneEditText);
		emailEditText = (EditText) root.findViewById(R.id.emailEditText);
		maleRadioButton = (RadioButton) root.findViewById(R.id.maleRadioButton);
		femaleRadioButton = (RadioButton) root.findViewById(R.id.femaleRadioButton);
		sendButton = (Button) root.findViewById(R.id.sendButton);


		introTextView = (TextView) root.findViewById(R.id.introTextView);
		lawTextView = (TextView) root.findViewById(R.id.lawTextView);
		checkImageView = (ImageView) root.findViewById(R.id.checkImageView);

		sendButton = (Button) root.findViewById(R.id.sendButton);


		//reload views
		reloadViews();

		//set listener
		setListener();

		return root;
	}

	private void reloadViews(){



		//reload區域資訊
		reloadAreaViews();

		//init date
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		//init views
		String today = year+"-"+(month+1)+"-"+day;
		dateTextView.setText(today);

		String introText = 
				"<font color=#3b434a>"+"您好，請您於下方表單留下"+"</font>"+
						"<font color=#009038>"+"買屋需求及聯絡資訊"+"</font>"+
						"<font color=#3b434a>"+"業務人員將盡快與你聯繫"+"</font>";
		introTextView.setText(Html.fromHtml(introText));	


		String lawText = 
				"<font color=#3b434a>"+getString(R.string.form_law_text1)+"</font>"+
						"<font color=#009038>"+getString(R.string.form_law_text2)+"</font>";
		lawTextView.setText(Html.fromHtml(lawText));	

	}

	private void reloadAreaViews(){


		cityRow.setSelectedText(SearchInfo.getInstance().cityList.get(this.selectedCity).cityName);

		areaName = SearchInfo.getInstance().cityList.get(this.selectedCity).areas.get(this.selectedArea).name;
		cityName = SearchInfo.getInstance().cityList.get(this.selectedCity).cityName;

		zipcode = SearchInfo.getInstance().cityList.get(this.selectedCity).areas.get(this.selectedArea).zipCode;



		if(zipcode!="0"){
			reloadRoad(zipcode);
			Log.e("test", "zipcode" +zipcode);

		}

		areaRow.setSelectedText(areaName);
	}

	private void reloadRoadViews(){

		this.roadRow.setSelectedText(road.get(selectedRoad));
		roadName = road.get(selectedRoad);
	}

	private void reloadRoad(String zipcode){
		SinyiService.getRoad(zipcode, new RoadCallback(){

			@Override
			public void onResult(boolean success, ArrayList<String> roadArray) {
				if (success){
					Log.e("test","get road success!");
					road = roadArray;
				}else{
					Log.e("test","get road fail!");
					road = null;
					showToast("無網路連線，請開啟網路");
				}
			}
		});


		Log.e("test", "LeaveMessageService.getRoad(zipcode, new RoadCallback(){ "+road);
	}


	private void setListener(){

		lawTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = BHConstants.FORM_LAW_DETAIL_URL;
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});


		View.OnClickListener areaRowListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				roadRow.setSelectedText("");

				if (areaWheelView.getVisibility()==View.GONE){
					//hideAllWheels();
					shadow.setVisibility(View.VISIBLE);
					areaWheelView.reloadWheelsToSelected(selectedCity, selectedArea);
					areaWheelView.setVisibility(View.VISIBLE);

				}
			}
		};

		areaRow.setOnClickListener(areaRowListener);
		cityRow.setOnClickListener(areaRowListener);


		areaWheelView.setWheelListener(new WheelListener(){
			@Override
			public void onClickOk(int cityIndex, int areaIndex) {
				selectedCity = cityIndex;
				selectedArea = areaIndex;
				Log.e("test", "selectedArea="+selectedArea);
				if(selectedArea == 0){
					showToast("請選擇行政區");
					shadow.setVisibility(View.VISIBLE);
					areaWheelView.setVisibility(View.VISIBLE);
				}
				else{
					reloadAreaViews();
					shadow.setVisibility(View.GONE);
				}

			}

			@Override
			public void onClickCancel() {
				shadow.setVisibility(View.GONE);
			}
		});

		roadRow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				reloadAreaViews();
				if(road!=null){
					Log.e("test8", road.toString());
					if(!road.isEmpty()){
						shadow.setVisibility(View.VISIBLE);
						//hideAllWheels();
						String[] array = road.toArray(new String[road.size()]);
						singleWheelView.initWheels(array, selectedRoad);
						singleWheelView.setVisibility(View.VISIBLE);

						singleWheelView.setWheelListener(new SingleWheelListener(){
							@Override
							public void onClickOk(int selectedIndex) {
								selectedRoad = selectedIndex;
								reloadRoadViews();
								shadow.setVisibility(View.GONE);
							}

							@Override
							public void onClickCancel() {
								shadow.setVisibility(View.GONE);
							}
						});
					}
				}
			}
		});



		dateTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog();
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


				String demand = demandEditText.getText().toString();
				String name = nameEditText.getText().toString();
				String sex = (maleRadioButton.isChecked())? "1":"2";
				String mobile = phoneEditText.getText().toString();
				String email = emailEditText.getText().toString();
				String date = dateTextView.getText().toString();

				if (roadName.equals("")){
					showToast(getString(R.string.please_type_in)+"路");
					return;
				}

				if (name.equals("")){
					showToast(getString(R.string.please_type_in)+getString(R.string.name));
					return;
				}

				if (mobile.equals("")){
					showToast(getString(R.string.please_type_in)+getString(R.string.phone));
					return;
				}

//				if (email.equals("")){
//					showToast(getString(R.string.please_type_in)+getString(R.string.email));
//					return;
//				}

				if (!checkImageView.isSelected()){
					showToast(getString(R.string.please_check_law));
					return;
				}

				HashMap<String, Object> params = new HashMap<String, Object>();

				Log.e("test", cityName+areaName+roadName);

				params.put(BHConstants.PARAM_MESSAGE_TYPE, "6");
				params.put(BHConstants.PARAM_NAME, name);
				params.put(BHConstants.PARAM_SEX, sex);
				params.put(BHConstants.PARAM_MOBILE, mobile);
				params.put(BHConstants.PARAM_EMAIL, email);
				params.put(BHConstants.PARAM_CONTACT_START_DATE, date);

				params.put("buy_city", cityName);
				params.put("buy_county", areaName);
				params.put("buy_road", roadName);
				params.put("desc", demand);

				LeaveMessageService.sendForm(params, new FormCallback(){
					@Override
					public void onResult(boolean success, String message) {
						if (success){
							showToast(message);
							//clear data

							demandEditText.setText("");
							nameEditText.setText("");
							//String sex = (maleRadioButton.isChecked())? "1":"2";
							phoneEditText.setText("");
							emailEditText.setText("");
							reloadViews();//set date to today;
							roadRow.setSelectedText("");
							cityRow.setSelectedText("");
							areaRow.setSelectedText("");

							road = null;

							//finish();
						}else{
							showToast(getString(R.string.network_not_stable));
						}
					}
				});

			}
		});

	}



	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(Constants.FRAGMENT_ARG_POSITION));
	}

	public void showToast(String str){
		Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
	}

	public void showDatePickerDialog() {
		DatePickerFragment datePickerFragment = new DatePickerFragment();
		datePickerFragment.setListener(datePickerListener);
		datePickerFragment.initDate(year, month, day);
		datePickerFragment.show(getChildFragmentManager(), "datePicker"); //change function
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


}
