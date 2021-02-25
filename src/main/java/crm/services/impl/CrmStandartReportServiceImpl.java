package crm.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import crm.constants.DemoConstants;
import crm.dao.CrmCallDao;
import crm.dao.CrmDocDemoDao;
import crm.dao.CrmDocRecoDao;
import crm.services.CrmStandartReportService;
import crm.tables.CrmDocDemo;
import crm.tables.CrmDocReco;
import crm.tables.report.CrmStandartReport;
import general.Validation;
import general.dao.BranchDao;
import general.dao.PyramidDao;
import general.dao.SalaryDao;
import general.tables.Branch;
import general.tables.Position;
import general.tables.Salary;

@Service("crmStandartReportService")
public class CrmStandartReportServiceImpl implements CrmStandartReportService {

	@Autowired
	PyramidDao pyramidDao;

	@Autowired
	CrmDocRecoDao recoDao;

	@Autowired
	CrmDocDemoDao demoDao;

	@Autowired
	CrmCallDao callDao;

	@Autowired
	BranchDao branchDao;

	@Autowired
	SalaryDao salaryDao;

	private static final int FOR_BRANCH = 1;
	private static final int FOR_MANAGER = 2;
	private static final int FOR_DEALER = 3;

	@Override
	public List<CrmStandartReport> getStandartReportData(String bukrs, Long branchId, Long managerId) {

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		int month = cal.get(Calendar.MONTH) + 1;

		List<CrmStandartReport> out = new ArrayList<>();

		if (Validation.isEmptyLong(branchId)) {
			List<Salary> salaries = salaryDao.findAllCurrentWithStaff(String.format(
					" s1.bukrs = %s AND s1.position_id IN(%d,%d,%d,%d) ", bukrs, Position.DEALER_POSITION_ID,
					Position.STAZHER_DEALER_POSITION_ID, Position.MANAGER_POSITION_ID, Position.DEMOSEC_POSITION_ID));
			Map<Long, List<Salary>> brManagers = new HashMap<>();
			Map<Long, List<Salary>> brDealers = new HashMap<>();
			Map<Long, List<Salary>> brSecretars = new HashMap<>();
			for (Salary sal : salaries) {
				if (Position.DEALER_POSITION_ID.equals(sal.getPosition_id())
						|| Position.STAZHER_DEALER_POSITION_ID.equals(sal.getPosition_id())
						|| Position.MANAGER_POSITION_ID.equals(sal.getPosition_id())) {

					List<Salary> dealers = new ArrayList<>();
					List<Salary> managers = new ArrayList<>();
					if (brDealers.containsKey(sal.getBranch_id())) {
						dealers = brDealers.get(sal.getBranch_id());
					}

					dealers.add(sal);
					brDealers.put(sal.getBranch_id(), dealers);

					if (Position.MANAGER_POSITION_ID.equals(sal.getPosition_id())) {
						if (brManagers.containsKey(sal.getBranch_id())) {
							managers = brManagers.get(sal.getBranch_id());
						}

						managers.add(sal);
						brManagers.put(sal.getBranch_id(), managers);
					}
				} else if (Position.DEMOSEC_POSITION_ID.equals(sal.getPosition_id())) {
					List<Salary> demosecs = new ArrayList<>();
					if (brSecretars.containsKey(sal.getBranch_id())) {
						demosecs = brSecretars.get(sal.getBranch_id());
					}
					demosecs.add(sal);
					brSecretars.put(sal.getBranch_id(), demosecs);
				}
			}

			List<Branch> branchs = new ArrayList<>();
			for (Branch br : branchDao.findByBukrs(bukrs)) {
				if (br.getType() == 3 && !Validation.isEmptyLong(br.getTovarCategory())) {
					CrmStandartReport csr = new CrmStandartReport();
					branchs.add(br);
					List<Salary> dealers = brDealers.containsKey(br.getBranch_id()) ? brDealers.get(br.getBranch_id())
							: new ArrayList<>();
					List<Salary> managers = brManagers.containsKey(br.getBranch_id())
							? brManagers.get(br.getBranch_id()) : new ArrayList<>();

					List<Salary> secretars = brSecretars.containsKey(br.getBranch_id())
							? brSecretars.get(br.getBranch_id()) : new ArrayList<>();

					int dealersSize = dealers.size();

					csr.setId(br.getBranch_id());
					csr.setName(br.getText45());
					csr.setBukrs(br.getBukrs());
					csr.setBranchId(br.getBranch_id());

					csr.setManagerCount(managers.size());
					csr.setDealerCount(dealersSize);
					csr.setDemoSecCount(secretars.size());

					csr.setMinDealerCount(csr.getMinDealerCount() * csr.getMinManagerCount());
					csr.setAvDealerCount(csr.getAvDealerCount() * csr.getAvManagerCount());
					csr.setMinRecoCount(csr.getMinRecoCount() * dealersSize);
					csr.setAvRecoCount(csr.getAvRecoCount() * dealersSize);
					csr.setMinDemoCount(csr.getMinDemoCount() * dealersSize);
					csr.setAvDemoCount(csr.getAvDemoCount() * dealersSize);
					csr.setMinShownDemoCount(csr.getMinShownDemoCount() * dealersSize);
					csr.setAvShownDemoCount(csr.getAvShownDemoCount() * dealersSize);
					csr.setMinDemoFromDemoCount(csr.getMinDemoFromDemoCount() * dealersSize);
					csr.setAvDemoFromDemoCount(csr.getAvDemoFromDemoCount() * dealersSize);
					csr.setSaleFromDemoCount(csr.getSaleFromDemoCount() * dealersSize);
					csr.setAvSaleFromDemoCount(csr.getAvSaleFromDemoCount() * dealersSize);
					csr.setMinVisitCount(csr.getMinVisitCount() * dealersSize);
					csr.setAvVisitCount(csr.getAvVisitCount() * dealersSize);
					csr.setMinRecoFromVisitCount(csr.getMinRecoFromVisitCount() * dealersSize);
					csr.setAvRecoFromVisitCount(csr.getAvRecoFromVisitCount() * dealersSize);

					out.add(csr);
				}
			}

			String cond = String.format(" bukrs = %s AND doc_date >= '%s' ", bukrs,
					cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : month) + "-01");
			List<CrmDocReco> recoList = recoDao.findAllByCondition(cond);
			// System.out.println("RECO: " + recoList.size());
			prepareRecoDataFor(recoList, out, FOR_BRANCH);

			String cond2 = String.format(" bukrs = %s AND date_time >= '%s' ", bukrs,
					cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : month) + "-01");
			// System.out.println(cond2);
			List<CrmDocDemo> demoList = demoDao.findAllByCondition(cond2);
			prepareDemoDataFor(demoList, out, FOR_BRANCH);
		} else {
			if (Validation.isEmptyLong(managerId)) {
				List<String> allDealerIds = new ArrayList<>();
				Branch branch = branchDao.find(branchId);
				List<Salary> managers = salaryDao.findAllCurrentWithStaff(
						String.format(" s1.bukrs = %s AND s1.branch_id=%d AND s1.position_id=%d ", bukrs, branchId,
								Position.MANAGER_POSITION_ID));
				for (Salary sal : managers) {
					List<Salary> dealers = pyramidDao.findAllDealersByBranchManagerId(bukrs, branchId,
							sal.getStaff_id());
					dealers.add(sal);
					List<Salary> secretars = pyramidDao.findAllDemosecsByBranchManagerId(bukrs, branchId,
							sal.getStaff_id());

					int dealersSize = dealers.size();
					List<Long> dealerIds = new ArrayList<>();
					for (Salary dealer : dealers) {
						dealerIds.add(dealer.getStaff_id());
						if (!allDealerIds.contains(dealer.getStaff_id().toString())) {
							allDealerIds.add(dealer.getStaff_id().toString());
						}
					}

					CrmStandartReport csr = new CrmStandartReport();

					csr.setId(sal.getStaff_id());
					csr.setName(sal.getP_staff() == null ? "Нет менеджера" : sal.getP_staff().getLF());
					csr.setBukrs(sal.getBukrs());
					csr.setBranchId(sal.getBranch_id());
					csr.setDealerIds(dealerIds);

					// csr.setManagerCount(managers.size());
					csr.setDealerCount(dealersSize);
					csr.setDemoSecCount(secretars.size());

					// csr.setMinDealerCount(csr.getMinDealerCount() *
					// csr.getMinManagerCount());
					// csr.setAvDealerCount(csr.getAvDealerCount() *
					// csr.getAvManagerCount());
					csr.setMinRecoCount(csr.getMinRecoCount() * dealersSize);
					csr.setAvRecoCount(csr.getAvRecoCount() * dealersSize);
					csr.setMinDemoCount(csr.getMinDemoCount() * dealersSize);
					csr.setAvDemoCount(csr.getAvDemoCount() * dealersSize);
					csr.setMinShownDemoCount(csr.getMinShownDemoCount() * dealersSize);
					csr.setAvShownDemoCount(csr.getAvShownDemoCount() * dealersSize);
					csr.setMinDemoFromDemoCount(csr.getMinDemoFromDemoCount() * dealersSize);
					csr.setAvDemoFromDemoCount(csr.getAvDemoFromDemoCount() * dealersSize);
					csr.setSaleFromDemoCount(csr.getSaleFromDemoCount() * dealersSize);
					csr.setAvSaleFromDemoCount(csr.getAvSaleFromDemoCount() * dealersSize);
					csr.setMinVisitCount(csr.getMinVisitCount() * dealersSize);
					csr.setAvVisitCount(csr.getAvVisitCount() * dealersSize);
					csr.setMinRecoFromVisitCount(csr.getMinRecoFromVisitCount() * dealersSize);
					csr.setAvRecoFromVisitCount(csr.getAvRecoFromVisitCount() * dealersSize);
					csr.setDealerCountInGroup(dealersSize);

					out.add(csr);
				}

				String cond = String.format(" bukrs = %s AND doc_date >= '%s' ", bukrs,
						cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : month) + "-01");
				if (!Validation.isEmptyCollection(allDealerIds)) {
					cond += " AND responsible_id IN(" + String.join(",", allDealerIds) + ") ";
				}
				List<CrmDocReco> recoList = recoDao.findAllByCondition(cond);
				prepareRecoDataFor(recoList, out, FOR_MANAGER);

				String cond2 = String.format(" bukrs = %s AND date_time >= '%s' ", bukrs,
						cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : month) + "-01");

				if (Validation.isEmptyCollection(allDealerIds)) {
					cond2 += " AND dealer_id = -1 ";
				} else {
					cond2 += " AND dealer_id IN(" + String.join(",", allDealerIds) + ") ";
				}

				List<CrmDocDemo> demoList = demoDao.findAllByCondition(cond2);
				prepareDemoDataFor(demoList, out, FOR_MANAGER);

			} else {
				List<String> allDealerIds = new ArrayList<>();
				Salary manager = salaryDao.find(managerId);
				List<Salary> dealers = pyramidDao.findAllDealersByBranchManagerId(bukrs, branchId, managerId);
				dealers.add(manager);
				for (Salary sal : dealers) {
					if (sal == null) {
						continue;
					}
					CrmStandartReport csr = new CrmStandartReport();

					csr.setId(sal.getStaff_id());
					csr.setName(sal.getP_staff() == null ? "Неизвестный" : sal.getP_staff().getLF());
					csr.setPositionId(sal.getPosition_id());
					csr.setBukrs(sal.getBukrs());
					csr.setBranchId(sal.getBranch_id());
					out.add(csr);
					allDealerIds.add(sal.getStaff_id().toString());
				}

				List<Salary> secretars = pyramidDao.findAllDemosecsByBranchManagerId(bukrs, branchId, managerId);

				String cond = String.format(" bukrs = %s AND doc_date >= '%s' ", bukrs,
						cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : month) + "-01");
				if (Validation.isEmptyCollection(allDealerIds)) {
					cond += " AND responsible_id= -1";
				} else {
					cond += " AND responsible_id IN(" + String.join(",", allDealerIds) + ") ";
				}
				List<CrmDocReco> recoList = recoDao.findAllByCondition(cond);
				prepareRecoDataFor(recoList, out, FOR_DEALER);

				String cond2 = String.format(" bukrs = %s AND date_time >= '%s' ", bukrs,
						cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : month) + "-01");

				if (Validation.isEmptyCollection(allDealerIds)) {
					cond2 += " AND dealer_id = -1 ";
				} else {
					cond2 += " AND dealer_id IN(" + String.join(",", allDealerIds) + ") ";
				}

				List<CrmDocDemo> demoList = demoDao.findAllByCondition(cond2);
				prepareDemoDataFor(demoList, out, FOR_DEALER);
			}
		}

		return out;
	}

	private void prepareRecoDataFor(List<CrmDocReco> recoList, List<CrmStandartReport> target, int forWhat) {
		Map<Long, List<CrmDocReco>> recoFromDemoMap = new HashMap<>();
		Map<Long, List<CrmDocReco>> recoFromVisitMap = new HashMap<>();

		Map<Long, Long> dealerManagerMap = new HashMap<>();
		if (FOR_MANAGER == forWhat) {
			for (CrmStandartReport csr : target) {
				for (Long dealerId : csr.getDealerIds()) {
					dealerManagerMap.put(dealerId, csr.getId());
				}
			}
		}

		for (CrmDocReco cdr : recoList) {
			if (!Validation.isEmptyLong(cdr.getDemoId())) {
				List<CrmDocReco> temp1 = new ArrayList<>();
				Long managerId = null;
				if (FOR_BRANCH == forWhat) {
					if (recoFromDemoMap.containsKey(cdr.getBranchId())) {
						temp1 = recoFromDemoMap.get(cdr.getBranchId());
					}
				} else if (FOR_MANAGER == forWhat) {
					if (dealerManagerMap.containsKey(cdr.getResponsibleId())) {
						managerId = dealerManagerMap.get(cdr.getResponsibleId());

						if (recoFromDemoMap.containsKey(managerId)) {
							temp1 = recoFromDemoMap.get(managerId);
						}
					}

				} else if (FOR_DEALER == forWhat) {
					if (recoFromDemoMap.containsKey(cdr.getResponsibleId())) {
						temp1 = recoFromDemoMap.get(cdr.getResponsibleId());
					}
				}
				// System.out.println("CDR: " + cdr.toString());
				temp1.add(cdr);

				if (FOR_BRANCH == forWhat) {
					recoFromDemoMap.put(cdr.getBranchId(), temp1);
				} else if (FOR_MANAGER == forWhat) {
					if (!Validation.isEmptyLong(managerId)) {
						recoFromDemoMap.put(managerId, temp1);
					}
				} else if (FOR_DEALER == forWhat) {
					recoFromDemoMap.put(cdr.getResponsibleId(), temp1);
				}

			}

			if (!Validation.isEmptyLong(cdr.getVisitId())) {
				List<CrmDocReco> temp2 = new ArrayList<>();
				Long managerId = null;
				if (FOR_BRANCH == forWhat) {
					if (recoFromVisitMap.containsKey(cdr.getBranchId())) {
						temp2 = recoFromVisitMap.get(cdr.getBranchId());
					}
				} else if (FOR_MANAGER == forWhat) {
					if (dealerManagerMap.containsKey(cdr.getResponsibleId())) {
						managerId = dealerManagerMap.get(cdr.getResponsibleId());

						if (recoFromVisitMap.containsKey(managerId)) {
							temp2 = recoFromVisitMap.get(managerId);
						}
					}
				} else if (FOR_DEALER == forWhat) {
					if (recoFromVisitMap.containsKey(cdr.getResponsibleId())) {
						temp2 = recoFromVisitMap.get(cdr.getResponsibleId());
					}
				}
				temp2.add(cdr);

				if (FOR_BRANCH == forWhat) {
					recoFromVisitMap.put(cdr.getBranchId(), temp2);
				} else if (FOR_MANAGER == forWhat) {
					if (!Validation.isEmptyLong(managerId)) {
						recoFromVisitMap.put(managerId, temp2);
					}
				} else if (FOR_DEALER == forWhat) {
					recoFromVisitMap.put(cdr.getResponsibleId(), temp2);
				}
			}
		}

		// System.out.println(recoFromDemoMap.size() + " => " +
		// recoFromVisitMap.size());

		for (CrmStandartReport csr : target) {
			if (recoFromDemoMap.containsKey(csr.getId())) {
				List<CrmDocReco> temp = recoFromDemoMap.get(csr.getId());
				csr.setRecoCount(temp.size());
			}

			if (recoFromVisitMap.containsKey(csr.getId())) {
				List<CrmDocReco> temp = recoFromVisitMap.get(csr.getId());
				csr.setRecoFromVisitCount(temp.size());
			}
		}
	}

	private void prepareDemoDataFor(List<CrmDocDemo> demoList, List<CrmStandartReport> target, int forWhat) {
		Map<Long, List<CrmDocDemo>> allDemoMap = new HashMap<>();
		Map<Long, List<CrmDocDemo>> shownDemoMap = new HashMap<>();
		Map<Long, List<CrmDocDemo>> demoFromDemoMap = new HashMap<>();

		Map<Long, Long> dealerManagerMap = new HashMap<>();
		if (FOR_MANAGER == forWhat) {
			for (CrmStandartReport csr : target) {
				for (Long dealerId : csr.getDealerIds()) {
					dealerManagerMap.put(dealerId, csr.getId());
				}
			}
		}

		for (CrmDocDemo demo : demoList) {
			if (!Validation.isEmptyLong(demo.getRecoId())) {
				List<CrmDocDemo> temp1 = new ArrayList<>();
				Long managerId = null;
				if (FOR_BRANCH == forWhat) {
					if (allDemoMap.containsKey(demo.getBranchId())) {
						temp1 = allDemoMap.get(demo.getBranchId());
					}
				} else if (FOR_MANAGER == forWhat) {
					if (dealerManagerMap.containsKey(demo.getDealerId())) {
						managerId = dealerManagerMap.get(demo.getDealerId());

						if (allDemoMap.containsKey(managerId)) {
							temp1 = allDemoMap.get(managerId);
						}
					}

				} else if (FOR_DEALER == forWhat) {
					if (allDemoMap.containsKey(demo.getDealerId())) {
						temp1 = allDemoMap.get(demo.getDealerId());
					}
				}
				// System.out.println("CDR: " + cdr.toString());
				temp1.add(demo);

				if (FOR_BRANCH == forWhat) {
					allDemoMap.put(demo.getBranchId(), temp1);
				} else if (FOR_MANAGER == forWhat) {
					if (!Validation.isEmptyLong(managerId)) {
						allDemoMap.put(managerId, temp1);
					}
				} else if (FOR_DEALER == forWhat) {
					allDemoMap.put(demo.getDealerId(), temp1);
				}
			} else if (!Validation.isEmptyLong(demo.getParentId())) {
				List<CrmDocDemo> temp1 = new ArrayList<>();
				Long managerId = null;
				if (FOR_BRANCH == forWhat) {
					if (demoFromDemoMap.containsKey(demo.getBranchId())) {
						temp1 = demoFromDemoMap.get(demo.getBranchId());
					}
				} else if (FOR_MANAGER == forWhat) {
					if (dealerManagerMap.containsKey(demo.getDealerId())) {
						managerId = dealerManagerMap.get(demo.getDealerId());

						if (demoFromDemoMap.containsKey(managerId)) {
							temp1 = demoFromDemoMap.get(managerId);
						}
					}

				} else if (FOR_DEALER == forWhat) {
					if (demoFromDemoMap.containsKey(demo.getDealerId())) {
						temp1 = demoFromDemoMap.get(demo.getDealerId());
					}
				}
				// System.out.println("CDR: " + cdr.toString());
				temp1.add(demo);

				if (FOR_BRANCH == forWhat) {
					demoFromDemoMap.put(demo.getBranchId(), temp1);
				} else if (FOR_MANAGER == forWhat) {
					if (!Validation.isEmptyLong(managerId)) {
						demoFromDemoMap.put(managerId, temp1);
					}
				} else if (FOR_DEALER == forWhat) {
					demoFromDemoMap.put(demo.getDealerId(), temp1);
				}
			}

			if (!DemoConstants.RESULT_CANCELLED.equals(demo.getResultId())
					&& !DemoConstants.RESULT_UNKNOWN.equals(demo.getResultId())
					&& DemoConstants.RESULT_MOVED.equals(demo.getResultId())) {
				List<CrmDocDemo> temp2 = new ArrayList<>();
				Long managerId = null;
				if (FOR_BRANCH == forWhat) {
					if (shownDemoMap.containsKey(demo.getBranchId())) {
						temp2 = shownDemoMap.get(demo.getBranchId());
					}
				} else if (FOR_MANAGER == forWhat) {
					if (dealerManagerMap.containsKey(demo.getDealerId())) {
						managerId = dealerManagerMap.get(demo.getDealerId());

						if (shownDemoMap.containsKey(managerId)) {
							temp2 = shownDemoMap.get(managerId);
						}
					}
				} else if (FOR_DEALER == forWhat) {
					if (shownDemoMap.containsKey(demo.getDealerId())) {
						temp2 = shownDemoMap.get(demo.getDealerId());
					}
				}
				temp2.add(demo);

				if (FOR_BRANCH == forWhat) {
					shownDemoMap.put(demo.getBranchId(), temp2);
				} else if (FOR_MANAGER == forWhat) {
					if (!Validation.isEmptyLong(managerId)) {
						shownDemoMap.put(managerId, temp2);
					}
				} else if (FOR_DEALER == forWhat) {
					shownDemoMap.put(demo.getDealerId(), temp2);
				}
			}
		}

		// System.out.println(recoFromDemoMap.size() + " => " +
		// recoFromVisitMap.size());

		for (CrmStandartReport csr : target) {
			if (allDemoMap.containsKey(csr.getId())) {
				List<CrmDocDemo> temp = allDemoMap.get(csr.getId());
				csr.setDemoCount(temp.size());
			}

			if (shownDemoMap.containsKey(csr.getId())) {
				List<CrmDocDemo> temp = shownDemoMap.get(csr.getId());
				csr.setShownDemoCount(temp.size());
			}

			if (demoFromDemoMap.containsKey(csr.getId())) {
				List<CrmDocDemo> temp = demoFromDemoMap.get(csr.getId());
				csr.setDemoFromDemoCount(temp.size());
			}
		}
	}

}
