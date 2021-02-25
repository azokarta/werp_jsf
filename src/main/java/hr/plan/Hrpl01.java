package hr.plan;

import java.util.ArrayList;
import java.util.List;

import f4.PositionF4;
import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.dao.DAOException;
import general.dao.PyramidDao;
import general.services.SalePlanService;
import general.tables.Position;
import general.tables.Pyramid;
import general.tables.SalePlan;
import general.tables.Staff;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "hrpl01Bean")
@ViewScoped
public class Hrpl01 {

	private final static Long transactionId = 68L;
	private final static String transactionCode = "HRPL01";
	
	private String bukrs;
	private SalePlan newSP = new SalePlan();

	@PostConstruct
	public void init() {
		try {
			PermissionController.canRead(userData, transactionId);
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void save() {
		try {
			PermissionController.canWrite(userData, transactionId);
			SalePlanService spService = (SalePlanService) appContext
					.getContext().getBean("salePlanService");
			newSP.setCreated_by(userData.getUserid());
			newSP.setUpdated_by(userData.getUserid());
			spService.createSalePlan(newSP);
			newSP = new SalePlan();
			bukrs = "";
			this.selectedStaff = null;
			this.staffPositions = new ArrayList<Position>();
			GeneralUtil.addSuccessMessage("План добавлен успешно");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public void setUserData(User userData) {
		this.userData = userData;
	}

	public SalePlan getNewSP() {
		return newSP;
	}

	public void setNewSP(SalePlan newSP) {
		this.newSP = newSP;
	}

	private Staff selectedStaff;
	public Staff getSelectedStaff() {
		return selectedStaff;
	}
	
	private List<Position> staffPositions;
	public List<Position> getStaffPositions() {
		this.staffPositions = new ArrayList<Position>();
		if(this.selectedStaff != null){
			PyramidDao d = (PyramidDao)appContext.getContext().getBean("pyramidDao");
			for(Pyramid p:d.dynamicFindPyramid(" staff_id = " + this.selectedStaff.getStaff_id())){
				String positionName = "";
				for(Position pos:this.p_positionF4Bean.getPosition_list()){
					if(pos.getPosition_id().longValue() == p.getPosition_id().longValue()){
						positionName = pos.getText();
					}
				}
				if(positionName.length() > 0){
					Position temp = new Position();
					temp.setPosition_id(p.getPosition_id());
					temp.setText(positionName);
					this.staffPositions.add(temp);
				}
			}
		}
		return staffPositions;
	}

	public void assignStaff(Staff stf) {
		/*if (this.newSP != null && stf != null) {
			this.newSP.setStaff_id(stf.getStaff_id());
			this.selectedStaff = stf;
		}*/
	}
	
	public void resetStaff(){
		this.selectedStaff = null;
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	
	@ManagedProperty(value = "#{positionF4Bean}")
	private PositionF4 p_positionF4Bean;
	public void setP_positionF4Bean(PositionF4 p_positionF4Bean) {
		this.p_positionF4Bean = p_positionF4Bean;
	}

	String breadcrumb = "Отдел кадров > План > Новый план";

	public String getBreadcrumb() {
		return breadcrumb;
	}

	private List<LocalYearClass> years;

	public List<LocalYearClass> getYears() {
		this.years = new ArrayList<Hrpl01.LocalYearClass>();
		for (int i = 2015; i < 2020; i++) {
			LocalYearClass temp = new LocalYearClass();
			temp.setId(i);
			temp.setValue(i + "");
			this.years.add(temp);
		}
		return this.years;
	}

	public class LocalYearClass {
		private int id;
		private String value;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}
}