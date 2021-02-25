package general.beans;

import general.AppContext;
import general.dao.BsegDao;
import general.tables.Bseg;
import general.tables.search.BsegSearch;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="bsegBean")
@ViewScoped
public class BsegBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Bseg> items;
	private Bseg selected;
	private BsegSearch searchModel = new BsegSearch();
	
	public List<Bseg> getItems() {
		BsegDao d = (BsegDao)appContext.getContext().getBean("bsegDao");
		String cond = this.searchModel.getCondition();
		if(cond.length() == 0){
			cond = " 1 = 1 ";
		}
		this.items = d.dynamicFindBseg(cond);
		return items;
	}
	public void setItems(List<Bseg> items) {
		this.items = items;
	}
	
	public Bseg getSelected() {
		return selected;
	}
	public void setSelected(Bseg selected) {
		this.selected = selected;
	}
	
	public BsegSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(BsegSearch searchModel) {
		this.searchModel = searchModel;
	}
	
	public List<Bseg> getItemsByBelnr(Long belnr){
		this.searchModel.setBelnr(belnr);
		return this.getItems();
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
