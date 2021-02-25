package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.MatnrBukrsDao;
import general.tables.Matnr;
import general.tables.MatnrBukrs;

@Component("matnrBukrsDao")
public class MatnrBukrsDaoImpl extends GenericDaoImpl<MatnrBukrs> implements MatnrBukrsDao {
	
	@Override
	public List<MatnrBukrs> findAllByBukrs(String in_bukrs) {
		
		String qq = "Select m from MatnrBukrs m "
				+ " Where m.bukrs = '" + in_bukrs + "'";
		Query q = this.em.createQuery(qq);
		
		List<MatnrBukrs> ml = q.getResultList();   
		return ml;
	}
	
}
