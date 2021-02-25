package general.dao.impl;

import general.dao.ActionTypeDao;
import general.tables.ActionType;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;
@Component("actionTypeDao")
public class ActionTypeDaoImpl extends GenericDaoImpl<ActionType> implements ActionTypeDao{
	@Override
	public List<ActionType> findAll() {
		Query q = this.em.createQuery("Select a FROM ActionType a");
		return q.getResultList();
	}
}
