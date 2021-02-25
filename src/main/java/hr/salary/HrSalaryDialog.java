package hr.salary;

import general.AppContext;
import general.dao.SalaryDao;
import general.tables.Salary;
import general.tables.Staff;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import datamodels.SalaryModel;

@ManagedBean(name = "hrSalaryDialog")
@ViewScoped
public class HrSalaryDialog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1010356056340066127L;	
	
	public void initBean() {
		//System.out.println("INITING BEAN ....");
		salaryModel = new SalaryModel(getSalaryDao());
		salaryModel.getSearchModel().setCurrent(true);
	}
	
	private Salary selected;
	public Salary getSelected() {
		return selected;
	}

	public void setSelected(Salary selected) {
		this.selected = selected;
	}
	

	List<Salary> items;
	public List<Salary> getItems() {
		return items;
	}
	
	public void loadItems(){
		SalaryDao sDao = (SalaryDao)appContext.getContext().getBean("salaryDao");
		//sDao.findAllWithStaff(cond, first, pageSize);
	}
	
	private SalaryModel salaryModel;
	public SalaryModel getSalaryModel() {
		return salaryModel;
	}
	public void setSalaryModel(SalaryModel salaryModel) {
		this.salaryModel = salaryModel;
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	public AppContext getAppContext() {
		return appContext;
	}
	
	private SalaryDao getSalaryDao() {
		return (SalaryDao) appContext.getContext().getBean("salaryDao");
	}

	public void Reset(){
		this.selected = null;
	}
	
	public void Search(){
		selected = null;
	}
}
