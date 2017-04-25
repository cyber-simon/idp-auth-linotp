/*******************************************************************************
 * Copyright 2017 Michael Simon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package edu.kit.scc.linotp;

import java.util.List;

import org.opensaml.messaging.context.BaseContext;

public class TokenContext extends BaseContext {

	private String token;
	private String error;
	private String transactionId;
	private String message;
	private String username;
	
	private List<LinotpTokenInfo> tokenList;
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<LinotpTokenInfo> getTokenList() {
		return tokenList;
	}

	public void setTokenList(List<LinotpTokenInfo> tokenList) {
		this.tokenList = tokenList;
	}
}
