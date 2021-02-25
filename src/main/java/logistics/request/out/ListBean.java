package logistics.request.out;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.dao.RequestOutDao;
import general.dao.UserDao;
import general.tables.RequestOut;
import general.tables.Staff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import user.User;

@ManagedBean(name="requestOutListBean")
public class ListBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -947515600727826728L;
	static final Long transactionId = 233L;
	static final String transactionCode = "LG_REQ_OUT";
	
	@PostConstruct
	public void init(){
		if(!GeneralUtil.isAjaxRequest()){
			PermissionController.canRead(userData, transactionId);
			loadStaffMap();
			loadRList();
			
		}
	}

	Map<Long, Staff> stfMap = new HashMap<Long, Staff>();
	private void loadStaffMap(){
		UserDao d = (UserDao)appContext.getContext().getBean("userDao");
		List<general.tables.User> l = d.findAllWithStaff();
		for(general.tables.User u:l){
			stfMap.put(u.getUser_id(), u.getStaff());
		}
	}
	
	List<RequestOut> rList = new ArrayList<RequestOut>();
	private void loadRList(){
		RequestOutDao rDao = (RequestOutDao)appContext.getContext().getBean("requestOutDao");
		rList = rDao.findAll(" 1 = 1 ORDER BY id DESC ");
		for(RequestOut r:rList){
			Staff stf = stfMap.get(r.getCreated_by());
			if(stf == null){
				r.setCreator(new Staff());
			}else{
				r.setCreator(stf);
			}
		}
	}
	
	public List<RequestOut> getrList() {
		return rList;
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty("#{userinfo}")
	User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}
}
