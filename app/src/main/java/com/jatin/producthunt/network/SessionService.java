
package com.jatin.producthunt.network;


import com.jatin.producthunt.model.AccessToken;
import com.jatin.producthunt.model.Authentication;
import com.jatin.producthunt.util.Constants;

import retrofit.RestAdapter;
import rx.Observable;

public class SessionService {

	public static final Authentication authentication = new Authentication (
			Constants.CLIENT_ID, Constants.CLIENT_SECRET, Constants.GRANT_TYPE);

	public Observable<AccessToken> askForToken () {
		PHApi api = new RestAdapter.Builder ()
				.setEndpoint (Constants.API_URL)
				.build ()
				.create (PHApi.class);
		return api.getAccessToken (authentication);
	}
}
