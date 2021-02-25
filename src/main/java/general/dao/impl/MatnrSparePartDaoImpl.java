package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.MatnrSparePartDao;
import general.tables.MatnrSparePart;

import org.springframework.stereotype.Component;
@Component("matnrSparePartDao")
public class MatnrSparePartDaoImpl extends GenericDaoImpl<MatnrSparePart> implements MatnrSparePartDao{

	@Override
	public List<MatnrSparePart> findAll(String cond) {
		String s = " SELECT s FROM MatnrSparePart s ";
		if(cond.length() > 0){
			s += " WHERE " + cond;
		}
		Query query = this.em.createQuery(s); 
		return query.getResultList();
	}
	
	@Override
	public List<MatnrSparePart> findAllByTovarID(Long tovID) {
		String s = " SELECT s FROM MatnrSparePart s WHERE s.tovar_id = " + tovID;
		Query query = this.em.createQuery(s); 
		return query.getResultList();
	}	
}
