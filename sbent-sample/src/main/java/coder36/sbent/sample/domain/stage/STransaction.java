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
	private Long id;
	
	@Column( nullable=false)
	private BigDecimal amount;
	
	@ManyToOne(optional=false,fetch=FetchType.LAZY)
	private SCustomer customer;

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
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the customer
	 */
	public SCustomer getCustomer() {
		return customer;
	}

	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(SCustomer customer) {
		this.customer = customer;
	}	
	
}
