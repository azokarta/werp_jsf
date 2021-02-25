package general.tables; 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "expence_type")
@javax.persistence.SequenceGenerator(name="seq_expence_type_id",sequenceName="seq_expence_type_id", allocationSize = 1)
public class ExpenceType {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_expence_type_id")
    private Long et_id;
	
	public Long getEt_id() {
		return et_id;
	}

	public void setEt_id(Long et_id) {
		this.et_id = et_id;
	}

	@Column(name = "name")
    private String name;
	
	@Column(name = "customer_id")
    private Long customer_id;
	
	
	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
