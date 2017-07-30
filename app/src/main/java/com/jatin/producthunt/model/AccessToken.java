
package com.jatin.producthunt.model;

import com.google.gson.annotations.SerializedName;

public class AccessToken {

	@SerializedName ("access_token")
	private String accessToken;
	@SerializedName ("token_type")
	private String tokenType;
	@SerializedName ("expires_in")
	private long expiresIn;
	private String scope;

	public String getAccessToken () {
		return accessToken;
	}

	public void setAccessToken (String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType () {
		return tokenType;
	}

	public void setTokenType (String tokenType) {
		this.tokenType = tokenType;
	}

	public long getExpiresIn () {
		return expiresIn;
	}

	public void setExpiresIn (long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getScope () {
		return scope;
	}

	public void setScope (String scope) {
		this.scope = scope;
	}
}
