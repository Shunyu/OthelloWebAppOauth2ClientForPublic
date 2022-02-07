package com.beautifulsetouchi.AiOthelloGameClientServer.logout.handler;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public final class OidcClientInitiatedLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	private String postLogoutRedirectUri;
	private String endSessionEndpoint;

	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		String targetUrl = null;
		if (authentication instanceof OAuth2AuthenticationToken && authentication.getPrincipal() instanceof OidcUser) {
			
			URI endSessionEndpoint = this.endSessionEndpoint();
			
			if (endSessionEndpoint != null) {
				String idToken = idToken(authentication);
				String postLogoutRedirectUri = postLogoutRedirectUri(request);
				targetUrl = endpointUri(endSessionEndpoint, idToken, postLogoutRedirectUri);
			}
		}
		return (targetUrl != null) ? targetUrl : super.determineTargetUrl(request, response);
	}
	
	private URI endSessionEndpoint() {
		return URI.create(this.endSessionEndpoint);
	}

	private String idToken(Authentication authentication) {
		return ((OidcUser) authentication.getPrincipal()).getIdToken().getTokenValue();
	}

	private String postLogoutRedirectUri(HttpServletRequest request) {
		if (this.postLogoutRedirectUri == null) {
			return null;
		}
		// @formatter:off
		UriComponents uriComponents = UriComponentsBuilder
				.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
				.replacePath(request.getContextPath())
				.replaceQuery(null)
				.fragment(null)
				.build();
		return UriComponentsBuilder.fromUriString(this.postLogoutRedirectUri)
				.buildAndExpand(Collections.singletonMap("baseUrl", uriComponents.toUriString()))
				.toUriString();
		// @formatter:on
	}

	private String endpointUri(URI endSessionEndpoint, String idToken, String postLogoutRedirectUri) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUri(endSessionEndpoint);
		builder.queryParam("id_token_hint", idToken);
		if (postLogoutRedirectUri != null) {
			builder.queryParam("post_logout_redirect_uri", postLogoutRedirectUri);
		}
		// @formatter:off
		return builder.encode(StandardCharsets.UTF_8)
				.build()
				.toUriString();
		// @formatter:on
	}

	/**
	 * Set the post logout redirect uri to use
	 * @param postLogoutRedirectUri - A valid URL to which the OP should redirect after
	 * logging out the user
	 * @deprecated {@link #setPostLogoutRedirectUri(String)}
	 */
	@Deprecated
	public void setPostLogoutRedirectUri(URI postLogoutRedirectUri) {
		Assert.notNull(postLogoutRedirectUri, "postLogoutRedirectUri cannot be null");
		this.postLogoutRedirectUri = postLogoutRedirectUri.toASCIIString();
	}

	/**
	 * Set the post logout redirect uri template to use. Supports the {@code "{baseUrl}"}
	 * placeholder, for example:
	 *
	 * <pre>
	 * 	handler.setPostLogoutRedirectUri("{baseUrl}");
	 * </pre>
	 *
	 * will make so that {@code post_logout_redirect_uri} will be set to the base url for
	 * the client application.
	 * @param postLogoutRedirectUri - A template for creating the
	 * {@code post_logout_redirect_uri} query parameter
	 * @since 5.3
	 */
	public void setPostLogoutRedirectUri(String postLogoutRedirectUri) {
		Assert.notNull(postLogoutRedirectUri, "postLogoutRedirectUri cannot be null");
		this.postLogoutRedirectUri = postLogoutRedirectUri;
	}

	public void setEndSessionEndpoint(String endSessionEndpoint) {
		Assert.notNull(endSessionEndpoint, "endSessionEndpoint cannot be null");
		this.endSessionEndpoint = endSessionEndpoint;
	}
	
}
