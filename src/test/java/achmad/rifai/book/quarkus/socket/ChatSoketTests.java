package achmad.rifai.book.quarkus.socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import achmad.rifai.book.quarkus.dto.Chat;
import achmad.rifai.book.quarkus.utils.BsonUtils;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.mongodb.reactive.ReactiveMongoDatabase;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.groups.MultiCreate;

@QuarkusTest
class ChatSoketTests {

	@InjectMock ReactiveMongoClient reactiveMongoClient;

	@Inject Logger logger;

	@Test
	void testChatFor() throws IOException, DeploymentException, InterruptedException {
		try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
			assertEquals(chat.getDest(), MESSAGES.poll(20, TimeUnit.SECONDS));
		}
	}

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void inisial() {
		ReactiveMongoDatabase database = mock(ReactiveMongoDatabase.class);
		when(reactiveMongoClient.getDatabase(DB)).thenReturn(database);
		ReactiveMongoCollection<Chat> collection = mock(ReactiveMongoCollection.class);
		when(reactiveMongoClient.getDatabase(DB).getCollection(DB, Chat.class)).thenReturn(collection);
		when(reactiveMongoClient.getDatabase(DB).getCollection(DB, Chat.class).find(BsonUtils.chatFor(chat.getTo())))
			.thenReturn(MultiCreate.INSTANCE.item(chat));
	}

	private static final String DB = "chat";

	private final Chat chat = Chat.builder()
			.dest("Achmad")
			.timestamp(System.currentTimeMillis())
			.to("rifai")
			.message("Pesan")
			.build();

	private static final LinkedBlockingDeque<String> MESSAGES = new LinkedBlockingDeque<>();

	@TestHTTPResource("/chats/rifai") URI uri;

	@ClientEndpoint
	public static class Client {

		@Inject Logger logger;

		@OnOpen
		public void open(Session session) {
			logger.info(session);
		}

		@OnMessage
		void message(String msg) {
			logger.info(msg);
			MESSAGES.add(msg);
		}

	}

}
