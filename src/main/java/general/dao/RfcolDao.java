package general.dao;

import java.util.List;

import general.output.tables.RfcolResultTable;
import general.tables.Rfcol;

public interface RfcolDao extends GenericDao<Rfcol> {
	public List<RfcolResultTable> dynamicFindCollectorStat(String a_dynamicWhere, java.sql.Date a_budat_from, java.sql.Date a_budat_to,int a_group_by, int a_status) throws DAOException;
	public List<Object[]> dynamicFindCollectorDetail(String a_dynamicWhere, java.sql.Date a_budat_from, java.sql.Date a_budat_to, int a_status) throws DAOException;
	public int countSaved(String a_bukrs, int a_gjahr, int a_monat) throws DAOException;
	public List<Rfcol> findBy(String a_bukrs, int a_gjahr, int a_monat, String a_dyn, int a_group_by, int a_status) throws DAOException;
	
	public List<Object[]> dynamicFrep3Result(String a_dynamicWhere, String a_lang) throws DAOException;
	public List<Object[]> dynamicFrep3Detail(String a_dynamicWhere, String a_lang) throws DAOException;
	
	
	public List<Object[]> dynamicFrep4Result(String a_dynamicWhere, String a_lang) throws DAOException;
	public List<Object[]> dynamicFrep4Detail(String a_dynamicWhere, String a_lang) throws DAOException;
	
	public List<Object[]> dynamicFrep5Result(String a_dynamicWhere) throws DAOException;
	public List<Object[]> dynamicFrep5Detail(String a_dynamicWhere) throws DAOException;
	
	public List<Object[]> dynamicFrep8Result(String a_dynamicWhere) throws DAOException;
	
	public List<Object[]> dynamicFoeaResult(String a_dynamicWhere, String a_lang) throws DAOException;
	public List<Object[]> dynamicFoeaDetail(String a_dynamicWhere) throws DAOException;
	
	
	
	
}
