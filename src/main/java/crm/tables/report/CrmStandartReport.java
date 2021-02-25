package crm.tables.report;

import java.util.ArrayList;
import java.util.List;

public class CrmStandartReport {

	private Long id;
	private String bukrs;
	private Long branchId;
	private Long managerId;
	private Long staffId;
	private Long positionId;
	private List<Long> dealerIds = new ArrayList<>();

	private int recoCount;
	private int demoCount;
	private int shownDemoCount;
	private int demoFromDemoCount;
	private int saleFromDemoCount;
	private int visitCount;
	private int recoFromVisitCount;
	private int dealerCount;
	private int newDealerCount;
	private int demoSecCount;
	private int managerCount;
	private int dealerCountInGroup;

	// Процент демо к звонку
	private int demoCallPercent;

	// Процент показанных демо
	private int shownDemoCallPercent;
	private String staffName;
	private String name;

	private int minRecoCount = 200;
	private int avRecoCount = 265;
	private int minDemoCount = 20;
	private int avDemoCount = 25;
	private int minShownDemoCount = 14;
	private int avShownDemoCount = 20;
	private int minDemoFromDemoCount = 2;
	private int avDemoFromDemoCount = 3;
	private int minSaleFromDemoCount = 3;
	private int avSaleFromDemoCount = 4;
	private int minVisitCount = 5;
	private int avVisitCount = 7;
	private int minRecoFromVisitCount = 4;
	private int avRecoFromVisitCount = 5;
	private int minDealerCount = 3;
	private int avDealerCount = 4;
	private int minNewDealerCount = 1;
	private int avNewDealerCount = 1;
	// private int demoSecCount;
	private int minManagerCount = 3;
	private int avManagerCount = 4;

	// Минимум 20% звонка должно быть демо
	private int minDemoCallPercent = 20;

	// В среднем 25% звонка должно быть демо
	private int avDemoCallPercent = 25;

	private int minShownDemoCallPercent = 70;
	private int avShownDemoCallPercent = 70;

	// Количество дилеров в группе
	private int minDealerCountInGroup = 3;
	private int avDealerCountInGroup = 4;

	public int getMinDealerCountInGroup() {
		return minDealerCountInGroup;
	}

	public void setMinDealerCountInGroup(int minDealerCountInGroup) {
		this.minDealerCountInGroup = minDealerCountInGroup;
	}

	public int getDealerCountInGroup() {
		return dealerCountInGroup;
	}

	public void setDealerCountInGroup(int dealerCountInGroup) {
		this.dealerCountInGroup = dealerCountInGroup;
	}

	public int getAvDealerCountInGroup() {
		return avDealerCountInGroup;
	}

	public void setAvDealerCountInGroup(int avDealerCountInGroup) {
		this.avDealerCountInGroup = avDealerCountInGroup;
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

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	public Long getPositionId() {
		return positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}

	public int getRecoCount() {
		return recoCount;
	}

	public void setRecoCount(int recoCount) {
		this.recoCount = recoCount;
	}

	public int getDemoCount() {
		return demoCount;
	}

	public void setDemoCount(int demoCount) {
		this.demoCount = demoCount;
	}

	public int getShownDemoCount() {
		return shownDemoCount;
	}

	public void setShownDemoCount(int shownDemoCount) {
		this.shownDemoCount = shownDemoCount;
	}

	public int getDemoFromDemoCount() {
		return demoFromDemoCount;
	}

	public void setDemoFromDemoCount(int demoFromDemoCount) {
		this.demoFromDemoCount = demoFromDemoCount;
	}

	public int getSaleFromDemoCount() {
		return saleFromDemoCount;
	}

	public void setSaleFromDemoCount(int saleFromDemoCount) {
		this.saleFromDemoCount = saleFromDemoCount;
	}

	public int getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}

	public int getRecoFromVisitCount() {
		return recoFromVisitCount;
	}

	public void setRecoFromVisitCount(int recoFromVisitCount) {
		this.recoFromVisitCount = recoFromVisitCount;
	}

	public int getDealerCount() {
		return dealerCount;
	}

	public void setDealerCount(int dealerCount) {
		this.dealerCount = dealerCount;
	}

	public int getNewDealerCount() {
		return newDealerCount;
	}

	public void setNewDealerCount(int newDealerCount) {
		this.newDealerCount = newDealerCount;
	}

	public int getDemoSecCount() {
		return demoSecCount;
	}

	public void setDemoSecCount(int demoSecCount) {
		this.demoSecCount = demoSecCount;
	}

	public int getManagerCount() {
		return managerCount;
	}

	public void setManagerCount(int managerCount) {
		this.managerCount = managerCount;
	}

	public int getDemoCallPercent() {
		return demoCallPercent;
	}

	public void setDemoCallPercent(int demoCallPercent) {
		this.demoCallPercent = demoCallPercent;
	}

	public int getShownDemoCallPercent() {
		return shownDemoCallPercent;
	}

	public void setShownDemoCallPercent(int shownDemoCallPercent) {
		this.shownDemoCallPercent = shownDemoCallPercent;
	}

	public int getMinRecoCount() {
		return minRecoCount;
	}

	public void setMinRecoCount(int minRecoCount) {
		this.minRecoCount = minRecoCount;
	}

	public int getAvRecoCount() {
		return avRecoCount;
	}

	public void setAvRecoCount(int avRecoCount) {
		this.avRecoCount = avRecoCount;
	}

	public int getMinDemoCount() {
		return minDemoCount;
	}

	public void setMinDemoCount(int minDemoCount) {
		this.minDemoCount = minDemoCount;
	}

	public int getAvDemoCount() {
		return avDemoCount;
	}

	public void setAvDemoCount(int avDemoCount) {
		this.avDemoCount = avDemoCount;
	}

	public int getMinShownDemoCount() {
		return minShownDemoCount;
	}

	public void setMinShownDemoCount(int minShownDemoCount) {
		this.minShownDemoCount = minShownDemoCount;
	}

	public int getAvShownDemoCount() {
		return avShownDemoCount;
	}

	public void setAvShownDemoCount(int avShownDemoCount) {
		this.avShownDemoCount = avShownDemoCount;
	}

	public int getMinDemoFromDemoCount() {
		return minDemoFromDemoCount;
	}

	public void setMinDemoFromDemoCount(int minDemoFromDemoCount) {
		this.minDemoFromDemoCount = minDemoFromDemoCount;
	}

	public int getAvDemoFromDemoCount() {
		return avDemoFromDemoCount;
	}

	public void setAvDemoFromDemoCount(int avDemoFromDemoCount) {
		this.avDemoFromDemoCount = avDemoFromDemoCount;
	}

	public int getMinSaleFromDemoCount() {
		return minSaleFromDemoCount;
	}

	public void setMinSaleFromDemoCount(int minSaleFromDemoCount) {
		this.minSaleFromDemoCount = minSaleFromDemoCount;
	}

	public int getAvSaleFromDemoCount() {
		return avSaleFromDemoCount;
	}

	public void setAvSaleFromDemoCount(int avSaleFromDemoCount) {
		this.avSaleFromDemoCount = avSaleFromDemoCount;
	}

	public int getMinVisitCount() {
		return minVisitCount;
	}

	public void setMinVisitCount(int minVisitCount) {
		this.minVisitCount = minVisitCount;
	}

	public int getAvVisitCount() {
		return avVisitCount;
	}

	public void setAvVisitCount(int avVisitCount) {
		this.avVisitCount = avVisitCount;
	}

	public int getMinRecoFromVisitCount() {
		return minRecoFromVisitCount;
	}

	public void setMinRecoFromVisitCount(int minRecoFromVisitCount) {
		this.minRecoFromVisitCount = minRecoFromVisitCount;
	}

	public int getAvRecoFromVisitCount() {
		return avRecoFromVisitCount;
	}

	public void setAvRecoFromVisitCount(int avRecoFromVisitCount) {
		this.avRecoFromVisitCount = avRecoFromVisitCount;
	}

	public int getMinDealerCount() {
		return minDealerCount;
	}

	public void setMinDealerCount(int minDealerCount) {
		this.minDealerCount = minDealerCount;
	}

	public int getAvDealerCount() {
		return avDealerCount;
	}

	public void setAvDealerCount(int avDealerCount) {
		this.avDealerCount = avDealerCount;
	}

	public int getMinNewDealerCount() {
		return minNewDealerCount;
	}

	public void setMinNewDealerCount(int minNewDealerCount) {
		this.minNewDealerCount = minNewDealerCount;
	}

	public int getAvNewDealerCount() {
		return avNewDealerCount;
	}

	public void setAvNewDealerCount(int avNewDealerCount) {
		this.avNewDealerCount = avNewDealerCount;
	}

	public int getMinManagerCount() {
		return minManagerCount;
	}

	public void setMinManagerCount(int minManagerCount) {
		this.minManagerCount = minManagerCount;
	}

	public int getAvManagerCount() {
		return avManagerCount;
	}

	public void setAvManagerCount(int avManagerCount) {
		this.avManagerCount = avManagerCount;
	}

	public int getMinDemoCallPercent() {
		return minDemoCallPercent;
	}

	public void setMinDemoCallPercent(int minDemoCallPercent) {
		this.minDemoCallPercent = minDemoCallPercent;
	}

	public int getAvDemoCallPercent() {
		return avDemoCallPercent;
	}

	public void setAvDemoCallPercent(int avDemoCallPercent) {
		this.avDemoCallPercent = avDemoCallPercent;
	}

	public int getMinShownDemoCallPercent() {
		return minShownDemoCallPercent;
	}

	public void setMinShownDemoCallPercent(int minShownDemoCallPercent) {
		this.minShownDemoCallPercent = minShownDemoCallPercent;
	}

	public int getAvShownDemoCallPercent() {
		return avShownDemoCallPercent;
	}

	public void setAvShownDemoCallPercent(int avShownDemoCallPercent) {
		this.avShownDemoCallPercent = avShownDemoCallPercent;
	}

	public List<Long> getDealerIds() {
		return dealerIds;
	}

	public void setDealerIds(List<Long> dealerIds) {
		this.dealerIds = dealerIds;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		CrmStandartReport other = (CrmStandartReport) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
