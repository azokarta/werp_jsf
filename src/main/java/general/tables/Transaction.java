package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transaction")
@javax.persistence.SequenceGenerator(name = "seq_transaction_id", sequenceName = "seq_transaction_id", allocationSize = 1)
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_transaction_id")

	@Column(name = "transaction_id", nullable = true)
	private Long transaction_id;

	@Column(name = "transaction_code", nullable = true)
	private String transaction_code;

	/*
	 * @Column(name = "text45", nullable = true) private String text45;
	 */

	@Column(name = "name_ru")
	private String name_ru;

	@Column(name = "name_en")
	private String name_en;

	@Column(name = "name_tr")
	private String name_tr;

	@Column(name = "FRONT_URL")
	private String front_url;

	@Column(name = "FRONT_COMPONENT")
	private String front_component;

	@Column(name = "url", nullable = true)
	private String url;

	public Long getTransaction_id() {
		return transaction_id;
	}

	@Column(name = "folder_id")
	private Long folder_id;

	public Long getFolder_id() {
		return folder_id;
	}

	public void setFolder_id(Long folder_id) {
		this.folder_id = folder_id;
	}

	public void setTransaction_id(Long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getTransaction_code() {
		return transaction_code;
	}

	public void setTransaction_code(String transaction_code) {
		this.transaction_code = transaction_code;
	}

	/*
	 * public String getText45() { return text45; }
	 * 
	 * public void setText45(String text45) { this.text45 = text45; }
	 */

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName_ru() {
		return name_ru;
	}

	public void setName_ru(String name_ru) {
		this.name_ru = name_ru;
	}

	public String getName_en() {
		return name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
	}

	public String getName_tr() {
		return name_tr;
	}

	public void setName_tr(String name_tr) {
		this.name_tr = name_tr;
	}

	public String getFront_url() {
		return front_url;
	}

	public void setFront_url(String front_url) {
		this.front_url = front_url;
	}

	public String getFront_component() {
		return front_component;
	}

	public void setFront_component(String front_component) {
		this.front_component = front_component;
	}

	public String getName(String lang) {
		if (lang.equals("en") && this.name_en != null && this.name_en.length() > 0) {
			return this.name_en;
		} else if (lang.equals("tr") && this.name_tr != null && this.name_tr.length() > 0) {
			return this.name_tr;
		}

		return this.name_ru;
	}
}
