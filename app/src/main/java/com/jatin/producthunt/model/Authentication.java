
package com.jatin.producthunt.model;

import com.google.gson.annotations.SerializedName;

public class Authentication {

	@SerializedName ("client_id")
	private String clientId;
	@SerializedName ("client_secret")
	private String clientSecret;
	@SerializedName ("grant_type")
	private String grantType;

	public Authentication(String clientId, String clientSecret, String grantType) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.grantType = grantType;
	}

	public String getGrantType () {
		return grantType;
	}

	public void setGrantType (String grantType) {
		this.grantType = grantType;
	}

	public String getClientId () {
		return clientId;
	}

	public void setClientId (String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret () {
		return clientSecret;
	}

	public void setClientSecret (String clientSecret) {
		this.clientSecret = clientSecret;
	}
}