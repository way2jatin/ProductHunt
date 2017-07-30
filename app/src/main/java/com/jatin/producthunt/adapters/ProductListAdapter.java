

package com.jatin.producthunt.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.jatin.producthunt.R;
import com.jatin.producthunt.model.Product;
import com.jatin.producthunt.util.ViewUtils;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>
		implements View.OnClickListener {

	private final static int ANIM_LIST_ENTER_DURATION = 700;
	private static final int ANIMATED_ITEMS_COUNT = 3;

	private List<Product> mProducts;
	private Context mContext;
	private OnProductClickListener mOnProductClickListener;
	private int lastAnimatedPosition = -1;

	public ProductListAdapter(Context context, List<Product> products) {
		this.mContext = context;
		this.mProducts = products;
	}

	@Override
	public int getItemCount () {
		return mProducts.size ();
	}

	@Override
	public void onBindViewHolder (final ProductViewHolder holder, int position) {
		runEnterAnimation (holder.itemView, position);
		holder.screenshotRipple.setTag (position);
		holder.viewComments.setTag (position);
		Picasso.with (mContext).load (R.drawable.ic_votes).into (holder.votesIcon);
		Picasso.with (mContext).load (R.drawable.ic_comment).into (holder.commentsIcon);
		loadCardText (holder);
		loadImage (holder);
	}

	private void loadCardText (ProductViewHolder holder) {
		Product product = mProducts.get (holder.getLayoutPosition ());
		holder.title.setText (product.getName ());
		holder.description.setText (product.getTagline ());
		holder.votes.setText (String.valueOf (product.getVotesCount ()));
		holder.comments.setText (String.valueOf (product.getCommentsCount ()));
	}

	private void loadImage (final ProductViewHolder holder) {
		String imgUrl = mProducts.get (holder.getLayoutPosition ())
				.getScreenshotUrl ().getSmallImgUrl ();
		holder.progressWheel.setVisibility (View.VISIBLE);
		holder.progressWheel.spin ();
		Picasso.with (mContext).load (imgUrl)
				.fit ()
				.centerCrop ()
				.into (holder.screenshot, new Callback () {
					@Override
					public void onSuccess () {
						holder.progressWheel.setVisibility (View.GONE);
						holder.progressWheel.stopSpinning ();
					}

					@Override
					public void onError () {
						// TODO: Error view
					}
				});
	}

	@Override
	public ProductViewHolder onCreateViewHolder (ViewGroup viewGroup, final int i) {
		View itemView = LayoutInflater.
				from (viewGroup.getContext ()).
				inflate (R.layout.item_product_card, viewGroup, false);

		ProductViewHolder holder = new ProductViewHolder (itemView);
		holder.screenshotRipple.setOnClickListener (this);
		holder.viewComments.setOnClickListener (this);
		return holder;
	}

	@Override
	public void onClick (View v) {
		if (v.getId () == R.id.card_product_image_ripple) {
			mOnProductClickListener.onImageClick (v, (Integer) v.getTag ());
		} else if (v.getId () == R.id.card_product_view_comments) {
			mOnProductClickListener.onCommentsClick (v, mProducts.get ((Integer) v.getTag ()));
		}
	}

	private void runEnterAnimation (View view, int position) {
		if (position >= ANIMATED_ITEMS_COUNT - 1) {
			return;
		}
		if (position > lastAnimatedPosition) {
			lastAnimatedPosition = position;
			view.setTranslationY (ViewUtils.getScreenHeight (mContext));
			view.animate ()
					.translationY (0)
					.setInterpolator (new DecelerateInterpolator(3.f))
					.setDuration (ANIM_LIST_ENTER_DURATION)
					.start ();
		}
	}

	public void setOnProductClickListener (OnProductClickListener onProductClickListener) {
		this.mOnProductClickListener = onProductClickListener;
	}

	public static class ProductViewHolder extends RecyclerView.ViewHolder {

		@InjectView (R.id.card_product_title)
		RobotoTextView title;
		@InjectView (R.id.card_product_description)
        TextView description;
		@InjectView (R.id.card_product_image)
        ImageView screenshot;
		@InjectView (R.id.card_product_image_ripple)
        View screenshotRipple;
		@InjectView (R.id.card_product_upvotes)
        TextView votes;
		@InjectView (R.id.card_product_comments_total)
        TextView comments;
		@InjectView (R.id.card_product_progress_wheel)
		ProgressWheel progressWheel;
		@InjectView (R.id.card_product_view_comments)
		RobotoTextView viewComments;
		@InjectView (R.id.card_product_comments_icon)
        ImageView commentsIcon;
		@InjectView (R.id.card_product_votes_icon)
        ImageView votesIcon;

		public ProductViewHolder (View view) {
			super (view);
			ButterKnife.inject (this, view);
		}
	}

	public interface OnProductClickListener {
		void onImageClick (View v, int position);

		void onCommentsClick (View v, Product product);
	}
}
