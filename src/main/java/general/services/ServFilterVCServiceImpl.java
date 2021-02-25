package general.services;

import general.PermissionController;
import general.Validation;
import general.dao.BranchDao;
import general.dao.CityregDao;
import general.dao.ContractDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.SalePlanDao;
import general.dao.ServCRMHistoryDao;
import general.dao.ServCRMScheduleDao;
import general.dao.ServFilterVCDao;
import general.dao.StaffDao;
import general.dao.UserRoleDao;
import general.output.tables.ServFilterOutput;
import general.tables.Branch;
import general.tables.BusinessArea;
import general.tables.SalePlan;
import general.tables.ServCRMHistory;
import general.tables.ServCRMSchedule;
import general.tables.ServFilterVC;
import general.tables.UserRole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.User;

@Service("sfvcService")
public class ServFilterVCServiceImpl implements ServFilterVCService {
	
	@Autowired
	ServFilterVCDao sfvcDao;
	
	@Autowired
	ContractDao conDao;
	
	@Autowired
	CustomerDao cusDao;
	
	@Autowired
	BranchDao brDao;
	
	@Autowired
	StaffDao stDao;	
	
	@Autowired
	CityregDao crDao;	
	
	@Autowired
	ServCRMHistoryDao schDao;
	
	@Autowired
	ServCRMScheduleDao scsDao;
	
	@Autowired
	SalePlanDao spDao;
		
	@Override
	public List<ServFilterOutput> getZFList(String in_bukrs, Long in_branchID, Date in_date, 
			int in_list_type, int inListWar, Long in_con_num, String in_tovSN, int in_cat) throws DAOException {
		List<ServFilterOutput> sfoL = new ArrayList<ServFilterOutput>();
		
		try {
			
			
			if (!Validation.isEmptyString(in_tovSN)) {
				in_tovSN = in_tovSN.trim();
				sfoL = sfvcDao.findAllBySN(in_bukrs, in_con_num, in_tovSN);
			} else {
				int listType = in_list_type;
//				listType = in_list_type * 10 + inListWar;			
//				if (listType / 10 == 1) listType = 10; 
				
				switch (listType) {
				case 1:
					// All
					sfoL = sfvcDao.findAllByBranch(in_bukrs, in_branchID, in_cat);
					break;
					
				case 2:
					// Cur + Warranty
					sfoL = sfvcDao.findAllCurrentByDate(in_bukrs, in_branchID, in_date, inListWar);
					break;

				case 3:
					// Overdue + Warranty
					sfoL = sfvcDao.findAllOverdueByDate(in_bukrs, in_branchID, in_date, inListWar);
					break;

				default:
					String msg = "ListType: " + listType + "is not defined!";
					System.out.println(msg);
					throw new Exception(msg);
				}
			}
			
			//sfoL = sfDao.findAllTest(in_bukrs, in_branchID, in_date);
			//System.out.println("Total found: " + sfoL.size());
			for (ServFilterOutput sfo:sfoL) {
						boolean tt = false;
						sfo.tel = "";
						
						if (!Validation.isEmptyString(sfo.getMob1())) {
							sfo.tel = sfo.getMob1();
							tt = true;
						}
						if (!Validation.isEmptyString(sfo.getMob2())) {
							if (tt) sfo.tel += ", ";
							sfo.tel += sfo.getMob2();
							tt = true;
						}
							
						if (!Validation.isEmptyString(sfo.getTelDom())) {
							if (tt) sfo.tel += ", "; 
							sfo.tel += sfo.getTelDom();
						}
						
						switch (sfo.getCrm_category()) {
						case 1:sfo.setCrmColor("#3f7");							
							break;
						case 2:sfo.setCrmColor("#ff7");							
							break;
						case 3:sfo.setCrmColor("#f37");							
							break;
						case 4:sfo.setCrmColor("#777");							
							break;
						default:
							break;
						}
						/*
						sfo.f1 = calculateAgeinMonths(in_date, sf.getF1_date_next());
						sfo.f2 = calculateAgeinMonths(in_date, sf.getF2_date_next());
						sfo.f3 = calculateAgeinMonths(in_date, sf.getF3_date_next());
						sfo.f4 = calculateAgeinMonths(in_date, sf.getF4_date_next());
						sfo.f5 = calculateAgeinMonths(in_date, sf.getF5_date_next());
						*/
			}
			
			System.out.println("TOTAL OUTPUT: " + sfoL.size());
			return sfoL;			
		} catch(Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	@Override
	public List<ServFilterVC> getZFListForPlan(String in_bukrs, Long in_branchID, Date in_date, boolean overdue) throws DAOException {
		List<ServFilterVC> sfL = new ArrayList<>();
		try {
			if (overdue) 
				sfL = sfvcDao.findAllForOverduePlanByDate(in_bukrs, in_branchID, in_date);
			else
				sfL = sfvcDao.findAllForCurrentPlanByDate(in_bukrs, in_branchID, in_date);
			
			//sfoL = sfDao.findAllTest(in_bukrs, in_branchID, in_date);
			//System.out.println("Total found: " + sfoL.size());
			
			System.out.println("TOTAL OUTPUT: " + sfL.size());
			return sfL;			
		} catch(Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	@Override
	public boolean createNewSCH(ServCRMHistory a_sch, User userData, Long trId) throws DAOException {
		try {
			if (PermissionController.canCreate(userData, trId)) {
				if (a_sch.getActionDate() == null)
					throw new DAOException("Date is empty!");
				
				if (a_sch.getContractId() == null)
					throw new DAOException("ContractID is invalid!");
				
				if (Validation.isEmptyLong(a_sch.getActionId()))
					throw new DAOException("ActionID is invalid!");
				
				if (Validation.isEmptyLong(a_sch.getStaffId()))
					throw new DAOException("StaffID is invalid!");
				
				if (Validation.isEmptyString(a_sch.getInfo()))
					throw new DAOException("Info is empty!");			
				
				schDao.create(a_sch);												
				return true;	
			} else {
				throw new Exception("No permission!");
			}						
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	@Override 
	public boolean deleteSCHByServId(Long in_servId, User userData, Long trId) throws DAOException {
		try {
			ServCRMHistory sch = schDao.findByServID(in_servId);
			if (sch != null)
				schDao.delete(sch.getId());
			return true;
		} catch(Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	@Override
	public boolean createNewSCS(ServCRMSchedule a_scs, User userData, Long trId) throws DAOException {
		try {
			if (PermissionController.canCreate(userData, trId)) {
				if (a_scs.getScheduledDate() == null)
					throw new DAOException("Date is empty!");
				
				if (Validation.isEmptyLong(a_scs.getContractId()))
					throw new DAOException("ContractID is invalid!");
				
				if (Validation.isEmptyLong(a_scs.getStaffId()))
					throw new DAOException("StaffID is invalid!");
				
				if (Validation.isEmptyString(a_scs.getInfo()))
					throw new DAOException("Info is empty!");
				
				if (Validation.isEmptyLong(a_scs.getBranchId()))
					throw new DAOException("Branch is empty!");
				
				scsDao.create(a_scs);												
				return true;	
			} else {
				throw new Exception("No permission!");
			}						
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}	
	
	@Override 
	public ServFilterVC getSFByContractId(Long inConId) throws DAOException {
		try {
			return sfvcDao.findByContractID(inConId);
		} catch(Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	@Override
	public boolean savePlan(Long inBranchId, byte inType, Date inDate, double inSummPlan, String inWaers, User inUser, Long transactionId) throws DAOException {
		PermissionController.canWrite(inUser, transactionId);
		try {
			int month = inDate.getMonth() + 1;
			int year = inDate.getYear();
			
			SalePlan sp = new SalePlan();
			sp = spDao.findByBranchAndMonthYear(inBranchId, month, year, new Long(BusinessArea.AREA_SERVICE), inType);
			
			if (sp == null && inType > 10) {
				sp = new SalePlan();
				sp.setBranch_id(inBranchId);
				Branch br = brDao.find(inBranchId);
				sp.setBukrs(br.getBukrs());
				sp.setBusiness_area_id(new Long(BusinessArea.AREA_SERVICE));
				sp.setCountry_id(br.getCountry_id());
				sp.setPlan_type(inType);
				sp.setYear(year);
				sp.setMonth(month);
				sp.setPlan_sum(inSummPlan);
				sp.setWaers(inWaers);
				sp.setCreated_by(inUser.getUserid());
				sp.setCreated_date(Calendar.getInstance().getTime());
				validateSP(sp, true);
				spDao.create(sp);
			} else {
				sp.setPlan_sum(inSummPlan);
				sp.setWaers(inWaers);
				validateSP(sp, false);
				spDao.update(sp);
			}		
					
			return true;
		} catch(Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	@Override
	public boolean saveDone(Long inBranchId, byte inType, Date inDate, double inSummDone, String inWaers, User inUser, Long transactionId) throws DAOException {
		PermissionController.canWrite(inUser, transactionId);
		try {
			int month = inDate.getMonth() + 1;
			int year = inDate.getYear();
			
			SalePlan sp = new SalePlan();
			sp = spDao.findByBranchAndMonthYear(inBranchId, month, year, new Long(BusinessArea.AREA_SERVICE), inType);
			
			if (sp != null) {
				if (inSummDone > 0)
					sp.setDone_sum(inSummDone);
				else throw new DAOException("Выполненная сумма не указана или равна нулю!");
				validateSP(sp, false);
				
				spDao.update(sp);
			} else {
				throw new DAOException("План еще не сохранен!");
			}		
					
			return true;
		} catch(Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	private String validateSP(SalePlan sp, boolean isNew){
		String error = "";
		if(sp.getBranch_id() == 0){
			error += "Поле Филиал обязательно для заполнение" + "\n";
		}
		if(sp.getPlan_sum() <= 0){
			error += "Поле План должно быть больше 0" + "\n";
		}
		if (Validation.isEmptyString(sp.getWaers()))
			error += "Валюта не указана!" + "\n";
		if (sp.getYear() <= 0 || sp.getMonth() <= 0)
			error += "Дата не указана!" + "\n";
		if(isNew){
			sp.setCreated_date(Calendar.getInstance().getTime());
		}
		sp.setUpdated_date(Calendar.getInstance().getTime());
		return error;
	}
	
	@Autowired
	UserRoleDao urlDao; 
	
	@Override
	public boolean updateSF(ServFilterVC in_sf, User userData, Long trId) throws DAOException {
		try {
			if (PermissionController.canAll(userData, trId)) {
				
				List<UserRole> urL = new ArrayList<UserRole>();
				urL = urlDao.findUserRoles(userData.getUserid());
				boolean isCoordinator = false;
				// System.out.println("Role list: " + urL.size());
				for (UserRole wa_ur : urL) {
					// System.out.println(wa_ur.getRoleId());
					if (Arrays.asList(24,30,18,19).contains(wa_ur.getRoleId().intValue())) {						
						isCoordinator = true;
						// System.out.println("IS COORDINATOR!");
					}
				}
				System.out.println("IS COORDINATOR: " + isCoordinator);

				//if (isCoordinator || (in_sf.getCrm_category() < 4)) {
					sfvcDao.update(in_sf);
//				} else 
//					throw new DAOException("У вас недостаточно привилегий!");
				return true;
			}
			return false;
		} catch(Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
}
