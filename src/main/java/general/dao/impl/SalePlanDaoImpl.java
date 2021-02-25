package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.SalePlanDao;
import general.tables.SalePlan;

@Component("salePlanDao")
public class SalePlanDaoImpl extends GenericDaoImpl<SalePlan> implements SalePlanDao {

	@Override
	public SalePlan findCurrentSP(Long branchId) {
		Query q = this.em.createQuery("SELECT sp FROM SalePlan sp WHERE is_current = 1 AND branch_id = :b");
		q.setParameter("b", branchId);
		List<SalePlan> l = q.getResultList();
		if(l.size() > 0){
			return l.get(0);
		}
		return null;
	}

	@Override
	public List<SalePlan> findAllByBranchId(Long branchId) {
		Query q = this.em.createQuery("SELECT sp FROM SalePlan sp WHERE branch_id = :b ORDER BY sp_id DESC");
		q.setParameter("b", branchId);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SalePlan> findAll(String condition) {
		String s = "SELECT sp FROM SalePlan sp ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		return q.getResultList();
	}
	
	@Override
	public List<SalePlan> findAllByBukrs(String a_bukrs) {
		Query q = this.em.createQuery("SELECT sp FROM SalePlan sp WHERE bukrs = :bukrs");
		q.setParameter("bukrs", a_bukrs);
		return q.getResultList();
	}
	
	@Override
	public SalePlan findByBranchAndMonthYear(Long branchId, int inMonth, int inYear, Long inBa, byte inType) {
		String query = "SELECT sp FROM SalePlan sp "
				+ " WHERE branch_id = :b "
				+ " and year = :inYear "
				+ " and month = :inMonth "
				+ " and business_area_id = :inBa"
				+ " and plan_type = :inType"
				+ " ORDER BY sp_id DESC";
		Query q = this.em.createQuery(query);
		q.setParameter("b", branchId);
		q.setParameter("inYear", inYear);
		q.setParameter("inMonth", inMonth);
		q.setParameter("inBa", inBa);
		q.setParameter("inType", inType);
		List<SalePlan> spL =  q.getResultList();
		if (spL.size() > 0) return spL.get(0);
		return null;
	}
}
