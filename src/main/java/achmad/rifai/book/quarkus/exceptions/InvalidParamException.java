package achmad.rifai.book.quarkus.exceptions;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import achmad.rifai.book.quarkus.dto.Book;

public class InvalidParamException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7337387443816780308L;

	private final transient Set<ConstraintViolation<Book>> errorList;

	public InvalidParamException(Set<ConstraintViolation<Book>> errorList) {
		super(errorList.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", ")));
		this.errorList = errorList;
	}

	public Set<ConstraintViolation<Book>> getErrorList() {
		return errorList;
	}

}
