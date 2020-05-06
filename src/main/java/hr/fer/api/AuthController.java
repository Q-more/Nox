package hr.fer.api;

import hr.fer.model.api.AuthProvider;
import hr.fer.model.User;
import hr.fer.repository.UserRepository;
import hr.fer.security.TokenProvider;
import java.net.URI;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * @author lucija on 01/10/2019
 */

@RestController
@RequestMapping(path = "/auth")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(
		@Valid @RequestBody LoginRequest loginRequest
	) {
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				loginRequest.getEmail(),
				loginRequest.getPassword()
			)
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = tokenProvider.createToken(authentication);
		return ResponseEntity.ok(AuthResponse.builder().accessToken(token).build());
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(
		@Valid @RequestBody SignUpRequest signUpRequest
	) {
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			ResponseEntity.badRequest().body("Email address already in use.");
		}

		// Creating user's account
		User user = User.builder()
			.firstName(signUpRequest.getFirstName())
			.lastName(signUpRequest.getLastName())
			.email(signUpRequest.getEmail())
			.password(passwordEncoder.encode(signUpRequest.getPassword()))
			.authProvider(AuthProvider.EMAIL)
			.build();

		User result = userRepository.save(user);

		URI location = ServletUriComponentsBuilder
			.fromCurrentContextPath().path("/user/me")
			.buildAndExpand(result.getId())
			.toUri();

		return ResponseEntity.created(location)
			.body(new ApiResponse(true, "User registered successfully."));
	}

	@Value
	public static class LoginRequest {

		@Email
		@NotBlank
		private final String email;

		@NotBlank
		private final String password;
	}

	@Value
	public static class SignUpRequest {

		@NotBlank
		private final String firstName;
		private final String lastName;

		@Email
		@NotBlank
		private final String email;

		@NotBlank
		private final String password;
	}

	@Value
	@Builder
	public static class AuthResponse {

		@NonNull
		private String accessToken;

		@NonNull
		@Builder.Default
		private String tokenType = "Bearer";
	}

	@Value
	public static class ApiResponse {
		private boolean success;
		private String message;
	}
}
