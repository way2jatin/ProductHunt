
package com.jatin.producthunt.model;

import java.util.List;

public class Comments {

	List<Comment> comments;

	public List<Comment> getComments () {
		return comments;
	}

	public void setComments (List<Comment> comments) {
		this.comments = comments;
	}

	public int getCount () {
		return comments.size ();
	}
}
