package achmad.rifai.book.quarkus.codec;

import org.bson.BsonReader;
import org.bson.BsonTimestamp;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import com.mongodb.MongoClientSettings;

import achmad.rifai.book.quarkus.dto.Chat;

public class ChatCodec implements CollectibleCodec<Chat> {

	private final Codec<Document> codec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);

	@Override
	public void encode(BsonWriter writer, Chat value, EncoderContext encoderContext) {
		Document doc = new Document();
		doc.put("timestamp", value.getTimestamp());
		doc.put("to", value.getTo());
		doc.put("dest", value.getDest());
		doc.put("message", value.getMessage());
		codec.encode(writer, doc, encoderContext);
	}

	@Override
	public Class<Chat> getEncoderClass() {
		return Chat.class;
	}

	@Override
	public Chat decode(BsonReader reader, DecoderContext decoderContext) {
		Document doc = codec.decode(reader, decoderContext);
		return Chat.builder()
				.dest(doc.getString("dest"))
				.message(doc.getString("message"))
				.timestamp(doc.getLong("timestamp"))
				.to(doc.getString("to"))
				.build();
	}

	@Override
	public Chat generateIdIfAbsentFromDocument(Chat document) {
		return document;
	}

	@Override
	public boolean documentHasId(Chat document) {
		return true;
	}

	@Override
	public BsonValue getDocumentId(Chat document) {
		return new BsonTimestamp(document.getTimestamp());
	}

}
