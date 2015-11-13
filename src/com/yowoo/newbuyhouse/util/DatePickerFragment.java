package com.yowoo.newbuyhouse.util;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment
                            implements DatePickerDialog.OnDateSetListener{

	public interface DatePickerListener{
		public void onDateSet(DatePicker view, int year, int month, int day);
	}
	
	private DatePickerListener listener;
	private DatePickerDialog datePickerDialog;
	
	int year=0, month=0, day=0;
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
	    
        // Create a new instance of DatePickerDialog and return it
        datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        return datePickerDialog;
    }
    

    public void onDateSet(DatePicker view, int year, int month, int day) {
    		Log.e("test", "DatePickerFragment: onDateSet");
    		if (view.isShown()){
    			if (listener!=null){
    				listener.onDateSet(view, year, month, day);
    			}
    		}

    }
    
    public void initDate(int year, int month, int day){
	    this.year = year;
	    this.month = month;
	    this.day = day;
    		
    }
    
    public void setListener(DatePickerListener listener){
    		this.listener = listener;
    }
}