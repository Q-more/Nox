package hr.fer.security.oauth2;

import hr.fer.configuration.AppConfiguration;
import hr.fer.exceptions.BadRequestException;
import hr.fer.security.TokenProvider;
import hr.fer.utils.CookieUtils;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;

import static hr.fer.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

/**
 * @author lucija on 01/10/2019
 */
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final TokenProvider tokenProvider;
  private final AppConfiguration appConfiguration;
  private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException {
    String targetUrl = determineTargetUrl(request, authentication);

    if (response.isCommitted()) {
      logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
      return;
    }

    clearAuthenticationAttributes(request, response);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  protected String determineTargetUrl(
      HttpServletRequest request,
      Authentication authentication
  ) {
    Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
        .map(Cookie::getValue);

    if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
      throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
    }

    String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

    String token = tokenProvider.createToken(authentication);

    return UriComponentsBuilder.fromUriString(targetUrl)
        .queryParam("token", token)
        .build().toUriString();
  }

  protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
  }

  private boolean isAuthorizedRedirectUri(String uri) {
    URI clientRedirectUri = URI.create(uri);

    return appConfiguration.getOauth2().getAuthorizedRedirectUris()
        .stream()
        .anyMatch(authorizedRedirectUri -> {
          // Only validate host and port. Let the clients use different paths if they want to
          URI authorizedURI = URI.create(authorizedRedirectUri);
          return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
              && authorizedURI.getPort() == clientRedirectUri.getPort();
        });
  }

}
