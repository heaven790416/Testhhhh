package com.yowoo.newbuyhouse.login;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.BaseActivity.OkOnClickListener;
import com.yowoo.newbuyhouse.network.LoginService;
import com.yowoo.newbuyhouse.network.TrackService;
import com.yowoo.newbuyhouse.network.LoginService.MemberCallback;
import com.yowoo.newbuyhouse.network.TrackService.TrackCallback;

public class LoginStatusAction implements LoginStatus{

	BaseActivity context;
	
	public LoginStatusAction(BaseActivity context){
		this.context = context;
	}
	
	@Override
	public void updateTrackAndIsLoginAction() {
		// TODO Auto-generated method stub
		int localFavCount = LoginInfo.getInstance().favJSONArray.length();
		if (localFavCount==0){
			//local沒有收藏物件，直接更新isLogin
			updateIsLoginAction();
		}else{
			//local有收藏物件，先詢問是否合併
			//若選是，先合併再isLogin
			//若選否，直接isLogin
			showFavMergeDialogAction();
		}
	}

	@Override
	public void showFavMergeDialogAction() {
		// TODO Auto-generated method stub
		int localFavCount = LoginInfo.getInstance().favJSONArray.length();
		String msg = String.format(context.getString(R.string.prompt_merge_fav_house),localFavCount);
		String status = "success"; 
		
		context.showYesNoDialog_TrackCollect_Login("登入成功！", msg, status, new OkOnClickListener(){
			@Override
			public void didClickOk() {
				//TODO: yes, 要合併
				String houseNOs = LoginInfo.getInstance().getFavHouseNOs();
				if (!houseNOs.equals("")){

					Log.e("test", "houseNOs:"+houseNOs);
					TrackService.trackHouse(houseNOs, "", new TrackCallback(){
						@Override
						public void onResult(boolean success, String debugMessage) {
							Log.e("test", "trackHouse:"+success+" msg:"+debugMessage);
							//不管trackhouse的merge有沒有成功，都繼續進行登入後更新資料動作
							updateIsLoginAction();
						}
					});
				}else{
					updateIsLoginAction();
				}
			}

			@Override
			public void didClickCancel() {
				//TODO: no, 不合併
				showConfirmDialogAction();
			}
		});
	}


	@Override
	public void showConfirmDialogAction() {
		// TODO Auto-generated method stub
		String status = "confirm";
		context.showYesNoDialog_TrackCollect_Login("", context.getString(R.string.prompt_delete_fav), status,  new OkOnClickListener(){

			@Override
			public void didClickOk() {
				//確定要刪除local favs，略過合併功能
				updateIsLoginAction();
			}

			@Override
			public void didClickCancel() {
				//不直接刪除，退回上一步驟，詢問是否合併
				showFavMergeDialogAction();
			}
		});
	}
	
	@Override
	public void updateIsLoginAction() {
		// TODO Auto-generated method stub
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_FAV, 1);
		
		LoginService.isLogin(params, new MemberCallback(){
			@Override
			public void onResult(boolean success,JSONObject memberJSONObject,
					String debugMessage) {
				if (success){
					Log.e("test", "isLogin:"+success+" member:"+memberJSONObject.toString());
					Log.e("test", "fav count:"+LoginInfo.getInstance().favJSONArray.length());
					//check是否已有手機/Email資料
					//若沒有，邀請使用者增加填寫手機＆email
					updateUserDataAction();
					context.finish();
				}else{
					context.showToast(context.getString(R.string.prompt_update_user_data_fail));
				}
			}
		});
	}

	@Override
	public void updateUserDataAction() {
		// TODO Auto-generated method stub
		String mobile = LoginInfo.getInstance().getMemberStringData(UserConstants.KEY_MOBILE);
		String email = LoginInfo.getInstance().getMemberStringData(UserConstants.KEY_EMAIL);
		
		if (mobile.equals("")&&email.equals("")){
			Intent intent = new Intent().setClass(context, InputBasicDataActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(intent);
		}
	}


	
}
