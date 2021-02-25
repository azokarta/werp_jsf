package general.tables;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "contract_type")
@javax.persistence.SequenceGenerator(name="seq_contract_type_id",sequenceName="seq_contract_type_id", allocationSize = 1)
public class ContractType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -357028152390321292L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_contract_type_id")
    private Long contract_type_id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "spras")
	private String spras;

	@Column(name = "business_area_id")
	private Long business_area_id;
	
	@Column(name = "matnr")
	private Long matnr;

	@Column(name = "created_by", nullable = true)
	private Integer created_by;

	@Column(name = "created_date", nullable = true)
	private Date created_date;

	@Column(name = "updated_by", nullable = true)
	private Integer updated_by;

	@Column(name = "updated_date", nullable = true)
	private Date updated_date;
	
	@Column(name = "war_fiz")
	private int war_fiz;
	
	@Column(name = "war_yur")
	private int war_yur;

	@Column(name = "tovar_category")
	private int tovar_category;
	
	@Column(name = "country_id")
    private Long countryId;  
    
	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	
	public static final int TOVARCAT_VACUUM_CLEANER = 1;
	public static final int TOVARCAT_WATER_FILTER = 2;

	public int getTovar_category() {
		return tovar_category;
	}

	public void setTovar_category(int tovar_category) {
		this.tovar_category = tovar_category;
	}

	public int getWar_fiz() {
		return war_fiz;
	}

	public void setWar_fiz(int war_fiz) {
		this.war_fiz = war_fiz;
	}

	public int getWar_yur() {
		return war_yur;
	}

	public void setWar_yur(int war_yur) {
		this.war_yur = war_yur;
	}

	public Long getContract_type_id() {
		return contract_type_id;
	}

	public void setContract_type_id(Long contract_type_id) {
		this.contract_type_id = contract_type_id;
	}
	
	public Long getBusiness_area_id() {
		return business_area_id;
	}

	public void setBusiness_area_id(Long business_area_id) {
		this.business_area_id = business_area_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public String getSpras() {
		return spras;
	}

	public void setSpras(String spras) {
		this.spras = spras;
	}
	
	public Integer getCreated_by() {
		return created_by;
	}

	public void setCreated_by(Integer created_by) {
		this.created_by = created_by;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public Integer getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(Integer updated_by) {
		this.updated_by = updated_by;
	}

	public Date getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(Date updated_date) {
		this.updated_date = updated_date;
	}

	public Long getMatnr() {
		return matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}
}
