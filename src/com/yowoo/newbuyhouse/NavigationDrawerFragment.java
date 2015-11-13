package com.yowoo.newbuyhouse;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.fragment.MyFragment;
import com.yowoo.newbuyhouse.login.LoginActivity;
import com.yowoo.newbuyhouse.login.LoginInfo;
import com.yowoo.newbuyhouse.login.ModifyPwActivity;
import com.yowoo.newbuyhouse.login.ProfileActivity;
import com.yowoo.newbuyhouse.login.UserConstants;
import com.yowoo.newbuyhouse.network.LoginService;
import com.yowoo.newbuyhouse.network.LoginService.LoginCallback;
import com.yowoo.newbuyhouse.network.TrackService;
import com.yowoo.newbuyhouse.network.TrackService.TrackCallback;
import com.yowoo.newbuyhouse.view.SelectRow;

/**
 * Fragment used for managing interactions for and presentation of a navigation
 * drawer. See the <a href=
 * "https://developer.android.com/design/patterns/navigation-drawer.html#Interaction"
 * > design guidelines</a> for a complete explanation of the behaviors
 * implemented here.
 */
public class NavigationDrawerFragment extends MyFragment {

	/**
	 * å Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the
	 * user manually expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private View mFragmentContainerView;

	private int mCurrentSelectedPosition = 0;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;

	// User Info View
	TextView nameTextView;
	ImageView arrowImageView;
	Button logInOutButton;
	// TitleRow profileRow, passwordRow;
	RelativeLayout profileRow, passwordRow;

	private View mProfile_password_Layout, mProfileContainer;
	private boolean mProfile_password_Layout_isOpen = false,
			mProfileContainer_able;

	// logout
	// private int selection_position_logout = 4;//住戶登出

	// select item titile
	static String[] seletItemTitles;

	public NavigationDrawerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.e("test", "Navigation: onCreate");

		// Read in the flag indicating whether or not the user has
		// demonstrated awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState
					.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}

		// get extra to set current fragment position(from notification)
		try {
			if (getActivity() != null) {
				mCurrentSelectedPosition = getActivity().getIntent()
						.getExtras()
						.getInt(BHConstants.EXTRA_GOTO_FRAGMENT_POS);
			}
		} catch (Exception e) {
		}

		// Select either the default item (0) or the last selected item.
		selectItem(mCurrentSelectedPosition);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.e("test", "NavigationDrawerFragment: onActivityCreate!");
		// Indicate that this fragment would like to influence the set
		// of actions in the action bar.
		setHasOptionsMenu(true);

		// 原本是在onCreateView裡面，但是因為在onCreateView中時activity的action bar尚未被init完成，
		// 所以此時會是null的情形，因此將mDrawerListView的content留到onActivityCreated時來做init.
		ActionBar actionBar = getActionBar();

		// TODO:完整選單
		seletItemTitles = new String[] {
				getString(R.string.drawer_section_house),
				getString(R.string.drawer_section_price),
				getString(R.string.drawer_section_track),
				getString(R.string.drawer_section_news),
				getString(R.string.drawer_section_store),
				getString(R.string.drawer_section_chat),
				getString(R.string.drawer_section_leave_message),
				getString(R.string.drawer_section_leave_message_sell) };

		// 第一階段選單
		// seletItemTitles = new String[] {
		// getString(R.string.drawer_section_house),
		// getString(R.string.drawer_section_store)
		// };

		mDrawerListView.setAdapter(selectItemAdapter);
		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout rootView = (RelativeLayout) inflater.inflate(
				R.layout.fragment_navigation_drawer, container, false);
		mDrawerListView = (ListView) rootView
				.findViewById(R.id.mDrawerListView);

		// mDrawerListView = (ListView)
		// inflater.inflate(R.layout.fragment_navigation_drawer, container,
		// false);
		mDrawerListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						selectItem(position);
					}
				});

		mProfile_password_Layout = rootView
				.findViewById(R.id.profile_password_Layout);

		// set user info view
		nameTextView = (TextView) rootView.findViewById(R.id.nameTextView);
		arrowImageView = (ImageView) rootView.findViewById(R.id.arrowImageView);
		logInOutButton = (Button) rootView.findViewById(R.id.logInOutButton);
		profileRow = (RelativeLayout) rootView.findViewById(R.id.profileRow);
		passwordRow = (RelativeLayout) rootView.findViewById(R.id.passwordRow);

		mProfileContainer = (View) rootView.findViewById(R.id.profileContainer);

		// set picasso

		// get basic user info from loginInfo or server
		fetchUserInfo();

		// set listener
		setListener();

		return rootView;
		// return mDrawerListView;
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null
				&& mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	/* Yowoo */
	public void triggerDrawer() {
		if (isDrawerOpen())
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		else
			mDrawerLayout.openDrawer(mFragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation
	 * drawer interactions.
	 * 
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the
		// drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener

		// ActionBarDrawerToggle ties together the the proper
		// interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), /*
																 * host Activity
																 */
		mDrawerLayout, /* DrawerLayout object */
		R.string.navigation_drawer_open, /*
										 * "open drawer" description for
										 * accessibility
										 */
		R.string.navigation_drawer_close /*
										 * "close drawer" description for
										 * accessibility
										 */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().supportInvalidateOptionsMenu(); // calls
				// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer;
					// store this flag to prevent
					// auto-showing
					// the navigation drawer automatically
					// in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true)
							.apply();
				}

				getActivity().supportInvalidateOptionsMenu(); // calls
				// onPrepareOptionsMenu()
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to
		// introduce them to the drawer,
		// per the navigation drawer design guidelines.
		// if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
		// mDrawerLayout.openDrawer(mFragmentContainerView);
		// }

		// Defer code dependent on restoration of previous instance
		// state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void selectItem(int position) {
		mCurrentSelectedPosition = position;

		// others
		if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(position, true);
			selectItemAdapter.notifyDataSetChanged();
		}

		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the
		// action bar. See also
		// showGlobalContextActionBar, which controls the top-left area
		// of the action bar.

		if (mDrawerLayout != null && isDrawerOpen()) {
			// inflater.inflate(R.menu.global, menu);
			// showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Log.e("test", "NaviDrawser: Home pressed");
		}

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// if (item.getItemId() == R.id.action_example) {
		// return true;
		// }

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to
	 * show the global app 'context', rather than just what's in the current
	 * screen.
	 */
	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		// actionBar.setDisplayShowTitleEnabled(true);
		// actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar() {
		return ((ActionBarActivity) getActivity()).getSupportActionBar();
	}

	/**
	 * Callbacks interface that all activities using this fragment must
	 * implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position);
	}

	/* User Info */
	private void fetchUserInfo() {
		reloadUserInfoView();

	}

	private void reloadUserInfoView() {

		if (LoginInfo.getInstance().isLogined()) {
			// set name
			String nickname = LoginInfo.getInstance().getDisplayName();
			// if (nickname.equals("")) nickname =
			// getString(R.string.user_no_nickname);
			arrowImageView.setVisibility(View.VISIBLE);
			nameTextView.setText(nickname);
			logInOutButton.setText(getString(R.string.logout));
			mProfileContainer_able = true;

			// TODO: temp
			if (LoginInfo.getInstance().getLoginBy() == UserConstants.LOGIN_BY_OPEN_ID) {
				passwordRow.setVisibility(View.GONE);
			} else {
				passwordRow.setVisibility(View.VISIBLE);
			}
		} else {
			nameTextView.setText(getString(R.string.member_login));
			arrowImageView.setVisibility(View.GONE);
			logInOutButton.setText(getString(R.string.login));
			mProfileContainer_able = false;
			mProfile_password_Layout.setVisibility(View.GONE);
		}

	}

	/* Broadcast Receivers */
	@Override
	public void onResume() {
		super.onResume();

		this.reloadUserInfoView();

		// register broadcast receiver
		// LocalBroadcastManager broadcastManager =
		// LocalBroadcastManager.getInstance(getActivity());
		// broadcastManager.registerReceiver(userUpdateInfoBroadcastReceiver,
		// new IntentFilter(UserConstants.BROADCAST_USER_UPDATE_INFO));

		Log.e("test",
				"NavigationDrawerFragment: onResume: register broadcast receiver!");
	}

	@Override
	public void onPause() {
		super.onPause();

		// LocalBroadcastManager broadcastManager =
		// LocalBroadcastManager.getInstance(getActivity());
		// broadcastManager.unregisterReceiver(userUpdateInfoBroadcastReceiver);

		Log.e("test",
				"UserSettingFragment: onPause: unregister broadcast receiver!");
	}

	private void setListener() {
		logInOutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LoginInfo.getInstance().isLogined()) {
					logoutAllAction();
				} else {
					Intent intent = new Intent().setClass(getActivity(),
							LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			}
		});

		mProfileContainer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Log.e("test", "mProfileContainer.setOnClickListener");
				Log.e("test", "mProfileContainer_able "
						+ mProfileContainer_able);
				if (mProfileContainer_able) {
					if (!mProfile_password_Layout_isOpen) {
						mProfile_password_Layout.setVisibility(View.VISIBLE);
						mProfile_password_Layout_isOpen = true;
					} else {
						mProfile_password_Layout.setVisibility(View.GONE);
						mProfile_password_Layout_isOpen = false;
					}
				}
			}
		});

		profileRow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent().setClass(getActivity(),
						ProfileActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		passwordRow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent().setClass(getActivity(),
						ModifyPwActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

	}

	// private BroadcastReceiver userUpdateInfoBroadcastReceiver = new
	// BroadcastReceiver() {
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// Log.e("test","NavigationDrawerFragment: get userUpdateInfo broadcast receiver!");
	//
	// try {
	// String action = intent.getAction();
	// if(UserConstants.BROADCAST_USER_UPDATE_INFO.equals(action)) {
	// reloadUserInfoView();
	// }
	// }catch(Exception e){
	// e.printStackTrace();
	// }
	// }
	// };

	/* Logout */
	private void logoutAllAction() {
		// 先消除gcmToken
		String gcmToken = Singleton.preferences.getString(
				BHConstants.GCM_TOKEN, "");
		if (gcmToken.equals("")) {
			logoutAction();
		} else {
			TrackService.logoutAppGCMToken(gcmToken, new TrackCallback() {
				@Override
				public void onResult(boolean success, String debugMessage) {
					Log.e("test", "logoutGCMToken:" + success + " msg:"
							+ debugMessage);
					// clear pref
					Singleton.preferenceEditor.putString(BHConstants.GCM_TOKEN,
							"").commit();

					// 不管有沒有token取消成功，都讓他做登出...
					logoutAction();
				}
			});
		}
	}

	private void logoutAction(){
		
		LoginService.logout(new LoginCallback(){
			@Override
			public void onResult(boolean success, String debubMessage) {
				if (!success){
					showActivityToast(R.string.logout_fail);
					return;
				}
				
				showActivityToast(R.string.logout_success);
				LoginInfo.getInstance().logout();
				//FBLogout
				LoginManager.getInstance().logOut();
				reloadUserInfoView();
				
				//send broadcast to anyone who wants to know
				if (getActivity()!=null){
					Intent intent = new Intent();
					intent.setAction(BHConstants.BROADCAST_USER_STATUS);
					LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
				}
			}
		});
		
	}

	/* Adapter */
	BaseAdapter selectItemAdapter = new BaseAdapter() {
		@Override
		public int getCount() {
			return seletItemTitles.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View rowView, ViewGroup parent) {

			if (rowView == null
					|| rowView.getClass().equals(SelectRow.class) == false) {
				rowView = new SelectRow(getActivity());
			}

			Boolean isSelected = (position == mCurrentSelectedPosition) ? true
					: false;

			SelectRow selectRow = (SelectRow) rowView;
			selectRow.reloadCell(position, seletItemTitles[position],
					isSelected);

			selectRow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Special: 聯絡客服，直接執行動作
					if (position == BHConstants.MENU_CHAT_POSITION) {
						if (getActivity() != null) {
							((BaseActivity) getActivity()).contactService();
						}
						return;
					}

					// 如果目前已經在此功能頁，直接關掉menu
					if (position == mCurrentSelectedPosition) {
						triggerDrawer();
						return;
					}

					selectItem(position);
				}
			});

			return rowView;
		}
	};

}
