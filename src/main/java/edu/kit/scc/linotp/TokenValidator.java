package edu.kit.scc.linotp;

import javax.annotation.Nonnull;
import javax.security.auth.Subject;

import net.shibboleth.idp.authn.AbstractValidationAction;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.context.SubjectContext;
import net.shibboleth.idp.authn.principal.UsernamePrincipal;
import net.shibboleth.idp.session.context.SessionContext;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenValidator extends AbstractValidationAction {

	private final Logger logger = LoggerFactory.getLogger(TokenValidator.class);
	
	private String username;
	
	private String host;
	private String serviceUsername;
	private String servicePassword;
	private Boolean checkCert;
	
    @Override
    protected boolean doPreExecute(
            @Nonnull ProfileRequestContext profileRequestContext,
            @Nonnull AuthenticationContext authenticationContext) {
        if (!super.doPreExecute(profileRequestContext, authenticationContext)) {
            return false;
        }

        if (authenticationContext.getParent() != null) {
	        SubjectContext subjectContext = authenticationContext.getParent().getSubcontext(SubjectContext.class);       
	        if (subjectContext != null && subjectContext.getPrincipalName() != null) {
	        	username = subjectContext.getPrincipalName();
	        }
	        else {
	        	SessionContext sessionContext = authenticationContext.getParent().getSubcontext(SessionContext.class);
	        	
	        	if (sessionContext != null && sessionContext.getIdPSession() != null
	        			&& sessionContext.getIdPSession().getPrincipalName() != null) {
	        		username = sessionContext.getIdPSession().getPrincipalName();
	        	}
	        }
        }

        if (username == null) {
        	logger.warn("{} No previous SubjectContext or Principal is set", getLogPrefix());
        	handleError(profileRequestContext, authenticationContext, "NoCredentials", AuthnEventIds.NO_CREDENTIALS);
        	return false;
        }
        
    	logger.debug("{} PrincipalName from SubjectContext is {}", getLogPrefix(), username);
        return true;
    }
	
	@Override
	protected Subject populateSubject(Subject subject) {
		logger.debug("{} TokenValidator populateSubject is called", getLogPrefix());		
		if (StringSupport.trimOrNull(username) != null) {
			logger.debug("{} Populate subject {}", getLogPrefix(), username);
			subject.getPrincipals().add(new UsernamePrincipal(username));
			return subject;
		}
		return null;
	}
	
	@Override
	protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext,
			@Nonnull final AuthenticationContext authenticationContext) {
		logger.debug("{} Entering TokenValidator", getLogPrefix());		

		TokenContext tokenCtx = authenticationContext.getSubcontext(TokenContext.class, true);

		logger.debug("{} TokenValidator is called with token {} for user {}", getLogPrefix(), tokenCtx.getToken(), username);

		try {
			LinotpConnection connection = new LinotpConnection(host, serviceUsername, servicePassword, checkCert);
			boolean login = connection.validateToken(tokenCtx);
			
			if (login == true) {
				buildAuthenticationResult(profileRequestContext, authenticationContext);
				return;
			}
				
			handleError(profileRequestContext, authenticationContext, new LinotpLoginException("InvalidCredentials"),
						AuthnEventIds.INVALID_CREDENTIALS);
	
		}		
		catch (Exception e) {
			logger.warn("{} Exception while validating token: {}", getLogPrefix(), e.getMessage());
			handleError(profileRequestContext, authenticationContext, new LinotpLoginException("GenericException"),
					AuthnEventIds.AUTHN_EXCEPTION);
		}
	}
	
	public void setHost(@Nonnull @NotEmpty final String fieldName) {
		logger.debug("{} {} is tokencode field from the form", getLogPrefix(), fieldName);
		ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
		host = fieldName;
	}

	public void setServiceUsername(@Nonnull @NotEmpty final String fieldName) {
		logger.debug("{} {} is tokencode field from the form", getLogPrefix(), fieldName);
		ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
		serviceUsername = fieldName;
	}

	public void setServicePassword(@Nonnull @NotEmpty final String fieldName) {
		logger.debug("{} {} is tokencode field from the form", getLogPrefix(), fieldName);
		ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
		servicePassword = fieldName;
	}

	public void setCheckCert(@Nonnull @NotEmpty final Boolean fieldName) {
		ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
		checkCert = fieldName;
	}

}
