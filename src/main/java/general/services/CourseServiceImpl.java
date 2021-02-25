package general.services;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.dao.CourseDao;
import general.tables.Course;
import general.dao.DAOException;

@Service("courseService")
public class CourseServiceImpl implements CourseService {
	
	@Autowired
	private CourseDao cDao;


	@Override
	public void create(Course c) throws DAOException {
		String error = validate(c, true);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		cDao.create(c);
	}
	
	private String validate(Course c,boolean isNew)
	{
		String error = "";
		if(c.getName_ru().length() == 0)
		{
			error += "Заполните наименование (рус)" + "\n";
		}
		if(isNew){
			c.setCreated_date(Calendar.getInstance().getTime());
		}
		c.setUpdated_date(Calendar.getInstance().getTime());
		
		return error;
	}
	
	@Override
	public void update(Course c) throws DAOException {
		String error = validate(c, false);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		cDao.update(c);
	}
}
