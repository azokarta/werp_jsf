package general.dao;

import general.tables.TableIdLimit;
 

public interface TableIdLimitDao extends GenericDao<TableIdLimit>{
	public Long countByIds(String a_table_name,String a_table_field,String a_table_field_value,
			String a_table_id, int a_gjahr);
	
	public TableIdLimit findByIds(String a_table_name,String a_table_field,String a_table_field_value,
			String a_table_id, int a_gjahr);
}
