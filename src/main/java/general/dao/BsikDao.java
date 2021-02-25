package general.dao;

import java.util.List;

import general.tables.Bsik;

public interface BsikDao extends GenericDao<Bsik>{
	public List<Bsik> dynamicSearch(String a_dynamicWhere); 
	public String dynamicSearchSingleHkont(String a_dynamicWhere);
}
