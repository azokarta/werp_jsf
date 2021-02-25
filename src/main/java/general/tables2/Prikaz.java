package general.tables2;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity 
@Table(name = "prikaz") 
@javax.persistence.SequenceGenerator(name="seq_id_prikaz",sequenceName="seq_id_prikaz", allocationSize = 1)
public class Prikaz implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_id_prikaz")
	@Column(name = "id_prikaz")
	private Long id_prikaz;	
	
	@Column(name = "type_prikaz")
	private int type_prikaz;
	
	@Column(name = "date_prikaz")
	private Date date_prikaz;

	@Column(name = "name_prikaz")
	private String name_prikaz;
	
	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "position_id")
	private Long position_id;
	
	@Column(name = "status_id")
	private int status_id;
	
	@Column(name = "parent_id_prikaz")
	private Long parent_id_prikaz;
	
	@Column(name = "creator_id")
	private Long creator_id;
	
	@Column(name = "version")
	private int version;
	
	@Column(name = "code")
	private String code;
	
	@Column(name = "branch_id")
	private Long branch_id;
	
	@Column(name = "department_id")
	private int department_id;
	
	public int getDepartment_id() {
		return department_id;
	}

	public void setDepartment_id(int department_id) {
		this.department_id = department_id;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getId_prikaz() {
		return id_prikaz;
	}

	public void setId_prikaz(Long id_prikaz) {
		this.id_prikaz = id_prikaz;
	}

	public int getType_prikaz() {
		return type_prikaz;
	}

	public void setType_prikaz(int type_prikaz) {
		this.type_prikaz = type_prikaz;
	}

	public Date getDate_prikaz() {
		return date_prikaz;
	}

	public void setDate_prikaz(Date date_prikaz) {
		this.date_prikaz = date_prikaz;
	}

	public String getName_prikaz() {
		return name_prikaz;
	}

	public void setName_prikaz(String name_prikaz) {
		this.name_prikaz = name_prikaz;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getPosition_id() {
		return position_id;
	}

	public void setPosition_id(Long position_id) {
		this.position_id = position_id;
	}

	public int getStatus_id() {
		return status_id;
	}

	public void setStatus_id(int status_id) {
		this.status_id = status_id;
	}

	public Long getParent_id_prikaz() {
		return parent_id_prikaz;
	}

	public void setParent_id_prikaz(Long parent_id_prikaz) {
		this.parent_id_prikaz = parent_id_prikaz;
	}

	public Long getCreator_id() {
		return creator_id;
	}

	public void setCreator_id(Long creator_id) {
		this.creator_id = creator_id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	
	public static int new_status = 0;
	public static int sent_status = 1;
	public static int confirmed_status=2;
	public static int cancelled_status = 3;
	
	
	public Prikaz()
	{
		this.status_id=new_status;
		this.version=1;
	}
	
	public static int prikaz_type = 1;
	
}
