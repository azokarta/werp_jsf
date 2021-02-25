package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.PrebsegDao;
import general.tables.Prebseg;

@Component("prebsegDao")
public class PrebsegDaoImpl extends GenericDaoImpl<Prebseg> implements PrebsegDao{
	public int updateDynamicPrebseg(String a_dynamicSet, String a_dynamicWhere)
	{
		a_dynamicWhere = "update Prebseg set "+a_dynamicSet+" where "+a_dynamicWhere;
		Query query = this.em.createQuery(a_dynamicWhere);
		return query.executeUpdate();
	}
	
	public List<Prebseg> dynamicFindPrebseg(String a_dynamicWhere) {
		Query query = this.em
				.createQuery("select b FROM Prebseg b where "+a_dynamicWhere); 
		List<Prebseg> l_prebkpf = query.getResultList();
		return l_prebkpf;
	}
}
