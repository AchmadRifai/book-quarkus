package achmad.rifai.book.quarkus.socket;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.jboss.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

import achmad.rifai.book.quarkus.dto.Chat;
import achmad.rifai.book.quarkus.service.ChatService;
import io.smallrye.mutiny.Multi;

@ServerEndpoint("/chats/{dest}")
@Singleton
public class ChatSocket {

	@Inject Logger logger;

	@Inject ChatService service;

	Map<String, Session> map = new ConcurrentHashMap<>();

	Map<String, Multi<Chat>> mapSubscribe = new ConcurrentHashMap<>();

	@Inject ObjectMapper objectMapper;

	@OnOpen
	public void onOpen(Session session, @PathParam("dest") String username) {
		map.put(username, session);
		mapSubscribe.put(username, service.chatFor(username));
		map.keySet().forEach(s->mapSubscribe.get(s)
				.collect()
				.asList()
				.subscribe()
				.with(l->map.get(s)
						.getAsyncRemote()
						.sendObject(l.stream()
						.filter(Objects::nonNull)
						.map(c-> c.getDest().equals(s) ? c.getTo() : c.getDest())
						.distinct()
						.collect(Collectors.joining("\n")), r->{
					if (Objects.nonNull(r.getException())) {
						Throwable e = r.getException();
						logger.fatal(e.getMessage(), e);
					}
				})));
	}

	@OnClose
	public void onClose(Session session, @PathParam("dest") String username) throws IOException {
		map.remove(username).close();
		mapSubscribe.remove(username).onTermination();
	}

}
