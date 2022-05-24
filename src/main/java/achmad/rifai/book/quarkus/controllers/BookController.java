package achmad.rifai.book.quarkus.controllers;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import achmad.rifai.book.quarkus.dto.Book;
import achmad.rifai.book.quarkus.dto.BookIsbnReq;
import achmad.rifai.book.quarkus.dto.BookWriterReq;
import achmad.rifai.book.quarkus.dto.DeletedBookRes;
import achmad.rifai.book.quarkus.exceptions.InvalidParamException;
import achmad.rifai.book.quarkus.exceptions.IsbnInvalidParamException;
import achmad.rifai.book.quarkus.exceptions.WriterInvalidParamException;
import achmad.rifai.book.quarkus.service.BookService;

@Path("/book")
@Produces(MediaType.APPLICATION_JSON)
public class BookController {

	@Inject BookService bookService;

	@Inject Logger logger;

	@Inject Validator validator;

	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	public Response create(Book req) {
		Set<ConstraintViolation<Book>> invalidated = validator.validate(req);
		if (!invalidated.isEmpty()) 
			throw new InvalidParamException(invalidated);
		Book result = bookService.create(req);
		return Response.created(URI.create("/book")).entity(result).build();
	}

	@GET
	public List<Book> read() {
		return bookService.read().collect(Collectors.toList());
	}

	@Consumes(MediaType.APPLICATION_JSON)
	@PUT
	public Response update(Book book) {
		logger.info(book);
		Set<ConstraintViolation<Book>> invalidated = validator.validate(book);
		if (!invalidated.isEmpty()) 
			throw new InvalidParamException(invalidated);
		Book result = bookService.update(book);
		return Response.accepted(result).build();
	}

	@DELETE
	@Path("/{isbn}")
	public Response delete(@PathParam("isbn") String isbn) {
		boolean result = bookService.delete(bookService.isbn(BookIsbnReq.builder().isbn(isbn).build()));
		return Response.accepted(DeletedBookRes.builder()
					.deleted(result)
					.build())
				.build();
	}

	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/isbn")
	@POST
	public Book isbn(BookIsbnReq req) {
		Set<ConstraintViolation<BookIsbnReq>> invalidated = validator.validate(req);
		if (!invalidated.isEmpty()) 
			throw new IsbnInvalidParamException(invalidated);
		return bookService.isbn(req);
	}

	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/writer")
	@POST
	public List<Book> writer(BookWriterReq req) {
		Set<ConstraintViolation<BookWriterReq>> invalidated = validator.validate(req);
		if (!invalidated.isEmpty()) 
			throw new WriterInvalidParamException(invalidated);
		return bookService.findByWriter(req);
	}

}
