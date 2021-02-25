package general.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.PromotionDao;
import general.tables.Branch;
import general.tables.Promotion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("promotionService")
public class PromotionServiceImpl implements PromotionService {
	@Autowired
	PromotionDao promoDao;

	@Autowired
	BranchDao branchDao;

	@Override
	public Promotion createPromo(Promotion p) throws DAOException {
		String error = validatePromo(p, true);

		if (error.length() > 0) {
			throw new DAOException(error);
		}

		if (p.getId() != null)
			p.setId(null);

		p.setIs_active(1L);
		promoDao.create(p);

		Calendar oDate = Calendar.getInstance();
		oDate.setTime(p.getDate_start());
		// Updating order number
		String oNumber = "";
		oNumber = Integer.toString(oDate.get(Calendar.YEAR));
		oNumber = oNumber.substring(oNumber.length() - 1);
		oNumber = oNumber + Integer.toString(oDate.get(Calendar.MONTH));

		Long p_type_id = 0L;
		if (p.getPm_type() != null && p.getPm_type() > 0)
			p_type_id = p.getPm_type();

		Long p_scope = 0L;
		if (p.getPm_scope().equals("INT")) {
			p_scope = 1L;
		} else if (p.getPm_scope().equals("GEN")) {
			p_scope = 2L;
		} else if (p.getPm_scope().equals("REG")) {
			p_scope = 3L;
		}
		if (p.getPm_scope().equals("COM")) {
			p_scope = 4L;
		}

		Long br_id = 0L;
		if (p.getBranch_id() != null && p.getBranch_id() > 0)
			br_id = p.getBranch_id();

		oNumber = Long.toString(p_type_id) + Long.toString(p_scope)
				+ Long.toString(br_id) + Long.toString(p.getId()) + oNumber;
		p.setPm_number(Long.parseLong(oNumber));

		System.out.println("Promo-campaign NUMBER: " + p.getPm_number());
		promoDao.update(p);

		addMessage("A new Promotion with #" + oNumber
				+ " has been successfully created!", "");

		return p;
	}

	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	private String validatePromo(Promotion p, boolean isNew) {
		String error = "";

		// ********************************************************
		// Validation check
		// ********************************************************

		return error;
	}

	@Override
	public void updatePromo(Promotion p) throws DAOException {
		String error = validatePromo(p, false);
		if (error.length() > 0) {
			throw new DAOException(error);
		}
		promoDao.update(p);

	}

	@Override
	public List<Promotion> findAllByBranch(Long a_branchID) throws DAOException {

		List<Branch> br_list = branchDao.findAll();

		Map<Long, Branch> l_branch_map = new HashMap<Long, Branch>();

		for (Branch bra : br_list) {
			l_branch_map.put(bra.getBranch_id(), bra);
		}

		Branch b = l_branch_map.get(a_branchID);

		Branch bc = l_branch_map.get(b.getParent_branch_id());

		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		Calendar d = Calendar.getInstance();
		// System.out.println("Current date: " + format1.format(d.getTime()));
		// To be added the date parameters to injection query

		String wcl = "branch_id = " + String.valueOf(a_branchID)
				+ " and bukrs = '" + b.getBukrs() + "' and pm_scope = 'INT'"
				+ " and date_start <= " + "'" + format1.format(d.getTime()).toString() + "'"
				+ " and date_end >= " + "'" + format1.format(d.getTime()).toString() + "'"
				+ " and is_active = 1";
		System.out.println(wcl);
		
		//to_date('2001-12-10', 'yyyy-mm-dd')
		
		List<Promotion> res1 = promoDao.dynamicFindAll(wcl);

		wcl = "country_id = " + String.valueOf(b.getCountry_id())
				+ " and bukrs = '" + b.getBukrs() + "' and pm_scope = 'GEN'"
				+ " and date_start <= " + "'" + format1.format(d.getTime()).toString() + "'"
				+ " and date_end >= " + "'" + format1.format(d.getTime()).toString() + "'"
				+ " and is_active = 1";
		List<Promotion> res2 = promoDao.dynamicFindAll(wcl);

		wcl = "region_id = " + String.valueOf(bc.getParent_branch_id())
				+ " and bukrs = '" + b.getBukrs() + "' and pm_scope = 'REG'"
				+ " and date_start <= " + "'" + format1.format(d.getTime()).toString() + "'"
				+ " and date_end >= " + "'" + format1.format(d.getTime()).toString() + "'"
				+ " and is_active = 1";
		List<Promotion> res3 = promoDao.dynamicFindAll(wcl);

		wcl = "bukrs = '" + b.getBukrs() + "' and pm_scope = 'COM'"
				+ " and date_start <= " + "'" + format1.format(d.getTime()).toString() + "'"
				+ " and date_end >= " + "'" + format1.format(d.getTime()).toString() + "'"
				+ " and is_active = 1";
		List<Promotion> res4 = promoDao.dynamicFindAll(wcl);

		List<Promotion> results = new ArrayList<Promotion>();
		results.addAll(res1);
		results.addAll(res2);
		results.addAll(res3);
		results.addAll(res4);

		System.out.println("Total Promo Found for " + b.getText45() + " : "
				+ results.size());

		return results;
	}

	public List<Promotion> findAllByCurrentDate() {

		Calendar d = Calendar.getInstance();
		System.out.println("Current date: " + d);
		List<Promotion> result = new ArrayList<Promotion>();
		/*
		 * Query q = this.em.createQuery(String.format(
		 * "SELECT p FROM Promotion p Where branch_id = 'yyyy-MM-dd'", d));
		 * List<Promotion> result = q.getResultList();
		 */
		return result;
	}
}
