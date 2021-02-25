package general.services;
 
import java.util.List;

import general.dao.DAOException; 
import general.output.tables.FaeaOutputTable;
import general.output.tables.FosaResultTable;
import general.tables.Payroll;
import general.tables.TempPayroll;

import org.springframework.transaction.annotation.Transactional;

public interface PayrollService {
	@Transactional
	List<TempPayroll> applySalary (int a_monat, int a_gjahr, String a_bukrs) throws DAOException;
		
	@Transactional
	void changeTempPayroll(TempPayroll a_tp) throws DAOException;
	
	
	@Transactional
	public void createNew(Payroll a_prl,Long a_userId, boolean createFiDoc,String a_tcode, int a_type) throws DAOException;
	
	@Transactional
	public void saveFaea(List<FaeaOutputTable> rt_list,Long a_userId,String a_tcode) throws DAOException;
	
	@Transactional
	public void saveFosa(List<FosaResultTable> rt_list) throws DAOException;
}
