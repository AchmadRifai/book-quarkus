package achmad.rifai.book.quarkus.exceptions;

public class EmptyISBNException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1341784347851727349L;

	public EmptyISBNException(String message) {
		super(message);
	}

}
