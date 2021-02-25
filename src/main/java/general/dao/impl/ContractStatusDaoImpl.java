package general.dao.impl;

import general.dao.ContractStatusDao;
import general.tables.ContractStatus;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;
@Component("contractStatusDao")
public class ContractStatusDaoImpl extends GenericDaoImpl<ContractStatus> implements ContractStatusDao{
	@Override
	public List<ContractStatus> findAll() {
		Query q = this.em.createQuery("Select c FROM ContractStatus c");
		return q.getResultList();
	}
	
	@Override
	public List<ContractStatus> findAllActive() {
		Query q = this.em.createQuery("Select c FROM ContractStatus c where c.isActive = 1");
		return q.getResultList();
	}
}