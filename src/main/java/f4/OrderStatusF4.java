package f4;
import general.AppContext;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean; 
import javax.faces.bean.ManagedProperty;
 



import general.dao.DAOException;
import general.dao.OrderStatusDao;
import general.tables.OrderStatus;

@ManagedBean(name = "orderStatusF4Bean")
@ApplicationScoped
public class OrderStatusF4 {
	
	
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	
	/*@ManagedProperty(value="#{userinfo}")
	private User userData;
	public void setUserData(User userData) {
	  this.userData = userData;
	}*/
	
	
	List<OrderStatus> os_list = new ArrayList<OrderStatus>(); 
	@PostConstruct
	public void init(){ 
		try
		{
			os_list = new ArrayList<OrderStatus>();
			OrderStatusDao osDao = (OrderStatusDao) appContext.getContext().getBean("orderStatusDao");
			os_list = osDao.findAll();		
		}
	    catch(Exception ex)
		{
	    	System.out.println("Order status F4 not loaded");
	    	throw new DAOException("Order status F4 not loaded");
		}
	}
	
	public void updateF4()
	{
		try
		{
			os_list = new ArrayList<OrderStatus>();
			OrderStatusDao osDao = (OrderStatusDao) appContext.getContext().getBean("orderStatusDao");
			os_list = osDao.findAll();		
		}
	    catch(Exception ex)
		{
	    	System.out.println("Order status F4 not loaded");
	    	throw new DAOException("Order status F4 not loaded");
		}
	}
	
	public List<OrderStatus> getOrderStatus_list(){
		return os_list;
	}
	
	public List<OrderStatus> getOs_list() {
		return os_list;
	}

	public String getName(Long osId,String lang){
		if(osId == null || osId.longValue() == 0){
			return "";
		}
		for(OrderStatus os:this.os_list){
			if(os.getOs_id() == osId){
				return os.getName(lang);
			}
		}
		
		return "";
	}
} 
