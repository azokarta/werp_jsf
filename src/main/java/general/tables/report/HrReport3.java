package general.tables.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.tables.Salary;

public class HrReport3 {

	private Long departmentId;
	private Long positionId;
	private List<Salary> salaryList = new ArrayList<>();

	public List<Salary> getSalaryList() {
		return salaryList;
	}

	public void setSalaryList(List<Salary> salaryList) {
		this.salaryList = salaryList;
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

	public int getStaffCount() {
		Map<Long,Integer> tempMap = new HashMap<>();
		for(Salary sal:salaryList){
			tempMap.put(sal.getStaff_id(), 1);
		}
		return tempMap.size();
	}
	
	public int getSalaryCount(){
		return salaryList.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((departmentId == null) ? 0 : departmentId.hashCode());
		result = prime * result + ((positionId == null) ? 0 : positionId.hashCode());
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
		HrReport3 other = (HrReport3) obj;
		if (departmentId == null) {
			if (other.departmentId != null)
				return false;
		} else if (!departmentId.equals(other.departmentId))
			return false;
		if (positionId == null) {
			if (other.positionId != null)
				return false;
		} else if (!positionId.equals(other.positionId))
			return false;
		return true;
	}

}
