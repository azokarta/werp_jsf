package general.dao.impl;
 

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.tables.SubCompany;
import general.dao.SubCompanyDao;

@Component("subCompanyDao")
public class SubCompanyDaoImpl extends GenericDaoImpl<SubCompany> implements SubCompanyDao {
	
	public List<SubCompany> findAll(){
    	Query query = this.em.createQuery("SELECT sc FROM SubCompany sc");
    	List<SubCompany> l =  query.getResultList();
    	return l;
    }
	
	@Override
	public List<SubCompany> findAll(String c){
		String q = "SELECT sc FROM SubCompany sc ";
		if(c.length() > 0){
			q += " WHERE " + c;
		}
    	Query query = this.em.createQuery(q);
    	List<SubCompany> l =  query.getResultList();
    	return l;
    }
}