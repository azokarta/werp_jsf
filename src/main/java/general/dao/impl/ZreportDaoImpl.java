package general.dao.impl;

import general.dao.DAOException;
import general.dao.ZreportDao;
import general.tables.Zreport;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;
@Component("zreportDao")
public class ZreportDaoImpl extends GenericDaoImpl<Zreport> implements ZreportDao{
	public List<Zreport> findAll() {
		Query query = this.em
				.createQuery("SELECT b FROM Zreport b"); 
		List<Zreport> l = query.getResultList();
		return l;
	}
	
	public List<Object[]> frep2Act(Long a_conNum) throws DAOException
	{	
		try
		{
			
			Query query = this.em
					.createNativeQuery("select br1.text45 branchname"
								+" ,con1.contract_number sn"
								+" ,to_date(con1.contract_date,'YYYY-MM-DD') condate"
								+" ,cus1.lastname || ' ' || cus1.firstname  || ' ' || cus1.middlename clientfio"
								+" ,cus1.name clientname"
								+" ,cus1.fiz_yur clienttype"
								+" ,st1.lastname || ' ' || st1.firstname  || ' ' || st1.middlename dealerfio"
								+" from branch br1, contract con1,customer cus1, staff st1"
								+" where br1.branch_id=con1.branch_id and con1.customer_id=cus1.customer_id"
								+" and con1.dealer=st1.staff_id and con1.contract_number="+a_conNum); 


			List<Object[]> results = query.getResultList();
			return results;
		}
		catch (Exception ex)
		{
			throw new DAOException(ex.getMessage());
		}
			
	}
	
}

	