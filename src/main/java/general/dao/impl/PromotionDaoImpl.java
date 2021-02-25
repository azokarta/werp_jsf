package general.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import f4.BranchF4;
import general.dao.PromotionDao;
import general.tables.Branch;
import general.tables.Promotion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component; 
@Component("promotionDao")
public class PromotionDaoImpl extends GenericDaoImpl<Promotion> implements PromotionDao {

	@Override
	public Promotion findByNumber(Long a_number) {
		Promotion resultPromo = null;
		Query q = this.em.createQuery(String.format("SELECT p FROM Promotion p WHERE p.pm_number = '%d'", a_number));
		List results = q.getResultList();
		if(!results.isEmpty())
		{
			resultPromo = (Promotion)results.get(0);
		}
		
		return resultPromo;
	}
	
	@Override
	public List<Promotion> findAll() {
		Query q = this.em.createQuery(String.format("SELECT p FROM Promotion p"));
		List<Promotion> results = q.getResultList();
		
		return results;
	}
	
	@Override
	public List<Promotion> dynamicFindAll(String wcl) {
		Query q = this.em.createQuery(String.format("SELECT p FROM Promotion p Where " + wcl));
		List<Promotion> results = q.getResultList();
		
		return results;
	}
	
	@Override
	public List<Promotion> findContractPromotions(Long a_contractId) {
		Query q = this.em.createQuery(String.format("SELECT p FROM Promotion p, ContractPromos cp Where p.id=cp.promo_id and cp.contract_id = " + a_contractId));
		List<Promotion> results = q.getResultList();

		return results;
	}
}