package general.dao.impl;

import general.Validation;
import general.dao.HrDocItemDao;
import general.dao.StaffDao;
import general.tables.HrDoc;
import general.tables.HrDocItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("hrDocItemDao")
public class HrDocItemDaoImpl extends GenericDaoImpl<HrDocItem>implements HrDocItemDao {

	@Autowired
	StaffDao stfDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<HrDocItem> findAll(String cond) {
		String s = "SELECT h FROM HrDocItem h ";
		if (cond.length() > 0) {
			s += " WHERE " + cond;
		}
		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HrDocItem> findAllRep4Data(String bukrs, List<String> branchIds, List<String> departmentIds,
			List<String> positionIds, Date fromDate, Date toDate, int typeId) {
		String s = " SELECT item.id, item.department_id, item.branch_id, item.begin_date, item.end_date,doc.id,doc.type_id FROM hr_doc_item item, hr_doc doc"
				+ " WHERE doc.id=item.doc_id AND doc.status_id = " + HrDoc.STATUS_CLOSED;

		if (!Validation.isEmptyString(bukrs)) {
			s += " AND doc.bukrs = " + bukrs;
		}

		if (!Validation.isEmptyCollection(branchIds)) {
			s += " AND doc.branch_id IN(" + String.join(",", branchIds) + ") ";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");

		if (fromDate != null) {
			String fromDateStr = sdf.format(fromDate).toString();
			String ss = " ( doc.type_id = %d AND item.end_date >= '%s' ) OR ( doc.type_id != %d AND item.begin_date >= '%s') ";
			s += " AND " + String.format(ss, HrDoc.TYPE_DISMISS, fromDateStr, HrDoc.TYPE_DISMISS, fromDateStr);
		}

		if (toDate != null) {
			String toDateStr = sdf.format(fromDate).toString();
			String ss = " ( doc.type_id = %d AND item.end_date <= '%s' ) OR ( doc.type_id != %d AND item.begin_date <= '%s') ";
			s += " AND " + String.format(ss, HrDoc.TYPE_DISMISS, toDateStr, HrDoc.TYPE_DISMISS, toDateStr);
		}

		if (!Validation.isEmptyCollection(departmentIds)) {
			s += " AND item.department_id IN(" + String.join(",", departmentIds) + ") ";
		}

		if (!Validation.isEmptyCollection(positionIds)) {
			s += " AND item.position_id IN(" + String.join(",", positionIds) + ") ";
		}

		if (typeId > 0) {
			s += " AND doc.type_id = " + typeId;
		}

		Query q = em.createNativeQuery(s);
		List<Object[]> result = q.getResultList();
		List<HrDocItem> out = new ArrayList<>();
		for (Object[] row : result) {
			HrDocItem hrDocItem = new HrDocItem();
			hrDocItem.setId(new Long(String.valueOf(row[0])));
			hrDocItem.setDepartmentId(new Long(String.valueOf(row[1])));
			hrDocItem.setBranchId(new Long(String.valueOf(row[2])));
			String beginDateStr = String.valueOf(row[3]);
			String endDateStr = String.valueOf(row[4]);
			if (!Validation.isEmptyString(beginDateStr)) {
				try {
					hrDocItem.setBeginDate(sdf.parse(beginDateStr));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}

			try {
				hrDocItem.setEndDate(sdf.parse(endDateStr));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

			Long docId = new Long(String.valueOf(row[5]));
			Integer docTypeId = new Integer(String.valueOf(row[6]));

			HrDoc doc = new HrDoc();
			doc.setId(docId);
			doc.setTypeId(docTypeId);
			hrDocItem.setHrDoc(doc);

			out.add(hrDocItem);
		}

		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HrDocItem> findAllNotClosedBypassDocByStaffId(Long staffId) {
		String s = " SELECT item, doc FROM HrDocItem item, HrDoc doc WHERE item.hrDoc=doc AND item.staffId=%d AND doc.statusId NOT IN(%d,%d,%d) ";
		Query q = em.createQuery(
				String.format(s, staffId, HrDoc.STATUS_CANCELLED, HrDoc.STATUS_CLOSED, HrDoc.STATUS_REFUSED));
		List<Object[]> result = q.getResultList();
		List<HrDocItem> out = new ArrayList<>();
		for (Object[] o : result) {
			HrDocItem item = (HrDocItem) o[0];
			HrDoc doc = (HrDoc) o[1];
			item.setHrDoc(doc);
			out.add(item);
		}
		return out;
	}

}
