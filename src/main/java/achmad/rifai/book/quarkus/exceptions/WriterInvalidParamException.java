package achmad.rifai.book.quarkus.exceptions;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import achmad.rifai.book.quarkus.dto.BookWriterReq;

public class WriterInvalidParamException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -862584623145024251L;

	private final transient Set<ConstraintViolation<BookWriterReq>> set;

	public WriterInvalidParamException(Set<ConstraintViolation<BookWriterReq>> set) {
		super(set.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", ")));
		this.set = set;
	}

	public Set<ConstraintViolation<BookWriterReq>> getSet() {
		return set;
	}

}
