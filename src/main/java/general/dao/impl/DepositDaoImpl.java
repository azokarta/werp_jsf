package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.DepositDao;
import general.tables.Deposit;

import org.springframework.stereotype.Component;

@Component("depositDao")
public class DepositDaoImpl extends GenericDaoImpl<Deposit> implements DepositDao{

	public List<Deposit> findAll() {
		Query query = this.em
				.createQuery("select d FROM Deposit d");
		List<Deposit> l_deposit =  query.getResultList();
		return l_deposit;
	}
	public List<Deposit> dynamicFind(String a_dynamicWhere){ 
    	
    	Query query = this.em
                .createQuery("select b FROM Deposit b where "+a_dynamicWhere); 
    	List<Deposit> dep =  query.getResultList();
    	return dep;
    }
	public Deposit dynamicFindSingle(String a_dynamicWhere){
    	String select = "select b FROM Deposit b where "+a_dynamicWhere;
    	Query query = this.em.createQuery(select);
    	List results = query.getResultList();
    	if (!results.isEmpty())
    		return (Deposit) results.get(0);
    	else
    		return null;
    }
	
	public Long countDynamicSearch(String a_dynamicWhere){
    	Query query = this.em
                .createQuery("select count(d.deposit_id) FROM Deposit d where " + a_dynamicWhere); 
        Long count = (Long) query.getSingleResult();        
        return count;
    }
	public int updateDynamicSingleDeposit(Long a_staff_id, String a_waers, String a_dynamicWhere)
	{
		a_dynamicWhere = "update Deposit set "+a_dynamicWhere+" where staff_id ="+a_staff_id+" and waers ="+a_waers;
		System.out.println(a_dynamicWhere);
		Query query = this.em.createQuery(a_dynamicWhere);
		return query.executeUpdate();
	}
}
