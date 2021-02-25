package general.dao.impl;
 
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.Validation;
import general.dao.PositionDao; 
import general.tables.Position;

@Component("positionDao")
public class PositionDaoImpl extends GenericDaoImpl<Position> implements PositionDao{
	@SuppressWarnings("unchecked")
	public List<Position> findAll()
	{ 
		Query query = this.em
                .createQuery("SELECT p FROM Position p order by p.text"); 
		List<Position> pos = query.getResultList();
    	return pos;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Position> findAll(String cond) {
		String s = "SELECT p FROM Position p ";
		if(!Validation.isEmptyString(cond)){
			s += " WHERE " + cond;
		}
		Query query = this.em
                .createQuery(s); 
		return query.getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<Position> findAllOrdered(String a_lang)
	{ 
		String orderBy = "p.text";
		if (a_lang==null || a_lang.equals("ru")) orderBy = "p.text";
		else if (a_lang==null || a_lang.equals("tr")) orderBy = "p.text_tr";
		else if (a_lang==null || a_lang.equals("en")) orderBy = "p.text_en";
		Query query = this.em
                .createQuery("SELECT p FROM Position p order by "+orderBy); 
		List<Position> pos = query.getResultList();
    	return pos;
	}
}
