package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.WerksDao;
import general.tables.Werks;

import org.springframework.stereotype.Component;

@Component("werksDao")
public class WerksDaoImpl extends GenericDaoImpl<Werks> implements WerksDao {
	public List<Werks> findAll() {
		Query query = this.em
				.createQuery("select w FROM Werks w");
		List<Werks> l_werks =  query.getResultList();
		return l_werks;
	}

	@Override
	public List<Werks> findAll(String cond) {
		Query query = this.em
				.createQuery("select w FROM Werks w WHERE " + cond);
		List<Werks> l_werks =  query.getResultList();
		return l_werks;
	}
}
