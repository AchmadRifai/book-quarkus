package achmad.rifai.book.quarkus.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Chat implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6284188125949086891L;

	private long timestamp;
	private String to;
	private String dest;
	private String message;

}
