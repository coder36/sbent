package coder36.sbent.sample.domain;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import org.hibernate.annotations.Index;

/**
 * Domain Object
 * @author Mark Middleton
 */
@Entity
@org.hibernate.annotations.Table(appliesTo="Customer", indexes={@Index( name="ninoix", columnNames="nino")})
public class Customer {
	
	@Id @GeneratedValue( strategy=GenerationType.AUTO)
	private long id;
	
	@Column( nullable=false, length=60 )
	private String name;
	
	@Column( nullable=false, length=60 )
	private String nino;	
	
	@ManyToMany( fetch=FetchType.LAZY)
	@JoinTable ( name = "BankCustomer", 
		         joinColumns={ @JoinColumn( name="customer_id" )},
		         inverseJoinColumns= {@JoinColumn( name="bank_id" ) } )
	private Set<Bank> banks = new HashSet<Bank>();

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

	/**
	 * @return the nino
	 */
	public String getNino() {
		return nino;
	}

	/**
	 * @param nino the nino to set
	 */
	public void setNino(String nino) {
		this.nino = nino;
	}

	/**
	 * @return the banks
	 */
	public Set<Bank> getBanks() {
		return banks;
	}
	
}
