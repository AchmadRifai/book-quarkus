package achmad.rifai.book.quarkus.codec;

import java.util.Objects;
import java.util.UUID;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import com.mongodb.MongoClientSettings;

import achmad.rifai.book.quarkus.dto.Book;

public class BookCodec implements CollectibleCodec<Book> {

	private final Codec<Document> codec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);

	@Override
	public void encode(BsonWriter writer, Book value, EncoderContext encoderContext) {
		Document doc = new Document();
		doc.put("writer", value.getWriter());
		doc.put("isbn", value.getIsbn());
		doc.put("title", value.getTitle());
		codec.encode(writer, doc, encoderContext);
	}

	@Override
	public Class<Book> getEncoderClass() {
		return Book.class;
	}

	@Override
	public Book decode(BsonReader reader, DecoderContext decoderContext) {
		Document doc = codec.decode(reader, decoderContext);
		return Book.builder()
				.isbn(doc.getString("isbn"))
				.title(doc.getString("title"))
				.writer(doc.getString("writer"))
				.build();
	}

	@Override
	public Book generateIdIfAbsentFromDocument(Book document) {
		if (!documentHasId(document)) 
			document.setIsbn(UUID.randomUUID().toString());
		return document;
	}

	@Override
	public boolean documentHasId(Book document) {
		return Objects.nonNull(document.getIsbn());
	}

	@Override
	public BsonValue getDocumentId(Book document) {
		return new BsonString(document.getIsbn());
	}

}
