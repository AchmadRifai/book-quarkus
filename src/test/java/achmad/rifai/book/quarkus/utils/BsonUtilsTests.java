package achmad.rifai.book.quarkus.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.bson.BsonValue;
import org.bson.conversions.Bson;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import achmad.rifai.book.quarkus.dto.Book;
import achmad.rifai.book.quarkus.dto.BookWriterReq;
import achmad.rifai.book.quarkus.dto.Chat;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class BsonUtilsTests {

	@Inject Logger logger;

	@Test
	void testChangedDocumentIsbn() {
		Bson bson = BsonUtils.changedDocument(book);
		assertTrue(bson.toBsonDocument().containsKey(BsonUtils.ISBN));
	}

	@Test
	void testChangedDocumentTitle() {
		Bson bson = BsonUtils.changedDocument(book);
		assertTrue(bson.toBsonDocument().containsKey(BsonUtils.TITLE));
	}

	@Test
	void testChangedDocumentWriter() {
		Bson bson = BsonUtils.changedDocument(book);
		assertTrue(bson.toBsonDocument().containsKey(BsonUtils.WRITER));
	}

	@Test
	void testDocumentId() {
		Bson bson = BsonUtils.documentId(book);
		assertTrue(bson.toBsonDocument().containsKey(BsonUtils.ISBN));
	}

	@Test
	void testDocumentWriter() {
		Bson bson = BsonUtils.documentWriter(writerReq);
		assertTrue(bson.toBsonDocument().containsKey(BsonUtils.WRITER));
	}

	@Test
	void testChatChangeDocumentDest() {
		Bson bson = BsonUtils.chatChangeDocument(chat);
		assertTrue(bson.toBsonDocument().containsKey(BsonUtils.DEST));
	}

	@Test
	void testChatChangeDocumentMessage() {
		Bson bson = BsonUtils.chatChangeDocument(chat);
		assertTrue(bson.toBsonDocument().containsKey(BsonUtils.MESSAGE));
	}

	@Test
	void testChatChangeDocumentTo() {
		Bson bson = BsonUtils.chatChangeDocument(chat);
		assertTrue(bson.toBsonDocument().containsKey(BsonUtils.TO));
	}

	@Test
	void testChatChangeDocumentTimestamp() {
		Bson bson = BsonUtils.chatChangeDocument(chat);
		assertTrue(bson.toBsonDocument().containsKey(BsonUtils.TIMESTAMP));
	}

	@Test
	void testChatAreaOr() {
		Bson bson = BsonUtils.chatArea(chat);
		logger.info(bson.toBsonDocument());
		assertTrue(bson.toBsonDocument().isArray(OR_OPERATION));
	}

	@Test
	void testChatAreaOrDocument() {
		Bson bson = BsonUtils.chatArea(chat);
		logger.info(bson.toBsonDocument());
		assertTrue(bson.toBsonDocument().getArray(OR_OPERATION).stream().allMatch(BsonValue::isDocument));
	}

	@Test
	void testChatAreaOrTo() {
		Bson bson = BsonUtils.chatArea(chat);
		logger.info(bson.toBsonDocument());
		assertTrue(bson.toBsonDocument().getArray(OR_OPERATION).stream().allMatch(b->b.asDocument().containsKey(BsonUtils.TO)));
	}

	@Test
	void testChatAreaOrDest() {
		Bson bson = BsonUtils.chatArea(chat);
		logger.info(bson.toBsonDocument());
		assertTrue(bson.toBsonDocument().getArray(OR_OPERATION).stream().allMatch(b->b.asDocument().containsKey(BsonUtils.DEST)));
	}

	@Test
	void testChatFor() {
		Bson bson = BsonUtils.chatFor(chat.getDest());
		assertTrue(bson.toBsonDocument().isArray(OR_OPERATION));
	}

	@Test
	void testChatForTo() {
		Bson bson = BsonUtils.chatFor(chat.getDest());
		assertTrue(bson.toBsonDocument().getArray(OR_OPERATION).stream().anyMatch(b->b.asDocument().containsKey(BsonUtils.TO)));
	}

	@Test
	void testChatForDest() {
		Bson bson = BsonUtils.chatFor(chat.getDest());
		assertTrue(bson.toBsonDocument().getArray(OR_OPERATION).stream().anyMatch(b->b.asDocument().containsKey(BsonUtils.DEST)));
	}

	private final String OR_OPERATION = "$or";

	private final Chat chat = Chat.builder()
			.dest("Rifa'i")
			.message("Hallo")
			.timestamp(System.currentTimeMillis())
			.to("Achmad")
			.build();

	private final Book book = Book.builder()
			.isbn("978-161-729-045-9")
			.title("Mahabarata")
			.writer("Byasa")
			.build();

	private final BookWriterReq writerReq = BookWriterReq.builder()
			.writer(book.getWriter())
			.build();

}
