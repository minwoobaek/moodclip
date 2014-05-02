package org.moodclip.menu.setting.friends;

import java.util.ArrayList;
import java.util.List;

import org.moodclip.main.R;

import android.R.integer;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;

public class FriendsInviteAdapter extends ArrayAdapter<FriendsObject> {

	// ArrayList<FriendsObject> userFriends = new ArrayList<FriendsObject>();

	public FriendsInviteAdapter(Context friendsInviteFragment,
			List<FriendsObject> objects) {
		super(friendsInviteFragment, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final int pos = position;
		LayoutInflater inflater = LayoutInflater.from(getContext());

		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.friends_invite_adapter,
					null);

			holder = new ViewHolder();

			Typeface typeface = Typeface.createFromAsset(getContext()
					.getAssets(), "540.ttf");

			holder.friendsName = (TextView) convertView
					.findViewById(R.id.friendsInviteName);
			holder.friendsName.setTypeface(typeface);
			// holder.pic_square = (ImageView)
			// itemView.findViewById(R.id.pic_square);
			holder.profilePictureView = (ProfilePictureView) convertView
					.findViewById(R.id.friendsInvitePic);
			holder.friendsInviteBtn = (ImageView) convertView
					.findViewById(R.id.friendsInviteBtn);
			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		// userFriends.add(getItem(pos));
		holder.friendsName.setText(FriendsActivity.Data.get(pos).name);
		// holder.pic_square.setImageBitmap(userFriends.bm);
		holder.profilePictureView.setProfileId(FriendsActivity.Data.get(pos).uid);

		holder.friendsInviteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FriendsActivity.sendRequestDialog(FriendsActivity.Data.get(pos).uid);
			}
		});

		return convertView;
	}

	public class ViewHolder {
		// ImageView pic_square;
		TextView friendsName;
		ProfilePictureView profilePictureView;
		ImageView friendsInviteBtn;
	}

}
