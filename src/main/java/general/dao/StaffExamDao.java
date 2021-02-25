package general.dao;

import java.util.List;

import general.tables.StaffExam;

public interface StaffExamDao extends GenericDao<StaffExam> {
	public List<StaffExam> findAll(String condition);
}
