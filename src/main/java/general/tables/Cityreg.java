package general.tables; 
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cityreg")
@javax.persistence.SequenceGenerator(name="seq_cityreg_id",sequenceName="seq_cityreg_id", allocationSize = 1)
public class Cityreg implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7118912003312595725L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_cityreg_id")
    private Long idcityreg;
	
	@Column(name = "city_id")
    private Long city_id;
	
	@Column(name = "regname")
    private String regname;

	public Long getIdcityreg() {
		return idcityreg;
	}

	public void setIdcityreg(Long idcityreg) {
		this.idcityreg = idcityreg;
	}

	public Long getCity_id() {
		return city_id;
	}

	public void setCity_id(Long city_id) {
		this.city_id = city_id;
	}

	public String getRegname() {
		return regname;
	}

	public void setRegname(String regname) {
		this.regname = regname;
	}
    
}
