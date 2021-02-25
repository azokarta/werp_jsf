package hr.bonus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import general.GeneralUtil;
import general.MessageProvider;
import general.PermissionController;
import general.dao.DAOException; 
import general.services.BonusArchiveService;
import general.services.BonusService;
import general.tables.Bonus;
import general.tables.BusinessArea;
import general.tables.Matnr;
import general.tables.Position;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "hrb02Bean", eager = true)
@ViewScoped
public class Hrb02 extends HrbBase implements Serializable
{
	private static final long serialVersionUID = -8229010388879293264L;
	private final static String transaction_code = "HRB02";
	final static Long transactionId = 25L;
	private boolean canEdit = false;
	
	public boolean getCanEdit()
	{
		return canEdit;
	}
	
	@PostConstruct
	public void init(){
		PermissionController.canRead(userData, transactionId);
	}

	List<Bonus> bonusList = new ArrayList<Bonus>();		
	Bonus searchBonus = new Bonus();
	public List<Bonus> getBonusList() {
		return bonusList;
	}
	
	public Bonus getSearchBonus() {
		return searchBonus;
	}

	public void setSearchBonus(Bonus b) {
		this.searchBonus = b;
	}
 
	public void loadBusinessArea(){
		bonusList = new ArrayList<Bonus>();
		businessAreaList =  new ArrayList<BusinessArea>(); 
		positionList = new ArrayList<Position>();
		for (BusinessArea wa_businessArea : businessAreaF4Bean.getBusinessArea_list()) {
			if (searchBonus.getBukrs().equals(wa_businessArea.getBukrs())){
				businessAreaList.add(wa_businessArea);
			}			
		}
	}
	//*****************************************************************
	//***************************To save method************************
	public void save(){
		try {
			PermissionController.canWrite(userData, transactionId);
			if (bonusList.size() == 0){
				throw new DAOException("No bonus to save");
			}
			for(Bonus wa_bonus: bonusList){ 
				if (wa_bonus.getBukrs() == null || wa_bonus.getBukrs().equals("0") || wa_bonus.getBukrs().isEmpty()){
	    			wa_bonus.setBukrs(searchBonus.getBukrs());
	    		}
	    		 
				if (wa_bonus.getCountry_id() == null || wa_bonus.getCountry_id() == 0)
	    		{   
					wa_bonus.setCountry_id(searchBonus.getCountry_id());
	    		} 
				if (wa_bonus.getBusiness_area_id() == null || wa_bonus.getBusiness_area_id() == 0)
	    		{ 
					wa_bonus.setBusiness_area_id(searchBonus.getBusiness_area_id());  	    			
	    		} 
				if (wa_bonus.getYear() == 0)
	    		{ 
					wa_bonus.setYear(searchBonus.getYear());     
	    		} 
				if (wa_bonus.getMonth() == 0)
	    		{ 
					wa_bonus.setMonth(searchBonus.getMonth());   
	    		}
				if (wa_bonus.getBonus_type_id()==0){
					throw new DAOException("Bonus type not chosen");
				}
				if (wa_bonus.getPosition_id()==0){
					throw new DAOException("Position not chosen");
				}
				
			}
			Long count = (long) 0; 
			BonusArchiveService bonusArchiveService = (BonusArchiveService) appContext.getContext().getBean("bonusArchiveService");	
			count = bonusArchiveService.checkBonusExistence(searchBonus.getMonth(), searchBonus.getYear());
			 
			if (count>0){
				throw new DAOException("Bonus cannot be created or updated");
			}
			
			BonusService bonusService = (BonusService) appContext.getContext().getBean("bonusService");
			bonusService.createOrUpdateBonuses(bonusList);
			GeneralUtil.addSuccessMessage(new MessageProvider().getSuccessValue("changes_saved_success"));
		}
		catch (DAOException ex)
		{
			bonusList = new ArrayList<Bonus>();
			GeneralUtil.addErrorMessage(ex.getMessage()); 
		}
	}
	//*****************************************************************
	//***************************To search method**********************
	public void search(){
		try {  
			bonusList = new ArrayList<Bonus>(); 
			positionList = new ArrayList<Position>();
			BonusService bonusService = (BonusService) appContext.getContext().getBean("bonusService");	 
			bonusList = bonusService.dynamicSearch(searchBonus);
		}
		catch (DAOException ex)
		{
			bonusList = new ArrayList<Bonus>();
			GeneralUtil.addErrorMessage(ex.getMessage()); 
		}
	}

	@Override
	Long getTransactionId() {
		return this.transactionId;
	}
	
	@Override
	public String getBreadcrumb() {
		return super.getBreadcrumb() + " > Изменить бонус";
	}

	private Bonus currentBonus;
	public Bonus getCurrentBonus() {
		return currentBonus;
	}
	public void setCurrentBonus(Bonus currentBonus) {
		this.currentBonus = currentBonus;
	}
	
	public void assignMatnr(Matnr mtnr){
		if(this.currentBonus != null && mtnr != null){
			this.currentBonus.setMatnr(mtnr.getMatnr());
		}
	}
}