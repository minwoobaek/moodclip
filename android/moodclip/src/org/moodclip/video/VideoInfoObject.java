/*
 *  ���׿�û ȯ���� ������ ��Ÿ��
 */
package org.moodclip.video;

import android.graphics.Bitmap;

public class VideoInfoObject {
	
	public String title;
	public String serial_num;
	public String goodCntMale;
	public String goodCntFemale;
	public String duration;
	public String description;
	public String emotion;
	public Bitmap bm;
	public Bitmap bm2;
	public int likeCnt;

	public VideoInfoObject() {
	}

	public VideoInfoObject(String title, String serial_num,Bitmap bm,Bitmap bm2,String goodCntMale,String goodCntFemale, String duration, String description) {
		super();
		this.title = title;
		this.serial_num = serial_num;
		this.bm = bm;
		this.bm2 = bm2;
		this.goodCntMale = goodCntMale;
		this.goodCntFemale = goodCntFemale;
		this.duration = duration;
		this.description = description;
	}
	public String getTitle() { return title; }
    public String getSerial_num() { return serial_num; }
    public String getgoodCntMale() { return goodCntMale; }
    public String getgoodCntFemale() { return goodCntFemale; }
    public String getDuration() { return duration; }
    public String getDescription() { return description; }
    public String getEmotion() { return emotion; }
    public int getlikeCnt() { return likeCnt; }
}