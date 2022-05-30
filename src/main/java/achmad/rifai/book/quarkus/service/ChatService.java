package achmad.rifai.book.quarkus.service;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

import achmad.rifai.book.quarkus.dto.Chat;
import achmad.rifai.book.quarkus.utils.BsonUtils;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Multi;

@Singleton
public class ChatService {

	@Inject ReactiveMongoClient reactiveMongoClient;

	@Inject MongoClient mongoClient;

	public InsertOneResult create(Chat chat) {
		return collectionSync().insertOne(chat);
	}

	public Multi<Chat> read() {
		return getCollection()
				.find();
	}

	public DeleteResult delete(Chat chat) {
		return collectionSync().deleteMany(BsonUtils.chatChangeDocument(chat));
	}

	public Multi<Chat> chatArea(Chat chat) {
		return getCollection().find(BsonUtils.chatArea(chat));
	}

	public Multi<Chat> chatFor(String username) {
		return getCollection().find(BsonUtils.chatFor(username));
	}

	private MongoCollection<Chat> collectionSync() {
		return mongoClient.getDatabase("chat").getCollection("chat", Chat.class);
	}

	private ReactiveMongoCollection<Chat> getCollection() {
		return reactiveMongoClient.getDatabase("chat").getCollection("chat", Chat.class);
	}

}
