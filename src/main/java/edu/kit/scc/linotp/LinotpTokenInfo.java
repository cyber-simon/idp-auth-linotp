package edu.kit.scc.linotp;

public class LinotpTokenInfo {

	private long tokenId;
	private String serial;
	private String tokenType;
	private String tokenInfo;
	private String tokenDesc;
	private boolean isActive;
	
	public long getTokenId() {
		return tokenId;
	}
	
	public void setTokenId(long tokenId) {
		this.tokenId = tokenId;
	}
	
	public String getTokenType() {
		return tokenType;
	}
	
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	
	public String getTokenInfo() {
		return tokenInfo;
	}
	
	public void setTokenInfo(String tokenInfo) {
		this.tokenInfo = tokenInfo;
	}
	
	public String getTokenDesc() {
		return tokenDesc;
	}
	
	public void setTokenDesc(String tokenDesc) {
		this.tokenDesc = tokenDesc;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}
}
