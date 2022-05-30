package achmad.rifai.book.quarkus.socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import achmad.rifai.book.quarkus.config.TimestampCapturer;
import achmad.rifai.book.quarkus.dto.Chat;
import achmad.rifai.book.quarkus.service.ChatService;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
class ConversationSocketTests {

	@Test
	void testConnection() throws IOException, DeploymentException {
		try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
			assertTrue(session.isOpen());
		}
	}

	@Test
	void testMessage() throws IOException, DeploymentException, InterruptedException {
		try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
			session.getAsyncRemote().sendText(CHAT.getMessage());
			assertEquals(CHAT, objectMapper.readValue(MESSAGES.poll(20, TimeUnit.SECONDS), Chat.class));
		}
		service.delete(CHAT);
	}

	@BeforeEach
	public void inisial() {
		when(capturer.now()).thenReturn(CURRENT_TIME);
	}

	@InjectMock TimestampCapturer capturer;

	@Inject ChatService service;
	@Inject Logger logger;
	@Inject ObjectMapper objectMapper;

	private static final LinkedBlockingDeque<String> MESSAGES = new LinkedBlockingDeque<>();
	private static final Long CURRENT_TIME = 1L;
	private static final Chat CHAT = Chat.builder()
			.timestamp(CURRENT_TIME)
			.dest("adam")
			.to("hawa")
			.message("test")
			.build();

	@TestHTTPResource("/conversation/adam/hawa") URI uri;

	@ClientEndpoint
	public static class Client {

		@Inject Logger logger;

		@Inject ObjectMapper objectMapper;

		@OnMessage
		void message(String msg) {
			logger.info(msg);
			MESSAGES.add(msg);
		}

		@OnOpen
		public void open(Session session) {
			logger.info(session);
		}

	}

}
