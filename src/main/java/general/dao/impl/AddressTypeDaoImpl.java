package general.dao.impl;

import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Component;
import general.dao.AddressTypeDao;
import general.tables.AddressType;

@Component("addressTypeDao")
public class AddressTypeDaoImpl extends GenericDaoImpl<AddressType> implements AddressTypeDao {
		public List<AddressType> findAll() {
			Query query = this.em
					.createQuery("SELECT c FROM AddressType c"); 
			List<AddressType> la = query.getResultList();
			return la;
		}
		
		@Override
		public List<AddressType> findAll(String condition) {
			String s = " SELECT c FROM AddressType c WHERE " + condition;
			Query query = this.em
					.createQuery(s); 
			List<AddressType> la = query.getResultList();
			return la;
		}		

}
