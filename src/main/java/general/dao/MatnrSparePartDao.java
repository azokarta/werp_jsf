package general.dao;

import java.util.List;

import general.tables.MatnrSparePart;

public interface MatnrSparePartDao extends GenericDao<MatnrSparePart> {
	List<MatnrSparePart> findAllByTovarID(Long tovID);
	List<MatnrSparePart> findAll(String cond);
}
