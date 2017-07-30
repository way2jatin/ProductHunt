
package com.jatin.producthunt.model;

import com.google.gson.annotations.SerializedName;

public class User {

	private int id;
	private String name;
	private String headline;
	private String username;
	@SerializedName ("image_url")
	private ImageUrl imageUrl;

	public class ImageUrl {
		@SerializedName ("48px")
		private String smallImgUrl;
		@SerializedName ("73px")
		private String largeImgUrl;

		public ImageUrl (String smallImgUrl, String largeImgUrl) {
			this.smallImgUrl = smallImgUrl;
			this.largeImgUrl = largeImgUrl;
		}

		public String getSmallImgUrl () {
			return smallImgUrl;
		}

		public void setSmallImgUrl (String smallImgUrl) {
			this.smallImgUrl = smallImgUrl;
		}

		public String getLargeImgUrl () {
			return largeImgUrl;
		}

		public void setLargeImgUrl (String largeImgUrl) {
			this.largeImgUrl = largeImgUrl;
		}
	}

	public int getId () {
		return id;
	}

	public void setId (int id) {
		this.id = id;
	}

	public String getName () {
		return name;
	}

	public void setName (String name) {
		this.name = name;
	}

	public String getHeadline () {
		return headline;
	}

	public void setHeadline (String headline) {
		this.headline = headline;
	}

	public String getUsername () {
		return username;
	}

	public void setUsername (String username) {
		this.username = username;
	}

	public ImageUrl getImageUrl () {
		return imageUrl;
	}

	public void setImageUrl (ImageUrl imageUrl) {
		this.imageUrl = imageUrl;
	}
}
