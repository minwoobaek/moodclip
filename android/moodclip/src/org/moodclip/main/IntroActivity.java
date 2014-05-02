package org.moodclip.main;

import java.util.HashMap;
import java.util.Map;

import org.moodclip.login.PreLoginActivity;

import com.flurry.android.FlurryAdListener;
import com.flurry.android.FlurryAdSize;
import com.flurry.android.FlurryAdType;
import com.flurry.android.FlurryAds;
import com.flurry.android.FlurryAgent;
import com.flurry.android.impl.ads.FlurryAdModule;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
public class IntroActivity extends Activity {

	Handler handler;
	ImageView image;
	Intent intent;

	public boolean loginState = false;
	public boolean tutorialState = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_intro);

		SharedPreferences setting = getSharedPreferences("SETTING", 0);
		loginState = setting.getBoolean("loginState", loginState);
		tutorialState = setting.getBoolean("TutorialState", tutorialState);

		handler = new Handler();
		handler.postDelayed(r, 700);
		
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.setReportLocation (false);
		FlurryAgent.onStartSession(this, "J78FS2F7MZ24Q7T9KSDV");
		FlurryAgent.logEvent("intro");
		}
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
	
	
	Runnable r = new Runnable() {
		public void run() {
			if (loginState) {
				if (tutorialState) {
					intent = new Intent(IntroActivity.this,
							MainActivity.class);
					FlurryAgent.logEvent("MainActivity");

					SharedPreferences userinfo = getSharedPreferences("INFO", 0);
					String facebookNum = userinfo.getString("id", "");
					String userName = userinfo.getString("name", "");

					intent.putExtra("facebookNum", facebookNum);
					intent.putExtra("userName", userName);

					startActivity(intent);
					finish();
				}else{
					intent = new Intent(IntroActivity.this, TutorialActivity1.class);
					FlurryAgent.logEvent("Tutorial1");
					
					SharedPreferences userinfo = getSharedPreferences("INFO", 0);
					String facebookNum = userinfo.getString("id", "");
					String userName = userinfo.getString("name", "");

					intent.putExtra("facebookNum", facebookNum);
					intent.putExtra("userName", userName);
					
					startActivity(intent);
					finish();
				}
				// fade in,fade out
				// overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			} else {
				intent = new Intent(IntroActivity.this,
						PreLoginActivity.class);
				FlurryAgent.logEvent("PreLoginActivity");
				startActivity(intent);
				finish();
				// fade in,fade out
				// overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			}
		}
	};

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		handler.removeCallbacks(r);
		FlurryAgent.logEvent("onbackpressed");
	}

}
