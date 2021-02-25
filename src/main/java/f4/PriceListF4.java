package f4;

import general.AppContext;
import general.dao.DAOException;
import general.dao.PriceListDao;
import general.tables.ContractType;
import general.tables.PriceList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
 

@ManagedBean(name="priceListF4Bean")
@ApplicationScoped
public class PriceListF4 {
	//***************************Application Context***************************
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public AppContext getAppContext() {
		return appContext;  
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	//*************************************************************************
	
	List<PriceList> priceList_list = new ArrayList<PriceList>(); 
	Map<Long, PriceList> pl_map_l = new HashMap<Long, PriceList>();
	
	
	
	public void setPriceList_list(List<PriceList> priceList_list) {
		this.priceList_list = priceList_list;
	}

	public void setPl_map_l(Map<Long, PriceList> pl_map_l) {
		this.pl_map_l = pl_map_l;
	}

	@PostConstruct
	public void init(){ 
		try
		{
			PriceListDao priceListDao = (PriceListDao) appContext.getContext().getBean("priceListDao");
			priceList_list = priceListDao.findAll();
			
			for (PriceList pl : priceList_list) {
				pl_map_l.put(pl.getPrice_list_id(), pl);
			}
		}
		catch(DAOException ex)
		{
			System.out.println("PriceList F4 not loaded");
			throw new DAOException("PriceList F4 not loaded");
		}
	}

	public void updateF4()
	{
		try
		{
			priceList_list = new ArrayList<PriceList>(); 
			pl_map_l = new HashMap<Long, PriceList>();
			PriceListDao priceListDao = (PriceListDao) appContext.getContext().getBean("priceListDao");
			priceList_list = priceListDao.findAll();
			
			for (PriceList pl : priceList_list) {
				pl_map_l.put(pl.getPrice_list_id(), pl);
			}			
		}
	    catch(Exception ex)
		{
	    	System.out.println("PriceList F4 not loaded");
	    	throw new DAOException("PriceList F4 not loaded");
		}
	}
	
	public List<PriceList> getPriceList_list(){
		return priceList_list;
	}
	
	public List<PriceList> getPriceListByCustomerID(Long a_cutomer_id){
		try
		{
			List<PriceList> pl_list = new ArrayList<PriceList>();
			String cond = "customer_id = " + Long.toString(a_cutomer_id);
			PriceListDao priceListDao = (PriceListDao) appContext.getContext().getBean("priceListDao");
			pl_list = priceListDao.dynamicFindPriceList(cond);
			
			return pl_list;
		}
		catch(DAOException ex)
		{
			throw new DAOException("PriceList F4 not loaded");
		}
		
	}
	public List<PriceList> getPriceListByCountry2(String a_bukrs, Long a_countryId){
		try
		{
			List<PriceList> pl_list = new ArrayList<PriceList>();
			String cond = "p.bukrs = " + a_bukrs + " AND p.country_id = " + a_countryId
					+ " and p.active = 1 ";
					//+ " ORDER BY p.from_date DESC";
			
			PriceListDao priceListDao = (PriceListDao) appContext.getContext().getBean("priceListDao");
			pl_list = priceListDao.dynamicFindPriceList(cond);
			
			return pl_list ;
		}
		catch(DAOException ex)
		{
			throw new DAOException("PriceList F4 not loaded");
		}		
	}
	
	public List<PriceList> getPriceListByCountry(String a_bukrs, Long a_countryId){
		try
		{
			List<PriceList> pl_list = new ArrayList<PriceList>();
			String cond = "p.bukrs = " + a_bukrs + " AND p.country_id = " + a_countryId
					+ " and p.active = 1 "
					+ " and p.from_date = some ("
					+ " SELECT max(p2.from_date) FROM PriceList p2"
					+ " WHERE p2.matnr = p.matnr AND p2.country_id = p.country_id AND p2.month = p.month"
					+ " group by p2.country_id, p2.matnr, p2.month"
					+ ")";
					//+ " ORDER BY p.from_date DESC";
			
			PriceListDao priceListDao = (PriceListDao) appContext.getContext().getBean("priceListDao");
			pl_list = priceListDao.dynamicFindPriceList(cond);
			
			return pl_list ;
		}
		catch(DAOException ex)
		{
			throw new DAOException("PriceList F4 not loaded");
		}		
	}

	public List<PriceList> getPriceListByCountryAndDate(String a_bukrs, Long a_countryId, Date inDate){
		try
		{
			List<PriceList> pl_list = new ArrayList<PriceList>();
			java.sql.Date ind = new java.sql.Date(inDate.getTime());
			String cond = "p.bukrs = " + a_bukrs + " AND p.country_id = " + a_countryId
					+ " and p.active = 1 "
					+ " and p.from_date = some ("
					+ " SELECT max(p2.from_date) FROM PriceList p2"
					+ " WHERE p2.matnr = p.matnr AND p2.country_id = p.country_id AND p2.month = p.month"
					+ " and p2.from_date <= '" + ind + "' "
					+ " group by p2.country_id, p2.matnr, p2.month"
					+ ")";
					//+ " ORDER BY p.from_date DESC";
			
			PriceListDao priceListDao = (PriceListDao) appContext.getContext().getBean("priceListDao");
			pl_list = priceListDao.dynamicFindPriceList(cond);
			
			return pl_list ;
		}
		catch(DAOException ex)
		{
			throw new DAOException("PriceList F4 not loaded");
		}		
	}

	
	public List<PriceList> getPriceListByContractType(ContractType a_ct, Long a_countryId){
		try
		{
			List<PriceList> plist = new ArrayList<PriceList>();
			/*
			String cond = "inner join ( "
					+ "select t.matnr, t.month, t.country_id, max(t.from_date) as MaxDate "
					+ "from Price_List t "
					+ "group by t.country_id, t.matnr, t.month "
					+ ") tm on p.matnr = tm.matnr and p.month = tm.month and p.country_id = tm.country_id and p.from_date = tm.MaxDate "
					+ " WHERE matnr = '" + a_ct.getMatnr()
					+ " AND country_id = " + a_countryId;
					//+ "' AND customer_id = 0";
			*/
			
			String qry = "select p"+
			" FROM  PriceList p"+
			" where p.price_list_id = some ("+
			"SELECT max(p2.price_list_id)"+
			" FROM PriceList p2 where p2.active=1"+ 
			" group by p2.country_id, p2.matnr, p2.waers, p2.month, p2.month_type, p2.branch_id )"
		  	+ " AND p.matnr = " + a_ct.getMatnr()
			+ " AND p.country_id = " + a_countryId
			+ " AND p.active = 1";
			
			PriceListDao priceListDao = (PriceListDao) appContext.getContext().getBean("priceListDao");
			plist = priceListDao.findAllQuery(qry);
			
			return plist ;
		}
		catch(DAOException ex)
		{
			throw new DAOException("PriceList F4 not loaded");
		}		
	}

	public List<PriceList> getPriceListByContractTypeAndDate(ContractType a_ct, Long a_countryId, Date inDate){
		try
		{
			List<PriceList> plist = new ArrayList<PriceList>();
			/*
			String cond = "inner join ( "
					+ "select t.matnr, t.month, t.country_id, max(t.from_date) as MaxDate "
					+ "from Price_List t "
					+ "group by t.country_id, t.matnr, t.month "
					+ ") tm on p.matnr = tm.matnr and p.month = tm.month and p.country_id = tm.country_id and p.from_date = tm.MaxDate "
					+ " WHERE matnr = '" + a_ct.getMatnr()
					+ " AND country_id = " + a_countryId;
					//+ "' AND customer_id = 0";
			*/
			
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd"); 
			
			String qry = "select p"+
			" FROM  PriceList p"+
			" where "
			+ " p.price_list_id = some ("+
			"SELECT max(p2.price_list_id)"+
			" FROM PriceList p2 "
			+ " where p2.from_date <= '" + format1.format(inDate) + "'  and p2.active=1" + 
			" group by p2.country_id, p2.matnr, p2.waers, p2.month, p2.month_type, p2.branch_id )"
		  	+ " AND p.matnr = " + a_ct.getMatnr()
			+ " AND p.country_id = " + a_countryId
			+ " AND p.active = 1";
			
			PriceListDao priceListDao = (PriceListDao) appContext.getContext().getBean("priceListDao");
			plist = priceListDao.findAllQuery(qry);
			
			if (plist == null || plist.size() == 0)
				plist = this.getPriceListByContractType(a_ct, a_countryId);
			
			return plist ;
		}
		catch(DAOException ex)
		{
			throw new DAOException("PriceList F4 not loaded");
		}		
	}
	
	public List<PriceList> getPriceListByMatnrAndBranch(Long a_matnr, Long a_branch_id){
		try
		{
			List<PriceList> plist = new ArrayList<PriceList>();
			/*
			String cond = "inner join ( "
					+ "select t.matnr, t.month, t.country_id, max(t.from_date) as MaxDate "
					+ "from Price_List t "
					+ "group by t.country_id, t.matnr, t.month "
					+ ") tm on p.matnr = tm.matnr and p.month = tm.month and p.country_id = tm.country_id and p.from_date = tm.MaxDate "
					+ " WHERE matnr = '" + a_ct.getMatnr()
					+ " AND country_id = " + a_countryId;
					//+ "' AND customer_id = 0";
			*/
			
			String qry = "select p"+
			" FROM  PriceList p"+
			" where p.matnr = " + a_matnr
			+ " AND p.branch_id = " + a_branch_id
			+ " AND p.active = 1 order by month asc, price asc";
			
			PriceListDao priceListDao = (PriceListDao) appContext.getContext().getBean("priceListDao");
			plist = priceListDao.findAllQuery(qry);
			
			return plist ;
		}
		catch(DAOException ex)
		{
			throw new DAOException("PriceList F4 not loaded");
		}		
	}
	
	public Map<Long, PriceList> getPl_map_l() {
		return pl_map_l;
	}
 
}

