package general.tables;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * The persistent class for the HR_DOC_TRANSFER_ITEM database table.
 * 
 */
@Entity
@Table(name = "HR_DOC_TRANSFER_ITEM")
@NamedQuery(name = "HrDocTransferItem.findAll", query = "SELECT h FROM HrDocTransferItem h")
public class HrDocTransferItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.DATE)
	@Column(name = "BEGIN_DATE")
	private Date beginDate;

	@Column(name = "DEPARTMENT_ID")
	private Long departmentId;

	@Column(name = "FROM_SALARY_ID")
	private Long fromSalaryId;

	@Id
	@SequenceGenerator(name = "HR_DOC_TRANSFER_ITEM_ID_GENERATOR", sequenceName = "SEQ_HR_DOC_TRANSFER_ITEM_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HR_DOC_TRANSFER_ITEM_ID_GENERATOR")
	private Long id;

	@Column(name = "MANAGER_ID")
	private Long managerId;

	@Column(name = "POSITION_ID")
	private Long positionId;

	@Column(name = "SALARY_ID")
	private Long salaryId;

	@Column(name = "STATUS_ID")
	private Integer statusId;

	@Column(name = "BRANCH_ID")
	private Long branchId;

	@Column(name = "FROM_MANAGER_ID")
	private Long fromManagerId;

	@Column(name = "AMOUNT")
	private Double amount;

	@Column(name = "CURRENCY")
	private String currency;

	@Column(name = "BUSSINESS_AREA_ID")
	private Long bussinessAreaId;

	// bi-directional many-to-one association to HrDocTransfer
	@ManyToOne
	@JoinColumn(name = "DOC_ID", referencedColumnName = "ID")
	private HrDocTransfer hrDocTransfer;

	public HrDocTransferItem() {
		setStatusId(0);
		setSalaryId(0L);
		setFromManagerId(0L);
		setAmount(0D);
		setBussinessAreaId(0L);
	}

	public Date getBeginDate() {
		return this.beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Long getDepartmentId() {
		return this.departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public Long getFromSalaryId() {
		return fromSalaryId;
	}

	public void setFromSalaryId(Long fromSalaryId) {
		this.fromSalaryId = fromSalaryId;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getManagerId() {
		return this.managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public Long getPositionId() {
		return this.positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}

	public Long getSalaryId() {
		return this.salaryId;
	}

	public void setSalaryId(Long salaryId) {
		this.salaryId = salaryId;
	}

	public Integer getStatusId() {
		return this.statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public HrDocTransfer getHrDocTransfer() {
		return this.hrDocTransfer;
	}

	public void setHrDocTransfer(HrDocTransfer hrDocTransfer) {
		this.hrDocTransfer = hrDocTransfer;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public Long getFromManagerId() {
		return fromManagerId;
	}

	public void setFromManagerId(Long fromManagerId) {
		this.fromManagerId = fromManagerId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Long getBussinessAreaId() {
		return bussinessAreaId;
	}

	public void setBussinessAreaId(Long bussinessAreaId) {
		this.bussinessAreaId = bussinessAreaId;
	}

	@Transient
	private Salary fromSalary;

	public Salary getFromSalary() {
		return fromSalary;
	}

	public void setFromSalary(Salary fromSalary) {
		this.fromSalary = fromSalary;
	}

	/**
	 * Старый менеджер
	 */
	@Transient
	private Staff fromManager;

	public Staff getFromManager() {
		return fromManager;
	}

	public void setFromManager(Staff fromManager) {
		this.fromManager = fromManager;
	}

	/**
	 * Новый манагер
	 */
	@Transient
	private Staff manager;

	public Staff getManager() {
		return manager;
	}

	public void setManager(Staff manager) {
		this.manager = manager;
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see java.lang.Object#hashCode()
	// */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fromSalaryId == null) ? 0 : fromSalaryId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HrDocTransferItem other = (HrDocTransferItem) obj;
		if (fromSalaryId == null) {
			if (other.fromSalaryId != null)
				return false;
		} else if (!fromSalaryId.equals(other.fromSalaryId))
			return false;
		return true;
	}

}