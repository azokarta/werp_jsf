package general.services.hr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dit.message.UserStaff;
import general.Validation;
import general.dao.DAOException;
import general.dao.HrDocDao;
import general.dao.PyramidDao;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.dao.UserDao;
import general.services.MyDocsService;
import general.services.SalaryService;
import general.springservice.MessageSpSer;
import general.tables.HrDoc;
import general.tables.HrDocActionLog;
import general.tables.HrDocApprover;
import general.tables.HrDocItem;
import general.tables.MyDocs;
import general.tables.Pyramid;
import general.tables.Salary;
import general.tables.Staff;
import general.tables2.MessageHeader;
import general.validators.HrDocValidator;
import user.User;

@Service("hrDocService")
public class HrDocServiceImpl implements HrDocService {

	@Autowired
	HrDocDao hrDocDao;

	@Autowired
	HrDocValidator validator;

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

	@Autowired
	HrDocActionLogService logService;

	@Autowired
	MessageSpSer messageService;

	@Autowired
	StaffDao staffDao;

	private static final String NOTIFY_HEADER_TXT = "Документ на согласования(%s) ";
	private static final String NOTIFY_BODY_TXT = "Документ на согласования. Для просмотра пройдите по ссылке: <a target=\"_blank\" href=\"%s/hr/doc/View.xhtml?id=%d\">%s<a/>";

	// private HrDoc document;
	//
	// public HrDocServiceImpl(HrDoc document) {
	// this.document = document;
	// }

	@Transactional
	@Override
	public void create(HrDoc doc, User userData) throws DAOException {
		doc.setId(null);
		doc.setCreatedAt(Calendar.getInstance().getTime());
		doc.setUpdatedAt(Calendar.getInstance().getTime());
		doc.setCreatedBy(userData.getUserid());
		doc.setUpdatedBy(userData.getUserid());
		// doc.setCurrentRespId(userData.getUserid());

		if (!validator.isValid(doc)) {
			throw new DAOException(validator.getError());
		}

		setItemManagers(doc);
		setApprovers(doc);
		hrDocDao.create(doc);

		// На создании у автора
		mdService.addMd(doc, userData, doc.getCreatedBy(), MyDocs.STATUS_CREATE);

		HrDocActionLog log = new HrDocActionLog(HrDocActionLog.ACTION_CREATE, null, doc.getId(), userData.getUserid());
		logService.create(log, userData.getUserid());
	}

	private void setItemManagers(HrDoc doc) {
		if (doc.getTypeId() == HrDoc.TYPE_TRANSFER) {
			for (HrDocItem item : doc.getHrDocItems()) {
				/**
				 * Если дилер (POSITION_ID=4), то добавляем менеджера
				 */
				if (item.getSalary() != null && item.getSalary().getPosition_id() == 4L) {
					item.setOldManagerId(hrDocDao.getStaffParentManagerId(item.getSalary()));
				} else {
					// System.err.println("DDD: " + item.getFromSalary());
				}
			}
		}
	}

	private void setApprovers(HrDoc doc) {

		if (HrDoc.STATUS_ON_CREATE == doc.getStatusId() && doc.getTypeId() == HrDoc.TYPE_TRANSFER) {
			Map<Long, Integer> branchesMap = new HashMap<>();
			for (HrDocItem item : doc.getHrDocItems()) {
				branchesMap.put(item.getBranchId(), 1);
			}

			branchesMap.put(doc.getBranchId(), 1);
			String[] branchIds = new String[branchesMap.size()];
			int k = 0;
			for (Entry<Long, Integer> e : branchesMap.entrySet()) {
				branchIds[k] = e.getKey().toString();
				k++;
			}

			List<HrDocApprover> approverList = new ArrayList<>();

			/**
			 * 10 - position id Директора
			 */
			Map<Long, Salary> userSalaryMap = hrDocDao.getUserSalaryMap(branchIds, 10L);

			for (Entry<Long, Salary> e : userSalaryMap.entrySet()) {
				HrDocApprover approver = new HrDocApprover();
				// approver.setIsCurrent(0);
				// approver.setSortOrder(order);
				approver.setStatusId(HrDocApprover.STATUS_NONE);
				approver.setTitle(e.getValue().getP_staff().getLF());
				approver.setPositionId(e.getValue().getPosition_id());
				approver.setHrDoc(doc);
				approver.setUserId(e.getKey());
				approver.setStaffId(e.getValue().getStaff_id());
				approverList.add(approver);
			}

			doc.setHrDocApprovers(approverList);

		}
	}

	@Override
	public void update(HrDoc doc, User userData) throws DAOException {
		doc.setUpdatedAt(Calendar.getInstance().getTime());
		doc.setUpdatedBy(userData.getUserid());
		// doc.setCurrentRespId(userData.getUserid());
		if (!validator.isValid(doc)) {
			throw new DAOException(validator.getError());
		}
		setItemManagers(doc);
		setApprovers(doc);
		hrDocDao.update(doc);
		mdService.addMd(doc, userData, doc.getUpdatedBy(), MyDocs.STATUS_CREATE);

		// LOG
		HrDocActionLog log = new HrDocActionLog(HrDocActionLog.ACTION_UPDATE, null, doc.getId(), userData.getUserid());
		logService.create(log, userData.getUserid());
	}

	/**
	 * Отправка документа Отправляет фин менеджер
	 */
	@Override
	public void send(HrDoc doc, User userData) throws DAOException {
		if (HrDoc.STATUS_ON_CREATE != doc.getStatusId() && HrDoc.STATUS_ON_EXECUTION != doc.getStatusId()) {
			throw new DAOException("Нет доступа!");
		}

		mdService.addMd(doc, userData, userData.getUserid(), MyDocs.STATUS_SENT);

		goNext(doc, userData);

		// LOG
		HrDocActionLog log = new HrDocActionLog(HrDocActionLog.ACTION_SEND, null, doc.getId(), userData.getUserid());
		logService.create(log, userData.getUserid());
	}

	private void goNext(HrDoc doc, User userData) {
		List<HrDocApprover> approvers = doc.getHrDocApprovers();
		// List<HrDocApprover> tempList = new ArrayList<>();
		HrDocApprover nextApprover = null;
		if (approvers == null) {

		} else {
			for (HrDocApprover ap : approvers) {
				if (HrDocApprover.STATUS_NONE == ap.getStatusId()) {
					// tempList.add(ap);
					nextApprover = ap;
					break;
				}
			}
		}

		if (nextApprover == null) {
			mdService.addMd(doc, userData, doc.getResponsibleId(), MyDocs.STATUS_IN);
			// doc.setCurrentRespId(doc.getResponsibleId());
			doc.setStatusId(HrDoc.STATUS_ON_EXECUTION);
			sendNotify(doc, userData.getUserid(), doc.getResponsibleId(), "Аяпов Бахтияр");
		} else {
			// nextApprover.setStatusId(HrDocApprover.S);
			mdService.addMd(doc, userData, nextApprover.getUserId(), MyDocs.STATUS_IN);
			doc.setStatusId(HrDoc.STATUS_ON_APPROVEMENT);
			sendNotify(doc, userData.getUserid(), nextApprover.getUserId(), nextApprover.getTitle());
		}

		// if (tempList.size() == 0) {
		// mdService.addMd(doc, userData, doc.getResponsibleId(),
		// MyDocs.STATUS_IN);
		// // doc.setCurrentRespId(doc.getResponsibleId());
		// doc.setStatusId(HrDoc.STATUS_ON_EXECUTION);
		// sendNotify(doc, userData.getUserid(), doc.getResponsibleId());
		// // sendNotify();
		// } else {
		// for (HrDocApprover apr : tempList) {
		// mdService.addMd(doc, userData, apr.getUserId(), MyDocs.STATUS_IN);
		// doc.setStatusId(HrDoc.STATUS_ON_APPROVEMENT);
		// sendNotify(doc, userData.getUserid(), apr.getUserId());
		// }
		// // doc.setCurrentRespId(currentApprover.getUserId());
		// }

		hrDocDao.update(doc);
	}

	/**
	 * ДЕЙСТВИЕ ОТКАЗ
	 */
	@Override
	public void refuse(HrDoc doc, User userData, String note) throws DAOException {
		if (doc.getStatusId() != HrDoc.STATUS_ON_APPROVEMENT) {
			throw new DAOException("Нет доступа!");
		}

		for (HrDocApprover appr : doc.getHrDocApprovers()) {
			if (userData.getUserid().equals(appr.getUserId())) {
				appr.setStatusId(HrDocApprover.STATUS_REFUSED);
				break;
			}
		}

		mdService.addMd(doc, userData, userData.getUserid(), MyDocs.STATUS_REFUSED);
		mdService.addMd(doc, userData, doc.getCreatedBy(), MyDocs.STATUS_REFUSED);
		mdService.addMd(doc, userData, doc.getResponsibleId(), MyDocs.STATUS_REFUSED);

		doc.setStatusId(HrDoc.STATUS_REFUSED);
		hrDocDao.update(doc);

		// LOG
		HrDocActionLog log = new HrDocActionLog(HrDocActionLog.ACTION_REFUSE, null, doc.getId(), userData.getUserid());
		log.setNote(note);
		logService.create(log, userData.getUserid());
	}

	/**
	 * ДЕЙСТВИЕ СОГЛАСОВАНИЕ
	 */
	@Override
	public void approve(HrDoc doc, User userData) throws DAOException {
		if (doc.getStatusId() != HrDoc.STATUS_ON_APPROVEMENT) {
			throw new DAOException("Нет доступа!");
		}

		if (doc.getHrDocApprovers() == null) {
			return;
		}

		for (HrDocApprover appr : doc.getHrDocApprovers()) {
			if (userData.getUserid().equals(appr.getUserId())) {
				appr.setStatusId(HrDocApprover.STATUS_APPROVED);
				break;
			}
		}

		mdService.addMd(doc, userData, userData.getUserid(), MyDocs.STATUS_CONFIRMED);
		goNext(doc, userData);

		// LOG
		HrDocActionLog log = new HrDocActionLog(HrDocActionLog.ACTION_APPROVE, null, doc.getId(), userData.getUserid());
		logService.create(log, userData.getUserid());
	}

	@Override
	public void close(HrDoc doc, User userData) throws DAOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addApprover(HrDoc doc, Salary salary, User userData) throws DAOException {
		if (doc.getStatusId() != HrDoc.STATUS_ON_EXECUTION && doc.getStatusId() != HrDoc.STATUS_ON_CREATE) {
			throw new DAOException("Нет доступа!");
		}

		List<general.tables.User> uList = userDao.findAll(" staff_id = " + salary.getStaff_id());
		if (uList.size() == 0) {
			throw new DAOException("Не найден пользователь у сотрудника!");
		}

		HrDocApprover temp = new HrDocApprover();
		temp.setStaffId(salary.getStaff_id());
		temp.setCreatedBy(userData.getUserid());
		temp.setPositionId(salary.getPosition_id());
		temp.setStatusId(HrDocApprover.STATUS_NONE);
		temp.setTitle(salary.getP_staff().getLF());
		temp.setUserId(uList.get(0).getUser_id());

		doc.addHrDocApprover(temp);

		hrDocDao.update(doc);

		// LOG
		HrDocActionLog log = new HrDocActionLog(HrDocActionLog.ACTION_ADD_APPROVER, null, doc.getId(),
				userData.getUserid());
		logService.create(log, userData.getUserid());
	}

	@Override
	public void addAmount(HrDoc doc, User userData) throws DAOException {
		if (doc.getStatusId() != HrDoc.STATUS_ON_EXECUTION) {
			throw new DAOException("Нет доступа!");
		}

		hrDocDao.update(doc);

		HrDocActionLog log = new HrDocActionLog(HrDocActionLog.ACTION_ADD_AMOUNT, null, doc.getId(),
				userData.getUserid());
		logService.create(log, userData.getUserid());
	}

	@Override
	public void addSalariesAndCloseDoc(HrDoc doc, User userData) throws DAOException {
		if (doc.getStatusId() != HrDoc.STATUS_ON_EXECUTION) {
			throw new DAOException("Нет доступа!");
		}

		Salary oldSal = null;
		Salary newSal;
		Calendar cal = Calendar.getInstance();
		List<Salary> salList = new ArrayList<>();
		for (HrDocItem item : doc.getHrDocItems()) {
			if (doc.getTypeId() == HrDoc.TYPE_TRANSFER) {
				oldSal = salDao.find(item.getSalaryId());
				cal.setTime(item.getBeginDate());
				cal.add(Calendar.DAY_OF_YEAR, -1);
				oldSal.setEnd_date(cal.getTime());
				// salDao.update(oldSal);
				// Проверка на Изменение толька зар/плата сотрудника
				if (isOnlySalaryChanged(item, oldSal)) {
					salDao.update(oldSal);
				} else {
					salService.removeSalary(oldSal, userData.getUserid());
				}

			}

			if (doc.getTypeId() == HrDoc.TYPE_TRANSFER && !Validation.isEmptyLong(item.getManagerId())) {// ??
				List<Pyramid> tempList = pyramidDao.dynamicFindPyramid(
						String.format(" position_id = %d AND staff_id = %d", 3, item.getManagerId()));
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
			newSal.setBusiness_area_id(item.getBusinessAreaId());
			newSal.setCreated_by(userData.getUserid());
			newSal.setCreated_date(Calendar.getInstance().getTime());
			newSal.setDepartment_id(item.getDepartmentId());
			newSal.setHr_doc_id(doc.getId());
			newSal.setNote(doc.getTypeId() == HrDoc.TYPE_TRANSFER ? "Выполнено Перевод" : "Принято на работу");
			newSal.setPosition_id(item.getPositionId());
			newSal.setSalary_type("monthly");
			newSal.setStaff_id(item.getStaffId());
			newSal.setUpdated_by(userData.getUserid());
			newSal.setUpdated_date(Calendar.getInstance().getTime());
			if (doc.getTypeId() == HrDoc.TYPE_TRANSFER) {
				newSal.setCountry_id(oldSal.getCountry_id());
				newSal.setWaers(Validation.isEmptyString(item.getCurrency()) ? oldSal.getWaers() : item.getCurrency());
			} else {
				newSal.setWaers(item.getCurrency());
				newSal.setCountry_id(0L);
			}

			newSal.setParent_pyramid_id(parentPyramidId);
			newSal.setP_staff(item.getStaff());
			// salService.createSalary(newSal, parentPyramidId);
			salList.add(newSal);
		}

		for (Salary sal : salList) {
			salService.createSalary(sal, sal.getParent_pyramid_id());
		}

		doc.setStatusId(HrDoc.STATUS_CLOSED);
		hrDocDao.update(doc);
		mdService.addMd(doc, userData, userData.getUserid(), MyDocs.STATUS_CLOSED);
		mdService.addMd(doc, userData, doc.getCreatedBy(), MyDocs.STATUS_CLOSED);

		// LOG
		HrDocActionLog log = new HrDocActionLog(HrDocActionLog.ACTION_ADD_SALARY, null, doc.getId(),
				userData.getUserid());
		log.setNote("Процесс завершен. Документ закрыт.");
		logService.create(log, userData.getUserid());
	}

	private boolean isOnlySalaryChanged(HrDocItem item, Salary oldSal) {
		if (!oldSal.getBukrs().equals(item.getBukrs())) {
			return false;
		}

		if (!oldSal.getBranch_id().equals(item.getBranchId())) {
			return false;
		}

		if (!oldSal.getPosition_id().equals(item.getPositionId())) {
			return false;
		}

		// if (!Validation.isEmptyLong(item.getManagerId()) &&
		// !Validation.isEmptyLong(oldSal.getParent_pyramid_id())) {
		if (!Validation.isEmptyLong(item.getManagerId())) {
			Pyramid currPyramid = pyramidDao.findPyramid(oldSal.getBukrs(), oldSal.getBranch_id(), oldSal.getStaff_id(),
					oldSal.getPosition_id());
			if (currPyramid != null) {
				Pyramid curPyrParent = pyramidDao.find(currPyramid.getParent_pyramid_id());
				if (curPyrParent != null && !item.getManagerId().equals(curPyrParent.getStaff_id())) {
					return false;
				}
			}
			// Pyramid oldSalParentPyramid =
			// pyramidDao.find(oldSal.getParent_pyramid_id());
			// if
			// (!item.getManagerId().equals(oldSalParentPyramid.getStaff_id()))
			// {
			// return false;
			// }
		} else {
			// throw new DAOException("NO OLD SAL PARENT pYRAMID");
		}

		return true;
	}

	@Override
	public void removeApprover(HrDoc doc, User userData, Long approverId) throws DAOException {
		// System.out.println("APP ID: " + approverId);
		List<HrDocApprover> newList = new ArrayList<>();
		boolean hasApps = false;
		String error = "";
		HrDocApprover apForRemove = null;
		for (HrDocApprover approver : doc.getHrDocApprovers()) {
			if (approver.getId().equals(approverId)) {
				if (HrDocApprover.STATUS_NONE != approver.getStatusId()) {
					error = "Согласующий не может быть удален, так как уже совершил действие";
				}
				apForRemove = approver;
			} else {
				if (HrDocApprover.STATUS_NONE == approver.getStatusId()) {
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

		doc.setHrDocApprovers(newList);

		if (!hasApps) {
			if (doc.getTypeId() == HrDoc.TYPE_DISMISS && doc.getStatusId() == HrDoc.STATUS_ON_CREATE) {

			} else if (doc.getTypeId() == HrDoc.TYPE_BYPASS_SHEET) {
				if (doc.getStatusId() == HrDoc.STATUS_ON_CREATE) {
					mdService.addMd(doc, userData, userData.getUserid(), MyDocs.STATUS_CREATE);
				} else if (doc.getStatusId() == HrDoc.STATUS_ON_APPROVEMENT) {
					if (userData.getUserid().equals(doc.getCreatedBy())) {
						mdService.addMd(doc, userData, userData.getUserid(), MyDocs.STATUS_CREATE);
						doc.setStatusId(HrDoc.STATUS_ON_CREATE);
					} else {
						mdService.addMd(doc, userData, userData.getUserid(), MyDocs.STATUS_IN);
						doc.setStatusId(HrDoc.STATUS_ON_EXECUTION);
					}

				}
			} else {
				mdService.addMd(doc, userData, doc.getResponsibleId(), MyDocs.STATUS_IN);
				doc.setStatusId(HrDoc.STATUS_ON_EXECUTION);
			}
		}

		mdService.removeMd(doc, apForRemove.getUserId());
		hrDocDao.update(doc);
	}

	@Override
	public void dismissEmployeesAndCloseDoc(HrDoc doc, User userData) throws DAOException {
		if (doc.getTypeId() != HrDoc.TYPE_DISMISS) {
			throw new DAOException("Не поддерживается");
		}

		List<Staff> removedStaffs = new ArrayList<>();

		for (HrDocItem item : doc.getHrDocItems()) {
			Salary sal = salDao.find(item.getSalaryId());
			if (sal == null) {
				throw new DAOException("Должность не найдена!");
			}

			sal.setEnd_date(item.getEndDate());
			salService.removeSalary(sal, userData.getUserid());

			/**
			 * Если у сотрудника больше нет должностей, то отмечаем его как В
			 * ПРОЦЕССЕ УВОЛЬНЕНИЯ
			 */
			List<Salary> otherSalList = salDao
					.findAllCurrent(" staff_id = " + sal.getStaff_id() + " AND salary_id != " + sal.getSalary_id());
			if (Validation.isEmptyCollection(otherSalList)) {
				removedStaffs.add(staffDao.find(sal.getStaff_id()));
			}
		}

		for (Staff stf : removedStaffs) {
			stf.setOnDismiss(1);
			staffDao.update(stf);
		}

		doc.setStatusId(HrDoc.STATUS_CLOSED);
		hrDocDao.update(doc);
		mdService.addMd(doc, userData, userData.getUserid(), MyDocs.STATUS_CLOSED);
		mdService.addMd(doc, userData, doc.getCreatedBy(), MyDocs.STATUS_CLOSED);
	}

	@Override
	public void cancelDocument(HrDoc doc, User userData) throws DAOException {
		if (Validation.isEmptyLong(doc.getResponsibleId())) {
			throw new DAOException("Нет доступа");
		}

		if (!userData.isSys_admin() && !doc.getResponsibleId().equals(userData.getUserid())
				&& !userData.getUserid().equals(doc.getCreatedBy())) {
			throw new DAOException("Нет доступа");
		}

		doc.setStatusId(HrDoc.STATUS_CANCELLED);
		doc.setUpdatedBy(userData.getUserid());
		doc.setUpdatedAt(Calendar.getInstance().getTime());
		hrDocDao.update(doc);

		mdService.removeAllFromMd(doc);

		// LOG
		HrDocActionLog log = new HrDocActionLog(HrDocActionLog.ACTION_CANCEL, null, doc.getId(), userData.getUserid());
		logService.create(log, userData.getUserid());
	}

	@Override
	public void completeDocument(HrDoc doc, User userData) throws DAOException {
		if (HrDoc.STATUS_CLOSED == doc.getStatusId()) {
			throw new DAOException("Документ уже закрыт");
		}
		if (HrDoc.TYPE_CHANGE_SALARY == doc.getTypeId()) {
			for (HrDocItem item : doc.getHrDocItems()) {
				Salary sal = salDao.find(item.getSalaryId());
				if (sal != null) {
					Calendar cal = Calendar.getInstance();
					Salary newSal = new Salary();
					BeanUtils.copyProperties(sal, newSal);
					newSal.setAmount(item.getAmount());
					newSal.setBeg_date(item.getBeginDate());
					newSal.setSalary_id(null);
					newSal.setWaers(item.getCurrency());

					cal.setTime(item.getBeginDate());
					cal.add(Calendar.DAY_OF_YEAR, -1);
					sal.setEnd_date(cal.getTime());

					salDao.update(sal);
					salDao.create(newSal);
				}
			}
		} else if (HrDoc.TYPE_BYPASS_SHEET == doc.getTypeId()) {
			for (HrDocItem item : doc.getHrDocItems()) {
				Salary sal = salDao.find(item.getSalaryId());
				if (sal != null) {
					Staff stf = staffDao.find(sal.getStaff_id());
					if (stf != null) {
						stf.setOnDismiss(0);
						staffDao.update(stf);
					}
				}
			}
		}

		doc.setStatusId(HrDoc.STATUS_CLOSED);
		doc.setUpdatedAt(Calendar.getInstance().getTime());
		doc.setUpdatedBy(userData.getUserid());
		hrDocDao.update(doc);
		mdService.addMd(doc, userData, userData.getUserid(), MyDocs.STATUS_CLOSED);
		mdService.addMd(doc, userData, doc.getCreatedBy(), MyDocs.STATUS_CLOSED);

		// LOG
		HrDocActionLog log = new HrDocActionLog(HrDocActionLog.ACTION_COMPLETE_DOC, null, doc.getId(),
				userData.getUserid());
		logService.create(log, userData.getUserid());
	}

	private void sendNotify(HrDoc doc, Long fromId, Long toUserId, String userFio) {
		UserStaff us = new UserStaff();
		// us.setUserFio(userFio);
		us.setUsername(userFio);
		us.setUser_id(toUserId);

		List<UserStaff> usList = new ArrayList<>();
		usList.add(us);

		MessageHeader mh = new MessageHeader();
		mh.setMess_date(Calendar.getInstance().getTime());
		mh.setMess_from(99L);
		mh.setMess_head_text(String.format(NOTIFY_HEADER_TXT, doc.getDocTypeName()));
		mh.setMess_text(String.format(NOTIFY_BODY_TXT,
				FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath(), doc.getId(),
				doc.getDocTypeName()));

		messageService.sendMessage(usList, mh, new ArrayList<>(), fromId);
	}
}