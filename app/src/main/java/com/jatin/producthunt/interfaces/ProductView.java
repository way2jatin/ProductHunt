package com.jatin.producthunt.interfaces;

import android.content.Context;
import android.view.View;

import com.jatin.producthunt.adapters.ProductListAdapter;



public interface ProductView {

	void initializeRecyclerView();

	void setAdapterForRecyclerView(ProductListAdapter adapter);

	void showRefreshingIndicator();

	void hideRefreshingIndicator();

	void showEmptyView();

	void hideEmptyView();

	void showNoNetworkError();

	void showContextMenu (View v, int position);

	void hideContextMenu();

	void hideActivityTransition();

	void setToolbarTitle (String title);

	int getActivity();

	Context getContext();

	ProductListAdapter.OnProductClickListener getProductClickListener();
}
