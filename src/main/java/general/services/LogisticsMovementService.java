package general.services;

import java.util.List;
import java.util.Map;

import general.dao.DAOException;
import general.tables.Bkpf;
import general.tables.MatnrList;
import general.tables.MatnrMovement;

import org.springframework.transaction.annotation.Transactional;


public interface LogisticsMovementService{
	
	@Transactional
	public void create(MatnrMovement mm, List<MatnrList> mlList,String tCode,Long branchId,List<Bkpf> findocList,Map<Long, List<String>> barcodeMap) throws DAOException;
	
	@Transactional
	public void receive(MatnrMovement mm,MatnrMovement receivedMovement, List<MatnrList> mlList,String tCode,Long branchId,Map<Long, List<String>> barcodeMap) throws DAOException;
}