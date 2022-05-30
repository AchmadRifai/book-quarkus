package achmad.rifai.book.quarkus.socket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import achmad.rifai.book.quarkus.config.TimestampCapturer;
import achmad.rifai.book.quarkus.dto.Chat;
import achmad.rifai.book.quarkus.service.ChatService;
import io.smallrye.mutiny.Multi;

@ServerEndpoint("/conversation/{from}/{to}")
@Singleton
public class ConversationSocket {

	@Inject Logger logger;

	@Inject ChatService service;

	Map<String, Session> map = new ConcurrentHashMap<>();

	Map<String, Multi<Chat>> mapSubscribe = new ConcurrentHashMap<>();

	@Inject ObjectMapper objectMapper;

	@Inject TimestampCapturer capturer;

	@OnMessage
	public void onMessage(String message, @PathParam("from") String me, @PathParam("to") String to) {
		service.create(Chat.builder()
				.timestamp(capturer.now())
				.dest(me)
				.to(to)
				.message(message)
				.build());
	}

	@OnClose
	public void onClose(Session session, @PathParam("from") String me, @PathParam("to") String to) throws IOException {
		final String key = String.format("%s & %s", me, to);
		map.remove(key).close();
		mapSubscribe.remove(key).onTermination();
	}

	@OnOpen
	public void onOpen(Session session, @PathParam("from") String me, @PathParam("to") String to) {
		logger.info(session.getId());
		String key = String.format("%s & %s", me, to);
		map.put(key, session);
		mapSubscribe.put(key, service.chatArea(Chat.builder().dest(me).to(to).build()));
		map.keySet().forEach(s->mapSubscribe.get(s).subscribe().with(c->{
			try {
				map.get(s)
						.getAsyncRemote().sendObject(objectMapper.writeValueAsString(c));
			} catch (JsonProcessingException e) {
				logger.fatal(e.getLocalizedMessage(), e);
			}
		}));
	}

}
