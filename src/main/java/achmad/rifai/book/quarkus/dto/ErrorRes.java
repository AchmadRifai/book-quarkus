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
public class ErrorRes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -744971505865636387L;

	private ErrorDto error;

}
