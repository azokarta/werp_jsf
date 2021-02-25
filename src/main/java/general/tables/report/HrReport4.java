package general.tables.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.tables.Salary;

public class HrReport4 {

	private Long branchId;
	private Long departmentId;
	private Long positionId;
	private int month;
	private String typeName;
	private int countDismiss;
	private int countChangeSalary;
	private int countRecruitment;
	private int countTransfer;

	public HrReport4(Long branchId, Long departmentId, Long positionId, int month, String typeName) {
		super();
		this.branchId = branchId;
		this.departmentId = departmentId;
		this.positionId = positionId;
		this.month = month;
		this.typeName = typeName;
	}

	public int getCountTransfer() {
		return countTransfer;
	}

	public void setCountTransfer(int countTransfer) {
		this.countTransfer = countTransfer;
	}

	public int getCountDismiss() {
		return countDismiss;
	}

	public void setCountDismiss(int countDismiss) {
		this.countDismiss = countDismiss;
	}

	public int getCountChangeSalary() {
		return countChangeSalary;
	}

	public void setCountChangeSalary(int countChangeSalary) {
		this.countChangeSalary = countChangeSalary;
	}

	public int getCountRecruitment() {
		return countRecruitment;
	}

	public void setCountRecruitment(int countRecruitment) {
		this.countRecruitment = countRecruitment;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public Long getPositionId() {
		return positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((branchId == null) ? 0 : branchId.hashCode());
		result = prime * result + ((departmentId == null) ? 0 : departmentId.hashCode());
		result = prime * result + month;
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
		HrReport4 other = (HrReport4) obj;
		if (branchId == null) {
			if (other.branchId != null)
				return false;
		} else if (!branchId.equals(other.branchId))
			return false;
		if (departmentId == null) {
			if (other.departmentId != null)
				return false;
		} else if (!departmentId.equals(other.departmentId))
			return false;
		if (month != other.month)
			return false;
		return true;
	}

}
