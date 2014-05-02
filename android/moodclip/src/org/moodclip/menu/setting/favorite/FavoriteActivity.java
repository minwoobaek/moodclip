package org.moodclip.menu.setting.favorite;

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
import org.moodclip.menu.setting.friends.FriendsActivity;
import org.moodclip.video.gallery.GalleryVideoInfoObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.widget.ProfilePictureView;
import com.flurry.android.FlurryAgent;

public class FavoriteActivity extends Activity {

	ImageView friendsBtn;
	ImageView FavoriteEmotion;

	GridView FavoriteGridview;
	ArrayList<GalleryVideoInfoObject> mData = new ArrayList<GalleryVideoInfoObject>();
	FavoriteListAdapter Adapter;

	public String emotion;
	public String title;
	public String description;
	public String serial_num;
	public String maleGoodCnt;
	public String femaleGoodCnt;
	public String duration = "";
	public String kind;
	public String owner;
	public String videosrc;
	public Bitmap PageBm;
	public Bitmap Bm;
	
	String recentlyEmotion;

	//Fragment fragments = new Fragment();
	Intent intent;
	int figures[] = { 0, 0, 0, 0, 0, 0, 0, 0 };
	
	public boolean loginType;
	
	ProfilePictureView Profile_picture;
	TextView Profile_name;
	
	String user_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.favorite_activity);
		FlurryAgent.logEvent("FavoriteActivity");
		/*
		ActionBar ab = getActionBar();
		ab.setDisplayShowHomeEnabled(true);
		ab.setHomeButtonEnabled(false);
		ab.setDisplayShowCustomEnabled(true);		
		ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.moodclip_main_actionbar));
		ab.setIcon(getResources().getDrawable(R.drawable.my_page));
		*/
		/*
		FragmentManager fm = getSupportFragmentManager();
		fragments = fm.findFragmentById(R.id.UserProfile);
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.show(fragments);
		transaction.commit();
		*/
		Profile_picture = (ProfilePictureView)findViewById(R.id.profilePicture);
		Profile_picture.setCropped(true);
		Profile_name = (TextView) findViewById(R.id.profileName);
		
		Typeface typeface = Typeface.createFromAsset(getAssets(), "540.ttf");
		Profile_name.setTypeface(typeface);
		
		//SharedPreferences userinfo = getSharedPreferences("INFO", 0);
		intent = getIntent();
		user_id = intent.getStringExtra("uid");
		Profile_picture.setProfileId(user_id);
		Profile_name.setText(intent.getStringExtra("name"));

		friendsBtn = (ImageView) findViewById(R.id.friendsBtn);
		SharedPreferences setting = getSharedPreferences("LOGINTYPE",0);
		loginType = setting.getBoolean("loginType", loginType);
		if(loginType){
			friendsBtn.setVisibility(View.INVISIBLE);
		}
		if("false".equals(intent.getStringExtra("flag"))){
			friendsBtn.setVisibility(View.INVISIBLE);
		}
		friendsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(FavoriteActivity.this,
						FriendsActivity.class);
				FlurryAgent.logEvent("FriendsActivity");
				startActivity(intent);
			}
		});
		FavoriteEmotion = (ImageView) findViewById(R.id.FavoriteEmotion);

		FavoriteGridview = (GridView) findViewById(R.id.FavoriteGridview);
		FavoriteGridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				GalleryVideoInfoObject videoData = mData.get(position);
				serial_num = videoData.serial_num;
				emotion = videoData.emotion;
				title = videoData.title;
				kind = videoData.kind;
				description = videoData.description;
				maleGoodCnt = videoData.goodCntMale;
				femaleGoodCnt = videoData.goodCntFemale;
				PageBm = videoData.pageBm;
				Bm = videoData.bm;
				owner = videoData.owner;
				//duration = videoData.duration;

				intent = new Intent(FavoriteActivity.this,
						YoutubeAlonePlayerActivity.class);
				intent.putExtra("serial_num", serial_num);
				intent.putExtra("emotion", emotion);
				intent.putExtra("title", title);
				intent.putExtra("owner", owner);
				intent.putExtra("kind", kind);
				intent.putExtra("description", description);
				intent.putExtra("maleGoodCnt", maleGoodCnt);
				intent.putExtra("femaleGoodCnt", femaleGoodCnt);
				intent.putExtra("duration", duration);
				intent.putExtra("PageBm", PageBm);
				intent.putExtra("thumbnail_url", Bm);
				intent.putExtra("user_id", user_id);
				startActivity(intent);
			}
		});

		new ListTask().execute();
	}

	private class ListTask extends
			AsyncTask<Void, Integer, ArrayList<GalleryVideoInfoObject>> {
		private ProgressDialog progDial;

		ArrayList<GalleryVideoInfoObject> jsonAllList = null;
		JSONArray jsonArray = null;
		public String VideoSrc;
		public Bitmap ThumbnailBm;

		protected void onPreExecute() {
			progDial = ProgressDialog.show(FavoriteActivity.this, "",
					"잠시만 기다려주세요..");
			progDial.setCancelable(true);
		}

		protected ArrayList<GalleryVideoInfoObject> doInBackground(
				Void... params) {

			//SharedPreferences userinfo = getSharedPreferences("INFO", 0);
			//String userEmail = userinfo.getString("email", "");
			String str = "";

			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost("http://211.110.1.168:8002/NewFavoriteList");

				List<BasicNameValuePair> param = new ArrayList<BasicNameValuePair>();
				
				String num = null;
				
				if(loginType){
					
					num = intent.getStringExtra("email");
					
				}else if(!loginType){
					
					num = intent.getStringExtra("uid");
				}
				
				param.add(new BasicNameValuePair("user_email", num));
				// param.add(new BasicNameValuePair("video_emotion", emotion));

				UrlEncodedFormEntity ent;
				ent = new UrlEncodedFormEntity(param, HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePOST = client.execute(post);
				HttpEntity resEntity = responsePOST.getEntity();

				if (resEntity != null) {
					Log.d("http test",
							"resEntity.toString() = " + resEntity.toString());
					InputStream inputstream = resEntity.getContent();
					BufferedReader bufferedreader = new BufferedReader(
							new InputStreamReader(inputstream, "UTF-8"));
					String line = null;
					while ((line = bufferedreader.readLine()) != null) {
						str += line + "\n";
						Log.d("http test", "str = " + str);
					}
				}

			} catch (Exception e) {
				android.util.Log.e("http error", "error = " + e);
				e.printStackTrace();
			}

			try {

				jsonAllList = new ArrayList<GalleryVideoInfoObject>();
				jsonArray = new JSONArray(str);

				for (int i = 0; i < jsonArray.length(); i++) {

					GalleryVideoInfoObject entity = new GalleryVideoInfoObject();
					JSONObject jData = jsonArray.getJSONObject(i);

					entity.goodCntMale = Integer.toString(jData
							.getInt("male_like_cnt"));
					entity.goodCntFemale = Integer.toString(jData
							.getInt("female_like_cnt"));
					entity.title = jData.getString("title");
					entity.serial_num = jData.getString("serial_num");
					entity.kind = jData.getString("kind");
					setPageIcon(entity.kind);
					entity.pageBm = PageBm;
					entity.emotion = jData.getString("emotion");
					ManyEmotion(jData.getString("emotion"));
					
					if(entity.kind.endsWith("youtube")){
						entity.bm = GetImageFromURL("http://i1.ytimg.com/vi/"+ jData.getString("serial_num") + "/mqdefault.jpg");
						entity.src = jData.getString("serial_num");
					}else{
						entity.bm = GetImageFromURL(jData.getString("thumbnail_url"));
					}

					jsonAllList.add(entity);

				}
			} catch (JSONException je) {
				Log.e("getJSONBloodRequestAllList",
						"JSON",
						je);
			}

			return jsonAllList;
		}

		protected void onPostExecute(ArrayList<GalleryVideoInfoObject> result) {
			
			mData = result;
			if (result != null && result.size() > 0) {
				
				max(figures);
				
				Adapter = new FavoriteListAdapter(FavoriteActivity.this, mData);
				FavoriteGridview.setAdapter(Adapter);
				Adapter.notifyDataSetChanged();
			}

			progDial.dismiss();
		}

		private Bitmap GetImageFromURL(String strImageURL) {
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

	}

	public void ManyEmotion(String emotion) {

		if (emotion.equals("amazing")) {
			figures[0]++;
			FlurryAgent.logEvent("amazing");
		} else if (emotion.equals("cool")) {
			figures[1]++;
			FlurryAgent.logEvent("cool");
		} else if (emotion.equals("disgusting")) {
			figures[2]++;
			FlurryAgent.logEvent("codisgusting");
		} else if (emotion.equals("funny")) {
			figures[3]++;
			FlurryAgent.logEvent("funny");
		} else if (emotion.equals("impressive")) {
			figures[4]++;
			FlurryAgent.logEvent("impressive");
		} else if (emotion.equals("sad")) {
			figures[5]++;
			FlurryAgent.logEvent("sad");
		} else if (emotion.equals("scare")) {
			figures[6]++;
			FlurryAgent.logEvent("scare");
		} else if (emotion.equals("sexy")) {
			figures[7]++;
			FlurryAgent.logEvent("sexy");
		}
	}

	public void max(int n[]) {
		int max = n[0];
		int cnt = 8;

		for (int i = 1; i < n.length; i++) {
			if (n[i] > max) {
				max = n[i];
				cnt = i;
			}
		}

		if (cnt == 0) {
			FavoriteEmotion.setImageResource(R.drawable.profile_emotion_amazing);
			FlurryAgent.logEvent("profile_emotion_amazing");
		} 
		else if (cnt == 1) {
			FavoriteEmotion.setImageResource(R.drawable.profile_emotion_crazy);
			FlurryAgent.logEvent("profile_emotion_crazy");
		} 
		else if (cnt == 2) {
			FavoriteEmotion.setImageResource(R.drawable.profile_emotion_exciting);
			FlurryAgent.logEvent("profile_emotion_exciting");
		} 
		else if (cnt == 3) {
			FavoriteEmotion.setImageResource(R.drawable.profile_emotion_funny);
			FlurryAgent.logEvent("profile_emotion_funny");
		} 
		else if (cnt == 4) {
			FavoriteEmotion.setImageResource(R.drawable.profile_emotion_impressive);
			FlurryAgent.logEvent("profile_emotion_impressive");
		} 
		else if (cnt == 5) {
			FavoriteEmotion.setImageResource(R.drawable.profile_emotion_cute);
			FlurryAgent.logEvent("profile_emotion_cute");
		} 
		else if (cnt == 6) {
			FavoriteEmotion.setImageResource(R.drawable.profile_emotion_scare);
			FlurryAgent.logEvent("profile_emotion_scare");
		} 
		else if (cnt == 7) {
			FavoriteEmotion.setImageResource(R.drawable.profile_emotion_sexy);
			FlurryAgent.logEvent("profile_emotion_sexy");
		} 
		else if (cnt == 8) {
			FavoriteEmotion.setImageResource(0);
		}
	}

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		intent = new Intent();
		setResult(RESULT_OK,intent);
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	/*
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		  case android.R.id.home:
		   finish();
		   overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
		   break;
		}
		return true;
	}
	*/
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
	public void setPageBm(String url){
		PageBm = GetImageFromURL(url);
	}
	
	public void setPageIcon(String kind) {
		
		Log.d("bmw",""+kind);
		
		if(kind == "youtube"){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/s100x100/1466034_202033716646413_467756070_s.png");
			FlurryAgent.logEvent("youtube");
		}else if(kind.equals("moodclip")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/s100x100/1466034_202033716646413_467756070_s.png");
			FlurryAgent.logEvent("moodclip");
		}else if(kind.equals("thestar301")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/c35.9.110.110/s100x100/419407_376809259107104_1235961106_a.jpg");
			FlurryAgent.logEvent("thestar301");
		}else if(kind.equals("hahaha99990")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/c9.9.113.113/s100x100/999348_501741766567750_827280592_s.jpg");
			FlurryAgent.logEvent("hahaha99990");
		}else if(kind.equals("humorletter")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/s100x100/1461432_551929198228054_131826972_s.png");
			FlurryAgent.logEvent("humorletter");
		}else if(kind.equals("ppangdong")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c9.9.113.113/s100x100/600219_513354578738528_1109559049_s.png");
			FlurryAgent.logEvent("ppangdong");
		}else if(kind.equals("humorstorage")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c9.9.113.113/s100x100/946185_377793025659992_1548575262_s.png");
			FlurryAgent.logEvent("humorstorage");
		}else if(kind.equals("ItsBeenRealBrother")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c12.12.156.156/s100x100/9342_511425502239814_628508102_a.jpg");
			FlurryAgent.logEvent("ItsBeenRealBrother");
		}else if(kind.equals("20bird")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/c13.12.155.155/s100x100/936056_297374053730776_1692627149_a.jpg");
			FlurryAgent.logEvent("20bird");
		}else if(kind.equals("thelastwang")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/c27.0.116.116/s100x100/64676_291791597607645_785965089_a.jpg");
			FlurryAgent.logEvent("thelastwang");
		}else if(kind.equals("012345678910x")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/c0.0.109.109/s100x100/954799_167683913419456_1502078313_s.jpg");
			FlurryAgent.logEvent("012345678910x");
		}else if(kind.equals("Minal.Rampage")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/c0.5.111.111/s100x100/1383673_431047750329387_500868873_s.png");
			FlurryAgent.logEvent("Minal.Rampage");
		}else if(kind.equals("nimppongppal")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c9.9.113.113/s100x100/8567_200016653489324_1916896387_s.jpg");
			FlurryAgent.logEvent("nimppongppal");
		}else if(kind.equals("Hmovie")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c8.0.115.115/s100x100/579066_441249489282554_341575019_s.png");
			FlurryAgent.logEvent("Hmovie");
		}else if(kind.equals("thebestvideopage")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/c9.9.113.113/s100x100/954743_498729413542792_1709935294_s.png");
			FlurryAgent.logEvent("thebestvideopage");
		}else if(kind.equals("speedgoodclick")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/c9.9.113.113/s100x100/401872_533808329989689_699242581_s.jpg");
			FlurryAgent.logEvent("speedgoodclick");
		}else if(kind.equals("onewaypage")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/c35.9.110.110/s100x100/419407_376809259107104_1235961106_a.jpg");
			FlurryAgent.logEvent("onewaypage");
		}else if(kind.equals("Todayvideo")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c15.8.100.100/s100x100/691_170156723158206_1401625975_s.jpg");
			FlurryAgent.logEvent("Todayvideo");
		}else if(kind.equals("5mkin")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/s100x100/64161_481771205222219_1704489638_s.jpg");
			FlurryAgent.logEvent("5mkin");
		}else if(kind.equals("woodongpage")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/s100x100/1453387_764310476919533_1776347175_s.png");
			FlurryAgent.logEvent("woodongpage");
		}else if(kind.equals("dntTkahf")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/s100x100/1238921_683895998287076_362231334_s.jpg");
			FlurryAgent.logEvent("dntTkahf");
		}else if(kind.equals("elitevideo")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/s100x100/537984_513988785353803_149951184_s.jpg");
			FlurryAgent.logEvent("elitevideo");
		}else if(kind.equals("goodsfun")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/c1.0.128.128/s100x100/1011182_598277733539802_1716084300_s.png");
			FlurryAgent.logEvent("goodsfun");
		}else if(kind.equals("Jageking")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c9.9.113.113/s100x100/1001209_646042785407626_1778109405_s.jpg");
			FlurryAgent.logEvent("Jageking");
		}else if(kind.equals("pandoratv")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/s100x100/1459933_737070596321710_130360528_s.jpg");
			FlurryAgent.logEvent("pandoratv");
		}else if(kind.equals("620595034636897")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c9.9.113.113/s100x100/1011613_673926989303701_572522780_s.jpg");
			FlurryAgent.logEvent("620595034636897");
		}
		
		else if(kind.equals("NimIMuseowoJugseubnida")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/c0.0.129.129/s100x100/1395992_200152870168088_1045199227_s.jpg");
			FlurryAgent.logEvent("NimIMuseowoJugseubnida");
		}else if(kind.equals("uuuuu11111")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c11.0.108.108/s100x100/554156_530013253744728_593525696_s.png");
			FlurryAgent.logEvent("uuuuu11111");
		}else if(kind.equals("cospicospi.page")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/c0.34.156.156/s100x100/988621_500702463333933_1666577450_a.jpg");
			FlurryAgent.logEvent("cospicospi");
		}else if(kind.equals("ohscare")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c146.21.258.258/s160x160/941431_662174683799328_843988840_n.jpg");
			FlurryAgent.logEvent("ohscare");
		}
		
		else if(kind.equals("18crazyvideo")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c37.8.106.106/s100x100/1094970_621771237854218_1805952122_a.png");
			FlurryAgent.logEvent("18crazyvideo");
		}else if(kind.equals("michindong")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/c8.0.115.115/s100x100/541772_573168642754478_1096489606_s.jpg");
			FlurryAgent.logEvent("michindong");
		}
		
		else if(kind.equals("5mkin19")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/s100x100/1175398_235625673257966_854521260_s.jpg");
			FlurryAgent.logEvent("5mkin19");
		}else if(kind.equals("secretfolder")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash1/c9.9.113.113/s100x100/1004671_495549427186570_957032266_s.jpg");
			FlurryAgent.logEvent("secretfolder");
		}
		
		else if(kind.equals("allthatdate")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/s100x100/1012739_327771320688517_1130570075_s.jpg");
			FlurryAgent.logEvent("allthatdate");
		}else if(kind.equals("allthatdrama")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/c8.0.117.117/s100x100/1098305_672415479454185_978800756_a.jpg");
			FlurryAgent.logEvent("allthatdrama");
		}else if(kind.equals("womenvideos")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/s100x100/1011325_290944997715942_2067997948_s.jpg");
			FlurryAgent.logEvent("womenvideos");
		}else if(kind.equals("saalcafe")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/c9.9.113.113/s100x100/318057_565414670153273_141166646_s.png");
			FlurryAgent.logEvent("saalcafe");
		}else if(kind.equals("LovePropose.me")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/s100x100/601762_365102353557224_653174076_s.jpg");
			FlurryAgent.logEvent("LovePropose");
		}else if(kind.equals("208524412532625")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/c17.0.97.97/267363_210003955718004_3042331_s.jpg");
			FlurryAgent.logEvent("208524412532625");
		}
		
		else if(kind.equals("AnimalArirang")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/c0.0.180.180/s100x100/1395289_217897001715219_320668063_a.jpg");
			FlurryAgent.logEvent("AnimalArirang");
		}else if(kind.equals("cutycutylab")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/s100x100/1146489_366236366837187_1369836271_s.jpg");
			FlurryAgent.logEvent("cutycutylab");
		}else if(kind.equals("hidoggydoggy")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/s100x100/1425555_179991248861896_132254635_s.jpg");
			FlurryAgent.logEvent("hidoggydoggy");
		}else if(kind.equals("dogsamo")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/c9.9.113.113/s100x100/164231_305461169590997_16638219_s.jpg?lvh=1");
			FlurryAgent.logEvent("dogsamo");
		}else if(kind.equals("rcatshouse")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/c191.29.359.359/s100x100/1000465_204688536356407_917914204_n.jpg");
			FlurryAgent.logEvent("rcatshouse");
		}else if(kind.equals("hellolovelybaby")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc1/c11.0.108.108/s100x100/995114_270389473104122_328569214_s.png");
			FlurryAgent.logEvent("hellolovelybaby");
		}
		
		else if(kind.equals("mmakorean")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/c26.0.128.128/s100x100/1395978_583562571692438_1021564858_a.jpg");
			FlurryAgent.logEvent("mmakorean");
		}else if(kind.equals("Chuggusipnya")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c9.9.113.113/s100x100/9372_178055635693048_1727824384_s.jpg");
			FlurryAgent.logEvent("Chuggusipnya");
		}else if(kind.equals("ezfootball")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/c31.9.118.118/s100x100/972126_464857743600436_27928657_a.jpg");
			FlurryAgent.logEvent("ezfootball");
		}else if(kind.equals("yachukdong")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/c18.52.156.156/s100x100/431548_456779837703787_1468609440_a.jpg");
			FlurryAgent.logEvent("yachukdong");
		}else if(kind.equals("sportssmile")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/c9.9.113.113/s100x100/260393_556298254421918_90077549_s.png");
			FlurryAgent.logEvent("sportssmile");
		}else if(kind.equals("sportsSNS")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/s100x100/1374190_551151878293599_1815550392_s.png");
			FlurryAgent.logEvent("sportsSNS");
		}else if(kind.equals("basketballjoa")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c10.9.110.110/s100x100/526654_205736962906491_502122256_s.jpg");
			FlurryAgent.logEvent("basketballjoa");
		}else if(kind.equals("manlysoccer")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/s100x100/1459162_758889780791990_1828821980_s.jpg");
			FlurryAgent.logEvent("manlysoccer");
		}else if(kind.equals("fightglobuy")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c3.0.125.125/s100x100/935617_388888211229815_1485349673_s.jpg");
			FlurryAgent.logEvent("fightglobuy");
		}
		
		else if(kind.equals("moviecapture")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/s100x100/1375262_311042709037355_121558102_s.jpg");
			FlurryAgent.logEvent("moviecapture");
		}else if(kind.equals("likethedrama")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/c12.8.107.107/s100x100/64142_525886967453331_1456352791_s.jpg");
			FlurryAgent.logEvent("likethedrama");
		}else if(kind.equals("ourmovie")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/c8.0.115.115/s100x100/1394422_556381361121916_779477618_s.jpg");
			FlurryAgent.logEvent("ourmovie");
		}else if(kind.equals("504118042983176")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/c42.8.96.96/5474_504120126316301_1668754807_a.jpg");
			FlurryAgent.logEvent("504118042983176");
		}
	}

}

