package reference;

import general.GeneralUtil;
import general.dao.DAOException;
import general.dao.DepartmentDao;
import general.services.DepartmentService;
import general.tables.Department;
import general.tables.search.DepartmentSearch;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="refDepartment")
@ViewScoped
public class RefDepartment extends RefBase<Department> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static final Long transactionId = 75L;
	
	@PostConstruct
	public void init(){
		super.init();
		selectedRecord = new Department();
	}
	
        @Override
        public List<Department> getRecordList() {
            if(this.recordList == null){
                this.loadRecordList();
            }
            return recordList;
	}
        
        private DepartmentSearch searchModel = new DepartmentSearch();
        
        private void loadRecordList(){
            DepartmentDao d = (DepartmentDao)appContext.getContext().getBean("departmentDao");
            this.recordList = d.findAll(this.searchModel.getCondition());
        }
        
	@Override
	public void Search(){
            this.loadRecordList();
	}


	@Override
	public Long getTransactionId() {
		return transactionId;
	}

    @Override
    public void Create() {
        try{
            DepartmentService service = (DepartmentService)appContext.getContext().getBean("departmentService");
            service.create(this.selectedRecord);
            GeneralUtil.hideDialog("DepartmentCreateDialog");
            GeneralUtil.addSuccessMessage("Department Added Successfully");
        }catch(DAOException e){
            GeneralUtil.addErrorMessage(e.getMessage());
        }
    }

    @Override
    public void Update() {
        try{
            DepartmentService service = (DepartmentService)appContext.getContext().getBean("departmentService");
            service.update(this.selectedRecord);
            GeneralUtil.hideDialog("DepartmentUpdateDialog");
            GeneralUtil.addSuccessMessage("Department Updated Successfully");
        }catch(DAOException e){
            GeneralUtil.addErrorMessage(e.getMessage());
        }
    }

    /**
     * @return the searchModel
     */
    public DepartmentSearch getSearchModel() {
        return searchModel;
    }

    /**
     * @param searchModel the searchModel to set
     */
    public void setSearchModel(DepartmentSearch searchModel) {
        this.searchModel = searchModel;
    }

    @Override
    public Department prepareCreate() {
        this.selectedRecord = new Department();
        return this.selectedRecord;
    }

    @Override
    public String getBreadcrumb() {
        return super.getBreadcrumb() + " > Все департаменты ";
    }
}