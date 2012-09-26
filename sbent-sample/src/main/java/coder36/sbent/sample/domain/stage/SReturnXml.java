package coder36.sbent.sample.domain.stage;

import javax.persistence.*;

/**
 * Domain object
 * @author Mark Middleton
 */
@Entity
public class SReturnXml {

	@Id @GeneratedValue( strategy=GenerationType.AUTO)
	private Long id;
	
	@Column( nullable=false)
	@Lob
	private String xml;
	
	@Column( nullable=false)
	@Enumerated(EnumType.STRING)
	private Status status = Status.NEW;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the xml
	 */
	public String getXml() {
		return xml;
	}

	/**
	 * @param xml the xml to set
	 */
	public void setXml(String xml) {
		this.xml = xml;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
	
}
