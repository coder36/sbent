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
	private long id;
	
	@Column
	private String bankName;
	
	@Column @Temporal( TemporalType.DATE)
	private Date periodEnd;
	
	@ManyToOne(optional=true )
	private Bank bank;
		
	@OneToMany( mappedBy="ret", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<SCustomer> customers = new ArrayList<SCustomer>();
	
	public void addCustomer( SCustomer c ) {
		customers.add( c );
		c.setRet( this );
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
	 * @return the bankName
	 */
	public String getBankName() {
		return bankName;
	}

	/**
	 * @param bankName the bankName to set
	 */
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	/**
	 * @return the periodEnd
	 */
	public Date getPeriodEnd() {
		return periodEnd;
	}

	/**
	 * @param periodEnd the periodEnd to set
	 */
	public void setPeriodEnd(Date periodEnd) {
		this.periodEnd = periodEnd;
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
	 * @return the customers
	 */
	public List<SCustomer> getCustomers() {
		return customers;
	}
}
