package achmad.rifai.book.quarkus.config;

import java.util.HashMap;
import java.util.Map;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import achmad.rifai.book.quarkus.codec.BookCodec;
import achmad.rifai.book.quarkus.codec.ChatCodec;
import achmad.rifai.book.quarkus.dto.Book;
import achmad.rifai.book.quarkus.dto.Chat;

public class ServiceBookProvider implements CodecProvider {

	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		Map<Class<?>, Codec<T>> map = mapClass();
		if (map.containsKey(clazz)) 
			return map.get(clazz);
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T> Map<Class<?>, Codec<T>> mapClass() {
		Map<Class<?>, Codec<T>> map = new HashMap<>();
		map.put(Book.class, (Codec<T>) new BookCodec());
		map.put(Chat.class, (Codec<T>) new ChatCodec());
		return map;
	}

}
