package general.tables;

import java.lang.Long;
import java.lang.String;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: AssetCatalog
 *
 */
@Entity
@Table(name="asset_catalog")
@javax.persistence.SequenceGenerator(name="seq_asset_catalog_id",sequenceName="seq_asset_catalog_id", allocationSize = 1)
public class AssetCatalog{

	   
	@Id
	@Column(name="ac_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_asset_catalog_id")
	private Long ac_id;

	public Long getAc_id() {
		return ac_id;
	}

	public AssetCatalog() {
		this.name_en = "";
		this.name_ru = "";
		this.name_tr = "";
	}
	
	@Column(name="name_ru")
	private String name_ru;
	
	@Column(name="name_en")
	private String name_en;
	
	@Column(name="name_tr")
	private String name_tr;
	
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
	
	public String getName(String lang){
		if(lang.equals("en")){
			return this.name_en;
		}
		else if(lang.equals("tr")){
			return this.name_tr;
		}
		
		return this.name_ru;
	}
}
