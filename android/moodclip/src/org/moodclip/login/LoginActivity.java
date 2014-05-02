package org.moodclip.login;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.json.JSONObject;
import org.moodclip.main.R;
import org.moodclip.main.TutorialActivity1;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	TextView txtUserEmail, txtUserPassword;
	String userEmail, userPassword, userName;
	ImageView okBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		FlurryAgent.logEvent("LoginActivity");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_login);
		
		txtUserEmail = (TextView) findViewById(R.id.loginId);
		txtUserPassword = (TextView) findViewById(R.id.loginPwd);
		
		okBtn = (ImageView) findViewById(R.id.loginOkBtn);
		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				userEmail = txtUserEmail.getText().toString();
				userPassword = txtUserPassword.getText().toString();

				if (userEmail != null && !userEmail.equals("")
						&& userPassword != null && !userPassword.equals("")) {
					new httpTask().execute();
				}
			}
		});		
	}
	
	private class httpTask extends AsyncTask<Void, Void, String> {
		String str ="";
		
		private ProgressDialog progDial;
		
		@Override
		protected String doInBackground(Void... params) {
			String loginResult = "";
			JSONArray jsonArray = null;
			JSONObject jsonobject = null;
			
			try { 
				HttpClient client = new DefaultHttpClient();
				String postURL = "http://211.110.1.168:8001/Login";
				HttpPost post = new HttpPost(postURL);

				List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
				param.add(new BasicNameValuePair("user_email", userEmail));
				param.add(new BasicNameValuePair("user_pwd", userPassword));
				
				UrlEncodedFormEntity ent;
				ent = new UrlEncodedFormEntity(param, HTTP.UTF_8);
				post.setEntity(ent);				
				HttpResponse responsePOST = client.execute(post);
				HttpEntity resEntity = responsePOST.getEntity();

				if (resEntity != null) {
					Log.d("http test","resEntity.toString() = "+resEntity.toString());
					InputStream inputstream = resEntity.getContent();
					BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, "UTF-8"));
					String line = null;
					while((line = bufferedreader.readLine())!= null){
						str += line + "\n";
						Log.d("http test","str = "+ str);
					}
					//str = str.replaceAll("&quot;", "\"");
					
					jsonArray = new JSONArray(str);
					jsonobject = jsonArray.getJSONObject(0);
					loginResult = jsonobject.getString("name");
					
					Log.d("http test","loginResult =" +str );
				}

			} catch (Exception e) {
				android.util.Log.e("http error", "error = " + e);
				e.printStackTrace();
			}
			
			return loginResult;
		}
		protected void onPreExecute() {
			progDial = ProgressDialog.show(LoginActivity.this, "로딩중. ", "잠시만 기다려주세요.." );
		}
		
		@Override
		protected void onPostExecute(String loginResult) {
			
			if(loginResult.equals("fuckingfail")){ 
				FlurryAgent.logEvent("fuckingfail");
				//Log.d("httptest","loginResult =" +loginResult );
				Toast.makeText(LoginActivity.this, "다시 시도해 주세요. ", Toast.LENGTH_SHORT).show();
				
			} else {
				
				Log.d("httptest","loginResult =" +loginResult );
				
				//UserInfo Setting
		    	SharedPreferences userinfo = getSharedPreferences("INFO",0);
		    	SharedPreferences.Editor Editor = userinfo.edit();
		    	Editor.putString("id", userEmail);
		    	Editor.putString("name", loginResult);
		    	userName = loginResult;
		    	Editor.commit();
				
				//Login Logout Type Setting
		    	SharedPreferences setting = getSharedPreferences("LOGINTYPE",0);
		    	SharedPreferences.Editor editor = setting.edit();
		    	editor.putBoolean("loginType", true);
		    	editor.commit();
		    	
				Intent i = new Intent(LoginActivity.this, TutorialActivity1.class);
				i.putExtra("facebookNum", userEmail);
				i.putExtra("userName", userName);
				startActivity(i);
				PreLoginActivity.PreLogin.finish();
				finish();
			}
			progDial.dismiss();
		}
	 }
}

