package hr.staff;

import f4.BranchF4;
import f4.PositionF4;
import general.AppContext;
import general.GeneralUtil;
import general.Helper;
import general.MessageProvider;
import general.Validation;
import general.dao.DAOException;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.services.StaffService;
import general.tables.Branch;
import general.tables.Deposit;
import general.tables.Position;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.search.StaffSearch;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.hibernate.annotations.common.util.StringHelper;

import user.User;

@ManagedBean(name = "hrsListBean")
@ViewScoped
public class HrsList implements Serializable {

	private static final long serialVersionUID = 7820303625385065014L;
	private int dismissed = 0;
	public int getDismissed() {
		return dismissed;
	}

	public void setDismissed(int dismissed) {
		this.dismissed = dismissed;
	}

	@PostConstruct
	public void init() {
		if(!userData.isSys_admin()){
			this.staffSearch.setBukrs(userData.getBukrs());
		}
		if(GeneralUtil.isAjaxRequest()){
			return;
		}
		// TODO PERMISSION
	}

	private StaffSearch staffSearch = new StaffSearch();
	private Staff selected;
	private List<Staff> staffList = null;
	private boolean canUpdate = false;
	private boolean canCreate = false;
	private boolean canRead = false;

	public StaffSearch getStaffSearch() {
		return staffSearch;
	}

	public void setStaffSearch(StaffSearch staffSearch) {
		this.staffSearch = staffSearch;
	}

	public Staff getSelected() {
		return selected;
	}

	public void setSelected(Staff selected) {
		this.selected = selected;
	}

	public List<Staff> getStaffList() {
		if (this.staffList == null) {
			this.setStaffList();
		}
		return staffList;
	}

	public void setStaffList() {
		StaffDao stfDao = (StaffDao) appContext.getContext()
				.getBean("staffDao");
		String cond = this.staffSearch.getCondition();
		if (!Validation.isEmptyString(this.staffSearch.getBukrs())
				&& this.staffSearch.getBranch_id() == 0) {
			List<Branch> brList = branchF4Bean.getAllBranchByBukrs(staffSearch
					.getBukrs());
			if (brList.size() > 0) {
				String[] ids = new String[brList.size()];
				int iCounter = 0;
				for (Branch br : brList) {
					ids[iCounter] = br.getBranch_id().toString();
					iCounter++;
				}
				cond += (cond.length() > 0 ? " AND " : " ") + " branch_id IN('"
						+ StringHelper.join("','", ids) + "' ) ";
			}
		}
		
		if(dismissed == 1){
			cond += (cond.length() > 0 ? " AND " : " ") + " position_id = 1 ";
		}else{
			cond += (cond.length() > 0 ? " AND " : " ") + " position_id != 1 ";
		}
		
		boolean byPosition = false;
		if(staffSearch.getPosition_id() != null && staffSearch.getPosition_id().longValue() > 0){
			cond = String.format(" staff_id IN(%s) ", "'" + String.join("','", getStaffIdsFromSalaryByPositionId(staffSearch.getPosition_id())) + "'");
			byPosition = true;
		}

		this.staffList = stfDao.findAll(cond);
		if(!byPosition){
			loadSalaries();
		}
	}
	
	private String[] getStaffIdsFromSalaryByPositionId(Long posId){
		SalaryDao d = (SalaryDao)appContext.getContext().getBean("salaryDao");
		staffSalaries = d.findAll(String.format(" position_id = %d AND end_date > '%s'", posId,Helper.getCurrentDateForDb()));
		String[] out = new String[staffSalaries.size()];
		int i = 0;
		for(Salary s:staffSalaries){
			out[i] = s.getStaff_id().toString();
			i++;
		}
		
		return out;
	}
	
	private List<Salary> staffSalaries = new ArrayList<Salary>();
	private void loadSalaries(){
		staffSalaries.clear();
		String[] ids = new String[staffList.size()];
		int i = 0;
		for(Staff stf : staffList){
			ids[i] = stf.getStaff_id().toString();
			i++;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String condition = "";
		if(ids.length > 1000){
			int chunkSize = (int)Math.ceil(ids.length/1000D);
			String[] conditions = new String[chunkSize];
			int counter = 0;
			for(int k = 0; k < ids.length; k += 1000){
				conditions[counter] = String.format(" staff_id IN (%s) ", "'" + String.join("','", Arrays.copyOfRange(ids, k, (k+1000 < ids.length ? k+1000 : ids.length))) + "'");
				counter++;
			}
			condition = "( " + String.join(" OR ", conditions) + " ) ";
		}else{
			condition = String.format("staff_id IN(%s) ", "'" + String.join("','", ids) + "'");
		}
		
		condition += " AND end_date > '" + sdf.format(new Date()) + "' ";
		if(i > 0){
			SalaryDao d = (SalaryDao)appContext.getContext().getBean("salaryDao");
			staffSalaries = d.findAll(condition);
		}
	}
	
	public String getSalaries(Long staffId){
		StringBuffer sb = new StringBuffer("");
		for(Salary sl:staffSalaries){
			if(sl.getStaff_id().equals(staffId) && sl.isCurrentSalary()){
				sb.append(positionF4Bean.getName(sl.getPosition_id().toString()) + ", ");
			}
		}
		return sb.toString().replaceAll(", $", "");
	}

	public boolean isCanUpdate() {
		return canUpdate;
	}

	public boolean isCanCreate() {
		return canCreate;
	}

	public boolean isCanRead() {
		return canRead;
	}

	@ManagedProperty(value = "#{appContext}")
	AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{positionF4Bean}")
	private PositionF4 positionF4Bean;

	public void setPositionF4Bean(PositionF4 positionF4Bean) {
		this.positionF4Bean = positionF4Bean;
	}

	@ManagedProperty(value = "#{userinfo}")
	private User userData;
	public void setUserData(User userData) {
		this.userData = userData;
	}

	public Staff prepareCreate() {
		this.selected = new Staff();
		return selected;
	}

	private List<Salary> staffSalaryList = null;

	public List<Salary> getStaffSalaryList() {
		return staffSalaryList;
	}

	private void setStaffSalaryList() {
		SalaryDao sDao = (SalaryDao) appContext.getContext().getBean(
				"salaryDao");
		this.staffSalaryList = sDao.findByStaffId(this.selected.getStaff_id());
	}

	public void prepareHistory() {
		this.setStaffSalaryList();
	}

	public void Create() {
//		try {
//
//			StaffService staffService = (StaffService) appContext.getContext()
//					.getBean("staffService");
//			this.selected.setCreated_by(userData.getUserid());
//			this.selected.setUpdated_by(userData.getUserid());
//			staffService.createStaff(this.selected,userData);
//			GeneralUtil.addSuccessMessage("Сотрудник добавлен успешно");
//			GeneralUtil.hideDialog("StaffCreateDialog");
//		} catch (DAOException ex) {
//			GeneralUtil.addErrorMessage(ex.getMessage());
//		}
		GeneralUtil.addErrorMessage("Deprecated!");
	}

	public void Update() {
		GeneralUtil.addErrorMessage("Deprecated!");
//		try {
//
//			StaffService staffService = (StaffService) appContext.getContext()
//					.getBean("staffService");
//			this.selected.setUpdated_by(userData.getUserid());
//			staffService.updateStaff(this.selected,userData);
//			GeneralUtil.addSuccessMessage("Сотрудник обновлен успешно");
//			GeneralUtil.hideDialog("StaffUpdateDialog");
//		} catch (DAOException ex) {
//			GeneralUtil.addErrorMessage(ex.getMessage());
//		}
	}

	public void Search() {
		this.setStaffList();
	}

	public void Reset() {
		this.selected = null;
	}

	public String getStaffPosition(Long positionId) {
		for (Position p : positionF4Bean.getPosition_list()) {
			if (p.getPosition_id() == positionId) {
				return p.getText();
			}
		}

		return "";
	}

	public void exportXLS(Object document) {
		// System.out.println(document);
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);

		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
			HSSFCell cell = header.getCell(i);

			cell.setCellStyle(cellStyle);
		}
	}

	String breadcrumb = " Отдел кадров > Список сотрудников";

	public String getBreadcrumb() {
		MessageProvider mp = new MessageProvider();
		if(dismissed == 1){
			return mp.getValue("breadcrumb.hrslist_dismissed");
		}
		
		return mp.getValue("breadcrumb.hrslist");
	}

	@ManagedProperty(value = "#{hrsDeposit}")
	private HrsDeposit hrsDeposit;

	public void setHrsDeposit(HrsDeposit hrsDeposit) {
		this.hrsDeposit = hrsDeposit;
	}

	public List<Deposit> getStaffDeposites() {
		if (this.selected != null) {
			return this.hrsDeposit.getItemsByStaffId(this.selected
					.getStaff_id());
		}

		return null;
	}

	@ManagedProperty(value = "#{branchF4Bean}")
	private BranchF4 branchF4Bean;

	public void setBranchF4Bean(BranchF4 branchF4Bean) {
		this.branchF4Bean = branchF4Bean;
	}

}