
package com.jatin.producthunt.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Posts implements Parcelable {

	public static final String TAG = Collections.class.getSimpleName ();

	public static final String PARCELABLE_KEY = TAG + ":" + "ParcelableKey";

	private List<Product> posts;

	public Posts() {
		posts = new ArrayList<> ();
	}

	public Posts(Parcel in) {
		posts = new ArrayList<> ();
		in.readList (posts, getClass ().getClassLoader ());
	}

	public List<Product> getPosts () {
		return posts;
	}

	public void setPosts (List<Product> posts) {
		this.posts = posts;
	}

	public boolean isEmpty () {
		return posts.isEmpty ();
	}

	public void clear () {
		posts.clear ();
	}

	@Override
	public int describeContents () {
		return 0;
	}

	public static Posts getParcelable (Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			return savedInstanceState.getParcelable (PARCELABLE_KEY);
		} else {
			throw new IllegalArgumentException (TAG + ": \'getParcelable\' Method has null argument: savedInstanceState.");
		}
	}

	public static void putParcelable (Bundle savedInstanceState, Posts posts) {
		if (savedInstanceState != null && posts != null) {
			savedInstanceState.putParcelable (PARCELABLE_KEY, posts);
		}
	}

	@Override
	public void writeToParcel (Parcel parcel, int i) {
		parcel.writeList (posts);
	}

	public static Creator<Posts> getCREATOR () {
		return CREATOR;
	}

	public static final Creator<Posts> CREATOR = new Parcelable.Creator<Posts> () {
		public Posts createFromParcel (Parcel in) {
			return new Posts (in);
		}

		public Posts[] newArray (int size) {
			return new Posts[size];
		}
	};
}
