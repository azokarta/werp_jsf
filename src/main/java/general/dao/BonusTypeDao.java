package general.dao;

import java.util.List;

import general.tables.BonusType;

public interface BonusTypeDao extends GenericDao<BonusType>{
	public List<BonusType> findAll();
}
