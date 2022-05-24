package achmad.rifai.book.quarkus.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;

import org.jboss.logging.Logger;

import achmad.rifai.book.quarkus.dto.ErrorDto;
import achmad.rifai.book.quarkus.dto.ErrorRes;
import achmad.rifai.book.quarkus.dto.MessageDto;
import achmad.rifai.book.quarkus.exceptions.DataNotFoundException;
import achmad.rifai.book.quarkus.exceptions.DuplicateDataException;
import achmad.rifai.book.quarkus.exceptions.InvalidParamException;
import achmad.rifai.book.quarkus.exceptions.IsbnInvalidParamException;
import achmad.rifai.book.quarkus.exceptions.WriterInvalidParamException;

@javax.ws.rs.ext.Provider
public class BookExceptionMapper implements ExceptionMapper<Exception> {

	@Inject Provider<ContainerRequestContext> provider;

	@Context UriInfo uriInfo;

	@Inject Logger logger;

	@Inject TimestampCapturer capturer;

	@Override
	public Response toResponse(Exception exception) {
		Map<Function<Exception, Boolean>, Function<Exception, ErrorRes>> errorMap = errorMap();
		Optional<Function<Exception, ErrorRes>> optionalFunction = errorMap.keySet()
				.parallelStream()
				.filter(Objects::nonNull)
				.filter(f->f.apply(exception))
				.map(errorMap::get)
				.filter(Objects::nonNull)
				.findFirst();
		ErrorRes res = optionalFunction.isPresent() ? optionalFunction.get().apply(exception) : internalServerError(exception);
		return Response.status(res.getError().getStatus()).entity(res).build();
	}

	private Map<Function<Exception, Boolean>, Function<Exception, ErrorRes>> errorMap() {
		Map<Function<Exception, Boolean>, Function<Exception, ErrorRes>> map = new HashMap<>();
		map.put(DataNotFoundException.class::isInstance, this::dataNotFound);
		map.put(DuplicateDataException.class::isInstance, this::duplicateData);
		map.put(InvalidParamException.class::isInstance, this::invalidParams);
		map.put(NotAllowedException.class::isInstance, this::notAllowed);
		map.put(IsbnInvalidParamException.class::isInstance, this::invalidParamIsbn);
		map.put(WriterInvalidParamException.class::isInstance, this::invalidParamWriter);
		map.put(NotFoundException.class::isInstance, this::notFound);
		return map;
	}

	private ErrorRes notFound(Exception e) {
		NotFoundException exception = (NotFoundException) e;
		logger.fatal(exception.getMessage(), exception);
		Map<Integer, MessageDto> msgMap = mapMessage();
		return ErrorRes.builder()
				.error(ErrorDto.builder()
						.timestamp(capturer.now())
						.status(Response.Status.NOT_FOUND.getStatusCode())
						.exception(exception.getClass().getName())
						.messages(Arrays.asList(msgMap.get(Response.Status.NOT_FOUND.getStatusCode())))
						.error(exception.getMessage())
						.path(uriInfo.getPath())
						.build())
				.build();
	}

	private ErrorRes invalidParamWriter(Exception e) {
		WriterInvalidParamException exception = (WriterInvalidParamException) e;
		logger.info(exception.getSet());
		logger.fatal(exception.getMessage(), exception);
		return ErrorRes.builder()
				.error(ErrorDto.builder()
						.timestamp(capturer.now())
						.status(Response.Status.BAD_REQUEST.getStatusCode())
						.exception(exception.getClass().getName())
						.messages(exception.getSet()
								.parallelStream()
								.map(v->MessageDto.builder()
										.message(v.getMessage())
										.reason(String.format("Writer : \"%s\"", v.getRootBean().getWriter()))
										.build())
								.collect(Collectors.toList()))
						.error(exception.getMessage())
						.path(uriInfo.getPath())
						.build())
				.build();
	}

	private ErrorRes invalidParamIsbn(Exception e) {
		IsbnInvalidParamException exception = (IsbnInvalidParamException) e;
		logger.info(exception.getSet());
		logger.fatal(exception.getMessage(), exception);
		return ErrorRes.builder()
				.error(ErrorDto.builder()
						.timestamp(capturer.now())
						.status(Response.Status.BAD_REQUEST.getStatusCode())
						.exception(exception.getClass().getName())
						.messages(exception.getSet()
								.parallelStream()
								.map(v->MessageDto.builder()
										.message(v.getMessage())
										.reason(String.format("Isbn : \"%s\"", v.getRootBean().getIsbn()))
										.build())
								.collect(Collectors.toList()))
						.error(exception.getMessage())
						.path(uriInfo.getPath())
						.build())
				.build();
	}

	private ErrorRes notAllowed(Exception e) {
		logger.fatal(e.getMessage(), e);
		Map<Integer, MessageDto> msgMap = mapMessage();
		return ErrorRes.builder()
				.error(ErrorDto.builder()
						.timestamp(capturer.now())
						.status(Response.Status.METHOD_NOT_ALLOWED.getStatusCode())
						.exception(e.getClass().getName())
						.messages(Arrays.asList(msgMap.get(Response.Status.METHOD_NOT_ALLOWED.getStatusCode())))
						.error(e.getMessage())
						.path(uriInfo.getPath())
						.build())
				.build();
	}

	private ErrorRes invalidParams(Exception e) {
		InvalidParamException exception = (InvalidParamException) e;
		logger.info(exception.getErrorList());
		logger.fatal(exception.getMessage(), exception);
		return ErrorRes.builder()
				.error(ErrorDto.builder()
						.timestamp(capturer.now())
						.status(Response.Status.BAD_REQUEST.getStatusCode())
						.exception(exception.getClass().getName())
						.messages(exception.getErrorList()
								.parallelStream()
								.map(v->MessageDto.builder()
										.message(v.getMessage())
										.reason(String.format("ISBN :\"%s\", Writer :\"%s\", title : \"%s\"", 
												v.getRootBean().getIsbn(), 
												v.getRootBean().getWriter(), 
												v.getRootBean().getTitle()))
										.build())
								.collect(Collectors.toList()))
						.error(exception.getMessage())
						.path(uriInfo.getPath())
						.build())
				.build();
	}

	private ErrorRes duplicateData(Exception e) {
		DuplicateDataException exception = (DuplicateDataException) e;
		logger.fatal(exception.getMessage(), exception);
		Map<Integer, MessageDto> msgMap = mapMessage();
		return ErrorRes.builder()
				.error(ErrorDto.builder()
						.timestamp(capturer.now())
						.status(Response.Status.CONFLICT.getStatusCode())
						.exception(exception.getClass().getName())
						.messages(Arrays.asList(msgMap.get(Response.Status.CONFLICT.getStatusCode())))
						.error(exception.getMessage())
						.path(uriInfo.getPath())
						.build())
				.build();
	}

	private ErrorRes dataNotFound(Exception e) {
		DataNotFoundException exception = (DataNotFoundException) e;
		logger.fatal(exception.getMessage(), exception);
		Map<Integer, MessageDto> msgMap = mapMessage();
		return ErrorRes.builder()
				.error(ErrorDto.builder()
						.timestamp(capturer.now())
						.status(Response.Status.NOT_FOUND.getStatusCode())
						.exception(exception.getClass().getName())
						.messages(Arrays.asList(msgMap.get(Response.Status.NOT_FOUND.getStatusCode())))
						.error(exception.getMessage())
						.path(uriInfo.getPath())
						.build())
				.build();
	}

	private ErrorRes internalServerError(Exception e) {
		logger.fatal(e.getMessage(), e);
		Map<Integer, MessageDto> msgMap = mapMessage();
		return ErrorRes.builder()
				.error(ErrorDto.builder()
						.timestamp(capturer.now())
						.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
						.exception(e.getClass().getName())
						.messages(Arrays.asList(msgMap.get(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())))
						.error(e.getMessage())
						.path(uriInfo.getPath())
						.build())
				.build();
	}

	private Map<Integer, MessageDto> mapMessage() {
		Map<Integer, MessageDto> map = new HashMap<>();
		map.put(Response.Status.NOT_FOUND.getStatusCode(), MessageDto.builder().message("Not Found").reason("notFound").build());
		map.put(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
				MessageDto.builder().message("Internal Server Error").reason("internalServerError").build());
		map.put(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), 
				MessageDto.builder().message("Not Allowed").reason("notAllowed").build());
		map.put(Response.Status.CONFLICT.getStatusCode(), MessageDto.builder().message("conflict").reason("Conflict").build());
		map.put(Response.Status.BAD_REQUEST.getStatusCode(), MessageDto.builder().message("invalid parameters")
				.reason("invalidParameters").build());
		return map;
	}

}
