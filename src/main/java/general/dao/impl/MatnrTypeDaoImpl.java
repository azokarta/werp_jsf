package general.dao.impl;


import java.util.List;

import javax.persistence.Query;
import general.dao.MatnrTypeDao;
import general.tables.MatnrType;

import org.springframework.stereotype.Component;

@Component("matnrTypeDao")
public class MatnrTypeDaoImpl extends GenericDaoImpl<MatnrType> implements MatnrTypeDao{

	@SuppressWarnings("unchecked")
	public List<MatnrType> findAll(String condition) {
		String s = " SELECT mt FROM MatnrType mt ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		Query query = this.em
				.createQuery(s);
		return query.getResultList();
	}
}
