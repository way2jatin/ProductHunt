
package com.jatin.producthunt.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;

import com.jatin.producthunt.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class BaseActivity extends AppCompatActivity {

	@InjectView (R.id.toolbar)
    Toolbar mToolBar;

	@Override
	public void setContentView (int layoutResID) {
		super.setContentView (layoutResID);
		ButterKnife.inject (this);
		setSupportActionBar(getToolbar());
		setActionBarTitle("Today\'s Products");
	}

	@Override
	public void onDestroy () {
//		PicassoTools.clearCache (Picasso.with (this));
		super.onDestroy();
	}

	public Toolbar getToolbar () {
		return mToolBar;
	}

	protected void setActionBarTitle (String title) {
		ActionBar actionBar = getSupportActionBar ();
		if (actionBar != null) {
			actionBar.setTitle (title);
		}
		else {
			actionBar.setTitle("Products");
		}
	}

	protected LinearLayoutManager getLayoutManager () {
		LinearLayoutManager layoutManager = new LinearLayoutManager (this);
		layoutManager.setOrientation (LinearLayoutManager.VERTICAL);
		return layoutManager;
	}
}