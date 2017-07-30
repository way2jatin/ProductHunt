
package com.jatin.producthunt.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class ViewUtils {

	private static int screenHeight = 0;

	public static int dpToPx (int dp) {
		return (int) (dp * Resources.getSystem ().getDisplayMetrics ().density);
	}

	public static int getScreenHeight (Context c) {
		if (screenHeight == 0) {
			WindowManager wm = (WindowManager) c.getSystemService (Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay ();
			Point size = new Point ();
			display.getSize (size);
			screenHeight = size.y;
		}

		return screenHeight;
	}
}
