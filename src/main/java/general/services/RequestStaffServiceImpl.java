package general.services;

import java.util.ArrayList;

import general.dao.DAOException;
import general.dao.RequestStaffDao;
import general.tables.RequestStaff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("requestStaffService")
public class RequestStaffServiceImpl implements
		RequestStaffService {

	@Autowired
	RequestStaffDao rsDao;
	
	@Override
	public void create(RequestStaff rs) throws DAOException {
		// TODO Auto-generated method stub
		rsDao.create(rs);
	}

	@Override
	public void create(ArrayList<RequestStaff> rsList) throws DAOException {
		for(RequestStaff rs:rsList){
			rsDao.create(rs);
		}
	}	
}