package coder36.sbent.sample.domain;

import javax.persistence.*;

/**
 * Domain object
 * @author Mark Middleton
 */
@Entity
public class Bank {

	@Id @GeneratedValue( strategy=GenerationType.AUTO)
	public long id;

	@Column( nullable=false, length=60)
	public String name;
	
}
