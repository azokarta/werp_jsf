package general.tables.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import general.tables.Staff;

public class MarketingReport1 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Демо
	public static final Integer TYPE_DEMO = 1;

	// Рекомендации
	public static final Integer TYPE_REFERENCE = 2;

	// Сатыш
	public static final Integer TYPE_SALE = 3;

	private String staffName;
	private String columnType;
	private Map<Integer, Map<Integer, Integer>> data = new HashMap<>();
	private Long staffId;
	private Staff staffObject;
	private Integer demoSum = 0;
	private Integer refSum = 0;
	private Integer saleSum = 0;
	private Long managerPyramidId;
	private int staffCount;

	public int getStaffCount() {
		return staffCount;
	}

	public void setStaffCount(int staffCount) {
		this.staffCount = staffCount;
	}

	public Long getManagerPyramidId() {
		return managerPyramidId;
	}

	public void setManagerPyramidId(Long managerPyramidId) {
		this.managerPyramidId = managerPyramidId;
	}

	public Integer getRefSum() {
		return refSum;
	}

	public void setRefSum(Integer refSum) {
		this.refSum = refSum;
	}

	public Integer getSaleSum() {
		return saleSum;
	}

	public void setSaleSum(Integer saleSum) {
		this.saleSum = saleSum;
	}

	public Staff getStaffObject() {
		return staffObject;
	}

	public void setStaffObject(Staff staffObject) {
		this.staffObject = staffObject;
	}

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public Map<Integer, Map<Integer, Integer>> getData() {
		return data;
	}

	public void setData(Map<Integer, Map<Integer, Integer>> data) {
		this.data = data;
	}

	public Integer getDemoSum() {
		return demoSum;
	}

	public void setDemoSum(Integer demoSum) {
		this.demoSum = demoSum;
	}

	public Integer getSum(Integer dataType) {
		Integer out = 0;
		Map<Integer, Integer> temp = null;
		if ((temp = data.get(dataType)) != null) {
			for (Entry<Integer, Integer> e : temp.entrySet()) {
				out += e.getValue();
			}
		}

		return out;
	}

	public void generateData() {
		demoSum = 0;
		refSum = 0;
		saleSum = 0;
		Map<Integer, Integer> temp = null;
		if ((temp = data.get(TYPE_DEMO)) != null) {
			for (Entry<Integer, Integer> e : temp.entrySet()) {
				demoSum += e.getValue();
			}
		}

		if ((temp = data.get(TYPE_REFERENCE)) != null) {
			for (Entry<Integer, Integer> e : temp.entrySet()) {
				refSum += e.getValue();
			}
		}

		if ((temp = data.get(TYPE_SALE)) != null) {
			for (Entry<Integer, Integer> e : temp.entrySet()) {
				saleSum += e.getValue();
			}
		}
	}

	public Integer getData(Integer dataType, Integer day) {
		Integer out = 0;
		Map<Integer, Integer> temp = null;
		if ((temp = data.get(dataType)) != null) {
			out = temp.get(day);
		}

		return out == null ? 0 : out;
	}

	public Double getSalePerDemo() {
		Double demoCount = 0D;
		Double saleCount = 0D;
		if (data.containsKey(TYPE_DEMO)) {
			for (Entry<Integer, Integer> e : data.get(TYPE_DEMO).entrySet()) {
				demoCount += e.getValue().doubleValue();
			}
		}

		if (data.containsKey(TYPE_SALE)) {
			for (Entry<Integer, Integer> e : data.get(TYPE_SALE).entrySet()) {
				saleCount += e.getValue().doubleValue();
			}
		}

		if (saleCount == 0D) {
			return 0D;
		}

		return new Double(demoCount / saleCount);
	}

	public Double getReferencePerDemo() {
		Double demoCount = 0D;
		Double refCount = 0D;
		if (data.containsKey(TYPE_DEMO)) {
			for (Entry<Integer, Integer> e : data.get(TYPE_DEMO).entrySet()) {
				demoCount += e.getValue().doubleValue();
			}
		}

		if (data.containsKey(TYPE_REFERENCE)) {
			for (Entry<Integer, Integer> e : data.get(TYPE_REFERENCE).entrySet()) {
				refCount += e.getValue().doubleValue();
			}
		}

		if (refCount == 0D) {
			return 0D;
		}

		return new Double(refCount / demoCount);
	}

	public Double getDemoPerDealer() {
		Double demoCount = 0D;
		if (data.containsKey(TYPE_DEMO)) {
			for (Entry<Integer, Integer> e : data.get(TYPE_DEMO).entrySet()) {
				demoCount += e.getValue().doubleValue();
			}
		}

		if (staffCount == 0) {
			return 0D;
		}

		return new Double(demoCount / staffCount);
	}

	public Double getRefPerDealer() {
		if (staffCount == 0) {
			return 0D;
		}
		Double refCount = 0D;

		if (data.containsKey(TYPE_REFERENCE)) {
			for (Entry<Integer, Integer> e : data.get(TYPE_REFERENCE).entrySet()) {
				refCount += e.getValue().doubleValue();
			}
		}

		return new Double(refCount / staffCount);
	}

	public Double getSalePerDealer() {
		if (staffCount == 0) {
			return 0D;
		}
		Double saleCount = 0D;

		if (data.containsKey(TYPE_SALE)) {
			for (Entry<Integer, Integer> e : data.get(TYPE_SALE).entrySet()) {
				saleCount += e.getValue().doubleValue();
			}
		}

		return new Double(saleCount / staffCount);
	}
}
