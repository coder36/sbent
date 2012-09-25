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
	public long id;
	
	@Column( nullable=false, length=60 )
	public String name;
	
	@Column( nullable=false, length=60 )
	public String nino;	
	
	@ManyToMany
	@JoinTable ( name = "BankCustomer", 
		         joinColumns={ @JoinColumn( name="customer_id" )},
		         inverseJoinColumns= {@JoinColumn( name="bank_id" ) } )
	public Set<Bank> banks = new HashSet<Bank>();
}
