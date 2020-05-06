package hr.fer.security.oauth2.user;

import hr.fer.exceptions.OAuth2AuthenticationProcessingException;
import hr.fer.model.api.AuthProvider;
import java.util.Map;

import lombok.experimental.UtilityClass;

/**
 * @author lucija on 01/10/2019
 */
@UtilityClass
public class OAuth2UserInfoFactory {

	public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
		if (AuthProvider.GOOGLE.toString().equalsIgnoreCase(registrationId)) {
			return new GoogleOAuth2UserInfo(attributes);
		} else if (AuthProvider.FACEBOOK.toString().equalsIgnoreCase(registrationId)) {
			return new FacebookOAuth2UserInfo(attributes);
		} else {
			throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
		}
	}
}

