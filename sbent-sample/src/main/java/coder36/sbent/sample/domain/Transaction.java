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
	private long id;
	
	@ManyToOne( optional=false, fetch=FetchType.LAZY)
	private Customer customer;
	
	@ManyToOne
	private Bank bank;
	
	@Column( nullable=false)
	private BigDecimal amount;

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
	 * @return the customer
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * @return the bank
	 */
	public Bank getBank() {
		return bank;
	}

	/**
	 * @param bank the bank to set
	 */
	public void setBank(Bank bank) {
		this.bank = bank;
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
}
