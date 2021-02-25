package general.tables;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "contract_history")
@javax.persistence.SequenceGenerator(name="seq_contract_history_id",sequenceName="seq_contract_history_id", allocationSize = 1)
public class ContractHistory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4314522644764752919L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_contract_history_id")
    private Long id;
	
	public ContractHistory() {
		Calendar cal = Calendar.getInstance();
		this.rec_date = cal.getTime();
	}
	
	public ContractHistory(Long conId) {
		this.contract_id = conId;
		Calendar cal = Calendar.getInstance();
		this.rec_date = cal.getTime();
	}
	
	
	@Column(name = "contract_id")
	private Long contract_id;

	@Column(name = "rec_date")
	private Date rec_date;
	
	@Column(name = "oper_on")
	private Long oper_on;
	
	/* oper_on
	 * 1 - Status
	 * 2 - Contract_type

	 * 20 - Price
	 * 21 - Payment graphic

	 * 30 - Tovar

	 * 40 - Dealer
	 * 41 - SD - Skidka_dilera
	 * 42 - AD - Akciya ot dilera

	 * 50 - DemoSec
	 * 60 - Collector
	  
	 * 80 - Promo 

	 * 90 - Rekomendatel 
	 * 91 - SK - Skidka rekomendatelyu
	 * 92 - VZ - Voznagrajdenie rekomendatelyu
	 * 93 - SR - Skidka OT Rekomendatelya
	 
	 * 100 - Service branch 

	 */

	@Column(name = "oper_type")
	private Long oper_type;
	
	/* oper_type
	 * 1 - Add
	 * 2 - Remove
	 * 3 - Change
	 * 4 - Given
	 * 5 - Returned
	 */

	@Column(name = "old_id")
	private Long old_id;
	
	@Column(name = "new_id")
	private Long new_id;
	
	@Column(name = "info")
	private String info;

	@Column(name = "processed")
	private int processed;
	
	@Column(name = "user_id")
	private Long user_id;
	
	@Column(name = "old_text")
	private String old_text;
	
	@Column(name = "new_text")
	private String new_text;
	
	public String getOld_text() {
		return old_text;
	}

	public void setOld_text(String old_text) {
		this.old_text = old_text;
	}

	public String getNew_text() {
		return new_text;
	}

	public void setNew_text(String new_text) {
		this.new_text = new_text;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Long getOper_on() {
		return oper_on;
	}

	public void setOper_on(Long oper_on) {
		this.oper_on = oper_on;
	}

	public int getProcessed() {
		return processed;
	}

	public void setProcessed(int processed) {
		this.processed = processed;
	}

	public Long getContract_id() {
		return contract_id;
	}

	public void setContract_id(Long contract_id) {
		this.contract_id = contract_id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getRec_date() {
		return rec_date;
	}

	public void setRec_date(Date rec_date) {
		this.rec_date = rec_date;
	}

	public Long getOper_type() {
		return oper_type;
	}

	public void setOper_type(Long oper_type) {
		this.oper_type = oper_type;
	}

	public Long getOld_id() {
		return old_id;
	}

	public void setOld_id(Long old_id) {
		this.old_id = old_id;
	}

	public Long getNew_id() {
		return new_id;
	}

	public void setNew_id(Long new_id) {
		this.new_id = new_id;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
