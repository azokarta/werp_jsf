package general.dao.impl;

import general.dao.StaffEducationDao;
import general.tables.StaffEducation;
import java.util.List;
import javax.persistence.Query;

import org.springframework.stereotype.Component;
@Component("staffEducationDao")
public class StaffEducationDaoImpl extends GenericDaoImpl<StaffEducation> implements StaffEducationDao{

	@Override
	public List<StaffEducation> findAll(String cond) {
		String s = "Select s FROM StaffEducation s";
		if(cond.length() > 0){
			s += " WHERE " + cond;
		}
		Query q = this.em.createQuery(s);
		return q.getResultList();
	}
	
}
