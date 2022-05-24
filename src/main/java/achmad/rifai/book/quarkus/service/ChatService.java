package achmad.rifai.book.quarkus.service;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

import achmad.rifai.book.quarkus.dto.Chat;
import achmad.rifai.book.quarkus.utils.BsonUtils;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@Singleton
public class ChatService {

	@Inject ReactiveMongoClient reactiveMongoClient;

	public Uni<InsertOneResult> create(Chat chat) {
		return getCollection()
				.insertOne(chat);
	}

	public Multi<Chat> read() {
		return getCollection()
				.find();
	}

	public Uni<DeleteResult> delete(Chat chat) {
		return getCollection()
				.deleteMany(BsonUtils.chatChangeDocument(chat));
	}

	public Multi<Chat> chatArea(Chat chat) {
		return getCollection().find(BsonUtils.chatArea(chat));
	}

	public Multi<Chat> chatFor(String username) {
		return getCollection().find(BsonUtils.chatFor(username));
	}

	private ReactiveMongoCollection<Chat> getCollection() {
		return reactiveMongoClient.getDatabase("chat").getCollection("chat", Chat.class);
	}

}
