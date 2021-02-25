package general.springservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.Helper;
import general.Validation;
import general.dao.DAOException;
import general.dao2.KpiSettingDao;
import general.tables.KpiItem;
import general.tables.KpiSetting;

@Service("kpiSettingService")
public class KpiSettingServiceImpl implements KpiSettingService {

	@Autowired
	KpiSettingDao kpiDao;

	@Override
	public void create(KpiSetting entity, Long userId) throws DAOException {
		validate(entity, userId);
		kpiDao.create(entity);
	}

	private void validate(KpiSetting entity, Long userId) throws DAOException {

		if (entity.isNew()) {
			entity.setCreatedAt(Calendar.getInstance().getTime());
			entity.setCreatedBy(userId);
		} else {

		}

		entity.setUpdatedAt(Calendar.getInstance().getTime());
		entity.setUpdatedBy(userId);

		String error = "";
		if (Validation.isEmptyString(entity.getBukrs())) {
			error += "Выберите компанию \n";
		}

		if (Validation.isEmptyLong(entity.getPositionId())) {
			error += "Выберите должность \n";
		}

		if (entity.getFromDate() == null) {
			error += "Введите дату начала \n";
		}

		List<KpiItem> items = entity.getKpiItems();
		if (Validation.isEmptyCollection(items)) {
			error += "Список индикаторов пуст! \n";
		} else {
			double d = 0D;
			for (KpiItem item : items) {
				d += item.getPoint();
			}

			if (d != 100) {
				error += "Сумма балов должно быть равно 100";
			}
		}

		if (error.length() == 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String cond = String.format(
					"bukrs = %s AND branch_id=%d AND position_id=%d AND (to_date IS NULL OR to_date >= '%s')",
					entity.getBukrs(), entity.getBranchId(), entity.getPositionId(), sdf.format(entity.getFromDate()));
			if (!entity.isNew()) {
				cond += " AND id != " + entity.getId();
			}

			// System.out.println("COND: " + cond);

			List<KpiSetting> temp = kpiDao.findAll(cond);
			if (!Validation.isEmptyCollection(temp)) {
				error += "Настройки по такой критерии существует, закройте их! \n";
			}
		}

		if (error.length() > 0) {
			throw new DAOException(error);
		}
	}

	@Override
	public void update(KpiSetting entity, Long userId) throws DAOException {
		validate(entity, userId);
		kpiDao.update(entity);
	}
}
