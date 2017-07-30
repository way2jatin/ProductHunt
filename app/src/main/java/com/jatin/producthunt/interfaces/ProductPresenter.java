
package com.jatin.producthunt.interfaces;

import android.os.Bundle;
import android.view.View;

import com.jatin.producthunt.model.Product;


public interface ProductPresenter {

	void onActivityCreated(Bundle savedInstanceState);

	void onSaveInstanceState(Bundle outState);

	void onDestroy();

	void onRefresh();

	void onDateSet(int year, int monthOfYear, int dayOfMonth);

	void onShareClick(int feedItem);

	void onImageClick(View v, int feedItem);

	void onCommentsClick(View v, Product product);
}
