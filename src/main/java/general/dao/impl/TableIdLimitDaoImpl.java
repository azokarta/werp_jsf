package general.dao.impl;
 
import javax.persistence.Query;

import general.dao.TableIdLimitDao; 
import general.tables.TableIdLimit;

import org.springframework.stereotype.Component;
@Component("tableIdLimitDao")
public class TableIdLimitDaoImpl extends GenericDaoImpl<TableIdLimit> implements TableIdLimitDao{
	public Long countByIds(String a_table_name,String a_table_field,String a_table_field_value,
			String a_table_id, int a_gjahr){
		Query query = this.em
                .createQuery("select count(t.table_name) FROM TableIdLimit t where t.table_name= :table_name"
                		+ " and t.table_field= :table_field"
                		+ " and t.table_field_value= :table_field_value"
                		+ " and t.table_id= :table_id"
                		+ " and t.gjahr= :gjahr");
        query.setParameter("table_name", a_table_name);   
        query.setParameter("table_field", a_table_field);  
        query.setParameter("table_field_value", a_table_field_value);  
        query.setParameter("table_id", a_table_id);  
        query.setParameter("gjahr", a_gjahr);       
        Long count = (Long) query.getSingleResult();        
        return count;
    }
	public TableIdLimit findByIds(String a_table_name,String a_table_field,String a_table_field_value,
			String a_table_id, int a_gjahr){
    	Query query = this.em
                .createQuery("select t FROM TableIdLimit t where t.table_name= :table_name"
                		+ " and t.table_field= :table_field"
                		+ " and t.table_field_value= :table_field_value"
                		+ " and t.table_id= :table_id"
                		+ " and t.gjahr= :gjahr");
        query.setParameter("table_name", a_table_name);   
        query.setParameter("table_field", a_table_field);  
        query.setParameter("table_field_value", a_table_field_value);  
        query.setParameter("table_id", a_table_id);  
        query.setParameter("gjahr", a_gjahr);  
        TableIdLimit tbil = (TableIdLimit) query.getSingleResult();
    	return tbil;
    }
}
