package achmad.rifai.book.quarkus.service;

import static achmad.rifai.book.quarkus.utils.BsonUtils.changedDocument;
import static achmad.rifai.book.quarkus.utils.BsonUtils.documentId;
import static achmad.rifai.book.quarkus.utils.BsonUtils.documentWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import achmad.rifai.book.quarkus.dto.Book;
import achmad.rifai.book.quarkus.dto.BookIsbnReq;
import achmad.rifai.book.quarkus.dto.BookWriterReq;
import achmad.rifai.book.quarkus.exceptions.DataNotFoundException;
import achmad.rifai.book.quarkus.exceptions.DuplicateDataException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
class BookServiceTests {

	@Inject
	BookService bookService;

	@Inject Logger logger;

	@InjectMock MongoClient mongoClient;

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
	}

	@Test
	void testRead_Success() {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find().iterator().hasNext()).thenReturn(true).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find().iterator().next()).thenReturn(book);
		assertEquals(1, bookService.read().count());
	}

	@Test
	void testRead_NotFound() {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find().iterator().hasNext()).thenReturn(false);
		assertThrows(DataNotFoundException.class, ()->bookService.read());
	}

	@Test
	void testCreate_Success() {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().next()).thenReturn(book);
		assertEquals(book, bookService.create(book));
	}

	@Test
	void testCreate_Duplicate() {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().next()).thenReturn(book);
		assertThrows(DuplicateDataException.class, ()->bookService.create(book));
	}

	@Test
	void testCreate_FailSave() {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().hasNext()).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(changedDocument(book)).cursor().next()).thenReturn(book);
		assertThrows(DuplicateDataException.class, ()->bookService.create(book));
	}

	@Test
	void testUpdate_Success() {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().next()).thenReturn(book);
		assertEquals(book, bookService.update(book));
	}

	@Test
	void testUpdate_NotFound() {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(false);
		assertThrows(DataNotFoundException.class, ()->bookService.update(book));
	}

	@Test
	void testDelete_Success() {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext())
			.thenReturn(true).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().next()).thenReturn(book);
		assertTrue(bookService.delete(book));
	}

	@Test
	void testDelete_NotFound() {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(false);
		assertThrows(DataNotFoundException.class, ()->bookService.delete(book));
	}

	@Test
	void testDelete_FailDelete() {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(true);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().next()).thenReturn(book);
		assertThrows(DuplicateDataException.class, ()->bookService.delete(book));
	}

	@Test
	void testIsbn_Success() {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext())
			.thenReturn(true).thenReturn(false);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().next()).thenReturn(book);
		Book result = bookService.isbn(isbnReq);
		assertEquals(book, result);
	}

	@Test
	void testIsbn_NotFound() {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentId(book)).cursor().hasNext()).thenReturn(false);
		assertThrows(DataNotFoundException.class, ()->bookService.isbn(isbnReq));
	}

	@Test
	void testWriter_Success() {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentWriter(writerReq)).cursor().next()).thenReturn(book);
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentWriter(writerReq)).cursor().hasNext())
			.thenReturn(true).thenReturn(false);
		List<Book> books = bookService.findByWriter(writerReq);
		assertEquals(Arrays.asList(book), books);
	}

	@Test
	void testWriter_NotFound() {
		when(mongoClient.getDatabase(DB).getCollection(DB, Book.class).find(documentWriter(writerReq)).cursor().hasNext()).thenReturn(false);
		assertThrows(DataNotFoundException.class, ()->bookService.findByWriter(writerReq));
	}

	private final String DB = "book";

	private final Book book = Book.builder()
			.isbn("978-161-729-045-9")
			.title("Mahabarata")
			.writer("Byasa")
			.build();

	private final BookIsbnReq isbnReq = BookIsbnReq.builder()
			.isbn(book.getIsbn())
			.build();

	private final BookWriterReq writerReq = BookWriterReq.builder()
			.writer(book.getWriter())
			.build();

}
