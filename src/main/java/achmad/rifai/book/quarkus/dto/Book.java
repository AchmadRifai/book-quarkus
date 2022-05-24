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
public class Book implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1760819539040923111L;

	@NotBlank(message = "Title must not be blank")
	private String title;

	@NotBlank(message = "ISBN must not be blank")
	private String isbn;

	@NotBlank(message = "writer must not be blank")
	private String writer;

}
	