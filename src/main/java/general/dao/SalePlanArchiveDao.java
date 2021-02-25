package general.dao;
import java.util.List;

import general.tables.SalePlanArchive;
public interface SalePlanArchiveDao extends GenericDao<SalePlanArchive>{
	public List<SalePlanArchive> dynamicFind(String a_dynamicWhere);
}
