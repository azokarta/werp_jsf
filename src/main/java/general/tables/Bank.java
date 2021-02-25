package general.tables;

import java.lang.Long;
import java.lang.String;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: AssetCatalog
 *
 */
@Entity
@Table(name="bank")
@javax.persistence.SequenceGenerator(name="seq_bank_id",sequenceName="seq_bank_id", allocationSize = 1)
public class Bank{

	public Bank() {
		this.swift_code = "";
		this.name_ru = "";
		this.name_en = "";
		this.name_tr = "";
		this.address = "";
		this.created_date = Calendar.getInstance().getTime();
		this.updated_date = Calendar.getInstance().getTime();
	}
	   
	@Id
	@Column(name="b_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_bank_id")
	private Long b_id;

	@Column(name="name_ru")
	private String name_ru;
	
	@Column(name="name_en")
	private String name_en;
	
	@Column(name="name_tr")
	private String name_tr;
	
	@Column(name="address")
	private String address;
	
	@Column(name="swift_code")
	private String swift_code;
	
	@Column(name = "created_by", nullable = true)
	private Long created_by;
	
	@Column(name = "created_date", nullable = true)
	private Date created_date;
	
	@Column(name = "updated_by", nullable = true)
	private Long updated_by;
	
	@Column(name = "updated_date", nullable = true)
	private Date updated_date;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSwift_code() {
		return swift_code;
	}

	public void setSwift_code(String swift_code) {
		this.swift_code = swift_code;
	}

	public Long getCreated_by() {
		return created_by;
	}

	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public Long getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(Long updated_by) {
		this.updated_by = updated_by;
	}

	public Date getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(Date updated_date) {
		this.updated_date = updated_date;
	}

	public Long getB_id() {
		return b_id;
	}

	public String getName(String lang){
		if(lang.equals("en")){
			return this.name_en;
		}else if(lang.equals("tr")){
			return this.name_tr;
		}
		
		return this.name_ru;
	}
}
