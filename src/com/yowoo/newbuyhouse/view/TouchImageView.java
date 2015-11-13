package com.yowoo.newbuyhouse.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class TouchImageView extends ImageView {

	Matrix matrix;

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF last = new PointF();
	PointF start = new PointF();
	float minScale = 1f;
	float maxScale = 3f;
	float[] m;


	int viewWidth, viewHeight;
	static final int CLICK = 3;
	float saveScale = 1f;
	protected float origWidth, origHeight;
	int oldMeasuredWidth, oldMeasuredHeight;

	ScaleGestureDetector mScaleDetector;
	GestureDetector mDoubleTapDetector;

	Context context;

	public MyViewPager viewPager = null;

	public TouchImageView(Context context) {
		super(context);
		sharedConstructing(context);
	}

	public TouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sharedConstructing(context);
	}

	public void reset() {
		saveScale = 1.0f;
		matrix = new Matrix();
		m = new float[9];
		setScaleType(ImageView.ScaleType.CENTER_CROP);
		setImageMatrix(matrix);
		setScaleType(ScaleType.MATRIX);

		if(viewPager!=null) {
			viewPager.setPagingEnabled(true);
		}
	}

	private void sharedConstructing(Context context) {
		super.setClickable(true);
		this.context = context;
		mDoubleTapDetector = new GestureDetector(context, new DoubleTapGestureListener());
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		matrix = new Matrix();
		m = new float[9];
		setImageMatrix(matrix);
		setScaleType(ScaleType.MATRIX);

		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mScaleDetector.onTouchEvent(event);
				mDoubleTapDetector.onTouchEvent(event);

				PointF curr = new PointF(event.getX(), event.getY());

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					last.set(curr);
					start.set(last);
					mode = DRAG;
					break;

				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						float deltaX = curr.x - last.x;
						float deltaY = curr.y - last.y;
						float fixTransX = getFixDragTrans(deltaX, viewWidth, origWidth * saveScale);
						float fixTransY = getFixDragTrans(deltaY, viewHeight, origHeight * saveScale);

						matrix.postTranslate(fixTransX, fixTransY);
						fixTrans();
						last.set(curr.x, curr.y);
					}
					break;

				case MotionEvent.ACTION_UP:
					mode = NONE;
					int xDiff = (int) Math.abs(curr.x - start.x);
					int yDiff = (int) Math.abs(curr.y - start.y);
					if (xDiff < CLICK && yDiff < CLICK)
						performClick();
					break;

				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				}

				setImageMatrix(matrix);
				invalidate();
				return true; // indicate event was handled
			}

		});
	}

	public void setMaxZoom(float x) {
		maxScale = x;
	}

	private class DoubleTapGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
		// event when double tap occurs
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			float x = e.getX();
			float y = e.getY();

			if(saveScale!=1.0f) {
				// back to original state
				reset();
			} else {
				matrix.postScale(2.0f, 2.0f,x,y);
				saveScale = 2.0f;

				if(viewPager!=null) {
					viewPager.setPagingEnabled(false);
				}
			}

			return true;
		}
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			mode = ZOOM;
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float mScaleFactor = detector.getScaleFactor();
			float origScale = saveScale;
			saveScale *= mScaleFactor;
			if (saveScale > maxScale) {
				saveScale = maxScale;
				mScaleFactor = maxScale / origScale;
			} else if (saveScale < minScale) {
				saveScale = minScale;
				mScaleFactor = minScale / origScale;
			}

			if (origWidth * saveScale <= viewWidth || origHeight * saveScale <= viewHeight)
				matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2, viewHeight / 2);
			else
				matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());

			fixTrans();

			return true;
		}


	}

	void fixTrans() {
		matrix.getValues(m);
		float transX = m[Matrix.MTRANS_X];
		float transY = m[Matrix.MTRANS_Y];

		float fixTransX = getFixTrans(transX, viewWidth, origWidth * saveScale);
		float fixTransY = getFixTrans(transY, viewHeight, origHeight * saveScale);

		if (fixTransX != 0 || fixTransY != 0)
			matrix.postTranslate(fixTransX, fixTransY);

		if(viewPager!=null) {
			if(mode==ZOOM) {
				if(saveScale==1.0f) {
					viewPager.setPagingEnabled(true);
				} else {
					viewPager.setPagingEnabled(false);
				}
			} else if(mode==DRAG && fixTransX!=0) {

				if(fixTransX<0) {
					int previousPageIndex = viewPager.getCurrentItem() - 1;

					if(previousPageIndex>=0) {
						// is not last page => scroll to next one
						viewPager.setCurrentItem(previousPageIndex, true);
						viewPager.setPagingEnabled(true);
					}
				} else if(fixTransX>0) {
					int nextPageIndex = viewPager.getCurrentItem() + 1;

					if(nextPageIndex<viewPager.getChildCount()) {
						// is not last page => scroll to next one
						viewPager.setCurrentItem(nextPageIndex, true);
						viewPager.setPagingEnabled(true);
					}
				} else if(fixTransX==0) {
					viewPager.setPagingEnabled(false);
				}
			}
		}
	}

	float getFixTrans(float trans, float viewSize, float contentSize) {
		float minTrans, maxTrans;

		if (contentSize <= viewSize) {
			minTrans = 0;
			maxTrans = viewSize - contentSize;
		} else {
			minTrans = viewSize - contentSize;
			maxTrans = 0;
		}

		if (trans < minTrans)
			return -trans + minTrans;
		if (trans > maxTrans)
			return -trans + maxTrans;
		return 0;
	}

	float getFixDragTrans(float delta, float viewSize, float contentSize) {
		if (contentSize <= viewSize) {
			return 0f;
		}
		return delta;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		viewHeight = MeasureSpec.getSize(heightMeasureSpec);

		//
		// Rescales image on rotation
		//
		if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight
				|| viewWidth == 0 || viewHeight == 0)
			return;
		oldMeasuredHeight = viewHeight;
		oldMeasuredWidth = viewWidth;

		if (saveScale == 1) {
			//Fit to screen.
			float scale;

			Drawable drawable = getDrawable();
			if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0)
				return;
			int bmWidth = drawable.getIntrinsicWidth();
			int bmHeight = drawable.getIntrinsicHeight();

			float scaleX = (float) viewWidth / (float) bmWidth;
			float scaleY = (float) viewHeight / (float) bmHeight;
			scale = Math.min(scaleX, scaleY);
			matrix.setScale(scale, scale);

			// Center the image
			float redundantYSpace = (float) viewHeight - (scale * (float) bmHeight);
			float redundantXSpace = (float) viewWidth - (scale * (float) bmWidth);
			redundantYSpace /= (float) 2;
			redundantXSpace /= (float) 2;

			matrix.postTranslate(redundantXSpace, redundantYSpace);

			origWidth = viewWidth - 2 * redundantXSpace;
			origHeight = viewHeight - 2 * redundantYSpace;
			setImageMatrix(matrix);
		}
		fixTrans();
	}

	public int getMode() {
		return mode;
	}
}