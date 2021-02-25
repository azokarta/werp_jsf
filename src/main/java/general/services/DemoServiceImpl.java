package general.services;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

import general.MessageProvider;
import general.Validation;
import general.dao.DAOException;
import general.dao.DemoDao;
import general.tables.Demonstration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("demoService")
public class DemoServiceImpl implements DemoService {

	@Autowired
	DemoDao demoDao;

	@Override
	public void create(List<Demonstration> demoList) throws DAOException {
		String error = "";
		for (Demonstration d : demoList) {
			error += validate(d, null, true);
			if (error.length() > 0) {
				throw new DAOException(error);
			}
		}

		for (Demonstration d : demoList) {
			demoDao.create(d);
		}
	}

	private String validate(Demonstration demo, Long userId, boolean isNew) {
		MessageProvider mp = new MessageProvider();
		String error = "";
		if (Validation.isEmptyString(demo.getBukrs())) {
			error += "Выберите компанию \n";
		}

		if (Validation.isEmptyLong(demo.getBranchId())) {
			error += "Выберите филиал \n";
		}
		if (Validation.isEmptyLong(demo.getDealerId())) {
			error += MessageFormat.format(mp.getErrorValue("field_is_required"), mp.getValue("dms.demo.dealer_id"));
		}
		if (Validation.isEmptyLong(demo.getDemosecId())) {
			// error += MessageFormat.format(
			// mp.getErrorValue("field_is_required"),
			// mp.getValue("dms.demo.demosec_id"));
			demo.setDemosecId(0L);
		} else {

		}

		if (Validation.isEmptyString(demo.getCustomerName())) {
			error += MessageFormat.format(mp.getErrorValue("field_is_required"), mp.getValue("dms.demo.customer_name"));
		}

		if (Validation.isEmptyString(demo.getCustomerAddress())) {
			error += MessageFormat.format(mp.getErrorValue("field_is_required"),
					mp.getValue("dms.demo.customer_address"));
		}

		if (Validation.isEmptyString(demo.getCustomerMobile())) {
			error += MessageFormat.format(mp.getErrorValue("field_is_required"),
					mp.getValue("dms.demo.customer_mobile"));
		}

		// if (demo.getStatusId() == 0) {
		// error += MessageFormat.format(mp.getErrorValue("field_is_required"),
		// mp.getValue("dms.demo.status"));
		// }

		if (isNew) {
			demo.setCreatedAt(Calendar.getInstance().getTime());
			demo.setCreatedBy(userId);
		}

		demo.setUpdatedAt(Calendar.getInstance().getTime());
		demo.setUpdatedBy(userId);

		if (demo.getLocation() == null || demo.getLocation() == 0) {
			error += MessageFormat.format(mp.getErrorValue("field_is_required"), mp.getValue("dms.demo.location"));
		}

		return error;
	}

	@Override
	public void update(Demonstration demo, Long userId) throws DAOException {
		String error = validate(demo, userId, false);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		demoDao.update(demo);
	}

	@Override
	public void create(Demonstration demo, Long userId) throws DAOException {
		String error = validate(demo, userId, true);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		demoDao.create(demo);
	}

}
