package hr.staff;
/**
 * РАСХОДЫ СОТРУДНИКА
 */
import general.AppContext;
import general.GeneralUtil;
import general.MessageController;
import general.dao.DAOException;
import general.dao.ExpenceTypeDao;
import general.dao.StaffExpenceDao;
import general.services.StaffExpenceService;
import general.tables.ExpenceType;
import general.tables.StaffExpence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import user.User;

@ManagedBean(name="hrsExpenceBean")
@ViewScoped
public class HrsExpence implements Serializable
{
	private static final long serialVersionUID = -4763085192717722277L;
	private StaffExpence newExpence = new StaffExpence();
	

	private List<ExpenceType> etList = new ArrayList<ExpenceType>();
	

	@PostConstruct
	public void init(){
		try{
			//Check Permission
			System.out.println(this.getClass() + " init... ");
			this.loadEtList();
		}catch(DAOException e){
			
		}
	}
	
	private void loadEtList(){
		ExpenceTypeDao d = (ExpenceTypeDao)appContext.getContext().getBean("expenceTypeDao");
		this.etList = d.findAll();
	}
	
	
	
	public void setExpenceStaffId(Long staffId){
		this.newExpence.setStaff_id(staffId);
	}
	
	
	private List<StaffExpence> seList = new ArrayList<StaffExpence>();
	
	public void loadSeList(Long staffId){
		StaffExpenceDao d = (StaffExpenceDao)appContext.getContext().getBean("staffExpenceDao");
		this.seList = d.findAllByStaffId(staffId);
	}
	

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	public AppContext getAppContext() {
		return appContext;
	}
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	
	@ManagedProperty(value = "#{userinfo}")
	private User userData;
	public User getUserData() {
		return userData;
	}
	public void setUserData(User userData) {
		this.userData = userData;
	}
	
	private Long expenceIdForDelete = 0L;
	public void setExpenceIdForDelete(Long eId){
		/*Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		this.courseIdForDelete = Long.valueOf(params.get("courseIdForDelete"));*/
		this.expenceIdForDelete = eId;
		System.out.println("DELETE COURSE: " + this.expenceIdForDelete);
	}
	
	public StaffExpence getNewExpence() {
		return newExpence;
	}

	public void setNewExpence(StaffExpence newExpence) {
		this.newExpence = newExpence;
	}

	public List<ExpenceType> getEtList() {
		return etList;
	}

	public List<StaffExpence> getSeList(Long staffId) {
		this.loadSeList(staffId);
		return seList;
	}

	public void create(){
		try{
			StaffExpenceService service = (StaffExpenceService)appContext.getContext().getBean("staffExpenceService");
			this.newExpence.setCreated_by(Long.valueOf(userData.getUserid()));
			this.newExpence.setUpdated_by(Long.valueOf(userData.getUserid()));
			service.create(this.newExpence);
			this.newExpence = new StaffExpence();
			GeneralUtil.addSuccessMessage("Expence Added Successfully!");
			GeneralUtil.hideDialog("createExpenceWidget");
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void delete(StaffExpence se){
		try{
			StaffExpenceService service = (StaffExpenceService)appContext.getContext().getBean("staffExpenceService");
			service.delete(se);
			GeneralUtil.addSuccessMessage("Expence Deleted");
			this.loadEtList();
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
}