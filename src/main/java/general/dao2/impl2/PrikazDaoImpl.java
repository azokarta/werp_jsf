package general.dao2.impl2;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.DAOException;
import general.dao.impl.GenericDaoImpl;
import general.dao2.PrikazDao;
import general.tables2.Agreement;
import general.tables2.MessageAttach;
import general.tables2.Prikaz;


@Component("prikazDao")
public class PrikazDaoImpl extends GenericDaoImpl<Prikaz> implements PrikazDao{
	public List<Object[]> findListByUser(Long a_userId, String al_status, String a_dynClause) throws DAOException
	{	
		try
		{
			String s1 = "";
			s1="SELECT p.id_prikaz,"
					+" p.type_prikaz,"
					+" to_char(p.date_prikaz, 'DD.MM.YYYY'),"
					+" p.name_prikaz,"
					+" p.bukrs,"
					+" p.position_id,"
					+" p.status_id,"
					+" p.parent_id_prikaz,"
					+" p.creator_id,"
					+" p.version,"
					+" p.code,"
					+" p.branch_id,"
					+" p.department_id"
					+" FROM PRIKAZ p where (p.branch_id in (select u.branch_id from user_table u where u.user_id="+a_userId+") or p.branch_id=0) and p.status_id in ("+al_status+")"					
					+" and (p.position_id = 0 or p.position_id in (select sa.position_id from salary sa, user_table u where sa.staff_id=u.staff_id"
					+" and sa.beg_date <= to_date(sysdate) and sa.end_date>=to_date(sysdate)"
					+" and u.user_id="+a_userId+"))"
					+a_dynClause
					+" order by date_prikaz";
			Query query = this.em.createNativeQuery(s1);
			List<Object[]> results = query.getResultList();
			return results;
			
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	public List<Object[]> findListByUserReadyToAgree(Long a_userId) throws DAOException
	{	
		try
		{
			String s1 = "";
			s1="SELECT p.id_prikaz,"
					+" p.type_prikaz,"
					+" to_char(p.date_prikaz, 'DD.MM.YYYY'),"
					+" p.name_prikaz,"
					+" p.bukrs,"
					+" p.position_id,"
					+" p.status_id,"
					+" p.parent_id_prikaz,"
					+" p.creator_id,"
					+" p.version,"
					+" p.code,"
					+" p.branch_id,"
					+" p.department_id"
					+" FROM Prikaz p,Agreement a where "
					+" a.user_id="+a_userId+" and a.context_id=p.id_prikaz and a.context_a='PRIKAZ' and p.status_id=1 and a.current_a=1 and a.status_id=1";
			Query query = this.em.createNativeQuery(s1);
			List<Object[]> results = query.getResultList();
			return results;
			
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	@Override
	public List<Prikaz> findAll(String condition) {
		String s = "SELECT p FROM Prikaz p ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		
		Query query = this.em.createQuery(s);
		List<Prikaz> l =  query.getResultList();
		return l;
	}
}
