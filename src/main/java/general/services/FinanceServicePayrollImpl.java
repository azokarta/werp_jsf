package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.GeneralUtil;
import general.dao.BonusArchiveDao;
import general.dao.BonusDao;
import general.dao.BranchDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.ExchangeRateDao;
import general.dao.PayrollDao;
import general.dao.PyramidArchiveDao;
import general.dao.PyramidDao;
import general.dao.SalaryDao;
import general.dao.SalePlanArchiveDao;
import general.dao.SalePlanDao;
import general.dao.TempPayrollArchiveDao;
import general.dao.TempPayrollDao;
import general.tables.Bkpf;
import general.tables.Bonus;
import general.tables.BonusArchive;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Bsik;
import general.tables.ExchangeRate;
import general.tables.Payroll;
import general.tables.Pyramid;
import general.tables.PyramidArchive;
import general.tables.Salary;
import general.tables.SalePlan;
import general.tables.SalePlanArchive;
import general.tables.TempPayroll;
import general.tables.TempPayrollArchive;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("financeServicePayroll")
public class FinanceServicePayrollImpl implements FinanceServicePayroll{
			
	@Autowired
    private FinanceService financeService;

	@Autowired
    private PayrollDao prlDao;
	
	@Autowired
    private ExchangeRateDao erDao;
	
	@Autowired
    private TempPayrollDao tpDao; 
	
	@Autowired
	private SalePlanDao salePlanDao;
	
	@Autowired
	private SalePlanArchiveDao salePlanArcDao;
	
	@Autowired
	private PyramidDao pyramidDao;
	
	@Autowired
    private PyramidArchiveDao pyramidArcDao;
	
	@Autowired
	private TempPayrollArchiveDao tpaDao;
	
	@Autowired
    private BonusDao bonDao;
	
	@Autowired
    private BonusArchiveDao bonArcDao;
	
	@Autowired
	private SalaryDao slrDao;
	
	@Autowired
	private BranchDao brnDao;
	
	@Autowired
	private CustomerDao cusDao;

	/*@Autowired
    private BsegDao bsegDao;
	@Autowired
    private BsikDao bsikDao;		
	@Autowired
    private FmglflextDao fmglflextDao;	
	@Autowired
    private Fmglflext2Dao fmglflext2Dao;	
	@Autowired
    private TableIdLimitDao tableIdLimitDao;
	@Autowired
    private HkontDao hkontDao;	
	@Autowired
    private BschlDao bschlDao;
	@Autowired
    private PayrollDao payrollDao;
	@Autowired
    private StaffDao staffDao;
	@Autowired
    private SalaryDao salaryDao;*/
	
	int premium = 1;
	int bonus = 2;
	int wage = 3;
	int opv = 4;
	int ipn = 5;
	int socnal = 6;
	int deposit = 7;
	int skidka = 8;
	int otmena = 9;
	int rashody = 10;
	
	public boolean hrppPayrollPayments (String a_bukrs,  Long a_branch_id, Long a_customer_id, Long a_staff_id,
			String a_tcode, Long a_userId, double a_summa, String a_waers, String a_cashbankGLA,Long selectedAvansOdob_payroll_id,String a_bktxt ) throws DAOException{
		try{
			
			Calendar curDate = Calendar.getInstance();
			if (a_bukrs==null || a_bukrs.equals("0") || a_bukrs.length()==0){
				throw new DAOException("Выберите компанию");
			}
			if (a_waers==null || a_waers.equals("0") || a_waers.length()==0){
				throw new DAOException("Выберите валюту");
			}
			if (a_cashbankGLA==null || a_cashbankGLA.equals("0") || a_cashbankGLA.length()==0){
				throw new DAOException("Выберите счет фирмы");
			}
			if (a_branch_id == null)
			{
				throw new DAOException("Выберите филиал");
			}
			if (a_customer_id == null)
			{
				throw new DAOException("Выберите сотрудника");
			}
			if (a_staff_id == null)
			{
				throw new DAOException("Выберите сотрудника");
			}
			
			if (a_summa==0 || a_summa<0)
			{
				throw new DAOException("Сумма 0 или отрицательная");
			}
			
			double dmbtr = 0;
			double wrbtr = 0;
			double kursf = 1;
			
			if (!a_waers.equals("USD"))
			{
				List<ExchangeRate> l_er = new ArrayList<ExchangeRate>();
				l_er = erDao.getLastCurrencyRates();
				
				for (ExchangeRate wa_er:l_er)
				{
					if (wa_er.getSecondary_currency().equals(a_waers) && wa_er.getType()==1)
					{
						dmbtr = a_summa/wa_er.getSc_value();
						wrbtr = a_summa;
						kursf = wa_er.getSc_value();
						break;
					}
				}
			}
			else
			{
				dmbtr = a_summa;
				wrbtr = 0;
				kursf = 1;
			}
			dmbtr = GeneralUtil.round(dmbtr, 2);
			wrbtr = GeneralUtil.round(wrbtr, 2);
		
			
			
			String text60 = "";
			if (a_bktxt!=null && a_bktxt.length()>60)
			{
				text60 = a_bktxt.substring(0, 60);
			}
			
			
			Bkpf p_bkpf_SP = new Bkpf();			
			Bseg p_bseg_SP_kredit = new Bseg();
			Bseg p_bseg_SP_debet = new Bseg();

			
			
			p_bkpf_SP.setBukrs(a_bukrs);
			p_bkpf_SP.setBukrs(a_bukrs);
			p_bkpf_SP.setGjahr(curDate.get(Calendar.YEAR));
			p_bkpf_SP.setMonat(curDate.get(Calendar.MONTH)+1);
			p_bkpf_SP.setCustomer_id(a_customer_id);
			p_bkpf_SP.setTcode(a_tcode);
			p_bkpf_SP.setUsnam(a_userId);
			p_bkpf_SP.setBudat(curDate.getTime());
			p_bkpf_SP.setBldat(curDate.getTime());
			p_bkpf_SP.setWaers(a_waers);
			p_bkpf_SP.setDmbtr(dmbtr);
			p_bkpf_SP.setWrbtr(wrbtr);
			p_bkpf_SP.setKursf(kursf);
			p_bkpf_SP.setAwtyp(2);
			p_bkpf_SP.setCpudt(curDate.getTime());
			p_bkpf_SP.setBrnch(a_branch_id);
			p_bkpf_SP.setBlart("SP");
			p_bkpf_SP.setBktxt(text60);
			
			
			
			
			
			p_bseg_SP_debet.setBukrs(a_bukrs);
			p_bseg_SP_debet.setGjahr(curDate.get(Calendar.YEAR));
			p_bseg_SP_debet.setBuzei(1);
			p_bseg_SP_debet.setBschl("25");
			p_bseg_SP_debet.setHkont("33500001");
			p_bseg_SP_debet.setShkzg("S");
			p_bseg_SP_debet.setDmbtr(dmbtr);
			p_bseg_SP_debet.setWrbtr(wrbtr);
			p_bseg_SP_debet.setLifnr(a_customer_id);
			p_bseg_SP_debet.setSgtxt(text60);
			
			p_bseg_SP_kredit.setBukrs(a_bukrs);
			p_bseg_SP_kredit.setGjahr(curDate.get(Calendar.YEAR));
			p_bseg_SP_kredit.setBuzei(2);
			p_bseg_SP_kredit.setBschl("50");
			p_bseg_SP_kredit.setHkont(a_cashbankGLA);
			p_bseg_SP_kredit.setShkzg("H");
			p_bseg_SP_kredit.setDmbtr(dmbtr);
			p_bseg_SP_kredit.setWrbtr(wrbtr);
			
			List<Bseg> l_bseg_SP = new ArrayList<Bseg>();
			List<Bsik> l_bsik_SP = new ArrayList<Bsik>();
			
			l_bseg_SP.add(p_bseg_SP_debet);
			l_bseg_SP.add(p_bseg_SP_kredit);
			for (Bseg wa_bseg:l_bseg_SP)
			{
				Bsik wa_bsik = new Bsik();
				BeanUtils.copyProperties(wa_bseg, wa_bsik);
				l_bsik_SP.add(wa_bsik);
			}
			financeService.check_empty_fields(p_bkpf_SP, l_bseg_SP);
			financeService.insertNewFiDoc(p_bkpf_SP, l_bseg_SP, l_bsik_SP);
			
			Payroll wa_payroll = new Payroll();
			if (selectedAvansOdob_payroll_id>0)
			{
				wa_payroll = prlDao.find(selectedAvansOdob_payroll_id);
				wa_payroll.setApprove(0);
				wa_payroll.setText45(a_bktxt);
				wa_payroll.setBranch_id(a_branch_id);
				wa_payroll.setWaers(a_waers);
				wa_payroll.setBukrs(a_bukrs);
				if (p_bkpf_SP.getWaers().equals("USD"))
			    {
			    	wa_payroll.setDmbtr(p_bkpf_SP.getDmbtr());
			    }
			    else
			    {
			    	wa_payroll.setDmbtr(p_bkpf_SP.getWrbtr());
			    }
				wa_payroll.setBldat(p_bkpf_SP.getBudat());
				wa_payroll.setAwkey(GeneralUtil.getPreparedAwkey(p_bkpf_SP.getBelnr(), p_bkpf_SP.getGjahr()));
				prlDao.update(wa_payroll);
			}
			else
			{
				
	            wa_payroll.setWaers(a_waers);
			    wa_payroll.setBukrs(a_bukrs);
			    if (p_bkpf_SP.getWaers().equals("USD"))
			    {
			    	wa_payroll.setDmbtr(p_bkpf_SP.getDmbtr());
			    }
			    else
			    {
			    	wa_payroll.setDmbtr(p_bkpf_SP.getWrbtr());
			    }
			    wa_payroll.setBranch_id(a_branch_id);
			    wa_payroll.setDrcrk("H");
			    wa_payroll.setApprove(0);
			    wa_payroll.setStaff_id(a_staff_id);
			    wa_payroll.setGjahr(p_bkpf_SP.getGjahr());
			    wa_payroll.setMonat(p_bkpf_SP.getMonat());
			    wa_payroll.setPayroll_date(p_bkpf_SP.getBudat());
			    wa_payroll.setText45(a_bktxt);
			    wa_payroll.setBldat(p_bkpf_SP.getBudat());
			    wa_payroll.setAwkey(GeneralUtil.getPreparedAwkey(p_bkpf_SP.getBelnr(), p_bkpf_SP.getGjahr()));
			    prlDao.create(wa_payroll);
			    
			}
			p_bkpf_SP.setPayroll_id(wa_payroll.getPayroll_id());
		    financeService.updateFiDoc(p_bkpf_SP, null);
			
			
			
			/*
			if (wa_tp.getType()==3)
			{
				if (wa_tp.getPosition_id() == 3 || 
						wa_tp.getPosition_id() == 4 || wa_tp.getPosition_id() == 5 || 
						wa_tp.getPosition_id() == 6 || wa_tp.getPosition_id() == 8 || 
						wa_tp.getPosition_id() == 9 || wa_tp.getPosition_id() == 10)
				{
					p_bkpf.setBlart("SS");
					if (wa_tp.getAmount()>0) {wa_bsegKredit.setHkont("33500002");	wa_bsegDebet.setHkont("71100001");}
					else if (wa_tp.getAmount()<0) {wa_bsegKredit.setHkont("71100001");	wa_bsegDebet.setHkont("12100130");}
					
				}
				else
				{
					p_bkpf.setBlart("AS"); 
					if (wa_tp.getAmount()>0) {wa_bsegKredit.setHkont("33500001");	wa_bsegDebet.setHkont("72100001");}
					else if (wa_tp.getAmount()<0) {wa_bsegKredit.setHkont("72100001");	wa_bsegDebet.setHkont("12100130");}
				}
			}
			else if  (wa_tp.getType()==2)
			{
				p_bkpf.setBlart("BS");
				if (wa_tp.getAmount()>0) 
				{
					wa_bsegDebet.setHkont("71100001");
					if (wa_tp.getPosition_id() == 9){wa_bsegKredit.setHkont("33500011");}//БОНУС_ВЗНОСЩИКА					
					else if (wa_tp.getPosition_id() == 8){wa_bsegKredit.setHkont("33500012");}//БОНУС_ДЕМО_СЕКРЕТАРЯ
					else if (wa_tp.getPosition_id() == 4){wa_bsegKredit.setHkont("33500013");}//БОНУС_ДИЛЕРА
					else if (wa_tp.getPosition_id() == 10){wa_bsegKredit.setHkont("33500014");}//БОНУС_ДИРЕКТОРА
					else if (wa_tp.getPosition_id() == 5){wa_bsegKredit.setHkont("33500015");}//БОНУС_КООРДИНАТОРА
					else if (wa_tp.getPosition_id() == 3){wa_bsegKredit.setHkont("33500016");}//БОНУС_МЕНЕДЖЕРА
					else if (wa_tp.getPosition_id() == 12){wa_bsegKredit.setHkont("33500017");}//БОНУС_УЧИТЕЛЯ 
					else if (wa_tp.getPosition_id() == 15){wa_bsegKredit.setHkont("33500017");}//БОНУС_УЧИТЕЛЯ
					if (wa_tp.getAmount()<0) {wa_bsegKredit.setHkont("71100001");	wa_bsegDebet.setHkont("12100130");}
				}	
				else if (wa_tp.getAmount()<0)
				{	
					wa_bsegKredit.setHkont("71100001");	wa_bsegDebet.setHkont("12100130");
				}
			}
			else if  (wa_tp.getType()==1)
			{
				p_bkpf.setBlart("PS");
				if (wa_tp.getAmount()>0) 
				{
					wa_bsegDebet.setHkont("71100001");
					if (wa_tp.getPosition_id() == 8){wa_bsegKredit.setHkont("33500021");}//ПРЕМИЯ_ДЕМО_СЕКРЕТАРЯ
					else if (wa_tp.getPosition_id() == 4){wa_bsegKredit.setHkont("33500022");}//ПРЕМИЯ_ДИЛЕРА
					else if (wa_tp.getPosition_id() == 3){wa_bsegKredit.setHkont("33500016");}//БОНУС_МЕНЕДЖЕРА
					else if (wa_tp.getPosition_id() == 10){wa_bsegKredit.setHkont("33500023");}//ПРЕМИЯ_ДИРЕКТОРА
					else if (wa_tp.getPosition_id() == 13){wa_bsegKredit.setHkont("33500025");}//ПРЕМИЯ_ЗАМЕНЩИКА
					else if (wa_tp.getPosition_id() == 14){wa_bsegKredit.setHkont("33500026");}//ПРЕМИЯ_ЗАМЕНЩИКА_СЕКРЕТАРЯ
					else if (wa_tp.getPosition_id() == 11){wa_bsegKredit.setHkont("33500027");}//ПРЕМИЯ_УСТАНОВЩИКА
					else if (wa_tp.getPosition_id() == 12){wa_bsegKredit.setHkont("33500028");}//ПРЕМИЯ_УЧИТЕЛЯ 
					else if (wa_tp.getPosition_id() == 15){wa_bsegKredit.setHkont("33500028");}//ПРЕМИЯ_УЧИТЕЛЯ 
						

				}
				else if (wa_tp.getAmount()<0)
				{
					wa_bsegKredit.setHkont("71100001");	wa_bsegDebet.setHkont("12100130");
				}
			}
			*/
			/*List<Bsik> pl_bsik = new ArrayList<Bsik>();
			List<Bsid> pl_bsid = new ArrayList<Bsid>();
			String kreditorHkont = "";
			//String debitorHkont = "";
			if (a_bkpf.getWaers() == null || a_bkpf.getWaers().isEmpty())
			{
				throw new DAOException("Waers is empty");
			}
			if (a_bkpf.getBrnch()==null || a_bkpf.getBrnch() == 0){
				throw new DAOException("Branch is empty");
			}
			
			if (a_bkpf.getAwkey()==null || a_bkpf.getAwkey() == 0){
				throw new DAOException("Awkey is empty");
			}
			if (a_bkpf.getUsnam()==null || a_bkpf.getUsnam() == 0){
				throw new DAOException("User is empty");
			}
			if (a_bkpf.getAwtyp()!=2){
				throw new DAOException("Doc awtyp incorrect");
			}
			if (a_bkpf.getTcode() == null || a_bkpf.getTcode().isEmpty())
			{
				throw new DAOException("Transaction code is empty");
			}
			if (a_bkpf.getCustomer_id()== null || a_bkpf.getCustomer_id() == 0 ){
				throw new DAOException("Customer id is empty");
			}
			if (a_bkpf.getKursf() <= 0){
				throw new DAOException("Currency rate is empty");
			}
			if (a_bkpf.getDmbtr()==0 || a_bkpf.getDmbtr_paid()>0){
				throw new DAOException("USD amount not correct");
			}
			if (!a_bkpf.getWaers().equals("USD") && a_bkpf.getWrbtr() == 0){ 
				throw new DAOException(a_bkpf.getWaers()+" amount not correct"); 
			}
			if (a_staff_id==null || a_staff_id==0)
			{
				throw new DAOException("Выберите сотрудника");
			}
			kreditorHkont = "";
			for (Bseg wa_bseg: al_bseg)
			{
				if (a_bkpf.getWaers().equals("USD") && wa_bseg.getDmbtr() == 0){ 
					throw new DAOException("The amount in local currency is 0"); 
				}
				else if (wa_bseg.getWrbtr() == 0){					
					throw new DAOException("The amount in foreign currency is 0"); 
				}
				//System.out.println(wa_bseg.getBuzei()+" "+wa_bseg.getBschl());
				if (wa_bseg.getHkont().startsWith("33"))
				{
					kreditorHkont = wa_bseg.getHkont();
				}
				Bsik wa_bsik = new Bsik(); 
				wa_bsik.setBukrs(wa_bseg.getBukrs());
				wa_bsik.setGjahr(wa_bseg.getGjahr());
				wa_bsik.setBuzei(wa_bseg.getBuzei());
				wa_bsik.setBschl(wa_bseg.getBschl());
				wa_bsik.setHkont(wa_bseg.getHkont());
				wa_bsik.setShkzg(wa_bseg.getShkzg());
				wa_bsik.setDmbtr(wa_bseg.getDmbtr());
				wa_bsik.setWrbtr(wa_bseg.getWrbtr());
				wa_bsik.(wa_bseg.getLifnr());        
				pl_bsik.add(wa_bsik);
			}
			//Create fin docs
			financeService.check_empty_fields(a_bkpf, al_bseg);
			financeService.insertNewFiDoc(a_bkpf, al_bseg, pl_bsik, pl_bsid);
			//if (a_bkpf.getBlart().equals("SP")||a_bkpf.getBlart().equals("BP")||a_bkpf.getBlart().equals("PP"))
			//{
				Payroll wa_payroll = new Payroll();
	            wa_payroll.setWaers(a_bkpf.getWaers());
			    wa_payroll.setBukrs(a_bkpf.getBukrs());
			    if (a_bkpf.getWaers().equals("USD"))
			    {
			    	wa_payroll.setDmbtr(a_bkpf.getDmbtr());
			    }
			    else
			    {
			    	wa_payroll.setDmbtr(a_bkpf.getWrbtr());
			    }
			    wa_payroll.setDrcrk("H");
			    wa_payroll.setStaff_id(a_staff_id);
			    wa_payroll.setGjahr(a_bkpf.getGjahr());
			    wa_payroll.setMonat(a_bkpf.getMonat());
			    wa_payroll.setPayroll_date(a_bkpf.getBudat());
			    if (a_bkpf.getBlart().equals("SP"))wa_payroll.setText45("Salary");
			    else if (a_bkpf.getBlart().equals("BP"))wa_payroll.setText45("Bonus");
			    else if (a_bkpf.getBlart().equals("PP"))wa_payroll.setText45("Premium");

			    wa_payroll.setText45("Avans");
			    prlDao.create(wa_payroll);
			    a_bkpf.setPayroll_id(wa_payroll.getPayroll_id());
			    financeService.updateFiDoc(a_bkpf, null);
			//}
			String dynamicWhereClause = "";
			if (a_bkpf_old.getWaers().equals("USD"))
			{
				dynamicWhereClause = " dmbtr_paid = dmbtr_paid +" + a_bkpf.getDmbtr();
			}
			else
			{
				dynamicWhereClause = " wrbtr_paid = wrbtr_paid +" + a_bkpf.getWrbtr() +", dmbtr_paid = dmbtr_paid +" + a_bkpf.getDmbtr();
			}
			
            if (bkpfDao.updateDynamicSingleBkpf(a_bkpf_old.getBelnr(), a_bkpf_old.getGjahr(), dynamicWhereClause)!=1)
        	{
        		throw new DAOException("Account Recievable not updated");
        	}
            dynamicWhereClause = "";
            dynamicWhereClause = " belnr = "+a_bkpf_old.getBelnr()+" and gjahr = "+a_bkpf_old.getGjahr();
            Bkpf wa_bkpf = bkpfDao.dynamicFindSingleBkpf(dynamicWhereClause);
            if ((wa_bkpf.getWaers().equals("USD") && wa_bkpf.getDmbtr()==wa_bkpf.getDmbtr_paid()) ||
	            	(!wa_bkpf.getWaers().equals("USD") && wa_bkpf.getWrbtr()==wa_bkpf.getWrbtr_paid()))	
	            {
            		wa_bkpf.setClosed(1);
	            	//If it is last payment
	            	if (!wa_bkpf.getWaers().equals("USD") && wa_bkpf.getDmbtr()!= wa_bkpf.getDmbtr_paid())
					{	
						Bkpf wa_bkpf4 = new Bkpf();
						List<Bseg> wal_bseg4 = new ArrayList<Bseg>();
						List<Bsid> wal_bsid4 = new ArrayList<Bsid>();
						List<Bsik> wal_bsik4 = new ArrayList<Bsik>();
						wa_bkpf4.setBukrs(a_bkpf.getBukrs());
						wa_bkpf4.setGjahr(a_bkpf.getGjahr()); 
						wa_bkpf4.setBlart("ED");
						wa_bkpf4.setBudat(a_bkpf.getBudat());
						wa_bkpf4.setBldat(a_bkpf.getBldat());
			            wa_bkpf4.setMonat(a_bkpf.getMonat());
			            wa_bkpf4.setCpudt(a_bkpf.getCpudt());
			            wa_bkpf4.setUsnam(a_bkpf.getUsnam());
			            wa_bkpf4.setTcode(a_bkpf.getTcode());
			            wa_bkpf4.setWaers("USD");
			            wa_bkpf4.setKursf(a_bkpf.getKursf()); 
			            wa_bkpf4.setBrnch(a_bkpf.getBrnch());
						wa_bkpf4.setContract_number(a_bkpf.getContract_number()); 
						wa_bkpf4.setCustomer_id(a_bkpf.getCustomer_id());
						wa_bkpf4.setAwtyp(a_bkpf.getAwtyp());
						wa_bkpf4.setAwkey(a_bkpf.getAwkey());
						wa_bkpf4.setAwkey2(a_bkpf.getAwkey2());
						Bseg wa_bsegD = new Bseg();
						Bseg wa_bsegK = new Bseg();
						wa_bsegD.setBukrs(wa_bkpf4.getBukrs());
						wa_bsegD.setGjahr(wa_bkpf4.getGjahr());
						wa_bsegK.setBukrs(wa_bkpf4.getBukrs());
						wa_bsegK.setGjahr(wa_bkpf4.getGjahr());
						if (wa_bkpf.getDmbtr()>wa_bkpf.getDmbtr_paid())
						{	
							wa_bkpf4.setDmbtr(wa_bkpf.getDmbtr()- wa_bkpf.getDmbtr_paid());
							wa_bsegD.setBuzei(1);
							wa_bsegD.setBschl("40");
							wa_bsegD.setShkzg("S");					
							wa_bsegD.setHkont("74300006");
							wa_bsegD.setDmbtr(wa_bkpf.getDmbtr()- wa_bkpf.getDmbtr_paid());
							wal_bseg4.add(wa_bsegD);
							
							wa_bsegK.setBuzei(2);
							wa_bsegK.setBschl("50");
							wa_bsegK.setShkzg("H");
							wa_bsegK.setHkont(kreditorHkont);							
							wa_bsegK.setLifnr(wa_bkpf4.getCustomer_id());
							wa_bsegK.setDmbtr(wa_bkpf.getDmbtr()- wa_bkpf.getDmbtr_paid());
							wal_bseg4.add(wa_bsegK);
						}
						else
						{
							wa_bkpf4.setDmbtr(wa_bkpf.getDmbtr_paid() - wa_bkpf.getDmbtr());
							wa_bsegD.setBuzei(1);
							wa_bsegD.setBschl("40");
							wa_bsegD.setShkzg("S");
							wa_bsegD.setHkont(kreditorHkont);
							wa_bsegD.setDmbtr(wa_bkpf.getDmbtr_paid() - wa_bkpf.getDmbtr());
							wal_bseg4.add(wa_bsegD);
							
							wa_bsegK.setBuzei(2);	
							wa_bsegK.setBschl("50");
							wa_bsegK.setShkzg("H");
							wa_bsegK.setHkont("");				
							wa_bsegK.setHkont("62500006");
							wa_bsegK.setDmbtr(wa_bkpf.getDmbtr_paid() - wa_bkpf.getDmbtr());
							wal_bseg4.add(wa_bsegK);
						}
						//Create fin docs
						financeService.check_empty_fields(wa_bkpf4, wal_bseg4);
						financeService.insertNewFiDoc(wa_bkpf4, wal_bseg4, wal_bsik4, wal_bsid4);
						
					}
	        }
            
            
			System.out.println("*******************************************");
			System.out.println("Payroll");
			System.out.println(a_bkpf_old.getBelnr() + " " +a_bkpf_old.getGjahr()  + " " + a_bkpf_old.getDmbtr()  + " " + a_bkpf_old.getDmbtr_paid()  + " " + 
					a_bkpf_old.getWrbtr() + " " + a_bkpf_old.getWrbtr_paid());
			System.out.println(a_bkpf.getBelnr() + " " +a_bkpf.getGjahr()  + " " + a_bkpf.getDmbtr()  + " " + a_bkpf.getDmbtr_paid()  + " " + 
					a_bkpf.getWrbtr() + " " + a_bkpf.getWrbtr_paid());
			for (Bseg wa_bseg: al_bseg)
			{
				System.out.print(wa_bseg.getBuzei());
				System.out.print(" "+wa_bseg.getBschl());
				System.out.print(" "+wa_bseg.getHkont());
				System.out.print(" "+wa_bseg.getShkzg());
				System.out.print(" "+wa_bseg.getDmbtr());
				System.out.println(" "+wa_bseg.getWrbtr());
			}
			System.out.println("*******************************************");
			System.out.println("*******************************************");
			System.out.println("Avans");
			for (HrppPayrollTable wa_hpt:al_hpt)
			{
				
				Bkpf p_bkpf2 = new Bkpf();
				Bkpf p_bkpf2_old = new Bkpf();
				List<Bseg> pl_bseg2 = new ArrayList<Bseg>(); 
				List<Bsid> pl_bsid2 = new ArrayList<Bsid>();
				List<Bsik> pl_bsik2 = new ArrayList<Bsik>();
				p_bkpf2 = wa_hpt.getP_bkpf_avans_new();
				//p_bkpf2 = Clone.cloneBkpf(wa_hpt.getP_bkpf_avans_new());
				p_bkpf2_old = wa_hpt.getP_bkpf_avans_old();
	
				if (p_bkpf2.getWaers() == null || p_bkpf2.getWaers().isEmpty())
				{
					throw new DAOException("Waers is empty");
				}
				if (p_bkpf2.getBrnch()==null || p_bkpf2.getBrnch() == 0){
					throw new DAOException("Branch is empty");
				}
				if (p_bkpf2.getAwkey()==null || p_bkpf2.getAwkey() == 0){
					throw new DAOException("Awkey is empty");
				}
				if (p_bkpf2.getUsnam()==null || p_bkpf2.getUsnam() == 0){
					throw new DAOException("User is empty");
				}
				if (p_bkpf2.getAwtyp()!=1){
					throw new DAOException("Doc awtyp incorrect");
				}
				if (p_bkpf2.getTcode() == null || p_bkpf2.getTcode().isEmpty())
				{
					throw new DAOException("Transaction code is empty");
				}
				if (p_bkpf2.getCustomer_id()== null || p_bkpf2.getCustomer_id() == 0 ){
					throw new DAOException("Customer id is empty");
				}
				if (p_bkpf2.getKursf() <= 0){
					throw new DAOException("Currency rate is empty");
				}
				if (p_bkpf2.getDmbtr()==0 || p_bkpf2.getDmbtr_paid()>0){
					throw new DAOException("USD amount not correct");
				}
				if (!p_bkpf2.getWaers().equals("USD") && p_bkpf2.getWrbtr() == 0){ 
					throw new DAOException(p_bkpf2.getWaers()+" amount not correct"); 
				}
				pl_bseg2 = wa_hpt.getL_bseg_avans_new();
				//pl_bseg2 = Clone.cloneLBseg(pl_bseg2);
				//double p_dmbtr = 0;
				//double p_wrbtr = 0;
				//String debitor_hkont = "";
				//Long matnr = null;
				//debitorHkont = "";
				for (Bseg wa_bseg: pl_bseg2)
				{
					if (p_bkpf2.getWaers().equals("USD") && wa_bseg.getDmbtr() == 0){ 
						throw new DAOException("The amount in local currency is 0"); 
					}
					else if (wa_bseg.getWrbtr() == 0){					
						throw new DAOException("The amount in foreign currency is 0"); 
					}
					if (wa_bseg.getHkont().startsWith("121"))
					{
						//debitorHkont = wa_bseg.getHkont();
					}
					//System.out.println(wa_bseg.getBuzei()+" "+wa_bseg.getBschl());
					Bsid wa_bsid2 = new Bsid(); 
					wa_bsid2.setBukrs(wa_bseg.getBukrs());
					wa_bsid2.setGjahr(wa_bseg.getGjahr());
					wa_bsid2.setBuzei(wa_bseg.getBuzei());
					wa_bsid2.setBschl(wa_bseg.getBschl());
					wa_bsid2.setHkont(wa_bseg.getHkont());
					wa_bsid2.setShkzg(wa_bseg.getShkzg());
					wa_bsid2.setDmbtr(wa_bseg.getDmbtr());
					wa_bsid2.setWrbtr(wa_bseg.getWrbtr());
					wa_bsid2.setLifnr(wa_bseg.getKunnr());       
	            	pl_bsid2.add(wa_bsid2);
				}
				System.out.println(p_bkpf2_old.getBelnr() + " " +p_bkpf2_old.getGjahr()  + " " + p_bkpf2_old.getDmbtr()  + " " + p_bkpf2_old.getDmbtr_paid()  + " " + 
						p_bkpf2_old.getWrbtr() + " " + p_bkpf2_old.getWrbtr_paid());
				System.out.println(p_bkpf2.getBelnr() + " " +p_bkpf2.getGjahr()  + " " + p_bkpf2.getDmbtr()  + " " + p_bkpf2.getDmbtr_paid()  + " " + 
						p_bkpf2.getWrbtr() + " " + p_bkpf2.getWrbtr_paid());
				for (Bseg wa_bseg: pl_bseg2)
				{
					System.out.print(wa_bseg.getBuzei());
					System.out.print(" "+wa_bseg.getBschl());
					System.out.print(" "+wa_bseg.getHkont());
					System.out.print(" "+wa_bseg.getShkzg());
					System.out.print(" "+wa_bseg.getDmbtr());
					System.out.println(" "+wa_bseg.getWrbtr());
				}
				//System.out.println(pl_bseg2.size());
				System.out.println("*********");
				//Create fin docs
				financeService.check_empty_fields(p_bkpf2, pl_bseg2);
				financeService.insertNewFiDoc(p_bkpf2, pl_bseg2, pl_bsik2, pl_bsid2);
				//if (p_bkpf2.getBlart().equals("SP")||p_bkpf2.getBlart().equals("BP")||p_bkpf2.getBlart().equals("PP"))
				//{
					Payroll wa_payroll2 = new Payroll();
					wa_payroll2.setWaers(p_bkpf2.getWaers());
					wa_payroll2.setBukrs(p_bkpf2.getBukrs());
				    if (p_bkpf2.getWaers().equals("USD"))
				    {
				    	wa_payroll2.setDmbtr(p_bkpf2.getDmbtr());
				    }
				    else
				    {
				    	wa_payroll2.setDmbtr(p_bkpf2.getWrbtr());
				    }
				    wa_payroll2.setDrcrk("S");
				    wa_payroll2.setStaff_id(a_staff_id);
				    wa_payroll2.setGjahr(p_bkpf2.getGjahr());
				    wa_payroll2.setMonat(p_bkpf2.getMonat());
				    wa_payroll2.setPayroll_date(p_bkpf2.getBudat());
				    wa_payroll2.setText45("Avans");
				    //prlDao.create(wa_payroll2);
				    //a_bkpf.setPayroll_id(wa_payroll2.getPayroll_id());
				    //financeService.updateFiDoc(p_bkpf2, null);
				//}
				
				dynamicWhereClause = "";
				if (a_bkpf_old.getWaers().equals("USD"))
				{
					dynamicWhereClause = " dmbtr_paid = dmbtr_paid +" + p_bkpf2.getDmbtr();
				}
				else
				{
					dynamicWhereClause = " wrbtr_paid = wrbtr_paid +" + p_bkpf2.getWrbtr() +", dmbtr_paid = dmbtr_paid +" + p_bkpf2.getDmbtr();
				}
				
	            if (bkpfDao.updateDynamicSingleBkpf(p_bkpf2_old.getBelnr(), p_bkpf2_old.getGjahr(), dynamicWhereClause)!=1)
	        	{
	        		throw new DAOException("Account Recievable not updated");
	        	}
	            dynamicWhereClause = "";
	            dynamicWhereClause = " belnr = "+p_bkpf2_old.getBelnr()+" and gjahr = "+p_bkpf2_old.getGjahr();
	            Bkpf wa_bkpfLast = bkpfDao.dynamicFindSingleBkpf(dynamicWhereClause);
	            if ((wa_bkpfLast.getWaers().equals("USD") && wa_bkpfLast.getDmbtr()==wa_bkpfLast.getDmbtr_paid()) ||
		            	(!wa_bkpfLast.getWaers().equals("USD") && wa_bkpfLast.getWrbtr()==wa_bkpfLast.getWrbtr_paid()))	
		            {
		            	//If it is last payment
		            	if (!wa_bkpfLast.getWaers().equals("USD") && wa_bkpfLast.getDmbtr()!= wa_bkpfLast.getDmbtr_paid())
						{	
							Bkpf wa_bkpf4 = new Bkpf();
							List<Bseg> wal_bseg4 = new ArrayList<Bseg>();
							List<Bsid> wal_bsid4 = new ArrayList<Bsid>();
							List<Bsik> wal_bsik4 = new ArrayList<Bsik>();
							wa_bkpf4.setBukrs(p_bkpf2.getBukrs());
							wa_bkpf4.setGjahr(p_bkpf2.getGjahr()); 
							wa_bkpf4.setBlart("ED");
							wa_bkpf4.setBudat(p_bkpf2.getBudat());
							wa_bkpf4.setBldat(p_bkpf2.getBldat());
				            wa_bkpf4.setMonat(p_bkpf2.getMonat());
				            wa_bkpf4.setCpudt(p_bkpf2.getCpudt());
				            wa_bkpf4.setUsnam(p_bkpf2.getUsnam());
				            wa_bkpf4.setTcode(p_bkpf2.getTcode());
				            wa_bkpf4.setWaers("USD");
				            wa_bkpf4.setKursf(p_bkpf2.getKursf()); 
				            wa_bkpf4.setBrnch(p_bkpf2.getBrnch());
							wa_bkpf4.setContract_number(p_bkpf2.getContract_number()); 
							wa_bkpf4.setCustomer_id(p_bkpf2.getCustomer_id());
							wa_bkpf4.setAwtyp(p_bkpf2.getAwtyp());
							wa_bkpf4.setAwkey(p_bkpf2.getAwkey());
							wa_bkpf4.setAwkey2(p_bkpf2.getAwkey2());
							Bseg wa_bsegD = new Bseg();
							Bseg wa_bsegK = new Bseg();
							wa_bsegD.setBukrs(wa_bkpf4.getBukrs());
							wa_bsegD.setGjahr(wa_bkpf4.getGjahr());
							wa_bsegK.setBukrs(wa_bkpf4.getBukrs());
							wa_bsegK.setGjahr(wa_bkpf4.getGjahr());
							if (wa_bkpfLast.getDmbtr()>wa_bkpfLast.getDmbtr_paid())
							{	
								wa_bkpf4.setDmbtr(wa_bkpfLast.getDmbtr()- wa_bkpfLast.getDmbtr_paid());
								wa_bsegD.setBuzei(1);
								wa_bsegD.setBschl("40");
								wa_bsegD.setShkzg("S");					
								wa_bsegD.setHkont("74300006");
								wa_bsegD.setDmbtr(wa_bkpfLast.getDmbtr()- wa_bkpfLast.getDmbtr_paid());
								wal_bseg4.add(wa_bsegD);
								
								wa_bsegK.setBuzei(2);
								wa_bsegK.setBschl("50");
								wa_bsegK.setShkzg("H");
								wa_bsegK.setHkont(kreditorHkont);							
								wa_bsegK.setLifnr(wa_bkpf4.getCustomer_id());
								wa_bsegK.setDmbtr(wa_bkpfLast.getDmbtr()- wa_bkpfLast.getDmbtr_paid());
								wal_bseg4.add(wa_bsegK);
							}
							else
							{
								wa_bkpf4.setDmbtr(wa_bkpfLast.getDmbtr_paid() - wa_bkpfLast.getDmbtr());
								wa_bsegD.setBuzei(1);
								wa_bsegD.setBschl("40");
								wa_bsegD.setShkzg("S");
								wa_bsegD.setHkont(kreditorHkont);
								wa_bsegD.setDmbtr(wa_bkpfLast.getDmbtr_paid() - wa_bkpfLast.getDmbtr());
								wal_bseg4.add(wa_bsegD);
								
								wa_bsegK.setBuzei(2);	
								wa_bsegK.setBschl("50");
								wa_bsegK.setShkzg("H");
								wa_bsegK.setHkont("");				
								wa_bsegK.setHkont("62500006");
								wa_bsegK.setDmbtr(wa_bkpfLast.getDmbtr_paid() - wa_bkpfLast.getDmbtr());
								wal_bseg4.add(wa_bsegK);
							}
							//Create fin docs
							financeService.check_empty_fields(wa_bkpf4, wal_bseg4);
							financeService.insertNewFiDoc(wa_bkpf4, wal_bseg4, wal_bsik4, wal_bsid4);
						}
		        }
			}	 *///
			System.out.println("*******************************************");	
			return true;
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	public void createSalaryPayments(List<TempPayroll> al_tp, String a_bukrs, int a_gjahr, int a_monat, Long a_userId) throws DAOException{
		try
		{				
			System.out.println("financeservice");
			
			if (a_gjahr==0)
			{
				throw new DAOException("Year not selected. Finance Service(Salary)");
			}
			if (a_bukrs==null || a_bukrs.length()<4){
				throw new DAOException("Company not selected. Finance Service(Salary)");
			}
			if (a_monat<1 || a_monat>12){
				throw new DAOException("Month not selected. Finance Service(Salary)");
			}
			int count_arc = 0;
			//List<TempPayrollArchive> l_tpa = new ArrayList<TempPayrollArchive>();
			String dynamicWhereClause = "";
			dynamicWhereClause = dynamicWhereClause + " bukrs = '"+a_bukrs+"'";
			dynamicWhereClause = dynamicWhereClause + " and gjahr = '"+a_gjahr+"'"; 
			dynamicWhereClause = dynamicWhereClause + " and monat = '"+a_monat+"'";
			count_arc = tpaDao.countDynamicSearch(dynamicWhereClause).intValue();
			
			/*
			private String bukrs;
			private int gjahr; 
			private int monat;
			private Long branch_id;
			private Long staff_id;
			private String waers;
			private Long bonus_id;
			private Long salary_id;
			private Long position_id;
			private int matnr_count;
			private double plan_amount;
			private double fact_amount;
			private Date bldat;
			private Long contract_number;
			
			
			private double dmbtr; -----------
			private int bonus_type_id;-------------
			private Long payroll_id;
			private String drcrk;------------
			private String text45;	------------
			private Date payroll_date;------------*/
			
			
			//Calendar curDate = Calendar.getInstance();
			
			List<ExchangeRate> l_er = new ArrayList<ExchangeRate>();
			Map<String,ExchangeRate> l_er_map = new HashMap<String,ExchangeRate>();
			l_er = erDao.getLastCurrencyRates();
			
			for (ExchangeRate wa_er:l_er)
			{
				if (wa_er.getType()==1)
				{
					l_er_map.put(wa_er.getSecondary_currency(), wa_er);
				}
			}
			
			//Map<Long,List<TempPayroll>> l_tp_map = new HashMap<Long,List<TempPayroll>>();
			Calendar firstDay = Calendar.getInstance(); 
			Calendar lastDay = Calendar.getInstance();			  
			firstDay.set(a_gjahr, a_monat-1, 1);
			lastDay.set(a_gjahr, a_monat-1, firstDay.getActualMaximum(Calendar.DAY_OF_MONTH));
			
			Map<Long,Branch> l_brn_map = new  HashMap<Long,Branch>();
			for(Branch wa_brn:brnDao.findByBukrs(a_bukrs))
			{
				l_brn_map.put(wa_brn.getBranch_id(), wa_brn);
			}
			List<Salary> l_salary = new ArrayList<Salary>(); 
			l_salary = slrDao.findByBukrs (GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay), a_bukrs);
			Map<Long,Salary> l_sal_brn_map = new HashMap<Long,Salary>();
			for(Salary wa_salary: l_salary){
				l_sal_brn_map.put(wa_salary.getStaff_id(), wa_salary);
			}	
			
			Map<String,ZpTotal> l_zptotal_map = new HashMap<String,ZpTotal>();
			
			for (TempPayroll wa_tp:al_tp)
			{ 
				if (wa_tp.getDrcrk()==null)
				{
					throw new DAOException("Drcrk null");
				}
				if (wa_tp.getStaff_id().equals(793L))
				{
					System.out.println("STOP");
				}
				
				Payroll wa_prl = new Payroll();
				BeanUtils.copyProperties(wa_tp, wa_prl);
				wa_prl.setBonus_type_id(wa_tp.getType());
				//wa_prl.setDrcrk("S");
				wa_prl.setApprove(0);
				wa_prl.setPayroll_date(lastDay.getTime());
				if (wa_tp.getType()==7)
				{
					wa_prl.setBonus_type_id(7);
					wa_prl.setApprove(1);
				}	
				else if (wa_tp.getType()==4 || wa_tp.getType()==5 ||wa_tp.getType()==6)
				{
					//wa_prl.setDrcrk("H");
				}
				else if (wa_tp.getType()==13)
				{	
					wa_prl.setDrcrk("H");
					wa_prl.setApprove(8);
				}
			
				if (wa_tp.getStaff_id()==null || wa_tp.getStaff_id().equals("0")){
					throw new DAOException("Employee not selected. Payroll Service");
				}
				if (wa_tp.getBranch_id()==null){
					throw new DAOException("Branch not selected. Payroll Service, Staff id = " + wa_tp.getStaff_id());
				}
				
				wa_prl.setDmbtr(Math.abs(wa_tp.getAmount()));
			    
			    if (wa_tp.getText45()!=null && wa_tp.getText45().length()>0)
			    {
			    	wa_prl.setText45(wa_tp.getText45());
			    }
			    
			    ZpTotal wa_zp = l_zptotal_map.get(String.valueOf(wa_prl.getStaff_id())+wa_prl.getWaers());
			    if(wa_tp.getStaff_id().equals(13247L))
		    	{
		    		System.out.println("here");
		    	}
			    
			    if (wa_zp==null || wa_zp.getCustomer_id()==null)
			    {
			    	wa_zp = new ZpTotal();
			    	//Long branch_id = 0L;
			    	Salary wa_salary = l_sal_brn_map.get(wa_prl.getStaff_id());
			    	if (wa_salary==null)
			    	{
			    		if (wa_prl.getStaff_id().equals(13020L)) { wa_salary = slrDao.find(6330L); }
			    		else
			    		{
			    			throw new DAOException("У сотрудника отсутствует должность. ID:"+wa_prl.getStaff_id());
			    		}
			    		
			    		//continue;
			    	}
			    	else
			    	{
			    		wa_zp.setBranch_id(wa_salary.getBranch_id());
			    	}
			    	
			    	wa_zp.setBukrs(wa_prl.getBukrs());
			    	wa_zp.setCustomer_id(wa_salary.getP_staff().getCustomer_id());
			    	wa_zp.setWaers(wa_tp.getWaers());
			    	
			    	if (wa_prl.getDrcrk().equals("S"))
			    	{
			    		wa_zp.setZp_amount(wa_prl.getDmbtr());
			    	}
			    	else
			    	{
			    		if (wa_prl.getApprove()==8)
			    		{
			    			ExpAll wa_ea = new ExpAll();
				    		wa_ea.setExp_cus_id(null);
				    		wa_ea.setExp_amount(wa_prl.getDmbtr());
				    		wa_ea.setWaers(wa_prl.getWaers());
				    		wa_ea.setType(wa_tp.getType());
			    			wa_zp.setDolg(wa_ea);
			    		}
			    		else
			    		{
			    			ExpAll wa_ea = new ExpAll();
				    		wa_ea.setExp_cus_id(wa_tp.getExp_cus_id());
				    		wa_ea.setExp_amount(wa_prl.getDmbtr());
				    		wa_ea.setWaers(wa_prl.getWaers());
				    		wa_ea.setType(wa_tp.getType());
				    		wa_zp.getL_ea().add(wa_ea);
			    		}
			    		
			    	}
			    	wa_zp.getL_prl().add(wa_prl);
			    	l_zptotal_map.put(String.valueOf(wa_prl.getStaff_id())+wa_prl.getWaers(), wa_zp);
			    }
			    else
			    {
			    	if (wa_prl.getDrcrk().equals("S"))
			    	{
			    		wa_zp.setZp_amount(wa_zp.getZp_amount()+wa_prl.getDmbtr());
			    	}
			    	else
			    	{
			    		if (wa_prl.getApprove()==8)
			    		{
			    			ExpAll wa_ea = new ExpAll();
				    		wa_ea.setExp_cus_id(null);
				    		wa_ea.setExp_amount(wa_prl.getDmbtr());
				    		wa_ea.setWaers(wa_prl.getWaers());
				    		wa_ea.setType(wa_tp.getType());
			    			wa_zp.setDolg(wa_ea);
			    		}
			    		else
			    		{
			    			ExpAll wa_ea = new ExpAll();
				    		wa_ea.setExp_cus_id(wa_tp.getExp_cus_id());
				    		wa_ea.setExp_amount(wa_prl.getDmbtr());
				    		wa_ea.setWaers(wa_prl.getWaers());
				    		wa_ea.setType(wa_tp.getType());
				    		wa_zp.getL_ea().add(wa_ea);
			    		}
			    	}
			    	wa_zp.getL_prl().add(wa_prl);
			    }
			    
			    //prlDao.create(wa_prl);
			    
			    
			   
			}
			
			//Map<String,ZpTotal>
			for (Map.Entry<String, ZpTotal> entry : l_zptotal_map.entrySet()) {
		    	//key_long = null;
		    	ZpTotal wa_zp = new ZpTotal();
		    	wa_zp = (ZpTotal) entry.getValue();
		    	Long a_staff_id = null;
		    	for(Payroll wa_prl:wa_zp.getL_prl())
		    	{
		    		a_staff_id = wa_prl.getStaff_id();
		    		
		    	}
		    	if (a_staff_id.equals(19372L))
				{
	    			System.out.println("STOP");	  //5130,793  			
				}
	    		//else continue;
		    	System.out.println(a_staff_id+" staff_id");
		    	//System.out.println("Customer id:"+wa_zp.getCustomer_id());
		    	if (!wa_zp.getWaers().equals("USD"))
				{
			    	ExchangeRate wa_er = new ExchangeRate();
			    	wa_er = l_er_map.get(wa_zp.getWaers());
			    	if (wa_er==null || wa_er.getSecondary_currency()==null || wa_er.getSecondary_currency().length()==0)
			    	{
			    		throw new DAOException("Курс валют не определен."+" "+wa_zp.getWaers());
			    	}
			    	else
			    	{
			    		if (wa_zp.getDolg()!=null && wa_zp.getDolg().getExp_amount()>0)
			    		{
			    			//Dolg
			    			insertSalaryFi(wa_zp.getBukrs(),a_userId,wa_zp.getBranch_id(), wa_zp.getWaers(), wa_zp.getDolg().getExp_amount(), wa_er.getSc_value(), wa_zp.getCustomer_id(), "H",null, l_brn_map,"HRPL",8);
			    		}
			    		//Nachislenie
			    		wa_zp.setAwkey(insertSalaryFi(wa_zp.getBukrs(),a_userId,wa_zp.getBranch_id(), wa_zp.getWaers(), wa_zp.getZp_amount(), wa_er.getSc_value(), wa_zp.getCustomer_id(), "S",wa_zp.getL_ea(), l_brn_map,"HRPL",6));
			    	}
				}
				else
				{
					if (wa_zp.getDolg()!=null && wa_zp.getDolg().getExp_amount()>0)
		    		{
						//Dolg
		    			insertSalaryFi(wa_zp.getBukrs(),a_userId,wa_zp.getBranch_id(), wa_zp.getWaers(), wa_zp.getDolg().getExp_amount(), 1, wa_zp.getCustomer_id(), "H",null, l_brn_map,"HRPL",8);
		    		}
					//Nachislenie
					wa_zp.setAwkey(insertSalaryFi(wa_zp.getBukrs(),a_userId,wa_zp.getBranch_id(), wa_zp.getWaers(), wa_zp.getZp_amount(), 1, wa_zp.getCustomer_id(), "S",wa_zp.getL_ea(), l_brn_map,"HRPL",6));
				}
		    	for(Payroll wa_prl:wa_zp.getL_prl())
		    	{
		    		wa_prl.setAwkey(wa_zp.getAwkey());
		    		if (wa_prl.getApprove()==8 && wa_prl.getDrcrk().equals("H")){
		    			Payroll new_dolg_from_schet = new Payroll();
						BeanUtils.copyProperties(wa_prl, new_dolg_from_schet);
						new_dolg_from_schet.setApprove(0);
						prlDao.create(new_dolg_from_schet);
		    		}		    		
		    		
		    		prlDao.create(wa_prl);
		    	}
		    	
			}    
			int count = 0;
			for (TempPayroll wa_tp:al_tp)
			{
				
				TempPayrollArchive wa_tpa = new TempPayrollArchive();
				BeanUtils.copyProperties(wa_tp, wa_tpa);
				tpaDao.create(wa_tpa);
				count++;
				System.out.println(count);
			}
			
			tpDao.deleteAllByBukrs(a_bukrs);
			
			if (count_arc==0)
			{	
				List<Bonus> l_bonus = bonDao.dynamicFindBonuses(" bukrs = '"+a_bukrs+"'");
				List<SalePlan> l_sale_plan = salePlanDao.findAllByBukrs(a_bukrs);
				List<Pyramid> l_pyramid = pyramidDao.dynamicFindPyramid(" bukrs = '"+a_bukrs+"'");
				if (l_bonus!=null && l_bonus.size()>0)
				{
					for (Bonus wa_bonus:l_bonus)
					{
						BonusArchive wa_bon_arc = new BonusArchive();
						BeanUtils.copyProperties(wa_bonus, wa_bon_arc);
						/*wa_bon_arc.setBonus_id(wa_bonus.getBonus_id());						
						wa_bon_arc.setBonus_type_id(wa_bonus.getBonus_type_id());
						wa_bon_arc.setPosition_id(wa_bonus.getPosition_id());
						wa_bon_arc.setCountry_id(wa_bonus.getCountry_id());
						wa_bon_arc.setBusiness_area_id(wa_bonus.getBusiness_area_id());
						wa_bon_arc.setFrom_num(wa_bonus.getFrom_num());
						wa_bon_arc.setTo_num(wa_bonus.getTo_num());
						wa_bon_arc.setCoef(wa_bonus.getCoef());
						wa_bon_arc.setWaers(wa_bonus.getWaers());
						wa_bon_arc.setReq_value(wa_bonus.getReq_value());
						wa_bon_arc.setMatnr(wa_bonus.getMatnr());
						*/
						wa_bon_arc.setBukrs(a_bukrs);
						wa_bon_arc.setMonth(a_monat);
						wa_bon_arc.setYear(a_gjahr);
						bonArcDao.create(wa_bon_arc);
					    //pyramidArcDao;
						
						//salePlanArcDao;
					}
				}
				else
				{
					//throw new DAOException("No bonuses for Archiving financeService, create salary");
				}
				if (l_sale_plan!=null && l_sale_plan.size()>0)
				{
					for (SalePlan wa_sp:l_sale_plan)
					{
						SalePlanArchive wa_sp_arc = new SalePlanArchive();
						BeanUtils.copyProperties(wa_sp, wa_sp_arc);
						/*wa_sp_arc.setSp_id(wa_sp.getSp_id());
						wa_sp_arc.setCreated_by(wa_sp.getCreated_by());
						wa_sp_arc.setCreated_date(wa_sp.getCreated_date());
						wa_sp_arc.setUpdated_by(wa_sp.getUpdated_by());
						wa_sp_arc.setUpdated_date(wa_sp.getUpdated_date());
						wa_sp_arc.setBranch_id(wa_sp.getBranch_id());
						wa_sp_arc.setPlan_count(wa_sp.getPlan_count());
						wa_sp_arc.setBusiness_area_id(wa_sp.getBusiness_area_id());
						wa_sp_arc.setWaers(wa_sp.getWaers());
						wa_sp_arc.setPlan_sum(wa_sp.getPlan_sum());
						wa_sp_arc.setCountry_id(wa_sp.getCountry_id());
						*/
						wa_sp_arc.setMonth(a_monat);
						wa_sp_arc.setYear(a_gjahr);
						wa_sp_arc.setBukrs(a_bukrs);
						salePlanArcDao.create(wa_sp_arc);
					}
				}
				else
				{
					//throw new DAOException("No sale plan for Archiving financeService, create salary");
				}
				if (l_pyramid!=null && l_pyramid.size()>0)
				{
					for (Pyramid wa_pyr:l_pyramid)
					{
						PyramidArchive wa_pyr_arc = new PyramidArchive();
						BeanUtils.copyProperties(wa_pyr, wa_pyr_arc);
						/*wa_pyr_arc.setPyramid_id(wa_pyr.getPyramid_id());
						wa_pyr_arc.setStaff_id(wa_pyr.getStaff_id());
						wa_pyr_arc.setParent_pyramid_id(wa_pyr.getParent_pyramid_id());
						wa_pyr_arc.setPosition_id(wa_pyr.getPosition_id());
						wa_pyr_arc.setBranch_id(wa_pyr.getBranch_id());
						wa_pyr_arc.setBusiness_area_id(wa_pyr.getBusiness_area_id());											
						wa_pyr_arc.setCreated_by(wa_pyr.getCreated_by());
						wa_pyr_arc.setCreated_date(wa_pyr.getCreated_date());
						wa_pyr_arc.setUpdated_by(wa_pyr.getUpdated_by());
						wa_pyr_arc.setUpdated_date(wa_pyr.getUpdated_date());*/
						wa_pyr_arc.setMonth(a_monat);
						wa_pyr_arc.setYear(a_gjahr);
						wa_pyr_arc.setBukrs(a_bukrs);
						
						if (wa_pyr_arc.getStaff_id()!=null && wa_pyr_arc.getStaff_id().equals(0L)){
							wa_pyr_arc.setStaff_id(null);
						}
						pyramidArcDao.create(wa_pyr_arc);
	
					}
				}
				else
				{
					//throw new DAOException("No pyramid for Archiving financeService, create salary");
				}
				
		}
			 
			
		        
		}
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	public Long insertSalaryFi(String a_bukrs,Long a_userId,Long a_branchId, String a_waers, double a_amount, double a_kursf, Long a_customer_id, String a_shkzg,List<ExpAll> l_ea, Map<Long,Branch> l_brn_map,String a_tcode, int a_type) throws DAOException
	{
		try
		{
			// type 6 = Nachislenie
			// type 9 = Uderzhanie
			// type 3 = Avans
			// type 8 = Dolg
			double summa_rashod = 0;
			double zp_amount = 0;
			zp_amount = a_amount;
			if (l_ea!=null && l_ea.size()>0)
			{
				for(ExpAll wa_ea:l_ea)
				{
					summa_rashod = summa_rashod + wa_ea.getExp_amount();
				}
			}
			if (summa_rashod>=a_amount)
			{
				a_shkzg = "H";
				a_amount = summa_rashod-a_amount;
			}
			
			Calendar curDate = Calendar.getInstance();
			double dmbtr = 0;
			double wrbtr = 0;
			double kursf = 1;
			 if (!a_waers.equals("USD"))
				{
				 	dmbtr = a_amount/a_kursf;
					wrbtr = a_amount;
					kursf = a_kursf;
				}
				else
				{
					dmbtr = a_amount;
					wrbtr = 0;
					kursf = 1;
				}
				dmbtr = GeneralUtil.round(dmbtr, 2);
				wrbtr = GeneralUtil.round(wrbtr, 2);
			    
			    Bkpf p_bkpf_AS = new Bkpf();
				Bseg p_bseg_AS_kredit = new Bseg();
				Bseg p_bseg_AS_debet = new Bseg();
				List<Bseg> l_bseg_AS = new ArrayList<Bseg>();
				p_bkpf_AS.setBukrs(a_bukrs);
				p_bkpf_AS.setGjahr(curDate.get(Calendar.YEAR));
				p_bkpf_AS.setMonat(curDate.get(Calendar.MONTH)+1);
				p_bkpf_AS.setCustomer_id(a_customer_id);
				p_bkpf_AS.setTcode(a_tcode);
				p_bkpf_AS.setUsnam(a_userId);
				p_bkpf_AS.setBudat(curDate.getTime());
				p_bkpf_AS.setBldat(curDate.getTime());
				p_bkpf_AS.setWaers(a_waers);
				p_bkpf_AS.setDmbtr(dmbtr);
				p_bkpf_AS.setWrbtr(wrbtr);
				p_bkpf_AS.setKursf(kursf);
				p_bkpf_AS.setClosed(0);
				p_bkpf_AS.setAwtyp(2);
				p_bkpf_AS.setCpudt(curDate.getTime());
				p_bkpf_AS.setBrnch(a_branchId);
				
				// type 6 = Nachislenie
				// type 9 = Uderzhanie
				// type 3 = Avans
				// type 8 = Dolg
				if (a_type == 6)
				{
					// type 6 = Nachislenie
					// type 9 = Uderzhanie
					// type 3 = Avans
					// type 8 = Dolg
					int buzei = 0;
					p_bkpf_AS.setBlart("AS");
					
					
					
					
					
					
					
					
					
					
					if (l_ea==null || l_ea.size()==0)
					{
						buzei++;
						p_bseg_AS_debet.setBukrs(a_bukrs);
						p_bseg_AS_debet.setGjahr(curDate.get(Calendar.YEAR));
						p_bseg_AS_debet.setBuzei(buzei);
						p_bseg_AS_debet.setBschl("40");
						p_bseg_AS_debet.setHkont("71100001");
						p_bseg_AS_debet.setShkzg("S");
						p_bseg_AS_debet.setDmbtr(dmbtr);
						p_bseg_AS_debet.setWrbtr(wrbtr);
						
						buzei++;
						p_bseg_AS_kredit.setBukrs(a_bukrs);
						p_bseg_AS_kredit.setGjahr(curDate.get(Calendar.YEAR));
						p_bseg_AS_kredit.setBuzei(buzei);
						p_bseg_AS_kredit.setBschl("34");
						p_bseg_AS_kredit.setHkont("33500001");
						p_bseg_AS_kredit.setShkzg("H");
						p_bseg_AS_kredit.setDmbtr(dmbtr);
						p_bseg_AS_kredit.setWrbtr(wrbtr);
						p_bseg_AS_kredit.setLifnr(a_customer_id);
						l_bseg_AS.add(p_bseg_AS_debet);
						l_bseg_AS.add(p_bseg_AS_kredit);
						
					}
					else
					{
						//int pos = 2;
						double tot_dmbtr = 0;
						double tot_wrbtr = 0;
						for(ExpAll wa_ea:l_ea)
						{
								buzei++;
								Bseg wa_bseg_AS_kredit = new Bseg();
								wa_bseg_AS_kredit.setBukrs(a_bukrs);
								wa_bseg_AS_kredit.setGjahr(curDate.get(Calendar.YEAR));
								wa_bseg_AS_kredit.setBuzei(buzei);
								wa_bseg_AS_kredit.setBschl("31");							
								wa_bseg_AS_kredit.setShkzg("H");
								wa_bseg_AS_kredit.setLifnr(wa_ea.getExp_cus_id());
								if (!a_waers.equals("USD"))
								{
									wa_bseg_AS_kredit.setDmbtr(GeneralUtil.round(wa_ea.getExp_amount()/kursf, 2));
									wa_bseg_AS_kredit.setWrbtr(wa_ea.getExp_amount());
									tot_dmbtr = tot_dmbtr + wa_bseg_AS_kredit.getDmbtr();
									tot_wrbtr = tot_wrbtr + wa_bseg_AS_kredit.getWrbtr();
								}
								else
								{								
									wa_bseg_AS_kredit.setDmbtr(wa_ea.getExp_amount());
									wa_bseg_AS_kredit.setWrbtr(0);
									tot_dmbtr = tot_dmbtr + wa_bseg_AS_kredit.getDmbtr();
									tot_wrbtr = 0;
								}
								
								
								if (wa_ea.getType()==opv)
								{
									wa_bseg_AS_kredit.setHkont("32200001");
								}
								else if (wa_ea.getType()==ipn)
								{
									wa_bseg_AS_kredit.setHkont("31200001");
								}
								else
								{								
									wa_bseg_AS_kredit.setHkont("33100001");
								}
								
								
								l_bseg_AS.add(wa_bseg_AS_kredit);
								//pos++;
							
							
						}
						if (zp_amount>0)
						{	
							buzei++;
							p_bseg_AS_debet.setBukrs(a_bukrs);
							p_bseg_AS_debet.setGjahr(curDate.get(Calendar.YEAR));
							p_bseg_AS_debet.setBuzei(buzei);
							p_bseg_AS_debet.setBschl("40");
							p_bseg_AS_debet.setHkont("71100001");
							p_bseg_AS_debet.setShkzg("S");
							p_bseg_AS_debet.setDmbtr(dmbtr);
							p_bseg_AS_debet.setWrbtr(wrbtr);
							
							buzei++;
							p_bseg_AS_kredit.setBukrs(a_bukrs);
							p_bseg_AS_kredit.setGjahr(curDate.get(Calendar.YEAR));
							p_bseg_AS_kredit.setBuzei(buzei);
							p_bseg_AS_kredit.setBschl("34");
							p_bseg_AS_kredit.setHkont("33500001");
							p_bseg_AS_kredit.setShkzg("H");
							p_bseg_AS_kredit.setLifnr(a_customer_id);
							if (!a_waers.equals("USD"))
							{							
								p_bseg_AS_kredit.setWrbtr(wrbtr-tot_wrbtr);
								p_bseg_AS_kredit.setDmbtr(GeneralUtil.round(p_bseg_AS_kredit.getWrbtr()/kursf, 2));
								
								p_bkpf_AS.setDmbtr(tot_dmbtr+p_bseg_AS_kredit.getDmbtr());
								p_bseg_AS_debet.setDmbtr(tot_dmbtr+p_bseg_AS_kredit.getDmbtr());
								
							}
							else
							{								
								p_bseg_AS_kredit.setDmbtr(dmbtr-tot_dmbtr);
								p_bseg_AS_kredit.setWrbtr(0);
							}
							l_bseg_AS.add(p_bseg_AS_debet);
							l_bseg_AS.add(p_bseg_AS_kredit);
						}
						else
						{
							buzei++;
							p_bseg_AS_debet.setBukrs(a_bukrs);
							p_bseg_AS_debet.setGjahr(curDate.get(Calendar.YEAR));
							p_bseg_AS_debet.setBuzei(buzei);
							p_bseg_AS_debet.setBschl("21");
							p_bseg_AS_debet.setHkont("33500001");
							p_bseg_AS_debet.setShkzg("S");
							p_bseg_AS_debet.setLifnr(a_customer_id);
							if (l_ea.size()==0)
							{
								p_bseg_AS_debet.setDmbtr(dmbtr);
							}							
							else
							{
								for (Bseg wa_bs:l_bseg_AS)
								{
									p_bseg_AS_debet.setDmbtr(p_bseg_AS_debet.getDmbtr()+wa_bs.getDmbtr());
								}
							}
							p_bseg_AS_debet.setWrbtr(wrbtr);
							l_bseg_AS.add(p_bseg_AS_debet);
						}
						
					}
				}
				// type 6 = Nachislenie
				// type 9 = Uderzhanie
				// type 3 = Avans
				// type 8 = Dolg
				else if (a_type == 9)
				{
					// type 9 = Uderzhanie
					p_bkpf_AS.setBlart("S1");
					
					p_bseg_AS_debet.setBukrs(a_bukrs);
					p_bseg_AS_debet.setGjahr(curDate.get(Calendar.YEAR));
					p_bseg_AS_debet.setBuzei(1);
					p_bseg_AS_debet.setBschl("21");
					p_bseg_AS_debet.setHkont("33500001");
					p_bseg_AS_debet.setShkzg("S");
					p_bseg_AS_debet.setDmbtr(dmbtr);
					p_bseg_AS_debet.setWrbtr(wrbtr);
					p_bseg_AS_debet.setLifnr(a_customer_id);
					
					p_bseg_AS_kredit.setBukrs(a_bukrs);
					p_bseg_AS_kredit.setGjahr(curDate.get(Calendar.YEAR));
					p_bseg_AS_kredit.setBuzei(2);
					p_bseg_AS_kredit.setBschl("50");
					p_bseg_AS_kredit.setHkont("71100001");
					p_bseg_AS_kredit.setShkzg("H");
					p_bseg_AS_kredit.setDmbtr(dmbtr);
					p_bseg_AS_kredit.setWrbtr(wrbtr);
					p_bseg_AS_kredit.setLifnr(null);
					
					l_bseg_AS.add(p_bseg_AS_debet);
					l_bseg_AS.add(p_bseg_AS_kredit);
					
					
				}
				// type 6 = Nachislenie
				// type 9 = Uderzhanie
				// type 3 = Avans
				// type 8 = Dolg
				else if (a_type == 3)
				{
					return null;					
				}
				else if (a_type == 8)
				{
					// type 8 = Dolg
					p_bkpf_AS.setBlart("S2");
					
					p_bseg_AS_debet.setBukrs(a_bukrs);
					p_bseg_AS_debet.setGjahr(curDate.get(Calendar.YEAR));
					p_bseg_AS_debet.setBuzei(1);
					p_bseg_AS_debet.setBschl("21");
					p_bseg_AS_debet.setHkont("33500001");
					p_bseg_AS_debet.setShkzg("S");
					p_bseg_AS_debet.setDmbtr(dmbtr);
					p_bseg_AS_debet.setWrbtr(wrbtr);
					p_bseg_AS_debet.setLifnr(a_customer_id);
					
					p_bseg_AS_kredit.setBukrs(a_bukrs);
					p_bseg_AS_kredit.setGjahr(curDate.get(Calendar.YEAR));
					p_bseg_AS_kredit.setBuzei(2);
					p_bseg_AS_kredit.setBschl("34");
					p_bseg_AS_kredit.setHkont("33500002");
					p_bseg_AS_kredit.setShkzg("H");
					p_bseg_AS_kredit.setDmbtr(dmbtr);
					p_bseg_AS_kredit.setWrbtr(wrbtr);
					p_bseg_AS_kredit.setLifnr(a_customer_id);
					p_bkpf_AS.setBktxt("Оплата долг");
					l_bseg_AS.add(p_bseg_AS_debet);
					l_bseg_AS.add(p_bseg_AS_kredit);
				}
				else
					throw new DAOException("Ошибка тип начисления зп");
				
				
				
				
				
				
				List<Bsik> l_bsik_AS = new ArrayList<Bsik>();
				
				for (Bseg wa_bseg:l_bseg_AS)
				{
					Bsik wa_bsik = new Bsik();
					BeanUtils.copyProperties(wa_bseg, wa_bsik);
					l_bsik_AS.add(wa_bsik);
				}
				financeService.check_empty_fields(p_bkpf_AS, l_bseg_AS);
				Long belnr = financeService.insertNewFiDoc(p_bkpf_AS, l_bseg_AS, l_bsik_AS);
				
				return GeneralUtil.getPreparedAwkey(belnr, p_bkpf_AS.getGjahr());
		}
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	public void fahrbSave(String a_bukrs,Long a_userId, Long a_branchId, int a_oper_type, Payroll a_prl){
		try
		{
			Calendar curDate = Calendar.getInstance();
			double kursf = 1;
			double dmbtr = 0;
			double wrbtr = 0;
			Long awkey;
			Long customer_id;
			List<ExchangeRate> l_er = new ArrayList<ExchangeRate>();
			Map<String,ExchangeRate> l_er_map = new HashMap<String,ExchangeRate>();
			l_er = erDao.getLastCurrencyRates();
			
			for (ExchangeRate wa_er:l_er)
			{
				if (wa_er.getType()==1)
				{
					l_er_map.put(wa_er.getSecondary_currency(), wa_er);
				}
			}
			if (!a_prl.getWaers().equals("USD"))
			{
		    	ExchangeRate wa_er = new ExchangeRate();
		    	wa_er = l_er_map.get(a_prl.getWaers());
		    	if (wa_er==null || wa_er.getSecondary_currency()==null || wa_er.getSecondary_currency().length()==0)
		    	{
		    		throw new DAOException("Курс валют не определен."+" "+a_prl.getWaers());
		    	}
		    	else
		    	{
		    		kursf = wa_er.getSc_value();
		    		wrbtr = a_prl.getDmbtr();
		    		dmbtr = GeneralUtil.round(wrbtr/kursf,2);
					
		    	}
			}
			else
			{
				kursf = 1;
				dmbtr = a_prl.getDmbtr();
				wrbtr = 0;
			}
			
			customer_id = cusDao.findByStaffId(a_prl.getStaff_id()).getId();
			if (a_oper_type==1)
			{
//				private Long payroll_id;				
//				private Long salary_id;
//				private Long bonus_id;
//				private int bonus_type_id;
//				private Long position_id;
//				private int matnr_count;
//				private double plan_amount;
//				private double fact_amount;				
//				private Long contract_number;
//				private Long parent_payroll_id;
//				private Long manager_id;
//				private Long trainer_id;
//				private Long director_id;
//				private Long main_trainer_id;
//				private Long coordinator_id;
//				private Long approved_by;
				
				Payroll new_prl1 = new Payroll();
				new_prl1.setBukrs(a_prl.getBukrs());
				new_prl1.setStaff_id(a_prl.getStaff_id());
				new_prl1.setDrcrk("H");	
				new_prl1.setApprove(8);
				new_prl1.setBranch_id(a_prl.getBranch_id());
				new_prl1.setWaers(a_prl.getWaers());
				new_prl1.setDmbtr(a_prl.getDmbtr());
				new_prl1.setGjahr(curDate.get(Calendar.YEAR));
				new_prl1.setMonat(curDate.get(Calendar.MONTH)+1);				
				new_prl1.setPayroll_date(curDate.getTime());
				new_prl1.setBldat(curDate.getTime());
				new_prl1.setCreated_by(a_userId);
				new_prl1.setText45("Оплата долг");
				//Dolg
				awkey = insertSalaryFi(new_prl1.getBukrs(),new_prl1.getCreated_by(),new_prl1.getBranch_id(), new_prl1.getWaers(), new_prl1.getDmbtr(), kursf, customer_id, "H",null, null,"FAHRB",8);
				new_prl1.setAwkey(awkey);
				prlDao.create(new_prl1);
			}
			else if (a_oper_type==2 || a_oper_type==3)
			{
				Bkpf p_bkpf_AS = new Bkpf();
				Bseg p_bseg_AS_kredit = new Bseg();
				Bseg p_bseg_AS_debet = new Bseg();
				List<Bseg> l_bseg_AS = new ArrayList<Bseg>();
				//Перевод с долг. счета на баланс = 2
				//Перевод с баланс. счета на долг = 3
				String text = "";
				if (a_oper_type==2)
				{
					text = "Перевод с долг. счета на баланс";
					
					p_bseg_AS_kredit.setBschl("34");
					p_bseg_AS_kredit.setHkont("33500001");
					p_bseg_AS_kredit.setShkzg("H");
					
					p_bseg_AS_debet.setBschl("21");
					p_bseg_AS_debet.setHkont("33500002");
					p_bseg_AS_debet.setShkzg("S");
				}
				else if (a_oper_type==3) 
				{
					text = "Перевод с баланс. счета на долг";
					
					
				}
				
				
				
				Payroll new_prl1 = new Payroll();
				new_prl1.setBukrs(a_prl.getBukrs());
				new_prl1.setStaff_id(a_prl.getStaff_id());
				new_prl1.setDrcrk("S");	
				new_prl1.setApprove(0);
				new_prl1.setBranch_id(a_prl.getBranch_id());
				new_prl1.setWaers(a_prl.getWaers());
				new_prl1.setDmbtr(a_prl.getDmbtr());
				new_prl1.setGjahr(curDate.get(Calendar.YEAR));
				new_prl1.setMonat(curDate.get(Calendar.MONTH)+1);				
				new_prl1.setPayroll_date(curDate.getTime());
				new_prl1.setBldat(curDate.getTime());
				new_prl1.setCreated_by(a_userId);
				new_prl1.setText45(text);
				
				Payroll new_prl2 = new Payroll();
				new_prl2.setBukrs(a_prl.getBukrs());
				new_prl2.setStaff_id(a_prl.getStaff_id());
				new_prl2.setDrcrk("S");	
				new_prl2.setApprove(8);
				new_prl2.setBranch_id(a_prl.getBranch_id());
				new_prl2.setWaers(a_prl.getWaers());
				new_prl2.setDmbtr(a_prl.getDmbtr());
				new_prl2.setGjahr(curDate.get(Calendar.YEAR));
				new_prl2.setMonat(curDate.get(Calendar.MONTH)+1);				
				new_prl2.setPayroll_date(curDate.getTime());
				new_prl2.setBldat(curDate.getTime());	
				new_prl2.setCreated_by(a_userId);
				new_prl2.setText45(text);
				
				
				
				p_bkpf_AS.setBukrs(a_bukrs);
				p_bkpf_AS.setGjahr(curDate.get(Calendar.YEAR));
				p_bkpf_AS.setMonat(curDate.get(Calendar.MONTH)+1);
				p_bkpf_AS.setCustomer_id(customer_id);
				p_bkpf_AS.setTcode("FAHRB");
				p_bkpf_AS.setUsnam(a_userId);
				p_bkpf_AS.setBudat(curDate.getTime());
				p_bkpf_AS.setBldat(curDate.getTime());
				p_bkpf_AS.setWaers(a_prl.getWaers());
				p_bkpf_AS.setDmbtr(dmbtr);
				p_bkpf_AS.setWrbtr(wrbtr);
				p_bkpf_AS.setKursf(kursf);
				p_bkpf_AS.setClosed(0);
				p_bkpf_AS.setAwtyp(2);
				p_bkpf_AS.setCpudt(curDate.getTime());
				p_bkpf_AS.setBrnch(a_branchId);				
				p_bkpf_AS.setBlart("S2");
				p_bkpf_AS.setBktxt(text);
				
				p_bseg_AS_debet.setBukrs(a_bukrs);
				p_bseg_AS_debet.setGjahr(curDate.get(Calendar.YEAR));
				p_bseg_AS_debet.setBuzei(1);				
				p_bseg_AS_debet.setDmbtr(dmbtr);
				p_bseg_AS_debet.setWrbtr(wrbtr);
				p_bseg_AS_debet.setLifnr(customer_id);
				p_bseg_AS_debet.setBschl("21");
				p_bseg_AS_debet.setHkont("33500002");
				p_bseg_AS_debet.setShkzg("S");
				
				
				
				
				
				p_bseg_AS_kredit.setBukrs(a_bukrs);
				p_bseg_AS_kredit.setGjahr(curDate.get(Calendar.YEAR));
				p_bseg_AS_kredit.setBuzei(2);				
				p_bseg_AS_kredit.setDmbtr(dmbtr);
				p_bseg_AS_kredit.setWrbtr(wrbtr);
				p_bseg_AS_kredit.setLifnr(customer_id);
				p_bseg_AS_kredit.setBschl("34");
				p_bseg_AS_kredit.setHkont("33500001");
				p_bseg_AS_kredit.setShkzg("H");
				
				
				
				
				
				List<Bsik> l_bsik_AS = new ArrayList<Bsik>();
				l_bseg_AS.add(p_bseg_AS_debet);
				l_bseg_AS.add(p_bseg_AS_kredit);
				for (Bseg wa_bseg:l_bseg_AS)
				{
					Bsik wa_bsik = new Bsik();
					BeanUtils.copyProperties(wa_bseg, wa_bsik);
					l_bsik_AS.add(wa_bsik);
				}
				financeService.check_empty_fields(p_bkpf_AS, l_bseg_AS);
				Long belnr = financeService.insertNewFiDoc(p_bkpf_AS, l_bseg_AS, l_bsik_AS);
				awkey = GeneralUtil.getPreparedAwkey(belnr, p_bkpf_AS.getGjahr());
				new_prl1.setAwkey(awkey);
				new_prl2.setAwkey(awkey);
				prlDao.create(new_prl1);
				prlDao.create(new_prl2);
				
			}
			else
			{
				throw new DAOException("Операция не существует.");
			}
			
//			a_prl.setAwkey(awkey);
			//wa_zp.setAwkey(insertSalaryFi(wa_zp.getBukrs(),a_userId,wa_zp.getBranch_id(), wa_zp.getWaers(), wa_zp.getZp_amount(), wa_er.getSc_value(), wa_zp.getCustomer_id(), "S",wa_zp.getL_ea(), l_brn_map,"HRPL",6));
		
		}
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	/*
	public void old_wages() throws DAOException
	{
		try{
			Calendar curDate = Calendar.getInstance();
			System.out.println("Old values");
			List<ExchangeRate> l_er = new ArrayList<ExchangeRate>();
			Map<String,ExchangeRate> l_er_map = new HashMap<String,ExchangeRate>();
			l_er = erDao.getLastCurrencyRates();
			
			for (ExchangeRate wa_er:l_er)
			{
				if (wa_er.getType()==1)
				{
					l_er_map.put(wa_er.getSecondary_currency(), wa_er);
				}
			}
			
			List<Payroll> l_payroll_schet = new ArrayList<Payroll>();
			List<Payroll> l_payroll_deposit = new ArrayList<Payroll>();			
			List<Payroll> l_payroll_zablok = new ArrayList<Payroll>();
			//l_payroll_schet = payrollDao.findByBukrsBranchAllSchet_oldwages(GeneralUtil.getSQLDate(curDate));			
			l_payroll_deposit = payrollDao.findByBukrsBranchAllDeposit_oldwages();
			//l_payroll_zablok = payrollDao.findByBukrsBranchAllZablok_oldwages(GeneralUtil.getSQLDate(curDate));
			System.out.println(l_payroll_schet.size());
			System.out.println(l_payroll_deposit.size());
			System.out.println(l_payroll_zablok.size());
			Long userId=1L;
			String drcrkS="S";
			String drcrkH="H";
			int countAll=0;
			int countOper=0;
			int countbrr=0;
			for(Payroll wa_prl:l_payroll_deposit)
			{
				String dynamicWhereClause = "";
				dynamicWhereClause = dynamicWhereClause + " and sal.bukrs = '"+wa_prl.getBukrs()+"' and sal.staff_id="+wa_prl.getStaff_id();
				List<Salary> l_salary = new ArrayList<Salary>();
				
				
				
				if (wa_prl.getBranch_id()==null)
				{
					
					l_salary = salaryDao.findDynamic(dynamicWhereClause);
					if (l_salary==null || l_salary.size()==0)
					{
						l_salary = salaryDao.findDynamic_oldWages(dynamicWhereClause);
						if (l_salary==null || l_salary.size()==0)
						{
							throw new DAOException("Salary null "+wa_prl.getStaff_id());
						}
					}
					wa_prl.setBranch_id(l_salary.get(0).getBranch_id());
					
					countbrr++;
				}
				if (wa_prl.getDmbtr()>0)
				{
					countAll++;
					Long cusID = 
					staffDao.find(wa_prl.getStaff_id()).getCustomer_id();
					if (cusID == null||cusID==0)
					{
						throw new DAOException("Customer is null "+wa_prl.getStaff_id());
					}
					if (!wa_prl.getWaers().equals("USD"))
					{
				    	ExchangeRate wa_er = new ExchangeRate();
				    	wa_er = l_er_map.get(wa_prl.getWaers());
				    	if (wa_er==null || wa_er.getSecondary_currency()==null || wa_er.getSecondary_currency().length()==0)
				    	{
				    		throw new DAOException("Курс валют не определен."+" "+wa_prl.getWaers());
				    	}
				    	else
				    	{
				    		countOper++;
				    		insertSalaryFi_oldwages(wa_prl.getBukrs(),userId,wa_prl.getBranch_id(), wa_prl.getWaers(), wa_prl.getDmbtr(), wa_er.getSc_value(), cusID, drcrkS);

				    	}
					}
					else
					{
						countOper++;
						insertSalaryFi_oldwages(wa_prl.getBukrs(),userId,wa_prl.getBranch_id(), wa_prl.getWaers(), wa_prl.getDmbtr(), 1, cusID, drcrkS);
					}
					
				}
				else if (wa_prl.getDmbtr()<0)
				{
					countAll++;
					double summa = 0;
					summa = wa_prl.getDmbtr()*-1;
					Long cusID = 
							staffDao.find(wa_prl.getStaff_id()).getCustomer_id();
							if (cusID == null||cusID==0)
							{
								throw new DAOException("Customer is null "+wa_prl.getStaff_id());
							}
							if (!wa_prl.getWaers().equals("USD"))
							{
						    	ExchangeRate wa_er = new ExchangeRate();
						    	wa_er = l_er_map.get(wa_prl.getWaers());
						    	if (wa_er==null || wa_er.getSecondary_currency()==null || wa_er.getSecondary_currency().length()==0)
						    	{
						    		throw new DAOException("Курс валют не определен."+" "+wa_prl.getWaers());
						    	}
						    	else
						    	{
						    		countOper++;
						    		insertSalaryFi_oldwages(wa_prl.getBukrs(),userId,wa_prl.getBranch_id(), wa_prl.getWaers(), summa, wa_er.getSc_value(), cusID, drcrkH);

						    	}
							}
							else
							{
								countOper++;
								insertSalaryFi_oldwages(wa_prl.getBukrs(),userId,wa_prl.getBranch_id(), wa_prl.getWaers(), summa, 1, cusID, drcrkH);
							}
				}
			}
			
			
			System.out.println("countAll "+countAll);
			System.out.println("countOper "+countOper);
			System.out.println("countbrr "+countbrr);	
		}
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	public void insertSalaryFi_oldwages(String a_bukrs,Long a_userId,Long a_branchId, String a_waers, double a_amount, double a_kursf, Long a_customer_id, String a_shkzg) throws DAOException
	{
		try
		{
			Calendar curDate = Calendar.getInstance();
			double dmbtr = 0;
			double wrbtr = 0;
			double kursf = 1;
			 if (!a_waers.equals("USD"))
				{
				 	dmbtr = a_amount/a_kursf;
					wrbtr = a_amount;
					kursf = a_kursf;
				}
				else
				{
					dmbtr = a_amount;
					wrbtr = 0;
					kursf = 1;
				}
				dmbtr = GeneralUtil.round(dmbtr, 2);
				wrbtr = GeneralUtil.round(wrbtr, 2);
			    
			    Bkpf p_bkpf_AS = new Bkpf();
				Bseg p_bseg_AS_kredit = new Bseg();
				Bseg p_bseg_AS_debet = new Bseg();
				p_bkpf_AS.setBukrs(a_bukrs);
				p_bkpf_AS.setGjahr(curDate.get(Calendar.YEAR));
				p_bkpf_AS.setMonat(curDate.get(Calendar.MONTH)+1);
				p_bkpf_AS.setCustomer_id(a_customer_id);
				p_bkpf_AS.setTcode("HRPL");
				p_bkpf_AS.setUsnam(a_userId);
				p_bkpf_AS.setBudat(curDate.getTime());
				p_bkpf_AS.setBldat(curDate.getTime());
				p_bkpf_AS.setWaers(a_waers);
				p_bkpf_AS.setDmbtr(dmbtr);
				p_bkpf_AS.setWrbtr(wrbtr);
				p_bkpf_AS.setKursf(kursf);
				p_bkpf_AS.setClosed(0);
				p_bkpf_AS.setAwtyp(2);
				p_bkpf_AS.setCpudt(curDate.getTime());
				p_bkpf_AS.setBrnch(a_branchId);
				p_bkpf_AS.setBlart("AS");
				
				
				p_bseg_AS_debet.setBukrs(a_bukrs);
				p_bseg_AS_debet.setGjahr(curDate.get(Calendar.YEAR));
				p_bseg_AS_debet.setBuzei(1);
				p_bseg_AS_debet.setBschl("40");
				p_bseg_AS_debet.setHkont("71100001");
				p_bseg_AS_debet.setShkzg("S");
				p_bseg_AS_debet.setDmbtr(dmbtr);
				p_bseg_AS_debet.setWrbtr(wrbtr);			
				
				p_bseg_AS_kredit.setBukrs(a_bukrs);
				p_bseg_AS_kredit.setGjahr(curDate.get(Calendar.YEAR));
				p_bseg_AS_kredit.setBuzei(2);
				p_bseg_AS_kredit.setBschl("34");
				p_bseg_AS_kredit.setHkont("33500001");
				p_bseg_AS_kredit.setShkzg("H");
				p_bseg_AS_kredit.setDmbtr(dmbtr);
				p_bseg_AS_kredit.setWrbtr(wrbtr);
				p_bseg_AS_kredit.setLifnr(a_customer_id);
				
				if (a_shkzg.equals("H"))
				{
					p_bseg_AS_debet.setBschl("21");
					p_bseg_AS_debet.setHkont("33500001");
					p_bseg_AS_debet.setLifnr(a_customer_id);
					
					p_bseg_AS_kredit.setBschl("50");
					p_bseg_AS_kredit.setHkont("60100030");
					p_bseg_AS_kredit.setLifnr(null);
				}
				
				
				List<Bseg> l_bseg_AS = new ArrayList<Bseg>();
				List<Bsik> l_bsik_AS = new ArrayList<Bsik>();
				
				l_bseg_AS.add(p_bseg_AS_debet);
				l_bseg_AS.add(p_bseg_AS_kredit);
				for (Bseg wa_bseg:l_bseg_AS)
				{
					Bsik wa_bsik = new Bsik();
					BeanUtils.copyProperties(wa_bseg, wa_bsik);
					l_bsik_AS.add(wa_bsik);
				}
				check_empty_fields_oldwages(p_bkpf_AS, l_bseg_AS);
				insertNewFiDoc_oldwages(p_bkpf_AS, l_bseg_AS, l_bsik_AS);
		}
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	public void check_empty_fields_oldwages(Bkpf a_bkpf, List<Bseg> l_bseg) throws DAOException{
		try{ 
			double dmbtr_h = 0;
			double dmbtr_s = 0;
			double wrbtr_h = 0;
			double wrbtr_s = 0;
			int curYear = 0;
			//Blart
			if (a_bkpf.getBlart()==null || a_bkpf.getBlart().isEmpty()) {	  
				throw new DAOException("Document Type is empty");
			}  
			Calendar curDate = Calendar.getInstance(); 
	        Calendar firstDay = Calendar.getInstance();  
	        Calendar lastDay = Calendar.getInstance(); 
	        curYear = curDate.get(Calendar.YEAR);    
	        firstDay.set(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), 1);
	        lastDay.set(Calendar.DAY_OF_MONTH, lastDay.getActualMaximum(Calendar.DAY_OF_MONTH));
	         
	        SimpleDateFormat formatter=new SimpleDateFormat("dd.MM.yyyy");  
	        String curDateString = formatter.format(firstDay.getTime());  
	        
	        Calendar test = Calendar.getInstance();
	        test.setTime(a_bkpf.getBldat());
	        
	        firstDay.set(Calendar.HOUR_OF_DAY, 0);
	        firstDay.set(Calendar.MINUTE, 0);
	        firstDay.set(Calendar.SECOND, 0);
	        firstDay.set(Calendar.MILLISECOND, 0);
	        lastDay.set(Calendar.HOUR_OF_DAY, 0);
	        lastDay.set(Calendar.MINUTE, 0);
	        lastDay.set(Calendar.SECOND, 0);
	        lastDay.set(Calendar.MILLISECOND, 0);
	       
			//Bldat
			if (a_bkpf.getBldat().before(firstDay.getTime())) { 
				throw new DAOException("Document date is empty or less than "+curDateString); 
			}
				          
			//budat
			if (a_bkpf.getBudat().before(firstDay.getTime())) {
				throw new DAOException("Posting date is empty or less than "+curDateString);
			}
			
			//monat
			if (a_bkpf.getMonat() < 1 && a_bkpf.getMonat() >12) {
				throw new DAOException("Period must be between 1 and 12");
			}
			
			//bukrs
			if (a_bkpf.getBukrs()== null  || a_bkpf.getBukrs().isEmpty()) {
				throw new DAOException("Company code is empty"); 
			}

			//waers
			if (a_bkpf.getWaers()==null || a_bkpf.getWaers().isEmpty()) {
				throw new DAOException("Currency is empty"); 
			} 
			
			//year
			if (a_bkpf.getGjahr()!=curYear) {
				//throw new DAOException("Year is empty or not equal to "+curYear); 
			}
			
			for (Bseg wa_bseg : l_bseg) {
				//bukrs
				if (wa_bseg.getBukrs()==null || wa_bseg.getBukrs().isEmpty() || (!a_bkpf.getBukrs().equals(wa_bseg.getBukrs()))){
					throw new DAOException("Position "+wa_bseg.getBuzei()+" Company code is empty or not equal to header company code");
				}
				
				Integer awtyp = a_bkpf.getAwtyp();
				
				//hkont
				if (wa_bseg.getHkont()==null || wa_bseg.getHkont().isEmpty()) {
					throw new DAOException("Position "+wa_bseg.getBuzei()+": General Ledger is empty, Customer id = " + a_bkpf.getCustomer_id());  
				}
				
				
				if (awtyp!=null && (a_bkpf.getAwtyp()==2 || a_bkpf.getAwtyp()==1) && (wa_bseg.getHkont().startsWith("12") || wa_bseg.getHkont().startsWith("3")) && wa_bseg.getLifnr()==null)
				{
					throw new DAOException("Position "+wa_bseg.getBuzei()+" Customer id is empty");
				}
 
				//gjahr
				if (wa_bseg.getGjahr() == 0 || a_bkpf.getGjahr() != wa_bseg.getGjahr()){
					throw new DAOException("Position "+wa_bseg.getBuzei()+" Year is empty or not equal to header year");
				}
				
				//bschl
				if (wa_bseg.getBschl()==null || wa_bseg.getBschl().isEmpty()) {
					throw new DAOException("Position "+wa_bseg.getBuzei()+": Posting key is empty"); 
				}				
				
				
				Bschl wa_bschl = bschlDao.find(wa_bseg.getBschl());
				if (wa_bschl == null || (!wa_bseg.getShkzg().equals(wa_bschl.getShkzg()))){
					throw new DAOException("Position "+wa_bseg.getBuzei()+": No such Posting key with debet/credit value '"+wa_bseg.getShkzg()+"'");
				}
		 
				
				
						
				//shkzg
				if (wa_bseg.getShkzg()==null || wa_bseg.getShkzg().isEmpty()) {
					throw new DAOException("Position "+wa_bseg.getBuzei()+": Debet/credit is empty");  
				}
				//System.out.println(a_bkpf.getAwkey() + " " +wa_bseg.getBschl() + " "+ wa_bseg.getDmbtr());
				if (wa_bseg.getDmbtr() == 0 && (a_bkpf.getBlart().equals("ST")))
				{
					//OK
				}
				else if (wa_bseg.getDmbtr() == 0 && ((wa_bseg.getBschl().equals("3")||wa_bseg.getBschl().equals("50")) && (a_bkpf.getBlart().equals("AK")))){
					//OK
				}
				else if (wa_bseg.getDmbtr() == 0 && ((wa_bseg.getBschl().equals("1")||wa_bseg.getBschl().equals("50")) && (a_bkpf.getBlart().equals("GS")))){
					//OK
				}
				else if (wa_bseg.getDmbtr() == 0 && (a_bkpf.getBlart().equals("GW"))){
					//OK
				}
				else if (wa_bseg.getDmbtr() == 0)
				{
					throw new DAOException("The amount is empty");
				}
				
				if (wa_bseg.getShkzg().equals("H")){
					dmbtr_h = dmbtr_h + wa_bseg.getDmbtr();
					dmbtr_h=GeneralUtil.round(dmbtr_h, 2);
				}
				else if (wa_bseg.getShkzg().equals("S")){
					dmbtr_s = dmbtr_s + wa_bseg.getDmbtr();
					dmbtr_s=GeneralUtil.round(dmbtr_s, 2);
				}	
				
				if (wa_bseg.getShkzg().equals("H")){
					wrbtr_h = wrbtr_h + wa_bseg.getWrbtr();
					wrbtr_h=GeneralUtil.round(wrbtr_h, 2);
				}
				else if (wa_bseg.getShkzg().equals("S")){
					wrbtr_s = wrbtr_s + wa_bseg.getWrbtr();
					wrbtr_s=GeneralUtil.round(wrbtr_s, 2);
				}	
				
				
				
			}
			
			System.out.println(dmbtr_h);
			System.out.println(dmbtr_s);
			System.out.println(wrbtr_h);
			System.out.println(wrbtr_s);
			if (a_bkpf.getBlart().equals("AK")||a_bkpf.getBlart().equals("ST")||a_bkpf.getBlart().equals("GS"))
			{
				if ((a_bkpf.getWaers().equals("USD")) && (dmbtr_h != dmbtr_s)){ 
					throw new DAOException("Debet and credit amounts are not equal in local currency");  
				}
				if ((!a_bkpf.getWaers().equals("USD")) && (wrbtr_h != wrbtr_s)){ 
					throw new DAOException("Debet and credit amounts are not equal in foriegn currency");  
				}
			}
			else
			{
				if ((a_bkpf.getWaers().equals("USD")) && (dmbtr_h == 0) || (dmbtr_h != dmbtr_s)){ 
					throw new DAOException("Debet and credit amounts are not equal in local currency");  
				}
				if ((!a_bkpf.getWaers().equals("USD")) && ((wrbtr_h == 0) || (wrbtr_h != wrbtr_s))){ 
					throw new DAOException("Debet and credit amounts are not equal in foriegn currency");  
				}
			}
			
			
		}  
		catch (Exception ex)
		{	  
			System.out.println("Document type: "+a_bkpf.getBlart());
			for (Bseg wa_bseg : l_bseg) {
				System.out.println("Position: "+wa_bseg.getBuzei());
				System.out.println("Hkont: "+wa_bseg.getHkont());
				System.out.println("USD amount: "+wa_bseg.getDmbtr());
				System.out.println(a_bkpf.getWaers()+" amount: "+wa_bseg.getWrbtr());
			}
			throw new DAOException(ex.getMessage()+", finance service->check_empty_fields");
		}
	}
	
	public Long insertNewFiDoc_oldwages(Bkpf a_bkpf, List<Bseg> l_bseg, List<Bsik> l_bsik) throws DAOException{
		try{
			TableIdLimit p_tableIdLimit = new TableIdLimit();
			
			if (tableIdLimitDao.countByIds("bkpf","blart",a_bkpf.getBlart(),"belnr",a_bkpf.getGjahr())>0){
				p_tableIdLimit = tableIdLimitDao.findByIds("bkpf","blart",a_bkpf.getBlart(),"belnr",a_bkpf.getGjahr());
			}
			else{ 
				throw new DAOException("No id limit for this type of document in table (table_id_limit) "+a_bkpf.getBlart());
			}
			
			long awkey = p_tableIdLimit.getCurrent_id() * 10000; 
			awkey = awkey + a_bkpf.getGjahr(); 
			a_bkpf.setBelnr(p_tableIdLimit.getCurrent_id());
			if (p_tableIdLimit.getCurrent_id()==p_tableIdLimit.getTo_id()){
				throw new DAOException("Id limit exceed (table_id_limit)");
			}
			p_tableIdLimit.setCurrent_id(p_tableIdLimit.getCurrent_id()+ 1); 
			tableIdLimitDao.update(p_tableIdLimit);
			
			//a_bkpf.setAwkey(awkey);
			for (Bseg wa_bseg : l_bseg) {
				wa_bseg.setBelnr(a_bkpf.getBelnr());
			}
			
			if (l_bsik.size()>0){
				for (Bsik wa_bsik : l_bsik) {
					wa_bsik.setBelnr(a_bkpf.getBelnr());
				}
			}
			

			if (a_bkpf.getBktxt()!=null && a_bkpf.getBktxt().length()>25)
			{
				a_bkpf.setBktxt(a_bkpf.getBktxt().substring(0,25));
			}
			
			if (a_bkpf.getWaers().equals("USD") && a_bkpf.getDmbtr_paid()>a_bkpf.getDmbtr())
			{
				throw new DAOException("Ошибка, переплата.");
			}
			else if (!a_bkpf.getWaers().equals("USD") && a_bkpf.getWrbtr_paid()>a_bkpf.getWrbtr())
			{
				throw new DAOException("Ошибка, переплата.");
			}
			
			//Creating process 
			bkpfDao.create(a_bkpf);

			for (Bseg wa_bseg : l_bseg) { 			
 
				Fmglflext p_fmglflext = new Fmglflext();
				Fmglflext2 p_fmglflext2 = new Fmglflext2(); 
				//if (fmglflextDao.countByIds(a_bkpf.getBukrs(), a_bkpf.getGjahr(), wa_bseg.getHkont(), wa_bseg.getShkzg())>0){
				//	p_fmglflext = fmglflextDao.findByIds(a_bkpf.getBukrs(), a_bkpf.getGjahr(), wa_bseg.getHkont(), wa_bseg.getShkzg());
				//}
				try 
				{
					p_fmglflext = fmglflextDao.findByIds(a_bkpf.getBukrs(), a_bkpf.getGjahr(), wa_bseg.getHkont(), wa_bseg.getShkzg());
				}
				catch (NoResultException ex) 
				{
					p_fmglflext.setBukrs(a_bkpf.getBukrs());
					p_fmglflext.setGjahr(a_bkpf.getGjahr());
					p_fmglflext.setHkont(wa_bseg.getHkont());
					p_fmglflext.setDrcrk(wa_bseg.getShkzg());
				}
				//if (fmglflext2Dao.countByIds(a_bkpf.getBukrs(), a_bkpf.getGjahr(), wa_bseg.getHkont(), wa_bseg.getShkzg(), a_bkpf.getBrnch())>0){
				//	p_fmglflext2 = fmglflext2Dao.findByIds(a_bkpf.getBukrs(), a_bkpf.getGjahr(), wa_bseg.getHkont(), wa_bseg.getShkzg(), a_bkpf.getBrnch());
				//}
				
				try 
				{
					if (a_bkpf.getBrnch()==null)
					{
						throw new DAOException("Branch id is null for fmglflext2");
					}
					p_fmglflext2 = fmglflext2Dao.findByIds(a_bkpf.getBukrs(), a_bkpf.getGjahr(), wa_bseg.getHkont(), wa_bseg.getShkzg(), a_bkpf.getBrnch());
				}
				catch (NoResultException ex) 
				{
					p_fmglflext2.setBukrs(a_bkpf.getBukrs());
					p_fmglflext2.setGjahr(a_bkpf.getGjahr());
					p_fmglflext2.setHkont(wa_bseg.getHkont());
					p_fmglflext2.setDrcrk(wa_bseg.getShkzg());
					p_fmglflext2.setBranch_id(a_bkpf.getBrnch());
				}

				double summa = 0;  
				Hkont wa_hkont = new Hkont(); 
				//if (hkontDao.countByIds(wa_bseg.getBukrs(), wa_bseg.getHkont())>0){
				//	wa_hkont = hkontDao.findByIds(wa_bseg.getBukrs(), wa_bseg.getHkont());
				//}
				
				try 
				{
					wa_hkont = hkontDao.findByIds(wa_bseg.getBukrs(), wa_bseg.getHkont());
				}
				catch (NoResultException ex) 
				{
					throw new DAOException("No such general ledger account");
				}
				if (wa_hkont.getWaers()==null || wa_hkont.getWaers().equals("USD")){
				   	summa = wa_bseg.getDmbtr(); 
				   	if (p_fmglflext.getWaers()==null){
				   		p_fmglflext.setWaers("USD");
				   	}
				   	if (p_fmglflext2.getWaers()==null){
				   		p_fmglflext2.setWaers("USD");
				   	}
				}
				else{
					summa = wa_bseg.getWrbtr();
					if (p_fmglflext.getWaers()==null){
				   		p_fmglflext.setWaers(wa_hkont.getWaers());
				   	}
				   	if (p_fmglflext2.getWaers()==null){
				   		p_fmglflext2.setWaers(wa_hkont.getWaers());
				   	}
				}
				if (a_bkpf.getMonat() == 1)
				{	
					p_fmglflext.setMonth1(p_fmglflext.getMonth1()+ summa);
					p_fmglflext2.setMonth1(p_fmglflext2.getMonth1()+ summa);
				}
				else if (a_bkpf.getMonat() == 2)
				{	
					p_fmglflext.setMonth2(p_fmglflext.getMonth2()+ summa);
					p_fmglflext2.setMonth2(p_fmglflext2.getMonth2()+ summa);
				}
				else if (a_bkpf.getMonat() == 3)
				{	
					p_fmglflext.setMonth3(p_fmglflext.getMonth3()+ summa);
					p_fmglflext2.setMonth3(p_fmglflext2.getMonth3()+ summa);
				}
				else if (a_bkpf.getMonat() == 4)
				{	
					p_fmglflext.setMonth4(p_fmglflext.getMonth4()+ summa);
					p_fmglflext2.setMonth4(p_fmglflext2.getMonth4()+ summa);
				}
				else if (a_bkpf.getMonat() == 5)
				{	
					p_fmglflext.setMonth5(p_fmglflext.getMonth5()+ summa);
					p_fmglflext2.setMonth5(p_fmglflext2.getMonth5()+ summa);
				}
				else if (a_bkpf.getMonat() == 6)
				{	
					p_fmglflext.setMonth6(p_fmglflext.getMonth6()+ summa);
					p_fmglflext2.setMonth6(p_fmglflext2.getMonth6()+ summa);
				}
				else if (a_bkpf.getMonat() == 7)
				{	
					p_fmglflext.setMonth7(p_fmglflext.getMonth7()+ summa);
					p_fmglflext2.setMonth7(p_fmglflext2.getMonth7()+ summa);
				}
				else if (a_bkpf.getMonat() == 8)
				{	
					p_fmglflext.setMonth8(p_fmglflext.getMonth8()+ summa);
					p_fmglflext2.setMonth8(p_fmglflext2.getMonth8()+ summa);
				}
				else if (a_bkpf.getMonat() == 9)
				{	
					p_fmglflext.setMonth9(p_fmglflext.getMonth9()+ summa);
					p_fmglflext2.setMonth9(p_fmglflext2.getMonth9()+ summa);
				}
				else if (a_bkpf.getMonat() == 10)
				{	
					p_fmglflext.setMonth10(p_fmglflext.getMonth10()+ summa);
					p_fmglflext2.setMonth10(p_fmglflext2.getMonth10()+ summa);
				}
				else if (a_bkpf.getMonat() == 11)
				{	
					p_fmglflext.setMonth11(p_fmglflext.getMonth11()+ summa);
					p_fmglflext2.setMonth11(p_fmglflext2.getMonth11()+ summa);
				}
				else if (a_bkpf.getMonat() == 12)
				{	
					p_fmglflext.setMonth12(p_fmglflext.getMonth12()+ summa);
					p_fmglflext2.setMonth12(p_fmglflext2.getMonth12()+ summa);
				} 
				
				bsegDao.create(wa_bseg); 
				fmglflextDao.create(p_fmglflext); 
				fmglflext2Dao.create(p_fmglflext2); 
			}
			
			if (l_bsik.size()>0){
				for (Bsik wa_bsik : l_bsik) { 
					bsikDao.create(wa_bsik);
				}
			}

			
			return a_bkpf.getBelnr();
			
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}*/
	public class ExpAll
	{
		private Long exp_cus_id;
		private String waers;
		private double exp_amount;
		private int type = 0;
		public Long getExp_cus_id() {
			return exp_cus_id;
		}
		public void setExp_cus_id(Long exp_cus_id) {
			this.exp_cus_id = exp_cus_id;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		public double getExp_amount() {
			return exp_amount;
		}
		public void setExp_amount(double exp_amount) {
			this.exp_amount = exp_amount;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		
	}
	public class ZpTotal
	{
		public ZpTotal()
		{
			branch_id = 0L;
			bukrs = "";
			waers = "";
			zp_amount = 0;
		}
		private String bukrs;
		private Long branch_id;
		private String waers;
		private double zp_amount;
		private Long customer_id;
		private Long awkey;
		private ExpAll dolg = new ExpAll();
		private List<ExpAll> l_ea =  new ArrayList<ExpAll>();
		private List<Payroll> l_prl =  new ArrayList<Payroll>();
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		public Long getBranch_id() {
			return branch_id;
		}
		public void setBranch_id(Long branch_id) {
			this.branch_id = branch_id;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		public double getZp_amount() {
			return zp_amount;
		}
		public void setZp_amount(double zp_amount) {
			this.zp_amount = zp_amount;
		}
		
		public Long getCustomer_id() {
			return customer_id;
		}
		public void setCustomer_id(Long customer_id) {
			this.customer_id = customer_id;
		}
		
		public Long getAwkey() {
			return awkey;
		}
		public void setAwkey(Long awkey) {
			this.awkey = awkey;
		}
		public List<ExpAll> getL_ea() {
			return l_ea;
		}
		public void setL_ea(List<ExpAll> l_ea) {
			this.l_ea = l_ea;
		}
		public List<Payroll> getL_prl() {
			return l_prl;
		}
		public void setL_prl(List<Payroll> l_prl) {
			this.l_prl = l_prl;
		}
		public ExpAll getDolg() {
			return dolg;
		}
		public void setDolg(ExpAll dolg) {
			this.dolg = dolg;
		}
		
		
	}
}
