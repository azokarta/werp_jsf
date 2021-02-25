package hr.staff;

import f4.BranchF4;
import f4.BukrsF4;
import f4.BusinessAreaF4;
import f4.PositionF4;
import general.AppContext;
import general.GeneralUtil;
import general.MessageProvider;
import general.Validation;
import general.dao.DAOException;
import general.dao.PyramidDao;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.dao.SubCompanyDao;
import general.services.SalaryService;
import general.tables.Branch;
import general.tables.Bukrs;
import general.tables.BusinessArea;
import general.tables.Position;
import general.tables.Pyramid;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.SubCompany;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name="hrsSalaryListBean")
@ViewScoped
public class HrsSalaryList implements Serializable{

	private static final long serialVersionUID = 3765525083824671899L;
	private Salary selected;
	public Salary getSelected() {
		return selected;
	}
	public void setSelected(Salary selected) {
		this.selected = selected;
	}


	private Staff selectedStaff;
	public Staff getSelectedStaff() {
		return selectedStaff;
	}
	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}


	List<Salary> salaryList = new ArrayList<Salary>();
	public List<Salary> getSalaryList() {
		//if(this.salaryList == null){
			if(this.selectedStaff != null){
				SalaryDao sDao = (SalaryDao)appContext.getContext().getBean("salaryDao");
				this.salaryList = sDao.findAll("staff_id = " + this.selectedStaff.getStaff_id() + " ORDER BY end_date DESC ");
			}
		//}
		return salaryList;
	}
	
	public List<Bukrs> getBukrsListt(){
		return null;
	}
	
	public String getSalaryBukrs(String salaryBookrsId){
		for(Bukrs b:bukrsF4Bean.getBukrs_list()){
			if(salaryBookrsId == b.getBukrs()){
				return b.getName();
			}
		}
		return "";
	}
	
	public String getSalaryBranch(Long salaryBranchId){
		for(Branch b:branchF4Bean.getBranch_list()){
			if(b.getBranch_id() == salaryBranchId){
				return b.getText45();
			}
		}
		
		return "";
	}
	
	public String getSalaryPosition(Long salaryPositionId){
		for(Position p:positionF4Bean.getPosition_list()){
			if(p.getPosition_id() == salaryPositionId){
				return p.getText();
			}
		}
		return "";
	}

	public String getListDialogHeader(){
		String ss = new MessageProvider().getValue("hr.staff.staff_salary_history");
		if(this.selectedStaff != null){
			return ss + " (" + this.selectedStaff.getLF() + ")"; 
		}
		return ss;
	}
	
	public Salary prepareCreate(){
		this.selected = new Salary();
		return this.selected;
	}
	
	private List<Branch> branchList = new ArrayList<Branch>();
	private List<BusinessArea> businessAreaList = new ArrayList<BusinessArea>();
	public void loadBranchList(){
		this.branchList = new ArrayList<Branch>();
		for(Branch b:this.branchF4Bean.getBranch_list()){
			if(b.getBusiness_area_id() == this.selected.getBusiness_area_id()){
				branchList.add(b);
			}
		}
	}
	
	public List<Branch> getBranchList() {
		return branchList;
	}
	public List<BusinessArea> getBusinessAreaList() {
		return businessAreaList;
	}
	public void loadBusinessAreaList(){
		this.businessAreaList = new ArrayList<BusinessArea>();
		for(BusinessArea b:this.baF4Bean.getBusinessArea_list()){
			if(b.getBukrs().equals(this.selected.getBukrs())){
				this.businessAreaList.add(b);
			}
		}
	}
	
	private List<SubCompany> subCompanyList = null;	
	public List<SubCompany> getSubCompanyList() {
		if(this.subCompanyList == null){
			SubCompanyDao sDao = (SubCompanyDao)appContext.getContext().getBean("subCompanyDao");
			this.subCompanyList = sDao.findAll();
		}
		return subCompanyList;
	}
	@PostConstruct
	public void init(){
		
	}
	
	public void Create(){
		try{
			if(this.selectedStaff == null){
				throw new DAOException("Выберите сотрудника");
			}
			SalaryService sService = (SalaryService)appContext.getContext().getBean("salaryService");
			
			this.selected.setCreated_by(userData.getUserid());
			this.selected.setUpdated_by(userData.getUserid());
			this.selected.setStaff_id(this.selectedStaff.getStaff_id());
			this.selected.setP_staff(this.selectedStaff);
			for(Branch br:this.branchF4Bean.getBranch_list()){
				if(br.getBranch_id() == this.selected.getBranch_id()){
					this.selected.setCountry_id(br.getCountry_id());
					break;
				}
			}
			sService.createSalary(this.selected,parentPyramidId);
			
			GeneralUtil.addSuccessMessage("Salary Created Successfully");
			GeneralUtil.hideDialog("SalaryCreateDialog");
			
		}catch(DAOException e){
			selected.setSalary_id(null);
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void Reset(){
		this.selected = null;
	}
	
	private Long parentPyramidId = 0L;
	public Long getParentPyramidId() {
		return parentPyramidId;
	}
	public void setParentPyramidId(Long parentPyramidId) {
		this.parentPyramidId = parentPyramidId;
	}


	private List<OutputPyramid> outputPyramidList = new ArrayList<HrsSalaryList.OutputPyramid>();
	public List<OutputPyramid> getOutputPyramidList() {
		return outputPyramidList;
	}


	public void setPyramidItems() {
		outputPyramidList.clear();
		if(!Validation.isEmptyString(selected.getBukrs())){
			PyramidDao pDao = (PyramidDao)appContext.getContext().getBean("pyramidDao");
			try{
				Pyramid pRoot = pDao.findRoot(selected.getBukrs());
				outputPyramidList.add(getOutputPyramid(pRoot, 0));
				addPyramidRecursively(pRoot.getPyramid_id(), 0);
			}catch(DAOException e){
				
			}
		}
	}
	
	private OutputPyramid getOutputPyramid(Pyramid p,int deep){
		OutputPyramid op = new OutputPyramid();
		StaffDao stfDao = (StaffDao)appContext.getContext().getBean("staffDao");
		StringBuffer sb = new StringBuffer("");
		for(int k = 0; k < deep; k++){
			sb.append(" -> ");
		}
		if(p.getStaff_id() != null && p.getStaff_id().longValue() > 0){
			Staff stf = stfDao.find(p.getStaff_id());
			if(stf != null){
				sb.append(stf.getLF());
			}
		}
		
		if(p.getPosition_id() != null && p.getPosition_id().longValue() > 0){
			sb.append("(" + positionF4Bean.getName("" + p.getPosition_id()) + ") ");
		}
		op.setTitle(sb.toString());
		op.setPyramid_id(p.getPyramid_id());
		return op;
	}
	
	private void addPyramidRecursively(Long parentId,int deep){
		PyramidDao pDao = (PyramidDao)appContext.getContext().getBean("pyramidDao");
		
		for(Pyramid p:pDao.dynamicFindPyramid("parent_pyramid_id = " + parentId + " AND position_id != 4 ")){
			outputPyramidList.add(getOutputPyramid(p, deep));
			addPyramidRecursively(p.getPyramid_id(), deep + 1);
		}
	}
	
	public class OutputPyramid{
		Long pyramid_id;
		String title;
		public Long getPyramid_id() {
			return pyramid_id;
		}
		public void setPyramid_id(Long pyramid_id) {
			this.pyramid_id = pyramid_id;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
	}
	
	public boolean isCurrentSalary(Salary s){
		return s.getEnd_date().after(new Date());
	}
	
	
	public void removeSalary(Salary s){
		
	}


	@ManagedProperty(value = "#{appContext}")
	AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
	
	@ManagedProperty(value = "#{bukrsF4Bean}")
	private BukrsF4 bukrsF4Bean;
	public void setBukrsF4Bean(BukrsF4 bukrsF4Bean) {
		this.bukrsF4Bean = bukrsF4Bean;
	}
	
	@ManagedProperty(value = "#{branchF4Bean}")
	private BranchF4 branchF4Bean;
	public void setBranchF4Bean(BranchF4 branchF4Bean) {
		this.branchF4Bean = branchF4Bean;
	}
	
	@ManagedProperty(value = "#{positionF4Bean}")
	private PositionF4 positionF4Bean;
	public void setPositionF4Bean(PositionF4 positionF4Bean) {
		this.positionF4Bean = positionF4Bean;
	}
	
	@ManagedProperty(value="#{businessAreaF4Bean}")
	BusinessAreaF4 baF4Bean;
	public void setBaF4Bean(BusinessAreaF4 baF4Bean) {
		this.baF4Bean = baF4Bean;
	}
	
	@ManagedProperty(value = "#{userinfo}")
	private User userData;
	public void setUserData(User userData) {
		this.userData = userData;
	}
}