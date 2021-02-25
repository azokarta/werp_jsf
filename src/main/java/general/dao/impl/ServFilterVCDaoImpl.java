package general.dao.impl;

import general.GeneralUtil;
import general.Validation;
import general.dao.ServFilterVCDao;
import general.output.tables.ServFilterOutput;
import general.tables.Contract;
import general.tables.ContractStatus;
import general.tables.ServFilter;
import general.tables.ServFilterVC;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("servFilterVCDao")
public class ServFilterVCDaoImpl extends GenericDaoImpl<ServFilterVC> implements ServFilterVCDao {

	@Override
	public List<ServFilterOutput> findAllBySN(String in_bukrs,
			Long in_con_number, String in_tovSN) {
		List<ServFilterOutput> sfoL = new ArrayList<ServFilterOutput>();

		SimpleDateFormat format1 = new SimpleDateFormat("YYYY-MM-dd");
		Calendar cal = Calendar.getInstance();
		java.sql.Date d = new java.sql.Date(GeneralUtil.getFirstDateOfMonth(cal.getTime()).getTime());
		System.out.println("INPUTS ARE:   Bukrs: " + in_bukrs
				+ "   ConNumber: " + in_con_number + "     TovarSN: "
				+ in_tovSN);
		String s2 = "SELECT"
				+ " br.text45," // 0
				+ " ct.contract_number," // 1
				+ " ct.tovar_serial," // 2
				+ " ct.contract_date," // 3
				+ " (cs.name || ' ' || cs.lastname || ' ' || cs.firstname || ' ' || cs.middlename) as cusFIO," // 4
				+ " (COALESCE(st.lastname,'') || ' ' || COALESCE(st.firstname,'')) as staffLF," // 5
				+ " (select cr.regname from cityreg cr, address ad1 where ad1.addr_id = ct.addr_service_id and ad1.regid = cr.idcityreg) as rayon," // 6
				+ " (select ad.address from address ad where ad.addr_id = ct.addr_service_id) as adres," // 7
				+ " (select ad.tel_dom from address ad where ad.addr_id = ct.addr_service_id) as tel," // 8
				+ " (select ad.tel_mob1 from address ad where ad.addr_id = ct.addr_service_id) as mob1," // 9
				+ " (select ad.tel_mob2 from address ad where ad.addr_id = ct.addr_service_id) as mob2," // 10
				+ " (((EXTRACT(YEAR FROM DATE '" + d + "')-EXTRACT(YEAR FROM sf.f1_date_next))*12+EXTRACT(MONTH FROM DATE '"
				+ d + "'))-EXTRACT(MONTH FROM sf.f1_date_next)) as F1," // 11
				+ " sf.id," // 12
				+ " ct.contract_id," // 13
				+ " ct.old_sn," // 14
				+ " ct.customer_id," // 15
				+ " (select ad.tel_dom from address ad where ad.addr_id = ct.addr_work_id) as telRab," // 16
				+ " (select ad.tel_mob1 from address ad where ad.addr_id = ct.addr_work_id) as mobRab1," // 17
				+ " (select ad.tel_mob2 from address ad where ad.addr_id = ct.addr_work_id) as mobRab2," // 18
				+ " sf.crm_category" // 19
				+ " FROM"
				+ " serv_filter_vc sf"
				+ " left join contract ct on ct.contract_id = sf.contract_id"
				+ " left join branch br on br.branch_id = ct.serv_branch_id"
				+ " left join customer cs on cs.customer_id = ct.customer_id"
				+ " left join staff st on st.staff_id = ct.dealer"
				+ " where"
				+ " sf.active = 1";

		if (!Validation.isEmptyLong(in_con_number)) {
			String q2 = "select c from Contract c where c.contract_number = "
					+ in_con_number;
			Query query = this.em.createNativeQuery(q2);
			List<Contract> cL = query.getResultList();
			if (cL.size() > 0) {
				Contract con = cL.get(0);
				s2 += " and sf.contract_id = " + con.getContract_id();
			}
		}

		if (!Validation.isEmptyString(in_tovSN)) {
			s2 += " and sf.tovar_sn = '" + in_tovSN + "'";
		}

		s2 += " ORDER BY ct.contract_number ASC";

		Query query = this.em.createNativeQuery(s2);
		// System.out.println("QUERY:   " + s2);

		List<Object[]> results = query.getResultList();
		int i = 1;
		System.out.println("OK");
		for (Object[] result : results) {
			ServFilterOutput sfo = new ServFilterOutput(i);

			sfo.setActive((byte) 1);
			sfo.setBukrs(in_bukrs);

			if (result[0] != null)
				sfo.setBranch((String) result[0]);
			if (result[1] != null)
				sfo.setConNumber(Long.parseLong(String.valueOf(result[1])));
			if (result[2] != null)
				sfo.setTovSerial((String) result[2]);
			if (result[3] != null)
				sfo.setConDate((Date) result[3]);
			if (result[4] != null)
				sfo.setCustomerFIO((String) result[4]);
			if (result[5] != null)
				sfo.setDealerFIO((String) result[5]);
			if (result[6] != null)
				sfo.setRayon((String) result[6]);
			if (result[7] != null)
				sfo.setAddrService((String) result[7]);
			if (result[8] != null)
				sfo.setTelDom((String) result[8]);
			if (result[9] != null)
				sfo.setMob1((String) result[9]);
			if (result[10] != null)
				sfo.setMob2((String) result[10]);
			if (result[11] != null)
				sfo.setF1(Integer.parseInt(String.valueOf(result[11])));
			
			if (result[12] != null)
				sfo.setId(Long.parseLong(String.valueOf(result[12])));
			if (result[13] != null)
				sfo.setContract_id(Long.parseLong(String.valueOf(result[13])));
			if (result[14] != null)
				sfo.setOldSN(Long.parseLong(String.valueOf(result[14])));
			if (result[15] != null)
				sfo.setCustomerID(Long.parseLong(String.valueOf(result[15])));
			if (result[16] != null) {
				String resTel = (String) result[16];
				String telRab = "";
				if (!Validation.isEmptyString(telRab) && telRab.length() > 0)
					telRab += ", ";
				telRab += resTel;
				sfo.setTelRab(telRab);
			}
			if (result[17] != null) {
				String resTel = (String) result[17];
				String telRab = sfo.getTelRab();
				if (!Validation.isEmptyString(telRab) && telRab.length() > 0)
					telRab += ", ";
				telRab += resTel;
				sfo.setTelRab(telRab);
			}
			if (result[18] != null) {
				String resTel = (String) result[18];
				String telRab = sfo.getTelRab();
				if (!Validation.isEmptyString(telRab) && telRab.length() > 0)
					telRab += ", ";
				telRab += resTel;
				sfo.setTelRab(telRab);
			}
			if (result[19] != null)
				sfo.setCrm_category(Integer.parseInt(String.valueOf(result[19])));
			sfoL.add(sfo);
			i++;
		}
		System.out.println("Total found: " + sfoL.size());
		return sfoL;
	}

	@Override
	public List<ServFilterOutput> findAllByBranch(String in_bukrs,
			Long in_branch, int in_cat) {
		List<ServFilterOutput> sfoL = new ArrayList<ServFilterOutput>();

		SimpleDateFormat format1 = new SimpleDateFormat("YYYY-MM-dd");
		Calendar cal = Calendar.getInstance();
		java.sql.Date d = new java.sql.Date(GeneralUtil.getFirstDateOfMonth(cal.getTime()).getTime());
		System.out.println("INPUTS ARE:   Bukrs: " + in_bukrs + "   Category: " + in_cat);

		String s2 = "SELECT"
				+ " br.text45," // 0
				+ " ct.contract_number," // 1
				+ " ct.tovar_serial," // 2
				+ " ct.contract_date," // 2
				+ " (cs.name || ' ' || cs.lastname || ' ' || cs.firstname || ' ' || cs.middlename) as cusFIO," // 4
				+ " (COALESCE(st.lastname,'') || ' ' || COALESCE(st.firstname,'')) as staffLF," // 5
				+ " (select cr.regname from cityreg cr, address ad1 where ad1.addr_id = ct.addr_service_id and ad1.regid = cr.idcityreg) as rayon," // 6
				+ " (select ad.address from address ad where ad.addr_id = ct.addr_service_id) as adres," // 7
				+ " (select ad.tel_dom from address ad where ad.addr_id = ct.addr_service_id) as tel," // 8
				+ " (select ad.tel_mob1 from address ad where ad.addr_id = ct.addr_service_id) as mob1," // 9
				+ " (select ad.tel_mob2 from address ad where ad.addr_id = ct.addr_service_id) as mob2," // 10
				+ " (((EXTRACT(YEAR FROM DATE '" + d + "')-EXTRACT(YEAR FROM sf.f1_date_next))*12+EXTRACT(MONTH FROM DATE '"
				+ d + "'))-EXTRACT(MONTH FROM sf.f1_date_next)) as F1," // 11
				+ " sf.id," // 12
				+ " ct.contract_id," // 13
				+ " ct.old_sn," // 14
				+ " ct.customer_id," // 15
				+ " (select ad.tel_dom from address ad where ad.addr_id = ct.addr_work_id) as telRab," // 16
				+ " (select ad.tel_mob1 from address ad where ad.addr_id = ct.addr_work_id) as mobRab1," // 17
				+ " (select ad.tel_mob2 from address ad where ad.addr_id = ct.addr_work_id) as mobRab2," // 18
				+ " sf.crm_category" // 19
				+ " FROM"
				+ " serv_filter_vc sf"
				+ " left join contract ct on ct.contract_id = sf.contract_id"
				+ " left join branch br on br.branch_id = ct.serv_branch_id"
				+ " left join customer cs on cs.customer_id = ct.customer_id"
				+ " left join staff st on st.staff_id = ct.dealer"
				+ " where"
				+ " ct.serv_branch_id = " + in_branch + " and sf.active = 1";
		if (in_cat > 0)
			s2 += " and sf.crm_category = " + in_cat;
		s2 += " ORDER BY ct.contract_number ASC";

		Query query = this.em.createNativeQuery(s2);
//		 System.out.println("QUERY:   " + s2);

		List<Object[]> results = query.getResultList();
		int i = 1;
		System.out.println("OK");
		for (Object[] result : results) {
			ServFilterOutput sfo = new ServFilterOutput(i);

			sfo.setActive((byte) 1);
			sfo.setBukrs(in_bukrs);

			if (result[0] != null)
				sfo.setBranch((String) result[0]);
			if (result[1] != null)
				sfo.setConNumber(Long.parseLong(String.valueOf(result[1])));
			if (result[2] != null)
				sfo.setTovSerial((String) result[2]);
			if (result[3] != null)
				sfo.setConDate((Date) result[3]);
			if (result[4] != null)
				sfo.setCustomerFIO((String) result[4]);
			if (result[5] != null)
				sfo.setDealerFIO((String) result[5]);
			if (result[6] != null)
				sfo.setRayon((String) result[6]);
			if (result[7] != null)
				sfo.setAddrService((String) result[7]);
			if (result[8] != null)
				sfo.setTelDom((String) result[8]);
			if (result[9] != null)
				sfo.setMob1((String) result[9]);
			if (result[10] != null)
				sfo.setMob2((String) result[10]);
			if (result[11] != null)
				sfo.setF1(Integer.parseInt(String.valueOf(result[11])));
			if (result[12] != null)
				sfo.setId(Long.parseLong(String.valueOf(result[12])));
			if (result[13] != null)
				sfo.setContract_id(Long.parseLong(String.valueOf(result[13])));
			if (result[14] != null)
				sfo.setOldSN(Long.parseLong(String.valueOf(result[14])));
			if (result[15] != null)
				sfo.setCustomerID(Long.parseLong(String.valueOf(result[15])));
			if (result[16] != null) {
				String resTel = (String) result[16];
				String telRab = "";
				if (!Validation.isEmptyString(telRab) && telRab.length() > 0)
					telRab += ", ";
				telRab += resTel;
				sfo.setTelRab(telRab);
			}
			if (result[17] != null) {
				String resTel = (String) result[17];
				String telRab = sfo.getTelRab();
				if (!Validation.isEmptyString(telRab) && telRab.length() > 0)
					telRab += ", ";
				telRab += resTel;
				sfo.setTelRab(telRab);
			}
			if (result[18] != null) {
				String resTel = (String) result[18];
				String telRab = sfo.getTelRab();
				if (!Validation.isEmptyString(telRab) && telRab.length() > 0)
					telRab += ", ";
				telRab += resTel;
				sfo.setTelRab(telRab);
			}
			if (result[19] != null)
				sfo.setCrm_category(Integer.parseInt(String.valueOf(result[19])));
			sfoL.add(sfo);
			i++;
		}
		System.out.println("Total found: " + sfoL.size());
		return sfoL;
	}

	@Override
	public List<ServFilterOutput> findAllCurrentByDate(String in_bukrs,
			Long in_branch, Date in_date, int warType) {
		List<ServFilterOutput> sfoL = new ArrayList<ServFilterOutput>();

		SimpleDateFormat format1 = new SimpleDateFormat("YYYY-MM-dd");
		String df = format1.format(GeneralUtil.getFirstDateOfMonth(in_date));
		Date d = in_date;
		String dl = format1.format(GeneralUtil.getLastDateOfMonth(in_date));

		System.out.println("INPUTS ARE:   Bukrs: " + in_bukrs + "     Branch: "
				+ in_branch + "     Date: " + format1.format(in_date));
		System.out.println("DF: " + df + "     DL: " + dl);

		String warComparison = " and (((EXTRACT(YEAR FROM DATE '" + df + "')-EXTRACT(YEAR FROM ct.matnr_release_date))*12"
				+ "+EXTRACT(MONTH FROM DATE '" + df + "'))-EXTRACT(MONTH FROM ct.matnr_release_date))";
		if (warType == 1) warComparison = " ";
		else if (warType == 2) warComparison += " <= ct.warranty";
		else if (warType == 3) warComparison += " > ct.warranty";
		
		String s2 = "SELECT"
				+ " br.text45," // 0
				+ " ct.contract_number," // 1
				+ " ct.tovar_serial," // 2
				+ " ct.contract_date," // 2
				+ " (cs.name || ' ' || cs.lastname || ' ' || cs.firstname || ' ' || cs.middlename) as cusFIO," // 4
				+ " (COALESCE(st.lastname,'') || ' ' || COALESCE(st.firstname,'')) as staffLF," // 5
				+ " (select cr.regname from cityreg cr, address ad1 where ad1.addr_id = ct.addr_service_id and ad1.regid = cr.idcityreg) as rayon," // 6
				+ " (select ad.address from address ad where ad.addr_id = ct.addr_service_id) as adres," // 7
				+ " (select ad.tel_dom from address ad where ad.addr_id = ct.addr_service_id) as tel," // 8
				+ " (select ad.tel_mob1 from address ad where ad.addr_id = ct.addr_service_id) as mob1," // 9
				+ " (select ad.tel_mob2 from address ad where ad.addr_id = ct.addr_service_id) as mob2," // 10
				+ " (((EXTRACT(YEAR FROM DATE '" + df + "')-EXTRACT(YEAR FROM sf.f1_date_next))*12+EXTRACT(MONTH FROM DATE '"
				+ df + "'))-EXTRACT(MONTH FROM sf.f1_date_next)) as F1," // 11
				
				+ " sf.id," // 12
				+ " ct.contract_id," // 13
				+ " ct.old_sn," // 14
				+ " ct.customer_id," // 15
				+ " (select ad.tel_dom from address ad where ad.addr_id = ct.addr_work_id) as telRab," // 16
				+ " (select ad.tel_mob1 from address ad where ad.addr_id = ct.addr_work_id) as mobRab1," // 17
				+ " (select ad.tel_mob2 from address ad where ad.addr_id = ct.addr_work_id) as mobRab2," // 18
				+ " sf.crm_category" // 19
				+ " FROM"
				+ " serv_filter_vc sf"
				+ " left join contract ct on ct.contract_id = sf.contract_id"
				+ " left join branch br on br.branch_id = ct.serv_branch_id"
				+ " left join customer cs on cs.customer_id = ct.customer_id"
				+ " left join staff st on st.staff_id = ct.dealer"
				+ " where"
				+ " ct.serv_branch_id = " + in_branch
				+ " and sf.active = 1" 
				+ " and sf.crm_category <> " + ServFilter.CRMCAT_BLACK 
				+ " AND ("
				+ " (COALESCE(sf.f1_date_next,DATE '1999-01-01') between DATE '" + df + "' and DATE '" + dl + "')"
				+ " )"
				+ warComparison
				+ " ORDER BY ct.contract_number ASC";

		Query query = this.em.createNativeQuery(s2);
		// System.out.println("QUERY:   " + s2);

		List<Object[]> results = query.getResultList();
		int i = 1;
		System.out.println("OK");
		for (Object[] result : results) {
			ServFilterOutput sfo = new ServFilterOutput(i);

			sfo.setActive((byte) 1);
			sfo.setBukrs(in_bukrs);

			if (result[0] != null)
				sfo.setBranch((String) result[0]);
			if (result[1] != null)
				sfo.setConNumber(Long.parseLong(String.valueOf(result[1])));
			if (result[2] != null)
				sfo.setTovSerial((String) result[2]);
			if (result[3] != null)
				sfo.setConDate((Date) result[3]);
			if (result[4] != null)
				sfo.setCustomerFIO((String) result[4]);
			if (result[5] != null)
				sfo.setDealerFIO((String) result[5]);
			if (result[6] != null)
				sfo.setRayon((String) result[6]);
			if (result[7] != null)
				sfo.setAddrService((String) result[7]);
			if (result[8] != null)
				sfo.setTelDom((String) result[8]);
			if (result[9] != null)
				sfo.setMob1((String) result[9]);
			if (result[10] != null)
				sfo.setMob2((String) result[10]);
			if (result[11] != null)
				sfo.setF1(Integer.parseInt(String.valueOf(result[11])));
			if (result[12] != null)
				sfo.setId(Long.parseLong(String.valueOf(result[12])));
			if (result[13] != null)
				sfo.setContract_id(Long.parseLong(String.valueOf(result[13])));
			if (result[14] != null)
				sfo.setOldSN(Long.parseLong(String.valueOf(result[14])));
			if (result[15] != null)
				sfo.setCustomerID(Long.parseLong(String.valueOf(result[15])));
			if (result[16] != null) {
				String resTel = (String) result[16];
				String telRab = "";
				if (!Validation.isEmptyString(telRab) && telRab.length() > 0)
					telRab += ", ";
				telRab += resTel;
				sfo.setTelRab(telRab);
			}
			if (result[17] != null) {
				String resTel = (String) result[17];
				String telRab = sfo.getTelRab();
				if (!Validation.isEmptyString(telRab) && telRab.length() > 0)
					telRab += ", ";
				telRab += resTel;
				sfo.setTelRab(telRab);
			}
			if (result[18] != null) {
				String resTel = (String) result[18];
				String telRab = sfo.getTelRab();
				if (!Validation.isEmptyString(telRab) && telRab.length() > 0)
					telRab += ", ";
				telRab += resTel;
				sfo.setTelRab(telRab);
			}
			if (result[19] != null)
				sfo.setCrm_category(Integer.parseInt(String.valueOf(result[19])));
			sfoL.add(sfo);
			i++;
		}
		System.out.println("Total found: " + sfoL.size());
		return sfoL;
	}

	@Override
	public List<ServFilterOutput> findAllOverdueByDate(String in_bukrs,
			Long in_branch, Date in_date, int warType) {
		List<ServFilterOutput> sfoL = new ArrayList<ServFilterOutput>();

		SimpleDateFormat format1 = new SimpleDateFormat("YYYY-MM-dd");
		String df = format1.format(GeneralUtil.getFirstDateOfMonth(in_date));
		String dl = format1.format(GeneralUtil.getLastDateOfMonth(in_date));

		String warComparison = " and (((EXTRACT(YEAR FROM DATE '" + df + "')-EXTRACT(YEAR FROM ct.matnr_release_date))*12"
				+ "+EXTRACT(MONTH FROM DATE '" + df + "'))-EXTRACT(MONTH FROM ct.matnr_release_date))";
		if (warType == 1) warComparison = " ";
		else if (warType == 2) warComparison += " <= ct.warranty";
		else if (warType == 3) warComparison += " > ct.warranty";
		
		String s2 = "SELECT"
				+ " br.text45," // 0
				+ " ct.contract_number," // 1
				+ " ct.tovar_serial," // 2
				+ " ct.contract_date," // 2
				+ " (cs.name || ' ' || cs.lastname || ' ' || cs.firstname || ' ' || cs.middlename) as cusFIO," // 4
				+ " (COALESCE(st.lastname,'') || ' ' || COALESCE(st.firstname,'')) as staffLF," // 5
				+ " (select cr.regname from cityreg cr, address ad1 where ad1.addr_id = ct.addr_service_id and ad1.regid = cr.idcityreg) as rayon," // 6
				+ " (select ad.address from address ad where ad.addr_id = ct.addr_service_id) as adres," // 7
				+ " (select ad.tel_dom from address ad where ad.addr_id = ct.addr_service_id) as tel," // 8
				+ " (select ad.tel_mob1 from address ad where ad.addr_id = ct.addr_service_id) as mob1," // 9
				+ " (select ad.tel_mob2 from address ad where ad.addr_id = ct.addr_service_id) as mob2," // 10
				+ " (((EXTRACT(YEAR FROM DATE '" + df + "')-EXTRACT(YEAR FROM sf.f1_date_next))*12+EXTRACT(MONTH FROM DATE '"
				+ df + "'))-EXTRACT(MONTH FROM sf.f1_date_next)) as F1," // 11
				
				+ " sf.id," // 12
				+ " ct.contract_id," // 13
				+ " ct.old_sn," // 14
				+ " ct.customer_id," // 15
				+ " (select ad.tel_dom from address ad where ad.addr_id = ct.addr_work_id) as telRab," // 16
				+ " (select ad.tel_mob1 from address ad where ad.addr_id = ct.addr_work_id) as mobRab1," // 17
				+ " (select ad.tel_mob2 from address ad where ad.addr_id = ct.addr_work_id) as mobRab2," // 18
				+ " sf.crm_category" // 19
				+ " FROM"
				+ " serv_filter_vc sf"
				+ " left join branch br on br.branch_id = sf.serv_branch"
				+ " left join contract ct on ct.contract_id = sf.contract_id"
				+ " left join customer cs on cs.customer_id = ct.customer_id"
				+ " left join staff st on st.staff_id = ct.dealer"
				+ " where"
				+ " ct.serv_branch_id = " + in_branch 
				+ " and sf.active = 1"
				+ " and sf.crm_category <> " + ServFilter.CRMCAT_BLACK
				+ " AND (" + " (COALESCE(sf.f1_date_next,DATE '1999-01-01') < DATE '" + df + "')"
				+ " )"
				+ " AND not ("
				+ " (COALESCE(sf.f1_date_next,DATE '1999-01-01') between DATE '" + df + "' and DATE '" + dl + "')"
				+ " )"
				+ warComparison
				+ " ORDER BY ct.contract_number ASC";

		Query query = this.em.createNativeQuery(s2);
		List<Object[]> results = query.getResultList();
		int i = 1;
		System.out.println("OK");
		for (Object[] result : results) {
			ServFilterOutput sfo = new ServFilterOutput(i);

			sfo.setActive((byte) 1);
			sfo.setBukrs(in_bukrs);

			if (result[0] != null)
				sfo.setBranch((String) result[0]);
			if (result[1] != null)
				sfo.setConNumber(Long.parseLong(String.valueOf(result[1])));
			if (result[2] != null)
				sfo.setTovSerial((String) result[2]);
			if (result[3] != null)
				sfo.setConDate((Date) result[3]);
			if (result[4] != null)
				sfo.setCustomerFIO((String) result[4]);
			if (result[5] != null)
				sfo.setDealerFIO((String) result[5]);
			if (result[6] != null)
				sfo.setRayon((String) result[6]);
			if (result[7] != null)
				sfo.setAddrService((String) result[7]);
			if (result[8] != null)
				sfo.setTelDom((String) result[8]);
			if (result[9] != null)
				sfo.setMob1((String) result[9]);
			if (result[10] != null)
				sfo.setMob2((String) result[10]);
			if (result[11] != null)
				sfo.setF1(Integer.parseInt(String.valueOf(result[11])));
			
			if (result[12] != null)
				sfo.setId(Long.parseLong(String.valueOf(result[12])));
			if (result[13] != null)
				sfo.setContract_id(Long.parseLong(String.valueOf(result[13])));
			if (result[14] != null)
				sfo.setOldSN(Long.parseLong(String.valueOf(result[14])));
			if (result[15] != null)
				sfo.setCustomerID(Long.parseLong(String.valueOf(result[15])));
			if (result[16] != null) {
				String resTel = (String) result[16];
				String telRab = "";
				if (!Validation.isEmptyString(telRab) && telRab.length() > 0)
					telRab += ", ";
				telRab += resTel;
				sfo.setTelRab(telRab);
			}
			if (result[17] != null) {
				String resTel = (String) result[17];
				String telRab = sfo.getTelRab();
				if (!Validation.isEmptyString(telRab) && telRab.length() > 0)
					telRab += ", ";
				telRab += resTel;
				sfo.setTelRab(telRab);
			}
			if (result[18] != null) {
				String resTel = (String) result[18];
				String telRab = sfo.getTelRab();
				if (!Validation.isEmptyString(telRab) && telRab.length() > 0)
					telRab += ", ";
				telRab += resTel;
				sfo.setTelRab(telRab);
			}
			if (result[19] != null)
				sfo.setCrm_category(Integer.parseInt(String.valueOf(result[19])));
			sfoL.add(sfo);
			i++;
		}
		return sfoL;
	}

	@Override
	public List<ServFilterVC> findAllByCRMCategory(String in_bukrs,
			Long in_branch, int in_crmCat) {
		String query = "SELECT sf FROM ServFilterVC sf WHERE sf.bukrs = '"
				+ in_bukrs + "' and sf.active = 1 "
				+ "and sf.crm_category = :crm";
		Query q = this.em.createQuery(query);
		q.setParameter("crm", in_crmCat);
		List<ServFilterVC> result = q.getResultList();
		return result;
	}

	@Override
	public ServFilterVC findByTovarSN(String in_bukrs, String in_tovSN) {
		String query = "SELECT sf FROM ServFilterVC sf WHERE sf.bukrs = '"
				+ in_bukrs + "' and sf.active = 1 " + "and sf.tovar_sn = :tsn";
		Query q = this.em.createQuery(query);
		q.setParameter("tsn", in_tovSN);
		List<ServFilterVC> result = q.getResultList();
		if (result.size() > 0) {
			return result.get(0);
		}
		return null;
	}

	@Override
	public ServFilterVC findByContractID(Long in_conId) {
		String query = "SELECT sf FROM ServFilterVC sf WHERE sf.contract_id = :in_conId";
		Query q = this.em.createQuery(query);
		q.setParameter("in_conId", in_conId);
		List<ServFilterVC> result = q.getResultList();
		if (result.size() > 0) {
			return result.get(0);
		}
		return null;
	}

	@Override
	public List<ServFilterVC> findAllForCurrentPlanByDate(String in_bukrs,
			Long in_branch, Date in_date) {
		SimpleDateFormat format1 = new SimpleDateFormat("YYYY-MM-dd");
		String df = format1.format(GeneralUtil.getFirstDateOfMonth(in_date));
		Date d = in_date;
		String dl = format1.format(GeneralUtil.getLastDateOfMonth(in_date));

		System.out.println("INPUTS ARE:   Bukrs: " + in_bukrs + "     Branch: "
				+ in_branch + "     Date: " + format1.format(in_date));
		System.out.println("DF: " + df + "     DL: " + dl);

		String s2 = "SELECT "
				+ " sf.id," // 0
				+ " sf.bukrs," // 1
				+ " sf.tovar_sn," // 2
				+ " sf.crm_category," // 3
				+ " sf.fno," // 4
				+ " sf.f1_mt," // 5
				+ " sf.f1_sid," // 6
				+ " sf.f1_date," // 7
				+ " sf.f1_sid_prev," // 8
				+ " sf.f1_date_prev," // 9
				+ " sf.f1_date_next," // 10
				+ " sf.active," // 35
				+ " sf.serv_branch," // 36
				+ " sf.contract_id" // 37
				+ " FROM serv_filter_vc sf, Contract ct " + " where"
				+ " ct.serv_branch_id = " + in_branch
				+ " and sf.contract_id = ct.contract_id"
				+ " and ct.contract_status_id <> "
				+ ContractStatus.STATUS_CANCELLED
				+ " and sf.active = 1"
				+ " AND ("
				+ " (COALESCE(sf.f1_date_next,DATE '1999-01-01') between DATE '" + df + "' and DATE '" + dl + "')"
				+ " OR  ((COALESCE(sf.f1_date,DATE '1999-01-01') between DATE '" + df + "' and DATE '" + dl + "')"
				+ " AND (ROUND(MONTHS_BETWEEN(COALESCE(sf.f1_date,DATE '1999-01-01'), COALESCE(sf.f1_date_prev,DATE '1999-01-01'))) = sf.f1_mt))"
				+ " ) " + " ORDER BY ct.contract_number ASC";

		Query query = this.em.createNativeQuery(s2);
		//System.out.println("QUERY:   " + s2);
		List<Object[]> results = query.getResultList();
		List<ServFilterVC> sfL = new ArrayList<>();
		int i = 0;
		for (Object[] res : results) {
			ServFilterVC sf = new ServFilterVC();
			// BeanUtils.copyProperties(res, sf);
			if (res[0] != null)
				sf.setId(Long.parseLong(String.valueOf(res[0])));
			if (res[1] != null)
				sf.setBukrs(String.valueOf(res[1]));
			if (res[2] != null)
				sf.setTovar_sn(String.valueOf(res[2]));
			if (res[3] != null)
				sf.setCrm_category(Integer.parseInt(String.valueOf(res[3])));
			if (res[4] != null)
				sf.setFno(Integer.parseInt(String.valueOf(res[4])));

			if (res[5] != null)
				sf.setF1_mt(Integer.parseInt(String.valueOf(res[5])));
			if (res[6] != null)
				sf.setF1_sid(Long.parseLong(String.valueOf(res[6])));
			if (res[7] != null)
				sf.setF1_date((Date) res[7]);
			if (res[8] != null)
				sf.setF1_sid_prev(Long.parseLong(res[8].toString()));
			if (res[9] != null)
				sf.setF1_date_prev((Date) res[9]);
			if (res[10] != null)
				sf.setF1_date_next((Date) res[10]);


			if (res[35] != null)
				sf.setActive(Byte.parseByte(String.valueOf(res[35])));
			if (res[36] != null)
				sf.setServ_branch(Long.parseLong(res[36].toString()));
			if (res[37] != null)
				sf.setContract_id(Long.parseLong(res[37].toString()));

			sfL.add(sf);
		}
		System.out.println("OK");
		System.out.println("Total found: " + sfL.size());
		return sfL;
	}

	@Override
	public List<ServFilterVC> findAllForOverduePlanByDate(String in_bukrs,
			Long in_branch, Date in_date) {
		SimpleDateFormat format1 = new SimpleDateFormat("YYYY-MM-dd");
		String df = format1.format(GeneralUtil.getFirstDateOfMonth(in_date));
		Date d = in_date;
		String dl = format1.format(GeneralUtil.getLastDateOfMonth(in_date));

		System.out.println("INPUTS ARE:   Bukrs: " + in_bukrs + "     Branch: "
				+ in_branch + "     Date: " + format1.format(in_date));
		System.out.println("DF: " + df + "     DL: " + dl);

		String s2 = "SELECT "
				+ " sf.id," // 0
				+ " sf.bukrs," // 1
				+ " sf.tovar_sn," // 2
				+ " sf.crm_category," // 3
				+ " sf.fno," // 4
				+ " sf.f1_mt," // 5
				+ " sf.f1_sid," // 6
				+ " sf.f1_date," // 7
				+ " sf.f1_sid_prev," // 8
				+ " sf.f1_date_prev," // 9
				+ " sf.f1_date_next," // 10
				
				+ " sf.active," // 11
				+ " sf.serv_branch," // 12
				+ " sf.contract_id" // 13
				+ " FROM serv_filter_vc sf, Contract ct " + " where"
				+ " ct.serv_branch_id = " + in_branch
				+ " and sf.contract_id = ct.contract_id"
				+ " and ct.contract_status_id <> "
				+ ContractStatus.STATUS_CANCELLED
				+ " and sf.active = 1"
				+ " AND ("
				+ " (COALESCE(sf.f1_date_next,DATE '1999-01-01') < DATE '" + df + "')"
				+ " OR  ((COALESCE(sf.f1_date,DATE '1999-01-01') between DATE '" + df + "' and DATE '" + dl + "')"
				+ " AND (ROUND(MONTHS_BETWEEN(COALESCE(sf.f1_date,DATE '1999-01-01'), COALESCE(sf.f1_date_prev,DATE '1999-01-01'))) > sf.f1_mt))"
				+ ")"
				+ " AND not ("
				+ " (COALESCE(sf.f1_date_next,DATE '1999-01-01') between DATE '" + df + "' and DATE '" + dl + "')"
				+ ")"
				+ " ORDER BY ct.contract_number ASC";

		Query query = this.em.createNativeQuery(s2);
		//System.out.println("QUERY:   " + s2);
		List<Object[]> results = query.getResultList();
		List<ServFilterVC> sfL = new ArrayList<>();
		for (Object[] res : results) {
			ServFilterVC sf = new ServFilterVC();
			// BeanUtils.copyProperties(res, sf);
			if (res[0] != null)
				sf.setId(Long.parseLong(String.valueOf(res[0])));
			if (res[1] != null)
				sf.setBukrs(String.valueOf(res[1]));
			if (res[2] != null)
				sf.setTovar_sn(String.valueOf(res[2]));
			if (res[3] != null)
				sf.setCrm_category(Integer.parseInt(String.valueOf(res[3])));
			if (res[4] != null)
				sf.setFno(Integer.parseInt(String.valueOf(res[4])));

			if (res[5] != null)
				sf.setF1_mt(Integer.parseInt(String.valueOf(res[5])));
			if (res[6] != null)
				sf.setF1_sid(Long.parseLong(String.valueOf(res[6])));
			if (res[7] != null)
				sf.setF1_date((Date) res[7]);
			if (res[8] != null)
				sf.setF1_sid_prev(Long.parseLong(res[8].toString()));
			if (res[9] != null)
				sf.setF1_date_prev((Date) res[9]);
			if (res[10] != null)
				sf.setF1_date_next((Date) res[10]);

			if (res[11] != null)
				sf.setActive(Byte.parseByte(String.valueOf(res[11])));
			if (res[12] != null)
				sf.setServ_branch(Long.parseLong(res[12].toString()));
			if (res[13] != null)
				sf.setContract_id(Long.parseLong(res[13].toString()));

			sfL.add(sf);
		}
		System.out.println("OK");
		System.out.println("Total found: " + sfL.size());
		return sfL;
	}	

}
