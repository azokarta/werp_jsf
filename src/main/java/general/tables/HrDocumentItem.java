package general.tables;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 * The persistent class for the HR_DOCUMENT_ITEM database table.
 * 
 */
@Entity
@Table(name = "HR_DOCUMENT_ITEM")
public class HrDocumentItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -599509774773651040L;

	@Id
	@SequenceGenerator(name = "HR_DOCUMENT_ITEM_ID_GENERATOR", sequenceName = "SEQ_HR_DOCUMENT_ITEM_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HR_DOCUMENT_ITEM_ID_GENERATOR")
	private Long id;

	@Column(name = "NEW_AMOUNT")
	private Double newAmount;

	@Column(name = "NEW_BRANCH_ID")
	private Long newBranchId;

	@Column(name = "NEW_BUKRS")
	private String newBukrs;

	@Column(name = "NEW_DEPARTMENT_ID")
	private Long newDepartmentId;

	@Column(name = "NEW_POSITION_ID")
	private Long newPositionId;

	@Column(name = "SALARY_ID")
	private Long salaryId;

	@Column(name = "STATUS_ID")
	private Integer statusId;

	@Column(name = "NEW_MANAGER_ID")
	private Long newManagerId;

	@Column(name = "STAFF_ID")
	private Long staffId;

	@Column(name = "NEW_CURRENCY")
	private String newCurrency;

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	@Column(name = "NEW_BEGIN_DATE")
	private Date newBeginDate;

	public HrDocumentItem() {
		this.newAmount = 0D;
		this.newBranchId = 0L;
		this.newBukrs = "";
		this.newDepartmentId = 0L;
		this.newPositionId = 0L;
		this.salaryId = 0L;
		this.statusId = 0;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// public Long getHrDocumentId() {
	// return this.hrDocumentId;
	// }
	//
	// public void setHrDocumentId(Long hrDocumentId) {
	// this.hrDocumentId = hrDocumentId;
	// }

	public static final Integer STATUS_DONE = 1;

	public Double getNewAmount() {
		return this.newAmount;
	}

	public void setNewAmount(Double newAmount) {
		this.newAmount = newAmount;
	}

	public Long getNewBranchId() {
		return this.newBranchId;
	}

	public void setNewBranchId(Long newBranchId) {
		this.newBranchId = newBranchId;
	}

	public String getNewBukrs() {
		return this.newBukrs;
	}

	public void setNewBukrs(String newBukrs) {
		this.newBukrs = newBukrs;
	}

	public Long getNewDepartmentId() {
		return this.newDepartmentId;
	}

	public void setNewDepartmentId(Long newDepartmentId) {
		this.newDepartmentId = newDepartmentId;
	}

	public Long getNewPositionId() {
		return this.newPositionId;
	}

	public void setNewPositionId(Long newPositionId) {
		this.newPositionId = newPositionId;
	}

	public Long getSalaryId() {
		return this.salaryId;
	}

	public void setSalaryId(Long salaryId) {
		this.salaryId = salaryId;
	}

	// public Long getStaffId() {
	// return this.staffId;
	// }
	//
	// public void setStaffId(Long staffId) {
	// this.staffId = staffId;
	// }

	public Date getNewBeginDate() {
		return newBeginDate;
	}

	public void setNewBeginDate(Date newBeginDate) {
		this.newBeginDate = newBeginDate;
	}

	public Long getNewManagerId() {
		return newManagerId;
	}

	public void setNewManagerId(Long newManagerId) {
		this.newManagerId = newManagerId;
	}

	@ManyToOne
	@JoinColumn(name = "hr_document_id")
	private HrDocument hrDocument;

	public HrDocument getHrDocument() {
		return hrDocument;
	}

	public void setHrDocument(HrDocument hrDocument) {
		this.hrDocument = hrDocument;
	}

	@Transient
	private String waers;

	@Transient
	Long newBusinessAreaId;

	public String getWaers() {
		return waers;
	}

	public void setWaers(String waers) {
		this.waers = waers;
	}

	public Long getNewBusinessAreaId() {
		return newBusinessAreaId;
	}

	public void setNewBusinessAreaId(Long newBusinessAreaId) {
		this.newBusinessAreaId = newBusinessAreaId;
	}

	public String getNewCurrency() {
		return newCurrency;
	}

	public void setNewCurrency(String newCurrency) {
		this.newCurrency = newCurrency;
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
	private Staff manager;

	public Staff getManager() {
		return manager;
	}

	public void setManager(Staff manager) {
		this.manager = manager;
	}

	@Transient
	private Salary salary;

	public Salary getSalary() {
		return salary;
	}

	public void setSalary(Salary salary) {
		this.salary = salary;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hrDocument == null) ? 0 : hrDocument.hashCode());
		result = prime * result + ((staffId == null) ? 0 : staffId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HrDocumentItem other = (HrDocumentItem) obj;
		if (hrDocument == null) {
			if (other.hrDocument != null)
				return false;
		} else if (!hrDocument.equals(other.hrDocument))
			return false;
		if (staffId == null) {
			if (other.staffId != null)
				return false;
		} else if (!staffId.equals(other.staffId))
			return false;
		return true;
	}

}