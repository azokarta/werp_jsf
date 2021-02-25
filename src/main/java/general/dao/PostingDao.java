package general.dao;

import java.util.List;

import general.tables.Posting;

public interface PostingDao extends GenericDao<Posting>{
	public List<Posting> findAll(String condition);
}
