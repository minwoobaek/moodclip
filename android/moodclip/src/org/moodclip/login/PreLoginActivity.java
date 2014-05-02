/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.moodclip.login;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.json.JSONObject;
import org.moodclip.main.R;
import org.moodclip.main.TutorialActivity1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.flurry.android.FlurryAgent;

public class PreLoginActivity extends Activity {
	
	public static Activity PreLogin;

	private int m_nPreTouchPosX = 0;
	public boolean loginState = false;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();

	ImageView login;
	ImageView join;
	ImageView loginFacebook;
	LinearLayout lay;
	
	public String user_id;
	public String user_email;
	public String user_name;
	public String user_gender;
	public String user_birthday;
	
	String data;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		FlurryAgent.logEvent("preloginActivity");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_prelogin);
		
		PreLogin = PreLoginActivity.this;

		lay = (LinearLayout) findViewById(R.id.loginLayout);
		loginFacebook = (ImageView) findViewById(R.id.loginFacebook);
		login = (ImageView) findViewById(R.id.login);
		login.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in1 = new Intent(PreLoginActivity.this, LoginActivity.class);
				startActivity(in1);
			}		
		});
		join = (ImageView) findViewById(R.id.join);
		join.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in2 = new Intent(PreLoginActivity.this, JoinActivity.class);
				startActivity(in2);
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
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	private void updateView() {
		Session session = Session.getActiveSession();
		if (session.isOpened()) {
			
			makeMeRequest(session);

		} else {
			loginFacebook.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					onClickLogin();
				}
			});
		}
	}

	private void onClickLogin() {
		FlurryAgent.logEvent("onclicklogin");
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(this)
            .setPermissions(Arrays.asList("basic_info","email","user_birthday"))
            .setCallback(statusCallback));
		} else {
			Session.openActiveSession(this, true, statusCallback);
		}
	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			updateView();
		}
	}

	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		String fqlQuery = "SELECT uid,name,pic FROM user WHERE uid IN "
				+ "( SELECT uid2 FROM friend WHERE uid1 = me() )";
		Bundle params = new Bundle();
		params.putString("q", fqlQuery);
		Request request = new Request(session, "/fql", params, HttpMethod.GET,
				new Request.Callback() {
					public void onCompleted(Response response) {
						try {

							GraphObject go = response.getGraphObject();
							JSONObject jso = go.getInnerJSONObject();
							JSONArray arr = jso.getJSONArray("data");

							data = arr.toString();
							
							SharedPreferences userinfo = getSharedPreferences("INFO",0);
					    	SharedPreferences.Editor Editor = userinfo.edit();
					    	Editor.putString("friends", data);
					    	Editor.commit();
					    	
							//Log.d("bmw",data);
							
						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
				});
		Request.executeBatchAsync(request);
		
		Request req = Request.newMeRequest(session,
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						// TODO Auto-generated method stub
						// If the response is successful
						if (session == Session.getActiveSession()) {
							if (user != null) {

								// Set the id for the ProfilePictureView
								// view that in turn displays the profile
								// picture.
								// profilePictureView.setProfileId(user.getId());
								// Set the Textview's text to the user's name.
								// userNameView.setText(user.getName());

								// LoginActivity.this.user = user;
						    	
								user_id = user.getId();
								user_email = (String) user.getProperty("email");
								user_name = user.getName();
								user_gender = (String) user.getProperty("gender");
								user_birthday = user.getBirthday();
								
								//UserInfo Setting
						    	SharedPreferences userinfo = getSharedPreferences("INFO",0);
						    	SharedPreferences.Editor Editor = userinfo.edit();
						    	Editor.putString("access_token", session.getAccessToken());
						    	Editor.putString("email", user_email);
						    	Editor.putString("id", user_id);
						    	Editor.putString("name", user_name);
						    	Editor.putString("birthday", user_birthday);
						    	Editor.putString("gender", user_gender);
						    	Editor.commit();
								
								Log.d("bmw", "user_id " + user_id);
								Log.d("bmw", "user_name " + user_name);
								Log.d("bmw", "user_email " + user_email);
								//Log.d("bmw", ""+ response.getGraphObject().getProperty("email"));
								Log.d("bmw", "user_birthday " + user_birthday);
								Log.d("bmw", "user_gender " + user_gender);
								
								new createAccountTask().execute();

								//Next();
							}
						}
						if (response.getError() != null) {
							// Handle errors, will do so later.
						}
					}
				});
		req.executeAsync();
	}
	private class createAccountTask extends AsyncTask<Void, Void, String> {
		
		@Override
		protected String doInBackground(Void... params) {
			String str = "";
			String createResult = "";
			try {
				HttpClient client = new DefaultHttpClient();
				String postURL = "http://211.110.1.168:8001/FacebookJoin";
				HttpPost post = new HttpPost(postURL);

				List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
				param.add(new BasicNameValuePair("user_id", user_id));
				param.add(new BasicNameValuePair("user_email", user_email));
				param.add(new BasicNameValuePair("user_name", user_name));
				param.add(new BasicNameValuePair("user_birthday", user_birthday));
				param.add(new BasicNameValuePair("user_gender", user_gender));
				
				//Log.d(" bmw 1 ","resEntity.toString() = "+param.toString());
				
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(param, HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePOST = client.execute(post);
				HttpEntity resEntity = responsePOST.getEntity();

				if (resEntity != null) {
					//Log.d(" bmw 2 ","resEntity.toString() = "+resEntity.toString());
					InputStream inputstream = resEntity.getContent();
					BufferedReader bufferedreader = new BufferedReader(
							new InputStreamReader(inputstream, "utf-8"));
					String line = "";
					while ((line = bufferedreader.readLine()) != null) {
						createResult += line + "\n";
						//Log.d(" bmw 3 ","createResult = "+ createResult);
					}
					
					JSONObject jsonobject = new JSONObject(createResult);
					str = jsonobject.getString("result");
					//Log.d(" bmw 4 ", "result = " + str);
				} else {
				}

			} catch (Exception e) {
				android.util.Log.e(" bmw error", "error = " + e);
				e.printStackTrace();
			}
			return str;
		}
		
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("success")) {
				Log.d("httptest", "success");
				Next();
			} else if (result.equals("fail")) {
				Log.d("httptest", "fail");
			}
		}
	}

	public void Next() {
    	
		//Login Logout Type Setting
    	SharedPreferences setting = getSharedPreferences("LOGINTYPE",0);
    	SharedPreferences.Editor editor = setting.edit();
    	editor.putBoolean("loginType", false);
    	editor.commit();
    	
		Intent intent = new Intent(PreLoginActivity.this,
				TutorialActivity1.class);
		FlurryAgent.logEvent("tutorialactivity1");
		intent.putExtra("facebookNum", user_id);
		intent.putExtra("userName", user_name);
		startActivity(intent);
		finish();
	}
	@Override 
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_in,
				R.anim.push_right_out);
	}
}
