/*
 *  ���׿�û ȯ���� ������ ��Ÿ��
 */
package org.moodclip.video.gallery;

import android.graphics.Bitmap;

public class GalleryVideoInfoObject {
	
	public String title;
	public String serial_num;
	public String goodCntMale;
	public String goodCntFemale;
	public String kind;
	public Bitmap bm;
	public Bitmap pageBm;
	public String src;
	public String owner;
	public String emotion;
	public Double duration;
	public String description = "";
	//public Bitmap bm2;

	public GalleryVideoInfoObject() {
	}

	public GalleryVideoInfoObject(String title, String serial_num,Bitmap bm,String goodCntMale,String goodCntFemale, String kind,String src,Double duration,String description) {
		super();
		this.title = title;
		this.serial_num = serial_num;
		this.bm = bm;
		//this.bm2 = bm2;
		this.goodCntMale = goodCntMale;
		this.goodCntFemale = goodCntFemale;
		this.kind = kind;
		this.src = src;
		this.duration = duration;
		this.description = description;
	}
	public String getTitle() { return title; }
    public String getSerial_num() { return serial_num; }
    public String getgoodCntMale() { return goodCntMale; }
    public String getgoodCntFemale() { return goodCntFemale; }
    public String getKind() { return kind; }
    public String getSrc() { return src; }
    public String getDescription() { return description; }
    public Double getDuration() { return duration; }
    public String getOwner() { return owner; }
}