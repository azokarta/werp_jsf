package general.services;


import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import general.GeneralUtil;
import general.dao.DAOException;
import general.dao.ExchangeRateDao;
import general.dao.PrebkpfDao;
import general.dao.PrebsegDao;
import general.tables.Bkpf;
import general.tables.Bseg;
import general.tables.Prebkpf;
import general.tables.Prebseg;

@Service("prebkpfService")
public class PrebkpfServiceImpl implements PrebkpfService{

	@Autowired
	PrebkpfDao preBkpfDao;
	
	@Autowired
	PrebsegDao preBsegDao;
	
	@Autowired
	ExchangeRateDao erDao;
	
	@Autowired
	FinanceService financeService; 
	
	@Override
	public void save(Prebkpf a_prebkpf,List<Prebseg> al_prebseg) throws DAOException{
		try
		{			
			
			Calendar curDate = Calendar.getInstance();
			int curYear = curDate.get(Calendar.YEAR);

			Calendar bldatCal = Calendar.getInstance();
			bldatCal.setTime(a_prebkpf.getBldat());
			int bldatYear = bldatCal.get(Calendar.YEAR);

			if (bldatYear>curYear)
			{
				throw new DAOException("Contact Administrator, Date problem");
			}

			preBkpfDao.create(a_prebkpf);
			for(Prebseg wa_prebseg:al_prebseg)
			{
				wa_prebseg.setPrebkpf_id(a_prebkpf.getPrebkpf_id());
				preBsegDao.create(wa_prebseg);
			}
			
			
			
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		} 
	}
	
	
	public void saveFinance(String a_bukrs,String a_prebkpf_id,Long a_userId) throws DAOException{
		try
		{
			Calendar curDate = Calendar.getInstance();
			//GeneralUtil.getSQLDate(Calendar.getInstance());
			List<Prebkpf> l_prebkpf = new ArrayList<Prebkpf>();			
			l_prebkpf = preBkpfDao.dynamicFindPrebkpf(" prebkpf_id in "+a_prebkpf_id);
			
			Map<Long,List<Bseg>> l_bseg_map = new HashMap<Long,List<Bseg>>();
			List<Prebseg> l_prebseg = new ArrayList<Prebseg>();			
			l_prebseg = preBsegDao.dynamicFindPrebseg(" prebkpf_id in "+a_prebkpf_id);
			
			for(Prebseg wa:l_prebseg)
			{
				Bseg wa_bseg = new Bseg();
				BeanUtils.copyProperties(wa, wa_bseg);
				wa_bseg.setGjahr(curDate.get(Calendar.YEAR));
				
				List<Bseg> wal_bseg = new ArrayList<Bseg>();
				wal_bseg = l_bseg_map.get(wa.getPrebkpf_id());
				if (wal_bseg == null || wal_bseg.size()==0)
				{
					
					wal_bseg = new ArrayList<Bseg>();
					wal_bseg.add(wa_bseg);
					l_bseg_map.put(wa.getPrebkpf_id(), wal_bseg);
				}
				else
					wal_bseg.add(wa_bseg);
			}
			
			Long newDocBelnr = null;
//			Map<String,ExchangeRate> l_er_map = new HashMap<String,ExchangeRate>();
//			l_er_map = erDao.getLastCurrencyRatesDynamicMap(" type=1");
			for(Prebkpf wa:l_prebkpf)
			{
				newDocBelnr = null;
				Bkpf wa_bkpf = new Bkpf();
				List<Bseg> wal_bseg = new ArrayList<Bseg>();
				wal_bseg = l_bseg_map.get(wa.getPrebkpf_id());
				BeanUtils.copyProperties(wa, wa_bkpf);
				wa_bkpf.setBudat(curDate.getTime());
				wa_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
				wa_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
				//fkaaec
				if (wa.getBlart().equals("AE") && wa.getCustomer_id()!=null)
				{
					newDocBelnr = financeService.EpxenseNoInvoice(wa_bkpf, wal_bseg);
				}
				//faco01
				else if (wa.getBlart().equals("KP") && wa.getCustomer_id()!=null)
				{
					Bkpf p_bkpf_VZ = new Bkpf();
					if (wa.getAwkey()!=null && wa.getAwkey()>0)
					{
						p_bkpf_VZ.setBelnr(GeneralUtil.getPreparedBelnr(wa.getAwkey()));
						p_bkpf_VZ.setGjahr(GeneralUtil.getPreparedGjahr(wa.getAwkey()));
						p_bkpf_VZ.setBukrs(wa.getBukrs());
					}
					newDocBelnr = financeService.createFACO01(wa_bkpf,wal_bseg,p_bkpf_VZ);
				}
				//fdcio
				else if (wa.getBlart().equals("OR") && wa.getCustomer_id()!=null)
				{
					newDocBelnr = financeService.IncomeNoInvoice(wa_bkpf, wal_bseg);
				}
				//faci01
				else if (wa.getBlart().equals("DP") && wa.getCustomer_id()!=null)
				{
					newDocBelnr = financeService.createFACI01(wa_bkpf,wal_bseg);
				}
				//fksg
				else if (wa.getBlart().equals("G2"))
				{
					newDocBelnr = financeService.IncomeNoInvoiceFKSG(wa_bkpf, wal_bseg, wa.getUsnam(), wa.getPodotchetnik_id());
				}
//				//fkaae
//				else if ((wa.getBlart().equals("AE") || wa.getBlart().equals("CE") || wa.getBlart().equals("TE") || wa.getBlart().equals("SE")) && wa.getCustomer_id()!=null)
//				{
//					newDocBelnr = financeService.createAccountPayableDocs(wa_bkpf, wal_bseg);
//				}
//				//ftaxk
//				else if ((wa.getBlart().equals("KT") || wa.getBlart().equals("NT")) && wa.getCustomer_id()!=null)
//				{
//					newDocBelnr = financeService.createAccountPayableDocs(wa_bkpf, wal_bseg);
//				}
				
				wa.setBelnr(newDocBelnr);
				wa.setGjahr(wa_bkpf.getGjahr());
				wa.setStatus(1);
				wa.setDone_by(a_userId);
				wa.setDone_date(curDate.getTime());
				preBkpfDao.update(wa);
							
//				if (wa.getBlart().equals("AE"))
//				{
//					saveAEDocument(wa,l_er_map,a_userId);
//				}
//				else if (wa.getBlart().equals("KP"))
//				{
//					saveKPDocument(wa,l_er_map,a_userId);
//				}
				
			}
			
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		} 
	}
	
	public void cancelStatus(String a_prebkpf_id,Long a_userId) throws DAOException{
		try
		{
			preBkpfDao.updateDynamicPreBkpf(" status=2, done_by= "+a_userId+", done_date='"+GeneralUtil.getSQLDate(Calendar.getInstance())+"'", " prebkpf_id in "+a_prebkpf_id);
//			for(Prebkpf wa:al_prebkpf)
//			{
//				wa.setStatus(2);
//				preDao.update(wa);
//			}
//			
			//preDao.create(a_prebkpf);
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		} 
	}
	
	
	
	public boolean createPrebkpf(Bkpf a_bkpf, List<Bseg> wal_bseg) throws DAOException{
		try{


			Calendar curDate = Calendar.getInstance();
            Time cputm = new Time(curDate.getTimeInMillis());

			Prebkpf p_prebkpf = new Prebkpf();
			BeanUtils.copyProperties(a_bkpf,p_prebkpf);

			p_prebkpf.setCreated_date(cputm);
			if (p_prebkpf.getWaers() != null && p_prebkpf.getWaers().equals("USD")) {
				p_prebkpf.setSumma(p_prebkpf.getDmbtr());
			} else if (p_prebkpf.getWaers() != null && (!p_prebkpf.getWaers().equals("USD"))) {
				p_prebkpf.setSumma(p_prebkpf.getWrbtr());
			}

			List<Prebseg> l_prebseg = new ArrayList<Prebseg>();
			for(Bseg wa_bseg:wal_bseg){
				if (wa_bseg.getShkzg().equals("S")) p_prebkpf.setHkont_d(wa_bseg.getHkont());
				else p_prebkpf.setHkont_k(wa_bseg.getHkont());



				Prebseg wa_prebseg = new Prebseg();
				BeanUtils.copyProperties(wa_bseg,wa_prebseg);
				l_prebseg.add(wa_prebseg);
			}


			save(p_prebkpf,l_prebseg);
			
			return true;

		}
		catch(Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	
	
//	public void saveAEDocument(Prebkpf wa_pre,Map<String,ExchangeRate> l_er_map,Long a_userId) throws DAOException{
//		try { 
//			
//			Calendar curDate = Calendar.getInstance();
//			if (wa_pre.getWaers() == null || wa_pre.getWaers().isEmpty()){
//				throw new DAOException("Выберите валюту.");
//			}
//
//			if (wa_pre.getBukrs() == null || wa_pre.getBukrs().isEmpty())
//			{
//				throw new DAOException("Выберите компанию.");
//			}
//			
//			if (wa_pre.getHkont_k() == null || wa_pre.getHkont_k().length()==0)
//			{
//				throw new DAOException("Выберите Касса/Банк.");
//			}
//
//			Bkpf p_bkpf = new Bkpf();
//			List<Bseg> l_bsegFinal = new ArrayList<Bseg>();	
//			Bseg p_bsegKredit = new Bseg();
//			Bseg p_bsegDebet = new Bseg();
//			
//			
//			p_bkpf.setBukrs(wa_pre.getBukrs());
//			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
//			p_bkpf.setBlart(wa_pre.getBlart());
//			p_bkpf.setBldat(wa_pre.getBldat());
//			p_bkpf.setBudat(curDate.getTime());
//			p_bkpf.setUsnam(wa_pre.getUsnam()); 
//			p_bkpf.setTcode(wa_pre.getTcode()); 			
//			p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1); 
//			p_bkpf.setBrnch(wa_pre.getBrnch());
//			p_bkpf.setContract_number(wa_pre.getContract_number());
//			p_bkpf.setBusiness_area_id(wa_pre.getBusiness_area_id());
//			p_bkpf.setCpudt(curDate.getTime());
//			p_bkpf.setWaers(wa_pre.getWaers());
//			//p_bkpf.setCustomer_id(wa_pre.getCustomer_id());	
//			p_bkpf.setDep(wa_pre.getDep());
//			p_bkpf.setBktxt(wa_pre.getBktxt());
//			p_bkpf.setAwtyp(0);
//			
//			
//			
//			
//						
//			
//			
//			
//			
//			p_bsegKredit.setBuzei(1);
//			p_bsegKredit.setHkont(wa_pre.getHkont_k());
//			p_bsegKredit.setBukrs(p_bkpf.getBukrs());
//			p_bsegKredit.setGjahr(p_bkpf.getGjahr());
//			p_bsegKredit.setBschl("50");
//			p_bsegKredit.setShkzg("H");
//
//			p_bsegDebet.setBuzei(2);
//			p_bsegDebet.setHkont(wa_pre.getHkont_d());
//			p_bsegDebet.setBukrs(p_bkpf.getBukrs());
//			p_bsegDebet.setGjahr(p_bkpf.getGjahr());
//			p_bsegDebet.setBschl("40");
//			p_bsegDebet.setShkzg("S");
//			
//			
//			
//			
//			
//			if (p_bsegDebet.getHkont() == null || p_bsegDebet.getHkont().isEmpty())
//			{
//				throw new DAOException("Choose GL account");
//			} 
//			
//			if (wa_pre.getDmbtr()==0 || wa_pre.getDmbtr()<0){
//				throw new DAOException("Enter the amount");
//			}
//			else
//			{
//				if (p_bkpf.getWaers()!=null && p_bkpf.getWaers().equals("USD") ){
//					p_bkpf.setKursf(1);
//					p_bsegKredit.setDmbtr(wa_pre.getDmbtr());
//					p_bsegDebet.setDmbtr(wa_pre.getDmbtr());
//				}
//				else if (p_bkpf.getWaers()!=null  && (!p_bkpf.getWaers().equals("USD")) ){
//					ExchangeRate wa_er = l_er_map.get(p_bkpf.getWaers());
//					p_bkpf.setKursf(wa_er.getSc_value());
//					
//					p_bsegKredit.setWrbtr(wa_pre.getDmbtr());
//					p_bsegDebet.setWrbtr(wa_pre.getDmbtr());					
//					
//					p_bsegKredit.setDmbtr(GeneralUtil.round(p_bsegKredit.getWrbtr()/wa_er.getSc_value(), 2));
//					p_bsegDebet.setDmbtr(GeneralUtil.round(p_bsegDebet.getWrbtr()/wa_er.getSc_value(), 2));
//				}
//				
//			}
//			l_bsegFinal.add(p_bsegKredit);
//			l_bsegFinal.add(p_bsegDebet);
//			Long newDocBelnr = financeService.EpxenseNoInvoice(p_bkpf, l_bsegFinal);	
//
//			wa_pre.setBelnr(newDocBelnr);
//			wa_pre.setGjahr(p_bkpf.getGjahr());
//			wa_pre.setStatus(1);
//			wa_pre.setDone_by(a_userId);
//			wa_pre.setDone_date(curDate.getTime());
//			preDao.update(wa_pre);
//			
//		} 
//		catch (DAOException ex)
//		{   
//			throw new DAOException(ex.getMessage());
//		}  
//		
//	}
//	
//	public void saveKPDocument(Prebkpf wa_pre,Map<String,ExchangeRate> l_er_map,Long a_userId) throws DAOException{
//		try { 
//			
//			Calendar curDate = Calendar.getInstance();
//			if (wa_pre.getWaers() == null || wa_pre.getWaers().isEmpty()){
//				throw new DAOException("Выберите валюту.");
//			}
//
//			if (wa_pre.getBukrs() == null || wa_pre.getBukrs().isEmpty())
//			{
//				throw new DAOException("Выберите компанию.");
//			}
//			
//			if (wa_pre.getHkont_k() == null || wa_pre.getHkont_k().length()==0)
//			{
//				throw new DAOException("Выберите Касса/Банк.");
//			}
//
//			Bkpf p_bkpf = new Bkpf();
//			 
//			
//			List<Bseg> l_bsegFinal = new ArrayList<Bseg>();	
//			Bseg p_bsegKredit = new Bseg();
//			Bseg p_bsegDebet = new Bseg();
//			
//			
//			p_bkpf.setBukrs(wa_pre.getBukrs());
//			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
//			p_bkpf.setBlart(wa_pre.getBlart());
//			p_bkpf.setBldat(wa_pre.getBldat());
//			p_bkpf.setBudat(curDate.getTime());
//			p_bkpf.setUsnam(wa_pre.getUsnam()); 
//			p_bkpf.setTcode(wa_pre.getTcode()); 			
//			p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1); 
//			p_bkpf.setBrnch(wa_pre.getBrnch());
//			p_bkpf.setContract_number(wa_pre.getContract_number());
//			p_bkpf.setBusiness_area_id(wa_pre.getBusiness_area_id());
//			p_bkpf.setCpudt(curDate.getTime());
//			p_bkpf.setWaers(wa_pre.getWaers());
//			p_bkpf.setCustomer_id(wa_pre.getCustomer_id());	
//			p_bkpf.setDep(wa_pre.getDep());
//			p_bkpf.setBktxt(wa_pre.getBktxt());
//			p_bkpf.setAwtyp(2);
//			
//			
//			
//			
//						
//			
//			
//			
//			
//			p_bsegKredit.setBuzei(1);
//			p_bsegKredit.setHkont(wa_pre.getHkont_k());
//			p_bsegKredit.setBukrs(p_bkpf.getBukrs());
//			p_bsegKredit.setGjahr(p_bkpf.getGjahr());
//			p_bsegKredit.setBschl("50");
//			p_bsegKredit.setShkzg("H");
//
//			p_bsegDebet.setBuzei(2);
//			p_bsegDebet.setHkont(wa_pre.getHkont_d());
//			p_bsegDebet.setBukrs(p_bkpf.getBukrs());
//			p_bsegDebet.setGjahr(p_bkpf.getGjahr());
//			p_bsegDebet.setBschl("25");
//			p_bsegDebet.setShkzg("S");
//			p_bsegDebet.setLifnr(p_bkpf.getCustomer_id());
//			
//			
//			
//			
//			
//			if (p_bsegDebet.getHkont() == null || p_bsegDebet.getHkont().isEmpty())
//			{
//				throw new DAOException("Choose GL account");
//			} 
//			
//			if (wa_pre.getDmbtr()==0 || wa_pre.getDmbtr()<0){
//				throw new DAOException("Enter the amount");
//			}
//			else
//			{
//				if (p_bkpf.getWaers()!=null && p_bkpf.getWaers().equals("USD") ){
//					p_bkpf.setKursf(1);
//					p_bsegKredit.setDmbtr(wa_pre.getDmbtr());
//					p_bsegDebet.setDmbtr(wa_pre.getDmbtr());
//				}
//				else if (p_bkpf.getWaers()!=null  && (!p_bkpf.getWaers().equals("USD")) ){
//					ExchangeRate wa_er = l_er_map.get(p_bkpf.getWaers());
//					p_bkpf.setKursf(wa_er.getSc_value());
//					
//					p_bsegKredit.setWrbtr(wa_pre.getDmbtr());
//					p_bsegDebet.setWrbtr(wa_pre.getDmbtr());					
//					
//					p_bsegKredit.setDmbtr(GeneralUtil.round(p_bsegKredit.getWrbtr()/wa_er.getSc_value(), 2));
//					p_bsegDebet.setDmbtr(GeneralUtil.round(p_bsegDebet.getWrbtr()/wa_er.getSc_value(), 2));
//				}
//				
//			}
//			l_bsegFinal.add(p_bsegKredit);
//			l_bsegFinal.add(p_bsegDebet);
//			
//			
//			
//			Bkpf p_bkpf_VZ = new Bkpf();
//			if (wa_pre.getAwkey()!=null && wa_pre.getAwkey()>0)
//			{
//				p_bkpf_VZ.setBelnr(GeneralUtil.getPreparedBelnr(wa_pre.getAwkey()));
//				p_bkpf_VZ.setGjahr(GeneralUtil.getPreparedGjahr(wa_pre.getAwkey()));
//			}
//			
//			Long newDocBelnr = financeService.createFACO01(p_bkpf,l_bsegFinal,p_bkpf_VZ);	
//
//			wa_pre.setBelnr(newDocBelnr);
//			wa_pre.setGjahr(p_bkpf.getGjahr());
//			wa_pre.setStatus(1);
//			wa_pre.setDone_by(a_userId);
//			wa_pre.setDone_date(curDate.getTime());
//			preDao.update(wa_pre);
//			
//		} 
//		catch (DAOException ex)
//		{   
//			throw new DAOException(ex.getMessage());
//		}  
//		
//	}
}
