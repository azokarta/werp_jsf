package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import general.Validation;
import general.dao.DAOException;
import general.dao.MyDocsDao;
import general.dao.RelatedDocsDao;
import general.dao.RequestDao;
import general.dao.RequestStaffDao;
import general.dao.StaffDao;
import general.dao.UserDao;
import general.tables.MyDocs;
import general.tables.ReqEventLog;
import general.tables.Request;
import general.tables.RequestMatnr;
import general.tables.RequestStaff;
import general.tables.Staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.User;

@Service("requestService")
public class RequestServiceImpl implements RequestService {

	@Autowired
	RelatedDocsService rdService;

	@Autowired
	RequestMatnrService rmService;

	@Autowired
	MyDocsService mdService;

	@Autowired
	MyDocsDao mdDao;

	@Autowired
	RequestDao rDao;

	@Autowired
	ReqEventLogService relService;

	@Autowired
	RequestStaffService rsService;

	@Autowired
	RelatedDocsDao rdDao;

	@Autowired
	RequestStaffDao rsDao;

	@Autowired
	StaffDao stfDao;

	@Autowired
	UserDao userDao;

	@Override
	public void create(Request r, List<RequestMatnr> reqMatnrs, User user)
			throws DAOException {
		// TODO Auto-generated method stub
		String error = validateRequest(r, user, true);
		if (error.length() > 0) {
			throw new DAOException(error);
		}
		r.setCurrent_responsible(user.getUserid());
		r.setId(null);
		rDao.create(r);
		for (RequestMatnr rm : reqMatnrs) {
			rm.setRequest_id(r.getId());
		}
		// addRelatedDoc(r.getId(), 0L, r.getId());
		rmService.create(reqMatnrs);
		addEventLog(ReqEventLog.ACTION_CREATE, r.getId(), user);
		addMyDocs(MyDocs.STATUS_CREATE, r.getId(), 0L, user,
				r.getBranch_id(), r.getBukrs());
	}

	private void addEventLog(Integer actionId, Long requestId, User userData) {
		ReqEventLog rel = new ReqEventLog();
		rel.setAction_id(actionId);
		rel.setDatetime(Calendar.getInstance().getTime());
		rel.setRequest_id(requestId);
		rel.setStaff_id(userData.getUserid());
		relService.create(rel);
	}

	private void addMyDocs(Integer statusId, Long contextId, Long owner,
			User userData, Long branchId, String bukrs) {

		MyDocs md = new MyDocs(Request.CONTEXT, contextId, owner, statusId);
		md.setBranch_id(branchId);
		md.setBukrs(bukrs);
		mdService.create(md, userData);
	}

	@Override
	public void update(Request r, List<RequestMatnr> reqMatnrs, User user)
			throws DAOException {
		// TODO Auto-generated method stub
		String error = validateRequest(r, user, false);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		rDao.update(r);
		for (RequestMatnr rm : reqMatnrs) {
			rm.setRequest_id(r.getId());
		}

		rmService.create(reqMatnrs);
		addEventLog(ReqEventLog.ACTION_UPDATE, r.getId(), user);
	}

	private String validateRequest(Request r, User user, boolean isNew) {
		String error = "";
		if (isNew) {
			r.setCreated_at(Calendar.getInstance().getTime());
			r.setCreated_by(user.getUserid());
			r.setStatus_id(Request.STATUS_1);
		}

		r.setUpdated_at(Calendar.getInstance().getTime());
		r.setUpdated_by(user.getUserid());
		// r.setBranch_id(user.getBranch_id());

		if (Validation.isEmptyString(r.getBukrs())) {
			error += "Выберите компанию" + "\n";
		}

		if (Validation.isEmptyLong(r.getBranch_id())) {
			error += "Выберите филиал" + "\n";
		}

		// System.out.println("BUKRS: " + r.getBukrs());

		if (Validation.isEmptyLong(r.getDepartment_id())) {
			error += "Выберите департамент" + "\n";
		}

		if (Validation.isEmptyLong(r.getCurrent_responsible())) {
			r.setCurrent_responsible(0L);
		}

		return error;
	}

	@Override
	public void nextStep(Request r, List<general.tables.User> userList,
			User userData) throws DAOException {

		// List<general.tables.User> uList = userDao.findAll(" staff_id = " +
		// 274);//Arifjan aka
		// if(uList.size() == 0){
		// throw new DAOException("User Arifjan not found!");
		// }
		// general.tables.User usr = uList.get(0);

		addEventLog(ReqEventLog.ACTION_SEND, r.getId(), userData);

		addMyDocs(MyDocs.STATUS_IN, r.getId(), 0L, userData,r.getRes_branch_id(),r.getBukrs());
		addMyDocs(MyDocs.STATUS_SENT, r.getId(), 0L, userData,r.getBranch_id(),r.getBukrs());
		r.setStatus_id(Request.STATUS_3);
		rDao.update(r);

		// Integer eventAction = ReqEventLog.ACTION_SEND;
		// Long currStfId = r.getCurrent_responsible();
		// if (userList.size() > 0) {
		// r.setCurrent_responsible(userList.get(0).getStaff_id());
		// ArrayList<RequestStaff> rsList = new ArrayList<RequestStaff>();
		// for (general.tables.User u : userList) {
		// RequestStaff rs = new RequestStaff();
		// rs.setRequest_id(r.getId());
		// rs.setStaff_id(u.getUser_id());
		// rs.setStatus_id(r.getStatus_id());
		// rsList.add(rs);
		// }
		//
		// rsService.create(rsList);
		// } else {
		// eventAction = ReqEventLog.ACTION_CONFIRM;
		// List<RequestStaff> rsList = rsDao.findAll(String.format(
		// " request_id = %d AND flag = %d ", r.getId(), 0));
		// if (rsList.size() > 0) {
		// r.setCurrent_responsible(rsList.get(0).getStaff_id());
		// } else {
		// r.setCurrent_responsible(r.getLast_responsible());
		// r.setStatus_id(Request.STATUS_3);
		// }
		//
		// if (r.getCreated_by().equals(currStfId)) {
		// eventAction = ReqEventLog.ACTION_SEND;
		// }
		// }
		//
		// rDao.update(r);
		// RequestStaff currRS = rsDao.find(currStfId);
		// if (currRS != null) {
		// currRS.setFlag(1);
		// rsDao.update(currRS);
		// }
		//
		// addEventLog(eventAction, r.getId(), userData);
		// addMyDocs(MyDocs.STATUS_IN, r.getId(), r.getCurrent_responsible(),
		// userData);
		// addMyDocs(MyDocs.STATUS_SENT, r.getId(), currStfId, userData);
	}

}