package general.dao.impl;

import general.Validation;
import general.dao.AccountMatnrStateDao;
import general.tables.AccountMatnrState;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;
@Component("accountMatnrStateDao")
public class AccountMatnrStateDaoImpl extends GenericDaoImpl<AccountMatnrState> implements AccountMatnrStateDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<AccountMatnrState> findAll(String condition) {
		String s = " SELECT a FROM AccountMatnrState a ";
		if(!Validation.isEmptyString(condition)){
			s += " WHERE " + condition;
		}
		
		Query q = em.createQuery(s);
		return q.getResultList();
	}
	
}
