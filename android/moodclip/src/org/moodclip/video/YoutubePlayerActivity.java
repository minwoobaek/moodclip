package org.moodclip.video;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.moodclip.main.MainActivity;
import org.moodclip.main.R;
import org.moodclip.menu.setting.SettingActicity;
import org.moodclip.video.YoutubeApi.DeveloperKey;
import org.moodclip.video.YoutubeApi.YouTubeFailureRecoveryActivity;
import org.moodclip.video.comments.CommentsActivity;
import org.moodclip.video.gallery.FeedbackHttpHandler;
import org.moodclip.video.gallery.GalleryHttpHandler;
import org.moodclip.video.gallery.GalleryParsingHandler;
import org.moodclip.video.gallery.GalleryVideoInfoObject;
import org.moodclip.video.gallery.VideoThumbnailAdapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.flurry.android.FlurryAgent;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * A simple YouTube Android API demo application which shows how to create a
 * simple application that displays a YouTube Video in a
 * {@link YouTubePlayerView}.
 * <p>
 * Note, to use a {@link YouTubePlayerView}, your activity must extend
 * {@link YouTubeBaseActivity}.
 * 
 */
public class YoutubePlayerActivity extends YouTubeFailureRecoveryActivity implements YouTubePlayer.OnFullscreenListener {
	
	public static Activity PlayerActivity;

	TextView pageTitle;
	Gallery gallery;
	TextView text;
	ImageView pageImg;

	ImageView player_main_btn;
	ImageView player_menu_btn;
	ImageView btnGood;
	ImageView comments;
	ImageView share;

	public String signal = "false";
	public String pageCodename;
	public String videosrc_url;
	public String emotion;
	public String title;
	public String description = "";
	public String serial_num;
	public String maleGoodCnt;
	public String femaleGoodCnt;
	public Bitmap bm;
	public Bitmap pageBm;
	int backcnt = 0;  // 취소버튼 카운트
	public String owner;

	public String likeBtnReqState = "yes";
	public int CurrnetPosition = 0;
	public int CurrnetPositionCnt = 0;
	public List<Integer> position_list;

	ArrayList<GalleryVideoInfoObject> mData = new ArrayList<GalleryVideoInfoObject>();
	StringBuilder Data;

	VideoThumbnailAdapter Adapter;
	Intent intent;

	YouTubePlayerView youTubeView;
	YouTubePlayer Player;
	VideoView videoView;
	MediaController mc;

	ImageView EmotionBar;
	public Boolean likeBtnState;
	public boolean loginType;

	FrameLayout lay;
	public boolean portraitFlag = true;
	ImageView loadingImg;

	String user_id;
	int videoGroup_Order = 1;
	AnimationDrawable frame;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		FlurryAgent.logEvent("youtubeplay");

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video_player);

		position_list = new ArrayList<Integer>();
		position_list.add(CurrnetPosition);

		Typeface typeface = Typeface.createFromAsset(getAssets(), "RIXGOM.TTF");
		/*
		 * ab = getActionBar(); ab.setDisplayShowHomeEnabled(false);
		 * ab.setHomeButtonEnabled(false); ab.setDisplayShowCustomEnabled(true);
		 * ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.
		 * actionbar_background));
		 */
		lay = (FrameLayout) findViewById(R.id.lay);
		loadingImg = (ImageView) findViewById(R.id.loading);

		pageImg = (ImageView) findViewById(R.id.pageImg);
		player_main_btn = (ImageView) findViewById(R.id.player_main_btn);
		player_menu_btn = (ImageView) findViewById(R.id.player_menu_btn);
		EmotionBar = (ImageView) findViewById(R.id.EmotionBar);
		youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		btnGood = (ImageView) findViewById(R.id.goodBtn);
		pageTitle = (TextView) findViewById(R.id.pageTitle);
		text = (TextView) findViewById(R.id.playerTitle);
		text.setTypeface(typeface);
		comments = (ImageView) findViewById(R.id.commentsPage);
		share = (ImageView) findViewById(R.id.share);
		gallery = (Gallery) findViewById(R.id.gallery1);

		mc = new MediaController(this);
		videoView = (VideoView) findViewById(R.id.VideoView);
		mc.setMediaPlayer(videoView);
		videoView.setMediaController(mc);
		
		setMediaController();
		
		PlayerActivity = YoutubePlayerActivity.this;

		// Intent Get
		intent = getIntent();
		// Emotion PlayBar Set
		user_id = intent.getStringExtra("user_id");
		Log.d("bmw","으"+user_id);
		emotion = intent.getStringExtra("emotion");
		PlayBarButtonSet(emotion);

		// Thread Start
		new AsyncVideoJSONList().execute();

		// !!!-------------CLICK EVENT-----------!!!

		player_main_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		player_menu_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				intent = new Intent(YoutubePlayerActivity.this,
						SettingActicity.class);
				intent.putExtra("flag", "true");
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);

			}
		});
		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

				CurrnetPosition = position;
				position_list.add(CurrnetPosition);
				CurrnetPositionCnt++;

				GalleryVideoInfoObject videoData = mData.get(position);
				serial_num = videoData.serial_num;
				title = videoData.title;
				maleGoodCnt = videoData.goodCntMale;
				femaleGoodCnt = videoData.goodCntFemale;
				bm = videoData.bm;
				pageBm = videoData.pageBm;
				videosrc_url = videoData.src;
				pageCodename = videoData.kind;
				owner = videoData.owner;

				signal = "true";
				VideoTitleSerialNumSet(signal);
				if (pageCodename.equals("youtube")) {
					youTubeView.setVisibility(View.VISIBLE);
					videoView.setVisibility(View.INVISIBLE);
					Player.cueVideo(serial_num);
				} else {
					youTubeView.setVisibility(View.INVISIBLE);
					videoView.setVisibility(View.VISIBLE);
					playvideo(videosrc_url);
				}

				likeBtnReqState = "yes";
				new LikeBtnTask().execute();

			}
		});
		gallery.setOnTouchListener(new View.OnTouchListener() {
			float xAtDown;
			float xAtUp;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					xAtDown = event.getX();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					xAtUp = event.getX();
					if (xAtDown > xAtUp) {
						if (Adapter.getCount() == gallery
								.getLastVisiblePosition() + 1) {
							videoGroup_Order++;
							new AddAsyncVideoJSONList().execute();
							LoadingDialog.showLoading(YoutubePlayerActivity.this);
						}
					}
				}
				return false;
			}
		});
		btnGood.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				likeBtnReqState = "no";
				new LikeBtnTask().execute();

				if (likeBtnState){
					Toast.makeText(YoutubePlayerActivity.this, "마이클립에 저장되었습니다.",
							Toast.LENGTH_SHORT).show();
					LikeBtnSetLiked(emotion);
				}else if(likeBtnState==false){
						Toast.makeText(YoutubePlayerActivity.this, "마이클립에서 삭제되었습니다.",
								Toast.LENGTH_SHORT).show();
						LikeBtnSetLike(emotion);
				}
			}
		});
		comments.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(getApplicationContext(),
						CommentsActivity.class);
				intent.putExtra("serial_num", serial_num);
				intent.putExtra("emotion", emotion);
				intent.putExtra("title", title);
				intent.putExtra("kind", pageCodename);
				intent.putExtra("description", description);
				intent.putExtra("maleGoodCnt", maleGoodCnt);
				intent.putExtra("femaleGoodCnt", femaleGoodCnt);
				intent.putExtra("bm", bm);
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
			}
		});
		share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				if (pageCodename.equals("youtube")) {
					intent.putExtra(Intent.EXTRA_TEXT,
							"http://www.youtube.com/watch?v=" + serial_num);
				} else {
					intent.putExtra(Intent.EXTRA_TEXT,
							"https://www.facebook.com/photo.php?v="
									+ serial_num);
				}
				intent.addCategory(Intent.CATEGORY_DEFAULT);

				startActivity(Intent.createChooser(intent, "공유"));
			}
		});
		videoView.setOnPreparedListener(new OnPreparedListener() {
			public void onPrepared(MediaPlayer mp) {
				loadingImg.setBackgroundDrawable(null);
				frame.stop();
				videoView.setVisibility(View.VISIBLE);
			}
		});
	}
	

	// !!!!!!!!!!!!!----------onCreate Method End-----------!!!!!!!!!!!!!!!

	
	@Override
	 protected void onPause() {
		//finish();
		 super.onPause();
	}
	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			Player = player;
			// Player.cueVideos(serial_list);
			// Player.cueVideo(serial_num);
			// Player.loadVideos(serial_list);
			// Player.cueVideo(serial_num);
		}
	}

	@Override
	protected Provider getYouTubePlayerProvider() {
		// TODO Auto-generated method stub
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}

	public void startVideo() {
		youTubeView.initialize(DeveloperKey.DEVELOPER_KEY, this);
	}

	public class AsyncVideoJSONList extends
			AsyncTask<String, Integer, ArrayList<GalleryVideoInfoObject>> {
		//ProgressDialog Dialog;

		protected void onPreExecute() {
			//Dialog = ProgressDialog.show(YoutubePlayerActivity.this, "","잠시만 기다려주세요..");
			//Dialog.setCancelable(false);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		@Override
		protected ArrayList<GalleryVideoInfoObject> doInBackground(
				String... params) {
			Data = GalleryHttpHandler.getData(emotion, user_id);
			GalleryParsingHandler parser = new GalleryParsingHandler();
			return parser.getJSONVideoRequestAllList(Data, true, false, 1);
		}

		@Override
		protected void onPostExecute(ArrayList<GalleryVideoInfoObject> result) {
			
			VideoTitleSerialNumSet(signal);
			new LikeBtnTask().execute();
			startVideo();

			if (result != null) {
				mData = result;
				Adapter = new VideoThumbnailAdapter(YoutubePlayerActivity.this,
						mData);
				gallery.setAdapter(Adapter);
				Adapter.notifyDataSetChanged();
			}
			if (pageCodename.equals("youtube")) {
				youTubeView.setVisibility(View.VISIBLE);
				videoView.setVisibility(View.INVISIBLE);
				Player.cueVideo(serial_num);
			} else {
				youTubeView.setVisibility(View.INVISIBLE);
				videoView.setVisibility(View.VISIBLE);
			}
			//Dialog.dismiss();
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			new AddAsyncVideoJSONList().execute();
		}
	}

	private class LikeBtnTask extends AsyncTask<Void, Void, String> {

		protected void onPreExecute() {
			LikeBtnSetLike(emotion);
		}

		@Override
		protected String doInBackground(Void... params) {

			SharedPreferences setting = getSharedPreferences("LOGINTYPE", 0);
			loginType = setting.getBoolean("loginType", loginType);
			String num = null;

			if (loginType) {
				SharedPreferences userinfo = getSharedPreferences("INFO", 0);
				num = userinfo.getString("email", "");

			} else if (!loginType) {
				SharedPreferences userinfo = getSharedPreferences("INFO", 0);
				num = userinfo.getString("id", "");
			}

			return FeedbackHttpHandler.getData(num, serial_num, emotion, likeBtnReqState);

		}

		@Override
		protected void onPostExecute(String loginResult) {
			Log.d("httptest", "" + loginResult);
			if (loginResult.equals("fail")) {

				Toast.makeText(YoutubePlayerActivity.this, "오류 ",
						Toast.LENGTH_SHORT).show();

			} else if (loginResult.equals("like")) {

				LikeBtnSetLiked(emotion);
				likeBtnState = false;

			} else if (loginResult.equals("liked")) {
				LikeBtnSetLike(emotion);
				likeBtnState = true;

			} else if (loginResult.equals("exist")) {
				LikeBtnSetLiked(emotion);
				likeBtnState = false;

			} else if (loginResult.equals("non")) {
				LikeBtnSetLike(emotion);
				likeBtnState = true;
			}
		}
	}

	public class AddAsyncVideoJSONList extends
			AsyncTask<String, Integer, ArrayList<GalleryVideoInfoObject>> {
		public boolean running = true;

		protected void onCancelled() {
			running = false;
		}

		protected void onPreExecute() {

		}

		@Override
		protected ArrayList<GalleryVideoInfoObject> doInBackground(
				String... params) {
			Data = GalleryHttpHandler.getData2(user_id, Integer.toString(videoGroup_Order));
			GalleryParsingHandler parser = new GalleryParsingHandler();
			mData.addAll(parser.getJSONVideoRequestAllList(Data, false, false,0));
			return mData;
		}

		@Override
		protected void onPostExecute(ArrayList<GalleryVideoInfoObject> result) {

			if (result != null) {

				Adapter.notifyDataSetChanged();
			}
			LoadingDialog.hideLoading();
			
			videoGroup_Order++;
			new AddAsyncVideoJSONList().execute();
			
		}
	}

	@Override
	public void onBackPressed() {

		if (portraitFlag) {
			if (CurrnetPosition == 0) {
				super.onBackPressed();

			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

				CurrnetPosition = position_list.get(CurrnetPositionCnt - 1);
				position_list.remove(CurrnetPositionCnt);
				CurrnetPositionCnt--;

				GalleryVideoInfoObject videoData = mData.get(CurrnetPosition);
				serial_num = videoData.serial_num;
				videosrc_url = videoData.src;
				title = videoData.title;
				maleGoodCnt = videoData.goodCntMale;
				femaleGoodCnt = videoData.goodCntFemale;
				bm = videoData.bm;
				pageBm = videoData.pageBm;
				owner = videoData.owner;

				signal = "true";
				VideoTitleSerialNumSet(signal);
				if (pageCodename.equals("youtube")) {
					youTubeView.setVisibility(View.VISIBLE);
					videoView.setVisibility(View.INVISIBLE);
					Player.cueVideo(serial_num);
				} else {
					youTubeView.setVisibility(View.INVISIBLE);
					videoView.setVisibility(View.VISIBLE);
					playvideo(videosrc_url);
				}

				likeBtnReqState = "yes";
				new LikeBtnTask().execute();

				gallery.setSelection(CurrnetPosition);
			}
		} else {
			
			
			if(backcnt==0){   // back 버튼 카운트
				Toast.makeText(getApplicationContext(), "이전 화면으로 돌아가려면 취소 버튼을 다시 누르세요.",Toast.LENGTH_SHORT).show();
				backcnt++;    
				Handler hd = new Handler();
				hd.postDelayed(new Runnable() {
					@Override
					public void run() {
						backcnt =0;   // 3초 뒤에 다시 0으로 됨
					}
				}, 3000);
			}else if(backcnt==1){   // back 버튼 카운트 1되면 원래 화면으로 돌아가면서 종료
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					if (pageCodename.equals("youtube")) {
						intent = new Intent(YoutubePlayerActivity.this,
								YoutubePlayerFullscreenActivity.class);
						intent.putExtra("serial_num", serial_num);
						intent.putExtra("time", Player.getCurrentTimeMillis());
						startActivityForResult(intent, 1);
					} else {
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LayoutParams.MATCH_PARENT, 0, 46);
						lay.setLayoutParams(params);
						
						setMediaController();
						
						Handler hd = new Handler();
						hd.postDelayed(new Runnable() {
							@Override
							public void run() {
								setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);  // 2초뒤 센서 돌아옴
							}
						}, 2000);
					}
				}
		}
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * 
	 * getMenuInflater().inflate(R.menu.youtube_player, menu); return true; }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) {
	 * 
	 * switch (item.getItemId()) {
	 * 
	 * case R.id.item1: finish(); break;
	 * 
	 * case R.id.item2:
	 * 
	 * intent = new Intent(YoutubePlayerActivity.this, SettingActicity.class);
	 * intent.putExtra("flag", "true"); startActivity(intent);
	 * overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	 * 
	 * break;
	 * 
	 * }
	 * 
	 * 
	 * return true; }
	 */

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

			portraitFlag = true;
			if (pageCodename.equals("youtube")) {
				intent = new Intent(YoutubePlayerActivity.this,
						YoutubePlayerFullscreenActivity.class);
				intent.putExtra("serial_num", serial_num);
				intent.putExtra("time", Player.getCurrentTimeMillis());
				startActivityForResult(intent, 1);
			} else {
				
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, 0, 46);
				lay.setLayoutParams(params);

				setMediaController();
			}

		} else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

			portraitFlag = false;

			if (pageCodename.equals("youtube")) {
				intent = new Intent(YoutubePlayerActivity.this,
						YoutubePlayerFullscreenActivity.class);
				intent.putExtra("serial_num", serial_num);
				intent.putExtra("time", Player.getCurrentTimeMillis());
				startActivityForResult(intent, 1);
			} else {
				Display display = getWindowManager().getDefaultDisplay();
				int w = display.getWidth();
				int h = display.getHeight();

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						w, h);
				lay.setLayoutParams(params);

				FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
						Gravity.BOTTOM);
				mc.setLayoutParams(params2);

			}
		}

	}

	@Override
	public void onFullscreen(boolean isFullscreen) {
		//boolean fullscreen = isFullscreen;
	}

	public void VideoTitleSerialNumSet(String signal) {
		if (signal.equals("true")) {
			text.setText(title);
			pageTitle.setText(owner);
			pageImg.setImageBitmap(pageBm);
		} else {
			serial_num = GalleryParsingHandler.getSerialNum();
			pageCodename = GalleryParsingHandler.getKind();
			// videosrc_url = GalleryParsingHandler.getSrc();
			title = GalleryParsingHandler.getTitle();
			maleGoodCnt = GalleryParsingHandler.getgoodCntMale();
			femaleGoodCnt = GalleryParsingHandler.getgoodCntFemale();
			bm = GalleryParsingHandler.getBitmap();
			pageBm = GalleryParsingHandler.getPageBitmap();
			owner = GalleryParsingHandler.getOwner();
			
			text.setText(title);
			pageTitle.setText(owner);
			pageImg.setImageBitmap(pageBm);
		}
	}

	public void PlayBarButtonSet(String e) {

		if (e.equals("amazing")) {
			EmotionBar.setBackgroundResource(R.drawable.emotion_bar_amazing);
			comments.setImageResource(R.drawable.comments_btn_amazing_sel);
			share.setImageResource(R.drawable.share_btn_amazing_sel);

		} else if (e.equals("exciting")) {
			EmotionBar.setBackgroundResource(R.drawable.emotion_bar_exciting);
			comments.setImageResource(R.drawable.comments_btn_exciting_sel);
			share.setImageResource(R.drawable.share_btn_exciting_sel);

		} else if (e.equals("scare")) {
			EmotionBar.setBackgroundResource(R.drawable.emotion_bar_scare);
			comments.setImageResource(R.drawable.comments_btn_scare_sel);
			share.setImageResource(R.drawable.share_btn_scare_sel);

		} else if (e.equals("funny")) {
			EmotionBar.setBackgroundResource(R.drawable.emotion_bar_funny);
			comments.setImageResource(R.drawable.comments_btn_funny_sel);
			share.setImageResource(R.drawable.share_btn_funny_sel);

		} else if (e.equals("impressive")) {
			EmotionBar.setBackgroundResource(R.drawable.emotion_bar_impressive);
			comments.setImageResource(R.drawable.comments_btn_impressive_sel);
			share.setImageResource(R.drawable.share_btn_impressive_sel);

		} else if (e.equals("crazy")) {
			EmotionBar.setBackgroundResource(R.drawable.emotion_bar_crazy);
			comments.setImageResource(R.drawable.comments_btn_crazy_sel);
			share.setImageResource(R.drawable.share_btn_crazy_sel);

		} else if (e.equals("cute")) {
			EmotionBar.setBackgroundResource(R.drawable.emotion_bar_cute);
			comments.setImageResource(R.drawable.comments_btn_cute_sel);
			share.setImageResource(R.drawable.share_btn_cute_sel);

		} else if (e.equals("sexy")) {
			EmotionBar.setBackgroundResource(R.drawable.emotion_bar_sexy);
			comments.setImageResource(R.drawable.comments_btn_sexy_sel);
			share.setImageResource(R.drawable.share_btn_sexy_sel);

		}
	}

	public void LikeBtnSetLiked(String e) {

		if (e.equals("amazing")) {
			btnGood.setImageResource(R.drawable.liked_btn_amazing_sel);

		} else if (e.equals("exciting")) {
			btnGood.setImageResource(R.drawable.liked_btn_exciting_sel);

		} else if (e.equals("scare")) {
			btnGood.setImageResource(R.drawable.liked_btn_scare_sel);

		} else if (e.equals("funny")) {
			btnGood.setImageResource(R.drawable.liked_btn_funny_sel);

		} else if (e.equals("impressive")) {
			btnGood.setImageResource(R.drawable.liked_btn_impressive_sel);

		} else if (e.equals("crazy")) {
			btnGood.setImageResource(R.drawable.liked_btn_crazy_sel);

		} else if (e.equals("cute")) {
			btnGood.setImageResource(R.drawable.liked_btn_cute_sel);

		} else if (e.equals("sexy")) {
			btnGood.setImageResource(R.drawable.liked_btn_sexy_sel);

		}
	}

	public void LikeBtnSetLike(String e) {

		if (e.equals("amazing")) {
			btnGood.setImageResource(R.drawable.like_btn_amazing_sel);

		} else if (e.equals("exciting")) {
			btnGood.setImageResource(R.drawable.like_btn_exciting_sel);

		} else if (e.equals("scare")) {
			btnGood.setImageResource(R.drawable.like_btn_scare_sel);

		} else if (e.equals("funny")) {
			btnGood.setImageResource(R.drawable.like_btn_funny_sel);

		} else if (e.equals("impressive")) {
			btnGood.setImageResource(R.drawable.like_btn_impressive_sel);

		} else if (e.equals("crazy")) {
			btnGood.setImageResource(R.drawable.like_btn_crazy_sel);

		} else if (e.equals("cute")) {
			btnGood.setImageResource(R.drawable.like_btn_cute_sel);

		} else if (e.equals("sexy")) {
			btnGood.setImageResource(R.drawable.like_btn_sexy_sel);

		}
	}

	void playvideo(String url) {

		loadingImg.setBackgroundResource(R.anim.loading);
		
		frame = (AnimationDrawable) loadingImg.getBackground();
		frame.start();
		
		Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotation);
		loadingImg.startAnimation(rotation);
		
		new VideoSourceTask().execute();
	}

	public void setMediaController() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int deviceWidth = displayMetrics.widthPixels;
		int deviceHeight = displayMetrics.heightPixels;

		FrameLayout.MarginLayoutParams margin = new FrameLayout.MarginLayoutParams(
				MarginLayoutParams.MATCH_PARENT,
				MarginLayoutParams.WRAP_CONTENT);
		if (deviceHeight == 1280 || deviceHeight == 1184) {
			margin.setMargins(0, 0, 0, 630);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					margin);
			mc.setLayoutParams(params);

			// 600 -> 1.5배 -> 900
		} else if (deviceWidth == 1080) {
			margin.setMargins(0, 0, 0, 900);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					margin);
			mc.setLayoutParams(params);
		} else if (deviceHeight == 1024) {
			margin.setMargins(0, 0, 0, 504);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					margin);
			mc.setLayoutParams(params);
		}
	}

	public class VideoSourceTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			String data = null;
			JSONObject jData = null;
				try {
					jData = new JSONObject(GalleryHttpHandler.getVideoSrc(serial_num).toString());
					data = jData.getString("source");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {

			if (result != null) {
				videoView.setVideoURI(Uri.parse(result)); 
				videoView.start();
			}else{
				Toast.makeText(getApplicationContext(), "영상을 불러오지 못했습니다",Toast.LENGTH_SHORT).show();
			}
		}
	}
	
}