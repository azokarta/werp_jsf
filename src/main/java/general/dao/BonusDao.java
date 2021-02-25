package general.dao;

import java.util.List;

import general.tables.Bonus;

public interface BonusDao extends GenericDao<Bonus>{
	public List<Bonus> dynamicFindBonuses(String a_dynamicWhere);
	public List<Bonus> findAll();
	public Bonus dynamicFindBonus(String a_dynamicWhere);
}
