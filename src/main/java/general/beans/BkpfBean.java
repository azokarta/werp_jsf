package general.beans;

import general.AppContext;
import general.dao.BkpfDao;
import general.tables.Bkpf;
import general.tables.search.BkpfSearch;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="bkpfBean")
@ViewScoped
public class BkpfBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Bkpf> items;
	private Bkpf selected;
	private BkpfSearch searchModel = new BkpfSearch();
	
	public List<Bkpf> getItemsByBlart(String s){
		searchModel.setStorno(0);
		searchModel.setBlart(s);
		return this.getItems();
	}
	
	public BkpfSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(BkpfSearch searchModel) {
		this.searchModel = searchModel;
	}

	public List<Bkpf> getItems() {
		BkpfDao d = (BkpfDao)appContext.getContext().getBean("bkpfDao");
		String cond = searchModel.getCondition();
		if(cond.length() == 0){
			cond = " 1 = 1 ";
		}
		this.items = d.dynamicFindBkpf(cond);
		return items;
	}



	public Bkpf getSelected() {
		return selected;
	}
	public void setSelected(Bkpf selected) {
		this.selected = selected;
	}



	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
