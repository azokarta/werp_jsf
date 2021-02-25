package general.services; 
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.tables.StaffExam;

public interface StaffExamService{
	
	@Transactional 
	void create( List<StaffExam> seList) throws DAOException;
}