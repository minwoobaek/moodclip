package org.moodclip.main;

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

public class TutorialActivity3 extends Activity implements OnTouchListener{
	
	private int m_nPreTouchPosX = 0;
	ImageView btn;
	Intent intent;
    FrameLayout lay;
    
    String userName = "";
	String facebookNum = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_tutorial3);
		
		intent = getIntent();
		userName = intent.getStringExtra("userName");
		facebookNum = intent.getStringExtra("facebookNum");
		
		lay = (FrameLayout) findViewById(R.id.tutorial3);
		lay.setOnTouchListener(this);
		
		btn = (ImageView)findViewById(R.id.never_again_btn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences setting = getSharedPreferences("SETTING", 0);
				SharedPreferences.Editor editor = setting.edit();
				editor.putBoolean("TutorialState", true);
				editor.commit();
				
				intent = new Intent(TutorialActivity3.this,
						MainActivity.class);
				FlurryAgent.logEvent("MainActivity");
				intent.putExtra("facebookNum", facebookNum);
				intent.putExtra("userName", userName);
				startActivity(intent);
				TutorialActivity1.TutorialActivity1.finish();
				TutorialActivity2.TutorialActivity2.finish();
				finish();
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		});
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
				intent = new Intent(TutorialActivity3.this,
						MainActivity.class);
				FlurryAgent.logEvent("MainActivity");
				intent.putExtra("facebookNum", facebookNum);
				intent.putExtra("userName", userName);
				startActivity(intent);
				TutorialActivity1.TutorialActivity1.finish();
				TutorialActivity2.TutorialActivity2.finish();
				finish();
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);

			} else if (nTouchPosX > m_nPreTouchPosX) {
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
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

}
