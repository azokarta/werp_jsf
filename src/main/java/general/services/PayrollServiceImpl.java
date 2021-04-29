package general.services;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//07.06.2016
//without cancel bonus, temp_payroll_archive
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.GeneralUtil;
import general.dao.BonusDao;
import general.dao.BranchDao;
import general.dao.ContractDao;
import general.dao.ContractTypeDao;
import general.dao.CurrencyDao;
import general.dao.CustomerDao;
import general.dao.DAOException;
import general.dao.ExchangeRateDao;
import general.dao.ExpenceTypeDao;
import general.dao.PayrollDao;
import general.dao.PriceListDao;
import general.dao.PyramidDao;
import general.dao.SalaryDao;
import general.dao.SalePlanDao;
import general.dao.StaffDao;
import general.dao.StaffExpenceDao;
import general.dao.StaffOfficialDataDao;
import general.dao.TempPayrollArchiveDao;
import general.dao.TempPayrollDao;
import general.output.tables.FaeaOutputTable;
import general.output.tables.FosaResultTable;
import general.tables.Bonus;
import general.tables.Branch;
import general.tables.Contract;
import general.tables.ContractType;
import general.tables.Currency;
import general.tables.ExchangeRate;
import general.tables.ExpenceType;
import general.tables.Payroll;
import general.tables.PriceList;
import general.tables.Pyramid;
import general.tables.Salary;
import general.tables.SalePlan;
import general.tables.Staff;
import general.tables.StaffExpence;
import general.tables.StaffOfficialData;
import general.tables.TempPayroll;












import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import crm.dto.CrmKpiFinanceDto;
import crm.services.CrmKpiReportService;

@Service("payrollService")
public class PayrollServiceImpl implements PayrollService{
	/*
	1	ru	Не работает
	2	ru	Разработчик
	3	ru	Менеджер
	4	ru	Дилер
	5	ru	Кординатор
	6	ru	Управляющий директор
	7	ru	Зав.склад
	8	ru	Демо секретарь
	9	ru	Взносщик
	10	ru	Директор
	11	ru	Установщик
	12	ru	Обучатель
	13	ru	Заменщик фильтров Ceb.
	15	ru	Гл. обучатель
	16	ru	Мастер Roboclean
	17	ru	Мастер Cebilon
	18	ru	Оператор зам. фильтер
	19	ru	Оператор профил. Rob.
	20	ru	Начальник Сервиса
	21	ru	Кординатор Сервис
	22	ru	Директор Сервис*/
	
	int dealer_pos_id=4;
	int manager_pos_id=3;
	int coordinator_pos_id=5;
	int demo_pos_id=8;
	int collector_pos_id=9;
	int director_pos_id=10;
	int trainer_pos_id=12;
	int main_trainer_pos_id=15;
	int assistant_director_pos_id=6;
	
	//bonus_type_id
	int premium = 1;
	int bonus = 2;
	int wage = 3;
	int opv = 4;
	int ipn = 5;
	int socnal = 6;
	int deposit = 7;
	int skidka = 8;
	int otmena = 9;
	int rashody = 10;
	int dolg = 13;
	int tradeIn1 = 17;
	
	@Autowired
	ContractDao conDao;
	
	@Autowired
	StaffDao stfDao;
	
	@Autowired
	SalaryDao slrDao;
	
	@Autowired
	PayrollDao prlDao;
	
	@Autowired
	CustomerDao cusDao;
	
	@Autowired
	ExchangeRateDao erDao;
	
	@Autowired
    private BonusDao bonDao;
	
	
	@Autowired
    private BranchDao brnDao;	 
	
	
	@Autowired
    private PriceListDao pirceListDao;
	
	@Autowired
    private TempPayrollDao tpDao; 
	
	
	@Autowired
	private TempPayrollArchiveDao tpaDao;
	
	@Autowired
	private CurrencyDao currencyDao;
	
	
	@Autowired
	private SalePlanDao salePlanDao;
	
	
	@Autowired
	private PyramidDao pyramidDao;
	
	
	@Autowired
    private ContractTypeDao conTypeDao;
	
	@Autowired
    private StaffExpenceDao stfExpDao;
	
	@Autowired
    private StaffOfficialDataDao stfOffDataDao;
	
	
	@Autowired
    private FinanceServicePayroll financeServicePayroll;
	
	@Autowired
    private ExpenceTypeDao expType;
	
	@Autowired
	private CrmKpiReportService marketingKpiReportService;
	
	//Wage = 97 type
	//Premium = 98 type
	//Bonus = 99 type
	public List<TempPayroll> applySalary (int a_monat, int a_gjahr, String a_bukrs) throws DAOException{
		try
		{	
//			String branch_id_string = "";
			String position_id_string = "";
			String matnr_string = "";
			String staff_id_string = "";
			String bonus_type_string = "";
			String branch_id_string = "";
			String pyramid_id_string = "";			
			Long key_long = null;
			int count_arc = 0;
			
			//List<TempPayrollArchive> l_tpa = new ArrayList<TempPayrollArchive>();
			String dynamicWhereClause = "";
			dynamicWhereClause = dynamicWhereClause + " bukrs = '"+a_bukrs+"'";
			dynamicWhereClause = dynamicWhereClause + " and gjahr = '"+a_gjahr+"'"; 
			dynamicWhereClause = dynamicWhereClause + " and monat = '"+a_monat+"'";
			count_arc = tpaDao.countDynamicSearch(dynamicWhereClause).intValue();

			
			//if (count_arc>0)
			//{
			//	l_tpa = tpaDao.dynamicSearch(dynamicWhereClause);
			//	//throw new DAOException("за "+a_monat+" месяц "+a_gjahr+" года ЗП, Бонусы, Премии начислены");
			//}
			
			
			
			if (count_arc==0)
			{	
			tpDao.deleteAllByBukrs(a_bukrs);
			//cbDao.deleteByBukrsGjahrMonth(a_bukrs,a_monat,a_gjahr);
			List<TempPayroll> l_tp = new ArrayList<TempPayroll>();
			List<ExchangeRate> l_er = new ArrayList<ExchangeRate>();	 
			List<Currency> l_currency = new ArrayList<Currency>();
			Calendar curDate = Calendar.getInstance();
			Calendar firstDay = Calendar.getInstance(); 
			Calendar lastDay = Calendar.getInstance();			  
			firstDay.set(a_gjahr, a_monat-1, 1);
			lastDay.set(a_gjahr, a_monat-1, firstDay.getActualMaximum(Calendar.DAY_OF_MONTH));
			
			if (a_monat > 12 || a_monat < 1){
				throw new DAOException("Month not selected");
			}
			if (a_gjahr > curDate.get(Calendar.YEAR) || a_gjahr < curDate.get(Calendar.YEAR)-1){
				throw new DAOException("Year not selected");
			}
			if (a_bukrs == null || a_bukrs.equals("0")){
				throw new DAOException("Company not selected");
			}
			
			
			
			//***************************************Get Ex. Rate*****************************************************
			if (curDate.getTime().after(lastDay.getTime()))
			{				
				l_er = erDao.getLastCurrencyRates(new java.sql.Date(lastDay.getTimeInMillis()));
			}
			else
			{
				l_er = erDao.getLastCurrencyRates(new java.sql.Date(curDate.getTimeInMillis()));
			}
			if(l_er.size()==0)
			{
				throw new DAOException("Currencies not found");
			}
			
			Map<String,ExchangeRate> l_er_map = new HashMap<String,ExchangeRate>();
			//Map<String,ExchangeRate> l_er_map_internal = new HashMap<String,ExchangeRate>();
			//Map<String,ExchangeRate> l_er_map_national = new HashMap<String,ExchangeRate>();
			String key_string = "";
			for (ExchangeRate wa_er:l_er)
			{
				//key_string = String.valueOf(wa_er.getType()) + wa_er.getSecondary_currency();
				//l_er_map.put(key_string, wa_er);
				
				if (wa_er.getType()==1)
				{
					key_string = String.valueOf(wa_er.getType()) + wa_er.getSecondary_currency();					
					l_er_map.put(key_string, wa_er);
				}
				else if (wa_er.getType()==2)
				{
					if (wa_er.getBukrs()==null || wa_er.getBukrs().length()<1)
					{
						throw new DAOException("Внутренний курс неправильный. Exrate ID: "+wa_er.getExrate_id());
					}
					if (wa_er.getBukrs().equals(a_bukrs))
					{
						key_string = String.valueOf(wa_er.getType()) + wa_er.getSecondary_currency();					
						l_er_map.put(key_string, wa_er);
					}
				}
			}
			l_currency = currencyDao.findAllWithCountries();
			
			Map<Long, ExchangeRate> map_er = new HashMap<Long, ExchangeRate>();
			Map<String, ExchangeRate> map_er_waers = new HashMap<String, ExchangeRate>();

			for(Currency wa_cur:l_currency)
			{
				ExchangeRate wa_er = new ExchangeRate();
				wa_er = l_er_map.get("2"+wa_cur.getCurrency());
				if (wa_er==null)
				{
					wa_er = l_er_map.get("1"+wa_cur.getCurrency());
				}

				if (wa_er!=null)
				{
					map_er.put(wa_cur.getP_country().getCountry_id(), wa_er);
					map_er_waers.put(wa_cur.getCurrency(), wa_er);
				}
			}
			//*********************************************************************************************************
			//***************************************Get Branch********************************************************
			Map<Long, Branch> map_branch = new HashMap<Long, Branch>();
			List<Branch> l_branch = new ArrayList<Branch>();
			l_branch = brnDao.findByBukrs(a_bukrs);
			//Branch main_branch = new Branch();
			for(Branch wa_branch:l_branch)
			{
				map_branch.put(wa_branch.getBranch_id(), wa_branch);
				if (wa_branch.getParent_branch_id()==null)
				{	
					//main_branch = wa_branch;
				}
			}
			//*********************************************************************************************************
			//***************************************Get Staff*********************************************************
			Map<Long, Staff> map_staff = new HashMap<Long, Staff>();
			List<Staff> l_staff = new ArrayList<Staff>();
			l_staff = stfDao.findAll();
			for(Staff wa_staff:l_staff)
			{
				map_staff.put(wa_staff.getStaff_id(), wa_staff);
			}			
			List<StaffExpence> l_se = new ArrayList<StaffExpence>();
			Map<Long,List<StaffExpence>> l_stf_exp_map = new HashMap<Long,List<StaffExpence>>();
			Map<Long,StaffOfficialData> l_stf_off_data_map = new HashMap<Long,StaffOfficialData>();
			//Map<String,StaffOfficialData> l_stf_off_data_to_be_charged_map = new HashMap<String,StaffOfficialData>();
			l_stf_off_data_map = stfOffDataDao.findAllMapByBukrs(a_bukrs);
			l_se = stfExpDao.findAllByBukrsDate(a_bukrs,GeneralUtil.getSQLDate(curDate));
			//System.out.println(l_stf_off_data_map.size());
			for (StaffExpence wa_se:l_se)
			{
				List<StaffExpence> wal_se = new ArrayList<StaffExpence>();
				wal_se = l_stf_exp_map.get(wa_se.getStaff_id());
				if (wal_se==null || wal_se.size()==0)
				{
					wal_se = new ArrayList<StaffExpence>();
					wal_se.add(wa_se);
					l_stf_exp_map.put(wa_se.getStaff_id(), wal_se);
				}
				else
				{
					wal_se.add(wa_se);
				}
			}
			
			List<ExpenceType> l_et = new ArrayList<ExpenceType>();
			Map<Long,ExpenceType> l_et_map = new HashMap<Long,ExpenceType>();
			l_et = expType.findAll();
			
			for (ExpenceType wa_et:l_et)
			{
				l_et_map.put(wa_et.getEt_id(), wa_et);
			}
			
			//*********************************************************************************************************
			
			List<Salary> l_salary = new ArrayList<Salary>(); 
			//l_salary = slrDao.findByBukrs (new java.sql.Date(firstDay.getTimeInMillis()), new java.sql.Date(lastDay.getTimeInMillis()), a_bukrs);
			l_salary = slrDao.findByBukrs (GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay), a_bukrs);
			
			//l_payroll = prlDao.findByMonatGjahr(a_monat, a_gjahr, a_bukrs);
			
			for(Salary wa_salary: l_salary){
				if(wa_salary.getEnd_date().equals(wa_salary.getBeg_date())){
					  continue;
				}
//				if(wa_salary.getAmount()==0){
//					  continue;
//				}
				if (wa_salary.getStaff_id().equals(19116L))//Salary currency must be USD or KZT Employee_id:4552)
				{
//					continue;
					System.out.println("Stop");
				}
				if (wa_salary.getStaff_id().equals(16964L))//Salary currency must be USD or KZT Employee_id:4552)
				{
					System.out.println("Stop");
				}
				double sal_amount = 0;
				if (wa_salary.getPosition_id() != 1 &&  (wa_salary.getEnd_date().after(firstDay.getTime()) || wa_salary.getEnd_date().equals(firstDay.getTime()))){
					//slrDao.findPreviousSalary(wa_salary.getSalary_id(), a_bukrs)
					//System.out.println(wa_salary.getCountry_id());
					
					Calendar wa_curDate = Calendar.getInstance();
					int dayStart = 0;
					int dayEnd = 0;
					int days= lastDay.get(Calendar.DAY_OF_MONTH);
					boolean fullMonth = false;
					
					ExchangeRate wa_er = map_er.get(map_branch.get(wa_salary.getBranch_id()).getCountry_id());
					if (wa_er==null)
					{
						System.out.println(wa_salary.getStaff_id());
						
						throw new DAOException("ExchangeRate not found for country id "+wa_salary.getCountry_id());
					}
					
					if (wa_salary.getBeg_date().equals(firstDay.getTime()) || wa_salary.getBeg_date().before(firstDay.getTime()))
					{
						dayStart = 0;
					}
					else
					{
						wa_curDate.setTime(wa_salary.getBeg_date());
						dayStart = wa_curDate.get(Calendar.DAY_OF_MONTH)-1;
					}
					
					if (wa_salary.getEnd_date().equals(lastDay.getTime()) || wa_salary.getEnd_date().after(lastDay.getTime()))
					{
						dayEnd = lastDay.get(Calendar.DAY_OF_MONTH);
					}
					else
					{
						wa_curDate.setTime(wa_salary.getEnd_date());
						dayEnd = wa_curDate.get(Calendar.DAY_OF_MONTH);
					}
					
					if (dayEnd-dayStart == days)
					{
						fullMonth = true;
					}
					else 
					{
						fullMonth = false;
					}	
					
					
					/*
					if (wa_salary.getWaers()==null || wa_salary.getWaers().isEmpty())
					{
						throw new DAOException("Currency does not exist");
					}

					if (wa_salary.getAmount()>0){
						if (fullMonth) sal_amount = wa_salary.getAmount(); 
						else sal_amount = wa_salary.getAmount()/days*(dayEnd-dayStart);
					}
					*/
					if (wa_salary.getWaers()!=wa_er.getSecondary_currency() && wa_salary.getWaers().equals("USD"))
					{
						if (wa_salary.getAmount()>0){
							//sal_amount = wa_salary.getAmount()*wa_er.getSc_value();
							if (fullMonth) sal_amount = wa_salary.getAmount()*wa_er.getSc_value();
							else sal_amount = (wa_salary.getAmount()*wa_er.getSc_value())/days*(dayEnd-dayStart);
						}
					}
					else if (wa_salary.getWaers().equals(wa_er.getSecondary_currency()))
					{
						if (wa_salary.getAmount()>0){
							//sal_amount = wa_salary.getAmount();
							if (fullMonth) sal_amount = wa_salary.getAmount(); 
							else sal_amount = wa_salary.getAmount()/days*(dayEnd-dayStart);
						}
					}
					else{
						throw new DAOException("Salary currency must be USD or "+wa_er.getSecondary_currency()+ " Employee_id:"+wa_salary.getStaff_id());
					}
					
					sal_amount = Math.round(sal_amount);
					
					TempPayroll wa_tp = new TempPayroll();
					wa_tp.setBukrs(wa_salary.getBukrs());
					wa_tp.setAmount(sal_amount);
					wa_tp.setWaers(wa_er.getSecondary_currency());
					wa_tp.setStaff_id(wa_salary.getStaff_id());
					wa_tp.setStaff_name(wa_salary.getP_staff().getLastname()+" "+wa_salary.getP_staff().getFirstname()+" "+wa_salary.getP_staff().getMiddlename());
					wa_tp.setSalary_id(wa_salary.getSalary_id());
					wa_tp.setBranch_id(wa_salary.getBranch_id());
					wa_tp.setCustomer_id(wa_salary.getP_staff().getCustomer_id());
					wa_tp.setGjahr(a_gjahr);
					wa_tp.setMonat(a_monat);
					wa_tp.setPosition_id(wa_salary.getPosition_id());
					wa_tp.setType(wage);
					wa_tp.setDrcrk("S");
					if (wa_salary.getBeg_date().before(firstDay.getTime()))
					{
						wa_tp.setFrom_date(firstDay.getTime());
					}
					else
					{
						wa_tp.setFrom_date(wa_salary.getBeg_date());
					}
					
					if (wa_salary.getEnd_date().after(lastDay.getTime()))
					{
						wa_tp.setTo_date(lastDay.getTime());
					}
					else
					{
						wa_tp.setTo_date(wa_salary.getEnd_date());
					}
					wa_tp.setTo_date(lastDay.getTime());
					Branch wa_branch = map_branch.get(wa_salary.getBranch_id());
					wa_tp.setBranch_name(wa_branch.getText45());
					
					
					wa_tp.setKursf(wa_er.getSc_value());
					wa_tp.setBldat(curDate.getTime());
					if (wa_salary.getStaff_id().equals(287L) 
							|| wa_salary.getStaff_id().equals(296L)
							|| wa_salary.getStaff_id().equals(13032L)
							|| wa_salary.getStaff_id().equals(298L))
					{
						wa_tp.setAmount(wa_salary.getAmount());
						wa_tp.setWaers(wa_salary.getWaers());
						wa_tp.setKursf(1);
					}
					
						
//						if(wa_salary.getStaff_id().equals(4655L))
//						{
//							System.out.println("here");
//						}//Salary currency must be USD or KZT Employee_id:4552
						
						List<StaffExpence> wal_se = l_stf_exp_map.get(wa_tp.getStaff_id());						
						if (wal_se!=null && wal_se.size()>0)
						{
							for(StaffExpence wa_se:wal_se)
							{
								if (wa_se.getSum()>0)
								{
									ExpenceType wa_et = l_et_map.get(wa_se.getEt_id());
									if (wa_et==null || wa_et.getCustomer_id()==null)
									{
										//throw new DAOException("Контрагент в типе расходов отсутствует. '"+wa_et.getName()+"'");
									}
									else
									{
										TempPayroll wa_tp_temp = new TempPayroll();
										BeanUtils.copyProperties(wa_tp, wa_tp_temp);
										wa_tp_temp.setDrcrk("H");
										if (wa_et.getEt_id().equals(8L)) wa_tp_temp.setType(dolg);
										else wa_tp_temp.setType(rashody);
										wa_tp_temp.setWaers(wa_se.getCurrency());
										wa_tp_temp.setAmount(wa_se.getSum());
										wa_tp_temp.setExp_cus_id(wa_et.getCustomer_id());
										wa_tp_temp.setText45(wa_et.getName());
										l_tp.add(wa_tp_temp);
									}
										
									
								}
								
							}
							l_stf_exp_map.remove(wa_tp.getStaff_id());
							wal_se = new ArrayList<StaffExpence>();
						}
						
						StaffOfficialData wa_sod = l_stf_off_data_map.get(wa_tp.getStaff_id());
						if (wa_sod!=null)
						{
							if (wa_sod.getIpn()>0)
							{	
								TempPayroll wa_tp_temp = new TempPayroll();
								BeanUtils.copyProperties(wa_tp, wa_tp_temp);
								wa_tp_temp.setType(ipn);
								wa_tp_temp.setDrcrk("H");
								wa_tp_temp.setWaers(wa_sod.getCurrency());
								wa_tp_temp.setAmount(wa_sod.getIpn());
								if (wa_sod.getCurrency().equals("KZT"))
								{
									wa_tp_temp.setExp_cus_id(243L);
									l_tp.add(wa_tp_temp);
								}if (wa_sod.getCurrency().equals("UZS"))
								{
									wa_tp_temp.setExp_cus_id(165891L);
									l_tp.add(wa_tp_temp);
								}
								if (wa_sod.getCurrency().equals("KGS"))
								{
									wa_tp_temp.setExp_cus_id(171085L);
									l_tp.add(wa_tp_temp);
								}
							}
							if (wa_sod.getPension()>0)
							{	
								TempPayroll wa_tp_temp = new TempPayroll();
								BeanUtils.copyProperties(wa_tp, wa_tp_temp);
								wa_tp_temp.setType(opv);
								wa_tp_temp.setDrcrk("H");
								wa_tp_temp.setWaers(wa_sod.getCurrency());
								wa_tp_temp.setAmount(wa_sod.getPension());								
								if (wa_sod.getCurrency().equals("KZT"))
								{
									wa_tp_temp.setExp_cus_id(242L);
									l_tp.add(wa_tp_temp);
								}
								if (wa_sod.getCurrency().equals("UZS"))
								{
									wa_tp_temp.setExp_cus_id(165892L);
									l_tp.add(wa_tp_temp);
								}

								if (wa_sod.getCurrency().equals("KGS"))
								{
									wa_tp_temp.setExp_cus_id(171086L);
									l_tp.add(wa_tp_temp);
								}
							}
//							if (wa_sod.getOsmsFromStaff()!=null && wa_sod.getOsmsFromStaff()>0 && wa_sod.getOsmsFromStaff()<wa_tp.getAmount())
//							{
//								wa_tp.setAmount(wa_tp.getAmount()-wa_sod.getOsmsFromStaff());
//								wa_tp.setText45(wa_tp.getText45()+" ОСМС:"+wa_sod.getOsmsFromStaff());
//							}
							l_stf_off_data_map.remove(wa_tp.getStaff_id());
							wa_sod = new StaffOfficialData();
							
						}
					if (sal_amount>0)
					{
						l_tp.add(wa_tp);
					}
					
				}
				
				/*if (l_payroll.size()>0){
					for (Payroll wa_payroll:l_payroll){
						if (wa_payroll.getStaff_id() == wa_salary.getStaff_id() && 
								wa_payroll.getBukrs().equals(wa_salary.getBukrs()) && 
								wa_payroll.getSalary_id() == wa_salary.getSalary_id() &&
								wa_payroll.getMonat() == a_monat &&
								wa_payroll.getGjahr() == a_gjahr){
							prl_amount = prl_amount + wa_payroll.getDmbtr();							
						}
					}
				}
				
				sal_amount = sal_amount - prl_amount;
				
				if (sal_amount != 0){
					System.out.println(wa_salary.getSalary_id());
					for (Staff wa_staff:l_staff)
					{	
						if (wa_salary.getStaff_id() == wa_staff.getStaff_id()){
							System.out.println("Staff: "+wa_staff.getStaff_id());
							System.out.println("sal_amount: "+sal_amount);
							System.out.println("a_monat: "+a_monat);
							System.out.println("a_gjahr: "+a_gjahr);
							System.out.println("a_bukrs: "+a_bukrs);
							System.out.println("Staff: "+wa_staff.getStaff_id());
							System.out.println("getBranch_id: "+wa_salary.getBranch_id());
							System.out.println("getCustomer_id: "+wa_staff.getCustomer_id());
							System.out.println("getPosition_id: "+wa_salary.getPosition_id());
							//fiServ.createSalaryPayments(sal_amount, a_monat, a_gjahr, a_bukrs, wa_salary.getStaff_id(), 
							//		wa_salary.getBranch_id(),  wa_salary.getSalary_id(),  
							//		wa_salary.getPosition_id(),wa_staff.getCustomer_id());
							PayrollBonusPayment wa_payrollBonusPayment = new PayrollBonusPayment(); 
							wa_payrollBonusPayment.setBranch_id(wa_salary.getBranch_id());
							wa_payrollBonusPayment.setBukrs(a_bukrs);
							wa_payrollBonusPayment.setGjahr(a_gjahr);
							wa_payrollBonusPayment.setMonat(a_monat);
							wa_payrollBonusPayment.setStaff_id(wa_salary.getStaff_id());
							wa_payrollBonusPayment.setPosition_id(wa_salary.getPosition_id());
							wa_payrollBonusPayment.setSalary(sal_amount);
							System.out.println("pbpDao");
							pbpDao.create(wa_payrollBonusPayment);
							break;
						}
						
					} 
					
					
				}
				*/
				
			}
			
			//***************************************Get SubCompany****************************************************
			/*Map<Long,SubCompany> l_sub_com_map = new HashMap<Long,SubCompany>();
			List<SubCompany> l_sub_com = new ArrayList<SubCompany>();
			l_sub_com = subComDao.findAll();
			for (SubCompany wa_sc:l_sub_com)
			{
				l_sub_com_map.put(wa_sc.getSc_id(), wa_sc);
			}*/
			//*********************************************************************************************************
			//***************************************Get SubCompany****************************************************
			/*Map<String,Customer> l_cus_tax_map = new HashMap<String,Customer>();
			List<Customer> l_cus_tax = new ArrayList<Customer>();
			l_cus_tax = cusDao.dynamicFindCustomers(" customer_id in (242,243)");
			for (Customer wa_cus:l_cus_tax)
			{
				if (wa_cus.getId()==242){l_cus_tax_map.put("KZTPEN", wa_cus);}
				else if (wa_cus.getId()==243){l_cus_tax_map.put("KZTTAX", wa_cus);}
			}*/
			//*********************************************************************************************************
			//***************************************Get Bonus,Prime***************************************************
			//key = bonus_type_id, country_id, matnr, position_id
			List<Bonus> l_bonus = new ArrayList<Bonus>();
			l_bonus = bonDao.dynamicFindBonuses(" bukrs = "+a_bukrs+" and position_id>0 and matnr is not null and country_id is not null order by matnr,bonus_type_id,position_id,from_num");
			
			/*List<BonusArchive> l_bonus_arc = new ArrayList<BonusArchive>();
			if (count_arc>0)
			{
				//salePlanArcDao pyramidArcDao bonArcDao
				
				l_bonus_arc = bonArcDao.dynamicFindBonuses(" bukrs = "+a_bukrs+" and position_id>0 and month="+a_monat+" and year = "+a_gjahr);
				
				for(int i =0;i<l_bonus_arc.size();i++)
				{
					Bonus wa_bonus = new Bonus();
					BeanUtils.copyProperties(l_bonus_arc.get(i), wa_bonus);
					l_bonus.add(wa_bonus);
				}
				//throw new DAOException("за "+a_monat+" месяц "+a_gjahr+" года ЗП, Бонусы, Премии начислены");
			}
			else
			{
				l_bonus = bonDao.dynamicFindBonuses(" bukrs = "+a_bukrs+" and position_id>0");
				
			}*/
			
			Map<Long,Bonus> l_bonus_map = new HashMap<Long,Bonus>();
			List<Bonus> l_bonus_collector = new ArrayList<Bonus>();
			List<Bonus> l_bonus_manager = new ArrayList<Bonus>();
			
			Map<Long,List<Bonus>> l_bonus_manager_map = new HashMap<Long,List<Bonus>>();
			Map<Long,List<Bonus>> l_bonus_dealer_map = new HashMap<Long,List<Bonus>>();
			for (Bonus wa_bonus:l_bonus)
			{
				//System.out.println(wa_bonus.getBonus_id());
				
				if (wa_bonus.getPosition_id()==collector_pos_id)
				{
					l_bonus_collector.add(wa_bonus);
				}
				if (wa_bonus.getPosition_id()==manager_pos_id)
				{
					l_bonus_manager.add(wa_bonus);					
				}
//				if (wa_bonus.getBonus_id().equals(370L))
//				{
//					System.out.println("Stop");
//				}
				
				GeneralUtil.checkForNullLong(wa_bonus.getBonus_type_id(), " Table bonus -> bonus_type_id, bonus_id ="+wa_bonus.getBonus_id());
				bonus_type_string = String.valueOf(wa_bonus.getBonus_type_id());
					
				GeneralUtil.checkForNullLong(wa_bonus.getBranch_id(), " Table bonus -> branch_id, bonus_id ="+wa_bonus.getBonus_id());
				branch_id_string = String.valueOf(wa_bonus.getBranch_id());
				
				GeneralUtil.checkForNullLong(wa_bonus.getMatnr(), " Table bonus -> matnr, bonus_id ="+wa_bonus.getBonus_id());
				matnr_string = String.valueOf(wa_bonus.getMatnr());
				
				GeneralUtil.checkForNullLong(wa_bonus.getPosition_id(), " Table bonus -> position_id, bonus_id ="+wa_bonus.getBonus_id());
				position_id_string = String.valueOf(wa_bonus.getPosition_id());
				//(21812)	
				key_long = Long.parseLong(bonus_type_string + branch_id_string + matnr_string + position_id_string);
				if (wa_bonus.getBonus_id().equals(2545L)){
					System.out.println("Bonus");
				}
				
				if (wa_bonus.getPosition_id()==manager_pos_id )
				{
					List<Bonus> wal_bonus = l_bonus_manager_map.get(key_long);
					if (wal_bonus==null || wal_bonus.size()==0)
					{
						wal_bonus = new ArrayList<Bonus>();
						wal_bonus.add(wa_bonus);
						l_bonus_manager_map.put(key_long, wal_bonus);
					}
					else
					{
						wal_bonus.add(wa_bonus);
					}
				}				
				else if (wa_bonus.getPosition_id()==dealer_pos_id )
				{
					List<Bonus> wal_bonus = l_bonus_dealer_map.get(key_long);
					if (wal_bonus==null || wal_bonus.size()==0)
					{
						wal_bonus = new ArrayList<Bonus>();
						wal_bonus.add(wa_bonus);
						l_bonus_dealer_map.put(key_long, wal_bonus);
					}
					else
					{
						wal_bonus.add(wa_bonus);
					}
				}
				
//				if ((wa_bonus.getPosition_id()==manager_pos_id || wa_bonus.getPosition_id()==dealer_pos_id) && wa_bonus.getBukrs().equals("2000"))
//				{
//					
//					//if (wa_bonus.getMatnr().equals(816L))
//					//{
//					//	System.out.println("ZZZ");
//					//}
//					String matnr_type_string = "";
//					matnr_type_string = getMatnrBonusGroup2000(wa_bonus.getMatnr());
//					if (matnr_type_string.equals("815")) //rainbow
//					{
//						if (wa_bonus.getPosition_id()==dealer_pos_id && wa_bonus.getFrom_num()==1 && wa_bonus.getTo_num()==2)
//						{key_long = Long.parseLong(bonus_type_string + branch_id_string + matnr_type_string + position_id_string+"1");}
//						else if (wa_bonus.getPosition_id()==dealer_pos_id && wa_bonus.getFrom_num()==3 && wa_bonus.getTo_num()==6)
//						{key_long = Long.parseLong(bonus_type_string + branch_id_string + matnr_type_string + position_id_string+"2");}
//						else if (wa_bonus.getPosition_id()==dealer_pos_id && wa_bonus.getFrom_num()==7 && wa_bonus.getTo_num()==99999)
//						{key_long = Long.parseLong(bonus_type_string + branch_id_string + matnr_type_string + position_id_string+"3");}						
//						else if (wa_bonus.getPosition_id()==manager_pos_id && wa_bonus.getFrom_num()==6 && wa_bonus.getTo_num()==14)
//						{key_long = Long.parseLong(bonus_type_string + branch_id_string + matnr_type_string + position_id_string+"1");}
//						else if (wa_bonus.getPosition_id()==manager_pos_id && wa_bonus.getFrom_num()==15 && wa_bonus.getTo_num()==99999)
//						{key_long = Long.parseLong(bonus_type_string + branch_id_string + matnr_type_string + position_id_string+"2");}
//					}
//					else if (matnr_type_string.equals("913")) //Rexwat Eco && Rexwat Eco RO ReStyle 2016
//					{
//						if (wa_bonus.getPosition_id()==dealer_pos_id && wa_bonus.getFrom_num()==1 && wa_bonus.getTo_num()==3)
//						{key_long = Long.parseLong(bonus_type_string + branch_id_string + matnr_type_string + position_id_string+"1");}
//						else if (wa_bonus.getPosition_id()==dealer_pos_id && wa_bonus.getFrom_num()==4 && wa_bonus.getTo_num()==7)
//						{key_long = Long.parseLong(bonus_type_string + branch_id_string + matnr_type_string + position_id_string+"2");}
//						else if (wa_bonus.getPosition_id()==dealer_pos_id && wa_bonus.getFrom_num()==8 && wa_bonus.getTo_num()==99999)
//						{key_long = Long.parseLong(bonus_type_string + branch_id_string + matnr_type_string + position_id_string+"3");}						
//						else if (wa_bonus.getPosition_id()==manager_pos_id && wa_bonus.getFrom_num()==6 && wa_bonus.getTo_num()==14)
//						{key_long = Long.parseLong(bonus_type_string + branch_id_string + matnr_type_string + position_id_string+"1");}
//						else if (wa_bonus.getPosition_id()==manager_pos_id && wa_bonus.getFrom_num()==15 && wa_bonus.getTo_num()==99999)
//						{key_long = Long.parseLong(bonus_type_string + branch_id_string + matnr_type_string + position_id_string+"2");}
//					}
//					else if (matnr_type_string.equals("816")) //Rexwat Atlas Premium 22 && Rexwat Atlas Premium 15	
//					{
//						key_long = Long.parseLong(bonus_type_string + branch_id_string + matnr_type_string + position_id_string);
//					}
//					else continue;
//					
//					
//					System.out.println(key_long);
//					
//					l_bonus_map.put(key_long, wa_bonus);
//				}
				else// if (wa_bonus.getBukrs().equals("1000"))
				{
					System.out.println(key_long);
					l_bonus_map.put(key_long, wa_bonus);
				}
				
				
				
			}
			
			//*********************************************************************************************************
			//***************************************Get Sale Plan*****************************************************
//			coutry_id_string = "";
			position_id_string = "";
			matnr_string = "";
			staff_id_string = "";
			bonus_type_string = "";
			branch_id_string = "";
			key_long = null;
			//key = bukrs, branch_id
			List<SalePlan> l_sale_plan = new ArrayList<SalePlan>();
			l_sale_plan = salePlanDao.findAllByBukrs(a_bukrs);
			
			/*
			List<SalePlanArchive> l_sale_plan_arc = new ArrayList<SalePlanArchive>();
			if (count_arc>0)
			{
				//salePlanArcDao pyramidArcDao bonArcDao
				l_sale_plan_arc = salePlanArcDao.dynamicFind(" bukrs = "+a_bukrs+"  and month="+a_monat+" and year = "+a_gjahr);
				for(int i =0;i<l_sale_plan_arc.size();i++)
				{
					SalePlan wa_sp = new SalePlan();
					BeanUtils.copyProperties(l_sale_plan_arc.get(i), wa_sp);
					l_sale_plan.add(wa_sp);
				}
				//throw new DAOException("за "+a_monat+" месяц "+a_gjahr+" года ЗП, Бонусы, Премии начислены");
			}
			else
			{
				l_sale_plan = salePlanDao.findAllByBukrs(a_bukrs);
				
			}*/
			
			Map<Long,SalePlan> l_sale_plan_map = new HashMap<Long,SalePlan>();
			for (SalePlan wa_sale_plan:l_sale_plan)
			{
				GeneralUtil.checkForNullLong(a_bukrs, " Table Sale_plan -> bukrs, sale_plan_id ="+wa_sale_plan.getSp_id());
				
				GeneralUtil.checkForNullLong(wa_sale_plan.getBranch_id(), " Table Sale_plan -> branch_id, sale_plan_id ="+wa_sale_plan.getSp_id());
				branch_id_string = String.valueOf(wa_sale_plan.getBranch_id());

				
				key_long = Long.parseLong(a_bukrs + branch_id_string);
				l_sale_plan_map.put(key_long, wa_sale_plan);
			}
			//*********************************************************************************************************
			//***************************************Get Pyramid*******************************************************
//			coutry_id_string = "";
			position_id_string = "";
			matnr_string = "";
			staff_id_string = "";
			bonus_type_string = "";
			branch_id_string = "";
			pyramid_id_string = "";
			key_long = null;
			
			//key = bukrs, pyramid_id
			//key = bukrs, staff_id, position_id, branch_id
			List<Pyramid> l_pyramid = new ArrayList<Pyramid>();
			l_pyramid = pyramidDao.dynamicFindPyramid(" bukrs = "+ a_bukrs);
			
			/*List<PyramidArchive> l_pyramid_arc = new ArrayList<PyramidArchive>();
			if (count_arc>0)
			{
				//salePlanArcDao pyramidArcDao bonArcDao
				l_pyramid_arc = pyramidArcDao.dynamicFindPyramid(" bukrs = "+a_bukrs+"  and month="+a_monat+" and year = "+a_gjahr);				
				for(int i =0;i<l_pyramid_arc.size();i++)
				{
					Pyramid wa_pyr = new Pyramid();
					BeanUtils.copyProperties(l_pyramid_arc.get(i), wa_pyr);
					l_pyramid.add(wa_pyr);
				}
				//throw new DAOException("за "+a_monat+" месяц "+a_gjahr+" года ЗП, Бонусы, Премии начислены");
			}
			else
			{
				l_pyramid = pyramidDao.dynamicFindPyramid(" bukrs = "+ a_bukrs);
			}
			*/
			Map<Long,Pyramid> l_pyramid_map = new HashMap<Long,Pyramid>();
			Map<Long,Pyramid> l_pyramid_staff_map = new HashMap<Long,Pyramid>();			
			Map<Long,Pyramid> l_pyramid_ass_dir_map = new HashMap<Long,Pyramid>();
			Map<Long,Pyramid> l_pyramid_trainer_map = new HashMap<Long,Pyramid>();
			Map<Long,Pyramid> l_pyramid_trainer_main_map = new HashMap<Long,Pyramid>();
			for (Pyramid wa_pyramid:l_pyramid)
			{
				if (wa_pyramid.getPyramid_id().equals(9584L)){
					System.out.println(9584);
				}
				GeneralUtil.checkForNullLong(a_bukrs, " Table Pyramid -> bukrs");
				
				GeneralUtil.checkForNullLong(wa_pyramid.getPyramid_id(), " Table Pyramid -> pyramid_id, pyramid_id ="+wa_pyramid.getPyramid_id());
				pyramid_id_string = String.valueOf(wa_pyramid.getPyramid_id());
				
				GeneralUtil.checkForNullLong(wa_pyramid.getPosition_id(), " Table Pyramid -> position_id, pyramid_id ="+wa_pyramid.getPyramid_id());
				position_id_string = String.valueOf(wa_pyramid.getPosition_id());
				
				GeneralUtil.checkForNullLong(wa_pyramid.getBranch_id(), " Table Pyramid -> branch_id, pyramid_id ="+wa_pyramid.getPyramid_id());
				branch_id_string = String.valueOf(wa_pyramid.getBranch_id());
				
				
				if (wa_pyramid.getStaff_id()==null || wa_pyramid.getStaff_id()==0)
				{
					staff_id_string = "0";
				}
				else
				{
					staff_id_string = String.valueOf(wa_pyramid.getStaff_id());
				}
				
				key_long = Long.parseLong(a_bukrs + pyramid_id_string);
				l_pyramid_map.put(key_long, wa_pyramid);
				
				key_long = Long.parseLong(a_bukrs + staff_id_string + position_id_string + branch_id_string);
				l_pyramid_staff_map.put(key_long, wa_pyramid);
				//System.out.println(key_long);
				
				if (wa_pyramid.getPosition_id()==assistant_director_pos_id)
				{
					key_long = Long.parseLong(a_bukrs + position_id_string + branch_id_string);
					l_pyramid_ass_dir_map.put(key_long, wa_pyramid);
				}
				else if (wa_pyramid.getPosition_id()==trainer_pos_id)
				{
					key_long = Long.parseLong(a_bukrs + position_id_string + branch_id_string);
					l_pyramid_trainer_map.put(key_long, wa_pyramid);
				}
				else if (wa_pyramid.getPosition_id()==main_trainer_pos_id)
				{
					if (wa_pyramid.getBusiness_area_id()==null || wa_pyramid.getBusiness_area_id()==0)
					{
						throw new DAOException("Main trainer has not business area id, Table Pyramid, pyramid_id ="+wa_pyramid.getPyramid_id());
					}
					key_long = Long.parseLong(a_bukrs + position_id_string + String.valueOf(wa_pyramid.getBusiness_area_id()));
					l_pyramid_trainer_main_map.put(key_long, wa_pyramid);					
				}
				
			}
			//*********************************************************************************************************
			//System.out.println(l_pyramid_map.size());
			//System.out.println(l_bonus_map.size());
			//System.out.println(l_sale_plan_map.size());
			//System.out.println(l_er.size());
			//System.out.println(l_currency.size());
			//System.out.println(map_er.size());			
			//***************************************Get PaymentTemplate********************************************************
			Map<Long, PriceList> map_price_list = new HashMap<Long, PriceList>();
			List<PriceList> l_pl = new ArrayList<PriceList>();
			l_pl = pirceListDao.findAll();
			//Branch main_branch = new Branch();
			for(PriceList wa_pl:l_pl)
			{
				map_price_list.put(wa_pl.getPrice_list_id(), wa_pl);
			}
			//*********************************************************************************************************
			//***************************************Get Contract type*************************************************
			Map<Long, ContractType> map_con_type = new HashMap<Long, ContractType>();
			List<ContractType> l_con_type = new ArrayList<ContractType>();
			l_con_type = conTypeDao.findAll();
			for(ContractType wa_con_type:l_con_type)
			{
				map_con_type.put(wa_con_type.getContract_type_id(), wa_con_type);
			}
			//*********************************************************************************************************
			
			Calendar lastDate = Calendar.getInstance();
			Calendar begDate = Calendar.getInstance();
			begDate = GeneralUtil.getDate(01, a_monat, a_gjahr);
			lastDate = GeneralUtil.getLastDayOfMonth(begDate);
			//System.out.println(begDate.getTime());
			//System.out.println(lastDate.getTime());
			
			//Дилер, Демо, Менеджер, Директор, Кординатор, Обучатель Premium*******************************************
			//List<CancelBonus> l_cb = new ArrayList<CancelBonus>();
			List<Contract> l_contracts = new ArrayList<Contract>();
			String dynamicWhere = "";
			dynamicWhere = dynamicWhere + " bukrs = "+a_bukrs;
			dynamicWhere = dynamicWhere + " and contract_date between '"+GeneralUtil.getSQLDate(begDate)+"'";
			dynamicWhere = dynamicWhere + " and '"+GeneralUtil.getSQLDate(lastDate)+"'";
			dynamicWhere = dynamicWhere + " and contract_status_id NOT IN (2,3) and MARKED_TYPE=0 order by contract_date";
			l_contracts = conDao.dynamicFindContracts(dynamicWhere);
			//System.out.println(l_contracts.size());
			
			if (a_bukrs.equals("2000"))
			{	
				Map<Long,FactTableClass> l_fact_table = new HashMap<Long,FactTableClass>();
				collectFact2000(l_tp,l_fact_table, l_contracts, a_bukrs, map_er, l_pyramid_map, l_pyramid_staff_map,
						l_pyramid_trainer_map, l_pyramid_trainer_main_map, map_con_type,l_bonus_map, a_gjahr, a_monat, map_price_list, map_branch, map_staff
						
						,l_bonus_manager
						,l_bonus_manager_map
						,l_bonus_dealer_map,l_pyramid_ass_dir_map);
				
				
				//**********************************************************************************************************
				//Взносщик Premium******************************************************************************************
				//List<Contract> l_contracts_collector = new ArrayList<Contract>();		
				//Map<Long,Contract> l_contracts_collected_amount_map = new HashMap<Long,Contract>();
				//Map<Long,Contract> l_closed_contracts_collected_amount_map = new HashMap<Long,Contract>();
				//l_contracts_collector = conDao.findCollectorAmount(a_bukrs, GeneralUtil.getSQLDate(begDate),GeneralUtil.getSQLDate(lastDate));
				//l_contracts_collected_amount_map = conDao.findCollectedAmount(a_bukrs, GeneralUtil.getSQLDate(begDate),GeneralUtil.getSQLDate(lastDate));
				//l_closed_contracts_collected_amount_map = conDao.findCollectedAmountClosedContracts(a_bukrs, GeneralUtil.getSQLDate(begDate),GeneralUtil.getSQLDate(lastDate));
				//collectFactCollector2000(l_fact_table, l_contracts_collector,l_contracts_collected_amount_map,l_closed_contracts_collected_amount_map, a_bukrs, bonus);
				//**********************************************************************************************************
				applyBonus2000(l_tp, l_fact_table, map_branch, map_staff, map_er, l_bonus_map, l_bonus_collector, a_gjahr, a_monat,l_bonus_manager_map);
				applyNachalnik2000(l_tp,a_bukrs, a_gjahr, a_monat);
				applyFinAgent1000(l_tp,a_bukrs, a_gjahr, a_monat, map_branch,map_er);
				applyFinManager(l_tp,a_bukrs, a_gjahr, a_monat, map_branch,map_er);
				applyService1000(l_tp,a_bukrs, a_gjahr, a_monat);
				marketingTS(l_tp, a_bukrs, a_gjahr, a_monat, map_branch);
				
				
//				applyFin70000Tenge(l_tp, a_bukrs, a_gjahr, a_monat);
				marketingKPI(l_tp, a_bukrs, a_gjahr, a_monat, map_branch,map_staff,l_pyramid_ass_dir_map);
				//addCancelBonus2000(l_contracts,a_bukrs,a_gjahr,a_monat,l_pyramid_map, l_pyramid_staff_map,l_pyramid_trainer_map, l_pyramid_trainer_main_map, map_con_type,map_price_list,
				//		map_er, l_bonus_map, l_fact_table);
			}
			else if (a_bukrs.equals("1000"))
			{
				Map<Long,FactTableClass> l_fact_table = new HashMap<Long,FactTableClass>();
				
				collectFact1000(l_tp,l_fact_table, l_contracts, a_bukrs, map_er,l_pyramid_map, l_pyramid_staff_map,
						l_pyramid_trainer_map, l_pyramid_trainer_main_map, map_con_type,l_bonus_map, a_gjahr, a_monat,map_price_list, map_branch, map_staff
						
						,l_bonus_manager
						,l_bonus_manager_map
						,l_bonus_dealer_map,l_pyramid_ass_dir_map);
				
				
				
				//**********************************************************************************************************
				
				//Взносщик Premium******************************************************************************************
				//List<Contract> l_contracts_collector = new ArrayList<Contract>();		
				//Map<Long,Contract> l_contracts_collected_amount_map = new HashMap<Long,Contract>();
				//Map<Long,Contract> l_closed_contracts_collected_amount_map = new HashMap<Long,Contract>();
				//l_contracts_collector = conDao.findCollectorAmount(a_bukrs, GeneralUtil.getSQLDate(begDate),GeneralUtil.getSQLDate(lastDate));
				//l_contracts_collected_amount_map = conDao.findCollectedAmount(a_bukrs, GeneralUtil.getSQLDate(begDate),GeneralUtil.getSQLDate(lastDate));
				//l_closed_contracts_collected_amount_map = conDao.findCollectedAmountClosedContracts(a_bukrs, GeneralUtil.getSQLDate(begDate),GeneralUtil.getSQLDate(lastDate));
				//collectFactCollector1000(l_fact_table, l_contracts_collector,l_contracts_collected_amount_map,l_closed_contracts_collected_amount_map, a_bukrs, bonus);
				//**********************************************************************************************************
				
				
				applyBonus1000(l_tp, l_fact_table, map_branch, map_staff, map_er, l_bonus_map, l_bonus_collector, a_gjahr, a_monat,l_bonus_manager_map);
				applyService1000(l_tp,a_bukrs, a_gjahr, a_monat);
				marketingTS(l_tp, a_bukrs, a_gjahr, a_monat, map_branch);
				applyFinAgent1000(l_tp,a_bukrs, a_gjahr, a_monat, map_branch,map_er);
				applyFinManager(l_tp,a_bukrs, a_gjahr, a_monat, map_branch,map_er);
				
				
				applyNachalnikAnalytic1000(l_tp,a_bukrs, a_gjahr, a_monat);
				marketingKPI(l_tp, a_bukrs, a_gjahr, a_monat, map_branch,map_staff, l_pyramid_ass_dir_map);
				

//				applyFin70000Tenge(l_tp, a_bukrs, a_gjahr, a_monat);


				
				
				//addCancelBonus1000(l_contracts,a_bukrs,a_gjahr,a_monat,l_pyramid_map, l_pyramid_staff_map,l_pyramid_trainer_map, l_pyramid_trainer_main_map, map_con_type,map_price_list,
				//		map_er, l_bonus_map, l_bonus_manager,l_fact_table);
			}
			
			/*System.out.println("Size: "+l_tp.size());
			System.out.println("Size: "+l_fact_table.size());
			System.out.println("Size: "+map_branch.size());
			System.out.println("Size: "+map_staff.size());
			System.out.println("Size: "+map_er.size());
			System.out.println("Size: "+l_bonus_map.size());
			System.out.println("Size: "+l_bonus_collector.size());
			System.out.println("Year: "+a_gjahr);
			System.out.println("Monat: "+a_monat);*/
			
			
			//System.out.println("Size: "+l_tp.size());
			
			//applyTaxes(l_tp, l_stf_off_data_to_be_charged_map, l_sub_com_map, map_er_waers, a_bukrs, a_monat, a_gjahr, main_branch, l_cus_tax_map);
			//System.out.println("Size: "+l_tp.size());
			
			//System.out.println("ZZZZZZZ");
			
			
			List<TempPayroll> l_tp4 = new ArrayList<TempPayroll>();	
			for(TempPayroll wa_tp:l_tp)
			{
				if (wa_tp.getType()!=wage)
				{
					wa_tp.setFrom_date(begDate.getTime());
					wa_tp.setTo_date(lastDate.getTime());
				}
				if (wa_tp.getAmount()!=0 && wa_tp.getStaff_id()!=null && wa_tp.getStaff_id()>0)
				{
					Staff wa_staff = map_staff.get(wa_tp.getStaff_id());
					if (wa_staff!=null)
					{
						wa_tp.setStaff_name(wa_staff.getLastname()+" "+wa_staff.getFirstname()+" "+wa_staff.getMiddlename());
					}
					if (wa_tp.getDrcrk()==null)
					{
						wa_tp.setDrcrk("S");
					}
					l_tp4.add(wa_tp);
					tpDao.create(wa_tp);
				}
			}
			return l_tp4;
			
			/*if (l_tpa.size()>0)
			{		
				List<TempPayroll> l_tp2 = new ArrayList<TempPayroll>();
				Collections.sort(l_tp,new TempPayrollCompareCustomerId());
				Collections.sort(l_tpa,new TempPayrollArchiveCompareCustomerId());
				//OUTER:  //outer label
				for(TempPayroll wa_tp:l_tp)
				{
					//int count = 0;
					boolean found = false;
					//System.out.println(wa_tp.getStaff_id()+" "+wa_tp.getAmount()+" "+wa_tp.getBonus_id()+" "+wa_tp.getCustomer_id()+" "+wa_tp.getType());	
					
					TempPayrollArchive searchKey = new TempPayrollArchive();
					//searchKey.setStaff_id(wa_tp.getStaff_id());
					searchKey.setCustomer_id(wa_tp.getCustomer_id());
					int index = Collections.binarySearch(l_tpa, searchKey, new TempPayrollArchiveCompareCustomerId()); 
					while (index > 0 && new TempPayrollArchiveCompareCustomerId().compare(l_tpa.get(index), l_tpa.get(index-1)) == 0) {
					    index--;
					}
					//if (wa_tp.getCustomer_id()==260)
					//{
					//	System.out.println("ZZZZZZZZZZZZZZZZZZ");
  					//	System.out.println(found);
					//}
					//boolean loop = true;
					//INNER:
					while (index>=0 && index<l_tpa.size() )
	  				{
						
						TempPayrollArchive wa_tpa = l_tpa.get(index); 
						//System.out.println("*****************************");
						//System.out.println(index);
						//System.out.println(wa_tpa.getSalary_id());
						//System.out.println(wa_tpa.getBonus_id());
						//System.out.println(wa_tpa.getWaers());
						//System.out.println(wa_tpa.getStaff_id());
						//System.out.println(wa_tpa.getAmount());
						//System.out.println(wa_tpa.getCustomer_id());
						//System.out.println(wa_tpa.getType());
						//System.out.println(wa_tpa.getBranch_id());
						//System.out.println(wa_tpa.getPosition_id());
						//System.out.println("*****************************");
						
						
						if (wa_tpa.getCustomer_id()==null||wa_tp.getCustomer_id()==null||wa_tpa.getType()==0||wa_tpa.getType()==0||wa_tp.getCustomer_id()==null||wa_tp.getCustomer_id()==null)
						{
							throw new DAOException("Отсутствует ID для сравнения: PayrollService -> applySalary");
						}
	  					if (wa_tpa.getCustomer_id().equals(wa_tp.getCustomer_id()) && wa_tpa.getType()==wa_tp.getType() && wa_tp.getBranch_id().equals(wa_tpa.getBranch_id()))
	  					{
	  						
	  						if (wa_tp.getType()>3 && wa_tp.getType()<7 && wa_tpa.getType()>3 && wa_tpa.getType()<7 )
	  						{
	  							wa_tp.setAmount(wa_tp.getAmount()-wa_tpa.getAmount());
		  						l_tp2.add(wa_tp);
	  						}

	  						else if (wa_tpa.getPosition_id().equals(wa_tp.getPosition_id()))
	  						{
	  							wa_tp.setAmount(wa_tp.getAmount()-wa_tpa.getAmount());
	  	  						l_tp2.add(wa_tp);
	  						}	
	  						found = true;	
	  					}	
	  					//if (wa_tp.getCustomer_id()==260)
						//{
	  					//	System.out.println(found);
						//	System.out.println(index);
						//	System.out.println(wa_tpa.getSalary_id());
						//	System.out.println(wa_tpa.getBonus_id());
						//	System.out.println(wa_tpa.getWaers());
						//	System.out.println(wa_tpa.getStaff_id());
						//	System.out.println(wa_tpa.getAmount());
						//	System.out.println(wa_tpa.getCustomer_id());
						//	System.out.println(wa_tpa.getType());
						//	System.out.println(wa_tpa.getBranch_id());
						//	System.out.println(wa_tpa.getPosition_id());
						//}
	  					if ((!wa_tpa.getCustomer_id().equals(wa_tp.getCustomer_id())))
	  					{
	  						//System.out.println("AAAAAAAAAAAAAA");
	  						if (!found)
	  						{
	  							l_tp2.add(wa_tp);
	  						}
	  						//loop=false;
	  						break;
	  					}
	  					index++;
	  					//
	  					//	if (	((wa_tpa.getSalary_id()!=null && wa_tp.getSalary_id()!=null && wa_tpa.getSalary_id()==wa_tp.getSalary_id()))
	  					//		 || (wa_tpa.getBonus_id()!=null && wa_tp.getBonus_id()!=null && wa_tpa.getBonus_id()==wa_tp.getBonus_id())  
	  					//			&& wa_tpa.getWaers().equals(wa_tp.getWaers()))
	  					//	{
	  					//		if (wa_tpa.getAmount() != wa_tp.getAmount())
	  					//		{
	  					//			wa_tp.setAmount(wa_tp.getAmount()-wa_tpa.getAmount());
	  					//			l_tp2.add(wa_tp);
	  					//		} 
	  					//		continue OUTER;
	  					//	}
	  					//	index++;
	  					//	loop = true;
	  					//		
	  					//}
	  					//else
	  					//{ 

	  					//	l_tp2.add(wa_tp);
	  					//	loop = false;
	  					//}
	  				} 
					if (index<0 || index>=l_tpa.size())
					{
						l_tp2.add(wa_tp);
					}
					 
		  			
				}
				
				List<TempPayroll> l_tp4 = new ArrayList<TempPayroll>();				
				for(TempPayroll wa_tp3:l_tp2)
				{
					if (wa_tp3.getType()!=wage)
					{
						wa_tp3.setFrom_date(begDate.getTime());
						wa_tp3.setTo_date(lastDate.getTime());
					}
					
					if (wa_tp3.getAmount()!=0)
					{
						l_tp4.add(wa_tp3);
						tpDao.create(wa_tp3);
					}
					
				}				
				return l_tp4;				
				
			}
			else
			{
				List<TempPayroll> l_tp4 = new ArrayList<TempPayroll>();	
				for(TempPayroll wa_tp:l_tp)
				{
					if (wa_tp.getType()!=wage)
					{
						wa_tp.setFrom_date(begDate.getTime());
						wa_tp.setTo_date(lastDate.getTime());
					}
					if (wa_tp.getAmount()!=0)
					{
						l_tp4.add(wa_tp);
						tpDao.create(wa_tp);
					}
				}
				return l_tp4;
			}
			*/
			}
			
			else
			 return null;	
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		} 
		
	}
	
	public void changeTempPayroll(TempPayroll a_tp) throws DAOException{
		try
		{				
			tpDao.update(a_tp);
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	public class FactTableClass
	{
		public FactTableClass()
		{
			
		};
		private String bukrs = "";
		private Long staff_id = null;
		private Long matnr = null;
		private int type = 0;
		private Long country_id = null;
		private double dmbtr = 0;
		private double wrbtr = 0;
		private String waers = "";
		private Long position_id = null;
		private int counter = 0;		
		private Long branch_id = null;
		private double plan_amount = 0;
		private double fact_amount = 0;
		private int counter_rassrochka = 0;
		private int counter_min_fp_rassrochka = 0;
		
		public int getCounter_rassrochka() {
			return counter_rassrochka;
		}
		public void setCounter_rassrochka(int counter_rassrochka) {
			this.counter_rassrochka = counter_rassrochka;
		}
		public String getBukrs() {
			return bukrs;
		}
		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}
		public Long getStaff_id() {
			return staff_id;
		}
		public void setStaff_id(Long staff_id) {
			this.staff_id = staff_id;
		}
		public Long getMatnr() {
			return matnr;
		}
		public void setMatnr(Long matnr) {
			this.matnr = matnr;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public Long getCountry_id() {
			return country_id;
		}
		public void setCountry_id(Long country_id) {
			this.country_id = country_id;
		}
		public double getDmbtr() {
			return dmbtr;
		}
		public void setDmbtr(double dmbtr) {
			this.dmbtr = dmbtr;
		}
		public double getWrbtr() {
			return wrbtr;
		}
		public void setWrbtr(double wrbtr) {
			this.wrbtr = wrbtr;
		}
		public String getWaers() {
			return waers;
		}
		public void setWaers(String waers) {
			this.waers = waers;
		}
		public Long getPosition_id() {
			return position_id;
		}
		public void setPosition_id(Long position_id) {
			this.position_id = position_id;
		}
		public int getCounter() {
			return counter;
		}
		public void setCounter(int counter) {
			this.counter = counter;
		}
		public Long getBranch_id() {
			return branch_id;
		}
		public void setBranch_id(Long branch_id) {
			this.branch_id = branch_id;
		}
		public double getPlan_amount() {
			return plan_amount;
		}
		public void setPlan_amount(double plan_amount) {
			this.plan_amount = plan_amount;
		}
		public double getFact_amount() {
			return fact_amount;
		}
		public void setFact_amount(double fact_amount) {
			this.fact_amount = fact_amount;
		}
		public int getCounter_min_fp_rassrochka() {
			return counter_min_fp_rassrochka;
		}
		public void setCounter_min_fp_rassrochka(int counter_min_fp_rassrochka) {
			this.counter_min_fp_rassrochka = counter_min_fp_rassrochka;
		}
		
	}
	
	public void collectFact2000(List<TempPayroll> l_tp,Map<Long,FactTableClass> l_fact_table, List<Contract> l_contracts, String a_bukrs, Map<Long,ExchangeRate> map_er,
							Map<Long,Pyramid> l_pyramid_map, Map<Long,Pyramid> l_pyramid_staff_map, Map<Long,Pyramid> l_pyramid_trainer_map, 
							Map<Long,Pyramid> l_pyramid_trainer_main_map, Map<Long,ContractType> map_con_type,Map<Long,Bonus> l_bonus_map,int a_gjahr,int a_monat,Map<Long, 
							PriceList> map_price_list,Map<Long,Branch> map_branch, Map<Long,Staff> map_staff,List<Bonus> l_bonus_manager,
							Map<Long,List<Bonus>> l_bonus_manager_map,Map<Long,List<Bonus>> l_bonus_dealer_map,
							Map<Long,Pyramid> l_pyramid_ass_dir_map)
	{
		try
		{
			
			//////////////////////////////////////////////////////////////////////////////////
			//////////////////////////////////////////////////////////////////////////////////
			
			String country_id_string = "";
			String position_id_string = "";
			String matnr_string = "";
			String matnr_type_string="";
			String staff_id_string = "";
			String type_string = ""; 
			Long key_long = null;
			
			country_id_string = ""; //change country id
				position_id_string = "";
				matnr_string = "";
				matnr_type_string="";
				staff_id_string = "";
				type_string = "";
				key_long = null;
				Calendar curDate = Calendar.getInstance();

				
				//GreenLight
				for (Contract wa_contract :l_contracts)
				{
					
					Branch wa_branch = map_branch.get(wa_contract.getBranch_id());
					
					country_id_string = ""; //change country id
					position_id_string = "";
					matnr_string = "";
					matnr_type_string="";
					staff_id_string = "";
					type_string = "";
					System.out.println(wa_contract.getContract_id());
					if (wa_contract.getContract_id().equals(438198L))
					{
						System.out.println(wa_contract.getContract_id());
					}
					TempPayroll wa_tp_dealer = new TempPayroll();
	
					
					if (a_bukrs.equals("2000"))
					{
						//Rainbow Premium
						if (wa_contract.getContract_type_id()==3){matnr_string = "815";matnr_type_string = "815";} //rainbow e2
						else if (wa_contract.getContract_type_id()==17){matnr_string = "2930";matnr_type_string = "815";}	//rainbow srx
						else if (wa_contract.getContract_type_id()==4){matnr_string = "816";matnr_type_string = "816";}	//Rexwat Atlas Premium 22
						else if (wa_contract.getContract_type_id()==6){matnr_string = "910";matnr_type_string = "816";}	//Rexwat Atlas Premium 15	
						else if (wa_contract.getContract_type_id()==5){matnr_string = "817";matnr_type_string = "913";} //Rexwat Eco 
						else if (wa_contract.getContract_type_id()==10){matnr_string = "913";matnr_type_string = "913";} //Rexwat Eco RO ReStyle 2016
						else
						{
							continue;
						}
					}
					
					
					
					ExchangeRate wa_er = map_er.get(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid());
					
					

					
					
					if (wa_contract.getDealer()!=null)
					{	
						//Dealer position_id = 4
						country_id_string = String.valueOf(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
						position_id_string = String.valueOf(dealer_pos_id);
						type_string = String.valueOf(premium);
						staff_id_string = String.valueOf(wa_contract.getDealer());
						//key_long = country_id, matnr, type, position_id, staff_id
						key_long = Long.parseLong(country_id_string + matnr_type_string + type_string + position_id_string + staff_id_string);
						double counterGoods = 0;
						FactTableClass wa_ft = l_fact_table.get(key_long);
						if (wa_ft!=null)
						{
							wa_ft.setCounter(wa_ft.getCounter()+1);
							counterGoods = wa_ft.getCounter();
						}
						else if (wa_ft==null)
						{
							wa_ft = new FactTableClass();
							wa_ft.setCountry_id(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
							wa_ft.setMatnr(Long.parseLong(matnr_string));
							wa_ft.setPosition_id((long) dealer_pos_id);
							wa_ft.setStaff_id(wa_contract.getDealer());
							wa_ft.setBranch_id(wa_contract.getBranch_id());
							wa_ft.setBukrs(wa_contract.getBukrs());
							wa_ft.setType(premium);
							wa_ft.setCounter(1);
							l_fact_table.put(key_long, wa_ft);
							counterGoods = wa_ft.getCounter();
						}
						
						

						
						double deposit_amount = 0;
						GeneralUtil.checkForNullLong(matnr_string, "matnr id is null when calculating bonuses for dealers");
						
//						if (matnr_type_string.equals("815")) //rainbow
//						{							
//							if (wa_ft.getCounter()>=1 && wa_ft.getCounter()<=2)
//							{key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(dealer_pos_id)+"1");}
//							else if (wa_ft.getCounter()>=3 && wa_ft.getCounter()<=6)
//							{key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(dealer_pos_id)+"2");}
//							else if (wa_ft.getCounter()>=7 && wa_ft.getCounter()<=99999)
//							{key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(dealer_pos_id)+"3");}
//						}
//						else if (matnr_type_string.equals("913")) //Rexwat Eco && Rexwat Eco RO ReStyle 2016
//						{							
//							if (wa_ft.getCounter()>=1 && wa_ft.getCounter()<=3)
//							{key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(dealer_pos_id)+"1");}
//							else if (wa_ft.getCounter()>=4 && wa_ft.getCounter()<=7)
//							{key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(dealer_pos_id)+"2");}
//							else if (wa_ft.getCounter()>=8 && wa_ft.getCounter()<=99999)
//							{key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(dealer_pos_id)+"3");}
//						}
//						else if (matnr_type_string.equals("816")) //Rexwat Atlas Premium 22 && Rexwat Atlas Premium 15	
//						{
//							key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(dealer_pos_id));
//						}
						

						String bonusType = "";
						if (wa_contract.getTradeIn()==1) bonusType = String.valueOf(tradeIn1);
						else bonusType = String.valueOf(premium);
						
						key_long = Long.parseLong(bonusType+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(dealer_pos_id));
						List<Bonus> wal_bonus = l_bonus_dealer_map.get(key_long);
						Bonus wa_bonus = null;
						for(Bonus wa:wal_bonus)
						{
							if (wa.getFrom_num()<=counterGoods && counterGoods<= wa.getTo_num())
							{
								wa_bonus = new Bonus();
								wa_bonus = wa;
								break;
							}
						}
						if (wa_bonus==null)
						{
							throw new DAOException("dealer premium not found, Payroll Service impl 2663 line");
						}
						
						
						
										
						
						double subtructed_summa = 0;
						//добавляю депозитные суммы для дилеров
						if (wa_contract.getPayment_schedule()>1)
						{
							

							
							
							TempPayroll wa_tp_deposit = new TempPayroll();
							wa_tp_deposit.setBukrs(wa_contract.getBukrs());					
							wa_tp_deposit.setBranch_id(wa_contract.getBranch_id());
							wa_tp_deposit.setGjahr(a_gjahr);
							wa_tp_deposit.setMonat(a_monat);
							wa_tp_deposit.setStaff_id(wa_contract.getDealer());
							wa_tp_deposit.setPosition_id((long) dealer_pos_id);
							wa_tp_deposit.setType(deposit);
							wa_tp_deposit.setMatnr(Long.parseLong(matnr_string));
							wa_tp_deposit.setContract_number(wa_contract.getContract_number());
							wa_tp_deposit.setMatnr_count(1);
							wa_tp_deposit.setBranch_name(wa_branch.getText45());
							wa_tp_deposit.setDrcrk("S");
							wa_tp_deposit.setBldat(curDate.getTime());
							if (bonusType.equals("17")) wa_tp_deposit.setText45("Trade-In 1");
							if (wa_bonus.getWaers().equals("USD"))
							{
								wa_tp_deposit.setAmount(wa_bonus.getDeposit()*wa_er.getSc_value());
								wa_tp_deposit.setWaers(wa_er.getSecondary_currency());
								wa_tp_deposit.setKursf(wa_er.getSc_value());
								wa_tp_deposit.setBonus_id(wa_bonus.getBonus_id());
							}
							else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
							{
								wa_tp_deposit.setAmount(wa_bonus.getDeposit());
								wa_tp_deposit.setWaers(wa_bonus.getWaers());
								wa_tp_deposit.setBonus_id(wa_bonus.getBonus_id());
								wa_tp_deposit.setKursf(wa_er.getSc_value());
							}
							deposit_amount = wa_tp_deposit.getAmount();
							subtructed_summa = deposit_amount;
							l_tp.add(wa_tp_deposit);
							
							PriceList wa_pl = map_price_list.get(wa_contract.getPrice_list_id());
							if (wa_pl!=null && wa_pl.getPrem_div()>1L)
							{
								double div_coef = 0;
								div_coef = wa_bonus.getCoef()/wa_pl.getPrem_div();
								if (wa_pl.getPrem_div()>1L)
								{
									int loop_size = Integer.parseInt(String.valueOf(wa_pl.getPrem_div()));
									for (int o = 2;o<=loop_size;o++)
									{
										TempPayroll wa_tp_dealer2 = new TempPayroll();
										wa_tp_dealer2.setBukrs(wa_contract.getBukrs());					
										wa_tp_dealer2.setBranch_id(wa_contract.getBranch_id());
										wa_tp_dealer2.setGjahr(a_gjahr);
										wa_tp_dealer2.setMonat(a_monat);
										wa_tp_dealer2.setStaff_id(wa_contract.getDealer());
										wa_tp_dealer2.setPosition_id((long) dealer_pos_id);
										wa_tp_dealer2.setMatnr(Long.parseLong(matnr_string));
										wa_tp_dealer2.setType(premium);
										wa_tp_dealer2.setDrcrk("S");
										Calendar firstDay = Calendar.getInstance();
										firstDay.add(Calendar.MONTH, o-1);
										firstDay.set(Calendar.DAY_OF_MONTH, 1);
										wa_tp_dealer2.setBldat(firstDay.getTime());
										//wa_tp2.setBldat();
										wa_tp_dealer2.setContract_number(wa_contract.getContract_number());
										wa_tp_dealer2.setMatnr_count(1);
										wa_tp_dealer2.setBranch_name(wa_branch.getText45());
										if (bonusType.equals("17")) wa_tp_dealer2.setText45("Trade-In 1");
										//1815110220 1815110220
										if (wa_bonus.getWaers().equals("USD"))
										{
											wa_tp_dealer2.setAmount(div_coef*wa_er.getSc_value());
											wa_tp_dealer2.setWaers(wa_er.getSecondary_currency());
											wa_tp_dealer2.setKursf(wa_er.getSc_value());
											wa_tp_dealer2.setBonus_id(wa_bonus.getBonus_id());
										}
										else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
										{
											wa_tp_dealer2.setAmount(div_coef);
											wa_tp_dealer2.setWaers(wa_bonus.getWaers());
											wa_tp_dealer2.setBonus_id(wa_bonus.getBonus_id());
											wa_tp_dealer2.setKursf(wa_er.getSc_value());
										}
										else
										{
											throw new DAOException("Bonus currency not equal to the country currency");
										}	
										
										subtructed_summa = subtructed_summa + wa_tp_dealer2.getAmount();	
										if (o==loop_size)
										{
											subtructed_summa = subtructed_summa - deposit_amount;
											wa_tp_dealer2.setAmount(wa_tp_dealer2.getAmount()-deposit_amount);
										}
										
										l_tp.add(wa_tp_dealer2);
									}
									
									
								}
							}
								
							
							
						}
						
						
						wa_tp_dealer.setBukrs(wa_contract.getBukrs());					
						wa_tp_dealer.setBranch_id(wa_contract.getBranch_id());
						wa_tp_dealer.setGjahr(a_gjahr);
						wa_tp_dealer.setMonat(a_monat);
						wa_tp_dealer.setStaff_id(wa_contract.getDealer());
						wa_tp_dealer.setPosition_id((long) dealer_pos_id);
						wa_tp_dealer.setMatnr(Long.parseLong(matnr_string));
						wa_tp_dealer.setType(premium);
						wa_tp_dealer.setContract_number(wa_contract.getContract_number());
						wa_tp_dealer.setMatnr_count(1);
						wa_tp_dealer.setDrcrk("S");
						wa_tp_dealer.setBranch_name(wa_branch.getText45());
						wa_tp_dealer.setBldat(curDate.getTime());
						if (bonusType.equals("17")) wa_tp_dealer.setText45("Trade-In 1");
						//1815110220 1815110220
						if (wa_bonus.getWaers().equals("USD"))
						{
							wa_tp_dealer.setAmount(wa_bonus.getCoef()*wa_er.getSc_value());
							wa_tp_dealer.setWaers(wa_er.getSecondary_currency());
							wa_tp_dealer.setKursf(wa_er.getSc_value());
							wa_tp_dealer.setBonus_id(wa_bonus.getBonus_id());
							
						}
						else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
						{
							wa_tp_dealer.setAmount(wa_bonus.getCoef());
							wa_tp_dealer.setWaers(wa_bonus.getWaers());
							wa_tp_dealer.setBonus_id(wa_bonus.getBonus_id());
							wa_tp_dealer.setKursf(wa_er.getSc_value());
						}
						else
						{
							throw new DAOException("Bonus currency not equal to the country currency");
						}
						wa_tp_dealer.setAmount(wa_tp_dealer.getAmount()-subtructed_summa);
						
						
						l_tp.add(wa_tp_dealer);
						
						

						//
						
					}
					if (wa_contract.getDemo_sc()!=null)
					{
						//Demo secretary  position_id = 8
						country_id_string = String.valueOf(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
						position_id_string = String.valueOf(demo_pos_id);
						type_string = String.valueOf(premium);
						staff_id_string = String.valueOf(wa_contract.getDemo_sc());
						//key = country_id, matnr, type, position_id, staff_id
						key_long = Long.parseLong(country_id_string + matnr_type_string + type_string + position_id_string + staff_id_string);
						FactTableClass wa_ft3 = l_fact_table.get(key_long);
						if (wa_ft3!=null)
						{
							wa_ft3.setCounter(wa_ft3.getCounter()+1);
						}
						else if (wa_ft3==null)
						{
							FactTableClass wa_ft2= new FactTableClass();
							wa_ft2.setCountry_id(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
							wa_ft2.setMatnr(Long.parseLong(matnr_string));
							wa_ft2.setPosition_id(Long.parseLong(position_id_string));
							wa_ft2.setStaff_id(wa_contract.getDemo_sc());
							wa_ft2.setBranch_id(wa_contract.getBranch_id());
							wa_ft2.setBukrs(wa_contract.getBukrs());
							wa_ft2.setType(premium);
							wa_ft2.setCounter(1);
							l_fact_table.put(key_long, wa_ft2);
						}
						
						
						GeneralUtil.checkForNullLong(matnr_type_string, "matnr id is null when calculating bonuses for dealers");

						String bonusType = "";
						if (wa_contract.getTradeIn()==1) bonusType = String.valueOf(tradeIn1);
						else bonusType = String.valueOf(premium);
						key_long = Long.parseLong(bonusType+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(demo_pos_id));
						Bonus wa_bonus = l_bonus_map.get(key_long);
						
						TempPayroll wa_tp = new TempPayroll();
						wa_tp.setBukrs(wa_contract.getBukrs());					
						wa_tp.setBranch_id(wa_contract.getBranch_id());
						wa_tp.setGjahr(a_gjahr);
						wa_tp.setMonat(a_monat);
						wa_tp.setStaff_id(wa_contract.getDemo_sc());
						wa_tp.setPosition_id((long) demo_pos_id);
						wa_tp.setMatnr(Long.parseLong(matnr_string));
						wa_tp.setType(premium);
						wa_tp.setContract_number(wa_contract.getContract_number());
						wa_tp.setMatnr_count(1);
						wa_tp.setBranch_name(wa_branch.getText45());
						wa_tp.setDrcrk("S");
						wa_tp.setBldat(curDate.getTime());
						if (bonusType.equals("17")) wa_tp.setText45("Trade-In 1");
						//1815110220 1815110220
						if (wa_bonus.getWaers().equals("USD"))
						{
							wa_tp.setAmount(wa_bonus.getCoef()*wa_er.getSc_value());
							wa_tp.setWaers(wa_er.getSecondary_currency());
							wa_tp.setKursf(wa_er.getSc_value());
							wa_tp.setBonus_id(wa_bonus.getBonus_id());
						}
						else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
						{
							wa_tp.setAmount(wa_bonus.getCoef());
							wa_tp.setWaers(wa_bonus.getWaers());
							wa_tp.setBonus_id(wa_bonus.getBonus_id());
							wa_tp.setKursf(wa_er.getSc_value());
						}
						else
						{
							throw new DAOException("Bonus currency not equal to the country currency");
						}
						l_tp.add(wa_tp);
					}
					
					if (wa_contract.getDealer()!=null)
					{
						//key = a_bukrs + staff_id_string + position_id_string + branch_id_string
						//key = bukrs, pyramid_id
						//Manager premium position_id=3
						//System.out.println("beg "+wa_contract.getDealer()+wa_contract.getContract_number());
						boolean skip_manager = false;
						boolean skip_director = false;
						boolean skip_coordinator = false;
						Pyramid wa_dealer = new Pyramid();
						Pyramid wa_manager = new Pyramid();
						Pyramid wa_director = new Pyramid();
						Pyramid wa_coordinator = new Pyramid();						
						Pyramid wa_assistant_director = new Pyramid();
						Pyramid wa_trainer = new Pyramid();
						Pyramid wa_trainer_main = new Pyramid();
						key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_contract.getDealer())+String.valueOf(dealer_pos_id)+String.valueOf(wa_contract.getBranch_id()));
						wa_dealer = l_pyramid_staff_map.get(key_long);
						//if(wa_contract.getDealer().equals(209L))
						//{
						//	System.out.println("here");
						//}

						
						if (wa_dealer==null)
						{
							//check if dealer is manager
							key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_contract.getDealer())+String.valueOf(manager_pos_id)+String.valueOf(wa_contract.getBranch_id()));
							wa_manager = l_pyramid_staff_map.get(key_long);
							skip_manager = true;
							
							if (wa_manager==null)
							{
								//check if dealer is director
								key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_contract.getDealer())+String.valueOf(director_pos_id)+String.valueOf(wa_contract.getBranch_id()));
								wa_director = l_pyramid_staff_map.get(key_long);
								skip_director = true;
								
								//30.09.2020
								//direktorlarga ozderinin satularynan bonus tusedi
								skip_director = false;
								///////////////////////////////////////////////////
								
								if (wa_director==null)
								{
									//skip coordinator
									skip_coordinator = true;
								}
							}
								
						}
						
						if (wa_dealer!=null)
						{
							key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_dealer.getParent_pyramid_id()));
							wa_manager = l_pyramid_map.get(key_long);
						}
						
						
						if (skip_manager==false && wa_manager!=null && wa_manager.getPosition_id()==manager_pos_id && wa_manager.getStaff_id()!=null
								&& wa_tp_dealer.getStaff_id()!=wa_manager.getStaff_id())
						{
							key_long=null;
							//System.out.println("manager found");
							key_long = Long.parseLong(country_id_string + matnr_type_string + String.valueOf(premium) + String.valueOf(manager_pos_id) + String.valueOf(wa_manager.getStaff_id()));
							FactTableClass wa_ft = l_fact_table.get(key_long);
							double counterGoods = 0;
							if (wa_ft!=null)
							{
								wa_ft.setCounter(wa_ft.getCounter()+1);
								counterGoods=wa_ft.getCounter();
							}
							else if (wa_ft==null)
							{
								wa_ft= new FactTableClass();
								wa_ft.setCountry_id(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
								wa_ft.setMatnr(Long.parseLong(matnr_string));
								wa_ft.setPosition_id(wa_manager.getPosition_id());
								wa_ft.setStaff_id(wa_manager.getStaff_id());
								wa_ft.setBranch_id(wa_manager.getBranch_id());
								wa_ft.setBukrs(wa_contract.getBukrs());
								wa_ft.setType(premium);
								wa_ft.setCounter(1);
								l_fact_table.put(key_long, wa_ft);
								counterGoods=wa_ft.getCounter();
							}
							
							GeneralUtil.checkForNullLong(matnr_type_string, "matnr id is null when calculating bonuses for dealers");
							
							
							
//							if (matnr_type_string.equals("815")) //rainbow
//							{
//								if (wa_ft.getCounter()>=1 && wa_ft.getCounter()<=14)
//								{key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(manager_pos_id)+"1");}
//								else if (wa_ft.getCounter()>=15 && wa_ft.getCounter()<=99999)
//								{key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(manager_pos_id)+"2");}
//							}
//							else if (matnr_type_string.equals("913")) //Rexwat Eco && Rexwat Eco RO ReStyle 2016
//							{
//								if (wa_ft.getCounter()>=1 && wa_ft.getCounter()<=14)
//								{key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(manager_pos_id)+"1");}
//								else if (wa_ft.getCounter()>=15 && wa_ft.getCounter()<=99999)
//								{key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(manager_pos_id)+"2");}
//							}
//							else if (matnr_type_string.equals("816")) //Rexwat Atlas Premium 22 && Rexwat Atlas Premium 15	
//							{
//								key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(manager_pos_id));
//							}
							GeneralUtil.checkForNullLong(matnr_type_string, "matnr id is null when calculating bonuses for dealers");
							String bonusType = "";
							if (wa_contract.getTradeIn()==1) bonusType = String.valueOf(tradeIn1);
							else bonusType = String.valueOf(premium);
							key_long = Long.parseLong(bonusType+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(manager_pos_id));
							
							
							List<Bonus> wal_bonus = l_bonus_manager_map.get(key_long);
							Bonus wa_bonus = null;
							for(Bonus wa:wal_bonus)
							{
								if ((wa.getFrom_num()==6 && counterGoods<= wa.getTo_num())||wa.getFrom_num()<=counterGoods && counterGoods<= wa.getTo_num())
								{
									wa_bonus = new Bonus();
									wa_bonus = wa;
									break;
								}
							}
							if (wa_bonus==null)
							{
								throw new DAOException("manager premium not found, Payroll Service impl 2977 line");
							}
							
							if (wa_bonus!=null && wa_bonus.getCoef()>0)
							{
								TempPayroll wa_tp = new TempPayroll();
								wa_tp.setBukrs(wa_contract.getBukrs());					
								wa_tp.setBranch_id(wa_contract.getBranch_id());
								wa_tp.setGjahr(a_gjahr);
								wa_tp.setMonat(a_monat);
								wa_tp.setStaff_id(wa_manager.getStaff_id());
								wa_tp.setPosition_id((long) manager_pos_id);
								wa_tp.setMatnr(Long.parseLong(matnr_string));
								wa_tp.setType(premium);
								wa_tp.setMatnr_count(1);
								wa_tp.setDrcrk("S");
								wa_tp.setContract_number(wa_contract.getContract_number());
								wa_tp.setBranch_name(wa_branch.getText45());
								wa_tp.setBldat(curDate.getTime());
								if (bonusType.equals("17")) wa_tp.setText45("Trade-In 1");
								//1815110220 1815110220
								if (wa_bonus.getWaers().equals("USD"))
								{
									wa_tp.setAmount(wa_bonus.getCoef()*wa_er.getSc_value());
									wa_tp.setWaers(wa_er.getSecondary_currency());
									wa_tp.setKursf(wa_er.getSc_value());
									wa_tp.setBonus_id(wa_bonus.getBonus_id());
								}
								else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
								{
									wa_tp.setAmount(wa_bonus.getCoef());
									wa_tp.setWaers(wa_bonus.getWaers());
									wa_tp.setBonus_id(wa_bonus.getBonus_id());
									wa_tp.setKursf(wa_er.getSc_value());
								}
								else
								{
									throw new DAOException("Bonus currency not equal to the country currency");
								}
								l_tp.add(wa_tp);
								
								if (wa_tp_dealer!=null && wa_tp_dealer.getStaff_id()>0)
								{
									wa_tp_dealer.setManager_id(wa_manager.getStaff_id());
								}
								
							}
							
							
							
						}
						
						//key = bukrs, pyramid_id
						//Director premium position_id=10
						if (wa_manager!=null)
						{
							key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_manager.getParent_pyramid_id()));
							wa_director = l_pyramid_map.get(key_long);
						}
						
						//System.out.println(wa_manager.getParent_pyramid_id());
						if (skip_director==false && wa_director!=null && wa_director.getPosition_id()==director_pos_id && wa_director.getStaff_id()!=null)
						{
							
							key_long = Long.parseLong(country_id_string + matnr_type_string + String.valueOf(premium) + String.valueOf(director_pos_id) + String.valueOf(wa_director.getStaff_id()));
							//System.out.println(wa_director.getStaff_id()+" staff "+key_long);
							FactTableClass wa_ft = l_fact_table.get(key_long);
							if (wa_ft!=null)
							{
								wa_ft.setCounter(wa_ft.getCounter()+1);
								//System.out.println("counter "+wa_ft.getCounter());
							}
							else if (wa_ft==null)
							{
								FactTableClass wa_ft2= new FactTableClass();
								wa_ft2.setCountry_id(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
								wa_ft2.setMatnr(Long.parseLong(matnr_string));
								wa_ft2.setPosition_id(wa_director.getPosition_id());
								wa_ft2.setStaff_id(wa_director.getStaff_id());
								wa_ft2.setBranch_id(wa_director.getBranch_id());
								wa_ft2.setBukrs(wa_contract.getBukrs());
								wa_ft2.setType(premium);
								wa_ft2.setCounter(1);
								l_fact_table.put(key_long, wa_ft2);
								//System.out.println("director");
							}
							

							String bonusType = "";
							if (wa_contract.getTradeIn()==1) bonusType = String.valueOf(tradeIn1);
							else bonusType = String.valueOf(premium);
							GeneralUtil.checkForNullLong(matnr_type_string, "matnr id is null when calculating bonuses for dealers");
							key_long = Long.parseLong(bonusType+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(director_pos_id));
							Bonus wa_bonus = l_bonus_map.get(key_long);
							
							TempPayroll wa_tp = new TempPayroll();
							wa_tp.setBukrs(wa_contract.getBukrs());					
							wa_tp.setBranch_id(wa_contract.getBranch_id());
							wa_tp.setGjahr(a_gjahr);
							wa_tp.setMonat(a_monat);
							wa_tp.setStaff_id(wa_director.getStaff_id());
							wa_tp.setPosition_id((long) director_pos_id);
							wa_tp.setMatnr(Long.parseLong(matnr_string));
							wa_tp.setType(premium);
							wa_tp.setMatnr_count(1);
							wa_tp.setDrcrk("S");
							wa_tp.setContract_number(wa_contract.getContract_number());
							wa_tp.setBranch_name(wa_branch.getText45());
							wa_tp.setBldat(curDate.getTime());
							if (bonusType.equals("17")) wa_tp.setText45("Trade-In 1");
							//1815110220 1815110220
							if (wa_bonus.getWaers().equals("USD"))
							{
								wa_tp.setAmount(wa_bonus.getCoef()*wa_er.getSc_value());
								wa_tp.setWaers(wa_er.getSecondary_currency());
								wa_tp.setKursf(wa_er.getSc_value());
								wa_tp.setBonus_id(wa_bonus.getBonus_id());
							}
							else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
							{
								wa_tp.setAmount(wa_bonus.getCoef());
								wa_tp.setWaers(wa_bonus.getWaers());
								wa_tp.setBonus_id(wa_bonus.getBonus_id());
								wa_tp.setKursf(wa_er.getSc_value());
							}
							else
							{
								throw new DAOException("Bonus currency not equal to the country currency");
							}
							l_tp.add(wa_tp);
							
							if (wa_tp_dealer!=null && wa_tp_dealer.getStaff_id()>0)
							{
								wa_tp_dealer.setDirector_id(wa_director.getStaff_id());
							}
							
						}
						
						//key = bukrs, pyramid_id
						//Coordinator premium position_id=5
						if (wa_director!=null)
						{
							key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_director.getParent_pyramid_id()));
							wa_coordinator = l_pyramid_map.get(key_long);
						}
						
						if (skip_coordinator==false && wa_coordinator!=null && wa_coordinator.getPosition_id()==coordinator_pos_id && wa_coordinator.getStaff_id()!=null)
						{
							
							key_long = Long.parseLong(country_id_string + matnr_type_string + String.valueOf(premium) + String.valueOf(coordinator_pos_id) + String.valueOf(wa_coordinator.getStaff_id()));
							FactTableClass wa_ft = l_fact_table.get(key_long);
							if (wa_ft!=null)
							{
								wa_ft.setCounter(wa_ft.getCounter()+1);
							}
							else if (wa_ft==null)
							{
								FactTableClass wa_ft2= new FactTableClass();
								wa_ft2.setCountry_id(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
								wa_ft2.setMatnr(Long.parseLong(matnr_string));
								wa_ft2.setPosition_id(wa_coordinator.getPosition_id());
								wa_ft2.setStaff_id(wa_coordinator.getStaff_id());
								wa_ft2.setBranch_id(wa_branch.getBranch_id());
								wa_ft2.setBukrs(wa_contract.getBukrs());
								wa_ft2.setType(premium);
								wa_ft2.setCounter(1);
								l_fact_table.put(key_long, wa_ft2);
							}
							
							if (wa_tp_dealer!=null && wa_tp_dealer.getStaff_id()>0)
							{
								wa_tp_dealer.setCoordinator_id(wa_coordinator.getStaff_id());
							}
							
						}
						
						
						//key = a_bukrs + position_id_string + branch_id_string
						//Assitant director position_id=6
						key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(assistant_director_pos_id)+String.valueOf(wa_contract.getBranch_id()));
						wa_assistant_director = l_pyramid_ass_dir_map.get(key_long);
						
						if (wa_assistant_director!=null && wa_assistant_director.getPosition_id()==assistant_director_pos_id && wa_assistant_director.getStaff_id()!=null)
						{
							//not take on account if the sale is assiatant director's.
							if (wa_contract.getDealer()!=wa_assistant_director.getStaff_id()){
								key_long = Long.parseLong(country_id_string + matnr_type_string + String.valueOf(premium) + String.valueOf(assistant_director_pos_id) + String.valueOf(wa_assistant_director.getStaff_id()));
								FactTableClass wa_ft = l_fact_table.get(key_long);
								if (wa_ft!=null)
								{
									wa_ft.setCounter(wa_ft.getCounter()+1);
								}
								else if (wa_ft==null)
								{
									FactTableClass wa_ft2= new FactTableClass();
									wa_ft2.setCountry_id(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
									wa_ft2.setMatnr(Long.parseLong(matnr_string));
									wa_ft2.setPosition_id(wa_assistant_director.getPosition_id());
									wa_ft2.setStaff_id(wa_assistant_director.getStaff_id());
									wa_ft2.setBranch_id(wa_assistant_director.getBranch_id());
									wa_ft2.setBukrs(wa_contract.getBukrs());
									wa_ft2.setType(premium);
									wa_ft2.setCounter(1);
									l_fact_table.put(key_long, wa_ft2);
								}
								
								GeneralUtil.checkForNullLong(matnr_type_string, "matnr id is null when calculating bonuses for dealers");
								key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(assistant_director_pos_id));
								Bonus wa_bonus = l_bonus_map.get(key_long);

								
								TempPayroll wa_tp = new TempPayroll();
								wa_tp.setBukrs(wa_contract.getBukrs());					
								wa_tp.setBranch_id(wa_contract.getBranch_id());
								wa_tp.setGjahr(a_gjahr);
								wa_tp.setMonat(a_monat);
								wa_tp.setStaff_id(wa_assistant_director.getStaff_id());
								wa_tp.setPosition_id((long) assistant_director_pos_id);
								wa_tp.setMatnr(Long.parseLong(matnr_string));
								wa_tp.setType(premium);
								wa_tp.setDrcrk("S");
								wa_tp.setMatnr_count(1);
								wa_tp.setContract_number(wa_contract.getContract_number());
								wa_tp.setBranch_name(wa_branch.getText45());
								wa_tp.setBldat(curDate.getTime());
								//1815110220 1815110220
								if (wa_bonus.getWaers().equals("USD"))
								{
									wa_tp.setAmount(wa_bonus.getCoef()*wa_er.getSc_value());
									wa_tp.setWaers(wa_er.getSecondary_currency());
									wa_tp.setKursf(wa_er.getSc_value());
									wa_tp.setBonus_id(wa_bonus.getBonus_id());
								}
								else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
								{
									wa_tp.setAmount(wa_bonus.getCoef());
									wa_tp.setWaers(wa_bonus.getWaers());
									wa_tp.setBonus_id(wa_bonus.getBonus_id());
									wa_tp.setKursf(wa_er.getSc_value());
								}
								else
								{
									throw new DAOException("Bonus currency not equal to the country currency");
								}
								l_tp.add(wa_tp);
								
							}
							
							
							
						}
						
						//key = a_bukrs + position_id_string + branch_id_string
						//Trainer premium position_id=12
						key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(trainer_pos_id)+String.valueOf(wa_contract.getBranch_id()));
						wa_trainer = l_pyramid_trainer_map.get(key_long);
						if (wa_trainer!=null && wa_trainer.getPosition_id()==trainer_pos_id && wa_trainer.getStaff_id()!=null)
						{
							key_long = Long.parseLong(country_id_string + matnr_type_string + String.valueOf(premium) + String.valueOf(trainer_pos_id) + String.valueOf(wa_trainer.getStaff_id()));
							FactTableClass wa_ft = l_fact_table.get(key_long);
							if (wa_ft!=null)
							{
								wa_ft.setCounter(wa_ft.getCounter()+1);
							}
							else if (wa_ft==null)
							{
								FactTableClass wa_ft2= new FactTableClass();
								wa_ft2.setCountry_id(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
								wa_ft2.setMatnr(Long.parseLong(matnr_string));
								wa_ft2.setPosition_id(wa_trainer.getPosition_id());
								wa_ft2.setStaff_id(wa_trainer.getStaff_id());
								wa_ft2.setBranch_id(wa_trainer.getBranch_id());
								wa_ft2.setBukrs(wa_contract.getBukrs());
								wa_ft2.setType(premium);
								wa_ft2.setCounter(1);
								l_fact_table.put(key_long, wa_ft2);
							}
							
							GeneralUtil.checkForNullLong(matnr_type_string, "matnr id is null when calculating bonuses for dealers");
							key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(trainer_pos_id));
							Bonus wa_bonus = l_bonus_map.get(key_long);

							
							TempPayroll wa_tp = new TempPayroll();
							wa_tp.setBukrs(wa_contract.getBukrs());					
							wa_tp.setBranch_id(wa_contract.getBranch_id());
							wa_tp.setGjahr(a_gjahr);
							wa_tp.setMonat(a_monat);
							wa_tp.setStaff_id(wa_trainer.getStaff_id());
							wa_tp.setPosition_id((long) trainer_pos_id);
							wa_tp.setMatnr(Long.parseLong(matnr_string));
							wa_tp.setType(premium);
							wa_tp.setDrcrk("S");
							wa_tp.setMatnr_count(1);
							wa_tp.setContract_number(wa_contract.getContract_number());
							wa_tp.setBranch_name(wa_branch.getText45());
							wa_tp.setBldat(curDate.getTime());
							//1815110220 1815110220
							if (wa_bonus.getWaers().equals("USD"))
							{
								wa_tp.setAmount(wa_bonus.getCoef()*wa_er.getSc_value());
								wa_tp.setWaers(wa_er.getSecondary_currency());
								wa_tp.setKursf(wa_er.getSc_value());
								wa_tp.setBonus_id(wa_bonus.getBonus_id());
							}
							else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
							{
								wa_tp.setAmount(wa_bonus.getCoef());
								wa_tp.setWaers(wa_bonus.getWaers());
								wa_tp.setBonus_id(wa_bonus.getBonus_id());
								wa_tp.setKursf(wa_er.getSc_value());
							}
							else
							{
								throw new DAOException("Bonus currency not equal to the country currency");
							}
							l_tp.add(wa_tp);
							if (wa_tp_dealer!=null && wa_tp_dealer.getStaff_id()>0)
							{
								wa_tp_dealer.setTrainer_id(wa_trainer.getStaff_id());
							}
							
							
						}
						//key = a_bukrs + position_id_string + Business_area_id)
						//Main trainer premium position_id=15
						ContractType wa_con_type = map_con_type.get(wa_contract.getContract_type_id());
						if (wa_con_type==null)
						{
							throw new DAOException("No such contract_type in DB");
						}
						
						key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(main_trainer_pos_id)+String.valueOf(wa_con_type.getBusiness_area_id()));
						wa_trainer_main = l_pyramid_trainer_main_map.get(key_long);
						//System.out.println(l_pyramid_trainer_main_map.size()+ " size");
						//System.out.println(key_long);
						//System.out.println(wa_trainer_main.getStaff_id());
						if (wa_trainer_main!=null && wa_trainer_main.getPosition_id()==main_trainer_pos_id && wa_trainer_main.getStaff_id()!=null)
						{
							
							key_long = Long.parseLong(country_id_string + matnr_type_string + String.valueOf(premium) + String.valueOf(main_trainer_pos_id) + String.valueOf(wa_trainer_main.getStaff_id()));
							FactTableClass wa_ft = l_fact_table.get(key_long);
							if (wa_ft!=null)
							{
								wa_ft.setCounter(wa_ft.getCounter()+1);
							}
							else if (wa_ft==null)
							{
								FactTableClass wa_ft2= new FactTableClass();
								wa_ft2.setCountry_id(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
								wa_ft2.setMatnr(Long.parseLong(matnr_string));
								wa_ft2.setPosition_id(wa_trainer_main.getPosition_id());
								wa_ft2.setStaff_id(wa_trainer_main.getStaff_id());
								wa_ft2.setBranch_id(wa_trainer_main.getBranch_id());
								wa_ft2.setBukrs(wa_contract.getBukrs());
								wa_ft2.setType(premium);
								wa_ft2.setCounter(1);
								l_fact_table.put(key_long, wa_ft2);
							}
							
							if (wa_tp_dealer!=null && wa_tp_dealer.getStaff_id()>0)
							{
								wa_tp_dealer.setMain_trainer_id(wa_trainer_main.getStaff_id());
							}
							
						}
						
						
					//System.out.println("end");
					}					
				}
				
				
			
		}
		catch (DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	public void collectFactCollector2000(Map<Long, FactTableClass> l_fact_table, List<Contract> l_contracts_collector, 
			Map<Long,Contract> l_contracts_collected_amount_map,Map<Long,Contract> l_closed_contracts_collected_amount_map,String a_bukrs, int bonus)
	{
		try
		{ 
			Long key_long = null;
			for(Contract wa_contract:l_contracts_collector)
			{		
				FactTableClass wa_ft2= new FactTableClass();
				wa_ft2.setPosition_id((long) collector_pos_id);
				wa_ft2.setStaff_id(wa_contract.getCollector());
				wa_ft2.setBukrs(wa_contract.getBukrs());
				wa_ft2.setPlan_amount(wa_contract.getPrice());				
				wa_ft2.setType(bonus);
				//wa_ft2.setCountry_id(wa_contract.getAddr_dom_countryid()); //change country id
				
				Contract wa_contract3 = l_closed_contracts_collected_amount_map.get(wa_contract.getCollector());
				if (wa_contract3!=null && wa_contract3.getCollector()!=null && wa_contract3.getCollector()>0)
				{
					wa_ft2.setPlan_amount(wa_ft2.getPlan_amount() + wa_contract3.getPrice());
				}
				
				Contract wa_contract2 = l_contracts_collected_amount_map.get(wa_contract.getCollector());
				if (wa_contract2!=null && wa_contract2.getCollector()!=null && wa_contract2.getCollector()>0)
				{
					wa_ft2.setFact_amount(wa_contract2.getPaid());
				}
				
				//key_long = Long.parseLong(String.valueOf(wa_contract.getAddr_dom_countryid()) + "0" + String.valueOf(bonus) + String.valueOf(collector_pos_id) + String.valueOf(wa_contract.getCollector()));	
				l_fact_table.put(key_long, wa_ft2);
				
			}	
		}
		catch (DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	public void applyBonus2000(List<TempPayroll> l_tp, Map<Long,FactTableClass> l_fact_table, Map<Long,Branch> map_branch, Map<Long,Staff> map_staff, Map<Long,ExchangeRate> map_er, 
			Map<Long,Bonus> l_bonus_map, 
			List<Bonus> l_bonus_collector, int a_gjahr, int a_monat
			,Map<Long,List<Bonus>> l_bonus_manager_map){
		try{
			Long key_long = null;
			for (Map.Entry<Long, FactTableClass> entry : l_fact_table.entrySet()) {
		    	key_long = null;
		        FactTableClass wa_ft = new FactTableClass();
		        wa_ft = (FactTableClass) entry.getValue();
		        //System.out.println(entry.getKey());
		        Calendar curDate =Calendar.getInstance();
		        Calendar firstDay = Calendar.getInstance(); 
				Calendar lastDay = Calendar.getInstance();			  
				firstDay.set(a_gjahr, a_monat-1, 1);
				lastDay.set(a_gjahr, a_monat-1, firstDay.getActualMaximum(Calendar.DAY_OF_MONTH));
		        GeneralUtil.checkForNullLong(wa_ft.getCountry_id(), " No country id when calculating bonuses");
		        ExchangeRate wa_er = map_er.get(wa_ft.getCountry_id());
		        if (wa_ft.getStaff_id()!=null && wa_ft.getStaff_id()>0)
		        {
		        	TempPayroll wa_tp = new TempPayroll();
					wa_tp.setBukrs(wa_ft.getBukrs());
					wa_tp.setStaff_id(wa_ft.getStaff_id());
					wa_tp.setBranch_id(wa_ft.getBranch_id());
					wa_tp.setGjahr(a_gjahr);
					wa_tp.setMonat(a_monat);
					wa_tp.setPosition_id(wa_ft.getPosition_id());
					wa_tp.setType(wa_ft.getType());
					wa_tp.setFact_amount(wa_ft.getFact_amount());
					wa_tp.setPlan_amount(wa_ft.getPlan_amount());
					wa_tp.setMatnr(wa_ft.getMatnr());
					wa_tp.setMatnr_count(wa_ft.getCounter());
					wa_tp.setBldat(curDate.getTime());
					wa_tp.setFrom_date(firstDay.getTime());
					wa_tp.setTo_date(lastDay.getTime());
					wa_tp.setDrcrk("S");
					Branch wa_branch = map_branch.get(wa_ft.getBranch_id());
					Staff wa_staff = map_staff.get(wa_ft.getStaff_id());
					if (wa_staff!=null && wa_branch==null)
					{
						wa_branch = map_branch.get(wa_staff.getBranch_id());
					}
					if (wa_staff!=null && wa_branch!=null)
					{	
						wa_tp.setBranch_name(wa_branch.getText45());
						wa_tp.setStaff_name(wa_staff.getLastname()+" "+wa_staff.getFirstname()+" "+wa_staff.getMiddlename());
						if (wa_tp.getCustomer_id()==null || wa_tp.getCustomer_id()==0)
						{
							wa_tp.setCustomer_id(wa_staff.getCustomer_id());
						}
						
//						if(entry.getKey().equals(1913134305L))
//						{		
//							System.out.println("manager");
//						}
						
						//Premium
						//key = bonus_type_id, country_id, matnr, position_id
						
//						if (wa_ft.getPosition_id()==main_trainer_pos_id)
//						{
//							
//							GeneralUtil.checkForNullLong(wa_ft.getMatnr(), "matnr id is null when calculating bonuses for dealers");
//							key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_ft.getBranch_id())+String.valueOf(wa_tp.getMatnr())+String.valueOf(wa_ft.getPosition_id()));
//							Bonus wa_bonus = l_bonus_map.get(key_long);
//							Branch main_trainer_branch = brnDao.find(wa_ft.getBranch_id());
//							ExchangeRate wa_er2 = map_er.get(main_trainer_branch.getCountry_id());
//							if (wa_bonus!=null && wa_er2!=null)
//							{
//								
//								
//								//1815110220 1815110220
//								if (wa_bonus.getWaers().equals("USD"))
//								{
//									wa_tp.setAmount(wa_tp.getMatnr_count()*wa_bonus.getCoef()*wa_er2.getSc_value());
//									wa_tp.setWaers(wa_er2.getSecondary_currency());
//									wa_tp.setKursf(wa_er2.getSc_value());
//									wa_tp.setBonus_id(wa_bonus.getBonus_id());
//								}
//								else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er2.getSecondary_currency()))
//								{
//									wa_tp.setAmount(wa_tp.getMatnr_count()*wa_bonus.getCoef());
//									wa_tp.setWaers(wa_bonus.getWaers());
//									wa_tp.setBonus_id(wa_bonus.getBonus_id());
//									wa_tp.setKursf(wa_er2.getSc_value());
//								}
//								else
//								{
//									throw new DAOException("Bonus currency not equal to the country currency");
//								}
//								
//								
//								
//								if (wa_tp.getAmount()>0)
//								{
//									l_tp.add(wa_tp);
//								}
//							}
//						}
//						
//						
//						//bonus
//						//key = bonus_type_id, country_id, matnr, position_id
//						if (wa_ft.getPosition_id()==collector_pos_id)
//						{
//							TempPayroll wa_tp3 = new TempPayroll();
//							BeanUtils.copyProperties(wa_tp, wa_tp3);
//							wa_tp3.setAmount(0);
//							for (Bonus wa_bonus:l_bonus_collector)
//							{
//								double wa_percentage = (100*wa_tp3.getFact_amount())/wa_tp3.getPlan_amount();
//								if (wa_bonus.getFrom_num()<=wa_percentage && wa_percentage<=wa_bonus.getTo_num())
//								{
//									if (wa_bonus!=null && wa_er!=null)
//									{
//										wa_tp3.setAmount(wa_tp3.getPlan_amount()*wa_bonus.getCoef()/100*wa_er.getSc_value());
//										wa_tp3.setWaers(wa_er.getSecondary_currency());
//										wa_tp3.setKursf(wa_er.getSc_value());
//										wa_tp3.setBranch_id(wa_staff.getBranch_id());
//										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
//									}
//									break;
//								}
//								
//							}
//							if (wa_tp3.getAmount()>0)
//							{
//								l_tp.add(wa_tp3);
//							}
//							
//							
//						}
						//Bonus
						//key = bonus_type_id, country_id, matnr, position_id
						
						
						if (wa_ft.getPosition_id()==manager_pos_id)
						{
							
							
							TempPayroll wa_tp3 = new TempPayroll();
							BeanUtils.copyProperties(wa_tp, wa_tp3);
							wa_tp3.setAmount(0);
							
							GeneralUtil.checkForNullLong(wa_ft.getMatnr(), " No matnr id when calculating bonuses for dealers");
							
//							if (wa_ft.getCounter()>=1 && wa_ft.getCounter()<=14)
//							{key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+getMatnrBonusGroup2000(String.valueOf(wa_tp3.getMatnr()))+String.valueOf(manager_pos_id)+"1");}
//							else if (wa_ft.getCounter()>=15 && wa_ft.getCounter()<=99999)
//							{key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+getMatnrBonusGroup2000(String.valueOf(wa_tp3.getMatnr()))+String.valueOf(manager_pos_id)+"2");}
//							Bonus wa_bonus = l_bonus_map.get(key_long);

							key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_ft.getBranch_id())+getMatnrBonusGroup2000(String.valueOf(wa_tp3.getMatnr()))+String.valueOf(wa_ft.getPosition_id()));
							List<Bonus> wal_bonus = l_bonus_manager_map.get(key_long);
							Bonus wa_bonus = null;
							
							
							
							
							key_long = Long.parseLong(String.valueOf(wa_ft.getCountry_id())+getMatnrBonusGroup2000(String.valueOf(wa_tp3.getMatnr()))+String.valueOf(premium)+String.valueOf(dealer_pos_id)+String.valueOf(wa_ft.getStaff_id()));
							FactTableClass wa_ft_bon = l_fact_table.get(key_long);
							int dealerSales = 0;
							if (wa_ft_bon!=null){
								dealerSales = wa_ft_bon.getCounter();
							}
							int groupSales = 0;
							if (wa_ft_bon!=null){
								groupSales = wa_tp.getMatnr_count();
							}
							
							for(Bonus wa:wal_bonus)
							{
								if (wa.getFrom_num()<=groupSales && groupSales<=wa.getTo_num())
								{
									wa_bonus = wa;
									break;
								}
							}
							if (wa_bonus==null || dealerSales<wa_bonus.getReq_value())
							{
								for (int i =0; i<l_tp.size();i++)
								{
									TempPayroll wa_tp_roll =  new TempPayroll();
									wa_tp_roll = l_tp.get(i);
//									if (wa_tp_roll.getContract_number()!=null && wa_tp_roll.getContract_number().equals(366929L))
//							        {
//										System.out.println("Delete123");
//							        }
									
									if (wa_tp_roll.getMatnr()!=null && getMatnrBonusGroup2000(wa_tp_roll.getMatnr()).equals(getMatnrBonusGroup2000(String.valueOf(wa_tp3.getMatnr()))))
									{	
										if (wa_tp_roll.getPosition_id()==manager_pos_id && wa_tp_roll.getStaff_id().equals(wa_ft.getStaff_id()))
										{
//											if (wa_tp.getStaff_id().equals(5031L))
//											{
//												System.out.println(5031L+" "+wa_tp_roll.getContract_number());
//											}
											l_tp.remove(i);
											i--;
										}
									}
								}
							}
//							if (wa_bonus!=null && wa_er!=null)
//							{
//								
//								if (wa_ft_bon==null || wa_ft_bon.getCounter()<wa_bonus.getReq_value()|| wa_tp.getMatnr_count()<6)
//								{
//									for (int i =0; i<l_tp.size();i++)
//									{
//										TempPayroll wa_tp_roll =  new TempPayroll();
//										wa_tp_roll = l_tp.get(i);
//										
//										if (wa_tp_roll.getMatnr()!=null && getMatnrBonusGroup2000(String.valueOf(wa_tp_roll.getMatnr())).equals(String.valueOf(wa_bonus.getMatnr())))
//										{	
//											if (wa_tp_roll.getPosition_id()==manager_pos_id && wa_tp_roll.getStaff_id().equals(wa_ft.getStaff_id()))
//											{
//												l_tp.remove(i);
//												i--;
//											}
//										}
//									}
//									
//								}
//							}
							GeneralUtil.checkForNullLong(getMatnrBonusGroup2000(String.valueOf(wa_tp3.getMatnr())), "matnr id is null when calculating bonuses for dealers");
							key_long = Long.parseLong(String.valueOf(bonus)+String.valueOf(wa_branch.getBranch_id())+getMatnrBonusGroup2000(String.valueOf(wa_tp3.getMatnr()))+String.valueOf(manager_pos_id));
							Bonus wa_bonus_man = l_bonus_map.get(key_long);
							
														
							
							if (wa_bonus_man!=null && wa_er!=null)
							{
								if (wa_tp3.getMatnr_count()>=wa_bonus_man.getFrom_num())
								{
							
									if (wa_bonus_man.getWaers().equals("USD"))
									{
										wa_tp3.setAmount((wa_tp3.getMatnr_count()-wa_bonus_man.getFrom_num()+1)*wa_bonus_man.getCoef()*wa_er.getSc_value());
										wa_tp3.setWaers(wa_er.getSecondary_currency());
										wa_tp3.setKursf(wa_er.getSc_value());
										wa_tp3.setBonus_id(wa_bonus_man.getBonus_id());
									}
									else if (!wa_bonus_man.getWaers().equals("USD") && wa_bonus_man.getWaers().equals(wa_er.getSecondary_currency()))
									{
										wa_tp3.setAmount((wa_tp3.getMatnr_count()-wa_bonus_man.getFrom_num()+1)*wa_bonus_man.getCoef());
										wa_tp3.setWaers(wa_bonus_man.getWaers());
										wa_tp3.setBonus_id(wa_bonus_man.getBonus_id());
										wa_tp3.setKursf(wa_er.getSc_value());
									}
									else
									{
										throw new DAOException("Bonus currency not equal to the country currency");
									}
								}
							}
							if (wa_tp3.getAmount()>0)
							{
								l_tp.add(wa_tp3);
							}
						}	
						//Bonus
						//key = bonus_type_id, country_id, matnr, position_id
						if (wa_ft.getPosition_id()==trainer_pos_id || wa_ft.getPosition_id()==director_pos_id)
						{
							TempPayroll wa_tp3 = new TempPayroll();
							BeanUtils.copyProperties(wa_tp, wa_tp3);
							wa_tp3.setAmount(0);
							GeneralUtil.checkForNullLong(wa_ft.getMatnr(), " No matnr id when calculating bonuses for dealers");
							key_long = Long.parseLong(String.valueOf(bonus)+String.valueOf(wa_ft.getBranch_id())+String.valueOf(wa_tp3.getMatnr())+String.valueOf(wa_ft.getPosition_id()));
							Bonus wa_bonus = l_bonus_map.get(key_long);
							
							
							if (wa_bonus!=null && wa_er!=null)
							{
								if (wa_tp3.getMatnr_count()>=wa_bonus.getFrom_num())
								{
							
									if (wa_bonus.getWaers().equals("USD"))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef()*wa_er.getSc_value());
										wa_tp3.setWaers(wa_er.getSecondary_currency());
										wa_tp3.setKursf(wa_er.getSc_value());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
									}
									else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef());
										wa_tp3.setWaers(wa_bonus.getWaers());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
										wa_tp3.setKursf(wa_er.getSc_value());
									}
									else
									{
										throw new DAOException("Bonus currency not equal to the country currency");
									}
								}
							}
							if (wa_tp3.getAmount()>0)
							{
								l_tp.add(wa_tp3);
							}
						}
						//bonus
						if (wa_ft.getPosition_id()==main_trainer_pos_id)
						{
							TempPayroll wa_tp3 = new TempPayroll();
							BeanUtils.copyProperties(wa_tp, wa_tp3);
							wa_tp3.setAmount(0);
							GeneralUtil.checkForNullLong(wa_ft.getMatnr(), " No matnr id when calculating bonuses for dealers");
							key_long = Long.parseLong(String.valueOf(bonus)+String.valueOf(wa_ft.getBranch_id())+String.valueOf(wa_tp3.getMatnr())+String.valueOf(wa_ft.getPosition_id()));
							Bonus wa_bonus = l_bonus_map.get(key_long);
							Branch main_trainer_branch = brnDao.find(wa_ft.getBranch_id());
							ExchangeRate wa_er2 = map_er.get(main_trainer_branch.getCountry_id());
							
							if (wa_bonus!=null && wa_er2!=null)
							{
								if (wa_tp3.getMatnr_count()>=wa_bonus.getFrom_num())
								{
							
									if (wa_bonus.getWaers().equals("USD"))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef()*wa_er2.getSc_value());
										wa_tp3.setWaers(wa_er2.getSecondary_currency());
										wa_tp3.setKursf(wa_er2.getSc_value());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
									}
									else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er2.getSecondary_currency()))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef());
										wa_tp3.setWaers(wa_bonus.getWaers());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
										wa_tp3.setKursf(wa_er2.getSc_value());
									}
									else
									{
										throw new DAOException("Bonus currency not equal to the country currency");
									}
								}
							}
							if (wa_tp3.getAmount()>0)
							{
								l_tp.add(wa_tp3);
							}
						}
						//premium
						if (wa_ft.getPosition_id()==main_trainer_pos_id)
						{
							TempPayroll wa_tp3 = new TempPayroll();
							BeanUtils.copyProperties(wa_tp, wa_tp3);
							wa_tp3.setAmount(0);
							GeneralUtil.checkForNullLong(wa_ft.getMatnr(), " No matnr id when calculating bonuses for dealers");
							key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_ft.getBranch_id())+String.valueOf(wa_tp3.getMatnr())+String.valueOf(wa_ft.getPosition_id()));
							Bonus wa_bonus = l_bonus_map.get(key_long);
							Branch main_trainer_branch = brnDao.find(wa_ft.getBranch_id());
							ExchangeRate wa_er2 = map_er.get(main_trainer_branch.getCountry_id());
							
							if (wa_bonus!=null && wa_er2!=null)
							{
								if (wa_tp3.getMatnr_count()>=wa_bonus.getFrom_num())
								{
							
									if (wa_bonus.getWaers().equals("USD"))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef()*wa_er2.getSc_value());
										wa_tp3.setWaers(wa_er2.getSecondary_currency());
										wa_tp3.setKursf(wa_er2.getSc_value());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
									}
									else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er2.getSecondary_currency()))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef());
										wa_tp3.setWaers(wa_bonus.getWaers());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
										wa_tp3.setKursf(wa_er2.getSc_value());
									}
									else
									{
										throw new DAOException("Bonus currency not equal to the country currency");
									}
								}
							}
							if (wa_tp3.getAmount()>0)
							{
								l_tp.add(wa_tp3);
							}
						}
						//bonus
						if (wa_ft.getPosition_id()==coordinator_pos_id)
						{
							TempPayroll wa_tp3 = new TempPayroll();
							BeanUtils.copyProperties(wa_tp, wa_tp3);
							wa_tp3.setAmount(0);
							GeneralUtil.checkForNullLong(wa_ft.getMatnr(), " No matnr id when calculating bonuses for dealers");
							key_long = Long.parseLong(String.valueOf(bonus)+String.valueOf(wa_ft.getBranch_id())+String.valueOf(wa_tp3.getMatnr())+String.valueOf(wa_ft.getPosition_id()));
							Bonus wa_bonus = l_bonus_map.get(key_long);
							Branch coordinator_branch = brnDao.find(wa_ft.getBranch_id());
							ExchangeRate wa_er2 = map_er.get(coordinator_branch.getCountry_id());
							
							if (wa_bonus!=null && wa_er2!=null)
							{
								if (wa_tp3.getMatnr_count()>=wa_bonus.getFrom_num())
								{
							
									if (wa_bonus.getWaers().equals("USD"))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef()*wa_er2.getSc_value());
										wa_tp3.setWaers(wa_er2.getSecondary_currency());
										wa_tp3.setKursf(wa_er2.getSc_value());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
									}
									else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er2.getSecondary_currency()))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef());
										wa_tp3.setWaers(wa_bonus.getWaers());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
										wa_tp3.setKursf(wa_er2.getSc_value());
									}
									else
									{
										throw new DAOException("Bonus currency not equal to the country currency");
									}
								}
							}
							if (wa_tp3.getAmount()>0)
							{
								l_tp.add(wa_tp3);
							}
						}
						//premium
						if (wa_ft.getPosition_id()==coordinator_pos_id)
						{
							TempPayroll wa_tp3 = new TempPayroll();
							BeanUtils.copyProperties(wa_tp, wa_tp3);
							wa_tp3.setAmount(0);
							GeneralUtil.checkForNullLong(wa_ft.getMatnr(), " No matnr id when calculating bonuses for dealers");
							key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_ft.getBranch_id())+String.valueOf(wa_tp3.getMatnr())+String.valueOf(wa_ft.getPosition_id()));
							Bonus wa_bonus = l_bonus_map.get(key_long);
							Branch coordinator_branch = brnDao.find(wa_ft.getBranch_id());
							ExchangeRate wa_er2 = map_er.get(coordinator_branch.getCountry_id());
							
							if (wa_bonus!=null && wa_er2!=null)
							{
								if (wa_tp3.getMatnr_count()>=wa_bonus.getFrom_num())
								{
							
									if (wa_bonus.getWaers().equals("USD"))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef()*wa_er2.getSc_value());
										wa_tp3.setWaers(wa_er2.getSecondary_currency());
										wa_tp3.setKursf(wa_er2.getSc_value());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
									}
									else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er2.getSecondary_currency()))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef());
										wa_tp3.setWaers(wa_bonus.getWaers());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
										wa_tp3.setKursf(wa_er2.getSc_value());
									}
									else
									{
										throw new DAOException("Bonus currency not equal to the country currency");
									}
								}
							}
							if (wa_tp3.getAmount()>0)
							{
								l_tp.add(wa_tp3);
							}
						}
						
					}
		        }

		    }

		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	public void collectFact1000(List<TempPayroll> l_tp,Map<Long,FactTableClass> l_fact_table, List<Contract> l_contracts, String a_bukrs, Map<Long,ExchangeRate> map_er,
			Map<Long,Pyramid> l_pyramid_map, Map<Long,Pyramid> l_pyramid_staff_map, Map<Long,Pyramid> l_pyramid_trainer_map, 
			Map<Long,Pyramid> l_pyramid_trainer_main_map, Map<Long,ContractType> map_con_type,Map<Long,Bonus> l_bonus_map,int a_gjahr,int a_monat,Map<Long, 
			PriceList> map_price_list,Map<Long,Branch> map_branch, Map<Long,Staff> map_staff,List<Bonus> l_bonus_manager,
			Map<Long,List<Bonus>> l_bonus_manager_map,Map<Long,List<Bonus>> l_bonus_dealer_map,
			Map<Long,Pyramid> l_pyramid_ass_dir_map
			
			
			)
	{
		try
		{
			//////////////////////////////////////////////////////////////////////////////////
			//////////////////////////////////////////////////////////////////////////////////
			
			String coutry_id_string = "";
			String position_id_string = "";
			String matnr_string = "";
			String matnr_type_string = "";
			String staff_id_string = "";
			String type_string = ""; 
			Long key_long = null;
			
				coutry_id_string = ""; //change country id
				position_id_string = "";
				matnr_string = "";
				matnr_type_string = "";
				staff_id_string = "";
				type_string = "";
				key_long = null;
				Calendar curDate = Calendar.getInstance();

				
				//GreenLight
				for (Contract wa_contract :l_contracts)
				{
//						11	Roboclean 114K SPLUS	4
//					    1	Roboclean 114F	1
					
//						2	Cebilon Digital Unique	812					
//						7	Compact Cebilon-101MC	256
//						8	Cebilon-S	3
//						9	Cebilon-101M	2
//						12	Rexwat ATLAS PREMIER 22	816
//						13	Rexwat ECO	817
//						14	Rexwat ATLAS PREMIER 15	910
//						15	Rexwat ECO RO ReStyle 2016	913
//						


					Branch wa_branch = map_branch.get(wa_contract.getBranch_id());
					
					coutry_id_string = ""; //change country id
					position_id_string = "";
					matnr_string = "";
					matnr_type_string = "";
					staff_id_string = "";
					type_string = "";
					System.out.println(wa_contract.getContract_id() + " Contract_id; "+wa_contract.getDealer()+" Staff_id dealer");
					if (wa_contract.getContract_id().equals(445485L))
					{
						System.out.println(wa_contract.getContract_id());
					}
					TempPayroll wa_tp_dealer = new TempPayroll();
					
					if (a_bukrs.equals("1000"))
					{
						
						if (wa_contract.getContract_type_id()==1) {matnr_string = "1"; matnr_type_string = "4";}//Rob 114F						
						else if (wa_contract.getContract_type_id()==11){ matnr_string = "4"; matnr_type_string = "4";}//Rob Splus		
						else if (wa_contract.getContract_type_id()==2){ matnr_string = "812"; matnr_type_string = "812";}//Ceb Dig Uniqe
						else if (wa_contract.getContract_type_id()==7){ matnr_string = "256"; matnr_type_string = "812";}//Compact Cebilon-101MC
						else if (wa_contract.getContract_type_id()==8) {matnr_string = "3"; matnr_type_string = "812";}//Cebilon-S
						else if (wa_contract.getContract_type_id()==9) {matnr_string = "2"; matnr_type_string = "812";}//Cebilon-101M
						else if (wa_contract.getContract_type_id()==12) {matnr_string = "816"; matnr_type_string = "816";}//Rexwat ATLAS PREMIER 22
						else if (wa_contract.getContract_type_id()==13) {matnr_string = "817"; matnr_type_string = "913";}//Rexwat ECO
						else if (wa_contract.getContract_type_id()==14) {matnr_string = "910"; matnr_type_string = "910";}//Rexwat ATLAS PREMIER 15
						else if (wa_contract.getContract_type_id()==15) {matnr_string = "913"; matnr_type_string = "913";}//Rexwat ECO RO ReStyle 2016

						else
						{
							continue;
						}
					}
					
					ExchangeRate wa_er = map_er.get(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid());
					
					

					
					
					if (wa_contract.getDealer()!=null)
					{	
						//Dealer position_id = 4
						coutry_id_string = String.valueOf(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
						position_id_string = String.valueOf(dealer_pos_id);
						type_string = String.valueOf(premium);
						staff_id_string = String.valueOf(wa_contract.getDealer());
						//key_long = country_id, matnr, type, position_id, staff_id
						key_long = Long.parseLong(coutry_id_string + matnr_type_string + type_string + position_id_string + staff_id_string);
						double counterGoods = 0;
						FactTableClass wa_ft = l_fact_table.get(key_long);
						if (wa_ft!=null)
						{
							wa_ft.setCounter(wa_ft.getCounter()+1);
							counterGoods = wa_ft.getCounter();
						}
						else if (wa_ft==null)
						{
							FactTableClass wa_ft2= new FactTableClass();
							wa_ft2.setCountry_id(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
							wa_ft2.setMatnr(Long.parseLong(matnr_string));
							wa_ft2.setPosition_id((long) dealer_pos_id);
							wa_ft2.setStaff_id(wa_contract.getDealer());
							wa_ft2.setBranch_id(wa_contract.getBranch_id());
							wa_ft2.setBukrs(wa_contract.getBukrs());
							wa_ft2.setType(premium);
							wa_ft2.setCounter(1);
							l_fact_table.put(key_long, wa_ft2);
							counterGoods = wa_ft2.getCounter();
						}
						
						

						
						double deposit_amount = 0;
						GeneralUtil.checkForNullLong(matnr_type_string, "matnr id is null when calculating bonuses for dealers");
						String bonusType = "";
						if (wa_contract.getTradeIn()==1) bonusType = String.valueOf(tradeIn1);
						else bonusType = String.valueOf(premium);
						
						key_long = Long.parseLong(bonusType+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(dealer_pos_id));
						List<Bonus> wal_bonus = l_bonus_dealer_map.get(key_long);
						Bonus wa_bonus = null;
						for(Bonus wa:wal_bonus)
						{
							if (wa.getFrom_num()<=counterGoods && counterGoods<= wa.getTo_num())
							{
								wa_bonus = new Bonus();
								wa_bonus = wa;
								break;
							}
						}
						if (wa_bonus==null)
						{
							throw new DAOException("dealer premium not found, Payroll Service impl 2663 line");
						}
						
						
						double subtructed_summa = 0;
						//добавляю депозитные суммы для дилеров
						if (wa_contract.getPayment_schedule()>1)
						{
							
							
							
							TempPayroll wa_tp_deposit = new TempPayroll();
							wa_tp_deposit.setBukrs(wa_contract.getBukrs());					
							wa_tp_deposit.setBranch_id(wa_contract.getBranch_id());
							wa_tp_deposit.setGjahr(a_gjahr);
							wa_tp_deposit.setMonat(a_monat);
							wa_tp_deposit.setStaff_id(wa_contract.getDealer());
							wa_tp_deposit.setPosition_id((long) dealer_pos_id);
							wa_tp_deposit.setType(deposit);
							wa_tp_deposit.setMatnr(Long.parseLong(matnr_string));
							wa_tp_deposit.setContract_number(wa_contract.getContract_number());
							wa_tp_deposit.setMatnr_count(1);
							wa_tp_deposit.setBranch_name(wa_branch.getText45());
							wa_tp_deposit.setDrcrk("S");
							wa_tp_deposit.setBldat(curDate.getTime());
							if (bonusType.equals("17")) wa_tp_deposit.setText45("Trade-In 1");
							if (wa_bonus.getWaers().equals("USD"))
							{
								wa_tp_deposit.setAmount(wa_bonus.getDeposit()*wa_er.getSc_value());
								wa_tp_deposit.setWaers(wa_er.getSecondary_currency());
								wa_tp_deposit.setKursf(wa_er.getSc_value());
								wa_tp_deposit.setBonus_id(wa_bonus.getBonus_id());
							}
							else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
							{
								wa_tp_deposit.setAmount(wa_bonus.getDeposit());
								wa_tp_deposit.setWaers(wa_bonus.getWaers());
								wa_tp_deposit.setBonus_id(wa_bonus.getBonus_id());
								wa_tp_deposit.setKursf(wa_er.getSc_value());
							}
							deposit_amount = wa_tp_deposit.getAmount();
							subtructed_summa = deposit_amount;
							l_tp.add(wa_tp_deposit);
							
							PriceList wa_pl = map_price_list.get(wa_contract.getPrice_list_id());
							if (wa_pl!=null && wa_pl.getPrem_div()>=1L)
							{
								double div_coef = 0;
								div_coef = wa_bonus.getCoef()/wa_pl.getPrem_div();
								if (wa_pl.getPrem_div()>=1L)
								{
									int loop_size = Integer.parseInt(String.valueOf(wa_pl.getPrem_div()));
									for (int o = 1;o<=loop_size;o++)
									{
										TempPayroll wa_tp_dealer2 = new TempPayroll();
										wa_tp_dealer2.setBukrs(wa_contract.getBukrs());					
										wa_tp_dealer2.setBranch_id(wa_contract.getBranch_id());
										wa_tp_dealer2.setGjahr(a_gjahr);
										wa_tp_dealer2.setMonat(a_monat);
										wa_tp_dealer2.setStaff_id(wa_contract.getDealer());
										wa_tp_dealer2.setPosition_id((long) dealer_pos_id);
										wa_tp_dealer2.setMatnr(Long.parseLong(matnr_string));
										wa_tp_dealer2.setType(premium);
										wa_tp_dealer2.setDrcrk("S");
										Calendar firstDay = Calendar.getInstance();
										firstDay.add(Calendar.MONTH, o-1);
										firstDay.set(Calendar.DAY_OF_MONTH, 1);
										wa_tp_dealer2.setBldat(firstDay.getTime());
										//wa_tp2.setBldat();
										wa_tp_dealer2.setContract_number(wa_contract.getContract_number());
										wa_tp_dealer2.setMatnr_count(1);
										wa_tp_dealer2.setBranch_name(wa_branch.getText45());
										if (bonusType.equals("17")) wa_tp_dealer2.setText45("Trade-In 1");
										//1815110220 1815110220
										if (wa_bonus.getWaers().equals("USD"))
										{
											wa_tp_dealer2.setAmount(div_coef*wa_er.getSc_value());
											wa_tp_dealer2.setWaers(wa_er.getSecondary_currency());
											wa_tp_dealer2.setKursf(wa_er.getSc_value());
											wa_tp_dealer2.setBonus_id(wa_bonus.getBonus_id());
										}
										else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
										{
											wa_tp_dealer2.setAmount(div_coef);
											wa_tp_dealer2.setWaers(wa_bonus.getWaers());
											wa_tp_dealer2.setBonus_id(wa_bonus.getBonus_id());
											wa_tp_dealer2.setKursf(wa_er.getSc_value());
										}
										else
										{
											throw new DAOException("Bonus currency not equal to the country currency");
										}	
										
										subtructed_summa = subtructed_summa + wa_tp_dealer2.getAmount();	
										if (o==loop_size)
										{
											subtructed_summa = subtructed_summa - deposit_amount;
											wa_tp_dealer2.setAmount(wa_tp_dealer2.getAmount()-deposit_amount);
										}
										
										l_tp.add(wa_tp_dealer2);
									}
									
									
								}
							}
								
							
							
						}
						
						
						wa_tp_dealer.setBukrs(wa_contract.getBukrs());					
						wa_tp_dealer.setBranch_id(wa_contract.getBranch_id());
						wa_tp_dealer.setGjahr(a_gjahr);
						wa_tp_dealer.setMonat(a_monat);
						wa_tp_dealer.setStaff_id(wa_contract.getDealer());
						wa_tp_dealer.setPosition_id((long) dealer_pos_id);
						wa_tp_dealer.setMatnr(Long.parseLong(matnr_string));
						wa_tp_dealer.setType(premium);
						wa_tp_dealer.setContract_number(wa_contract.getContract_number());
						wa_tp_dealer.setMatnr_count(1);
						wa_tp_dealer.setDrcrk("S");
						wa_tp_dealer.setBranch_name(wa_branch.getText45());
						wa_tp_dealer.setBldat(curDate.getTime());
						if (bonusType.equals("17")) wa_tp_dealer.setText45("Trade-In 1");
						//1815110220 1815110220
						if (wa_bonus.getWaers().equals("USD"))
						{
							wa_tp_dealer.setAmount(wa_bonus.getCoef()*wa_er.getSc_value());
							wa_tp_dealer.setWaers(wa_er.getSecondary_currency());
							wa_tp_dealer.setKursf(wa_er.getSc_value());
							wa_tp_dealer.setBonus_id(wa_bonus.getBonus_id());
							
						}
						else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
						{
							wa_tp_dealer.setAmount(wa_bonus.getCoef());
							wa_tp_dealer.setWaers(wa_bonus.getWaers());
							wa_tp_dealer.setBonus_id(wa_bonus.getBonus_id());
							wa_tp_dealer.setKursf(wa_er.getSc_value());
						}
						else
						{
							throw new DAOException("Bonus currency not equal to the country currency");
						}
						wa_tp_dealer.setAmount(wa_tp_dealer.getAmount()-subtructed_summa);
						
						
						l_tp.add(wa_tp_dealer);
						
						

						//
						
					}
					if (wa_contract.getDemo_sc()!=null)
					{
						//Demo secretary  position_id = 8
						coutry_id_string = String.valueOf(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
						position_id_string = String.valueOf(demo_pos_id);
						type_string = String.valueOf(premium);
						staff_id_string = String.valueOf(wa_contract.getDemo_sc());
						//key = country_id, matnr, type, position_id, staff_id
						key_long = Long.parseLong(coutry_id_string + matnr_type_string + type_string + position_id_string + staff_id_string);
						FactTableClass wa_ft3 = l_fact_table.get(key_long);
						if (wa_ft3!=null)
						{
							wa_ft3.setCounter(wa_ft3.getCounter()+1);
						}
						else if (wa_ft3==null)
						{
							FactTableClass wa_ft2= new FactTableClass();
							wa_ft2.setCountry_id(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
							wa_ft2.setMatnr(Long.parseLong(matnr_string));
							wa_ft2.setPosition_id(Long.parseLong(position_id_string));
							wa_ft2.setStaff_id(wa_contract.getDemo_sc());
							wa_ft2.setBranch_id(wa_contract.getBranch_id());
							wa_ft2.setBukrs(wa_contract.getBukrs());
							wa_ft2.setType(premium);
							wa_ft2.setCounter(1);
							l_fact_table.put(key_long, wa_ft2);
						}
						
						
						GeneralUtil.checkForNullLong(matnr_type_string, "matnr id is null when calculating bonuses for dealers");

						String bonusType = "";
						if (wa_contract.getTradeIn()==1) bonusType = String.valueOf(tradeIn1);
						else bonusType = String.valueOf(premium);					
						
						key_long = Long.parseLong(bonusType+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(demo_pos_id));
						Bonus wa_bonus = l_bonus_map.get(key_long);
						
						TempPayroll wa_tp = new TempPayroll();
						wa_tp.setBukrs(wa_contract.getBukrs());					
						wa_tp.setBranch_id(wa_contract.getBranch_id());
						wa_tp.setGjahr(a_gjahr);
						wa_tp.setMonat(a_monat);
						wa_tp.setStaff_id(wa_contract.getDemo_sc());
						wa_tp.setPosition_id((long) demo_pos_id);
						wa_tp.setMatnr(Long.parseLong(matnr_string));
						wa_tp.setType(premium);
						wa_tp.setContract_number(wa_contract.getContract_number());
						wa_tp.setMatnr_count(1);
						wa_tp.setBranch_name(wa_branch.getText45());
						wa_tp.setDrcrk("S");
						wa_tp.setBldat(curDate.getTime());
						if (bonusType.equals("17")) wa_tp.setText45("Trade-In 1");
						//1815110220 1815110220
						if (wa_bonus.getWaers().equals("USD"))
						{
							wa_tp.setAmount(wa_bonus.getCoef()*wa_er.getSc_value());
							wa_tp.setWaers(wa_er.getSecondary_currency());
							wa_tp.setKursf(wa_er.getSc_value());
							wa_tp.setBonus_id(wa_bonus.getBonus_id());
						}
						else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
						{
							wa_tp.setAmount(wa_bonus.getCoef());
							wa_tp.setWaers(wa_bonus.getWaers());
							wa_tp.setBonus_id(wa_bonus.getBonus_id());
							wa_tp.setKursf(wa_er.getSc_value());
						}
						else
						{
							throw new DAOException("Bonus currency not equal to the country currency");
						}
						l_tp.add(wa_tp);
					}
					
					if (wa_contract.getDealer()!=null)
					{
						//key = a_bukrs + staff_id_string + position_id_string + branch_id_string
						//key = bukrs, pyramid_id
						//Manager premium position_id=3
						//System.out.println("beg "+wa_contract.getDealer()+wa_contract.getContract_number());
						boolean skip_manager = false;
						boolean skip_director = false;
						boolean skip_coordinator = false;
						Pyramid wa_dealer = new Pyramid();
						Pyramid wa_manager = new Pyramid();
						Pyramid wa_director = new Pyramid();
						Pyramid wa_coordinator = new Pyramid();
						Pyramid wa_trainer = new Pyramid();
						Pyramid wa_assistant_director = new Pyramid();
						Pyramid wa_trainer_main = new Pyramid();
						key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_contract.getDealer())+String.valueOf(dealer_pos_id)+String.valueOf(wa_contract.getBranch_id()));
						wa_dealer = l_pyramid_staff_map.get(key_long);
						//if(wa_contract.getDealer().equals(209L))
						//{
						//	System.out.println("here");
						//}

						
						if (wa_dealer==null)
						{
							//check if dealer is manager
							key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_contract.getDealer())+String.valueOf(manager_pos_id)+String.valueOf(wa_contract.getBranch_id()));
							wa_manager = l_pyramid_staff_map.get(key_long);
							skip_manager = true;
							
							if (wa_manager==null)
							{
								//check if dealer is director
								key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_contract.getDealer())+String.valueOf(director_pos_id)+String.valueOf(wa_contract.getBranch_id()));
								wa_director = l_pyramid_staff_map.get(key_long);
								skip_director = true;
								
								//30.09.2020
								//direktorlarga ozderinin satularynan bonus tusedi
								if (wa_branch.getCountry_id().equals(1L)||wa_branch.getCountry_id().equals(6L)){
									skip_director = false;
								}								
								///////////////////////////////////////////////////
								
								
								
								if (wa_director==null)
								{
									//skip coordinator
									skip_coordinator = true;
								}
							}
								
						}
						
						if (wa_dealer!=null)
						{
							key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_dealer.getParent_pyramid_id()));
							wa_manager = l_pyramid_map.get(key_long);
						}
						
						
						if (skip_manager==false && wa_manager!=null && wa_manager.getPosition_id()==manager_pos_id && wa_manager.getStaff_id()!=null 
								&& wa_tp_dealer.getStaff_id()!=wa_manager.getStaff_id())
						{
							
							
							//System.out.println("manager found");
							key_long = Long.parseLong(coutry_id_string + matnr_type_string + String.valueOf(premium) + String.valueOf(manager_pos_id) + String.valueOf(wa_manager.getStaff_id()));
							FactTableClass wa_ft = l_fact_table.get(key_long);
							double counterGoods = 0;
							if (wa_ft!=null)
							{
								wa_ft.setCounter(wa_ft.getCounter()+1);
								counterGoods=wa_ft.getCounter();
							}
							else if (wa_ft==null)
							{
								FactTableClass wa_ft2= new FactTableClass();
								wa_ft2.setCountry_id(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
								wa_ft2.setMatnr(Long.parseLong(matnr_string));
								wa_ft2.setPosition_id(wa_manager.getPosition_id());
								wa_ft2.setStaff_id(wa_manager.getStaff_id());
								wa_ft2.setBranch_id(wa_manager.getBranch_id());
								wa_ft2.setBukrs(wa_contract.getBukrs());
								wa_ft2.setType(premium);
								wa_ft2.setCounter(1);
								l_fact_table.put(key_long, wa_ft2);
								counterGoods=wa_ft2.getCounter();
							}
							
							GeneralUtil.checkForNullLong(matnr_type_string, "matnr id is null when calculating bonuses for dealers");


							String bonusType = "";
							if (wa_contract.getTradeIn()==1) bonusType = String.valueOf(tradeIn1);
							else bonusType = String.valueOf(premium);			
							
							key_long = Long.parseLong(bonusType+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(manager_pos_id));
//							Bonus wa_bonus = l_bonus_map.get(key_long);
							List<Bonus> wal_bonus = l_bonus_manager_map.get(key_long);
							Bonus wa_bonus = null;
							for(Bonus wa:wal_bonus)
							{
								if ((wa.getFrom_num()==6 && counterGoods<= wa.getTo_num())||wa.getFrom_num()<=counterGoods && counterGoods<= wa.getTo_num())
								{
									wa_bonus = new Bonus();
									wa_bonus = wa;
									break;
								}
							}
							if (wa_bonus==null)
							{
								throw new DAOException("manager premium not found, Payroll Service impl 2977 line");
							}
							
							
							
							TempPayroll wa_tp = new TempPayroll();
							wa_tp.setBukrs(wa_contract.getBukrs());					
							wa_tp.setBranch_id(wa_contract.getBranch_id());
							wa_tp.setGjahr(a_gjahr);
							wa_tp.setMonat(a_monat);
							wa_tp.setStaff_id(wa_manager.getStaff_id());
							wa_tp.setPosition_id((long) manager_pos_id);
							wa_tp.setMatnr(Long.parseLong(matnr_string));
							wa_tp.setType(premium);
							wa_tp.setMatnr_count(1);
							wa_tp.setDrcrk("S");
							wa_tp.setContract_number(wa_contract.getContract_number());
							wa_tp.setBranch_name(wa_branch.getText45());
							wa_tp.setBldat(curDate.getTime());
							if (bonusType.equals("17")) wa_tp.setText45("Trade-In 1");
							//1815110220 1815110220
							if (wa_bonus.getWaers().equals("USD"))
							{
								wa_tp.setAmount(wa_bonus.getCoef()*wa_er.getSc_value());
								wa_tp.setWaers(wa_er.getSecondary_currency());
								wa_tp.setKursf(wa_er.getSc_value());
								wa_tp.setBonus_id(wa_bonus.getBonus_id());
							}
							else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
							{
								wa_tp.setAmount(wa_bonus.getCoef());
								wa_tp.setWaers(wa_bonus.getWaers());
								wa_tp.setBonus_id(wa_bonus.getBonus_id());
								wa_tp.setKursf(wa_er.getSc_value());
							}
							else
							{
								throw new DAOException("Bonus currency not equal to the country currency");
							}
							l_tp.add(wa_tp);
							
							if (wa_tp_dealer!=null && wa_tp_dealer.getStaff_id()>0)
							{
								wa_tp_dealer.setManager_id(wa_manager.getStaff_id());
							}
						}
						
						//key = bukrs, pyramid_id
						//Director premium position_id=10
						if (wa_manager!=null)
						{
							key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_manager.getParent_pyramid_id()));
							wa_director = l_pyramid_map.get(key_long);
						}
						
						//System.out.println(wa_manager.getParent_pyramid_id());
						if (skip_director==false && wa_director!=null && wa_director.getPosition_id()==director_pos_id && wa_director.getStaff_id()!=null)
						{
							
							key_long = Long.parseLong(coutry_id_string + matnr_type_string + String.valueOf(premium) + String.valueOf(director_pos_id) + String.valueOf(wa_director.getStaff_id()));
							//System.out.println(wa_director.getStaff_id()+" staff "+key_long);
							FactTableClass wa_ft = l_fact_table.get(key_long);
							if (wa_ft!=null)
							{
								wa_ft.setCounter(wa_ft.getCounter()+1);
								//System.out.println("counter "+wa_ft.getCounter());
							}
							else if (wa_ft==null)
							{
								FactTableClass wa_ft2= new FactTableClass();
								wa_ft2.setCountry_id(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
								wa_ft2.setMatnr(Long.parseLong(matnr_string));
								wa_ft2.setPosition_id(wa_director.getPosition_id());
								wa_ft2.setStaff_id(wa_director.getStaff_id());
								wa_ft2.setBranch_id(wa_director.getBranch_id());
								wa_ft2.setBukrs(wa_contract.getBukrs());
								wa_ft2.setType(premium);
								wa_ft2.setCounter(1);
								l_fact_table.put(key_long, wa_ft2);
								//System.out.println("director");
							}
							
							GeneralUtil.checkForNullLong(matnr_type_string, "matnr id is null when calculating bonuses for dealers");

							String bonusType = "";
							if (wa_contract.getTradeIn()==1) bonusType = String.valueOf(tradeIn1);
							else bonusType = String.valueOf(premium);			
							
							
							key_long = Long.parseLong(bonusType+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(director_pos_id));
							Bonus wa_bonus = l_bonus_map.get(key_long);
							
							TempPayroll wa_tp = new TempPayroll();
							wa_tp.setBukrs(wa_contract.getBukrs());					
							wa_tp.setBranch_id(wa_contract.getBranch_id());
							wa_tp.setGjahr(a_gjahr);
							wa_tp.setMonat(a_monat);
							wa_tp.setStaff_id(wa_director.getStaff_id());
							wa_tp.setPosition_id((long) director_pos_id);
							wa_tp.setMatnr(Long.parseLong(matnr_string));
							wa_tp.setType(premium);
							wa_tp.setMatnr_count(1);
							wa_tp.setDrcrk("S");
							wa_tp.setContract_number(wa_contract.getContract_number());
							wa_tp.setBranch_name(wa_branch.getText45());
							wa_tp.setBldat(curDate.getTime());
							if (bonusType.equals("17")) wa_tp.setText45("Trade-In 1");
							//1815110220 1815110220
							if (wa_bonus.getWaers().equals("USD"))
							{
								wa_tp.setAmount(wa_bonus.getCoef()*wa_er.getSc_value());
								wa_tp.setWaers(wa_er.getSecondary_currency());
								wa_tp.setKursf(wa_er.getSc_value());
								wa_tp.setBonus_id(wa_bonus.getBonus_id());
							}
							else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
							{
								wa_tp.setAmount(wa_bonus.getCoef());
								wa_tp.setWaers(wa_bonus.getWaers());
								wa_tp.setBonus_id(wa_bonus.getBonus_id());
								wa_tp.setKursf(wa_er.getSc_value());
							}
							else
							{
								throw new DAOException("Bonus currency not equal to the country currency");
							}
							l_tp.add(wa_tp);
							
							if (wa_tp_dealer!=null && wa_tp_dealer.getStaff_id()>0)
							{
								wa_tp_dealer.setDirector_id(wa_director.getStaff_id());
							}
							
						}
						
						//key = bukrs, pyramid_id
						//Coordinator premium position_id=5
						if (wa_director!=null)
						{
							key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_director.getParent_pyramid_id()));
							wa_coordinator = l_pyramid_map.get(key_long);
						}
						
						if (skip_coordinator==false && wa_coordinator!=null && wa_coordinator.getPosition_id()==coordinator_pos_id && wa_coordinator.getStaff_id()!=null && !wa_branch.getCountry_id().equals(9L))
						{
							
							key_long = Long.parseLong(matnr_type_string + String.valueOf(premium) + String.valueOf(coordinator_pos_id) + String.valueOf(wa_coordinator.getStaff_id()));
							FactTableClass wa_ft = l_fact_table.get(key_long);
							if (wa_ft!=null)
							{
								wa_ft.setCounter(wa_ft.getCounter()+1);
							}
							else if (wa_ft==null)
							{
								FactTableClass wa_ft2= new FactTableClass();
								if (coutry_id_string.equals("5")||coutry_id_string.equals("6"))
								{
									wa_ft2.setCountry_id(1L);
								}
								else
								{
									wa_ft2.setCountry_id(wa_branch.getCountry_id());
								}
								
//								wa_ft2.setCountry_id(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
								wa_ft2.setMatnr(Long.parseLong(matnr_string));
								wa_ft2.setPosition_id(wa_coordinator.getPosition_id());
								wa_ft2.setStaff_id(wa_coordinator.getStaff_id());
								wa_ft2.setBranch_id(wa_coordinator.getBranch_id());
								wa_ft2.setBukrs(wa_contract.getBukrs());
								wa_ft2.setType(premium);
								wa_ft2.setCounter(1);
								l_fact_table.put(key_long, wa_ft2);
							}
							
							if (wa_tp_dealer!=null && wa_tp_dealer.getStaff_id()>0)
							{
								wa_tp_dealer.setCoordinator_id(wa_coordinator.getStaff_id());
							}
							
						}
						
						
						
						
						//key = a_bukrs + position_id_string + branch_id_string
						//Assitant director position_id=6
						key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(assistant_director_pos_id)+String.valueOf(wa_contract.getBranch_id()));
						wa_assistant_director = l_pyramid_ass_dir_map.get(key_long);
						
						if (wa_assistant_director!=null && wa_assistant_director.getPosition_id()==assistant_director_pos_id && wa_assistant_director.getStaff_id()!=null)
						{
							//not take on account if the sale is assiatant director's.
							if (wa_contract.getDealer()!=wa_assistant_director.getStaff_id()){
								key_long = Long.parseLong(coutry_id_string + matnr_type_string + String.valueOf(premium) + String.valueOf(assistant_director_pos_id) + String.valueOf(wa_assistant_director.getStaff_id()));
								FactTableClass wa_ft = l_fact_table.get(key_long);
								if (wa_ft!=null)
								{
									wa_ft.setCounter(wa_ft.getCounter()+1);
								}
								else if (wa_ft==null)
								{
									FactTableClass wa_ft2= new FactTableClass();
									wa_ft2.setCountry_id(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
									wa_ft2.setMatnr(Long.parseLong(matnr_string));
									wa_ft2.setPosition_id(wa_assistant_director.getPosition_id());
									wa_ft2.setStaff_id(wa_assistant_director.getStaff_id());
									wa_ft2.setBranch_id(wa_assistant_director.getBranch_id());
									wa_ft2.setBukrs(wa_contract.getBukrs());
									wa_ft2.setType(premium);
									wa_ft2.setCounter(1);
									l_fact_table.put(key_long, wa_ft2);
								}
								
								GeneralUtil.checkForNullLong(matnr_type_string, "matnr id is null when calculating bonuses for dealers");
								key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(assistant_director_pos_id));
								Bonus wa_bonus = l_bonus_map.get(key_long);

								
								TempPayroll wa_tp = new TempPayroll();
								wa_tp.setBukrs(wa_contract.getBukrs());					
								wa_tp.setBranch_id(wa_contract.getBranch_id());
								wa_tp.setGjahr(a_gjahr);
								wa_tp.setMonat(a_monat);
								wa_tp.setStaff_id(wa_assistant_director.getStaff_id());
								wa_tp.setPosition_id((long) assistant_director_pos_id);
								wa_tp.setMatnr(Long.parseLong(matnr_string));
								wa_tp.setType(premium);
								wa_tp.setDrcrk("S");
								wa_tp.setMatnr_count(1);
								wa_tp.setContract_number(wa_contract.getContract_number());
								wa_tp.setBranch_name(wa_branch.getText45());
								wa_tp.setBldat(curDate.getTime());
								
								if(wa_bonus==null){
									throw new DAOException("Assitsnet direktora bonus oqiylmagan "+wa_contract.getContract_number());
								}
								//1815110220 1815110220
								if (wa_bonus.getWaers().equals("USD"))
								{
									wa_tp.setAmount(wa_bonus.getCoef()*wa_er.getSc_value());
									wa_tp.setWaers(wa_er.getSecondary_currency());
									wa_tp.setKursf(wa_er.getSc_value());
									wa_tp.setBonus_id(wa_bonus.getBonus_id());
								}
								else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
								{
									wa_tp.setAmount(wa_bonus.getCoef());
									wa_tp.setWaers(wa_bonus.getWaers());
									wa_tp.setBonus_id(wa_bonus.getBonus_id());
									wa_tp.setKursf(wa_er.getSc_value());
								}
								else
								{
									throw new DAOException("Bonus currency not equal to the country currency");
								}
								l_tp.add(wa_tp);
								
							}
							
							
							
						}
						
						
						
						//key = a_bukrs + position_id_string + branch_id_string
						//Trainer premium position_id=12
						key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(trainer_pos_id)+String.valueOf(wa_contract.getBranch_id()));
						wa_trainer = l_pyramid_trainer_map.get(key_long);
						if (wa_trainer!=null && wa_trainer.getPosition_id()==trainer_pos_id && wa_trainer.getStaff_id()!=null)
						{
							key_long = Long.parseLong(coutry_id_string + matnr_type_string + String.valueOf(premium) + String.valueOf(trainer_pos_id) + String.valueOf(wa_trainer.getStaff_id()));
							FactTableClass wa_ft = l_fact_table.get(key_long);
							if (wa_ft!=null)
							{
								wa_ft.setCounter(wa_ft.getCounter()+1);
							}
							else if (wa_ft==null)
							{
								FactTableClass wa_ft2= new FactTableClass();
								wa_ft2.setCountry_id(wa_branch.getCountry_id());//wa_contract.getAddr_dom_countryid()); //change country id
								wa_ft2.setMatnr(Long.parseLong(matnr_string));
								wa_ft2.setPosition_id(wa_trainer.getPosition_id());
								wa_ft2.setStaff_id(wa_trainer.getStaff_id());
								wa_ft2.setBranch_id(wa_trainer.getBranch_id());
								wa_ft2.setBukrs(wa_contract.getBukrs());
								wa_ft2.setType(premium);
								wa_ft2.setCounter(1);
								l_fact_table.put(key_long, wa_ft2);
							}
							
							GeneralUtil.checkForNullLong(matnr_type_string, "matnr id is null when calculating bonuses for dealers");
							key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(trainer_pos_id));
							Bonus wa_bonus = l_bonus_map.get(key_long);

							
							TempPayroll wa_tp = new TempPayroll();
							wa_tp.setBukrs(wa_contract.getBukrs());					
							wa_tp.setBranch_id(wa_contract.getBranch_id());
							wa_tp.setGjahr(a_gjahr);
							wa_tp.setMonat(a_monat);
							wa_tp.setStaff_id(wa_trainer.getStaff_id());
							wa_tp.setPosition_id((long) trainer_pos_id);
							wa_tp.setMatnr(Long.parseLong(matnr_string));
							wa_tp.setType(premium);
							wa_tp.setDrcrk("S");
							wa_tp.setMatnr_count(1);
							wa_tp.setContract_number(wa_contract.getContract_number());
							wa_tp.setBranch_name(wa_branch.getText45());
							wa_tp.setBldat(curDate.getTime());
							//1815110220 1815110220
							if (wa_bonus.getWaers().equals("USD"))
							{
								wa_tp.setAmount(wa_bonus.getCoef()*wa_er.getSc_value());
								wa_tp.setWaers(wa_er.getSecondary_currency());
								wa_tp.setKursf(wa_er.getSc_value());
								wa_tp.setBonus_id(wa_bonus.getBonus_id());
							}
							else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
							{
								wa_tp.setAmount(wa_bonus.getCoef());
								wa_tp.setWaers(wa_bonus.getWaers());
								wa_tp.setBonus_id(wa_bonus.getBonus_id());
								wa_tp.setKursf(wa_er.getSc_value());
							}
							else
							{
								throw new DAOException("Bonus currency not equal to the country currency");
							}
							l_tp.add(wa_tp);
							if (wa_tp_dealer!=null && wa_tp_dealer.getStaff_id()>0)
							{
								wa_tp_dealer.setTrainer_id(wa_trainer.getStaff_id());
							}
							
							
						}
						//key = a_bukrs + position_id_string + Business_area_id)
						//Main trainer premium position_id=15
						ContractType wa_con_type = map_con_type.get(wa_contract.getContract_type_id());
						if (wa_con_type==null)
						{
							throw new DAOException("No such contract_type in DB");
						}
						
						key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(main_trainer_pos_id)+String.valueOf(wa_con_type.getBusiness_area_id()));
						wa_trainer_main = l_pyramid_trainer_main_map.get(key_long);
						//System.out.println(l_pyramid_trainer_main_map.size()+ " size");
						//System.out.println(key_long);
						//System.out.println(wa_trainer_main.getStaff_id());
						if (wa_trainer_main!=null && wa_trainer_main.getPosition_id()==main_trainer_pos_id && wa_trainer_main.getStaff_id()!=null)
						{
							if (coutry_id_string.equals("5")||coutry_id_string.equals("6"))
							{
								key_long = Long.parseLong("1" + matnr_type_string + String.valueOf(premium) + String.valueOf(main_trainer_pos_id) + String.valueOf(wa_trainer_main.getStaff_id()));
							}
							else
							{
								key_long = Long.parseLong(coutry_id_string + matnr_type_string + String.valueOf(premium) + String.valueOf(main_trainer_pos_id) + String.valueOf(wa_trainer_main.getStaff_id()));
							}
							
	//						key_long = Long.parseLong(coutry_id_string + matnr_type_string + String.valueOf(premium) + String.valueOf(main_trainer_pos_id) + String.valueOf(wa_trainer_main.getStaff_id()));
							FactTableClass wa_ft = l_fact_table.get(key_long);
							if (wa_ft!=null)
							{
								wa_ft.setCounter(wa_ft.getCounter()+1);
							}
							else if (wa_ft==null)
							{
								FactTableClass wa_ft2= new FactTableClass();
								if (coutry_id_string.equals("5")||coutry_id_string.equals("6"))
								{
									wa_ft2.setCountry_id(1L);
								}
								else
								{
									wa_ft2.setCountry_id(wa_branch.getCountry_id());
								}
								
								wa_ft2.setMatnr(Long.parseLong(matnr_string));
								wa_ft2.setPosition_id(wa_trainer_main.getPosition_id());
								wa_ft2.setStaff_id(wa_trainer_main.getStaff_id());
								wa_ft2.setBranch_id(wa_trainer_main.getBranch_id());
								wa_ft2.setBukrs(wa_contract.getBukrs());
								wa_ft2.setType(premium);
								wa_ft2.setCounter(1);
								l_fact_table.put(key_long, wa_ft2);
							}
							
							if (wa_tp_dealer!=null && wa_tp_dealer.getStaff_id()>0)
							{
								wa_tp_dealer.setMain_trainer_id(wa_trainer_main.getStaff_id());
							}
							
						}
						
						
					//System.out.println("end");
					}					
				}
		
		
		}
		catch (DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}

	public void collectFactCollector1000(Map<Long, FactTableClass> l_fact_table, List<Contract> l_contracts_collector, 
			Map<Long,Contract> l_contracts_collected_amount_map,Map<Long,Contract> l_closed_contracts_collected_amount_map,String a_bukrs, int bonus)
	{
		try
		{ 
			Long key_long = null;
			for(Contract wa_contract:l_contracts_collector)
			{		
			FactTableClass wa_ft2= new FactTableClass();
			wa_ft2.setPosition_id((long) collector_pos_id);
			wa_ft2.setStaff_id(wa_contract.getCollector());
			wa_ft2.setBukrs(wa_contract.getBukrs());
			wa_ft2.setPlan_amount(wa_contract.getPrice());				
			wa_ft2.setType(bonus);
			//wa_ft2.setCountry_id(wa_contract.getAddr_dom_countryid()); //change country id
			
			Contract wa_contract3 = l_closed_contracts_collected_amount_map.get(wa_contract.getCollector());
			if (wa_contract3!=null && wa_contract3.getCollector()!=null && wa_contract3.getCollector()>0)
			{
				wa_ft2.setPlan_amount(wa_ft2.getPlan_amount() + wa_contract3.getPrice());
			}
			
			Contract wa_contract2 = l_contracts_collected_amount_map.get(wa_contract.getCollector());
			if (wa_contract2!=null && wa_contract2.getCollector()!=null && wa_contract2.getCollector()>0)
			{
				wa_ft2.setFact_amount(wa_contract2.getPaid());
			}
			
			//key_long = Long.parseLong(String.valueOf(wa_contract.getAddr_dom_countryid()) + "0" + String.valueOf(bonus) + String.valueOf(collector_pos_id) + String.valueOf(wa_contract.getCollector()));	
			l_fact_table.put(key_long, wa_ft2);
			
			}	
		}
		catch (DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}

	public void applyBonus1000(List<TempPayroll> l_tp, Map<Long,FactTableClass> l_fact_table, Map<Long,Branch> map_branch, Map<Long,Staff> map_staff, Map<Long,ExchangeRate> map_er, 
			Map<Long,Bonus> l_bonus_map, 
			List<Bonus> l_bonus_collector, int a_gjahr, int a_monat
			,Map<Long,List<Bonus>> l_bonus_manager_map
			){
		try{
			
			Long key_long = null;
			String matnr_type_string = "";
			int loopCount = 0;
			for (Map.Entry<Long, FactTableClass> entry : l_fact_table.entrySet()) {
				loopCount++;
				System.out.println("Loop "+loopCount);
		    	key_long = null;
		        FactTableClass wa_ft = new FactTableClass();
		        wa_ft = (FactTableClass) entry.getValue();
		        if (entry.getKey().equals(812155724L))
		        {
		        	System.out.println(entry.getKey());
		        }
		        
		        
		        //System.out.println(entry.getKey());
		        Calendar curDate =Calendar.getInstance();
		        Calendar firstDay = Calendar.getInstance(); 
				Calendar lastDay = Calendar.getInstance();			  
				firstDay.set(a_gjahr, a_monat-1, 1);
				lastDay.set(a_gjahr, a_monat-1, firstDay.getActualMaximum(Calendar.DAY_OF_MONTH));
		        GeneralUtil.checkForNullLong(wa_ft.getCountry_id(), " No country id when calculating bonuses");
		        ExchangeRate wa_er = map_er.get(wa_ft.getCountry_id());
		        matnr_type_string = "";
		        if (wa_ft.getStaff_id()!=null && wa_ft.getStaff_id()>0)
		        {
		        	
		        	TempPayroll wa_tp = new TempPayroll();
					wa_tp.setBukrs(wa_ft.getBukrs());
					wa_tp.setStaff_id(wa_ft.getStaff_id());
					wa_tp.setBranch_id(wa_ft.getBranch_id());
					wa_tp.setGjahr(a_gjahr);
					wa_tp.setMonat(a_monat);
					wa_tp.setPosition_id(wa_ft.getPosition_id());
					wa_tp.setType(wa_ft.getType());
					wa_tp.setFact_amount(wa_ft.getFact_amount());
					wa_tp.setPlan_amount(wa_ft.getPlan_amount());
					wa_tp.setMatnr(wa_ft.getMatnr());
					wa_tp.setMatnr_count(wa_ft.getCounter());
					wa_tp.setBldat(curDate.getTime());
					wa_tp.setFrom_date(firstDay.getTime());
					wa_tp.setTo_date(lastDay.getTime());
					wa_tp.setDrcrk("S");
					Branch wa_branch = map_branch.get(wa_ft.getBranch_id());
					Staff wa_staff = map_staff.get(wa_ft.getStaff_id());
					if (wa_staff!=null && wa_branch==null)
					{
						wa_branch = map_branch.get(wa_staff.getBranch_id());
					}
					if (wa_staff!=null && wa_branch!=null)
					{	
						wa_tp.setBranch_name(wa_branch.getText45());
						wa_tp.setStaff_name(wa_staff.getLastname()+" "+wa_staff.getFirstname()+" "+wa_staff.getMiddlename());
						if (wa_tp.getCustomer_id()==null || wa_tp.getCustomer_id()==0)
						{
							wa_tp.setCustomer_id(wa_staff.getCustomer_id());
						}
						if (wa_tp.getMatnr().equals(1L)) { matnr_type_string = "4";}//Rob 114F	matnr_string = "1";					
						else if (wa_tp.getMatnr().equals(4L)){ matnr_type_string = "4";}//Rob Splus		
						else if (wa_tp.getMatnr().equals(812L)){  matnr_type_string = "812";}//Ceb Dig Uniqe
						else if (wa_tp.getMatnr().equals(256L)){  matnr_type_string = "812";}//Compact Cebilon-101MC
						else if (wa_tp.getMatnr().equals(3L)) { matnr_type_string = "812";}//Cebilon-S
						else if (wa_tp.getMatnr().equals(2L)) { matnr_type_string = "812";}//Cebilon-101M
						else if (wa_tp.getMatnr().equals(816L)) { matnr_type_string = "816";}//Rexwat ATLAS PREMIER 22
						else if (wa_tp.getMatnr().equals(817L)) {matnr_type_string = "913";}//Rexwat ECO
						else if (wa_tp.getMatnr().equals(910L)) { matnr_type_string = "910";}//Rexwat ATLAS PREMIER 15
						else if (wa_tp.getMatnr().equals(913L)) { matnr_type_string = "913";}//Rexwat ECO RO ReStyle 2016
						
						
						//Premium
						//key = bonus_type_id, country_id, matnr, position_id
						
//						if (wa_ft.getPosition_id()==main_trainer_pos_id)
//						{
//							
//							GeneralUtil.checkForNullLong(wa_ft.getMatnr(), "matnr id is null when calculating bonuses for dealers");
//							key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_ft.getCountry_id())+String.valueOf(wa_tp.getMatnr())+String.valueOf(wa_ft.getPosition_id()));
//							Bonus wa_bonus = l_bonus_map.get(key_long);
//							Branch main_trainer_branch = brnDao.find(wa_ft.getBranch_id());
//							ExchangeRate wa_er2 = map_er.get(main_trainer_branch.getCountry_id());
//							if (wa_bonus!=null && wa_er2!=null)
//							{
//								
//								
//								//1815110220 1815110220
//								if (wa_bonus.getWaers().equals("USD"))
//								{
//									wa_tp.setAmount(wa_tp.getMatnr_count()*wa_bonus.getCoef()*wa_er2.getSc_value());
//									wa_tp.setWaers(wa_er2.getSecondary_currency());
//									wa_tp.setKursf(wa_er2.getSc_value());
//									wa_tp.setBonus_id(wa_bonus.getBonus_id());
//								}
//								else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er2.getSecondary_currency()))
//								{
//									wa_tp.setAmount(wa_tp.getMatnr_count()*wa_bonus.getCoef());
//									wa_tp.setWaers(wa_bonus.getWaers());
//									wa_tp.setBonus_id(wa_bonus.getBonus_id());
//									wa_tp.setKursf(wa_er2.getSc_value());
//								}
//								else
//								{
//									throw new DAOException("Bonus currency not equal to the country currency");
//								}
//								
//								
//								
//								if (wa_tp.getAmount()>0)
//								{
//									l_tp.add(wa_tp);
//								}
//							}
//						}
						
						
						//bonus
						//key = bonus_type_id, country_id, matnr, position_id

						//Bonus
						//key = bonus_type_id, country_id, matnr, position_id
//						if (loopCount==92)
//						{
//							System.out.println(loopCount);
//						}
						
						if (wa_ft.getPosition_id()==manager_pos_id)
						{
							
							
							TempPayroll wa_tp3 = new TempPayroll();
							BeanUtils.copyProperties(wa_tp, wa_tp3);
							wa_tp3.setAmount(0);
							
							GeneralUtil.checkForNullLong(wa_ft.getMatnr(), " No matnr id when calculating bonuses for dealers");
							key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_ft.getBranch_id())+matnr_type_string+String.valueOf(wa_ft.getPosition_id()));
							List<Bonus> wal_bonus = l_bonus_manager_map.get(key_long);
							
							if (wa_tp.getStaff_id().equals(5031L))
							{
								System.out.println(5031L);
							}
							Bonus wa_bonus = null;
							
							
							key_long = Long.parseLong(String.valueOf(wa_ft.getCountry_id())+matnr_type_string+String.valueOf(premium)+String.valueOf(dealer_pos_id)+String.valueOf(wa_ft.getStaff_id()));
							FactTableClass wa_ft_bon = l_fact_table.get(key_long);
							int dealerSales = 0;
							if (wa_ft_bon!=null){
								dealerSales = wa_ft_bon.getCounter();
							}
							
							int groupSales = 0;
							if (wa_ft_bon!=null){
								groupSales = wa_tp.getMatnr_count();
							}
							
//							if (loopCount==92)
//							{
//								System.out.println(loopCount);
//							}
							
							for(Bonus wa:wal_bonus)
							{
								if (wa.getFrom_num()<=groupSales && groupSales<=wa.getTo_num())
								{
									wa_bonus = wa;
									break;
								}
							}
							
							if (wa_bonus==null || dealerSales<wa_bonus.getReq_value())
							{
								for (int i =0; i<l_tp.size();i++)
								{
									TempPayroll wa_tp_roll =  new TempPayroll();
									wa_tp_roll = l_tp.get(i);
//									if (wa_tp_roll.getContract_number()!=null && wa_tp_roll.getContract_number().equals(366929L))
//							        {
//										System.out.println("Delete123");
//							        }
									
									if (wa_tp_roll.getMatnr()!=null && getMatnrBonusGroup(wa_tp_roll.getMatnr()).equals(matnr_type_string))
									{	
										if (wa_tp_roll.getPosition_id()==manager_pos_id && wa_tp_roll.getStaff_id().equals(wa_ft.getStaff_id()))
										{
											if (wa_tp.getStaff_id().equals(5031L))
											{
												System.out.println(5031L+" "+wa_tp_roll.getContract_number());
											}
											l_tp.remove(i);
											i--;
										}
									}
								}
							}
							
//							if (wa_bonus!=null && wa_er!=null)
//							{
//								
//								if (wa_ft_bon==null || dealerSales<wa_bonus.getReq_value()|| wa_tp.getMatnr_count()<wa_bonus.getFrom_num())
//								{
//									if (wa_ft.getStaff_id().equals(11816L))
//							        {
//							        	System.out.println("Delete123");
//							        }
//									
//									
//								}
//							}
							
							
							//System.out.println("manager found");
							
							GeneralUtil.checkForNullLong(matnr_type_string, "matnr id is null when calculating bonuses for dealers");
							key_long = Long.parseLong(String.valueOf(bonus)+String.valueOf(wa_branch.getBranch_id())+matnr_type_string+String.valueOf(manager_pos_id));
							Bonus wa_bonus_man = l_bonus_map.get(key_long);
							
														
							
							if (wa_bonus_man!=null && wa_er!=null)
							{
								if (wa_tp3.getMatnr_count()>=wa_bonus_man.getFrom_num())
								{
							
									if (wa_bonus_man.getWaers().equals("USD"))
									{
										wa_tp3.setAmount((wa_tp3.getMatnr_count()-wa_bonus_man.getFrom_num()+1)*wa_bonus_man.getCoef()*wa_er.getSc_value());
										wa_tp3.setWaers(wa_er.getSecondary_currency());
										wa_tp3.setKursf(wa_er.getSc_value());
										wa_tp3.setBonus_id(wa_bonus_man.getBonus_id());
									}
									else if (!wa_bonus_man.getWaers().equals("USD") && wa_bonus_man.getWaers().equals(wa_er.getSecondary_currency()))
									{
										wa_tp3.setAmount((wa_tp3.getMatnr_count()-wa_bonus_man.getFrom_num()+1)*wa_bonus_man.getCoef());
										wa_tp3.setWaers(wa_bonus_man.getWaers());
										wa_tp3.setBonus_id(wa_bonus_man.getBonus_id());
										wa_tp3.setKursf(wa_er.getSc_value());
									}
									else
									{
										throw new DAOException("Bonus currency not equal to the country currency");
									}
								}
							}
							if (wa_tp3.getAmount()>0)
							{
								l_tp.add(wa_tp3);
							}
							
						}
						
						//Bonus
						//key = bonus_type_id, country_id, matnr, position_id
						if (wa_ft.getPosition_id()==trainer_pos_id || wa_ft.getPosition_id()==director_pos_id)
						{
							TempPayroll wa_tp3 = new TempPayroll();
							BeanUtils.copyProperties(wa_tp, wa_tp3);
							wa_tp3.setAmount(0);
							GeneralUtil.checkForNullLong(wa_ft.getMatnr(), " No matnr id when calculating bonuses for dealers");
							key_long = Long.parseLong(String.valueOf(bonus)+String.valueOf(wa_ft.getBranch_id())+matnr_type_string+String.valueOf(wa_ft.getPosition_id()));
							Bonus wa_bonus = l_bonus_map.get(key_long);
							
							
							if (wa_bonus!=null && wa_er!=null)
							{
								if (wa_tp3.getMatnr_count()>=wa_bonus.getFrom_num())
								{
							
									if (wa_bonus.getWaers().equals("USD"))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef()*wa_er.getSc_value());
										wa_tp3.setWaers(wa_er.getSecondary_currency());
										wa_tp3.setKursf(wa_er.getSc_value());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
									}
									else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef());
										wa_tp3.setWaers(wa_bonus.getWaers());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
										wa_tp3.setKursf(wa_er.getSc_value());
									}
									else
									{
										throw new DAOException("Bonus currency not equal to the country currency");
									}
								}
							}
							if (wa_tp3.getAmount()>0)
							{
								l_tp.add(wa_tp3);
							}
						}
						//bonus
						if (wa_ft.getPosition_id()==main_trainer_pos_id)
						{
							TempPayroll wa_tp3 = new TempPayroll();
							BeanUtils.copyProperties(wa_tp, wa_tp3);
							wa_tp3.setAmount(0);
							GeneralUtil.checkForNullLong(wa_ft.getMatnr(), " No matnr id when calculating bonuses for dealers");
							key_long = Long.parseLong(String.valueOf(bonus)+String.valueOf(wa_ft.getBranch_id())+matnr_type_string+String.valueOf(wa_ft.getPosition_id()));
							Bonus wa_bonus = l_bonus_map.get(key_long);
							Branch main_trainer_branch = brnDao.find(wa_ft.getBranch_id());
							ExchangeRate wa_er2 = map_er.get(main_trainer_branch.getCountry_id());
							
							if (wa_bonus!=null && wa_er2!=null)
							{
								if (wa_tp3.getMatnr_count()>=wa_bonus.getFrom_num())
								{
							
									if (wa_bonus.getWaers().equals("USD"))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef()*wa_er2.getSc_value());
										wa_tp3.setWaers(wa_er2.getSecondary_currency());
										wa_tp3.setKursf(wa_er2.getSc_value());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
									}
									else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er2.getSecondary_currency()))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef());
										wa_tp3.setWaers(wa_bonus.getWaers());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
										wa_tp3.setKursf(wa_er2.getSc_value());
									}
									else
									{
										throw new DAOException("Bonus currency not equal to the country currency");
									}
								}
							}
							if (wa_tp3.getAmount()>0)
							{
								l_tp.add(wa_tp3);
							}
						}
						//premium
						if (wa_ft.getPosition_id()==main_trainer_pos_id)
						{
							TempPayroll wa_tp3 = new TempPayroll();
							BeanUtils.copyProperties(wa_tp, wa_tp3);
							wa_tp3.setAmount(0);
							GeneralUtil.checkForNullLong(wa_ft.getMatnr(), " No matnr id when calculating bonuses for dealers");
							key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_ft.getBranch_id())+matnr_type_string+String.valueOf(wa_ft.getPosition_id()));
							Bonus wa_bonus = l_bonus_map.get(key_long);
							Branch main_trainer_branch = brnDao.find(wa_ft.getBranch_id());
							ExchangeRate wa_er2 = map_er.get(main_trainer_branch.getCountry_id());
							
							if (wa_bonus!=null && wa_er2!=null)
							{
								if (wa_tp3.getMatnr_count()>=wa_bonus.getFrom_num())
								{
							
									if (wa_bonus.getWaers().equals("USD"))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef()*wa_er2.getSc_value());
										wa_tp3.setWaers(wa_er2.getSecondary_currency());
										wa_tp3.setKursf(wa_er2.getSc_value());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
									}
									else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er2.getSecondary_currency()))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef());
										wa_tp3.setWaers(wa_bonus.getWaers());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
										wa_tp3.setKursf(wa_er2.getSc_value());
									}
									else
									{
										throw new DAOException("Bonus currency not equal to the country currency");
									}
								}
							}
							if (wa_tp3.getAmount()>0)
							{
								l_tp.add(wa_tp3);
							}
						}
						//bonus
						if (wa_ft.getPosition_id()==coordinator_pos_id)
						{
							TempPayroll wa_tp3 = new TempPayroll();
							BeanUtils.copyProperties(wa_tp, wa_tp3);
							wa_tp3.setAmount(0);
							GeneralUtil.checkForNullLong(wa_ft.getMatnr(), " No matnr id when calculating bonuses for dealers");
							key_long = Long.parseLong(String.valueOf(bonus)+String.valueOf(wa_ft.getBranch_id())+matnr_type_string+String.valueOf(wa_ft.getPosition_id()));
							Bonus wa_bonus = l_bonus_map.get(key_long);
							Branch coordinator_branch = brnDao.find(wa_ft.getBranch_id());
							ExchangeRate wa_er2 = map_er.get(coordinator_branch.getCountry_id());
							
							if (wa_bonus!=null && wa_er2!=null)
							{
								if (wa_tp3.getMatnr_count()>=wa_bonus.getFrom_num())
								{
							
									if (wa_bonus.getWaers().equals("USD"))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef()*wa_er2.getSc_value());
										wa_tp3.setWaers(wa_er2.getSecondary_currency());
										wa_tp3.setKursf(wa_er2.getSc_value());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
									}
									else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er2.getSecondary_currency()))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef());
										wa_tp3.setWaers(wa_bonus.getWaers());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
										wa_tp3.setKursf(wa_er2.getSc_value());
									}
									else
									{
										throw new DAOException("Bonus currency not equal to the country currency");
									}
								}
							}
							if (wa_tp3.getAmount()>0)
							{
								wa_tp3.setBranch_id(60L);
								l_tp.add(wa_tp3);
							}
						}
						//premium
						if (wa_ft.getPosition_id()==coordinator_pos_id)
						{
							if (wa_ft.getStaff_id().equals(5718L))
							{
								System.out.println("541311432 Manager found 3271 line");
							}
							TempPayroll wa_tp3 = new TempPayroll();
							BeanUtils.copyProperties(wa_tp, wa_tp3);
							wa_tp3.setAmount(0);
							GeneralUtil.checkForNullLong(wa_ft.getMatnr(), " No matnr id when calculating bonuses for dealers");
							key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_ft.getBranch_id())+matnr_type_string+String.valueOf(wa_ft.getPosition_id()));
							Bonus wa_bonus = l_bonus_map.get(key_long);
							Branch coordinator_branch = brnDao.find(wa_ft.getBranch_id());
							ExchangeRate wa_er2 = map_er.get(coordinator_branch.getCountry_id());
							
							if (wa_bonus!=null && wa_er2!=null)
							{
								if (wa_tp3.getMatnr_count()>=wa_bonus.getFrom_num())
								{
							
									if (wa_bonus.getWaers().equals("USD"))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef()*wa_er2.getSc_value());
										wa_tp3.setWaers(wa_er2.getSecondary_currency());
										wa_tp3.setKursf(wa_er2.getSc_value());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
									}
									else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er2.getSecondary_currency()))
									{
										wa_tp3.setAmount(wa_tp3.getMatnr_count()*wa_bonus.getCoef());
										wa_tp3.setWaers(wa_bonus.getWaers());
										wa_tp3.setBonus_id(wa_bonus.getBonus_id());
										wa_tp3.setKursf(wa_er2.getSc_value());
									}
									else
									{
										throw new DAOException("Bonus currency not equal to the country currency");
									}
								}
							}
							if (wa_tp3.getAmount()>0)
							{
								l_tp.add(wa_tp3);
							}
						}
						
					}
		        }

		    }
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	public void applyService1000(List<TempPayroll> l_tp, String a_bukrs, int a_gjahr, int a_monat){
		try{
			Calendar firstDay = Calendar.getInstance(); 
			Calendar lastDay = Calendar.getInstance();			  
			firstDay.set(a_gjahr, a_monat-1, 1);
			lastDay.set(a_gjahr, a_monat-1, firstDay.getActualMaximum(Calendar.DAY_OF_MONTH));
			Calendar curDate = Calendar.getInstance();	
			
			
			
			List<Object[]> results = prlDao.serviceZp(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay));
			for (Object[] result : results) {
//				0 bukrs
//				1 branch_id
//				2 staff_id
//				3 currency
//				4 summa
//				5 customer_id
//				6 fio
//				7 branchText
				TempPayroll wa_tp_service = new TempPayroll();
				wa_tp_service.setBukrs(a_bukrs);			
				wa_tp_service.setGjahr(a_gjahr);
				wa_tp_service.setMonat(a_monat);
				
				if (result[1]!=null) wa_tp_service.setBranch_id(Long.parseLong(String.valueOf(result[1])));
				if (result[2]!=null) wa_tp_service.setStaff_id(Long.parseLong(String.valueOf(result[2])));
				if (result[3]!=null) wa_tp_service.setWaers(String.valueOf(result[3]));
				if (result[4]!=null) wa_tp_service.setAmount(Double.parseDouble(String.valueOf(result[4]))); 
				if (result[5]!=null) wa_tp_service.setCustomer_id(Long.parseLong(String.valueOf(result[5]))); 
				if (result[6]!=null) wa_tp_service.setStaff_name(String.valueOf(result[6]));
				if (result[7]!=null) wa_tp_service.setBranch_name(String.valueOf(result[7]));
				wa_tp_service.setDrcrk("S");
				wa_tp_service.setBldat(curDate.getTime());
				wa_tp_service.setType(bonus);
				
				//wa_tp_service.setPosition_id((long) dealer_pos_id);				
				//wa_tp_deposit.setMatnr(Long.parseLong(matnr_string));
				//wa_tp_deposit.setContract_number(wa_contract.getContract_number());
				//wa_tp_deposit.setMatnr_count(1);
				l_tp.add(wa_tp_service);
				

			}
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}	
	public void applyNachalnik2000(List<TempPayroll> l_tp, String a_bukrs, int a_gjahr, int a_monat){
		try{
			Calendar firstDay = Calendar.getInstance(); 
			Calendar lastDay = Calendar.getInstance();			  
			firstDay.set(a_gjahr, a_monat-1, 1);
			lastDay.set(a_gjahr, a_monat-1, firstDay.getActualMaximum(Calendar.DAY_OF_MONTH));
			Calendar curDate = Calendar.getInstance();	

			Object[] count_result = prlDao.totalContractGreenLight(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay));
			int total = 0;
			int atlas = 0;
			int eco = 0;
			int rainbow = 0;
			
			if (count_result[0]!=null) total= Integer.parseInt(String.valueOf(count_result[0]));
			if (count_result[1]!=null) atlas= Integer.parseInt(String.valueOf(count_result[1]));
			if (count_result[2]!=null) eco= Integer.parseInt(String.valueOf(count_result[2]));
			if (count_result[3]!=null) rainbow= Integer.parseInt(String.valueOf(count_result[3]));
			
			List<Object[]> results = prlDao.nachalnik2000(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay));
			for (Object[] result : results) {
//				0 bukrs
//				1 branch_id
//				2 staff_id
//				3 currency
//				4 summa
//				5 customer_id
//				6 fio
//				7 branchText
				TempPayroll wa_tp_nach = new TempPayroll();
				wa_tp_nach.setBukrs(a_bukrs);			
				wa_tp_nach.setGjahr(a_gjahr);
				wa_tp_nach.setMonat(a_monat);
				
				if (result[1]!=null) wa_tp_nach.setBranch_id(Long.parseLong(String.valueOf(result[1])));
				if (result[2]!=null) wa_tp_nach.setStaff_id(Long.parseLong(String.valueOf(result[2])));
				//if (result[3]!=null) wa_tp_nach.setWaers(String.valueOf(result[3]));
				//if (result[4]!=null) wa_tp_nach.setAmount(Double.parseDouble(String.valueOf(result[4]))); 
				if (result[5]!=null) wa_tp_nach.setCustomer_id(Long.parseLong(String.valueOf(result[5]))); 
				if (result[6]!=null) wa_tp_nach.setStaff_name(String.valueOf(result[6]));
				if (result[7]!=null) wa_tp_nach.setBranch_name(String.valueOf(result[7]));
				if (result[8]!=null) wa_tp_nach.setPosition_id(Long.parseLong(String.valueOf(result[8])));
				//if (result[9]!=null) wa_tp_nach.setText45(String.valueOf(result[9]));
				wa_tp_nach.setDrcrk("S");
				wa_tp_nach.setBldat(curDate.getTime());
				wa_tp_nach.setType(bonus);
				
//				116 Brand manager Rainbow 4300 KZT
//				117 Brand manager Rexwat 2150 KZT
				
				wa_tp_nach.setWaers("KZT");
				
				
				if (wa_tp_nach.getPosition_id().equals(116L)) wa_tp_nach.setAmount(rainbow*4300);
				if (wa_tp_nach.getPosition_id().equals(117L)) wa_tp_nach.setAmount((atlas + eco)*2150);
				if (wa_tp_nach.getPosition_id().equals(45L)) wa_tp_nach.setAmount(total*95);
				
				
//				wa_tp_nach.setAmount(atlas*2000+eco*1000+rainbow*2000);
					
					
				
				//wa_tp_service.setPosition_id((long) dealer_pos_id);				
				//wa_tp_deposit.setMatnr(Long.parseLong(matnr_string));
				//wa_tp_deposit.setContract_number(wa_contract.getContract_number());
				//wa_tp_deposit.setMatnr_count(1);
				l_tp.add(wa_tp_nach);
				

			}
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	public void applyNachalnikAnalytic1000(List<TempPayroll> l_tp, String a_bukrs, int a_gjahr, int a_monat){
		try{
			Calendar firstDay = Calendar.getInstance(); 
			Calendar lastDay = Calendar.getInstance();			  
			firstDay.set(a_gjahr, a_monat-1, 1);
			lastDay.set(a_gjahr, a_monat-1, firstDay.getActualMaximum(Calendar.DAY_OF_MONTH));
			Calendar curDate = Calendar.getInstance();	

			int countProdazhExceptBaku = prlDao.totalContractExceptBaku(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay));
			int countProdazhTotal = prlDao.totalContractTotal(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay));
			
			int countProdazhKazakhstan = prlDao.totalContractKazakhstan(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay));
			int countProdazhRoboclean = prlDao.totalContractRoboclean(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay));
			int countProdazhCebilon = prlDao.totalContractCebilon(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay));
			
			int countProdazhRobocleanExceptBaku = prlDao.totalContractRobocleanExceptBaku(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay));
			int countProdazhCebilonExceptBaku = prlDao.totalContractCebilonExceptBaku(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay));
			
			List<Long> kzkg = new ArrayList<Long>();
			kzkg.add(1L);
			kzkg.add(6L);
			int countProdazhKzKg =  prlDao.totalContractDynamic(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay),kzkg);
			int countProdazhRobKzKg =  prlDao.totalContractRobocleanDynamic(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay),kzkg);
			int countProdazhCebKzKg =  prlDao.totalContractCebilonDynamic(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay),kzkg);
			
			List<Long> uzTj = new ArrayList<Long>();
			uzTj.add(5L);
			uzTj.add(11L);
			int countProdazhUzTj =  prlDao.totalContractDynamic(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay),uzTj);
			
			List<Long> uz = new ArrayList<Long>();
			uz.add(5L);
			int countProdazhUz =  prlDao.totalContractDynamic(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay),uz);

			
			List<Object[]> results = prlDao.nachalnikAndAnalyticZp(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay));
			for (Object[] result : results) {
//				0 bukrs
//				1 branch_id
//				2 staff_id
//				3 currency
//				4 summa
//				5 customer_id
//				6 fio
//				7 branchText
				
				Long country_id = null;
				TempPayroll wa_tp_nach = new TempPayroll();
				wa_tp_nach.setBukrs(a_bukrs);			
				wa_tp_nach.setGjahr(a_gjahr);
				wa_tp_nach.setMonat(a_monat);
				
				if (result[1]!=null) wa_tp_nach.setBranch_id(Long.parseLong(String.valueOf(result[1])));
				if (result[2]!=null) wa_tp_nach.setStaff_id(Long.parseLong(String.valueOf(result[2])));
				//if (result[3]!=null) wa_tp_nach.setWaers(String.valueOf(result[3]));
				//if (result[4]!=null) wa_tp_nach.setAmount(Double.parseDouble(String.valueOf(result[4]))); 
				if (result[5]!=null) wa_tp_nach.setCustomer_id(Long.parseLong(String.valueOf(result[5]))); 
				if (result[6]!=null) wa_tp_nach.setStaff_name(String.valueOf(result[6]));
				if (result[7]!=null) wa_tp_nach.setBranch_name(String.valueOf(result[7]));
				if (result[8]!=null) wa_tp_nach.setPosition_id(Long.parseLong(String.valueOf(result[8])));
				//if (result[9]!=null) wa_tp_nach.setText45(String.valueOf(result[9]));

				if (result[10]!=null) country_id = Long.parseLong(String.valueOf(result[10]));
				wa_tp_nach.setDrcrk("S");
				wa_tp_nach.setBldat(curDate.getTime());
				wa_tp_nach.setType(bonus);
				

//				45 Ст. оператор Call центра -> 125 kzt
				
//				98 Зам. генерального директора и начальник отдела маркетинга Burhan abi Aura 800kzt, GreenLight 2700
				
//				104 Директор по продукту Робоклин Serik abi aura 4300 kzt
//				95 Директор по продукту Себилон Erbolat abi aura 4300 kzt
				
//				99 Обучатель по Робоклин Daniyar abi aura 750
//				97 Обучатель по Себилон Shuhrat abi aura 750
				
				
				//Kazakhstan
				if (country_id.equals(1L)){
					wa_tp_nach.setWaers("KZT");
					
					if (wa_tp_nach.getPosition_id().equals(98L)) wa_tp_nach.setAmount(countProdazhKzKg*800);
					else if (wa_tp_nach.getPosition_id().equals(104L) || wa_tp_nach.getPosition_id().equals(95L)){
						int countSales = prlDao.totalContractsByUserBranch(a_bukrs,GeneralUtil.getSQLDate(firstDay),GeneralUtil.getSQLDate(lastDay),wa_tp_nach.getStaff_id());
						wa_tp_nach.setAmount(countSales*4300);						
					} 
					
					
					
					
					
					
					else if (wa_tp_nach.getPosition_id().equals(45L)) wa_tp_nach.setAmount(countProdazhKazakhstan*125);
					
//					44 Оператор Call центра -> 125 kzt
					else if (wa_tp_nach.getPosition_id().equals(44L))
					{
						wa_tp_nach.setAmount(countProdazhKazakhstan*125);
//						5714
//				        3459
//				        5712
//				        3460	
						
						
						if (wa_tp_nach.getStaff_id().equals(13699L))
						{
							wa_tp_nach.setAmount(countProdazhKazakhstan*125);
						}					
						if (wa_tp_nach.getStaff_id().equals(14354L))
						{
							wa_tp_nach.setAmount(countProdazhKazakhstan*125);
						}
						
						if (wa_tp_nach.getStaff_id().equals(14592L))
						{
							wa_tp_nach.setAmount(countProdazhKazakhstan*125);
						}
						if (wa_tp_nach.getStaff_id().equals(14605L))
						{
							wa_tp_nach.setAmount(countProdazhKazakhstan*125);
						}
						if (wa_tp_nach.getStaff_id().equals(14612L))
						{
							wa_tp_nach.setAmount(countProdazhKazakhstan*95);
						}

						if (wa_tp_nach.getStaff_id().equals(1729L))
						{
							wa_tp_nach.setAmount(countProdazhKazakhstan*125);
						}


						
						if (wa_tp_nach.getStaff_id().equals(14319L))
						{
							wa_tp_nach.setAmount(countProdazhKazakhstan*95);
						}
						if (wa_tp_nach.getStaff_id().equals(16420L))
						{
							wa_tp_nach.setAmount(countProdazhKazakhstan*125);
						}
						if (wa_tp_nach.getStaff_id().equals(4991L))
						{
							wa_tp_nach.setAmount(countProdazhKazakhstan*105);
						}

						if (wa_tp_nach.getStaff_id().equals(17150L))
						{
							wa_tp_nach.setAmount(countProdazhKazakhstan*105);
						}
						if (wa_tp_nach.getStaff_id().equals(19699L))
						{
							wa_tp_nach.setAmount(countProdazhKazakhstan*105);
						}
						if (wa_tp_nach.getStaff_id().equals(20236L))
						{
							wa_tp_nach.setAmount(countProdazhKazakhstan*75);
						}
						if (wa_tp_nach.getStaff_id().equals(19753L))
						{
							wa_tp_nach.setAmount(countProdazhKazakhstan*75);
						}
						 
						
					
//						if ( wa_tp_nach.getStaff_id().equals(283L))
//						{
//							wa_tp_nach.setAmount(0);
//						}
					}
						
						
					
					//wa_tp_service.setPosition_id((long) dealer_pos_id);				
					//wa_tp_deposit.setMatnr(Long.parseLong(matnr_string));
					//wa_tp_deposit.setContract_number(wa_contract.getContract_number());
					//wa_tp_deposit.setMatnr_count(1);
					
					if (wa_tp_nach.getStaff_id().equals(631L))
					{
						continue;
					}
					else if (wa_tp_nach.getStaff_id().equals(16713L))
					{
						continue;
					}
					if (wa_tp_nach.getStaff_id().equals(19366L))
					{
						continue;
					}	
					if (wa_tp_nach.getStaff_id().equals(19878L))
					{
						continue;
					}					
					else
					{					
						l_tp.add(wa_tp_nach);					
					}
				}
				//Uzbekistan
				else if (country_id.equals(5L)){
					if (wa_tp_nach.getStaff_id().equals(18331L))
					{
						wa_tp_nach.setAmount(countProdazhUz*1500);
						wa_tp_nach.setWaers("UZS");
						l_tp.add(wa_tp_nach);
					}
					
				}
				
				

			}
			
			TempPayroll wa_tp_nach2 = new TempPayroll();
			wa_tp_nach2.setBukrs(a_bukrs);			
			wa_tp_nach2.setGjahr(a_gjahr);
			wa_tp_nach2.setMonat(a_monat);
			wa_tp_nach2.setBranch_id(60L);
			wa_tp_nach2.setStaff_id(296L);
			wa_tp_nach2.setCustomer_id(362L);
			wa_tp_nach2.setStaff_name("Карааслан Хакан");
			wa_tp_nach2.setBranch_name("ГЛ.ОФИС-AURA ");
			wa_tp_nach2.setPosition_id(70L);
			wa_tp_nach2.setDrcrk("S");
			wa_tp_nach2.setBldat(curDate.getTime());
			wa_tp_nach2.setType(premium);
			wa_tp_nach2.setWaers("USD");
			wa_tp_nach2.setAmount(countProdazhTotal*5);
			wa_tp_nach2.setText45("Premium from Aura only");
			l_tp.add(wa_tp_nach2);
			
			
			TempPayroll wa_tp_nach25 = new TempPayroll();
			wa_tp_nach25.setBukrs(a_bukrs);			
			wa_tp_nach25.setGjahr(a_gjahr);
			wa_tp_nach25.setMonat(a_monat);
			wa_tp_nach25.setBranch_id(80L);
			wa_tp_nach25.setStaff_id(11370L);
			wa_tp_nach25.setCustomer_id(140368L);
			wa_tp_nach25.setStaff_name("Safaev Kemaleddin");
			wa_tp_nach25.setBranch_name("UZ-Main");
			wa_tp_nach25.setPosition_id(23L);
			wa_tp_nach25.setDrcrk("S");
			wa_tp_nach25.setBldat(curDate.getTime());
			wa_tp_nach25.setType(premium);
			wa_tp_nach25.setWaers("UZS");
			wa_tp_nach25.setAmount(countProdazhUzTj*60000);
			wa_tp_nach25.setText45("Premium from Uz, TJ");
			l_tp.add(wa_tp_nach25);
			
			TempPayroll wa_tp_nach26 = new TempPayroll();
			wa_tp_nach26.setBukrs(a_bukrs);			
			wa_tp_nach26.setGjahr(a_gjahr);
			wa_tp_nach26.setMonat(a_monat);
			wa_tp_nach26.setBranch_id(80L);
			wa_tp_nach26.setStaff_id(2242L);
			wa_tp_nach26.setCustomer_id(131513L);
			wa_tp_nach26.setStaff_name("Mambetov Shuhrat");
			wa_tp_nach26.setBranch_name("UZ-Main");
			wa_tp_nach26.setPosition_id(97L);
			wa_tp_nach26.setDrcrk("S");
			wa_tp_nach26.setBldat(curDate.getTime());
			wa_tp_nach26.setType(premium);
			wa_tp_nach26.setWaers("UZS");
			wa_tp_nach26.setAmount(countProdazhUzTj*20000);
			wa_tp_nach26.setText45("Premium from Uz, TJ Obuchatel ROB CEB");
			l_tp.add(wa_tp_nach26);
				
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	public void marketingKPI(List<TempPayroll> l_tp, String a_bukrs, int a_gjahr, int a_monat,Map<Long, Branch> map_branch,Map<Long, Staff> map_staff,Map<Long,Pyramid> l_pyramid_ass_dir_map ){
		try{
			Calendar firstDay = Calendar.getInstance(); 
			Calendar lastDay = Calendar.getInstance();			  
			firstDay.set(a_gjahr, a_monat-1, 1);
			lastDay.set(a_gjahr, a_monat-1, firstDay.getActualMaximum(Calendar.DAY_OF_MONTH));
			Calendar curDate = Calendar.getInstance();	



			Map<String,Bonus> l_bonus_kpi_map = new HashMap<String,Bonus>();
			List<Bonus> l_bonus_KPI = new ArrayList<Bonus>();
			l_bonus_KPI = bonDao.dynamicFindBonuses(" b.bonus_type_id=14 and b.bukrs='"+a_bukrs+"'");
			for (Bonus wa_bonus : l_bonus_KPI) {
				String key = "br"+wa_bonus.getBranch_id()+"pos"+wa_bonus.getPosition_id();
				l_bonus_kpi_map.put(key, wa_bonus);

			}
			
			List<CrmKpiFinanceDto> crmDto = marketingKpiReportService.getDataForFinance(a_bukrs,a_gjahr,a_monat);
			  
			for (CrmKpiFinanceDto wa_crmDto : crmDto) {
				TempPayroll wa_tp_nach = new TempPayroll();
				wa_tp_nach.setBukrs(a_bukrs);			
				wa_tp_nach.setGjahr(a_gjahr);
				wa_tp_nach.setMonat(a_monat);
				wa_tp_nach.setBldat(curDate.getTime());
				wa_tp_nach.setText45("KPI Marketing");
				
				wa_tp_nach.setBranch_id(wa_crmDto.getBranchId());
				wa_tp_nach.setStaff_id(wa_crmDto.getStaffId());
				wa_tp_nach.setCustomer_id(wa_crmDto.getCustomerId());
				wa_tp_nach.setPosition_id(wa_crmDto.getPositionId());
				wa_tp_nach.setStaff_name(wa_crmDto.getStaffName());
				

				wa_tp_nach.setBranch_name(map_branch.get(wa_tp_nach.getBranch_id()).getText45());
				
				Bonus wa_bonus = new Bonus();
				String key = "br"+wa_tp_nach.getBranch_id()+"pos"+wa_tp_nach.getPosition_id();
				wa_bonus = l_bonus_kpi_map.get(key);
				
				double totAverageScore = wa_crmDto.getTotalAverageScore();
				
				if (wa_bonus!=null && totAverageScore>=wa_bonus.getFrom_num() && totAverageScore<=wa_bonus.getTo_num())
				{
					wa_tp_nach.setType(14);
					wa_tp_nach.setDrcrk("S");
					wa_tp_nach.setWaers(wa_bonus.getWaers());				
					wa_tp_nach.setAmount(totAverageScore*wa_bonus.getCoef());
					l_tp.add(wa_tp_nach);
					
				}
				
				if (wa_crmDto.getPositionId()==Long.parseLong(String.valueOf(director_pos_id)))
				{

					Long key_long = null;
					Pyramid wa_assistant_director = new Pyramid();
					
					key_long = Long.parseLong(a_bukrs+String.valueOf(assistant_director_pos_id)+String.valueOf(wa_crmDto.getBranchId()));
					wa_assistant_director = l_pyramid_ass_dir_map.get(key_long);
					
					if (wa_assistant_director!=null){
						Staff wa_staff = map_staff.get(wa_assistant_director.getStaff_id());
						
						TempPayroll wa_tp_nach2 = new TempPayroll();
						wa_tp_nach2.setBukrs(a_bukrs);			
						wa_tp_nach2.setGjahr(a_gjahr);
						wa_tp_nach2.setMonat(a_monat);
						wa_tp_nach2.setBldat(curDate.getTime());
						wa_tp_nach2.setText45("KPI Marketing");
						
						wa_tp_nach2.setBranch_id(wa_crmDto.getBranchId());
						wa_tp_nach2.setStaff_id(wa_staff.getStaff_id());
						wa_tp_nach2.setCustomer_id(wa_staff.getCustomer_id());
						wa_tp_nach2.setPosition_id(Long.parseLong(String.valueOf(assistant_director_pos_id)));
						wa_tp_nach2.setStaff_name(wa_staff.getFullFIO());
						

						wa_tp_nach2.setBranch_name(map_branch.get(wa_tp_nach2.getBranch_id()).getText45());
						
						
						Bonus wa_bonus2 = new Bonus();
						String key2 = "br"+wa_tp_nach2.getBranch_id()+"pos"+wa_tp_nach2.getPosition_id();
						wa_bonus2 = l_bonus_kpi_map.get(key2);
						
						double totAverageScore2 = wa_crmDto.getTotalAverageScore();
						
						if (wa_bonus2!=null && totAverageScore2>=wa_bonus2.getFrom_num() && totAverageScore2<=wa_bonus2.getTo_num())
						{
							wa_tp_nach2.setType(14);
							wa_tp_nach2.setDrcrk("S");
							wa_tp_nach2.setWaers(wa_bonus2.getWaers());				
							wa_tp_nach2.setAmount(totAverageScore2*wa_bonus2.getCoef());
							l_tp.add(wa_tp_nach2);
							
						}
					}

					
					
					
				}
				
			}
			
			
			
				
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	
	public void marketingTS(List<TempPayroll> l_tp, String a_bukrs, int a_gjahr, int a_monat,Map<Long, Branch> map_branch ){
		try{
			Calendar firstDay = Calendar.getInstance(); 
			Calendar lastDay = Calendar.getInstance();			  
			firstDay.set(a_gjahr, a_monat-1, 1);
			lastDay.set(a_gjahr, a_monat-1, firstDay.getActualMaximum(Calendar.DAY_OF_MONTH));
			Calendar curDate = Calendar.getInstance();	



			Map<String,Bonus> l_bonus_ts_map = new HashMap<String,Bonus>();
			List<Bonus> l_bonus_TS = new ArrayList<Bonus>();
			l_bonus_TS = bonDao.dynamicFindBonuses(" b.bonus_type_id=15 and b.bukrs='"+a_bukrs+"'");
			for (Bonus wa_bonus : l_bonus_TS) {
				String key = "";
				if (wa_bonus.getPosition_id()==null || wa_bonus.getPosition_id().equals(0L)){
					key = "br"+wa_bonus.getBranch_id()+"posNull";	
				}
				else
				{
					key = "br"+wa_bonus.getBranch_id()+"pos"+wa_bonus.getPosition_id();					
				}
				l_bonus_ts_map.put(key, wa_bonus);

			}
			
			List<Object[]> l_obj = prlDao.findMarketingTSStaff(a_bukrs, GeneralUtil.getSQLDate(firstDay), GeneralUtil.getSQLDate(lastDay));
			
			
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			  
			for (Object[] result : l_obj) {
				Long contractBranchId = null;
				TempPayroll wa_tp_nach = new TempPayroll();
				wa_tp_nach.setBukrs(a_bukrs);			
				wa_tp_nach.setGjahr(a_gjahr);
				wa_tp_nach.setMonat(a_monat);
				wa_tp_nach.setBldat(curDate.getTime());
				wa_tp_nach.setText45("TS Marketing");
				
//				int prodaja = 0;
				int minuendMonth =  0;
				int minuendYear = 0;
				int subtrahendMonth =  0;
				int subtrahendYear = 0;
				
				if (result[0]!=null) wa_tp_nach.setBranch_id(Long.parseLong(String.valueOf(result[0])));
				if (result[1]!=null) wa_tp_nach.setStaff_id(Long.parseLong(String.valueOf(result[1])));
				if (result[2]!=null) wa_tp_nach.setCustomer_id(Long.parseLong(String.valueOf(result[2]))); 
				if (result[3]!=null) wa_tp_nach.setPosition_id(Long.parseLong(String.valueOf(result[3])));				
				if (result[4]!=null) wa_tp_nach.setStaff_name(String.valueOf(result[4]));				
				if (result[5]!=null) wa_tp_nach.setContract_number(Long.parseLong(String.valueOf(result[5])));

				if (result[6]!=null) contractBranchId = Long.parseLong(String.valueOf(result[6]));
				if (result[7]!=null){
					Calendar tsDate = Calendar.getInstance();

					minuendMonth =  lastDay.get(Calendar.MONTH);
					minuendYear = lastDay.get(Calendar.YEAR);
					String tsDateString = String.valueOf(result[7]);
					try {
						tsDate.setTime(sdf.parse(tsDateString));
					} catch (ParseException e) {

						throw new DAOException("TS Date not set, reference staff_id "+wa_tp_nach.getStaff_id());
					}// all done
					subtrahendMonth =  tsDate.get(Calendar.MONTH);
					subtrahendYear = tsDate.get(Calendar.YEAR);
				}else
				{
					throw new DAOException("TS Date not set, reference staff_id "+wa_tp_nach.getStaff_id());
				}
				
				
				
//				wa_tp_nach.setBranch_id(wa_crmDto.getBranchId());
//				wa_tp_nach.setStaff_id(wa_crmDto.getStaffId());
//				wa_tp_nach.setCustomer_id(wa_crmDto.getCustomerId());
//				wa_tp_nach.setPosition_id(wa_crmDto.getPositionId());
//				wa_tp_nach.setStaff_name(wa_crmDto.getStaffName());
				

				wa_tp_nach.setBranch_name(map_branch.get(wa_tp_nach.getBranch_id()).getText45());
				wa_tp_nach.setFact_amount(1);
				
				Bonus wa_bonus = new Bonus();
				String key = "br"+contractBranchId+"pos"+wa_tp_nach.getPosition_id();
				wa_bonus = l_bonus_ts_map.get(key);
				
				if (wa_bonus==null){
					key = "br"+contractBranchId+"posNull";
					wa_bonus = l_bonus_ts_map.get(key);
				}
				
				int diff =((minuendYear*12)+minuendMonth) - ((subtrahendYear*12)+subtrahendMonth); 
				if (wa_bonus!=null && diff<=wa_bonus.getTerm_in_month())
				{
					wa_tp_nach.setType(15);
					wa_tp_nach.setDrcrk("S");
					wa_tp_nach.setWaers(wa_bonus.getWaers());				
					wa_tp_nach.setAmount(wa_bonus.getCoef());
					l_tp.add(wa_tp_nach);
					
				}
				
			}
			
			
			
				
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	public void applyFin70000Tenge(List<TempPayroll> l_tp, String a_bukrs, int a_gjahr, int a_monat){
		try{
			Calendar firstDay = Calendar.getInstance(); 
			Calendar lastDay = Calendar.getInstance();			  
			firstDay.set(a_gjahr, a_monat-1, 1);
			lastDay.set(a_gjahr, a_monat-1, firstDay.getActualMaximum(Calendar.DAY_OF_MONTH));
			Calendar curDate = Calendar.getInstance();	

			double plan = 0;
			String waers = "";
			List<Object[]> results = new ArrayList<Object[]>();
			if (a_bukrs.equals("1000"))
			{
				plan =5000000; waers="KZT";
				//results = prlDao.finAgent70000Tenge(a_bukrs,  GeneralUtil.getSQLDate(firstDay), GeneralUtil.getSQLDate(lastDay), waers, plan);
				results = prlDao.finAgent70000Tenge(a_bukrs, a_gjahr, a_monat, waers, plan);
				
				plan =108300000; waers="UZS";
				results.addAll(prlDao.finAgent70000Tenge(a_bukrs, a_gjahr, a_monat, waers, plan));
				
				plan =1130000; waers="KGS";
				results.addAll(prlDao.finAgent70000Tenge(a_bukrs, a_gjahr, a_monat, waers, plan));
				
				plan =25500; waers="AZN";
				results.addAll(prlDao.finAgent70000Tenge(a_bukrs, a_gjahr, a_monat, waers, plan));
			}
			else
			{
				plan =5000000; waers="KZT";
				results = prlDao.finAgent70000Tenge(a_bukrs, a_gjahr, a_monat, waers, plan);
			}

			
			
			for (Object[] result : results) {
//				0 br.branch_id,
//				1 st.staff_id,
//				2 st.customer_id, 
//				3 initcap(st.lastname)||' '|| initcap(st.firstname) fio,
//				4 br.text45,
//				5 p.position_id,
//				6 waers"
//				7 p.text,

				TempPayroll wa_tp_nach = new TempPayroll();
				wa_tp_nach.setBukrs(a_bukrs);			
				wa_tp_nach.setGjahr(a_gjahr);
				wa_tp_nach.setMonat(a_monat);
				wa_tp_nach.setBldat(curDate.getTime());
				
				if (result[0]!=null) wa_tp_nach.setBranch_id(Long.parseLong(String.valueOf(result[0])));
				if (result[1]!=null) wa_tp_nach.setStaff_id(Long.parseLong(String.valueOf(result[1])));
				if (result[2]!=null) wa_tp_nach.setCustomer_id(Long.parseLong(String.valueOf(result[2]))); 
				if (result[3]!=null) wa_tp_nach.setStaff_name(String.valueOf(result[3]));
				if (result[4]!=null) wa_tp_nach.setBranch_name(String.valueOf(result[4]));
				if (result[5]!=null) wa_tp_nach.setPosition_id(Long.parseLong(String.valueOf(result[5])));
				if (result[6]!=null) wa_tp_nach.setWaers(String.valueOf(result[6]));
				
				
				wa_tp_nach.setFrom_date(firstDay.getTime());
				wa_tp_nach.setTo_date(lastDay.getTime());
				
				
				
				
				wa_tp_nach.setDrcrk("S");
				wa_tp_nach.setBldat(curDate.getTime());
				wa_tp_nach.setType(wage);
				wa_tp_nach.setText45("фин агент зарплата");


				
				
				if (wa_tp_nach.getWaers().equals("KZT")) wa_tp_nach.setAmount(70000);
				else if (wa_tp_nach.getWaers().equals("UZS")) wa_tp_nach.setAmount(1516666.67);
				else if (wa_tp_nach.getWaers().equals("KGS")) wa_tp_nach.setAmount(15866.67);
				else if (wa_tp_nach.getWaers().equals("AZN")) wa_tp_nach.setAmount(210);
				l_tp.add(wa_tp_nach);
				

			}
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	
	public void applyFinAgent1000(List<TempPayroll> l_tp, String a_bukrs, int a_gjahr, int a_monat,Map<Long, Branch> map_branch,Map<Long, ExchangeRate> map_er ){
		try{
			Calendar curDate = Calendar.getInstance();	
			
			
			List<Object[]> results = prlDao.finAgentZp(a_bukrs, a_gjahr, a_monat);
			for (Object[] result : results) {
//				6 ras plan
//				7 ras_poluchen
//				8 one_month summa
				double proc = 0;
				double ras_plan = 0;
				double ras_poluchen = 0;
				double one_month_summa = 0;
				double summa = 0;
				TempPayroll wa_tp_fin = new TempPayroll();
				wa_tp_fin.setBukrs(a_bukrs);			
				wa_tp_fin.setGjahr(a_gjahr);
				wa_tp_fin.setMonat(a_monat);
				wa_tp_fin.setBldat(curDate.getTime());
				if (result[1]!=null) wa_tp_fin.setWaers(String.valueOf(result[1]));
				if (result[2]!=null) wa_tp_fin.setStaff_id(Long.parseLong(String.valueOf(result[2])));
				if (result[3]!=null) wa_tp_fin.setStaff_name(String.valueOf(result[3]));
				if (result[4]!=null) wa_tp_fin.setCustomer_id(Long.parseLong(String.valueOf(result[4]))); 
				if (result[5]!=null) wa_tp_fin.setBranch_id(Long.parseLong(String.valueOf(result[5])));
				
				wa_tp_fin.setText45("Fin agent bonus");
				wa_tp_fin.setPosition_id(9L);
				wa_tp_fin.setDrcrk("S");
				wa_tp_fin.setBldat(curDate.getTime());
				wa_tp_fin.setType(bonus);
				
				
				if (result[6]!=null) ras_plan = Double.parseDouble(String.valueOf(result[6]));				
				if (result[7]!=null) ras_poluchen = Double.parseDouble(String.valueOf(result[7]));
				if (result[8]!=null) one_month_summa = Double.parseDouble(String.valueOf(result[8]));
				if (result[9]!=null) wa_tp_fin.setBranch_name(String.valueOf(result[9]));
				if (ras_plan>0) proc = GeneralUtil.round(ras_poluchen/ras_plan*100, 0);
				
//				if (proc>=1 && proc<80) summa = ras_poluchen * 2/100;
//				else if (proc>=80 && proc<90) summa = ras_poluchen * 3/100;
//				else if (proc>=90 && proc<95) summa = ras_poluchen * 4/100;
//				else if (proc>=95 && proc<100) summa = ras_poluchen * 5/100;
//				else if (proc>=100) summa = ras_poluchen * 6/100;

				summa = ras_poluchen * 5/100;
				
				summa = summa + one_month_summa;
				if (summa>0)
				{
					if (wa_tp_fin.getWaers().equals("USD"))
					{
						ExchangeRate wa_er = map_er.get(map_branch.get(wa_tp_fin.getBranch_id()).getCountry_id());
						summa = wa_er.getSc_value()*summa;
						wa_tp_fin.setWaers(wa_er.getSecondary_currency());
					}
					wa_tp_fin.setAmount(summa);
					l_tp.add(wa_tp_fin);
				}
				
				
				

			}
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	
	public void applyFinManager(List<TempPayroll> l_tp, String a_bukrs, int a_gjahr, int a_monat,Map<Long, Branch> map_branch,Map<Long, ExchangeRate> map_er ){
		try{
			Calendar curDate = Calendar.getInstance();	
			
			
			List<Object[]> results = prlDao.finManagerZp(a_bukrs, a_gjahr, a_monat);
			for (Object[] result : results) {
//				6 ras plan
//				7 ras_poluchen
//				8 one_month summa
				double proc = 0;
				double ras_plan = 0;
				double ras_poluchen = 0;
				double one_month_plan = 0;
				double one_month_poluchen = 0;
				double summa = 0;
				Long countryId = 0L;
				TempPayroll wa_tp_fin = new TempPayroll();
				wa_tp_fin.setBukrs(a_bukrs);			
				wa_tp_fin.setGjahr(a_gjahr);
				wa_tp_fin.setMonat(a_monat);
				wa_tp_fin.setBldat(curDate.getTime());
				if (result[1]!=null) wa_tp_fin.setWaers(String.valueOf(result[1]));
				if (result[2]!=null) wa_tp_fin.setStaff_id(Long.parseLong(String.valueOf(result[2])));
				if (result[3]!=null) wa_tp_fin.setStaff_name(String.valueOf(result[3]));
				if (result[4]!=null) wa_tp_fin.setCustomer_id(Long.parseLong(String.valueOf(result[4]))); 
				if (result[5]!=null) wa_tp_fin.setBranch_id(Long.parseLong(String.valueOf(result[5])));
				
				wa_tp_fin.setText45("Fin manager bonus");
				wa_tp_fin.setPosition_id(65L);
				wa_tp_fin.setDrcrk("S");
				wa_tp_fin.setBldat(curDate.getTime());
				wa_tp_fin.setType(bonus);
				
				
				if (result[7]!=null) ras_plan = Double.parseDouble(String.valueOf(result[7]));				
				if (result[8]!=null) ras_poluchen = Double.parseDouble(String.valueOf(result[8]));
				if (result[9]!=null) one_month_plan = Double.parseDouble(String.valueOf(result[9]));
				if (result[10]!=null) one_month_poluchen = Double.parseDouble(String.valueOf(result[10]));
				
				
				if (result[6]!=null) wa_tp_fin.setBranch_name(String.valueOf(result[6]));
				

				if (result[11]!=null) countryId = Long.parseLong(String.valueOf(result[11]));
				
				
//				if (ras_plan>0) proc = GeneralUtil.round(ras_poluchen/ras_plan*100, 0);
				
//				if (proc>=1 && proc<80) summa = ras_poluchen * 2/100;
//				else if (proc>=80 && proc<90) summa = ras_poluchen * 3/100;
//				else if (proc>=90 && proc<95) summa = ras_poluchen * 4/100;
//				else if (proc>=95 && proc<100) summa = ras_poluchen * 5/100;
//				else if (proc>=100) summa = ras_poluchen * 6/100;

				summa = (ras_poluchen + one_month_poluchen) * 1.2/100;
				
				//krome uzbekistana
				if (summa>0 && !countryId.equals(5L))
				{
					if (wa_tp_fin.getWaers().equals("USD"))
					{
						ExchangeRate wa_er = map_er.get(map_branch.get(wa_tp_fin.getBranch_id()).getCountry_id());
						summa = wa_er.getSc_value()*summa;
						wa_tp_fin.setWaers(wa_er.getSecondary_currency());
					}
					wa_tp_fin.setAmount(summa);
					l_tp.add(wa_tp_fin);
				}
				
				
				

			}
		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	
	
	public void createNew(Payroll a_prl,Long a_userId, boolean createFiDoc, String a_tcode, int a_type) throws DAOException
	{
		try
		{
			Long awkey = null;
			Payroll new_prl = new Payroll();
			BeanUtils.copyProperties(a_prl, new_prl);
			
			if(createFiDoc)
			{	
				List<ExchangeRate> l_er = new ArrayList<ExchangeRate>();
				Map<String,ExchangeRate> l_er_map = new HashMap<String,ExchangeRate>();
				l_er = erDao.getLastCurrencyRates();
				Long cus_id=stfDao.find(new_prl.getStaff_id()).getCustomer_id();
				for (ExchangeRate wa_er:l_er)
				{
					if (wa_er.getType()==1)
					{
						l_er_map.put(wa_er.getSecondary_currency(), wa_er);
					}
				}
				
				if (!new_prl.getWaers().equals("USD"))
				{
				   	ExchangeRate wa_er = new ExchangeRate();
				   	wa_er = l_er_map.get(new_prl.getWaers());
				   	if (wa_er==null || wa_er.getSecondary_currency()==null || wa_er.getSecondary_currency().length()==0)
				   	{
				   		throw new DAOException("Курс валют не определен."+" "+new_prl.getWaers());
				   	}
				   	else
				   	{
				   		awkey = financeServicePayroll.insertSalaryFi(new_prl.getBukrs(),a_userId,new_prl.getBranch_id(), new_prl.getWaers(), new_prl.getDmbtr(), wa_er.getSc_value(), cus_id, new_prl.getDrcrk(),null,null,a_tcode,a_type);
				   		new_prl.setAwkey(awkey);
			    	}
				}
				else
				{
					awkey = financeServicePayroll.insertSalaryFi(new_prl.getBukrs(),a_userId,new_prl.getBranch_id(), new_prl.getWaers(), new_prl.getDmbtr(), 1, cus_id, new_prl.getDrcrk(),null,null,a_tcode,a_type);
					new_prl.setAwkey(awkey);
				}
			}
			
			prlDao.create(new_prl);
		}
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	//Odobrenie
	public void saveFaea(List<FaeaOutputTable> rt_list,Long a_userId,String a_tcode) throws DAOException
	{
		try
		{
			
			List<ExchangeRate> l_er = new ArrayList<ExchangeRate>();
			Map<String,ExchangeRate> l_er_map = new HashMap<String,ExchangeRate>();
			l_er = erDao.getLastCurrencyRates();
			
			for (ExchangeRate wa_er:l_er)
			{
				if (wa_er.getType()==1)
				{
					l_er_map.put(wa_er.getSecondary_currency(), wa_er);
				}
			}
			for(FaeaOutputTable rt:rt_list)
			{
				if (rt.getStatus()>0)
				{
					Payroll wa_prl = new Payroll();
					wa_prl = prlDao.find(rt.getPayroll_id());
					wa_prl.setDmbtr(rt.getDmbtr());
					wa_prl.setApproved_by(a_userId);
					Long awkey;
					
					if (rt.getStatus()==1)
					{
						//odobrit
						//3 Avans
						if (wa_prl.getApprove()==3)
						{
							wa_prl.setApprove(4);
						}
						else if (wa_prl.getApprove()==6 || wa_prl.getApprove()==9)
						{
							//6 Nachislenie
							//9 Uderzhanie
							
							
							Long cus_id = stfDao.find(wa_prl.getStaff_id()).getCustomer_id();
							
							if (!wa_prl.getWaers().equals("USD"))
							{
							   	ExchangeRate wa_er = new ExchangeRate();
							   	wa_er = l_er_map.get(wa_prl.getWaers());
							   	if (wa_er==null || wa_er.getSecondary_currency()==null || wa_er.getSecondary_currency().length()==0)
							   	{
							   		throw new DAOException("Курс валют не определен."+" "+wa_prl.getWaers());
							   	}
							   	else
							   	{
							   		int a_type = 0;
							   		if (wa_prl.getApprove()==6) a_type = 6;
							   		else if (wa_prl.getApprove()==9) a_type = 9;
							   		awkey = financeServicePayroll.insertSalaryFi(wa_prl.getBukrs(),a_userId,wa_prl.getBranch_id(), wa_prl.getWaers(), wa_prl.getDmbtr(), wa_er.getSc_value(), cus_id, wa_prl.getDrcrk(),null,null,a_tcode,a_type);
							   		wa_prl.setAwkey(awkey);
							   		wa_prl.setApprove(0);
									prlDao.update(wa_prl);
						    	}
							}
							else
							{
								int a_type = 0;
						   		if (wa_prl.getApprove()==6) a_type = 6;
						   		else if (wa_prl.getApprove()==9) a_type = 9;
								awkey = financeServicePayroll.insertSalaryFi(wa_prl.getBukrs(),a_userId,wa_prl.getBranch_id(), wa_prl.getWaers(), wa_prl.getDmbtr(), 1, cus_id, wa_prl.getDrcrk(),null,null,a_tcode,a_type);
								wa_prl.setAwkey(awkey);
								wa_prl.setApprove(0);
								prlDao.update(wa_prl);
							}
						}
						
						
					}
					else if (rt.getStatus()==2)
					{
						//otklonit
						wa_prl.setApprove(2);
					}
					
					prlDao.update(wa_prl);
				}
			}
			
		}
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	public void saveFosa(List<FosaResultTable> rt_list) throws DAOException
	{
		try
		{
			
			for(FosaResultTable rt:rt_list)
			{
				if (rt.isEditable())
				{
					tpDao.updateDynamicTempPayroll(rt.getApprove(), " waers = '"+rt.getWaers()+"' and staff_id="+rt.getStaff_id()+" and bukrs = "+rt.getBukrs());
				}
				
			}
			
		}
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	/*
	public void addCancelBonus1000(List<Contract> l_contracts, String a_bukrs, int a_gjahr, int a_monat,
			Map<Long,Pyramid> l_pyramid_map, Map<Long,Pyramid> l_pyramid_staff_map, Map<Long,Pyramid> l_pyramid_trainer_map, 
			Map<Long,Pyramid> l_pyramid_trainer_main_map, Map<Long,ContractType> map_con_type,Map<Long, PriceList> map_price_list,
			Map<Long,ExchangeRate> map_er, 
			Map<Long,Bonus> l_bonus_map, List<Bonus> l_bonus_manager, Map<Long,FactTableClass> l_fact_table)
	{
		try
		{
			Map<Long,FactTableClass> l_fact_table_manager_new_cancel_bonus = new HashMap<Long,FactTableClass>();
			
			String matnr_string = "";
			Long key_long = null;
			
			matnr_string = "";
			key_long = null;
			List<CancelBonus> l_cb = new ArrayList<CancelBonus>();
			
			
			//Aura
			for (Contract wa_contract :l_contracts)
			{
				ExchangeRate wa_er = map_er.get(wa_contract.getAddr_dom_countryid());
				
				matnr_string = "";
				CancelBonus wa_cb_dealer = new CancelBonus();
				CancelBonus wa_cb_manager = new CancelBonus();
				CancelBonus wa_cb_director = new CancelBonus();
				CancelBonus wa_cb_coordinator = new CancelBonus();
				CancelBonus wa_cb_trainer = new CancelBonus();
				CancelBonus wa_cb_main_trainer = new CancelBonus();
				
				if (a_bukrs.equals("1000"))
				{
					//Roboclean Premium
					if (wa_contract.getContract_type_id()==1)
					{	
						matnr_string = "1";
					}
					//Cebilon Premium
					else if (wa_contract.getContract_type_id()==2) matnr_string = "2";
					else if (wa_contract.getContract_type_id()==7) matnr_string = "2";
					else if (wa_contract.getContract_type_id()==8) matnr_string = "2";
					else if (wa_contract.getContract_type_id()==9) matnr_string = "2";
					else
					{
						continue;
					}
				}	

				
				
				if (wa_contract.getDealer()!=null)
				{						
					wa_cb_dealer.setGjahr(a_gjahr);
					wa_cb_dealer.setMonat(a_monat);					
					wa_cb_dealer.setStaff_id(wa_contract.getDealer());
					wa_cb_dealer.setPosition_id((long) dealer_pos_id);					
					wa_cb_dealer.setType(0);
					wa_cb_dealer.setContract_id(wa_contract.getContract_id());
					wa_cb_dealer.setContract_number(wa_contract.getContract_number());					
					
					
					GeneralUtil.checkForNullLong(wa_contract.getAddr_dom_countryid(), " No country id when calculating bonuses");
					
					key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_contract.getAddr_dom_countryid())+matnr_string+String.valueOf(dealer_pos_id));
					Bonus wa_bonus = l_bonus_map.get(key_long);
					
					if (wa_bonus!=null && wa_er!=null)
					{
						
						
						//1815110220 1815110220
						if (wa_bonus.getWaers().equals("USD"))
						{
							wa_cb_dealer.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_dealer.setDmbtr(wa_bonus.getCoef()*wa_er.getSc_value());
							wa_cb_dealer.setWaers(wa_er.getSecondary_currency());
							wa_cb_dealer.setKursf(wa_er.getSc_value());
							wa_cb_dealer.setBonus_id(wa_bonus.getBonus_id());
							
							if (wa_contract.getPayment_schedule()>1)
							{
								wa_cb_dealer.setDmbtr(wa_cb_dealer.getDmbtr()-(wa_bonus.getDeposit()*wa_er.getSc_value()));
							}
							
						}
						else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
						{
							wa_cb_dealer.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_dealer.setDmbtr(wa_bonus.getCoef());
							wa_cb_dealer.setWaers(wa_bonus.getWaers());
							wa_cb_dealer.setBonus_id(wa_bonus.getBonus_id());
							wa_cb_dealer.setKursf(wa_er.getSc_value());
							if (wa_contract.getPayment_schedule()>1)
							{
								wa_cb_dealer.setDmbtr(wa_cb_dealer.getDmbtr()-wa_bonus.getDeposit());
							}
						}
						l_cb.add(wa_cb_dealer);
					}
					
					
						
					
				}
				
				
				if (wa_contract.getDealer()!=null)
				{
					//key = a_bukrs + staff_id_string + position_id_string + branch_id_string
					//key = bukrs, pyramid_id
					//Manager premium position_id=3
					//System.out.println("beg "+wa_contract.getDealer()+wa_contract.getContract_number());
					boolean skip_manager = false;
					boolean skip_director = false;
					boolean skip_coordinator = false;
					Pyramid wa_dealer = new Pyramid();
					Pyramid wa_manager = new Pyramid();
					Pyramid wa_director = new Pyramid();
					Pyramid wa_coordinator = new Pyramid();
					Pyramid wa_trainer = new Pyramid();
					Pyramid wa_trainer_main = new Pyramid();
					key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_contract.getDealer())+String.valueOf(dealer_pos_id)+String.valueOf(wa_contract.getBranch_id()));
					wa_dealer = l_pyramid_staff_map.get(key_long);
			
					
					if (wa_dealer==null)
					{
						//check if dealer is manager
						key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_contract.getDealer())+String.valueOf(manager_pos_id)+String.valueOf(wa_contract.getBranch_id()));
						wa_manager = l_pyramid_staff_map.get(key_long);
						skip_manager = true;
						
						if (wa_manager==null)
						{
							//check if if dealer is director
							key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_contract.getDealer())+String.valueOf(director_pos_id)+String.valueOf(wa_contract.getBranch_id()));
							wa_director = l_pyramid_staff_map.get(key_long);
							skip_director = true;
							if (wa_director==null)
							{
								//skip coordinator
								skip_coordinator = true;
							}
						}
							
					}
					
					if (wa_dealer!=null)
					{
						key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_dealer.getParent_pyramid_id()));
						wa_manager = l_pyramid_map.get(key_long);
					}
					
					
					if (skip_manager==false && wa_manager!=null && wa_manager.getPosition_id()==manager_pos_id && wa_manager.getStaff_id()!=null)
					{
						wa_cb_dealer.setManager_id(wa_manager.getStaff_id());
						
						
						//System.out.println("manager found");
						key_long = Long.parseLong(String.valueOf(wa_contract.getAddr_dom_countryid()) + matnr_string + String.valueOf(premium) + String.valueOf(manager_pos_id) + String.valueOf(wa_manager.getStaff_id()));
						FactTableClass wa_ft = l_fact_table_manager_new_cancel_bonus.get(key_long);
						if (wa_ft!=null)
						{
							wa_ft.setCounter(wa_ft.getCounter()+1);
						}
						else if (wa_ft==null)
						{
							FactTableClass wa_ft2= new FactTableClass();
							wa_ft2.setCountry_id(wa_contract.getAddr_dom_countryid()); //change country id
							wa_ft2.setMatnr(Long.parseLong(matnr_string));
							wa_ft2.setPosition_id(wa_manager.getPosition_id());
							wa_ft2.setStaff_id(wa_manager.getStaff_id());
							wa_ft2.setBranch_id(wa_manager.getBranch_id());
							wa_ft2.setBukrs(wa_contract.getBukrs()); 
							wa_ft2.setType(premium);
							wa_ft2.setCounter(1);
							l_fact_table_manager_new_cancel_bonus.put(key_long, wa_ft2);
						}
						FactTableClass wa_ft3 = l_fact_table_manager_new_cancel_bonus.get(key_long);
						
						
						GeneralUtil.checkForNullLong(wa_ft3.getMatnr(), " No matnr id when calculating bonuses for dealers");
						key_long = Long.parseLong(String.valueOf(wa_ft3.getCountry_id())+String.valueOf(wa_ft3.getMatnr())+
								String.valueOf(premium)+String.valueOf(dealer_pos_id)+String.valueOf(wa_ft3.getStaff_id()));
						FactTableClass wa_ft_bon = l_fact_table.get(key_long);
						wa_cb_manager.setGjahr(a_gjahr);
						wa_cb_manager.setMonat(a_monat);					
						wa_cb_manager.setStaff_id(wa_manager.getStaff_id());
						wa_cb_manager.setPosition_id((long) manager_pos_id);					
						wa_cb_manager.setType(0);
						wa_cb_manager.setContract_id(wa_contract.getContract_id());
						wa_cb_manager.setContract_number(wa_contract.getContract_number());	
						
						for (Bonus wa_bonus:l_bonus_manager)
						{
						
							
							if (wa_ft_bon.getCounter()>=wa_bonus.getReq_value() && wa_ft.getMatnr().equals(wa_bonus.getMatnr()))
							{
								if (wa_ft3.getCounter()>=wa_bonus.getFrom_num() && wa_ft3.getCounter()<=wa_bonus.getTo_num())
								{
																		
									if (wa_bonus.getWaers().equals("USD"))
									{
										wa_cb_manager.setDmbtr(wa_bonus.getCoef()*wa_er.getSc_value());
										wa_cb_manager.setWaers(wa_er.getSecondary_currency());
										wa_cb_manager.setKursf(wa_er.getSc_value());
										wa_cb_manager.setBonus_id(wa_bonus.getBonus_id());
										wa_cb_dealer.setBonus_type_id(wa_bonus.getBonus_type_id());
										l_cb.add(wa_cb_manager);
										break;
									}
									else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
									{
										wa_cb_manager.setDmbtr(wa_bonus.getCoef());
										wa_cb_manager.setWaers(wa_bonus.getWaers());
										wa_cb_manager.setBonus_id(wa_bonus.getBonus_id());
										wa_cb_manager.setKursf(wa_er.getSc_value());
										wa_cb_dealer.setBonus_type_id(wa_bonus.getBonus_type_id());
										l_cb.add(wa_cb_manager);
										break;
									}
									else
									{
										throw new DAOException("Bonus currency not equal to the country currency");
									}
									
								}
							}
							
							
							
						}
						
						
						
					}
					
					//key = bukrs, pyramid_id
					//Director premium position_id=10
					if (wa_manager!=null)
					{
						key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_manager.getParent_pyramid_id()));
						wa_director = l_pyramid_map.get(key_long);
					}
					
					//System.out.println(wa_manager.getParent_pyramid_id());
					if (skip_director==false && wa_director!=null && wa_director.getPosition_id()==director_pos_id && wa_director.getStaff_id()!=null)
					{
						wa_cb_dealer.setDirector_id(wa_director.getStaff_id());
						
						
						GeneralUtil.checkForNullLong(matnr_string, "matnr id is null when calculating bonuses for dealers");
						key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_contract.getAddr_dom_countryid())+String.valueOf(matnr_string)+String.valueOf(director_pos_id));
						Bonus wa_bonus = l_bonus_map.get(key_long);
						wa_cb_director.setGjahr(a_gjahr);
						wa_cb_director.setMonat(a_monat);					
						wa_cb_director.setStaff_id(wa_director.getStaff_id());
						wa_cb_director.setPosition_id((long) director_pos_id);					
						wa_cb_director.setType(0);
						wa_cb_director.setContract_id(wa_contract.getContract_id());
						wa_cb_director.setContract_number(wa_contract.getContract_number());	
						if (wa_bonus.getWaers().equals("USD"))
						{
							wa_cb_director.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_director.setDmbtr(wa_bonus.getCoef()*wa_er.getSc_value());
							wa_cb_director.setWaers(wa_er.getSecondary_currency());
							wa_cb_director.setKursf(wa_er.getSc_value());
							wa_cb_director.setBonus_id(wa_bonus.getBonus_id());
							
						}
						else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
						{
							wa_cb_director.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_director.setDmbtr(wa_bonus.getCoef());
							wa_cb_director.setWaers(wa_bonus.getWaers());
							wa_cb_director.setBonus_id(wa_bonus.getBonus_id());
							wa_cb_director.setKursf(wa_er.getSc_value());
						}
						l_cb.add(wa_cb_director);
						
					}
					//key = bukrs, pyramid_id
					//Coordinator premium position_id=5
					if (wa_director!=null)
					{
						key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_director.getParent_pyramid_id()));
						wa_coordinator = l_pyramid_map.get(key_long);
					}
					
					if (skip_coordinator==false && wa_coordinator!=null && wa_coordinator.getPosition_id()==coordinator_pos_id && wa_coordinator.getStaff_id()!=null)
					{
						
						wa_cb_dealer.setCoordinator_id(wa_coordinator.getStaff_id());
						Branch coordinator_branch = brnDao.find(wa_coordinator.getBranch_id());
						ExchangeRate wa_er2 = map_er.get(coordinator_branch.getCountry_id());
						
						GeneralUtil.checkForNullLong(matnr_string, "matnr id is null when calculating bonuses for dealers");
						key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_contract.getAddr_dom_countryid())+String.valueOf(matnr_string)+String.valueOf(coordinator_pos_id));
						Bonus wa_bonus = l_bonus_map.get(key_long);
						wa_cb_coordinator.setGjahr(a_gjahr);
						wa_cb_coordinator.setMonat(a_monat);					
						wa_cb_coordinator.setStaff_id(wa_coordinator.getStaff_id());
						wa_cb_coordinator.setPosition_id((long) coordinator_pos_id);					
						wa_cb_coordinator.setType(0);
						wa_cb_coordinator.setContract_id(wa_contract.getContract_id());
						wa_cb_coordinator.setContract_number(wa_contract.getContract_number());	
						if (wa_bonus.getWaers().equals("USD"))
						{
							wa_cb_coordinator.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_coordinator.setDmbtr(wa_bonus.getCoef()*wa_er2.getSc_value());
							wa_cb_coordinator.setWaers(wa_er2.getSecondary_currency());
							wa_cb_coordinator.setKursf(wa_er2.getSc_value());
							wa_cb_coordinator.setBonus_id(wa_bonus.getBonus_id());
							
						}
						else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er2.getSecondary_currency()))
						{
							wa_cb_coordinator.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_coordinator.setDmbtr(wa_bonus.getCoef());
							wa_cb_coordinator.setWaers(wa_bonus.getWaers());
							wa_cb_coordinator.setBonus_id(wa_bonus.getBonus_id());
							wa_cb_coordinator.setKursf(wa_er2.getSc_value());
						}
						l_cb.add(wa_cb_coordinator);
						
					}
					
					//key = a_bukrs + position_id_string + branch_id_string
					//Trainer premium position_id=12
					key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(trainer_pos_id)+String.valueOf(wa_contract.getBranch_id()));
					wa_trainer = l_pyramid_trainer_map.get(key_long);
					if (wa_trainer!=null && wa_trainer.getPosition_id()==trainer_pos_id && wa_trainer.getStaff_id()!=null)
					{
						wa_cb_dealer.setTrainer_id(wa_trainer.getStaff_id());
						
						
						GeneralUtil.checkForNullLong(matnr_string, "matnr id is null when calculating bonuses for dealers");
						key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_contract.getAddr_dom_countryid())+String.valueOf(matnr_string)+String.valueOf(trainer_pos_id));
						Bonus wa_bonus = l_bonus_map.get(key_long);
						wa_cb_trainer.setGjahr(a_gjahr);
						wa_cb_trainer.setMonat(a_monat);					
						wa_cb_trainer.setStaff_id(wa_trainer.getStaff_id());
						wa_cb_trainer.setPosition_id((long) trainer_pos_id);					
						wa_cb_trainer.setType(0);
						wa_cb_trainer.setContract_id(wa_contract.getContract_id());
						wa_cb_trainer.setContract_number(wa_contract.getContract_number());	
						if (wa_bonus.getWaers().equals("USD"))
						{
							wa_cb_trainer.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_trainer.setDmbtr(wa_bonus.getCoef()*wa_er.getSc_value());
							wa_cb_trainer.setWaers(wa_er.getSecondary_currency());
							wa_cb_trainer.setKursf(wa_er.getSc_value());
							wa_cb_trainer.setBonus_id(wa_bonus.getBonus_id());
							
						}
						else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
						{
							wa_cb_trainer.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_trainer.setDmbtr(wa_bonus.getCoef());
							wa_cb_trainer.setWaers(wa_bonus.getWaers());
							wa_cb_trainer.setBonus_id(wa_bonus.getBonus_id());
							wa_cb_trainer.setKursf(wa_er.getSc_value());
						}
						l_cb.add(wa_cb_trainer);
						
					}
					//key = a_bukrs + position_id_string + Business_area_id)
					//Main trainer premium position_id=15
					ContractType wa_con_type = map_con_type.get(wa_contract.getContract_type_id());
					if (wa_con_type==null)
					{
						throw new DAOException("No such contract_type in DB");
					}
					
					key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(main_trainer_pos_id)+String.valueOf(wa_con_type.getBusiness_area_id()));
					wa_trainer_main = l_pyramid_trainer_main_map.get(key_long);
					//System.out.println(l_pyramid_trainer_main_map.size()+ " size");
					//System.out.println(key_long);
					//System.out.println(wa_trainer_main.getStaff_id());
					if (wa_trainer_main!=null && wa_trainer_main.getPosition_id()==main_trainer_pos_id && wa_trainer_main.getStaff_id()!=null)
					{
						wa_cb_dealer.setMain_trainer_id(wa_trainer_main.getStaff_id());
						Branch main_trainer_branch = brnDao.find(wa_trainer_main.getBranch_id());
						ExchangeRate wa_er2 = map_er.get(main_trainer_branch.getCountry_id());
						
						GeneralUtil.checkForNullLong(matnr_string, "matnr id is null when calculating bonuses for dealers");
						key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_contract.getAddr_dom_countryid())+String.valueOf(matnr_string)+String.valueOf(main_trainer_pos_id));
						Bonus wa_bonus = l_bonus_map.get(key_long);
						wa_cb_main_trainer.setGjahr(a_gjahr);
						wa_cb_main_trainer.setMonat(a_monat);					
						wa_cb_main_trainer.setStaff_id(wa_trainer_main.getStaff_id());
						wa_cb_main_trainer.setPosition_id((long) main_trainer_pos_id);					
						wa_cb_main_trainer.setType(0);
						wa_cb_main_trainer.setContract_id(wa_contract.getContract_id());
						wa_cb_main_trainer.setContract_number(wa_contract.getContract_number());	
						if (wa_bonus.getWaers().equals("USD"))
						{
							wa_cb_main_trainer.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_main_trainer.setDmbtr(wa_bonus.getCoef()*wa_er2.getSc_value());
							wa_cb_main_trainer.setWaers(wa_er2.getSecondary_currency());
							wa_cb_main_trainer.setKursf(wa_er2.getSc_value());
							wa_cb_main_trainer.setBonus_id(wa_bonus.getBonus_id());
							
						}
						else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er2.getSecondary_currency()))
						{
							wa_cb_main_trainer.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_main_trainer.setDmbtr(wa_bonus.getCoef());
							wa_cb_main_trainer.setWaers(wa_bonus.getWaers());
							wa_cb_main_trainer.setBonus_id(wa_bonus.getBonus_id());
							wa_cb_main_trainer.setKursf(wa_er2.getSc_value());
						}
						l_cb.add(wa_cb_main_trainer);
						
					}
					
					
				//System.out.println("end");
				}					
			}
			
			for(CancelBonus wa_cb:l_cb)
			{
				wa_cb.setBukrs(a_bukrs);
				cbDao.create(wa_cb);
			}

		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
	public void addCancelBonus2000(List<Contract> l_contracts, String a_bukrs, int a_gjahr, int a_monat,
			Map<Long,Pyramid> l_pyramid_map, Map<Long,Pyramid> l_pyramid_staff_map, Map<Long,Pyramid> l_pyramid_trainer_map, 
			Map<Long,Pyramid> l_pyramid_trainer_main_map, Map<Long,ContractType> map_con_type,Map<Long, PriceList> map_price_list,
			Map<Long,ExchangeRate> map_er, 
			Map<Long,Bonus> l_bonus_map,  Map<Long,FactTableClass> l_fact_table)
	{
		try
		{
			
			String matnr_string = "";
			Long key_long = null;
			
			matnr_string = "";
			key_long = null;
			List<CancelBonus> l_cb = new ArrayList<CancelBonus>();
			
			
			//Aura
			for (Contract wa_contract :l_contracts)
			{
				ExchangeRate wa_er = map_er.get(wa_contract.getAddr_dom_countryid());
				
				matnr_string = "";
				CancelBonus wa_cb_dealer = new CancelBonus();
				CancelBonus wa_cb_manager = new CancelBonus();
				CancelBonus wa_cb_director = new CancelBonus();
				CancelBonus wa_cb_coordinator = new CancelBonus();
				CancelBonus wa_cb_trainer = new CancelBonus();
				CancelBonus wa_cb_main_trainer = new CancelBonus();
				

				if (a_bukrs.equals("2000"))
				{
					//Rainbow Premium
					if (wa_contract.getContract_type_id()==3)
					{	
						matnr_string = "815";
					}
					//Rexwat Atlas Premium
					else if (wa_contract.getContract_type_id()==4)
					{
						matnr_string = "816";					
						
					}					
					//Rexwat Atlas Premium
					else if (wa_contract.getContract_type_id()==5)
					{
						matnr_string = "817";					
						
					}
					else
					{
						continue;
					}
				}
				
				
				if (wa_contract.getDealer()!=null)
				{			
					wa_cb_dealer.setGjahr(a_gjahr);
					wa_cb_dealer.setMonat(a_monat);					
					wa_cb_dealer.setStaff_id(wa_contract.getDealer());
					wa_cb_dealer.setPosition_id((long) dealer_pos_id);					
					wa_cb_dealer.setType(0);
					wa_cb_dealer.setContract_id(wa_contract.getContract_id());
					wa_cb_dealer.setContract_number(wa_contract.getContract_number());					
					
					
					GeneralUtil.checkForNullLong(wa_contract.getAddr_dom_countryid(), " No country id when calculating bonuses");					
					key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_contract.getAddr_dom_countryid())+matnr_string+String.valueOf(dealer_pos_id));
					Bonus wa_bonus = l_bonus_map.get(key_long);
					
					if (wa_bonus!=null && wa_er!=null)
					{
						
						
						//1815110220 1815110220
						if (wa_bonus.getWaers().equals("USD"))
						{
							wa_cb_dealer.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_dealer.setDmbtr(wa_bonus.getCoef()*wa_er.getSc_value());
							wa_cb_dealer.setWaers(wa_er.getSecondary_currency());
							wa_cb_dealer.setKursf(wa_er.getSc_value());
							wa_cb_dealer.setBonus_id(wa_bonus.getBonus_id());
							
							if (wa_contract.getPayment_schedule()>1)
							{
								wa_cb_dealer.setDmbtr(wa_cb_dealer.getDmbtr()-(wa_bonus.getDeposit()*wa_er.getSc_value()));
							}
							
						}
						else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
						{
							wa_cb_dealer.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_dealer.setDmbtr(wa_bonus.getCoef());
							wa_cb_dealer.setWaers(wa_bonus.getWaers());
							wa_cb_dealer.setBonus_id(wa_bonus.getBonus_id());
							wa_cb_dealer.setKursf(wa_er.getSc_value());
							if (wa_contract.getPayment_schedule()>1)
							{
								wa_cb_dealer.setDmbtr(wa_cb_dealer.getDmbtr()-wa_bonus.getDeposit());
							}
						}
						l_cb.add(wa_cb_dealer);
					}
					
					
						
					
				}
				
				
				if (wa_contract.getDealer()!=null)
				{
					//key = a_bukrs + staff_id_string + position_id_string + branch_id_string
					//key = bukrs, pyramid_id
					//Manager premium position_id=3
					//System.out.println("beg "+wa_contract.getDealer()+wa_contract.getContract_number());
					boolean skip_manager = false;
					boolean skip_director = false;
					boolean skip_coordinator = false;
					Pyramid wa_dealer = new Pyramid();
					Pyramid wa_manager = new Pyramid();
					Pyramid wa_director = new Pyramid();
					Pyramid wa_coordinator = new Pyramid();
					Pyramid wa_trainer = new Pyramid();
					Pyramid wa_trainer_main = new Pyramid();
					key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_contract.getDealer())+String.valueOf(dealer_pos_id)+String.valueOf(wa_contract.getBranch_id()));
					wa_dealer = l_pyramid_staff_map.get(key_long);
			
					
					if (wa_dealer==null)
					{
						//check if dealer is manager
						key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_contract.getDealer())+String.valueOf(manager_pos_id)+String.valueOf(wa_contract.getBranch_id()));
						wa_manager = l_pyramid_staff_map.get(key_long);
						skip_manager = true;
						
						if (wa_manager==null)
						{
							//check if if dealer is director
							key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_contract.getDealer())+String.valueOf(director_pos_id)+String.valueOf(wa_contract.getBranch_id()));
							wa_director = l_pyramid_staff_map.get(key_long);
							skip_director = true;
							if (wa_director==null)
							{
								//skip coordinator
								skip_coordinator = true;
							}
						}
							
					}
					
					if (wa_dealer!=null)
					{
						key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_dealer.getParent_pyramid_id()));
						wa_manager = l_pyramid_map.get(key_long);
					}
					
					
					if (skip_manager==false && wa_manager!=null && wa_manager.getPosition_id()==manager_pos_id && wa_manager.getStaff_id()!=null)
					{
						wa_cb_dealer.setManager_id(wa_manager.getStaff_id());
						

						
						
						GeneralUtil.checkForNullLong(wa_contract.getAddr_dom_countryid(), " No country id when calculating bonuses");
						key_long = Long.parseLong(String.valueOf(bonus)+String.valueOf(wa_contract.getAddr_dom_countryid())+matnr_string+String.valueOf(manager_pos_id));
						Bonus wa_bonus = l_bonus_map.get(key_long);

						
						key_long = Long.parseLong(String.valueOf(wa_contract.getAddr_dom_countryid())+matnr_string+
								String.valueOf(premium)+String.valueOf(dealer_pos_id)+String.valueOf(wa_manager.getStaff_id()));
						FactTableClass wa_ft_bon = l_fact_table.get(key_long);
						wa_cb_manager.setGjahr(a_gjahr);
						wa_cb_manager.setMonat(a_monat);					
						wa_cb_manager.setStaff_id(wa_manager.getStaff_id());
						wa_cb_manager.setPosition_id((long) manager_pos_id);					
						wa_cb_manager.setType(0);
						wa_cb_manager.setContract_id(wa_contract.getContract_id());
						wa_cb_manager.setContract_number(wa_contract.getContract_number());	

						if (wa_bonus!=null && wa_er!=null && wa_ft_bon!=null)
						{
							//System.out.println("Manager "+ wa_manager.getStaff_id()+" Dealer "+wa_dealer.getStaff_id());
							
							if (wa_ft_bon.getCounter()>=wa_bonus.getFrom_num())
							{
								if (wa_bonus.getWaers().equals("USD"))
								{
									wa_cb_manager.setDmbtr(wa_bonus.getCoef()*wa_er.getSc_value());
									wa_cb_manager.setWaers(wa_er.getSecondary_currency());
									wa_cb_manager.setKursf(wa_er.getSc_value());
									wa_cb_manager.setBonus_id(wa_bonus.getBonus_id());
									wa_cb_dealer.setBonus_type_id(wa_bonus.getBonus_type_id());
									l_cb.add(wa_cb_manager);
									break;
								}
								else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
								{
									wa_cb_manager.setDmbtr(wa_bonus.getCoef());
									wa_cb_manager.setWaers(wa_bonus.getWaers());
									wa_cb_manager.setBonus_id(wa_bonus.getBonus_id());
									wa_cb_manager.setKursf(wa_er.getSc_value());
									wa_cb_dealer.setBonus_type_id(wa_bonus.getBonus_type_id());
									l_cb.add(wa_cb_manager);
									break;
								}
								else
								{
									throw new DAOException("Bonus currency not equal to the country currency");
								}
							}
							
						}
						
						
												
						
						
					}
					
					//key = bukrs, pyramid_id
					//Director premium position_id=10
					if (wa_manager!=null)
					{
						key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_manager.getParent_pyramid_id()));
						wa_director = l_pyramid_map.get(key_long);
					}
					
					//System.out.println(wa_manager.getParent_pyramid_id());
					if (skip_director==false && wa_director!=null && wa_director.getPosition_id()==director_pos_id && wa_director.getStaff_id()!=null)
					{
						wa_cb_dealer.setDirector_id(wa_director.getStaff_id());
						
						
						GeneralUtil.checkForNullLong(matnr_string, "matnr id is null when calculating bonuses for dealers");
						key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_contract.getAddr_dom_countryid())+String.valueOf(matnr_string)+String.valueOf(director_pos_id));
						Bonus wa_bonus = l_bonus_map.get(key_long);
						wa_cb_director.setGjahr(a_gjahr);
						wa_cb_director.setMonat(a_monat);					
						wa_cb_director.setStaff_id(wa_director.getStaff_id());
						wa_cb_director.setPosition_id((long) director_pos_id);					
						wa_cb_director.setType(0);
						wa_cb_director.setContract_id(wa_contract.getContract_id());
						wa_cb_director.setContract_number(wa_contract.getContract_number());	
						if (wa_bonus.getWaers().equals("USD"))
						{
							wa_cb_director.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_director.setDmbtr(wa_bonus.getCoef()*wa_er.getSc_value());
							wa_cb_director.setWaers(wa_er.getSecondary_currency());
							wa_cb_director.setKursf(wa_er.getSc_value());
							wa_cb_director.setBonus_id(wa_bonus.getBonus_id());
							
						}
						else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
						{
							wa_cb_director.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_director.setDmbtr(wa_bonus.getCoef());
							wa_cb_director.setWaers(wa_bonus.getWaers());
							wa_cb_director.setBonus_id(wa_bonus.getBonus_id());
							wa_cb_director.setKursf(wa_er.getSc_value());
						}
						l_cb.add(wa_cb_director);
						
					}
					//key = bukrs, pyramid_id
					//Coordinator premium position_id=5
					if (wa_director!=null)
					{
						key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(wa_director.getParent_pyramid_id()));
						wa_coordinator = l_pyramid_map.get(key_long);
					}
					
					if (skip_coordinator==false && wa_coordinator!=null && wa_coordinator.getPosition_id()==coordinator_pos_id && wa_coordinator.getStaff_id()!=null)
					{
						
						wa_cb_dealer.setCoordinator_id(wa_coordinator.getStaff_id());
						Branch coordinator_branch = brnDao.find(wa_coordinator.getBranch_id());
						ExchangeRate wa_er2 = map_er.get(coordinator_branch.getCountry_id());
						
						GeneralUtil.checkForNullLong(matnr_string, "matnr id is null when calculating bonuses for dealers");
						key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_contract.getAddr_dom_countryid())+String.valueOf(matnr_string)+String.valueOf(coordinator_pos_id));
						Bonus wa_bonus = l_bonus_map.get(key_long);
						wa_cb_coordinator.setGjahr(a_gjahr);
						wa_cb_coordinator.setMonat(a_monat);					
						wa_cb_coordinator.setStaff_id(wa_coordinator.getStaff_id());
						wa_cb_coordinator.setPosition_id((long) coordinator_pos_id);					
						wa_cb_coordinator.setType(0);
						wa_cb_coordinator.setContract_id(wa_contract.getContract_id());
						wa_cb_coordinator.setContract_number(wa_contract.getContract_number());	
						if (wa_bonus.getWaers().equals("USD"))
						{
							wa_cb_coordinator.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_coordinator.setDmbtr(wa_bonus.getCoef()*wa_er2.getSc_value());
							wa_cb_coordinator.setWaers(wa_er2.getSecondary_currency());
							wa_cb_coordinator.setKursf(wa_er2.getSc_value());
							wa_cb_coordinator.setBonus_id(wa_bonus.getBonus_id());
							
						}
						else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er2.getSecondary_currency()))
						{
							wa_cb_coordinator.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_coordinator.setDmbtr(wa_bonus.getCoef());
							wa_cb_coordinator.setWaers(wa_bonus.getWaers());
							wa_cb_coordinator.setBonus_id(wa_bonus.getBonus_id());
							wa_cb_coordinator.setKursf(wa_er2.getSc_value());
						}
						l_cb.add(wa_cb_coordinator);
						
					}
					
					//key = a_bukrs + position_id_string + branch_id_string
					//Trainer premium position_id=12
					key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(trainer_pos_id)+String.valueOf(wa_contract.getBranch_id()));
					wa_trainer = l_pyramid_trainer_map.get(key_long);
					if (wa_trainer!=null && wa_trainer.getPosition_id()==trainer_pos_id && wa_trainer.getStaff_id()!=null)
					{
						wa_cb_dealer.setTrainer_id(wa_trainer.getStaff_id());
						
						
						GeneralUtil.checkForNullLong(matnr_string, "matnr id is null when calculating bonuses for dealers");
						key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_contract.getAddr_dom_countryid())+String.valueOf(matnr_string)+String.valueOf(trainer_pos_id));
						Bonus wa_bonus = l_bonus_map.get(key_long);
						wa_cb_trainer.setGjahr(a_gjahr);
						wa_cb_trainer.setMonat(a_monat);					
						wa_cb_trainer.setStaff_id(wa_trainer.getStaff_id());
						wa_cb_trainer.setPosition_id((long) trainer_pos_id);					
						wa_cb_trainer.setType(0);
						wa_cb_trainer.setContract_id(wa_contract.getContract_id());
						wa_cb_trainer.setContract_number(wa_contract.getContract_number());	
						if (wa_bonus.getWaers().equals("USD"))
						{
							wa_cb_trainer.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_trainer.setDmbtr(wa_bonus.getCoef()*wa_er.getSc_value());
							wa_cb_trainer.setWaers(wa_er.getSecondary_currency());
							wa_cb_trainer.setKursf(wa_er.getSc_value());
							wa_cb_trainer.setBonus_id(wa_bonus.getBonus_id());
							
						}
						else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er.getSecondary_currency()))
						{
							wa_cb_trainer.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_trainer.setDmbtr(wa_bonus.getCoef());
							wa_cb_trainer.setWaers(wa_bonus.getWaers());
							wa_cb_trainer.setBonus_id(wa_bonus.getBonus_id());
							wa_cb_trainer.setKursf(wa_er.getSc_value());
						}
						l_cb.add(wa_cb_trainer);
						
					}
					//key = a_bukrs + position_id_string + Business_area_id)
					//Main trainer premium position_id=15
					ContractType wa_con_type = map_con_type.get(wa_contract.getContract_type_id());
					if (wa_con_type==null)
					{
						throw new DAOException("No such contract_type in DB");
					}
					
					key_long = Long.parseLong(wa_contract.getBukrs()+String.valueOf(main_trainer_pos_id)+String.valueOf(wa_con_type.getBusiness_area_id()));
					wa_trainer_main = l_pyramid_trainer_main_map.get(key_long);
					//System.out.println(l_pyramid_trainer_main_map.size()+ " size");
					//System.out.println(key_long);
					//System.out.println(wa_trainer_main.getStaff_id());
					if (wa_trainer_main!=null && wa_trainer_main.getPosition_id()==main_trainer_pos_id && wa_trainer_main.getStaff_id()!=null)
					{
						wa_cb_dealer.setMain_trainer_id(wa_trainer_main.getStaff_id());
						Branch main_trainer_branch = brnDao.find(wa_trainer_main.getBranch_id());
						ExchangeRate wa_er2 = map_er.get(main_trainer_branch.getCountry_id());
						
						GeneralUtil.checkForNullLong(matnr_string, "matnr id is null when calculating bonuses for dealers");
						key_long = Long.parseLong(String.valueOf(premium)+String.valueOf(wa_contract.getAddr_dom_countryid())+String.valueOf(matnr_string)+String.valueOf(main_trainer_pos_id));
						Bonus wa_bonus = l_bonus_map.get(key_long);
						wa_cb_main_trainer.setGjahr(a_gjahr);
						wa_cb_main_trainer.setMonat(a_monat);					
						wa_cb_main_trainer.setStaff_id(wa_trainer_main.getStaff_id());
						wa_cb_main_trainer.setPosition_id((long) main_trainer_pos_id);					
						wa_cb_main_trainer.setType(0);
						wa_cb_main_trainer.setContract_id(wa_contract.getContract_id());
						wa_cb_main_trainer.setContract_number(wa_contract.getContract_number());	
						if (wa_bonus.getWaers().equals("USD"))
						{
							wa_cb_main_trainer.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_main_trainer.setDmbtr(wa_bonus.getCoef()*wa_er2.getSc_value());
							wa_cb_main_trainer.setWaers(wa_er2.getSecondary_currency());
							wa_cb_main_trainer.setKursf(wa_er2.getSc_value());
							wa_cb_main_trainer.setBonus_id(wa_bonus.getBonus_id());
							
						}
						else if (!wa_bonus.getWaers().equals("USD") && wa_bonus.getWaers().equals(wa_er2.getSecondary_currency()))
						{
							wa_cb_main_trainer.setBonus_type_id(wa_bonus.getBonus_type_id());
							wa_cb_main_trainer.setDmbtr(wa_bonus.getCoef());
							wa_cb_main_trainer.setWaers(wa_bonus.getWaers());
							wa_cb_main_trainer.setBonus_id(wa_bonus.getBonus_id());
							wa_cb_main_trainer.setKursf(wa_er2.getSc_value());
						}
						l_cb.add(wa_cb_main_trainer);
						
					}
					
					
				//System.out.println("end");
				}					
			}
			
			for(CancelBonus wa_cb:l_cb)
			{
				wa_cb.setBukrs(a_bukrs);
				cbDao.create(wa_cb);
			}

		}
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		}
	}
*/
	
	public String getMatnrBonusGroup(Long a_matnr)
	{
		if (a_matnr.equals(1L)) { return "4";}//Rob 114F	matnr_string = "1";					
		else if (a_matnr.equals(4L)){ return "4";}//Rob Splus	
		
		else if (a_matnr.equals(815L)) { return "815";}//rainbow
		else if (a_matnr.equals(2930L)){ return "815";}//rainbow srx
		
		else if (a_matnr.equals(812L)){  return "812";}//Ceb Dig Uniqe
		else if (a_matnr.equals(256L)){  return "812";}//Compact Cebilon-101MC
		else if (a_matnr.equals(3L)) { return "812";}//Cebilon-S
		else if (a_matnr.equals(2L)) { return "812";}//Cebilon-101M
		
		else if (a_matnr.equals(816L)) { return "816";}//Rexwat ATLAS PREMIER 22
		else if (a_matnr.equals(910L)) { return "816";}//Rexwat ATLAS PREMIER 15

		else if (a_matnr.equals(817L)) { return "913";}//Rexwat ECO
		else if (a_matnr.equals(913L)) { return "913";}//Rexwat ECO RO ReStyle 2016
		return "";
	}
	public String getMatnrBonusGroup2000(String a_matnr)
	{
		//Rainbow Premium
		if (a_matnr.equals("815")) { return "815";}//rainbow
		else if (a_matnr.equals("2930")){ return "815";}//rainbow srx
		
		else if (a_matnr.equals("816")){ return "816";}//Rexwat Atlas Premium 22
		else if (a_matnr.equals("910")){ return "816";}//Rexwat Atlas Premium 15
		
		else if (a_matnr.equals("817")){ return "913";}//Rexwat Eco 
		else if (a_matnr.equals("913")){ return "913";}//Rexwat Eco RO ReStyle 2016
		else return "";
		
	}
	public String getMatnrBonusGroup2000(Long a_matnr)
	{
		//Rainbow Premium
		if (a_matnr.equals(815L)) { return "815";}//rainbow
		else if (a_matnr.equals(2930L)){ return "815";}//rainbow srx
		
		else if (a_matnr.equals(816L)){ return "816";}//Rexwat Atlas Premium 22
		else if (a_matnr.equals(910L)){ return "816";}//Rexwat Atlas Premium 15	
		
		else if (a_matnr.equals(817L)){ return "913";}//Rexwat Eco 
		else if (a_matnr.equals(913L)){ return "913";}//Rexwat Eco RO ReStyle 2016
		else return "";
		
	}
}
