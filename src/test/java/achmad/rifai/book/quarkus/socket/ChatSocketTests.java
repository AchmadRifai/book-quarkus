package achmad.rifai.book.quarkus.socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ChatSocketTests {

	@Test
	void testSocketChat() throws IOException, DeploymentException, InterruptedException {
		try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
			assertEquals("Connected", MESSAGES.poll(10, TimeUnit.SECONDS));
			assertEquals("User rifai joined", MESSAGES.poll(10, TimeUnit.SECONDS));
			session.getAsyncRemote().sendText("Hello world");
			assertEquals(">> rifai: Hello world", MESSAGES.poll(10, TimeUnit.SECONDS));
		}
	}

	private static final LinkedBlockingDeque<String> MESSAGES = new LinkedBlockingDeque<>();

	@TestHTTPResource("/chat/rifai") URI uri;

	@ClientEndpoint
	public static class Client {

		@OnOpen
		public void open(Session session) {
			MESSAGES.add("Connected");
			session.getAsyncRemote().sendText("_ready_");
		}

		@OnMessage
		void message(String msg) {
			MESSAGES.add(msg);
		}

	}

}
