package general.tables.report;

import crm.constants.HrConstants;

public class HrReport6 {

	private String bukrs;
	private Long branchId;
	private Long positionId;
	private Long staffId;
	private String fullName;
	private Integer catId;
	private Double monthCount;
	private Double yearCount;

	public Double getMonthCount() {
		return monthCount;
	}

	public void setMonthCount(Double monthCount) {
		this.monthCount = monthCount;
		if (monthCount <= HrConstants.BEGINNER_COUNT) {
			setCatId(HrConstants.CAT_BEGINNER);
		} else if (monthCount <= HrConstants.DEALER_COUNT) {
			setCatId(HrConstants.CAT_DEALER);
		} else {
			setCatId(HrConstants.CAT_PROF);
		}

		// else if (monthCount <= HrConstants.PROF_COUNT) {
		// setCatId(HrConstants.CAT_PROF);
		// } else {
		// setCatId(HrConstants.CAT_MASTER);
		// }
	}

	public String getCatName() {
		return HrConstants.getCats().get(getCatId());
	}

	public Double getYearCount() {
		return yearCount;
	}

	public void setYearCount(Double yearCount) {
		this.yearCount = yearCount;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public Long getPositionId() {
		return positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Integer getCatId() {
		return catId;
	}

	public void setCatId(Integer catId) {
		this.catId = catId;
	}

}
