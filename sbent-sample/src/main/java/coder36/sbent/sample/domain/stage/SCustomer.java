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
	private Long id;
	
	@Column( length=30 )
	private String name;
	
	@Column( length=9 )
	private String nino;
	
	@ManyToOne
	private SReturn ret;	
	
	@OneToMany( mappedBy="customer", cascade=CascadeType.ALL )
	private List<STransaction> transactions = new ArrayList<STransaction>();
	
	/**
	 * Add Transaction
	 * @param t
	 */
	public void addTransaction( STransaction t ) {
		transactions.add( t );
		t.setCustomer( this );
	}

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
	 * @return the ret
	 */
	public SReturn getRet() {
		return ret;
	}

	/**
	 * @param ret the ret to set
	 */
	public void setRet(SReturn ret) {
		this.ret = ret;
	}

	/**
	 * @return the transactions
	 */
	public List<STransaction> getTransactions() {
		return transactions;
	}
	
}
