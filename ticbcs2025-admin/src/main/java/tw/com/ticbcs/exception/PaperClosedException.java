package tw.com.ticbcs.exception;

public class PaperClosedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PaperClosedException(String message) {
        super(message);
    }
}
