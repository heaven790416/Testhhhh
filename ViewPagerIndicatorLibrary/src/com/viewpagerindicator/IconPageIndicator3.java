/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2012 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viewpagerindicator;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.FILL_PARENT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * This widget implements the dynamic action bar tab behavior that can change
 * across different configurations or circumstances.
 */
public class IconPageIndicator3 extends HorizontalScrollView implements
		PageIndicator {
	private final IcsLinearLayout mIconsLayout;

	private ViewPager mViewPager;
	private OnPageChangeListener mListener;
	private Runnable mIconSelector;
	private int mSelectedIndex;
	private int mMaxTabWidth;
	private int mSelectedTabIndex;

	public IconPageIndicator3(Context context) {
		this(context, null);
	}

	public IconPageIndicator3(Context context, AttributeSet attrs) {
		super(context, attrs);
		setHorizontalScrollBarEnabled(false);
		// setHorizontalScrollBarEnabled(true);

		mIconsLayout = new IcsLinearLayout(context,
				R.attr.vpiIconPageIndicatorStyle);

		addView(mIconsLayout, new ViewGroup.LayoutParams(WRAP_CONTENT,
				MATCH_PARENT));
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		// System.out.println("widthMode: " + widthMode);
		final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
		// System.out.println("lockedExpanded: " + lockedExpanded);
		setFillViewport(lockedExpanded);

		final int childCount = mIconsLayout.getChildCount();
		if (childCount > 1
				&& (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
			if (childCount > 2) {
//				mMaxTabWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) * 0.4f);
				mMaxTabWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) / (float)childCount);
				// System.out.println("mMaxTabWidth1: " + mMaxTabWidth);
			} else {
				mMaxTabWidth = MeasureSpec.getSize(widthMeasureSpec) / 2;
				// System.out.println("mMaxTabWidth2: " + mMaxTabWidth);
			}
		} else {
			mMaxTabWidth = -1;
			// System.out.println("mMaxTabWidth3: " + -1);
		}
		
		final int oldWidth = getMeasuredWidth();
		// final int oldHeight = getMeasuredHeight();
		// System.out.println("oldWidth: " + oldWidth);
		// System.out.println("oldHeight: " + oldHeight);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int newWidth = getMeasuredWidth();
		// final int newHeight = getMeasuredHeight();
		// System.out.println("newHeight: " + newHeight);
		// System.out.println("newWidth: " + newWidth);
		if (lockedExpanded && oldWidth != newWidth) {
			// Recenter the tab display if we're at a new (scrollable) size.
			setCurrentItem(mSelectedTabIndex);
			// System.out.println("4");
		}
	}

	private void animateToIcon(final int position) {
		final View iconView = mIconsLayout.getChildAt(position);
		if (mIconSelector != null) {
			removeCallbacks(mIconSelector);
		}
		mIconSelector = new Runnable() {
			public void run() {
				final int scrollPos = iconView.getLeft()
						- (getWidth() - iconView.getWidth()) / 2;
				smoothScrollTo(scrollPos, 0);
				mIconSelector = null;
			}
		};
		post(mIconSelector);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mIconSelector != null) {
			// Re-post the selector we saved
			post(mIconSelector);
		}
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mIconSelector != null) {
			removeCallbacks(mIconSelector);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		if (mListener != null) {
			mListener.onPageScrollStateChanged(arg0);
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		if (mListener != null) {
			mListener.onPageScrolled(arg0, arg1, arg2);
		}
	}

	@Override
	public void onPageSelected(int arg0) {
		setCurrentItem(arg0);
		if (mListener != null) {
			mListener.onPageSelected(arg0);
		}
	}

	@Override
	public void setViewPager(ViewPager view) {
		if (mViewPager == view) {
			return;
		}
		if (mViewPager != null) {
			mViewPager.setOnPageChangeListener(null);
		}
		PagerAdapter adapter = view.getAdapter();
		if (adapter == null) {
			throw new IllegalStateException(
					"ViewPager does not have adapter instance.");
		}
		mViewPager = view;
		view.setOnPageChangeListener(this);
		notifyDataSetChanged();
	}

	public void notifyDataSetChanged() {
		mIconsLayout.removeAllViews();
		IconPagerAdapter iconAdapter = (IconPagerAdapter) mViewPager
				.getAdapter();
		int count = iconAdapter.getCount();
		for (int i = 0; i < count; i++) {
			TabTextView view = new TabTextView(getContext());
			// LinearLayout.LayoutParams lp = new
			// LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
			// LinearLayout.LayoutParams.WRAP_CONTENT);
			// lp.setMargins(0, 0, 0, 0 ); // Here you can set margins.
			// view.setLayoutParams(lp);
			// view.setScaleType(ScaleType.FIT_XY);
//			view.setScaleType(ScaleType.CENTER_CROP);
//			view.setImageResource(iconAdapter.getIconResId(i));
			
			view.setTag("" + i);
			switch(i+1) {
			case 1:
				view.setText("新聞動態");
				break;
			case 2:
				view.setText("微樓書");
				break;
			case 3:
				view.setText("720度全景");
				break;
			}
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					int viewPosition = Integer.parseInt(v.getTag().toString());

					mViewPager.setCurrentItem(viewPosition);
				}
			});
			mIconsLayout.addView(view, new LinearLayout.LayoutParams(0,
					MATCH_PARENT, 1));
		}
		if (mSelectedIndex > count) {
			mSelectedIndex = count - 1;
		}
		setCurrentItem(mSelectedIndex);
		requestLayout();
	}

	private class TabTextView extends TextView {
		private int mIndex;

		public TabTextView(Context context) {
			super(context, null, R.attr.vpiIconPageIndicatorStyle);
		}

		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);

			// Re-measure if we went beyond our maximum size.
			if (mMaxTabWidth > 0 && getMeasuredWidth() > mMaxTabWidth) {
				super.onMeasure(MeasureSpec.makeMeasureSpec(mMaxTabWidth,
						MeasureSpec.EXACTLY), heightMeasureSpec);
			}
		}

		public int getIndex() {
			return mIndex;
		}
	}

	@Override
	public void setViewPager(ViewPager view, int initialPosition) {
		setViewPager(view);
		setCurrentItem(initialPosition);
	}

	@Override
	public void setCurrentItem(int item) {
		if (mViewPager == null) {
			throw new IllegalStateException("ViewPager has not been bound.");
		}
		mSelectedIndex = item;
		mViewPager.setCurrentItem(item);

		int tabCount = mIconsLayout.getChildCount();
		for (int i = 0; i < tabCount; i++) {
			View child = mIconsLayout.getChildAt(i);
			boolean isSelected = (i == item);
			child.setSelected(isSelected);
			if (isSelected) {
				animateToIcon(item);
			}
		}
	}

	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mListener = listener;
	}
}
