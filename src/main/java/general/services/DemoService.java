package general.services;

import java.util.List;

import general.dao.DAOException;
import general.tables.Demonstration;

import org.springframework.transaction.annotation.Transactional;


public interface DemoService {
	@Transactional
	void create(List<Demonstration> demoList) throws DAOException;
	
	@Transactional
	void update(Demonstration demo,Long userId) throws DAOException;
	
	@Transactional
	void create(Demonstration demo,Long userId) throws DAOException;
}
