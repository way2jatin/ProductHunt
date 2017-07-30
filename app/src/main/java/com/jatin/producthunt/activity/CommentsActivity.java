
package com.jatin.producthunt.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.jatin.producthunt.R;
import com.jatin.producthunt.adapters.CommentListAdapter;
import com.jatin.producthunt.interfaces.CommentsView;
import com.jatin.producthunt.model.Product;
import com.jatin.producthunt.presenters.CommentPresenterImpl;
import com.jatin.producthunt.util.ViewUtils;
import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class CommentsActivity extends AppCompatActivity implements CommentsView {

	public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
	private static final int ANIM_LAYOUT_INTRO_DURATION = 250;

	@InjectView (R.id.toolbar)
    Toolbar mToolBar;
	@InjectView (android.R.id.list)
    RecyclerView mRecyclerView;
	@InjectView (R.id.comments_layout)
	FrameLayout mCommentsLayout;
	@InjectView (R.id.comments_list_progress_wheel)
    ProgressWheel mListProgressWheel;
	@InjectView (R.id.comments_empty_view)
	LinearLayout mEmptyView;

	private int mDrawingStartLocation;
	private CommentPresenterImpl mPresenter;
	private ActionBar mActionBar;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_comments);
		ButterKnife.inject (this);
		expandAnimation (savedInstanceState);
		Product product = getIntent ().getParcelableExtra ("product");
		mPresenter = new CommentPresenterImpl(this, product);
		mPresenter.onActivityCreated (savedInstanceState);

	}

	@Override
	public void onDestroy () {
		mPresenter.onDestroy ();
		super.onDestroy ();
	}

	private LinearLayoutManager getLayoutManager () {
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation (LinearLayoutManager.VERTICAL);
		return layoutManager;
	}

	private void expandAnimation (Bundle savedInstanceState) {
		mDrawingStartLocation = getIntent ().getIntExtra (ARG_DRAWING_START_LOCATION, 0);
		if (savedInstanceState == null) {
			mCommentsLayout.getViewTreeObserver ().addOnPreDrawListener (new ViewTreeObserver
					.OnPreDrawListener () {
				@Override
				public boolean onPreDraw () {
					mCommentsLayout.getViewTreeObserver ().removeOnPreDrawListener (this);
					startIntroAnimation ();
					return true;
				}
			});
		}
	}

	private void startIntroAnimation () {
		mCommentsLayout.setScaleY (0.1f);
		mCommentsLayout.setPivotY (mDrawingStartLocation);
		mCommentsLayout.animate ()
				.scaleY (1)
				.setDuration (ANIM_LAYOUT_INTRO_DURATION)
				.setInterpolator (new AccelerateInterpolator ())
				.start ();
	}

	@Override
	public void onBackPressed () {
		mPresenter.onBackPressed ();
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		return mPresenter.onOptionsItemSelected (item);
	}

	@Override
	public void initializeRecyclerView () {
		mRecyclerView.setHasFixedSize (true);
		mRecyclerView.setItemAnimator (new DefaultItemAnimator());
		mRecyclerView.setLayoutManager (getLayoutManager ());
	}

	@Override
	public void initializeToolBar () {
		setSupportActionBar (mToolBar);
		mActionBar = getSupportActionBar ();
		if (mActionBar != null) {
			mActionBar.setElevation (5);
			mActionBar.setDisplayHomeAsUpEnabled (true);
		}
	}

	@Override
	public void setAdapterForRecyclerView (CommentListAdapter adapter) {
		mRecyclerView.setAdapter (adapter);
	}

	@Override
	public void goBack () {
		mCommentsLayout.animate ()
				.translationY (ViewUtils.getScreenHeight (this))
				.setDuration (200)
				.setListener (new AnimatorListenerAdapter () {
					@Override
					public void onAnimationEnd (Animator animation) {
						CommentsActivity.super.onBackPressed ();
						overridePendingTransition (0, 0);
					}
				})
				.start ();
	}

	@Override
	public void setToolbarTitle (String title) {
		mActionBar.setTitle (title);
	}

	@Override
	public void showRefreshIndicator () {
		mListProgressWheel.setVisibility (View.VISIBLE);
		mListProgressWheel.spin ();
		mListProgressWheel.setBarColor (getResources ().getColor (R.color.primary_accent));
	}

	@Override
	public void hideRefreshIndicator () {
		mListProgressWheel.setVisibility (View.GONE);
		mListProgressWheel.stopSpinning ();
	}

	@Override
	public void showEmptyView () {
		mEmptyView.setVisibility (View.VISIBLE);
	}

	@Override
	public void hideEmptyView () {
		mEmptyView.setVisibility (View.GONE);
	}

	@Override
	public Context getContext () {
		return this;
	}
}
