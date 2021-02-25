package general.tables; 

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "contract_promos")
@javax.persistence.SequenceGenerator(name="seq_contract_promos_id",sequenceName="seq_contract_promos_id", allocationSize = 1)
public class ContractPromos implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1892226678765477569L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_contract_promos_id")
	private Long id;
	
	@Column(name = "contract_id")
	private Long contract_id;
	
	@Column(name = "promo_id")
	private Long promo_id;

	public Long getContract_id() {
		return contract_id;
	}

	public void setContract_id(Long contract_id) {
		this.contract_id = contract_id;
	}

	public Long getPromo_id() {
		return promo_id;
	}

	public void setPromo_id(Long promo_id) {
		this.promo_id = promo_id;
	}

	public Long getId() {
		return id;
	}
	 
}
