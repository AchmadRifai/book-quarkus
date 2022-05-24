package achmad.rifai.book.quarkus.service;

import static achmad.rifai.book.quarkus.utils.BsonUtils.changedDocument;
import static achmad.rifai.book.quarkus.utils.BsonUtils.documentId;
import static achmad.rifai.book.quarkus.utils.BsonUtils.documentWriter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import achmad.rifai.book.quarkus.dto.Book;
import achmad.rifai.book.quarkus.dto.BookIsbnReq;
import achmad.rifai.book.quarkus.dto.BookWriterReq;
import achmad.rifai.book.quarkus.exceptions.DataNotFoundException;
import achmad.rifai.book.quarkus.exceptions.DuplicateDataException;

@Singleton
public class BookService {

	@Inject MongoClient mongoClient;

	public Book create(Book book) {
		MongoCursor<Book> cursor = getCollection().find(documentId(book)).cursor();
		if (cursor.hasNext()) 
			throw new DuplicateDataException("Book is exists on DB");
		cursor.close();
		getCollection().insertOne(book);
		MongoCursor<Book> cursor2 = getCollection().find(changedDocument(book)).cursor();
		try {
			if (!cursor2.hasNext()) 
				throw new DuplicateDataException("Book cannot save");
			return cursor2.next();
		} finally {
			cursor2.close();
		}
	}

	public Stream<Book> read() {
		MongoCursor<Book> cursor = getCollection().find().iterator();
		List<Book> books = Stream.iterate(cursor, MongoCursor::hasNext, c->c)
				.onClose(cursor::close)
				.map(MongoCursor::next)
				.collect(Collectors.toList());
		if (books.isEmpty()) 
			throw new DataNotFoundException("Books not found");
		return books.stream();
	}

	public Book update(Book book) {
		MongoCursor<Book> cursor = getCollection().find(documentId(book)).cursor();
		if (!cursor.hasNext()) 
			throw new DataNotFoundException("Book not found");
		Book book2 = cursor.next();
		cursor.close();
		getCollection().findOneAndUpdate(documentId(book), changedDocument(book));
		return book2;
	}

	public boolean delete(Book book) {
		MongoCursor<Book> cursor = getCollection().find(documentId(book)).cursor();
		if (!cursor.hasNext()) 
			throw new DataNotFoundException("Book cannot deleted");
		cursor.close();
		getCollection().findOneAndDelete(documentId(book));
		MongoCursor<Book> cursor2 = getCollection().find(documentId(book)).cursor();
		boolean deleted = !cursor2.hasNext();
		cursor2.close();
		if (!deleted) 
			throw new DuplicateDataException("Book is failed to delete");
		return deleted;
	}

	private MongoCollection<Book> getCollection() {
		return mongoClient.getDatabase("book").getCollection("book", Book.class);
	}

	public Book isbn(BookIsbnReq isbnReq) {
		MongoCursor<Book> cursor = getCollection().find(documentId(Book.builder()
				.isbn(isbnReq.getIsbn())
				.build())).cursor();
		if (!cursor.hasNext()) 
			throw new DataNotFoundException("Book not found");
		try {
			return cursor.next();
		} finally {
			cursor.close();
		}
	}

	public List<Book> findByWriter(BookWriterReq writerReq) {
		MongoCursor<Book> cursor = getCollection().find(documentWriter(writerReq)).cursor();
		List<Book> books = Stream.iterate(cursor, MongoCursor::hasNext, c->c)
				.onClose(cursor::close)
				.map(MongoCursor::next)
				.collect(Collectors.toList());
		if (books.isEmpty()) 
			throw new DataNotFoundException(String.format("Writer %s don't have any book", writerReq.getWriter()));
		return books;
	}

}
