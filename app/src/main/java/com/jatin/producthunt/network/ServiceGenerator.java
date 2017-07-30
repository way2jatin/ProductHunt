
package com.jatin.producthunt.network;

import retrofit.RestAdapter;

public class ServiceGenerator {

	private ServiceGenerator() {
	}

	public static <S> S createService (Class<S> serviceClass,
	                                   String baseUrl) {
		AccessTokenProvider accessTokenProvider = new AccessTokenProvider ();
		RestAdapter restAdapter = new RestAdapter.Builder ()
				.setEndpoint (baseUrl)
				.setRequestInterceptor (accessTokenProvider)
				.build ();
		return restAdapter.create (serviceClass);
	}

}