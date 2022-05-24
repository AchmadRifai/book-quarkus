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
public class MessageDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5006215416377140903L;

	private String reason;
	private String message;

}
