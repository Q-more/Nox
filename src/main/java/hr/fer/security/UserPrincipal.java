package hr.fer.security;

import hr.fer.model.User;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * @author lucija on 01/10/2019
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPrincipal implements OAuth2User, UserDetails {

	private String id;
	private String email;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;
	private Map<String, Object> attributes;

	public static UserPrincipal create(User user) {
		return new UserPrincipal(
			user.getId(),
			user.getEmail(),
			user.getPassword(),
			getAuthority(),
			null
		);
	}

	public static UserPrincipal create(User user, Map<String, Object> attributes) {
		return new UserPrincipal(
			user.getId(),
			user.getEmail(),
			user.getPassword(),
			getAuthority(),
			attributes
		);
	}

	private static Collection<? extends GrantedAuthority> getAuthority() {
		return Collections.
			singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getName() {
		return String.valueOf(id);
	}
}
