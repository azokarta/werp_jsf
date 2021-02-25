package reference;

import java.io.Serializable;
import java.util.List;

import general.GeneralUtil;
import general.dao.AssetCatalogDao;
import general.dao.DAOException;
import general.services.AssetCatalogService;
import general.tables.AssetCatalog;
import general.tables.search.DepartmentSearch;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="refAssetCatalog")
@ViewScoped
public class RefAssetCatalog extends RefBase<AssetCatalog> implements Serializable
{
	private static final long serialVersionUID = 1685735440597937928L;
	private final static Long transactionId = 66L;

	private DepartmentSearch searchModel = new DepartmentSearch();
	public DepartmentSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(DepartmentSearch searchModel) {
		this.searchModel = searchModel;
	}

	@PostConstruct
	public void init(){
		super.init();
		this.selectedRecord = new AssetCatalog();
		//System.out.println("INIT " + this.getClass());
		
	}

	
	public void Create(){
		try{
			AssetCatalogService catService = (AssetCatalogService)appContext.getContext().getBean("assetCatalogService");
			selectedRecord.setCreated_by(userData.getUserid());
			selectedRecord.setUpdated_by(userData.getUserid());
			catService.createCatalog(selectedRecord);
			this.selectedRecord = new AssetCatalog();
			GeneralUtil.addSuccessMessage("Catalog saved successfully!");
			GeneralUtil.hideDialog("AssetCatalogCreateDialog");
		}
		catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void Update(){
		try{
			AssetCatalogService catService = (AssetCatalogService)appContext.getContext().getBean("assetCatalogService");
			selectedRecord.setUpdated_by(userData.getUserid());
			catService.updateCatalog(this.selectedRecord);
			this.selectedRecord = new AssetCatalog();
			GeneralUtil.addSuccessMessage("Catalog saved successfully!");
			GeneralUtil.hideDialog("");
		}
		catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	@Override
	public List<AssetCatalog> getRecordList() {
		AssetCatalogDao d = (AssetCatalogDao)appContext.getContext().getBean("assetCatalogDao");
		this.recordList = d.findAll(this.searchModel.getCondition());
		return this.recordList;
	}
	
	@Override
	public Long getTransactionId() {
		return transactionId;
	}
	
	@Override
    public AssetCatalog prepareCreate() {
        this.selectedRecord = new AssetCatalog();
        return this.selectedRecord;
    }
	
	@Override
	public void Search() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getBreadcrumb() {
		// TODO Auto-generated method stub
		return super.getBreadcrumb() + " > Группа ОС";
	}
}
