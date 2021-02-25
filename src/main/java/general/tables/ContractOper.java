package general.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "contract_oper")
@javax.persistence.SequenceGenerator(name="seq_contract_oper_id",sequenceName="seq_contract_oper_id", allocationSize = 1)
public class ContractOper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2987503371767088079L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_contract_oper_id")
    private Long id;

	@Column(name = "en")
	private String oper_name_en;

	@Column(name = "ru")
	private String oper_name_ru;

	@Column(name = "tr")
	private String oper_name_tr;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOper_name_en() {
		return oper_name_en;
	}

	public void setOper_name_en(String oper_name_en) {
		this.oper_name_en = oper_name_en;
	}

	public String getOper_name_ru() {
		return oper_name_ru;
	}

	public void setOper_name_ru(String oper_name_ru) {
		this.oper_name_ru = oper_name_ru;
	}

	public String getOper_name_tr() {
		return oper_name_tr;
	}

	public void setOper_name_tr(String oper_name_tr) {
		this.oper_name_tr = oper_name_tr;
	}

}
