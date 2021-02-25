package reference;

import general.GeneralUtil;
import general.PermissionController;
import general.dao.BankDao;
import general.dao.DAOException;
import general.services.BankService;
import general.tables.Bank;
import general.tables.search.BankSearch;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="refBank")
@ViewScoped
public class RefBank extends RefBase<Bank> implements Serializable{
	
	private static final long serialVersionUID = -5191891977396067638L;
	private static final Long transactionId = 75L;
	
	private BankSearch searchModel = new BankSearch();
	public BankSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(BankSearch searchModel) {
		this.searchModel = searchModel;
	}

	@PostConstruct
	public void init(){
		PermissionController.canRead(userData, transactionId);
		super.init();
		selectedRecord = new Bank();
		if(GeneralUtil.isAjaxRequest()){
			return;
		}
		
		loadRecordList();
		//System.out.println(this.getClass() + " init...");
		//this.loadRecordList("");
	}
	
	@Override
	public void Search(){
	}

	@Override
	public void Create() {
		try{
			PermissionController.canWrite(userData, transactionId);
			BankService bService = (BankService)appContext.getContext().getBean("bankService");
			selectedRecord.setCreated_by(userData.getUserid());
			selectedRecord.setUpdated_by(userData.getUserid());
			bService.create(selectedRecord);
			this.selectedRecord = new Bank();
			GeneralUtil.addSuccessMessage("SUCCESS REF BANK");
			GeneralUtil.hideDialog("BankCreateDialog");
			recordList.add(selectedRecord);
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	@Override
	public void Update() {
		
	}
	
	private void loadRecordList(){
		BankDao d = (BankDao)appContext.getContext().getBean("bankDao");
		this.recordList = d.findAll(this.searchModel.getCondition());
	}
	
	@Override
	public List<Bank> getRecordList() {
		
		return super.getRecordList();
	}

	@Override
	public Long getTransactionId() {
		return transactionId;
	}
	
	@Override
	public String getBreadcrumb() {
		return super.getBreadcrumb() + " > Список банков";
	}
	

	@Override
    public Bank prepareCreate() {
        this.selectedRecord = new Bank();
        return this.selectedRecord;
    }
}
