package coder36.sbent.sample.domain.stage;

import java.math.BigDecimal;

import javax.persistence.*;

/**
 * Domain Object
 * @author Mark Middleton
 */
@Entity
public class STransaction {

	@Id @GeneratedValue
	public Long id;
	
	@Column( nullable=false)
	public BigDecimal amount;
	
	@ManyToOne
	public SCustomer customer;
}
