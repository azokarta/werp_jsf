package dit.eventlog;

import general.AppContext;
import general.GeneralUtil;
import general.dao.EventLogDao;
import general.tables.search.EventLogSearch;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import datamodels.EventlogModel;

@ManagedBean(name="ditEventLog")
@ViewScoped
public class DitEventLog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7076275953654224380L;

	@PostConstruct
	public void init(){
		if(!GeneralUtil.isAjaxRequest()){
			EventLogDao elDao = (EventLogDao)appContext.getContext().getBean("eventLogDao");
			elModel = new EventlogModel(elDao);
			System.out.println("INIT...");
		}
	}
	
	private EventLogSearch searchModel = new EventLogSearch();
	public EventLogSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(EventLogSearch searchModel) {
		this.searchModel = searchModel;
	}
	
	private EventlogModel elModel;
	public EventlogModel getElModel() {
		return elModel;
	}
	public void setElModel(EventlogModel elModel) {
		this.elModel = elModel;
	}
	
	public void Search(){
		
	}

	@ManagedProperty("#{appContext}")
	AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
