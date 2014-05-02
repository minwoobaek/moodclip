package org.moodclip.menu.setting.friends;

import java.util.ArrayList;
import java.util.List;

import org.moodclip.main.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;

public class FriendsMemberAdapter extends ArrayAdapter<FriendsObject>{
	
	//ArrayList<FriendsObject> userFriends = new ArrayList<FriendsObject>();
	
	Context cont;
	
	public FriendsMemberAdapter(Context context,
			List<FriendsObject> objects) {
		super(context, 0, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final int pos = position;
		
		LayoutInflater inflater = LayoutInflater.from(getContext());

		View itemView;
		ViewHolder holder;

		if (convertView == null) {
			itemView = inflater.inflate(R.layout.friends_member_adapter, null);

			holder = new ViewHolder();

			Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "540.ttf");
			holder.friendsName = (TextView) itemView.findViewById(R.id.friendsMemberName);
			holder.friendsName.setTypeface(typeface);
			//holder.pic_square = (ImageView) itemView.findViewById(R.id.pic_square);
			holder.profilePictureView = (ProfilePictureView) itemView.findViewById(R.id.friendsMemberPic);

			itemView.setTag(holder);
		} else {
			itemView = convertView;
			holder = (ViewHolder) itemView.getTag();
		}
		//userFriends.add(getItem(position));
		
		//FriendsObject userFriends = getItem(position);
		holder.friendsName.setText(FriendsActivity.memberData.get(position).name);
		//holder.pic_square.setImageBitmap(userFriends.bm);
		holder.profilePictureView.setProfileId(FriendsActivity.memberData.get(position).uid);
		
		
		return itemView;
	}

	public class ViewHolder {
		//ImageView pic_square;
		TextView friendsName;
		ProfilePictureView profilePictureView;
	}

}
