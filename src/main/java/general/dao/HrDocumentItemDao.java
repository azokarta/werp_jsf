package general.dao;

import java.util.List;

import general.tables.HrDocumentItem;

public interface HrDocumentItemDao extends GenericDao<HrDocumentItem>{
	public List<HrDocumentItem> findAll(String cond);
	
}
