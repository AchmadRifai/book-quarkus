package achmad.rifai.book.quarkus.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.inject.Inject;

import org.bson.BsonString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

import achmad.rifai.book.quarkus.dto.Chat;
import achmad.rifai.book.quarkus.utils.BsonUtils;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.mongodb.reactive.ReactiveMongoDatabase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.groups.MultiCreate;
import io.smallrye.mutiny.groups.UniCreate;

@QuarkusTest
class ChatServiceTests {

	@InjectMock ReactiveMongoClient reactiveMongoClient;
	@InjectMock MongoClient mongoClient;

	@Inject ChatService service;

	@Test
	void testCreate() {
		when(reactiveMongoClient.getDatabase(DB).getCollection(DB, Chat.class).insertOne(chat))
			.thenReturn(UniCreate.INSTANCE.item(InsertOneResult.acknowledged(new BsonString(DB))));
		when(mongoClient.getDatabase(DB).getCollection(DB, Chat.class).insertOne(chat)).thenReturn(InsertOneResult.acknowledged(new BsonString(DB)));
		assertNotNull(service.create(chat));
	}

	@Test
	void testRead() {
		when(reactiveMongoClient.getDatabase(DB).getCollection(DB, Chat.class).find()).thenReturn(MultiCreate.INSTANCE.item(chat));
		service.read().collect().asList().invoke(l->assertEquals(List.of(chat), l));
	}

	@Test
	void testDelete() {
		when(reactiveMongoClient.getDatabase(DB).getCollection(DB, Chat.class).deleteOne(BsonUtils.chatChangeDocument(chat)))
			.thenReturn(UniCreate.INSTANCE.item(DeleteResult.acknowledged(1)));
		when(mongoClient.getDatabase(DB).getCollection(DB, Chat.class).deleteMany(BsonUtils.chatChangeDocument(chat)))
			.thenReturn(DeleteResult.acknowledged(1));
		assertEquals(1, service.delete(chat).getDeletedCount());
	}

	@Test
	void testChatArea() {
		when(reactiveMongoClient.getDatabase(DB).getCollection(DB, Chat.class).find(BsonUtils.chatArea(chat)))
			.thenReturn(MultiCreate.INSTANCE.item(chat));
		service.chatArea(chat).collect().asList().invoke(l->assertEquals(List.of(chat), l));
	}

	@Test
	void testChatFor() {
		when(reactiveMongoClient.getDatabase(DB).getCollection(DB, Chat.class).find(BsonUtils.chatFor(chat.getDest())))
			.thenReturn(MultiCreate.INSTANCE.item(chat));
		service.chatFor(chat.getDest()).collect().asList().subscribe().with(l->assertEquals(List.of(chat), l));
	}

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void inisial() {
		ReactiveMongoDatabase database = mock(ReactiveMongoDatabase.class);
		when(reactiveMongoClient.getDatabase(DB)).thenReturn(database);
		ReactiveMongoCollection<Chat> collection = mock(ReactiveMongoCollection.class);
		when(reactiveMongoClient.getDatabase(DB).getCollection(DB, Chat.class)).thenReturn(collection);
		MongoDatabase database2 = mock(MongoDatabase.class);
		when(mongoClient.getDatabase(DB)).thenReturn(database2);
		MongoCollection<Chat> collection2 = mock(MongoCollection.class);
		when(mongoClient.getDatabase(DB).getCollection(DB, Chat.class)).thenReturn(collection2);
	}

	private static final String DB = "chat";

	private final Chat chat = Chat.builder()
			.dest("Achmad")
			.timestamp(System.currentTimeMillis())
			.to("Rifa'í")
			.message("Pesan")
			.build();

}
