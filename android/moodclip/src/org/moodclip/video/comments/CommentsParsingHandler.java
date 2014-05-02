package org.moodclip.video.comments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommentsParsingHandler {
	
	public static ArrayList<CommentsObject> Parsing(String buf,int progress) {

		ArrayList<CommentsObject> jsonAllList = null;
		JSONArray jsonArray = null;
		int end;
		try {

			jsonAllList = new ArrayList<CommentsObject>();
			jsonArray = new JSONArray(buf);
			CommentsObject entity;

			int jsonObjSize = jsonArray.length();
			/*
			if(jsonObjSize < 6){
				end = jsonObjSize;
			}else{
				end = 5;
			}
			*/
			for (int i = (progress*5); i < (progress+1)*5; i++) {

				entity = new CommentsObject();

				JSONObject jData = jsonArray.getJSONObject(i);

				entity.commentsNum = jData.getString("id");
				entity.comments = jData.getString("comments");
				entity.userName = jData.getString("user_name");
				entity.userId = jData.getString("user_id");
				entity.emotionTag = jData.getString("emotion");

				jsonAllList.add(entity);
			}
			
		} catch (JSONException je) {
		}

		return jsonAllList;
	}

}
