package general.beans;

import general.AppContext;
import general.Validation;
import general.dao.MatnrDao;
import general.tables.Matnr;
import general.tables.search.MatnrSearch;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class MatnrBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Matnr> items;
	private Matnr selected;
	private MatnrSearch searchModel = new MatnrSearch();
	
	public MatnrSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(MatnrSearch searchModel) {
		this.searchModel = searchModel;
	}
	
	public Matnr getSelected() {
		return selected;
	}
	public void setSelected(Matnr selected) {
		this.selected = selected;
	}
	
	public List<Matnr> getItems(){
		if(items == null){
			loadItems();
		}
		return this.items;
	}
	
	public List<Matnr> getItemsByBukrs(String bukrs){
		if(!Validation.isEmptyString(bukrs)){
			//this.searchModel.setBukrs(bukrs);
			loadItems();
			return this.getItems();
		}
		
		return this.items;
	}
	
	private void loadItems(){
		MatnrDao d = (MatnrDao)appContext.getContext().getBean("matnrDao");
		this.items = d.findAll(this.searchModel.getCondition());
	}
	
	public void Search(){
		loadItems();
	}
	
	
	@ManagedProperty(value="#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
