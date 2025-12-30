package bookstore.exceptionCenter;

public class BookException extends RuntimeException {

	private int code;

	public BookException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
}
