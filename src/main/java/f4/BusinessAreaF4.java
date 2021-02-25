package f4;

import general.AppContext;
import general.dao.BusinessAreaDao;
import general.dao.DAOException;
import general.tables.Branch;
import general.tables.BusinessArea;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
 

@ManagedBean(name="businessAreaF4Bean")
@ApplicationScoped
public class BusinessAreaF4 {
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	
	List<BusinessArea> l_ba = new ArrayList<BusinessArea>(); 
	public List<BusinessArea> getL_ba(){
		return l_ba;
	} 

	public List<BusinessArea> getBusinessArea_list(){
		return l_ba;
	} 
	
	
	@PostConstruct
	public void init(){
		try
		{
			l_ba = new ArrayList<BusinessArea>();
			BusinessAreaDao businessAreaDao = (BusinessAreaDao) appContext.getContext().getBean("businessAreaDao");
			this.l_ba = businessAreaDao.findAll();		
		}
	    catch(Exception ex)
		{
	    	System.out.println("Business area F4 not loaded");
	    	throw new DAOException("Business area F4 not loaded");
		}
	}
	
	public void updateF4()
	{
		try
		{
			l_ba = new ArrayList<BusinessArea>();
			BusinessAreaDao businessAreaDao = (BusinessAreaDao) appContext.getContext().getBean("businessAreaDao");
			this.l_ba = businessAreaDao.findAll();		
		}
	    catch(Exception ex)
		{
	    	System.out.println("Business area F4 not loaded");
	    	throw new DAOException("Business area F4 not loaded");
		}
	}
	
    //*****************************Branch**********************************
  	@ManagedProperty(value="#{branchF4Bean}")
  	private BranchF4 p_branchF4Bean;
  	public void setP_branchF4Bean(BranchF4 p_branchF4) {
  	      this.p_branchF4Bean = p_branchF4;
  	}
  	public BranchF4 getP_branchF4Bean() {
		  return p_branchF4Bean;
		}
    //*****************************Branch**********************************
  	public List<BusinessArea> getBAByBA(Long a_ba_id)
	{
  		List<BusinessArea> wa_ba_list = new ArrayList<BusinessArea>();
  		for(BusinessArea wa_ba:l_ba)
		{
			if (a_ba_id==wa_ba.getBusiness_area_id())
			{
				wa_ba_list.add(wa_ba);
			}				
		}
  		return wa_ba_list;
	}
	public List<BusinessArea> getByBukrs(String a_bukrs,String a_user_bukrs, Long a_branch_id)
	{
		//System.out.println(a_bukrs);
		//System.out.println(a_user_bukrs);
		//System.out.println(a_branch_id);
		List<BusinessArea> wa_ba_list = new ArrayList<BusinessArea>();
		if (a_user_bukrs != null && a_user_bukrs.length()>0 && !a_user_bukrs.equals("0001"))
		{
			//System.out.println("zzz");
			if (a_branch_id!=null && a_branch_id>1)
			{
				
				for(Branch wa_branch:p_branchF4Bean.branch_list)
				{
					if (wa_branch.getBranch_id()==a_branch_id && wa_branch.getBukrs().equals(a_bukrs))
					{
						if (wa_branch.getBusiness_area_id()!=null)
						{
							for(BusinessArea wa_ba:l_ba)
							{
								if (wa_ba.getBukrs().equals(a_bukrs) && wa_branch.getBusiness_area_id()==wa_ba.getBusiness_area_id())
								{
									wa_ba_list.add(wa_ba);
								}				
							}
						}
						else
						{
							for(BusinessArea wa_ba:l_ba)
							{
								if (wa_ba.getBukrs().equals(a_bukrs))
								{
									wa_ba_list.add(wa_ba);
								}				
							}
						}
						
					}				
				}
			}
						
		}
		else if (a_bukrs!=null && a_bukrs.length()>0)
		{
			if (!a_bukrs.equals("0"))
			{
				for(BusinessArea wa_ba:l_ba)
				{
					if (wa_ba.getBukrs().equals(a_bukrs))
					{
						wa_ba_list.add(wa_ba);
					}				
				}
			}
			
		}
		
		return wa_ba_list;
		
	}
	
	
	 


	  
}
