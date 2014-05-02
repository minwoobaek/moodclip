package org.moodclip.video.comments;


public class CommentsObject {
	public String commentsNum;
	public String comments;
	public String userName;
	public String userId;
	public String emotionTag;
	
	public CommentsObject() {
	}

	public CommentsObject(String comments, String userName,String userId,String emotionTag) {
		super();
		this.comments = comments;
		this.userName = userName;
		this.userId = userId;
		this.emotionTag = emotionTag;
	}
	public String getComments() { return comments; }
    public String getUserName() { return userName; }
    public String getUserId() { return userId; }
    public String getEmotionTag() { return emotionTag; }

}
