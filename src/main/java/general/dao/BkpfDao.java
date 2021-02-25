package general.dao;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import general.output.tables.Fcus01ResultTable;
import general.output.tables.RfcojResultTable;
import general.output.tables.RfdocrowResultTable;
import general.tables.Bkpf;

public interface BkpfDao extends GenericDao<Bkpf> {
	public List<Bkpf> getByBukrsAndDate(String bukrs,  Date fdate, Date tdate); 
	public List<Bkpf> dynamicFindBkpf(String a_dynamicWhere);
	public Bkpf dynamicFindSingleBkpf(String a_dynamicWhere, String a_bukrs);
	public int updateDynamicSingleBkpf(Long a_belnr, int a_gjahr, String a_dynamicWhere, String a_bukrs);
	public List<Bkpf> findBkpfHrpp(List<Long> al_customer_id, String a_dynamicWhere);
	public String getWaersSingleBkpf(String a_dynamicWhere, String a_bukrs);
	public int dynamicCountBkpf(String a_dynamicWhere);
	public Bkpf findStornoSingleBkpf(Long a_belnr, int a_gjahr, String a_bukrs);
	public Bkpf findOriginalSingleBkpf(Long a_belnr, int a_gjahr, String a_bukrs);
	public List<RfcojResultTable> findResultTableRfcoj(String a_dynamicWhere, String a_waers,int a_selectedService);
	public List<RfdocrowResultTable> findResultTableRfdocrow(String a_dynamicWhere,boolean customer);
	public void findResultTableFcus01(Map<String,List<Fcus01ResultTable>> l_client_map
			,Map<String,List<Fcus01ResultTable>> l_supplier_map
			,Map<String,List<Fcus01ResultTable>> l_podotchet_map
			,Map<String,List<Fcus01ResultTable>> l_salary_map
			,Map<String,String> l_currency_map,String a_dynamicWhere,accounting.other.Fcus01.SearchTable p_searchTable);
	
	public Long getNextValueBkpf(String a_blartType);

	
}
