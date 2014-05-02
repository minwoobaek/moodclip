package org.moodclip.video.comments;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.moodclip.main.R;
import org.moodclip.menu.setting.favorite.YoutubeAlonePlayerActivity;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

public class CommentsActivity extends Activity {

	ArrayList<CommentsObject> Data = null;

	ListView commentsListView = null;
	EditText comments;
	ImageView CommentsBtn;

	String userName;
	String userId;

	CommentsListAdapter Adapter;
	Intent intent;
	String str;

	ImageView VideoImg;
	TextView VideoTitle;
	TextView VideoDescription;
	TextView malePercentage;
	TextView femalePercentage;

	String serial_num;
	String emotion;
	String title;
	String description;
	String maleGoodCnt;
	String femaleGoodCnt;
	Bitmap bm;
	String kind;

	ImageView TagBtn;
	TableLayout EmotionTagTableLayout;

	ImageView AmazingTag;
	ImageView FunnyTag;
	ImageView ScareTag;
	ImageView ImpressiveTag;
	ImageView SexyTag;
	ImageView CuteTag;
	ImageView CrazyTag;
	ImageView ExcitingTag;

	ImageView CountEmotion;
	TextView LikeCount;

	public Boolean TagFlag = true;
	public String emotionTag = null;
	
	public String DeleteEmotionTag = null;
	public String DeleteComments = null;
	public String DeleteCommentsNum = null;

	InputMethodManager imm;
	public Boolean listviewFlag = true;
	public int Progress = 0;

	ImageView CommentsAdd;
	
	public boolean loginType;
	
	View listviewItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		FlurryAgent.logEvent("commentsActivity");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.comments_activity);

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		Data = new ArrayList<CommentsObject>();

		SharedPreferences userinfo = getSharedPreferences("INFO", 0);
		userName = userinfo.getString("name", "");
		
		SharedPreferences setting = getSharedPreferences("LOGINTYPE",0);
		loginType = setting.getBoolean("loginType", loginType);
		if(loginType){
			userId = userinfo.getString("email", "");
			
		}else if(!loginType){
			userId = userinfo.getString("id", "");
		}

		intent = getIntent();
		serial_num = intent.getStringExtra("serial_num");
		emotion = intent.getStringExtra("emotion");
		title = intent.getStringExtra("title");
		kind = intent.getStringExtra("kind");
		description = intent.getStringExtra("description");
		maleGoodCnt = intent.getStringExtra("maleGoodCnt");
		femaleGoodCnt = intent.getStringExtra("femaleGoodCnt");
		bm = (Bitmap)intent.getParcelableExtra("bm");

		View header = getLayoutInflater().inflate(
				R.layout.comments_listview_header, null, false);
		View footer = getLayoutInflater().inflate(
				R.layout.comments_listview_footer, null, false);

		commentsListView = (ListView) findViewById(R.id.CommentslistView);
		commentsListView.addHeaderView(header);
		commentsListView.addFooterView(footer);

		// commentsListView.setDivider(null);

		VideoImg = (ImageView) header.findViewById(R.id.VideoImg);
		VideoTitle = (TextView) header.findViewById(R.id.VideoTitle);
		VideoDescription = (TextView) header.findViewById(R.id.VideoDescription);
		malePercentage = (TextView) header.findViewById(R.id.malePercentage);
		femalePercentage = (TextView) header.findViewById(R.id.femalePercentage);
		CountEmotion = (ImageView) header.findViewById(R.id.CountEmotion);
		LikeCount = (TextView) header.findViewById(R.id.LikeCount);

		CommentsAdd = (ImageView) footer.findViewById(R.id.CommentsAdd);
		comments = (EditText) footer.findViewById(R.id.comments);
		CommentsBtn = (ImageView) footer.findViewById(R.id.commentsBtn);
		TagBtn = (ImageView) footer.findViewById(R.id.Tagbtn);
		TagBtn.setBackgroundColor(Color.rgb(148, 148, 148));
		EmotionTagTableLayout = (TableLayout) footer
				.findViewById(R.id.EmotionTagTableLayout);

		AmazingTag = (ImageView) footer.findViewById(R.id.AmazingTag);
		FunnyTag = (ImageView) footer.findViewById(R.id.FunnyTag);
		ScareTag = (ImageView) footer.findViewById(R.id.ScareTag);
		ImpressiveTag = (ImageView) footer.findViewById(R.id.ImpressiveTag);
		SexyTag = (ImageView) footer.findViewById(R.id.SexyTag);
		CrazyTag = (ImageView) footer.findViewById(R.id.CrazyTag);
		CuteTag = (ImageView) footer.findViewById(R.id.CuteTag);
		ExcitingTag = (ImageView) footer.findViewById(R.id.ExcitingTag);

		new CommentsShowTask().execute();

		double x = Double.parseDouble(maleGoodCnt);
		double y = Double.parseDouble(femaleGoodCnt);

		VideoImg.setImageBitmap(bm);
		VideoTitle.setText(title);
		VideoDescription.setText(description);
		if(x==0){
			malePercentage.setText("0%");
		}else{
			malePercentage.setText((Double.toString((100 * x / (x + y))))
					.substring(0, 4) + "%");
		}
		if(y==0){
			femalePercentage.setText("0%");
		}else{
			femalePercentage.setText((Double.toString((100 * y / (x + y))))
				.substring(0, 4) + "%");
		}

		EmotionCountSet(emotion);
		int cnt = Integer.parseInt(maleGoodCnt)
				+ Integer.parseInt(femaleGoodCnt);
		LikeCount.setText(Integer.toString(cnt));
		
		commentsListView.setLongClickable(true);
		commentsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				CommentsObject obj = Data.get(arg2-1);
				
				if(userId.equals(obj.userId)){
					DeleteEmotionTag = obj.emotionTag;
					DeleteComments = obj.comments;
					DeleteCommentsNum = obj.commentsNum;
					DialogSimple();
				}
				return false;
			}
			
		});
		CommentsAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Progress++;
				new AddCommentsShowTask().execute();
			}
		});

		CommentsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imm.hideSoftInputFromWindow(comments.getWindowToken(), 0);
				TagBtn.setImageResource(R.drawable.tag_btn);
				TagBtn.setBackgroundColor(Color.rgb(148, 148, 148));
				new CommentsBtnTask().execute();
			}
		});
		TagBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (TagFlag) {
					
					TagBtn.setBackgroundColor(Color.rgb(148, 148, 148));
					TagBtn.setImageResource(R.drawable.tag_btn);
					AmazingTag.setImageResource(R.drawable.btn_moodclip_main_activitymain_amazing);
					FunnyTag.setImageResource(R.drawable.btn_moodclip_main_activitymain_funny);
					ScareTag.setImageResource(R.drawable.btn_moodclip_main_activitymain_fear);
					ImpressiveTag.setImageResource(R.drawable.btn_moodclip_main_activitymain_impressive);
					SexyTag.setImageResource(R.drawable.btn_moodclip_main_activitymain_sexy);
					CrazyTag.setImageResource(R.drawable.btn_moodclip_main_activitymain_exciting);
					CuteTag.setImageResource(R.drawable.btn_moodclip_main_activitymain_cute);
					ExcitingTag.setImageResource(R.drawable.btn_moodclip_main_activitymain_crazy);

					TagFlag = false;
					emotionTag = null;
				} else {
					TagBtn.setBackgroundColor(Color.rgb(148, 148, 148));
					Reset();
					emotionTag = null;
				}
				commentsListView.setSelectionFromTop(Data.size() + 2, 0);
			}
		});
		AmazingTag.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TagBtn.setImageResource(R.drawable.emotion_tag_amazing);
				TagBtn.setBackgroundColor(Color.WHITE);
				Reset();
				commentsListView.setSelectionFromTop(Data.size() + 2, 0);
				emotionTag = "amazing";
			}
		});
		FunnyTag.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TagBtn.setImageResource(R.drawable.emotion_tag_funny);
				TagBtn.setBackgroundColor(Color.WHITE);
				Reset();
				commentsListView.setSelectionFromTop(Data.size() + 2, 0);
				emotionTag = "funny";
			}
		});
		ScareTag.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TagBtn.setImageResource(R.drawable.emotion_tag_scare);
				TagBtn.setBackgroundColor(Color.WHITE);
				Reset();
				commentsListView.setSelectionFromTop(Data.size() + 2, 0);
				emotionTag = "scare";
			}
		});
		ImpressiveTag.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TagBtn.setImageResource(R.drawable.emotion_tag_impressive);
				TagBtn.setBackgroundColor(Color.WHITE);
				Reset();
				commentsListView.setSelectionFromTop(Data.size() + 2, 0);
				emotionTag = "impressive";
			}
		});
		SexyTag.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TagBtn.setImageResource(R.drawable.emotion_tag_sexy);
				TagBtn.setBackgroundColor(Color.WHITE);
				Reset();
				commentsListView.setSelectionFromTop(Data.size() + 2, 0);
				emotionTag = "sexy";
			}
		});
		CrazyTag.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TagBtn.setImageResource(R.drawable.emotion_tag_exciting);
				TagBtn.setBackgroundColor(Color.WHITE);
				Reset();
				commentsListView.setSelectionFromTop(Data.size() + 2, 0);
				emotionTag = "crazy";
			}
		});
		CuteTag.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TagBtn.setImageResource(R.drawable.emotion_tag_cute);
				TagBtn.setBackgroundColor(Color.WHITE);
				Reset();
				commentsListView.setSelectionFromTop(Data.size() + 2, 0);
				emotionTag = "cute";
			}
		});
		ExcitingTag.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TagBtn.setImageResource(R.drawable.emotion_tag_crazy);
				TagBtn.setBackgroundColor(Color.WHITE);
				Reset();
				commentsListView.setSelectionFromTop(Data.size() + 2, 0);
				emotionTag = "exciting";
			}
		});
	}

	public class AddCommentsShowTask extends
			AsyncTask<Void, Void, ArrayList<CommentsObject>> {
		
		private ProgressDialog progDial;

		protected void onPreExecute() {
			progDial = ProgressDialog.show(CommentsActivity.this, "",
					"잠시만 기다려주세요");
		}

		@Override
		protected ArrayList<CommentsObject> doInBackground(Void... params)  {

			Data.addAll(CommentsParsingHandler.Parsing(str, Progress));
			return Data;
		}

		@Override
		protected void onPostExecute(ArrayList<CommentsObject> result) {

			if (result != null) {
				Adapter = new CommentsListAdapter(CommentsActivity.this,
						R.layout.comments_adapter, Data);
				commentsListView.setAdapter(Adapter);
				Adapter.notifyDataSetChanged();
			}
			if(result.size() < (5*(Progress+1))){
				CommentsAdd.setVisibility(View.INVISIBLE);
			}
			commentsListView.setSelectionFromTop(Data.size() + 3, 720);
			progDial.dismiss();
		}
	}

	private class CommentsShowTask extends
			AsyncTask<Void, Void, ArrayList<CommentsObject>> {

		private ProgressDialog progDial;

		@Override
		protected ArrayList<CommentsObject> doInBackground(Void... params) {
			str = CommentsHttpHandler.getData(false, "CommentsShow", serial_num,
					comments.getText().toString(), userName, userId,
					emotionTag,DeleteCommentsNum);

			return CommentsParsingHandler.Parsing(str, Progress);
		}

		protected void onPreExecute() {
			progDial = ProgressDialog.show(CommentsActivity.this, "",
					"잠시만 기다려주세요");
		}

		@Override
		protected void onPostExecute(ArrayList<CommentsObject> result) {

			if (result.size() >= 0) {
				Data = result;
				Adapter = new CommentsListAdapter(CommentsActivity.this,
						R.layout.comments_adapter, Data);
				commentsListView.setAdapter(Adapter);
				Adapter.notifyDataSetChanged();
			}
			/*
			if(result.size() < 5){
				CommentsAdd.setVisibility(View.INVISIBLE);
			}
			*/

			try {
				// bm = Bitmap.createScaledBitmap(bm, VideoImg.getWidth(),
				// VideoImg.getHeight(),true);
			} catch (Exception e) {

			}

			if (listviewFlag) {
				listviewFlag = false;
			} else if (!listviewFlag) {
				commentsListView.setSelectionFromTop(Data.size() + 3, 720);
			}

			progDial.dismiss();
		}
	}

	private class CommentsBtnTask extends AsyncTask<Void, Void, String> {

		private ProgressDialog progDial;

		@Override
		protected String doInBackground(Void... params) {
			return CommentsHttpHandler.getData(true, "CommentsInsert", serial_num,
					comments.getText().toString(), userName, userId, emotionTag,DeleteCommentsNum);
		}

		protected void onPreExecute() {
			progDial = ProgressDialog.show(CommentsActivity.this, "", "로딩중");
		}

		@Override
		protected void onPostExecute(String str) {

			JSONObject jsonobject = null;
			String result = null;

			try {
				jsonobject = new JSONObject(str);
				result = jsonobject.getString("result");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (result.equals("success")) {
				Log.d("httptest", "success");
				comments.setText("");
				new CommentsShowTask().execute();
			} else if (result.equals("fail")) {
				Log.d("httptest", "fail");
			}
			commentsListView.setSelectionFromTop(Data.size() + 3, 0);
			progDial.dismiss();
		}
	}

	private static Bitmap GetImageFromURL(String strImageURL) {
		Bitmap bitMap = null;
		// InputStream is = null;

		try {
			URL imageURL = new URL(strImageURL);
			HttpURLConnection conn = (HttpURLConnection) imageURL
					.openConnection();
			BufferedInputStream bis = new BufferedInputStream(
					conn.getInputStream(), 10240);
			bitMap = BitmapFactory.decodeStream(bis);
			bis.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return bitMap;
	}

	public void Reset() {
		AmazingTag.setImageResource(0);
		FunnyTag.setImageResource(0);
		ScareTag.setImageResource(0);
		ImpressiveTag.setImageResource(0);
		SexyTag.setImageResource(0);
		CrazyTag.setImageResource(0);
		CuteTag.setImageResource(0);
		ExcitingTag.setImageResource(0);

		TagFlag = true;
	}

	public void EmotionCountSet(String emotion) {

		if (emotion.equals("amazing")) {
			CountEmotion.setImageResource(R.drawable.count_amazing);
		} else if (emotion.equals("crazy")) {
			CountEmotion.setImageResource(R.drawable.count_crazy);
		} else if (emotion.equals("cute")) {
			CountEmotion.setImageResource(R.drawable.count_cute);
		} else if (emotion.equals("exciting")) {
			CountEmotion.setImageResource(R.drawable.count_exciting);
		} else if (emotion.equals("impressive")) {
			CountEmotion.setImageResource(R.drawable.count_impressive);
		} else if (emotion.equals("funny")) {
			CountEmotion.setImageResource(R.drawable.count_funny);
		} else if (emotion.equals("scare")) {
			CountEmotion.setImageResource(R.drawable.count_scare);
		} else if (emotion.equals("sexy")) {
			CountEmotion.setImageResource(R.drawable.count_sexy);
		}
	}
	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.comments, menu); return true; }
	 */
	public void MatrixTime(int delayTime) {
		long saveTime = System.currentTimeMillis();
		long currTime = 0;
		while (currTime - saveTime < delayTime) {
			currTime = System.currentTimeMillis();
		}
	}
	
	private void DialogSimple(){
	    AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
	    alt_bld.setMessage("댓글을 삭제 하시겠습니까?").setCancelable(
	        false).setPositiveButton("예",
	        new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	            // Action for 'Yes' Button
	        	new CommentsDeleteTask().execute();
	        }
	        }).setNegativeButton("아니오",
	        new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	            // Action for 'NO' Button
	            dialog.cancel();
	        }
	        });
	    AlertDialog alert = alt_bld.create();
	    // Title for AlertDialog
	    //alert.setTitle("Title");
	    // Icon for AlertDialog
	    //alert.setIcon(R.drawable.icon);
	    alert.show();
	}
	
	private class CommentsDeleteTask extends AsyncTask<Void, Void, String> {

		private ProgressDialog progDial;

		@Override
		protected String doInBackground(Void... params) {
			return CommentsHttpHandler.getData(true, "CommentsDelete", serial_num,
					DeleteComments, userName, userId, DeleteEmotionTag,DeleteCommentsNum);
		}

		protected void onPreExecute() {
			progDial = ProgressDialog.show(CommentsActivity.this, "", "로딩중");
		}

		@Override
		protected void onPostExecute(String str) {

			JSONObject jsonobject = null;
			String result = null;

			try {
				jsonobject = new JSONObject(str);
				result = jsonobject.getString("result");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (result.equals("success")) {
				Log.d("httptest", "success");
				comments.setText("");
				new CommentsShowTask().execute();
			} else if (result.equals("fail")) {
				Log.d("httptest", "fail");
			}
			commentsListView.setSelectionFromTop(Data.size() + 3, 0);
			progDial.dismiss();
		}
	}

}
