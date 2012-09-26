package coder36.sbent.sample.domain;

import javax.persistence.*;

/**
 * Domain object
 * @author Mark Middleton
 */
@Entity
public class Bank {

	@Id @GeneratedValue( strategy=GenerationType.AUTO)
	private long id;

	@Column( nullable=false, length=60)
	private String name;
	
	@Version
	private Integer version;

	/**
	 * @return the version
	 */
	public Integer getVersion() {
		return version;
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
}
