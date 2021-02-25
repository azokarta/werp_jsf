package general.dao;

import java.util.List;

import general.tables.BonusArchive;

public interface BonusArchiveDao extends GenericDao<BonusArchive>{
	public Long countAnyOldOrEqualBonus(int a_monat, int a_gjahr);
	public List<BonusArchive> dynamicFindBonuses(String a_dynamicWhere);
	public BonusArchive dynamicFindBonus(String a_dynamicWhere);
}
