package hr.bonus;

import java.util.ArrayList;
import java.util.List;

import f4.BukrsF4;
import f4.BusinessAreaF4;
import f4.PositionF4;
import general.AppContext;
import general.MessageController;
import general.PermissionController;
import general.dao.DAOException;
import general.tables.Bukrs;
import general.tables.BusinessArea;
import general.tables.Position;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import user.User;
@ManagedBean
public abstract class HrbBase 
{	
	protected boolean canRead = true;
	public boolean isCanRead() {
		return true;
	}
	
	@PostConstruct
	public void init(){
		this.canRead = true;
		//this.canRead = PermissionController.getInstance().isCanRead(userData, this.getTransactionId());
		try 
		{
			if (userData.getBukrs().equals("0001")){
				for (Bukrs b: bukrsF4Bean.getBukrs_list()){
					if (!b.getBukrs().equals("0001")){
						bukrsList.add(b);
					}
				}
			}
			else{
				for (Bukrs b: bukrsF4Bean.getBukrs_list()){
					if (userData.getBukrs().equals(b.getBukrs())){
						bukrsList.add(b);
					}
				}
			}
		}
		catch (DAOException ex)
		{
			MessageController.getInstance().addError(ex.getMessage()); 
			//toMainPage();
		}
	}

	List<Bukrs> bukrsList = new ArrayList<Bukrs>();
	public List<Bukrs> getBukrsList(){
		return bukrsList;
	}
	List<BusinessArea> businessAreaList;
	public List<BusinessArea> getBusinessAreaList()
	{
		return businessAreaList;
	}
	List<Position> positionList = new ArrayList<Position>();
	public List<Position> getPositionList(){
		return positionList;
	}
	
	@ManagedProperty(value="#{appContext}")
	public AppContext appContext;
	public AppContext getAppContext() {
	    return appContext;
	}
	public void setAppContext(AppContext appContext) {
	    this.appContext = appContext;
	}
	
	@ManagedProperty(value="#{businessAreaF4Bean}")
	BusinessAreaF4 businessAreaF4Bean;
	public BusinessAreaF4 getBusinessAreaF4Bean() {
	    return businessAreaF4Bean;
	}
	public void setBusinessAreaF4Bean(BusinessAreaF4 businessAreaF4Bean) {
	    this.businessAreaF4Bean = businessAreaF4Bean;
	}
	
	@ManagedProperty(value="#{userinfo}")
	User userData;
	public User getUserData() {
	    return userData;
	}
	public void setUserData(User userData) {
	    this.userData = userData;
	}
	
	@ManagedProperty(value="#{bukrsF4Bean}")
	BukrsF4 bukrsF4Bean;
	public BukrsF4 getBukrsF4Bean() {
	    return bukrsF4Bean;
	}
	public void setBukrsF4Bean(BukrsF4 b) {
	    this.bukrsF4Bean = b;
	}
	
	@ManagedProperty(value="#{positionF4Bean}")
	PositionF4 positionF4Bean;
	public PositionF4 getPositionF4Bean() {
	    return positionF4Bean;
	}
	public void setPositionF4Bean(PositionF4 p) {
	    this.positionF4Bean = p;
	}
	
	abstract Long getTransactionId();
	
	String breadcrumb = "Отдел кадров > Бонус";
	public String getBreadcrumb(){
		return this.breadcrumb;
	}
}