package dms.migrate;

import f4.MatnrF4;
import f4.ServCRMActionF4;
import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.ContractDao;
import general.dao.DAOException;
import general.dao.StaffDao;
import general.services.PriceListService;
import general.services.ServFilterService;
import general.tables.Contract;
import general.tables.Matnr;
import general.tables.PriceList;
import general.tables.ServCRMAction;
import general.tables.ServCRMHistory;
import general.tables.Staff;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "migrateBean", eager = true)
@ViewScoped
public class MigPrice {

	public void migratePriceCSV() {
		// String csvFile = "/updfiles/pricekg.csv";
		String csvFile = "/updfiles/priceuz.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";|;\\s*|\\s;\\s*";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			int j = 0;

			PriceListService plServ = (PriceListService) appContext
					.getContext().getBean("priceListService");
			List<PriceList> plL = new ArrayList<PriceList>();
			p_matnrF4Bean.init();

			while ((line = br.readLine()) != null) {
				// use semicolon as separator
				String[] price = line.split(cvsSplitBy);
				// System.out.println(line);
				// for (int i = 0;i<price.length; i++) {
				// System.out.println(i + " - " + price[i]);
				// }
				// System.out.println("Price [code= " + price[0] + " , name=" +
				// price[1] + " , price=" + price[2] + " " + price[3] + "]");
				// System.out.println(price[3]);
				if (price.length == 4) {
					Matnr m = p_matnrF4Bean.getC_matnr_map().get(
							price[0].toString());
					if (m != null && !Validation.isEmptyLong(m.getMatnr())) {
						j++;
						// System.out.println(j + " - Ok");
						PriceList pl = new PriceList();
						pl.setActive(1L);
						pl.setBukrs("1000");
						pl.setCountry_id(6L);
						String string = "2016-10-01";
						DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
						Date date = format.parse(string);
						pl.setFrom_date(date);

						pl.setMatnr(m.getMatnr());
						pl.setMatnrObject(m);
						pl.setMonth(0);
						pl.setMonth_type(0L);
						String p = price[2];
						p = p.trim();
						p = p.replace(" ", "");
						pl.setPrice(Double.parseDouble(p));
						pl.setWaers(price[3]);
						pl.setPrem_div(1L);
						plL.add(pl);
					} else
						System.out.println("Not found: " + price[0]);
				}
			}
			System.out.println("Creating " + plL.size() + " new prices!");
			plServ.createPriceList(plL);
			System.out.println("Success!");
			GeneralUtil.addSuccessMessage("New prices successfully migrated!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
			GeneralUtil.addErrorMessage("Parse Exception! " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void migrateCRMHistoryCSV() {
		// String csvFile = "/updfiles/pricekg.csv";
		String csvFile = "/updfiles/crmhis.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";|;\\s*|\\s;\\s*";
						
		Map<String, Long> ma = new HashMap<String, Long>();
		ma.put("ЗАМЕНА_ФИЛЬТРОВ", 1L);
		ma.put("СЕРВИС", 3L);
		ma.put("ЗВОНОК", 10L);
		ma.put("НАЗНАЧЕН_ЗВОНОК", 11L);
		ma.put("СМЕНА_КАТЕГОРИИ", 14L);		
		
		try {
			br = new BufferedReader(new FileReader(csvFile));
			int j = 0;

			ServFilterService sfServ = (ServFilterService) appContext
					.getContext().getBean("servFilterService");
			
			StaffDao sDao = (StaffDao) appContext.getContext().getBean("staffDao");
			ContractDao conDao = (ContractDao) appContext.getContext().getBean("contractDao"); 

			while ((line = br.readLine()) != null) {
				// use semicolon as separator
				String[] inSc = line.split(cvsSplitBy);
//				System.out.println("CRM Log: [Data=" + inSc[1] 
//						+ " ,   TOV-SN=" + inSc[2]
//						+ " ,   ACTION = " + inSc[3]
//						+ " ,   SOTR_ID " + inSc[4]  
//						+ " ,   INFO=" + inSc[5] + "]");
				if (inSc.length == 6) {
					Long a = ma.get(inSc[3]);
					if (a != null && !Validation.isEmptyLong(a)) {
						// System.out.println(j + " - Ok");
						ServCRMHistory sch = new ServCRMHistory();
						
						DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						//DateFormat format = new SimpleDateFormat("dd.mm.yyyy hh:MM:ss");
						Date date = format.parse(inSc[1]);
						sch.setActionDate(date);						
						
						sch.setActionId(a);
						String info = inSc[5];
						if (info.length() > 127) {
							//System.out.println("Large String: " + info);
							info = info.substring(0, 127);
						}
						sch.setInfo(info);
						sch.setTovarSN(inSc[2]);						
						
						if (inSc[4].length() > 0) {
							String con = " old_id = " + Long.parseLong(inSc[4]);
							List<Staff> s = sDao.findAll(con);
							if (s.size() > 0) {
								sch.setStaffId(s.get(0).getStaff_id());
							}
						}
						
						Contract c = conDao.findByTovarSN(inSc[2]);
						if (c != null && !Validation.isEmptyLong(c.getContract_id())) {
							j++;
							sch.setContractId(c.getContract_id());
							sfServ.createNewSCH(sch, userData, 93L);
						} else System.out.println("Contract with TOV_SN: '" + inSc[2] + "' not found!");
					} else
						System.out.println("Not found: " + inSc[3]);
				}
			}
			System.out.println("Success!");
			System.out.println("Migated " + j + " CRM Logs!");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
			GeneralUtil.addErrorMessage("Parse Exception! " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
			e.printStackTrace();
		} finally {
			if (br != null) {
				System.out.println("Final on: " + line);
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// ***************************Application Context********************
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	// ***************************MatnrF4*****************************************************
	@ManagedProperty(value = "#{matnrF4Bean}")
	private MatnrF4 p_matnrF4Bean;

	public MatnrF4 getP_matnrF4Bean() {
		return p_matnrF4Bean;
	}

	public void setP_matnrF4Bean(MatnrF4 p_matnrF4Bean) {
		this.p_matnrF4Bean = p_matnrF4Bean;
	}
	
	// ***************************User session***************************
	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}
}
