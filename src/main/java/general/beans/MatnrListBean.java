package general.beans;

import general.AppContext;
import general.dao.MatnrListDao;
import general.tables.MatnrList;
import general.tables.search.MatnrListSearch;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedProperty;

public class MatnrListBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<MatnrList> items;
	private MatnrList selected;
	private MatnrListSearch searchModel = new MatnrListSearch();
	
	public MatnrList getSelected() {
		return selected;
	}
	public void setSelected(MatnrList selected) {
		this.selected = selected;
	}
	public List<MatnrList> getItems() {
		MatnrListDao mlDao = (MatnrListDao)appContext.getContext().getBean("matnrListDao");
		this.items = mlDao.dynamicFind3(this.searchModel.getCondition());
		return items;
	}
	public MatnrListSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(MatnrListSearch searchModel) {
		this.searchModel = searchModel;
	}
	
	public void setWerks(Long werks){
		this.searchModel.setWerks(werks);
	}
	
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

}
