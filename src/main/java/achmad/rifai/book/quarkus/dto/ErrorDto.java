package achmad.rifai.book.quarkus.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ErrorDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2584210519326480436L;

	private long timestamp;
	private int status;
	private String error;
	private String exception;
	private List<MessageDto> messages;
	private String path;

}
