
package com.jatin.producthunt.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

	public static boolean hasInternetAccess (Context c) {
		ConnectivityManager cm =
				(ConnectivityManager) c.getSystemService (Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo ();
		return activeNetwork != null &&
				activeNetwork.isConnectedOrConnecting ();
	}
}
