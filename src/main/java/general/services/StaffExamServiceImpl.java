package general.services;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.MessageProvider;
import general.Validation;
import general.tables.StaffExam;
import general.dao.DAOException;
import general.dao.StaffExamDao;

@Service("staffExamService")
public class StaffExamServiceImpl implements StaffExamService {

	@Autowired
	private StaffExamDao dao;

	private String validate(StaffExam se) {
		MessageProvider mp = new MessageProvider();
		String error = "";
		if (Validation.isEmptyString(se.getTitle())) {
			error += MessageFormat.format(
					mp.getErrorValue("field_is_required"),
					mp.getValue("hr.exam.title"));
		}
		if (se.getStaff_id() == 0L) {
			error += MessageFormat.format(
					mp.getErrorValue("field_is_required"),
					mp.getValue("hr.exam.staff_id"));
		}

		se.setCreated_date(Calendar.getInstance().getTime());
		
		return error;
	}

	@Override
	public void create(List<StaffExam> seList) throws DAOException {
		String error = "";
		for (StaffExam se : seList) {
			error += validate(se);
			if (error.length() > 0) {
				throw new DAOException(error);
			}
		}

		for (StaffExam se : seList) {
			dao.create(se);
		}
	}
}
