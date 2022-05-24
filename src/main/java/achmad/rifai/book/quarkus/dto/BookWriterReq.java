package achmad.rifai.book.quarkus.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class BookWriterReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8850828511740759334L;

	@NotBlank
	@NotEmpty
	private String writer;

}
