package hr.fer.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author lucija on 01/12/2019
 */

@Data
@Builder
@Document(collection = "users")
public class User {

  @Id
	private String id;

	private String name;
	private String email;
  private String password; // Only active if registered with email.
  private String imageUrl;

	// Provider stuff.
	private String providerId;
	private AuthProvider authProvider;

}
