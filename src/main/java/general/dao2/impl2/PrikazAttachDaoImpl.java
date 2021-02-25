package general.dao2.impl2;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.DAOException;
import general.dao.impl.GenericDaoImpl;
import general.dao2.PrikazAttachDao;
import general.tables2.PrikazAttach;


@Component("prikazAttachDao")
public class PrikazAttachDaoImpl extends GenericDaoImpl<PrikazAttach> implements PrikazAttachDao{
	public List<PrikazAttach> findListByUser(Long a_userId, String al_status) throws DAOException
	{	
		try
		{
			String s1 = "";
			s1="SELECT pa "
					+" FROM Prikaz p,PrikazAttach pa where (p.branch_id in (select u.branch_id from User u where u.user_id="+a_userId+") or p.branch_id=0)  and p.status_id in ("+al_status+")"
					+" and p.id_prikaz=pa.id_prikaz"
					+" and (p.position_id = 0 or p.position_id in (select sa.position_id from Salary sa, User u where sa.staff_id=u.staff_id"
					+" and sa.beg_date <= to_date(sysdate) and sa.end_date>=to_date(sysdate)"
					+" and u.user_id="+a_userId+"))"
					+" order by date_prikaz";
			Query query = this.em.createQuery(s1);
			List<PrikazAttach> results = query.getResultList();
			return results;
			
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	@Override
	public int deleteByIdPrikaz(Long a_id_prikaz) throws DAOException{
		try
		{
			Query query = this.em
					.createQuery("delete FROM PrikazAttach ps where id_prikaz = :id_prikaz");
			query.setParameter("id_prikaz", a_id_prikaz);
			int answer = query.executeUpdate();
			if (answer!=1)
			{
				throw new DAOException("Could not delete PrikazAttach");
			}
			else 
				return 0;
		}
		catch (DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	
		
	}
}
