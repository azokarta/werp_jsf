package hr.bonus;

import general.GeneralUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import general.Helper;
import general.PermissionController;
import general.dao.BonusDao;
import general.dao.DAOException;
import general.services.BonusService;
import general.tables.Bonus;
import general.tables.BusinessArea;
import general.tables.Matnr;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "hrb01Bean", eager = true)
@ViewScoped
public class Hrb01 extends HrbBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2838697473546120709L;
	private final static String transaction_code = "HRB01";
	final static Long transactionId = 25L;

	@PostConstruct
	public void init() {
		PermissionController.canWrite(userData, transactionId);
	}

	public void loadBusinessArea() {
		businessAreaList = new ArrayList<BusinessArea>();
		for (BusinessArea ba : businessAreaF4Bean.getBusinessArea_list()) {
			if (bonus.getBukrs().equals(ba.getBukrs())) {
				businessAreaList.add(ba);
			}
		}
	}

	private Bonus bonus = new Bonus();

	public Bonus getBonus() {
		return bonus;
	}

	public void setBonus(Bonus b) {
		bonus = b;
	}

	public void create() {
		try {
			PermissionController.canWrite(userData, transactionId);
			BonusService bonusService = (BonusService) appContext.getContext()
					.getBean("bonusService");
			BonusDao bDao = (BonusDao) appContext.getContext().getBean(
					"bonusDao");
			List<Bonus> thisMonthBonuses = bDao.dynamicFindBonuses(String
					.format("month = %d AND year = %d",
							Helper.getCurrentMonth(), Helper.getCurrentYear()));

			boolean isFound = false;
			for (Bonus b : thisMonthBonuses) {
				if (b.getYear() == Helper.getCurrentYear()
						&& b.getMonth() == Helper.getCurrentMonth()
						&& b.getBukrs().equals(bonus.getBukrs())
						&& b.getBonus_type_id() == bonus.getBonus_type_id()
						&& b.getBusiness_area_id() == bonus
								.getBusiness_area_id()
						&& b.getFrom_num() == bonus.getFrom_num()
						&& b.getTo_num() == bonus.getTo_num()
						&& b.getPosition_id() == bonus.getPosition_id()
						&& b.getMatnr() == bonus.getMatnr()
						&& b.getCategory_id() == bonus.getCategory_id()
						&& b.getCountry_id() == bonus.getCountry_id()) {
					isFound = true;
				}
			}

			if (isFound == true) {
				throw new DAOException(
						"Бонус с такими параметрами уже добавлен в этом месяце");
			}
			bonusService.createBonus(bonus);
			GeneralUtil.addSuccessMessage("Bonus saved successfully");
			bonus = new Bonus();
		} catch (DAOException ex) {
			GeneralUtil.addErrorMessage(ex.getMessage());
		}
	}

	private Matnr selectedMatnr;

	public Matnr getSelectedMatnr() {
		return selectedMatnr;
	}

	public void setSelectedMatnr(Matnr selectedMatnr) {
		this.selectedMatnr = selectedMatnr;
	}

	public void assignMatnr(Matnr m) {
		if (m != null && this.bonus != null) {
			this.selectedMatnr = m;
			this.bonus.setMatnr(this.selectedMatnr.getMatnr());
		}
	}

	@Override
	Long getTransactionId() {
		return this.transactionId;
	}

	@Override
	public String getBreadcrumb() {
		return super.getBreadcrumb() + " > Новый бонус";
	}
}