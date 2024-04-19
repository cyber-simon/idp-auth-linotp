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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

public class LinotpUserDecoder {

	private static Logger logger = LoggerFactory.getLogger(LinotpUserDecoder.class);
	
	public LinotpUser decode(JsonObject object) {
		LinotpUser user = new LinotpUser();
		user.setUserId(JsonHelper.getStringOrNull(object, "userid"));
		user.setUserName(JsonHelper.getStringOrNull(object, "username"));
		user.setSurName(JsonHelper.getStringOrNull(object, "surname"));
		user.setGivenName(JsonHelper.getStringOrNull(object, "givenname"));
		user.setEmail(JsonHelper.getStringOrNull(object, "email"));
		user.setUserIdResolver(JsonHelper.getStringOrNull(object, "resolver"));
		
		return user;
	}
	
	public List<LinotpUser> decodeUserList(JsonObject object) {
        JsonObject result = object.getJsonObject("result");
        
        Boolean status = result.getBoolean("status", false);
	    
        if (logger.isDebugEnabled())
	    	logger.debug("LinOTP Session status {} and value {}", status);

    	List<LinotpUser> userList = new ArrayList<LinotpUser>();

    	if (status && result.containsKey("value") && result.containsKey("value")) {
        	
        	JsonArray data = result.getJsonArray("value");
        	
        	for (int i=0; i<data.size(); i++) {
    	        if (logger.isDebugEnabled())
    		    	logger.debug("LinOTP processing user {}", i);

    	        JsonObject jo = data.getJsonObject(i);
        		
    	        LinotpUser user = decode(jo);
        		
    	        userList.add(user);
        	}
        	
        }
    	
    	return userList;
	}
}
