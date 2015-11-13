package com.yowoo.newbuyhouse.view;

import java.util.HashSet;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.Singleton;

public class MultiChooseRow extends RelativeLayout{

	private TextView titleTextView, selectedTextView;
	public RelativeLayout titleContainer, selectItemContainer;
	public RowLayout buttonContainer;
	
	private String[] items;
	private Context context;
	private HashSet<Integer> selectedUses = new HashSet<Integer>();
	
	public interface MultiChooseListener{
		public void onClickTitle(Boolean isOpened);
	}
	
	private MultiChooseListener listener;
	
	public MultiChooseRow(Context context) {
		super(context);
		init(context);
	}

	public MultiChooseRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.filterRow,
				0, 0);

		try {
			String titleText = a.getString(R.styleable.filterRow_titleText);
			this.titleTextView.setText(titleText);
		} finally {
			a.recycle();
		}
		
	}

	public MultiChooseRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		
	}
	

	protected void init(Context context) {
		this.context = context;
		View root = LayoutInflater.from(context).inflate(R.layout.multi_choose_view, this);

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		selectedTextView = (TextView) findViewById(R.id.selectedTextView);
		
		this.buttonContainer = (RowLayout) this.findViewById(R.id.buttonContainer);
		this.titleContainer = (RelativeLayout) this.findViewById(R.id.titleContainer);
		this.selectItemContainer = (RelativeLayout) this.findViewById(R.id.selectItemContainer);
		
		titleContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Boolean isOpened = false;
				if (selectItemContainer.getVisibility()==View.VISIBLE){
					selectItemContainer.setVisibility(View.GONE);
					isOpened = false;
				}else{
					selectItemContainer.setVisibility(View.VISIBLE);
					isOpened = true;
				}
				
				if (listener!=null){
					listener.onClickTitle(isOpened);
				}
			}
		});
		
    }
	
	public void initItems(String[] items, HashSet<Integer> selectedUses){
		this.items = items;
		this.selectedUses = selectedUses;
		
		//create button
		for (int i=0; i<items.length; i++){
			Button button = new Button(context);
			button.setText(items[i]);
			button.setTag(String.valueOf(i));
			button.setOnClickListener(buttonListener);
			button.setBackgroundResource(R.drawable.bg_multi_choose_item_selector);
			button.setTextColor(getResources().getColor(R.color.multi_select_btn_text_color));
			
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, Singleton.dpToPixel(40));
			button.setLayoutParams(params);
			
			buttonContainer.addView(button);
		}
		
	}
	
	View.OnClickListener buttonListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int index = Integer.valueOf((String) v.getTag());
			
			if (selectedUses.contains(index)){
				//已被選,需設為未選
				selectedUses.remove(index);
				v.setSelected(false);
				((Button)v).setTextColor(getResources().getColor(R.color.filter_gray_color));
			}else{
				selectedUses.add(index);
				v.setSelected(true);
				((Button)v).setTextColor(getResources().getColor(R.color.filter_green_color));
			}
			
			reloadSelectedText(selectedUses);
			
		}
	};
	
	public void setSelectedText(String text){
		selectedTextView.setText(text);
	}
	
	public void reloadSelectedText(HashSet<Integer> selectedUses){
		String text = "";
		Boolean isFirst = true;
		for (int i=0; i<items.length; i++){
			if (selectedUses.contains(i)){
				if (isFirst) {
					text+=items[i];
					isFirst = false;
				}else{
					text += ","+items[i];
				}
			}
		}
		
		this.selectedTextView.setText(text);
		
		final String savedText = text;
		this.selectedTextView.post(new Runnable(){
			@Override
			public void run() {
				selectedTextView.setText(savedText);
			}
		});
		
	}
	
	public void reloadSelectedButton(HashSet<Integer> selectedUses){
		Button button;
		for (int i=0; i<items.length; i++){
			button = (Button) buttonContainer.getChildAt(i);
			
			if (selectedUses.contains(i)){
				//已被選
				button.setSelected(true);
				button.setTextColor(getResources().getColor(R.color.filter_green_color));
			}else{
				button.setSelected(false);
				button.setTextColor(getResources().getColor(R.color.filter_gray_color));
			}
		}
	}
	
	
	public void reloadRow(HashSet<Integer> selectedUses){
		reloadSelectedText(selectedUses);
		reloadSelectedButton(selectedUses);
	}
	
	public void setListener(MultiChooseListener listener){
		this.listener = listener;
	}
	
	public void close(){
		selectItemContainer.setVisibility(View.GONE);
	}



}
