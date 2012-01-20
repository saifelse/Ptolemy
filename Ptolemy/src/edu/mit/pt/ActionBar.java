package edu.mit.pt;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActionBar extends RelativeLayout {

	private static final float BUTTON_WIDTH_DP = 48.0f;
	// Arbitrary offset, just don't make it 0 because id=0=NO_ID.
	private static final int BASE_ID = 100;

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
	
	/**
	 * Helper function to configure back button to go back to last activity.
	 */
	static public void setDefaultBackAction(final Activity a) {
		setBackAction(a, new Runnable() {
			@Override
			public void run() {
				a.finish();
			}
		});
	}

	/**
	 * Configures back button to run r when pressed.
	 * @param a Activity.
	 * @param r Runnable to run.
	 */
	static public void setBackAction(Activity a, final Runnable r) {
		a.findViewById(R.id.navBackButton).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						r.run();
					}
				});
	}
	
	static public void setButton(Activity a, View button) {
		setButtons(a, new View[] { button });
	}

	static public void setButtons(Activity a, View[] buttons) {
		ActionBar actionBar = (ActionBar) a.findViewById(R.id.nav);
		int width = Config.getPixelsFromDp(a, BUTTON_WIDTH_DP);

		for (int i = 0; i < buttons.length; i++) {
			RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(
					width, LayoutParams.FILL_PARENT);
			View b = buttons[i];
			b.setId(BASE_ID + i);
			if (i == 0) {
				layout.addRule(ALIGN_PARENT_RIGHT);
			} else {
				layout.addRule(LEFT_OF, BASE_ID + i - 1);
			}
			actionBar.addView(b, layout);
		}
	}

}
