package reference;

import java.io.Serializable;
import java.util.List;

import general.GeneralUtil;
import general.MessageController;
import general.PermissionController;
import general.dao.DAOException;
import general.dao.SubCompanyDao;
import general.services.SubCompanyService;
import general.tables.SubCompany;
import general.tables.search.SubCompanySearch;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

@ManagedBean(name="refSubCompany")
@ViewScoped
public class RefSubCompany extends RefBase<SubCompany> implements Serializable
{
	
	private static final long serialVersionUID = 506403116105814837L;
	private final static Long transactionId = 72L;
	
	private SubCompanySearch searchModel = new SubCompanySearch();
	public SubCompanySearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(SubCompanySearch searchModel) {
		this.searchModel = searchModel;
	}


	@PostConstruct
	public void init(){
		PermissionController.canRead(userData, transactionId);
		super.init();
		//System.out.println("INIT " + this.getClass());
		selectedRecord = new SubCompany();
	}
	

	public void Create(){
		try{
			PermissionController.canWrite(userData, transactionId);
			SubCompanyService scService = (SubCompanyService)appContext.getContext().getBean("subCompanyService");
			selectedRecord.setCreated_by(Long.valueOf(userData.getUserid()));
			selectedRecord.setUpdated_by(Long.valueOf(userData.getUserid()));
			scService.create(selectedRecord);
			selectedRecord = new SubCompany();
			GeneralUtil.addSuccessMessage("Success");
			GeneralUtil.hideDialog("SubCompanyCreateDialog");
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void Update(){

	}
	
	public void delete() {
		System.out.println("DELETED");
	}


	@Override
	public void Search() {
	}


	@Override
	public Long getTransactionId() {
		return transactionId;
	}
	
	@Override
	public SubCompany prepareCreate() {
		this.selectedRecord = new SubCompany();
		return this.selectedRecord;
	}
	
	@Override
	public List<SubCompany> getRecordList() {
		SubCompanyDao d = (SubCompanyDao)appContext.getContext().getBean("subCompanyDao");
		this.recordList = d.findAll(this.searchModel.getCondition());
		return this.recordList;
	}
	
	@Override
	public String getBreadcrumb() {
		// TODO Auto-generated method stub
		return super.getBreadcrumb() + " > Дочерние компании (ТОО, ИП)";
	}
}
