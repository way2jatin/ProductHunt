
package com.jatin.producthunt.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jatin.producthunt.R;
import com.jatin.producthunt.model.Comment;
import com.jatin.producthunt.model.User;
import com.jatin.producthunt.util.CircleTransform;
import com.jatin.producthunt.util.ViewUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private List<Comment> mComments;
	private Context mContext;

	private final int VIEW_COMMENT = 0;
	private final int VIEW_PROGRESS = 1;

	public CommentListAdapter(Context context, List<Comment> comments) {
		this.mContext = context;
		this.mComments = comments;
	}

	@Override
	public int getItemCount () {
		return mComments.size ();
	}

	@Override
	public int getItemViewType(int position) {
		return mComments.get(position) != null ? VIEW_COMMENT : VIEW_PROGRESS;
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup viewGroup, int viewType) {
		if (viewType == VIEW_COMMENT){
			View itemView = LayoutInflater.
					from (viewGroup.getContext ()).
					inflate (R.layout.item_comment, viewGroup, false);
			return new CommentsViewHolder(itemView);
		}
		else {
			View itemView = LayoutInflater.from(viewGroup.getContext())
					.inflate(R.layout.item_progress, viewGroup, false);

			return new ProgressViewHolder(itemView);
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof CommentsViewHolder){
			CommentsViewHolder viewHolder = (CommentsViewHolder) holder;
			Comment comment = mComments.get (position);
			LinearLayout.MarginLayoutParams params = (LinearLayout.MarginLayoutParams) viewHolder
					.commentLayout.getLayoutParams ();
			params.setMargins (comment.getLevel () * 30, 0, 0, 0);
			viewHolder.commentLayout.setLayoutParams (params);
			loadComment (viewHolder, position);
		}
	}

	private void loadComment (CommentsViewHolder holder, int position) {
		Comment comment = mComments.get (position);
		User user = comment.getUser ();
		holder.comment.setText (Html.fromHtml (comment.getBody ()));
		holder.name.setText (Html.fromHtml (user.getName ()) + " -  @"
				+ Html.fromHtml (user.getUsername ()));
		if (comment.isMaker ()) {
			holder.name.setTextColor (mContext.getResources ()
					.getColor (R.color.text_indicator_maker));
		} else {
			holder.name.setTextColor (mContext.getResources ().getColor
					(R.color.text_default));
		}
		if (user.getHeadline () != null) {
			holder.headline.setText (Html.fromHtml (user.getHeadline ()));
		}
		Picasso.with (mContext).load (user.getImageUrl ().getLargeImgUrl ())
				.resize (ViewUtils.dpToPx (56), ViewUtils.dpToPx (56))
				.centerCrop ()
				.transform (new CircleTransform())
				.into (holder.avatar);

		holder.imgShare.setOnClickListener(view -> shareOnWhatsapp(comment));
	}

	public boolean isProgressViewVisible() {
		return mComments.contains(null);
	}

	private void shareOnWhatsapp(Comment comment) {
		Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
		whatsappIntent.setType("text/plain");
		whatsappIntent.setPackage("com.whatsapp");
		whatsappIntent.putExtra(Intent.EXTRA_TEXT, comment.getBody());
		try {
			mContext.startActivity(whatsappIntent);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(mContext,"Whatsapp have not been installed.",Toast.LENGTH_LONG).show();
		}
	}


	public int getCharacterItemsCount() {
		if (isProgressViewVisible())
			return mComments.size() - 1;

		return mComments.size();
	}

	public static class CommentsViewHolder extends RecyclerView.ViewHolder {

		@InjectView (R.id.comment_text)
		TextView comment;
		@InjectView (R.id.comment_avatar)
		ImageView avatar;
		@InjectView (R.id.comment_layout)
		LinearLayout commentLayout;
		@InjectView (R.id.comment_name)
		TextView name;
		@InjectView (R.id.comment_headline)
		TextView headline;
		@InjectView(R.id.img_share)
		ImageView imgShare;

		public CommentsViewHolder (View itemView) {
			super (itemView);
			ButterKnife.inject (this, itemView);
		}
	}

	public class ProgressViewHolder extends RecyclerView.ViewHolder {
		public ProgressViewHolder(View itemView) {
			super(itemView);
		}
	}
}
