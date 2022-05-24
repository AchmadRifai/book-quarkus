package achmad.rifai.book.quarkus.service.controllers;

import static achmad.rifai.book.quarkus.utils.BsonUtils.changedDocument;
import static achmad.rifai.book.quarkus.utils.BsonUtils.documentId;
import static achmad.rifai.book.quarkus.utils.BsonUtils.documentWriter;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import achmad.rifai.book.quarkus.config.TimestampCapturer;
import achmad.rifai.book.quarkus.dto.Book;
import achmad.rifai.book.quarkus.dto.BookIsbnReq;
import achmad.rifai.book.quarkus.dto.BookWriterReq;
import achmad.rifai.book.quarkus.dto.DeletedBookRes;
import achmad.rifai.book.quarkus.dto.ErrorDto;
import achmad.rifai.book.quarkus.dto.ErrorRes;
import achmad.rifai.book.quarkus.dto.MessageDto;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.response.Response;

@QuarkusTest
class BookControllerTests {

	@InjectMock MongoClient mongoClient;

	@Inject ObjectMapper objectMapper;

	@Inject Logger logger;

	@InjectMock TimestampCapturer capturer;

	@Test
	void testGet_200() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find().iterator().hasNext()).thenReturn(true).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find().iterator().next()).thenReturn(book);
		given()
			.when()
			.get(PATH)
			.then()
			.statusCode(200)
			.body(is(objectMapper.writeValueAsString(Arrays.asList(book))));
	}

	@Test
	void testGet_404() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find().iterator().hasNext()).thenReturn(false);
		Response res=given().when().get(PATH);
		logger.info(res.thenReturn().asString());
		res.then()
			.statusCode(404).and()
			.body(is(objectMapper.writeValueAsString(errorGet404)));
	}

	@Test
	void testPost_201() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().next()).thenReturn(book);
		given()
			.header("Content-Type", "application/json")
			.body(objectMapper.writeValueAsString(book))
			.when()
			.post(PATH)
			.then()
			.statusCode(201)
			.body(is(objectMapper.writeValueAsString(book)));
	}

	@Test
	void testPost_400IsbnBlank() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().next()).thenReturn(book);
		Response res = given().header("Content-Type", "application/json")
				.body(objectMapper.writeValueAsString(Book.builder()
						.isbn(BLANK)
						.title(TITLE)
						.writer(WRITER)
						.build())).when().post(PATH);
		logger.info(res.thenReturn().asString());
		res.then()
			.statusCode(400).and()
			.body(is(objectMapper.writeValueAsString(errorPost400Isbn)));
	}

	@Test
	void testPost_400TitleBlank() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().next()).thenReturn(book);
		Response res = given().header("Content-Type", "application/json")
				.body(objectMapper.writeValueAsString(Book.builder()
						.isbn(ISBN)
						.title(BLANK)
						.writer(WRITER)
						.build())).when().post(PATH);
		logger.info(res.thenReturn().asString());
		res.then()
			.statusCode(400).and()
			.body(is(objectMapper.writeValueAsString(errorPost400Title)));
	}

	@Test
	void testPost_400WriterBlank() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().next()).thenReturn(book);
		Response res = given().header("Content-Type", "application/json")
				.body(objectMapper.writeValueAsString(Book.builder()
						.isbn(ISBN)
						.title(TITLE)
						.writer(BLANK)
						.build())).when().post(PATH);
		logger.info(res.thenReturn().asString());
		res.then()
			.statusCode(400).and()
			.body(is(objectMapper.writeValueAsString(errorPost400Writer)));
	}

	@Test
	void testPost_409Duplicate() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().next()).thenReturn(book);
		Response res = given().header("Content-Type", "application/json")
				.body(objectMapper.writeValueAsString(book)).when().post(PATH);
		logger.info(res.thenReturn().asString());
		res.then()
			.statusCode(409).and()
			.body(is(objectMapper.writeValueAsString(errorPost409Duplicate)));
	}

	@Test
	void testPost_409FailSave() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().hasNext()).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().next()).thenReturn(book);
		Response res = given().header("Content-Type", "application/json")
				.body(objectMapper.writeValueAsString(book)).when().post(PATH);
		logger.info(res.thenReturn().asString());
		res.then()
			.statusCode(409);
	}

	@Test
	void testPut_202() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().next()).thenReturn(book);
		given()
			.header("Content-Type", "application/json")
			.body(objectMapper.writeValueAsString(book))
			.when()
			.put(PATH)
			.then()
			.statusCode(202)
			.body(is(objectMapper.writeValueAsString(book)));
	}

	@Test
	void testPut_400IsbnBlank() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().next()).thenReturn(book);
		Response res = given().header("Content-Type", "application/json")
				.body(objectMapper.writeValueAsString(Book.builder()
						.isbn(BLANK)
						.title(TITLE)
						.writer(WRITER)
						.build())).when().put(PATH);
		logger.info(res.thenReturn().asString());
		res.then()
			.statusCode(400);
	}

	@Test
	void testPut_400TitleBlank() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().next()).thenReturn(book);
		Response res = given().header("Content-Type", "application/json")
				.body(objectMapper.writeValueAsString(Book.builder()
						.isbn(ISBN)
						.title(BLANK)
						.writer(WRITER)
						.build())).when().put(PATH);
		logger.info(res.thenReturn().asString());
		res.then()
			.statusCode(400);
	}

	@Test
	void testPut_400WriterBlank() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().next()).thenReturn(book);
		Response res = given().header("Content-Type", "application/json")
				.body(objectMapper.writeValueAsString(Book.builder()
						.isbn(ISBN)
						.title(TITLE)
						.writer(BLANK)
						.build())).when().put(PATH);
		logger.info(res.thenReturn().asString());
		res.then()
			.statusCode(400);
	}

	@Test
	void testPut_404() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(false);
		Response res = given().header("Content-Type", "application/json")
				.body(objectMapper.writeValueAsString(book)).when().put(PATH);
		logger.info(res.thenReturn().asString());
		res.then()
			.statusCode(404);
	}

	@Test
	void testDelete_202() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext())
			.thenReturn(true).thenReturn(true).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().next()).thenReturn(book);
		given()
			.when()
			.delete(PATH + "/" + book.getIsbn())
			.then()
			.statusCode(202)
			.body(is(objectMapper.writeValueAsString(DeletedBookRes.builder().deleted(true).build())));
	}

	@Test
	void testDelete_404() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find().iterator().hasNext()).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find().iterator().next()).thenReturn(book);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext())
			.thenReturn(true).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().next()).thenReturn(book);
		Response res = given().header("Content-Type", "application/json")
				.body(objectMapper.writeValueAsString(book)).when().delete(PATH + "/" + book.getIsbn());
		logger.info(res.thenReturn().asString());
		res.then()
			.statusCode(404);
	}

	@Test
	void testDelete_405() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find().iterator().hasNext()).thenReturn(true).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find().iterator().next()).thenReturn(book);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext())
			.thenReturn(true).thenReturn(false);
		Response res = given().header("Content-Type", "application/json")
				.body(objectMapper.writeValueAsString(book)).when().delete(PATH + "/ ");
		logger.info(res.thenReturn().asString());
		res.then()
			.statusCode(405);
	}

	@Test
	void testDelete_409() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find().iterator().hasNext()).thenReturn(true).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find().iterator().next()).thenReturn(book);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().next()).thenReturn(book);
		Response res = given().header("Content-Type", "application/json")
				.body(objectMapper.writeValueAsString(book)).when().delete(PATH + "/" + book.getIsbn());
		logger.info(res.thenReturn().asString());
		res.then()
			.statusCode(409);
	}

	@Test
	void testIsbn_200() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext())
			.thenReturn(true).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().next()).thenReturn(book);
		given()
			.when()
			.header("Content-Type", "application/json")
			.body(objectMapper.writeValueAsString(book))
			.when()
			.post(PATH + "/isbn")
			.then()
			.statusCode(200)
			.body(is(objectMapper.writeValueAsString(book)));
	}

	@Test
	void testIsbn_400() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(false);
		Response res = given().when()
				.header("Content-Type", "application/json")
				.body(objectMapper.writeValueAsString(BookIsbnReq.builder().isbn(BLANK).build()))
				.when()
				.post(PATH + "/isbn");
		logger.info(res.thenReturn().asString());
		res.then().statusCode(400);
	}

	@Test
	void testIsbn_404() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(false);
		Response res = given().when()
				.header("Content-Type", "application/json")
				.body(objectMapper.writeValueAsString(book))
				.when()
				.post(PATH + "/isbn");
		logger.info(res.thenReturn().asString());
		res.then().statusCode(404);
	}

	@Test
	void testWriter_200() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentWriter(writerReq)).cursor().next()).thenReturn(book);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentWriter(writerReq)).cursor().hasNext())
			.thenReturn(true).thenReturn(false);
		given()
			.when()
			.header("Content-Type", "application/json")
			.body(objectMapper.writeValueAsString(writerReq))
			.when()
			.post(PATH + "/writer")
			.then()
			.statusCode(200)
			.body(is(objectMapper.writeValueAsString(Arrays.asList(book))));
	}

	@Test
	void testWriter_400() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentWriter(writerReq)).cursor().next()).thenReturn(book);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentWriter(writerReq)).cursor().hasNext())
			.thenReturn(true).thenReturn(false);
		Response res = given().when()
				.header("Content-Type", "application/json")
				.body(objectMapper.writeValueAsString(BookWriterReq.builder().writer(BLANK).build()))
				.when()
				.post(PATH + "/writer");
		logger.info(res.thenReturn().asString());
		res.then().statusCode(400);
	}

	@Test
	void testWriter_404() throws JsonProcessingException {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentWriter(writerReq)).cursor().hasNext()).thenReturn(false);
		Response res = given().when()
				.header("Content-Type", "application/json")
				.body(objectMapper.writeValueAsString(writerReq))
				.when()
				.post(PATH + "/writer");
		logger.info(res.thenReturn().asString());
		res.then().statusCode(404);
	}

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void inisial() {
		MongoDatabase database = mock(MongoDatabase.class);
		MongoCollection<Book> collection = mock(MongoCollection.class);
		FindIterable<Book> iterable = mock(FindIterable.class);
		MongoCursor<Book> cursor = mock(MongoCursor.class);
		FindIterable<Book> iterable3 = mock(FindIterable.class);
		FindIterable<Book> iterable2 = mock(FindIterable.class);
		MongoCursor<Book> cursor3 = mock(MongoCursor.class);
		MongoCursor<Book> cursor2 = mock(MongoCursor.class);
		when(iterable3.cursor()).thenReturn(cursor3);
		when(iterable2.cursor()).thenReturn(cursor2);
		when(iterable.iterator()).thenReturn(cursor);
		when(collection.find()).thenReturn(iterable);
		when(database.getCollection(DB, Book.class)).thenReturn(collection);
		when(mongoClient.getDatabase(DB)).thenReturn(database);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book))).thenReturn(iterable2);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book))).thenReturn(iterable3);
		FindIterable<Book> iterable4 = mock(FindIterable.class);
		MongoCursor<Book> cursor4 = mock(MongoCursor.class);
		when(iterable4.cursor()).thenReturn(cursor4);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentWriter(writerReq))).thenReturn(iterable4);
		when(capturer.now()).thenReturn(CURRENT_TIME);
	}

	private final String DB = "book";
	private final String PATH = "/book";
	private final String ISBN = "978-161-729-045-9";
	private final String TITLE = "Mahabarata";
	private final String WRITER = "Byasa";
	private final String BLANK = " ";
	private final long CURRENT_TIME = 1L;

	private final Book book = Book.builder()
			.isbn(ISBN)
			.title(TITLE)
			.writer(WRITER)
			.build();

	private final BookWriterReq writerReq = BookWriterReq.builder()
			.writer(book.getWriter())
			.build();

	private final List<MessageDto> messages = List.of(MessageDto.builder().message("invalid parameters")
			.reason("invalidParameters").build(),
			MessageDto.builder().message("conflict").reason("Conflict").build(),
			MessageDto.builder().message("Not Allowed").reason("notAllowed").build(),
			MessageDto.builder().message("Internal Server Error").reason("internalServerError").build(),
			MessageDto.builder().message("Not Found").reason("notFound").build(),
			MessageDto.builder().message("ISBN must not be blank")
			.reason("ISBN :\" \", Writer :\"Byasa\", title : \"Mahabarata\"").build(),
			MessageDto.builder().message("Title must not be blank")
			.reason("ISBN :\"978-161-729-045-9\", Writer :\"Byasa\", title : \" \"").build(),
			MessageDto.builder().message("writer must not be blank")
			.reason("ISBN :\"978-161-729-045-9\", Writer :\" \", title : \"Mahabarata\"").build());

	private final ErrorRes errorGet404 = ErrorRes.builder()
			.error(ErrorDto.builder()
					.timestamp(CURRENT_TIME)
					.error("Books not found")
					.status(404)
					.exception(achmad.rifai.book.quarkus.exceptions.DataNotFoundException.class.getName())
					.path(PATH)
					.messages(messages.stream()
							.filter(m->m.getReason().equalsIgnoreCase("notFound"))
							.collect(Collectors.toList()))
					.build())
			.build();

	private final ErrorRes errorPost400Isbn = ErrorRes.builder()
			.error(ErrorDto.builder()
					.timestamp(CURRENT_TIME)
					.error("ISBN must not be blank")
					.status(400)
					.exception(achmad.rifai.book.quarkus.exceptions.InvalidParamException.class.getName())
					.path(PATH)
					.messages(messages.stream()
							.filter(m->m.getMessage().contains("ISBN"))
							.collect(Collectors.toList()))
					.build())
			.build();

	private final ErrorRes errorPost400Title = ErrorRes.builder()
			.error(ErrorDto.builder()
					.timestamp(CURRENT_TIME)
					.error("Title must not be blank")
					.status(400)
					.exception(achmad.rifai.book.quarkus.exceptions.InvalidParamException.class.getName())
					.path(PATH)
					.messages(messages.stream()
							.filter(m->m.getMessage().contains("Title"))
							.collect(Collectors.toList()))
					.build())
			.build();

	private final ErrorRes errorPost400Writer = ErrorRes.builder()
			.error(ErrorDto.builder()
					.timestamp(CURRENT_TIME)
					.error("writer must not be blank")
					.status(400)
					.exception(achmad.rifai.book.quarkus.exceptions.InvalidParamException.class.getName())
					.path(PATH)
					.messages(messages.stream()
							.filter(m->m.getMessage().contains("writer"))
							.collect(Collectors.toList()))
					.build())
			.build();

	private final ErrorRes errorPost409Duplicate = ErrorRes.builder()
			.error(ErrorDto.builder()
					.timestamp(CURRENT_TIME)
					.error("Book is exists on DB")
					.status(409)
					.exception(achmad.rifai.book.quarkus.exceptions.DuplicateDataException.class.getName())
					.path(PATH)
					.messages(messages.stream()
							.filter(m->m.getMessage().equalsIgnoreCase("conflict"))
							.collect(Collectors.toList()))
					.build())
			.build();

	private final ErrorRes errorPost409FailSave = ErrorRes.builder()
			.error(ErrorDto.builder()
					.timestamp(CURRENT_TIME)
					.error("Book cannot save")
					.status(409)
					.exception(achmad.rifai.book.quarkus.exceptions.DuplicateDataException.class.getName())
					.path(PATH)
					.messages(messages.stream()
							.filter(m->m.getMessage().equalsIgnoreCase("conflict"))
							.collect(Collectors.toList()))
					.build())
			.build();

}
