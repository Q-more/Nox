package hr.fer.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * @author lucija on 01/10/2019
 */
public class OAuth2AuthenticationProcessingException extends AuthenticationException {

	public OAuth2AuthenticationProcessingException(String msg, Throwable t) {
		super(msg, t);
	}

	public OAuth2AuthenticationProcessingException(String msg) {
		super(msg);
	}
}