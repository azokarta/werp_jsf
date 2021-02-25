package general.services;

import general.dao.DAOException;
import general.tables.Bonus;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

public interface BonusService {
	@Transactional
	List<Bonus> dynamicSearch (Bonus a_bonus) throws DAOException;
	
	@Transactional
	public void createOrUpdateBonuses (List<Bonus> pl_bonus) throws DAOException;
	
	@Transactional
	public void createBonus(Bonus b) throws DAOException;
	
	@Transactional
	public void updateBonus(Bonus b) throws DAOException;
}
