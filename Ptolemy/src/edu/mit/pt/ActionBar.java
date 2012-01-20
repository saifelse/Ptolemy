package edu.mit.pt;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActionBar extends RelativeLayout {

	public ActionBar(Context context) {
		super(context);
	}

	public ActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ActionBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	static public void setTitle(Activity a, String title) {
		TextView titleView = (TextView) a.findViewById(R.id.navTitle);
		titleView.setText(title);
	}

	static public void setBackAction(Activity a, final Runnable r) {
		a.findViewById(R.id.navBackButton).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						r.run();
					}
				});
	}

	static public void setButtons(Activity a, View[] buttons) {
		ActionBar actionBar = (ActionBar) a.findViewById(R.id.nav);
		for (int i = 0; i < buttons.length; i++) {
			RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			actionBar.addView(buttons[i], layout);
		}
	}

}
