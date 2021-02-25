package general.services;

import java.text.MessageFormat;
import java.util.Calendar;

import general.MessageProvider;
import general.Validation;
import general.dao.DAOException;
import general.dao.StaffOfficialDataDao;
import general.tables.StaffOfficialData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("staffOfficialDataService")
public class StaffOfficialDataServiceImpl implements StaffOfficialDataService {

	@Autowired
	private StaffOfficialDataDao dao;

	@Override
	public void create(StaffOfficialData d) throws DAOException {
		String error = this.validate(d, true);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		dao.create(d);
	}

	private String validate(StaffOfficialData d, boolean isNew) {
		MessageProvider messageProvider = new MessageProvider();
		String error = "";
		
		if (Validation.isEmptyString(d.getBukrs())) {
			error += "Выберите компанию" + "\n";
		}
		
		if (d.getSalary() == 0D) {
			error += MessageFormat.format(
					messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.official_data.salary")) + "\n";
		}

		if (d.getCurrency().isEmpty()) {
			error += MessageFormat.format(
					messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.official_data.currency")) + "\n";
		}

		if (d.getSub_company_id() == 0L) {
			error += MessageFormat.format(
					messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.official_data.sub_company_id")) + "\n";
		}

		if (d.getSalary_type().isEmpty()) {
			error += MessageFormat.format(
					messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.official_data.salary_type")) + "\n";
		}

		if (d.getPension() == 0D) {
//			error += MessageFormat.format(
//					messageProvider.getErrorValue("field_is_required"),
//					messageProvider.getValue("hr.official_data.pension")) + "\n";
		}

		if (d.getStaff_id() == 0L) {
			error += MessageFormat.format(
					messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("hr.official_data.staff_id")) + "\n";
		}

		if (isNew) {
			d.setCreated_date(Calendar.getInstance().getTime());
		}
		d.setUpdated_date(Calendar.getInstance().getTime());

		return error;
	}

	@Override
	public void update(StaffOfficialData d) throws DAOException {
		String error = this.validate(d, false);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		dao.update(d);
	}
}
