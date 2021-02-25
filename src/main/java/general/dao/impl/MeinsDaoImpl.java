package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.DAOException;
import general.dao.MeinsDao;
import general.tables.Meins;

import org.springframework.stereotype.Component;

@Component("meinsDao")
public class MeinsDaoImpl extends GenericDaoImpl<Meins> implements MeinsDao {
	public List<Meins> findAll() {
		Query query = this.em
				.createQuery("select m FROM Meins m");
		List<Meins> l_meins =  query.getResultList();
		return l_meins;
	}
	public int updateDynamicSingle(Long meins)
	{
		String a_dynamicWhere = "";
		a_dynamicWhere = "update Meins set spras = 'ru' where meins = 4";//+ meins;
		//System.out.println(a_dynamicWhere);
		Query query = this.em.createQuery(a_dynamicWhere);
		return query.executeUpdate();	
		//return query.executeUpdate();
			
	}
}
