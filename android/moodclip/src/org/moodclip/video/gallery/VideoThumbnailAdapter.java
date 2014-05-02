package org.moodclip.video.gallery;

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

public class VideoThumbnailAdapter extends ArrayAdapter<GalleryVideoInfoObject> {

	public VideoThumbnailAdapter(Context context,
			List<GalleryVideoInfoObject> objects) {
		super(context, 0, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = LayoutInflater.from(getContext());

		View itemView;
		ViewHolder holder;

		if (convertView == null) {
			itemView = inflater.inflate(R.layout.gallery_adapter, null);

			holder = new ViewHolder();

			holder.imgView = (ImageView) itemView.findViewById(R.id.thumbnail);
			
			Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "RIXGOM.TTF");
			holder.title = (TextView) itemView.findViewById(R.id.textView1);
			holder.title.setTypeface(typeface);
			
			holder.cnt = (TextView) itemView.findViewById(R.id.like);
			holder.duration = (TextView) itemView.findViewById(R.id.duration);

			itemView.setTag(holder);
		} else {
			itemView = convertView;
			holder = (ViewHolder) itemView.getTag();
		}

		GalleryVideoInfoObject videoData = getItem(position);
	

			//holder.imgView.setBackgroundResource(R.drawable.ic_launcher);
	
		holder.imgView.setImageBitmap(videoData.bm);
		holder.title.setText(videoData.title);
		int cnt = Integer.parseInt(videoData.goodCntFemale) + Integer.parseInt(videoData.goodCntMale);
		holder.cnt.setText(" "+Integer.toString(cnt));
		
		int duration =  Integer.parseInt(String.valueOf(Math.round(videoData.duration)));
		int min = duration/60;
		int sec = duration%60;
		
		if(sec<10){
			holder.duration.setText(""+Integer.toString(min) +":0"+Integer.toString(sec));
		}else{
			holder.duration.setText(""+Integer.toString(min) +":"+Integer.toString(sec));
		}
	
		return itemView;
	}

	public class ViewHolder {
		TextView title;
		TextView cnt;
		TextView duration;
		ImageView imgView;
	}
}
