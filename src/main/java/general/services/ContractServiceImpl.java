package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import crm.dao.CrmDocDemoDao;
import crm.services.CrmDocDemoService;
import crm.tables.CrmDocDemo;
import dms.contract.ContractFinOperations;
import general.GeneralUtil;
import general.PermissionController;
import general.Validation;
import general.dao.BkpfDao;
import general.dao.BonusDao;
import general.dao.BranchDao;
import general.dao.ContractDao;
import general.dao.ContractHistoryDao;
import general.dao.ContractPromosDao;
import general.dao.ContractTypeDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.ExchangeRateDao;
import general.dao.MatnrDao;
import general.dao.PaymentScheduleDao;
import general.dao.PaymentScheduleTemporaryDao;
import general.dao.PromotionDao;
import general.dao.ServFilterVCDao;
import general.dao.StaffDao;
import general.tables.Bkpf;
import general.tables.Bonus;
import general.tables.Branch;
import general.tables.Contract;
import general.tables.ContractHistory;
import general.tables.ContractPromos;
import general.tables.ContractStatus;
import general.tables.ContractType;
import general.tables.Customer;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.PaymentSchedule;
import general.tables.PaymentScheduleTemporary;
import general.tables.Payroll;
import general.tables.Promotion;
import general.tables.ServFilter;
import general.tables.ServFilterVC;
import general.tables.Staff;
import general.validators.ContractValidator;
import user.User;

@Service("contractService")
public class ContractServiceImpl implements ContractService {
	@Autowired
	private ContractDao conDao;

	@Autowired
	private CustomerDao cusDao;

	@Autowired
	private StaffDao stfDao;

	@Autowired
	private FinanceServiceDms financeServiceDMS;

	@Autowired
	private PaymentScheduleDao psDao;

	@Autowired
	private ContractPromosDao cpDao;

	@Autowired
	private BonusDao bonDao;

	@Autowired
	private ContractTypeDao ctDao;

	@Autowired
	private BranchDao brDao;

	@Autowired
	BkpfDao bkpfDao;

	@Autowired
	PromotionDao promoDao;

	@Autowired
	ContractHistoryDao chDao;

	@Autowired
	MatnrDao matnrDao;

	@Autowired
	InvoiceService is;

	@Autowired
	ContractValidator conVal;

	@Autowired
	PaymentScheduleTemporaryDao pstDao;

	@Autowired
	MatnrListService mlService;
	
	@Autowired
	CrmDocDemoService crmDocDemoService;
	
	@Autowired
	ExchangeRateDao exchangeRateDao;
	

	@Override
	public boolean createContract(Contract con, Branch a_branch, User userData, String a_trcode, String a_currency,
			double a_kurs, double a_USD, double a_LocalPrice, Long a_matnrId, PaymentSchedule a_ps[], Long a_promos[],
			double a_ref_skidka_vol, double refRate, String refWaers, String refDiscountWaers, String locale,CrmDocDemo demo)
			throws DAOException {
		try {
			if (conVal.validateContract(con, a_ps, refRate, refWaers, refDiscountWaers, locale, true, userData)) {
				
				
				ContractType ct1 = ctDao.find(con.getContract_type_id());
				con.setTovar_category(ct1.getTovar_category());

				con.setWaers(a_currency);
				con.setRate(a_kurs);
				con.setPaid(0);
				con.setSkidka_vol(0L);
				con.setLast_state(1);
				Calendar cal = Calendar.getInstance();
				con.setCreated_date(cal.getTime());
				con.setOld_id(0L);
				con.setOld_sn(0L);
				con.setCreated_by(userData.getUserid());
				con.setMtConfirmed(Contract.MT_CONFIRMED_APPROVED);
				con.setMarkedType(Contract.MT_STANDARD_PRICE);
				
				//set warranty to 48 month if con type is rainbow and the customer is fiz (if fiz in the dmsc01 the warranty is set to 36month)
				if (ct1.getContract_type_id().equals(3L) && con.getWarranty()==36)
				{
					con.setWarranty(48);
				}

				conDao.create(con);
				
				

				// Updating contract number
				String conNumber = "";
				/*
				 * conNumber = Integer.toString(conDate.get(Calendar.YEAR));
				 * conNumber = conNumber.substring(conNumber.length() - 1);
				 * conNumber = conNumber +
				 * Integer.toString(conDate.get(Calendar.MONTH)); conNumber =
				 * Long.toString(con.getBranch_id()) +
				 * Long.toString(con.getContract_type_id()) +
				 * Long.toString(con.getContract_id()) + conNumber;
				 */
				conNumber = con.getContract_id().toString();
				con.setContract_number(Long.parseLong(conNumber));
				
				if (con.getBukrs().equals("6000")){
					if (demo!=null && demo.getId()!=null && demo.getId()>0L){
						crmDocDemoService.contractCreated(con,demo);
					}
					
				}
				else if (!(
						con.getBranch_id()==58L ||
								con.getBranch_id()==59L ||
										con.getBranch_id()==211L ||
												con.getBranch_id()==212L ||
														con.getBranch_id()==210L ||
																con.getBranch_id()==221L ||
																		con.getBranch_id()==222L||
																				con.getBranch_id()==237L||
																						con.getBranch_id()==238L||
																								con.getBranch_id()==77L)
						)
				{
					if (demo == null || demo.getId()==null || demo.getId()==0L)
					{
						con.setBranch_id(0L);
						throw new DAOException("Выберите демо карту");
					}
					else
					{
						crmDocDemoService.contractCreated(con,demo);
						 
					}
				}
				
				
				System.out.println(
						"***************************************************************************************** ");
				System.out.println("Here are all parameters going to financeService: ");
				System.out.println("Contract Number: " + con.getContract_number());
				System.out.println("Branch: " + a_branch.getText45());
				System.out.println("User_id: " + userData.getUserid());
				System.out.println("Transaction: " + a_trcode);
				System.out.println("Currency: " + a_currency);
				System.out.println("Exchange Rate USD: " + a_kurs);
				System.out.println("Price USD: " + a_USD);
				System.out.println("Price LOCAL: " + a_LocalPrice);
				System.out.println(
						"***************************************************************************************** ");

				List<ContractFinOperations> contrFOList = new ArrayList<ContractFinOperations>();
				ContractFinOperations cfo;

				// SKIDKA_RECOMENDATELYA_FinOperList
				if (con.getRef_contract_id() != null && con.getRef_contract_id() > 0) {
					ContractType ct = ctDao.find(con.getContract_type_id());
					Contract refContr = conDao.find(con.getRef_contract_id());
					Branch conBr = brDao.find(con.getBranch_id());
					// Branch refBr = brDao.find(refContr.getBranch_id());
					ContractType refContrType = ctDao.find(refContr.getContract_type_id());

					// System.out.println("REF CnotractType: " +
					// ref_contr.getContrType().getName());
//					String wcl = " bukrs = '" + con.getBukrs() + "' and bonus_type_id = 8 and positi0try_id();

					int skidkaRek = 16;//Скидка от дилера клиенту
					String wcl = " bonus_type_id = "+skidkaRek+" and branch_id = " + con.getBranch_id();

					List<Bonus> bL = bonDao.dynamicFindBonuses(wcl);
					Bonus skidka_rek = new Bonus();
					if (bL.size() > 0) {
						skidka_rek = bL.get(0);
						System.out.println("Skidka amount: " + skidka_rek.getCoef() + " " + skidka_rek.getWaers());
					} else
						throw new DAOException("Couldn't find Referencer's discount in Bonus Table!");
					
					double dmbtr = 0;
					double wrbtr = 0;
					String refInfoString = "";
					double debt = refContr.getPrice() - refContr.getPaid();
					
					
					
					

//					double skidka_toRek = skidka_rek.getCoef();
//					double vozn = skidka_toRek - debt / refRate;
//					if (vozn > 0) {
//						skidka_toRek -= vozn;
//					}
					
					double skidka_toRek = 0;
					double vozn = 0;
					
					double skSumma = skidka_rek.getCoef();
					String skWaers = skidka_rek.getWaers();
					
//					if (!skWaers.equals("USD") && !skWaers.equals(con.getWaers()))
//					{
//						throw new DAOException("В Бонусной таблице неправильно указана валюта");
//					}
//					double inRate = exchangeRateDao.getLastCurrencyRateInternal(con.getBukrs(), "USD", con.getWaers());
					double inRate = 1;
					if (debt>0){
						if(!refContr.getWaers().equals("USD") && refContr.getWaers().equals(skWaers))
						{
							skidka_toRek = skSumma;
							vozn = skidka_toRek - debt;
							if (vozn > 0) {
								skidka_toRek = skidka_toRek -vozn;
							}
							refWaers = skWaers;
						}
						else if (refContr.getWaers().equals("USD") && !refContr.getWaers().equals(skWaers)){
							skidka_toRek = skSumma/inRate;
							skidka_toRek = GeneralUtil.round(skidka_toRek, 2);
							vozn = skidka_toRek - debt;
							if (vozn > 0) {
								skidka_toRek = skidka_toRek -vozn;
							}
							vozn=vozn*inRate;
							refWaers = refContr.getWaers();
						}
						
					}
					else
					{
						if (skWaers.equals("USD"))
						{
							
							vozn = inRate*skSumma;
							skWaers = con.getWaers();
							
						}
						else if (skWaers.equals(con.getWaers())){
							vozn = skSumma;
						}
							
						
					}
					
					
					
					

					if (a_ref_skidka_vol > 0) {
						// Skidka_Rekomendatelyu
						if (refContr.getPaid() < refContr.getPrice()) {
							cfo = new ContractFinOperations();
							cfo.setWaers(refWaers);

							dmbtr = skidka_rek.getCoef() - a_ref_skidka_vol / a_kurs;
							double skidka = a_ref_skidka_vol / a_kurs * refRate;
							wrbtr = skidka_rek.getCoef() * refRate - skidka;

							System.out.println("SKIDKA 1 - DMBTR: " + dmbtr);
							System.out.println("SKIDKA 1 - WRBTR: " + wrbtr);
							System.out.println("SKIDKA 1 - WAERS: " + refWaers);

							cfo.setDmbtr(dmbtr);
							cfo.setWrbtr(wrbtr);
							cfo.setRate(refRate);
							cfo.setCustomer_id(refContr.getCustomer_id());
							cfo.setMatnr(refContrType.getMatnr());
							cfo.setAwkey(refContr.getAwkey());
							cfo.setContractNumber(refContr.getContract_number());
							cfo.setOper_type(1);
							refInfoString = "Discount SN:" + con.getContract_number();
							cfo.setOperationInfo(refInfoString);
							contrFOList.add(cfo);
						}

						// Skidka_Ot_Rekomendatelya-
						cfo = new ContractFinOperations();
						cfo.setWaers(a_currency);
						dmbtr = a_ref_skidka_vol / a_kurs;
						wrbtr = a_ref_skidka_vol;
						cfo.setDmbtr(dmbtr);
						cfo.setWrbtr(wrbtr);
						cfo.setContractNumber(refContr.getContract_number());
						System.out.println("SKIDKA 3 - DMBTR: " + dmbtr);
						System.out.println("SKIDKA 3 - WRBTR: " + wrbtr);
						System.out.println("SKIDKA 3 - WAERS: " + a_currency);

						cfo.setRate(a_kurs);
						cfo.setOper_type(3);
						refInfoString = "Discount SN:" + con.getContract_number();
						cfo.setOperationInfo(refInfoString);
						cfo.setCustomer_id(con.getCustomer_id());
						contrFOList.add(cfo);
					} else {

						// SKIDKA REKOMENDATELYU
						if (debt > 0) {
							cfo = new ContractFinOperations();
							cfo.setWaers(refWaers);

							dmbtr = skidka_toRek;
							System.out.println("SKIDA DMBTR: " + dmbtr);
							wrbtr = skidka_toRek;
							System.out.println("SKIDA WRBTR: " + wrbtr);

							cfo.setDmbtr(dmbtr);
							cfo.setWrbtr(wrbtr);
							cfo.setRate(1);
							cfo.setCustomer_id(refContr.getCustomer_id());
							cfo.setMatnr(refContrType.getMatnr());
							cfo.setAwkey(refContr.getAwkey());
							cfo.setContractNumber(refContr.getContract_number());
							cfo.setOper_type(1);
							refInfoString = "Discount SN:" + con.getContract_number();
							cfo.setOperationInfo(refInfoString);
							contrFOList.add(cfo);
						}

						// VOZNAGRAJDENIE KLIENTU
						if (vozn > 0) {
							cfo = new ContractFinOperations();
							cfo.setWaers(skWaers);
							dmbtr = vozn;
							wrbtr = vozn;
							cfo.setDmbtr(dmbtr);
							cfo.setWrbtr(wrbtr);
							cfo.setRate(1);
							cfo.setCustomer_id(refContr.getCustomer_id());
							cfo.setAwkey(refContr.getAwkey());
							cfo.setContractNumber(con.getContract_number());
							cfo.setOper_type(2);
							refInfoString = "Reward from SN:" + con.getContract_number();
							cfo.setOperationInfo(refInfoString);
							contrFOList.add(cfo);
						}
					}
				}
					

				// SKIDKA_OT_DILERA
				if (con.getDealer_subtract() > 0) {
					int skidkaOtDileraKlientu = 11;//Скидка от дилера клиенту
					
					Bonus wa_bonus  = bonDao.dynamicFindBonus(" bonus_type_id = "+skidkaOtDileraKlientu+" and branch_id = "+con.getBranch_id());
					double summaSkidkaOtDileraKlientu = 0;
					if (wa_bonus==null){

						throw new Exception("Скидка от дилера клиенту не определена.");	
					}
					else
					{
						summaSkidkaOtDileraKlientu = wa_bonus.getCoef();
					}
					
					if (con.getDealer_subtract()>summaSkidkaOtDileraKlientu)
					{
						String info = "Сумма скидки от дилера должна быть в пределах: 0 и "+summaSkidkaOtDileraKlientu +" " + wa_bonus.getWaers();
						throw new Exception(info);			
					}
					
					
					cfo = new ContractFinOperations();
					cfo.setWaers(a_currency);
					double dmbtr = con.getDealer_subtract() / a_kurs;
					double wrbtr = con.getDealer_subtract();
					cfo.setDmbtr(dmbtr);
					cfo.setWrbtr(wrbtr);
					cfo.setRate(a_kurs);
					Staff dealer = stfDao.find(con.getDealer());
					cfo.setCustomer_id(dealer.getCustomer_id());
					cfo.setStaff_id(con.getDealer());
					// cfo.setAwkey(con.getAwkey());
					cfo.setOper_type(4);
					String infoString = "Discount from Dealer";
					cfo.setOperationInfo(infoString);
					contrFOList.add(cfo);
				}

				// Promo_Materials_list_into_FO
				List<Promotion> promoList = new ArrayList<Promotion>();
				Promotion promo;
				double dmbtr = 0;
				double wrbtr = 0;
				double dfd = 0;
				double wfd = 0;

				String matNames = "";
				for (int i = 0; i < a_promos.length; i++) {
					promo = new Promotion();
					promo = promoDao.find(a_promos[i]);
					promoList.add(promo);
					if (i > 0) {
						matNames = matNames + ", ";
					}
					matNames = matNames + promo.getName();

					if (promo.getFd_currency().equals("USD")) {
						dfd = promo.getFrom_dealer();
					} else {
						wfd = promo.getFrom_dealer();
					}

					dmbtr += dfd;
					wrbtr += wfd;
				}

				wrbtr += dmbtr * a_kurs;
				dmbtr = wrbtr / a_kurs;

				System.out.println("Promo DMBTR: " + dmbtr);
				System.out.println("Promo WRBTR: " + wrbtr);

				if (a_promos.length > 0) {
					cfo = new ContractFinOperations();
					cfo.setWaers(a_currency);
					cfo.setDmbtr(dmbtr);
					cfo.setWrbtr(wrbtr);
					cfo.setRate(0);
					Staff dealer = stfDao.find(con.getDealer());
					cfo.setCustomer_id(dealer.getCustomer_id());
					cfo.setContractNumber(con.getContract_number());
					cfo.setStaff_id(con.getDealer());

					// cfo.setAwkey(con.getAwkey());
					cfo.setOper_type(5);

					String infoString = "For Promo-Materials: " + a_promos.length;

					cfo.setOperationInfo(infoString);
					contrFOList.add(cfo);
				}

//				if (1==1)
//				{
//					throw new DAOException("ZZZ");
//				}
				System.out.println("Vizov metoda - createContractRecievable Success!");
				Long awkey = financeServiceDMS.createContractRecievable(con, a_branch, userData.getUserid(), a_trcode,
						a_currency, a_kurs, a_USD, a_LocalPrice, a_matnrId, contrFOList, promoList);

				System.out.println(awkey);
				System.out.println("Metod - createContractRecievable Success!");
				System.out.println(
						"***************************************************************************************** ");

				con.setAwkey(awkey);
				conDao.update(con);

				// ************** Payment Schedule

				int j = a_ps.length - 1;
				double skidka_sum = a_ref_skidka_vol + con.getDealer_subtract();
				double paying = 0;

				con.setPaid(skidka_sum);
				conDao.update(con);

				while (skidka_sum > 0) {
					paying = a_ps[j].getSum();
					if (skidka_sum < paying) {
						paying = skidka_sum;
					}

					a_ps[j].setPaid(paying);
					skidka_sum -= paying;

					j--;
				}

				// ************** Create Payment Schedule
				createPaymentSchedule(con, a_ps);

				// ************************ Promos ***************************
				for (int i = 0; i < a_promos.length; i++) {
					ContractPromos cp = new ContractPromos();
					cp.setContract_id(con.getContract_id());
					cp.setPromo_id(a_promos[i]);
					cpDao.create(cp);
				}

				// new DAOException("A new Contract with #" + conNumber +
				// " has been succesfully created!");
				System.out.println("Promotions Count: " + promoList.size());
				is.createWriteoffDocFromContract(con, promoList);
				
				
				
				
				
				return true;
			} else
				return false;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	// ***********************************************************************************************************
	// *************************************** UPDATE CONTRACT
	// ***********************************************************************************************************

	@Override
	public boolean updateContractPromos(Contract a_contract, Bkpf a_bkpf, List<Promotion> promoList, Long a_userID,
			String a_tcode, String info, List<ContractHistory> a_chl) throws DAOException {
		try {
			ContractFinOperations cfo = new ContractFinOperations();

			// Promo_Materials_list_into_FO
			double dmbtr = 0;
			double wrbtr = 0;
			double dfd = 0;
			double wfd = 0;

			// Delete old promos by ContrID
			cpDao.deleteByContractId(a_contract.getContract_id());

			// Add new promos
			for (Promotion wa_promo : promoList) {

				ContractPromos cp = new ContractPromos();
				cp.setContract_id(a_contract.getContract_id());
				cp.setPromo_id(wa_promo.getId());
				cpDao.create(cp);

				/*
				 * dfd = wa_promo.getFrom_dealer(); wfd = dfd *
				 * a_bkpf.getKursf();
				 */
				if (wa_promo.getFd_currency().equals("USD"))
					dfd = wa_promo.getFrom_dealer();
				else
					wfd = wa_promo.getFrom_dealer();

				dmbtr += dfd;
				wrbtr += wfd;
			}

			if (promoList.size() > 0 && wrbtr > 0) {
				cfo.setWaers(a_bkpf.getWaers());
				cfo.setDmbtr(dmbtr);
				cfo.setWrbtr(wrbtr);
				cfo.setRate(a_bkpf.getKursf());
				Staff dealer = stfDao.find(a_contract.getDealer());
				cfo.setCustomer_id(dealer.getCustomer_id());
				cfo.setContractNumber(a_contract.getContract_number());
				cfo.setStaff_id(a_contract.getDealer());

				// cfo.setAwkey(con.getAwkey());
				cfo.setOper_type(5);

				String infoString = "For " + info;

				cfo.setOperationInfo(infoString);
			}

			System.out.println(
					"***************************************************************************************** ");
			System.out.println("Here are all parameters going to financeService: ");
			System.out.println("Contract Number: " + a_contract.getContract_number());
			System.out.println("Branch_id: " + a_bkpf.getBrnch());
			System.out.println("User_id: " + a_userID);
			System.out.println("Transaction: " + a_tcode);
			System.out.println("Currency: " + a_bkpf.getWaers());
			System.out.println("Exchange Rate USD: " + a_bkpf.getKursf());
			System.out.println("Dmbtr: " + dmbtr);
			System.out.println("Wrbtr: " + wrbtr);
			System.out.println(
					"***************************************************************************************** ");

			// System.out.println("Vizov metoda - addPromoMaterials!");
			financeServiceDMS.addPromoMaterials(a_bkpf, promoList, a_userID, a_tcode, cfo);
			// System.out.println("Vizov metoda - addPromoMaterials -
			// Success!");

			// is.updateWriteoffDocFromContract(a_contract, promoList);
			is.onChangeContractPromo(a_contract, promoList, a_userID);

			/*
			 * System.out.println("Creating contract history!"); for
			 * (ContractHistory wa_ch : a_chl) { wa_ch.setUser_id(a_userID);
			 * wa_ch.setProcessed(1); chDao.create(wa_ch); }
			 * System.out.println("Contract history created!");
			 */
			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Transactional
	public boolean updateDealerDiscount(Contract con, PaymentSchedule a_ps[], ContractHistory a_ch, User userData,
			Long transactionId, String trCode) throws DAOException {
		try {
			PermissionController.canWrite(userData, transactionId);

			List<ContractFinOperations> contrFOList = new ArrayList<ContractFinOperations>();
			ContractFinOperations cfo = new ContractFinOperations();

			boolean remove = true;
			// SKIDKA_OT_DILERA
			if (con.getDealer_subtract() > 0) {
				int skidkaOtDileraKlientu = 11;//Скидка от дилера клиенту
				
				Bonus wa_bonus  = bonDao.dynamicFindBonus(" bonus_type_id = "+skidkaOtDileraKlientu+" and branch_id = "+con.getBranch_id());
				double summaSkidkaOtDileraKlientu = 0;
				if (wa_bonus==null){

					throw new Exception("Скидка от дилера клиенту не определена.");	
				}
				else
				{
					summaSkidkaOtDileraKlientu = wa_bonus.getCoef();
				}
				
				if (con.getDealer_subtract()>summaSkidkaOtDileraKlientu)
				{
					String info = "Сумма скидки от дилера должна быть в пределах: 0 и "+summaSkidkaOtDileraKlientu +" " + wa_bonus.getWaers();
					throw new Exception(info);			
				}
				remove = false;
				cfo = new ContractFinOperations();
				cfo.setWaers(con.getWaers());
				double dmbtr = con.getDealer_subtract() / con.getRate();
				double wrbtr = con.getDealer_subtract();
				cfo.setDmbtr(dmbtr);
				cfo.setWrbtr(wrbtr);
				cfo.setRate(con.getRate());
				Staff dealer = stfDao.find(con.getDealer());
				cfo.setCustomer_id(dealer.getCustomer_id());
				cfo.setStaff_id(con.getDealer());
				// cfo.setAwkey(con.getAwkey());
				cfo.setOper_type(4);
				String infoString = "Discount from Dealer";
				cfo.setOperationInfo(infoString);
				contrFOList.add(cfo);
			}

			String awk = String.valueOf(con.getAwkey());
			String wcl = "belnr = " + awk.substring(0, awk.length() - 4) + " and gjahr = "
					+ awk.substring(awk.length() - 4, awk.length()) + " and blart = 'GC'" + " and storno = 0";
			Bkpf p_bkpf = bkpfDao.dynamicFindSingleBkpf(wcl,con.getBukrs());

			if (remove) {
				financeServiceDMS.addDiscountFromDealer(p_bkpf, userData.getUserid(), trCode, null);
			} else {
				financeServiceDMS.addDiscountFromDealer(p_bkpf, userData.getUserid(), trCode, cfo);
			}

			// addDiscountFromDealer(Bkpf a_bkpf_GC, Long a_userID,String
			// a_tcode, ContractFinOperations a_contrFO)

			con.setUpdated_by(userData.getUserid());
			con.setUpdated_date(Calendar.getInstance().getTime());
			if (con.getPrice() == con.getPaid())
				con.setContract_status_id(new Long(ContractStatus.STATUS_CLOSED));
			conDao.update(con);

			// ************** Payment Schedule
			// int j = a_ps.length - 1;
			// double skidka_sum = con.getDealer_subtract();
			// double paying = 0;
			//
			// con.setPaid(skidka_sum);
			// conDao.update(con);
			//
			// while (skidka_sum > 0) {
			// paying = a_ps[j].getSum();
			// if (skidka_sum < paying) {
			// paying = skidka_sum;
			// }
			// a_ps[j].setPaid(paying);
			// skidka_sum -= paying;
			// j--;
			// }
			//
			// // ************** Create Payment Schedule
			// for (int i = 0; i <= con.getPayment_schedule(); i++) {
			// a_ps[i].setAwkey(con.getAwkey());
			// a_ps[i].setBukrs(con.getBukrs());
			// a_ps[i].setPayment_schedule_id(null);
			// psDao.create(a_ps[i]);
			// }

			System.out.println("Creating contract history!");
			a_ch.setUser_id(userData.getUserid());
			a_ch.setProcessed(1);
			chDao.create(a_ch);

			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public boolean SaveContractHistory(Long con_id, List<ContractHistory> a_chl, Long a_userId) throws DAOException {
		try {
			System.out.println("Creating contract history!");
			for (ContractHistory wa_ch : a_chl) {
				wa_ch.setContract_id(con_id);
				wa_ch.setUser_id(a_userId);
				wa_ch.setProcessed(1);
				chDao.create(wa_ch);
			}
			System.out.println("Contract history created!");
			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public boolean AddRemoveRecommender(Contract a_con, Contract a_oldCon, double refRate, String refWaers,
			String refWaersDiscount, Bkpf a_bkpf_GC, Long a_userID, String a_tcode) throws DAOException {
		try {
			if (a_oldCon.getRef_contract_id() != null && a_oldCon.getRef_contract_id() > 0) {
				financeServiceDMS.addDiscountToRecommender(a_bkpf_GC, a_userID, a_tcode, null);
				financeServiceDMS.addRewardToRecommender(a_bkpf_GC, a_userID, a_tcode, null);
			}

			ContractFinOperations cfo = new ContractFinOperations();
			// Promo_Materials_list_into_FO
			double dmbtr = 0;
			double wrbtr = 0;

			// SKIDKA_RECOMENDATELYA_FinOperList
			if (a_con.getRef_contract_id() != null && a_con.getRef_contract_id() > 0) {

				ContractType ct = ctDao.find(a_con.getContract_type_id());
				Contract refContr = conDao.find(a_con.getRef_contract_id());
				Branch conBr = brDao.find(a_con.getBranch_id());
				// Branch refBr = brDao.find(refContr.getBranch_id());
				ContractType refContrType = ctDao.find(refContr.getContract_type_id());

				// System.out.println("REF CnotractType: " +
				// ref_contr.getContrType().getName());
//				String wcl = " bukrs = '" + a_con.getBukrs() + "' and bonus_type_id = 8 and position_id = -1 "
//						+ " and matnr = " + ct.getMatnr() + " and country_id = " + conBr.getCountry_id();

				int skidkaRek = 16;//Скидка от дилера клиенту
				String wcl = " bonus_type_id = "+skidkaRek+" and branch_id = " + a_con.getBranch_id();

				List<Bonus> bL = bonDao.dynamicFindBonuses(wcl);
				Bonus skidka_rek = new Bonus();
				if (bL.size() > 0) {
					skidka_rek = bL.get(0);
					System.out.println("Skidka amount: " + skidka_rek.getCoef() + " " + skidka_rek.getWaers());
				} else
					throw new DAOException("Couldn't find Referencer's discount in Bonus Table!");

				String refInfoString = "";
				double debt = refContr.getPrice() - refContr.getPaid();

//				double skidka_toRek = skidka_rek.getCoef();
//				double vozn = skidka_toRek - debt / refRate;
//				if (vozn > 0) {
//					skidka_toRek -= vozn;
//				}
				
				double skidka_toRek = 0;
				double vozn = 0;
				
				double skSumma = skidka_rek.getCoef();
				String skWaers = skidka_rek.getWaers();
				
//				if (!skWaers.equals("USD") && !skWaers.equals(a_con.getWaers()))
//				{
//					throw new DAOException("В Бонусной таблице неправильно указана валюта");
//				}
//				double inRate = exchangeRateDao.getLastCurrencyRateInternal(a_con.getBukrs(), "USD", a_con.getWaers());
				int inRate = 1;
				if (debt>0){
					if(!refContr.getWaers().equals("USD") && refContr.getWaers().equals(skWaers))
					{
						skidka_toRek = skSumma;
						vozn = skidka_toRek - debt;
						if (vozn > 0) {
							skidka_toRek = skidka_toRek -vozn;
						}
						refWaers = skWaers;
					}
					else if (refContr.getWaers().equals("USD") && !refContr.getWaers().equals(skWaers)){
						skidka_toRek = skSumma/inRate;
						skidka_toRek = GeneralUtil.round(skidka_toRek, 2);
						vozn = skidka_toRek - debt;
						if (vozn > 0) {
							skidka_toRek = skidka_toRek -vozn;
						}
						vozn=vozn*inRate;
						refWaers = refContr.getWaers();
					}
					
				}
				else
				{
					if (skWaers.equals("USD"))
					{
						
						vozn = inRate*skSumma;
						skWaers = a_con.getWaers();
						
					}
					else if (skWaers.equals(a_con.getWaers())){
						vozn = skSumma;
					}
						
					
				}

				System.out.println("debt = " + debt);
				// SKIDKA REKOMENDATELYU
				if (debt > 0) {
					System.out.println("Inside DEBT > 0");
					cfo = new ContractFinOperations();
					cfo.setWaers(refWaers);

					dmbtr = skidka_toRek;
					System.out.println("SKIDA DMBTR: " + dmbtr);
					wrbtr = skidka_toRek;
					System.out.println("SKIDA WRBTR: " + wrbtr);

					cfo.setDmbtr(dmbtr);
					cfo.setWrbtr(wrbtr);
					cfo.setRate(1);
					cfo.setCustomer_id(refContr.getCustomer_id());
					cfo.setMatnr(refContrType.getMatnr());
					cfo.setAwkey(refContr.getAwkey());
					cfo.setContractNumber(refContr.getContract_number());
					cfo.setOper_type(1);
					refInfoString = "Discount SN:" + a_con.getContract_number();
					cfo.setOperationInfo(refInfoString);
					financeServiceDMS.addDiscountToRecommender(a_bkpf_GC, a_userID, a_tcode, cfo);
				}

				// VOZNAGRAJDENIE KLIENTU
				System.out.println("vozn = " + vozn);
				if (vozn > 0) {
					System.out.println("Inside VOZN > 0");
					cfo = new ContractFinOperations();
					cfo.setWaers(skWaers);
					dmbtr = vozn;
					wrbtr = vozn;
					cfo.setDmbtr(dmbtr);
					cfo.setWrbtr(wrbtr);
					cfo.setRate(1);
					cfo.setCustomer_id(refContr.getCustomer_id());
					cfo.setAwkey(refContr.getAwkey());
					cfo.setContractNumber(a_con.getContract_number());
					cfo.setOper_type(2);
					refInfoString = "Reward from SN:" + a_con.getContract_number();
					cfo.setOperationInfo(refInfoString);
					System.out.println("Call addRewardToRecommender!");
					System.out.println("BELNR: " + a_bkpf_GC.getBelnr());
					financeServiceDMS.addRewardToRecommender(a_bkpf_GC, a_userID, a_tcode, cfo);
					System.out.println("Success addRewardToRecommender!");
				}
			}

			conDao.update(a_con);
			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public boolean updateContract(Contract a_con, PaymentSchedule a_ps[], User userData, String locale)
			throws DAOException {
		try {
			if (conVal.validateContractEssentials(a_con, a_ps, 180, " ", " ", locale, false)) {
				Calendar curDate = Calendar.getInstance();
				a_con.setUpdated_date(curDate.getTime());
				a_con.setUpdated_by(userData.getUserid());
				conDao.update(a_con);
				
				if ((a_con.getBranch_id()==25L ||
						a_con.getBranch_id()==26L ||
								a_con.getBranch_id()==27L ||
										a_con.getBranch_id()==28L ||
												a_con.getBranch_id()==105L ||
														a_con.getBranch_id()==106L))
				{
					crmDocDemoService.contractUpdated(a_con);
				}
				
				if (a_con.getContract_status_id() == 17) {
					mlService.doResold(a_con, userData);
				}
			}
			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public boolean changeContractTovar(Contract a_con, Contract a_oldCon, Bkpf a_bkpf_GC, Long a_userID, String a_tcode)
			throws DAOException {
		try {
			if (!Validation.isEmptyString(a_con.getTovar_serial())) {

				// System.out.println("Promotions Count: " + promoList.size());
				is.updateWriteoffDocFromContract(a_con, null);

				// is.createWriteoffDocFromContract(con, promoList);
				conDao.update(a_con);
			}

			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public boolean updateContractPrice(Contract a_contract, Bkpf a_old_bkpf, Branch a_branch, User userData,
			String a_tcode, String a_waers, double a_kursf, double a_dmbtr, double a_wrbtr, Long a_matnr,
			PaymentSchedule a_ps[], ContractHistory a_ch) throws DAOException {
		try {
			
			Long oldAwkey = a_contract.getAwkey();
			
			System.out.println(
					"***************************************************************************************** ");
			System.out.println("Here are all parameters going to financeService: ");
			System.out.println("Contract Number: " + a_contract.getContract_number());
			System.out.println("Branch: " + a_branch.getText45());
			System.out.println("User_id: " + userData.getUserid());
			System.out.println("Transaction: " + a_tcode);
			System.out.println("Currency: " + a_waers);
			System.out.println("Exchange Rate USD: " + a_kursf);
			System.out.println("Price USD: " + a_dmbtr);
			System.out.println("Price LOCAL: " + a_wrbtr);
			System.out.println(
					"***************************************************************************************** ");

			Long awkey = financeServiceDMS.recreateContract(a_contract, a_old_bkpf, a_branch, userData.getUserid(), a_tcode,
					a_waers, a_kursf, a_dmbtr, a_wrbtr, a_matnr);

			// ********************_Remove_OLD_Paymetn_schedule_***********************

			System.out.println(awkey);
			System.out.println("Metod - createContractRecievable Success!");
			System.out.println(
					"***************************************************************************************** ");

			psDao.deleteByAwkey(a_contract.getAwkey(),a_contract.getBukrs());

			a_contract.setUpdated_by(userData.getUserid());
			a_contract.setUpdated_date(Calendar.getInstance().getTime());
			a_contract.setNew_contract_date(Calendar.getInstance().getTime());
			a_contract.setAwkey(awkey);
			if (a_contract.getPrice() == a_contract.getPaid())
				a_contract.setContract_status_id(new Long(ContractStatus.STATUS_CLOSED));
			conDao.update(a_contract);

			// ************** Create Payment Schedule
			System.out.println("Creating new payment schedule!");
			for (PaymentSchedule wa_ps : a_ps) {
				wa_ps.setPayment_schedule_id(null);
				wa_ps.setAwkey(awkey);
				wa_ps.setBukrs(a_contract.getBukrs());
				psDao.create(wa_ps);

				/*
				 * System.out.println(i + " Month: " + a_ps[i].getSum() +
				 * " Date: " + a_ps[i].getPayment_date() + " Awkey: " +
				 * a_ps[i].getAwkey() + " Bukrs: " + a_ps[i].getBukrs());
				 */
			}
			System.out.println("New payment schedule created!");

			System.out.println("Creating contract history!");
			a_ch.setUser_id(userData.getUserid());
			a_ch.setProcessed(1);
			a_ch.setId(null);
			chDao.create(a_ch);

			if (a_contract.getPrice() == a_contract.getPaid())
				financeServiceDMS.updateContract(a_contract.getAwkey(),a_contract.getBukrs());

			System.out.println("Contract history created!");

			// Онласын айтады, бир нарсе шакыру керек
			is.onChangeContractAwkey(oldAwkey, awkey, userData);

			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public boolean updateContractPaymentGraphic(Contract a_contract, PaymentSchedule a_ps[], ContractHistory a_ch,
			Long a_userId) throws DAOException {
		try {
			System.out.println("Updating Contract!");
			conDao.update(a_contract);
			System.out.println("Contract updated!");

			System.out.println("Deleting old payment schedule!");
			psDao.deleteByAwkey(a_contract.getAwkey(),a_contract.getBukrs());
			System.out.println("Old payment schedule deleted!");

			// ************** Create Payment Schedule
			System.out.println("Creating new payment schedule!");
			for (PaymentSchedule wa_ps : a_ps) {
				wa_ps.setPayment_schedule_id(null);
				wa_ps.setAwkey(a_contract.getAwkey());
				wa_ps.setBukrs(a_contract.getBukrs());
				System.out.println(wa_ps);
				psDao.create(wa_ps);

				/*
				 * System.out.println(i + " Month: " + a_ps[i].getSum() +
				 * " Date: " + a_ps[i].getPayment_date() + " Awkey: " +
				 * a_ps[i].getAwkey() + " Bukrs: " + a_ps[i].getBukrs());
				 */
			}
			System.out.println("New payment schedule created!");

			System.out.println("Creating contract history!");
			a_ch.setUser_id(a_userId);
			a_ch.setProcessed(1);
			a_ch.setId(null);
			chDao.create(a_ch);

			System.out.println("Contract history created!");
			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	public boolean CancelContract(Contract a_contract, Bkpf a_bkpf, double a_kursf, double a_dmbtr, double a_wrbtr,
			User userData, String a_tcode, String info) throws DAOException {
		try {
			Contract con = a_contract;
			// *************************** Load payment schedule

			Calendar cal = Calendar.getInstance();
			con.setContract_status_id(3L);
			con.setUpdated_by(userData.getUserid());
			con.setCancel_date(cal.getTime());

			String info1 = info;
			if (con.getInfo() != null) {
				if (con.getInfo().length() > 0) {
					info1 = info1 + " | " + con.getInfo();
				}
				con.setInfo(info1);
			}
			con.setUpdated_date(Calendar.getInstance().getTime());
			conDao.update(con);

			// ***********************************************************
			System.out.println("Sending for CancelContract!");
			financeServiceDMS.cancelContract(con, a_bkpf, a_kursf, a_dmbtr, a_wrbtr, userData.getUserid(), a_tcode);
			System.out.println("Success!");

			// Contract History

			ContractHistory wa_ch = new ContractHistory();

			wa_ch.setContract_id(a_contract.getContract_id());
			wa_ch.setRec_date(cal.getTime());
			wa_ch.setOld_id(a_contract.getContract_id());
			wa_ch.setOper_on(1L);
			wa_ch.setOper_type(3L);

			String wa_info = "Contract was Cancelled. " + a_contract.getInfo();
			wa_info = wa_info.substring(0, Math.min(wa_info.length(), 250));

			wa_ch.setInfo(wa_info);
			wa_ch.setProcessed(0);
			wa_ch.setUser_id(userData.getUserid());

			chDao.create(wa_ch);

			is.createReturnDocFromContract(con, userData);
			
			if ((con.getBranch_id()==25L ||
					con.getBranch_id()==26L ||
							con.getBranch_id()==27L ||
									con.getBranch_id()==28L ||
											con.getBranch_id()==105L ||
													con.getBranch_id()==106L))
			{
				crmDocDemoService.contractUpdated(con);
			}
			
			

			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public boolean forWriteOffAssets(Contract contract, List<MatnrList> matnrList_L, Long userId) throws DAOException {
		try {
			// Contract contract = conDao.findByContractNumber(a_con_number);
			if (contract != null) {
				Long mainAssetId = ctDao.find(contract.getContract_type_id()).getMatnr();
				for (MatnrList wa_ml : matnrList_L) {
					Matnr matnr = matnrDao.find(wa_ml.getMatnr());
					String wa_info = "The material " + matnr.getText45();
					if (wa_ml.getMatnr().equals(mainAssetId)) {
						contract.setMatnr_release_date(Calendar.getInstance().getTime());
						contract.setMatnr_list_id(wa_ml.getMatnr_list_id());
						contract.setTovar_serial(wa_ml.getBarcode());
						contract.setUpdated_date(Calendar.getInstance().getTime());
						contract.setUpdated_by(userId);
						contract.setLast_state(Contract.LASTSTATE_GIVEN);
						conDao.update(contract);

						wa_info += " SN: " + wa_ml.getBarcode();
						
						if (contract.getTovar_category() == ContractType.TOVARCAT_VACUUM_CLEANER) {
							createServFilterVCForNewContract(contract);
						}							
					}
					wa_info += " was given to customer.";

					// Contract History
					ContractHistory wa_ch = new ContractHistory();

					Calendar cal = Calendar.getInstance();
					wa_ch.setContract_id(contract.getContract_id());
					wa_ch.setRec_date(cal.getTime());
					wa_ch.setNew_id(wa_ml.getMatnr());
					wa_ch.setOper_on(30L);
					wa_ch.setOper_type(4L);

					wa_info = wa_info.substring(0, Math.min(wa_info.length(), 250));

					wa_ch.setInfo(wa_info);
					wa_ch.setProcessed(1);
					wa_ch.setUser_id(userId);

					chDao.create(wa_ch);

				}
			} else {
				throw new DAOException("Contract is empty!");
			}
			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}

	}
	
	@Autowired
	ServFilterVCDao sfvcDao;
	
	@Transactional 
	public boolean createServFilterVCForNewContract(Contract a_con) throws Exception {
		try {
			
			ServFilterVC sf = sfvcDao.findByTovarSN(a_con.getBukrs(),
					a_con.getTovar_serial());
			if (sf == null)
				sf = new ServFilterVC();

			sf.setContract_id(a_con.getContract_id());
			sf.setTovar_sn(a_con.getTovar_serial());
			sf.setActive((byte) 1);
			sf.setCrm_category(1);
			sf.setBukrs(a_con.getBukrs());
			sf.setServ_branch(a_con.getServ_branch_id());
			sf.setFno(1);

			Calendar cal = Calendar.getInstance();
//			ServZFBranchMT szfmt = szfmtDao.findLastMTByBranch(a_service.getBranch_id());
//
//			if (szfmt == null) {
//				throw new Exception("Branch filter change default month-terms are not set!");
//			}
			
			sf.setF1_date(a_con.getMatnr_release_date());
//			sf.setF1_sid(a_con.getId());
			sf.setF1_mt(ServFilterVC.F1_MT);
			cal.setTime(a_con.getMatnr_release_date());
			cal.add(Calendar.MONTH, sf.getF1_mt());
			sf.setF1_date_next(cal.getTime());

			if (sf.getId() != null && sf.getId() > 0)
				sfvcDao.update(sf);
			else
				sfvcDao.create(sf);
			
			// ***************************************************************
			
			return true;
		} catch(Exception ex) {
			throw new Exception(ex.getMessage());
		}				
	}
	

	@Override
	public boolean forContractReturnMatnr(Long a_con_number, List<Long> matnrId_list, String info, Long userId)
			throws DAOException {
		try {
			Contract con = conDao.findByContractNumber(a_con_number);
			Long mainAssetId = ctDao.find(con.getContract_type_id()).getMatnr();
			
			
			
			if (con != null) {
				for (Long wa_mId : matnrId_list) {
					Matnr matnr = matnrDao.find(wa_mId);
					String wa_info = "The material " + matnr.getText45();

					if (wa_mId.equals(mainAssetId)) {
						con.setLast_state(3);
						con.setUpdated_by(userId);
						String info1 = info;

						if (!Validation.isEmptyString(info1)) {
							if (con.getInfo().length() > 0) {
								info1 = info1 + " | " + con.getInfo();
							}
							info1 = info1.substring(0, Math.min(info1.length(), 250));
							con.setInfo(info1);
						}
						con.setUpdated_date(Calendar.getInstance().getTime());
						// con.setTovar_serial("");
						if(matnr.getType()==1)
						{
							con.setTovar_serial("");
							con.setMatnr_list_id(null);
							con.setMatnr_release_date(null);
						}
						
						conDao.update(con);
						wa_info += " SN: " + con.getTovar_serial();
					}
					else if(matnr.getType()==8)
					{
						Long a_contract_promos_id = cpDao.findContrID(con.getContract_id(), matnr.getMatnr());
						if (a_contract_promos_id!=null)
						{
							cpDao.delete(a_contract_promos_id);
						}else
						{
							throw new DAOException("Свяжитесь с администратором -> ContractService line 1069");
						}
						
						
					}
					else
					{
						throw new DAOException("Свяжитесь с администратором -> ContractService line 1076");
					}
					
					
					wa_info += " has been Returned from customer.";

					// Contract History
					Calendar cal = Calendar.getInstance();
					ContractHistory wa_ch = new ContractHistory();

					wa_ch.setContract_id(con.getContract_id());
					wa_ch.setRec_date(cal.getTime());
					wa_ch.setOld_id(wa_mId);
					wa_ch.setOper_on(30L);
					wa_ch.setOper_type(5L);
					wa_info = wa_info.substring(0, Math.min(wa_info.length(), 250));
					wa_ch.setInfo(wa_info);
					wa_ch.setProcessed(1);
					wa_ch.setUser_id(userId);
					chDao.create(wa_ch);
				}
			} else {
				throw new DAOException("Contract #" + a_con_number + " not found!");
			}
			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public double countTotalPaid(Contract con) throws DAOException {
		try {
			double sum_paid = 0;

			System.out.println("Counting paid sum for Contract #" + con.getContract_number());
			String dynamicWhereClause = " awkey = " + con.getAwkey() + " and storno = 0";
			List<Bkpf> l_bkpfPayments = bkpfDao.dynamicFindBkpf(dynamicWhereClause);

			if (l_bkpfPayments.size() > 0) {
				for (Bkpf wa_bkpf1 : l_bkpfPayments) {
					System.out.println("Blart: " + wa_bkpf1.getBlart());
					if (wa_bkpf1.getBlart().equals("CP")) {
						System.out.println("Wrbtr: " + wa_bkpf1.getWrbtr());
						sum_paid += wa_bkpf1.getWrbtr();
					}
				}
			}

			System.out.println("Paid sum: " + sum_paid);
			return sum_paid;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public double countTotalSkidka(Contract con) throws DAOException {
		try {
			double sum_wrbtr = 0;

			String dynamicWhereClause = "";
			dynamicWhereClause = dynamicWhereClause + "awkey = " + con.getAwkey();
			List<Bkpf> l_bkpfPayments = bkpfDao.dynamicFindBkpf(dynamicWhereClause);

			if (l_bkpfPayments.size() > 0) {
				for (Bkpf wa_bkpf1 : l_bkpfPayments) {
					if (wa_bkpf1.getBlart().equals("SK") || wa_bkpf1.getBlart().equals("SD")
							|| wa_bkpf1.getBlart().equals("SR")) {
						sum_wrbtr += wa_bkpf1.getWrbtr();
					}
				}
			}

			System.out.println("Skidka sum: " + sum_wrbtr);
			return sum_wrbtr;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	// *******************************************************************************************************
	// ****************************************SEARCH_BY_CONTRACT_NUMBER**************************************
	// *******************************************************************************************************

	@Override
	public Contract searchByContractNumber(Long a_contract_number) throws DAOException {
		Contract con = new Contract();
		try {
			int count = 0;
			count = conDao.countContractByNumber(a_contract_number).intValue();
			if (count == 0) {
				throw new DAOException("No contract found");
			} else if (count == 1) {
				con = conDao.findByContractNumber(a_contract_number);
				return con;
			} else {
				throw new DAOException("Unexpected error");
			}
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	// *******************************************************************************************************
	// **************************************GET_CUSTOMER_BY_ID***********************************************
	// *******************************************************************************************************

	@Override
	public Customer getCustomerById(Long a_customer_id) throws DAOException {
		Customer cus = new Customer();
		try {
			cus = cusDao.find(a_customer_id);
			return cus;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	// *******************************************************************************************************
	// **************************************GET_STUFF_BY_ID**************************************************
	// *******************************************************************************************************

	@Override
	public Staff getStaffById(Long a_staff_id) throws DAOException {
		Staff stf = new Staff();
		try {
			stf = stfDao.find(a_staff_id);
			return stf;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	// *******************************************************************************************************

	@Transactional
	public boolean changeDealer(Contract a_con, Long a_old_dealer_staff_id, Long a_new_dealer_staff_id, Long a_userID,
			String tCode) throws DAOException {
		try {
			financeServiceDMS.changeDealer(a_con, a_old_dealer_staff_id, a_new_dealer_staff_id, a_userID, tCode);
			conDao.update(a_con);
			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	// ******************************************************************************************************

	@Transactional
	public boolean createCutoffpriceContract(Contract con, Branch a_branch, User userData, Long a_trId, String a_trcode,
			String a_currency, double a_kurs, double a_USD, double a_LocalPrice, Long a_matnrId, PaymentSchedule a_ps[],
			String locale) throws DAOException {
		try {
			PermissionController.canWrite(userData, a_trId);

			if (conVal.validateContract(con, a_ps, 1, "", "", locale, true, userData)) {
				ContractType ct1 = ctDao.find(con.getContract_type_id());
				con.setTovar_category(ct1.getTovar_category());

				con.setWaers(a_currency);
				con.setRate(a_kurs);
				con.setPaid(0);
				con.setSkidka_vol(0L);
				con.setLast_state(1);
				Calendar cal = Calendar.getInstance();
				con.setCreated_date(cal.getTime());
				con.setOld_id(0L);
				con.setOld_sn(0L);
				con.setCreated_by(userData.getUserid());
				con.setMtConfirmed(Contract.MT_CONFIRMED_FALSE);
				con.setMarkedType(Contract.MT_CUT_OFF_PRICE);

				conDao.create(con);

				// Updating contract number
				String conNumber = "";
				conNumber = con.getContract_id().toString();
				con.setContract_number(Long.parseLong(conNumber));
				conDao.update(con);
				
				createPaymentScheduleTemporary(con, a_ps);

				return true;
			} else
				return false;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	@Transactional
	public boolean approveCutoffpriceContract(Contract con, Branch a_branch, User userData, Long a_trId,
			String a_trCode, String a_currency, double a_kurs, double a_USD, double a_LocalPrice, Long a_matnrId,
			PaymentSchedule a_ps[], String locale) throws DAOException {
		try {
			PermissionController.canWrite(userData, a_trId);
			if (conVal.validateContract(con, a_ps, 1,a_currency, "", locale, true, userData)) {

				con.setMtConfirmed(Contract.MT_CONFIRMED_APPROVED);

				System.out.println(
						"***************************************************************************************** ");
				System.out.println("Here are all parameters going to financeService: ");
				System.out.println("Contract Number: " + con.getContract_number());
				System.out.println("Branch: " + a_branch.getText45());
				System.out.println("User_id: " + userData.getUserid());
				System.out.println("Transaction: " + a_trCode);
				System.out.println("Currency: " + a_currency);
				System.out.println("Exchange Rate USD: " + a_kurs);
				System.out.println("Price USD: " + a_USD);
				System.out.println("Price LOCAL: " + a_LocalPrice);
				System.out.println(
						"***************************************************************************************** ");

				List<ContractFinOperations> contrFOList = new ArrayList<ContractFinOperations>();
				ContractFinOperations cfo;

				System.out.println("Vizov metoda - createContractRecievable Success!");
				Long awkey = financeServiceDMS.createContractRecievable(con, a_branch, userData.getUserid(), a_trCode,
						a_currency, a_kurs, a_USD, a_LocalPrice, a_matnrId, null, null);

				System.out.println(awkey);
				System.out.println("Metod - createContractRecievable Success!");
				System.out.println(
						"***************************************************************************************** ");

				con.setAwkey(awkey);
				conDao.update(con);

				// ************** Create Payment Schedule
				pstDao.deleteByContractId(con.getContract_id());
				createPaymentSchedule(con, a_ps);				

				is.createWriteoffDocFromContract(con, null);

				return true;
			} else
				return false;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	// *************************************************************************************************************
	
	@Transactional
	public boolean createUniversalContract(Contract con, Branch a_branch, List<Payroll> pl, User userData, Long a_trId, String a_trcode,
			String a_currency, double a_kurs, double a_USD, double a_LocalPrice, Long a_matnrId, PaymentSchedule a_ps[],
			String locale) throws DAOException {
		try {
			PermissionController.canWrite(userData, a_trId);

			if (conVal.validateContract(con, a_ps, 1, "", "", locale, true, userData)) {
				ContractType ct1 = ctDao.find(con.getContract_type_id());
				con.setTovar_category(ct1.getTovar_category());

				con.setWaers(a_currency);
				con.setRate(a_kurs);
				con.setPaid(0);
				con.setSkidka_vol(0L);
				con.setLast_state(1);
				Calendar cal = Calendar.getInstance();
				con.setCreated_date(cal.getTime());
				con.setOld_id(0L);
				con.setOld_sn(0L);
				con.setCreated_by(userData.getUserid());
				con.setMtConfirmed(Contract.MT_CONFIRMED_FALSE);
				con.setMarkedType(Contract.MT_CUT_OFF_PRICE);

				conDao.create(con);

				// Updating contract number
				String conNumber = "";
				conNumber = con.getContract_id().toString();
				con.setContract_number(Long.parseLong(conNumber));
				conDao.update(con);
				
				
				createPaymentScheduleTemporary(con, a_ps);

//				if (pl == null || pl.size()==0) throw new DAOException("Пожалуйта укажите премии!");
				if (pl != null && pl.size() > 0) {
					createPayrollTemporary(pl, con.getContract_number(), a_trcode, userData);
				}
				
				return true;
			} else
				return false;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

//	@Autowired 
//	PayrollDao plDao;
	
	private void createPayrollTemporary(List<Payroll> pl, Long contractNumber, String a_trcode, User userData) {
		try {
			for (Payroll p:pl) {
				p.setContract_number(contractNumber);
				//plDao.create(p);				
			}			
			financeServiceDMS.universalContractCreatePayroll(pl, a_trcode, userData.getUserid());			
		} catch(DAOException ex) {
			throw new DAOException(ex);
		}
	}
	
	@Transactional
	public boolean approveUniversalContract(Contract con, Branch a_branch, User userData, Long a_trId,
			String a_trCode, String a_currency, double a_kurs, double a_USD, double a_LocalPrice, Long a_matnrId,
			PaymentSchedule a_ps[], String locale) throws DAOException {
		try {
			PermissionController.canWrite(userData, a_trId);
			if (conVal.validateContract(con, a_ps, 1,a_currency, "", locale, true, userData)) {

				con.setMtConfirmed(Contract.MT_CONFIRMED_APPROVED);

				System.out.println(
						"***************************************************************************************** ");
				System.out.println("Here are all parameters going to financeService: ");
				System.out.println("Contract Number: " + con.getContract_number());
				System.out.println("Branch: " + a_branch.getText45());
				System.out.println("User_id: " + userData.getUserid());
				System.out.println("Transaction: " + a_trCode);
				System.out.println("Currency: " + a_currency);
				System.out.println("Exchange Rate USD: " + a_kurs);
				System.out.println("Price USD: " + a_USD);
				System.out.println("Price LOCAL: " + a_LocalPrice);
				System.out.println(
						"***************************************************************************************** ");

				List<ContractFinOperations> contrFOList = new ArrayList<ContractFinOperations>();
				ContractFinOperations cfo;

				System.out.println("Vizov metoda - createContractRecievable Success!");
				Long awkey = financeServiceDMS.createContractRecievable(con, a_branch, userData.getUserid(), a_trCode,
						a_currency, a_kurs, a_USD, a_LocalPrice, a_matnrId, null, null);

				System.out.println(awkey);
				System.out.println("Metod - createContractRecievable Success!");
				System.out.println(
						"***************************************************************************************** ");

				con.setAwkey(awkey);
				conDao.update(con);

				// ************** Create Payment Schedule
				pstDao.deleteByContractId(con.getContract_id());
				createPaymentSchedule(con, a_ps);				

				is.createWriteoffDocFromContract(con, null);
				
				return financeServiceDMS.universalContractSavePayroll(con, "SAVE", a_trCode, userData.getUserid()); //OperType "SAVE" || "DELETE"
			} else
				return false;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Transactional
	private void createPaymentSchedule(Contract a_con, PaymentSchedule[] a_ps) throws Exception {
		if (!Validation.isEmptyLong(a_con.getAwkey())) {
			for (int i = 0; i <= a_con.getPayment_schedule(); i++) {
				a_ps[i].setAwkey(a_con.getAwkey());
				a_ps[i].setBukrs(a_con.getBukrs());
				a_ps[i].setPayment_schedule_id(null);
				psDao.create(a_ps[i]);
			}
		} else {
			throw new Exception("Couldn't create Payment Schedule. Awkey is empty!");
		}
	}

	@Transactional
	private void createPaymentScheduleTemporary(Contract a_con, PaymentSchedule[] a_ps) throws Exception {
		if (!Validation.isEmptyLong(a_con.getContract_id())) {
			for (int i = 0; i <= a_con.getPayment_schedule(); i++) {
				PaymentScheduleTemporary pst = new PaymentScheduleTemporary();
				pst.setId(null);
				pst.setBukrs(a_con.getBukrs());
				pst.setContractId(a_con.getContract_id());

				pst.setIsFirstPayment(a_ps[i].getIsFirstPayment());
				pst.setPaid(a_ps[i].getPaid());
				pst.setPayment_date(a_ps[i].getPayment_date());
				pst.setSum(a_ps[i].getSum());

				pstDao.create(pst);
			}
		} else {
			throw new Exception("Couldn't create Payment Schedule. Awkey is empty!");
		}
	}

	@Override
	public boolean declineCutoffpriceContract(Contract a_contract, User userData, Long transactionId)
			throws DAOException {
		try {
			PermissionController.canWrite(userData, transactionId);
			pstDao.deleteByContractId(a_contract.getContract_id());
			conDao.delete(a_contract.getContract_id());
			return true;
		} catch (DAOException ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Transactional
	public boolean createServiceContract(Contract a_con, String a_currency, 
			Branch a_branch, User userData, Long a_trId, String a_trCode) throws DAOException {
		try {
			PermissionController.canWrite(userData, a_trId);
			
			if (conVal.validateServiceContract(a_con, userData)) {
				ContractType ct1 = ctDao.find(a_con.getContract_type_id());
				a_con.setTovar_category(ct1.getTovar_category());

				a_con.setContract_status_id(new Long(ContractStatus.STATUS_FORSERVICE)); // 18 - Service contract
				a_con.setFinBranchId(a_con.getBranch_id());
				a_con.setWaers(a_currency);
				a_con.setRate(1);
				a_con.setPaid(0);
				a_con.setSkidka_vol(0L);
				a_con.setLast_state(Contract.LASTSTATE_GIVEN);
				Calendar cal = Calendar.getInstance();
				a_con.setCreated_date(cal.getTime());
				a_con.setOld_id(0L);
				a_con.setOld_sn(0L);
				a_con.setCreated_by(userData.getUserid());
				a_con.setMtConfirmed(Contract.MT_CONFIRMED_APPROVED);
				a_con.setMarkedType(Contract.MT_SERVICE);

				conDao.create(a_con);

				// Updating contract number
				String conNumber = "";
				conNumber = a_con.getContract_id().toString();
				a_con.setContract_number(Long.parseLong(conNumber));
				conDao.update(a_con);
				
				mlService.onCreateServiceContract(a_con, userData);
				
				return true;
			}			
			return false;
		} catch(Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
}