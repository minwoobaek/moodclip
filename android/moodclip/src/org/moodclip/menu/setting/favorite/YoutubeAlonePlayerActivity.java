package org.moodclip.menu.setting.favorite;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.moodclip.main.MainActivity;
import org.moodclip.main.R;
import org.moodclip.menu.setting.SettingActicity;
import org.moodclip.video.LoadingDialog;
import org.moodclip.video.YoutubePlayerActivity;
import org.moodclip.video.YoutubePlayerFullscreenActivity;
import org.moodclip.video.YoutubeApi.DeveloperKey;
import org.moodclip.video.YoutubeApi.YouTubeFailureRecoveryActivity;
import org.moodclip.video.YoutubePlayerActivity.VideoSourceTask;
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
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphObject;
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
 */
public class YoutubeAlonePlayerActivity extends YouTubeFailureRecoveryActivity
		implements YouTubePlayer.OnFullscreenListener {

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
	public String kind;
	public String videosrc;
	public String emotion;
	public String owner;
	public String title;
	public String description = "";
	public String serial_num;
	public String maleGoodCnt;
	public String femaleGoodCnt;
	public Bitmap bm;
	public Bitmap pageBm;

	public static String Emotion;
	public static String Title;
	public static String Description;
	public static String Kind;
	public static String Owner;
	public static String Serial_num;
	public static String MaleGoodCnt;
	public static String FemaleGoodCnt;
	public static Double Duration;
	public static Bitmap Thumbnail_url;

	public List<String> serial_list;
	public String check = "yes";
	public int Progress = 0;
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
	public Boolean flag;
	public boolean loginType;
	public boolean portraitFlag = true;

	FrameLayout lay;
	String user_id;
	int order = 1;
	ImageView loading;
	AnimationDrawable frame;
	
	int backcnt = 0;  // 취소버튼 카운트

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video_player);
		FlurryAgent.logEvent("YoutubeAlonePlayter");
		position_list = new ArrayList<Integer>();
		position_list.add(CurrnetPosition);

		Typeface typeface = Typeface.createFromAsset(getAssets(), "RIXGOM.TTF");

		lay = (FrameLayout) findViewById(R.id.lay);
		loading = (ImageView) findViewById(R.id.loading);

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

		PlayerActivity = YoutubeAlonePlayerActivity.this;

		// Intent Get
		intent = getIntent();
		// Emotion PlayBar Set
		serial_num = intent.getStringExtra("serial_num");
		emotion = intent.getStringExtra("emotion");
		title = intent.getStringExtra("title");
		kind = intent.getStringExtra("kind");
		owner = intent.getStringExtra("owner");
		description = intent.getStringExtra("description");
		maleGoodCnt = intent.getStringExtra("maleGoodCnt");
		femaleGoodCnt = intent.getStringExtra("femaleGoodCnt");
		pageBm = (Bitmap) intent.getParcelableExtra("PageBm");
		Thumbnail_url = (Bitmap) intent.getParcelableExtra("thumbnail_url");

		user_id = intent.getStringExtra("user_id");

		Serial_num = serial_num;
		Emotion = emotion;
		Title = title;
		Kind = kind;
		// Owner = owner;
		Description = description;
		MaleGoodCnt = maleGoodCnt;
		FemaleGoodCnt = femaleGoodCnt;
		Duration = intent.getDoubleExtra("duration", 0);

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

				intent = new Intent(YoutubeAlonePlayerActivity.this,
						SettingActicity.class);
				intent.putExtra("flag", "true");
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);

			}
		});

		// Gallery Click Event
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
				videosrc = videoData.src;
				kind = videoData.kind;
				owner = videoData.owner;

				text.setText(title);
				pageTitle.setText(owner);
				pageImg.setImageBitmap(pageBm);

				if (kind.equals("youtube")) {
					youTubeView.setVisibility(View.VISIBLE);
					videoView.setVisibility(View.INVISIBLE);
					Player.cueVideo(serial_num);
				} else {
					youTubeView.setVisibility(View.INVISIBLE);
					videoView.setVisibility(View.VISIBLE);
					playvideo(videosrc);
				}

				check = "yes";
				new FeedbackTask().execute();

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
							order++;
							new AddAsyncVideoJSONList().execute();
							LoadingDialog
									.showLoading(YoutubeAlonePlayerActivity.this);
						}
					}
				}
				return false;
			}
		});
		btnGood.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				check = "no";
				new FeedbackTask().execute();

				if (flag)
					LikeBtnSetLiked(emotion);
				else
					LikeBtnSetLike(emotion);
			}
		});

		comments.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FlurryAgent.logEvent("comments");
				// TODO Auto-generated method stub
				intent = new Intent(getApplicationContext(),
						CommentsActivity.class);
				intent.putExtra("serial_num", serial_num);
				intent.putExtra("emotion", emotion);
				intent.putExtra("title", title);
				intent.putExtra("description", description);
				intent.putExtra("maleGoodCnt", maleGoodCnt);
				intent.putExtra("femaleGoodCnt", femaleGoodCnt);
				// intent.putExtra("bm", bm);
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			}
		});
		share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(Intent.ACTION_SEND);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				// intent.putExtra(Intent.EXTRA_SUBJECT, title);
				if (kind.equals("youtube")) {
					FlurryAgent.logEvent("youtube");
					intent.putExtra(Intent.EXTRA_TEXT,
							"http://www.youtube.com/watch?v=" + serial_num);
				} else {
					FlurryAgent.logEvent("facebook");
					intent.putExtra(Intent.EXTRA_TEXT,
							"https://www.facebook.com/photo.php?v="
									+ serial_num);
				}
				intent.setType("text/plain");
				startActivity(Intent.createChooser(intent, "공유"));
			}
		});
		videoView.setOnPreparedListener(new OnPreparedListener() {
			public void onPrepared(MediaPlayer mp) {
				loading.setBackgroundDrawable(null);
				frame.stop();
				videoView.setVisibility(View.VISIBLE);
			}
		});

	}

	// !!!!!!!!!!!!!----------onCreate Method End-----------!!!!!!!!!!!!!!!

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			Player = player;
			// Player.cueVideos(serial_list);
			// Player.cueVideo(serial_num);
			// Player.loadVideos(serial_list);
			// Player.loadVideo(serial_num);
		}
	}

	@Override
	protected Provider getYouTubePlayerProvider() {
		// TODO Auto-generated method stub
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}

	public void startVideo() {
		FlurryAgent.logEvent("startvideo");
		youTubeView.initialize(DeveloperKey.DEVELOPER_KEY, this);
	}

	public class AsyncVideoJSONList extends
			AsyncTask<String, Integer, ArrayList<GalleryVideoInfoObject>> {
		ProgressDialog Dialog;

		protected void onPreExecute() {
			Dialog = ProgressDialog.show(YoutubeAlonePlayerActivity.this, "",
					"잠시만 기다려주세요..");
			Dialog.setCancelable(true);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		@Override
		protected ArrayList<GalleryVideoInfoObject> doInBackground(
				String... params) {
			Data = GalleryHttpHandler.FavoritPlayerGet(emotion, user_id,
					serial_num);
			GalleryParsingHandler parser = new GalleryParsingHandler();
			return parser.getJSONVideoRequestAllList(Data, false, true, 1);
		}

		@Override
		protected void onPostExecute(ArrayList<GalleryVideoInfoObject> result) {

			text.setText(title);
			pageTitle.setText(owner);
			pageImg.setImageBitmap(pageBm);
			new FeedbackTask().execute();
			startVideo();

			if (result != null) {
				mData = result;
				Adapter = new VideoThumbnailAdapter(
						YoutubeAlonePlayerActivity.this, mData);
				gallery.setAdapter(Adapter);
				Adapter.notifyDataSetChanged();
			}
			if (kind.equals("youtube")) {
				youTubeView.setVisibility(View.VISIBLE);
				videoView.setVisibility(View.INVISIBLE);
				Player.cueVideo(serial_num);
			} else {
				youTubeView.setVisibility(View.INVISIBLE);
				videoView.setVisibility(View.VISIBLE);
				// playvideo(videosrc);
			}
			Dialog.dismiss();
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			new AddAsyncVideoJSONList().execute();
		}
	}

	private class FeedbackTask extends AsyncTask<Void, Void, String> {

		public boolean running = true;

		protected void onCancelled() {
			running = false;
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

			Log.d("bmw", "" + num);

			return FeedbackHttpHandler.getData(num, serial_num, emotion, check);

		}

		protected void onPreExecute() {
			LikeBtnSetLike(emotion);
		}

		@Override
		protected void onPostExecute(String loginResult) {
			Log.d("httptest", "" + loginResult);
			if (loginResult.equals("fail")) {

				Toast.makeText(YoutubeAlonePlayerActivity.this, "오류 ",
						Toast.LENGTH_SHORT).show();

			} else if (loginResult.equals("like")) {

				LikeBtnSetLiked(emotion);
				flag = false;

				/*
				 * int cnt = Integer.parseInt(goodCnt); cnt++; goodCnt =
				 * Integer.toString(cnt); like.setText(" " + goodCnt);
				 */

			} else if (loginResult.equals("liked")) {
				LikeBtnSetLike(emotion);
				flag = true;

				/*
				 * int cnt = Integer.parseInt(goodCnt); cnt--; goodCnt =
				 * Integer.toString(cnt); like.setText(" " + goodCnt);
				 */

			} else if (loginResult.equals("exist")) {
				LikeBtnSetLiked(emotion);
				flag = false;

			} else if (loginResult.equals("non")) {
				LikeBtnSetLike(emotion);
				flag = true;
			}
			running = false;
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
			Data = GalleryHttpHandler
					.getData2(user_id, Integer.toString(order));
			GalleryParsingHandler parser = new GalleryParsingHandler();
			mData.addAll(parser.getJSONVideoRequestAllList(Data, false, false,
					0));
			return mData;
		}

		@Override
		protected void onPostExecute(ArrayList<GalleryVideoInfoObject> result) {

			if (result != null) {
				Adapter.notifyDataSetChanged();
			}
			LoadingDialog.hideLoading();
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
				videosrc = videoData.src;
				title = videoData.title;
				maleGoodCnt = videoData.goodCntMale;
				femaleGoodCnt = videoData.goodCntFemale;
				bm = videoData.bm;
				pageBm = videoData.pageBm;

				signal = "true";
				text.setText(title);
				pageTitle.setText(owner);
				pageImg.setImageBitmap(pageBm);

				if (kind.equals("youtube")) {
					youTubeView.setVisibility(View.VISIBLE);
					videoView.setVisibility(View.INVISIBLE);
					Player.cueVideo(serial_num);
				} else {
					youTubeView.setVisibility(View.INVISIBLE);
					videoView.setVisibility(View.VISIBLE);
					playvideo(videosrc);
				}

				check = "yes";
				new FeedbackTask().execute();

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
					if (kind.equals("youtube")) {
						intent = new Intent(YoutubeAlonePlayerActivity.this,
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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

			portraitFlag = true;
			if (kind.equals("youtube")) {
				intent = new Intent(YoutubeAlonePlayerActivity.this,
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

			if (kind.equals("youtube")) {
				intent = new Intent(YoutubeAlonePlayerActivity.this,
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				if (kind.equals("youtube")) {
					Player.loadVideo(serial_num,
							data.getIntExtra("CurrentTime", 0));
				} else {

				}
			}
		}
	}

	public void PlayBarButtonSet(String e) {

		if (e.equals("amazing")) {
			EmotionBar.setBackgroundResource(R.drawable.emotion_bar_amazing);
			comments.setImageResource(R.drawable.comments_btn_amazing);
			share.setImageResource(R.drawable.share_btn_amazing);

		} else if (e.equals("exciting")) {
			EmotionBar.setBackgroundResource(R.drawable.emotion_bar_exciting);
			comments.setImageResource(R.drawable.comments_btn_exciting);
			share.setImageResource(R.drawable.share_btn_exciting);

		} else if (e.equals("scare")) {
			EmotionBar.setBackgroundResource(R.drawable.emotion_bar_scare);
			comments.setImageResource(R.drawable.comments_btn_scare);
			share.setImageResource(R.drawable.share_btn_scare);

		} else if (e.equals("funny")) {
			EmotionBar.setBackgroundResource(R.drawable.emotion_bar_funny);
			comments.setImageResource(R.drawable.comments_btn_funny);
			share.setImageResource(R.drawable.share_btn_funny);

		} else if (e.equals("impressive")) {
			EmotionBar.setBackgroundResource(R.drawable.emotion_bar_impressive);
			comments.setImageResource(R.drawable.comments_btn_impressive);
			share.setImageResource(R.drawable.share_btn_impressive);

		} else if (e.equals("crazy")) {
			EmotionBar.setBackgroundResource(R.drawable.emotion_bar_crazy);
			comments.setImageResource(R.drawable.comments_btn_crazy);
			share.setImageResource(R.drawable.share_btn_crazy);

		} else if (e.equals("cute")) {
			EmotionBar.setBackgroundResource(R.drawable.emotion_bar_cute);
			comments.setImageResource(R.drawable.comments_btn_cute);
			share.setImageResource(R.drawable.share_btn_cute);

		} else if (e.equals("sexy")) {
			EmotionBar.setBackgroundResource(R.drawable.emotion_bar_sexy);
			comments.setImageResource(R.drawable.comments_btn_sexy);
			share.setImageResource(R.drawable.share_btn_sexy);

		}
	}

	public void LikeBtnSetLiked(String e) {

		if (e.equals("amazing")) {
			btnGood.setImageResource(R.drawable.liked_btn_amazing);

		} else if (e.equals("exciting")) {
			btnGood.setImageResource(R.drawable.liked_btn_exciting);

		} else if (e.equals("scare")) {
			btnGood.setImageResource(R.drawable.liked_btn_scare);

		} else if (e.equals("funny")) {
			btnGood.setImageResource(R.drawable.liked_btn_funny);

		} else if (e.equals("impressive")) {
			btnGood.setImageResource(R.drawable.liked_btn_impressive);

		} else if (e.equals("crazy")) {
			btnGood.setImageResource(R.drawable.liked_btn_crazy);

		} else if (e.equals("cute")) {
			btnGood.setImageResource(R.drawable.liked_btn_cute);

		} else if (e.equals("sexy")) {
			btnGood.setImageResource(R.drawable.liked_btn_sexy);

		}
	}

	public void LikeBtnSetLike(String e) {

		if (e.equals("amazing")) {
			btnGood.setImageResource(R.drawable.like_btn_amazing);

		} else if (e.equals("exciting")) {
			btnGood.setImageResource(R.drawable.like_btn_exciting);

		} else if (e.equals("scare")) {
			btnGood.setImageResource(R.drawable.like_btn_scare);

		} else if (e.equals("funny")) {
			btnGood.setImageResource(R.drawable.like_btn_funny);

		} else if (e.equals("impressive")) {
			btnGood.setImageResource(R.drawable.like_btn_impressive);

		} else if (e.equals("crazy")) {
			btnGood.setImageResource(R.drawable.like_btn_crazy);

		} else if (e.equals("cute")) {
			btnGood.setImageResource(R.drawable.like_btn_cute);

		} else if (e.equals("sexy")) {
			btnGood.setImageResource(R.drawable.like_btn_sexy);

		}
	}

	void playvideo(String url) {

		loading.setBackgroundResource(R.anim.loading);

		frame = (AnimationDrawable) loading.getBackground();
		frame.start();

		Animation rotation = AnimationUtils
				.loadAnimation(this, R.anim.rotation);
		loading.startAnimation(rotation);
		// videoView.setVisibility(View.INVISIBLE);
		// showLoading(YoutubePlayerActivity.this);
		// progDial.setVisibility(ProgressBar.VISIBLE);
		// progDial.setIndeterminate(true);
		// progDial.setMax(100);
		// getWindow().setFormat(PixelFormat.TRANSLUCENT);
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
		// mc.setY(100);
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
				jData = new JSONObject(GalleryHttpHandler.getVideoSrc(
						serial_num).toString());
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
			} else {
				Toast.makeText(getApplicationContext(), "영상을 불러오지 못했습니다",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onFullscreen(boolean arg0) {
		// TODO Auto-generated method stub

	}
}