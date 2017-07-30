
package com.jatin.producthunt.network;


import com.jatin.producthunt.model.AccessToken;
import com.jatin.producthunt.model.Authentication;
import com.jatin.producthunt.model.Comments;
import com.jatin.producthunt.model.Posts;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface PHApi {

	@POST ("/oauth/token")
	Observable<AccessToken> getAccessToken(@Body Authentication auth);

	@GET ("/posts")
	Observable<Posts> getPosts();

	@GET ("/posts")
	Observable<Posts> getPostsByDate(@Query("day") String date);

	@GET ("/posts/{product-id}/comments?per_page=5")
	Observable<Comments> getComments(@Path("product-id") int productId);
}
