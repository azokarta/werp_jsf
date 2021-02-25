package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.MatnrListDao;
import general.dao.MatnrListSoldDao;
import general.dao.MatnrMovementDao;
import general.dao.MatnrMovementItemDao;
import general.dao.MatnrReturnedDao;
import general.tables.Bkpf;
import general.tables.MatnrList;
import general.tables.MatnrListSold;
import general.tables.MatnrMovement;
import general.tables.MatnrMovementItem;
import general.tables.MatnrReturned;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("matnrReturnedService")
public class MatnrReturnedServiceImpl implements MatnrReturnedService{
	
	@Autowired
    private MatnrReturnedDao mrDao;
	
	@Autowired
	ContractService contractService;
	
	@Autowired
	MatnrListSoldDao mlsDao;
	
	@Autowired
	MatnrListDao mlDao;
	
	@Autowired
	MatnrMovementDao mmDao;
	
	@Autowired
	MatnrMovementItemDao mmiDao;
	
	@Autowired
	FinanceServiceLogistics fServiceLogistics;

	@Override
	public void create(Bkpf bkpf, MatnrMovement mm,List<MatnrListSold> mlsList, String transactionCode) throws DAOException {
		// TODO Auto-generated method stub
		String error = valiadte(mm,true);
		if(mlsList.size() == 0){
			error += " Выберите материалов для возврата " + "\n";
		}
		if(error.length() > 0){
			throw new DAOException(error);
		}
		//Long awkey = fServiceLogistics.removeMatnrFromWerks(mlsList, bkpf, mlsList.get(0).getBukrs(), mr.getCreated_by(), bkpf.getBrnch(), transactionCode,mr.getTo_werks());
		Long awkey = null;//fServiceLogistics.returnMatnrToWerks(mlsList, bkpf, mlsList.get(0).getBukrs(), mm.getCreated_by(), bkpf.getBrnch(), transactionCode, mm.getTo_werks());
		
		/*********START Matnr Movement***********/
		mm.setAwkey(awkey);
		mm.setFrom_werks(0L);
		mm.setMm_type(MatnrMovement.TYPE_RETURN);
		mm.setStatus(MatnrMovement.STATUS_RECEIVED);
		mm.setTo_werks(mm.getTo_werks());
		mmDao.create(mm);
		
		/*********END Matnr Movement***********/
		MatnrList newMl;
		List<Long> matnrListForContract = new ArrayList<Long>();
		for(MatnrListSold mls:mlsList){
			newMl = mlDao.find(mls.getMatnr_list_id());
			if(newMl == null){
				continue;
			}
			newMl.setAwkey(mls.getAwkey());
			newMl.setBarcode(mls.getBarcode());
			newMl.setBukrs(mls.getBukrs());
			newMl.setCreated_by(mls.getCreated_by());
			newMl.setCreated_date(mls.getCreated_date());
			newMl.setCustomer_id(mls.getCustomer_id());
			newMl.setDmbtr(mls.getDmbtr());
			newMl.setGjahr(mls.getGjahr());
			newMl.setMatnr(mls.getMatnr());
			newMl.setMatnr_list_id(mls.getMatnr_list_id());
			//System.out.println("MLS_ID: " + mls.getMatnr_list_id());
			newMl.setMenge(1D);
			newMl.setStaff_id(mls.getStaff_id());
			newMl.setStatus(MatnrList.STATUS_RECEIVED);
			newMl.setWerks(mm.getTo_werks());
			mlDao.update(newMl);
			//mlDao.create(newMl);
			mlsDao.delete(mls.getMls_id());
			matnrListForContract.add(mls.getMatnr());
			
			
			MatnrMovementItem movementItem = new MatnrMovementItem();
			movementItem.setMatnr(mls.getMatnr());
			movementItem.setMatnr_list_id(newMl.getMatnr_list_id());
			movementItem.setMm_id(mm.getMm_id());
			movementItem.setNew_dmbtr(newMl.getDmbtr());
			movementItem.setOld_dmbtr(newMl.getDmbtr());
			movementItem.setReceived_date(mm.getMm_date());
			movementItem.setStatus(MatnrMovement.STATUS_RECEIVED);
			mmiDao.create(movementItem);
		}
		if(!Validation.isEmptyLong(bkpf.getContract_number())){
			//contractService.forCancelContract(bkpf.getContract_number(),matnrListForContract, "", mm.getCreated_by());
		}
		
		//mrDao.create(mr);
		//List<MatnrListSold> mlsList = ;
		
	}
	
	private String valiadte(MatnrMovement mm, boolean isNew){
		String s = "";
		if(Validation.isEmptyString(mm.getBukrs())){
			s += "Выберите компанию" + "\n";;
		}
		
		if(Validation.isEmptyLong(mm.getTo_werks())){
			s += "Выберите склад" + "\n";
		}
		
		if(isNew){
			mm.setCreated_date(GeneralUtil.removeTime(Calendar.getInstance().getTime()));
		}
		
		if(mm.getMm_date() == null){
			s += "Введите дату возврата" + "\n";
		}
		
		return s;
	}

	@Override
	public void update(MatnrReturned mr,List<MatnrListSold> mlList) throws DAOException {
		// TODO Auto-generated method stub
		
	}
}
