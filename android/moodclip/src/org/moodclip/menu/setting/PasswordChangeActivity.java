package org.moodclip.menu.setting;

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
import org.json.JSONObject;
import org.moodclip.main.R;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordChangeActivity extends Activity {
	EditText existingPwd;
	EditText newPwd;
	EditText newPwd2;
	Button changeComplete;
	
	String existingStr = "";
	String NewPwd = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		FlurryAgent.logEvent("passwordchange");				
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_password_change);
		
		existingPwd = (EditText) findViewById(R.id.existingPwd);
		newPwd = (EditText) findViewById(R.id.newPwd);
		newPwd2 = (EditText) findViewById(R.id.newPwd2);
		changeComplete = (Button) findViewById(R.id.changeComplete);
		
		changeComplete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				existingStr = existingPwd.getText().toString();
				String pwd1 = newPwd.getText().toString();
				String pwd2 = newPwd2.getText().toString();
				if(pwd1.equals(pwd2) && pwd1.length() >6 && pwd2.length()>6){
					NewPwd = pwd1;
					new httpTask().execute();
				}else{
					Toast.makeText(PasswordChangeActivity.this, "새로운 비밀번호를 확인해 주세요 ", Toast.LENGTH_SHORT).show();
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
			try { 
				HttpClient client = new DefaultHttpClient();
				String postURL = "http://211.110.1.168:8001/PwdChange";
				HttpPost post = new HttpPost(postURL);
				
				SharedPreferences userinfo = getSharedPreferences("INFO",0);
				String userEmail = userinfo.getString("email", "");

				List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
				param.add(new BasicNameValuePair("user_email", userEmail));
				Log.w("moodclip","userEmail : "+userEmail);
				param.add(new BasicNameValuePair("user_pwd", existingStr));
				param.add(new BasicNameValuePair("user_newPwd", NewPwd));
				
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
					JSONObject jsonobject = new JSONObject(str);
					loginResult = jsonobject.getString("result");
					Log.d("http test","loginResult =" +loginResult );
				}

			} catch (Exception e) {
				android.util.Log.e("http error", "error = " + e);
				e.printStackTrace();
			}
			
			return loginResult;
		}
		protected void onPreExecute() {
			progDial = ProgressDialog.show(PasswordChangeActivity.this, "로딩중. ", "잠시만 기다려주세요.." );
		}
		
		@Override
		protected void onPostExecute(String loginResult) {
			//�������������������������������� �������������������������������������
			Log.d("httptest","" + loginResult);
			if(loginResult.equals("success")){ //�����������������������
				Log.d("http test","success");
				Toast.makeText(PasswordChangeActivity.this, "변경이 완료 되었습니다. ", Toast.LENGTH_SHORT).show();
				finish();
				
			} else if(loginResult.equals("fail")){
				Log.d("httptest","fail");
				Toast.makeText(PasswordChangeActivity.this, "기존 비밀번호가 틀렸습니다. ", Toast.LENGTH_SHORT).show();
			}
			progDial.dismiss();
		}
	 }
	@Override 
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_in,
				R.anim.push_right_out);
	}

}
