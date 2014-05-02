package org.moodclip.menu.setting;

import org.moodclip.main.IntroActivity;
import org.moodclip.main.MainActivity;
import org.moodclip.main.R;
import org.moodclip.menu.setting.favorite.FavoriteActivity;
import org.moodclip.video.YoutubePlayerActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.flurry.android.FlurryAgent;

public class SettingActicity extends Activity {
	
	Intent intent;
	ToggleButton mSetPushTb;
	ListView listview;
	
	ImageView setting_main_btn;
	ImageView notice;
	ImageView profile_btn;
	ImageView logout;
	ImageView setting;
	
	public boolean loginType;
	public String flag;
	
	private Session.StatusCallback statusCallback = new SessionStatusCallback();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		FlurryAgent.logEvent("settingactivity");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_activity);
		
		// Intent Get
		intent = getIntent();
		flag = intent.getStringExtra("flag");
		/*
		ActionBar ab = getActionBar();
		ab.setDisplayShowHomeEnabled(false);
		ab.setHomeButtonEnabled(false);
		ab.setDisplayShowCustomEnabled(true);	
		ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));
		*/
		setting_main_btn = (ImageView)findViewById(R.id.setting_main_btn);
		notice = (ImageView)findViewById(R.id.notice_btn);
		profile_btn = (ImageView) findViewById(R.id.profile_btn);
		logout = (ImageView) findViewById(R.id.logout_btn);
		setting = (ImageView)findViewById(R.id.setting_btn);
		
		notice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "준비중입니다.. ",Toast.LENGTH_SHORT).show();
			}
		});
		profile_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				intent = new Intent(SettingActicity.this, FavoriteActivity.class);
				
				SharedPreferences userinfo = getSharedPreferences("INFO", 0);
				intent.putExtra("uid", userinfo.getString("id", ""));
				intent.putExtra("name", userinfo.getString("name", ""));
				intent.putExtra("email", userinfo.getString("email", ""));
				intent.putExtra("flag", "true");
				
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		});
		setting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(SettingActicity.this, PasswordChangeActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out); 
			}
		});
		setting_main_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.push_right_in,
							R.anim.push_right_out);
			}
		});
		
/*		
		String []list = {"공지사항 ","비밀번호 변경","개인 Good History","로그아웃" };
		
		ArrayAdapter<String> adapter;
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
		
		listview = (ListView)findViewById(R.id.listView1);
		listview.setAdapter(adapter);
*/
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback,
						savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this)
						.setCallback(statusCallback));
			}
		}

		updateView();
				
		//pushToggle();

	}
/*
	public void pushToggle() {
		final SharedPreferences settingPref = getSharedPreferences("setting",
				Activity.MODE_PRIVATE);
		boolean msgPushed = settingPref.getBoolean("msg_push", false);

		mSetPushTb = (ToggleButton) findViewById(R.id.tb_moodclip_menu_activitysetting_push);
		mSetPushTb.setChecked(msgPushed);
		mSetPushTb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				Editor editor = settingPref.edit();
				editor.putBoolean("msg_push", isChecked);
				editor.commit();
			}
		});
	}
*/
	public void clickEvent(){
		logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//Login Logout State Setting
		    	SharedPreferences Setting = getSharedPreferences("SETTING",0);
		    	SharedPreferences.Editor editor = Setting.edit();
		    	editor.putBoolean("loginState", false);
		    	editor.commit();
		    	
				SharedPreferences setting = getSharedPreferences("LOGINTYPE",0);
				loginType = setting.getBoolean("loginType", loginType);
		    	
		    	SharedPreferences userinfo = getSharedPreferences("INFO",0);
		    	SharedPreferences.Editor Editor = userinfo.edit();
		    	Editor.clear();
		    	Editor.commit();
		    	
				if(loginType){
					Next();
				}else if(!loginType){
					Next();
			    	onClickLogout();
				}
			}
		});
		/*
		listview.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent,View v,int position,long id){
				switch(position){
				case 0:
					Toast.makeText(getApplicationContext(), "준비중입니다.. ",Toast.LENGTH_SHORT).show();
					break;
				
				case 1:
					intent = new Intent(MoodClip_menu_ActicitySettingMain.this, PasswordChangeActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out); 
					break;
				case 2:
					intent = new Intent(MoodClip_menu_ActicitySettingMain.this, PersonalFirstListActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
					break;
				case 3:
					SharedPreferences setting = getSharedPreferences("LOGINTYPE",0);
					loginType = setting.getBoolean("loginType", loginType);
					
					//Login Logout State Setting
			    	SharedPreferences Setting = getSharedPreferences("SETTING",0);
			    	SharedPreferences.Editor editor = Setting.edit();
			    	editor.putBoolean("loginState", false);
			    	editor.commit();
			    	
					if(loginType){
						Next();
					}else if(!loginType){
						Next();
				    	onClickLogout();
					}
					break;
				}
			}
		});
		*/
	}
	
	private void updateView() {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
        	clickEvent();
        } else {
        	clickEvent();
        }
    }

	@Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
 
    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }

    private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			// TODO Auto-generated method stub
			updateView();
		}
    }
	
	public void Next(){	
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		am.restartPackage(getPackageName());
    	Intent intent = new Intent(SettingActicity.this, IntroActivity.class);
		startActivity(intent);
		if(flag.equals("true")){
			YoutubePlayerActivity.PlayerActivity.finish();
		}
		MainActivity.Main.finish();
		finish();
    }
/*	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}
	

	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		  case R.id.setting_main:
		   finish();
		   overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
		   break;
		}
		return true;
	}
*/
	@Override 
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_in,
				R.anim.push_right_out);
	}
}
