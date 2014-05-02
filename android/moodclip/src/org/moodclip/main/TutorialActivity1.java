package org.moodclip.main;

import java.util.HashMap;
import java.util.Map;

import com.flurry.android.FlurryAds;
import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class TutorialActivity1 extends Activity implements OnTouchListener{
	
	private int m_nPreTouchPosX = 0;
	ImageView btn;
	Intent intent;
    FrameLayout lay;
    
    String userName = "";
	String facebookNum = "";
	
	public static Activity TutorialActivity1;
	@Override
	protected void onStart()
	{
		super.onStart();
	
		FlurryAgent.logEvent("Tutorialactivity1");
	}
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_tutorial1);
		
		TutorialActivity1 = TutorialActivity1.this;
		
		intent = getIntent();
		userName = intent.getStringExtra("userName");
		facebookNum = intent.getStringExtra("facebookNum");
		
		lay = (FrameLayout) findViewById(R.id.tutorial1);
		lay.setOnTouchListener(this);
		
		btn = (ImageView)findViewById(R.id.skip_btn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(TutorialActivity1.this,
						MainActivity.class);
				FlurryAgent.logEvent("MainActivity");
				intent.putExtra("facebookNum", facebookNum);
				intent.putExtra("userName", userName);
				finish();
				startActivity(intent);
			}
		});
		
		LoginLogoutStateSetting();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			m_nPreTouchPosX = (int) event.getX();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			int nTouchPosX = (int) event.getX();

			if (nTouchPosX < m_nPreTouchPosX) {
				intent = new Intent(TutorialActivity1.this,
						TutorialActivity2.class);
				FlurryAgent.logEvent("Tutorialactivity2");
				intent.putExtra("facebookNum", facebookNum);
				intent.putExtra("userName", userName);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);

			} else if (nTouchPosX > m_nPreTouchPosX) {
				
			}
			m_nPreTouchPosX = nTouchPosX;
		}

		return true;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	public void LoginLogoutStateSetting() {
		// Login Logout State Setting
		SharedPreferences setting = getSharedPreferences("SETTING", 0);
		SharedPreferences.Editor editor = setting.edit();
		editor.putBoolean("loginState", true);
		editor.commit();
	}
}
