package coder36.sbent.sample.domain.stage;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Index;

/**
 * Domain object
 * @author Mark Middleton
 */
@Entity
@org.hibernate.annotations.Table(appliesTo="SCustomer", indexes={@Index( name="ninoix", columnNames="nino")})
public class SCustomer {
	
	@Id @GeneratedValue
	public Long id;
	
	@Column( length=30 )
	public String name;
	
	@Column( length=9 )
	public String nino;
	
	@ManyToOne
	public SReturn ret;	
	
	@OneToMany( mappedBy="customer", cascade=CascadeType.ALL )
	public List<STransaction> transactions = new ArrayList<STransaction>();
	
	public void addTransaction( STransaction t ) {
		transactions.add( t );
		t.customer = this;
	}
}
