package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.PrebkpfDao;
import general.tables.Prebkpf;

@Component("prebkpfDao")
public class PrebkpfDaoImpl extends GenericDaoImpl<Prebkpf> implements PrebkpfDao{
	public int updateDynamicPreBkpf(String a_dynamicSet, String a_dynamicWhere)
	{
		a_dynamicWhere = "update Prebkpf set "+a_dynamicSet+" where "+a_dynamicWhere;
		Query query = this.em.createQuery(a_dynamicWhere);
		return query.executeUpdate();
	}
	
	public List<Prebkpf> dynamicFindPrebkpf(String a_dynamicWhere) {
		Query query = this.em
				.createQuery("select b FROM Prebkpf b where "+a_dynamicWhere); 
		List<Prebkpf> l_prebkpf = query.getResultList();
		return l_prebkpf;
	} 
}
