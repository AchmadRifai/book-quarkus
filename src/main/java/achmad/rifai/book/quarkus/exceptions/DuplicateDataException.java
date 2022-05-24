package achmad.rifai.book.quarkus.exceptions;

public class DuplicateDataException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1989813859759533503L;

	public DuplicateDataException(String message) {
		super(message);
	}

}
