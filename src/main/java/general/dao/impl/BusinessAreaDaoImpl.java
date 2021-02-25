package general.dao.impl;

import general.dao.BusinessAreaDao;
import general.tables.BusinessArea;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;
@Component("businessAreaDao")
public class BusinessAreaDaoImpl  extends GenericDaoImpl<BusinessArea> implements BusinessAreaDao{

		public List<BusinessArea> findAll() {
			Query query = this.em
					.createQuery("SELECT b FROM BusinessArea b"); 
			List<BusinessArea> l = query.getResultList();
			return l;
		}
}
