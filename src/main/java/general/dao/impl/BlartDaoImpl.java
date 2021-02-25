package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.BlartDao;
import general.tables.Blart;

@Component("blartDao")
public class BlartDaoImpl extends GenericDaoImpl<Blart> implements BlartDao {
	
	public Blart getByBlart(String blart) {
		Query query = this.em
				.createQuery("select b FROM Blart b where b.blart= :blart");
		query.setParameter("blart", blart);
		Blart r_blart = (Blart) query.getSingleResult();
		return r_blart;
	}
	public List<Blart> findAll() {
		Query query = this.em
				.createQuery("select b FROM Blart b");
		List<Blart> l_blart =  query.getResultList();
		return l_blart;
	}
}
