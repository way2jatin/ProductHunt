
package com.jatin.producthunt.presenters;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;

import com.jatin.producthunt.adapters.CommentListAdapter;
import com.jatin.producthunt.interfaces.CommentPresenter;
import com.jatin.producthunt.interfaces.CommentsView;
import com.jatin.producthunt.model.Comment;
import com.jatin.producthunt.model.Product;
import com.jatin.producthunt.util.PHService;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommentPresenterImpl implements CommentPresenter {

	private CommentsView mCommentsView;
	private List<Comment> mComments = new ArrayList<> ();
	private CommentListAdapter mAdapter;
	private boolean mBackPressed = false;
	private Product mProduct;
	private Subscription mSubscription;

	public CommentPresenterImpl(CommentsView commentsView, Product product) {
		mCommentsView = commentsView;
		mProduct = product;
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		mCommentsView.initializeToolBar ();
		mCommentsView.setToolbarTitle (mProduct.getName ());
		mCommentsView.initializeRecyclerView ();
		initializeAdapter ();

		mCommentsView.showRefreshIndicator ();

		if (savedInstanceState == null) {
			getComments (mProduct.getId ());
		}
	}

	private void getComments (int productId) {
		PHService phService = new PHService ();
		mSubscription = phService.getComments (productId)
				.subscribeOn (Schedulers.from (AsyncTask.THREAD_POOL_EXECUTOR))
				.observeOn (AndroidSchedulers.mainThread ())
				.subscribe (comments -> {
					if (comments.getCount () == 0) {
						mCommentsView.showEmptyView ();
					} else {
						for (int i = 0; i < comments.getCount (); i++) {
							processComment (comments.getComments ().get (i), 0);
						}
						mAdapter = new CommentListAdapter (mCommentsView.getContext (),
								mComments);
						mCommentsView.setAdapterForRecyclerView (mAdapter);
						mCommentsView.hideRefreshIndicator ();
						if (mComments.isEmpty ()) {
							mCommentsView.showEmptyView ();
						}
					}
				});
	}

	private void processComment (Comment comment, int level) {
		comment.setLevel (level);
		mComments.add (comment);
		if (!comment.getChildComments ().isEmpty ()) {
			++level;
			for (int i = 0; i < comment.getChildCommentCount (); i++) {
				List<Comment> childComments = comment.getChildComments ();
				processComment (childComments.get (i), level);
			}
		}
	}

	private void initializeAdapter () {
		mComments = new ArrayList<> ();
		mAdapter = new CommentListAdapter (mCommentsView.getContext (),
				mComments);
		mCommentsView.setAdapterForRecyclerView (mAdapter);
	}

	@Override
	public void onSaveInstanceState (Bundle outState) {

	}

	@Override
	public void onDestroy () {
		if (mSubscription != null) {
			mSubscription.unsubscribe ();
			mSubscription = null;
		}
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		int itemId = item.getItemId ();

		if (itemId == android.R.id.home) {
			if (!mBackPressed) {
				goBack ();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed () {
		if (!mBackPressed) {
			goBack ();
		}
	}

	private void goBack () {
		mBackPressed = true;
		mCommentsView.goBack ();
	}
}

