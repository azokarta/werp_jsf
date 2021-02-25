package general.tables;
import javax.persistence.Column;
import javax.persistence.Entity; 
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name = "LEGAL_ENTITY")
@javax.persistence.SequenceGenerator(name = "SEQ_LEGAL_ENTITY_ID", sequenceName = "SEQ_LEGAL_ENTITY_ID", allocationSize = 1)
public class LegalEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_LEGAL_ENTITY_ID")
    private Long id;

	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "name")
	private String name;

	
	public Long getId() {
		return id;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
		

}
