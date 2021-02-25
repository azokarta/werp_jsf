package reference;

import java.util.ArrayList;
import java.util.List;

import general.AppContext;
import general.MessageController;
import general.dao.CountryDao;
import general.dao.DAOException;
import general.tables.Country;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
@ManagedBean(name="refCountry",eager=true)
@ViewScoped
public class RefCountry
{
	private final static Long transaction_id = (long) 65;
	private List<Country> countryList = new ArrayList<Country>();
	private Country newCountry = new Country();
	public Country getNewCountry() {
		return newCountry;
	}

	public void setNewCountry(Country newCountry) {
		this.newCountry = newCountry;
	}

	private String countryName;
	private Country selectedCountry = new Country();
	
	@PostConstruct
	public void init(){
		try{
			System.out.println("INIT " + this.getClass());
			this.loadCountry();
			System.out.println(countryList.size());
		}
		catch(Exception e){
			MessageController.getInstance().addError(e.getMessage());
		}
	}
	
	public String getCountry() {
		return countryName;
	}

	public void setCountry(String countryName) {
		this.countryName = countryName;
	}

	public Country getSelectedCountry() {
		return selectedCountry;
	}

	public void setSelectedCountry(Country selectedCountry) {
		this.selectedCountry = selectedCountry;
		System.out.println("COUNTRY " + selectedCountry.getCountry());
	}

	public List<Country> getCountryList() {
		return countryList;
	}
	
	

	public void search()
	{
		System.out.println(countryList.size());
		this.loadCountry();
		System.out.println(countryList.size());
	}
	
	public void setCountryList(List<Country> countryList) {
		this.countryList = countryList;
	}

	public void create(){
		
	}
	
	public void update(){
		
	}
	
	private void loadCountry(){
		try{
		CountryDao countryDao = (CountryDao)appContext.getContext().getBean("countryDao");
		countryList = new ArrayList<Country>();
		if(countryName.length() > 0)
		{
			countryList = countryDao.findAll();
		}
		else
		{
			countryList = countryDao.findAll();
		}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
