package org.moodclip.video.gallery;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.moodclip.main.MainActivity;
import org.moodclip.menu.setting.favorite.YoutubeAlonePlayerActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


public class GalleryParsingHandler {
    
	public GalleryParsingHandler() {
		// entity = new GalleryVideoInfoObject();
	}
    
	static Context context = MainActivity.Main;
    
	public static String firstSerial_num;
	public static String firstTitle;
	public static String firstKind;
	public static String firstThumbnail_url;
	public static String firstGoodCntMale;
	public static String firstGoodCntFemale;
    
	public static double firstDuration;
	public static Bitmap firstBm;
	public static Bitmap firstPageBm;
	public static String firstVideoSrc;
	public static String firstOwner;
    
	public static String getTitle() {
		return firstTitle;
	}
    
	public static String getSerialNum() {
		return firstSerial_num;
	}
    
	public static String getKind() {
		return firstKind;
	}
	public static String getThumbnail_url() {
		return firstThumbnail_url;
	}
    
	public static String getgoodCntMale() {
		return firstGoodCntMale;
	}
    
	public static String getgoodCntFemale() {
		return firstGoodCntFemale;
	}
    
	public static Bitmap getBitmap() {
		return firstBm;
	}
	public static Bitmap getPageBitmap() {
		return firstPageBm;
	}
    
	public static String getSrc() {
		return firstVideoSrc;
	}
    
	public static double getDuration() {
		return firstDuration;
	}
    
	public static String getOwner() {
		return firstOwner;
	}
	
	public Bitmap PageBm;
	public String Owner;
	//public double Duration;
	//public String VideoSrc;
	//public Bitmap ThumbnailBm;
    
	public ArrayList<GalleryVideoInfoObject> getJSONVideoRequestAllList(StringBuilder buf, boolean flag1, Boolean flag2, int num) {
        
		ArrayList<GalleryVideoInfoObject> jsonAllList = null;
        
		JSONArray jsonArray = null;
		Log.d("bmw","똥 :"+buf);
		try {
            
			jsonAllList = new ArrayList<GalleryVideoInfoObject>();
			jsonArray = new JSONArray(buf.toString());
            
			int jsonObjSize = jsonArray.length();
            
			if (flag1) {
				JSONObject data = jsonArray.getJSONObject(0);
				firstSerial_num = data.getString("serial_num");
				firstTitle = data.getString("title");
				firstKind = data.getString("kind");
				firstThumbnail_url = data.getString("thumbnail_url");
				firstGoodCntMale = Integer.toString(data.getInt("male_like_cnt"));
				firstGoodCntFemale = Integer.toString(data.getInt("female_like_cnt"));
				firstDuration = data.getDouble("duration");
				
				GalleryVideoInfoObject entity = new GalleryVideoInfoObject();
				entity.serial_num = firstSerial_num;
				entity.title = firstTitle;
				entity.kind = firstKind;
				entity.goodCntMale = firstGoodCntMale;
				entity.goodCntFemale = firstGoodCntFemale;
				entity.duration = firstDuration;
				
				if (firstKind.endsWith("youtube")) {
					firstBm = GetImageFromURL("http://i1.ytimg.com/vi/" + firstSerial_num + "/mqdefault.jpg");
					firstPageBm = GetImageFromURL("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/s100x100/1466034_202033716646413_467756070_s.png");
					firstVideoSrc = firstSerial_num;
					firstOwner = "";
					
					entity.bm = firstBm;
					entity.pageBm = firstPageBm;
					entity.src = firstVideoSrc;
					entity.owner = "";
					
				} else {
					//setVideoSrc(serial_num);
					setPageIcon(firstKind);
					
					entity.pageBm = firstPageBm = PageBm;
					entity.owner = firstOwner = Owner;
					entity.bm = firstBm = GetImageFromURL(firstThumbnail_url);
					//firstVideoSrc = VideoSrc;
                    
				}
				jsonAllList.add(entity);
			}
			
			if (flag2) {
				GalleryVideoInfoObject entity = new GalleryVideoInfoObject();
				entity.serial_num = YoutubeAlonePlayerActivity.Serial_num;
				entity.title = YoutubeAlonePlayerActivity.Title;
				entity.description = YoutubeAlonePlayerActivity.Description;
				entity.goodCntMale = YoutubeAlonePlayerActivity.MaleGoodCnt;
				entity.goodCntFemale = YoutubeAlonePlayerActivity.FemaleGoodCnt;
				entity.kind = YoutubeAlonePlayerActivity.Kind;
                
				if (entity.kind.endsWith("youtube")) {
					entity.bm = GetImageFromURL("http://i1.ytimg.com/vi/"
                                                + YoutubeAlonePlayerActivity.Serial_num
                                                + "/mqdefault.jpg");
					entity.pageBm = GetImageFromURL("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/s100x100/1466034_202033716646413_467756070_s.png");
					entity.src = YoutubeAlonePlayerActivity.Serial_num;
					entity.duration = YoutubeAlonePlayerActivity.Duration;
					entity.owner = "";
				} else {
					setPageIcon(entity.kind);
					
					entity.pageBm = firstPageBm = PageBm;
					entity.owner = firstOwner = Owner;
					entity.bm = firstBm = YoutubeAlonePlayerActivity.Thumbnail_url;
					entity.duration = YoutubeAlonePlayerActivity.Duration;
				}
				jsonAllList.add(entity);
			}
			
			for (int i = num; i < jsonObjSize; i++) {
                
				GalleryVideoInfoObject entity = new GalleryVideoInfoObject();
                
				JSONObject jData = jsonArray.getJSONObject(i);
                
				entity.goodCntMale = Integer.toString(jData.getInt("male_like_cnt"));
				entity.goodCntFemale = Integer.toString(jData.getInt("female_like_cnt"));
				entity.kind = jData.getString("kind");
				entity.title = jData.getString("title");
				entity.serial_num = jData.getString("serial_num");
				entity.duration = jData.getDouble("duration");
				
				if (entity.kind.endsWith("youtube")) {
					entity.bm = GetImageFromURL("http://i1.ytimg.com/vi/"+ jData.getString("serial_num") + "/mqdefault.jpg");
					entity.pageBm = GetImageFromURL("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/s100x100/1466034_202033716646413_467756070_s.png");
					entity.src = entity.serial_num;
					entity.owner = "";
				} else {
					//setVideoSrc(entity.serial_num);
					setPageIcon(entity.kind);
					
					entity.pageBm = PageBm;
					entity.owner = Owner;
					entity.bm = GetImageFromURL(jData.getString("thumbnail_url"));
					//entity.src = VideoSrc;
				}
                
				jsonAllList.add(entity);
			}
		} catch (JSONException je) {
			Log.e("getJSONBloodRequestAllList",
                  "JSON占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙",
                  je);
		}
        
		return jsonAllList;
	}
    /*
     public void setVideoSrc(String serial_num) {
     
     String fqlQuery = "SELECT thumbnail_link,src,owner,length FROM video WHERE vid = "
     + serial_num;
     Bundle params = new Bundle();
     params.putString("q", fqlQuery);
     Request request = new Request(MainActivity.getSs(), "/fql", params,
     HttpMethod.GET, new Request.Callback() {
     public void onCompleted(Response response) {
     GraphObject go = response.getGraphObject();
     JSONObject jso = go.getInnerJSONObject();
     try {
     JSONArray arr = jso.getJSONArray("data");
     JSONObject data = arr.getJSONObject(0);
     ThumbnailBm = GetImageFromURL(data
     .getString("thumbnail_link"));
     VideoSrc = data.getString("src");
     Duration = data.getDouble("length");
     //Owner = data.getInt("owner");
     } catch (JSONException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
     }
     }
     });
     Request.executeAndWait(request);
     // Request.executeBatchAsync(request);
     
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
			
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inSampleSize = 2;
			option.inPurgeable = true;
			option.inDither = true;
			
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
		
		if(kind == "youtube"){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/s100x100/1466034_202033716646413_467756070_s.png");
			Owner = "무드클립";
		}else if(kind.equals("moodclip")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/s100x100/1466034_202033716646413_467756070_s.png");
			Owner = "무드클립";
		}else if(kind.equals("thestar301")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/c35.9.110.110/s100x100/419407_376809259107104_1235961106_a.jpg");
			Owner = "ㅎㅋㅎㅋ";
		}else if(kind.equals("hahaha99990")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/c9.9.113.113/s100x100/999348_501741766567750_827280592_s.jpg");
			Owner = "너무웃겨서 눈물날 지경입니다";
		}else if(kind.equals("humorletter")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/s100x100/1461432_551929198228054_131826972_s.png");
			Owner = "유머레터";
		}else if(kind.equals("ppangdong")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c9.9.113.113/s100x100/600219_513354578738528_1109559049_s.png");
			Owner = " 더풋샵 홍대리의 빵 터지는 동영상";
		}else if(kind.equals("humorstorage")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c9.9.113.113/s100x100/946185_377793025659992_1548575262_s.png");
			Owner = "유머저장소";
		}else if(kind.equals("ItsBeenRealBrother")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c12.12.156.156/s100x100/9342_511425502239814_628508102_a.jpg");
			Owner = "It's been REAL - 브라더";
		}else if(kind.equals("20bird")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/c13.12.155.155/s100x100/936056_297374053730776_1692627149_a.jpg");
			Owner = "김성근님이 미쳐날뛰고있습니다";
		}else if(kind.equals("thelastwang")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/c27.0.116.116/s100x100/64676_291791597607645_785965089_a.jpg");
			Owner = "끝판왕";
		}else if(kind.equals("012345678910x")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/c0.0.109.109/s100x100/954799_167683913419456_1502078313_s.jpg");
			Owner = "나이는 숫자에 불과하다";
		}else if(kind.equals("Minal.Rampage")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/c0.5.111.111/s100x100/1383673_431047750329387_500868873_s.png");
			Owner = "님이 미쳐날뛰고있습니다";
		}else if(kind.equals("nimppongppal")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c9.9.113.113/s100x100/8567_200016653489324_1916896387_s.jpg");
			Owner = "님이 뽕을 빨고 개소리를 지껄입니다";
		}else if(kind.equals("Hmovie")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c8.0.115.115/s100x100/579066_441249489282554_341575019_s.png");
			Owner = "동영상 천국";
		}else if(kind.equals("thebestvideopage")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/c9.9.113.113/s100x100/954743_498729413542792_1709935294_s.png");
			Owner = "베스트 비디오";
		}else if(kind.equals("speedgoodclick")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/c9.9.113.113/s100x100/401872_533808329989689_699242581_s.jpg");
			Owner = "빨리좋아요안눌러?";
		}else if(kind.equals("onewaypage")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/c35.9.110.110/s100x100/419407_376809259107104_1235961106_a.jpg");
			Owner = "어머! 이건 봐야 돼";
		}else if(kind.equals("Todayvideo")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c15.8.100.100/s100x100/691_170156723158206_1401625975_s.jpg");
			Owner = "오늘의 동영상";
		}else if(kind.equals("5mkin")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/s100x100/64161_481771205222219_1704489638_s.jpg");
			Owner = "오즐 - 5분의 즐거움";
		}else if(kind.equals("woodongpage")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/s100x100/1453387_764310476919533_1776347175_s.png");
			Owner = "우리들의 동영상";
		}else if(kind.equals("dntTkahf")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/s100x100/1238921_683895998287076_362231334_s.jpg");
			Owner = "웃기고/싸우고/몰카당하는 웃싸몰";
		}else if(kind.equals("elitevideo")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/s100x100/537984_513988785353803_149951184_s.jpg");
			Owner = "엘리트의 동영상";
		}else if(kind.equals("goodsfun")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/c1.0.128.128/s100x100/1011182_598277733539802_1716084300_s.png");
			Owner = "웃어보자";
		}else if(kind.equals("Jageking")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c9.9.113.113/s100x100/1001209_646042785407626_1778109405_s.jpg");
			Owner = "자게왕";
		}else if(kind.equals("pandoratv")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/s100x100/1459933_737070596321710_130360528_s.jpg");
			Owner = "판도라티비(PandoraTV)";
		}else if(kind.equals("620595034636897")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c9.9.113.113/s100x100/1011613_673926989303701_572522780_s.jpg");
			Owner = "쩐당";
		}
		
		else if(kind.equals("NimIMuseowoJugseubnida")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/c0.0.129.129/s100x100/1395992_200152870168088_1045199227_s.jpg");
			Owner = "님이 무서워 죽습니다.";
		}else if(kind.equals("uuuuu11111")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c11.0.108.108/s100x100/554156_530013253744728_593525696_s.png");
			Owner = "미스터리";
		}else if(kind.equals("cospicospi.page")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/c0.34.156.156/s100x100/988621_500702463333933_1666577450_a.jpg");
			Owner = "공포매니아-심령,공포 동영상";
		}else if(kind.equals("ohscare")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c146.21.258.258/s160x160/941431_662174683799328_843988840_n.jpg");
			Owner = "오싹해라";
		}
		
		else if(kind.equals("18crazyvideo")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c37.8.106.106/s100x100/1094970_621771237854218_1805952122_a.png");
			Owner = "18 미친 동영상";
		}else if(kind.equals("michindong")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/c8.0.115.115/s100x100/541772_573168642754478_1096489606_s.jpg");
			Owner = "미친 동영상";
		}
		
		else if(kind.equals("5mkin19")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/s100x100/1175398_235625673257966_854521260_s.jpg");
			Owner = "오즐19 - 5분의 즐거움 19금버전";
		}else if(kind.equals("secretfolder")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash1/c9.9.113.113/s100x100/1004671_495549427186570_957032266_s.jpg");
			Owner = "남자들의 비밀폴더";
		}
		
		else if(kind.equals("allthatdate")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/s100x100/1012739_327771320688517_1130570075_s.jpg");
			Owner = "연애의 모든 것";
		}else if(kind.equals("allthatdrama")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/c8.0.117.117/s100x100/1098305_672415479454185_978800756_a.jpg");
			Owner = "모두의 드라마";
		}else if(kind.equals("womenvideos")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/s100x100/1011325_290944997715942_2067997948_s.jpg");
			Owner = "여자들의 동영상";
		}else if(kind.equals("saalcafe")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/c9.9.113.113/s100x100/318057_565414670153273_141166646_s.png");
			Owner = "사랑할 때 알아야 할 것들";
		}else if(kind.equals("LovePropose.me")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/s100x100/601762_365102353557224_653174076_s.jpg");
			Owner = "러브프로포즈 (LovePropose)";
		}else if(kind.equals("208524412532625")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/c17.0.97.97/267363_210003955718004_3042331_s.jpg");
			Owner = "사랑해";
		}
		
		else if(kind.equals("AnimalArirang")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/c0.0.180.180/s100x100/1395289_217897001715219_320668063_a.jpg");
			Owner = "Animal Arirang";
		}else if(kind.equals("cutycutylab")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/s100x100/1146489_366236366837187_1369836271_s.jpg");
			Owner = "귀욤귀욤연구소";
		}else if(kind.equals("hidoggydoggy")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/s100x100/1425555_179991248861896_132254635_s.jpg");
			Owner = "도기도기";
		}else if(kind.equals("dogsamo")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/c9.9.113.113/s100x100/164231_305461169590997_16638219_s.jpg?lvh=1");
			Owner = "동물들의 동영상";
		}else if(kind.equals("rcatshouse")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/c191.29.359.359/s100x100/1000465_204688536356407_917914204_n.jpg");
			Owner = "옥탑방 고양이";
		}else if(kind.equals("hellolovelybaby")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc1/c11.0.108.108/s100x100/995114_270389473104122_328569214_s.png");
			Owner = "헬로 베이비";
		}
		
		else if(kind.equals("mmakorean")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/c26.0.128.128/s100x100/1395978_583562571692438_1021564858_a.jpg");
			Owner = " Mma종합격투기";
		}else if(kind.equals("Chuggusipnya")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c9.9.113.113/s100x100/9372_178055635693048_1727824384_s.jpg");
			Owner = "축구싶냐?";
		}else if(kind.equals("ezfootball")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/c31.9.118.118/s100x100/972126_464857743600436_27928657_a.jpg");
			Owner = "축구가 좋다";
		}else if(kind.equals("yachukdong")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/c18.52.156.156/s100x100/431548_456779837703787_1468609440_a.jpg");
			Owner = "야동말고 축동";
		}else if(kind.equals("sportssmile")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/c9.9.113.113/s100x100/260393_556298254421918_90077549_s.png");
			Owner = "스포츠 마니아 일루모여";
		}else if(kind.equals("sportsSNS")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/s100x100/1374190_551151878293599_1815550392_s.png");
			Owner = "스포츠";
		}else if(kind.equals("basketballjoa")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c10.9.110.110/s100x100/526654_205736962906491_502122256_s.jpg");
			Owner = "농구가좋아";
		}else if(kind.equals("manlysoccer")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/s100x100/1459162_758889780791990_1828821980_s.jpg");
			Owner = "남자라면 축구지";
		}else if(kind.equals("fightglobuy")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/c3.0.125.125/s100x100/935617_388888211229815_1485349673_s.jpg");
			Owner = "파이트 글로바이";
		}
		
		else if(kind.equals("moviecapture")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/s100x100/1375262_311042709037355_121558102_s.jpg");
			Owner = "영화공장 : Movie Factory";
		}else if(kind.equals("likethedrama")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/c12.8.107.107/s100x100/64142_525886967453331_1456352791_s.jpg");
			Owner = "드라마처럼 살아라";
		}else if(kind.equals("ourmovie")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/c8.0.115.115/s100x100/1394422_556381361121916_779477618_s.jpg");
			Owner = "영화는 방울방울";
		}else if(kind.equals("504118042983176")){
			setPageBm("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn2/c42.8.96.96/5474_504120126316301_1668754807_a.jpg");
			Owner = "추억은 방울방울";
		}
	}
    
}