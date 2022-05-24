package achmad.rifai.book.quarkus.utils;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import achmad.rifai.book.quarkus.dto.Book;
import achmad.rifai.book.quarkus.dto.BookWriterReq;
import achmad.rifai.book.quarkus.dto.Chat;

public class BsonUtils {

	private BsonUtils() {
		super();
	}

	public static final String TITLE = "title";
	public static final String ISBN = "isbn";
	public static final String WRITER = "writer";
	public static final String DEST = "dest";
	public static final String TO = "to";
	public static final String MESSAGE = "message";
	public static final String TIMESTAMP = "timestamp";

	public static Bson changedDocument(Book book) {
		return new BsonDocument()
				.append(TITLE, new BsonString(book.getTitle()))
				.append(ISBN, new BsonString(book.getIsbn()))
				.append(WRITER, new BsonString(book.getWriter()));
	}

	public static Bson documentId(Book book) {
		return new BsonDocument(ISBN, new BsonString(book.getIsbn()));
	}

	public static Bson documentWriter(BookWriterReq writerReq) {
		return new BsonDocument().append(WRITER, new BsonString(writerReq.getWriter()));
	}

	public static Bson chatChangeDocument(Chat chat) {
		return new BsonDocument()
				.append(DEST, new BsonString(chat.getDest()))
				.append(MESSAGE, new BsonString(chat.getMessage()))
				.append(TO, new BsonString(chat.getTo()))
				.append(TIMESTAMP, new BsonInt64(chat.getTimestamp()));
	}

	public static Bson chatArea(Chat chat) {
		return Filters.or(new BsonDocument()
				.append(DEST, new BsonString(chat.getDest()))
				.append(TO, new BsonString(chat.getTo())),
				new BsonDocument()
				.append(DEST, new BsonString(chat.getTo()))
				.append(TO, new BsonString(chat.getDest())));
	}

	public static Bson chatFor(String username) {
		return Filters.or(new BsonDocument().append(DEST, new BsonString(username)),
				new BsonDocument().append(TO, new BsonString(username)));
	}

}
