package f4;

import general.AppContext;
import general.Validation;
import general.dao.CustomerDao;
import general.tables.Customer;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;


@ManagedBean(name = "customerF4Bean")
@ApplicationScoped
public class CustomerF4 {
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
	
	public String getNameById(Long a_cus_id_long)
	{
		//System.out.println(a_cus_id);
		//Long a_cus_id_long = Long.parseLong(a_cus_id);
		CustomerDao cusDao = (CustomerDao) appContext.getContext().getBean("customerDao");
		if (a_cus_id_long!=null && a_cus_id_long>0)
		{
			Customer wa_cus = cusDao.find(a_cus_id_long);
			if (wa_cus.getFiz_yur()!= null && wa_cus.getFiz_yur()==2)
			{
				return Validation.returnFioInitials(wa_cus.getFirstname(), wa_cus.getLastname(), wa_cus.getMiddlename());
			}
			else if (wa_cus.getFiz_yur()!= null && wa_cus.getFiz_yur()==1)
			{
				return wa_cus.getName();
			}
		}
		return "";
	}
	
	public String getNameByIdString(String a_cus_id)
	{
		String name="";
		if (a_cus_id!=null && a_cus_id.length()>0)
		{
			Long a_cus_id_long = Long.parseLong(a_cus_id);
			CustomerDao cusDao = (CustomerDao) appContext.getContext().getBean("customerDao");
			if (a_cus_id_long!=null && a_cus_id_long>0)
			{
				Customer wa_cus = cusDao.find(a_cus_id_long);
				if (wa_cus.getFiz_yur()!= null && wa_cus.getFiz_yur()==2)
				{
					name = Validation.returnFioInitials(wa_cus.getFirstname(), wa_cus.getLastname(), wa_cus.getMiddlename());
				}
				else if (wa_cus.getFiz_yur()!= null && wa_cus.getFiz_yur()==1)
				{
					name = wa_cus.getName();
				}
			}
			
		}
		return name;
		
	}

}
