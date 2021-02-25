package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import general.PermissionController;
import general.Validation;
import general.dao.BranchDao;
import general.dao.ContractDao;
import general.dao.DAOException;
import general.dao.ServConMatnrWarDao;
import general.dao.ServFilterArchiveDao;
import general.dao.ServFilterDao;
import general.dao.ServFilterVCDao;
import general.dao.ServPosDao;
import general.dao.ServZFBranchMTDao;
import general.dao.ServiceDao;
import general.services.FinanceServServiceImpl.ServiceItem;
import general.tables.Branch;
import general.tables.Contract;
import general.tables.ContractStatus;
import general.tables.ContractType;
import general.tables.ServConMatnrWar;
import general.tables.ServFilter;
import general.tables.ServFilterArchive;
import general.tables.ServFilterVC;
import general.tables.ServPacketWar;
import general.tables.ServZFBranchMT;
import general.tables.ServicePos;
import general.tables.ServiceTable;
import general.tables.Staff;
import user.User;

@Service("serviceService")
public class ServiceServiceImpl implements ServiceService {
	@Autowired
	private ServiceDao sDao;

	@Autowired
	private ServPosDao spDao;

	@Autowired
	private ContractDao conDao;

	@Autowired
	private BranchDao brDao;

	@Autowired
	private FinanceServService financeServService;

	@Autowired
	private ServFilterDao sfDao;

	@Autowired
	private ServFilterArchiveDao sfaDao;

	@Autowired
	private ServZFBranchMTDao szfmtDao;

	@Autowired
	private ServConMatnrWarDao scmwDao;

	@Autowired
	InvoiceService inService;

	@Override
	public boolean createService(ServiceTable a_service, Branch a_branch,
			Contract a_con, Long a_customer_id, User userData, String a_trCode,
			Long a_trId, List<ServicePos> a_sp) throws DAOException {

		PermissionController.canWrite(userData, a_trId);

		try {
			if (a_con == null 
					|| a_con.getContract_status_id().intValue() != ContractStatus.STATUS_CANCELLED
					&& (a_con.getLast_state() == Contract.LASTSTATE_GIVEN 
					|| a_con.getLast_state() == Contract.LASTSTATE_INSTALLED)) {
				validateService(a_service, a_sp, a_branch);				
				// ServiceTable a_service = new ServiceTable();
				// BeanUtils.copyProperties(aold_service, a_service);
				a_service.setId(null);
				Calendar today = Calendar.getInstance();
				a_service.setUpdatedDate(today.getTime());
				a_service.setCreated_by(userData.getUserid());
				sDao.create(a_service);

				System.out.println("ServiceID: " + a_service.getId());
				System.out
						.println("--------------------------------------------");

				List<ServiceItem> si_l = new ArrayList<ServiceItem>();
				FinanceServServiceImpl finSSI = new FinanceServServiceImpl();
				for (ServicePos sp : a_sp) {
					ServiceItem si = finSSI.new ServiceItem();

					if (sp.getOperation() != null
							&& sp.getOperation().intValue() == 1) {
						si.setMatnr(sp.getMatnr_id());
						si.setKolichestvo(sp.getQuantity());
						si.setUsluga(0);
					} else if (sp.getOperation() != null
							&& sp.getOperation().intValue() == 2) {
						si.setUsluga(1);
						si.setKolichestvo(1);
					}

					si.setSumma(sp.getSumm());

					si_l.add(si);
					System.out.println(" Usluga: " + si.getUsluga()
							+ "     Matnr: " + si.getMatnr() + "     Kol: "
							+ si.getKolichestvo() + "     Summa: "
							+ si.getSumma());

					if ((sp.getNew_war_inm() > 0) && (sp.getOperation() == 1)) {
						ServConMatnrWar scmw = new ServConMatnrWar();

						scmw.setContract_id(a_service.getContract_id());
						scmw.setFrom_date(a_service.getDate_open());
						scmw.setMatnr_id(sp.getMatnr_id());
						scmw.setServ_id(a_service.getId());
						scmw.setTovar_sn(a_service.getTovar_sn());
						scmw.setWar_months(sp.getNew_war_inm());

						Calendar cal = Calendar.getInstance();
						cal.setTime(a_service.getDate_open());
						cal.add(Calendar.MONTH, sp.getNew_war_inm());
						scmw.setTo_date(cal.getTime());

						scmwDao.create(scmw);
					}
				}
				a_service.setAwkey(null);
				System.out.println("Company: " + a_service.getBukrs());
				System.out.println("Call for FinanceServService.sellParts!");

				Long awkey = financeServService
						.sellParts(si_l, a_service.getBukrs(), a_customer_id,
								a_service.getCurrency(),
								a_branch.getBranch_id(), userData.getUserid(),
								a_trCode, a_service.getDiscount(), a_service.getDate_open());
				System.out
						.println("Call for FinanceServService.sellParts success! Awkey: "
								+ awkey);
				a_service.setAwkey(awkey);
				
				a_service.setServ_num(a_service.getId());
				sDao.update(a_service);

				List<ServicePos> sPosList = new ArrayList<ServicePos>();
				// Save service positions
				for (ServicePos sp : a_sp) {
					sp.setId(null);
					sp.setServ_id(a_service.getId());
					sp.setCurrency(a_service.getCurrency());
					spDao.create(sp);
					sPosList.add(sp);
				}

				inService.createWriteoffDocFromService(a_service, sPosList);

				System.out.println("OK!");
				return true;
			} else
				throw new DAOException("Contract is not suitable for service!");
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Transactional
	public boolean createServiceFitting(ServiceTable a_service,
			Branch a_branch, Contract a_con, User userData, Long a_trId)
			throws DAOException {
		PermissionController.canWrite(userData, a_trId);

		try {
			validateService(a_service, null, a_branch);
			if (a_con.getContract_status_id().intValue() != 3
					&& a_con.getLast_state() == 2
					&& a_con.getTovar_category() == 2
					&& !Validation.isEmptyString(a_con.getTovar_serial())) {
				sDao.create(a_service);
				a_service.setServ_num(a_service.getId());
				sDao.update(a_service);

				a_con.setFitter(a_service.getMaster_id());
				a_con.setLast_state(4);
				conDao.update(a_con);

				if (a_con.getTovar_category() == ContractType.TOVARCAT_WATER_FILTER) {
					createServFilterForNewContract(a_con, a_service, userData, a_trId);
				}				

				return true;
			} else {
				throw new Exception(
						"Contract is not suitable for installation! Please check Contract Statuses and Category! Material may be not assigned to Contract yet!");
			}

		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public boolean createServiceZamenFilter(ServiceTable a_service,
			Branch a_branch, Contract a_con, Long a_customer_id, User userData,
			String a_trCode, Long a_trId, List<ServicePos> a_sp)
			throws DAOException {
		PermissionController.canWrite(userData, a_trId);

		try {
			validateService(a_service, a_sp, a_branch);

			System.out.println("ConStatus: " + a_con.getContract_status_id());
			System.out.println("LastState" + a_con.getLast_state());
			System.out.println("TovCat: " + a_con.getTovar_category());

			if (a_con.getContract_status_id().intValue() != 3
					&& a_con.getLast_state() == 4
					&& a_con.getTovar_category() == 2) {
				if (createService(a_service, a_branch, a_con, a_customer_id,
						userData, a_trCode, a_trId, a_sp)) {

					ServFilter sf = sfDao.findByTovarSN(a_service.getBukrs(),
							a_service.getTovar_sn());

					if (sf != null && sf.getId() != null && sf.getId() > 0) {
						sf.setActive((byte) 1);
						sf.setCrm_category(1);
						for (ServicePos sp : a_sp) {
							switch (sp.getFno()) {
							case 1: {
								createSFA(a_service, 1);
								sf.setF1_date_prev(sf.getF1_date());
								sf.setF1_sid_prev(sf.getF1_sid());
								sf.setF1_date(a_service.getDate_open());
								sf.setF1_sid(a_service.getId());

								Calendar cal = Calendar.getInstance();
								cal.setTime(a_service.getDate_open());
								cal.add(Calendar.MONTH, sf.getF1_mt());
								sf.setF1_date_next(cal.getTime());
								break;
							}

							case 2: {
								createSFA(a_service, 2);
								sf.setF2_date_prev(sf.getF2_date());
								sf.setF2_sid_prev(sf.getF2_sid());
								sf.setF2_date(a_service.getDate_open());
								sf.setF2_sid(a_service.getId());

								Calendar cal = Calendar.getInstance();
								cal.setTime(a_service.getDate_open());
								cal.add(Calendar.MONTH, sf.getF2_mt());
								sf.setF2_date_next(cal.getTime());
								break;
							}

							case 3: {
								createSFA(a_service, 3);
								sf.setF3_date_prev(sf.getF3_date());
								sf.setF3_sid_prev(sf.getF3_sid());
								sf.setF3_date(a_service.getDate_open());
								sf.setF3_sid(a_service.getId());

								Calendar cal = Calendar.getInstance();
								cal.setTime(a_service.getDate_open());
								cal.add(Calendar.MONTH, sf.getF3_mt());
								sf.setF3_date_next(cal.getTime());
								break;
							}

							case 4: {
								createSFA(a_service, 4);
								sf.setF4_date_prev(sf.getF4_date());
								sf.setF4_sid_prev(sf.getF4_sid());
								sf.setF4_date(a_service.getDate_open());
								sf.setF4_sid(a_service.getId());

								Calendar cal = Calendar.getInstance();
								cal.setTime(a_service.getDate_open());
								cal.add(Calendar.MONTH, sf.getF4_mt());
								sf.setF4_date_next(cal.getTime());
								break;
							}

							case 5: {
								createSFA(a_service, 5);
								sf.setF5_date_prev(sf.getF5_date());
								sf.setF5_sid_prev(sf.getF5_sid());
								sf.setF5_date(a_service.getDate_open());
								sf.setF5_sid(a_service.getId());

								Calendar cal = Calendar.getInstance();
								cal.setTime(a_service.getDate_open());
								cal.add(Calendar.MONTH, sf.getF5_mt());
								sf.setF5_date_next(cal.getTime());
								break;
							}
							default:
								break;
							}
						}
						sfDao.update(sf);
					}
					return true;
				} else
					throw new DAOException("Couldn't create service!");
			} else
				throw new DAOException(
						"Contract is not suitable for service ZF!");
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	private void createSFA(ServiceTable a_service, int a_fno) throws Exception {
		try {
			ServFilterArchive sfa = new ServFilterArchive();
			sfa.setFno(a_fno);
			sfa.setBukrs(a_service.getBukrs());
			sfa.setServ_date(a_service.getDate_open());
			sfa.setServ_id(a_service.getId());
			sfa.setTovar_sn(a_service.getTovar_sn());
			sfa.setContract_id(a_service.getContract_id());
			sfaDao.create(sfa);
		} catch (Exception ex) {
			throw ex;
		}
	}

	@Autowired
	ServFilterVCDao sfvcDao;
	
	@Override
	public boolean createServiceProphylaxis(ServiceTable a_service,
			Branch a_branch, Contract a_con, Long a_customer_id, User userData,
			String a_trCode, Long a_trId, List<ServicePos> a_sp,
			List<ServPacketWar> spWarL) throws DAOException {
		PermissionController.canWrite(userData, a_trId);
		try {
			validateService(a_service, a_sp, a_branch);

			if (createService(a_service, a_branch, a_con, a_customer_id,
					userData, a_trCode, a_trId, a_sp)) {

				System.out.println("Service created successfully!");

				System.out.println("Service Warrangty to create: "
						+ spWarL.size());

				for (ServPacketWar spw : spWarL) {
					ServConMatnrWar scmw = new ServConMatnrWar();

					scmw.setContract_id(a_service.getContract_id());
					scmw.setFrom_date(a_service.getDate_open());
					scmw.setMatnr_id(spw.getMatnr_id());
					scmw.setServ_id(a_service.getId());
					scmw.setTovar_sn(a_service.getTovar_sn());
					scmw.setWar_months(spw.getWar_months());

					Calendar cal = Calendar.getInstance();
					cal.setTime(a_service.getDate_open());
					cal.add(Calendar.MONTH, spw.getWar_months());
					scmw.setTo_date(cal.getTime());

					scmwDao.create(scmw);
				}
				
				// ************************************************************
				ServFilterVC sf = sfvcDao.findByTovarSN(a_service.getBukrs(),
						a_service.getTovar_sn());

				if (sf != null && sf.getId() != null && sf.getId() > 0) {
					sf.setActive((byte) 1);
					sf.setCrm_category(1);
					
							createSFA(a_service, 1);
							sf.setF1_date_prev(sf.getF1_date());
							if (!Validation.isEmptyLong(sf.getF1_sid())) {
								sf.setF1_sid_prev(sf.getF1_sid());
							}
							sf.setF1_date(a_service.getDate_open());
							sf.setF1_sid(a_service.getId());

							Calendar cal = Calendar.getInstance();
							cal.setTime(a_service.getDate_open());
							cal.add(Calendar.MONTH, sf.getF1_mt());
							sf.setF1_date_next(cal.getTime());							
					
					sfvcDao.update(sf);
				}
			}
			return true;
		} catch (Exception ex) {

			throw new DAOException(ex.getMessage());
		}
	}

	public void validateService(ServiceTable v_service, List<ServicePos> v_sp,
			Branch vv_branch) throws Exception {
		Validation vv = new Validation();
		try {
			if (v_service != null) {
				if (v_service.getBukrs() == null
						|| vv.isEmptyString(v_service.getBukrs())) {
					throw new Exception(
							"Bukrs is empty! Please select Company!");
				}

				if (v_service.getBranch_id() == null
						|| vv.isEmptyLong(v_service.getBranch_id())) {
					throw new Exception(
							"Branch is empty! Please select Branch!");
				} else if (vv_branch != null
						&& vv_branch.getBusiness_area_id().intValue() != 5) {
					throw new Exception(
							"Branch BursinessArea is not Service (5)! Please select correct Branch!");
				}

				if (v_service.getCategory() == 0) {
					throw new Exception(
							"Service Category is empty! Please specify the Category for service!");
				}
				
				if (Validation.isEmptyString(v_service.getCustomer_firstname())) {
					throw new Exception(
							"Укажите пожалуйста имя клиента!");
				} 
				
				if (Validation.isEmptyString(v_service.getAddr())) {
					throw new Exception(
							"Укажите пожалуйста адрес!");
				} 
				
				if (Validation.isEmptyString(v_service.getTel())) {
					throw new Exception(
							"Укажите пожалуйста тел. клиента!");
				} 				

				Calendar curDate = Calendar.getInstance();
				Calendar spDate = Calendar.getInstance();
				spDate.setTime(v_service.getDate_open());
				Calendar firstDay = Calendar.getInstance();
				Calendar lastDay = Calendar.getInstance();
				firstDay.set(curDate.get(Calendar.YEAR),
						curDate.get(Calendar.MONTH), 1);
				if (curDate.getTime().getDate() < 4) {
					System.out.println("CurDate: "
							+ curDate.getTime().getDate());
					firstDay.add(Calendar.MONTH, -1);
				}
				lastDay.set(Calendar.DAY_OF_MONTH,
						lastDay.getActualMaximum(Calendar.DAY_OF_MONTH));

				if (spDate.getTime().before(firstDay.getTime())
						&& spDate.getTime().after(lastDay.getTime()))
					throw new Exception(
							"Open date must be in the current month and not after current date!");

				if (v_service.getDate_open().after(
						Calendar.getInstance().getTime())) {
					throw new Exception("Please specify correct Date!");
				}

				// Check the old month insertion

				/*
				if (v_service.getServ_status() != null
						&& v_service.getServ_status().intValue() != 1) {
					throw new Exception("New service status is not correct!");
				}
				*/

				List<Integer> a = new ArrayList<Integer>();
				a.add(1);
				a.add(2);
				a.add(3);
				a.add(4);
				a.add(5);
				if (!a.contains(v_service.getServ_type())) {
					System.out.println("ServiceType: "
							+ v_service.getServ_type());
					throw new Exception("New service type is not correct!");
				}

				if ((v_service.getMaster_id() == null)
						|| (v_service.getMaster_id() != null && v_service
								.getMaster_id() == 0)) {
					throw new Exception(
							"No Master selected! Please select Master!");
				}

				// if (((v_service.getServ_type() == 1) ||
				// (v_service.getServ_type() == 4))
				/*
				 * if ((v_service.getServ_type() == 1) &&
				 * ((v_service.getOper_id() == null) || (v_service .getOper_id()
				 * != null && v_service.getOper_id() == 0))) { throw new
				 * Exception(
				 * "Operator is not selected! Please select Operator!"); }
				 */

				if (v_service.getContract_id() == null
						//|| (v_service.getContract_id() != null 
						//&& v_service.getContract_id() <= 0)
						) {
					throw new Exception(
							"Contract field is empty! Please select Contract!");
				}

				if (v_service.getTovar_id() == null
						|| (v_service.getTovar_id() != null && v_service
								.getTovar_id() <= 0)) {
					throw new Exception(
							"Material ID is incorrect! Please specify correct Material ID!");
				}

				if (v_service.getTovar_sn() == null
						|| Validation.isEmptyString(v_service.getTovar_sn())) {
					throw new Exception(
							"Material SN is incorrect or empty! Please specify correct Material SN!");
				}
				
				if (v_service.getPayment_due() + v_service.getDiscount() != v_service.getSummTotal())
					throw new Exception(
							"Summ is incorect!");
				
				if (v_sp != null)
					ValidateServicePos(v_service, v_sp);
			} else
				throw new Exception(
						"Service is empty! Please fill the service form!");
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	public void ValidateServicePos(ServiceTable v_service, List<ServicePos> v_sp)
			throws Exception {
		Validation vv = new Validation();
		try {
			/*
			 * for (ServicePos wa_sp:v_sp) { // nothing to check yet }
			 */
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}

	}

	@Override
	public boolean paymentStateUpdate(Long awkey, double sum, String a_bukrs)
			throws DAOException {
		try {
			ServiceTable st = sDao.findByAwkey(awkey,a_bukrs);
			st.setPaid(st.getPaid() + sum);

			if (st.getPaid() >= st.getPayment_due())
				st.setServ_status(new Long(ServiceTable.STATUS_PAID));
			else if (sum < 0) {
				st.setServ_status(new Long(ServiceTable.STATUS_NEW));
			}
			sDao.update(st);

			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	// **********************************************************************************************
	// ****************************************UPDATES***********************************************
	// **********************************************************************************************

	@Override
	public boolean updateService(ServiceTable a_service,
			ServiceTable a_service_old, Branch a_branch, Contract a_con,
			User userData, String a_trCode, Long a_trId) throws DAOException {
		PermissionController.canWrite(userData, a_trId);
		try {
			validateService(a_service, null, a_branch);
			
			// Should compare important fields with old_service!
			// Should add new additional fields: Updated_date, Updated_by 
			
			sDao.update(a_service);
			System.out.println("ServiceID: " + a_service.getId() + " is updated!");
			System.out.println("--------------------------------------------");

			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	// ******************************************************************************************

	public Long getMasterPos(ServiceTable a_service) {
		Long master_pos_id = 0L;
		if (a_service.getServ_type() == ServiceTable.TYPE_FILTERS)
			master_pos_id = new Long(Staff.POS_MASTER_FILTER);
		else if (a_service.getServ_type() == ServiceTable.TYPE_FITTING)
			master_pos_id = new Long(Staff.POS_MASTER_WAT);
		else if (a_service.getServ_type() == ServiceTable.TYPE_PACKET 
				|| a_service.getServ_type() == ServiceTable.TYPE_SERVICE) {
			if (a_service.getCategory() == ContractType.TOVARCAT_WATER_FILTER)
				master_pos_id = new Long(Staff.POS_MASTER_WAT);
			else if (a_service.getCategory() == ContractType.TOVARCAT_VACUUM_CLEANER)
				master_pos_id = new Long(Staff.POS_MASTER_VAC);
		} 
		return master_pos_id;
	}

	public Long getOperPos(ServiceTable a_service) {
		Long oper_pos_id = 0L;
		if (a_service.getServ_type() == ServiceTable.TYPE_FILTERS)
			oper_pos_id = new Long(Staff.POS_OPER_FILTER);
		else if (a_service.getServ_type() == ServiceTable.TYPE_PACKET) {
			if (a_service.getCategory() == ContractType.TOVARCAT_WATER_FILTER)
				oper_pos_id = new Long(Staff.POS_OPER_FILTER);
			else if (a_service.getCategory() == ContractType.TOVARCAT_VACUUM_CLEANER)
				oper_pos_id = new Long(Staff.POS_OPER_VAC);
		}
		return oper_pos_id;
	}

	// *******************************************************************************
	// ****************************CANCEL_SERVICE*************************************
	// *******************************************************************************

	private boolean removeConMatnrWar(Long a_servId) throws Exception {
		try {
			List<ServConMatnrWar> scmwl = scmwDao.findAllByServId(a_servId);
			for (ServConMatnrWar scmw : scmwl) {
				scmwDao.delete(scmw.getId());
			}
			return true;
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	// ******************************************************************************

	@Transactional
	public boolean cancelService(ServiceTable a_service, User userData,
			String a_trCode) throws DAOException {
		try {
			Long master_pos_id = getMasterPos(a_service);
			Long oper_pos_id = getOperPos(a_service);

			financeServService.cancelService(a_service.getAwkey(),
					userData.getUserid(), a_trCode, a_service.getMaster_id(),
					master_pos_id, a_service.getMaster_premi(),
					a_service.getCurrency(), a_service.getDate_open(),
					a_service.getId(), a_service.getOper_id(), oper_pos_id,
					a_service.getOper_premi(),a_service.getBukrs());

			a_service.setServ_status(new Long(ServiceTable.STATUS_CANCELLED));
			sDao.update(a_service);

			if (a_service.getServ_type() != ServiceTable.TYPE_FITTING)
				inService.returnWriteoffFromService(a_service, userData);

			removeConMatnrWar(a_service.getId());

			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	// ******************************************************************************

	@Transactional
	public boolean cancelFitting(ServiceTable a_service, Branch a_branch,
			Contract a_con, User userData, String a_trCode, Long a_trId)
			throws DAOException {
		try {
			if (cancelService(a_service, userData, a_trCode)) {
				a_con.setLast_state(Contract.LASTSTATE_GIVEN);
				conDao.update(a_con);
			}
			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	// ******************************************************************************

	@Transactional
	public boolean cancelFilter(ServiceTable a_service, Branch a_branch,
			User userData, String a_trCode, Long a_trId, List<ServicePos> a_sp)
			throws DAOException {
		try {
			if (cancelService(a_service, userData, a_trCode)) {

				ServFilter sf = sfDao.findByTovarSN(a_service.getBukrs(),
						a_service.getTovar_sn());

				if (sf != null && !Validation.isEmptyLong(sf.getContract_id())) {
					List<ServicePos> spl = spDao.findAllByServID(a_service.getId());
					for (ServicePos sp : spl) {
						if (sp.getOperation() == ServicePos.OPER_SELL) {
							ServFilterArchive sfa = sfaDao.findPrevFNOByTovarSN(
									a_service.getBukrs(), a_service.getTovar_sn(),
									sp.getFno(), a_service.getId());
							switch (sp.getFno()) {
							case 1: {
								if (sf.getF1_date_prev() != null) {
									sf.setF1_date(sf.getF1_date_prev());
									if (!Validation.isEmptyLong(sf.getF1_sid_prev())) 
										sf.setF1_sid(sf.getF1_sid_prev());
									if (sfa != null
											&& !Validation.isEmptyLong(sfa.getId())) {
										sf.setF1_date_prev(sfa.getServ_date());
										sf.setF1_sid_prev(sfa.getServ_id());
									}
								}
								break;
							}

							case 2: {
								if (sf.getF2_date_prev() != null) {
									sf.setF2_date(sf.getF2_date_prev());
									if (!Validation.isEmptyLong(sf.getF2_sid_prev()))
										sf.setF2_sid(sf.getF2_sid_prev());
									if (sfa != null
											&& !Validation.isEmptyLong(sfa.getId())) {
										sf.setF2_date_prev(sfa.getServ_date());
										sf.setF2_sid_prev(sfa.getServ_id());
									}
								}
								break;
							}

							case 3: {
								if (sf.getF3_date_prev() != null) {
									sf.setF3_date(sf.getF3_date_prev());
									if (!Validation.isEmptyLong(sf.getF3_sid_prev()))
										sf.setF3_sid(sf.getF3_sid_prev());
									if (sfa != null
											&& !Validation.isEmptyLong(sfa.getId())) {
										sf.setF3_date_prev(sfa.getServ_date());
										sf.setF3_sid_prev(sfa.getServ_id());
									}
								}
								break;
							}

							case 4: {
								if (sf.getF4_date_prev() != null) {
									sf.setF4_date(sf.getF4_date_prev());
									if (!Validation.isEmptyLong(sf.getF4_sid_prev()))
										sf.setF4_sid(sf.getF4_sid_prev());
									if (sfa != null
											&& !Validation.isEmptyLong(sfa.getId())) {
										sf.setF4_date_prev(sfa.getServ_date());
										sf.setF4_sid_prev(sfa.getServ_id());
									}
								}
								break;
							}

							case 5: {
								if (sf.getF5_date_prev() != null) {
									sf.setF5_date(sf.getF5_date_prev());
									if (!Validation.isEmptyLong(sf.getF5_sid_prev()))
										sf.setF5_sid(sf.getF5_sid_prev());
									if (sfa != null
											&& !Validation.isEmptyLong(sfa.getId())) {
										sf.setF5_date_prev(sfa.getServ_date());
										sf.setF5_sid_prev(sfa.getServ_id());
									}
								}
								break;
							}

							default:
								break;
							}
						}					
					}
					sfDao.update(sf);
				}
			}
			deleteSFAbyServId(a_service.getId());
			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	@Transactional 
	public boolean createServFilterForNewContract(Contract a_con, ServiceTable a_service, User userData, Long transactionId) throws Exception {
		try {
			PermissionController.canCreate(userData, transactionId);
			
			// ***************************************************************
			
			ServFilter sf = sfDao.findByTovarSN(a_service.getBukrs(),
					a_service.getTovar_sn());
			if (sf == null)
				sf = new ServFilter();

			sf.setContract_id(a_service.getContract_id());
			sf.setTovar_sn(a_service.getTovar_sn());
			sf.setActive((byte) 1);
			sf.setCrm_category(1);
			sf.setBukrs(a_service.getBukrs());
			sf.setServ_branch(a_service.getBranch_id());
			sf.setFno(5);

			Calendar cal = Calendar.getInstance();
			ServZFBranchMT szfmt = szfmtDao.findLastMTByBranch(a_service.getBranch_id());

			if (szfmt == null) {
				throw new Exception("Branch filter change default month-terms are not set!");
			}
			
			sf.setF1_date(a_service.getDate_open());
			sf.setF1_sid(a_service.getId());
			sf.setF1_mt(szfmt.getF1_mt());
			cal.setTime(a_service.getDate_open());
			cal.add(Calendar.MONTH, sf.getF1_mt());
			sf.setF1_date_next(cal.getTime());

			sf.setF2_date(a_service.getDate_open());
			sf.setF2_sid(a_service.getId());
			sf.setF2_mt(szfmt.getF2_mt());
			cal.setTime(a_service.getDate_open());
			cal.add(Calendar.MONTH, sf.getF2_mt());
			sf.setF2_date_next(cal.getTime());

			sf.setF3_date(a_service.getDate_open());
			sf.setF3_sid(a_service.getId());
			sf.setF3_mt(szfmt.getF3_mt());
			cal.setTime(a_service.getDate_open());
			cal.add(Calendar.MONTH, sf.getF3_mt());
			sf.setF3_date_next(cal.getTime());

			sf.setF4_date(a_service.getDate_open());
			sf.setF4_sid(a_service.getId());
			sf.setF4_mt(szfmt.getF4_mt());
			cal.setTime(a_service.getDate_open());
			cal.add(Calendar.MONTH, sf.getF4_mt());
			sf.setF4_date_next(cal.getTime());

			sf.setF5_date(a_service.getDate_open());
			sf.setF5_sid(a_service.getId());
			sf.setF5_mt(szfmt.getF5_mt());
			cal.setTime(a_service.getDate_open());
			cal.add(Calendar.MONTH, sf.getF5_mt());
			sf.setF5_date_next(cal.getTime());

			if (sf.getId() != null && sf.getId() > 0)
				sfDao.update(sf);
			else
				sfDao.create(sf);
			
			// ***************************************************************
			
			return true;
		} catch(Exception ex) {
			throw new Exception(ex.getMessage());
		}				
	}

	@Transactional
	private boolean deleteSFAbyServId(Long a_servId) throws Exception {
		try {
			List<ServFilterArchive> sfaL = sfaDao.findAllByServId(a_servId);
			for (int i = 0; i < sfaL.size(); i++) {
				sfaDao.delete(sfaL.get(i).getId());
			}
			return true;
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	// ******************************************************************************

	@Transactional
	public boolean cancelPacket(ServiceTable a_service, Branch a_branch,
			User userData, String a_trCode, Long a_trId)
			throws DAOException {
		try {
			if (cancelService(a_service, userData, a_trCode)) {
				ServFilterVC sf = sfvcDao.findByTovarSN(a_service.getBukrs(),
						a_service.getTovar_sn());

				if (sf != null && !Validation.isEmptyLong(sf.getContract_id())) {
					
							ServFilterArchive sfa = sfaDao.findPrevFNOByTovarSN(
									a_service.getBukrs(), a_service.getTovar_sn(),
									1, a_service.getId());
							
							if (sf.getF1_date_prev() != null) {
									sf.setF1_date(sf.getF1_date_prev());
									if (!Validation.isEmptyLong(sf.getF1_sid_prev())) 
										sf.setF1_sid(sf.getF1_sid_prev());
									if (sfa != null
											&& !Validation.isEmptyLong(sfa.getId())) {
										sf.setF1_date_prev(sfa.getServ_date());
										sf.setF1_sid_prev(sfa.getServ_id());
									}
							}						
					sfvcDao.update(sf);
				}
				return true;
			}
			return false;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

}
