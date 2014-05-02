package org.moodclip.main;

import java.util.HashMap;
import java.util.Map;

import org.moodclip.menu.setting.SettingActicity;
import org.moodclip.menu.setting.favorite.FavoriteActivity;
import org.moodclip.video.YoutubePlayerActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.widget.ProfilePictureView;
import com.flurry.android.FlurryAds;
import com.flurry.android.FlurryAgent;

public class MainActivity extends Activity {

		
		public static Activity Main;
		
		public String serial_num;
		public String title;
		public String signal = "false";
		
		public static final String FUNNY = "funny";
		public static final String CUTE = "cute";
		public static final String AMAZING = "amazing";
		public static final String IMPRESSIVE = "impressive";
		public static final String CRAZY = "crazy";
		public static final String SEXY = "sexy";
		public static final String EXCITING = "exciting";
		public static final String SCARE = "scare";

		Intent intent; 

		String[] emotion = { FUNNY, CUTE, AMAZING, IMPRESSIVE, CRAZY, SEXY,
				EXCITING, SCARE };
		String inemotion;
		String getEmotion;
		
		View originView;
		View touchingView;

		private ImageView funnyimg;
		private ImageView cuteimg;
		private ImageView amazingimg;
		private ImageView impressiveimg;
		private ImageView crazyimg;
		private ImageView sexyimg;
		private ImageView excitingimg;
		private ImageView scareimg;

		private LinearLayout dropLaylout;

		BitmapDrawable touchingImg; 

		//Fragment fragments = new Fragment();
		
		LinearLayout MainBtnLay;
		LinearLayout SettingLay;
		public boolean MainAndSettingFlag = true;
		
		ProfilePictureView miniProfile_picture;
		TextView miniProfile_name;
		FrameLayout miniProfileLayout;
		
		String userName="";
		String facebookNum="";

		ActionBar ab;
		
		ImageView MainMenu;
		private static Session ss;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.main_activity);
			FlurryAgent.logEvent("Main");
			/*
			ab = getActionBar();
			ab.setDisplayShowHomeEnabled(false);
			ab.setHomeButtonEnabled(false);
			ab.setDisplayShowCustomEnabled(true);		
			ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.moodclip_actionbar));
			*/
			MainMenu = (ImageView) findViewById(R.id.MainMenu);
			MainMenu.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					intent = new Intent(MainActivity.this,SettingActicity.class);
					   intent.putExtra("flag", "false");
					   startActivity(intent);
					   overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
				}
			});
			
			miniProfileLayout = (FrameLayout) findViewById(R.id.MiniProfileLayout);
			miniProfileLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					intent = new Intent(MainActivity.this, FavoriteActivity.class);
					
					SharedPreferences userinfo = getSharedPreferences("INFO", 0);
					intent.putExtra("uid", userinfo.getString("id", ""));
					intent.putExtra("name", userinfo.getString("name", ""));
					intent.putExtra("email", userinfo.getString("email", ""));
					intent.putExtra("flag", "true");
					FlurryAgent.logEvent("FavoriteActivity");
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
				}
			});
			miniProfile_picture = (ProfilePictureView)findViewById(R.id.MiniProfile_picture);
			miniProfile_picture.setCropped(true);
			miniProfile_name = (TextView) findViewById(R.id.MiniProfile_name);
			
			Typeface typeface = Typeface.createFromAsset(getAssets(), "540.ttf");
			miniProfile_name.setTypeface(typeface);
						
			intent = getIntent();
			userName = intent.getStringExtra("userName");
			facebookNum = intent.getStringExtra("facebookNum");
			
			miniProfile_picture.setProfileId(facebookNum);
			miniProfile_name.setText(userName);
			
			MainBtnLay = (LinearLayout) findViewById(R.id.MainBtnLay);
					
			MainBtnLay.setVisibility(View.VISIBLE);
			
			Main = MainActivity.this;

			funnyimg = (ImageView) findViewById(R.id.btn_moodclip_main_activitymain_funny);
			funnyimg.setOnTouchListener(mOnTouchListener);

			cuteimg = (ImageView) findViewById(R.id.btn_moodclip_main_activitymain_cute);
			cuteimg.setOnTouchListener(mOnTouchListener);

			amazingimg = (ImageView) findViewById(R.id.btn_moodclip_main_activitymain_amazing);
			amazingimg.setOnTouchListener(mOnTouchListener);

			impressiveimg = (ImageView) findViewById(R.id.btn_moodclip_main_activitymain_impressive);
			impressiveimg.setOnTouchListener(mOnTouchListener);

			crazyimg = (ImageView) findViewById(R.id.btn_moodclip_main_activitymain_crazy);
			crazyimg.setOnTouchListener(mOnTouchListener);

			sexyimg = (ImageView) findViewById(R.id.btn_moodclip_main_activitymain_sexy);
			sexyimg.setOnTouchListener(mOnTouchListener);

			excitingimg = (ImageView) findViewById(R.id.btn_moodclip_main_activitymain_exciting);
			excitingimg.setOnTouchListener(mOnTouchListener);

			scareimg = (ImageView) findViewById(R.id.btn_moodclip_main_activitymain_fear);
			scareimg.setOnTouchListener(mOnTouchListener);

			dropLaylout = (LinearLayout) findViewById(R.id.linearlayout_moodclip_main_activitymain_drop);
			dropLaylout.setOnDragListener(mDragListener);
			
			LoginLogoutStateSetting();
		}
		View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// TODO Auto-generated method stub

				switch (view.getId()) {
				case R.id.btn_moodclip_main_activitymain_funny:
					getEmotion = FUNNY;
					break;
				case R.id.btn_moodclip_main_activitymain_cute:
					getEmotion = CUTE;
					break;
				case R.id.btn_moodclip_main_activitymain_amazing:
					getEmotion = AMAZING;
					break;
				case R.id.btn_moodclip_main_activitymain_impressive:
					getEmotion = IMPRESSIVE;
					break;
				case R.id.btn_moodclip_main_activitymain_crazy:
					getEmotion = CRAZY;
					break;
				case R.id.btn_moodclip_main_activitymain_sexy:
					getEmotion = SEXY;
					break;
				case R.id.btn_moodclip_main_activitymain_exciting:
					getEmotion = EXCITING;
					break;
				case R.id.btn_moodclip_main_activitymain_fear:
					getEmotion = SCARE;
					break;
				}

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Vibrator vibrator = (Vibrator) getApplication()
							.getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(100);
					ClipData clip = ClipData.newPlainText("", getEmotion);
					view.startDrag(clip, new CanvasShadow(view, (int) event.getX(),
							(int) event.getY()), view, 0);
					view.setVisibility(View.INVISIBLE);
					touchingView = view; 
					
					return true;
					 
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					
					//view.setVisibility(View.VISIBLE);
					touchingView.setVisibility(View.VISIBLE);
					
					switch (touchingView.getId()) {
					case R.id.btn_moodclip_main_activitymain_funny:
						touchingImg = (BitmapDrawable) getResources().getDrawable(
								R.drawable.btn_moodclip_main_activitymain_funny);
						funnyimg.setImageDrawable(touchingImg);
						break;
					case R.id.btn_moodclip_main_activitymain_cute:
						touchingImg = (BitmapDrawable) getResources().getDrawable(
								R.drawable.btn_moodclip_main_activitymain_cute);
						cuteimg.setImageDrawable(touchingImg);
						break;
					case R.id.btn_moodclip_main_activitymain_amazing:
						touchingImg = (BitmapDrawable) getResources().getDrawable(
								R.drawable.btn_moodclip_main_activitymain_amazing);
						amazingimg.setImageDrawable(touchingImg);
						break;
					case R.id.btn_moodclip_main_activitymain_impressive:
						touchingImg = (BitmapDrawable) getResources().getDrawable(
										R.drawable.btn_moodclip_main_activitymain_impressive);
						impressiveimg.setImageDrawable(touchingImg);
						break;
					case R.id.btn_moodclip_main_activitymain_crazy:
						touchingImg = (BitmapDrawable) getResources().getDrawable(
								R.drawable.btn_moodclip_main_activitymain_crazy);
						crazyimg.setImageDrawable(touchingImg);
						break;
					case R.id.btn_moodclip_main_activitymain_sexy:
						touchingImg = (BitmapDrawable) getResources().getDrawable(
								R.drawable.btn_moodclip_main_activitymain_sexy);
						sexyimg.setImageDrawable(touchingImg);
						break;
					case R.id.btn_moodclip_main_activitymain_exciting:
						touchingImg = (BitmapDrawable) getResources()
								.getDrawable(
										R.drawable.btn_moodclip_main_activitymain_exciting);
						excitingimg.setImageDrawable(touchingImg);
						break;
					case R.id.btn_moodclip_main_activitymain_fear:
						touchingImg = (BitmapDrawable) getResources().getDrawable(
								R.drawable.btn_moodclip_main_activitymain_fear);
						scareimg.setImageDrawable(touchingImg);
						break;
					}
				}
				return false;
			}
		};
		

		View.OnDragListener mDragListener = new View.OnDragListener() {
			@Override
			public boolean onDrag(View v, DragEvent event) {
				// TODO Auto-generated method stub

				switch (event.getAction()) {
				case DragEvent.ACTION_DRAG_STARTED:
					return true;
				case DragEvent.ACTION_DRAG_ENTERED:
					return true;
				case DragEvent.ACTION_DRAG_EXITED:
					return true;
				case DragEvent.ACTION_DROP:
					inemotion = event.getClipData().getItemAt(0).getText()
							.toString(); 
					for (String value : emotion) {
						if (inemotion.equals(value)) {
							// intent = new Intent(getApplicationContext(),
							// MoodClip_ActivityVideoMain.class);
							// startActivity(intent);
							 intent = new Intent(getApplicationContext(),
									 YoutubePlayerActivity.class);
							 FlurryAgent.logEvent("YoutubePlayterActivity");
							 intent.putExtra("emotion", value);
							 intent.putExtra("user_id", facebookNum);
							 startActivity(intent);
							 overridePendingTransition(R.anim.push_right_in,
										R.anim.push_right_out);
						}
					}
					return true;
					
					
				case DragEvent.ACTION_DRAG_ENDED:
					touchingView.setVisibility(View.VISIBLE);

					switch (touchingView.getId()) {
					case R.id.btn_moodclip_main_activitymain_funny:
						touchingImg = (BitmapDrawable) getResources().getDrawable(
								R.drawable.btn_moodclip_main_activitymain_funny);
						funnyimg.setImageDrawable(touchingImg);
						FlurryAgent.logEvent("funny");
						break;
					case R.id.btn_moodclip_main_activitymain_cute:
						touchingImg = (BitmapDrawable) getResources().getDrawable(
								R.drawable.btn_moodclip_main_activitymain_cute);
						cuteimg.setImageDrawable(touchingImg);
						FlurryAgent.logEvent("cute");
						break;
					case R.id.btn_moodclip_main_activitymain_amazing:
						touchingImg = (BitmapDrawable) getResources().getDrawable(
								R.drawable.btn_moodclip_main_activitymain_amazing);
						amazingimg.setImageDrawable(touchingImg);
						FlurryAgent.logEvent("amazing");
						break;
					case R.id.btn_moodclip_main_activitymain_impressive:
						touchingImg = (BitmapDrawable) getResources().getDrawable(
										R.drawable.btn_moodclip_main_activitymain_impressive);
						impressiveimg.setImageDrawable(touchingImg);
						FlurryAgent.logEvent("impressinve");
						break;
					case R.id.btn_moodclip_main_activitymain_crazy:
						touchingImg = (BitmapDrawable) getResources().getDrawable(
								R.drawable.btn_moodclip_main_activitymain_crazy);
						crazyimg.setImageDrawable(touchingImg);
						FlurryAgent.logEvent("crazy");
						break;
					case R.id.btn_moodclip_main_activitymain_sexy:
						touchingImg = (BitmapDrawable) getResources().getDrawable(
								R.drawable.btn_moodclip_main_activitymain_sexy);
						sexyimg.setImageDrawable(touchingImg);
						FlurryAgent.logEvent("sexy");
						break;
					case R.id.btn_moodclip_main_activitymain_exciting:
						touchingImg = (BitmapDrawable) getResources()
								.getDrawable(
										R.drawable.btn_moodclip_main_activitymain_exciting);
						excitingimg.setImageDrawable(touchingImg);
						FlurryAgent.logEvent("exciting");
						break;
					case R.id.btn_moodclip_main_activitymain_fear:
						touchingImg = (BitmapDrawable) getResources().getDrawable(
								R.drawable.btn_moodclip_main_activitymain_fear);
						scareimg.setImageDrawable(touchingImg);
						FlurryAgent.logEvent("scare");
						
						break;
					}

					if (event.getResult()) { 
						return true;
					} else {

						Toast.makeText(getApplicationContext(), "물음표에 넣어 주세요",Toast.LENGTH_SHORT).show();
						return false;
					}
				}
				return false;
			}
		};

		class CanvasShadow extends View.DragShadowBuilder {
			int mWidth, mHeight;
			int mX, mY;
			

			public CanvasShadow(View v, int x, int y) {
				super(v);
				FlurryAgent.logEvent("canvasShadow");
				mWidth = v.getWidth();
				mHeight = v.getHeight();

				mX = x;
				mY = y;

				switch (v.getId()) {
				case R.id.btn_moodclip_main_activitymain_funny:
					touchingImg = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_moodclip_main_activitymain_funny_touch);
					funnyimg.setImageDrawable(touchingImg);
					FlurryAgent.logEvent("funny");
					break;
				case R.id.btn_moodclip_main_activitymain_cute:
					touchingImg = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_moodclip_main_activitymain_cute_touch);
					cuteimg.setImageDrawable(touchingImg);
					FlurryAgent.logEvent("cute");
					break;
				case R.id.btn_moodclip_main_activitymain_amazing:
					touchingImg = (BitmapDrawable) getResources()
							.getDrawable(
									R.drawable.btn_moodclip_main_activitymain_amazing_touch);
					amazingimg.setImageDrawable(touchingImg);
					FlurryAgent.logEvent("amazing");
					break;
				case R.id.btn_moodclip_main_activitymain_impressive:
					touchingImg = (BitmapDrawable) getResources()
							.getDrawable(
									R.drawable.btn_moodclip_main_activitymain_impressive_touch);
					impressiveimg.setImageDrawable(touchingImg);
					FlurryAgent.logEvent("impressive");
					break;
				case R.id.btn_moodclip_main_activitymain_crazy:
					touchingImg = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_moodclip_main_activitymain_crazy_touch);
					crazyimg.setImageDrawable(touchingImg);
					FlurryAgent.logEvent("crazy");
					break;
				case R.id.btn_moodclip_main_activitymain_sexy:
					touchingImg = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_moodclip_main_activitymain_sexy_touch);
					sexyimg.setImageDrawable(touchingImg);
					FlurryAgent.logEvent("sexy");
					break;
				case R.id.btn_moodclip_main_activitymain_exciting:
					touchingImg = (BitmapDrawable) getResources()
							.getDrawable(
									R.drawable.btn_moodclip_main_activitymain_exciting_touch);
					excitingimg.setImageDrawable(touchingImg);
					FlurryAgent.logEvent("exciting");
					break;
				case R.id.btn_moodclip_main_activitymain_fear:
					touchingImg = (BitmapDrawable) getResources().getDrawable(
							R.drawable.btn_moodclip_main_activitymain_fear_touch);
					scareimg.setImageDrawable(touchingImg);
					FlurryAgent.logEvent("scare");
					break;
				}
			}

			public void onProvideShadowMetrics(Point shadowSize,
					Point shadowTouchPoint) {
				shadowSize.set(mWidth, mHeight);
				shadowTouchPoint.set(mX, mY);
			}

			public void onDrawShadow(Canvas canvas) {
				super.onDrawShadow(canvas);

			}
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
/*
			SubMenu subMenu1 = menu.addSubMenu(null);

			subMenu1.add("怨듭��ы� ");
			subMenu1.add("�ㅼ� ");

			MenuItem subMenu1Item = subMenu1.getItem();
			subMenu1Item.setIcon(R.drawable.btn_moodclip_actionbar_menu);
			subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
					| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			return super.onCreateOptionsMenu(menu);
			*/
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			/*
			if (item.getTitle() == "怨듭��ы� ") {
				intent = new Intent(
						MainActivity.this,
						org.moodclip.menu.notice.MoodClip_menu_ActivityNoticeMain.class);
				startActivity(intent);
			}
			if (item.getTitle() == "�ㅼ� ") {
				intent = new Intent(
						MainActivity.this,
						org.moodclip.menu.setting.MoodClip_menu_ActicitySettingMain.class);
				startActivity(intent);
			}
			*/
			switch(item.getItemId()){
			  case R.id.item:
				  
			   intent = new Intent(MainActivity.this,SettingActicity.class);
			   intent.putExtra("flag", "false");
			   startActivity(intent);
			   overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				 /*
				  if(MainAndSettingFlag){
					  	MainBtnLay.setVisibility(View.INVISIBLE);
				  		SettingLay.setVisibility(View.VISIBLE);
				  		MainAndSettingFlag = false;
				  }else{
					  	MainBtnLay.setVisibility(View.VISIBLE);
				  		SettingLay.setVisibility(View.INVISIBLE);
				  		MainAndSettingFlag = true;
				  }
				  */
			   break;
			}
			return true;
		}
		
		public void LoginLogoutStateSetting(){
	    	//Login Logout State Setting
	    	SharedPreferences setting = getSharedPreferences("SETTING",0);
	    	SharedPreferences.Editor editor = setting.edit();
	    	editor.putBoolean("loginState", true);
	    	editor.commit();
	    }

}
