package hr.fer.security.oauth2.user;

import java.util.Map;

import lombok.AllArgsConstructor;

/**
 * @author lucija on 01/10/2019
 */
@AllArgsConstructor
public abstract class OAuth2UserInfo {
  protected final Map<String, Object> attributes;

  public abstract String getId();

  public abstract String getName();

  public abstract String getEmail();

  public abstract String getImageUrl();
}
