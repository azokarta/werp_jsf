package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.BranchDao;
import general.tables.Branch;

import org.springframework.stereotype.Component;
@Component("branchDao")
public class BranchDaoImpl extends GenericDaoImpl<Branch> implements BranchDao{
	public List<Branch> findAll(){ 
    	
    	Query query = this.em
                .createQuery("select b FROM Branch b order by text45"); 
    	//query.setMaxResults(20);  
    	List<Branch> brn =  query.getResultList();
    	return brn;
    }
	
	public List<Branch> findByBukrs(String a_bukrs){ 
    	
    	Query query = this.em
                .createQuery("select b FROM Branch b where b.bukrs= :bukrs order by text45"); 
    	//query.setMaxResults(20);  
    	query.setParameter("bukrs", a_bukrs);   
    	List<Branch> brn =  query.getResultList();
    	return brn;    	
    }
	
	public Branch findServBranchByBranch(Long a_branchId) { 
		String q = "select b FROM Branch b where "
				+ " b.parent_branch_id = some ("
				+ " SELECT bb.parent_branch_id FROM Branch bb WHERE "
				+ " bb.branch_id = " + a_branchId
				+ " ) and b.business_area_id = 5";
		Query query = this.em.createQuery(q); 
    	//query.setMaxResults(20);  
    	//query.setParameter("a_branchId", a_branchId);
    	List<Branch> brn =  query.getResultList();
    	if (brn.size() > 0) 
    		return brn.get(0);
    	else 
    		return null;
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<Branch> findChilds(Long branchId) {
		String s = " SELECT * FROM branch START WITH branch_id = " + branchId + " CONNECT BY PRIOR branch_id=parent_branch_id ";
		Query q = em.createNativeQuery(s,Branch.class);
		List<Branch> out = q.getResultList();
		//System.out.println("SIZE: " + out.size());
		return out;
	}
	


	@SuppressWarnings("unchecked")
	@Override
	public List<Branch> findUserBranchesDmsc01(String a_bukrs, Long a_userId) {
		String s = " select * from branch b where "+
					" b.branch_id in (select us.branch_id from user_branch us where us.user_id="+a_userId+" and us.bukrs='"+a_bukrs+"')"+
					" and b.tovar_category in (1,2) order by b.text45 ";
		Query q = em.createNativeQuery(s,Branch.class);
		List<Branch> out = q.getResultList();
		//System.out.println("SIZE: " + out.size());
		return out;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Branch> findUserBranchesDmsc01Admin(String a_bukrs, Long a_userId) {
		String s = " select * from branch b where "+
					" b.bukrs='"+a_bukrs+"'"+
					" and b.tovar_category in (1,2) order by b.text45 ";
		Query q = em.createNativeQuery(s,Branch.class);
		List<Branch> out = q.getResultList();
		//System.out.println("SIZE: " + out.size());
		return out;
	}
	
	
	@Override
	public List<Branch> findUserBranchesDmsc01Service(String a_bukrs, Long a_userId) {
		String s = " select * from branch b where "+
					" b.branch_id in (select us.branch_id from user_branch us where us.user_id="+a_userId+" and us.bukrs='"+a_bukrs+"')"+
					" and b.business_area_id in (5,6,7,9) order by b.text45 ";
		Query q = em.createNativeQuery(s,Branch.class);
		List<Branch> out = q.getResultList();
		//System.out.println("SIZE: " + out.size());
		return out;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Branch> findUserBranchesDmsc01ServiceAdmin(String a_bukrs, Long a_userId) {
		String s = " select * from branch b where "+
					" b.bukrs='"+a_bukrs+"'"+
					" and b.business_area_id in (5,6,7,9) order by b.text45 ";
		Query q = em.createNativeQuery(s,Branch.class);
		List<Branch> out = q.getResultList();
		//System.out.println("SIZE: " + out.size());
		return out;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Branch> findUserBranchesOffices(String a_bukrs, Long a_userId) {
		String s = " select * from branch b where "+
					" b.branch_id in (select us.branch_id from user_branch us where us.user_id="+a_userId+" and us.bukrs='"+a_bukrs+"')"+
					" and b.type = 3 order by b.text45 ";
		Query q = em.createNativeQuery(s,Branch.class);
		List<Branch> out = q.getResultList();
		//System.out.println("SIZE: " + out.size());
		return out;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Branch> findUserBranchesOfficesAdmin(String a_bukrs, Long a_userId) {
		String s = " select * from branch b where "+
					" b.bukrs='"+a_bukrs+"'"+
					" and b.type = 3 order by b.text45 ";
		Query q = em.createNativeQuery(s,Branch.class);
		List<Branch> out = q.getResultList();
		//System.out.println("SIZE: " + out.size());
		return out;
	}
	
}
