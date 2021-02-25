package general.dao.impl;

import general.dao.ServCRMActionDao;
import general.tables.ServCRMAction;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("servCRMActionDao")
public class ServCRMActionDaoImpl extends GenericDaoImpl<ServCRMAction> implements ServCRMActionDao {
		public List<ServCRMAction> findAll() {
			Query query = this.em
					.createQuery("select a FROM ServCRMAction a"); 
			List<ServCRMAction> la = query.getResultList();
			return la;
		}
		
		@Override
		public List<ServCRMAction> findAll(String condition) {
			String s = " SELECT a FROM ServCRMAction a WHERE " + condition;
			Query query = this.em
					.createQuery(s); 
			List<ServCRMAction> la = query.getResultList();
			return la;
		}
}
