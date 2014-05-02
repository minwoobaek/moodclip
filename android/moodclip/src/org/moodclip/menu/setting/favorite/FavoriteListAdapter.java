package org.moodclip.menu.setting.favorite;

import java.util.List;

import org.moodclip.main.R;
import org.moodclip.video.gallery.GalleryVideoInfoObject;

import com.flurry.android.FlurryAgent;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FavoriteListAdapter extends ArrayAdapter<GalleryVideoInfoObject> {
	
	public FavoriteListAdapter(Context context,
			List<GalleryVideoInfoObject> objects) {
		super(context, 0, objects);
		FlurryAgent.logEvent("FavoriteListAdapter");
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = LayoutInflater.from(getContext());

		View itemView = null;
		ViewHolder holder = null;

		if (convertView == null) {
			
			holder = new ViewHolder();
			
			itemView = inflater.inflate(R.layout.favorite_adapter, null);
			
			holder.FavoriteThumbnail = (ImageView) itemView.findViewById(R.id.FavoriteThumbnail);
			holder.FavoriteTitle = (TextView) itemView.findViewById(R.id.FavoriteTitle);
			
			Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "RIXGOM.TTF");
			holder.FavoriteTitle.setTypeface(typeface);
			
			holder.FavoriteLikeCnt = (TextView) itemView.findViewById(R.id.FavoriteLikeCnt);
			//holder.FavoriteDuration = (TextView) itemView.findViewById(R.id.FavoriteDuration);

			itemView.setTag(holder);
		} else {
			itemView = convertView;
			holder = (ViewHolder) itemView.getTag();
		}

		GalleryVideoInfoObject videoData = getItem(position);
		
		holder.FavoriteThumbnail.setImageBitmap(videoData.bm);
		holder.FavoriteTitle.setText(videoData.title);
		int cnt = Integer.parseInt(videoData.goodCntFemale) + Integer.parseInt(videoData.goodCntMale);
		holder.FavoriteLikeCnt.setText(" "+Integer.toString(cnt));
		//holder.FavoriteDuration.setText(videoData.duration);
	
		return itemView;
	}

	public class ViewHolder {
		TextView FavoriteTitle;
		TextView FavoriteLikeCnt;
		TextView FavoriteDuration;
		ImageView FavoriteThumbnail;
	}
}