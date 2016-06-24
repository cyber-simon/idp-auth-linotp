package edu.kit.scc.linotp;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
