package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.Validation;
import general.dao.DAOException;
import general.dao.RelatedDocsDao;
import general.dao.RequestOutDao;
import general.tables.Request;
import general.tables.RequestOut;
import general.tables.RequestOutMatnr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.User;

@Service("requestOutService")
public class RequestOutServiceImpl implements RequestOutService {

	@Autowired
	MyDocsService mdService;

	@Autowired
	RequestOutDao roDao;

	@Autowired
	RelatedDocsDao relDDao;

	@Autowired
	RelatedDocsService rdService;

	@Autowired
	RequestOutMatnrService romService;

	@Override
	public void create(RequestOut r, List<RequestOutMatnr> reqMatnrs,
			List<Request> parentDocs, User user) throws DAOException {
		r.setId(null);
		String error = validate(r, user, true);
		if (reqMatnrs == null || reqMatnrs.size() == 0) {
			error += "Список материалов пуст";
		}
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		roDao.create(r);
		for (RequestOutMatnr rom : reqMatnrs) {
			rom.setRequest_id(r.getId());
		}

		romService.create(reqMatnrs);
		addToParentDocs(r.getId(), parentDocs);
	}

	private void addToParentDocs(Long reqId, List<Request> parentDocs) {
		Map<String, List<Long>> m = new HashMap<String, List<Long>>();
		if(parentDocs != null && parentDocs.size() > 0){
			List<Long> tempLong = new ArrayList<Long>();
			for(Request r:parentDocs){
				tempLong.add(r.getId());
			}
			m.put(Request.CONTEXT, tempLong);
		}
		
		rdService.addChildToParents(reqId, RequestOut.CONTEXT, m);
	}

	private String validate(RequestOut r, User userData, boolean isNew) {
		String error = "";
		if (isNew) {
			r.setCreated_at(Calendar.getInstance().getTime());
			r.setCreated_by(userData.getUserid());
			r.setStatus_id(RequestOut.STATUS_OPENED);
		}
		if (Validation.isEmptyLong(r.getDepartment_id())) {
			error += "Выберите департамент" + "\n";
		}
		
		if(Validation.isEmptyString(r.getBukrs())){
			error += "Выберите компанию" + "\n";
		}

		r.setUpdated_at(Calendar.getInstance().getTime());
		r.setUpdated_by(userData.getUserid());

		return error;
	}

	@Override
	public void update(RequestOut r, List<RequestOutMatnr> reqMatnrs,
			List<Request> parentDocs, User user) throws DAOException {
		String error = validate(r, user, false);
		if (reqMatnrs == null || reqMatnrs.size() == 0) {
			error += "Список материалов пуст";
		}

		if (error.length() > 0) {
			throw new DAOException(error);
		}

		roDao.update(r);
		for (RequestOutMatnr rom : reqMatnrs) {
			rom.setRequest_id(r.getId());
		}

		romService.create(reqMatnrs);
		addToParentDocs(r.getId(), parentDocs);
	}

}