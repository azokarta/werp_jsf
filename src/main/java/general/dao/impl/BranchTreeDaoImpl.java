package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.BranchTreeDao;
import general.tables.BranchTree;

@Component("branchTreeDao")
public class BranchTreeDaoImpl extends GenericDaoImpl<BranchTree> implements BranchTreeDao{
public List<BranchTree> findAll(){ 
    	
    	Query query = this.em
                .createQuery("select b FROM BranchTree b"); 
    	//query.setMaxResults(20);  
    	List<BranchTree> brn =  query.getResultList();
    	return brn;
    }
	
	public List<BranchTree> findByBukrs(String a_bukrs){ 
    	
    	Query query = this.em
                .createQuery("select b FROM BranchTree b where b.bukrs= :bukrs"); 
    	//query.setMaxResults(20);  
    	query.setParameter("bukrs", a_bukrs);   
    	List<BranchTree> brn =  query.getResultList();
    	return brn;    	
    }
	
	public BranchTree findServBranchByBranch(Long a_branchId) { 
		String q = "select b FROM Branch b where "
				+ " b.parent_branch_id = some ("
				+ " SELECT bb.parent_branch_id FROM BranchTree bb WHERE "
				+ " bb.branch_id = " + a_branchId
				+ " ) and b.business_area_id = 5";
		Query query = this.em.createQuery(q); 
    	//query.setMaxResults(20);  
    	//query.setParameter("a_branchId", a_branchId);
    	List<BranchTree> brn =  query.getResultList();
    	if (brn.size() > 0) 
    		return brn.get(0);
    	else 
    		return null;
    }
}
