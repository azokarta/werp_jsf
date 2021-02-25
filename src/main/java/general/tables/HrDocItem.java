package general.tables;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * The persistent class for the HR_DOC_ITEM database table.
 * 
 */
@Entity
@Table(name = "HR_DOC_ITEM")
@NamedQuery(name = "HrDocItem.findAll", query = "SELECT h FROM HrDocItem h")
public class HrDocItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "HR_DOC_ITEM_ID_GENERATOR", sequenceName = "SEQ_HR_DOC_ITEM_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HR_DOC_ITEM_ID_GENERATOR")
	private Long id;

	@Temporal(TemporalType.DATE)
	@Column(name = "END_DATE")
	private Date endDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "BEGIN_DATE")
	private Date beginDate;

	@Column(name = "STAFF_ID")
	private Long staffId;

	@Column(name = "SALARY_ID")
	private Long salaryId;

	@Column(name = "MANAGER_ID")
	private Long managerId;

	@Column(name = "BRANCH_ID")
	private Long branchId;

	@Column(name = "POSITION_ID")
	private Long positionId;

	@Column(name = "DEPARTMENT_ID")
	private Long departmentId;

	private Double amount;

	@Column(name = "BUSINESS_AREA_ID")
	private Long businessAreaId;

	@Column(name = "OLD_MANAGER_ID")
	private Long oldManagerId;

	private String currency;

	private String bukrs;

	// bi-directional many-to-one association to HrDoc
	@ManyToOne
	@JoinColumn(name = "DOC_ID", referencedColumnName = "ID")
	private HrDoc hrDoc;

	public HrDocItem() {
		setSalaryId(0L);
		setAmount(0D);
		setBusinessAreaId(0L);
		setBranchId(0L);
		setPositionId(0L);
		setDepartmentId(0L);
		setManagerId(0L);
		setOldManagerId(0L);
	}

	public Long getOldManagerId() {
		return oldManagerId;
	}

	public void setOldManagerId(Long oldManagerId) {
		this.oldManagerId = oldManagerId;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId == null ? 0L : staffId;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId == null ? 0L : managerId;
	}

	public Long getSalaryId() {
		return this.salaryId;
	}

	public void setSalaryId(Long salaryId) {
		this.salaryId = salaryId == null ? 0L : salaryId;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		if (branchId == null) {
			this.branchId = 0L;
		} else {
			this.branchId = branchId;
		}
	}

	public Long getPositionId() {
		return positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId == null ? 0L : positionId;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId == null ? 0L : departmentId;
	}

	public Long getBusinessAreaId() {
		return businessAreaId;
	}

	public void setBusinessAreaId(Long businessAreaId) {
		this.businessAreaId = businessAreaId == null ? 0L : businessAreaId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount == null ? 0D : amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public HrDoc getHrDoc() {
		return this.hrDoc;
	}

	public void setHrDoc(HrDoc hrDoc) {
		this.hrDoc = hrDoc;
	}

	@Transient
	private Staff staff;

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	@Transient
	private Salary salary;

	public Salary getSalary() {
		return salary;
	}

	public void setSalary(Salary salary) {
		this.salary = salary;
	}

	@Transient
	private Staff manager;

	public Staff getManager() {
		return manager;
	}

	public void setManager(Staff manager) {
		this.manager = manager;
	}

	@Transient
	private Staff oldManager;

	public Staff getOldManager() {
		return oldManager;
	}

	public void setOldManager(Staff oldManager) {
		this.oldManager = oldManager;
	}

}