package general.dao.impl;

import general.dao.ContractOperDao;
import general.tables.ContractOper;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("contractOperDao")
public class ContractOperDaoImpl extends GenericDaoImpl<ContractOper> implements
		ContractOperDao {

	@Override
	public List<ContractOper> findAll() {
		Query query = this.em.createQuery("select co FROM ContractOper co");
		List<ContractOper> l_co = query.getResultList();
		return l_co;
	}

}
