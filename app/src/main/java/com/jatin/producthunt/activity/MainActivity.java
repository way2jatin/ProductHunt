package com.jatin.producthunt.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jatin.producthunt.R;
import com.jatin.producthunt.adapters.FeedContextMenuManager;
import com.jatin.producthunt.adapters.ProductListAdapter;
import com.jatin.producthunt.interfaces.ProductView;
import com.jatin.producthunt.model.Product;
import com.jatin.producthunt.presenters.ProductPresenterImpl;
import com.jatin.producthunt.util.FeedContextMenu;
import com.jatin.producthunt.util.ViewUtils;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity
        implements ProductListAdapter.OnProductClickListener,
        DatePickerDialog.OnDateSetListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener,
        ProductView {

    private final static int ANIM_TOOLBAR_INTRO_DURATION = 350;

    private Handler mHandler = new Handler();
    private Boolean mIsRefreshing = false;
    private Boolean mStartIntroAnimation = true;
    private ProductPresenterImpl mPresenter;

    @InjectView(android.R.id.list)
    RecyclerView mRecyclerView;
    @InjectView(R.id.list_progress_wheel)
    ProgressWheel mProgressWheel;
    @InjectView(R.id.products_empty_view)
    LinearLayout mEmptyView;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        boolean toolbarAnimation = getIntent().getBooleanExtra
                ("toolbar_animation", true);
        mStartIntroAnimation = (savedInstanceState == null) && toolbarAnimation;
        mProgressWheel.setBarColor(getResources().getColor(R.color.primary_accent));

        mPresenter = new ProductPresenterImpl(this);
        mPresenter.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int day) {
        mPresenter.onDateSet(year, month, day);
    }

    @Override
    public void onShareClick(int feedItem) {
        mPresenter.onShareClick(feedItem);
    }

    @Override
    public void onCancelClick(int feedItem) {
        hideContextMenu();
    }

    @Override
    public void onImageClick(View v, int position) {
        mPresenter.onImageClick(v, position);
    }

    @Override
    public void onCommentsClick(View v, Product product) {
        mPresenter.onCommentsClick(v, product);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        if (mStartIntroAnimation) {
            setToolbarIntroAnimation();
            mStartIntroAnimation = false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_main_refresh) {
            mPresenter.onRefresh();
            return true;
        }
        if (itemId == R.id.menu_calendar) {
            showDatePickerDialog();
            return true;
        }
        return false;
    }

    private void setToolbarIntroAnimation() {
        int toolBarSize = ViewUtils.dpToPx(56);
        getToolbar().setTranslationY(-toolBarSize);
        getToolbar().animate()
                .translationY(0)
                .setDuration(ANIM_TOOLBAR_INTRO_DURATION)
                .setStartDelay(300);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = DatePickerDialog.newInstance(
                MainActivity.this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.setMaxDate(calendar);
        dialog.show(getFragmentManager(), "Datepickerdialog");
    }

    private final Runnable refreshingContent = new Runnable() {
        public void run() {
            try {
                if (mIsRefreshing) {
                    mHandler.postDelayed(this, 500);
                } else {
                    mProgressWheel.stopSpinning();
                    mProgressWheel.setVisibility(View.GONE);
                }
            } catch (Exception error) {
//                Crashlytics.logException (error);
            }
        }
    };

    @Override
    public void initializeRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void setAdapterForRecyclerView(ProductListAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showRefreshingIndicator() {
        mProgressWheel.setVisibility(View.VISIBLE);
        mProgressWheel.spin();
        mHandler.post(refreshingContent);
        mIsRefreshing = true;
    }

    @Override
    public void hideRefreshingIndicator() {
        mIsRefreshing = false;
    }

    @Override
    public void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyView() {
        mEmptyView.setVisibility(View.GONE);
    }

    @Override
    public void showNoNetworkError() {
        SnackbarManager.show(
                Snackbar.with(getApplicationContext())
                        .text(getString(R.string.error_connection))
                        .actionLabel(getString(R.string.retry))
                        .actionColor(getResources().getColor(R.color.primary_color))
                        .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                        .actionListener(snackbar -> mPresenter.onRefresh()), this);
    }

    @Override
    public void showContextMenu(View v, int position) {
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v,
                position, this, true);
    }

    @Override
    public void hideContextMenu() {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void hideActivityTransition() {
        overridePendingTransition(0, 0);
    }

    @Override
    public void setToolbarTitle(String title) {
        setActionBarTitle(title);
    }

    @Override
    public int getActivity() {
        return ProductPresenterImpl.ACTIVITY_MAIN;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public ProductListAdapter.OnProductClickListener getProductClickListener() {
        return this;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }
}

