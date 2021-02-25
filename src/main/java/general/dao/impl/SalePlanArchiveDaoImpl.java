package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.SalePlanArchiveDao;
import general.tables.BonusArchive;
import general.tables.SalePlanArchive;

import org.springframework.stereotype.Component;
@Component("salePlanArcDao")
public class SalePlanArchiveDaoImpl extends GenericDaoImpl<SalePlanArchive> implements SalePlanArchiveDao{

	public List<SalePlanArchive> dynamicFind(String a_dynamicWhere){ 
    	
    	Query query = this.em
                .createQuery("select b FROM SalePlanArchive b where "+a_dynamicWhere); 
    	List<SalePlanArchive> bon =  query.getResultList();
    	return bon;
    }
}