package achmad.rifai.book.quarkus.socket;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.jboss.logging.Logger;

@ServerEndpoint("/chat/{username}")
@Singleton
public class ChatSoket {

	@Inject Logger logger;

	Map<String, Session> map = new ConcurrentHashMap<>();

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) {
		map.put(username, session);
	}

	@OnClose
	public void onClose(Session session, @PathParam("username") String username) {
		map.remove(username);
		broadcast(String.format("User %s left", username));
	}

	@OnError
	public void onError(Session session, @PathParam("username") String username, Throwable e) {
		broadcast("User " + username + "have gotten error : " + e.getMessage());
		logger.fatal(e.getMessage(), e);
	}

	@OnMessage
	public void onMessage(String message, @PathParam("username") String username) {
		if ("_ready_".equalsIgnoreCase(message)) 
			broadcast(String.format("User %s joined", username));
		else broadcast(String.format(">> %s: %s", username, message));
	}

	private void broadcast(String string) {
		map.values().forEach(s->s.getAsyncRemote().sendObject(string, r->{
			if (Objects.nonNull(r.getException())) {
				Throwable e = r.getException();
				logger.fatal(e.getMessage(), e);
			}
		}));
	}

}
