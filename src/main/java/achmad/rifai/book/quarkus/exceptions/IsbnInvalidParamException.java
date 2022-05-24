package achmad.rifai.book.quarkus.exceptions;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import achmad.rifai.book.quarkus.dto.BookIsbnReq;
import lombok.Getter;

@Getter
public class IsbnInvalidParamException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4710395055830640556L;

	private final transient Set<ConstraintViolation<BookIsbnReq>> set;

	public IsbnInvalidParamException(Set<ConstraintViolation<BookIsbnReq>> set) {
		super(set.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", ")));
		this.set = set;
	}

}
