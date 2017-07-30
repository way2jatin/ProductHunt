package com.jatin.producthunt.interfaces;

import android.os.Bundle;
import android.view.MenuItem;

public interface CommentPresenter {

	void onActivityCreated(Bundle savedInstanceState);

	void onSaveInstanceState(Bundle outState);

	void onDestroy();

	boolean onOptionsItemSelected(MenuItem item);

	void onBackPressed();
}
