/*
 *   Http ?????????????????????????????????��????????????????????????????????????????????????? ?????????????????????????????????????????????????????????????��
 */
package org.moodclip.video.gallery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class GalleryHttpHandler {
	private static final String DEBUG_TAG = "HttpAPIHelperHandler";
	
	public static StringBuilder jsonBuf = null;

	public static StringBuilder getData(String emotion,String user_id) {

		//HttpClient httpClient = null;

		BufferedReader jsonStreamData = null;

		HttpGet httpGet = null;
		ArrayList<GalleryVideoInfoObject> videoList = null;

		try {
			// ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????��?????????????????????? ????????????????????????????????????????????????????????�?			//httpClient = HttpConnectionManager.getGenerateHttpClient();
			HttpClient httpClient = new DefaultHttpClient();
			//
			/*
			ArrayList<BasicNameValuePair> queryString = new ArrayList<BasicNameValuePair>();
			queryString.add(new BasicNameValuePair("emotion", value));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(queryString,
					"UTF-8");
			*/
			//
			httpGet = new HttpGet("http://211.110.1.168:8001/Emotion1/"+emotion+"/"+user_id);
			//
			//httpPost.setEntity(entity);
			// 
			httpClient.getConnectionManager().closeIdleConnections(5000,
					TimeUnit.MILLISECONDS);
			// ?????????????????????????????????????????????????????? ??????????????????????????????????????????????????????????? ?????????????????????????????????????????????????? ??????????????????.
			HttpResponse httpResponse = httpClient.execute(httpGet);
			// ??????????????????????????? ?????????????????????????????????????????????
			HttpEntity responseBody = null;

			// ?????????????????????????????????????????????????????????????????????????????????????? ?????????????????????????????????????????????
			responseBody = httpResponse.getEntity();

			jsonStreamData = new BufferedReader(new InputStreamReader(
					responseBody.getContent(), "UTF-8"));
			String jsonLine = null;
			jsonBuf = new StringBuilder();
			for (jsonLine = null; (jsonLine = jsonStreamData.readLine()) != null;) {
				jsonBuf.append(jsonLine);
			}
			//videoList = GalleryParsingHandler.getJSONVideoRequestAllList(jsonBuf);

		} catch (Exception e) {
			Log.e(DEBUG_TAG,
					"insertBloodInfoRequestToServer(BloodEntityObject) --??????????????????--  ",
					e);
		} finally {
			if (jsonStreamData != null) {
				try {
					jsonStreamData.close();
				} catch (IOException ioe) {
				}
			}
			if (httpGet != null) {
				httpGet.abort();
			}
			/*
			if (httpClient != null) {
				// ?????????????????? ?????????????????????????????????????????????
				HttpConnectionManager.releaseConnection(httpClient);
			}
			*/
		}
		// ????????????????????????????????????????????????????????��???????????????????????????
		return jsonBuf;
	}
	
	public static StringBuilder getData2(String user_id, String order) {


		BufferedReader jsonStreamData = null;

		HttpGet httpGet = null;
		ArrayList<GalleryVideoInfoObject> videoList = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			httpGet = new HttpGet("http://211.110.1.168:8001/Emotion2/"+user_id+"/"+order);
		
			httpClient.getConnectionManager().closeIdleConnections(5000,
					TimeUnit.MILLISECONDS);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity responseBody = null;

			responseBody = httpResponse.getEntity();

			jsonStreamData = new BufferedReader(new InputStreamReader(
					responseBody.getContent(), "UTF-8"));
			String jsonLine = null;
			jsonBuf = new StringBuilder();
			for (jsonLine = null; (jsonLine = jsonStreamData.readLine()) != null;) {
				jsonBuf.append(jsonLine);
			}

		} catch (Exception e) {
			Log.e(DEBUG_TAG,
					"insertBloodInfoRequestToServer(BloodEntityObject) --??????????????????--  ",
					e);
		} finally {
			if (jsonStreamData != null) {
				try {
					jsonStreamData.close();
				} catch (IOException ioe) {
				}
			}
			if (httpGet != null) {
				httpGet.abort();
			}
		}
		return jsonBuf;
	}
	public static StringBuilder getVideoSrc(String video_id) {


		BufferedReader jsonStreamData = null;

		HttpGet httpGet = null;
		ArrayList<GalleryVideoInfoObject> videoList = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			httpGet = new HttpGet("http://211.110.1.168:8001/VideoSrc/"+video_id);
		
			httpClient.getConnectionManager().closeIdleConnections(5000,
					TimeUnit.MILLISECONDS);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity responseBody = null;

			responseBody = httpResponse.getEntity();

			jsonStreamData = new BufferedReader(new InputStreamReader(
					responseBody.getContent(), "UTF-8"));
			String jsonLine = null;
			jsonBuf = new StringBuilder();
			for (jsonLine = null; (jsonLine = jsonStreamData.readLine()) != null;) {
				jsonBuf.append(jsonLine);
			}

		} catch (Exception e) {
			Log.e(DEBUG_TAG,
					"insertBloodInfoRequestToServer(BloodEntityObject) --??????????????????--  ",
					e);
		} finally {
			if (jsonStreamData != null) {
				try {
					jsonStreamData.close();
				} catch (IOException ioe) {
				}
			}
			if (httpGet != null) {
				httpGet.abort();
			}
		}
		return jsonBuf;
	}
	
	public static StringBuilder FavoritPlayerGet(String emotion,String user_id,String serial_num) {


		BufferedReader jsonStreamData = null;

		HttpGet httpGet = null;
		ArrayList<GalleryVideoInfoObject> videoList = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			httpGet = new HttpGet("http://211.110.1.168:8001/FavoritEmotion/"+emotion+"/"+user_id+"/"+serial_num);
		
			httpClient.getConnectionManager().closeIdleConnections(5000,
					TimeUnit.MILLISECONDS);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity responseBody = null;

			responseBody = httpResponse.getEntity();

			jsonStreamData = new BufferedReader(new InputStreamReader(
					responseBody.getContent(), "UTF-8"));
			String jsonLine = null;
			jsonBuf = new StringBuilder();
			for (jsonLine = null; (jsonLine = jsonStreamData.readLine()) != null;) {
				jsonBuf.append(jsonLine);
			}

		} catch (Exception e) {
			Log.e(DEBUG_TAG,
					"insertBloodInfoRequestToServer(BloodEntityObject) --??????????????????--  ",
					e);
		} finally {
			if (jsonStreamData != null) {
				try {
					jsonStreamData.close();
				} catch (IOException ioe) {
				}
			}
			if (httpGet != null) {
				httpGet.abort();
			}
		}
		return jsonBuf;
	}
	
}