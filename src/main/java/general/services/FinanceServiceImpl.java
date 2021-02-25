package general.services;
   
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;









import general.GeneralUtil;
import general.dao.BkpfDao;
import general.dao.BranchDao;
import general.dao.BschlDao;
import general.dao.BsegDao;
import general.dao.BsikDao;
import general.dao.DAOException;
import general.dao.Fmglflext2Dao;
import general.dao.FmglflextDao;
import general.dao.HkontDao;
import general.dao.InvoiceDao;
import general.dao.PayrollDao;
import general.dao.TableIdLimitDao;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bschl;
import general.tables.Bseg;
import general.tables.Bsik;
import general.tables.Fmglflext;
import general.tables.Fmglflext2;
import general.tables.Hkont;
import general.tables.Invoice;
import general.tables.Payroll;
import general.tables.TableIdLimit;
import user.User;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import f4.BranchF4;

@Service("financeService")
public class FinanceServiceImpl implements FinanceService{
	@Autowired
    private BkpfDao bkpfDao;
	
	@Autowired
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
    private PayrollDao prlDao;
	
	
	@Autowired
	private InvoiceDao invoiceDao;
	
	@Autowired
	private ServiceService serviceServiceDao;
	
	@Autowired
	private FinanceServiceDms financeServiceDms;
	
	@Autowired
	private InvoiceService invoiceServiceDao;
	
	@Autowired
	private BranchDao branchDao;
	

	
	@Autowired
	private PrebkpfService prebkpfService;
	
	
	
	
	
	public Long createFACO01(Bkpf a_bkpf, List<Bseg> l_bseg,Bkpf a_bkpf_VZ) throws DAOException{
		try{			
			
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_bsik = new ArrayList<Bsik>();
			double p_dmbtr = 0;
			double p_wrbtr = 0;
			//Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis()); 		 
			if (a_bkpf.getWaers() == null || a_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (a_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (a_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			//if (a_bkpf.getBusiness_area_id()==null || a_bkpf.getBusiness_area_id()==0){
			//	throw new DAOException("Business area not chosen");
			//}
			if (a_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}
			 
			BeanUtils.copyProperties(a_bkpf, p_bkpf);
			p_bkpf.setDmbtr(0);
			p_bkpf.setWrbtr(0);
			
            for (Bseg wa_bseg : l_bseg) {
            	Bseg wa_bseg2 = new Bseg();
            	BeanUtils.copyProperties(wa_bseg, wa_bseg2);
            	pl_bseg.add(wa_bseg2);	 
            	
            	if (  p_bkpf.getWaers()!=null && p_bkpf.getWaers().equals("USD") 
               		&& wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("331") 
               		&& wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0 ) 
               	{	
               		p_bkpf.setDmbtr(p_bkpf.getDmbtr()+wa_bseg.getDmbtr()); 
               		p_dmbtr = p_dmbtr + wa_bseg.getDmbtr();
               	}
               	else if (p_bkpf.getWaers()!=null && !p_bkpf.getWaers().equals("USD") 
                   		&& wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("331") 
                   		&& wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0) 
               	{	
               		p_bkpf.setWrbtr(p_bkpf.getWrbtr()+wa_bseg.getWrbtr());
               		p_wrbtr = p_wrbtr + wa_bseg.getWrbtr();
               		p_bkpf.setDmbtr(p_bkpf.getDmbtr()+wa_bseg.getDmbtr()); 
               		p_dmbtr = p_dmbtr + wa_bseg.getDmbtr();
               	}
            	
            		Bsik wa_bsik = new Bsik();
            		BeanUtils.copyProperties(wa_bseg, wa_bsik);                	
                	pl_bsik.add(wa_bsik);
			}  
            
            if (a_bkpf_VZ!=null && a_bkpf_VZ.getBelnr()!=null && a_bkpf_VZ.getGjahr()>2004)
            {
            	p_bkpf.setAwkey(GeneralUtil.getPreparedAwkey(a_bkpf_VZ.getBelnr(),a_bkpf_VZ.getGjahr()));
            }
            
            //Inserting new payment fi doc
            check_empty_fields(p_bkpf, pl_bseg);   
            Long belnr = insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);

            
            if (a_bkpf_VZ!=null && a_bkpf_VZ.getBelnr()!=null && a_bkpf_VZ.getGjahr()>2004)
            {
            	bkpfDao.updateDynamicSingleBkpf(a_bkpf_VZ.getBelnr(), a_bkpf_VZ.getGjahr(), "closed = 1",a_bkpf_VZ.getBukrs());
            }
            
            return belnr;
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	public Long createFACI01(Bkpf a_bkpf, List<Bseg> l_bseg) throws DAOException{
		try{			
			
			double wa_dmbtr = 0;
			double wa_wrbtr = 0;
			
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_bsik = new ArrayList<Bsik>(); 
			double p_dmbtr = 0;
			double p_wrbtr = 0;
			//Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis()); 		 
			if (a_bkpf.getWaers() == null || a_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (a_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (a_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			//if (a_bkpf.getBusiness_area_id()==null || a_bkpf.getBusiness_area_id()==0){
			//	throw new DAOException("Business area not chosen");
			//}
			if (a_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}
			 
			 
			BeanUtils.copyProperties(a_bkpf, p_bkpf); 
			

			p_bkpf.setDmbtr(0);
			p_bkpf.setWrbtr(0);
            for (Bseg wa_bseg : l_bseg) {
            	Bseg wa_bseg2 = new Bseg(); 
            	BeanUtils.copyProperties(wa_bseg, wa_bseg2);
            	pl_bseg.add(wa_bseg2);	 
            	
            	if (wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("1210") && wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0)
            	{
            		wa_dmbtr = wa_dmbtr + wa_bseg.getDmbtr();
                	wa_wrbtr = wa_wrbtr + wa_bseg.getWrbtr();
            	}
            	
            	if (  p_bkpf.getWaers()!=null && p_bkpf.getWaers().equals("USD") 
            		&& wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("12") 
            		&& wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0 ) 
            	{	
            		p_bkpf.setDmbtr(p_bkpf.getDmbtr()+wa_bseg.getDmbtr()); 
            		p_dmbtr = p_dmbtr + wa_bseg.getDmbtr();
            	}
            	else if (p_bkpf.getWaers()!=null && !p_bkpf.getWaers().equals("USD") 
                		&& wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("12") 
                		&& wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0) 
            	{	            		
            		p_bkpf.setWrbtr(p_bkpf.getWrbtr()+wa_bseg.getWrbtr());
               		p_wrbtr = p_wrbtr + wa_bseg.getWrbtr();
               		p_bkpf.setDmbtr(p_bkpf.getDmbtr()+wa_bseg.getDmbtr()); 
               		p_dmbtr = p_dmbtr + wa_bseg.getDmbtr();
            	}
            	
            	Bsik wa_bsik = new Bsik(); 
            	BeanUtils.copyProperties(wa_bseg, wa_bsik);                   	
                pl_bsik.add(wa_bsik);
            	
			}  
            if (p_bkpf.getBlart().equals("CP") && p_bkpf.getContract_number()!=null )
            {
            	//System.out.println("Contract payment");
            	throw new DAOException("You can't pay for contracts in this transaction");
            	
            }
            
            
            check_empty_fields(p_bkpf, pl_bseg);   
            Long belnr = insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);
            
            
            //Updating Account Recievable
            if (p_bkpf.getAwkey()!=null)
            {
            	Bkpf p_bkpf_account_rec = new Bkpf();
                Long wa_belnr =  Long.parseLong(String.valueOf(p_bkpf.getAwkey()).substring(0, 10));
            	int wa_gjahr = Integer.parseInt(String.valueOf(p_bkpf.getAwkey()).substring(10, 14));
            	p_bkpf_account_rec = bkpfDao.findOriginalSingleBkpf(wa_belnr,wa_gjahr,p_bkpf.getBukrs());
            	if (p_bkpf_account_rec!=null && p_bkpf_account_rec.getBlart()!=null && p_bkpf_account_rec.getBlart().equals("GS"))
            	{
            		if (p_bkpf.getWaers().equals("USD"))
                    {
            			if (p_bkpf_account_rec.getDmbtr()<p_bkpf_account_rec.getDmbtr_paid()+p_dmbtr)
            			{
            				throw new DAOException("Вы переплачиваете сервис. Оплата не произведена.");
            			}
                    }
            		else
            		{
            			if (p_bkpf_account_rec.getWrbtr()<p_bkpf_account_rec.getWrbtr_paid()+p_wrbtr)
            			{
            				throw new DAOException("Вы переплачиваете сервис. Оплата не произведена.");
            			}
            		}
            		if (p_bkpf.getWaers().equals("USD"))
                    {
                    	p_bkpf_account_rec.setDmbtr_paid(p_bkpf_account_rec.getDmbtr_paid()+p_dmbtr);
                    	updateFiDoc(p_bkpf_account_rec, null);
                    	serviceServiceDao.paymentStateUpdate(p_bkpf.getAwkey(), p_bkpf.getDmbtr(),p_bkpf.getBukrs());
                    	invoiceServiceDao.writeoffFromService(p_bkpf.getAwkey(),p_bkpf.getUsnam(),p_bkpf.getBukrs());
                    }
                    else
                    {
                    	p_bkpf_account_rec.setDmbtr_paid(p_bkpf_account_rec.getDmbtr_paid()+p_dmbtr);
                    	p_bkpf_account_rec.setWrbtr_paid(p_bkpf_account_rec.getWrbtr_paid()+p_wrbtr);
                    	updateFiDoc(p_bkpf_account_rec, null);
                    	serviceServiceDao.paymentStateUpdate(p_bkpf.getAwkey(), p_bkpf.getWrbtr(),p_bkpf.getBukrs());
                    	invoiceServiceDao.writeoffFromService(p_bkpf.getAwkey(),p_bkpf.getUsnam(),p_bkpf.getBukrs());
                    }
            	}
            	else
            	{
            		throw new DAOException("ПКО не разрешается по данному документу:"+p_bkpf.getAwkey());
            	}
            }
            
        	
            
            
            
            
            /*
            Long wa_belnr =  Long.parseLong(String.valueOf(p_bkpf.getAwkey()).substring(0, 10));
        	int wa_gjahr = Integer.parseInt(String.valueOf(p_bkpf.getAwkey()).substring(10, 14));
        	String dynamicWhereClause = "";        	
            if (p_bkpf.getWaers().equals("USD"))
            {
            	dynamicWhereClause = dynamicWhereClause + " dmbtr_paid = dmbtr_paid +" + p_dmbtr;  
            }
            else
            {
            	dynamicWhereClause = dynamicWhereClause + " wrbtr_paid = wrbtr_paid +" + p_wrbtr +", dmbtr_paid = dmbtr_paid +" + p_dmbtr;           	
            }
            
            if (lastPayment)
            {
            	dynamicWhereClause = dynamicWhereClause +", closed = 1";
            }
            
            if (bkpfDao.updateDynamicSingleBkpf(wa_belnr, wa_gjahr, dynamicWhereClause)!=1)
        	{
        		throw new DAOException("Account Recievable not updated");
        	}*/
            
            return belnr;
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	//nachislenie i oplata
	public Long createAccountReceivableDocsRA(Bkpf a_bkpf, List<Bseg> l_bseg, Branch a_branch, String a_hkont) throws DAOException{
		try{			
			//Делаем начисление
            Long wa_belnr;
            a_bkpf.setClosed(1);
            wa_belnr = createAccountReceivableDocs(a_bkpf, l_bseg, a_branch);
            
            //Оплачиваем сразу
            Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_bsik = new ArrayList<Bsik>();

			double p_dmbtr = 0;
			double p_wrbtr = 0;
			
			 
			BeanUtils.copyProperties(a_bkpf, p_bkpf);             
			p_bkpf.setBlart("DP");
			p_bkpf.setAwkey(GeneralUtil.getPreparedAwkey(wa_belnr, a_bkpf.getGjahr()));
			p_bkpf.setAwtyp(1);
            for (Bseg wa_bseg : l_bseg) {
            	Bseg wa_bseg2 = new Bseg(); 
            	wa_bseg2.setGjahr(wa_bseg.getGjahr());
            	wa_bseg2.setBukrs(wa_bseg.getBukrs());
            	wa_bseg2.setBuzei(wa_bseg.getBuzei());
            	wa_bseg2.setHkont(wa_bseg.getHkont());
            	wa_bseg2.setDmbtr(wa_bseg.getDmbtr());
            	wa_bseg2.setWrbtr(wa_bseg.getWrbtr());
            	wa_bseg2.setLifnr(wa_bseg.getLifnr());
            	wa_bseg2.setLifnr(wa_bseg.getLifnr());
            	wa_bseg2.setMatnr(wa_bseg.getMatnr());
            	wa_bseg2.setWerks(wa_bseg.getWerks());
            	wa_bseg2.setMenge(wa_bseg.getMenge());
            	wa_bseg2.setSgtxt(wa_bseg.getSgtxt());
            	pl_bseg.add(wa_bseg2);	 
            	
            	if (  p_bkpf.getWaers()!=null && p_bkpf.getWaers().equals("USD") 
               		&& wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("1210") 
               		&& wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0 ) 
               	{	
               		p_bkpf.setDmbtr(p_bkpf.getDmbtr()+wa_bseg.getDmbtr()); 
               		p_dmbtr = p_dmbtr + wa_bseg.getDmbtr();
               	}
               	else if (p_bkpf.getWaers()!=null && !p_bkpf.getWaers().equals("USD") 
                   		&& wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("1210") 
                   		&& wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0) 
               	{	
               		p_bkpf.setWrbtr(p_bkpf.getWrbtr()+wa_bseg.getWrbtr());
               		p_wrbtr = p_wrbtr + wa_bseg.getWrbtr();
               		p_dmbtr = p_dmbtr + wa_bseg.getDmbtr();
               		p_bkpf.setDmbtr(p_bkpf.getWrbtr() / p_bkpf.getKursf());
               	}
            	
            	if (wa_bseg.getHkont().startsWith("1210"))
            	{
            		wa_bseg2.setBschl("15");
            		wa_bseg2.setShkzg("H");
            	}
            	else
            	{
            		wa_bseg2.setBschl("40");
            		wa_bseg2.setShkzg("S");
            		wa_bseg2.setHkont(a_hkont);
            	}
            	
            	
            	
            	
            		Bsik wa_bsik = new Bsik();
            		BeanUtils.copyProperties(wa_bseg2, wa_bsik);
                	pl_bsik.add(wa_bsik);
			}  
            
            //Inserting new payment fi doc
            check_empty_fields(p_bkpf, pl_bseg);   
            insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);
            
            //Updating Account Payable
        	int wa_gjahr = a_bkpf.getGjahr();
        	String dynamicWhereClause = "";        	
            if (p_bkpf.getWaers().equals("USD"))
            {
            	dynamicWhereClause = dynamicWhereClause + " dmbtr_paid = dmbtr_paid +" + p_dmbtr;
            }
            else
            {
            	dynamicWhereClause = dynamicWhereClause + " wrbtr_paid = wrbtr_paid +" + p_wrbtr +", dmbtr_paid = dmbtr_paid +" + p_dmbtr;           	
            }
            if (bkpfDao.updateDynamicSingleBkpf(wa_belnr, wa_gjahr, dynamicWhereClause,a_bkpf.getBukrs())!=1)
        	{
        		throw new DAOException("Account Recievable not updated");
        	}
            return p_bkpf.getBelnr();
            
            
			
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	//tolko nachislenie
	public Long createAccountReceivableDocs(Bkpf a_bkpf, List<Bseg> l_bseg, Branch a_branch) throws DAOException{
		try{			
			
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_bsik = new ArrayList<Bsik>();
			//Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis()); 		 
			if (a_bkpf.getWaers() == null || a_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (a_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (a_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			//if (a_bkpf.getBusiness_area_id()==null || a_bkpf.getBusiness_area_id()==0){
			//	throw new DAOException("Business area not chosen");
			//}
			if (a_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}
			 
			BeanUtils.copyProperties(a_bkpf, p_bkpf);

            for (Bseg wa_bseg : l_bseg) {
            	Bseg wa_bseg2 = new Bseg();
            	BeanUtils.copyProperties(wa_bseg, wa_bseg2);
            	pl_bseg.add(wa_bseg2);	
            	
            	if (  p_bkpf.getWaers()!=null && p_bkpf.getWaers().equals("USD") 
                	&& wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("1210") 
                	&& wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0 ) 
                {	
                	if (wa_bseg.getShkzg().equals("S"))
                	{
                    	p_bkpf.setDmbtr(p_bkpf.getDmbtr()+wa_bseg.getDmbtr());                		
                	}
                	else
                	{
                		p_bkpf.setDmbtr(p_bkpf.getDmbtr()-wa_bseg.getDmbtr());
                	}
                }
                else if (p_bkpf.getWaers()!=null && !p_bkpf.getWaers().equals("USD") 
                   		&& wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("1210") 
                   		&& wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0) 
                {	
                	if (wa_bseg.getShkzg().equals("S"))
                	{
                		p_bkpf.setWrbtr(p_bkpf.getWrbtr()+wa_bseg.getWrbtr());
                    	p_bkpf.setDmbtr(p_bkpf.getDmbtr()+wa_bseg.getDmbtr());               		
                	}
                	else
                	{
                		p_bkpf.setWrbtr(p_bkpf.getWrbtr()-wa_bseg.getWrbtr());
                    	p_bkpf.setDmbtr(p_bkpf.getDmbtr()-wa_bseg.getDmbtr());
                	}
                	
                }
            	
            	
            	Bsik wa_bsik = new Bsik();
            	BeanUtils.copyProperties(wa_bseg2, wa_bsik);		
            	pl_bsik.add(wa_bsik);
            	
            	if (wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0)
            	{
            		p_bkpf.setCustomer_id(wa_bseg.getLifnr());
            	}
            	 
			}  
            
            
            
            check_empty_fields(p_bkpf, pl_bseg); 
            return insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);

            
			
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	//nachislenie i oplata
	public Long createAccountPayableDocsPayRA(Bkpf a_bkpf, List<Bseg> l_bseg, Branch a_branch, Hkont a_hkont) throws DAOException{
		try{			
			//Делаем начисление
            Long wa_belnr;
            a_bkpf.setClosed(1);
            wa_belnr = createAccountPayableDocs(a_bkpf, l_bseg);
            
            //Оплачиваем сразу
            Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_bsik = new ArrayList<Bsik>();

			double p_dmbtr = 0;
			double p_wrbtr = 0;
			
			 
			BeanUtils.copyProperties(a_bkpf, p_bkpf);             
			p_bkpf.setBlart("KP");
			p_bkpf.setAwkey(GeneralUtil.getPreparedAwkey(wa_belnr, a_bkpf.getGjahr()));
			p_bkpf.setAwtyp(2);
            for (Bseg wa_bseg : l_bseg) {
            	Bseg wa_bseg2 = new Bseg(); 
            	wa_bseg2.setGjahr(wa_bseg.getGjahr());
            	wa_bseg2.setBukrs(wa_bseg.getBukrs());
            	wa_bseg2.setBuzei(wa_bseg.getBuzei());
            	wa_bseg2.setHkont(wa_bseg.getHkont());
            	wa_bseg2.setDmbtr(wa_bseg.getDmbtr());
            	wa_bseg2.setWrbtr(wa_bseg.getWrbtr());
            	wa_bseg2.setLifnr(wa_bseg.getLifnr());
            	wa_bseg2.setLifnr(wa_bseg.getLifnr());
            	wa_bseg2.setMatnr(wa_bseg.getMatnr());
            	wa_bseg2.setWerks(wa_bseg.getWerks());
            	wa_bseg2.setMenge(wa_bseg.getMenge());
            	wa_bseg2.setSgtxt(wa_bseg.getSgtxt());
            	pl_bseg.add(wa_bseg2);	 
            	
            	if (  p_bkpf.getWaers()!=null && p_bkpf.getWaers().equals("USD") 
               		&& wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("331") 
               		&& wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0 ) 
               	{	
               		p_bkpf.setDmbtr(p_bkpf.getDmbtr()+wa_bseg.getDmbtr()); 
               		p_dmbtr = p_dmbtr + wa_bseg.getDmbtr();
               	}
               	else if (p_bkpf.getWaers()!=null && !p_bkpf.getWaers().equals("USD") 
                   		&& wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("331") 
                   		&& wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0) 
               	{	
               		p_bkpf.setWrbtr(p_bkpf.getWrbtr()+wa_bseg.getWrbtr());
               		p_wrbtr = p_wrbtr + wa_bseg.getWrbtr();
               		p_dmbtr = p_dmbtr + wa_bseg.getDmbtr();
               		p_bkpf.setDmbtr(p_bkpf.getWrbtr() / p_bkpf.getKursf());
               	}
            	
            	if (wa_bseg.getHkont().startsWith("331"))
            	{
            		wa_bseg2.setBschl("25");
            		wa_bseg2.setShkzg("S");
            	}
            	else
            	{
            		wa_bseg2.setBschl("50");
            		wa_bseg2.setShkzg("H");
            		wa_bseg2.setHkont(a_hkont.getHkont());
            	}
            	
            	
            	
            	
            		Bsik wa_bsik = new Bsik();
            		BeanUtils.copyProperties(wa_bseg2, wa_bsik);
                	pl_bsik.add(wa_bsik);
			}  
            
            //Inserting new payment fi doc
            check_empty_fields(p_bkpf, pl_bseg);   
            insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);
            
            //Updating Account Payable
        	int wa_gjahr = a_bkpf.getGjahr();
        	String dynamicWhereClause = "";        	
            if (p_bkpf.getWaers().equals("USD"))
            {
            	dynamicWhereClause = dynamicWhereClause + " dmbtr_paid = dmbtr_paid +" + p_dmbtr;
            }
            else
            {
            	dynamicWhereClause = dynamicWhereClause + " wrbtr_paid = wrbtr_paid +" + p_wrbtr +", dmbtr_paid = dmbtr_paid +" + p_dmbtr;           	
            }
            if (bkpfDao.updateDynamicSingleBkpf(wa_belnr, wa_gjahr, dynamicWhereClause,a_bkpf.getBukrs())!=1)
        	{
        		throw new DAOException("Account Recievable not updated");
        	}
            return p_bkpf.getBelnr();
            
            
			
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	//tolko nachislenie
	public Long createAccountPayableDocs(Bkpf a_bkpf, List<Bseg> l_bseg) throws DAOException{
		try{			
			
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_bsik = new ArrayList<Bsik>(); 
			//Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis()); 		 
			if (a_bkpf.getWaers() == null || a_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (a_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (a_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			//if (a_bkpf.getBusiness_area_id()==null || a_bkpf.getBusiness_area_id()==0){
			//	throw new DAOException("Business area not chosen");
			//}
			if (a_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}
			 
			BeanUtils.copyProperties(a_bkpf, p_bkpf); 

            for (Bseg wa_bseg : l_bseg) {
            	Bseg wa_bseg2 = new Bseg(); 
            	BeanUtils.copyProperties(wa_bseg, wa_bseg2);
            	pl_bseg.add(wa_bseg2);	
            	
            	if (  p_bkpf.getWaers()!=null && p_bkpf.getWaers().equals("USD") 
               		&& wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("331") 
               		&& wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0 ) 
               	{	
               		p_bkpf.setDmbtr(GeneralUtil.round(p_bkpf.getDmbtr()+wa_bseg.getDmbtr(),2)); 
               	}
               	else if (p_bkpf.getWaers()!=null && !p_bkpf.getWaers().equals("USD") 
                	&& wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("331") 
                	&& wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0) 
               	{	
               		p_bkpf.setWrbtr(GeneralUtil.round(p_bkpf.getWrbtr()+wa_bseg.getWrbtr(),2));
               		p_bkpf.setDmbtr(GeneralUtil.round(p_bkpf.getWrbtr()/p_bkpf.getKursf(),2)); 
               	}
            	
            	
            	Bsik wa_bsik = new Bsik();
            	BeanUtils.copyProperties(wa_bseg2, wa_bsik); 				
            	pl_bsik.add(wa_bsik);
            	
            	if (wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0)
            	{
            		p_bkpf.setCustomer_id(wa_bseg.getLifnr());
            	}
            	 
			}  
            
            
            Long wa_belnr;
            check_empty_fields(p_bkpf, pl_bseg);
            wa_belnr = insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);
            
            if (p_bkpf.getInvoice_id()!=null && p_bkpf.getInvoice_id()>0)
            {
            	Invoice wa_invoice = new Invoice();
            	wa_invoice = invoiceDao.find(p_bkpf.getInvoice_id());
            	wa_invoice.setAwkey(Long.parseLong(String.valueOf(wa_belnr)+String.valueOf(p_bkpf.getGjahr())));
            	invoiceDao.update(wa_invoice);
            }
            return wa_belnr;
            
            
			
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	public Long createFAICF(Bkpf a_bkpf, List<Bseg> l_bseg) throws DAOException{
		try{			
			
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_bsik = new ArrayList<Bsik>(); 
			//Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis()); 		
			if (a_bkpf.getWaers() == null || a_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (a_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (a_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			//if (a_bkpf.getBusiness_area_id()==null || a_bkpf.getBusiness_area_id()==0){
			//	throw new DAOException("Business area not chosen");
			//}
			if (a_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}
			 
			 
			 
			p_bkpf.setBukrs(a_bkpf.getBukrs());
			p_bkpf.setGjahr(a_bkpf.getGjahr());
			p_bkpf.setBlart(a_bkpf.getBlart());
			p_bkpf.setBudat(a_bkpf.getBudat());
			p_bkpf.setBldat(a_bkpf.getBldat());
            p_bkpf.setMonat(a_bkpf.getMonat());
            p_bkpf.setCpudt(a_bkpf.getCpudt());
            p_bkpf.setUsnam(a_bkpf.getUsnam());
            p_bkpf.setTcode(a_bkpf.getTcode());
            p_bkpf.setWaers(a_bkpf.getWaers());
            p_bkpf.setKursf(a_bkpf.getKursf()); 
            p_bkpf.setBrnch(a_bkpf.getBrnch());
            p_bkpf.setBusiness_area_id(a_bkpf.getBusiness_area_id());
            p_bkpf.setCustomer_id(a_bkpf.getCustomer_id());
            p_bkpf.setContract_number(a_bkpf.getContract_number());
            p_bkpf.setAwtyp(a_bkpf.getAwtyp());
            
            p_bkpf.setAwkey(a_bkpf.getAwkey());
            p_bkpf.setAwkey2(a_bkpf.getAwkey2());
            p_bkpf.setBktxt(a_bkpf.getBktxt());
            
            p_bkpf.setDmbtr(0);
            p_bkpf.setDmbtr_paid(0);
            p_bkpf.setWrbtr(0);
            p_bkpf.setWrbtr_paid(0);
            
            for (Bseg wa_bseg : l_bseg) {
            	Bseg wa_bseg2 = new Bseg(); 
            	wa_bseg2.setGjahr(wa_bseg.getGjahr());
            	wa_bseg2.setBukrs(wa_bseg.getBukrs());
            	wa_bseg2.setBuzei(wa_bseg.getBuzei());
            	wa_bseg2.setBschl(wa_bseg.getBschl());
            	wa_bseg2.setHkont(wa_bseg.getHkont());
            	wa_bseg2.setShkzg(wa_bseg.getShkzg());
            	wa_bseg2.setDmbtr(wa_bseg.getDmbtr());
            	wa_bseg2.setWrbtr(wa_bseg.getWrbtr());
            	wa_bseg2.setLifnr(wa_bseg.getLifnr());
            	wa_bseg2.setLifnr(wa_bseg.getLifnr());
            	wa_bseg2.setMatnr(wa_bseg.getMatnr());
            	wa_bseg2.setWerks(wa_bseg.getWerks());
            	wa_bseg2.setMenge(wa_bseg.getMenge());
            	wa_bseg2.setSgtxt(wa_bseg.getSgtxt()); 
            	pl_bseg.add(wa_bseg2);
            	
            	
            	if(wa_bseg.getShkzg().equals("H"))
            	{
            		p_bkpf.setDmbtr(p_bkpf.getDmbtr()+wa_bseg2.getDmbtr());
            		p_bkpf.setWrbtr(p_bkpf.getWrbtr()+wa_bseg2.getWrbtr());
            	}
            	
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
            	pl_bsik.add(wa_bsik);
			}
            

			/*if (p_bkpf.getBlart().equals("HA"))
			{
				Payroll wa_payroll = new Payroll();
				wa_payroll.setStaff_id(a_staff_id);
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
			    wa_payroll.setDrcrk("H");
			    
			    wa_payroll.setGjahr(p_bkpf.getGjahr());
			    wa_payroll.setMonat(p_bkpf.getMonat());
			    wa_payroll.setPayroll_date(curDate.getTime());
			    wa_payroll.setText45("Avans");
			    prlDao.create(wa_payroll);
			    p_bkpf.setPayroll_id(wa_payroll.getPayroll_id());
			    updateFiDoc(p_bkpf, null);
			}*/
            
            check_empty_fields(p_bkpf, pl_bseg); 
            return insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);

            
			
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	public Long createFAES(Bkpf a_bkpf, List<Bseg> l_bseg, Long a_staff_id) throws DAOException{
		try{			
			
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_bsik = new ArrayList<Bsik>(); 
			//Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis()); 		
			if (a_bkpf.getWaers() == null || a_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (a_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (a_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			//if (a_bkpf.getBusiness_area_id()==null || a_bkpf.getBusiness_area_id()==0){
			//	throw new DAOException("Business area not chosen");
			//}
			if (a_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}
			 
			 
			 
			p_bkpf.setBukrs(a_bkpf.getBukrs());
			p_bkpf.setGjahr(a_bkpf.getGjahr());
			p_bkpf.setBlart(a_bkpf.getBlart());
			p_bkpf.setBudat(a_bkpf.getBudat());
			p_bkpf.setBldat(a_bkpf.getBldat());
            p_bkpf.setMonat(a_bkpf.getMonat());
            p_bkpf.setCpudt(a_bkpf.getCpudt());
            p_bkpf.setUsnam(a_bkpf.getUsnam());
            p_bkpf.setTcode(a_bkpf.getTcode());
            p_bkpf.setWaers(a_bkpf.getWaers());
            p_bkpf.setKursf(a_bkpf.getKursf()); 
            p_bkpf.setBrnch(a_bkpf.getBrnch());
            p_bkpf.setBusiness_area_id(a_bkpf.getBusiness_area_id());
            p_bkpf.setCustomer_id(a_bkpf.getCustomer_id());
            p_bkpf.setContract_number(a_bkpf.getContract_number());
            p_bkpf.setAwtyp(a_bkpf.getAwtyp());
            p_bkpf.setDmbtr(a_bkpf.getDmbtr());
            p_bkpf.setDmbtr_paid(a_bkpf.getDmbtr_paid());
            p_bkpf.setWrbtr(a_bkpf.getWrbtr());
            p_bkpf.setWrbtr_paid(a_bkpf.getWrbtr_paid());
            p_bkpf.setAwkey(a_bkpf.getAwkey());
            p_bkpf.setAwkey2(a_bkpf.getAwkey2());
            for (Bseg wa_bseg : l_bseg) {
            	Bseg wa_bseg2 = new Bseg(); 
            	wa_bseg2.setGjahr(wa_bseg.getGjahr());
            	wa_bseg2.setBukrs(wa_bseg.getBukrs());
            	wa_bseg2.setBuzei(wa_bseg.getBuzei());
            	wa_bseg2.setBschl(wa_bseg.getBschl());
            	wa_bseg2.setHkont(wa_bseg.getHkont());
            	wa_bseg2.setShkzg(wa_bseg.getShkzg());
            	wa_bseg2.setDmbtr(wa_bseg.getDmbtr());
            	wa_bseg2.setWrbtr(wa_bseg.getWrbtr());
            	wa_bseg2.setLifnr(wa_bseg.getLifnr());
            	wa_bseg2.setLifnr(wa_bseg.getLifnr());
            	wa_bseg2.setMatnr(wa_bseg.getMatnr());
            	wa_bseg2.setWerks(wa_bseg.getWerks());
            	wa_bseg2.setMenge(wa_bseg.getMenge());
            	wa_bseg2.setSgtxt(wa_bseg.getSgtxt()); 
            	pl_bseg.add(wa_bseg2);	
            	
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
            	pl_bsik.add(wa_bsik);
			}
            
            
            if (a_staff_id ==null)
            {
            	throw new DAOException("Customer or staff is null");
            }
			
            Payroll wa_payroll = new Payroll();
			wa_payroll.setStaff_id(a_staff_id);
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
			wa_payroll.setPayroll_date(p_bkpf.getCpudt());
			
			if (p_bkpf.getBlart().equals("AS")||p_bkpf.getBlart().equals("SS"))
			{
				wa_payroll.setText45("Зарплата");
			}
			else if(p_bkpf.getBlart().equals("BS"))
			{
				wa_payroll.setText45("Бонус");
			}
			else if(p_bkpf.getBlart().equals("PS"))
			{
				wa_payroll.setText45("Премия");
			}
			
			
			prlDao.create(wa_payroll);
			p_bkpf.setPayroll_id(wa_payroll.getPayroll_id());
			updateFiDoc(p_bkpf, null);
			
            
            check_empty_fields(p_bkpf, pl_bseg); 
            return insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);

            
			
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	public void cashPaymentSingleEmployee(Bkpf a_bkpf) throws DAOException
	{
		try
		{
			
			if (a_bkpf.getBukrs()==null)
			{
				throw new DAOException("Company not selected");
			}
			if (a_bkpf.getGjahr()==0)
			{
				throw new DAOException("Year not selected");
			}
			if (a_bkpf.getCustomer_id()==0)
			{
				throw new DAOException("Employee not selected");
			}
			
			//Пойск начисленных зп
			//_ap stands for account payable
			String dynamicWhereClause = "";
			List<Bkpf> l_bkpf_ap = new ArrayList<Bkpf>();
			dynamicWhereClause = dynamicWhereClause + "bukrs = "+"'"+a_bkpf.getBukrs()+"'";
			dynamicWhereClause = dynamicWhereClause + " and customer_id = "+a_bkpf.getCustomer_id();
			dynamicWhereClause = dynamicWhereClause + " and amount > 0";
			l_bkpf_ap = bkpfDao.dynamicFindBkpf(dynamicWhereClause);
			System.out.println(l_bkpf_ap);
			
		}
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	public Long insertNewFiDoc(Bkpf a_bkpf, List<Bseg> l_bseg, List<Bsik> l_bsik) throws DAOException{
		try{
			TableIdLimit p_tableIdLimit = new TableIdLimit();
			Calendar curDate = Calendar.getInstance();
			
			int curYear = curDate.get(Calendar.YEAR);

			Calendar bldatCal = Calendar.getInstance();
			bldatCal.setTime(a_bkpf.getBldat());
			int bldatYear = bldatCal.get(Calendar.YEAR);

			if (bldatYear>curYear || curYear!=2021)
			{
				throw new DAOException("Contact Administrator, Bldat Date problem");
			}

			Calendar budatCal = Calendar.getInstance();
			budatCal.setTime(a_bkpf.getBudat());
			int budatYear = budatCal.get(Calendar.YEAR);

			if (budatYear>curYear)
			{
				throw new DAOException("Contact Administrator, Budat Date problem");
			}
			
			
//			if (tableIdLimitDao.countByIds("bkpf","blart",a_bkpf.getBlart(),"belnr",a_bkpf.getGjahr())>0){
//				p_tableIdLimit = tableIdLimitDao.findByIds("bkpf","blart",a_bkpf.getBlart(),"belnr",a_bkpf.getGjahr());
//			}
//			else{ 
//				throw new DAOException("No id limit for this type of document in table (table_id_limit) "+a_bkpf.getBlart());
//			}
			
			//long awkey = p_tableIdLimit.getCurrent_id() * 10000; 
			//awkey = awkey + a_bkpf.getGjahr(); 
//			if (p_tableIdLimit.getCurrent_id()==p_tableIdLimit.getTo_id()){
//				throw new DAOException("Id limit exceed (table_id_limit)");
//			}
//			p_tableIdLimit.setCurrent_id(p_tableIdLimit.getCurrent_id()+ 1); 
//			tableIdLimitDao.update(p_tableIdLimit);
			
			Long tempBelnr = bkpfDao.getNextValueBkpf(a_bkpf.getBlart());
			if (tempBelnr==null){
				throw new DAOException("No id limit for this type of document in table (table_id_limit) "+a_bkpf.getBlart());
			}
			else {
				a_bkpf.setBelnr(tempBelnr);
			}

			if (l_bseg==null || l_bseg.size()==0){
				throw new DAOException("No bseg data");
			}
			
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
			if (l_bseg==null || l_bseg.size()==0)
			{
				System.out.println("Finance detail empty ask administrator");
			}
			
			
			double dmbtr_h = 0;
			double dmbtr_s = 0;
			double wrbtr_h = 0;
			double wrbtr_s = 0;
			for (Bseg wa_bseg : l_bseg) { 						
				
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

			if (!a_bkpf.getBlart().equals("GS")){	
				if ((a_bkpf.getWaers().equals("USD")) && (dmbtr_h != dmbtr_s ||  dmbtr_h!=a_bkpf.getDmbtr())){
					throw new DAOException("Debet and credit amounts are not equal in local currency with Header");
				}
				if ((!a_bkpf.getWaers().equals("USD")) && (wrbtr_h != wrbtr_s ||  wrbtr_h!=a_bkpf.getWrbtr())){
					throw new DAOException("Debet and credit amounts are not equal in foriegn currency with Header");
				}
			}
			//Creating process 
			a_bkpf.setCpudt(Calendar.getInstance().getTime());
			bkpfDao.create(a_bkpf);
			
			

			for (Bseg wa_bseg : l_bseg) { 		
				
				
							
				
				if (wa_bseg.getSgtxt()!=null && wa_bseg.getSgtxt().length()>50)
				{
					wa_bseg.setSgtxt(wa_bseg.getSgtxt().substring(0,50));
				}
				Fmglflext p_fmglflext = new Fmglflext();
				Fmglflext2 p_fmglflext2 = new Fmglflext2(); 
				//if (fmglflextDao.countByIds(a_bkpf.getBukrs(), a_bkpf.getGjahr(), wa_bseg.getHkont(), wa_bseg.getShkzg())>0){
				//	p_fmglflext = fmglflextDao.findByIds(a_bkpf.getBukrs(), a_bkpf.getGjahr(), wa_bseg.getHkont(), wa_bseg.getShkzg());
				//}
				/*p_fmglflext = fmglflextDao.findByIds(a_bkpf.getBukrs(), a_bkpf.getGjahr(), wa_bseg.getHkont(), wa_bseg.getShkzg());
				if (p_fmglflext==null || p_fmglflext.getBukrs()==null || p_fmglflext.getGjahr()==0 || p_fmglflext.getHkont()==null || p_fmglflext.getDrcrk()==null)
				{
					p_fmglflext.setBukrs(a_bkpf.getBukrs());
					p_fmglflext.setGjahr(a_bkpf.getGjahr());
					p_fmglflext.setHkont(wa_bseg.getHkont());
					p_fmglflext.setDrcrk(wa_bseg.getShkzg());
				} */
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
					p_fmglflext = fmglflextDao.create(p_fmglflext); 
				}
				//if (fmglflext2Dao.countByIds(a_bkpf.getBukrs(), a_bkpf.getGjahr(), wa_bseg.getHkont(), wa_bseg.getShkzg(), a_bkpf.getBrnch())>0){
				//	p_fmglflext2 = fmglflext2Dao.findByIds(a_bkpf.getBukrs(), a_bkpf.getGjahr(), wa_bseg.getHkont(), wa_bseg.getShkzg(), a_bkpf.getBrnch());
				//}
				/*p_fmglflext2 = fmglflext2Dao.findByIds(a_bkpf.getBukrs(), a_bkpf.getGjahr(), wa_bseg.getHkont(), wa_bseg.getShkzg(), a_bkpf.getBrnch());
				if (p_fmglflext2==null || p_fmglflext2.getBukrs()==null || p_fmglflext2.getGjahr()==0 || p_fmglflext2.getHkont()==null || p_fmglflext2.getDrcrk()==null || p_fmglflext2.getBranch_id()==0)
				{
					p_fmglflext2.setBukrs(a_bkpf.getBukrs());
					p_fmglflext2.setGjahr(a_bkpf.getGjahr());
					p_fmglflext2.setHkont(wa_bseg.getHkont());
					p_fmglflext2.setDrcrk(wa_bseg.getShkzg());
					p_fmglflext2.setBranch_id(a_bkpf.getBrnch());
					
				}*/
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
					p_fmglflext2 = fmglflext2Dao.create(p_fmglflext2); 
				}

				double summa = 0;  
				Hkont wa_hkont = new Hkont(); 
				//if (hkontDao.countByIds(wa_bseg.getBukrs(), wa_bseg.getHkont())>0){
				//	wa_hkont = hkontDao.findByIds(wa_bseg.getBukrs(), wa_bseg.getHkont());
				//}
				/*wa_hkont = hkontDao.findByIds(wa_bseg.getBukrs(), wa_bseg.getHkont());
				if (wa_hkont == null || wa_hkont.getBukrs()==null || wa_hkont.getHkont()==null)
				{
					throw new DAOException("No such general ledger account");
				} 
				*/
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
				
				if (a_bkpf.getMonat()>curDate.get(Calendar.MONTH)+1)
				{
					throw new DAOException("Финанс. ошибка. Обратитесь IT отделу.");
				}
				bsegDao.create(wa_bseg); 
				fmglflextDao.update(p_fmglflext); 
				fmglflext2Dao.update(p_fmglflext2); 
			}
			
			
			if (l_bsik.size()>0){
				for (Bsik wa_bsik : l_bsik) { 
//					bsikDao.create(wa_bsik);
				}
			}

			
			return a_bkpf.getBelnr();
			
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	public void updateFiDoc(Bkpf a_bkpf, List<Bseg> l_bseg) throws DAOException{
		try{
			if (a_bkpf!=null)
			{
				if (a_bkpf.getWaers().equals("USD")&&a_bkpf.getDmbtr()== a_bkpf.getDmbtr_paid())
    			{
					a_bkpf.setClosed(1);
    			}
            	if (!a_bkpf.getWaers().equals("USD")&&a_bkpf.getWrbtr()== a_bkpf.getWrbtr_paid())
    			{
            		a_bkpf.setClosed(1);
    			}
            	
				bkpfDao.update(a_bkpf);
			}			
			if (l_bseg!=null && l_bseg.size()>0)
			{			
				for (Bseg wa_bseg:l_bseg)
				{
					bsegDao.update(wa_bseg);
				}
			}
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	public Bkpf cancelFiDoc(Bkpf a_bkpf, List<Bseg> l_bseg,Long a_userID, String a_tcode) throws DAOException{
		try{
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_bsik = new ArrayList<Bsik>(); 
			Calendar curDate = Calendar.getInstance(); 		 
			if (a_bkpf.getWaers() == null || a_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			if (a_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			if (a_bkpf.getMonat()==0){
				throw new DAOException("Document month not chosen");
			}
			//if (a_bkpf.getBusiness_area_id()==null || a_bkpf.getBusiness_area_id()==0){
			//	throw new DAOException("Business area not chosen");
			//}
			if (a_bkpf.getCpudt()==null){
				throw new DAOException("System date not chosen");
			}
			
			Bkpf temp_bkpf = new Bkpf();
			temp_bkpf = bkpfDao.dynamicFindSingleBkpf(" belnr = "+a_bkpf.getBelnr()+" and gjahr = "+a_bkpf.getGjahr(),a_bkpf.getBukrs());
			if (temp_bkpf==null || temp_bkpf.getStorno()==1)
			{
				throw new DAOException("Неправильный фин. документ.");
			}
			
			 
			 
			p_bkpf.setStblg(a_bkpf.getBelnr());
			p_bkpf.setStjah(a_bkpf.getGjahr());
			p_bkpf.setBukrs(a_bkpf.getBukrs());
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR));
			p_bkpf.setBlart("ST");
			p_bkpf.setBudat(curDate.getTime());
			p_bkpf.setBldat(a_bkpf.getBldat());
            p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
            p_bkpf.setCpudt(curDate.getTime()); 
            p_bkpf.setUsnam(a_userID);
            p_bkpf.setTcode(a_tcode);
            p_bkpf.setWaers(a_bkpf.getWaers());
            p_bkpf.setKursf(a_bkpf.getKursf()); 
            //p_bkpf.setBrnch(userData.getBranch_id());
            p_bkpf.setBusiness_area_id(a_bkpf.getBusiness_area_id());
            p_bkpf.setCustomer_id(a_bkpf.getCustomer_id());
            p_bkpf.setContract_number(a_bkpf.getContract_number());
            p_bkpf.setAwkey(a_bkpf.getAwkey());
            p_bkpf.setAwtyp(a_bkpf.getAwtyp());
            p_bkpf.setDmbtr(a_bkpf.getDmbtr());
            p_bkpf.setDmbtr_paid(a_bkpf.getDmbtr_paid());
            p_bkpf.setWrbtr(a_bkpf.getWrbtr());
            p_bkpf.setWrbtr_paid(a_bkpf.getWrbtr_paid());
            p_bkpf.setAwkey2(a_bkpf.getAwkey2());
            p_bkpf.setBrnch(a_bkpf.getBrnch());
            p_bkpf.setClosed(1);
            for (Bseg wa_bseg : l_bseg) {
            	Bseg wa_bseg2 = new Bseg(); 
            	wa_bseg2.setGjahr(curDate.get(Calendar.YEAR));
            	wa_bseg2.setBukrs(wa_bseg.getBukrs());
            	wa_bseg2.setBuzei(wa_bseg.getBuzei());
            	
            	wa_bseg2.setHkont(wa_bseg.getHkont());
            	String wa_shkzg = "";
            	String wa_bschl = "";
            	if (wa_bseg.getShkzg().equals("S"))
            	{	
            		wa_shkzg = "H"; 
            		if (p_bkpf.getAwtyp()==0)
                	{
            			wa_bschl = "50";
                	}
                	else if (p_bkpf.getAwtyp()==1)
                	{
                		wa_bschl = "12";
                	}
                	else if (p_bkpf.getAwtyp()==2)
                	{
                		wa_bschl = "32";
                	}
            	}
            	else if (wa_bseg.getShkzg().equals("H"))
            	{
            		wa_shkzg = "S";
            		if (p_bkpf.getAwtyp()==0)
                	{
            			wa_bschl = "40";
                	}
                	else if (p_bkpf.getAwtyp()==1)
                	{
                		wa_bschl = "2";
                	}
                	else if (p_bkpf.getAwtyp()==2)
                	{
                		wa_bschl = "22";
                	}
            	}
            	
            	
            	wa_bseg2.setBschl(wa_bschl);
            	wa_bseg2.setShkzg(wa_shkzg);
            	wa_bseg2.setDmbtr(wa_bseg.getDmbtr());
            	wa_bseg2.setWrbtr(wa_bseg.getWrbtr());
            	wa_bseg2.setLifnr(wa_bseg.getLifnr());
            	wa_bseg2.setLifnr(wa_bseg.getLifnr());
            	wa_bseg2.setMatnr(wa_bseg.getMatnr());
            	wa_bseg2.setWerks(wa_bseg.getWerks());
            	wa_bseg2.setMenge(wa_bseg.getMenge());
            	wa_bseg2.setSgtxt(wa_bseg.getSgtxt()); 
            	pl_bseg.add(wa_bseg2);	
            	
            	Bsik wa_bsik = new Bsik();
            	wa_bsik.setGjahr(curDate.get(Calendar.YEAR));
            	wa_bsik.setBukrs(wa_bseg2.getBukrs());            	
            	wa_bsik.setBuzei(wa_bseg2.getBuzei());
            	wa_bsik.setBschl(wa_bschl);
            	wa_bsik.setHkont(wa_bseg2.getHkont());
            	wa_bsik.setShkzg(wa_shkzg);
            	wa_bsik.setDmbtr(wa_bseg2.getDmbtr());
            	wa_bsik.setWrbtr(wa_bseg2.getWrbtr());
            	wa_bsik.setLifnr(wa_bseg2.getLifnr());
            	wa_bsik.setMatnr(wa_bseg2.getMatnr());
            	wa_bsik.setWerks(wa_bseg2.getWerks());
            	wa_bsik.setMenge(wa_bseg2.getMenge());
            	wa_bsik.setSgtxt(wa_bseg2.getSgtxt());  				
            	pl_bsik.add(wa_bsik);
			}  
            check_empty_fields(p_bkpf, pl_bseg);
            insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);
            a_bkpf.setClosed(1);
            a_bkpf.setStorno(1);
            updateFiDoc(a_bkpf, null);
            return a_bkpf;
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	public void check_empty_fields(Bkpf a_bkpf, List<Bseg> l_bseg) throws DAOException{
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
			
			if (a_bkpf.getWaers() == null || a_bkpf.getWaers().isEmpty()){
				throw new DAOException("Currency is empty");
			}
			
			if (a_bkpf.getKursf() == 0){
				throw new DAOException("Currency rate is empty");
			}
			
			if (a_bkpf.getBrnch().equals(0L)){				
				throw new DAOException("Branch is empty");
			}
			else
			{
				if (branchDao.find(a_bkpf.getBrnch()).getType()!=3){
					if (!a_bkpf.getBlart().equals("I2")){
						throw new DAOException("incorrect branch type, contact administrator");
					}
				}
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
	        
	        //if (!a_bkpf.getWaers().equals("USD"))
	        //{
	        	//if (exchangeRateDao.countExRateForToday(a_bkpf.getWaers())==0)
	        	//{
	        	//	throw new DAOException("Курс на сегодня не введен.");
	        	//}	        		
	        //}
	        
	        
	        
			//Bldat
	        if (a_bkpf.getBldat()==null)
	        {
	        	throw new DAOException("Document date is empty");
	        }
//			if (a_bkpf.getBldat().before(firstDay.getTime())) { 
//				throw new DAOException("Document date is empty or less than "+curDateString); 
//			}
				          
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
				else if (wa_bseg.getDmbtr() == 0 && (a_bkpf.getBlart().equals("RG") || a_bkpf.getBlart().equals("PE") || a_bkpf.getBlart().equals("GW") || (a_bkpf.getBlart().equals("DP") && a_bkpf.getTcode().equals("FACI01")))){
					//OK
				}
				else if (!a_bkpf.getWaers().equals("USD") && wa_bseg.getDmbtr() == 0 && wa_bseg.getWrbtr() > 0)
				{
					//OK
				}
				else if (wa_bseg.getDmbtr() == 0)
				{
					System.out.println(a_bkpf.getCustomer_id());
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
			if (a_bkpf.getBlart().equals("AK")||a_bkpf.getBlart().equals("ST")||a_bkpf.getBlart().equals("GS")||a_bkpf.getBlart().equals("GW")
					 ||a_bkpf.getBlart().equals("PE")||a_bkpf.getBlart().equals("RG")||a_bkpf.getBlart().equals("DP"))
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
			
			if (!a_bkpf.getBlart().equals("GS")){				
				if ((a_bkpf.getWaers().equals("USD")) && (dmbtr_h != dmbtr_s ||  dmbtr_h!=a_bkpf.getDmbtr())){
					throw new DAOException("Debet and credit amounts are not equal in local currency with Header");
				}
				if ((!a_bkpf.getWaers().equals("USD")) && (wrbtr_h != wrbtr_s ||  wrbtr_h!=a_bkpf.getWrbtr())){
					throw new DAOException("Debet and credit amounts are not equal in foriegn currency with Header");
				}				
			}
			
			//Проверка средств в кассе
			Map<String,Double> l_cashBankAmountBseg = new HashMap<String,Double>(); 
			for (Bseg wa_bseg:l_bseg)
			{
				if ((wa_bseg.getHkont().startsWith("1010")||wa_bseg.getHkont().startsWith("1030")) && wa_bseg.getShkzg().equals("H"))
				{
					double amount = 0;
					Hkont wa_hkont = hkontDao.findByIds(wa_bseg.getBukrs(), wa_bseg.getHkont());
					if (wa_hkont==null)
					{
						throw new DAOException("Счет главной книги не найден. "+ wa_bseg.getBukrs()+" "+wa_bseg.getHkont());
					}
					else
					{
						if (wa_hkont.getWaers()==null) throw new DAOException("Валюта не присвоена. "+wa_bseg.getBukrs()+" "+wa_bseg.getHkont());
					}
					if (l_cashBankAmountBseg.get(wa_bseg.getHkont())==null)
					{
						if (wa_hkont.getWaers().equals("USD"))
						{
							amount = wa_bseg.getDmbtr();
						}
						else
						{
							amount = wa_bseg.getWrbtr();
						}
						l_cashBankAmountBseg.put(wa_bseg.getHkont(), amount);
					}
					else
					{
						amount = l_cashBankAmountBseg.get(wa_bseg.getHkont());
						if (wa_hkont.getWaers().equals("USD"))
						{
							amount = amount + wa_bseg.getDmbtr();
						}
						else
						{
							amount = amount + wa_bseg.getWrbtr();
						}
						l_cashBankAmountBseg.put(wa_bseg.getHkont(), amount);
						
					}
					
					
				}
			}
			for (Map.Entry<String,Double> entry : l_cashBankAmountBseg.entrySet()) {
		    	double amount = 0;
		    	amount = (Double) entry.getValue();
		    	System.out.println(fmglflextDao.findByBukrsGjahrAmount(a_bkpf.getBukrs(), a_bkpf.getGjahr(), entry.getKey()));
		    	if (amount>fmglflextDao.findByBukrsGjahrAmount(a_bkpf.getBukrs(), a_bkpf.getGjahr(), entry.getKey()))
		    	{
		    		throw new DAOException("Не достаточно средтсв.");  
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
	
	public Long EpxenseNoInvoice(Bkpf a_bkpf, List<Bseg> al_bseg) throws DAOException 
	{
		try
		{
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> l_bseg =  new ArrayList<Bseg>();
			List<Bsik> l_bsik =  new ArrayList<Bsik>();
			BeanUtils.copyProperties(a_bkpf, p_bkpf);
			
			p_bkpf.setDmbtr(0);
			p_bkpf.setWrbtr(0);
			for(Bseg wa_bseg:al_bseg)
			{
				Bseg p_bseg = new Bseg();
				BeanUtils.copyProperties(wa_bseg, p_bseg);
				l_bseg.add(wa_bseg);
				
				if (wa_bseg.getShkzg().equals("H"))
				{
					p_bkpf.setDmbtr(p_bkpf.getDmbtr()+wa_bseg.getDmbtr());
					p_bkpf.setWrbtr(p_bkpf.getWrbtr()+wa_bseg.getWrbtr());
				}
			}
			check_empty_fields(p_bkpf, l_bseg);
			insertNewFiDoc(p_bkpf, l_bseg, l_bsik);
			return p_bkpf.getBelnr();
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	public Long IncomeNoInvoice(Bkpf a_bkpf, List<Bseg> al_bseg) throws DAOException 
	{
		try
		{
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> l_bseg =  new ArrayList<Bseg>();
			List<Bsik> l_bsik =  new ArrayList<Bsik>();
			BeanUtils.copyProperties(a_bkpf, p_bkpf);

			p_bkpf.setDmbtr(0);
			p_bkpf.setWrbtr(0);
			for(Bseg wa_bseg:al_bseg)
			{
				Bseg p_bseg = new Bseg();
				BeanUtils.copyProperties(wa_bseg, p_bseg);
				l_bseg.add(wa_bseg);
				
				if (wa_bseg.getShkzg().equals("H"))
				{
					p_bkpf.setDmbtr(p_bkpf.getDmbtr()+wa_bseg.getDmbtr());
					p_bkpf.setWrbtr(p_bkpf.getWrbtr()+wa_bseg.getWrbtr());
				}
			}
			check_empty_fields(p_bkpf, l_bseg);
			insertNewFiDoc(p_bkpf, l_bseg, l_bsik);
			return p_bkpf.getBelnr();
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	public Long IncomeNoInvoiceFKSG(Bkpf a_bkpf, List<Bseg> al_bseg, Long userId,Long dealerId) throws DAOException 
	{
		try
		{
			if (dealerId==null || dealerId.equals(0L))
			{
				throw new DAOException("Подотчетник не выбран");
			}
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> l_bseg =  new ArrayList<Bseg>();
			List<Bsik> l_bsik =  new ArrayList<Bsik>();
			List<Bseg> l_bsegToLogisticsDepartment =  new ArrayList<Bseg>();
			BeanUtils.copyProperties(a_bkpf, p_bkpf);
			p_bkpf.setDmbtr(0);
			p_bkpf.setWrbtr(0);
			for(Bseg wa_bseg:al_bseg)
			{
				Bseg p_bseg = new Bseg();
				BeanUtils.copyProperties(wa_bseg, p_bseg);
				l_bseg.add(wa_bseg);
				
				if (wa_bseg.getShkzg().equals("H"))
				{
					//wa_bseg.setHkont(financeServiceLogistics.get6010MatnrHkont(p_bkpf.getBukrs(), wa_bseg.getMatnr(), "FinanceServiceImpl-->IncomeNoInvoiceFKSG"));
					l_bsegToLogisticsDepartment.add(wa_bseg);
					p_bkpf.setDmbtr(p_bkpf.getDmbtr()+wa_bseg.getDmbtr());
					p_bkpf.setWrbtr(p_bkpf.getWrbtr()+wa_bseg.getWrbtr());
				}
			}
			check_empty_fields(p_bkpf, l_bseg);
			insertNewFiDoc(p_bkpf, l_bseg, l_bsik);
			invoiceServiceDao.createWriteoffDocFromBkpf(p_bkpf, l_bsegToLogisticsDepartment,userId,dealerId,GeneralUtil.getPreparedAwkey(p_bkpf.getBelnr(), p_bkpf.getGjahr()));
			return p_bkpf.getBelnr();
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	public Bkpf cancelFA02(Bkpf a_bkpf,List<Bseg> al_bseg, User userData, String a_tcode) throws DAOException
	{
		try
		{
			if (a_bkpf.getBlart().equals("ST"))
			{
				throw new DAOException("Документ отмены нельзя отменить.");
			}
			if (a_bkpf.getStorno()==1)
			{
				throw new DAOException("Документ уже отменен.");
			}
			
			if (a_bkpf.getBlart().equals("G2"))
			{
				cancelFiDoc(a_bkpf, al_bseg, userData.getUserid(), a_tcode);
				invoiceServiceDao.onCancelBkpf(GeneralUtil.getPreparedAwkey(a_bkpf.getBelnr(), a_bkpf.getGjahr()),userData );
			}			
			else if (a_bkpf.getBlart().equals("GC")||a_bkpf.getBlart().equals("GS"))
			{
				throw new DAOException("Отмена начисления сервиса либо договора запрещена!");
			}
			else if (a_bkpf.getBlart().equals("GE"))
			{
				throw new DAOException("Документ покупки товара нельзя отменить!");
			}
			else if (a_bkpf.getBlart().equals("RP") || a_bkpf.getBlart().equals("OV") || a_bkpf.getBlart().equals("IF") || a_bkpf.getBlart().equals("K2"))
			{
				cancelFiDoc(a_bkpf, al_bseg, userData.getUserid(), a_tcode);
			}
			else if (a_bkpf.getBlart().equals("KP"))
			{
				cancelFiDoc(a_bkpf, al_bseg, userData.getUserid(), a_tcode);
			}
			else if (a_bkpf.getBlart().equals("ZZ"))
			{
				cancelFiDoc(a_bkpf, al_bseg, userData.getUserid(), a_tcode);
			}
			else
			{
				if (a_bkpf.getBlart().equals("DP") || a_bkpf.getBlart().equals("CP") || a_bkpf.getBlart().equals("CF") 
						|| a_bkpf.getBlart().equals("KP") || a_bkpf.getBlart().equals("AE") || a_bkpf.getBlart().equals("SE") || a_bkpf.getBlart().equals("I2"))
				{
					cancelFiDoc(a_bkpf, al_bseg, userData.getUserid(), a_tcode);
					if (a_bkpf.getAwkey()!=null)
					{
						Bkpf wa_bkpf = new Bkpf();
						wa_bkpf = bkpfDao.findOriginalSingleBkpf(GeneralUtil.getPreparedBelnr(a_bkpf.getAwkey()), GeneralUtil.getPreparedGjahr(a_bkpf.getAwkey()),a_bkpf.getBukrs());
						if (wa_bkpf.getBlart().equals("GS") && a_bkpf.getBlart().equals("DP"))
						{
							wa_bkpf.setDmbtr_paid(wa_bkpf.getDmbtr_paid()-a_bkpf.getDmbtr());
							wa_bkpf.setWrbtr_paid(wa_bkpf.getWrbtr_paid()-a_bkpf.getWrbtr());
							updateFiDoc(wa_bkpf, null);
							double summa = 0;
							if (wa_bkpf.getWaers().equals("USD")) summa = a_bkpf.getDmbtr()*-1;
							else summa = a_bkpf.getWrbtr()*-1;
							serviceServiceDao.paymentStateUpdate(a_bkpf.getAwkey(), summa,a_bkpf.getBukrs());
						}
						else if (wa_bkpf.getBlart().equals("GC") && (a_bkpf.getBlart().equals("CP") || a_bkpf.getBlart().equals("CF")))
						{	
							financeServiceDms.counterUpdateContractPayment(a_bkpf,a_bkpf.getDmbtr(),  a_bkpf.getWrbtr(),  userData.getUserid(), a_tcode);
						}
						else
						{
							throw new DAOException("Свяжитесь с администратором!");
						}
					}
					
				}
				
				
			}
			return a_bkpf;
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	//tolko nachislenie
		public Long createFdint(Bkpf a_bkpf, List<Bseg> l_bseg) throws DAOException{
			try{			
				
				Bkpf p_bkpf = new Bkpf();
				List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
				List<Bsik> pl_bsik = new ArrayList<Bsik>();
				//Calendar curDate = Calendar.getInstance(); 
				//Time cputm = new Time(curDate.getTimeInMillis()); 		 
				if (a_bkpf.getWaers() == null || a_bkpf.getWaers().isEmpty()){
					throw new DAOException("Currency is empty");
				}
				if (a_bkpf.getKursf() == 0){
					throw new DAOException("Currency rate is empty");
				}
				if (a_bkpf.getMonat()==0){
					throw new DAOException("Document month not chosen");
				}
				//if (a_bkpf.getBusiness_area_id()==null || a_bkpf.getBusiness_area_id()==0){
				//	throw new DAOException("Business area not chosen");
				//}
				if (a_bkpf.getCpudt()==null){
					throw new DAOException("System date not chosen");
				}
				 
				BeanUtils.copyProperties(a_bkpf, p_bkpf);

	            for (Bseg wa_bseg : l_bseg) {
	            	Bseg wa_bseg2 = new Bseg();
	            	BeanUtils.copyProperties(wa_bseg, wa_bseg2);
	            	pl_bseg.add(wa_bseg2);	
	            	
	            	if (  p_bkpf.getWaers()!=null && p_bkpf.getWaers().equals("USD") 
	                	&& wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("1210") 
	                	&& wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0 ) 
	                {	
	                	if (wa_bseg.getShkzg().equals("S"))
	                	{
	                    	p_bkpf.setDmbtr(p_bkpf.getDmbtr()+wa_bseg.getDmbtr());                		
	                	}
	                	else
	                	{
	                		p_bkpf.setDmbtr(p_bkpf.getDmbtr()-wa_bseg.getDmbtr());
	                	}
	                }
	                else if (p_bkpf.getWaers()!=null && !p_bkpf.getWaers().equals("USD") 
	                   		&& wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("1210") 
	                   		&& wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0) 
	                {	
	                	if (wa_bseg.getShkzg().equals("S"))
	                	{
	                		p_bkpf.setWrbtr(p_bkpf.getWrbtr()+wa_bseg.getWrbtr());
	                    	p_bkpf.setDmbtr(p_bkpf.getDmbtr()+wa_bseg.getDmbtr());               		
	                	}
	                	else
	                	{
	                		p_bkpf.setWrbtr(p_bkpf.getWrbtr()-wa_bseg.getWrbtr());
	                    	p_bkpf.setDmbtr(p_bkpf.getDmbtr()-wa_bseg.getDmbtr());
	                	}
	                	
	                }
	            	
	            	
	            	Bsik wa_bsik = new Bsik();
	            	BeanUtils.copyProperties(wa_bseg2, wa_bsik);		
	            	pl_bsik.add(wa_bsik);
	            	
	            	if (wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0)
	            	{
	            		p_bkpf.setCustomer_id(wa_bseg.getLifnr());
	            	}
	            	 
				}  
	            
	            
	            
	            check_empty_fields(p_bkpf, pl_bseg); 
//	            return insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);
	            
	            prebkpfService.createPrebkpf(p_bkpf, pl_bseg);
	            return 0L;

	            
				
			}  
			catch (Exception ex)
			{	    		
				throw new DAOException(ex.getMessage());
			}
		}
		
		
		//tolko nachislenie
		public Long createFkint(Bkpf a_bkpf, List<Bseg> l_bseg) throws DAOException{
			try{			
				
				Bkpf p_bkpf = new Bkpf();
				List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
				List<Bsik> pl_bsik = new ArrayList<Bsik>(); 
				//Calendar curDate = Calendar.getInstance(); 
				//Time cputm = new Time(curDate.getTimeInMillis()); 		 
				if (a_bkpf.getWaers() == null || a_bkpf.getWaers().isEmpty()){
					throw new DAOException("Currency is empty");
				}
				if (a_bkpf.getKursf() == 0){
					throw new DAOException("Currency rate is empty");
				}
				if (a_bkpf.getMonat()==0){
					throw new DAOException("Document month not chosen");
				}
				//if (a_bkpf.getBusiness_area_id()==null || a_bkpf.getBusiness_area_id()==0){
				//	throw new DAOException("Business area not chosen");
				//}
				if (a_bkpf.getCpudt()==null){
					throw new DAOException("System date not chosen");
				}
				 
				BeanUtils.copyProperties(a_bkpf, p_bkpf); 

	            for (Bseg wa_bseg : l_bseg) {
	            	Bseg wa_bseg2 = new Bseg(); 
	            	BeanUtils.copyProperties(wa_bseg, wa_bseg2);
	            	pl_bseg.add(wa_bseg2);	
	            	
	            	if (  p_bkpf.getWaers()!=null && p_bkpf.getWaers().equals("USD") 
	               		&& wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("331") 
	               		&& wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0 ) 
	               	{	
	               		p_bkpf.setDmbtr(GeneralUtil.round(p_bkpf.getDmbtr()+wa_bseg.getDmbtr(),2)); 
	               	}
	               	else if (p_bkpf.getWaers()!=null && !p_bkpf.getWaers().equals("USD") 
	                	&& wa_bseg.getHkont()!=null && wa_bseg.getHkont().startsWith("331") 
	                	&& wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0) 
	               	{	
	               		p_bkpf.setWrbtr(GeneralUtil.round(p_bkpf.getWrbtr()+wa_bseg.getWrbtr(),2));
	               		p_bkpf.setDmbtr(GeneralUtil.round(p_bkpf.getWrbtr()/p_bkpf.getKursf(),2)); 
	               	}
	            	
	            	
	            	Bsik wa_bsik = new Bsik();
	            	BeanUtils.copyProperties(wa_bseg2, wa_bsik); 				
	            	pl_bsik.add(wa_bsik);
	            	
	            	if (wa_bseg.getLifnr()!=null && wa_bseg.getLifnr()>0)
	            	{
	            		p_bkpf.setCustomer_id(wa_bseg.getLifnr());
	            	}
	            	 
				}  
	            
	            
	            Long wa_belnr;
	            check_empty_fields(p_bkpf, pl_bseg);
//	            wa_belnr = insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);
	            
	            prebkpfService.createPrebkpf(p_bkpf, pl_bseg);
	            return 0L;
	            
	            
				
			}  
			catch (Exception ex)
			{	    		
				throw new DAOException(ex.getMessage());
			}
		}
		
		
}
