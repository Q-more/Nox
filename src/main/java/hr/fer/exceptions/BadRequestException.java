package hr.fer.exceptions;

/**
 * @author lucija on 01/10/2019
 */
public class BadRequestException extends RuntimeException {
  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(String message, Throwable cause) {
    super(message, cause);
  }
}
