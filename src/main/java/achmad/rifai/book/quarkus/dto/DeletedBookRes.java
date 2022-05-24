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
public class DeletedBookRes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6835856059828144552L;

	private boolean deleted;

}
