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
public class Say implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2861945555449786819L;

	private String to;
	private String from;
	private String writer;
	private long time;

}
