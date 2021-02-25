package general.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.BonusArchiveDao;
import general.tables.Bkpf;
import general.tables.Bonus;
import general.tables.BonusArchive;

@Component("bonusArchiveDao")
public class BonusArchiveDaoImpl extends GenericDaoImpl<BonusArchive> implements BonusArchiveDao{
	public Long countAnyOldOrEqualBonus(int a_monat, int a_gjahr){
		if (a_monat == 1)
		{
			a_gjahr = a_gjahr - 1;
		}
		Query query = this.em
                .createQuery("select count(ba.bonus_id) FROM BonusArchive ba where ba.month = :monat and year = :gjahr");
        query.setParameter("monat", a_monat);   
        query.setParameter("gjahr", a_gjahr);   
        query.setFirstResult(0);
        query.setMaxResults(1);
        Long count = (Long) query.getSingleResult();        
        return count;
	}
	
	public List<BonusArchive> dynamicFindBonuses(String a_dynamicWhere){ 
    	
    	Query query = this.em
                .createQuery("select b FROM BonusArchive b where "+a_dynamicWhere); 
    	List<BonusArchive> bon =  query.getResultList();
    	return bon;
    }
	
	public BonusArchive dynamicFindBonus(String a_dynamicWhere){ 
    	
    	
    	try{ 
    		String select = "select b FROM BonusArchive b where "+a_dynamicWhere;
        	Query query = this.em
                    .createQuery(select); 
        	BonusArchive bon = (BonusArchive) query.getSingleResult();
        	return bon;
		}	
		catch (NoResultException nre){
			//Ignore this because as per your logic this is ok!
			return null;
			}
    }
}
