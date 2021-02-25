package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import general.GeneralUtil;
import general.dao.BkpfDao;
import general.dao.BranchDao;
import general.dao.BsegDao;
import general.dao.DAOException;
import general.dao.ExchangeRateDao;
import general.dao.TempPayrollArchiveDao;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.ExchangeRate;
import general.tables.Payroll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("financeServService")
public class FinanceServServiceImpl implements FinanceServService{
	@Autowired
	private BranchDao brDao;
	
	@Autowired
    private FinanceService financeService;
	
	@Autowired
    private ExchangeRateDao exrateDao;
	
	@Autowired
    private BkpfDao bkpfDao;
	
	@Autowired
    private BsegDao bsegDao;
	
	@Autowired
    private PayrollService prlSerDao;
	
	@Autowired
	private TempPayrollArchiveDao tpaDao;
	
	public long sellParts(List<ServiceItem> al_serviceItem, String a_bukrs,Long a_customer_id, String a_waers, Long a_branch_id, Long a_user_id, String a_tcode,double discount_summa,Date a_servDate) throws DAOException{
		try{
			Branch selectedBranch = new Branch();
			List<Bseg> l_bsegDebet = new ArrayList<Bseg>();
			Bkpf p_bkpf_GS = new Bkpf();
			List<ExchangeRate> exchageRate_list = new ArrayList<ExchangeRate>(); 
			
			if (a_waers == null || a_waers.isEmpty()){
				throw new DAOException("Select currency");
			}
			else
			{
				p_bkpf_GS.setWaers(a_waers);
			}
			exchageRate_list = exrateDao.getLastCurrencyRates();
			ExchangeRate selectedER = new ExchangeRate();
			for (ExchangeRate wa_er:exchageRate_list)
			{
				if (wa_er.getType()==2 && (wa_er.getBukrs()==null || wa_er.getBukrs().length()<1))
				{
					System.out.println("Exchange rate F4 internal company is null");
					throw new DAOException("Exchange rate F4 internal company is null");
				}
				
				if (wa_er.getType()==1 && wa_er.getSecondary_currency().equals(a_waers))
				{
					selectedER = wa_er;
					p_bkpf_GS.setKursf(selectedER.getSc_value());
				}
				
			}
			
			
			

			if (a_bukrs == null || a_bukrs.isEmpty())
			{
				throw new DAOException("Select company");
			} 
			else
			{
				p_bkpf_GS.setBukrs(a_bukrs);
			}

			if (a_branch_id==null)
			{
				throw new DAOException("Select branch");
			}
			else
			{
				selectedBranch = brDao.find(a_branch_id);
			}
			
			if (a_customer_id==null)
			{
				throw new DAOException("Select branch");
			}
			else
			{
				p_bkpf_GS.setCustomer_id(a_customer_id);
			}
			
			
			List<Bseg> l_bsegFinal = new ArrayList<Bseg>();	
			//Bseg p_bsegKredit = new Bseg();	
			Calendar cal = Calendar.getInstance();
			p_bkpf_GS.setBudat(cal.getTime());
			p_bkpf_GS.setBldat(a_servDate);
			p_bkpf_GS.setBlart("GS");
			p_bkpf_GS.setUsnam(a_user_id); 
			p_bkpf_GS.setTcode(a_tcode); 
			p_bkpf_GS.setDep(3L);
			p_bkpf_GS.setGjahr(cal.get(Calendar.YEAR)); 
			p_bkpf_GS.setMonat(cal.get(Calendar.MONTH)+1); 
			p_bkpf_GS.setBrnch(selectedBranch.getBranch_id());
			p_bkpf_GS.setBusiness_area_id(selectedBranch.getBusiness_area_id());
			p_bkpf_GS.setCpudt(cal.getTime());
			p_bkpf_GS.setAwtyp(1);
			
			
			/*p_bsegKredit.setBuzei(wa_buzei);
			p_bsegKredit.setHkont("60100013");
			p_bsegKredit.setBukrs(p_bkpf_GS.getBukrs());
			p_bsegKredit.setGjahr(p_bkpf_GS.getGjahr());
			p_bsegKredit.setBschl("50");
			p_bsegKredit.setShkzg("H");*/
			
			double dmbtr = 0;
			double wrbtr = 0;
			for(ServiceItem wa_si:al_serviceItem)
		  	{ 
				Bseg wa_bseg = new Bseg();
				wa_bseg.setBukrs(p_bkpf_GS.getBukrs());
				wa_bseg.setGjahr(p_bkpf_GS.getGjahr());
				wa_bseg.setBschl("1");
				wa_bseg.setShkzg("S");
				wa_bseg.setHkont("12100001");
				wa_bseg.setLifnr(p_bkpf_GS.getCustomer_id());
				
				if (wa_si.getMatnr() == null && wa_si.getUsluga()==0 && wa_si.getKolichestvo()==0)
			    {
			     throw new DAOException("Kolichestvo ne ukazano");
			    }
			    else
			    {
			     wa_bseg.setMenge(wa_si.getKolichestvo());
			    }
			    
			    if (wa_si.getMatnr() == null && wa_si.getUsluga()==0 && wa_si.getKolichestvo()==0)
			    {
			     throw new DAOException("Material ne vybran");
			    }
			    else
			    {
			     wa_bseg.setMatnr(wa_si.getMatnr());
			    }
			    
			    
			    if (wa_si.getUsluga()==1 && wa_si.getMatnr()==null)
			    {
			     wa_bseg.setMatnr(null);
			     wa_bseg.setMenge(0);
			     if (wa_si.summa==0)
			     {
			    	 //throw new DAOException("Usluga 0 ");
			    	 
			     }
			    }
				
				
				if (p_bkpf_GS.getWaers()!=null && p_bkpf_GS.getWaers().equals("USD") ){
					if (wa_si.getSumma()==0 ||wa_si.getSumma()<0){
						//throw new DAOException("Enter the amount");
					}
					else
					{
						wa_bseg.setDmbtr(wa_si.getSumma());
						p_bkpf_GS.setDmbtr(wa_bseg.getDmbtr()+p_bkpf_GS.getDmbtr());
						dmbtr = dmbtr + wa_bseg.getDmbtr();
					}
				}
				else if (p_bkpf_GS.getWaers()!=null  && (!p_bkpf_GS.getWaers().equals("USD")) ){
					if (wa_si.getSumma()==0 ||wa_si.getSumma()<0){
						//throw new DAOException("Enter the amount");
					}
					else
					{
						wa_bseg.setWrbtr(GeneralUtil.round(wa_si.getSumma(),2));
						wa_bseg.setDmbtr(GeneralUtil.round(wa_bseg.getWrbtr()/selectedER.getSc_value(), 2));
						dmbtr = dmbtr + wa_bseg.getDmbtr();
						wrbtr = wrbtr + wa_bseg.getWrbtr();
					}
				}
				
				l_bsegDebet.add(wa_bseg);
		  	}
			//p_bkpf_GS.setDmbtr(dmbtr);
			//p_bkpf_GS.setWrbtr(wrbtr);
			
			
			Bseg wa_bsegKredit = new Bseg();
			wa_bsegKredit.setBukrs(p_bkpf_GS.getBukrs());
			wa_bsegKredit.setGjahr(p_bkpf_GS.getGjahr());
			wa_bsegKredit.setHkont("60100013");
			
			wa_bsegKredit.setBschl("50");
			wa_bsegKredit.setShkzg("H");
			wa_bsegKredit.setDmbtr(dmbtr);
			wa_bsegKredit.setWrbtr(wrbtr);
			l_bsegFinal.add(wa_bsegKredit);
			for(Bseg wa_bseg:l_bsegDebet)
		  	{ 
				wa_bseg.setDmbtr(GeneralUtil.round(wa_bseg.getDmbtr(), 2));
				wa_bseg.setWrbtr(GeneralUtil.round(wa_bseg.getWrbtr(), 2));
				l_bsegFinal.add(wa_bseg);
		  	}
			
			if (discount_summa>0)
			{
				Bseg wa_bsegDebetDiscount = new Bseg();
				wa_bsegDebetDiscount.setBukrs(p_bkpf_GS.getBukrs());
				wa_bsegDebetDiscount.setGjahr(p_bkpf_GS.getGjahr());
				wa_bsegDebetDiscount.setBschl("40");
				wa_bsegDebetDiscount.setShkzg("S");
				wa_bsegDebetDiscount.setHkont("60300001");
				
				
				
				Bseg wa_bsegKreditDiscount = new Bseg();
				wa_bsegKreditDiscount.setBukrs(p_bkpf_GS.getBukrs());
				wa_bsegKreditDiscount.setGjahr(p_bkpf_GS.getGjahr());
				wa_bsegKreditDiscount.setHkont("12100001");				
				wa_bsegKreditDiscount.setBschl("15");
				wa_bsegKreditDiscount.setShkzg("H");
				wa_bsegKreditDiscount.setLifnr(p_bkpf_GS.getCustomer_id());
				
				if (p_bkpf_GS.getWaers()!=null && p_bkpf_GS.getWaers().equals("USD") ){
					wa_bsegDebetDiscount.setDmbtr(discount_summa);
					wa_bsegKreditDiscount.setDmbtr(discount_summa);
				}
				else if (p_bkpf_GS.getWaers()!=null  && (!p_bkpf_GS.getWaers().equals("USD")) ){
					wa_bsegDebetDiscount.setDmbtr(GeneralUtil.round(discount_summa/selectedER.getSc_value(), 2));
					wa_bsegDebetDiscount.setWrbtr(GeneralUtil.round(discount_summa,2));
					wa_bsegKreditDiscount.setDmbtr(GeneralUtil.round(discount_summa/selectedER.getSc_value(), 2));
					wa_bsegKreditDiscount.setWrbtr(GeneralUtil.round(discount_summa,2));
				}
				
				
				
				l_bsegFinal.add(wa_bsegDebetDiscount);
				l_bsegFinal.add(wa_bsegKreditDiscount);
			}
			
			int wa_buzei = 0;
			for(Bseg wa_bseg:l_bsegFinal)
		  	{ 
				wa_buzei = wa_buzei + 1;
				wa_bseg.setBuzei(wa_buzei);
				//System.out.println(wa_bseg.getDmbtr());
				//System.out.println(wa_bseg.getWrbtr());
		  	}

			
			
			//for(Bseg wa_bseg:l_bsegFinal)
		  		//{ 
			//	System.out.println(wa_bseg.getBukrs());
			//	System.out.println(wa_bseg.getDmbtr());
			//	System.out.println(wa_bseg.getWrbtr()); 
		  		//}

			
			Long newDocBelnr = financeService.createAccountReceivableDocs(p_bkpf_GS, l_bsegFinal, selectedBranch);
			return GeneralUtil.getPreparedAwkey(newDocBelnr, p_bkpf_GS.getGjahr());
		}
		catch (DAOException ex)
		{   
			throw new DAOException(ex.getMessage()); 
		}
	}
	public class ServiceItem {
		private Long matnr;
		private int usluga = 0;
		private double kolichestvo = 0;
		private double summa = 0;
		public Long getMatnr() {
			return matnr;
		}
		public void setMatnr(Long matnr) {
			this.matnr = matnr;
		}
		public int getUsluga() {
			return usluga;
		}
		public void setUsluga(int usluga) {
			this.usluga = usluga;
		}
		public double getSumma() {
			return summa;
		}
		public void setSumma(double summa) {
			this.summa = summa;
		}
		public double getKolichestvo() {
			return kolichestvo;
		}
		public void setKolichestvo(double kolichestvo) {
			this.kolichestvo = kolichestvo;
		}
		
	}
	public void cancelService(Long a_awkey,Long a_userID, String a_tcode, Long a_master_staff_id, Long a_master_position_id, 
			double a_summa_otmena_master, String a_currency_otmena, Date a_service_date, Long a_service_id,  Long a_operator_staff_id, Long a_operator_position_id, 
			double a_summa_otmena_oparator, String a_bukrs) throws DAOException
	{
		try{
			if (a_awkey!=null)
			{
				Calendar servCal = Calendar.getInstance();
				servCal.setTime(a_service_date);
				Bkpf p_bkpf_GS = new Bkpf();
				List<Bseg> l_bseg_GS = new ArrayList<Bseg>();
				p_bkpf_GS = bkpfDao.findOriginalSingleBkpf(GeneralUtil.getPreparedBelnr(a_awkey), GeneralUtil.getPreparedGjahr(a_awkey),a_bukrs);
				if (p_bkpf_GS.getStorno()==0)
				{
					l_bseg_GS = bsegDao.dynamicFindBseg(" bukrs='"+p_bkpf_GS.getBukrs()+"' and belnr = "+p_bkpf_GS.getBelnr()+" and gjahr="+p_bkpf_GS.getGjahr());
					financeService.cancelFiDoc(p_bkpf_GS, l_bseg_GS, a_userID, a_tcode);
					
					
					List<Bkpf> l_bkpf_DP = new ArrayList<Bkpf>();
					l_bkpf_DP =  bkpfDao.dynamicFindBkpf(" awkey = "+GeneralUtil.getPreparedAwkey(p_bkpf_GS.getBelnr(), p_bkpf_GS.getGjahr())+" and blart='DP' and storno=0");
					for(Bkpf wa_bkpf:l_bkpf_DP)
					{
						List<Bseg> wal_bseg_DP = new ArrayList<Bseg>();
						wal_bseg_DP = bsegDao.dynamicFindBseg(" bukrs='"+wa_bkpf.getBukrs()+"' and belnr = "+wa_bkpf.getBelnr()+" and gjahr="+wa_bkpf.getGjahr());
						financeService.cancelFiDoc(wa_bkpf, wal_bseg_DP, a_userID, a_tcode);
					}
					
					String dynamicWhereClause = "";
					dynamicWhereClause = dynamicWhereClause + " bukrs = '"+p_bkpf_GS.getBukrs()+"'";
					dynamicWhereClause = dynamicWhereClause + " and gjahr = '"+servCal.get(Calendar.YEAR)+"'"; 
					dynamicWhereClause = dynamicWhereClause + " and monat = '"+servCal.get(Calendar.MONTH)+1+"'";
					int count_arc = 0;
					count_arc = tpaDao.countDynamicSearch(dynamicWhereClause).intValue();
					if (count_arc>0 && a_currency_otmena!=null && a_currency_otmena.length()==3)
					{
						Calendar curDate = Calendar.getInstance();
						if (a_master_staff_id!=null && a_master_staff_id>0L && a_summa_otmena_master>0)
						{
							Payroll new_prl = new Payroll();						
							new_prl.setBukrs(p_bkpf_GS.getBukrs());
							new_prl.setGjahr(p_bkpf_GS.getGjahr());
							new_prl.setMonat(p_bkpf_GS.getMonat());				
							new_prl.setPayroll_date(curDate.getTime());
							new_prl.setBldat(curDate.getTime());
							new_prl.setApprove(0);
							new_prl.setCreated_by(a_userID);
							new_prl.setStaff_id(a_master_staff_id);
							new_prl.setPosition_id(a_master_position_id);
							new_prl.setAwkey(a_awkey);
							new_prl.setBldat(curDate.getTime());
							new_prl.setBranch_id(p_bkpf_GS.getBrnch());
							new_prl.setDrcrk("H");
							new_prl.setDmbtr(a_summa_otmena_master);
							new_prl.setWaers(a_currency_otmena);
							new_prl.setText45("Отмена сервиса "+a_service_id);
							prlSerDao.createNew(new_prl,a_userID,true,a_tcode,9);
						}
						if (a_operator_staff_id!=null && a_operator_staff_id>0L && a_summa_otmena_oparator>0)
						{
							Payroll new_prl = new Payroll();						
							new_prl.setBukrs(p_bkpf_GS.getBukrs());
							new_prl.setGjahr(p_bkpf_GS.getGjahr());
							new_prl.setMonat(p_bkpf_GS.getMonat());				
							new_prl.setPayroll_date(curDate.getTime());
							new_prl.setBldat(curDate.getTime());
							new_prl.setApprove(0);
							new_prl.setCreated_by(a_userID);
							new_prl.setStaff_id(a_operator_staff_id);
							new_prl.setPosition_id(a_operator_position_id);
							new_prl.setAwkey(a_awkey);
							new_prl.setBldat(curDate.getTime());
							new_prl.setBranch_id(p_bkpf_GS.getBrnch());
							new_prl.setDrcrk("H");
							new_prl.setDmbtr(a_summa_otmena_oparator);
							new_prl.setWaers(a_currency_otmena);
							new_prl.setText45("Отмена сервиса "+a_service_id);
							prlSerDao.createNew(new_prl,a_userID,true,a_tcode,6);
						}
						
					}
					
				}
				
			}
			
			
			
		}
		catch (DAOException ex)
		{   
			throw new DAOException(ex.getMessage()); 
		}
	}
	
	
}
