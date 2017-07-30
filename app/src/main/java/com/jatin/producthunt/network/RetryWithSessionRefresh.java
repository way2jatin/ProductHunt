
package com.jatin.producthunt.network;

import android.os.AsyncTask;

import org.apache.http.HttpStatus;

import retrofit.RetrofitError;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RetryWithSessionRefresh implements
		Func1<Observable<? extends Throwable>, Observable<?>> {

	private final SessionService sessionSerivce;
	private final AccessTokenProvider accessTokenProvider;

	public RetryWithSessionRefresh(SessionService sessionSerivce,
                                   AccessTokenProvider accessTokenProvider) {
		this.sessionSerivce = sessionSerivce;
		this.accessTokenProvider = accessTokenProvider;
	}

	@Override
	public Observable<?> call (Observable<? extends Throwable> observable) {
		return observable
				.flatMap (new Func1<Throwable, Observable<?>> () {
					int retryCount = 0;

					@Override
					public Observable<?> call (Throwable throwable) {
						retryCount++;
						// Retry once
						if (retryCount <= 1 && throwable instanceof RetrofitError) {
							final RetrofitError retrofitError = (RetrofitError) throwable;
							if (!retrofitError.isNetworkError ()
									&& retrofitError.getResponse ().getStatus () == HttpStatus.SC_UNAUTHORIZED) {
								return sessionSerivce
										.askForToken ()
										.doOnNext (accessTokenProvider::setSessionToken)
										.doOnError (throwable1 -> accessTokenProvider.resetSessionToken ())
										.observeOn (Schedulers.from (AsyncTask.THREAD_POOL_EXECUTOR))
										.subscribeOn (Schedulers.from (AsyncTask.THREAD_POOL_EXECUTOR));
							}
						}
						// No more retries, pass through error
						return Observable.error (throwable);
					}
				});
	}
}
