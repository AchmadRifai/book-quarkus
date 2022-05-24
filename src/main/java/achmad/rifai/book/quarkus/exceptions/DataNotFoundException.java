package achmad.rifai.book.quarkus.exceptions;

public class DataNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4534369132943299161L;

	public DataNotFoundException(String message) {
		super(message);
	}

}
