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

import javax.annotation.Nonnull;

import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.idp.authn.AbstractExtractionAction;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.ComponentSupport;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

public class ExtractTokenFromForm extends AbstractExtractionAction {

	/** Class logger. */
	@Nonnull
	private final Logger logger = LoggerFactory.getLogger(ExtractTokenFromForm.class);

	@Nonnull
	@NotEmpty
	private String tokenCodeField;

	public ExtractTokenFromForm() {
		super();
	}

	public void setTokenCodeField(@Nonnull @NotEmpty final String fieldName) {
		/* Commenting out field debug. This can exponse sensitive information (token codes and PINs) in the logs.
		logger.debug("{} {} is tokencode field from the form", getLogPrefix(), fieldName);
		*/
		ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
		tokenCodeField = fieldName;
	}


	@Override
	protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext,
			@Nonnull final AuthenticationContext authenticationContext) {

		final HttpServletRequest request = getHttpServletRequest();

		if (request == null) {
			logger.debug("{} Empty HttpServletRequest", getLogPrefix());
			ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
			return;
		}

		try {

			TokenContext tokenCtx = authenticationContext.getSubcontext(TokenContext.class, true);

			/** get tokencode from request **/
			String value = StringSupport.trimOrNull(request.getParameter(tokenCodeField));

			if (Strings.isNullOrEmpty(value)) {
				logger.debug("{} Empty tokenCode", getLogPrefix());
				ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.INVALID_CREDENTIALS);
				return;
			} else {
				/* Commenting out token code log. This is too sensitive to log.
				logger.debug("{} TokenCode: {}", getLogPrefix(), value);
				*/

				/** set tokencode to TokenCodeContext **/
				tokenCtx.setToken(value);
				logger.debug("Put Token code to the TokenCodeCtx");
				return;
			}

		} catch (Exception e) {
			logger.warn("{} Login by {} produced exception", getLogPrefix(),  e);
		}
	}
}
