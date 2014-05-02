package org.moodclip.menu.setting.friends;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.moodclip.main.R;
import org.moodclip.menu.setting.favorite.FavoriteActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.flurry.android.FlurryAgent;

public class FriendsActivity extends FragmentActivity {
	private static Context FriendsActivity = null;
	JSONArray arr;
	String data;
	String id;
	static FriendsMemberAdapter MemberAdapter;
	static FriendsInviteAdapter InviteAdapter;
	static ListView friendslistView;
	static ListView FriendsInviteListview;
	ImageView friendsMember;
	ImageView friendsInvite;
	ImageView rumor;

	ArrayList<FriendsObject> nonMemberData;
	static ArrayList<FriendsObject> memberData;
	static ArrayList<FriendsObject> Data;
	ArrayList<Integer> num;

	Intent intent;

	private ProgressDialog progDial;

	LinearLayout friendsMemberLayout;
	LinearLayout friendsInviteLayout;
	LinearLayout friendsRumorLayout;

	private Session.StatusCallback statusCallback = new SessionStatusCallback();

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			// TODO Auto-generated method stub
			updateView();
		}
	}
	private void updateView() {
		Session session = Session.getActiveSession();
		if (session.isOpened()) {

			friendsRumorLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					publishFeedDialog();

					friendsMemberLayout
							.setBackgroundResource(R.drawable.friendslist_background);
					friendsInviteLayout
							.setBackgroundResource(R.drawable.friendslist_background);
					friendsRumorLayout
							.setBackgroundResource(R.drawable.friendslist_backgrounded);
				}
			});

		} else {
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
		/*
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
		*/
		if (resultCode == RESULT_OK) {
			MemberAdapter.notifyDataSetChanged();
			InviteAdapter.notifyDataSetChanged();
			Toast.makeText(getApplicationContext(), "占쏙옙호 ", Toast.LENGTH_LONG).show();
		}
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		FlurryAgent.logEvent("friendsactivity");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friends_activity);

		friendsMemberLayout = (LinearLayout) findViewById(R.id.friendsMemberLayout);
		friendsInviteLayout = (LinearLayout) findViewById(R.id.friendsInviteLayout);
		friendsRumorLayout = (LinearLayout) findViewById(R.id.friendsRumorLayout);
		friendsMemberLayout
				.setBackgroundResource(R.drawable.friendslist_backgrounded);
		friendsInviteLayout
				.setBackgroundResource(R.drawable.friendslist_background);
		friendsRumorLayout
				.setBackgroundResource(R.drawable.friendslist_background);

		friendslistView = (ListView) findViewById(R.id.friendslistView);
		FriendsInviteListview = (ListView) findViewById(R.id.FriendsInviteListview);
		friendslistView.setVisibility(View.VISIBLE);
		FriendsInviteListview.setVisibility(View.INVISIBLE);

		friendsMember = (ImageView) findViewById(R.id.friendsMember);
		friendsInvite = (ImageView) findViewById(R.id.friendsInvite);
		rumor = (ImageView) findViewById(R.id.rumor);

		num = new ArrayList<Integer>();

		friendsMemberLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				friendslistView.setVisibility(View.VISIBLE);
				FriendsInviteListview.setVisibility(View.INVISIBLE);

				friendsMemberLayout
						.setBackgroundResource(R.drawable.friendslist_backgrounded);
				friendsInviteLayout
						.setBackgroundResource(R.drawable.friendslist_background);
				friendsRumorLayout
						.setBackgroundResource(R.drawable.friendslist_background);
			}
		});
		friendsInviteLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				friendslistView.setVisibility(View.INVISIBLE);
				FriendsInviteListview.setVisibility(View.VISIBLE);

				friendsMemberLayout
						.setBackgroundResource(R.drawable.friendslist_background);
				friendsInviteLayout
						.setBackgroundResource(R.drawable.friendslist_backgrounded);
				friendsRumorLayout
						.setBackgroundResource(R.drawable.friendslist_background);
			}
		});
		friendslistView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FriendsObject obj = memberData.get(position);
				Intent intent = new Intent(FriendsActivity.this,
						FavoriteActivity.class);
				intent.putExtra("uid", obj.uid);
				intent.putExtra("name", obj.name);
				intent.putExtra("email", obj.email);
				intent.putExtra("flag", "false");
				startActivity(intent);

			}
		});

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

		progDial = ProgressDialog
				.show(FriendsActivity.this, "", "잠시만 기다려주세요..");
		progDial.setCancelable(true);

		SharedPreferences userinfo = getSharedPreferences("INFO", 0);
		data = userinfo.getString("friends", "");

		new FriendsListTask().execute();

		FriendsActivity = this;
	}

	class FriendsListTask extends
			AsyncTask<Void, Void, ArrayList<FriendsObject>> {
		String createResult = "";

		@Override
		public ArrayList<FriendsObject> doInBackground(Void... params) {

			try {
				HttpClient client = new DefaultHttpClient();
				String postURL = "http://211.110.1.168:8002/Friends";
				HttpPost post = new HttpPost(postURL);

				List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
				param.add(new BasicNameValuePair("data", data));

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(param,
						HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePOST = client.execute(post);
				HttpEntity resEntity = responsePOST.getEntity();

				if (resEntity != null) {
					InputStream inputstream = resEntity.getContent();
					BufferedReader bufferedreader = new BufferedReader(
							new InputStreamReader(inputstream, "utf-8"));
					String line = "";
					while ((line = bufferedreader.readLine()) != null) {
						createResult += line + "\n";
					}
					Log.i("bmw", "" + createResult);
				} else {
				}

			} catch (Exception e) {
				android.util.Log.e("bmw", "error = " + e);
				e.printStackTrace();
			}

			ArrayList<FriendsObject> result = null;
			JSONArray jsonArray1 = null;
			JSONObject obj1 = null;
			JSONObject obj2 = null;
			JSONObject obj3 = null;

			try {

				Data = new ArrayList<FriendsObject>();
				result = new ArrayList<FriendsObject>();
				jsonArray1 = new JSONArray(createResult);
				/*
				 * job1 = jsonArray1.getJSONObject(0); job2 =
				 * jsonArray1.getJSONObject(1); jsonArray1 =
				 * job1.getJSONArray("data1"); jsonArray2 =
				 * job2.getJSONArray("data2");
				 */
				FriendsObject entity;

				for (int i = 0; i < jsonArray1.length(); i++) {

					entity = new FriendsObject();
					obj1 = jsonArray1.getJSONObject(i);

					entity.uid = obj1.getString("facebook_num");
					entity.name = obj1.getString("name");
					entity.email = obj1.getString("email");
					Log.d("data1","name : "+obj1.getString("name"));
					result.add(entity);
				}
				/*
				 * for (int i = 0; i < jsonArray2.length(); i++) {
				 * 
				 * entity = new FriendsObject(); obj =
				 * jsonArray2.getJSONObject(i);
				 * 
				 * entity.uid = obj.getString("uid"); entity.name =
				 * obj.getString("name"); //entity.bm =
				 * GetImageFromURL(obj.getString("pic")); Data.add(entity); }
				 */
				int cnt = 1;
				int CntCheck = 0;
				arr = new JSONArray(data);
				for (int l = 0; l < arr.length(); l++) {

					obj3 = arr.getJSONObject(l);
					entity = new FriendsObject();
					CntCheck = 0;

					for (int m = 0; m < jsonArray1.length(); m++) {
						obj2 = jsonArray1.getJSONObject(m);

						if (!obj3.getString("uid").equals(obj2.getString("facebook_num"))) {
							CntCheck++;
							if (CntCheck == jsonArray1.length()) {
								entity.uid = obj3.getString("uid");
								entity.name = obj3.getString("name");
								// entity2.bm =
								// GetImageFromURL(obj3.getString("pic"));
								Log.d("data2","name "+cnt+": "+obj3.getString("name"));
								cnt++;
								Data.add(entity);
							}
						}
					}

				}

			} catch (JSONException je) {
				je.printStackTrace();
			}

			return result;
		}

		public void onPreExecute() {

		}

		@Override
		public void onPostExecute(ArrayList<FriendsObject> result) {

			memberData = result;

			MemberAdapter = new FriendsMemberAdapter(FriendsActivity.this,memberData);
			friendslistView.setAdapter(MemberAdapter);
			MemberAdapter.notifyDataSetChanged();

			InviteAdapter = new FriendsInviteAdapter(FriendsActivity.this, Data);
			FriendsInviteListview.setAdapter(InviteAdapter);
			InviteAdapter.notifyDataSetChanged();

			progDial.dismiss();
		}
	}

	public static Bitmap GetImageFromURL(String strImageURL) {
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

	private void publishFeedDialog() {
		Bundle params = new Bundle();

		params.putString("name", "무드클립 (Moodclip)");
		params.putString("caption", "당신의 기분과 느낌을 up!");
		params.putString(
				"description",
				"당신이 느끼고 싶은 다양한 감정의 영상을 재미나게 보자! 혼자 있을 때!!심심 따분할 때!!감성이 마구 돋는 시간!!무드클립으로 기분 up up~! :) " );
		params.putString("link", "https://www.facebook.com/moodclip");
		params.putString(
				"picture",
				"http://static.wixstatic.com/media/189923_c23265eeb636eb34165bb90f9f25eb0e.png_srz_125_145_75_22_0.50_1.20_0.00_png_srz");

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(
				FriendsActivity.this, Session.getActiveSession(), params))
				.setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							// When the story is posted, echo the success
							// and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {
								MemberAdapter.notifyDataSetChanged();
								InviteAdapter.notifyDataSetChanged();
								Toast.makeText(FriendsActivity.this,
										"성공했습니다." ,
										Toast.LENGTH_SHORT).show();
							} else {
								MemberAdapter.notifyDataSetChanged();
								InviteAdapter.notifyDataSetChanged();
								// User clicked the Cancel button
								Toast.makeText(FriendsActivity.this,
										"실패했습니다..", Toast.LENGTH_SHORT).show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							MemberAdapter.notifyDataSetChanged();
							InviteAdapter.notifyDataSetChanged();
							// User clicked the "x" button
							Toast.makeText(FriendsActivity.this, "취소되었습니다.",
									Toast.LENGTH_SHORT).show();
						} else {
							MemberAdapter.notifyDataSetChanged();
							InviteAdapter.notifyDataSetChanged();
							// Generic, ex: network error
							Toast.makeText(FriendsActivity.this,
									"Error posting story", Toast.LENGTH_SHORT)
									.show();
						}
					}

				}).build();
		feedDialog.show();
	}

	public static void sendRequestDialog(String UserId) {

		Bundle params = new Bundle();
		params.putString("message", "Moodclip(무드클립) www.facebook.com/moodclip");
		// params.putString("data","{\"badge_of_awesomeness\":\"1\","
		// +"\"social_karma\":\"5\"}");
		params.putString("to", UserId);
		// params.putString("user_id","100006059799230");
		WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(
				FriendsActivity, Session.getActiveSession(), params))
				.setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error != null) {
							if (error instanceof FacebookOperationCanceledException) {
								MemberAdapter.notifyDataSetChanged();
								InviteAdapter.notifyDataSetChanged();
								
								Toast.makeText(FriendsActivity,"Request cancelled", Toast.LENGTH_SHORT).show();
							} else {
								MemberAdapter.notifyDataSetChanged();
								InviteAdapter.notifyDataSetChanged();
								
								Toast.makeText(FriendsActivity,"Network Error", Toast.LENGTH_SHORT).show();
							}
						} else {
							final String requestId = values
									.getString("request");
							if (requestId != null) {
								MemberAdapter.notifyDataSetChanged();
								InviteAdapter.notifyDataSetChanged();
								
								Toast.makeText(FriendsActivity, "Request sent",Toast.LENGTH_SHORT).show();
							} else {
								MemberAdapter.notifyDataSetChanged();
								InviteAdapter.notifyDataSetChanged();
								
								Toast.makeText(FriendsActivity,"Request cancelled", Toast.LENGTH_SHORT).show();
							}
						}
					}

				}).build();
		requestsDialog.show();

	}
	
	/*
	 * public static void Next(String uid,String name, String email){
	 * 
	 * intent = new Intent(FriendsActivity.this, FavoriteActivity.class);
	 * intent.putExtra("uid", uid); intent.putExtra("name", name);
	 * intent.putExtra("email", email); startActivity(intent);
	 * 
	 * }
	 */

}
