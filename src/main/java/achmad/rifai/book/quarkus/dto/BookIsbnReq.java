package achmad.rifai.book.quarkus.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class BookIsbnReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2632031355642456139L;

	@NotBlank(message = "ISBN must be not blank")
	private String isbn;

}
