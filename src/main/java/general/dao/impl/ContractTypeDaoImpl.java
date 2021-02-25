package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.ContractTypeDao;
import general.tables.ContractType;

import org.springframework.stereotype.Component;

@Component("contractTypeDao")
public class ContractTypeDaoImpl extends GenericDaoImpl<ContractType> implements ContractTypeDao {
	
	public List<ContractType> findAll() {
		Query query = this.em
				.createQuery("select ct FROM ContractType ct"); 
		List<ContractType> ct_list = query.getResultList();
		return ct_list;
	}
}
