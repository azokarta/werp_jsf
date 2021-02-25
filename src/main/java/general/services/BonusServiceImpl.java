package general.services;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import general.Helper;
import general.MessageProvider;
import general.dao.BonusDao;
import general.dao.DAOException;
import general.tables.Bonus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("bonusService")
public class BonusServiceImpl implements BonusService {
	@Autowired
	private BonusDao bonDao;

	@Override
	public void createOrUpdateBonuses(List<Bonus> pl_bonus) throws DAOException {
		Calendar currDate = Calendar.getInstance();
		if (pl_bonus.size() == 0) {
			throw new DAOException("No bonus to save");
		}
		for (Bonus wa_bonus : pl_bonus) {
			if (wa_bonus.getCoef() == 0 && wa_bonus.getReq_value() == 0) {
				throw new DAOException("Coefficient cannot be 0");
			}
			if (wa_bonus.getBonus_type_id() == 0) {
				throw new DAOException("Bonus type not chosen");
			}
			if (wa_bonus.getPosition_id() == 0) {
				throw new DAOException("Position not chosen");
			}
			if (wa_bonus.getDeposit() == null) {
				wa_bonus.setDeposit(0D);
			}
		}
		for (Bonus wa_bonus : pl_bonus) {
			bonDao.update(wa_bonus);
		}
	}

	@Override
	public List<Bonus> dynamicSearch(Bonus a_bonus) throws DAOException {
		try {

			List<Bonus> p_bonus_list = new ArrayList<Bonus>();
			String dynamicWhere = "";
			if (a_bonus.getBukrs() != null && (!a_bonus.getBukrs().equals("0"))
					&& (!a_bonus.getBukrs().isEmpty())) {
				if (dynamicWhere.length() > 0)
					dynamicWhere = dynamicWhere + " and ";
				dynamicWhere = dynamicWhere + "bukrs = " + a_bonus.getBukrs();
			}

			if (a_bonus.getCountry_id() > 0) {
				if (dynamicWhere.length() > 0)
					dynamicWhere = dynamicWhere + " and ";
				dynamicWhere = dynamicWhere + "country_id = "
						+ a_bonus.getCountry_id();
			}

			if (a_bonus.getBusiness_area_id() > 0) {
				if (dynamicWhere.length() > 0)
					dynamicWhere = dynamicWhere + " and ";
				dynamicWhere = dynamicWhere + "business_area_id = "
						+ a_bonus.getBusiness_area_id();
			}
			if (a_bonus.getYear() > 0) {
				if (dynamicWhere.length() > 0)
					dynamicWhere = dynamicWhere + " and ";
				dynamicWhere = dynamicWhere + "gjahr = " + a_bonus.getYear();
			}
			if (a_bonus.getMonth() > 0) {
				if (dynamicWhere.length() > 0)
					dynamicWhere = dynamicWhere + " and ";
				dynamicWhere = dynamicWhere + "monat = " + a_bonus.getMonth();
			}
			if (a_bonus.getPosition_id() > 0) {
				if (dynamicWhere.length() > 0)
					dynamicWhere = dynamicWhere + " and ";
				dynamicWhere = dynamicWhere + "position_id = " + a_bonus.getPosition_id();
			}

			String regx = ";,.";
			char[] ca = regx.toCharArray();
			for (char c : ca) {
				dynamicWhere = dynamicWhere.replace("" + c, "");
			}
			// System.out.println(dynamicWhere);
			p_bonus_list = bonDao.dynamicFindBonuses(dynamicWhere);
			return p_bonus_list;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	private String validate(Bonus b) {
		MessageProvider mProvider = new MessageProvider();
		String error = "";
		if (b.getBukrs().isEmpty() || Integer.valueOf(b.getBukrs()) == 0) {
			error += MessageFormat.format(
					mProvider.getErrorValue("field_is_required"),
					mProvider.getValue("company")) + "\n";
		}

		if (b.getBusiness_area_id() == 0) {
			// error += "Выберите бизнес сферу \n";
		}
		if (b.getCountry_id() == 0) {
			error += MessageFormat.format(
					mProvider.getErrorValue("field_is_required"),
					mProvider.getValue("country")) + "\n";
		}

		if (b.getBonus_type_id() == 0) {
			error += MessageFormat.format(
					mProvider.getErrorValue("field_is_required"),
					mProvider.getValue("hr.bonus.bonus_type_id")) + "\n";
		}

		if (b.getPosition_id() == 0) {
			error += MessageFormat.format(
					mProvider.getErrorValue("field_is_required"),
					mProvider.getValue("hr.bonus.position_id")) + "\n";
		}

		if (b.getMatnr() == null || b.getMatnr().longValue() == 0L) {
			// error += "Выберите продукт \n";
		}

		if (b.getBonus_type_id() != 1L) {
			if (b.getTo_num() == 0) {
				error += MessageFormat.format(
						mProvider.getErrorValue("field_is_required"),
						mProvider.getValue("hr.bonus.from_num")) + "\n";
			}

			if (b.getFrom_num() == 0) {
				error += MessageFormat.format(
						mProvider.getErrorValue("field_is_required"),
						mProvider.getValue("hr.bonus.to_num")) + "\n";
			}
		}
//		if (b.getCoef() == 0) {
//			error += MessageFormat.format(
//					mProvider.getErrorValue("field_is_required"),
//					mProvider.getValue("hr.bonus.coef")) + "\n";
//		}

		if (b.getWaers() == null || b.getWaers().isEmpty()) {
			// error += "Выберите валюту \n";
		}

		if (b.getDeposit() == null || b.getDeposit() == 0) {
			b.setDeposit(0D);
		}
		return error;
	}

	public void createBonus(Bonus b) {
		String error = validate(b);
		b.setYear(Helper.getCurrentYear());
		b.setMonth(Helper.getCurrentMonth());
		if (error.equals("")) {
			bonDao.create(b);
		} else {
			throw new DAOException(error);
		}
	}

	public void updateBonus(Bonus b) {
		String error = validate(b);
		b.setYear(Helper.getCurrentYear());
		b.setMonth(Helper.getCurrentMonth());
		if (error.equals("")) {
			bonDao.update(b);
		} else {
			throw new DAOException(error);
		}
	}
}
