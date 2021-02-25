package general.services;

import general.dao.BkpfDao;
import general.dao.BsegDao;
import general.dao.DAOException;
import general.output.tables.Podotchet;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service("financePodotchet")
public class FinancePodotchetImpl implements FinancePodotchet{
	@Autowired
    private BkpfDao bkpfDao;
	
	@Autowired
    private BsegDao bsegDao;
	public List<Podotchet> getPodotchetList(String a_bukrs, Long a_branch_id, String a_waers) throws DAOException 
	{
		try
		{
			List<Podotchet> l_p = new ArrayList<Podotchet>();
			if (a_bukrs!=null && !a_bukrs.equals("0") && a_branch_id!=null && a_branch_id>0 && a_waers!=null && !a_waers.equals("0"))
			{
				l_p = bsegDao.findPodocthetByBukrBranchWaers(a_bukrs, a_branch_id, a_waers);
			}
			
			return l_p;
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
}
