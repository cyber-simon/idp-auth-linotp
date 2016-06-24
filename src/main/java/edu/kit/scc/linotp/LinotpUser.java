package edu.kit.scc.linotp;

public class LinotpUser {

	private String userId;
	private String userName;
	private String surName;
	private String givenName;
	private String email;
	private String userIdResolver;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSurName() {
		return surName;
	}

	public void setSurName(String surName) {
		this.surName = surName;
	}
	
	public String getGivenName() {
		return givenName;
	}
	
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getUserIdResolver() {
		return userIdResolver;
	}
	
	public void setUserIdResolver(String userIdResolver) {
		this.userIdResolver = userIdResolver;
	}
}
