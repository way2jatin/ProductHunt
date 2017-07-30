
package com.jatin.producthunt.presenters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.jatin.producthunt.activity.CommentsActivity;
import com.jatin.producthunt.activity.WebActivity;
import com.jatin.producthunt.adapters.ProductListAdapter;
import com.jatin.producthunt.interfaces.ProductPresenter;
import com.jatin.producthunt.interfaces.ProductView;
import com.jatin.producthunt.model.Posts;
import com.jatin.producthunt.model.Product;
import com.jatin.producthunt.util.DateUtils;
import com.jatin.producthunt.util.NetworkUtils;
import com.jatin.producthunt.util.PHService;

import java.util.Calendar;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProductPresenterImpl implements ProductPresenter {

	public static final int ACTIVITY_MAIN = 1;

	private Subscription mSubscription;
	private ProductView mProductView;

	private PHService mPHService;
	private ProductListAdapter mAdapter;
	private String mDate;
	private Posts mPosts;
	private Observable<Posts> mPostsObservable;
	private Observer<Posts> mPostsObserver = new Observer<Posts> () {
		@Override
		public void onCompleted () {
			onCompleteNetworkRequest ();
		}

		@Override
		public void onError (Throwable e) {
			onNetworkError ();
		}

		@Override
		public void onNext (Posts posts) {
			showPosts (posts);
		}
	};

	private void onCompleteNetworkRequest () {
		mProductView.hideRefreshingIndicator ();
		if (mAdapter.getItemCount () == 0) {
			mProductView.showEmptyView ();
		}
	}

	public ProductPresenterImpl(ProductView productView) {
		this.mProductView = productView;
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		mProductView.initializeRecyclerView ();
		initializeAdapter ();

		if (!NetworkUtils.hasInternetAccess (mProductView.getContext ())) {
			mProductView.showEmptyView ();
			mProductView.showNoNetworkError ();
		} else {
			boolean isMainActivity = mProductView.getActivity () == ACTIVITY_MAIN;
			if (savedInstanceState == null) {
				mProductView.showRefreshingIndicator ();
				if (isMainActivity) {
					getPosts ();
				}
			} else {
				restoreInstanceState (savedInstanceState);
				getCache (isMainActivity);
			}
		}
	}

	private void initializeAdapter () {
		mPosts = new Posts ();
		mAdapter = new ProductListAdapter (mProductView.getContext (),
				mPosts.getPosts ());
		mAdapter.setOnProductClickListener (mProductView.getProductClickListener ());
		mProductView.setAdapterForRecyclerView (mAdapter);
	}

	private void getPosts () {
		mPHService = new PHService ();
		mPostsObservable = mPHService.getPosts (mDate)
				.subscribeOn (Schedulers.from (AsyncTask.THREAD_POOL_EXECUTOR))
				.observeOn (AndroidSchedulers.mainThread ());
		mSubscription = mPostsObservable.subscribe (mPostsObserver);
	}

	private void showPosts (Posts posts) {
		mPosts = posts;
		mAdapter = new ProductListAdapter (mProductView.getContext (),
				posts.getPosts ());
		mAdapter.setOnProductClickListener (mProductView.getProductClickListener ());
		mProductView.setAdapterForRecyclerView (mAdapter);
	}

	private void getCache (boolean isMainActivity) {
		if (mSubscription != null) {
			mSubscription.unsubscribe ();
			mSubscription = null;
		}
		if (isMainActivity) {
			if (mPostsObservable != null) {
				mProductView.showRefreshingIndicator ();
				mSubscription = mPostsObservable.subscribe (mPostsObserver);
			}
		}
	}

	private void onNetworkError () {
		mProductView.showNoNetworkError ();
		mProductView.hideRefreshingIndicator ();
		mProductView.showEmptyView ();
	}

	private void restoreInstanceState (Bundle savedInstanceState) {
		mPosts = Posts.getParcelable (savedInstanceState);
		mAdapter = new ProductListAdapter (mProductView.getContext (),
				mPosts.getPosts ());
		mAdapter.setOnProductClickListener (mProductView.getProductClickListener ());
		mProductView.setAdapterForRecyclerView (mAdapter);
	}

	@Override
	public void onSaveInstanceState (Bundle outState) {
		Posts.putParcelable (outState, mPosts);
	}

	@Override
	public void onDestroy () {
		if (mSubscription != null) {
			mSubscription.unsubscribe ();
			mSubscription = null;
		}
	}

	private void resetProductsIfExist () {
		if (!mPosts.isEmpty ()) {
			mPosts.clear ();
		}
	}

	@Override
	public void onRefresh () {
		mProductView.hideEmptyView ();
		mProductView.showRefreshingIndicator ();
		resetProductsIfExist ();
		getPosts ();
	}

	@Override
	public void onDateSet (int year, int monthOfYear, int dayOfMonth) {
		Calendar chosenCalendar = Calendar.getInstance ();
		chosenCalendar.set (Calendar.YEAR, year);
		chosenCalendar.set (Calendar.MONTH, monthOfYear);
		chosenCalendar.set (Calendar.DAY_OF_MONTH, dayOfMonth);
		mDate = DateUtils.getDateFormattedString (chosenCalendar);
		mProductView.setToolbarTitle (
				DateUtils.getMonth (monthOfYear) + " " + dayOfMonth);
		onRefresh ();
	}

	@Override
	public void onShareClick (int feedItem) {
		Product product = mPosts.getPosts ().get (feedItem);
		Intent share = new Intent (Intent.ACTION_SEND);
		share.setType ("text/plain");
		share.putExtra (Intent.EXTRA_SUBJECT, product.getName ());
		share.putExtra (Intent.EXTRA_TEXT, product.getProductUrl ());
		mProductView.getContext ().startActivity (
				Intent.createChooser (share, "Share " + "product"));
		mProductView.hideContextMenu ();
	}

	@Override
	public void onImageClick (View v, int feedItem) {
		Product product = mPosts.getPosts ().get (feedItem);
		Context context = mProductView.getContext ();

			Intent openUrl = new Intent (context, WebActivity.class);
			showExitAnimation (v, product, openUrl);

	}

	@Override
	public void onCommentsClick (View v, Product product) {
		Intent i = new Intent (mProductView.getContext (), CommentsActivity.class);
		showExitAnimation (v, product, i);
	}

	private void showExitAnimation (View v, Product product, Intent intent) {
		int[] startingLocation = new int[2];
		v.getLocationOnScreen (startingLocation);
		intent.putExtra (CommentsActivity.ARG_DRAWING_START_LOCATION,
				startingLocation[1]);
		intent.putExtra ("product", product);
		mProductView.getContext ().startActivity (intent);
		mProductView.hideActivityTransition ();
	}

}
