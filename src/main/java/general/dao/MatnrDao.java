package general.dao;

import java.util.List;
import java.util.Map;

import general.tables.Matnr;
import general.tables.Salary;
 

public interface MatnrDao extends GenericDao<Matnr>{ 
	public List<Matnr> findAll();
	
	public List<Matnr> findAll(String condition);
	
	public List<Matnr> findAllByBukrs(String a_bukrs);
	
	public Map<Long, Matnr> getMappedList(String cond);
	
	public int getRowCount(String cond);
	
	public List<Matnr> findAllLazy(String cond,int first, int pageSize);
	
	public List<Matnr> findMatnrParts(Long matnr);
	
	public Matnr findByCode(String code);
}
