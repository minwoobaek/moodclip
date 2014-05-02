package org.moodclip.main;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

public class TutorialActivity2 extends Activity implements OnTouchListener {

	ViewFlipper viewflipper;
	private int m_nPreTouchPosX = 0;
	Intent intent;
	
    String userName = "";
	String facebookNum = "";
	
	public static Activity TutorialActivity2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_tutorial2);
		FlurryAgent.logEvent("Tutorialactivity2");
		TutorialActivity2 = TutorialActivity2.this;
		
		intent = getIntent();
		userName = intent.getStringExtra("userName");
		facebookNum = intent.getStringExtra("facebookNum");

		viewflipper = (ViewFlipper) findViewById(R.id.viewFlipper1);
		viewflipper.setOnTouchListener(this);
		
		ViewFlipperSet();
		
	}

	public void ViewFlipperSet() {
		LinearLayout lay2 = (LinearLayout) View.inflate(this,
				R.layout.tutorial2, null);
		LinearLayout lay3 = (LinearLayout) View.inflate(this,
				R.layout.tutorial3, null);
		LinearLayout lay4 = (LinearLayout) View.inflate(this,
				R.layout.tutorial4, null);

		// viewflipper.addView(lay1);
		viewflipper.addView(lay2);
		viewflipper.addView(lay3);
		viewflipper.addView(lay4);
		FlurryAgent.logEvent("viewflipper");

	}

	private void MoveNextView() {
		if (viewflipper.getDisplayedChild() == 2) {
			intent = new Intent(TutorialActivity2.this, TutorialActivity3.class);
			FlurryAgent.logEvent("Tutorialactivity3");
			intent.putExtra("facebookNum", facebookNum);
			intent.putExtra("userName", userName);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

		} else {
			viewflipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_in));
			viewflipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_out));
			viewflipper.showNext();
		}
	}

	private void MovePreviousView() {
		if (viewflipper.getDisplayedChild() == 0) {
			finish();
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);

		} else {
			viewflipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_in));
			viewflipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_out));
			viewflipper.showPrevious();
		}
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

				MoveNextView();

			} else if (nTouchPosX > m_nPreTouchPosX) {

				MovePreviousView();
			}
			m_nPreTouchPosX = nTouchPosX;
		}

		return true;
	}

	@Override
	public void onBackPressed() {
		MovePreviousView();
	}

}
