package general.services;

import java.util.List;

import general.dao.DAOException;
import general.tables.Promotion;
import general.tables.Role;
import general.tables.Role_action;

import org.springframework.transaction.annotation.Transactional;

public interface PromotionService {
	@Transactional
	Promotion createPromo(Promotion p) throws DAOException;
	
	@Transactional
	void updatePromo(Promotion p) throws DAOException;
	
	@Transactional
	public List<Promotion> findAllByBranch(Long a_branchID) throws DAOException;
	
}
