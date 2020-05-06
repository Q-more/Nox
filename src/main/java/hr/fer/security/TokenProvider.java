package hr.fer.security;

import hr.fer.configuration.AppConfiguration;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lucija on 01/10/2019
 */
@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TokenProvider {

	private final AppConfiguration appConfiguration;

	public String createToken(Authentication authentication) {
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + appConfiguration.getAuth().getTokenExpirationMsec());

		return Jwts.builder()
			.setSubject(userPrincipal.getId())
			.setIssuedAt(new Date())
			.setExpiration(expiryDate)
			.signWith(SignatureAlgorithm.HS512, appConfiguration.getAuth().getTokenSecret())
			.compact();
	}

	public String getUserIdFromToken(String token) {
		return Jwts.parser()
			.setSigningKey(appConfiguration.getAuth().getTokenSecret())
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(appConfiguration.getAuth().getTokenSecret()).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException ex) {
			log.error("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			log.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty.");
		}
		return false;
	}
}
