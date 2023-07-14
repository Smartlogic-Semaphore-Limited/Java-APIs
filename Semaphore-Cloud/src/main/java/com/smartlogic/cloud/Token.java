package com.smartlogic.cloud;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {

	// The minimum time remaining on a token for it to be considered valid
	public static long safetyMarginS = 3600;
	
	private String access_token;
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		if (access_token.startsWith("bearer ")) {
			this.access_token = access_token;
		} else {
			this.access_token = "bearer " + access_token;
		}
	}

	private String token_type;
	public String getToken_type() {
		return token_type;
	}
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	private Date expiryDate;
	private int expires_in;
	public int getExpires_in() {
		return expires_in;
	}
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpires_in(int expires_in) {
		expiryDate = new Date((new Date()).getTime() + 1000*(expires_in - safetyMarginS));
		this.expires_in = expires_in;
	}
	
	public boolean isExpired() {
		return expiryDate.before(new Date());
	}

	private String userName;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	private String issued;
	public String getIssued() {
		return issued;
	}
	@JsonProperty(".issued")
	public void setIssued(String issued) {
		this.issued = issued;
	}

	private String expires;
	public String getExpires() {
		return expires;
	}
	@JsonProperty(".expires")
	public void setExpires(String expires) {
		this.expires = expires;
	}

	private String statusCode;
	public String getStatusCode() {
		return statusCode;
	}
	@JsonProperty("statusCode")
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
}
