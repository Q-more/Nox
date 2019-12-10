package hr.fer.security.oauth2;

import hr.fer.exceptions.OAuth2AuthenticationProcessingException;
import hr.fer.model.AuthProvider;
import hr.fer.model.User;
import hr.fer.repository.UserRepository;
import hr.fer.security.UserPrincipal;
import hr.fer.security.oauth2.user.OAuth2UserInfo;
import hr.fer.security.oauth2.user.OAuth2UserInfoFactory;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;

/**
 * @author lucija on 01/10/2019
 */
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

	private UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

		try {
			return processOAuth2User(oAuth2UserRequest, oAuth2User);
		} catch (AuthenticationException ex) {
			throw ex;
		} catch (Exception ex) {
			// Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
		}
	}

	private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
			oAuth2UserRequest.getClientRegistration().getRegistrationId(),
			oAuth2User.getAttributes()
		);

		if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
			throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
		}

		Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
		User user;
		if (userOptional.isPresent()) {
			user = userOptional.get();
			AuthProvider provider = AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase());
			if (!user.getAuthProvider().equals(provider)) {
				throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
					user.getAuthProvider() + " account. Please use your " + user.getAuthProvider() +
					" account to login.");
			}
			user = updateExistingUser(user, oAuth2UserInfo);
		} else {
			user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
		}

		return UserPrincipal.create(user, oAuth2User.getAttributes());
	}

	private User registerNewUser(
		OAuth2UserRequest oAuth2UserRequest,
		OAuth2UserInfo oAuth2UserInfo
	) {
		User user = User.builder()
			.providerId(oAuth2UserInfo.getId())
			.authProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()))
			.name(oAuth2UserInfo.getName())
			.email(oAuth2UserInfo.getEmail())
			.imageUrl(oAuth2UserInfo.getImageUrl())
			.build();
		return userRepository.save(user);
	}

	private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
		existingUser.setName(oAuth2UserInfo.getName());
		existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
		return userRepository.save(existingUser);
	}
}
