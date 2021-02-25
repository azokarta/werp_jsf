package general.services;

import java.util.List;

import general.dao.DAOException;
import general.tables.Bkpf;
import general.tables.MatnrListSold;
import general.tables.MatnrMovement;
import general.tables.MatnrReturned;

import org.springframework.transaction.annotation.Transactional;

public interface MatnrReturnedService {

	@Transactional
	public void create(Bkpf bkpf, MatnrMovement mm,List<MatnrListSold> mlList, String transactionCode) throws DAOException;

	@Transactional
	public void update(MatnrReturned mr,List<MatnrListSold> mlList) throws DAOException;
}
