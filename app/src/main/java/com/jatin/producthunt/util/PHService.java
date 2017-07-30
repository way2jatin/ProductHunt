
package com.jatin.producthunt.util;

import com.jatin.producthunt.model.Comments;
import com.jatin.producthunt.model.Posts;
import com.jatin.producthunt.network.AccessTokenProvider;
import com.jatin.producthunt.network.PHApi;
import com.jatin.producthunt.network.RetryWithSessionRefresh;
import com.jatin.producthunt.network.ServiceGenerator;
import com.jatin.producthunt.network.SessionService;

import rx.Observable;

public class PHService {

	private static final SessionService sessionService = new SessionService ();
	private static final AccessTokenProvider accessTokenProvider = new AccessTokenProvider ();

	public PHService() {
	}

	public Observable<Posts> getPosts (String date) {
		PHApi api = ServiceGenerator.createService (PHApi.class, Constants.API_URL);
		if (date == null) {
			return api.getPosts ().retryWhen (new RetryWithSessionRefresh
					(sessionService, accessTokenProvider));
		} else {
			return api.getPostsByDate (date).retryWhen (new RetryWithSessionRefresh
					(sessionService, accessTokenProvider));
		}
	}

	public Observable<Comments> getComments (int productId) {
		PHApi api = ServiceGenerator.createService (PHApi.class, Constants.API_URL);
		return api.getComments (productId).retryWhen (new RetryWithSessionRefresh
				(sessionService, accessTokenProvider));
	}

}
