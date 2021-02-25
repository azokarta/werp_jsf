package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import general.GeneralUtil;
import general.dao.DAOException;
import general.dao.RfcolDao;
import general.output.tables.RfcolResultTable;
import general.tables.Rfcol;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("rfcolService")
public class RfcolServiceImpl implements RfcolService{
	@Autowired
	private RfcolDao rfcolDao;
	public void save(String a_bukrs, int a_gjahr, int a_monat) throws DAOException
	{
		try{
			List<RfcolResultTable> results = new ArrayList<RfcolResultTable>();
			List<RfcolResultTable> results2 = new ArrayList<RfcolResultTable>();
			List<Rfcol> l_rfcol = new ArrayList<Rfcol>();
			if (rfcolDao.countSaved(a_bukrs, a_gjahr, a_monat)==0)
			{
				String dynamicWhereClause = "";
	  			dynamicWhereClause = "";
	  			Calendar date_from = Calendar.getInstance();
	  			Calendar date_to = Calendar.getInstance();
	  			
	  			date_from.set(Calendar.YEAR, a_gjahr);
	  			date_from.set(Calendar.MONTH, a_monat-1);
	  			date_from.set(Calendar.DAY_OF_MONTH, 1);
	  			
	  			
	  			date_to.setTime(GeneralUtil.getLastDayOfMonth(a_gjahr, a_monat).getTime());
	  			

	  			
	  			if (a_bukrs!=null && !a_bukrs.equals("0"))
	  			{
	  				dynamicWhereClause = dynamicWhereClause + " and br.bukrs = '"+a_bukrs+"' ";
	  			}
	  			else
	  				throw new DAOException("Выберите компанию");
	  			
	  			System.out.println(GeneralUtil.getSQLDate(date_from));
	  			System.out.println(GeneralUtil.getSQLDate(date_to));
	  			results = rfcolDao.dynamicFindCollectorStat(dynamicWhereClause, GeneralUtil.getSQLDate(date_from), GeneralUtil.getSQLDate(date_to), 2, 0);
	  			for (RfcolResultTable wa:results)
	  			{
	  				Rfcol wa_rfcol = new Rfcol();
	  				BeanUtils.copyProperties(wa, wa_rfcol);
	  				wa_rfcol.setStatus(0);
	  				wa_rfcol.setMonat(a_monat);
	  				wa_rfcol.setGjahr(a_gjahr);
	  				wa_rfcol.setBukrs(a_bukrs);
	  				l_rfcol.add(wa_rfcol);
	  			}
	  			
	  			results2 = rfcolDao.dynamicFindCollectorStat(dynamicWhereClause, GeneralUtil.getSQLDate(date_from), GeneralUtil.getSQLDate(date_to), 2, 1);
	  			for (RfcolResultTable wa:results2)
	  			{
	  				Rfcol wa_rfcol = new Rfcol();
	  				BeanUtils.copyProperties(wa, wa_rfcol);
	  				wa_rfcol.setStatus(1);
	  				wa_rfcol.setMonat(a_monat);
	  				wa_rfcol.setGjahr(a_gjahr);
	  				wa_rfcol.setBukrs(a_bukrs);
	  				l_rfcol.add(wa_rfcol);
	  			}
	  			
	  			
	  			for (Rfcol wa:l_rfcol)
	  			{
	  				rfcolDao.create(wa);
	  			}
			}
			else
			{
				throw new DAOException("Данные имеются за "+a_gjahr+" год и "+a_monat+" месяц");
			}
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	
	public List<RfcolResultTable> search(String a_bukrs, int a_gjahr, int a_monat,String a_dynamicWhere, java.sql.Date a_budat_from, java.sql.Date a_budat_to,int a_group_by, int a_status) throws DAOException
	{
		try{
			List<RfcolResultTable> l_result = new ArrayList<RfcolResultTable>();
			if (rfcolDao.countSaved(a_bukrs, a_gjahr, a_monat)==0)
			{
				
				l_result = rfcolDao.dynamicFindCollectorStat(a_dynamicWhere, a_budat_from,a_budat_to, a_group_by, a_status);
				return l_result;
			}
			else
			{
				List<Rfcol> l_rfcol = new ArrayList<Rfcol>();
				l_rfcol = rfcolDao.findBy(a_bukrs, a_gjahr, a_monat,a_dynamicWhere, a_group_by, a_status);
				for (Rfcol wa:l_rfcol)
	  			{
					RfcolResultTable wa_rrt = new RfcolResultTable();
	  				BeanUtils.copyProperties(wa, wa_rrt);
	  				l_result.add(wa_rrt);
	  			}
				
				return l_result;
			}
				
			
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	public List<Object[]> searchDetail(String a_bukrs, int a_gjahr, int a_monat,String a_dynamicWhere, java.sql.Date a_budat_from, java.sql.Date a_budat_to, int a_status) throws DAOException
	{
		try{
			List<Object[]> l_result = new ArrayList<Object[]>();
			if (rfcolDao.countSaved(a_bukrs, a_gjahr, a_monat)==0)
			{
				
				l_result = rfcolDao.dynamicFindCollectorDetail(a_dynamicWhere, a_budat_from,a_budat_to, a_status);
				return l_result;
			}
			else
			{
				throw new DAOException("Детали отсутствуют в архиве.");
			}
				
			
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
}
