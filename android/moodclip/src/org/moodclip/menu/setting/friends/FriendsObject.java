package org.moodclip.menu.setting.friends;

import android.graphics.Bitmap;

public class FriendsObject {
	
	public boolean flag = false;
	public String name;
	public String uid;
	public String email;
	public Bitmap bm;
	
	public FriendsObject() {
	}

	public FriendsObject(boolean flag, String uid,String name,Bitmap bm,String email){
	
		this.flag = flag;
		this.name = name;
		this.bm = bm;
		this.uid = uid;
		this.email = email;
		
	}
	

}
