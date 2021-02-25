package general.services;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.GeneralUtil;
import general.universal.SaveFacmassinTable;
import general.dao.BkpfDao;
import general.dao.BonusArchiveDao;
import general.dao.BranchDao;
import general.dao.BsegDao;
import general.dao.ContractDao;
import general.dao.ContractTypeDao;
import general.dao.DAOException;
import general.dao.ExchangeRateDao;
import general.dao.PaymentScheduleDao;
import general.dao.PayrollDao;
import general.dao.StaffDao;
import general.tables.Bkpf;
import general.tables.BonusArchive;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Bsik;
import general.tables.Contract;
import general.tables.ContractType;
import general.tables.ExchangeRate;
import general.tables.PaymentSchedule;
import general.tables.Payroll;
import general.tables.Promotion;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dms.contract.ContractFinOperations;

@Service("financeServiceDms")
public class FinanceServiceDmsImpl implements FinanceServiceDms{

	
	@Autowired
    private BkpfDao bkpfDao;
	
	@Autowired
	StaffDao stfDao;
	
	
	@Autowired
    private BsegDao bsegDao;
	
	@Autowired
    private BranchDao branchDao;
	
	@Autowired
    private FinanceService financeService;
	
	@Autowired
    private FinanceServicePayroll financeServicePayroll;	
	
	@Autowired
    private PayrollService payrollService;
	
	@Autowired
    private PaymentScheduleDao psDao;	
	
	@Autowired
    private ContractDao conDao;
	
	@Autowired
    private ContractTypeDao conTypeDao;
	
	
	
	@Autowired
	ExchangeRateDao erDao;
	
	@Autowired
	private BonusArchiveDao bonArcDao;
	
	@Autowired
	private PayrollDao prlDao;
	
	
	int dealer_pos_id=4;
	int manager_pos_id=3;
	int coordinator_pos_id=5;
	int demo_pos_id=8;
	int collector_pos_id=9;
	int director_pos_id=10;
	int trainer_pos_id=12;
	int main_trainer_pos_id=15;
	
	
	//bonus_type_id
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
	int skidkaotdilera = 11;
	int zaakciya = 12;
	
	public boolean massContractPayments (List<SaveFacmassinTable> al_sft) throws DAOException{
		try{	
			for (SaveFacmassinTable wa_sft:al_sft)
			{
				Calendar curDate = Calendar.getInstance();
				Bkpf p_bkpf = new Bkpf();
				List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
				List<Bsik> pl_bsik = new ArrayList<Bsik>();
				p_bkpf = wa_sft.getP_bkpf();
				
				if (p_bkpf.getContract_number()== null || p_bkpf.getContract_number() == 0 ){
					throw new DAOException("SN is empty");
				}
				if (p_bkpf.getWaers() == null || p_bkpf.getWaers().isEmpty())
				{
					throw new DAOException("Waers is empty");
				}
				if (p_bkpf.getBrnch()==null || p_bkpf.getBrnch() == 0){
					throw new DAOException("Branch is empty");
				}
				if (p_bkpf.getAwkey()==null || p_bkpf.getAwkey() == 0){
					throw new DAOException("Awkey is empty");
				}
				if (p_bkpf.getUsnam()==null || p_bkpf.getUsnam() == 0){
					throw new DAOException("User is empty");
				}
				if (p_bkpf.getAwtyp()!=1){
					throw new DAOException("Doc awtyp incorrect");
				}
				if (p_bkpf.getTcode() == null || p_bkpf.getTcode().isEmpty())
				{
					throw new DAOException("Transaction code is empty");
				}
				if (p_bkpf.getCustomer_id()== null || p_bkpf.getCustomer_id() == 0 ){
					throw new DAOException("Customer id is empty");
				}
				if (p_bkpf.getKursf() <= 0){
					throw new DAOException("Currency rate is empty");
				}
				if (p_bkpf.getDmbtr()==0 || p_bkpf.getDmbtr_paid()>0){
					throw new DAOException("USD amount not correct");
				}
				if (!p_bkpf.getWaers().equals("USD") && p_bkpf.getWrbtr() == 0){ 
					throw new DAOException(p_bkpf.getWaers()+" amount not correct"); 
				}
				pl_bseg = wa_sft.getL_bseg();
				double p_dmbtr = 0;
				double p_wrbtr = 0;
				String debitor_hkont = "";
				Long matnr = null;
				for (Bseg wa_bseg: pl_bseg)
				{
					if (p_bkpf.getWaers().equals("USD") && wa_bseg.getDmbtr() == 0){ 
						throw new DAOException("The amount in local currency is 0"); 
					}
					else if (!p_bkpf.getWaers().equals("USD") && wa_bseg.getWrbtr() == 0){					
						throw new DAOException("The amount in foreign currency is 0"); 
					}
					if (wa_bseg.getHkont().startsWith("1010") || wa_bseg.getHkont().startsWith("1030"))
					{
						p_dmbtr = p_dmbtr + wa_bseg.getDmbtr();
						p_wrbtr = p_wrbtr + wa_bseg.getWrbtr();
								
					}
					if (wa_bseg.getHkont().startsWith("1210")&& wa_bseg.getLifnr().equals(p_bkpf.getCustomer_id()))
					{
						debitor_hkont = wa_bseg.getHkont();
						matnr = wa_bseg.getMatnr();
					}
					//System.out.println(wa_bseg.getBuzei()+" "+wa_bseg.getBschl());
					Bsik wa_Bsik = new Bsik(); 
	            	wa_Bsik.setBukrs(wa_bseg.getBukrs());
	            	wa_Bsik.setGjahr(wa_bseg.getGjahr());
	            	wa_Bsik.setBuzei(wa_bseg.getBuzei());
	            	wa_Bsik.setBschl(wa_bseg.getBschl());
	            	wa_Bsik.setHkont(wa_bseg.getHkont());
	            	wa_Bsik.setShkzg(wa_bseg.getShkzg());
	            	wa_Bsik.setDmbtr(wa_bseg.getDmbtr());
	            	wa_Bsik.setWrbtr(wa_bseg.getWrbtr());
	            	wa_Bsik.setLifnr(wa_bseg.getLifnr()); 
	            	wa_Bsik.setMenge(wa_bseg.getMenge());
	            	wa_Bsik.setMatnr(wa_bseg.getMatnr());       
	            	pl_bsik.add(wa_Bsik);
				}
				
				//Create fin docs
				financeService.check_empty_fields(p_bkpf, pl_bseg);
				financeService.insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);
				updateContractPayment(p_bkpf, p_dmbtr,p_wrbtr,wa_sft.getPayment_schedule_id(),matnr,debitor_hkont,curDate);
				
				
			}
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
	public Long createContractRecievable (Contract a_contract, Branch a_branch, Long a_userID, String a_tcode, 
			String a_waers, double a_kursf, double a_dmbtr, double a_wrbtr, Long a_matnr, List<ContractFinOperations> contrFOList, List<Promotion> promoList) throws DAOException{
		try{			
			System.out.println("Creating Finance Document");
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
//			List<Bsik> pl_Bsik = new ArrayList<Bsik>();
			List<Bsik> pl_bsik = new ArrayList<Bsik>();
			
			Calendar curDate = Calendar.getInstance(); 
			Time cputm = new Time(curDate.getTimeInMillis()); 
			
			
			if (a_contract==null || a_contract.getContract_id()== null || a_contract.getContract_id() == 0 ){
				throw new DAOException("Contract id is empty");
			} 
			
			if (a_waers == null || a_waers.isEmpty())
			{
				throw new DAOException("Waers is empty");
			}
			
			if (a_waers.equals("USD") && a_dmbtr == 0){ 
				throw new DAOException("The amount in local currency is 0"); 
			}
			else if (a_wrbtr == 0){					
				throw new DAOException("The amount in foreign currency is 0"); 
			}
			
			if (a_branch==null){
				throw new DAOException("Branch is empty");
			}
			
			if (a_userID==null || a_userID <= 0){
				throw new DAOException("User is empty");
			}
			
			if (a_tcode == null || a_tcode.isEmpty())
			{
				throw new DAOException("Transaction code is empty");
			}
			
			if (a_kursf <= 0){
				throw new DAOException("Currency rate is empty");
			}
			
			p_bkpf.setBukrs(a_contract.getBukrs());
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setBlart("GC");
			p_bkpf.setBudat(curDate.getTime());
			p_bkpf.setBldat(curDate.getTime());
            p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
            p_bkpf.setCpudt(curDate.getTime());
            p_bkpf.setUsnam(a_userID);
            p_bkpf.setTcode(a_tcode);
            p_bkpf.setWaers(a_waers);
            p_bkpf.setKursf(a_kursf); 
            p_bkpf.setBrnch(a_branch.getBranch_id());
            p_bkpf.setBusiness_area_id(a_branch.getBusiness_area_id());
			p_bkpf.setContract_number(a_contract.getContract_number()); 
			p_bkpf.setCustomer_id(a_contract.getCustomer_id());
			p_bkpf.setAwtyp(1);
			
			List<ExchangeRate> exchageRate_list = new ArrayList<ExchangeRate>(); 
			
			if (a_waers == null || a_waers.isEmpty()){
				throw new DAOException("Select currency");
			}
			else
			{
				p_bkpf.setWaers(a_waers);
			}
			exchageRate_list = erDao.getLastCurrencyRates();
			
			if (a_waers.equals("USD")) p_bkpf.setDmbtr(a_dmbtr); 
			else
			{
				ExchangeRate selectedER = new ExchangeRate();
				for (ExchangeRate wa_er:exchageRate_list)
				{					
					if (wa_er.getType()==1 && wa_er.getSecondary_currency().equals(a_waers))
					{
						selectedER = wa_er;
						a_kursf = selectedER.getSc_value();
						a_dmbtr = GeneralUtil.round(a_wrbtr/a_kursf, 2);
						p_bkpf.setKursf(a_kursf);
						p_bkpf.setWrbtr(a_wrbtr); 
						p_bkpf.setDmbtr(a_dmbtr);
					}
					
				}
			}
			
			
				
			int wa_buzei = 0;
			wa_buzei++;
			String debetHkont="";
            Bseg wa_bseg = new Bseg();
            wa_bseg.setBukrs(p_bkpf.getBukrs());
            wa_bseg.setGjahr(p_bkpf.getGjahr());
            wa_bseg.setBuzei(wa_buzei);
            wa_bseg.setBschl("1");
			wa_bseg.setShkzg("S");
			wa_bseg.setHkont("12100001");
			wa_bseg.setLifnr(p_bkpf.getCustomer_id());			
			wa_bseg.setMatnr(a_matnr); 
			wa_bseg.setDmbtr(a_dmbtr);
			wa_bseg.setWrbtr(a_wrbtr);
			wa_bseg.setMenge(1); 
			pl_bseg.add(wa_bseg);
			
			wa_buzei++;
			wa_bseg = new Bseg();
            wa_bseg.setBukrs(p_bkpf.getBukrs());
            wa_bseg.setGjahr(p_bkpf.getGjahr());
            wa_bseg.setBuzei(wa_buzei);
            wa_bseg.setBschl("50");
			wa_bseg.setShkzg("H");
			if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==1 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100001");}//Roboclean
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==11 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100001");}//Roboclean
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==2 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100002");}//Cebilon
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==7 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100002");}//Cebilon
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==12 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100002");}//Cebilon
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==14 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100002");}//Cebilon
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==15 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100002");}//Cebilon		
			
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==3 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100001");}//Rainbow E2
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==17 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100001");}//Rainbow SRX
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==4 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100003");}//Rexwat Atlas
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==5 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100002");}//Rexwat Eco
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==10 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100002");}//Rexwat Eco Restyle 2016
			else if (a_contract.getBukrs().equals("6000")){	wa_bseg.setHkont("60100001");}//Roboclean
			else {throw new DAOException("Good type for income GL account not selected");}			
			wa_bseg.setDmbtr(a_dmbtr);
			wa_bseg.setWrbtr(a_wrbtr);
			pl_bseg.add(wa_bseg);

			
			
			
            
			if (p_bkpf.getWaers() == null || p_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (p_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (p_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			if (p_bkpf.getBusiness_area_id()==null || p_bkpf.getBusiness_area_id()==0){
				throw new DAOException("Business area not chosen");
			}
			if (p_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}

					/*
					if (wa_bseg.getStaff_id()!=null && wa_bseg.getStaff_id() > 0 && wa_bseg.getStaff_id() != a_contract.getCollector()){
						throw new DAOException("Collector and Contract collecter are not the same");
					} 

					if (wa_bseg.getLifnr() == null || wa_bseg.getLifnr() == 0 || (wa_bseg.getLifnr() != a_contract.getCustomer_id())){
						throw new DAOException("Customer is empty or not the same with the contract customer");
					}
					*/

				
					
				 
				
				
			
            for (Bseg wa_bseg2 : pl_bseg) { 
            	Bsik wa_Bsik = new Bsik(); 
            	wa_Bsik.setBukrs(wa_bseg2.getBukrs());
            	wa_Bsik.setGjahr(wa_bseg2.getGjahr());
            	wa_Bsik.setBuzei(wa_bseg2.getBuzei());
            	wa_Bsik.setBschl(wa_bseg2.getBschl());
            	wa_Bsik.setHkont(wa_bseg2.getHkont());
            	wa_Bsik.setShkzg(wa_bseg2.getShkzg());
            	wa_Bsik.setDmbtr(wa_bseg2.getDmbtr());
            	wa_Bsik.setWrbtr(wa_bseg2.getWrbtr());
            	wa_Bsik.setLifnr(wa_bseg2.getLifnr()); 
            	wa_Bsik.setMenge(wa_bseg2.getMenge());
            	wa_Bsik.setMatnr(wa_bseg2.getMatnr());            	 				
            	pl_bsik.add(wa_Bsik);
			}  
            
            financeService.check_empty_fields(p_bkpf, pl_bseg);
            Long wa_belnr =financeService.insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);
            
            createAdditionalFinDocs(contrFOList, a_branch, p_bkpf.getCustomer_id(), 
            		debetHkont, Long.parseLong(String.valueOf(p_bkpf.getBelnr())+String.valueOf(p_bkpf.getGjahr())), a_contract, curDate, cputm, a_userID,  a_tcode, p_bkpf, promoList);
          
            
            int gjahr = p_bkpf.getGjahr();
            Long wa_awkey = Long.parseLong( String.valueOf(wa_belnr)+String.valueOf(gjahr));
			return wa_awkey;
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	
	
	
	
	public void createAdditionalFinDocs(List<ContractFinOperations> a_contrFOList, Branch a_branch, Long a_customer_id, 
			String debetHkont, Long a_awkey, Contract a_contract, Calendar curDate, Time cputm, Long a_userID, String a_tcode, Bkpf new_contract_bkpf_GC, List<Promotion> promoList) throws DAOException
	{
		try{
			
			// 1 - Скидка рекомендателю SK
        	// 2 - Вознаграждение рекомендателю VZ
        	// 3 - Скидка от рекомендателя новому клиенту SR
        	// 4 - Скидка от дилера клиенту SD. DS
        	// 5 - Удержание от дилера за акционный товар AD
            //List<ContractFinOperations>
			
			if (a_contrFOList==null)
			{
				return;
			}
			
			
			//System.out.println( contrFOList.size()+" size");
   
            for(ContractFinOperations wa_cfo:a_contrFOList)
            {
            	System.out.println( "*************Operations*****************");
            	System.out.println( "Operation type: "+ wa_cfo.getOper_type());
            	System.out.println( "Currency: "+ wa_cfo.getWaers());
            	System.out.println( "Ex rate: "+ wa_cfo.getRate());
            	System.out.println( "Amount USD: "+ wa_cfo.getDmbtr());
            	System.out.println( "Amount "+ wa_cfo.getWaers() + ": "+ wa_cfo.getWrbtr());
            	System.out.println( "Awkey: "+ wa_cfo.getAwkey());
            	System.out.println( "Contract number: "+ wa_cfo.getContractNumber());
            	System.out.println( "Operation Info: "+ wa_cfo.getOperationInfo());
            	System.out.println( "Customer id: "+ wa_cfo.getCustomer_id());
            	System.out.println( "Matnr: "+ wa_cfo.getMatnr());
            	System.out.println( "Staff_id: "+ wa_cfo.getStaff_id());
            
            	//vv;
            	if (wa_cfo.getOper_type()==1)     // 1 - Скидка рекомендателю      	 SK
            	{	
            		discountToRecommender(new_contract_bkpf_GC, a_contract.getBukrs(), a_contract.getContract_number(), 
            				curDate, cputm, a_userID, a_tcode, 
            				wa_cfo.getWaers(), wa_cfo.getRate(), a_branch.getBranch_id(), a_branch.getBusiness_area_id(), 
            				wa_cfo.getOperationInfo(), null, null, 
            				wa_cfo.getCustomer_id(), wa_cfo.getAwkey(), wa_cfo.getDmbtr(), wa_cfo.getWrbtr(), wa_cfo.getMatnr());
            	}
            	else if (wa_cfo.getOper_type()==2) // 2 - Вознаграждение рекомендателю VZ
            	{
            		rewardToRecommender(new_contract_bkpf_GC, a_contract.getBukrs(), a_contract.getContract_number(), curDate, cputm, a_userID, a_tcode, 
            				wa_cfo.getWaers(), wa_cfo.getRate(), a_branch.getBranch_id(), a_branch.getBusiness_area_id(), 
            				wa_cfo.getOperationInfo(), null, null, 
            				wa_cfo.getCustomer_id(), null, wa_cfo.getDmbtr(), wa_cfo.getWrbtr());
            	}
            	else if (wa_cfo.getOper_type()==3) // 3 - Скидка от рекомендателя новому клиенту SR
            	{
            		throw new DAOException("Скидка от рекомендателя новому клиенту запрещено.");
            		//discountFromRecommender(new_contract_bkpf_GC, a_contract.getBukrs(), wa_cfo.getContractNumber(), curDate, cputm, a_userID, a_tcode, 
            		//		wa_cfo.getWaers(), wa_cfo.getRate(), a_branch.getBranch_id(), a_branch.getBusiness_area_id(),
            		//		wa_cfo.getOperationInfo(), debetHkont, hkontKredit, 
            		//		a_contract.getCustomer_id(), null, wa_cfo.getDmbtr(), wa_cfo.getWrbtr());
            		
            	}
            	else if (wa_cfo.getOper_type()==4) // 4 - Скидка от дилера клиенту SD. DS
            	{
            		discountFromDealer(new_contract_bkpf_GC, a_contract.getBukrs(), a_contract.getContract_number(), curDate, cputm, a_userID, a_tcode, 
            				wa_cfo.getWaers(), wa_cfo.getRate(), a_branch.getBranch_id(), a_branch.getBusiness_area_id(),
            				wa_cfo.getOperationInfo(), null, null, 
            				a_contract.getCustomer_id(), wa_cfo.getAwkey(), wa_cfo.getDmbtr(), wa_cfo.getWrbtr());
            		
            		chargeDealer(new_contract_bkpf_GC, a_contract.getBukrs(), a_contract.getContract_number(), curDate, cputm, a_userID, a_tcode, 
            				wa_cfo.getWaers(), wa_cfo.getRate(), a_branch.getBranch_id(), a_branch.getBusiness_area_id(),
            				wa_cfo.getOperationInfo(), null, null, 
            				wa_cfo.getCustomer_id(), null, wa_cfo.getDmbtr(), wa_cfo.getWrbtr());
            		
            	}
            	else if (wa_cfo.getOper_type()==5) // 5 - Удержание от дилера за акционный товар AD,AK
            	{
            		addPromoMaterials(new_contract_bkpf_GC, promoList, a_userID, a_tcode, wa_cfo);
            		/*bargainSaleDealer(new_contract_bkpf_GC, a_contract.getBukrs(), a_contract.getContract_number(), curDate, cputm, a_userID, a_tcode, 
            				wa_cfo.getWaers(), wa_cfo.getRate(), a_branch.getBranch_id(), a_branch.getBusiness_area_id(),
            				wa_cfo.getOperationInfo(), debetHkont, hkontKredit, 
            				wa_cfo.getCustomer_id(), wa_cfo.getAwkey(), wa_cfo.getDmbtr(), wa_cfo.getWrbtr());*/
            	}
            	else throw new DAOException("unknow oper type FinanceServiceDms, Create contract");
            	
            	/*Bseg wa_bseg_debet = new Bseg();
            	Bseg wa_bseg_kredit = new Bseg();
            	wal_bseg.clear();
            	wal_Bsik.clear();
            	wal_bsik.clear();
            	Bkpf wa_bkpf = new Bkpf();
            	wa_bseg_debet = new Bseg();
    			wa_bseg_kredit = new Bseg();
                blart="";
            	if (wa_cfo.getOper_type()==1||wa_cfo.getOper_type()==3)
            	{
            		blart = "SK"; //   wa_cfo.getOper_type()==1 //Скидка от рекомендателя новому клиенту wa_cfo.getOper_type()==3
            		wa_bseg_debet.setBschl("40");
            		wa_bseg_debet.setHkont("60300001");
            		wa_bseg_kredit.setBschl("18");
            		wa_bseg_kredit.setHkont(debetHkont);
            		wa_bseg_kredit.setLifnr(wa_cfo.getCustomer_id());
            		wa_bkpf.setAwtyp(1);
            		
            		if (wa_cfo.getOper_type()==3)
            		{
            			//wa_bkpf.setContract_number(wa_cfo.getContractNumber()); 
            			wa_bkpf.setAwkey(a_awkey);
            			wa_bkpf.setContract_number(a_contract.getContract_number()); 
            		}
            		if (wa_cfo.getOper_type()==1)
            		{
            			wa_bkpf.setContract_number(wa_cfo.getContractNumber()); 
            			wa_bkpf.setAwkey(wa_cfo.getAwkey());
            		}
            		
            	}
            	else if (wa_cfo.getOper_type()==2)
            	{
            		blart = "VZ"; // Вознаграждение рекомендателю
            		wa_bseg_debet.setBschl("40");
            		wa_bseg_debet.setHkont("60300001");
            		wa_bseg_kredit.setBschl("34");
            		wa_bseg_kredit.setHkont(hkontKredit);
            		wa_bseg_kredit.setLifnr(wa_cfo.getCustomer_id());
            		wa_bkpf.setContract_number(a_contract.getContract_number()); 
            		wa_bkpf.setAwtyp(2);
            	}
            	else if (wa_cfo.getOper_type()==4)
            	{
            		blart = "SD"; // Скидка от дилера клиенту
            		wa_bseg_debet.setBschl("4");
            		wa_bseg_debet.setHkont(debetHkont);
            		wa_bseg_kredit.setBschl("50");
            		wa_bseg_kredit.setHkont("60100030");
            		wa_bseg_kredit.setLifnr(wa_cfo.getCustomer_id());
            		wa_bkpf.setContract_number(a_contract.getContract_number());  
            		wa_bkpf.setAwtyp(1);
            	}
            	else if (wa_cfo.getOper_type()==5)
            	{
            		blart = "AD"; //Удержание от дилера за акционный товар
            		wa_bseg_debet.setBschl("4");
            		wa_bseg_debet.setHkont(debetHkont);
            		wa_bseg_kredit.setBschl("50");
            		wa_bseg_kredit.setHkont("60100030");
            		wa_bseg_kredit.setLifnr(wa_cfo.getCustomer_id());
            		wa_bkpf.setContract_number(a_contract.getContract_number());  
            		wa_bkpf.setAwtyp(1);
            	}
            	else throw new DAOException("unknow oper type FinanceServiceDms, Create contract");
            	
            	
            	wa_buzei = 0;           	
            	
            	wa_bkpf.setBukrs(a_contract.getBukrs());
            	wa_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
            	wa_bkpf.setBlart(blart);
            	wa_bkpf.setBudat(curDate.getTime());
            	wa_bkpf.setBldat(curDate.getTime());
            	wa_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
            	wa_bkpf.setCpudt(curDate.getTime());
            	wa_bkpf.setCputm(cputm); 
            	wa_bkpf.setUsnam(a_userID);
            	wa_bkpf.setTcode(a_tcode);
            	wa_bkpf.setWaers(wa_cfo.getWaers());
            	wa_bkpf.setKursf(wa_cfo.getRate()); 
            	wa_bkpf.setBrnch(a_branch.getBranch_id());
            	wa_bkpf.setBusiness_area_id(a_branch.getBusiness_area_id());
            	wa_bkpf.setBktxt(wa_cfo.getOperationInfo());
            	
            	//System.out.println();
            	wa_bkpf.setCustomer_id(wa_cfo.getCustomer_id());
    			if (wa_bkpf.getWaers().equals("USD")) wa_bkpf.setDmbtr(wa_cfo.getDmbtr()); 
    			else wa_bkpf.setWrbtr(wa_cfo.getWrbtr()); wa_bkpf.setDmbtr(wa_cfo.getWrbtr()/wa_cfo.getRate());

    			wa_buzei++;
    			
    			wa_bseg_debet.setBukrs(wa_bkpf.getBukrs());
    			wa_bseg_debet.setGjahr(wa_bkpf.getGjahr());
    			wa_bseg_debet.setBuzei(wa_buzei);
    			//wa_bseg_debet.setBschl("1");
    			wa_bseg_debet.setShkzg("S");    			
    			//wa_bseg_debet.setHkont(debetHkont);
    			//wa_bseg_debet.setLifnr(wa_bkpf.getCustomer_id());		
    			wa_bseg_debet.setDmbtr(wa_bkpf.getDmbtr());
    			wa_bseg_debet.setWrbtr(wa_bkpf.getWrbtr());
    			wal_bseg.add(wa_bseg_debet);
    			
    			wa_buzei++;
    			wa_bseg_kredit.setBukrs(wa_bkpf.getBukrs());
    			wa_bseg_kredit.setGjahr(wa_bkpf.getGjahr());
    			wa_bseg_kredit.setBuzei(wa_buzei);
    			//wa_bseg_kredit.setBschl("50");
    			wa_bseg_kredit.setShkzg("H");
    			//wa_bseg_kredit.setHkont(kreditHkont);		
    			wa_bseg_kredit.setDmbtr(wa_bkpf.getDmbtr());
    			wa_bseg_kredit.setWrbtr(wa_bkpf.getWrbtr());
    			wal_bseg.add(wa_bseg_kredit);
    			
    			
    				
    			
                
    			if (wa_bkpf.getWaers() == null || wa_bkpf.getWaers().isEmpty()){
    				throw new DAOException("Currency is empty");
    			}
    			if (wa_bkpf.getKursf() == 0){
    				throw new DAOException("Currency rate is empty");
    			}
    			if (wa_bkpf.getMonat()==0){
    				throw new DAOException("Document month not chosen");
    			}
    			if (wa_bkpf.getBusiness_area_id()==null || wa_bkpf.getBusiness_area_id()==0){
    				throw new DAOException("Business area not chosen");
    			}
    			if (wa_bkpf.getCpudt()==null){
    				throw new DAOException("System date not chosen");
    			}

    					/*
    					if (wa_bseg.getStaff_id()!=null && wa_bseg.getStaff_id() > 0 && wa_bseg.getStaff_id() != a_contract.getCollector()){
    						throw new DAOException("Collector and Contract collecter are not the same");
    					} 

    					if (wa_bseg.getLifnr() == null || wa_bseg.getLifnr() == 0 || (wa_bseg.getLifnr() != a_contract.getCustomer_id())){
    						throw new DAOException("Customer is empty or not the same with the contract customer");
    					}
    					//*

    				
    					
    				 
    			
    				
    			
                for (Bseg wa_bseg2 : wal_bseg) { 
                	
                	
                	if (wa_cfo.getOper_type()==2)
                	{
                		Bsik wa_bsik = new Bsik();
                    	wa_bsik.setGjahr(wa_bseg2.getGjahr());
                    	wa_bsik.setBukrs(wa_bseg2.getBukrs());            	
                    	wa_bsik.setBuzei(wa_bseg2.getBuzei());
                    	wa_bsik.setBschl(wa_bseg2.getBschl());
                    	wa_bsik.setHkont(wa_bseg2.getHkont());
                    	wa_bsik.setShkzg(wa_bseg2.getShkzg());
                    	wa_bsik.setDmbtr(wa_bseg2.getDmbtr());
                    	wa_bsik.setWrbtr(wa_bseg2.getWrbtr());
                    	wa_bsik.setLifnr(wa_bseg2.getLifnr());
                    	wa_bsik.setMatnr(wa_bseg2.getMatnr());
                    	wa_bsik.setWerks(wa_bseg2.getWerks());
                    	wa_bsik.setMenge(wa_bseg2.getMenge());
                    	wa_bsik.setSgtxt(wa_bseg2.getSgtxt());  				
                    	wal_bsik.add(wa_bsik);
                	}
                	else
                	{
                		Bsik wa_Bsik = new Bsik(); 
                    	wa_Bsik.setBukrs(wa_bseg2.getBukrs());
                    	wa_Bsik.setGjahr(wa_bseg2.getGjahr());
                    	wa_Bsik.setBuzei(wa_bseg2.getBuzei());
                    	wa_Bsik.setBschl(wa_bseg2.getBschl());
                    	wa_Bsik.setHkont(wa_bseg2.getHkont());
                    	wa_Bsik.setShkzg(wa_bseg2.getShkzg());
                    	wa_Bsik.setDmbtr(wa_bseg2.getDmbtr());
                    	wa_Bsik.setWrbtr(wa_bseg2.getWrbtr());
                    	wa_Bsik.setLifnr(wa_bseg2.getLifnr()); 
                    	wa_Bsik.setMenge(wa_bseg2.getMenge());
                    	wa_Bsik.setMatnr(wa_bseg2.getMatnr()); 
                    	wal_Bsik.add(wa_Bsik);
                    	//System.out.println(wa_bseg2.getBuzei());
                	}
                	
                	
                	
    			}  
                //for (Bseg wa_bseg5 : wal_bseg) {
    			//	System.out.print(wa_bseg5.getBukrs() + " ");
    			//	System.out.print(wa_bseg5.getGjahr() + " ");
    			//	System.out.print(wa_bseg5.getBuzei() + " ");
    			//	System.out.print(wa_bseg5.getBschl() + " ");
    			//	System.out.println(wa_bseg5.getDmbtr());
                //}	
                financeService.check_empty_fields(wa_bkpf, wal_bseg);      
                financeService.insertNewFiDoc(wa_bkpf, wal_bseg, wal_bsik, wal_Bsik);
                // 1 - Скидка рекомендателю
            	// 2 - Вознаграждение рекомендателю
            	// 3 - Скидка от рекомендателя новому клиенту
                
                if (wa_cfo.getOper_type()==1)
                {
                	System.out.println( "************Update PaymentSchedule*******");
                	updateContractPayment(wa_bkpf, wa_bkpf.getDmbtr(),wa_bkpf.getWrbtr(),null,wa_cfo.getMatnr(),debetHkont,curDate);   
                	System.out.println( "****************************************");
                }                
                else if (wa_cfo.getOper_type()==3||wa_cfo.getOper_type()==4)
                {
                	//System.out.println(new_contract_bkpf_GC.getDmbtr()-wa_bkpf.getDmbtr());
                	new_contract_bkpf_GC.setDmbtr_paid(new_contract_bkpf_GC.getDmbtr_paid()+wa_bkpf.getDmbtr());
                	new_contract_bkpf_GC.setWrbtr_paid(new_contract_bkpf_GC.getWrbtr_paid()+wa_bkpf.getWrbtr());
                	financeService.updateFiDoc(new_contract_bkpf_GC, null);
                }
                else if (wa_cfo.getOper_type()==5)
                {
                	System.out.println(wa_bkpf.getBlart());
                }
                System.out.println( "****************************************");
                */
            }
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	public void updateContractPayment(Bkpf p_bkpf_SK,double p_dmbtr_SK, double p_wrbtr_SK, Long payment_schedule_id, Long matnr, String debitor_hkont, Calendar curDate) throws DAOException
	{
		try
		{
			//Update Account recievable amount
			//Get the Account recievable finc doc
			//System.out.println(p_bkpf.getAwkey());
			Long wa_belnr_GC =  Long.parseLong(String.valueOf(p_bkpf_SK.getAwkey()).substring(0, 10));
        	int wa_gjahr_GC = Integer.parseInt(String.valueOf(p_bkpf_SK.getAwkey()).substring(10, 14));
        	String wa_bukrs_GC = p_bkpf_SK.getBukrs();
			String dynamicWhereClause = "";
            dynamicWhereClause = "";
            dynamicWhereClause = " belnr = "+wa_belnr_GC+" and gjahr = "+wa_gjahr_GC +" and storno=0 ";			
        	Bkpf wa_bkpf_GC = bkpfDao.dynamicFindSingleBkpf(dynamicWhereClause,wa_bukrs_GC);
        	if ((wa_bkpf_GC.getWaers().equals("USD") && wa_bkpf_GC.getDmbtr()<wa_bkpf_GC.getDmbtr_paid()+p_dmbtr_SK) ||
                	(!wa_bkpf_GC.getWaers().equals("USD") && wa_bkpf_GC.getWrbtr()<wa_bkpf_GC.getWrbtr_paid()+p_wrbtr_SK))	
            {
        		System.out.println(wa_bkpf_GC.getBelnr()+" "+wa_bkpf_GC.getDmbtr());
        		throw new DAOException("You can not overpay, FinanceService->updateContractPayment");
            }
        	
			dynamicWhereClause = "";
			if (p_bkpf_SK.getWaers().equals("USD"))
			{
				wa_bkpf_GC.setDmbtr_paid(wa_bkpf_GC.getDmbtr_paid()+p_dmbtr_SK);
				dynamicWhereClause = " dmbtr_paid = dmbtr_paid +" + p_dmbtr_SK;
			}
			else
			{
				wa_bkpf_GC.setDmbtr_paid(wa_bkpf_GC.getDmbtr_paid()+p_dmbtr_SK);
				wa_bkpf_GC.setWrbtr_paid(wa_bkpf_GC.getWrbtr_paid()+p_wrbtr_SK);
				dynamicWhereClause = " wrbtr_paid = wrbtr_paid +" + p_wrbtr_SK +", dmbtr_paid = dmbtr_paid +" + p_dmbtr_SK;
			}
			
            //if (bkpfDao.updateDynamicSingleBkpf(wa_belnr_GC, wa_gjahr_GC, dynamicWhereClause)!=1)
        	//{
        		//throw new DAOException("Account Recievable not updated");
        	//}
            
            System.out.println(((wa_bkpf_GC.getWaers().equals("USD") && wa_bkpf_GC.getDmbtr()==wa_bkpf_GC.getDmbtr_paid()) ||
            	(!wa_bkpf_GC.getWaers().equals("USD") && wa_bkpf_GC.getWrbtr()==wa_bkpf_GC.getWrbtr_paid()))	);
            
            if ((wa_bkpf_GC.getWaers().equals("USD") && wa_bkpf_GC.getDmbtr()==wa_bkpf_GC.getDmbtr_paid()) ||
            	(!wa_bkpf_GC.getWaers().equals("USD") && wa_bkpf_GC.getWrbtr()==wa_bkpf_GC.getWrbtr_paid()))	
            {
            	//System.out.println(555);
            	//If it is last payment
            	List<PaymentSchedule> l_ps = psDao.findAll(" awkey = "+ String.valueOf(wa_bkpf_GC.getBelnr())+String.valueOf(wa_bkpf_GC.getGjahr()),wa_bkpf_GC.getBukrs());
            	for (PaymentSchedule wa_ps:l_ps)
				{
            		if (wa_ps.getPaid()!=wa_ps.getSum())
            		{
            			wa_ps.setPaid(wa_ps.getSum());
                		psDao.update(wa_ps);
            		}
            		
				}
            	dynamicWhereClause = "";
				if (p_bkpf_SK.getWaers().equals("USD"))
				{
					dynamicWhereClause = " paid = paid +" + p_dmbtr_SK; 
				}
				else
				{
					dynamicWhereClause = " paid = paid +" + p_wrbtr_SK;
				}
				
				dynamicWhereClause = dynamicWhereClause + ", contract_status_id = 5";
				dynamicWhereClause = dynamicWhereClause + ", close_date = '"+GeneralUtil.getSQLDate(curDate)+"'";
				conDao.updateDynamicSingleCon(wa_bkpf_GC.getContract_number(), dynamicWhereClause);
				
				
				//Prikaz ot 31.08.17
//				List<Payroll> l_payroll_deposit = new ArrayList<Payroll>();
//				l_payroll_deposit = prlDao.findAll(" bukrs = '"+wa_bkpf_GC.getBukrs()+"' and contract_number="
//				+wa_bkpf_GC.getContract_number()+" and position_id="+dealer_pos_id+" and (bonus_type_id="+deposit+" or bldat >'"+GeneralUtil.getSQLDate(curDate)+"')");
//				
//				for (Payroll wa_payroll_deposit:l_payroll_deposit)
//				{
//					if (wa_payroll_deposit!=null)
//					{
//						wa_payroll_deposit.setPayroll_date(curDate.getTime());
//						wa_payroll_deposit.setBldat(curDate.getTime());
//						wa_payroll_deposit.setApprove(0);
//						prlDao.update(wa_payroll_deposit);
//					}
//				}
				
				
				

				bkpfDao.updateDynamicSingleBkpf(wa_belnr_GC, wa_gjahr_GC, " closed = 1",wa_bukrs_GC);
				
            }
            else
            {
            	System.out.println("******Payment Schedule***********");
        		System.out.println("Awkey: "+String.valueOf(wa_bkpf_GC.getBelnr())+String.valueOf(wa_bkpf_GC.getGjahr()));
        		System.out.println("Payment Schedule: "+payment_schedule_id);
            	//If it is NOT last payment
            	if (payment_schedule_id==null || payment_schedule_id==0)
            	{
            		
            		double amount = 0;
            		if (p_bkpf_SK.getWaers().equals("USD"))
    				{
            			amount = p_dmbtr_SK; 
    				}
    				else
    				{
    					amount = p_wrbtr_SK;
    				}
            		List<PaymentSchedule> l_ps = psDao.findAll(" awkey = "+ String.valueOf(wa_bkpf_GC.getBelnr())+String.valueOf(wa_bkpf_GC.getGjahr())+" order by ps.payment_date DESC",wa_bkpf_GC.getBukrs());
            		System.out.println("Startin amount: "+amount);
                	for (PaymentSchedule wa_ps:l_ps)
    				{
                		System.out.println("Sum: "+wa_ps.getSum());
                		System.out.println("Paid: "+wa_ps.getPaid());
                		System.out.println("Date: "+wa_ps.getPayment_date());
                		if (wa_ps.getSum()-wa_ps.getPaid()>amount)
                		{
                			wa_ps.setPaid(amount+wa_ps.getPaid());
                    		psDao.update(wa_ps);
                    		System.out.println("Condition 1, Amount: "+amount);
                    		break;
                		}
                		else if (wa_ps.getSum()-wa_ps.getPaid()==amount)
                		{
                			wa_ps.setPaid(amount+wa_ps.getPaid());
                    		psDao.update(wa_ps);
                    		System.out.println("Condition 2, Amount: "+amount);
                    		break;
                		}
                		else if (wa_ps.getSum()-wa_ps.getPaid()<amount)
                		{
                			amount = amount - (wa_ps.getSum()-wa_ps.getPaid());
                			wa_ps.setPaid(wa_ps.getSum());
                    		psDao.update(wa_ps);
                    		System.out.println("Condition 3, Amount: "+amount);
                		}
                		
    				}
                	

                	
                	System.out.println("**********************************");

    				if (p_bkpf_SK.getWaers().equals("USD"))
    				{
    					conDao.updateDynamicSingleConPaid(wa_bkpf_GC.getContract_number(), p_dmbtr_SK);
    				}
    				else
    				{
    					conDao.updateDynamicSingleConPaid(wa_bkpf_GC.getContract_number(), p_wrbtr_SK);
    				}
                	
                	
                	//System.out.println(555);
            	}
            	else
            	{
            		dynamicWhereClause = "";
    				if (p_bkpf_SK.getWaers().equals("USD"))
    				{
    					dynamicWhereClause = " paid = paid +" + p_dmbtr_SK; 
    				}
    				else
    				{
    					dynamicWhereClause = " paid = paid +" + p_wrbtr_SK;
    				}		
    				psDao.updateDynamicSinglePS(payment_schedule_id, wa_bkpf_GC.getBukrs(), dynamicWhereClause);

    				if (p_bkpf_SK.getWaers().equals("USD"))
    				{
    					conDao.updateDynamicSingleConPaid(wa_bkpf_GC.getContract_number(), p_dmbtr_SK);
    				}
    				else
    				{
    					conDao.updateDynamicSingleConPaid(wa_bkpf_GC.getContract_number(), p_wrbtr_SK);
    				}
            	}
            	
            }
            //throw new DAOException("zz");
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	
	

	
	public void discountFromRecommender(Bkpf new_contract_bkpf_GC, String a_bukrs,Long a_conNumber, Calendar curDate, Time cputm, Long a_userID, String a_tcode, 
				String a_waers, double a_kursf, Long a_branch_id, Long a_business_area_id, String a_operInfo, String a_debetHkont, String a_kreditHkont, 
				Long a_customer_id, Long a_awkey, double a_dmbtr, double a_wrbtr) throws DAOException
	{
		try{		
			//Скидка от рекомендателя новому клиенту wa_cfo.getOper_type()==3
			Bkpf wa_bkpf = new Bkpf();
			Bseg wa_bseg_debet = new Bseg();
        	Bseg wa_bseg_kredit = new Bseg();
        	List<Bseg> wal_bseg = new ArrayList<Bseg>(); 
			List<Bsik> wal_Bsik = new ArrayList<Bsik>();
			List<Bsik> wal_bsik = new ArrayList<Bsik>();        	
        	int wa_buzei = 0;           	
        	
        	wa_bkpf.setBukrs(a_bukrs);
        	wa_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
        	wa_bkpf.setBlart("SR");
        	wa_bkpf.setBudat(curDate.getTime());
        	wa_bkpf.setBldat(curDate.getTime());
        	wa_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
        	wa_bkpf.setCpudt(curDate.getTime());
        	wa_bkpf.setUsnam(a_userID);
        	wa_bkpf.setTcode(a_tcode);
        	wa_bkpf.setWaers(a_waers);
        	wa_bkpf.setKursf(a_kursf); 
        	wa_bkpf.setBrnch(a_branch_id);
        	wa_bkpf.setBusiness_area_id(a_business_area_id);
        	wa_bkpf.setBktxt(a_operInfo);
        	wa_bkpf.setContract_number(a_conNumber);
        	wa_bkpf.setAwkey(GeneralUtil.getPreparedAwkey(new_contract_bkpf_GC.getBelnr(), new_contract_bkpf_GC.getGjahr()));
        	wa_bkpf.setCustomer_id(a_customer_id);
        	wa_bkpf.setDmbtr(a_dmbtr);
        	wa_bkpf.setWrbtr(a_wrbtr);
        	wa_bkpf.setAwtyp(1);
        	
        	
        	
        	wa_buzei++;			
			wa_bseg_debet.setBukrs(wa_bkpf.getBukrs());
			wa_bseg_debet.setGjahr(wa_bkpf.getGjahr());
			wa_bseg_debet.setBuzei(wa_buzei);
        	wa_bseg_debet.setBschl("40");
    		wa_bseg_debet.setHkont("60300001");
			wa_bseg_debet.setShkzg("S");    				
			wa_bseg_debet.setDmbtr(wa_bkpf.getDmbtr());
			wa_bseg_debet.setWrbtr(wa_bkpf.getWrbtr());
			wal_bseg.add(wa_bseg_debet);
    		
    		
    		
    		wa_buzei++;	
    		wa_bseg_kredit.setBukrs(wa_bkpf.getBukrs());
			wa_bseg_kredit.setGjahr(wa_bkpf.getGjahr());
			wa_bseg_kredit.setBuzei(wa_buzei);
    		wa_bseg_kredit.setBschl("18");
    		wa_bseg_kredit.setHkont(a_debetHkont);
    		wa_bseg_kredit.setShkzg("H");
    		wa_bseg_kredit.setLifnr(a_customer_id);
    		wa_bseg_kredit.setDmbtr(wa_bkpf.getDmbtr());
			wa_bseg_kredit.setWrbtr(wa_bkpf.getWrbtr());
    		wal_bseg.add(wa_bseg_kredit);

			
			//wa_bseg_kredit.setBschl("50");
			
			//wa_bseg_kredit.setHkont(kreditHkont);		
			
			
			
			
				
			
            
			if (wa_bkpf.getWaers() == null || wa_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (wa_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (wa_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			if (wa_bkpf.getBusiness_area_id()==null || wa_bkpf.getBusiness_area_id()==0){
				throw new DAOException("Business area not chosen");
			}
			if (wa_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}

			
			
					
				 
			
				
			
            for (Bseg wa_bseg2 : wal_bseg) { 
            	
            	Bsik wa_Bsik = new Bsik(); 
            	wa_Bsik.setBukrs(wa_bseg2.getBukrs());
            	wa_Bsik.setGjahr(wa_bseg2.getGjahr());
            	wa_Bsik.setBuzei(wa_bseg2.getBuzei());
            	wa_Bsik.setBschl(wa_bseg2.getBschl());
            	wa_Bsik.setHkont(wa_bseg2.getHkont());
            	wa_Bsik.setShkzg(wa_bseg2.getShkzg());
            	wa_Bsik.setDmbtr(wa_bseg2.getDmbtr());
            	wa_Bsik.setWrbtr(wa_bseg2.getWrbtr());
            	wa_Bsik.setLifnr(wa_bseg2.getLifnr()); 
            	wa_Bsik.setMenge(wa_bseg2.getMenge());
            	wa_Bsik.setMatnr(wa_bseg2.getMatnr()); 
            	wal_Bsik.add(wa_Bsik);

            	
            	
            	
			}  
            //for (Bseg wa_bseg5 : wal_bseg) {
			//	System.out.print(wa_bseg5.getBukrs() + " ");
			//	System.out.print(wa_bseg5.getGjahr() + " ");
			//	System.out.print(wa_bseg5.getBuzei() + " ");
			//	System.out.print(wa_bseg5.getBschl() + " ");
			//	System.out.println(wa_bseg5.getDmbtr());
            //}	
            financeService.check_empty_fields(wa_bkpf, wal_bseg);      
            financeService.insertNewFiDoc(wa_bkpf, wal_bseg, wal_bsik);
            // 1 - Скидка рекомендателю
        	// 2 - Вознаграждение рекомендателю
        	// 3 - Скидка от рекомендателя новому клиенту
            
            new_contract_bkpf_GC.setDmbtr_paid(new_contract_bkpf_GC.getDmbtr_paid()+wa_bkpf.getDmbtr());
        	new_contract_bkpf_GC.setWrbtr_paid(new_contract_bkpf_GC.getWrbtr_paid()+wa_bkpf.getWrbtr());
        	financeService.updateFiDoc(new_contract_bkpf_GC, null);
        	throw new DAOException("Скидка от рекомендателя новому клиенту запрещено."); 

		}
		catch (DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	public void discountToRecommender(Bkpf new_contract_bkpf_GC, String a_bukrs,Long a_conNumber, Calendar curDate, Time cputm, Long a_userID, String a_tcode, 
			String a_waers, double a_kursf, Long a_branch_id, Long a_business_area_id, String a_operInfo, String a_debetHkont, String a_kreditHkont, 
			Long a_customer_id, Long a_awkey, double a_dmbtr, double a_wrbtr, Long a_matnr) throws DAOException
	{
		try{		
			// 1 - Скидка рекомендателю
			Bkpf wa_bkpf = new Bkpf();
			Bseg wa_bseg_debet = new Bseg();
	    	Bseg wa_bseg_kredit = new Bseg();
	    	List<Bseg> wal_bseg = new ArrayList<Bseg>(); 
			List<Bsik> wal_Bsik = new ArrayList<Bsik>();
			List<Bsik> wal_bsik = new ArrayList<Bsik>();        	
	    	int wa_buzei = 0;   
	
	    	
	    	
	
			
	    	if (!a_waers.equals("USD"))
			{
				ExchangeRate wa_rate = erDao.getLastCurrencyRate(a_waers,1);
				if (wa_rate==null)
				{
					throw new DAOException("Курс не найден. discountToRecommender");
				}
				else
				{
					wa_bkpf.setBktxt(" курс "+a_kursf);
					a_kursf = wa_rate.getSc_value();
					a_dmbtr = GeneralUtil.round(a_wrbtr/a_kursf, 2);					
				}
			}
			
			wa_bkpf.setBukrs(a_bukrs);
	    	wa_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
	    	wa_bkpf.setBlart("SK");
	    	wa_bkpf.setBudat(curDate.getTime());
	    	wa_bkpf.setBldat(curDate.getTime());
	    	wa_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
	    	wa_bkpf.setCpudt(curDate.getTime());
	    	wa_bkpf.setUsnam(a_userID);
	    	wa_bkpf.setTcode(a_tcode);
	    	wa_bkpf.setWaers(a_waers);
	    	wa_bkpf.setKursf(a_kursf); 
	    	wa_bkpf.setBrnch(a_branch_id);    	
	    	wa_bkpf.setBusiness_area_id(a_business_area_id);
	    	wa_bkpf.setBktxt(a_operInfo);
	    	wa_bkpf.setContract_number(new_contract_bkpf_GC.getContract_number());
	    	wa_bkpf.setAwkey(a_awkey);
	    	wa_bkpf.setCustomer_id(a_customer_id);
	    	wa_bkpf.setDmbtr(a_dmbtr);
	    	wa_bkpf.setWrbtr(a_wrbtr);
	    	wa_bkpf.setAwtyp(1);
	    	
	
	    	
	    	wa_buzei++;			
			wa_bseg_debet.setBukrs(wa_bkpf.getBukrs());
			wa_bseg_debet.setGjahr(wa_bkpf.getGjahr());
			wa_bseg_debet.setBuzei(wa_buzei);
	    	wa_bseg_debet.setBschl("40");
			wa_bseg_debet.setHkont("60300001");
			wa_bseg_debet.setShkzg("S");    				
			wa_bseg_debet.setDmbtr(wa_bkpf.getDmbtr());
			wa_bseg_debet.setWrbtr(wa_bkpf.getWrbtr());
			wal_bseg.add(wa_bseg_debet);
			
			
			wa_buzei++;	
			wa_bseg_kredit.setBukrs(wa_bkpf.getBukrs());
			wa_bseg_kredit.setGjahr(wa_bkpf.getGjahr());
			wa_bseg_kredit.setBuzei(wa_buzei);
			wa_bseg_kredit.setBschl("18");
			wa_bseg_kredit.setHkont("12100001");
			wa_bseg_kredit.setShkzg("H");
			wa_bseg_kredit.setLifnr(a_customer_id);
			wa_bseg_kredit.setDmbtr(wa_bkpf.getDmbtr());
			wa_bseg_kredit.setWrbtr(wa_bkpf.getWrbtr());
			wal_bseg.add(wa_bseg_kredit);
			
			
				
			
	        
			if (wa_bkpf.getWaers() == null || wa_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (wa_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (wa_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			if (wa_bkpf.getBusiness_area_id()==null || wa_bkpf.getBusiness_area_id()==0){
				throw new DAOException("Business area not chosen");
			}
			if (wa_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}
	
					/*
					if (wa_bseg.getStaff_id()!=null && wa_bseg.getStaff_id() > 0 && wa_bseg.getStaff_id() != a_contract.getCollector()){
						throw new DAOException("Collector and Contract collecter are not the same");
					} 
	
					if (wa_bseg.getLifnr() == null || wa_bseg.getLifnr() == 0 || (wa_bseg.getLifnr() != a_contract.getCustomer_id())){
						throw new DAOException("Customer is empty or not the same with the contract customer");
					}
					*/
	
				
					
				 
			
				
			
	        for (Bseg wa_bseg2 : wal_bseg) { 
	        	
	        	
	        	Bsik wa_Bsik = new Bsik(); 
	            wa_Bsik.setBukrs(wa_bseg2.getBukrs());
	            wa_Bsik.setGjahr(wa_bseg2.getGjahr());
	            wa_Bsik.setBuzei(wa_bseg2.getBuzei());
	            wa_Bsik.setBschl(wa_bseg2.getBschl());
	            wa_Bsik.setHkont(wa_bseg2.getHkont());
	            wa_Bsik.setShkzg(wa_bseg2.getShkzg());
	            wa_Bsik.setDmbtr(wa_bseg2.getDmbtr());
	            wa_Bsik.setWrbtr(wa_bseg2.getWrbtr());
	            wa_Bsik.setLifnr(wa_bseg2.getLifnr()); 
	            wa_Bsik.setMenge(wa_bseg2.getMenge());
	            wa_Bsik.setMatnr(wa_bseg2.getMatnr()); 
	            wal_Bsik.add(wa_Bsik);
	            	
	        	
			}  
	        //for (Bseg wa_bseg5 : wal_bseg) {
			//	System.out.print(wa_bseg5.getBukrs() + " ");
			//	System.out.print(wa_bseg5.getGjahr() + " ");
			//	System.out.print(wa_bseg5.getBuzei() + " ");
			//	System.out.print(wa_bseg5.getBschl() + " ");
			//	System.out.println(wa_bseg5.getDmbtr());
	        //}	
	        financeService.check_empty_fields(wa_bkpf, wal_bseg);      
	        financeService.insertNewFiDoc(wa_bkpf, wal_bseg, wal_bsik);
	        // 1 - Скидка рекомендателю
	    	// 2 - Вознаграждение рекомендателю
	    	// 3 - Скидка от рекомендателя новому клиенту
	        
	        
	        System.out.println( "************Update PaymentSchedule*******");
	        updateContractPayment(wa_bkpf, wa_bkpf.getDmbtr(),wa_bkpf.getWrbtr(),null,a_matnr,a_debetHkont,curDate);   
	        System.out.println( "****************************************");
	        
	    	
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	public void rewardToRecommender(Bkpf new_contract_bkpf_GC, String a_bukrs,Long a_conNumber, Calendar curDate, Time cputm, Long a_userID, String a_tcode, 
			String a_waers, double a_kursf, Long a_branch_id, Long a_business_area_id, String a_operInfo, String a_debetHkont, String a_kreditHkont, 
			Long a_customer_id, Long a_awkey, double a_dmbtr, double a_wrbtr) throws DAOException
	{
		try{		
			// 2 - Вознаграждение рекомендателю
			Bkpf wa_bkpf = new Bkpf();
			Bseg wa_bseg_debet = new Bseg();
			Bseg wa_bseg_kredit = new Bseg();
			List<Bseg> wal_bseg = new ArrayList<Bseg>(); 
			List<Bsik> wal_bsik = new ArrayList<Bsik>();        	
			int wa_buzei = 0;   
			
			
			
			if (!a_waers.equals("USD"))
			{
				ExchangeRate wa_rate = erDao.getLastCurrencyRate(a_waers,1);
				if (wa_rate==null)
				{
					throw new DAOException("Курс не найден. rewardToRecommender");
				}
				else
				{
					wa_bkpf.setBktxt(" курс "+a_kursf);
					a_kursf = wa_rate.getSc_value();
					a_dmbtr = GeneralUtil.round(a_wrbtr/a_kursf, 2);					
				}
			}
			else
			{
				throw new DAOException("валюта USD. rewardToRecommender");
			}
			
			
			wa_bkpf.setBukrs(a_bukrs);
	    	wa_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
	    	wa_bkpf.setBlart("VZ");
	    	wa_bkpf.setBudat(curDate.getTime());
	    	wa_bkpf.setBldat(curDate.getTime());
	    	wa_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
	    	wa_bkpf.setCpudt(curDate.getTime());
	    	wa_bkpf.setUsnam(a_userID);
	    	wa_bkpf.setTcode(a_tcode);
	    	wa_bkpf.setWaers(a_waers);
	    	wa_bkpf.setKursf(a_kursf); 
	    	wa_bkpf.setBrnch(a_branch_id);    	
	    	wa_bkpf.setBusiness_area_id(a_business_area_id);
	    	wa_bkpf.setBktxt(a_operInfo);
	    	wa_bkpf.setContract_number(a_conNumber);
	    	wa_bkpf.setAwkey(a_awkey);
	    	wa_bkpf.setCustomer_id(a_customer_id);
	    	wa_bkpf.setDmbtr(a_dmbtr);
	    	wa_bkpf.setWrbtr(a_wrbtr);
	    	wa_bkpf.setAwtyp(2);



        	
        	
    		wa_buzei++;			
			wa_bseg_debet.setBukrs(wa_bkpf.getBukrs());
			wa_bseg_debet.setGjahr(wa_bkpf.getGjahr());
			wa_bseg_debet.setBuzei(wa_buzei);
	    	wa_bseg_debet.setBschl("40");
			wa_bseg_debet.setHkont("71100030");
			wa_bseg_debet.setShkzg("S");    				
			wa_bseg_debet.setDmbtr(wa_bkpf.getDmbtr());
			wa_bseg_debet.setWrbtr(wa_bkpf.getWrbtr());
			wal_bseg.add(wa_bseg_debet);
			
			
			wa_buzei++;	
			wa_bseg_kredit.setBukrs(wa_bkpf.getBukrs());
			wa_bseg_kredit.setGjahr(wa_bkpf.getGjahr());
			wa_bseg_kredit.setBuzei(wa_buzei);
			wa_bseg_kredit.setBschl("34");
			wa_bseg_kredit.setHkont("33100001");
			wa_bseg_kredit.setShkzg("H");
			wa_bseg_kredit.setLifnr(a_customer_id);
			wa_bseg_kredit.setDmbtr(wa_bkpf.getDmbtr());
			wa_bseg_kredit.setWrbtr(wa_bkpf.getWrbtr());
			wal_bseg.add(wa_bseg_kredit);


			
			
			
				
			
            
			if (wa_bkpf.getWaers() == null || wa_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (wa_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (wa_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			if (wa_bkpf.getBusiness_area_id()==null || wa_bkpf.getBusiness_area_id()==0){
				throw new DAOException("Business area not chosen");
			}
			if (wa_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}

					/*
					if (wa_bseg.getStaff_id()!=null && wa_bseg.getStaff_id() > 0 && wa_bseg.getStaff_id() != a_contract.getCollector()){
						throw new DAOException("Collector and Contract collecter are not the same");
					} 

					if (wa_bseg.getLifnr() == null || wa_bseg.getLifnr() == 0 || (wa_bseg.getLifnr() != a_contract.getCustomer_id())){
						throw new DAOException("Customer is empty or not the same with the contract customer");
					}
					*/

				
					
				 
			
				
			
            for (Bseg wa_bseg2 : wal_bseg) { 

            		Bsik wa_bsik = new Bsik();
                	wa_bsik.setGjahr(wa_bseg2.getGjahr());
                	wa_bsik.setBukrs(wa_bseg2.getBukrs());            	
                	wa_bsik.setBuzei(wa_bseg2.getBuzei());
                	wa_bsik.setBschl(wa_bseg2.getBschl());
                	wa_bsik.setHkont(wa_bseg2.getHkont());
                	wa_bsik.setShkzg(wa_bseg2.getShkzg());
                	wa_bsik.setDmbtr(wa_bseg2.getDmbtr());
                	wa_bsik.setWrbtr(wa_bseg2.getWrbtr());
                	wa_bsik.setLifnr(wa_bseg2.getLifnr());
                	wa_bsik.setMatnr(wa_bseg2.getMatnr());
                	wa_bsik.setWerks(wa_bseg2.getWerks());
                	wa_bsik.setMenge(wa_bseg2.getMenge());
                	wa_bsik.setSgtxt(wa_bseg2.getSgtxt());  				
                	wal_bsik.add(wa_bsik);           	
            	
            	
			}  
            //for (Bseg wa_bseg5 : wal_bseg) {
			//	System.out.print(wa_bseg5.getBukrs() + " ");
			//	System.out.print(wa_bseg5.getGjahr() + " ");
			//	System.out.print(wa_bseg5.getBuzei() + " ");
			//	System.out.print(wa_bseg5.getBschl() + " ");
			//	System.out.println(wa_bseg5.getDmbtr());
            //}	
            financeService.check_empty_fields(wa_bkpf, wal_bseg);      
            financeService.insertNewFiDoc(wa_bkpf, wal_bseg, wal_bsik);
            // 1 - Скидка рекомендателю
        	// 2 - Вознаграждение рекомендателю
        	// 3 - Скидка от рекомендателя новому клиенту

		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	public void discountFromDealer(Bkpf new_contract_bkpf_GC, String a_bukrs,Long a_conNumber, Calendar curDate, Time cputm, Long a_userID, String a_tcode, 
			String a_waers, double a_kursf, Long a_branch_id, Long a_business_area_id, String a_operInfo, String a_debetHkont, String a_kreditHkont, 
			Long a_customer_id, Long a_awkey, double a_dmbtr, double a_wrbtr) throws DAOException
	{
		try{		
			// 4 - Скидка от дилера клиенту
			Bkpf wa_bkpf = new Bkpf();
			Bseg wa_bseg_debet = new Bseg();
	    	Bseg wa_bseg_kredit = new Bseg();
	    	List<Bseg> wal_bseg = new ArrayList<Bseg>(); 
			List<Bsik> wal_Bsik = new ArrayList<Bsik>();
			List<Bsik> wal_bsik = new ArrayList<Bsik>();        	
	    	int wa_buzei = 0;   		
	    	

        	
	    	if (!a_waers.equals("USD"))
			{
				ExchangeRate wa_rate = erDao.getLastCurrencyRate(a_waers,1);
				if (wa_rate==null)
				{
					throw new DAOException("Курс не найден. discountFromDealer");
				}
				else
				{
					wa_bkpf.setBktxt(" курс "+a_kursf);
					a_kursf = wa_rate.getSc_value();
					a_dmbtr = GeneralUtil.round(a_wrbtr/a_kursf, 2);					
				}
			}
	    	else
			{
				throw new DAOException("валюта USD. rewardToRecommender");
			}
        	wa_bkpf.setBukrs(a_bukrs);
	    	wa_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
	    	wa_bkpf.setBlart("SD");
	    	wa_bkpf.setBudat(curDate.getTime());
	    	wa_bkpf.setBldat(curDate.getTime());
	    	wa_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
	    	wa_bkpf.setCpudt(curDate.getTime());
	    	wa_bkpf.setUsnam(a_userID);
	    	wa_bkpf.setTcode(a_tcode);
	    	wa_bkpf.setWaers(a_waers);
	    	wa_bkpf.setKursf(a_kursf); 
	    	wa_bkpf.setBrnch(a_branch_id);    	
	    	wa_bkpf.setBusiness_area_id(a_business_area_id);
	    	wa_bkpf.setBktxt(a_operInfo);
	    	wa_bkpf.setContract_number(a_conNumber);
	    	wa_bkpf.setAwkey(GeneralUtil.getPreparedAwkey(new_contract_bkpf_GC.getBelnr(), new_contract_bkpf_GC.getGjahr()));
	    	wa_bkpf.setCustomer_id(a_customer_id);
	    	wa_bkpf.setDmbtr(a_dmbtr);
	    	wa_bkpf.setWrbtr(a_wrbtr);
	    	wa_bkpf.setAwtyp(1);




    		wa_buzei++;			
			wa_bseg_debet.setBukrs(wa_bkpf.getBukrs());
			wa_bseg_debet.setGjahr(wa_bkpf.getGjahr());
			wa_bseg_debet.setBuzei(wa_buzei);
	    	wa_bseg_debet.setBschl("40");
			wa_bseg_debet.setHkont("60300001");
			wa_bseg_debet.setShkzg("S");    			
			wa_bseg_debet.setLifnr(null);
			wa_bseg_debet.setDmbtr(wa_bkpf.getDmbtr());
			wa_bseg_debet.setWrbtr(wa_bkpf.getWrbtr());
			wal_bseg.add(wa_bseg_debet);
			

			wa_buzei++;	
			wa_bseg_kredit.setBukrs(wa_bkpf.getBukrs());
			wa_bseg_kredit.setGjahr(wa_bkpf.getGjahr());
			wa_bseg_kredit.setBuzei(wa_buzei);
			wa_bseg_kredit.setBschl("15");
			wa_bseg_kredit.setHkont("12100001");
			wa_bseg_kredit.setLifnr(a_customer_id);
			wa_bseg_kredit.setShkzg("H");
			
			wa_bseg_kredit.setDmbtr(wa_bkpf.getDmbtr());
			wa_bseg_kredit.setWrbtr(wa_bkpf.getWrbtr());
			wal_bseg.add(wa_bseg_kredit);
			
				
			
            
			if (wa_bkpf.getWaers() == null || wa_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (wa_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (wa_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			if (wa_bkpf.getBusiness_area_id()==null || wa_bkpf.getBusiness_area_id()==0){
				throw new DAOException("Business area not chosen");
			}
			if (wa_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}

					/*
					if (wa_bseg.getStaff_id()!=null && wa_bseg.getStaff_id() > 0 && wa_bseg.getStaff_id() != a_contract.getCollector()){
						throw new DAOException("Collector and Contract collecter are not the same");
					} 

					if (wa_bseg.getLifnr() == null || wa_bseg.getLifnr() == 0 || (wa_bseg.getLifnr() != a_contract.getCustomer_id())){
						throw new DAOException("Customer is empty or not the same with the contract customer");
					}
					*/

				
					
				 
			
				
			
            for (Bseg wa_bseg2 : wal_bseg) { 
            	
            	
            	Bsik wa_Bsik = new Bsik(); 
                wa_Bsik.setBukrs(wa_bseg2.getBukrs());
                wa_Bsik.setGjahr(wa_bseg2.getGjahr());
                wa_Bsik.setBuzei(wa_bseg2.getBuzei());
                wa_Bsik.setBschl(wa_bseg2.getBschl());
                wa_Bsik.setHkont(wa_bseg2.getHkont());
                wa_Bsik.setShkzg(wa_bseg2.getShkzg());
                wa_Bsik.setDmbtr(wa_bseg2.getDmbtr());
                wa_Bsik.setWrbtr(wa_bseg2.getWrbtr());
                wa_Bsik.setLifnr(wa_bseg2.getLifnr()); 
                wa_Bsik.setMenge(wa_bseg2.getMenge());
                wa_Bsik.setMatnr(wa_bseg2.getMatnr()); 
                wal_Bsik.add(wa_Bsik);
                
            	
            	
			}  
            //for (Bseg wa_bseg5 : wal_bseg) {
			//	System.out.print(wa_bseg5.getBukrs() + " ");
			//	System.out.print(wa_bseg5.getGjahr() + " ");
			//	System.out.print(wa_bseg5.getBuzei() + " ");
			//	System.out.print(wa_bseg5.getBschl() + " ");
			//	System.out.println(wa_bseg5.getDmbtr());
            //}	
            financeService.check_empty_fields(wa_bkpf, wal_bseg);      
            financeService.insertNewFiDoc(wa_bkpf, wal_bseg, wal_bsik);
            // 1 - Скидка рекомендателю
        	// 2 - Вознаграждение рекомендателю
        	// 3 - Скидка от рекомендателя новому клиенту
            
            new_contract_bkpf_GC.setDmbtr_paid(new_contract_bkpf_GC.getDmbtr_paid()+wa_bkpf.getDmbtr());
        	new_contract_bkpf_GC.setWrbtr_paid(new_contract_bkpf_GC.getWrbtr_paid()+wa_bkpf.getWrbtr());
        	financeService.updateFiDoc(new_contract_bkpf_GC, null);
			
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	public void chargeDealer(Bkpf new_contract_bkpf_GC, String a_bukrs,Long a_conNumber, Calendar curDate, Time cputm, Long a_userID, String a_tcode, 
			String a_waers, double a_kursf, Long a_branch_id, Long a_business_area_id, String a_operInfo, String a_debetHkont, String a_kreditHkont, 
			Long a_customer_id, Long a_awkey, double a_dmbtr, double a_wrbtr) throws DAOException
	{
		try{		
			// 4 - Скидка от дилера клиенту
			/*Bkpf wa_bkpf = new Bkpf();
			Bseg wa_bseg_debet = new Bseg();
	    	Bseg wa_bseg_kredit = new Bseg();
	    	List<Bseg> wal_bseg = new ArrayList<Bseg>(); 
			List<Bsik> wal_Bsik = new ArrayList<Bsik>();
			List<Bsik> wal_bsik = new ArrayList<Bsik>();        	
	    	int wa_buzei = 0;   		
	    	*/
			
			Contract con = conDao.findByContractNumber(new_contract_bkpf_GC.getContract_number());
			Calendar GC_date = Calendar.getInstance();
			GC_date.setTime(new_contract_bkpf_GC.getBudat());
			GC_date.setTime(GeneralUtil.removeTime(GC_date.getTime()));
			
			
	    	Payroll new_prl = new Payroll();
			new_prl.setGjahr(GC_date.get(Calendar.YEAR));
			new_prl.setMonat(GC_date.get(Calendar.MONTH)+1);
			new_prl.setApprove(0);
			new_prl.setPayroll_date(GC_date.getTime());
			new_prl.setBldat(GC_date.getTime());//BLDAT
			new_prl.setStaff_id(con.getDealer());
			new_prl.setBranch_id(con.getBranch_id());//BRANCH_ID
			new_prl.setContract_number(con.getContract_number());//CONTRACT_NUMBER
			new_prl.setPlan_amount(0);//PLAN_AMOUNT
			new_prl.setFact_amount(0);
			new_prl.setPosition_id((long) dealer_pos_id);
			new_prl.setMatnr_count(0);
			new_prl.setApprove(0);
			new_prl.setBukrs(con.getBukrs());
			new_prl.setDrcrk("H");
			new_prl.setWaers(a_waers);
			new_prl.setBonus_type_id(skidkaotdilera);
			new_prl.setText45("Скидка от дилера клиенту");
			if (a_waers.equals("USD"))
			{
				new_prl.setDmbtr(a_dmbtr);
			}
			else
			{
				new_prl.setDmbtr(a_wrbtr);
			}
			payrollService.createNew(new_prl,a_userID,true,a_tcode,9);

			
			

        	/*wa_bkpf.setBukrs(a_bukrs);
	    	wa_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
	    	wa_bkpf.setBlart("DS");
	    	wa_bkpf.setBudat(curDate.getTime());
	    	wa_bkpf.setBldat(curDate.getTime());
	    	wa_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
	    	wa_bkpf.setCpudt(curDate.getTime());
	    	wa_bkpf.setUsnam(a_userID);
	    	wa_bkpf.setTcode(a_tcode);
	    	wa_bkpf.setWaers(a_waers);
	    	wa_bkpf.setKursf(a_kursf); 
	    	wa_bkpf.setBrnch(a_branch_id);    	
	    	wa_bkpf.setBusiness_area_id(a_business_area_id);
	    	wa_bkpf.setBktxt(a_operInfo);
	    	wa_bkpf.setContract_number(a_conNumber);
	    	wa_bkpf.setAwkey(a_awkey);
	    	wa_bkpf.setCustomer_id(a_customer_id);
	    	wa_bkpf.setDmbtr(a_dmbtr);
	    	wa_bkpf.setWrbtr(a_wrbtr);
	    	wa_bkpf.setAwtyp(1);




    		wa_buzei++;			
			wa_bseg_debet.setBukrs(wa_bkpf.getBukrs());
			wa_bseg_debet.setGjahr(wa_bkpf.getGjahr());
			wa_bseg_debet.setBuzei(wa_buzei);
	    	wa_bseg_debet.setBschl("4");
			wa_bseg_debet.setHkont(a_debetHkont);
			wa_bseg_debet.setShkzg("S");    				
			wa_bseg_debet.setDmbtr(wa_bkpf.getDmbtr());
			wa_bseg_debet.setWrbtr(wa_bkpf.getWrbtr());
			wal_bseg.add(wa_bseg_debet);
			

			wa_buzei++;	
			wa_bseg_kredit.setBukrs(wa_bkpf.getBukrs());
			wa_bseg_kredit.setGjahr(wa_bkpf.getGjahr());
			wa_bseg_kredit.setBuzei(wa_buzei);
			wa_bseg_kredit.setBschl("50");
			wa_bseg_kredit.setHkont("60100030");
			wa_bseg_kredit.setShkzg("H");
			wa_bseg_kredit.setLifnr(a_customer_id);
			wa_bseg_kredit.setDmbtr(wa_bkpf.getDmbtr());
			wa_bseg_kredit.setWrbtr(wa_bkpf.getWrbtr());
			wal_bseg.add(wa_bseg_kredit);
			
				
			
            
			if (wa_bkpf.getWaers() == null || wa_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (wa_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (wa_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			if (wa_bkpf.getBusiness_area_id()==null || wa_bkpf.getBusiness_area_id()==0){
				throw new DAOException("Business area not chosen");
			}
			if (wa_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}

					/*
					if (wa_bseg.getStaff_id()!=null && wa_bseg.getStaff_id() > 0 && wa_bseg.getStaff_id() != a_contract.getCollector()){
						throw new DAOException("Collector and Contract collecter are not the same");
					} 

					if (wa_bseg.getLifnr() == null || wa_bseg.getLifnr() == 0 || (wa_bseg.getLifnr() != a_contract.getCustomer_id())){
						throw new DAOException("Customer is empty or not the same with the contract customer");
					}
					*/

				
					
				 
			
	    	/*	
			
            for (Bseg wa_bseg2 : wal_bseg) { 
            	
            	
            	Bsik wa_Bsik = new Bsik(); 
                wa_Bsik.setBukrs(wa_bseg2.getBukrs());
                wa_Bsik.setGjahr(wa_bseg2.getGjahr());
                wa_Bsik.setBuzei(wa_bseg2.getBuzei());
                wa_Bsik.setBschl(wa_bseg2.getBschl());
                wa_Bsik.setHkont(wa_bseg2.getHkont());
                wa_Bsik.setShkzg(wa_bseg2.getShkzg());
                wa_Bsik.setDmbtr(wa_bseg2.getDmbtr());
                wa_Bsik.setWrbtr(wa_bseg2.getWrbtr());
                wa_Bsik.setLifnr(wa_bseg2.getLifnr()); 
                wa_Bsik.setMenge(wa_bseg2.getMenge());
                wa_Bsik.setMatnr(wa_bseg2.getMatnr()); 
                wal_Bsik.add(wa_Bsik);
                
            	
            	
			}  
            //for (Bseg wa_bseg5 : wal_bseg) {
			//	System.out.print(wa_bseg5.getBukrs() + " ");
			//	System.out.print(wa_bseg5.getGjahr() + " ");
			//	System.out.print(wa_bseg5.getBuzei() + " ");
			//	System.out.print(wa_bseg5.getBschl() + " ");
			//	System.out.println(wa_bseg5.getDmbtr());
            //}	
            financeService.check_empty_fields(wa_bkpf, wal_bseg);      
            financeService.insertNewFiDoc(wa_bkpf, wal_bseg, wal_bsik, wal_Bsik);
            // 1 - Скидка рекомендателю
        	// 2 - Вознаграждение рекомендателю
        	// 3 - Скидка от рекомендателя новому клиенту
            
            */
			
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	public void bargainSaleDealer(Bkpf new_contract_bkpf_GC, String a_bukrs,Long a_conNumber, Calendar curDate, Time cputm, Long a_userID, String a_tcode, 
			String a_waers, double a_kursf, Long a_branch_id, Long a_business_area_id, String a_operInfo, String a_debetHkont, String a_kreditHkont, 
			Long a_customer_id, Long a_awkey, double a_dmbtr, double a_wrbtr) throws DAOException
	{
		try{		
			
			
			Contract con = conDao.findByContractNumber(new_contract_bkpf_GC.getContract_number());
			Calendar GC_date = Calendar.getInstance();
			GC_date.setTime(new_contract_bkpf_GC.getBudat());
			GC_date.setTime(GeneralUtil.removeTime(GC_date.getTime()));
			
			
	    	Payroll new_prl = new Payroll();
			new_prl.setGjahr(GC_date.get(Calendar.YEAR));
			new_prl.setMonat(GC_date.get(Calendar.MONTH)+1);
			new_prl.setApprove(0);
			new_prl.setPayroll_date(GC_date.getTime());
			new_prl.setBldat(GC_date.getTime());//BLDAT
			new_prl.setStaff_id(con.getDealer());
			new_prl.setBranch_id(con.getBranch_id());//BRANCH_ID
			new_prl.setContract_number(con.getContract_number());//CONTRACT_NUMBER
			new_prl.setPlan_amount(0);//PLAN_AMOUNT
			new_prl.setFact_amount(0);
			new_prl.setPosition_id((long) dealer_pos_id);
			new_prl.setMatnr_count(0);
			new_prl.setApprove(0);
			new_prl.setBukrs(con.getBukrs());
			new_prl.setDrcrk("H");
			new_prl.setWaers(a_waers);
			new_prl.setBonus_type_id(zaakciya);
			new_prl.setText45("за акционный товар");
			if (a_waers.equals("USD"))
			{
				new_prl.setDmbtr(a_dmbtr);
			}
			else
			{
				new_prl.setDmbtr(a_wrbtr);
			}
			payrollService.createNew(new_prl,a_userID,true,a_tcode,9);
			
			// 5 - Удержание от дилера за акционный товар
			/*Bkpf wa_bkpf = new Bkpf();
			Bseg wa_bseg_debet = new Bseg();
	    	Bseg wa_bseg_kredit = new Bseg();
	    	List<Bseg> wal_bseg = new ArrayList<Bseg>(); 
			List<Bsik> wal_Bsik = new ArrayList<Bsik>();
			List<Bsik> wal_bsik = new ArrayList<Bsik>();        	
	    	int wa_buzei = 0;   		

        	
        	wa_bkpf.setBukrs(a_bukrs);
	    	wa_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
	    	wa_bkpf.setBlart("AD");
	    	wa_bkpf.setBudat(curDate.getTime());
	    	wa_bkpf.setBldat(curDate.getTime());
	    	wa_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
	    	wa_bkpf.setCpudt(curDate.getTime());
	    	wa_bkpf.setUsnam(a_userID);
	    	wa_bkpf.setTcode(a_tcode);
	    	wa_bkpf.setWaers(a_waers);
	    	wa_bkpf.setKursf(a_kursf); 
	    	wa_bkpf.setBrnch(a_branch_id);    	
	    	wa_bkpf.setBusiness_area_id(a_business_area_id);
	    	wa_bkpf.setBktxt(a_operInfo);
	    	wa_bkpf.setContract_number(a_conNumber);
	    	wa_bkpf.setAwkey(null);
	    	wa_bkpf.setCustomer_id(a_customer_id);
	    	wa_bkpf.setDmbtr(a_dmbtr);
	    	wa_bkpf.setWrbtr(a_wrbtr);
	    	wa_bkpf.setAwtyp(1);



    		wa_buzei++;			
			wa_bseg_debet.setBukrs(wa_bkpf.getBukrs());
			wa_bseg_debet.setGjahr(wa_bkpf.getGjahr());
			wa_bseg_debet.setBuzei(wa_buzei);
	    	wa_bseg_debet.setBschl("4");
			wa_bseg_debet.setHkont(a_debetHkont);
			wa_bseg_debet.setShkzg("S");
			wa_bseg_debet.setLifnr(a_customer_id);
			wa_bseg_debet.setDmbtr(wa_bkpf.getDmbtr());
			wa_bseg_debet.setWrbtr(wa_bkpf.getWrbtr());
			wal_bseg.add(wa_bseg_debet);
			
			

			wa_buzei++;	
			wa_bseg_kredit.setBukrs(wa_bkpf.getBukrs());
			wa_bseg_kredit.setGjahr(wa_bkpf.getGjahr());
			wa_bseg_kredit.setBuzei(wa_buzei);
			wa_bseg_kredit.setBschl("50");
			wa_bseg_kredit.setHkont("60100030");
			wa_bseg_kredit.setShkzg("H");			
			wa_bseg_kredit.setDmbtr(wa_bkpf.getDmbtr());
			wa_bseg_kredit.setWrbtr(wa_bkpf.getWrbtr());
			wal_bseg.add(wa_bseg_kredit);
			
			
				
			
            
			if (wa_bkpf.getWaers() == null || wa_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (wa_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (wa_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			if (wa_bkpf.getBusiness_area_id()==null || wa_bkpf.getBusiness_area_id()==0){
				throw new DAOException("Business area not chosen");
			}
			if (wa_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}

					/*
					if (wa_bseg.getStaff_id()!=null && wa_bseg.getStaff_id() > 0 && wa_bseg.getStaff_id() != a_contract.getCollector()){
						throw new DAOException("Collector and Contract collecter are not the same");
					} 

					if (wa_bseg.getLifnr() == null || wa_bseg.getLifnr() == 0 || (wa_bseg.getLifnr() != a_contract.getCustomer_id())){
						throw new DAOException("Customer is empty or not the same with the contract customer");
					}
					

				
					
				 
			
				
			
            for (Bseg wa_bseg2 : wal_bseg) { 
            	
            	
            	Bsik wa_Bsik = new Bsik(); 
                wa_Bsik.setBukrs(wa_bseg2.getBukrs());
                wa_Bsik.setGjahr(wa_bseg2.getGjahr());
                wa_Bsik.setBuzei(wa_bseg2.getBuzei());
                wa_Bsik.setBschl(wa_bseg2.getBschl());
                wa_Bsik.setHkont(wa_bseg2.getHkont());
                wa_Bsik.setShkzg(wa_bseg2.getShkzg());
                wa_Bsik.setDmbtr(wa_bseg2.getDmbtr());
                wa_Bsik.setWrbtr(wa_bseg2.getWrbtr());
                wa_Bsik.setLifnr(wa_bseg2.getLifnr()); 
                wa_Bsik.setMenge(wa_bseg2.getMenge());
                wa_Bsik.setMatnr(wa_bseg2.getMatnr()); 
                wal_Bsik.add(wa_Bsik);
                //System.out.println(wa_bseg2.getBuzei());
            	
            	
			}  
            //for (Bseg wa_bseg5 : wal_bseg) {
			//	System.out.print(wa_bseg5.getBukrs() + " ");
			//	System.out.print(wa_bseg5.getGjahr() + " ");
			//	System.out.print(wa_bseg5.getBuzei() + " ");
			//	System.out.print(wa_bseg5.getBschl() + " ");
			//	System.out.println(wa_bseg5.getDmbtr());
            //}	
            
            financeService.check_empty_fields(wa_bkpf, wal_bseg);      
            financeService.insertNewFiDoc(wa_bkpf, wal_bseg, wal_bsik, wal_Bsik);*/
            // 1 - Скидка рекомендателю
        	// 2 - Вознаграждение рекомендателю
        	// 3 - Скидка от рекомендателя новому клиенту
            
			
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage()+" Bargain sale dealer-> finance service dms");
		}
	}
	
	public boolean addPromoMaterials(Bkpf a_bkpf_GC, List<Promotion> promoList, Long a_userID, String a_tcode, ContractFinOperations a_contrFO) throws DAOException
	{
		try{
			/*List<Bsik> al_Bsik = new ArrayList<Bsik>();
			al_Bsik = bsikDao.dynamicSearch("belnr = "+a_bkpf_GC.getBelnr()+" and gjahr = "+a_bkpf_GC.getGjahr()+" and bukrs = '"+a_bkpf_GC.getBukrs()+"' and hkont like '1210%'");
			if (al_Bsik.size()>1)
			{
				throw new DAOException("More than 1 hkont. addPromoMaterials");
			}*/
			

			Bkpf wa_bkpf = new Bkpf();
			List<Bseg> wal_bseg = new ArrayList<Bseg>();
			List<Bsik> wal_Bsik = new ArrayList<Bsik>();
			List<Bsik> wal_bsik = new ArrayList<Bsik>();
			Calendar curDate = Calendar.getInstance(); 
			Time cputm = new Time(curDate.getTimeInMillis()); 
			
			
			
			Bkpf a_bkpf_AK = new Bkpf();
			List<Bseg> al_bseg_AK = new ArrayList<Bseg>();
			a_bkpf_AK = bkpfDao.dynamicFindSingleBkpf("blart='AK' and contract_number="+a_bkpf_GC.getContract_number()
					+ " and storno = 0 and awkey = "+GeneralUtil.getPreparedAwkey(a_bkpf_GC.getBelnr(), a_bkpf_GC.getGjahr())+" and rownum=1 order by  budat desc,belnr desc ",a_bkpf_GC.getBukrs());
//			Bkpf a_bkpf_AK_GW = new Bkpf();
			if (a_bkpf_AK!=null && a_bkpf_AK.getBelnr()!=null)
			{
				al_bseg_AK = bsegDao.dynamicFindBseg(" belnr="+a_bkpf_AK.getBelnr()+" and gjahr="+a_bkpf_AK.getGjahr());
				financeService.cancelFiDoc(a_bkpf_AK, al_bseg_AK, a_userID, a_tcode);
				
//				a_bkpf_AK_GW = bkpfDao.dynamicFindSingleBkpf(" blart='GW' and storno = 0 and awkey = "+GeneralUtil.getPreparedAwkey(a_bkpf_AK.getBelnr(), a_bkpf_AK.getGjahr()));
//				if (a_bkpf_AK_GW==null)
//				{
//					//al_bseg_AK = bsegDao.dynamicFindBseg(" belnr="+a_bkpf_AK.getBelnr()+" and gjahr="+a_bkpf_AK.getGjahr());
//					//financeService.cancelFiDoc(a_bkpf_AK, al_bseg_AK, a_userID, a_tcode);
//				}
				
			}
			
			Contract con = conDao.findByContractNumber(a_bkpf_GC.getContract_number());
			Payroll prlAD = new Payroll();
			prlAD = prlDao.dynamicFindSinglePayroll(" bukrs ='"+a_bkpf_GC.getBukrs()+"' and contract_number="+a_bkpf_GC.getContract_number()+" and bonus_type_id="+zaakciya
					+" and staff_id="+con.getDealer());
			

			
			
			if (prlAD!=null && prlDao.dynamicFindCountPayroll(" parent_payroll_id="+prlAD.getPayroll_id())==0)
			{
				
				Payroll new_prl = new Payroll();
				new_prl.setGjahr(curDate.get(Calendar.YEAR));
				new_prl.setMonat(curDate.get(Calendar.MONTH)+1);
				new_prl.setApprove(0);
				new_prl.setPayroll_date(curDate.getTime());
				new_prl.setBldat(curDate.getTime());//BLDAT
				new_prl.setStaff_id(con.getDealer());
				new_prl.setBranch_id(con.getBranch_id());//BRANCH_ID
				new_prl.setContract_number(con.getContract_number());//CONTRACT_NUMBER
				new_prl.setPlan_amount(0);//PLAN_AMOUNT
				new_prl.setFact_amount(0);
				new_prl.setPosition_id((long) dealer_pos_id);
				new_prl.setMatnr_count(0);
				new_prl.setApprove(0);
				new_prl.setBukrs(con.getBukrs());
				new_prl.setDrcrk("S");
				new_prl.setWaers(prlAD.getWaers());
				new_prl.setDmbtr(prlAD.getDmbtr());				
				new_prl.setBonus_type_id(otmena);
				new_prl.setText45("Отмена Акция");				
				new_prl.setParent_payroll_id(prlAD.getPayroll_id());
				payrollService.createNew(new_prl,a_userID,true,a_tcode,6);
				
//				if (a_bkpf_AK_GW==null || a_bkpf_AK_GW.getAwkey2()!=null)//esli vozvrat byl
//				{
//					//financeService.cancelFiDoc(a_bkpf_AD, al_bseg_AD, a_userID, a_tcode);
//					
//					
//					/*Bkpf a_bkpf_DS = new Bkpf();
//					List<Bseg> al_bseg_DS = new ArrayList<Bseg>();
//					a_bkpf_DS = bkpfDao.dynamicFindSingleBkpf("blart='SD' and contract_number="+a_bkpf_GC.getContract_number()
//							+ " and awkey = "+GeneralUtil.getPreparedAwkey(a_bkpf_GC.getBelnr(), a_bkpf_GC.getGjahr()));*/
//					
//										
//						
//				    	Payroll new_prl = new Payroll();
//						new_prl.setGjahr(curDate.get(Calendar.YEAR));
//						new_prl.setMonat(curDate.get(Calendar.MONTH)+1);
//						new_prl.setApprove(0);
//						new_prl.setPayroll_date(curDate.getTime());
//						new_prl.setBldat(curDate.getTime());//BLDAT
//						new_prl.setStaff_id(con.getDealer());
//						new_prl.setBranch_id(con.getBranch_id());//BRANCH_ID
//						new_prl.setContract_number(con.getContract_number());//CONTRACT_NUMBER
//						new_prl.setPlan_amount(0);//PLAN_AMOUNT
//						new_prl.setFact_amount(0);
//						new_prl.setPosition_id((long) dealer_pos_id);
//						new_prl.setMatnr_count(0);
//						new_prl.setApprove(0);
//						new_prl.setBukrs(con.getBukrs());
//						new_prl.setDrcrk("S");
//						new_prl.setWaers(prlAD.getWaers());
//						new_prl.setDmbtr(prlAD.getDmbtr());				
//						new_prl.setBonus_type_id(otmena);
//						new_prl.setText45("Отмена Акция");				
//						new_prl.setParent_payroll_id(prlAD.getPayroll_id());
//						payrollService.createNew(new_prl,a_userID,true,a_tcode,6);
//					
//				}
				    				
				//financeService.cancelFiDoc(a_bkpf_AK, al_bseg_AK, a_userID);
			}
			
			if (promoList!=null && promoList.size()>0)
			{
				if (a_bkpf_GC.getBrnch()==null){
					throw new DAOException("Branch is empty");
				}
				
				if (a_userID==null || a_userID <= 0){
					throw new DAOException("User is empty");
				}
				
				if (a_tcode == null || a_tcode.isEmpty())
				{
					throw new DAOException("Transaction code is empty");
				}

				
				wa_bkpf.setBukrs(a_bkpf_GC.getBukrs());
				wa_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
				wa_bkpf.setBlart("AK");
				wa_bkpf.setBudat(curDate.getTime());
				wa_bkpf.setBldat(curDate.getTime());
				wa_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
				wa_bkpf.setCpudt(curDate.getTime());
				wa_bkpf.setUsnam(a_userID);
	            wa_bkpf.setTcode(a_tcode);
	            wa_bkpf.setWaers("USD");
	            wa_bkpf.setKursf(1); 
	            wa_bkpf.setBrnch(a_bkpf_GC.getBrnch());
	            wa_bkpf.setBusiness_area_id(a_bkpf_GC.getBusiness_area_id());
	            wa_bkpf.setContract_number(a_bkpf_GC.getContract_number()); 
	            wa_bkpf.setCustomer_id(a_bkpf_GC.getCustomer_id());
	            wa_bkpf.setAwkey(Long.parseLong(String.valueOf(a_bkpf_GC.getBelnr())+String.valueOf(a_bkpf_GC.getGjahr())));
	            wa_bkpf.setAwtyp(1);
	            wa_bkpf.setClosed(0);

					
				int wa_buzei = 0;
	            Bseg wa_bseg = new Bseg();
	            //33100001
	    		//12100001
				
				for (Promotion wa_pro:promoList)
				{
					wa_buzei++;
					wa_bseg = new Bseg();
		            wa_bseg.setBukrs(wa_bkpf.getBukrs());
		            wa_bseg.setGjahr(wa_bkpf.getGjahr());
		            wa_bseg.setBuzei(wa_buzei);
		            wa_bseg.setBschl("3");
					wa_bseg.setShkzg("S");
					wa_bseg.setHkont("12100001");
					wa_bseg.setLifnr(wa_bkpf.getCustomer_id());
					wa_bseg.setMatnr(wa_pro.getMatnr());
					wa_bseg.setDmbtr(0);
					wa_bseg.setWrbtr(0);
					wa_bseg.setMenge(1); 
					wal_bseg.add(wa_bseg);
				}
				
				wa_buzei++;
				wa_bseg = new Bseg();
	            wa_bseg.setBukrs(wa_bkpf.getBukrs());
	            wa_bseg.setGjahr(wa_bkpf.getGjahr());
	            wa_bseg.setBuzei(wa_buzei);
	            wa_bseg.setBschl("50");
				wa_bseg.setShkzg("H");
				wa_bseg.setHkont("60100030");
				wa_bseg.setDmbtr(0);
				wa_bseg.setWrbtr(0);
				wal_bseg.add(wa_bseg);
				
				
				
	            
				if (wa_bkpf.getWaers() == null || wa_bkpf.getWaers().isEmpty()){
					throw new DAOException("Currency is empty");
				}
				if (wa_bkpf.getKursf() == 0){
					throw new DAOException("Currency rate is empty");
				}
				if (wa_bkpf.getMonat()==0){
					throw new DAOException("Document month not chosen");
				}
				if (wa_bkpf.getBusiness_area_id()==null || wa_bkpf.getBusiness_area_id()==0){
					throw new DAOException("Business area not chosen");
				}
				if (wa_bkpf.getCpudt()==null){
					throw new DAOException("System date not chosen");
				}

						/*
						if (wa_bseg.getStaff_id()!=null && wa_bseg.getStaff_id() > 0 && wa_bseg.getStaff_id() != a_contract.getCollector()){
							throw new DAOException("Collector and Contract collecter are not the same");
						} 

						if (wa_bseg.getLifnr() == null || wa_bseg.getLifnr() == 0 || (wa_bseg.getLifnr() != a_contract.getCustomer_id())){
							throw new DAOException("Customer is empty or not the same with the contract customer");
						}
						*/

					
						
					 
					
					
				
	            for (Bseg wa_bseg2 : wal_bseg) { 
	            	Bsik wa_Bsik = new Bsik(); 
	            	wa_Bsik.setBukrs(wa_bseg2.getBukrs());
	            	wa_Bsik.setGjahr(wa_bseg2.getGjahr());
	            	wa_Bsik.setBuzei(wa_bseg2.getBuzei());
	            	wa_Bsik.setBschl(wa_bseg2.getBschl());
	            	wa_Bsik.setHkont(wa_bseg2.getHkont());
	            	wa_Bsik.setShkzg(wa_bseg2.getShkzg());
	            	wa_Bsik.setDmbtr(wa_bseg2.getDmbtr());
	            	wa_Bsik.setWrbtr(wa_bseg2.getWrbtr());
	            	wa_Bsik.setLifnr(wa_bseg2.getLifnr()); 
	            	wa_Bsik.setMenge(wa_bseg2.getMenge());
	            	wa_Bsik.setMatnr(wa_bseg2.getMatnr());            	 				
	            	wal_Bsik.add(wa_Bsik);
				}  
	            
	            financeService.check_empty_fields(wa_bkpf, wal_bseg);
	            financeService.insertNewFiDoc(wa_bkpf, wal_bseg, wal_bsik);
	            
	            
	            
			}

            
			

            if (promoList!=null && promoList.size()>0 && a_contrFO!=null && (a_contrFO.getDmbtr()>0 || a_contrFO.getWrbtr()>0))
            {
            	
            	bargainSaleDealer(a_bkpf_GC, a_bkpf_GC.getBukrs(), a_bkpf_GC.getContract_number(), curDate, cputm, a_userID, a_tcode, 
                		a_contrFO.getWaers(), a_contrFO.getRate(), a_bkpf_GC.getBrnch(), a_bkpf_GC.getBusiness_area_id(),
                		a_contrFO.getOperationInfo(), null, null, 
                		a_contrFO.getCustomer_id(), a_contrFO.getAwkey(), a_contrFO.getDmbtr(), a_contrFO.getWrbtr());
            }
            
			return true;
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage()+" Add promo materials-> finance service dms");
		}
	
	}
	public boolean addDiscountToRecommender(Bkpf a_bkpf_GC, Long a_userID, String a_tcode, ContractFinOperations a_contrFO) throws DAOException
	{
		try{
			/*List<Bsik> al_Bsik = new ArrayList<Bsik>();
			al_Bsik = bsikDao.dynamicSearch("belnr = "+a_bkpf_GC.getBelnr()+" and gjahr = "+a_bkpf_GC.getGjahr()+" and bukrs = '"+a_bkpf_GC.getBukrs()+"' and hkont like '1210%'");
			if (al_Bsik.size()>1)
			{
				throw new DAOException("More than 1 hkont. addDiscountToRecommender");
			}*/
			

			Calendar curDate = Calendar.getInstance(); 
			Time cputm = new Time(curDate.getTimeInMillis()); 
			
			Bkpf a_bkpf_SK = new Bkpf();
			List<Bseg> al_bseg_SK = new ArrayList<Bseg>();
			if (a_contrFO==null || a_contrFO.getAwkey()==null)
			{
				a_bkpf_SK = bkpfDao.dynamicFindSingleBkpf("blart='SK' and contract_number="+a_bkpf_GC.getContract_number()
						+ " and storno=0",a_bkpf_GC.getBukrs());
			}
			else
			{	
				a_bkpf_SK = bkpfDao.dynamicFindSingleBkpf("blart='SK' and contract_number="+a_bkpf_GC.getContract_number()
					+ " and storno=0 and awkey = "+a_contrFO.getAwkey(),a_bkpf_GC.getBukrs());
			}
			if (a_bkpf_SK!=null)
			{
				al_bseg_SK = bsegDao.dynamicFindBseg(" belnr="+a_bkpf_SK.getBelnr()+" and gjahr="+a_bkpf_SK.getGjahr());
				financeService.cancelFiDoc(a_bkpf_SK, al_bseg_SK, a_userID, a_tcode);
				counterUpdateContractPayment(a_bkpf_SK, a_bkpf_SK.getDmbtr(), a_bkpf_SK.getWrbtr(),  a_userID, a_tcode);
				
			}
			 
			

			if (a_contrFO!=null)
			{
				discountToRecommender(a_bkpf_GC, a_bkpf_GC.getBukrs(), a_bkpf_GC.getContract_number(), 
        				curDate, cputm, a_userID, a_tcode, 
        				a_contrFO.getWaers(), a_contrFO.getRate(), a_bkpf_GC.getBrnch(), a_bkpf_GC.getBusiness_area_id(), 
        				a_contrFO.getOperationInfo(), null, null, 
        				a_contrFO.getCustomer_id(), a_contrFO.getAwkey(), 
        				a_contrFO.getDmbtr(), a_contrFO.getWrbtr(), a_contrFO.getMatnr());
			}
            //int gjahr = wa_bkpf.getGjahr();
            //Long wa_awkey = null;// = Long.parseLong( String.valueOf(wa_belnr)+String.valueOf(gjahr));
			return true;
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	
	}
	public void counterUpdateContractPayment(Bkpf p_bkpf_SK,double p_dmbtr_SK, double p_wrbtr_SK,  Long a_userID,String a_tcode) throws DAOException
	{
		try
		{
			//Update Account recievable amount
			//Get the Account recievable finc doc
			//System.out.println(p_bkpf.getAwkey());
			Long wa_belnr_GC = GeneralUtil.getPreparedBelnr(p_bkpf_SK.getAwkey());
        	int wa_gjahr_GC = GeneralUtil.getPreparedGjahr(p_bkpf_SK.getAwkey());
        	String wa_bukrs_GC = p_bkpf_SK.getBukrs();
			String dynamicWhereClause = "";
            dynamicWhereClause = "";
            dynamicWhereClause = " belnr = "+wa_belnr_GC+" and gjahr = "+wa_gjahr_GC;			
        	Bkpf wa_bkpf_GC = bkpfDao.dynamicFindSingleBkpf(dynamicWhereClause,wa_bukrs_GC);
        	boolean closed = false;
        	if (wa_bkpf_GC.getWaers().equals("USD") && wa_bkpf_GC.getDmbtr()<=wa_bkpf_GC.getDmbtr_paid())
        	{
        		closed = true;
        	}
        	else if (!wa_bkpf_GC.getWaers().equals("USD") && wa_bkpf_GC.getWrbtr()<=wa_bkpf_GC.getWrbtr_paid())
        	{
        		closed = true;
        	}
        	
			dynamicWhereClause = "";
			if (p_bkpf_SK.getWaers().equals("USD"))
			{
				dynamicWhereClause = " dmbtr_paid = dmbtr_paid -" + p_dmbtr_SK;
			}
			else
			{
				dynamicWhereClause = " wrbtr_paid = wrbtr_paid -" + p_wrbtr_SK +", dmbtr_paid = dmbtr_paid -" + p_dmbtr_SK;
			}
			
            if (bkpfDao.updateDynamicSingleBkpf(wa_belnr_GC, wa_gjahr_GC, dynamicWhereClause,wa_bukrs_GC)!=1)
        	{
        		throw new DAOException("Account Recievable not updated");
        	}
            
            
            if (closed)
            {
            	System.out.println("Last payment canceled");
            	List<PaymentSchedule> l_ps = psDao.findAll(" awkey = "+ GeneralUtil.getPreparedAwkey(wa_bkpf_GC.getBelnr(), wa_bkpf_GC.getGjahr())+" order by ps.payment_date DESC",wa_bkpf_GC.getBukrs());
            	double amount = 0;
        		if (p_bkpf_SK.getWaers().equals("USD"))
				{
        			amount = p_dmbtr_SK; 
				}
				else
				{
					amount = p_wrbtr_SK;
				}
        		
        		System.out.println("Starting amount: "+amount);
            	for (PaymentSchedule wa_ps:l_ps)
				{
            		System.out.println("Sum: "+wa_ps.getSum());
            		System.out.println("Paid: "+wa_ps.getPaid());
            		System.out.println("Date: "+wa_ps.getPayment_date());
            		if (wa_ps.getPaid()>amount)
            		{
            			wa_ps.setPaid(wa_ps.getPaid()-amount);
            			psDao.update(wa_ps);
                		System.out.println("Condition 1, Amount: "+amount);
                		break;
            		}
            		else if (wa_ps.getPaid()==amount)
            		{
            			wa_ps.setPaid(0);
            			psDao.update(wa_ps);
                		System.out.println("Condition 2, Amount: "+amount);
                		break;
            		}
            		else if (wa_ps.getPaid()<amount)
            		{
            			amount = amount - (wa_ps.getPaid());
            			wa_ps.setPaid(0);
            			psDao.update(wa_ps);
                		System.out.println("Condition 3, Amount: "+amount);
            		}
            		
				}
            	
            	
            	
            	dynamicWhereClause = "";
				if (p_bkpf_SK.getWaers().equals("USD"))
				{
					dynamicWhereClause = " paid = paid -" + p_dmbtr_SK; 
				}
				else
				{
					dynamicWhereClause = " paid = paid -" + p_wrbtr_SK;
				}
				
				dynamicWhereClause = dynamicWhereClause + ", contract_status_id = 1";
				dynamicWhereClause = dynamicWhereClause + ", close_date = null";
				conDao.updateDynamicSingleCon(wa_bkpf_GC.getContract_number(), dynamicWhereClause);
				
            	
				/*for (PaymentScheduleArc wa_psa:l_psa)
				{
					PaymentSchedule wa_ps = new PaymentSchedule();
					wa_ps.setBukrs(wa_psa.getBukrs());
					wa_ps.setAwkey(wa_psa.getAwkey());
					wa_ps.setPaid(wa_psa.getPaid());
					wa_ps.setSum(wa_psa.getSum());
					wa_ps.setPayment_date(wa_psa.getPayment_date());
					psDao.create(wa_ps);

				}
				psArcDao.deleteByAwkey(GeneralUtil.getPreparedAwkey(wa_bkpf_GC.getBelnr(), wa_bkpf_GC.getGjahr()));*/
				
            }
            
            else
            {
            	System.out.println("******Payment Schedule***********");
        		System.out.println("Awkey: "+String.valueOf(wa_bkpf_GC.getBelnr())+String.valueOf(wa_bkpf_GC.getGjahr()));
            	//If it is NOT last payment
        		double amount = 0;
        		if (p_bkpf_SK.getWaers().equals("USD"))
				{
        			amount = p_dmbtr_SK; 
				}
				else
				{
					amount = p_wrbtr_SK;
				}
        		
        		List<PaymentSchedule> l_ps = psDao.findAll(" awkey = "+ GeneralUtil.getPreparedAwkey(wa_bkpf_GC.getBelnr(), wa_bkpf_GC.getGjahr())+" order by ps.payment_date DESC",wa_bkpf_GC.getBukrs());
        		System.out.println("Starting amount: "+amount);
            	for (PaymentSchedule wa_ps:l_ps)
				{
            		System.out.println("Sum: "+wa_ps.getSum());
            		System.out.println("Paid: "+wa_ps.getPaid());
            		System.out.println("Date: "+wa_ps.getPayment_date());
            		if (wa_ps.getPaid()>amount)
            		{
            			wa_ps.setPaid(wa_ps.getPaid()-amount);
                		psDao.update(wa_ps);
                		System.out.println("Condition 1, Amount: "+amount);
                		break;
            		}
            		else if (wa_ps.getPaid()==amount)
            		{
            			wa_ps.setPaid(0);
                		psDao.update(wa_ps);
                		System.out.println("Condition 2, Amount: "+amount);
                		break;
            		}
            		else if (wa_ps.getPaid()<amount)
            		{
            			amount = amount - (wa_ps.getPaid());
            			wa_ps.setPaid(0);
                		psDao.update(wa_ps);
                		System.out.println("Condition 3, Amount: "+amount);
            		}
            		
				}
            	

            	
            	System.out.println("**********************************");

				/*if (p_bkpf_SK.getWaers().equals("USD"))
				{
					
					conDao.updateDynamicSingleConPaid(wa_bkpf_GC.getContract_number(), p_dmbtr_SK);
				}
				else
				{
					conDao.updateDynamicSingleConPaid(wa_bkpf_GC.getContract_number(), p_wrbtr_SK);
				}*/
				dynamicWhereClause = "";
				if (p_bkpf_SK.getWaers().equals("USD"))
				{
					dynamicWhereClause = " paid = paid -" + p_dmbtr_SK; 
				}
				else
				{
					dynamicWhereClause = " paid = paid -" + p_wrbtr_SK;
				}
				
				conDao.updateDynamicSingleCon(wa_bkpf_GC.getContract_number(), dynamicWhereClause);
            	
            }
            //throw new DAOException("zz");
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	public boolean addRewardToRecommender(Bkpf a_bkpf_GC, Long a_userID,String a_tcode, ContractFinOperations a_contrFO) throws DAOException
	{
		try{//VZ
			/*List<Bsik> al_Bsik = new ArrayList<Bsik>();
			al_Bsik = bsikDao.dynamicSearch("belnr = "+a_bkpf_GC.getBelnr()+" and gjahr = "+a_bkpf_GC.getGjahr()+" and bukrs = '"+a_bkpf_GC.getBukrs()+"' and hkont like '1210%'");
			if (al_Bsik.size()>1)
			{
				throw new DAOException("More than 1 hkont. addRewardToRecommender");
			}*/

			Calendar curDate = Calendar.getInstance(); 
			Time cputm = new Time(curDate.getTimeInMillis()); 
			//String hkontKredit = "";
			Bkpf a_bkpf_VZ = new Bkpf();
			//List<Bseg> al_bseg_VZ = new ArrayList<Bseg>();
			a_bkpf_VZ = bkpfDao.dynamicFindSingleBkpf("blart='VZ' and storno = 0 and closed=0 and contract_number="+a_bkpf_GC.getContract_number(),a_bkpf_GC.getBukrs());
			
			/*if (a_bkpf_VZ!=null)
			{	
				List<Bsik> al_bsik = new ArrayList<Bsik>();
				al_bsik = bsikDao.dynamicSearch("belnr = "+a_bkpf_VZ.getBelnr()+" and gjahr = "+a_bkpf_VZ.getGjahr()+" and bukrs = '"+a_bkpf_VZ.getBukrs()+"' and hkont like '3310%'");
				if (al_bsik.size()>1)
				{
					throw new DAOException("More than 1 hkont. addRewardToRecommender");
				}
				hkontKredit = al_bsik.get(0).getHkont();
				
			}
			else
			{
				
			}
			*/
			if (a_bkpf_VZ!=null)
			{
				
					Bkpf wa_bkpf = new Bkpf();				
					Bseg wa_bseg_debet = new Bseg();
					Bseg wa_bseg_kredit = new Bseg();
					List<Bseg> wal_bseg = new ArrayList<Bseg>(); 
					List<Bsik> wal_Bsik = new ArrayList<Bsik>();
					List<Bsik> wal_bsik = new ArrayList<Bsik>();        	
					int wa_buzei = 0;   
					
					wa_bkpf.setBukrs(a_bkpf_VZ.getBukrs());
			    	wa_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			    	wa_bkpf.setBlart("VV");
			    	wa_bkpf.setBudat(curDate.getTime());
			    	wa_bkpf.setBldat(curDate.getTime());
			    	wa_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
			    	wa_bkpf.setCpudt(curDate.getTime());
			    	wa_bkpf.setUsnam(a_userID);
			    	wa_bkpf.setTcode(a_tcode);
			    	wa_bkpf.setBrnch(a_bkpf_VZ.getBrnch());    	
			    	wa_bkpf.setBusiness_area_id(a_bkpf_VZ.getBusiness_area_id());
			    	wa_bkpf.setBktxt("Возврат вознграждение SN:"+a_bkpf_VZ.getContract_number());
			    	wa_bkpf.setContract_number(a_bkpf_VZ.getContract_number());
			    	wa_bkpf.setAwkey(null);
			    	wa_bkpf.setCustomer_id(a_bkpf_VZ.getCustomer_id());
			    	wa_bkpf.setAwtyp(2);
			    	wa_bkpf.setWaers(a_bkpf_VZ.getWaers());
			    	wa_bkpf.setKursf(a_bkpf_VZ.getKursf());			    	
			    	wa_bkpf.setDmbtr(a_bkpf_VZ.getDmbtr());
			    	wa_bkpf.setWrbtr(a_bkpf_VZ.getWrbtr());
			    	wa_bkpf.setAwkey(GeneralUtil.getPreparedAwkey(a_bkpf_VZ.getBelnr(), a_bkpf_VZ.getGjahr()));


			    	
		        	
		        	
		    		wa_buzei++;			
					wa_bseg_debet.setBukrs(wa_bkpf.getBukrs());
					wa_bseg_debet.setGjahr(wa_bkpf.getGjahr());
					wa_bseg_debet.setBuzei(wa_buzei);
			    	wa_bseg_debet.setBschl("24");
					wa_bseg_debet.setHkont("33100001");
					wa_bseg_debet.setLifnr(a_bkpf_VZ.getCustomer_id());
					wa_bseg_debet.setShkzg("S");    				
					wa_bseg_debet.setDmbtr(wa_bkpf.getDmbtr());
					wa_bseg_debet.setWrbtr(wa_bkpf.getWrbtr());
					wal_bseg.add(wa_bseg_debet);
					
					
					wa_buzei++;	
					wa_bseg_kredit.setBukrs(wa_bkpf.getBukrs());
					wa_bseg_kredit.setGjahr(wa_bkpf.getGjahr());
					wa_bseg_kredit.setBuzei(wa_buzei);
					wa_bseg_kredit.setBschl("50");
					wa_bseg_kredit.setHkont("71100030");
					wa_bseg_kredit.setShkzg("H");
					wa_bseg_kredit.setDmbtr(wa_bkpf.getDmbtr());
					wa_bseg_kredit.setWrbtr(wa_bkpf.getWrbtr());
					wal_bseg.add(wa_bseg_kredit);


					
					
					
						
					
		            
					if (wa_bkpf.getWaers() == null || wa_bkpf.getWaers().isEmpty()){
						throw new DAOException("Currency is empty");
					}
					if (wa_bkpf.getKursf() == 0){
						throw new DAOException("Currency rate is empty");
					}
					if (wa_bkpf.getMonat()==0){
						throw new DAOException("Document month not chosen");
					}
					if (wa_bkpf.getBusiness_area_id()==null || wa_bkpf.getBusiness_area_id()==0){
						throw new DAOException("Business area not chosen");
					}
					if (wa_bkpf.getCpudt()==null){
						throw new DAOException("System date not chosen");
					}

							/*
							if (wa_bseg.getStaff_id()!=null && wa_bseg.getStaff_id() > 0 && wa_bseg.getStaff_id() != a_contract.getCollector()){
								throw new DAOException("Collector and Contract collecter are not the same");
							} 

							if (wa_bseg.getLifnr() == null || wa_bseg.getLifnr() == 0 || (wa_bseg.getLifnr() != a_contract.getCustomer_id())){
								throw new DAOException("Customer is empty or not the same with the contract customer");
							}
							*/

						
							
						 
					
						
					
		            for (Bseg wa_bseg2 : wal_bseg) { 

		            		Bsik wa_Bsik = new Bsik();
		            		wa_Bsik.setGjahr(wa_bseg2.getGjahr());
		            		wa_Bsik.setBukrs(wa_bseg2.getBukrs());            	
		            		wa_Bsik.setBuzei(wa_bseg2.getBuzei());
		            		wa_Bsik.setBschl(wa_bseg2.getBschl());
		            		wa_Bsik.setHkont(wa_bseg2.getHkont());
		            		wa_Bsik.setShkzg(wa_bseg2.getShkzg());
		            		wa_Bsik.setDmbtr(wa_bseg2.getDmbtr());
		            		wa_Bsik.setWrbtr(wa_bseg2.getWrbtr());
		            		wa_Bsik.setLifnr(wa_bseg2.getLifnr());
		            		wa_Bsik.setMatnr(wa_bseg2.getMatnr());
		            		wa_Bsik.setWerks(wa_bseg2.getWerks());
		            		wa_Bsik.setMenge(wa_bseg2.getMenge());
		            		wa_Bsik.setSgtxt(wa_bseg2.getSgtxt());  				
		                	wal_Bsik.add(wa_Bsik);           	
		            	
		            	
					}  
		            //for (Bseg wa_bseg5 : wal_bseg) {
					//	System.out.print(wa_bseg5.getBukrs() + " ");
					//	System.out.print(wa_bseg5.getGjahr() + " ");
					//	System.out.print(wa_bseg5.getBuzei() + " ");
					//	System.out.print(wa_bseg5.getBschl() + " ");
					//	System.out.println(wa_bseg5.getDmbtr());
		            //}	
		            financeService.check_empty_fields(wa_bkpf, wal_bseg);      
		            financeService.insertNewFiDoc(wa_bkpf, wal_bseg, wal_bsik);
					
		            bkpfDao.updateDynamicSingleBkpf(a_bkpf_VZ.getBelnr(), a_bkpf_VZ.getGjahr(), "closed = 1",a_bkpf_VZ.getBukrs());
				
				
			}
					
			if (a_contrFO!=null)
			{
				
				rewardToRecommender(a_bkpf_GC, a_bkpf_GC.getBukrs(), a_bkpf_GC.getContract_number(), curDate, cputm, a_userID, a_tcode, 
						a_contrFO.getWaers(), a_contrFO.getRate(), a_bkpf_GC.getBrnch(), a_bkpf_GC.getBusiness_area_id(), 
						a_contrFO.getOperationInfo(),  null, null,
						a_contrFO.getCustomer_id(), null, a_contrFO.getDmbtr(), a_contrFO.getWrbtr());
			}
			
			
			return true;
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	/*public boolean addDiscountFromRecommender(Bkpf a_bkpf_GC, Long a_userID,String a_tcode, ContractFinOperations a_contrFO) throws DAOException
	{
		try{//VZ
			List<Bsik> al_Bsik = new ArrayList<Bsik>();
			al_Bsik = bsikDao.dynamicSearch("belnr = "+a_bkpf_GC.getBelnr()+" and gjahr = "+a_bkpf_GC.getGjahr()+" and bukrs = '"+a_bkpf_GC.getBukrs()+"' and hkont like '1210%'");
			if (al_Bsik.size()>1)
			{
				throw new DAOException("More than 1 hkont. addDiscountToRecommender");
			}
			
			Map<Long,Branch> l_brn_map = new  HashMap<Long,Branch>();
			for(Branch wa_brn:branchDao.findByBukrs(a_bkpf_GC.getBukrs()))
			{
				l_brn_map.put(wa_brn.getBranch_id(), wa_brn);
			}
			String hkontKredit= GeneralUtil.getCreditorGLA(l_brn_map, a_bkpf_GC.getBrnch(), a_bkpf_GC.getCustomer_id());
			
			Calendar curDate = Calendar.getInstance(); 
			Time cputm = new Time(curDate.getTimeInMillis()); 
			
			Bkpf a_bkpf_SR = new Bkpf();
			List<Bseg> al_bseg_SR = new ArrayList<Bseg>();
			a_bkpf_SR = bkpfDao.dynamicFindSingleBkpf("blart='SR' and contract_number="+a_contrFO.getContractNumber()
					+ " and awkey = "+GeneralUtil.getPreparedAwkey(a_bkpf_GC.getBelnr(), a_bkpf_GC.getGjahr()));
			if (a_bkpf_SR!=null)
			{
				al_bseg_SR = bsegDao.dynamicFindBseg(" belnr="+a_bkpf_SR.getBelnr()+" and gjahr="+a_bkpf_SR.getGjahr());
				financeService.cancelFiDoc(a_bkpf_SR, al_bseg_SR, a_userID, a_tcode);
				a_bkpf_GC.setDmbtr_paid(a_bkpf_GC.getDmbtr_paid()-a_bkpf_SR.getDmbtr());
				a_bkpf_GC.setWrbtr_paid(a_bkpf_GC.getWrbtr_paid()-a_bkpf_SR.getWrbtr());
	        	financeService.updateFiDoc(a_bkpf_GC, null);
			}
			
			if (a_contrFO!=null)
			{
				discountFromRecommender(a_bkpf_GC, a_bkpf_GC.getBukrs(), a_contrFO.getContractNumber(), curDate, cputm, a_userID, a_tcode, 
						a_contrFO.getWaers(), a_contrFO.getRate(), a_bkpf_GC.getBrnch(), a_bkpf_GC.getBusiness_area_id(),
						a_contrFO.getOperationInfo(), al_Bsik.get(0).getHkont(), null, 
						a_bkpf_GC.getCustomer_id(), GeneralUtil.getPreparedAwkey(a_bkpf_GC.getBelnr(), a_bkpf_GC.getGjahr()), a_contrFO.getDmbtr(), a_contrFO.getWrbtr());
			}
			
			
			return true;
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}*/
	
	public boolean addDiscountFromDealer(Bkpf a_bkpf_GC, Long a_userID,String a_tcode, ContractFinOperations a_contrFO) throws DAOException{
		try{
			//SD, DS
			Calendar curDate = Calendar.getInstance(); 
			Time cputm = new Time(curDate.getTimeInMillis()); 
			
			Bkpf a_bkpf_SD = new Bkpf();
			List<Bseg> al_bseg_SD = new ArrayList<Bseg>();
			a_bkpf_SD = bkpfDao.dynamicFindSingleBkpf("blart='SD' and contract_number="+a_bkpf_GC.getContract_number()
					+ " and storno = 0 and awkey = "+GeneralUtil.getPreparedAwkey(a_bkpf_GC.getBelnr(), a_bkpf_GC.getGjahr()),a_bkpf_GC.getBukrs());
			
			
			
			if (a_bkpf_SD!=null)
			{
				al_bseg_SD = bsegDao.dynamicFindBseg(" belnr="+a_bkpf_SD.getBelnr()+" and gjahr="+a_bkpf_SD.getGjahr());
				financeService.cancelFiDoc(a_bkpf_SD, al_bseg_SD, a_userID, a_tcode);
				a_bkpf_GC.setDmbtr_paid(a_bkpf_GC.getDmbtr_paid()-a_bkpf_SD.getDmbtr());
				a_bkpf_GC.setWrbtr_paid(a_bkpf_GC.getWrbtr_paid()-a_bkpf_SD.getWrbtr());
	        	financeService.updateFiDoc(a_bkpf_GC, null);
			}
			
			Contract con = conDao.findByContractNumber(a_bkpf_GC.getContract_number());
			Payroll prlDS = new Payroll();
			prlDS = prlDao.dynamicFindSinglePayroll(" bukrs ='"+a_bkpf_GC.getBukrs()+"' and contract_number="+a_bkpf_GC.getContract_number()+" and bonus_type_id="+skidkaotdilera
					+" and staff_id="+con.getDealer());
			
			/*Bkpf a_bkpf_DS = new Bkpf();
			List<Bseg> al_bseg_DS = new ArrayList<Bseg>();
			a_bkpf_DS = bkpfDao.dynamicFindSingleBkpf("blart='SD' and contract_number="+a_bkpf_GC.getContract_number()
					+ " and awkey = "+GeneralUtil.getPreparedAwkey(a_bkpf_GC.getBelnr(), a_bkpf_GC.getGjahr()));*/
			
			if (prlDS!=null)
			{
								
				
		    	Payroll new_prl = new Payroll();
				new_prl.setGjahr(curDate.get(Calendar.YEAR));
				new_prl.setMonat(curDate.get(Calendar.MONTH)+1);
				new_prl.setApprove(0);
				new_prl.setPayroll_date(curDate.getTime());
				new_prl.setBldat(curDate.getTime());//BLDAT
				new_prl.setStaff_id(con.getDealer());
				new_prl.setBranch_id(con.getBranch_id());//BRANCH_ID
				new_prl.setContract_number(con.getContract_number());//CONTRACT_NUMBER
				new_prl.setPlan_amount(0);//PLAN_AMOUNT
				new_prl.setFact_amount(0);
				new_prl.setPosition_id((long) dealer_pos_id);
				new_prl.setMatnr_count(0);
				new_prl.setApprove(0);
				new_prl.setBukrs(con.getBukrs());
				new_prl.setDrcrk("S");
				new_prl.setWaers(prlDS.getWaers());
				new_prl.setDmbtr(prlDS.getDmbtr());				
				new_prl.setBonus_type_id(otmena);
				new_prl.setText45("Отмена Скидка");				
				new_prl.setParent_payroll_id(prlDS.getPayroll_id());
				payrollService.createNew(new_prl,a_userID,true,a_tcode,6);
				
				/*
				al_bseg_DS = bsegDao.dynamicFindBseg(" belnr="+a_bkpf_DS.getBelnr()+" and gjahr="+a_bkpf_DS.getGjahr());
				if (a_bkpf_DS.getClosed()==0 && a_bkpf_DS.getDmbtr_paid()==0 && a_bkpf_DS.getWrbtr_paid()==0)
				{
					financeService.cancelFiDoc(a_bkpf_DS, al_bseg_DS, a_userID, a_tcode);
				}
				else
				{
					Bkpf wa_bkpf = new Bkpf();				
					Bseg wa_bseg_debet = new Bseg();
					Bseg wa_bseg_kredit = new Bseg();
					List<Bseg> wal_bseg = new ArrayList<Bseg>(); 
					List<Bsik> wal_Bsik = new ArrayList<Bsik>();
					List<Bsik> wal_bsik = new ArrayList<Bsik>();        	
					int wa_buzei = 0;   
					
					wa_bkpf.setBukrs(a_bkpf_DS.getBukrs());
			    	wa_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			    	wa_bkpf.setBlart("VD");
			    	wa_bkpf.setBudat(curDate.getTime());
			    	wa_bkpf.setBldat(curDate.getTime());
			    	wa_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
			    	wa_bkpf.setCpudt(curDate.getTime());
			    	wa_bkpf.setUsnam(a_userID);
			    	wa_bkpf.setTcode(a_tcode);
			    	wa_bkpf.setBrnch(a_bkpf_DS.getBrnch());    	
			    	wa_bkpf.setBusiness_area_id(a_bkpf_DS.getBusiness_area_id());
			    	wa_bkpf.setBktxt("Возврат начисление скидки дилеру");
			    	wa_bkpf.setContract_number(a_bkpf_DS.getContract_number());
			    	wa_bkpf.setAwkey(null);
			    	wa_bkpf.setCustomer_id(a_bkpf_DS.getCustomer_id());
			    	wa_bkpf.setAwtyp(2);
			    	wa_bkpf.setWaers(a_bkpf_DS.getWaers());
			    	wa_bkpf.setKursf(a_bkpf_DS.getKursf());			    	
			    	wa_bkpf.setDmbtr(a_bkpf_DS.getDmbtr());
			    	wa_bkpf.setWrbtr(a_bkpf_DS.getDmbtr());
			    	
	

			    	
		        	
		        	
		    		wa_buzei++;			
					wa_bseg_debet.setBukrs(wa_bkpf.getBukrs());
					wa_bseg_debet.setGjahr(wa_bkpf.getGjahr());
					wa_bseg_debet.setBuzei(wa_buzei);
			    	wa_bseg_debet.setBschl("40");
					wa_bseg_debet.setHkont("71100030");					
					wa_bseg_debet.setShkzg("S");    				
					wa_bseg_debet.setDmbtr(wa_bkpf.getDmbtr());
					wa_bseg_debet.setWrbtr(wa_bkpf.getWrbtr());
					wal_bseg.add(wa_bseg_debet);
					
					
					wa_buzei++;	
					wa_bseg_kredit.setBukrs(wa_bkpf.getBukrs());
					wa_bseg_kredit.setGjahr(wa_bkpf.getGjahr());
					wa_bseg_kredit.setBuzei(wa_buzei);
					wa_bseg_kredit.setBschl("31");
					wa_bseg_kredit.setHkont(hkontKredit);
					wa_bseg_kredit.setShkzg("H");
					wa_bseg_debet.setLifnr(a_bkpf_DS.getCustomer_id());
					wa_bseg_kredit.setDmbtr(wa_bkpf.getDmbtr());
					wa_bseg_kredit.setWrbtr(wa_bkpf.getWrbtr());
					wal_bseg.add(wa_bseg_kredit);


					
					
					
						
					
		            
					if (wa_bkpf.getWaers() == null || wa_bkpf.getWaers().isEmpty()){
						throw new DAOException("Currency is empty");
					}
					if (wa_bkpf.getKursf() == 0){
						throw new DAOException("Currency rate is empty");
					}
					if (wa_bkpf.getMonat()==0){
						throw new DAOException("Document month not chosen");
					}
					if (wa_bkpf.getBusiness_area_id()==null || wa_bkpf.getBusiness_area_id()==0){
						throw new DAOException("Business area not chosen");
					}
					if (wa_bkpf.getCpudt()==null){
						throw new DAOException("System date not chosen");
					}

							/*
							if (wa_bseg.getStaff_id()!=null && wa_bseg.getStaff_id() > 0 && wa_bseg.getStaff_id() != a_contract.getCollector()){
								throw new DAOException("Collector and Contract collecter are not the same");
							} 

							if (wa_bseg.getLifnr() == null || wa_bseg.getLifnr() == 0 || (wa_bseg.getLifnr() != a_contract.getCustomer_id())){
								throw new DAOException("Customer is empty or not the same with the contract customer");
							}
							

						
							
						 
					
						
					
		            for (Bseg wa_bseg2 : wal_bseg) { 

		            		Bsik wa_bsik = new Bsik();
		            		wa_bsik.setGjahr(wa_bseg2.getGjahr());
		            		wa_bsik.setBukrs(wa_bseg2.getBukrs());            	
		            		wa_bsik.setBuzei(wa_bseg2.getBuzei());
		            		wa_bsik.setBschl(wa_bseg2.getBschl());
		            		wa_bsik.setHkont(wa_bseg2.getHkont());
		            		wa_bsik.setShkzg(wa_bseg2.getShkzg());
		            		wa_bsik.setDmbtr(wa_bseg2.getDmbtr());
		            		wa_bsik.setWrbtr(wa_bseg2.getWrbtr());
		            		wa_bsik.setLifnr(wa_bseg2.getLifnr());
		            		wa_bsik.setMatnr(wa_bseg2.getMatnr());
		            		wa_bsik.setWerks(wa_bseg2.getWerks());
		            		wa_bsik.setMenge(wa_bseg2.getMenge());
		            		wa_bsik.setSgtxt(wa_bseg2.getSgtxt());  				
		                	wal_bsik.add(wa_bsik);           	
		            	
		            	
					}  
		            //for (Bseg wa_bseg5 : wal_bseg) {
					//	System.out.print(wa_bseg5.getBukrs() + " ");
					//	System.out.print(wa_bseg5.getGjahr() + " ");
					//	System.out.print(wa_bseg5.getBuzei() + " ");
					//	System.out.print(wa_bseg5.getBschl() + " ");
					//	System.out.println(wa_bseg5.getDmbtr());
		            //}	
		            financeService.check_empty_fields(wa_bkpf, wal_bseg);      
		            financeService.insertNewFiDoc(wa_bkpf, wal_bseg, wal_bsik, wal_Bsik);
				}*/
				
			}
			
			if (a_contrFO!=null)
			{
				discountFromDealer(a_bkpf_GC, a_bkpf_GC.getBukrs(), a_bkpf_GC.getContract_number(), curDate, cputm, a_userID, a_tcode, 
						a_contrFO.getWaers(), a_contrFO.getRate(), a_bkpf_GC.getBrnch(), a_bkpf_GC.getBusiness_area_id(),
						a_contrFO.getOperationInfo(), null, null, 
						a_bkpf_GC.getCustomer_id(), a_contrFO.getAwkey(), a_contrFO.getDmbtr(), a_contrFO.getWrbtr());
				
				chargeDealer(a_bkpf_GC, a_bkpf_GC.getBukrs(), a_bkpf_GC.getContract_number(), curDate, cputm, a_userID, a_tcode, 
						a_contrFO.getWaers(), a_contrFO.getRate(), a_bkpf_GC.getBrnch(), a_bkpf_GC.getBusiness_area_id(),
						a_contrFO.getOperationInfo(), null, null, 
						a_contrFO.getCustomer_id(), a_contrFO.getAwkey(), a_contrFO.getDmbtr(), a_contrFO.getWrbtr());
			}
			
			return true;
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	public Long present(Contract a_contract, Branch a_branch, Long a_userID, String a_tcode,  Long a_matnr, List<Promotion> promoList) throws DAOException{
		try{			
			//System.out.println("ZZZZZZZZZZ");
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_Bsik = new ArrayList<Bsik>();
			List<Bsik> pl_bsik = new ArrayList<Bsik>();
			
			Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis()); 
			
			
			if (a_contract==null || a_contract.getContract_id()== null || a_contract.getContract_id() == 0 ){
				throw new DAOException("Contract id is empty");
			} 
			
			
			
			if (a_branch==null){
				throw new DAOException("Branch is empty");
			}
			
			if (a_userID==null || a_userID <= 0){
				throw new DAOException("User is empty");
			}
			
			if (a_tcode == null || a_tcode.isEmpty())
			{
				throw new DAOException("Transaction code is empty");
			}
			

			
			p_bkpf.setBukrs(a_contract.getBukrs());
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setBlart("GC");
			p_bkpf.setBudat(curDate.getTime());
			p_bkpf.setBldat(curDate.getTime());
            p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
            p_bkpf.setCpudt(curDate.getTime());
            p_bkpf.setUsnam(a_userID);
            p_bkpf.setTcode(a_tcode);
            p_bkpf.setWaers("USD");
            p_bkpf.setKursf(1); 
            p_bkpf.setBrnch(a_branch.getBranch_id());
            p_bkpf.setBusiness_area_id(a_branch.getBusiness_area_id());
			p_bkpf.setContract_number(a_contract.getContract_number()); 
			p_bkpf.setCustomer_id(a_contract.getCustomer_id());
			p_bkpf.setAwtyp(1);
			p_bkpf.setDmbtr(0);
			p_bkpf.setWrbtr(0);
			
			int wa_buzei = 0;
			wa_buzei++;
            Bseg wa_bseg = new Bseg();
            wa_bseg.setBukrs(p_bkpf.getBukrs());
            wa_bseg.setGjahr(p_bkpf.getGjahr());
            wa_bseg.setBuzei(wa_buzei);
            wa_bseg.setBschl("3");
			wa_bseg.setShkzg("S");
			wa_bseg.setHkont("12100001");
			wa_bseg.setLifnr(p_bkpf.getCustomer_id());			
			wa_bseg.setMatnr(a_matnr); 
			wa_bseg.setDmbtr(0);
			wa_bseg.setWrbtr(0);
			wa_bseg.setMenge(1); 
			pl_bseg.add(wa_bseg);
			
			wa_buzei++;
			wa_bseg = new Bseg();
            wa_bseg.setBukrs(p_bkpf.getBukrs());
            wa_bseg.setGjahr(p_bkpf.getGjahr());
            wa_bseg.setBuzei(wa_buzei);
            wa_bseg.setBschl("50");
			wa_bseg.setShkzg("H");
			if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==1 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100001");}//Roboclean
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==11 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100001");}//Roboclean
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==2 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100002");}//Cebilon
			
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==3 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100001");}//Rainbow E2
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==17 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100001");}//Rainbow SRX
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==4 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100003");}//Rexwat Atlas
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==5 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100002");}//Rexwat Eco
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==10 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100002");}//Rexwat Eco Restyle 2016
			else if (a_contract.getBukrs().equals("6000")){	wa_bseg.setHkont("60100001");}//Roboclean
			else {throw new DAOException("Good type for income GL account not selected");}		
			wa_bseg.setDmbtr(0);
			wa_bseg.setWrbtr(0);
			pl_bseg.add(wa_bseg);

			
			
			
            
			if (p_bkpf.getWaers() == null || p_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (p_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (p_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			if (p_bkpf.getBusiness_area_id()==null || p_bkpf.getBusiness_area_id()==0){
				throw new DAOException("Business area not chosen");
			}
			if (p_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}

					/*
					if (wa_bseg.getStaff_id()!=null && wa_bseg.getStaff_id() > 0 && wa_bseg.getStaff_id() != a_contract.getCollector()){
						throw new DAOException("Collector and Contract collecter are not the same");
					} 

					if (wa_bseg.getLifnr() == null || wa_bseg.getLifnr() == 0 || (wa_bseg.getLifnr() != a_contract.getCustomer_id())){
						throw new DAOException("Customer is empty or not the same with the contract customer");
					}
					*/

				
					
				 
				
				
			
            for (Bseg wa_bseg2 : pl_bseg) { 
            	Bsik wa_Bsik = new Bsik(); 
            	wa_Bsik.setBukrs(wa_bseg2.getBukrs());
            	wa_Bsik.setGjahr(wa_bseg2.getGjahr());
            	wa_Bsik.setBuzei(wa_bseg2.getBuzei());
            	wa_Bsik.setBschl(wa_bseg2.getBschl());
            	wa_Bsik.setHkont(wa_bseg2.getHkont());
            	wa_Bsik.setShkzg(wa_bseg2.getShkzg());
            	wa_Bsik.setDmbtr(wa_bseg2.getDmbtr());
            	wa_Bsik.setWrbtr(wa_bseg2.getWrbtr());
            	wa_Bsik.setLifnr(wa_bseg2.getLifnr()); 
            	wa_Bsik.setMenge(wa_bseg2.getMenge());
            	wa_Bsik.setMatnr(wa_bseg2.getMatnr());            	 				
            	pl_Bsik.add(wa_Bsik);
			}  
            
            financeService.check_empty_fields(p_bkpf, pl_bseg);
            Long wa_belnr =financeService.insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);
            
            addPromoMaterials(p_bkpf, promoList, a_userID, a_tcode, null);
            
            int gjahr = p_bkpf.getGjahr();
            Long wa_awkey = Long.parseLong( String.valueOf(wa_belnr)+String.valueOf(gjahr));
			return wa_awkey;
			
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	public Long recreateContract(Contract a_contract,Bkpf old_bkpf_GC, Branch a_branch_new, Long a_userID_new, String a_tcode_new, 
			String a_waers_new, double a_kursf_new, double a_dmbtr_new, double a_wrbtr_new, Long a_matnr_new) throws DAOException{
		try{			
			//System.out.println("ZZZZZZZZZZ");
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
//			List<Bsik> pl_Bsik = new ArrayList<Bsik>();
			List<Bsik> pl_bsik = new ArrayList<Bsik>();
			List<Bseg> old_bseg_GC = new ArrayList<Bseg>(); 
			
			Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis()); 
			
			
			if (a_contract==null || a_contract.getContract_id()== null || a_contract.getContract_id() == 0 ){
				throw new DAOException("Contract id is empty");
			} 
			
			if (a_waers_new == null || a_waers_new.isEmpty())
			{
				throw new DAOException("Waers is empty");
			}
			
			if (a_waers_new.equals("USD") && a_dmbtr_new == 0){ 
				throw new DAOException("The amount in local currency is 0"); 
			}
			else if (a_wrbtr_new == 0){					
				throw new DAOException("The amount in foreign currency is 0"); 
			}
			
			if (a_branch_new==null){
				throw new DAOException("Branch is empty");
			}
			
			if (a_userID_new==null || a_userID_new <= 0){
				throw new DAOException("User is empty");
			}
			
			if (a_tcode_new == null || a_tcode_new.isEmpty())
			{
				throw new DAOException("Transaction code is empty");
			}
			
			if (a_kursf_new <= 0){
				throw new DAOException("Курс не заполнен.");
			}
			
			if (old_bkpf_GC.getStorno()==1)
			{
				throw new DAOException("Неправильный фин. документ.");
			}
			List<ExchangeRate> exchageRate_list = new ArrayList<ExchangeRate>(); 
			
			if (a_waers_new == null || a_waers_new.isEmpty()){
				throw new DAOException("Select currency");
			}
			else
			{
				p_bkpf.setWaers(a_waers_new);
			}
			exchageRate_list = erDao.getLastCurrencyRates();
			
			if (a_waers_new.equals("USD")) p_bkpf.setDmbtr(a_dmbtr_new); 
			else
			{
				ExchangeRate selectedER = new ExchangeRate();
				for (ExchangeRate wa_er:exchageRate_list)
				{					
					if (wa_er.getType()==1 && wa_er.getSecondary_currency().equals(a_waers_new))
					{
						selectedER = wa_er;
						a_kursf_new = selectedER.getSc_value();
						a_dmbtr_new = GeneralUtil.round(a_wrbtr_new/a_kursf_new, 2);
						p_bkpf.setKursf(a_kursf_new);
						p_bkpf.setWrbtr(a_wrbtr_new); 
						p_bkpf.setDmbtr(a_dmbtr_new);
					}
					
				}
			}
			p_bkpf.setBukrs(a_contract.getBukrs());
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setBlart("GC");
			p_bkpf.setBudat(curDate.getTime());
			p_bkpf.setBldat(curDate.getTime());
            p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
            p_bkpf.setCpudt(curDate.getTime());
            p_bkpf.setUsnam(a_userID_new);
            p_bkpf.setTcode(a_tcode_new);
            p_bkpf.setWaers(a_waers_new);
            p_bkpf.setKursf(a_kursf_new); 
            p_bkpf.setBrnch(a_branch_new.getBranch_id());
            p_bkpf.setBusiness_area_id(a_branch_new.getBusiness_area_id());
			p_bkpf.setContract_number(a_contract.getContract_number()); 
			p_bkpf.setCustomer_id(a_contract.getCustomer_id());
			p_bkpf.setAwtyp(1);			
			if (a_waers_new.equals("USD")) p_bkpf.setDmbtr(a_dmbtr_new); 
			else p_bkpf.setWrbtr(a_wrbtr_new); p_bkpf.setDmbtr(a_dmbtr_new);
			p_bkpf.setDmbtr_paid(old_bkpf_GC.getDmbtr_paid());
			p_bkpf.setWrbtr_paid(old_bkpf_GC.getWrbtr_paid());
			p_bkpf.setOfficial(old_bkpf_GC.getOfficial());
			
			int wa_buzei = 0;
			wa_buzei++;
            Bseg wa_bseg = new Bseg();
            wa_bseg.setBukrs(p_bkpf.getBukrs());
            wa_bseg.setGjahr(p_bkpf.getGjahr());
            wa_bseg.setBuzei(wa_buzei);
            wa_bseg.setBschl("1");
			wa_bseg.setShkzg("S");
			wa_bseg.setHkont("12100001");
			wa_bseg.setLifnr(p_bkpf.getCustomer_id());			
			wa_bseg.setMatnr(a_matnr_new); 
			wa_bseg.setDmbtr(a_dmbtr_new);
			wa_bseg.setWrbtr(a_wrbtr_new);
			wa_bseg.setMenge(1); 
			pl_bseg.add(wa_bseg);
			
			wa_buzei++;
			wa_bseg = new Bseg();
            wa_bseg.setBukrs(p_bkpf.getBukrs());
            wa_bseg.setGjahr(p_bkpf.getGjahr());
            wa_bseg.setBuzei(wa_buzei);
            wa_bseg.setBschl("50");
			wa_bseg.setShkzg("H");
			if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==1 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100001");}//Roboclean
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==11 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100001");}//Roboclean
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==2 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100002");}//Cebilon
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==7 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100002");}//Cebilon
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==15 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100002");}//Cebilon	
			
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==3 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100001");}//Rainbow E2
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==17 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100001");}//Rainbow SRX
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==4 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100003");}//Rexwat Atlas
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==5 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100002");}//Rexwat Eco
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==10 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100002");}//Rexwat Eco Restyle 2016
			else if (a_contract.getBukrs().equals("6000")){	wa_bseg.setHkont("60100001");}//Roboclean
			else {throw new DAOException("Good type for income GL account not selected");}		
			wa_bseg.setDmbtr(a_dmbtr_new);
			wa_bseg.setWrbtr(a_wrbtr_new);
			pl_bseg.add(wa_bseg);

			
			
			
            
			if (p_bkpf.getWaers() == null || p_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (p_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (p_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			if (p_bkpf.getBusiness_area_id()==null || p_bkpf.getBusiness_area_id()==0){
				throw new DAOException("Business area not chosen");
			}
			if (p_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}

					/*
					if (wa_bseg.getStaff_id()!=null && wa_bseg.getStaff_id() > 0 && wa_bseg.getStaff_id() != a_contract.getCollector()){
						throw new DAOException("Collector and Contract collecter are not the same");
					} 

					if (wa_bseg.getLifnr() == null || wa_bseg.getLifnr() == 0 || (wa_bseg.getLifnr() != a_contract.getCustomer_id())){
						throw new DAOException("Customer is empty or not the same with the contract customer");
					}
					*/

				
					
				 
				
				
			
            for (Bseg wa_bseg2 : pl_bseg) { 
            	Bsik wa_Bsik = new Bsik(); 
            	wa_Bsik.setBukrs(wa_bseg2.getBukrs());
            	wa_Bsik.setGjahr(wa_bseg2.getGjahr());
            	wa_Bsik.setBuzei(wa_bseg2.getBuzei());
            	wa_Bsik.setBschl(wa_bseg2.getBschl());
            	wa_Bsik.setHkont(wa_bseg2.getHkont());
            	wa_Bsik.setShkzg(wa_bseg2.getShkzg());
            	wa_Bsik.setDmbtr(wa_bseg2.getDmbtr());
            	wa_Bsik.setWrbtr(wa_bseg2.getWrbtr());
            	wa_Bsik.setLifnr(wa_bseg2.getLifnr()); 
            	wa_Bsik.setMenge(wa_bseg2.getMenge());
            	wa_Bsik.setMatnr(wa_bseg2.getMatnr());            	 				
            	pl_bsik.add(wa_Bsik);
			}  
            
            old_bseg_GC = bsegDao.dynamicFindBseg(" bukrs='"+old_bkpf_GC.getBukrs()+"' and  belnr = "+old_bkpf_GC.getBelnr()+" and gjahr = "+old_bkpf_GC.getGjahr());
            financeService.cancelFiDoc(old_bkpf_GC, old_bseg_GC, a_userID_new, a_tcode_new);
            
            p_bkpf.setLog_doc(old_bkpf_GC.getLog_doc());
            financeService.check_empty_fields(p_bkpf, pl_bseg);
            financeService.insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);
            
            
            List<Bkpf> l_bkpf_related = new ArrayList<Bkpf>();
            l_bkpf_related = bkpfDao.dynamicFindBkpf(" bukrs='"+old_bkpf_GC.getBukrs()+"' and  awkey ="+GeneralUtil.getPreparedAwkey(old_bkpf_GC.getBelnr(), old_bkpf_GC.getGjahr()));
             
            for (Bkpf wa_bkpf_temp:l_bkpf_related)
            {
            	wa_bkpf_temp.setAwkey(GeneralUtil.getPreparedAwkey(p_bkpf.getBelnr(), p_bkpf.getGjahr()));
            	bkpfDao.update(wa_bkpf_temp);
            }
            
            Long wa_awkey = GeneralUtil.getPreparedAwkey(p_bkpf.getBelnr(), p_bkpf.getGjahr());//Long.parseLong( String.valueOf(wa_belnr)+String.valueOf(gjahr));
			return wa_awkey;
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
		
	}
	
	public void cancelContract(Contract a_contract, Bkpf a_bkpf_GC,double a_kursf_vozvart, double a_dmbtr_vozvrat, double a_wrbtr_vozvrat, Long a_userID, String a_tcode)throws DAOException{
		try{
			
			if (a_contract.getMarkedType()!=null && a_contract.getMarkedType().equals(3L))
			{
				return;
			}
		
			if (bkpfDao.dynamicCountBkpf(" bukrs="+a_contract.getBukrs()+" and awkey="+a_contract.getAwkey()+" and blart='SK' and storno=0")>0)
			{
				throw new DAOException("нужно удалить скидку рекомендателю");
			}
			
			double wa_dmbtr_raznica=0;
			double wa_wrbtr_raznica=0;
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_Bsik = new ArrayList<Bsik>();
			List<Bsik> pl_bsik = new ArrayList<Bsik>();
			
			List<Bseg> wal_bseg_GC = new ArrayList<Bseg>(); 
			List<ExchangeRate> l_er = new ArrayList<ExchangeRate>();
			Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis()); 
			wal_bseg_GC = bsegDao.dynamicFindBseg(" belnr = "+a_bkpf_GC.getBelnr()+" and gjahr="+a_bkpf_GC.getGjahr());
			String hkont1210="";
			String hkont6010="";
			//Long matnr = null;
			for (Bseg wa_bseg:wal_bseg_GC)
			{
				if (wa_bseg.getHkont().startsWith("1210"))
				{
					hkont1210 = wa_bseg.getHkont();
				}
				else if (wa_bseg.getHkont().startsWith("6010"))
				{
					hkont6010 = wa_bseg.getHkont();
				}
				
				if (wa_bseg.getMatnr()!=null)
				{
					//matnr= wa_bseg.getMatnr();
				}
				
			}
			
			if (a_bkpf_GC.getWaers().equals("USD"))
			{
				wa_wrbtr_raznica = 0;
				wa_dmbtr_raznica = a_bkpf_GC.getDmbtr()-a_bkpf_GC.getDmbtr_paid();
				p_bkpf.setWaers(a_bkpf_GC.getWaers());
				p_bkpf.setKursf(1);
			}
			else
			{
				wa_wrbtr_raznica = a_bkpf_GC.getWrbtr()-a_bkpf_GC.getWrbtr_paid();
				wa_dmbtr_raznica = wa_wrbtr_raznica/a_bkpf_GC.getKursf();
				p_bkpf.setWaers(a_bkpf_GC.getWaers());
				p_bkpf.setKursf(a_bkpf_GC.getKursf());
				
				
			}
			
			
			
			
			if (a_contract==null || a_contract.getContract_id()== null || a_contract.getContract_id() == 0 ){
				throw new DAOException("Contract id is empty");
			} 
			
			int wa_buzei = 0;
			if (!(wa_dmbtr_raznica==0 && wa_wrbtr_raznica==0))
			{	
				p_bkpf.setBukrs(a_contract.getBukrs());
				p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
				p_bkpf.setBlart("OT");
				p_bkpf.setBudat(curDate.getTime());
				p_bkpf.setBldat(curDate.getTime());
	            p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
	            p_bkpf.setCpudt(curDate.getTime());
	            p_bkpf.setUsnam(a_userID);
	            p_bkpf.setTcode(a_tcode);
	            
	            p_bkpf.setBrnch(a_bkpf_GC.getBrnch());
	            p_bkpf.setBusiness_area_id(a_bkpf_GC.getBusiness_area_id());
				p_bkpf.setContract_number(a_contract.getContract_number()); 
				p_bkpf.setCustomer_id(a_contract.getCustomer_id());
				p_bkpf.setAwtyp(1);			
				p_bkpf.setDmbtr(wa_dmbtr_raznica); 
				p_bkpf.setWrbtr(wa_wrbtr_raznica);
	
				
				
				wa_buzei++;
	            Bseg wa_bseg = new Bseg();
	            wa_bseg.setBukrs(p_bkpf.getBukrs());
	            wa_bseg.setGjahr(p_bkpf.getGjahr());
	            wa_bseg.setBuzei(wa_buzei);
	            wa_bseg.setBschl("16");
				wa_bseg.setShkzg("H");
				wa_bseg.setHkont(hkont1210);
				wa_bseg.setLifnr(a_bkpf_GC.getCustomer_id());	
				wa_bseg.setDmbtr(wa_dmbtr_raznica);
				wa_bseg.setWrbtr(wa_wrbtr_raznica);
				pl_bseg.add(wa_bseg);
				
				wa_buzei++;
				wa_bseg = new Bseg();
	            wa_bseg.setBukrs(p_bkpf.getBukrs());
	            wa_bseg.setGjahr(p_bkpf.getGjahr());
	            wa_bseg.setBuzei(wa_buzei);
	            wa_bseg.setBschl("40");
				wa_bseg.setShkzg("S");
				wa_bseg.setHkont(hkont6010);
				wa_bseg.setDmbtr(wa_dmbtr_raznica);
				wa_bseg.setWrbtr(wa_wrbtr_raznica);
				pl_bseg.add(wa_bseg);
	
				
				
				
	            
				if (p_bkpf.getWaers() == null || p_bkpf.getWaers().isEmpty()){
					throw new DAOException("Currency is empty");
				}
				if (p_bkpf.getKursf() == 0){
					throw new DAOException("Currency rate is empty");
				}
				if (p_bkpf.getMonat()==0){
					throw new DAOException("Document month not chosen");
				}
				if (p_bkpf.getBusiness_area_id()==null || p_bkpf.getBusiness_area_id()==0){
					throw new DAOException("Business area not chosen");
				}
				if (p_bkpf.getCpudt()==null){
					throw new DAOException("System date not chosen");
				}
	
						/*
						if (wa_bseg.getStaff_id()!=null && wa_bseg.getStaff_id() > 0 && wa_bseg.getStaff_id() != a_contract.getCollector()){
							throw new DAOException("Collector and Contract collecter are not the same");
						} 
	
						if (wa_bseg.getLifnr() == null || wa_bseg.getLifnr() == 0 || (wa_bseg.getLifnr() != a_contract.getCustomer_id())){
							throw new DAOException("Customer is empty or not the same with the contract customer");
						}
						*/
	
					
						
					 
					
					
				
	            for (Bseg wa_bseg2 : pl_bseg) { 
	            	Bsik wa_Bsik = new Bsik(); 
	            	wa_Bsik.setBukrs(wa_bseg2.getBukrs());
	            	wa_Bsik.setGjahr(wa_bseg2.getGjahr());
	            	wa_Bsik.setBuzei(wa_bseg2.getBuzei());
	            	wa_Bsik.setBschl(wa_bseg2.getBschl());
	            	wa_Bsik.setHkont(wa_bseg2.getHkont());
	            	wa_Bsik.setShkzg(wa_bseg2.getShkzg());
	            	wa_Bsik.setDmbtr(wa_bseg2.getDmbtr());
	            	wa_Bsik.setWrbtr(wa_bseg2.getWrbtr());
	            	wa_Bsik.setLifnr(wa_bseg2.getLifnr()); 
	            	wa_Bsik.setMenge(wa_bseg2.getMenge());
	            	wa_Bsik.setMatnr(wa_bseg2.getMatnr());            	 				
	            	pl_Bsik.add(wa_Bsik);
				}  
	            
	            financeService.check_empty_fields(p_bkpf, pl_bseg);
	            financeService.insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);
	            a_bkpf_GC.setDmbtr_paid(a_bkpf_GC.getDmbtr_paid()+wa_dmbtr_raznica);
	            a_bkpf_GC.setWrbtr_paid(a_bkpf_GC.getWrbtr_paid()+wa_wrbtr_raznica);
	            a_bkpf_GC.setClosed(0);
	            financeService.updateFiDoc(a_bkpf_GC, null);
			}
            
            /*String dynamicWhereClause = "";
            dynamicWhereClause = "";
            dynamicWhereClause = " wrbtr_paid = wrbtr_paid +" + wa_dmbtr_raznica +", dmbtr_paid = dmbtr_paid +" + wa_wrbtr_raznica;			
			
            if (bkpfDao.updateDynamicSingleBkpf(a_bkpf_GC.getBelnr(), a_bkpf_GC.getGjahr(), dynamicWhereClause)!=1)
        	{
        		throw new DAOException("Account Recievable not updated");
        	}*/         
            
            if (a_dmbtr_vozvrat>0)
            {
            	Bkpf p_bkpf_kreditor = new Bkpf();
    			List<Bseg> pl_bseg_kreditor = new ArrayList<Bseg>();
    			List<Bsik> pl_bsik_kreditor = new ArrayList<Bsik>();
    			
    			p_bkpf_kreditor.setBukrs(a_contract.getBukrs());
    			p_bkpf_kreditor.setGjahr(curDate.get(Calendar.YEAR)); 
    			p_bkpf_kreditor.setBlart("RP");
    			p_bkpf_kreditor.setBudat(curDate.getTime());
    			p_bkpf_kreditor.setBldat(curDate.getTime());
    			p_bkpf_kreditor.setMonat(curDate.get(Calendar.MONTH)+1);
    			p_bkpf_kreditor.setCpudt(curDate.getTime());
    			p_bkpf_kreditor.setUsnam(a_userID);
    			p_bkpf_kreditor.setTcode(a_tcode);
    			
                
                p_bkpf_kreditor.setBrnch(a_bkpf_GC.getBrnch());
                p_bkpf_kreditor.setBusiness_area_id(a_bkpf_GC.getBusiness_area_id());
    			p_bkpf_kreditor.setContract_number(a_contract.getContract_number()); 
    			p_bkpf_kreditor.setCustomer_id(a_contract.getCustomer_id());
    			p_bkpf_kreditor.setAwtyp(2);			
    			
    			
    			if (a_bkpf_GC.getWaers().equals("USD"))
    			{
    				p_bkpf_kreditor.setDmbtr(a_dmbtr_vozvrat); 
        			p_bkpf_kreditor.setWrbtr(0);
    			}
    			else
    			{
    				l_er = erDao.getLastCurrencyRates(new java.sql.Date(curDate.getTimeInMillis()));
    				for (ExchangeRate wa_er:l_er)
    				{
    					if (wa_er.getSecondary_currency().equals(a_bkpf_GC.getWaers())&& wa_er.getType()==1)
    					{
    						p_bkpf_kreditor.setWaers(a_bkpf_GC.getWaers());
    						//p_bkpf_kreditor.setWaers(a_bkpf_GC.getWaers());
    						p_bkpf_kreditor.setKursf(wa_er.getSc_value());
    						//p_bkpf_kreditor.setKursf(a_kursf_vozvart);
    						p_bkpf_kreditor.setDmbtr(a_wrbtr_vozvrat/p_bkpf_kreditor.getKursf()); 
    						p_bkpf_kreditor.setWrbtr(a_wrbtr_vozvrat);
    						break;
    					}
    					
    				}
    				
    				
    			}
    			
    			
    			
    			wa_buzei = 0;
    			wa_buzei++;
                Bseg wa_bseg_kreditorH = new Bseg();			
                wa_bseg_kreditorH.setBukrs(p_bkpf_kreditor.getBukrs());
                wa_bseg_kreditorH.setGjahr(p_bkpf_kreditor.getGjahr());
                wa_bseg_kreditorH.setBuzei(wa_buzei);
                wa_bseg_kreditorH.setBschl("38");
                wa_bseg_kreditorH.setShkzg("H");
    	    	wa_bseg_kreditorH.setHkont("33100001");
    	    	wa_bseg_kreditorH.setLifnr(a_bkpf_GC.getCustomer_id());	
    	    	wa_bseg_kreditorH.setDmbtr(p_bkpf_kreditor.getDmbtr());
    	    	wa_bseg_kreditorH.setWrbtr(p_bkpf_kreditor.getWrbtr());
    			pl_bseg_kreditor.add(wa_bseg_kreditorH);
    			
    			wa_buzei++;
    			Bseg wa_bseg_kreditorS = new Bseg();
    			wa_bseg_kreditorS.setBukrs(p_bkpf_kreditor.getBukrs());
    			wa_bseg_kreditorS.setGjahr(p_bkpf_kreditor.getGjahr());
    			wa_bseg_kreditorS.setBuzei(wa_buzei);
    			wa_bseg_kreditorS.setBschl("40");
                wa_bseg_kreditorS.setShkzg("S");
                wa_bseg_kreditorS.setHkont("60200001");
                wa_bseg_kreditorS.setDmbtr(p_bkpf_kreditor.getDmbtr());
                wa_bseg_kreditorS.setWrbtr(p_bkpf_kreditor.getWrbtr());
    			pl_bseg_kreditor.add(wa_bseg_kreditorS);

    			
    			
    			
                
    			if (p_bkpf_kreditor.getWaers() == null || p_bkpf_kreditor.getWaers().isEmpty()){
    				throw new DAOException("Currency is empty");
    			}
    			if (p_bkpf_kreditor.getKursf() == 0){
    				throw new DAOException("Currency rate is empty");
    			}
    			if (p_bkpf_kreditor.getMonat()==0){
    				throw new DAOException("Document month not chosen");
    			}
    			if (p_bkpf_kreditor.getBusiness_area_id()==null || p_bkpf_kreditor.getBusiness_area_id()==0){
    				throw new DAOException("Business area not chosen");
    			}
    			if (p_bkpf_kreditor.getCpudt()==null){
    				throw new DAOException("System date not chosen");
    			}

    					/*
    					if (wa_bseg.getStaff_id()!=null && wa_bseg.getStaff_id() > 0 && wa_bseg.getStaff_id() != a_contract.getCollector()){
    						throw new DAOException("Collector and Contract collecter are not the same");
    					} 

    					if (wa_bseg.getLifnr() == null || wa_bseg.getLifnr() == 0 || (wa_bseg.getLifnr() != a_contract.getCustomer_id())){
    						throw new DAOException("Customer is empty or not the same with the contract customer");
    					}
    					*/

    				
    					
    				 
    				
    				
    			
                for (Bseg wa_bseg2 : pl_bseg_kreditor) { 
                	Bsik wa_bsik = new Bsik(); 
                	wa_bsik.setBukrs(wa_bseg2.getBukrs());
                	wa_bsik.setGjahr(wa_bseg2.getGjahr());
                	wa_bsik.setBuzei(wa_bseg2.getBuzei());
                	wa_bsik.setBschl(wa_bseg2.getBschl());
                	wa_bsik.setHkont(wa_bseg2.getHkont());
                	wa_bsik.setShkzg(wa_bseg2.getShkzg());
                	wa_bsik.setDmbtr(wa_bseg2.getDmbtr());
                	wa_bsik.setWrbtr(wa_bseg2.getWrbtr());
                	wa_bsik.setLifnr(wa_bseg2.getLifnr()); 
                	wa_bsik.setMenge(wa_bseg2.getMenge());
                	wa_bsik.setMatnr(wa_bseg2.getMatnr());            	 				
                	pl_bsik_kreditor.add(wa_bsik);
    			}  
                
                financeService.check_empty_fields(p_bkpf_kreditor, pl_bseg_kreditor);
                financeService.insertNewFiDoc(p_bkpf_kreditor, pl_bseg_kreditor, pl_bsik_kreditor);
            }
            addRewardToRecommender(a_bkpf_GC, a_userID,a_tcode, null);
            addDiscountToRecommender(a_bkpf_GC, a_userID, a_tcode, null);
            addPromoMaterials(a_bkpf_GC, null, a_userID, a_tcode, null);
            cancelBonus(a_bkpf_GC, a_contract,  a_userID, a_tcode);
            
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	public void cancelBonus(Bkpf a_bkpf_GC, Contract a_contract, Long a_userID, String a_tcode) throws DAOException
	{
		try{
			Calendar curDate = Calendar.getInstance();
			Calendar conDate = Calendar.getInstance();
			ContractType wa_con_type = new ContractType();
			wa_con_type = conTypeDao.find(a_contract.getContract_type_id());
			if (a_contract==null || a_contract.getContract_id()== null || a_contract.getContract_id() == 0 ){
				throw new DAOException("No such contract type --> cancelBonus.method");			
			}
			
			Branch wa_branch = new Branch();
			if (a_contract==null || a_contract.getContract_id()== null || a_contract.getContract_id() == 0 ){
				throw new DAOException("Contract id is empty --> cancelBonus.method");			
			} 
			
			wa_branch = branchDao.find(a_contract.getBranch_id());
			List<Payroll> l_prl = new ArrayList<Payroll>();
			if (a_contract.getMarkedType()!=null && a_contract.getMarkedType().equals(1L))
			{
				//Universal
				l_prl = prlDao.findAll(" drcrk = 'S' and contract_number = "+a_contract.getContract_number());
				for(Payroll wa_prl:l_prl)
				{
					
					Payroll wa_prl_cancel = new Payroll();
					BeanUtils.copyProperties(wa_prl, wa_prl_cancel);
					wa_prl_cancel.setPayroll_id(null);
					if (wa_prl.getBranch_id()==null || wa_prl.getBranch_id().equals(0L)) wa_prl_cancel.setBranch_id(wa_branch.getBranch_id());	
					wa_prl_cancel.setDrcrk("H");
					wa_prl_cancel.setParent_payroll_id(wa_prl.getPayroll_id());
					wa_prl_cancel.setBonus_type_id(otmena);
					wa_prl_cancel.setText45("отмена договора");
					wa_prl_cancel.setPayroll_date(curDate.getTime());
					wa_prl_cancel.setBldat(curDate.getTime());
					payrollService.createNew(wa_prl_cancel,a_userID,true,a_tcode,9);
					
				}
				return;
			}
			else if (a_contract.getMarkedType()!=null && a_contract.getMarkedType().equals(3L))
			{
				return;
			}
			else if (a_contract.getMarkedType()!=null && a_contract.getMarkedType().equals(0L))
			{
				//Standart
				l_prl = prlDao.findAll(" drcrk = 'S' and bonus_type_id in (1,2,7,15) and contract_number = "+a_contract.getContract_number());
			}
			
			
			conDate.setTime(a_contract.getContract_date());
			int conYear = conDate.get(Calendar.YEAR);
			
			Payroll dealer_prl = new Payroll();
			//Payroll manager_prl = new Payroll();
			
			//Cancel dealer premium, manager premium, trainer premium, director premium
			if (a_bkpf_GC.getBukrs().equals("2000"))
			{
				for(Payroll wa_prl:l_prl)
				{
					
					if ((wa_prl.getPosition_id()==dealer_pos_id || wa_prl.getPosition_id()==manager_pos_id
					|| wa_prl.getPosition_id()==director_pos_id) || (wa_prl.getBonus_type_id()==15)  )
					{
						if (prlDao.dynamicFindCountPayroll(" parent_payroll_id = "+wa_prl.getPayroll_id())>0)
						{
							throw new DAOException("Бонус отменен.");
						}
						if (wa_prl.getBonus_type_id()==deposit)
						{
							wa_prl.setApprove(0);
						}
						Payroll wa_prl_cancel = new Payroll();
						BeanUtils.copyProperties(wa_prl, wa_prl_cancel);
						wa_prl_cancel.setPayroll_id(null);
						if (wa_prl.getBranch_id()==null || wa_prl.getBranch_id().equals(0L)) wa_prl_cancel.setBranch_id(wa_branch.getBranch_id());	
						wa_prl_cancel.setDrcrk("H");
						wa_prl_cancel.setParent_payroll_id(wa_prl.getPayroll_id());
						wa_prl_cancel.setBonus_type_id(otmena);
						wa_prl_cancel.setText45("отмена договора");
						wa_prl_cancel.setPayroll_date(curDate.getTime());
						wa_prl_cancel.setBldat(curDate.getTime());
						payrollService.createNew(wa_prl_cancel,a_userID,true,a_tcode,9);
						
						
						if (wa_prl.getBonus_type_id()==deposit)
						{
							prlDao.update(wa_prl);							
						}
						if(wa_prl.getPosition_id()==dealer_pos_id && wa_prl.getBonus_type_id()==premium && wa_prl.getManager_id()!=null)
						{
							dealer_prl = wa_prl;
						}
						if(wa_prl.getPosition_id()==dealer_pos_id && wa_prl.getBonus_type_id()==premium)
						{
							wa_prl.setBldat(curDate.getTime());
							prlDao.update(wa_prl);
						}
					}
					
				}
			}
			else if (a_bkpf_GC.getBukrs().equals("1000"))
			{
				int monat = 0;
				int gjahr = 0;
				for(Payroll wa_prl:l_prl)
				{
					
					if ((wa_prl.getPosition_id()==dealer_pos_id
					|| wa_prl.getPosition_id()==director_pos_id || (wa_prl.getPosition_id()==manager_pos_id && conYear>=2018))  || (wa_prl.getBonus_type_id()==15))
					{
						if (prlDao.dynamicFindCountPayroll(" parent_payroll_id = "+wa_prl.getPayroll_id())>0)
						{
							throw new DAOException("Бонус отменен.");
						}
						if (wa_prl.getBonus_type_id()==deposit)
						{
							wa_prl.setApprove(0);
						}
						Payroll wa_prl_cancel = new Payroll();
						BeanUtils.copyProperties(wa_prl, wa_prl_cancel);
						wa_prl_cancel.setPayroll_id(null);
						if (wa_prl.getBranch_id()==null || wa_prl.getBranch_id().equals(0L)) wa_prl_cancel.setBranch_id(wa_branch.getBranch_id());						
						wa_prl_cancel.setDrcrk("H");
						wa_prl_cancel.setParent_payroll_id(wa_prl.getPayroll_id());
						wa_prl_cancel.setBonus_type_id(otmena);
						wa_prl_cancel.setText45("отмена договора");
						wa_prl_cancel.setPayroll_date(curDate.getTime());
						wa_prl_cancel.setBldat(curDate.getTime());
						payrollService.createNew(wa_prl_cancel,a_userID,true,a_tcode,9);
						//prlDao.create(wa_prl_cancel);
						
						
						
						
						if (wa_prl.getBonus_type_id()==deposit)
						{
							prlDao.update(wa_prl);
						}
						if(wa_prl.getPosition_id()==dealer_pos_id && wa_prl.getBonus_type_id()==premium && wa_prl.getManager_id()!=null)
						{
							dealer_prl = wa_prl;
						}
						if(wa_prl.getPosition_id()==dealer_pos_id && wa_prl.getBonus_type_id()==premium)
						{
							wa_prl.setBldat(curDate.getTime());
							prlDao.update(wa_prl);
						}
					}
					
				}
				List<Long> l_prl_man = new ArrayList<Long>();
				int total_man = 0;
				int total_cancel_man = 0;
				l_prl_man = prlDao.dynamicFindPayrollId(" bukrs = '"+a_bkpf_GC.getBukrs()+"' and monat ="+monat+" and gjahr ="+gjahr+ " and position_id="+manager_pos_id);
				if (l_prl_man!=null && l_prl_man.size()>0 && dealer_prl!=null && dealer_prl.getManager_id()!=null && conYear<2018)
				{
					//Roboclean Premium
					String matnr_string = "";
					if (a_contract.getContract_type_id()==1)
					{	
						matnr_string = "1";
					}
					else if (a_contract.getContract_type_id()==11) matnr_string = "1";
					//Cebilon Premium
					else if (a_contract.getContract_type_id()==2) matnr_string = "2";
					else if (a_contract.getContract_type_id()==7) matnr_string = "2";
					else if (a_contract.getContract_type_id()==8) matnr_string = "2";
					else if (a_contract.getContract_type_id()==9) matnr_string = "2";
					
					
					total_man = l_prl_man.size();
					total_cancel_man = prlDao.dynamicFindCountPayroll(" bukrs = '"+a_bkpf_GC.getBukrs()+"' and monat ="+monat
							+" and gjahr ="+gjahr+ " and position_id="+manager_pos_id+" and bonus_type_id="+otmena, l_prl_man);
					total_man= total_man-total_cancel_man;
					
					List<BonusArchive> l_bonus = new ArrayList<BonusArchive>();
					l_bonus = bonArcDao.dynamicFindBonuses(" bukrs = '"+a_bkpf_GC.getBukrs()+"' and monat ="+monat+" and gjahr ="+gjahr
							+ " position_id="+manager_pos_id+" and matnr is not null order by matnr,bonus_type_id,position_id,from_num");
					for(BonusArchive wa_bonus:l_bonus)
					{
						if (wa_bonus.getCountry_id()!=null && wa_bonus.getCountry_id().equals(wa_branch.getCountry_id()) 
								&& 	wa_bonus.getBonus_type_id() == (long) premium && wa_bonus.getMatnr()!=null && String.valueOf(wa_bonus.getMatnr()).equals(matnr_string)
									)
							{
								if (wa_bonus.getFrom_num()<=total_man && total_man<=wa_bonus.getTo_num())
								{
									Calendar bonus_date = Calendar.getInstance(); 
									List<ExchangeRate> l_er = new ArrayList<ExchangeRate>();
									bonus_date.setTime(a_bkpf_GC.getBudat());
									l_er = erDao.getLastCurrencyRates(new java.sql.Date(bonus_date.getTimeInMillis()));
									ExchangeRate wa_er = new ExchangeRate();
									for (ExchangeRate wa_er2:l_er)
									{
										//key_string = String.valueOf(wa_er.getType()) + wa_er.getSecondary_currency();
										//l_er_map.put(key_string, wa_er);
										
										if (wa_er.getType()==2)
										{
											if (wa_er2.getBukrs()!=null && wa_er2.getBukrs().equals(a_bkpf_GC.getBukrs()) && wa_er.getSecondary_currency().equals(a_bkpf_GC.getWaers()))
											{
												wa_er = wa_er2;
												break;
											}
										}
									}
									
									
									Payroll wa_prl_cancel_man = new Payroll();
									wa_prl_cancel_man.setApprove(0);
									wa_prl_cancel_man.setBranch_id(wa_branch.getBranch_id());
									wa_prl_cancel_man.setPayroll_date(curDate.getTime());
									wa_prl_cancel_man.setBldat(curDate.getTime());
									wa_prl_cancel_man.setDrcrk("H");
									wa_prl_cancel_man.setStaff_id(dealer_prl.getManager_id());
									wa_prl_cancel_man.setWaers(a_bkpf_GC.getWaers());
									wa_prl_cancel_man.setBukrs(a_bkpf_GC.getBukrs());
									wa_prl_cancel_man.setGjahr(a_bkpf_GC.getGjahr());
									wa_prl_cancel_man.setMonat(a_bkpf_GC.getMonat());
									wa_prl_cancel_man.setPosition_id((long)manager_pos_id);
									wa_prl_cancel_man.setPayroll_date(a_bkpf_GC.getBudat());
									wa_prl_cancel_man.setText45("отмена договора бонус мен");
									wa_prl_cancel_man.setBonus_type_id(otmena);
									wa_prl_cancel_man.setBldat(curDate.getTime());
									if (wa_bonus.getWaers().equals("USD") && wa_er!=null && wa_er.getSecondary_currency()!=null )
									{
										wa_prl_cancel_man.setDmbtr(wa_bonus.getCoef()*wa_er.getSc_value());
										payrollService.createNew(wa_prl_cancel_man,a_userID,true,a_tcode,9);
										//prlDao.create(wa_prl_cancel_man);
									}
									else if (!wa_bonus.getWaers().equals("USD") && a_bkpf_GC.getWaers().equals(wa_bonus.getWaers()))
									{
										wa_prl_cancel_man.setDmbtr(wa_bonus.getCoef());
										payrollService.createNew(wa_prl_cancel_man,a_userID,true,a_tcode,9);
										//prlDao.create(wa_prl_cancel_man);
									}
									
								    
								   
								    
										
									
								}
							}
						
					}
				}
				
				
			}
			
			
			
			
			//deposit  
			//JOGARYDA OTMEN BOLIP KOIDIJOGARYDA OTMEN BOLIP KOIDIJOGARYDA OTMEN BOLIP KOIDIJOGARYDA OTMEN BOLIP KOIDI
			//////////////////////////////////
//			Payroll wa_payroll_deposit = new Payroll();
//			wa_payroll_deposit = prlDao.dynamicFindSinglePayroll(" bukrs = '"+a_bkpf_GC.getBukrs()+"' and gjahr = "+a_bkpf_GC.getGjahr()+" and contract_number="
//			+a_bkpf_GC.getContract_number()+" and bonus_type_id="+deposit+" and position_id="+dealer_pos_id);
//			
//			if (wa_payroll_deposit!=null)
//			{
//				wa_payroll_deposit.setApprove(0);
//				prlDao.update(wa_payroll_deposit);
//				
//				Payroll wa_prl_deposit_otmena = new Payroll();
//				BeanUtils.copyProperties(wa_payroll_deposit, wa_prl_deposit_otmena);
//				wa_prl_deposit_otmena.setPayroll_id(null);
//				if (wa_payroll_deposit.getBranch_id()==null || wa_payroll_deposit.getBranch_id().equals(0L)) wa_prl_deposit_otmena.setBranch_id(wa_branch.getBranch_id());
//				wa_prl_deposit_otmena.setDrcrk("H");
//				wa_prl_deposit_otmena.setParent_payroll_id(wa_payroll_deposit.getPayroll_id());
//				wa_prl_deposit_otmena.setBonus_type_id(otmena);
//				payrollService.createNew(wa_prl_deposit_otmena,a_userID,true);
//				//prlDao.create(wa_prl_deposit_otmena);
//			}
			
			
			
			//deposit//////////////////////////////////
			
			
			//Prikaz boyinsha glav. obuchateldardan premiyasi otmen bolmaidi
			//Cancel main trainer premium
//			Payroll payroll_main_trainer_premium = new Payroll();			
			Payroll payroll_coordinator_premium = new Payroll();			
//			
//			if (dealer_prl!=null && dealer_prl.getMain_trainer_id()!=null)
//			{
//				payroll_main_trainer_premium = prlDao.dynamicFindSinglePayroll(" bukrs = "+dealer_prl.getBukrs() 
//						+" and gjahr ="+dealer_prl.getGjahr()+" and monat = "+dealer_prl.getMonat()+" and position_id="+main_trainer_pos_id
//						+" and drcrk='S' and bonus_type_id="+premium+" and staff_id="+dealer_prl.getMain_trainer_id());
//				
//				
//				
//				if (payroll_main_trainer_premium!=null && payroll_main_trainer_premium.getDmbtr()>0)
//				{
//					Payroll wa_prl_cancel = new Payroll();
//					BeanUtils.copyProperties(payroll_main_trainer_premium, wa_prl_cancel);
//					wa_prl_cancel.setPayroll_id(null);
//					if (payroll_main_trainer_premium.getBranch_id()==null || payroll_main_trainer_premium.getBranch_id().equals(0L)) wa_prl_cancel.setBranch_id(wa_branch.getBranch_id());
//					wa_prl_cancel.setDrcrk("H");
//					wa_prl_cancel.setParent_payroll_id(payroll_main_trainer_premium.getPayroll_id());
//					wa_prl_cancel.setDmbtr(payroll_main_trainer_premium.getDmbtr()/payroll_main_trainer_premium.getMatnr_count());
//					wa_prl_cancel.setBonus_type_id(otmena);
//					wa_prl_cancel.setText45("отмена договора обучатель");
//					wa_prl_cancel.setPayroll_date(curDate.getTime());
//					wa_prl_cancel.setBldat(curDate.getTime());
//					payrollService.createNew(wa_prl_cancel,a_userID,true,a_tcode,9);
//					//prlDao.create(wa_prl_cancel);
//				}
//			}
			

			//Prikaz boyinsha glav. obuchateldardan premiyasi otmen bolmaidi
			
//			//Cancel coordinator premium			
//			if (dealer_prl!=null && dealer_prl.getCoordinator_id()!=null)
//			{
//				payroll_coordinator_premium = prlDao.dynamicFindSinglePayroll(" bukrs = "+dealer_prl.getBukrs() 
//						+" and gjahr ="+dealer_prl.getGjahr()+" and monat = "+dealer_prl.getMonat()+" and position_id="+coordinator_pos_id
//						+" and drcrk='S' and bonus_type_id="+premium+" and staff_id="+dealer_prl.getCoordinator_id());
//				
//				
//				
//				if (payroll_coordinator_premium!=null && payroll_coordinator_premium.getDmbtr()>0)
//				{
//					Payroll wa_prl_cancel = new Payroll();
//					BeanUtils.copyProperties(payroll_coordinator_premium, wa_prl_cancel);
//					if (payroll_coordinator_premium.getBranch_id()==null || payroll_coordinator_premium.getBranch_id().equals(0L)) wa_prl_cancel.setBranch_id(wa_branch.getBranch_id());
//					wa_prl_cancel.setPayroll_id(null);
//					wa_prl_cancel.setDrcrk("H");
//					wa_prl_cancel.setParent_payroll_id(payroll_coordinator_premium.getPayroll_id());
//					wa_prl_cancel.setDmbtr(payroll_coordinator_premium.getDmbtr()/payroll_coordinator_premium.getMatnr_count());
//					wa_prl_cancel.setBonus_type_id(otmena);
//					wa_prl_cancel.setText45("отмена договора коорд");
//					wa_prl_cancel.setPayroll_date(curDate.getTime());
//					wa_prl_cancel.setBldat(curDate.getTime());
//					payrollService.createNew(wa_prl_cancel,a_userID,true,a_tcode,9);
//					//prlDao.create(wa_prl_cancel);
//				}
//			}
//
//	
//			
//			
//
//			
//			
//			
//			
//			
//			Payroll payroll_coordinator_bonus = new Payroll();
//			if (dealer_prl!=null && dealer_prl.getCoordinator_id()!=null)
//			{
//				payroll_coordinator_bonus = prlDao.dynamicFindSinglePayroll(" bukrs = "+dealer_prl.getBukrs() 
//							+" and gjahr ="+dealer_prl.getGjahr()+" and monat = "+dealer_prl.getMonat()+" and position_id="+coordinator_pos_id
//							+" and drcrk='S' and bonus_type_id="+bonus+" and staff_id="+dealer_prl.getCoordinator_id());
//			}
//			
//			BonusArchive p_bonus_coordinator = new BonusArchive();			
//			p_bonus_coordinator = bonArcDao.dynamicFindBonus(" bukrs = "+a_contract.getBukrs()+" and bonus_type_id= "+bonus
//					+ " and month = "+dealer_prl.getMonat()+" and year ="+ dealer_prl.getGjahr() +" and position_id= "+coordinator_pos_id+" and matnr = "+wa_con_type.getMatnr());
//			if (p_bonus_coordinator!=null && p_bonus_coordinator.getBonus_id()!=null && payroll_coordinator_bonus!=null && payroll_coordinator_bonus.getPayroll_id()!=null)
//			{
//				Payroll payroll_coordinator_bonus_otmena = prlDao.dynamicFindSinglePayroll(" bukrs = "+dealer_prl.getBukrs() 
//						+" and gjahr ="+dealer_prl.getGjahr()+" and monat = "+dealer_prl.getMonat()+" and position_id="+coordinator_pos_id
//						+" and bonus_type_id="+otmena+" and staff_id="+dealer_prl.getCoordinator_id()+" and parent_payroll_id="+payroll_coordinator_bonus.getPayroll_id());
//				if (payroll_coordinator_bonus.getMatnr_count()-1<p_bonus_coordinator.getFrom_num() && payroll_coordinator_bonus_otmena==null)
//				{
//					Payroll wa_prl_cancel = new Payroll();
//					BeanUtils.copyProperties(payroll_coordinator_bonus, wa_prl_cancel);
//					wa_prl_cancel.setPayroll_id(null);
//					if (payroll_coordinator_bonus.getBranch_id()==null || payroll_coordinator_bonus.getBranch_id().equals(0L)) wa_prl_cancel.setBranch_id(wa_branch.getBranch_id());
//					wa_prl_cancel.setDrcrk("H");
//					wa_prl_cancel.setParent_payroll_id(payroll_coordinator_bonus.getPayroll_id());
//					wa_prl_cancel.setDmbtr(payroll_coordinator_bonus.getDmbtr());
//					wa_prl_cancel.setBonus_type_id(otmena);
//					wa_prl_cancel.setText45("отмена договора коорд бонус");
//					wa_prl_cancel.setPayroll_date(curDate.getTime());
//					wa_prl_cancel.setBldat(curDate.getTime());
//					payrollService.createNew(wa_prl_cancel,a_userID,true,a_tcode,9);
//					//prlDao.create(wa_prl_cancel);
//				}
//				
//			}
			
			//Prikaz boyinsha glav. obuchateldardan premiyasi otmen bolmaidi
//			Payroll payroll_main_trainer_bonus = new Payroll();
//			if (dealer_prl!=null && dealer_prl.getMain_trainer_id()!=null)
//			{
//				payroll_main_trainer_bonus = prlDao.dynamicFindSinglePayroll(" bukrs = "+dealer_prl.getBukrs() 
//						+" and gjahr ="+dealer_prl.getGjahr()+" and monat = "+dealer_prl.getMonat()+" and position_id="+main_trainer_pos_id
//						+" and drcrk='S' and bonus_type_id="+bonus+" and staff_id="+dealer_prl.getMain_trainer_id());
//			}	
//			BonusArchive p_bonus_main_trainer = new BonusArchive();
//			p_bonus_main_trainer  = bonArcDao.dynamicFindBonus(" bukrs = "+a_contract.getBukrs()+" and bonus_type_id= "+bonus
//					+ " and month = "+dealer_prl.getMonat()+" and year ="+ dealer_prl.getGjahr() +" and position_id= "+main_trainer_pos_id+" and matnr = "+wa_con_type.getMatnr());	
//			
//			if (p_bonus_main_trainer!=null && p_bonus_main_trainer.getBonus_id()!=null && payroll_main_trainer_bonus!=null && payroll_main_trainer_bonus.getPayroll_id()!=null)
//			{
//				Payroll payroll_main_trainer_bonus_otmena = prlDao.dynamicFindSinglePayroll(" bukrs = "+dealer_prl.getBukrs() 
//						+" and gjahr ="+dealer_prl.getGjahr()+" and monat = "+dealer_prl.getMonat()+" and position_id="+main_trainer_pos_id
//						+" and bonus_type_id="+otmena+" and staff_id="+dealer_prl.getMain_trainer_id()+" and parent_payroll_id="+payroll_main_trainer_bonus.getPayroll_id());
//				if (payroll_main_trainer_bonus.getMatnr_count()-1<p_bonus_main_trainer.getFrom_num() && payroll_main_trainer_bonus_otmena==null)
//				{
//					Payroll wa_prl_cancel = new Payroll();
//					BeanUtils.copyProperties(payroll_main_trainer_bonus, wa_prl_cancel);
//					if (payroll_main_trainer_bonus.getBranch_id()==null || payroll_main_trainer_bonus.getBranch_id().equals(0L)) wa_prl_cancel.setBranch_id(wa_branch.getBranch_id());
//					wa_prl_cancel.setPayroll_id(null);
//					wa_prl_cancel.setDrcrk("H");
//					wa_prl_cancel.setParent_payroll_id(payroll_main_trainer_bonus.getPayroll_id());
//					wa_prl_cancel.setDmbtr(payroll_main_trainer_bonus.getDmbtr());
//					wa_prl_cancel.setBonus_type_id(otmena);
//					wa_prl_cancel.setText45("отмена договора гл.обуч бонус");
//					wa_prl_cancel.setPayroll_date(curDate.getTime());
//					wa_prl_cancel.setBldat(curDate.getTime());
//					payrollService.createNew(wa_prl_cancel,a_userID,true,a_tcode,9);
//					//prlDao.create(wa_prl_cancel);
//				}
//			}
			
//			Payroll payroll_trainer_bonus = new Payroll();
//			if (dealer_prl!=null && dealer_prl.getTrainer_id()!=null)
//			{
//			payroll_trainer_bonus = prlDao.dynamicFindSinglePayroll(" bukrs = "+dealer_prl.getBukrs() 
//					+" and gjahr ="+dealer_prl.getGjahr()+" and monat = "+dealer_prl.getMonat()+" and position_id="+trainer_pos_id
//					+" and drcrk='S' and bonus_type_id="+bonus+" and staff_id="+dealer_prl.getTrainer_id());
//			}
//			
//			BonusArchive p_bonus_trainer = new BonusArchive();			
//			p_bonus_trainer  = bonArcDao.dynamicFindBonus(" bukrs = "+a_contract.getBukrs()+" and bonus_type_id= "+bonus
//					+ " and month = "+dealer_prl.getMonat()+" and year ="+ dealer_prl.getGjahr() +" and position_id= "+trainer_pos_id+" and matnr = "+wa_con_type.getMatnr()
//					+ " and "
//					) ;
//			if (p_bonus_trainer!=null && p_bonus_trainer.getBonus_id()!=null && payroll_trainer_bonus!=null && payroll_trainer_bonus.getPayroll_id()!=null)
//			{
//				Payroll payroll_trainer_bonus_otmena = prlDao.dynamicFindSinglePayroll(" bukrs = "+dealer_prl.getBukrs() 
//						+" and gjahr ="+dealer_prl.getGjahr()+" and monat = "+dealer_prl.getMonat()+" and position_id="+trainer_pos_id
//						+" and bonus_type_id="+otmena+" and staff_id="+dealer_prl.getTrainer_id()+" and parent_payroll_id="+payroll_trainer_bonus.getPayroll_id());
//				if (payroll_trainer_bonus.getMatnr_count()-1<p_bonus_trainer.getFrom_num() && payroll_trainer_bonus_otmena==null)
//				{
//					Payroll wa_prl_cancel = new Payroll();
//					BeanUtils.copyProperties(payroll_trainer_bonus, wa_prl_cancel);
//					if (payroll_trainer_bonus.getBranch_id()==null || payroll_trainer_bonus.getBranch_id().equals(0L)) wa_prl_cancel.setBranch_id(wa_branch.getBranch_id());
//					wa_prl_cancel.setPayroll_id(null);
//					wa_prl_cancel.setDrcrk("H");
//					wa_prl_cancel.setParent_payroll_id(payroll_trainer_bonus.getPayroll_id());
//					wa_prl_cancel.setDmbtr(payroll_trainer_bonus.getDmbtr());
//					wa_prl_cancel.setBonus_type_id(otmena);
//					wa_prl_cancel.setText45("отмена договора обуч бонус");
//					wa_prl_cancel.setPayroll_date(curDate.getTime());
//					wa_prl_cancel.setBldat(curDate.getTime());
//					payrollService.createNew(wa_prl_cancel,a_userID,true,a_tcode,9);
//					//prlDao.create(wa_prl_cancel);
//				}
//			}
	

			
			
		}
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	/*public void conClosedFromDepositToDealer(Bkpf wa_bkpf_GC, Bkpf wa_bkpf_payment) throws DAOException
	{
		try{
			
			
			/*Contract con = new Contract();
			con = conDao.findByContractNumber(wa_bkpf_GC.getContract_number());
			Staff dealer = new Staff();
			dealer = staffDao.find(con.getDealer());
			BonusArchive  wa_bonArc = new BonusArchive();
			Calendar conDate = Calendar.getInstance();
			PriceList wa_pl  = new PriceList();
			ContractHistory wa_ch = new ContractHistory();
			
			if (con!=null && con.getDealer()!=null)
			{
				conDate.setTime(con.getContract_date());
				
				wa_ch = conHisDao.findSinglePriceChange(GeneralUtil.getSQLDate(GeneralUtil.getLastDayOfMonth(conDate)),con.getContract_id());
				
				
				if (wa_ch!=null)
				{
					wa_pl = priceListDao.find(wa_ch.getOld_id());
				}
				else
				{
					wa_pl = priceListDao.find(con.getPrice_list_id());
				}
				
				
				wa_bonArc = bonArcDao.dynamicFindBonus(" year = "+ conDate.get(Calendar.YEAR) + " and month = "+ (conDate.get(Calendar.MONTH)+1) +" and bukrs ='"+wa_bkpf_GC.getBukrs()+"'"+
						" and deposit>0 and position_id = 4 and bonus_type_id = 1 and country_id = "+con.getAddr_dom_countryid()+" and matnr = "+wa_pl.getMatnr());

				
			}
			
			
			//int countPS = psDao.countAll(" awkey = "+ String.valueOf(wa_bkpf_GC.getBelnr())+String.valueOf(wa_bkpf_GC.getGjahr()));
			if (con!=null && dealer!=null  && wa_bonArc!=null && wa_pl!=null && wa_pl.getMonth()>1 && wa_bkpf_payment!=null)
			{	
				
				
				Bkpf p_bkpf = new Bkpf();
				List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
				List<Bsik> pl_Bsik = new ArrayList<Bsik>();
				List<Bsik> pl_bsik = new ArrayList<Bsik>();
				Bseg wa_bsegKredit = new Bseg(); 
				Bseg wa_bsegDebet = new Bseg();
				Payroll wa_payroll = new Payroll();
				Calendar curDate = Calendar.getInstance(); 
				//Time cputm = new Time(curDate.getTimeInMillis()); 
				p_bkpf.setGjahr(wa_bkpf_payment.getGjahr());			
				p_bkpf.setBukrs(wa_bkpf_payment.getBukrs());
				p_bkpf.setBudat(curDate.getTime());
				p_bkpf.setBldat(curDate.getTime());
				p_bkpf.setBrnch(wa_bkpf_payment.getBrnch());
				p_bkpf.setMonat(wa_bkpf_payment.getMonat());  
				p_bkpf.setCpudt(curDate.getTime());
				p_bkpf.setTcode("FACMASSIN");
				p_bkpf.setCustomer_id(dealer.getCustomer_id());
				p_bkpf.setBktxt("Deposit");
				
				//tenge kilamiz eger dollar bolsa
				if (wa_bonArc.getWaers().equals("USD"))
				{
					p_bkpf.setWaers(wa_bkpf_GC.getWaers());
					p_bkpf.setKursf(wa_bkpf_GC.getKursf());
					p_bkpf.setDmbtr(wa_bonArc.getDeposit());
					p_bkpf.setWrbtr(wa_bkpf_GC.getKursf()*wa_bonArc.getDeposit());
				}
				else
				{
					p_bkpf.setWaers(wa_bonArc.getWaers());
					p_bkpf.setKursf(wa_bkpf_GC.getKursf());
					p_bkpf.setDmbtr(wa_bonArc.getDeposit()/wa_bkpf_GC.getKursf());
					p_bkpf.setWrbtr(wa_bonArc.getDeposit());
				}
				
				
				Deposit wa_dep = new Deposit();
			    wa_dep = depDao.dynamicFindSingle(" waers = '"+p_bkpf.getWaers()+"' and staff_id = "+dealer.getStaff_id()+" and customer_id ="+dealer.getCustomer_id());
			    
			    if (wa_dep!=null)
			    {
			    	if (p_bkpf.getWaers().equals("USD"))
			    	{
			    		if (wa_dep.getAmount()<p_bkpf.getDmbtr())
			    		{
			    			throw new DAOException("Deposit amount is not enough");
			    		}
			    		else
			    		{
			    			wa_dep.setAmount(wa_dep.getAmount()-p_bkpf.getDmbtr());
			    		}
			    	}
			    	else
			    	{
			    		if (wa_dep.getAmount()<p_bkpf.getWrbtr())
			    		{
			    			throw new DAOException("Deposit amount is not enough");
			    		}
			    		else
			    		{
			    			wa_dep.setAmount(wa_dep.getAmount()-p_bkpf.getWrbtr());
			    		}
			    		
			    	}
			    }
			    
				if (wa_bkpf_payment.getBrnch()==null){
					throw new DAOException("Branch not selected. Finance Service(Salary), Customer id = " + p_bkpf.getCustomer_id());
				}
				if (dealer.getStaff_id()==null || dealer.getStaff_id().equals("0")){
					throw new DAOException("Employee not selected. Finance Service(Salary)");
				}
				
				p_bkpf.setBlart("PS");
				wa_bsegKredit.setHkont("33500022");				
				wa_bsegKredit.setGjahr(p_bkpf.getGjahr());
				wa_bsegKredit.setBukrs(p_bkpf.getBukrs());
				wa_bsegKredit.setBuzei(1);					 
				wa_bsegKredit.setLifnr(dealer.getCustomer_id());			
				
				wa_bsegDebet.setHkont("71100001");
				wa_bsegDebet.setGjahr(p_bkpf.getGjahr());
				wa_bsegDebet.setBukrs(p_bkpf.getBukrs());
				wa_bsegDebet.setBuzei(2);
				
				
				
	
				p_bkpf.setAwtyp(2);
				wa_bsegKredit.setLifnr(dealer.getCustomer_id());	
				wa_bsegKredit.setBschl("34");
				wa_bsegKredit.setShkzg("H");
				wa_bsegDebet.setBschl("40");
				wa_bsegDebet.setShkzg("S");
	
				wa_bsegKredit.setDmbtr(p_bkpf.getDmbtr());
				wa_bsegKredit.setWrbtr(p_bkpf.getWrbtr());
				wa_bsegDebet.setDmbtr(p_bkpf.getDmbtr());
				wa_bsegDebet.setWrbtr(p_bkpf.getWrbtr());		
				
				pl_bseg.add(wa_bsegKredit);
				pl_bseg.add(wa_bsegDebet);
				
				
				for (Bseg wa_bseg3 : pl_bseg) { 
				   	Bsik wa_bsik = new Bsik();   
				      	
				   	wa_bsik.setBukrs(wa_bseg3.getBukrs());
				  	wa_bsik.setGjahr(wa_bseg3.getGjahr());
				   	wa_bsik.setBuzei(wa_bseg3.getBuzei());
				   	wa_bsik.setBschl(wa_bseg3.getBschl());
				   	wa_bsik.setHkont(wa_bseg3.getHkont());
				  	wa_bsik.setShkzg(wa_bseg3.getShkzg());
				   	wa_bsik.setDmbtr(wa_bseg3.getDmbtr());
				   	wa_bsik.setWrbtr(wa_bseg3.getWrbtr());
				   	wa_bsik.setLifnr(wa_bseg3.getLifnr()); 				
				   	pl_bsik.add(wa_bsik);
				}  
					
		
	
			    financeService.check_empty_fields(p_bkpf, pl_bseg); 
			    financeService.insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik, pl_Bsik);
			    
			    if (p_bkpf.getAwtyp()==2)
				{
				    wa_payroll.setWaers(p_bkpf.getWaers());
				    wa_payroll.setBukrs(p_bkpf.getBukrs());
				    if (p_bkpf.getWaers().equals("USD"))
				    {
				    	wa_payroll.setDmbtr(p_bkpf.getDmbtr());
				    }
				    else
				    {
				    	wa_payroll.setDmbtr(p_bkpf.getWrbtr());
				    }
				    
				    wa_payroll.setDrcrk("S");
				    wa_payroll.setGjahr(p_bkpf.getGjahr());
				    wa_payroll.setMonat(p_bkpf.getMonat());
				    wa_payroll.setPayroll_date(curDate.getTime());
				    wa_payroll.setStaff_id(con.getDealer());
				    wa_payroll.setBonus_id(wa_bonArc.getBonus_id());
				    wa_payroll.setText45("Premium");
				    wa_payroll.setText45(p_bkpf.getBktxt());
				    prlDao.create(wa_payroll);
				    p_bkpf.setPayroll_id(wa_payroll.getPayroll_id());
				    financeService.updateFiDoc(p_bkpf, null);
				}
			    
			    depDao.update(wa_dep);
			    wa_bkpf_GC.setBktxt("ZZZZZZZZZZZZZZZZ");
			}
			else if (con!=null && dealer!=null  && wa_bonArc!=null && wa_pl!=null && wa_pl.getMonth()>1 && wa_bkpf_payment==null)
			{
				Deposit wa_dep = new Deposit();
			    wa_dep = depDao.dynamicFindSingle(" waers = '"+wa_bkpf_GC.getWaers()+"' and staff_id = "+dealer.getStaff_id()+" and customer_id ="+dealer.getCustomer_id());
			    
			  //tenge kilamiz eger dollar bolsa
				if (wa_dep!=null && wa_bonArc.getWaers().equals(wa_dep.getWaers())) //USD, or tenge
				{
					
					if (wa_dep.getAmount()<wa_bonArc.getDeposit())
		    		{
		    			throw new DAOException("Deposit amount is not enough");
		    		}
		    		else
		    		{
		    			wa_dep.setAmount(wa_dep.getAmount()-wa_bonArc.getDeposit());
		    		}
				}//wa_bkpf_GC.getWaers()
				else if (wa_dep!=null && wa_bonArc.getWaers().equals("USD")  && !wa_dep.getWaers().equals("USD"))
				{
					
					if (wa_dep.getAmount()<wa_bonArc.getDeposit()*wa_bkpf_GC.getKursf())
		    		{
		    			throw new DAOException("Deposit amount is not enough");
		    		}
		    		else
		    		{
		    			wa_dep.setAmount(wa_dep.getAmount()-(wa_bonArc.getDeposit()*wa_bkpf_GC.getKursf()));
		    		}
					
				}

				depDao.update(wa_dep);
			}
		}
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}*/
	/*
	public void updateOnceDeleteTest() throws DAOException
	{
		try{
			Long wa_belnr_GC =  1121100110L;
        	int wa_gjahr_GC = 2016;
        	Calendar curDate = Calendar.getInstance();
			String dynamicWhereClause = "";
            dynamicWhereClause = "";
            dynamicWhereClause = " belnr = "+wa_belnr_GC+" and gjahr = "+wa_gjahr_GC +" and storno=0 and closed=0";			
        	Bkpf wa_bkpf_GC = bkpfDao.dynamicFindSingleBkpf(dynamicWhereClause);


			
            //if (bkpfDao.updateDynamicSingleBkpf(wa_belnr_GC, wa_gjahr_GC, dynamicWhereClause)!=1)
        	//{
        		//throw new DAOException("Account Recievable not updated");
        	//}
            
            System.out.println(((wa_bkpf_GC.getWaers().equals("USD") && wa_bkpf_GC.getDmbtr()==wa_bkpf_GC.getDmbtr_paid()) ||
            	(!wa_bkpf_GC.getWaers().equals("USD") && wa_bkpf_GC.getWrbtr()==wa_bkpf_GC.getWrbtr_paid()))	);
            
            if (wa_bkpf_GC!=null)
            {	
	            if ((wa_bkpf_GC.getWaers().equals("USD") && wa_bkpf_GC.getDmbtr()==wa_bkpf_GC.getDmbtr_paid()) ||
	            	(!wa_bkpf_GC.getWaers().equals("USD") && wa_bkpf_GC.getWrbtr()==wa_bkpf_GC.getWrbtr_paid()))	
	            {
	            	//System.out.println(555);
	            	//If it is last payment
	
	            	dynamicWhereClause = "";
	
					
					dynamicWhereClause = " contract_status_id = 5";
					dynamicWhereClause = dynamicWhereClause + ", close_date = '"+GeneralUtil.getSQLDate(curDate)+"'";
					conDao.updateDynamicSingleCon(wa_bkpf_GC.getContract_number(), dynamicWhereClause);
	
					createEXRATEDiffDMS2(wa_bkpf_GC, "12100002", 815L);
					bkpfDao.updateDynamicSingleBkpf(wa_belnr_GC, wa_gjahr_GC, " closed = 1");
	            }
            }
		}
        catch (Exception ex)
    	{	    		
    		throw new DAOException(ex.getMessage());
    	}
	}
	
	public void createEXRATEDiffDMS2(Bkpf a_bkpf_GC, String a_hkont, Long a_matnr) throws DAOException 
	{
		try
		{
			Calendar curDate = Calendar.getInstance();
			if (!a_bkpf_GC.getWaers().equals("USD") && a_bkpf_GC.getDmbtr()!= a_bkpf_GC.getDmbtr_paid() && a_bkpf_GC.getWrbtr()== a_bkpf_GC.getWrbtr_paid())
			{	
				Bkpf wa_bkpf = new Bkpf();
				List<Bseg> wal_bseg = new ArrayList<Bseg>();
				List<Bsik> wal_Bsik = new ArrayList<Bsik>();
				List<Bsik> wal_bsik = new ArrayList<Bsik>();
				wa_bkpf.setBukrs(a_bkpf_GC.getBukrs());
				wa_bkpf.setGjahr(a_bkpf_GC.getGjahr()); 
				wa_bkpf.setBlart("ED");
				wa_bkpf.setBudat(curDate.getTime());
				wa_bkpf.setBldat(curDate.getTime());
				wa_bkpf.setMonat(a_bkpf_GC.getMonat());
				wa_bkpf.setCpudt(a_bkpf_GC.getCpudt());
				wa_bkpf.setCputm(a_bkpf_GC.getCputm()); 
				wa_bkpf.setUsnam(a_bkpf_GC.getUsnam());
				wa_bkpf.setTcode(a_bkpf_GC.getTcode());
				wa_bkpf.setWaers("USD");
				wa_bkpf.setKursf(a_bkpf_GC.getKursf()); 
				wa_bkpf.setBrnch(a_bkpf_GC.getBrnch());
				wa_bkpf.setContract_number(a_bkpf_GC.getContract_number()); 
				wa_bkpf.setCustomer_id(a_bkpf_GC.getCustomer_id());
				wa_bkpf.setAwtyp(a_bkpf_GC.getAwtyp());
				wa_bkpf.setAwkey(GeneralUtil.getPreparedAwkey(a_bkpf_GC.getBelnr(), a_bkpf_GC.getGjahr()));
				wa_bkpf.setAwkey2(a_bkpf_GC.getAwkey2());
				Bseg wa_bsegD = new Bseg();
				Bseg wa_bsegK = new Bseg();
				wa_bsegD.setBukrs(wa_bkpf.getBukrs());
				wa_bsegD.setGjahr(wa_bkpf.getGjahr());
				wa_bsegK.setBukrs(wa_bkpf.getBukrs());
				wa_bsegK.setGjahr(wa_bkpf.getGjahr());
				if (a_bkpf_GC.getDmbtr()>a_bkpf_GC.getDmbtr_paid())
				{	
					wa_bkpf.setDmbtr(a_bkpf_GC.getDmbtr()- a_bkpf_GC.getDmbtr_paid());
					wa_bsegD.setBuzei(1);
					wa_bsegD.setBschl("40");
					wa_bsegD.setShkzg("S");					
					if (a_matnr!=null && a_matnr == 1) {wa_bsegD.setHkont("74300003");}
					else if (a_matnr!=null && a_matnr == 2) {wa_bsegD.setHkont("74300004");}
					else if (a_matnr!=null && a_matnr == 815) {wa_bsegD.setHkont("74300003");}
					else if (a_matnr!=null && (a_matnr == 816 || a_matnr == 910)) {wa_bsegD.setHkont("74300005");}
					else if (a_matnr!=null && a_matnr == 817) {wa_bsegD.setHkont("74300004");}
					wa_bsegD.setDmbtr(a_bkpf_GC.getDmbtr()- a_bkpf_GC.getDmbtr_paid());
					wal_bseg.add(wa_bsegD);
					//System.out.println(matnr);
					//74300003	Курс. Разница Rainbow 815	RAINBOW
					//74300004	Курс. Разница Rexwat ECO 817	REXWAT ECO
					//74300005	Курс. Разница Rexwat Atlas 816	REXWAT ATLAS
					
					
					
					wa_bsegK.setBuzei(2);
					wa_bsegK.setBschl("50");
					wa_bsegK.setShkzg("H");
					wa_bsegK.setHkont(a_hkont);							
					wa_bsegK.setLifnr(wa_bkpf.getCustomer_id());
					wa_bsegK.setDmbtr(a_bkpf_GC.getDmbtr()- a_bkpf_GC.getDmbtr_paid());
					wal_bseg.add(wa_bsegK);
				}
				else
				{
					wa_bkpf.setDmbtr(a_bkpf_GC.getDmbtr_paid() - a_bkpf_GC.getDmbtr());
					wa_bsegD.setBuzei(1);
					wa_bsegD.setBschl("40");
					wa_bsegD.setShkzg("S");
					wa_bsegD.setHkont(a_hkont);
					wa_bsegD.setDmbtr(a_bkpf_GC.getDmbtr_paid() - a_bkpf_GC.getDmbtr());
					wal_bseg.add(wa_bsegD);
					
					wa_bsegK.setBuzei(2);	
					wa_bsegK.setBschl("50");
					wa_bsegK.setShkzg("H");
					wa_bsegK.setHkont("");				
					if (a_matnr!=null && a_matnr==1) {wa_bsegK.setHkont("62500003");}
					else if (a_matnr!=null && a_matnr==2) {wa_bsegK.setHkont("62500004");}
					else if (a_matnr!=null && a_matnr == 815) {wa_bsegD.setHkont("62500003");}
					else if (a_matnr!=null && (a_matnr == 816 || a_matnr == 910)) {wa_bsegD.setHkont("62500005");}
					else if (a_matnr!=null && a_matnr == 817) {wa_bsegD.setHkont("62500004");}
					//62500003	Курс. Разница Rainbow 815	RAINBOW
					//62500004	Курс. Разница Rexwat ECO 817	REXWAT ECO
					//62500005	Курс. Разница Rexwat Atlas 816	REXWAT ATLAS
					wa_bsegK.setDmbtr(a_bkpf_GC.getDmbtr_paid() - a_bkpf_GC.getDmbtr());
					wal_bseg.add(wa_bsegK);
				}
				//Create fin docs
				financeService.check_empty_fields(wa_bkpf, wal_bseg);
				financeService.insertNewFiDoc(wa_bkpf, wal_bseg, wal_bsik, wal_Bsik);
			}
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}*/
	
	public Long createContractRecievableOldData (Contract a_contract, Branch a_branch, 
			String a_waers, double a_kursf, double a_dmbtr, double a_wrbtr, Long a_matnr, List<ContractFinOperations> contrFOList, List<Promotion> promoList) throws DAOException{
		try{			
			System.out.println("Creating Finance Document");
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_Bsik = new ArrayList<Bsik>();
			List<Bsik> pl_bsik = new ArrayList<Bsik>();
			
			Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis()); 
			
			
			if (a_contract==null || a_contract.getContract_id()== null || a_contract.getContract_id() == 0 ){
				throw new DAOException("Contract id is empty");
			} 
			
			if (a_waers == null || a_waers.isEmpty())
			{
				throw new DAOException("Waers is empty");
			}
			
			if (a_waers.equals("USD") && a_dmbtr == 0){ 
				throw new DAOException("The amount in local currency is 0"); 
			}
			else if (a_wrbtr == 0){					
				throw new DAOException("The amount in foreign currency is 0"); 
			}
			
			if (a_branch==null){
				throw new DAOException("Branch is empty");
			}
			

			
			if (a_kursf <= 0){
				throw new DAOException("Currency rate is empty");
			}
			
			p_bkpf.setBukrs(a_contract.getBukrs());
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setBlart("GC");
			p_bkpf.setBudat(curDate.getTime());
			p_bkpf.setBldat(curDate.getTime());
            p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
            p_bkpf.setCpudt(curDate.getTime());
            //p_bkpf.setUsnam(a_userID);
            //p_bkpf.setTcode(a_tcode);
            p_bkpf.setWaers(a_waers);
            p_bkpf.setKursf(a_kursf); 
            p_bkpf.setBrnch(a_branch.getBranch_id());
            p_bkpf.setBusiness_area_id(a_branch.getBusiness_area_id());
			p_bkpf.setContract_number(a_contract.getContract_number()); 
			p_bkpf.setCustomer_id(a_contract.getCustomer_id());
			p_bkpf.setAwtyp(1);
			if (a_waers.equals("USD")) p_bkpf.setDmbtr(a_dmbtr); 
			else p_bkpf.setWrbtr(a_wrbtr); p_bkpf.setDmbtr(a_wrbtr/a_kursf);
				
			int wa_buzei = 0;
			wa_buzei++;
            Bseg wa_bseg = new Bseg();
            wa_bseg.setBukrs(p_bkpf.getBukrs());
            wa_bseg.setGjahr(p_bkpf.getGjahr());
            wa_bseg.setBuzei(wa_buzei);
            wa_bseg.setBschl("1");
			wa_bseg.setShkzg("S");
			wa_bseg.setHkont("12100001");
			wa_bseg.setLifnr(p_bkpf.getCustomer_id());			
			wa_bseg.setMatnr(a_matnr); 
			wa_bseg.setDmbtr(a_dmbtr);
			wa_bseg.setWrbtr(a_wrbtr);
			wa_bseg.setMenge(1); 
			pl_bseg.add(wa_bseg);
			
			wa_buzei++;
			wa_bseg = new Bseg();
            wa_bseg.setBukrs(p_bkpf.getBukrs());
            wa_bseg.setGjahr(p_bkpf.getGjahr());
            wa_bseg.setBuzei(wa_buzei);
            wa_bseg.setBschl("50");
			wa_bseg.setShkzg("H");
			if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==1 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100001");}//Roboclean
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==11 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100001");}//Roboclean
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==2 && a_contract.getBukrs().equals("1000")){	wa_bseg.setHkont("60100002");}//Cebilon
			
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==3 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100001");}//Rainbow E2
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==17 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100001");}//Rainbow SRX
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==4 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100003");}//Rexwat Atlas
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==5 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100002");}//Rexwat Eco
			else if (a_contract.getContract_type_id()!=null && a_contract.getContract_type_id()==10 && a_contract.getBukrs().equals("2000")){	wa_bseg.setHkont("60100002");}//Rexwat Eco Restyle 2016
			else if (a_contract.getBukrs().equals("6000")){	wa_bseg.setHkont("60100001");}//Roboclean
			else {throw new DAOException("Good type for income GL account not selected");}		
			wa_bseg.setDmbtr(a_dmbtr);
			wa_bseg.setWrbtr(a_wrbtr);
			pl_bseg.add(wa_bseg);

			
			
			
            
			if (p_bkpf.getWaers() == null || p_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (p_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (p_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			if (p_bkpf.getBusiness_area_id()==null || p_bkpf.getBusiness_area_id()==0){
				throw new DAOException("Business area not chosen");
			}
			if (p_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}

					/*
					if (wa_bseg.getStaff_id()!=null && wa_bseg.getStaff_id() > 0 && wa_bseg.getStaff_id() != a_contract.getCollector()){
						throw new DAOException("Collector and Contract collecter are not the same");
					} 

					if (wa_bseg.getLifnr() == null || wa_bseg.getLifnr() == 0 || (wa_bseg.getLifnr() != a_contract.getCustomer_id())){
						throw new DAOException("Customer is empty or not the same with the contract customer");
					}
					*/

				
					
				 
				
				
			
            for (Bseg wa_bseg2 : pl_bseg) { 
            	Bsik wa_Bsik = new Bsik(); 
            	wa_Bsik.setBukrs(wa_bseg2.getBukrs());
            	wa_Bsik.setGjahr(wa_bseg2.getGjahr());
            	wa_Bsik.setBuzei(wa_bseg2.getBuzei());
            	wa_Bsik.setBschl(wa_bseg2.getBschl());
            	wa_Bsik.setHkont(wa_bseg2.getHkont());
            	wa_Bsik.setShkzg(wa_bseg2.getShkzg());
            	wa_Bsik.setDmbtr(wa_bseg2.getDmbtr());
            	wa_Bsik.setWrbtr(wa_bseg2.getWrbtr());
            	wa_Bsik.setLifnr(wa_bseg2.getLifnr()); 
            	wa_Bsik.setMenge(wa_bseg2.getMenge());
            	wa_Bsik.setMatnr(wa_bseg2.getMatnr());            	 				
            	pl_bsik.add(wa_Bsik);
			}  
            
            financeService.check_empty_fields(p_bkpf, pl_bseg);
            Long wa_belnr =financeService.insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);
            
            //createAdditionalFinDocs(contrFOList, a_branch, p_bkpf.getCustomer_id(), 
            //		debetHkont, Long.parseLong(String.valueOf(p_bkpf.getBelnr())+String.valueOf(p_bkpf.getGjahr())), a_contract, curDate, cputm, a_userID,  a_tcode, p_bkpf, promoList);
          
            
            int gjahr = p_bkpf.getGjahr();
            Long wa_awkey = Long.parseLong( String.valueOf(wa_belnr)+String.valueOf(gjahr));
			return wa_awkey;
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	public void updateContract(Long a_awkey,String a_bukrs) throws DAOException
	{
		try
		{
			//Update Account recievable amount
			//Get the Account recievable finc doc
			//System.out.println(p_bkpf.getAwkey());
			Long wa_belnr_GC =  Long.parseLong(String.valueOf(a_awkey).substring(0, 10));
        	int wa_gjahr_GC = Integer.parseInt(String.valueOf(a_awkey).substring(10, 14));
        	String wa_bukrs_GC = a_bukrs;
        	Calendar curDate = Calendar.getInstance();
			String dynamicWhereClause = "";
            dynamicWhereClause = "";
            dynamicWhereClause = " belnr = "+wa_belnr_GC+" and gjahr = "+wa_gjahr_GC +" and storno=0";			
        	Bkpf wa_bkpf_GC = bkpfDao.dynamicFindSingleBkpf(dynamicWhereClause,wa_bukrs_GC);
        	if ((wa_bkpf_GC.getWaers().equals("USD") && wa_bkpf_GC.getDmbtr()>wa_bkpf_GC.getDmbtr_paid()) ||
            (!wa_bkpf_GC.getWaers().equals("USD") && wa_bkpf_GC.getWrbtr()>wa_bkpf_GC.getWrbtr_paid()))	
            {
        		throw new DAOException("Сумма не погашена.");
            }
        	
            
            if ((wa_bkpf_GC.getWaers().equals("USD") && wa_bkpf_GC.getDmbtr()==wa_bkpf_GC.getDmbtr_paid()) ||
            	(!wa_bkpf_GC.getWaers().equals("USD") && wa_bkpf_GC.getWrbtr()==wa_bkpf_GC.getWrbtr_paid()))	
            {

				
				dynamicWhereClause = " contract_status_id = 5";
				dynamicWhereClause = dynamicWhereClause + ", close_date = '"+GeneralUtil.getSQLDate(curDate)+"'";
				conDao.updateDynamicSingleCon(wa_bkpf_GC.getContract_number(), dynamicWhereClause);
				
				
				//Prikaz ot 31.08.17
//				Payroll wa_payroll_deposit = new Payroll();
//				wa_payroll_deposit = prlDao.dynamicFindSinglePayroll(" bukrs = '"+wa_bkpf_GC.getBukrs()+"' and contract_number="
//				+wa_bkpf_GC.getContract_number()+" and bonus_type_id="+deposit+" and position_id="+dealer_pos_id);
//				if (wa_payroll_deposit!=null)
//				{
//					wa_payroll_deposit.setApprove(0);
//					prlDao.update(wa_payroll_deposit);
//				}

				bkpfDao.updateDynamicSingleBkpf(wa_belnr_GC, wa_gjahr_GC, " closed = 1",wa_bukrs_GC);
				
            }
            
            //throw new DAOException("zz");
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	public void changeDealer(Contract a_con,Long a_old_dealer_staff_id,Long a_new_dealer_staff_id,Long a_userID, String a_tcode)throws DAOException
	{
		
		
		try{
			if (a_old_dealer_staff_id==null || a_old_dealer_staff_id.equals(0L))
			{
				throw new DAOException("old staff id null");
			}
			if (a_new_dealer_staff_id==null || a_new_dealer_staff_id.equals(0L))
			{
				throw new DAOException("new staff id null");
			}
			
			Payroll prlAD = new Payroll();
			prlAD = prlDao.dynamicFindSinglePayroll(" bukrs ='"+a_con.getBukrs()+"' and contract_number="+a_con.getContract_number()+" and bonus_type_id="+zaakciya
					+" and staff_id="+a_old_dealer_staff_id +" and position_id="+dealer_pos_id);
			
			Calendar curDate = Calendar.getInstance();
			
			
			if (prlAD!=null)
			{							
				    	Payroll new_prl = new Payroll();
						new_prl.setGjahr(curDate.get(Calendar.YEAR));
						new_prl.setMonat(curDate.get(Calendar.MONTH)+1);
						new_prl.setApprove(0);
						new_prl.setPayroll_date(curDate.getTime());
						new_prl.setBldat(curDate.getTime());//BLDAT
						new_prl.setStaff_id(a_old_dealer_staff_id);
						new_prl.setBranch_id(a_con.getBranch_id());//BRANCH_ID
						new_prl.setContract_number(a_con.getContract_number());//CONTRACT_NUMBER
						new_prl.setPlan_amount(0);//PLAN_AMOUNT
						new_prl.setFact_amount(0);
						new_prl.setPosition_id((long) dealer_pos_id);
						new_prl.setMatnr_count(0);
						new_prl.setApprove(0);
						new_prl.setBukrs(a_con.getBukrs());
						new_prl.setDrcrk("S");
						new_prl.setWaers(prlAD.getWaers());
						new_prl.setDmbtr(prlAD.getDmbtr());				
						new_prl.setBonus_type_id(otmena);
						new_prl.setText45("Замена дилера");				
						new_prl.setParent_payroll_id(prlAD.getPayroll_id());
						payrollService.createNew(new_prl,a_userID,true,a_tcode,6);
						
						Payroll new_prl2 = new Payroll();
						new_prl2.setGjahr(curDate.get(Calendar.YEAR));
						new_prl2.setMonat(curDate.get(Calendar.MONTH)+1);
						new_prl2.setApprove(0);
						new_prl2.setPayroll_date(curDate.getTime());
						new_prl2.setBldat(curDate.getTime());//BLDAT
						new_prl2.setStaff_id(a_new_dealer_staff_id);
						new_prl2.setBranch_id(a_con.getBranch_id());//BRANCH_ID
						new_prl2.setContract_number(a_con.getContract_number());//CONTRACT_NUMBER
						new_prl2.setPlan_amount(0);//PLAN_AMOUNT
						new_prl2.setFact_amount(0);
						new_prl2.setPosition_id((long) dealer_pos_id);
						new_prl2.setMatnr_count(0);
						new_prl2.setApprove(0);
						new_prl2.setBukrs(a_con.getBukrs());
						new_prl2.setDrcrk("H");
						new_prl2.setWaers(prlAD.getWaers());
						new_prl2.setDmbtr(prlAD.getDmbtr());	
						new_prl2.setBonus_type_id(zaakciya);
						new_prl2.setText45("за акционный товар");
						payrollService.createNew(new_prl2,a_userID,true,a_tcode,9);
			}
			
			Payroll prlSD = new Payroll();
			prlSD = prlDao.dynamicFindSinglePayroll(" bukrs ='"+a_con.getBukrs()+"' and contract_number="+a_con.getContract_number()+" and bonus_type_id="+skidkaotdilera
					+" and staff_id="+a_old_dealer_staff_id +" and position_id="+dealer_pos_id);
			
			if (prlSD!=null)
			{
				Payroll new_prl = new Payroll();
				new_prl.setGjahr(curDate.get(Calendar.YEAR));
				new_prl.setMonat(curDate.get(Calendar.MONTH)+1);
				new_prl.setApprove(0);
				new_prl.setPayroll_date(curDate.getTime());
				new_prl.setBldat(curDate.getTime());//BLDAT
				new_prl.setStaff_id(a_old_dealer_staff_id);
				new_prl.setBranch_id(a_con.getBranch_id());//BRANCH_ID
				new_prl.setContract_number(a_con.getContract_number());//CONTRACT_NUMBER
				new_prl.setPlan_amount(0);//PLAN_AMOUNT
				new_prl.setFact_amount(0);
				new_prl.setPosition_id((long) dealer_pos_id);
				new_prl.setMatnr_count(0);
				new_prl.setApprove(0);
				new_prl.setBukrs(a_con.getBukrs());
				new_prl.setDrcrk("S");
				new_prl.setWaers(prlSD.getWaers());
				new_prl.setDmbtr(prlSD.getDmbtr());				
				new_prl.setBonus_type_id(otmena);
				new_prl.setText45("Замена дилера");				
				new_prl.setParent_payroll_id(prlSD.getPayroll_id());
				payrollService.createNew(new_prl,a_userID,true,a_tcode,6);
				
				Payroll new_prl2 = new Payroll();
				new_prl2.setGjahr(curDate.get(Calendar.YEAR));
				new_prl2.setMonat(curDate.get(Calendar.MONTH)+1);
				new_prl2.setApprove(0);
				new_prl2.setPayroll_date(curDate.getTime());
				new_prl2.setBldat(curDate.getTime());//BLDAT
				new_prl2.setStaff_id(a_new_dealer_staff_id);
				new_prl2.setBranch_id(a_con.getBranch_id());//BRANCH_ID
				new_prl2.setContract_number(a_con.getContract_number());//CONTRACT_NUMBER
				new_prl2.setPlan_amount(0);//PLAN_AMOUNT
				new_prl2.setFact_amount(0);
				new_prl2.setPosition_id((long) dealer_pos_id);
				new_prl2.setMatnr_count(0);
				new_prl2.setApprove(0);
				new_prl2.setBukrs(a_con.getBukrs());
				new_prl2.setDrcrk("H");
				new_prl2.setWaers(prlSD.getWaers());
				new_prl2.setDmbtr(prlSD.getDmbtr());	
				new_prl2.setBonus_type_id(skidkaotdilera);
				new_prl2.setText45("Скидка от дилера клиенту");
				payrollService.createNew(new_prl2,a_userID,true,a_tcode,9);
			}
			

		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
		
	}
	
	public boolean universalContractCreatePayroll(List<Payroll> al_payroll,String a_tcode,Long a_userID) throws DAOException
	{
		try
		{
			Calendar curDate = Calendar.getInstance();
			for(Payroll wa_payroll:al_payroll)
			{
				wa_payroll.setGjahr(curDate.get(Calendar.YEAR));
				wa_payroll.setMonat(curDate.get(Calendar.MONTH)+1);
				wa_payroll.setPayroll_date(curDate.getTime());
				wa_payroll.setBldat(curDate.getTime());//BLDAT
				//wa_payroll.setStaff_id(a_new_dealer_staff_id);
				//wa_payroll.setBranch_id(a_con.getBranch_id());//BRANCH_ID
				//wa_payroll.setContract_number(a_con.getContract_number());//CONTRACT_NUMBER
				wa_payroll.setPlan_amount(0);//PLAN_AMOUNT
				wa_payroll.setFact_amount(0);
				//wa_payroll.setPosition_id((long) dealer_pos_id);
				wa_payroll.setMatnr_count(0);
				wa_payroll.setApprove(5);
				//wa_payroll.setBukrs(a_con.getBukrs());
				wa_payroll.setDrcrk("S");
				//wa_payroll.setWaers(prlSD.getWaers());
				//wa_payroll.setDmbtr(prlSD.getDmbtr());	
				//wa_payroll.setBonus_type_id(skidkaotdilera);
				wa_payroll.setText45(wa_payroll.getApproveName(""));
				payrollService.createNew(wa_payroll,a_userID,false,a_tcode,0);
			}
			return true;
		}catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	public boolean universalContractSavePayroll(Contract a_contract, String a_opertype,String a_tcode,Long a_userID) throws DAOException
	{
		try
		{
			if (a_contract.getMarkedType()!=null && a_contract.getMarkedType().equals(1L))
			{
				if (a_opertype.equals("SAVE"))
				{
					Calendar curDate = Calendar.getInstance();
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
					
					List<Payroll> l_payrollList = prlDao.findAll(" contract_number="+a_contract.getContract_number()+" and approve=5 and drcrk='S'");
					for(Payroll wa_payroll:l_payrollList)
					{
						Long awkey = null;
						Long cus_id=stfDao.find(wa_payroll.getStaff_id()).getCustomer_id();
						wa_payroll.setGjahr(curDate.get(Calendar.YEAR));
						wa_payroll.setMonat(curDate.get(Calendar.MONTH)+1);
						wa_payroll.setPayroll_date(curDate.getTime());
						wa_payroll.setBldat(curDate.getTime());//BLDAT
						wa_payroll.setApprove(0);
						wa_payroll.setDrcrk("S");
						
						
						ExchangeRate wa_er = new ExchangeRate();
					   	wa_er = l_er_map.get(wa_payroll.getWaers());
					   	if (wa_er==null || wa_er.getSecondary_currency()==null || wa_er.getSecondary_currency().length()==0)
					   	{
					   		throw new DAOException("Курс валют не определен."+" "+wa_payroll.getWaers());
					   	}
					   	else
					   	{
					   		awkey = financeServicePayroll.insertSalaryFi(wa_payroll.getBukrs(),a_userID,wa_payroll.getBranch_id(), wa_payroll.getWaers(), wa_payroll.getDmbtr(), 
					   				wa_er.getSc_value(), cus_id, wa_payroll.getDrcrk(),null,null,a_tcode,6);
					   		wa_payroll.setAwkey(awkey);
					   		prlDao.update(wa_payroll);
				    	}
					}
					return true;
					
				}
				else if (a_opertype.equals("DELETE"))
				{
					List<Payroll> l_payrollList = prlDao.findAll(" contract_number="+a_contract.getContract_number()+" and approve=5 and drcrk='S'");
					for(Payroll wa_payroll:l_payrollList)
					{
						prlDao.delete(wa_payroll.getPayroll_id());
					}
					return true;
				}
				else return false;
			}
			
			
			return true;
		}catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
}
