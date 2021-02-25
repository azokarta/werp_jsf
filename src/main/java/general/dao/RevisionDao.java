package general.dao;

import java.util.List;

import general.tables.Revision;

public interface RevisionDao extends GenericDao<Revision>{
	
	public List<Revision> findAll();
	
	public Revision findWithDetail(Long id);
}
