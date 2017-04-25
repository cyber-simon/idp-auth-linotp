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

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinotpTokenInfoDecoder {

	private static Logger logger = LoggerFactory.getLogger(LinotpTokenInfoDecoder.class);
	
	public LinotpTokenInfo decode(JsonObject object) {
		LinotpTokenInfo token = new LinotpTokenInfo();
		token.setTokenId(JsonHelper.getLongOrNull(object, "LinOtp.TokenId"));
		token.setTokenType(JsonHelper.getStringOrNull(object, "LinOtp.TokenType"));
		token.setTokenInfo(JsonHelper.getStringOrNull(object, "LinOtp.TokenInfo"));
		token.setTokenDesc(JsonHelper.getStringOrNull(object, "LinOtp.TokenDesc"));
		token.setSerial(JsonHelper.getStringOrNull(object, "LinOtp.TokenSerialnumber"));
		token.setActive(JsonHelper.getBooleanOrNull(object, "LinOtp.Isactive"));
		token.setFailCount(JsonHelper.getLongOrNull(object, "LinOtp.FailCount"));
		token.setMaxFailCount(JsonHelper.getLongOrNull(object, "LinOtp.MaxFail"));
		
		return token;
	}
	
	public List<LinotpTokenInfo> decodeTokeList(JsonObject object) {
        JsonObject result = object.getJsonObject("result");
        
        Boolean status = result.getBoolean("status", false);
	    
        if (logger.isDebugEnabled())
	    	logger.debug("LinOTP Session status {} and value {}", status);

    	List<LinotpTokenInfo> tokenList = new ArrayList<LinotpTokenInfo>();

    	if (status && result.containsKey("value") && result.getJsonObject("value").containsKey("data")) {
        	
        	JsonArray data = result.getJsonObject("value").getJsonArray("data");
        	
        	for (int i=0; i<data.size(); i++) {
    	        if (logger.isDebugEnabled())
    		    	logger.debug("LinOTP processing token {}", i);

    	        JsonObject jo = data.getJsonObject(i);
        		
    	        LinotpTokenInfo token = decode(jo);
        		
        		tokenList.add(token);
        	}
        	
        }
    	
    	return tokenList;
	}
}
