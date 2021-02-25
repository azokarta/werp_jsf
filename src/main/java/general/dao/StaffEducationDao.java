package general.dao;

import java.util.List;

import general.tables.StaffEducation;

public interface StaffEducationDao extends GenericDao<StaffEducation>{
	public List<StaffEducation> findAll(String cond);
}
