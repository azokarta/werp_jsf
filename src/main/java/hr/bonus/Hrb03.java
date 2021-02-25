package hr.bonus;

import java.io.Serializable;
import java.util.ArrayList; 
import java.util.List; 

import general.GeneralUtil;
import general.PermissionController;
import general.dao.BonusDao;
import general.dao.DAOException;
import general.tables.Bonus;
import general.tables.search.BonusSearch;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "hrb03Bean", eager = true)
@ViewScoped
public class Hrb03 extends HrbBase implements Serializable {

	private static final long serialVersionUID = 3007713126846171188L;
	private final static String transaction_code = "HRB03";
	final static Long transactionId = 27L;
	
	private BonusSearch searchModel = new BonusSearch();
	public BonusSearch getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(BonusSearch searchModel) {
		this.searchModel = searchModel;
	}

	@PostConstruct
	public void init(){
		if(!GeneralUtil.isAjaxRequest()){
			PermissionController.canRead(userData, transactionId);
		}
	}
	
	List<Bonus> bonusList = new ArrayList<Bonus>();		
	public List<Bonus> getBonusList() {
		return bonusList;
	}
	
	public void Search(){
		try{
			bonusList.clear();
			BonusDao bonusDao = (BonusDao)appContext.getContext().getBean("bonusDao");
			String cond = this.searchModel.getCondition();
			if(cond.length() == 0){
				throw new DAOException("Задайте параметры поиска");
			}
			cond = cond.length() > 0 ? cond : " 1 = 1 ";
			bonusList = bonusDao.dynamicFindBonuses(cond);
		}catch(DAOException e){
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}
	
	@Override
	Long getTransactionId() {
		return transactionId;
	}
}

	 
		
