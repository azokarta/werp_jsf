package general.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.Validation;
import general.dao.DAOException;
import general.dao.RequestMatnrDao;
import general.tables.RequestMatnr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("requestMatnrService")
public class RequestMatnrServiceImpl implements RequestMatnrService {

	@Autowired
	RequestMatnrDao rmDao;

	@Override
	public void create(RequestMatnr rm) throws DAOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void create(List<RequestMatnr> rmList) throws DAOException {
		String error = validate(rmList);
		if (rmList.size() == 0) {
			error += "Список материалов пуст " + "\n";
		}
		if (error.length() > 0) {
			throw new DAOException(error);
		}
		//System.out.println("SIZE: " + rmList.size());
		Map<Long, RequestMatnr> rmMap = new HashMap<Long, RequestMatnr>();
		Long reqId = rmList.get(0).getRequest_id();
		System.out.println("REQ ID: " + reqId);
		List<RequestMatnr> oldRmList = rmDao.findAll(" request_id = " + reqId);
		//System.out.println("SIZE OLD: " + oldRmList.size());
		for (RequestMatnr rm : rmList) {
			if(!Validation.isEmptyLong(rm.getId())){
				rmDao.update(rm);
				rmMap.put(rm.getId(), rm);
				continue;
			}
			boolean isExisted = false;
			if (oldRmList != null) {
				for (int i = 0; i < oldRmList.size(); i++) {
					RequestMatnr oldRm = oldRmList.get(i);
					if (oldRm.getMatnr().equals(rm.getMatnr())) {
						rm.setId(oldRm.getId());
						rmDao.update(rm);
						isExisted = true;
						rmMap.put(rm.getId(), rm);
						break;
					}
				}
			}

			if (!isExisted) {
				rmDao.create(rm);
				rmMap.put(rm.getId(), rm);
			}
		}
		
		if (oldRmList != null) {
			for(RequestMatnr rm:oldRmList){
				if(!rmMap.containsKey(rm.getId())){
					rmDao.delete(rm.getId());
				}
			}
		}
	}

	private String validate(List<RequestMatnr> rmList) {
		String error = "";

		for (RequestMatnr rm : rmList) {
			if(!Validation.isEmptyString(rm.getMatnr_str())){
				rm.getMatnr_str().trim();
			}
			
			if (Validation.isEmptyLong(rm.getMatnr())
					&& Validation.isEmptyString(rm.getMatnr_str())) {
				error += "Выберите материал" + "\n";
			}
			
			if(Validation.isEmptyLong(rm.getMatnr()) && !Validation.isEmptyString(rm.getMatnr_str())){
				rm.setMatnr(0L);
			}

			if (Validation.isEmptyLong(rm.getUnits())) {
				error += "Выберите ед. измерение" + "\n";
			}

			if (Validation.isEmptyLong(rm.getRequest_id())) {
				error += "Выберите заявку" + "\n";
			}

			if (new Double(rm.getQuantity()).intValue() == 0) {
				error += "Количество должно быть больше нуля" + "\n";
			}

			if (error.length() > 0) {
				break;
			}
		}

		return error;
	}
}