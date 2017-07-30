
package com.jatin.producthunt.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.jatin.producthunt.R;
import com.jatin.producthunt.model.Product;
import com.jatin.producthunt.util.ViewUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WebActivity extends AppCompatActivity {

	public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
	private static final int ANIM_LAYOUT_INTRO_DURATION = 250;
	private static final String URL_PLAY_STORE_SCHEME = "https://play.google" +
			".com/store/apps/details?";
	private static final String URL_DEVICE_PLAY_STORE_SCHEME = "market://details?";

	@InjectView (R.id.toolbar)
    Toolbar mToolBar;
	@InjectView (R.id.web_webview)
	WebView mWebView;

	private int mDrawingStartLocation;
	private boolean mBackPressed = false;
	private Product mProduct;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_web);
		ButterKnife.inject (this);

		mProduct = getIntent ().getParcelableExtra ("product");

		expandAnimation (savedInstanceState);
		setupToolbar ();
		setupWebView ();
	}

	@Override
	public void onDestroy () {
		super.onDestroy ();
	}

	private void openInBrowser () {
		Intent intent = new Intent (Intent.ACTION_VIEW).setData (Uri
				.parse (mProduct.getProductUrl ()));
		startActivity (intent);
	}

	private Intent getShareIntent () {
		Intent i = new Intent (Intent.ACTION_SEND);
		i.setType ("text/plain");
		i.putExtra (Intent.EXTRA_SUBJECT, mProduct.getName ());
		i.putExtra (Intent.EXTRA_TEXT, mProduct.getProductUrl ());
		return i;
	}

	private void goBack () {
		mBackPressed = true;
		mWebView.animate ()
				.translationY (ViewUtils.getScreenHeight (this))
				.setDuration (200)
				.setListener (new AnimatorListenerAdapter () {
					@Override
					public void onAnimationEnd (Animator animation) {
						WebActivity.super.onBackPressed ();
						overridePendingTransition (0, 0);
					}
				})
				.start ();
	}

	private void setupToolbar () {
		setSupportActionBar (mToolBar);
		ActionBar actionBar = getSupportActionBar ();
		actionBar.setDisplayHomeAsUpEnabled (true);
		actionBar.setTitle (getString (R.string.actionbar_loading));
		actionBar.setElevation (5);
	}

	private void setupWebView () {
		mWebView.setWebViewClient (new WebViewClient () {
			@Override
			public void onLoadResource (WebView view, String url) {
				if (isPlayStoreLink (url)) {
					redirectToPlayStore (url);
					view.stopLoading ();
				}
			}

			@Override
			public void onPageFinished (WebView view, String url) {
				getSupportActionBar ().setTitle (mProduct.getName ());
				if (isPlayStoreLink (url)) {
					redirectToPlayStore (url);
				}
			}

			@Override
			public void onReceivedSslError (WebView view, SslErrorHandler handler,
											SslError error) {
				Toast.makeText (WebActivity.this, getString (R.string.error_ssl),
						Toast.LENGTH_LONG).show ();
			}
		});
		mWebView.loadUrl (mProduct.getProductUrl ());
		mWebView.getSettings ().setBuiltInZoomControls (true);
		mWebView.getSettings ().setDisplayZoomControls (false);
		WebSettings webSettings = mWebView.getSettings ();
		webSettings.setCacheMode (WebSettings.LOAD_NO_CACHE);
		webSettings.setJavaScriptEnabled (true);
	}

	private void redirectToPlayStore (String url) {
		String link = url.replace (URL_PLAY_STORE_SCHEME, URL_DEVICE_PLAY_STORE_SCHEME);
		Intent intent = new Intent (Intent.ACTION_VIEW).setData (Uri
				.parse (link));
		startActivity (intent);
	}

	private boolean isPlayStoreLink (String url) {
		return url.contains (URL_PLAY_STORE_SCHEME);
	}

	private void expandAnimation (Bundle savedInstanceState) {
		mDrawingStartLocation = getIntent ().getIntExtra (ARG_DRAWING_START_LOCATION, 0);
		if (savedInstanceState == null) {
			mWebView.getViewTreeObserver ().addOnPreDrawListener (new ViewTreeObserver
					.OnPreDrawListener () {
				@Override
				public boolean onPreDraw () {
					mWebView.getViewTreeObserver ().removeOnPreDrawListener (this);
					startIntroAnimation ();
					return true;
				}
			});
		}
	}

	private void startIntroAnimation () {
		mWebView.setScaleY (0.1f);
		mWebView.setPivotY (mDrawingStartLocation);
		mWebView.animate ()
				.scaleY (1)
				.setDuration (ANIM_LAYOUT_INTRO_DURATION)
				.setInterpolator (new AccelerateInterpolator ())
				.start ();
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		getMenuInflater ().inflate (R.menu.web_menu, menu);
		MenuItem item = menu.findItem (R.id.menu_web_share);
		ShareActionProvider shareActionProvider = new ShareActionProvider(this);
		shareActionProvider.setShareIntent (getShareIntent ());
		MenuItemCompat.setActionProvider (item, shareActionProvider);
		return true;
	}

	@Override
	public void onBackPressed () {
		if (!mBackPressed) {
			goBack ();
		}
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		int itemId = item.getItemId ();
		switch (itemId) {
			case android.R.id.home:
				if (!mBackPressed) {
					goBack ();
				}
				return true;
			case R.id.menu_browser:
				openInBrowser ();
				return true;
			default:
				return false;
		}
	}
}
