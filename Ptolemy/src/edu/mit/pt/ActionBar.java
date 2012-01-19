package edu.mit.pt;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
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
		TextView titleView = (TextView) a.findViewById(R.id.homeTitle);
		titleView.setText(title);
	}
	
	static public void setBackAction(final Runnable r, Activity a) {
		Button back = (Button) a.findViewById(R.id.NavBackButton);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				r.run();
			}
		});
	}

}
