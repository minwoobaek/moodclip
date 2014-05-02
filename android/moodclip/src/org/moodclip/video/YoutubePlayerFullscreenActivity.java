package org.moodclip.video;


import org.moodclip.main.R;
import org.moodclip.video.YoutubeApi.DeveloperKey;
import org.moodclip.video.YoutubeApi.YouTubeFailureRecoveryActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;

import com.flurry.android.FlurryAgent;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
/**
 * Sample activity showing how to properly enable custom fullscreen behavior.
 * <p>
 * This is the preferred way of handling fullscreen because the default
 * fullscreen implementation will cause re-buffering of the video.
 */
public class YoutubePlayerFullscreenActivity extends
		YouTubeFailureRecoveryActivity implements View.OnClickListener,
		CompoundButton.OnCheckedChangeListener,
		YouTubePlayer.OnFullscreenListener {

	//private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9 ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT: ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;

	private YouTubePlayerView playerView;
	private YouTubePlayer Player;

	private boolean fullscreen;

	Intent intent;
	String serial_num;
	int time;
	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		FlurryAgent.logEvent("youtubeplayfullscreen");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video_player_fullscreen);
		playerView = (YouTubePlayerView) findViewById(R.id.youtube_view3);

		intent = getIntent();
		serial_num = intent.getStringExtra("serial_num");
		time = intent.getIntExtra("time", 0);

		playerView.initialize(DeveloperKey.DEVELOPER_KEY, this);
	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		this.Player = player;
		// setControlsEnabled();
		// Specify that we want to handle fullscreen behavior ourselves.
		// player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
		// player.setOnFullscreenListener(this);
		if (!wasRestored) {
			Player.loadVideo(serial_num,time);
			Player.setFullscreen(!fullscreen);
		}
	}

	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return playerView;
	}

	@Override
	public void onClick(View v) {
		Player.setFullscreen(!fullscreen);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int controlFlags = Player.getFullscreenControlFlags();
		if (isChecked) {
			// If you use the FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE,
			// your activity's normal UI
			// should never be laid out in landscape mode (since the video will
			// be fullscreen whenever the
			// activity is in landscape orientation). Therefore you should set
			// the activity's requested
			// orientation to portrait. Typically you would do this in your
			// AndroidManifest.xml, we do it
			// programmatically here since this activity demos fullscreen
			// behavior both with and without
			// this flag).
			//setRequestedOrientation(PORTRAIT_ORIENTATION);
			controlFlags |= YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			controlFlags &= ~YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
		}
		Player.setFullscreenControlFlags(controlFlags);
	}

	@Override
	public void onFullscreen(boolean isFullscreen) {
		fullscreen = isFullscreen;
		// doLayout();
	}
	public void onConfigurationChanged(Configuration newConfig) {
		FlurryAgent.logEvent("onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			intent = new Intent();
			intent.putExtra("CurrentTime", Player.getCurrentTimeMillis());
			setResult(RESULT_OK,intent);
			finish();
		} else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			
		}

	}
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		intent = new Intent();
		intent.putExtra("CurrentTime", Player.getCurrentTimeMillis());
		setResult(RESULT_OK,intent);
		finish();
	}

}
