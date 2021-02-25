package general.dao.impl;

import general.dao.BonusTypeDao;
import general.tables.BonusType;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("bonusTypeDao")
public class BonusTypeDaoImpl extends GenericDaoImpl<BonusType> implements BonusTypeDao{
	
	public List<BonusType> findAll(){     	
    	Query query = this.em.createQuery("select b FROM BonusType b");  
    	List<BonusType> l_bon_type =  query.getResultList();
    	return l_bon_type;
    }
}
