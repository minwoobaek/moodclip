package org.moodclip.video.comments;

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

import com.flurry.android.FlurryAgent;

import android.util.Log;



public class CommentsHttpHandler {

	public static String getData(boolean flag,String url,String serial_num,String comments,String userName, String userId,String emotionTag,String DeleteCommentsNum){
		FlurryAgent.logEvent("commentshttphandeler");
		String str = "";
		
		try {
			HttpClient client = new DefaultHttpClient();
			String postURL = "http://211.110.1.168:8002/"+url;
			HttpPost post = new HttpPost(postURL);

			List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
			if(flag){
				param.add(new BasicNameValuePair("comments", comments));
				param.add(new BasicNameValuePair("user_name", userName));
				param.add(new BasicNameValuePair("user_id", userId));
				param.add(new BasicNameValuePair("emotionTag", emotionTag));
				param.add(new BasicNameValuePair("commentsNum", DeleteCommentsNum));
			}
			param.add(new BasicNameValuePair("video_serial_num", serial_num));
			
			
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(param, HTTP.UTF_8);
			post.setEntity(ent);
			HttpResponse responsePOST = client.execute(post);
			HttpEntity resEntity = responsePOST.getEntity();

			if (resEntity != null) {
				InputStream inputstream = resEntity.getContent();
				BufferedReader bufferedreader = new BufferedReader(
						new InputStreamReader(inputstream, "utf-8"));
				String line = "";
				while ((line = bufferedreader.readLine()) != null) {
					str += line + "\n";
				}
				
			} else {
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
		
	}
}
