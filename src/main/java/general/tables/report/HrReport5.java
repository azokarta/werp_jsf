package general.tables.report;

import java.util.ArrayList;
import java.util.List;

public class HrReport5 {

	public static final Double BEGINNER_COUNT = 12D;
	public static final Double DEALER_COUNT = 36D;
	public static final Double PROF_COUNT = 60D;

	private String bukrs;
	private Long branchId;
	private Long positionId;
	private Double yearCount;
	private List<Long> staffIds = new ArrayList<>();
	private int beginnerCount;
	private int dealerCount;
	private int profCount;
	private int masterCount;

	public HrReport5(Long positionId) {
		super();
		this.positionId = positionId;
		this.beginnerCount = 0;
		this.dealerCount = 0;
		this.profCount = 0;
		this.masterCount = 0;
	}

	public void setCount(Double c, Long staffId) {
		if (c <= BEGINNER_COUNT) {
			beginnerCount++;
		} else if (c <= DEALER_COUNT) {
			dealerCount++;
		} else {
			profCount++;
		}

		// else if (c <= PROF_COUNT) {
		// profCount++;
		// } else {
		// masterCount++;
		// if (!getStaffIds().contains(staffId)) {
		// getStaffIds().add(staffId);
		// }
		// }
	}

	public int getBeginnerCount() {
		return beginnerCount;
	}

	public void setBeginnerCount(int beginnerCount) {
		this.beginnerCount = beginnerCount;
	}

	public int getDealerCount() {
		return dealerCount;
	}

	public void setDealerCount(int dealerCount) {
		this.dealerCount = dealerCount;
	}

	public int getProfCount() {
		return profCount;
	}

	public void setProfCount(int profCount) {
		this.profCount = profCount;
	}

	public int getMasterCount() {
		return masterCount;
	}

	public void setMasterCount(int masterCount) {
		this.masterCount = masterCount;
	}

	public List<Long> getStaffIds() {
		return staffIds;
	}

	public void setStaffIds(List<Long> staffIds) {
		this.staffIds = staffIds;
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

	public int getStaffCount() {
		return getStaffIds().size();
	}

	public Double getYearCount() {
		return yearCount;
	}

	public void setYearCount(Double yearCount) {
		this.yearCount = yearCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		HrReport5 other = (HrReport5) obj;
		if (positionId == null) {
			if (other.positionId != null)
				return false;
		} else if (!positionId.equals(other.positionId))
			return false;
		return true;
	}

}
