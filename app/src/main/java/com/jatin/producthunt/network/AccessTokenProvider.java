
package com.jatin.producthunt.network;


import com.jatin.producthunt.model.AccessToken;

import retrofit.RequestInterceptor;

public class AccessTokenProvider implements RequestInterceptor {
	private static String sessionToken = "";

	public AccessTokenProvider () {

	}

	public void setSessionToken (AccessToken token) {
		sessionToken = token.getAccessToken ();
	}

	public void resetSessionToken () {
		sessionToken = "";
	}

	@Override
	public void intercept (RequestFacade requestFacade) {
		requestFacade.addHeader ("Accept", "application/json");
		requestFacade.addHeader ("Authorization", "Bearer " + sessionToken);
	}
}
