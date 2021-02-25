package general.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.BsikDao;
import general.tables.Bkpf;
import general.tables.Bsik;
@Component("bsikDao")
public class BsikDaoImpl extends GenericDaoImpl<Bsik> implements BsikDao{
	public List<Bsik> dynamicSearch(String a_dynamicWhere){     	
    	Query query = this.em
                .createQuery("select b FROM Bsik b where "+a_dynamicWhere);    
    	List<Bsik> l_bsik =  query.getResultList();
    	return l_bsik;
    }
	
	public String dynamicSearchSingleHkont(String a_dynamicWhere){     	
	    try{
			Query query = this.em
	                .createQuery("select hkont FROM Bsik b where "+a_dynamicWhere);
	    	
	    	String wa_hkont = (String) query.getSingleResult();  	
	   	 
	    	return wa_hkont;
		}	
		catch (NoResultException nre){
			//Ignore this because as per your logic this is ok!
			return null;
		}
    }
	
}
