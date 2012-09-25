package coder36.sbent.sample.domain.stage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import coder36.sbent.sample.domain.Bank;

/**
 * Domain object
 * @author Mark Middleton
 */
@Entity
public class SReturn {

	@Id @GeneratedValue
	public long id;
	
	@Column
	public String bankName;
	
	@Column @Temporal( TemporalType.DATE)
	public Date periodEnd;
	
	@ManyToOne(optional=true )
	public Bank bank;
		
	@OneToMany( mappedBy="ret", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	public List<SCustomer> customers = new ArrayList<SCustomer>();
	
	public void addCustomer( SCustomer c ) {
		customers.add( c );
		c.ret = this;
	}
}
