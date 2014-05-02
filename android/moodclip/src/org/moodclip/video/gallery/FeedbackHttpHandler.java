package org.moodclip.video.gallery;

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

import android.util.Log;

public class FeedbackHttpHandler {
	
	public static String getData(String userEmail, String serial_num, String emotion, String check){
		String str ="";
		String loginResult = "";
		
		try { 
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://211.110.1.168:8002/Feedback");

			List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
			param.add(new BasicNameValuePair("user_email", userEmail));
			param.add(new BasicNameValuePair("video_serial_num", serial_num));
			param.add(new BasicNameValuePair("video_emotion", emotion));
			param.add(new BasicNameValuePair("check", check));
			
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
				
				//result�� �������� ���� ������
				loginResult = jsonobject.getString("result");
				Log.d("http test","loginResult =" +loginResult );
			}

		} catch (Exception e) {
			android.util.Log.e("http error", "error = " + e);
			e.printStackTrace();
		}
		
		return loginResult;
		
	}

}
