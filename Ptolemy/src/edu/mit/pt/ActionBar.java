package edu.mit.pt;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
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
	
	static public void setTitle(String title, Activity a) {
		TextView titleView = (TextView) a.findViewById(R.id.navTitle);
		titleView.setText(title);
	}

}
