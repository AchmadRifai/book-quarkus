package achmad.rifai.book.quarkus.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;

import achmad.rifai.book.quarkus.dto.Book;
import achmad.rifai.book.quarkus.dto.Chat;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class MongoDbTest {

	@Inject MongoClient mongoClient;

	@Inject ReactiveMongoClient reactiveMongoClient;

	@Test
	void testConnectionDb() {
		assertNotNull(mongoClient.getDatabase(BOOK));
	}

	@Test
	void testConnectionCollection() {
		assertNotNull(mongoClient.getDatabase(BOOK).getCollection(BOOK, Book.class));
	}

	@Test
	void testConnectionDbChat() {
		assertNotNull(mongoClient.getDatabase(CHAT));
	}

	@Test
	void testConnectionCollectionChat() {
		assertNotNull(mongoClient.getDatabase(CHAT).getCollection(CHAT, Chat.class));
	}

	@Test
	void testReactiveConnectionDb() {
		assertNotNull(reactiveMongoClient.getDatabase(CHAT));
	}

	@Test
	void testReactiveConnectionCollection() {
		assertNotNull(reactiveMongoClient.getDatabase(CHAT).getCollection(CHAT, Chat.class));
	}

	private static final String BOOK = "book";
	private static final String CHAT = "chat";

}
