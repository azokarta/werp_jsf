package general.dao;

import general.tables.ServCRMSchedule;

import java.util.List;

public interface ServCRMScheduleDao extends GenericDao<ServCRMSchedule>{
	public List<ServCRMSchedule> findAll();
	public List<ServCRMSchedule> findAll(String condition);
}
