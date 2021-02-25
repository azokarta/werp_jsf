package reports.finance;

import f4.BlartF4;
import f4.BranchF4;
import f4.BukrsF4;
import general.AppContext; 
import general.GeneralUtil;
import general.Validation;
import general.dao.BkpfDao;
import general.dao.BsegDao;
import general.dao.ContractDao;
import general.dao.CustomerDao;
import general.dao.DAOException; 
import general.dao.MatnrDao;
import general.dao.PaymentScheduleDao;
import general.dao.StaffDao;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Bukrs; 
import general.tables.Contract;
import general.tables.Customer;  
import general.tables.Matnr;
import general.tables.PaymentSchedule;
import general.tables.Staff;

import java.io.Serializable;

 






import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
 


import org.springframework.beans.BeanUtils;

import user.User;

@ManagedBean(name = "rfdoclogBean", eager = true)
@ViewScoped
public class Rfdoclog implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String transaction_code = "RFDOCLOG";
	private final static Long transaction_id = (long) 193;
	private final static Long read = (long) 1;
	private final static Long write = (long) 2;
	
	
	
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
	//**********************Getter Setter for sessionData**********************
	@ManagedProperty(value="#{userinfo}")
	private User userData;
	public User getUserData() {
	  return userData;
	}
	public void setUserData(User userData) {
	  this.userData = userData;
	}
	//*************************************************************************
	//***************************BukrsF4*******************************
	@ManagedProperty(value="#{bukrsF4Bean}")
	private BukrsF4 p_bukrsF4Bean;				
	public BukrsF4 getP_bukrsF4Bean() {
	  return p_bukrsF4Bean;
	}
	public void setP_bukrsF4Bean(BukrsF4 p_bukrsF4Bean) {
	  this.p_bukrsF4Bean = p_bukrsF4Bean;
	}
	
	List<Bukrs> bukrs_list = new ArrayList<Bukrs>();
	public List<Bukrs> getBukrs_list(){
		return bukrs_list;
	}	
	//******************************************************************
	//***************************BukrsF4*******************************
	@ManagedProperty(value="#{blartF4Bean}")
	private BlartF4 p_blartF4Bean;
	public BlartF4 getP_blartF4Bean() {
		return p_blartF4Bean;
	}
	public void setP_blartF4Bean(BlartF4 p_blartF4Bean) {
		this.p_blartF4Bean = p_blartF4Bean;
	}
	//******************************************************************	
    //*****************************Branch**********************************
  	@ManagedProperty(value="#{branchF4Bean}")
  	private BranchF4 p_branchF4Bean;
  	public void setP_branchF4Bean(BranchF4 p_branchF4) {
  	      this.p_branchF4Bean = p_branchF4;
  	}
  	public BranchF4 getP_branchF4Bean() {
		  return p_branchF4Bean;
		}
  	
  	private Branch selectedBranch = new Branch();
  	public Branch getSelectedBranch() {
		return selectedBranch;
	}

	public void setSelectedBranch(Branch selectedBranch) {
		this.selectedBranch = selectedBranch;
	}
	List<Branch> branch_list = new ArrayList<Branch>();
  	public List<Branch> geBranch_list(){
		return branch_list;
	}
    //**********************************************************************      	
	//****************************PostConstruct**********************************
	@PostConstruct
	public void init() { 
		try
		{ 
			//Calendar curDate = Calendar.getInstance();

			
			if (p_searchTable.belnr!=null && p_searchTable.gjahr>0 && p_searchTable.bukrs !=null){
				to_search();
			} 
			//p_searchTable.setBudat_to(curDate.getTime());

			
		}
		catch (DAOException ex)
		{
			 
			addMessage("Info",ex.getMessage());  
			//toMainPage();
		}
		
	}
	

	//*************************************************************************
	
  	
    //***********************************Others***********************************************************

  	public void toMainPage() {
		try {
			
	   	 	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	   	 	context.redirect(context.getRequestContextPath()  + "/general/mainPage.xhtml");
		}
		catch (Exception ex)
		{
			 
			addMessage("Info",ex.getMessage());  
		}
	}	
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext reqCtx = RequestContext.getCurrentInstance();
		reqCtx.update("form:messages");	
    } 	
	
	//*****************************************************************	
	//****************************Search Parameters**********************************
  	private int tabindex = 0;
  	public int getTabindex() {
		return tabindex;
	}
	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}
  	public void to_search()
  	{
  		try 
		{
  			l_outputTable = new ArrayList<OutputTable>();
  			l_out = new HashMap<Long,OutputTable>();
  			p_searchTable.clear();
  			if (p_searchTable.getBukrs()==null || p_searchTable.getBukrs().equals("0"))
  			{
  				throw new DAOException("Выберите компанию");
  			}
  			
  			if (p_searchTable.getBelnr()==null)
  			{
  				throw new DAOException("Номер документа не заполнен");
  			}
  			
  			if (p_searchTable.getBelnr()==null)
  			{
  				throw new DAOException("Год не заполнен");
  			}
  			//BsidDao bsidDao = (BsidDao) appContext.getContext().getBean("bsidDao");
  			BkpfDao bkpfDao = (BkpfDao) appContext.getContext().getBean("bkpfDao");
  			BsegDao bsegDao = (BsegDao) appContext.getContext().getBean("bsegDao");
  			MatnrDao matnrDao = (MatnrDao) appContext.getContext().getBean("matnrDao");
  			ContractDao contractDao = (ContractDao) appContext.getContext().getBean("contractDao");
  			StaffDao staffDao = (StaffDao) appContext.getContext().getBean("staffDao");
  			PaymentScheduleDao paymentScheduleDao = (PaymentScheduleDao) appContext.getContext().getBean("paymentScheduleDao");
  			CustomerDao customerDao = (CustomerDao) appContext.getContext().getBean("customerDao");
  			String dynamicWhereClause = "";
  			dynamicWhereClause = dynamicWhereClause + "bukrs = '"+p_searchTable.getBukrs()+"' and belnr = "+p_searchTable.getBelnr()+" and gjahr="+p_searchTable.getGjahr();
  			p_searchTable.setBkpf_main(bkpfDao.dynamicFindSingleBkpf(dynamicWhereClause,p_searchTable.getBukrs()));
  			p_searchTable.getL_bseg_main().addAll(bsegDao.dynamicFindBseg("belnr = "+p_searchTable.getBkpf_main().getBelnr()+
  					" and gjahr="+p_searchTable.getBkpf_main().getGjahr()+" and matnr<>null and menge>0"));
  			
  			
  			if (p_searchTable.getBkpf_main().getBlart().equals("GS")
  					|| p_searchTable.getBkpf_main().getBlart().equals("AK")
  					|| p_searchTable.getBkpf_main().getBlart().equals("GC"))
  			{
  				p_searchTable.setL_bkpf_GW(bkpfDao.dynamicFindBkpf("blart='GW' and storno=0 and awkey = "+GeneralUtil.getPreparedAwkey(p_searchTable.getBkpf_main().getBelnr(), p_searchTable.getBkpf_main().getGjahr())));
  			}
  			else
  			{
  				p_searchTable = new SearchTable();
  				return;
  			}
  			p_searchTable.l_ps.addAll(paymentScheduleDao.findAllByAwkey(GeneralUtil.getPreparedAwkey(p_searchTable.getBkpf_main().getBelnr(), p_searchTable.getBkpf_main().getGjahr()),p_searchTable.getBukrs()));
  			if (p_searchTable.getBkpf_main().getBlart().equals("GC"))
  			{
  				if (p_searchTable.l_ps.get(0).getPaid()==p_searchTable.l_ps.get(0).getSum())
  				{
  					p_searchTable.can_write_off=1;
  				}
  				p_searchTable.contract = contractDao.findByContractNumber(p_searchTable.getBkpf_main().getContract_number());
  				p_searchTable.dealer = staffDao.find(p_searchTable.contract.getDealer());
  				p_searchTable.dealerName = Validation.returnFioInitials(p_searchTable.dealer.getFirstname(), p_searchTable.dealer.getLastname(), p_searchTable.dealer.getMiddlename());
  			}
  			else if (p_searchTable.getBkpf_main().getBlart().equals("GS"))
  			{
  				if (p_searchTable.getBkpf_main().getWaers().equals("USD"))
  				{
  					if (p_searchTable.getBkpf_main().getDmbtr_paid()==p_searchTable.getBkpf_main().getDmbtr())
  					{
  						p_searchTable.can_write_off=1;
  					}
  				}
  				else
  				{
  					if (p_searchTable.getBkpf_main().getWrbtr_paid()==p_searchTable.getBkpf_main().getWrbtr())
  					{
  						p_searchTable.can_write_off=1;
  					}
  				}
  			}
  			else if (p_searchTable.getBkpf_main().getBlart().equals("AK"))
  			{
  				p_searchTable.l_ps.addAll(paymentScheduleDao.findAllByAwkey(p_searchTable.getBkpf_main().getAwkey(),p_searchTable.getBukrs()));
  				if (p_searchTable.l_ps.get(0).getPaid()==p_searchTable.l_ps.get(0).getSum())
  				{
  					p_searchTable.can_write_off=1;
  				}
  			}
  			
  			
  			for (Bkpf wa_bkpf:p_searchTable.getL_bkpf_GW())
  			{
  				p_searchTable.getL_bseg_GW().addAll(bsegDao.dynamicFindBseg("belnr = "+wa_bkpf.getBelnr()+" and gjahr="+wa_bkpf.getGjahr()+" and matnr<>null and menge>0"));
  				p_searchTable.getL_bkpf_RG().addAll(bkpfDao.dynamicFindBkpf("blart='RG' and storno=0 and awkey = "+GeneralUtil.getPreparedAwkey(wa_bkpf.getBelnr(), wa_bkpf.getGjahr())));
  			}
  			
  			for (Bkpf wa_bkpf:p_searchTable.getL_bkpf_RG())
  			{
  				p_searchTable.getL_bseg_RG().addAll(bsegDao.dynamicFindBseg("belnr = "+wa_bkpf.getBelnr()+" and gjahr="+wa_bkpf.getGjahr()+" and matnr<>null and menge>0"));
  			}
  			
  			
  			
  			
  			
  			
  			
  			l_outputTable.clear();
  			for (Bseg wa_bseg:p_searchTable.getL_bseg_main())
  			{
  				OutputTable wa_out = l_out.get(wa_bseg.getMatnr());
  				if (wa_out==null)
  				{
  					wa_out = new OutputTable();
  					wa_out.matnr = matnrDao.find(wa_bseg.getMatnr());
  					wa_out.matnr_id=wa_bseg.getMatnr();
  					wa_out.menge_sale = wa_bseg.getMenge();
  					l_outputTable.add(wa_out);
  					l_out.put(wa_out.matnr_id, wa_out);
  				}
  				else
  				{
  					wa_out.menge_sale = wa_out.menge_sale+wa_bseg.getMenge();
  				}
  				
  			}
  			
  			for (Bseg wa_bseg:p_searchTable.getL_bseg_GW())
  			{
  				OutputTable wa_out = l_out.get(wa_bseg.getMatnr());
  				if (wa_out==null)
  				{
  					wa_out = new OutputTable();
  					wa_out.matnr = matnrDao.find(wa_bseg.getMatnr());
  					wa_out.matnr_id=wa_bseg.getMatnr();
  					wa_out.menge_wof = wa_bseg.getMenge();
  					l_outputTable.add(wa_out);
  					l_out.put(wa_out.matnr_id, wa_out);
  				}
  				else
  				{
  					wa_out.menge_wof = wa_out.menge_wof+wa_bseg.getMenge();
  				}
  				
  			}
  			
  			for (Bseg wa_bseg:p_searchTable.getL_bseg_RG())
  			{
  				OutputTable wa_out = l_out.get(wa_bseg.getMatnr());
  				if (wa_out==null)
  				{
  					wa_out = new OutputTable();
  					wa_out.matnr = matnrDao.find(wa_bseg.getMatnr());
  					wa_out.matnr_id=wa_bseg.getMatnr();
  					wa_out.menge_rg = wa_bseg.getMenge();
  					l_outputTable.add(wa_out);
  					l_out.put(wa_out.matnr_id, wa_out);
  				}
  				else
  				{
  					wa_out.menge_rg = wa_out.menge_rg+wa_bseg.getMenge();
  				}
  				
  			}
  			
  			
  			
  			if (p_searchTable.can_write_off==1)
  			{
  				
  				double total_menge_sale=0;
  				double total_menge_wof=0;
  				for (OutputTable wa_out:l_outputTable)
  	  			{
  					total_menge_sale = total_menge_sale + wa_out.getMenge_sale();
  					total_menge_wof = total_menge_wof+ wa_out.getMenge_wof()-wa_out.getMenge_rg();
  	  			}
  				if (total_menge_sale>total_menge_wof)
  				{
  					p_searchTable.can_write_off=1;
  				}
  				else
  				{
  					p_searchTable.can_write_off=0;
  				}
  				
  				if (p_searchTable.getContract().getContract_status_id()!=null && (p_searchTable.getContract().getContract_status_id()==3 || p_searchTable.getContract().getContract_status_id()==5))
  				{
  					p_searchTable.can_write_off=0;
  				}
  			}
  			
  			
  			
			if (p_searchTable.getBkpf_main().getCustomer_id()!=null)
			{
				p_searchTable.customer = customerDao.find(p_searchTable.getBkpf_main().getCustomer_id());
				if (p_searchTable.customer.getFiz_yur() == 1)
				{
					p_searchTable.setClientName(p_searchTable.customer.getName());
				}
	  		  	else
	  		  	{
	  		  		p_searchTable.setClientName(Validation.returnFioInitials(p_searchTable.customer.getFirstname(), p_searchTable.customer.getLastname(), p_searchTable.customer.getMiddlename()));
	  		  	}
				
			}
				
  			
  			
  			tabindex = 1;
  	  		RequestContext reqCtx = RequestContext.getCurrentInstance();
  			reqCtx.update("form:tabView");				
		}
		catch (DAOException ex)
		{			
			addMessage("Info",ex.getMessage()); 
		}  		 
  		
  	}
  	//*******************************************************************************
	//******************************************************************
	

  	Map<Long,OutputTable> l_out = new HashMap<Long,OutputTable>();
	
	private List<OutputTable> l_outputTable = new ArrayList<OutputTable>();
	public List<OutputTable> getL_outputTable() {
		return l_outputTable;
	}
	public void setL_outputTable(List<OutputTable> l_outputTable) {
		this.l_outputTable = l_outputTable;
	}
	
	
	public class OutputTable {
		public OutputTable()
		{
			
		};
		private Matnr matnr = new Matnr();
		private Long matnr_id;
		private double menge_sale = 0;
		private double menge_wof = 0;
		private double menge_rg = 0;
		public Matnr getMatnr() {
			return matnr;
		}
		public void setMatnr(Matnr matnr) {
			this.matnr = matnr;
		}
		public double getMenge_sale() {
			return menge_sale;
		}
		public void setMenge_sale(double menge_sale) {
			this.menge_sale = menge_sale;
		}
		public double getMenge_wof() {
			return menge_wof;
		}
		public void setMenge_wof(double menge_wof) {
			this.menge_wof = menge_wof;
		}
		public double getMenge_rg() {
			return menge_rg;
		}
		public void setMenge_rg(double menge_rg) {
			this.menge_rg = menge_rg;
		}
		public Long getMatnr_id() {
			return matnr_id;
		}
		public void setMatnr_id(Long matnr_id) {
			this.matnr_id = matnr_id;
		}
		
			
		
	}
	
	private SearchTable p_searchTable = new SearchTable();
	public SearchTable getP_searchTable() {
		return p_searchTable;
	}
	public void setP_searchTable(SearchTable p_searchTable) {
		this.p_searchTable = p_searchTable;
	}

	


	public class SearchTable {
		public SearchTable()
		{
			
		};
		public void clear()
		{
			can_write_off = 0;
			customer = new Customer();
			bkpf_main = new Bkpf();
			clientName = "";
			l_bseg_main = new ArrayList<Bseg>();
			l_bkpf_GW = new ArrayList<Bkpf>();
			l_bseg_GW = new ArrayList<Bseg>();
			l_bkpf_RG = new ArrayList<Bkpf>();
			l_bseg_RG = new ArrayList<Bseg>();
			l_ps = new ArrayList<PaymentSchedule>();
			dealer = new Staff();
			contract = new Contract();
			dealerName = "";
		}
		Staff dealer = new Staff();
		Contract contract = new Contract();
		String dealerName = "";
		
		public Staff getDealer() {
			return dealer;
		}
		public void setDealer(Staff dealer) {
			this.dealer = dealer;
		}
		public Contract getContract() {
			return contract;
		}
		public void setContract(Contract contract) {
			this.contract = contract;
		}
		public String getDealerName() {
			return dealerName;
		}
		public void setDealerName(String dealerName) {
			this.dealerName = dealerName;
		}
		//can_write_off 0 no, 1 yes
		private String bukrs = "";
		private Long belnr;
		private int gjahr;
		private int can_write_off = 0;
		private Customer customer = new Customer();
		private Bkpf bkpf_main = new Bkpf();
		private String clientName = "";
		private List<Bseg> l_bseg_main = new ArrayList<Bseg>();
		private List<Bkpf> l_bkpf_GW = new ArrayList<Bkpf>();
		private List<Bseg> l_bseg_GW = new ArrayList<Bseg>();
		private List<Bkpf> l_bkpf_RG = new ArrayList<Bkpf>();
		private List<Bseg> l_bseg_RG = new ArrayList<Bseg>();
		private List<PaymentSchedule> l_ps = new ArrayList<PaymentSchedule>();
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		public Long getBelnr() {
			return belnr;
		}
		public void setBelnr(Long belnr) {
			this.belnr = belnr;
		}
		public int getGjahr() {
			return gjahr;
		}
		public void setGjahr(int gjahr) {
			this.gjahr = gjahr;
		}
		public Customer getCustomer() {
			return customer;
		}
		public void setCustomer(Customer customer) {
			this.customer = customer;
		}
		public Bkpf getBkpf_main() {
			return bkpf_main;
		}
		public void setBkpf_main(Bkpf bkpf_main) {
			this.bkpf_main = bkpf_main;
		}
		public List<Bseg> getL_bseg_main() {
			return l_bseg_main;
		}
		public void setL_bseg_main(List<Bseg> l_bseg_main) {
			this.l_bseg_main = l_bseg_main;
		}
		public List<Bkpf> getL_bkpf_GW() {
			return l_bkpf_GW;
		}
		public void setL_bkpf_GW(List<Bkpf> l_bkpf_GW) {
			this.l_bkpf_GW = l_bkpf_GW;
		}
		public List<Bseg> getL_bseg_GW() {
			return l_bseg_GW;
		}
		public void setL_bseg_GW(List<Bseg> l_bseg_GW) {
			this.l_bseg_GW = l_bseg_GW;
		}
		public List<Bkpf> getL_bkpf_RG() {
			return l_bkpf_RG;
		}
		public void setL_bkpf_RG(List<Bkpf> l_bkpf_RG) {
			this.l_bkpf_RG = l_bkpf_RG;
		}
		public List<Bseg> getL_bseg_RG() {
			return l_bseg_RG;
		}
		public void setL_bseg_RG(List<Bseg> l_bseg_RG) {
			this.l_bseg_RG = l_bseg_RG;
		}
		public List<PaymentSchedule> getL_ps() {
			return l_ps;
		}
		public void setL_ps(List<PaymentSchedule> l_ps) {
			this.l_ps = l_ps;
		}
		public int getCan_write_off() {
			return can_write_off;
		}
		public void setCan_write_off(int can_write_off) {
			this.can_write_off = can_write_off;
		}
		public String getClientName() {
			return clientName;
		}
		public void setClientName(String clientName) {
			this.clientName = clientName;
		}
		
		
		
		
		
	}	
	
	
	
	
	//*****************************************************************************************************
		
}
