package tw.com.ticbcs.exception;

public class AccountPasswordWrongException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AccountPasswordWrongException(String message) {
        super(message);
    }

}
