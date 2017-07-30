
package com.jatin.producthunt.interfaces;

import android.content.Context;

import com.jatin.producthunt.adapters.CommentListAdapter;


public interface CommentsView {

	void initializeRecyclerView();

	void initializeToolBar();

	void setAdapterForRecyclerView(CommentListAdapter adapter);

	void goBack();

	void setToolbarTitle(String title);

	void showRefreshIndicator();

	void hideRefreshIndicator();

	void showEmptyView();

	void hideEmptyView();

	Context getContext();

}
