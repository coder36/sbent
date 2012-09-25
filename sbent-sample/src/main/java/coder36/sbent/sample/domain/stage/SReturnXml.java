package coder36.sbent.sample.domain.stage;

import javax.persistence.*;

/**
 * Domain object
 * @author Mark Middleton
 */
@Entity
public class SReturnXml {

	@Id @GeneratedValue( strategy=GenerationType.AUTO)
	public Long id;
	
	@Column( nullable=false)
	@Lob
	public String xml;
	
	@Column( nullable=false)
	@Enumerated(EnumType.STRING)
	public Status status = Status.NEW;
}
