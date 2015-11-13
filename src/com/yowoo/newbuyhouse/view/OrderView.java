package com.yowoo.newbuyhouse.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.thinkermobile.sinyi.R;

public class OrderView extends RelativeLayout{

	//ArrayList<OrderItemView> orderItemViews;
	String[] orderItemTitles = new String[]{};
	
	LinearLayout container;
	Context context;
	
	int selectedIndex = 0;
	
	private OrderViewListener listener;
	
	public interface OrderViewListener{
		public void onClickItem(int index, String selectedText);
	}
	
	public OrderView(Context context) {
		super(context);
		init(context);
	}

	public OrderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		
		
	}

	public OrderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		
	}
	

	protected void init(Context context) {
		this.context = context;
		View root = LayoutInflater.from(context).inflate(R.layout.order_view, this);
		
		container = (LinearLayout) this.findViewById(R.id.container);
		
		//initItems();
    }
	
	public void initItems(String[] items){
		orderItemTitles = items;
		//orderItemViews = new ArrayList<OrderItemView>();
		
		OrderItemView itemView;
		for(int i=0; i<orderItemTitles.length; i++){
			itemView = new OrderItemView(context);
			itemView.titleTextView.setText(orderItemTitles[i]);
			itemView.setTag(String.valueOf(i));
			itemView.setOnClickListener(itemListener);
			this.container.addView(itemView);
		}
			
	}
	
	View.OnClickListener itemListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (listener!=null){
				int index = Integer.valueOf((String)v.getTag());
				
				listener.onClickItem(index, getSelectedItemText(index));
			}
		}
	};
	
	public void reloadView(int index){
		for (int i=0; i<orderItemTitles.length; i++){
			OrderItemView item = (OrderItemView) container.getChildAt(i);
			if (i==index){
				item.titleTextView.setTextColor(getResources().getColor(R.color.order_text_select_color));
				item.selectImageView.setVisibility(View.VISIBLE);
			}else{
				item.selectImageView.setVisibility(View.GONE);
				item.titleTextView.setTextColor(getResources().getColor(R.color.order_text_color));
			}
		}
	}
	
	public String getSelectedItemText(int index){
		//if (index==0) return getResources().getString(R.string.order);
		return orderItemTitles[index];
	}
	
	public void setOrderViewListener(OrderViewListener listener){
		this.listener = listener;
	}


}
