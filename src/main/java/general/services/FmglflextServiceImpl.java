package general.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.GeneralUtil;
import general.dao.DAOException;
import general.dao.Fmglflext2Dao;
import general.dao.FmglflextDao;
import general.tables.Fmglflext;
import general.tables.Fmglflext2;

@Service("fmglflextService")
public class FmglflextServiceImpl implements FmglflextService{
	@Autowired
    private FmglflextDao fmgDao;
	
	@Autowired
    private Fmglflext2Dao fmgDao2;
	public List<Fmglflext> getAccountsBalance(String a_bukrs, int a_gjahr, int a_monat, List<String> sl_hkont) throws DAOException
	{
		try{
			String fields = "";
			List<Fmglflext> l_fmg = new ArrayList<Fmglflext>();
			GeneralUtil.checkForNullLong(a_bukrs, "Company code is empty, FmglflextService->getAccountsBalance");
			if (a_gjahr==0)
			{
				throw new DAOException("Select year, FmglflextService->getAccountsBalance");
			}
			if (a_monat<1 || a_monat>12)
			{
				throw new DAOException("Select month, FmglflextService->getAccountsBalance");
			}
			else
			{
				switch (a_monat) {
					case 1:  fields = fields + "f.beg_amount+f.month1";
	                    break;
					case 2:  fields = fields + "f.beg_amount+f.month1+f.month2";
	                    break;
					case 3:  fields = fields + "f.beg_amount+f.month1+f.month2+f.month3";
	                    break;
					case 4:  fields = fields + "f.beg_amount+f.month1+f.month2+f.month3+f.month4";
	                    break;
					case 5:  fields = fields + "f.beg_amount+f.month1+f.month2+f.month3+f.month4+f.month5";
	                    break;
					case 6:  fields = fields + "f.beg_amount+f.month1+f.month2+f.month3+f.month4+f.month5+f.month6";
	                    break;
					case 7:  fields = fields + "f.beg_amount+f.month1+f.month2+f.month3+f.month4+f.month5+f.month6+f.month7";
	                    break;
					case 8:  fields = fields + "f.beg_amount+f.month1+f.month2+f.month3+f.month4+f.month5+f.month6+f.month7+f.month8";
	                    break;
					case 9:  fields = fields + "f.beg_amount+f.month1+f.month2+f.month3+f.month4+f.month5+f.month6+f.month7+f.month8+f.month9";
	                    break;
					case 10: fields = fields + "f.beg_amount+f.month1+f.month2+f.month3+f.month4+f.month5+f.month6+f.month7+f.month8+f.month9+f.month10";
	                    break;
					case 11: fields = fields + "f.beg_amount+f.month1+f.month2+f.month3+f.month4+f.month5+f.month6+f.month7+f.month8+f.month9+f.month10+f.month11";
	                    break;
					case 12: fields = fields + "f.beg_amount+f.month1+f.month2+f.month3+f.month4+f.month5+f.month6+f.month7+f.month8+f.month9+f.month10+f.month11+f.month12";
	                    break;
					
					}
					
			}
			if (sl_hkont.size()>0)
			{
				l_fmg = fmgDao.getBalanceByBukrsGjahrHkontList(a_bukrs, a_gjahr, sl_hkont, fields);
			}
			else
			{
				l_fmg = fmgDao.getBalanceByBukrsGjahr(a_bukrs, a_gjahr, fields);
			}

			//Adding Daily Fin Doc///////////////////////////////////////////////////////////
			Map<String, Fmglflext> fmglflextMap = new HashMap<>();

			for(Fmglflext wa: l_fmg){
				fmglflextMap.put(wa.getHkont(),wa);
			}
			List<Fmglflext> l_daily = fmgDao.getDailyFinDocGroupedByHkont(a_bukrs, sl_hkont);
			System.out.println(l_daily.size());
			System.out.println(a_bukrs);
			System.out.println(sl_hkont);
			for (Fmglflext wa : l_daily) {
				Fmglflext fmglflext = new Fmglflext();
				fmglflext = fmglflextMap.get(wa.getHkont());
				if (fmglflext!=null){
					fmglflext.setBeg_amount(fmglflext.getBeg_amount()+wa.getBeg_amount());
				}else{
					l_fmg.add(wa);
				}
			}
			///////////////////////////////////////////////////////////////////////////////
			return l_fmg;
				
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	
	public List<Object[]> getAccountsBalanceFrep6(String a_bukrs,int a_gjahr, List<String> al_branch_id, String a_hkontCashBank) throws DAOException
	{
		try{
			String whereClause="";
			String fields = "";
			GeneralUtil.checkForNullLong(a_bukrs, "Company code is empty, FmglflextService->getAccountsBalance");
			if (a_gjahr==0)
			{
				throw new DAOException("Select year, FmglflextService->getAccountsBalance");
			}
			
			fields = "f.beg_amount+f.month1+f.month2+f.month3+f.month4+f.month5+f.month6+f.month7+f.month8+f.month9+f.month10+f.month11+f.month12";
			
			if (al_branch_id.size()>0)
			{
				int count = 0;
				for (String wa_br_id:al_branch_id)
				{
					count++;
					if (count==1)
					{
						whereClause = whereClause + " and b.branch_id in ("+wa_br_id;
					}
					else
					{
						whereClause = whereClause + ","+wa_br_id;
					}
					
				}
				whereClause = whereClause + ")";
			}
			if (a_hkontCashBank!=null && !a_hkontCashBank.equals("0"))
			{
				whereClause = whereClause + " and s.hkont like '"+a_hkontCashBank+"%'";
			}
			List<Object[]> l_obj = fmgDao.getBalanceFrep6(a_bukrs, a_gjahr, whereClause, fields);
			if (l_obj.size()>0)
			{
				return l_obj;
			}
			else
			{
				l_obj = fmgDao.getBalanceFrep6(a_bukrs, a_gjahr-1, whereClause, fields);
				return l_obj;
			}
				
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	
	public void closeYear(String a_bukrs, int a_year)
	{
		try
		{
			Map<String,Fmglflext> l_fmg_map = new HashMap<String,Fmglflext>();
			List<Fmglflext> l_fmg = new ArrayList<Fmglflext>();
			if (a_year==2016)
			{
				
				l_fmg = fmgDao.findAll(a_bukrs);
			}
			else if (a_year>2016)
			{
				l_fmg = fmgDao.findAll(a_bukrs,a_year);
			}
			else
			{
				throw new DAOException("");
			}
			
			if (l_fmg!=null && l_fmg.size()>0)
			{
				for(Fmglflext wa_fmg:l_fmg)
				{
					l_fmg_map.put(wa_fmg.getBukrs()+String.valueOf(wa_fmg.getGjahr())+wa_fmg.getHkont()+wa_fmg.getDrcrk()+wa_fmg.getWaers(), wa_fmg);
				}
				
				for(Fmglflext wa_fmg:l_fmg)
				{
					Fmglflext wa_fmg_chng = l_fmg_map.get(wa_fmg.getBukrs()+String.valueOf(wa_fmg.getGjahr()+1)+wa_fmg.getHkont()+wa_fmg.getDrcrk()+wa_fmg.getWaers());
					if (wa_fmg_chng==null)
					{
						wa_fmg_chng = new Fmglflext();
						wa_fmg_chng.setBukrs(wa_fmg.getBukrs());
						wa_fmg_chng.setGjahr(wa_fmg.getGjahr()+1);
						wa_fmg_chng.setHkont(wa_fmg.getHkont());
						wa_fmg_chng.setDrcrk(wa_fmg.getDrcrk());
						wa_fmg_chng.setWaers(wa_fmg.getWaers());
						
						wa_fmg_chng.setBeg_amount(wa_fmg.getBeg_amount()+wa_fmg.getMonth1()+wa_fmg.getMonth2()+wa_fmg.getMonth3()+wa_fmg.getMonth4()+wa_fmg.getMonth5()+wa_fmg.getMonth6()
								+wa_fmg.getMonth7()+wa_fmg.getMonth8()+wa_fmg.getMonth9()+wa_fmg.getMonth10()+wa_fmg.getMonth11()+wa_fmg.getMonth12());
						fmgDao.create(wa_fmg_chng);
						l_fmg_map.put(wa_fmg_chng.getBukrs()+String.valueOf(wa_fmg_chng.getGjahr())+wa_fmg_chng.getHkont()+wa_fmg_chng.getDrcrk()+wa_fmg_chng.getWaers(), wa_fmg_chng);						
					}
					else
					{
						wa_fmg_chng.setBeg_amount(wa_fmg.getBeg_amount()+wa_fmg.getMonth1()+wa_fmg.getMonth2()+wa_fmg.getMonth3()+wa_fmg.getMonth4()+wa_fmg.getMonth5()+wa_fmg.getMonth6()
								+wa_fmg.getMonth7()+wa_fmg.getMonth8()+wa_fmg.getMonth9()+wa_fmg.getMonth10()+wa_fmg.getMonth11()+wa_fmg.getMonth12());
					}
					
				}
				Long key_long = null;
				for (Map.Entry<String, Fmglflext> entry : l_fmg_map.entrySet()) {
					key_long = null;
					Fmglflext wa_ft = new Fmglflext();
			        wa_ft = (Fmglflext) entry.getValue();
			        fmgDao.update(wa_ft);
			        
				}			    	
				
			}
			
			Map<String,Fmglflext2> l_fmg2_map = new HashMap<String,Fmglflext2>();
			List<Fmglflext2> l_fmg2 = new ArrayList<Fmglflext2>();
			if (a_year==2016)
			{
				
				l_fmg2 = fmgDao2.findAll(a_bukrs);
			}
			else if (a_year>2016)
			{
				l_fmg2 = fmgDao2.findAll(a_bukrs,a_year);
			}
			else
			{
				throw new DAOException("");
			}
			
			if (l_fmg2!=null && l_fmg2.size()>0)
			{
				for(Fmglflext2 wa_fmg2:l_fmg2)
				{
					l_fmg2_map.put(wa_fmg2.getBukrs()+String.valueOf(wa_fmg2.getGjahr())+wa_fmg2.getHkont()+wa_fmg2.getDrcrk()+wa_fmg2.getWaers()+String.valueOf(wa_fmg2.getBranch_id()), wa_fmg2);
				}
				
				for(Fmglflext2 wa_fmg2:l_fmg2)
				{
					Fmglflext2 wa_fmg2_chng = l_fmg2_map.get(wa_fmg2.getBukrs()+String.valueOf(wa_fmg2.getGjahr()+1)+wa_fmg2.getHkont()+wa_fmg2.getDrcrk()+wa_fmg2.getWaers()+String.valueOf(wa_fmg2.getBranch_id()));
					if (wa_fmg2_chng==null)
					{
						wa_fmg2_chng = new Fmglflext2();
						wa_fmg2_chng.setBukrs(wa_fmg2.getBukrs());
						wa_fmg2_chng.setGjahr(wa_fmg2.getGjahr()+1);
						wa_fmg2_chng.setHkont(wa_fmg2.getHkont());
						wa_fmg2_chng.setDrcrk(wa_fmg2.getDrcrk());
						wa_fmg2_chng.setWaers(wa_fmg2.getWaers());
						wa_fmg2_chng.setBranch_id(wa_fmg2.getBranch_id());
						
						wa_fmg2_chng.setBeg_amount(wa_fmg2.getBeg_amount()+wa_fmg2.getMonth1()+wa_fmg2.getMonth2()+wa_fmg2.getMonth3()+wa_fmg2.getMonth4()+wa_fmg2.getMonth5()+wa_fmg2.getMonth6()
								+wa_fmg2.getMonth7()+wa_fmg2.getMonth8()+wa_fmg2.getMonth9()+wa_fmg2.getMonth10()+wa_fmg2.getMonth11()+wa_fmg2.getMonth12());
						fmgDao2.create(wa_fmg2_chng);
						l_fmg2_map.put(wa_fmg2_chng.getBukrs()+String.valueOf(wa_fmg2_chng.getGjahr())+wa_fmg2_chng.getHkont()+
								wa_fmg2_chng.getDrcrk()+wa_fmg2_chng.getWaers()+String.valueOf(wa_fmg2_chng.getBranch_id()), wa_fmg2_chng);						
					}
					else
					{
						wa_fmg2_chng.setBeg_amount(wa_fmg2.getBeg_amount()+wa_fmg2.getMonth1()+wa_fmg2.getMonth2()+wa_fmg2.getMonth3()+wa_fmg2.getMonth4()+wa_fmg2.getMonth5()+wa_fmg2.getMonth6()
								+wa_fmg2.getMonth7()+wa_fmg2.getMonth8()+wa_fmg2.getMonth9()+wa_fmg2.getMonth10()+wa_fmg2.getMonth11()+wa_fmg2.getMonth12());
					}
					
				}
				Long key_long = null;
				for (Map.Entry<String, Fmglflext2> entry : l_fmg2_map.entrySet()) {
					key_long = null;
					Fmglflext2 wa_ft2 = new Fmglflext2();
			        wa_ft2 = (Fmglflext2) entry.getValue();
			        fmgDao2.update(wa_ft2);
			        
				}			    	
				
			}
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
}
