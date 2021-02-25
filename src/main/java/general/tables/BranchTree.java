package general.tables; 
import javax.persistence.Column;
import javax.persistence.Entity; 
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name = "branchtree")
public class BranchTree {
	@Id 
    private Long branch_id;
	
	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "parent_branch_id")
	private Long parent_branch_id;
	
	@Column(name = "business_area_id")
	private Long business_area_id;
	
	@Column(name = "country_id")
	private Long country_id;
	
	@Column(name = "text45")
	private String text45;
	
	@Column(name = "type")
	private Long type;

	@Column(name = "currency")
	private String currency;

	@Column(name = "state_id")
	private Long state_id;

	
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Long getState_id() {
		return state_id;
	}

	public void setState_id(Long state_id) {
		this.state_id = state_id;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getParent_branch_id() {
		return parent_branch_id;
	}

	public void setParent_branch_id(Long parent_branch_id) {
		this.parent_branch_id = parent_branch_id;
	}

	public String getText45() {
		return text45;
	}

	public void setText45(String text45) {
		this.text45 = text45;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public Long getBusiness_area_id() {
		return business_area_id;
	}

	public void setBusiness_area_id(Long business_area_id) {
		this.business_area_id = business_area_id;
	}

	public Long getCountry_id() {
		return country_id;
	}

	public void setCountry_id(Long country_id) {
		this.country_id = country_id;
	}


	
	

}
