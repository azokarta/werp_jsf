package general.services;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import general.GeneralUtil;
import general.dao.DAOException;
import general.output.tables.RfcolResultTable;

public interface RfcolService {
	
	@Transactional
	public void save(String a_bukrs, int a_gjahr, int a_monat) throws DAOException;
	
	@Transactional
	public List<RfcolResultTable> search(String a_bukrs, int a_gjahr, int a_monat, String a_dynamicWhere, java.sql.Date a_budat_from, java.sql.Date a_budat_to,int a_group_by, int a_status) throws DAOException;
	
	@Transactional
	public List<Object[]> searchDetail(String a_bukrs, int a_gjahr, int a_monat,String a_dynamicWhere, java.sql.Date a_budat_from, java.sql.Date a_budat_to, int a_status);
}
