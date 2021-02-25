package general.services;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.tables.StaffCourse;
import general.dao.DAOException;
import general.dao.StaffCourseDao;

@Service("staffCourseService")
public class StaffCourseServiceImpl implements StaffCourseService {
	
	@Autowired
	private StaffCourseDao dao;

	@Override
	public void create(StaffCourse s) throws DAOException {
		String error = this.validate(s);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		dao.create(s);
	}
	
	private String validate(StaffCourse s){
		String error = "";
		if(s.getStaff_id() == 0L){
			error += "Сотрудник не выбран \n";
		}
		if(s.getCourse_id() == 0L){
			error += "Курс не выбран \n";
		}
		
		if(s.getBegin_date() == null){
			error += "Выберите дату начала \n";
		}
		
		if(s.getFinish_date() == null){
			//error += "Выберите дату начала \n";
		}
		
		s.setCreated_date(Calendar.getInstance().getTime());
		s.setUpdated_date(Calendar.getInstance().getTime());
		return error;
	}
}
