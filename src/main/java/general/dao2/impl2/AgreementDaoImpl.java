package general.dao2.impl2;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.DAOException;
import general.dao.impl.GenericDaoImpl;
import general.dao2.AgreementDao;
import general.tables2.Agreement;

@Component("agreementDao")
public class AgreementDaoImpl extends GenericDaoImpl<Agreement> implements AgreementDao{
	public List<Agreement> findListByUser(Long a_userId, String al_status) throws DAOException
	{	
		try
		{
			String s1 = "";
			s1="SELECT A "
					+" FROM Prikaz p,Agreement a where (p.branch_id in (select u.branch_id from User u where u.user_id="+a_userId+") or p.branch_id=0) and p.status_id in ("+al_status+")"					
					+" and (p.position_id = 0 or p.position_id in (select sa.position_id from Salary sa, User u where sa.staff_id=u.staff_id"
					+" and sa.beg_date <= to_date(sysdate) and sa.end_date>=to_date(sysdate)"
					+" and u.user_id="+a_userId+"))"
					+" and a.context_id=p.id_prikaz and a.context_a='PRIKAZ' order by date_prikaz";
			Query query = this.em.createQuery(s1);
			List<Agreement> results = query.getResultList();
			return results;
			
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	public List<Agreement> findListByUserReadyToAgree(Long a_userId) throws DAOException
	{	
		try
		{
			String s1 = "";
			s1="Select a2 FROM Agreement a2 where a2.context_id in (SELECT a.context_id "
					+" FROM Prikaz p,Agreement a where "
					+" a.user_id="+a_userId+" and a.context_id=p.id_prikaz and a.context_a='PRIKAZ' and p.status_id=1 and a.current_a=1 and a.status_id=1)";
			Query query = this.em.createQuery(s1);
			List<Agreement> results = query.getResultList();
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
					.createQuery("delete FROM Agreement a where context_id = :context_id");
			query.setParameter("context_id", a_id_prikaz);
			int answer = query.executeUpdate();
			if (answer==0)
			{
				throw new DAOException("Could not delete Agreement");
			}
			else 
				return answer;
		}
		catch (DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	
		
	}
}
