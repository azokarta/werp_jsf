package general.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "business_area")
public class BusinessArea implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8630909398972088982L;

	@Id
    private Long business_area_id;

	public static final int AREA_ALL_EXCEPT_SERVICE = 0;
	public static final int AREA_ROBOCLEAN = 1;
	public static final int AREA_CEBILON = 2;
	public static final int AREA_RAINBOW = 3;
	public static final int AREA_REXWAT = 4;
	public static final int AREA_SERVICE = 5;
		
	@Column(name = "name")
	private String name;
	
	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "spras")
	private String spras;
 

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

	
}
