package general.dao;

import java.util.List;

import general.tables.RequestStaff;

public interface RequestStaffDao extends GenericDao<RequestStaff> {
	public List<RequestStaff> findAll(String condition);
}
