package bs.experian.integracion.infrastructure.exceptions;

public class RetryableProcessingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RetryableProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

}
