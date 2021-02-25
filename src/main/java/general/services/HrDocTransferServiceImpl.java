package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.Validation;
import general.dao.DAOException;
import general.dao.HrDocTransferDao;
import general.dao.PyramidDao;
import general.dao.SalaryDao;
import general.dao.UserDao;
import general.tables.HrDocTransfer;
import general.tables.HrDocTransferApprover;
import general.tables.HrDocTransferItem;
import general.tables.MyDocs;
import general.tables.Pyramid;
import general.tables.Salary;
import general.validators.HrDocTransferValidator;
import user.User;

@Service("hrDocTransferService")
public class HrDocTransferServiceImpl implements HrDocTransferService {

	@Autowired
	HrDocTransferDao hrDocTransferDao;

	@Autowired
	HrDocTransferValidator validator;

	@Autowired
	MyDocsService mdService;

	@Autowired
	UserDao userDao;

	@Autowired
	SalaryDao salDao;

	@Autowired
	PyramidDao pyramidDao;

	@Autowired
	SalaryService salService;

	@Override
	public void create(HrDocTransfer doc, User userData) throws DAOException {
		doc.setId(null);
		doc.setCreatedAt(Calendar.getInstance().getTime());
		doc.setUpdatedAt(Calendar.getInstance().getTime());
		doc.setCreatedBy(userData.getUserid());
		doc.setUpdatedBy(userData.getUserid());
		doc.setStatusId(HrDocTransfer.STATUS_ON_CREATE);
		doc.setCurrentRespId(userData.getUserid());
		if (!validator.isValid(doc)) {
			throw new DAOException(validator.getError());
		}

		setItemManagers(doc);
		setApprovers(doc);

		hrDocTransferDao.create(doc);

		// На создании у автора
		mdService.addMd(doc, userData, doc.getCreatedBy(), MyDocs.STATUS_CREATE);
	}

	private void setItemManagers(HrDocTransfer doc) {
		for (HrDocTransferItem item : doc.getHrDocTransferItems()) {
			/**
			 * Если дилер (POSITION_ID=4), то добавляем менеджера
			 */
			if (item.getFromSalary() != null && item.getFromSalary().getPosition_id() == 4L) {
				item.setFromManagerId(hrDocTransferDao.getStaffParentManagerId(item.getFromSalary()));
			} else {
				System.err.println("DDD: " + item.getFromSalary());
			}
		}
	}

	private void setApprovers(HrDocTransfer doc) {

		if (HrDocTransfer.STATUS_ON_CREATE.equals(doc.getStatusId())) {
			Map<Long, Integer> branchesMap = new HashMap<>();
			for (HrDocTransferItem item : doc.getHrDocTransferItems()) {
				branchesMap.put(item.getBranchId(), 1);
			}

			branchesMap.put(doc.getBranchId(), 1);
			String[] branchIds = new String[branchesMap.size()];
			int k = 0;
			for (Entry<Long, Integer> e : branchesMap.entrySet()) {
				branchIds[k] = e.getKey().toString();
				k++;
			}

			List<HrDocTransferApprover> approverList = new ArrayList<>();

			/**
			 * 10 - position id Директора
			 */
			Map<Long, Salary> userSalaryMap = hrDocTransferDao.getUserSalaryMap(branchIds, 10L);
			int order = 0;
			for (Entry<Long, Salary> e : userSalaryMap.entrySet()) {
				HrDocTransferApprover approver = new HrDocTransferApprover();
				approver.setIsCurrent(0);
				approver.setSortOrder(order);
				approver.setStatusId(0);
				approver.setTitle(e.getValue().getP_staff().getLF());
				approver.setPositionId(e.getValue().getPosition_id());
				approver.setHrDocTransfer(doc);
				approver.setUserId(e.getKey());
				approverList.add(approver);
				order++;
			}

			doc.setHrDocTransferApprovers(approverList);

		}
	}

	@Override
	public void update(HrDocTransfer doc, User userData) throws DAOException {
		doc.setUpdatedAt(Calendar.getInstance().getTime());
		doc.setUpdatedBy(userData.getUserid());
		doc.setCurrentRespId(userData.getUserid());
		if (!validator.isValid(doc)) {
			throw new DAOException(validator.getError());
		}
		setItemManagers(doc);
		setApprovers(doc);
		hrDocTransferDao.update(doc);
		mdService.addMd(doc, userData, doc.getUpdatedBy(), MyDocs.STATUS_CREATE);
	}

	/**
	 * Отправка документа Отправляет фин менеджер
	 */
	@Override
	public void send(HrDocTransfer doc, User userData) throws DAOException {
		if (!HrDocTransfer.STATUS_ON_CREATE.equals(doc.getStatusId())
				&& !HrDocTransfer.STATUS_ON_EXECUTION.equals(doc.getStatusId())) {
			throw new DAOException("Нет доступа!");
		}

		mdService.addMd(doc, userData, userData.getUserid(), MyDocs.STATUS_SENT);

		goNext(doc, userData);
	}

	private void goNext(HrDocTransfer doc, User userData) {
		HrDocTransferApprover currentApprover = null;
		List<HrDocTransferApprover> approvers = doc.getHrDocTransferApprovers();
		List<HrDocTransferApprover> tempList = new ArrayList<>();
		if (approvers == null) {

		} else {
			for (HrDocTransferApprover ap : approvers) {
				// if (!doc.getCurrentRespId().equals(ap.getUserId())
				// && ap.getStatusId() == HrDocTransferApprover.STATUS_NONE) {
				// currentApprover = ap;
				// break;
				// }
				if (HrDocTransferApprover.STATUS_NONE.equals(ap.getStatusId())) {
					tempList.add(ap);
				}
			}
		}

		// if (HrDocTransfer.STATUS_ON_CREATE.equals(doc.getStatusId())
		// || HrDocTransfer.STATUS_ON_EXECUTION.equals(doc.getStatusId())) {
		// mdService.addMd(doc, userData, userData.getUserid(),
		// MyDocs.STATUS_SENT);
		// }

		if (tempList.size() == 0) {
			mdService.addMd(doc, userData, doc.getResponsibleId(), MyDocs.STATUS_IN);
			doc.setCurrentRespId(doc.getResponsibleId());
			doc.setStatusId(HrDocTransfer.STATUS_ON_EXECUTION);
		} else {
			for (HrDocTransferApprover apr : tempList) {
				mdService.addMd(doc, userData, apr.getUserId(), MyDocs.STATUS_IN);
				doc.setStatusId(HrDocTransfer.STATUS_ON_APPROVEMENT);
			}
			// doc.setCurrentRespId(currentApprover.getUserId());
		}

		hrDocTransferDao.update(doc);
	}

	/**
	 * ДЕЙСТВИЕ ОТКАЗ
	 */
	@Override
	public void refuse(HrDocTransfer doc, User userData) throws DAOException {
		if (!HrDocTransfer.STATUS_ON_APPROVEMENT.equals(doc.getStatusId())) {
			throw new DAOException("Нет доступа!");
		}

		for (HrDocTransferApprover appr : doc.getHrDocTransferApprovers()) {
			if (userData.getUserid().equals(appr.getUserId())) {
				appr.setStatusId(HrDocTransferApprover.STATUS_REFUSED);
				break;
			}
		}

		mdService.addMd(doc, userData, userData.getUserid(), MyDocs.STATUS_REFUSED);

		doc.setStatusId(HrDocTransfer.STATUS_REFUSED);
		hrDocTransferDao.update(doc);
	}

	/**
	 * ДЕЙСТВИЕ СОГЛАСОВАНИЕ
	 */
	@Override
	public void approve(HrDocTransfer doc, User userData) throws DAOException {
		if (!HrDocTransfer.STATUS_ON_APPROVEMENT.equals(doc.getStatusId())) {
			throw new DAOException("Нет доступа!");
		}

		if (doc.getHrDocTransferApprovers() == null) {
			return;
		}

		for (HrDocTransferApprover appr : doc.getHrDocTransferApprovers()) {
			if (userData.getUserid().equals(appr.getUserId())) {
				appr.setStatusId(HrDocTransferApprover.STATUS_APPROVED);
				break;
			}
		}

		mdService.addMd(doc, userData, userData.getUserid(), MyDocs.STATUS_CONFIRMED);
		goNext(doc, userData);
	}

	@Override
	public void close(HrDocTransfer hdt, User userData) throws DAOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addApprover(HrDocTransfer doc, Salary salary, User userData) throws DAOException {
		if (!HrDocTransfer.STATUS_ON_EXECUTION.equals(doc.getStatusId())) {
			throw new DAOException("Нет доступа!");
		}

		List<general.tables.User> uList = userDao.findAll(" staff_id = " + salary.getStaff_id());
		if (uList.size() == 0) {
			throw new DAOException("Не найден пользователь у сотрудника!");
		}

		HrDocTransferApprover temp = new HrDocTransferApprover();
		temp.setCreatedBy(userData.getUserid());
		temp.setPositionId(salary.getPosition_id());
		temp.setSortOrder(0);
		temp.setStatusId(HrDocTransferApprover.STATUS_NONE);
		temp.setTitle(salary.getP_staff().getLF());
		temp.setUserId(uList.get(0).getUser_id());

		doc.addHrDocTransferApprover(temp);

		hrDocTransferDao.update(doc);
	}

	@Override
	public void addAmount(HrDocTransfer doc, User userData) throws DAOException {
		if (!HrDocTransfer.STATUS_ON_EXECUTION.equals(doc.getStatusId())) {
			throw new DAOException("Нет доступа!");
		}

		hrDocTransferDao.update(doc);
	}

	@Override
	public void addSalariesAndCloseDoc(HrDocTransfer doc, User userData) throws DAOException {
		if (!HrDocTransfer.STATUS_ON_EXECUTION.equals(doc.getStatusId())) {
			throw new DAOException("Нет доступа!");
		}

		Salary oldSal;
		Salary newSal;
		Calendar cal = Calendar.getInstance();
		for (HrDocTransferItem item : doc.getHrDocTransferItems()) {
			oldSal = salDao.find(item.getFromSalaryId());
			cal.setTime(item.getBeginDate());
			cal.add(Calendar.DAY_OF_YEAR, -1);
			oldSal.setEnd_date(cal.getTime());
			salDao.update(oldSal);

			if (!Validation.isEmptyLong(item.getFromManagerId())) {// ??
				List<Pyramid> tempList = pyramidDao.dynamicFindPyramid(
						String.format(" position_id = %d AND staff_id = %d", 3, item.getFromManagerId()));
				if (tempList.size() > 0) {
					List<Pyramid> oldPyrs = pyramidDao.dynamicFindPyramid(
							String.format(" parent_pyramid_id = %d AND position_id = %d AND staff_id = %d",
									tempList.get(0).getPyramid_id(), oldSal.getPosition_id(), oldSal.getStaff_id()));
					for (Pyramid p : oldPyrs) {
						pyramidDao.delete(p.getPyramid_id());
					}
				}
			}

			Long parentPyramidId = 0L;
			if (!Validation.isEmptyLong(item.getManagerId())) {
				List<Pyramid> pyramids = pyramidDao
						.dynamicFindPyramid(String.format(" bukrs = '%s' AND staff_id = %d AND position_id = %d ",
								doc.getBukrs(), item.getManagerId(), 3));// 3-Manager
																			// Id
				if (pyramids.size() > 0) {
					parentPyramidId = pyramids.get(0).getPyramid_id();
				}
			}

			/// New Salary
			newSal = new Salary();
			newSal.setAmount(item.getAmount());
			newSal.setBeg_date(item.getBeginDate());
			newSal.setBranch_id(item.getBranchId());
			newSal.setBukrs(doc.getBukrs());
			newSal.setBusiness_area_id(item.getBussinessAreaId());
			newSal.setCountry_id(oldSal.getCountry_id());
			newSal.setCreated_by(userData.getUserid());
			newSal.setCreated_date(Calendar.getInstance().getTime());
			newSal.setDepartment_id(item.getDepartmentId());
			newSal.setHr_doc_id(doc.getId());
			newSal.setNote("Выполнено Перевод");
			newSal.setPosition_id(item.getPositionId());
			newSal.setSalary_type("monthly");
			newSal.setStaff_id(oldSal.getStaff_id());
			newSal.setUpdated_by(userData.getUserid());
			newSal.setUpdated_date(Calendar.getInstance().getTime());
			newSal.setWaers(Validation.isEmptyString(item.getCurrency()) ? oldSal.getWaers() : item.getCurrency());
			newSal.setParent_pyramid_id(parentPyramidId);
			newSal.setP_staff(item.getFromSalary().getP_staff());
			salService.createSalary(newSal, parentPyramidId);
		}

		doc.setStatusId(HrDocTransfer.STATUS_CLOSED);
		hrDocTransferDao.update(doc);
		mdService.addMd(doc, userData, userData.getUserid(), MyDocs.STATUS_CLOSED);
		mdService.addMd(doc, userData, doc.getCreatedBy(), MyDocs.STATUS_CLOSED);
	}

	@Override
	public void removeApprover(HrDocTransfer doc, User userData, Long approverId) throws DAOException {
		System.out.println("APP ID:  " + approverId);
		List<HrDocTransferApprover> newList = new ArrayList<>();
		boolean hasApps = false;
		String error = "";
		HrDocTransferApprover apForRemove = null;
		for (HrDocTransferApprover approver : doc.getHrDocTransferApprovers()) {
			if (approver.getId().equals(approverId)) {
				if (!HrDocTransferApprover.STATUS_NONE.equals(approver.getStatusId())) {
					error = "Согласующий не может быть удален, так как уже совершил действие";
				}
				apForRemove = approver;
			} else {
				if (HrDocTransferApprover.STATUS_NONE.equals(approver.getStatusId())) {
					hasApps = true;
				}
				newList.add(approver);
			}
		}

		if (!Validation.isEmptyString(error)) {
			throw new DAOException(error);
		}

		if (apForRemove == null) {
			throw new DAOException("Согласующий не найден!");
		}

		doc.setHrDocTransferApprovers(newList);

		if (!hasApps) {
			mdService.addMd(doc, userData, doc.getResponsibleId(), MyDocs.STATUS_IN);
			doc.setStatusId(HrDocTransfer.STATUS_ON_EXECUTION);
		}

		mdService.removeMd(doc, apForRemove.getUserId());
		hrDocTransferDao.update(doc);
	}
}