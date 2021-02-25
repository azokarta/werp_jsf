package general.tables;
import javax.persistence.Column;
import javax.persistence.Entity; 
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name = "bank_partner")
public class BankPartner {
	@Id 
    private Long id;
	
	@Column(name = "name_short")
	private String name_short;
	
	@Column(name = "name_full")
	private String name_full;	
	
	@Column(name = "country_id")
	private Long country_id;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName_short() {
		return name_short;
	}

	public void setName_short(String name_short) {
		this.name_short = name_short;
	}

	public String getName_full() {
		return name_full;
	}

	public void setName_full(String name_full) {
		this.name_full = name_full;
	}

	public Long getCountry_id() {
		return country_id;
	}

	public void setCountry_id(Long country_id) {
		this.country_id = country_id;
	}

}
