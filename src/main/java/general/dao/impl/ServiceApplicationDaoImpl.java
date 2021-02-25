package general.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.Validation;
import general.dao.DAOException;
import general.dao.ServiceApplicationDao;
import general.output.tables.ServAppReportOutput;
import general.tables.ServiceApplication;
import general.tables.report.SalesReportOutput;
@Component("servAppDao")
public class ServiceApplicationDaoImpl extends GenericDaoImpl<ServiceApplication> implements ServiceApplicationDao {

	@Override
	public ServiceApplication findByNumber(Long a_number) {
		ServiceApplication resultSA = null;
		Query q = this.em.createQuery(String.format("SELECT sa FROM ServiceApplication sa WHERE sa.app_number = '%d'", a_number));
		List results = q.getResultList();
		if(!results.isEmpty())
		{
			resultSA = (ServiceApplication) results.get(0);
		}
		
		return resultSA;
	}
	
	@Override
	public List<ServiceApplication> findAll() {
		Query q = this.em.createQuery(String.format("SELECT sa FROM ServiceApplication sa"));
		List<ServiceApplication> results = q.getResultList();
		
		return results;
	}
	
	@Override
	public List<ServiceApplication> dynamicFindAll(String wcl) {
		Query q = this.em.createQuery(String.format("SELECT sa FROM ServiceApplication sa WHERE " + wcl));
		List<ServiceApplication> results = q.getResultList();
		
		return results;
	}
	
	@Override
	public List<ServiceApplication> findAll(String condition, int first, int max) {
		
		String s = " SELECT c FROM ServiceApplication c ";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		q.setFirstResult(first);
		q.setMaxResults(max);
		List<ServiceApplication> l = q.getResultList();
		
		return l;
	}
	
	@Override
	public int getRowCount(String condition) {
		String s = "select COUNT(c.id) FROM ServiceApplication c ";
		if(!Validation.isEmptyString(condition)){
			s += " WHERE " + condition;
		}
		Query query = this.em.createQuery(s);        
        return ((Long) query.getSingleResult()).intValue();
	}
	
	@Override
	public List<ServAppReportOutput> getSAReportBranch(String queryScript, String table) throws DAOException {
		try
		{
			List<ServAppReportOutput> outputTable = new ArrayList<ServAppReportOutput>();
			
			if (!Validation.isEmptyString(queryScript))
			{
				//System.out.println(queryScript);
			   	Query query = this.em .createNativeQuery(queryScript);
			   	//System.out.println("Table: " + table);
			   	//query.setParameter("table", table);
			   	
		    	List<Object[]> results = query.getResultList();
		    	//System.out.println("Results: " + results.size());
		    	
		    	int i = 0;
		     	for (Object[] result : results) {
		    		i++;
		    		ServAppReportOutput wa_sr = new ServAppReportOutput(i);
					//wa_fot.setIndex(index);
		     		
		     		if (result[0]!=null) wa_sr.getBranch().setBranch_id(Long.parseLong(String.valueOf(result[0])));
		     		if (result[1]!=null) wa_sr.getBranch().setText45(String.valueOf(result[1]));
		     		
		     		if (result[2]!=null) wa_sr.setRserv(Integer.parseInt(String.valueOf(result[2])));
		     		if (result[3]!=null) wa_sr.setCserv(Integer.parseInt(String.valueOf(result[3])));
		     		if (result[4]!=null) wa_sr.setProf(Integer.parseInt(String.valueOf(result[4])));
		     		if (result[5]!=null) wa_sr.setZf(Integer.parseInt(String.valueOf(result[5])));
		     		if (result[6]!=null) wa_sr.setOthers(Integer.parseInt(String.valueOf(result[6])));
		     		if (result[7]!=null) wa_sr.setComplain(Integer.parseInt(String.valueOf(result[7])));
		     		if (result[8]!=null) wa_sr.setTotal(Integer.parseInt(String.valueOf(result[8])));
					
		     		outputTable.add(wa_sr);
				}
			}
			
	    	return outputTable;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	
	public List<ServAppReportOutput> getSAReportStaff(String queryScript, String table) throws DAOException {
		try {
			List<ServAppReportOutput> outputTable = new ArrayList<ServAppReportOutput>();
			
			if (!Validation.isEmptyString(queryScript))
			{
				//System.out.println("Staff query: ");
				//System.out.println(queryScript);
			   	Query query = this.em .createNativeQuery(queryScript);
			   	//query.setParameter("table", table);
			   	
		    	List<Object[]> results = query.getResultList();
		    	
		    	int i = 0;
		     	for (Object[] result : results) {
		    		i++;
		    		ServAppReportOutput wa_sr = new ServAppReportOutput(i);
					//wa_fot.setIndex(index);
		     		
		    		if (result[0]!=null) wa_sr.getBranch().setBranch_id(Long.parseLong(String.valueOf(result[0])));
		     		if (result[1]!=null) wa_sr.getBranch().setText45(String.valueOf(result[1]));
		     		if (result[2]!=null) wa_sr.setRserv(Integer.parseInt(String.valueOf(result[2])));
		     		
		     		if (result[3]!=null) wa_sr.setCserv(Integer.parseInt(String.valueOf(result[3])));
		     		if (result[4]!=null) wa_sr.setProf(Integer.parseInt(String.valueOf(result[4])));
		     		if (result[5]!=null) wa_sr.setZf(Integer.parseInt(String.valueOf(result[5])));
		     		if (result[6]!=null) wa_sr.setOthers(Integer.parseInt(String.valueOf(result[6])));
		     		if (result[7]!=null) wa_sr.setComplain(Integer.parseInt(String.valueOf(result[7])));
		     		if (result[8]!=null) wa_sr.setTotal(Integer.parseInt(String.valueOf(result[8])));
					
		     		outputTable.add(wa_sr);
				}
			}
			
	    	return outputTable;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	
	public List<Object[]> dynamicSerrep1(String a_dynamicWhere) throws DAOException
	{	
		try
		{
			 		
			String sel = "";

			sel = "select sa.created_by,initcap(st.lastname)||' '||initcap(st.firstname) fio,count(sa.id) kol,sa.app_type,sa.app_status"
				+ " from service_application sa,user_table ut, staff st"
				+ " where sa.created_by=ut.user_id and st.staff_id=ut.staff_id"
				+ " "+ a_dynamicWhere
				+ " group by sa.created_by,initcap(st.lastname)||' '||initcap(st.firstname),sa.app_type,sa.app_status "
				+ " order by fio";
				
						
			Query query = this.em.createNativeQuery(sel);
			List<Object[]> results = query.getResultList();
			
			return results;

		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
	


}