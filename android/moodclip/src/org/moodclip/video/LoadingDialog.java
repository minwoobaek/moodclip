package org.moodclip.video;

import org.moodclip.main.R;

import com.flurry.android.FlurryAgent;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class LoadingDialog {

	private static Dialog m_loadingDialog = null;

	public static void hideLoading() {
		if (m_loadingDialog != null) {
			m_loadingDialog.dismiss();
			m_loadingDialog = null;
		}
	}

	public static void showLoading(Context context) {
		FlurryAgent.logEvent("showloading");
		if (m_loadingDialog == null) {
			m_loadingDialog = new Dialog(context, R.style.LoadingDialog);
			ProgressBar pb = new ProgressBar(context);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			pb.setLayoutParams(params);

			WindowManager.LayoutParams lp = m_loadingDialog.getWindow()
					.getAttributes();
			lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
			lp.x = 50;
			lp.y = 150;
			m_loadingDialog.getWindow().setAttributes(lp);

			m_loadingDialog.addContentView(pb, params);
			m_loadingDialog.setCancelable(false);
			m_loadingDialog.setCanceledOnTouchOutside(false);
			// m_loadingDialog.getWindow().setGravity(Gravity.BOTTOM);
			m_loadingDialog.show();
		}
	}
}
