package edu.kit.scc.linotp;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinotpConnection {

	private final Logger logger = LoggerFactory.getLogger(LinotpConnection.class);

	protected LinotpTokenInfoDecoder tokenDecoder;
	protected LinotpUserDecoder userDecoder;
	
	protected CloseableHttpClient httpClient;
	protected HttpClientContext httpContext;
	protected String host;
	
	protected String sessionId;

	public LinotpConnection(String host, String serviceUsername, String servicePassword, Boolean checkCert) throws LinotpSessionException {
		this.host = host;
		
		httpClient = getHttpClient(checkCert);
		httpContext = getHttpContext(serviceUsername, servicePassword);
		
		tokenDecoder = new LinotpTokenInfoDecoder();
		userDecoder = new LinotpUserDecoder();
	}

	public boolean isConnected() {
		return (sessionId != null);
	}

	public List<LinotpUser> getUserList(String criteria, String searchExpression) throws LinotpSessionException {
		logger.debug("Searching users");
	   	
    	CloseableHttpResponse response = null;
    	
    	try {
			URI uri = new URIBuilder().setScheme("https")
					.setHost(host)
					.setPath("/admin/userlist")
					.setParameter("session", sessionId)
					.setParameter(criteria, searchExpression)
					.build();
			
			HttpGet httpget = new HttpGet(uri);
			
			response = httpClient.execute(httpget, httpContext);

		    HttpEntity entity = response.getEntity();
		    String s = EntityUtils.toString(entity);
		    if (logger.isDebugEnabled())
		    	logger.debug("OTP Answer: {}", s);
		    
		    JsonReader reader = Json.createReader(new StringReader(s));
	        JsonObject otp = reader.readObject();

	        List<LinotpUser> userList  = userDecoder.decodeUserList(otp);

	        return userList;

		}  catch (Exception e) {
			logger.debug("Failed to init token", e);
			throw new LinotpSessionException("Failed to fetch init token", e);
		} finally {
			if (response != null)
				try {
					response.close();
				} catch (IOException e) {}
		}    	
	}

	public boolean validateToken(TokenContext tokenCtx) throws LinotpSessionException {
		logger.debug("Trying to validate token for {}", tokenCtx.getUsername());

		CloseableHttpResponse response = null;
		
		try {
			URIBuilder uriBuilder = new URIBuilder().setScheme("https")
					.setHost(host)
					.setPath("/validate/check")
					.setParameter("user", tokenCtx.getUsername())
					.setParameter("pass", tokenCtx.getToken());

			if (tokenCtx.getTransactionId() != null)
				uriBuilder.setParameter("transactionid", tokenCtx.getTransactionId());

			HttpGet httpget = new HttpGet(uriBuilder.build());
			
			response = httpClient.execute(httpget, httpContext);

		    HttpEntity entity = response.getEntity();
		    String s = EntityUtils.toString(entity);
		    if (logger.isTraceEnabled())
		    	logger.trace("OTP Answer: {}", s);
		
		    JsonReader reader = Json.createReader(new StringReader(s));
	        JsonObject otp = reader.readObject();
	        JsonObject result = otp.getJsonObject("result");
	        
	        Boolean status = result.getBoolean("status", false);
	        Boolean value = result.getBoolean("value", false);
		    
	        if (logger.isDebugEnabled())
		    	logger.debug("Validation status {} and value {}", status, value);

	        if (status == true && value == true) {
				return true;
	        }

		}  catch (Exception e) {
			logger.debug("Failed to generate token", e);
			throw new LinotpSessionException("Failed to generate token", e);
		} finally {
			if (response != null)
				try {
					response.close();
				} catch (IOException e) {}
		}
		
		return false;
	}
	
	public void generateToken(TokenContext tokenCtx) throws LinotpSessionException {
		logger.debug("Trying to create new token for {}", tokenCtx.getUsername());

		CloseableHttpResponse response = null;
		
		try {
			URI uri = new URIBuilder().setScheme("https")
					.setHost(host)
					.setPath("/validate/check")
					.setParameter("user", tokenCtx.getUsername())
					.setParameter("pass", "")
					.build();
			
			HttpGet httpget = new HttpGet(uri);
			
			response = httpClient.execute(httpget, httpContext);

		    HttpEntity entity = response.getEntity();
		    String s = EntityUtils.toString(entity);
		    if (logger.isTraceEnabled())
		    	logger.trace("OTP Answer: {}", s);
		    
		    JsonReader reader = Json.createReader(new StringReader(s));
	        JsonObject otp = reader.readObject();

	        if (otp.containsKey("detail")) {
		        JsonObject detail = otp.getJsonObject("detail");

		        if (detail.containsKey("transactionid")) {
				    if (logger.isDebugEnabled())
				    	logger.debug("Setting transactionid on tokenCtx: {}", detail.getString("transactionid"));
			        tokenCtx.setTransactionId(detail.getString("transactionid"));
			        tokenCtx.setMessage(detail.getString("message"));
		        }
		        else {
		        	logger.debug("{} No transaction ID, probably no challange/response");
		        }
	        }		        
	        else {
	        	logger.debug("{} No details, probably no challange/response");
	        }
		}  catch (Exception e) {
			logger.debug("Failed to generate token", e);
			throw new LinotpSessionException("Failed to generate token", e);
		} finally {
			if (response != null)
				try {
					response.close();
				} catch (IOException e) {}
		}
		
	}
	
	public void initEmailToken(String username, String email) throws LinotpSessionException {
		logger.debug("Register Email token for user {}", username);
	   	
    	CloseableHttpResponse response = null;
    	
    	try {
			URI uri = new URIBuilder().setScheme("https")
					.setHost(host)
					.setPath("/admin/init")
					.setParameter("session", sessionId)
					.setParameter("user", username)
					.setParameter("type", "email")
					.setParameter("email_address", email)
					.setParameter("description", email + " token auto generated")
					.build();
			
			HttpGet httpget = new HttpGet(uri);
			
			response = httpClient.execute(httpget, httpContext);

		    HttpEntity entity = response.getEntity();
		    String s = EntityUtils.toString(entity);
		    if (logger.isDebugEnabled())
		    	logger.debug("OTP Answer: {}", s);
		    
		    JsonReader reader = Json.createReader(new StringReader(s));
	        JsonObject otp = reader.readObject();

		}  catch (Exception e) {
			logger.debug("Failed to init token", e);
			throw new LinotpSessionException("Failed to fetch init token", e);
		} finally {
			if (response != null)
				try {
					response.close();
				} catch (IOException e) {}
		}    	
	}
	
    public List<LinotpTokenInfo> getTokenInfoList(String username) throws LinotpSessionException {
		logger.debug("requesting LinOTP user data for user {}", username);
   	
    	CloseableHttpResponse response = null;
    	
    	try {
			URI uri = new URIBuilder().setScheme("https")
					.setHost(host)
					.setPath("/admin/show")
					.setParameter("user", username)
					.setParameter("session", sessionId)
					.build();
			
			HttpGet httpget = new HttpGet(uri);
			
			response = httpClient.execute(httpget, httpContext);
		
		    HttpEntity entity = response.getEntity();
		    String s = EntityUtils.toString(entity);
		    if (logger.isTraceEnabled())
		    	logger.trace("OTP Answer: {}", s);
		    
		    JsonReader reader = Json.createReader(new StringReader(s));
	        JsonObject otp = reader.readObject();
	        
	        List<LinotpTokenInfo> tokenList  = tokenDecoder.decodeTokeList(otp);

	        return tokenList;
		}  catch (Exception e) {
			logger.debug("Failed to fetch token list", e);
			throw new LinotpSessionException("Failed to fetch token list", e);
		} finally {
			if (response != null)
				try {
					response.close();
				} catch (IOException e) {}
		}    	
    }
	
	public void requestAdminSession() throws LinotpSessionException {
		logger.debug("Requesting LinOTP session");
  
    	CloseableHttpResponse response = null;
    	
    	try {
			URI uri = new URIBuilder().setScheme("https")
					.setHost(host)
					.setPath("/admin/getsession")
					.build();
			
			HttpGet httpget = new HttpGet(uri);
			
			response = httpClient.execute(httpget, httpContext);
		
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				logger.warn("LinOTP Server denied our request: {}", response.getStatusLine());
				throw new LinotpSessionException("LinOTP Server denied our request:" + response.getStatusLine());
			}
			
		    HttpEntity entity = response.getEntity();
		    String s = EntityUtils.toString(entity);
		    if (logger.isTraceEnabled())
		    	logger.trace("OTP Answer: {}", s);
		    
		    JsonReader reader = Json.createReader(new StringReader(s));
	        JsonObject otp = reader.readObject();
	        
	        JsonObject result = otp.getJsonObject("result");
	        
	        Boolean status = result.getBoolean("status", false);
	        Boolean value = result.getBoolean("value", false);
		    
	        if (logger.isDebugEnabled())
		    	logger.debug("LinOTP Session status {} and value {}", status, value);

	        if (status == true && value == true) {
	        	for (Cookie c: httpContext.getCookieStore().getCookies()) {
	        		if (c.getName().equalsIgnoreCase("admin_session")) {
	        			logger.debug("extracted session id {}", c.getValue());
	        			sessionId = c.getValue();
	        		}
	        	}
	        }
	        else {
				throw new LinotpSessionException("LinOTP Server did not set the admin session id");
	        }
		}  catch (Exception e) {
			logger.debug("Failed to create new token", e);
			throw new LinotpSessionException("Failed to create session id", e);
		} finally {
			if (response != null)
				try {
					response.close();
				} catch (IOException e) {}
		}
    }

	
    private HttpClientContext getHttpContext(String serviceUsername, String servicePassword) {
    	CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    	credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(serviceUsername + ":" + servicePassword));
    	CookieStore cookieStore = new BasicCookieStore();
	    HttpClientContext context = HttpClientContext.create();
	    context.setCookieStore(cookieStore);
	    context.setCredentialsProvider(credentialsProvider);
		return context;    	
    }
    
	private CloseableHttpClient getHttpClient(Boolean checkCert) throws LinotpSessionException {
		CloseableHttpClient httpclient;
		
		if (checkCert) {
			httpclient = HttpClients.createDefault();
		}
		else {
			try {
				SSLContextBuilder builder = new SSLContextBuilder();
				SSLContext sslContext = builder.loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
				
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
						sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			} catch (KeyManagementException e) {
				throw new LinotpSessionException(e);
			} catch (NoSuchAlgorithmException e) {
				throw new LinotpSessionException(e);
			} catch (KeyStoreException e) {
				throw new LinotpSessionException(e);
			}
		}

		return httpclient;
	}
	
}
