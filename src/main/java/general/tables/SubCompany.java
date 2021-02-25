package general.tables;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: SubCompany
 *
 */
@Entity
@Table(name="sub_company")
@javax.persistence.SequenceGenerator(name="seq_sub_company_id",sequenceName="seq_sub_company_id",allocationSize=1)

public class SubCompany{

	   
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_sub_company_id")
	private Long sc_id;   
	
	@Column(name="name_ru")
	private String name_ru;   
	
	@Column(name="type")
	private String type;   

	@Column(name="created_date")
	private Date created_date;   
	
	@Column(name="updated_date")
	private Date updated_date;   

	@Column(name="created_by")
	private Long created_by;
	
	@Column(name="updated_by")
	private Long updated_by;   
	
	@Column(name="name_en")
	private String name_en;   
	
	@Column(name="name_tr")
	private String name_tr;
	
	@Column(name="bukrs")
	private String bukrs;
	
	@Column(name="CLOSED_DATE")
	private Date closed_date;
	
	@Column(name="director_id")
	private Long directorId;
	
	public Date getClosed_date() {
		return closed_date;
	}
	public void setClosed_date(Date closed_date) {
		this.closed_date = closed_date;
	}
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	
	public SubCompany() {
		this.sc_id = 0L;
	}   
	public Long getSc_id() {
		return this.sc_id;
	}

	public void setSc_id(Long sc_id) {
		this.sc_id = sc_id;
	}   
	public String getName_ru() {
		return this.name_ru;
	}

	public void setName_ru(String name_ru) {
		this.name_ru = name_ru;
	}   
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}   
	public Date getCreated_date() {
		return this.created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}   
	public Date getUpdated_date() {
		return this.updated_date;
	}

	public void setUpdated_date(Date updated_date) {
		this.updated_date = updated_date;
	}   
	public Long getCreated_by() {
		return this.created_by;
	}

	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}   
	public Long getUpdated_by() {
		return this.updated_by;
	}

	public void setUpdated_by(Long updated_by) {
		this.updated_by = updated_by;
	}   
	public String getName_en() {
		return this.name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
	}   
	public String getName_tr() {
		return this.name_tr;
	}

	public void setName_tr(String name_tr) {
		this.name_tr = name_tr;
	}
	public Long getDirectorId() {
		return directorId;
	}
	public void setDirectorId(Long directorId) {
		this.directorId = directorId;
	}
   
	
}
