package general.dao.impl;


import java.util.List;

import javax.persistence.Query;

import general.dao.CityDao;
import general.dao.ContractPromosDao;
import general.tables.City;
import general.tables.ContractPromos;

import org.springframework.stereotype.Component;

@Component("cpDao")
public class ContractPromosDaoImpl extends GenericDaoImpl<ContractPromos> implements ContractPromosDao{

	public List<ContractPromos> findAll() {
		Query query = this.em
				.createQuery("select c FROM ContractPromos c"); 
		List<ContractPromos> l_cp = query.getResultList();
		return l_cp;
	}
	
	public List<ContractPromos> findAllByContrID(Long contr_id) {
		Query query = this.em
				.createQuery("select c FROM ContractPromos c Where c.contract_id = " + Long.toString(contr_id)); 
		List<ContractPromos> l_cp = query.getResultList();
		return l_cp;
	}
	
	@Override
	public int deleteByContractId(Long a_contractId) {
		Query query = this.em
				.createQuery("delete FROM ContractPromos cp where contract_id = :a_contractId");
		query.setParameter("a_contractId", a_contractId);
		return query.executeUpdate();
	}
	
	public Long findContrID(Long contr_id,Long a_matnr) {
		Query query = this.em
				.createQuery("select cp.id from Promotion p, ContractPromos cp where p.id=cp.promo_id"
							+" and cp.contract_id="+contr_id+" and p.matnr="+a_matnr); 
		return (Long) query.getSingleResult();
	}
	
}
