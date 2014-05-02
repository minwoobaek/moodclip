package org.moodclip.login;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

public class JoinActivity extends Activity {

	EditText mEmail, mPassword, mName;
	DatePicker mBirth;
	RadioGroup mGender;
	ImageView Joinbtn;

	String email, password, name;
	String gender = "";
	String BirthDay = "";

	String createResult = "";

	OnDateChangedListener mListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		FlurryAgent.logEvent("JoinActivity");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_join);

		final GregorianCalendar oCalendar = new GregorianCalendar();

		mEmail = (EditText) findViewById(R.id.joinId);
		mPassword = (EditText) findViewById(R.id.joinPwd1);
		mName = (EditText) findViewById(R.id.joinName);
		mGender = (RadioGroup) findViewById(R.id.Gender);

		mBirth = (DatePicker) findViewById(R.id.birthDayPicker);
		mBirth.init(oCalendar.get(Calendar.YEAR),oCalendar.get(Calendar.MONTH),oCalendar.get(Calendar.DAY_OF_MONTH),
				new OnDateChangedListener() {
					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						monthOfYear++;
						if (monthOfYear < 10)
							BirthDay = "0" + monthOfYear + "/" + dayOfMonth + "/" + year;
						else
							BirthDay = monthOfYear + "/" + dayOfMonth + "/"+ year;
						
					}
				});

		Joinbtn = (ImageView) findViewById(R.id.joinOkBtn);
		Joinbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				email = mEmail.getText().toString();
				password = mPassword.getText().toString();
				name = mName.getText().toString();

				if (mGender.getCheckedRadioButtonId() == R.id.male) {
					gender = "male";
				} else if ((mGender.getCheckedRadioButtonId() == R.id.female)) {
					gender = "female";
				}

				if (email != null && !email.equals("")) {
					if (password != null && !password.equals("")) {
						if (name != null && !name.equals("")) {
							if (gender != null && !gender.equals("")) {
								if (password.length() > 5)
									new createAccountTask().execute();
								else
									Toast.makeText(getApplicationContext(),
											"비밀번호는 6자리 이상 입력해주세요",
											Toast.LENGTH_LONG).show();

							} else {
								Toast.makeText(getApplicationContext(),
										"성별을 선택해주세요", Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							Toast.makeText(getApplicationContext(),
									"이름을 입력해주세요", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "이메일을 입력해주세요",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	private class createAccountTask extends AsyncTask<Void, Void, String> {

		private ProgressDialog progDial;
		
		protected void onPreExecute() {
			progDial = ProgressDialog.show(JoinActivity.this, "로딩중. ",
					"잠시만 기다려주세요..");
		}

		@Override
		protected String doInBackground(Void... params) {
			String str = "";
			try {
				HttpClient client = new DefaultHttpClient();
				String postURL = "http://211.110.1.168:8001/Join";
				HttpPost post = new HttpPost(postURL);

				List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
				param.add(new BasicNameValuePair("user_email", email));
				param.add(new BasicNameValuePair("user_pwd", password));
				param.add(new BasicNameValuePair("user_name", name));
				param.add(new BasicNameValuePair("user_birthday", BirthDay));
				param.add(new BasicNameValuePair("user_gender", gender));

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(param,HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePOST = client.execute(post);
				HttpEntity resEntity = responsePOST.getEntity();

				if (resEntity != null) {
					Log.d(" bmw 2 ","resEntity.toString() = " + resEntity.toString());
					InputStream inputstream = resEntity.getContent();
					BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, "utf-8"));
					String line = "";
					while ((line = bufferedreader.readLine()) != null) {
						createResult += line + "\n";
						Log.d(" bmw 3 ", "createResult = " + createResult);
					}

					JSONObject jsonobject = new JSONObject(createResult);
					str = jsonobject.getString("result");
					Log.d(" bmw 4 ", "result = " + str);
				} else {
				}

			} catch (Exception e) {
				android.util.Log.e(" bmw error", "error = " + e);
				e.printStackTrace();
			}
			return str;
		}

		@Override
		protected void onPostExecute(String result) {
			
			if (result.equals("success")) {
				Log.d("httptest", "success");
				Toast.makeText(JoinActivity.this, "가입이 완료되었습니다. ",
						Toast.LENGTH_SHORT).show();
				finish();
				FlurryAgent.logEvent("joinsuccessd");
			} else if (result.equals("fail")) {
				Log.d("httptest", "fail");
				Toast.makeText(JoinActivity.this, "가입이 실패하였습니다. ",
						Toast.LENGTH_SHORT).show();
				FlurryAgent.logEvent("joinfail");
			}
			progDial.dismiss();
		}
	}
}
