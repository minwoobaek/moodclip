package org.moodclip.video.comments;

import java.util.List;

import org.moodclip.main.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;

public class CommentsListAdapter extends ArrayAdapter<CommentsObject> {
	
	private ViewHolder holder = null;
	private LayoutInflater inflater = null;

	public CommentsListAdapter(Context context, int textViewResourceId, List<CommentsObject> objects) {
		super(context,textViewResourceId, objects);
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	public long getItemId(int position) {
		return super.getItemId(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		//inflater = LayoutInflater.from(getContext());

		View itemView = convertView;

		if (convertView == null) {
			itemView = inflater.inflate(R.layout.comments_adapter, null);

			holder = new ViewHolder();
			Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "540.ttf");
			holder.comments = (TextView) itemView.findViewById(R.id.comments);
			holder.name = (TextView) itemView.findViewById(R.id.userName);
			
			holder.comments.setTypeface(typeface);
			holder.name.setTypeface(typeface);
			
			holder.profilePictureView = (ProfilePictureView) itemView.findViewById(R.id.selection_profile_pic);
			holder.EmotionTag = (ImageView) itemView.findViewById(R.id.EmotionTag);

			itemView.setTag(holder);
			
		} else {
			holder = (ViewHolder) itemView.getTag();
		}

		CommentsObject CommentsData = getItem(position);

		holder.comments.setText(CommentsData.comments);
		holder.name.setText(CommentsData.userName);
		holder.profilePictureView.setProfileId(CommentsData.userId);
		
		if(CommentsData.emotionTag.equals(null)){
			holder.EmotionTag.setImageResource(0);
		}else if(CommentsData.emotionTag.equals("amazing")){
			holder.EmotionTag.setBackgroundResource(R.drawable.emotion_tag_amazing);
		}else if(CommentsData.emotionTag.equals("funny")){
			holder.EmotionTag.setBackgroundResource(R.drawable.emotion_tag_funny);
		}else if(CommentsData.emotionTag.equals("scare")){
			holder.EmotionTag.setBackgroundResource(R.drawable.emotion_tag_scare);
		}else if(CommentsData.emotionTag.equals("impressive")){
			holder.EmotionTag.setBackgroundResource(R.drawable.emotion_tag_impressive);
		}else if(CommentsData.emotionTag.equals("sexy")){
			holder.EmotionTag.setBackgroundResource(R.drawable.emotion_tag_sexy);
		}else if(CommentsData.emotionTag.equals("crazy")){
			holder.EmotionTag.setBackgroundResource(R.drawable.emotion_tag_exciting);
		}else if(CommentsData.emotionTag.equals("cute")){
			holder.EmotionTag.setBackgroundResource(R.drawable.emotion_tag_cute);
		}else if(CommentsData.emotionTag.equals("exciting")){
			holder.EmotionTag.setBackgroundResource(R.drawable.emotion_tag_crazy);
		}

		return itemView;
	}

	public class ViewHolder {
		TextView comments;
		TextView name;
		ImageView EmotionTag;
		ProfilePictureView profilePictureView;
	}
	@Override
	protected void finalize() throws Throwable {
		free();
		super.finalize();
	}
	
	private void free(){
		inflater = null;
		holder = null;
	}
}
