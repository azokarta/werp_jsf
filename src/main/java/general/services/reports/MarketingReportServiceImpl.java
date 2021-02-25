package general.services.reports;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.charts.LineChartSerie;
import org.primefaces.model.chart.LineChartSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.Validation;
import general.dao.DAOException;
import general.dao.DemoDao;
import general.dao.PyramidArchiveDao;
import general.dao.PyramidDao;
import general.dao.StaffDao;
import general.tables.Demonstration;
import general.tables.Position;
import general.tables.Pyramid;
import general.tables.PyramidArchive;
import general.tables.Staff;
import general.tables.report.MarketingReport1;

@Service("marketingReportService")
public class MarketingReportServiceImpl implements MarketingReportService {

	@Autowired
	DemoDao demoDao;

	@Autowired
	PyramidDao pyramidDao;

	@Autowired
	PyramidArchiveDao pyrArchiveDao;

	@Autowired
	StaffDao staffDao;

	private Long managerPositionId = 3L;
	private Long demoSecPositionId = 8L;
	private Long dealerPositionId = 4L;
	private static final String NO_MANAGER_NAME = "Без менеджера";

	@Override
	public List<MarketingReport1> getRep1Data(String bukrs, Long branchId, int year, int month, Long managerPyramidId,
			int renderType) throws DAOException {
		String error = "";
		if (Validation.isEmptyString(bukrs)) {
			error += " Выберите компнию \n ";
		}

		if (Validation.isEmptyLong(branchId)) {
			error += " Выберите филиал \n";
		}

		if (year == 0) {
			error += " Выберите год \n";
		}

		if (!Validation.isEmptyString(error)) {
			throw new DAOException(error);
		}

		int currYear = Calendar.getInstance().get(Calendar.YEAR);
		int currMonth = Calendar.getInstance().get(Calendar.MONTH);
		List<Pyramid> pyrList = new ArrayList<>();
		List<PyramidArchive> pyrArchiveList = new ArrayList<>();
		boolean isCurYearAndMonth = false;
		if (currYear == year && currMonth == month) {
			isCurYearAndMonth = true;
			if (renderType == 1) {
				pyrList = pyramidDao.findByBukrsBranchPosition(bukrs, branchId, managerPositionId);
			} else {
				Pyramid p = pyramidDao.find(managerPyramidId);

				if (p == null) {
					throw new DAOException("Manager Not Found In Pyramid: " + managerPyramidId);
				}

				pyrList = pyramidDao.dynamicFindPyramid(" parent_pyramid_id = " + p.getPyramid_id()
						+ "  AND staff_id > 0 AND staff_id IS NOT NULL AND position_id != " + demoSecPositionId);
				pyrList.add(0, p);
			}

			return getPreparedData(pyrList, year, month + 1, renderType, branchId, isCurYearAndMonth);
		} else {
			if (renderType == 1) {
				pyrArchiveList = pyrArchiveDao.findByBukrsBranchPosition(year, month + 1, bukrs, branchId,
						managerPositionId);
			} else {
				PyramidArchive pa = pyrArchiveDao.find(managerPyramidId, year, month + 1);

				if (pa == null) {
					throw new DAOException("Manager Not Found In Pyramid: " + managerPyramidId);
				}
				pyrArchiveList = pyrArchiveDao.dynamicFindPyramid(String
						.format(" year=%d AND month=%d AND parent_pyramid_id = %d AND staff_id > 0 AND staff_id IS NOT NULL AND position_id != "
								+ demoSecPositionId, year, month + 1, pa.getPyramid_id()));
				pyrArchiveList.add(0, pa);
			}

			return getPreparedData(pyrArchiveList, year, month + 1, renderType, branchId, isCurYearAndMonth);
		}
	}

	private List<MarketingReport1> getPreparedData(List<?> pyramidList, int year, int month, int renderType,
			Long branchId, boolean isCurYearAndMonth) {
		Map<Long, Staff> stfMap = staffDao.getMappedList("");
		List<MarketingReport1> out = new ArrayList<>();
		Map<Integer, Map<Integer, Integer>> tempMap;
		int totalStaffCount = 0;
		List<String> totalStaffIds = new ArrayList<>();
		String cond = String.format(" EXTRACT(year FROM date_time ) = %d AND EXTRACT(month FROM date_time) = %d ", year,
				month);
		boolean b = false;
		for (Object ob : pyramidList) {
			MarketingReport1 mr = new MarketingReport1();
			String cond2 = "";
			Pyramid tempPyr = null;
			PyramidArchive tempPyrArchive = null;
			Staff pyrStaff = null;
			if (ob instanceof Pyramid) {
				tempPyr = (Pyramid) ob;
				pyrStaff = stfMap.get(tempPyr.getStaff_id());
			} else {
				tempPyrArchive = (PyramidArchive) ob;
				pyrStaff = stfMap.get(tempPyrArchive.getStaff_id());
			}
			if (renderType == 1) {
				List<String> staffIds = new ArrayList<>();
				if (tempPyrArchive == null) {
					List<Pyramid> childs = pyramidDao.dynamicFindPyramid(" parent_pyramid_id = "
							+ tempPyr.getPyramid_id() + " AND staff_id > 0 AND staff_id IS NOT NULL ");

					if (!Validation.isEmptyLong(tempPyr.getStaff_id())) {
						staffIds.add(tempPyr.getStaff_id().toString());
					}

					for (Pyramid child : childs) {
						staffIds.add(child.getStaff_id().toString());
					}
					mr.setStaffCount(staffIds.size());
				} else {
					List<PyramidArchive> childs = pyrArchiveDao.dynamicFindPyramid(String.format(
							" year=%d AND month=%d AND parent_pyramid_id = %d AND staff_id > 0 AND staff_id IS NOT NULL ",
							year, month, tempPyrArchive.getPyramid_id()));

					if (!Validation.isEmptyLong(tempPyrArchive.getStaff_id())) {
						staffIds.add(tempPyrArchive.getStaff_id().toString());
					}

					for (PyramidArchive child : childs) {
						staffIds.add(child.getStaff_id().toString());
					}
				}

				if (staffIds.size() == 0) {
					continue;
				}
				mr.setStaffCount(staffIds.size());
				totalStaffCount += mr.getStaffCount();
				cond2 = cond + " AND dealer_id IN( " + String.join(",", staffIds) + ") ";
				totalStaffIds.addAll(staffIds);
			} else {
				if (pyrStaff == null) {
					continue;
				}
				mr.setStaffCount(1);
				totalStaffCount += 1;
				cond2 = cond + " AND dealer_id = " + pyrStaff.getStaff_id();
				totalStaffIds.add(pyrStaff.getStaff_id().toString());
			}

			mr.setManagerPyramidId(tempPyr == null ? tempPyrArchive.getPyramid_id() : tempPyr.getPyramid_id());
			mr.setStaffName(pyrStaff == null ? NO_MANAGER_NAME : pyrStaff.getLF());
			mr.setStaffId(pyrStaff == null ? 0L : pyrStaff.getStaff_id());
			mr.setStaffObject(pyrStaff);
			// --
			mr.setData(getFilledTempMap(cond2));
			mr.generateData();
			out.add(mr);
		}

		/**
		 * Остальные
		 */
		if (renderType == 1 && !b) {
			if (isCurYearAndMonth) {
				List<Pyramid> otherPyrList = pyramidDao.findAllWithStaff(String.format(
						" p.branch_id = %d AND p.position_id NOT IN(%d,%d,%d) AND p.staff_id NOT IN(%s) AND p.staff_id IS NOT NULL AND p.staff_id > 0",
						branchId, managerPositionId, dealerPositionId, Position.STAZHER_DEALER_POSITION_ID,
						String.join(",", totalStaffIds)));
				List<String> staffIds = new ArrayList<>();

				for (Pyramid p : otherPyrList) {
					List<Demonstration> dList = demoDao.findAll(String
							.format(" EXTRACT(year FROM date_time ) = %d AND EXTRACT(month FROM date_time) = %d AND dealer_id=%d AND status_id != "
									+ Demonstration.DEMO_STATUS_MOVED, year, month, p.getStaff_id()));
					if (dList.size() > 0 && !staffIds.contains(p.getStaff_id().toString())) {
						staffIds.add(p.getStaff_id().toString());
					}
				}

				if (staffIds.size() > 0) {
					String cond2 = cond + " AND dealer_id IN(" + String.join(",", staffIds) + ") ";
					MarketingReport1 mr = new MarketingReport1();
					mr.setManagerPyramidId(0L);
					mr.setStaffName("Остальные");
					// mr.setStaffCount(staffIds.size());
					mr.setStaffCount(1);
					mr.setStaffId(0L);
					mr.setStaffObject(null);
					mr.setData(getFilledTempMap(cond2));
					mr.generateData();
					totalStaffCount += mr.getStaffCount();
					out.add(mr);
				}
			} else {
				List<PyramidArchive> otherPyrList = pyrArchiveDao.dynamicFindPyramid(String.format(
						" p.branch_id = %d AND p.position_id NOT IN(%d,%d,%d) AND p.staff_id NOT IN(%s) AND p.staff_id IS NOT NULL AND p.staff_id > 0",
						branchId, managerPositionId, dealerPositionId, Position.STAZHER_DEALER_POSITION_ID,
						String.join(",", totalStaffIds)));
				List<String> staffIds = new ArrayList<>();

				for (PyramidArchive p : otherPyrList) {
					List<Demonstration> dList = demoDao.findAll(String
							.format(" EXTRACT(year FROM date_time ) = %d AND EXTRACT(month FROM date_time) = %d AND dealer_id=%d AND status_id != "
									+ Demonstration.DEMO_STATUS_MOVED, year, month, p.getStaff_id()));

					if (dList.size() > 0 && !staffIds.contains(p.getStaff_id().toString())) {
						staffIds.add(p.getStaff_id().toString());
					}
				}

				if (staffIds.size() > 0) {
					String cond2 = cond + " AND dealer_id IN(" + String.join(",", staffIds) + ") ";
					MarketingReport1 mr = new MarketingReport1();
					mr.setManagerPyramidId(0L);
					mr.setStaffName("Остальные");
					// mr.setStaffCount(staffIds.size());
					mr.setStaffCount(1);
					mr.setStaffId(0L);
					mr.setStaffObject(null);
					mr.setData(getFilledTempMap(cond2));
					mr.generateData();
					totalStaffCount += mr.getStaffCount();
					out.add(mr);
				}
			}
		}

		// Если группа, то показываем ИТОГО ДЛЯ ВСЕГО ОФИСА
		if (out.size() > 1) {
			tempMap = new HashMap<>();
			Map<Integer, Integer> demoMap = new HashMap<>();
			Map<Integer, Integer> refMap = new HashMap<>();
			Map<Integer, Integer> saleMap = new HashMap<>();
			for (MarketingReport1 mr : out) {
				setResultData(mr.getData(), MarketingReport1.TYPE_DEMO, demoMap);
				setResultData(mr.getData(), MarketingReport1.TYPE_REFERENCE, refMap);
				setResultData(mr.getData(), MarketingReport1.TYPE_SALE, saleMap);
			}

			MarketingReport1 result = new MarketingReport1();
			result.setStaffName("ИТОГО");
			result.setStaffId(0L);
			result.setStaffObject(null);
			result.setStaffCount(totalStaffCount);

			tempMap.put(MarketingReport1.TYPE_DEMO, demoMap);
			tempMap.put(MarketingReport1.TYPE_REFERENCE, refMap);
			tempMap.put(MarketingReport1.TYPE_SALE, saleMap);
			result.setData(tempMap);
			result.generateData();

			out.add(result);
		}

		return out;
	}

	private Map<Integer, Map<Integer, Integer>> getFilledTempMap(String cond) {
		Map<Integer, Map<Integer, Integer>> tempMap = new HashMap<>();
		Map<Integer, Integer> tempDemoMap = new HashMap<>();
		Map<Integer, Integer> tempRefMap = new HashMap<>();
		Map<Integer, Integer> tempSaleMap = new HashMap<>();
		Calendar tempCal = Calendar.getInstance();

		List<Demonstration> demoList = demoDao.findAll(cond);
		for (Demonstration demo : demoList) {
			tempCal.setTime(demo.getDateTime());
			Integer day = tempCal.get(Calendar.DAY_OF_MONTH);
			int demoCount = 0;
			int saleCount = 0;
			int refCount = 0;
			if (tempDemoMap.containsKey(day)) {
				demoCount = tempDemoMap.get(day);
			}

			if (tempRefMap.containsKey(day)) {
				refCount = tempRefMap.get(day);
			}

			if (tempSaleMap.containsKey(day)) {
				saleCount = tempSaleMap.get(day);
			}

			if (!Demonstration.DEMO_STATUS_DELIVERY.equals(demo.getStatusId())) {
				demoCount++;
			}
			refCount += demo.getRefCount();
			saleCount += Demonstration.DEMO_STATUS_SOLD.equals(demo.getStatusId()) ? 1 : 0;
			if (Demonstration.DEMO_STATUS_DELIVERY.equals(demo.getStatusId())) {
				saleCount++;
			}

			if (Demonstration.DEMO_STATUS_SHOWN.equals(demo.getStatusId())
					|| Demonstration.DEMO_STATUS_CONTRACT.equals(demo.getStatusId())
					|| Demonstration.DEMO_STATUS_SOLD.equals(demo.getStatusId())
					|| Demonstration.DEMO_STATUS_DELIVERY.equals(demo.getStatusId())) {
				tempDemoMap.put(day, demoCount);
				tempRefMap.put(day, refCount);
				tempSaleMap.put(day, saleCount);
			}
		}

		tempMap.put(MarketingReport1.TYPE_DEMO, tempDemoMap);
		tempMap.put(MarketingReport1.TYPE_REFERENCE, tempRefMap);
		tempMap.put(MarketingReport1.TYPE_SALE, tempSaleMap);

		return tempMap;
	}

	private void setResultData(Map<Integer, Map<Integer, Integer>> dataMap, Integer type,
			Map<Integer, Integer> resultMap) {
		for (Entry<Integer, Integer> e : dataMap.get(type).entrySet()) {
			Integer i = 0;
			if (resultMap.containsKey(e.getKey())) {
				i = resultMap.get(e.getKey());
			}

			i += e.getValue();
			resultMap.put(e.getKey(), i);
		}
	}

	@Override
	public Map<String, LineChartSeries> getRep2Data(String bukrs, Long branchId, int year) {
		String cond = String.format(" bukrs='%s' AND branch_id=%d AND EXTRACT(year FROM date_time ) = %d ", bukrs,
				branchId, year);
		List<Demonstration> list = demoDao.findAll(cond);
		Map<Integer, Integer> demoMap = new HashMap<>();
		Map<Integer, Integer> saleMap = new HashMap<>();
		Map<Integer, Integer> recoMap = new HashMap<>();
		Calendar cal = Calendar.getInstance();
		for (Demonstration demo : list) {
			if (!Demonstration.DEMO_STATUS_CANCELLED.equals(demo.getStatusId())
					&& !Demonstration.DEMO_STATUS_MOVED.equals(demo.getStatusId())) {
				cal.setTime(demo.getDateTime());
				Integer month = cal.get(Calendar.MONTH) + 1;
				Integer count = 0;
				if (!Demonstration.DEMO_STATUS_DELIVERY.equals(demo.getStatusId())) {
					if (demoMap.containsKey(month)) {
						count = demoMap.get(month);
					}

					count++;
					demoMap.put(month, count);
				}

				if (Demonstration.DEMO_STATUS_SOLD.equals(demo.getStatusId())
						|| Demonstration.DEMO_STATUS_DELIVERY.equals(demo.getStatusId())) {
					count = 0;
					if (saleMap.containsKey(month)) {
						count = saleMap.get(month);
					}

					count++;
					saleMap.put(month, count);
				}

				count = 0;
				if (recoMap.containsKey(month)) {
					count = recoMap.get(month);
				}

				count += demo.getRefCount();
				recoMap.put(month, count);

			}
		}

		Map<String, LineChartSeries> out = new HashMap<>();
		out.put("demo", getPreparedLineChartSeries("Демо", demoMap));
		out.put("sale", getPreparedLineChartSeries("Продажа", saleMap));
		out.put("reco", getPreparedLineChartSeries("Рекомандации", recoMap));

		return out;
	}

	private LineChartSeries getPreparedLineChartSeries(String label, Map<Integer, Integer> demoMap) {
		LineChartSeries series = new LineChartSeries();
		// series.setFill(true);
		series.setLabel(label);

		for (Integer k = 1; k < 13; k++) {
			series.set(k, demoMap.get(k));
		}

		return series;
	}
}
