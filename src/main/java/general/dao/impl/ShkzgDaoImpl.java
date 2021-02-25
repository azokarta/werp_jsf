package general.dao.impl;

import general.dao.ShkzgDao;
import general.tables.Shkzg;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;
@Component("shkzgDao")
public class ShkzgDaoImpl extends GenericDaoImpl<Shkzg> implements ShkzgDao{
	@Override
	public List<Shkzg> findAll() {
		Query q = this.em.createQuery("Select s FROM Shkzg s");
		return q.getResultList();
	}
}