package bookstore.exceptionCenter;

public class BaseException extends RuntimeException {

	private int code;

	public BaseException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
}
