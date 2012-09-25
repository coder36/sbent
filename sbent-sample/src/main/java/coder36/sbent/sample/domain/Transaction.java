package coder36.sbent.sample.domain;

import java.math.BigDecimal;

import javax.persistence.*;

/**
 * Domain Object
 * @author Mark Middleton
 */
@Entity
public class Transaction {
	
	@Id @GeneratedValue( strategy=GenerationType.AUTO)
	public long id;
	
	@ManyToOne
	public Customer customer;
	
	@ManyToOne
	public Bank bank;
	
	@Column( nullable=false)
	public BigDecimal amount;
}
