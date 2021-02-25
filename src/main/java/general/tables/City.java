package general.tables; 
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "city")
@javax.persistence.SequenceGenerator(name="seq_city_id",sequenceName="seq_city_id", allocationSize = 1)
public class City implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7020742051584132839L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_city_id")
    private Long idcity;
	
	@Column(name = "name")
    private String name;
	
	@Column(name = "countryid")
    private Long countryid;
	
	@Column(name = "stateid")
    private Long stateid;
	
	@Column(name = "phonecode")
    private String phonecode;
    
	@Column(name = "citycode")
    private String citycode;

	public Long getIdcity() {
		return idcity;
	}

	public void setIdcity(Long idcity) {
		this.idcity = idcity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCountryid() {
		return countryid;
	}

	public void setCountryid(Long countryid) {
		this.countryid = countryid;
	}

	public Long getStateid() {
		return stateid;
	}

	public void setStateid(Long stateid) {
		this.stateid = stateid;
	}

	public String getPhonecode() {
		return phonecode;
	}

	public void setPhonecode(String phonecode) {
		this.phonecode = phonecode;
	}

	public String getCitycode() {
		return citycode;
	}

	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}
    
    
}
