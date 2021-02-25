package general.tables; 
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "state")
@javax.persistence.SequenceGenerator(name="seq_state_id",sequenceName="seq_state_id",allocationSize=1)
public class State implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2531024588774356710L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_state_id")
    private Long idstate;
	
	@Column(name = "statename")
    private String statename;
	
	@Column(name = "countryid")
    private Long countryid;
	
	@Column(name = "statecode")
    private String statecode;
	
	@Column(name = "phonecode")
    private String phonecode;
    
	public Long getIdstate() {
		return idstate;
	}
	public void setIdstate(Long idstate) {
		this.idstate = idstate;
	}
	public String getStatename() {
		return statename;
	}
	public void setStatename(String statename) {
		this.statename = statename;
	}
	public Long getCountryid() {
		return countryid;
	}
	public void setCountryid(Long countryid) {
		this.countryid = countryid;
	}
	public String getStatecode() {
		return statecode;
	}
	public void setStatecode(String statecode) {
		this.statecode = statecode;
	}
	public String getPhonecode() {
		return phonecode;
	}
	public void setPhonecode(String phonecode) {
		this.phonecode = phonecode;
	}
    
}
