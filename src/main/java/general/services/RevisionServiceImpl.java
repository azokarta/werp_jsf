package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.Validation;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.InvoiceItemDao;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.PriceListDao;
import general.dao.RevItemDao;
import general.dao.RevItemTitleDao;
import general.dao.RevisionDao;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.tables.Branch;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.PriceList;
import general.tables.RevItem;
import general.tables.RevItemTitle;
import general.tables.RevItemType;
import general.tables.RevResponsible;
import general.tables.Revision;
import general.tables.Salary;
import logistics.LogFinClass;
import user.User;

@Service("revisionService")
public class RevisionServiceImpl implements RevisionService {

	@Autowired
	RevisionDao revDao;

	@Autowired
	StaffDao staffDao;

	@Autowired
	SalaryDao salaryDao;

	@Autowired
	BranchDao branchDao;

	@Autowired
	MatnrListDao matnrListDao;

	@Autowired
	RevItemTitleDao revItemTitleDao;

	@Autowired
	RevItemDao revItemDao;

	@Autowired
	PriceListDao priceListDao;

	@Autowired
	MatnrDao matnrDao;

	@Autowired
	FinanceServiceLogistics finServiceLogistics;

	@Autowired
	MatnrListService mlService;

	@Autowired
	InvoiceDao invoiceDao;

	@Autowired
	InvoiceItemDao invoiceItemDao;

	@Override
	public List<Revision> findAll() {
		return revDao.findAll();
	}

	@Override
	public void create(Revision r, User userData) throws DAOException {
		String er = validate(r, userData, true);
		if (er.length() > 0) {
			throw new DAOException(er);
		}

		addResponsibles(r);
		revDao.create(r);
	}

	private void addResponsibles(Revision r) {
		List<RevResponsible> revResList = new ArrayList<>();
		List<Branch> childBrList = branchDao.findChilds(r.getBranchId());
		if (childBrList.size() > 0) {
			String[] ids = new String[childBrList.size()];
			for (int k = 0; k < childBrList.size(); k++) {
				ids[k] = childBrList.get(k).getBranch_id().toString();
			}
			List<Salary> salList = salaryDao.findAllCurrent(
					String.format("position_id IN(10,65,51,7) AND branch_id IN(%s) ", String.join(",", ids)));

			for (Salary sal : salList) {
				RevResponsible revRes = new RevResponsible();
				revRes.setPositionId(sal.getPosition_id());
				revRes.setStaffId(sal.getStaff_id());
				revRes.setRevision(r);
				revResList.add(revRes);
			}
		}

		r.setRevResponsibles(revResList);
	}

	private String validate(Revision r, User userData, boolean isNew) {
		String error = "";
		if (isNew) {
			r.setCreatedAt(Calendar.getInstance().getTime());
			r.setCreatedBy(userData.getUserid());
		}

		r.setUpdatedAt(Calendar.getInstance().getTime());
		r.setUpdatedBy(userData.getUserid());

		if (Validation.isEmptyString(r.getBukrs())) {
			error += "Выберите компанию \n";
		}

		if (Validation.isEmptyLong(r.getBranchId())) {
			error += "Выберите филиал \n";
		}

		if (Validation.isEmptyLong(r.getWerks())) {
			error += "Выберите склад \n";
		}

		if (r.getBeginDate() == null) {
			error += "Введите дата начало \n";
		}

		if (r.getFinishDate() == null) {
			error += "Введите дата окончание \n";
		}

		if (r.getBeginDate() != null && r.getFinishDate() != null && r.getBeginDate().after(r.getFinishDate())) {
			error += "Дата начало не может быть больше Дата окончания \n";
		}

		return error;
	}

	@Override
	public void update(Revision r, User userData) throws DAOException {
		if (!r.getStatus().equals(Revision.STATUS_NEW)) {
			throw new DAOException("Документ не редактируемый");
		}
		String er = validate(r, userData, false);
		if (er.length() > 0) {
			throw new DAOException(er);
		}

		Revision oldRev = revDao.find(r.getId());
		if (!oldRev.getBranchId().equals(r.getBranchId())) {
			r.setRevResponsibles(new ArrayList<>());
		}
		addResponsibles(r);
		revDao.update(r);
	}

	@Override
	public Revision find(Long id) {
		return revDao.find(id);
	}

	@Override
	public Revision findWithDetail(Long id) {
		return revDao.findWithDetail(id);
	}

	@Override
	public List<RevItemType> getItemTypes() {
		List<RevItemType> out = new ArrayList<>();

		RevItemType rob = new RevItemType();
		rob.setId(1);
		rob.setName("Roboclean");
		rob.setType(RevItemType.TYPE_MATNR);
		List<Long> robIds = new ArrayList<>();
		robIds.add(1L);
		robIds.add(4L);
		rob.setMatnrIdList(robIds);
		rob.setParentId(0);
		out.add(rob);

		RevItemType ceb = new RevItemType();
		ceb.setId(2);
		ceb.setName("Cebilon");
		ceb.setType(RevItemType.TYPE_MATNR);
		List<Long> cebIds = new ArrayList<>();
		cebIds.add(812L);
		cebIds.add(256L);
		cebIds.add(2L);
		cebIds.add(3L);
		cebIds.add(1006L);
		ceb.setMatnrIdList(cebIds);
		ceb.setParentId(0);
		out.add(ceb);

		RevItemType partRob = new RevItemType();
		partRob.setId(3);
		partRob.setName("Запчасти Roboclean");
		partRob.setType(RevItemType.TYPE_PART);
		partRob.setParentId(rob.getId());
		out.add(partRob);

		RevItemType partCeb = new RevItemType();
		partCeb.setId(4);
		partCeb.setName("Запчасти Cebilon");
		partCeb.setType(RevItemType.TYPE_PART);
		partCeb.setParentId(ceb.getId());
		out.add(partCeb);

		RevItemType partOther = new RevItemType();
		partOther.setId(5);
		partOther.setName("Остальные запчасти");
		partOther.setType(RevItemType.TYPE_PART);
		out.add(partOther);

		return out;
	}

	private RevItemType getRevItemTypeById(int id) {
		for (RevItemType r : getItemTypes()) {
			if (id == r.getId()) {
				return r;
			}
		}

		return null;
	}

	@Override
	public List<RevItem> findItemsByRevItemTypeId(Revision rev, int typeId) {
		RevItemType revType = getRevItemTypeById(typeId);
		if (revType == null) {
			return null;
		}

		List<RevItem> out = new ArrayList<>();

		List<MatnrList> mlList = new ArrayList<>();
		String cond = String.format(" werks = %d AND status IN('%s','%s','%s') ", rev.getWerks(),
				MatnrList.STATUS_RECEIVED, MatnrList.STATUS_RESERVED, MatnrList.STATUS_ACCOUNTABILITY);
		if (revType.getType() == RevItemType.TYPE_MATNR) {
			String[] ids = new String[revType.getMatnrIdList().size()];
			for (int k = 0; k < revType.getMatnrIdList().size(); k++) {
				ids[k] = revType.getMatnrIdList().get(k).toString();
			}

			cond += " AND matnr IN(" + String.join(",", ids) + ") ORDER BY matnr ";

			mlList = matnrListDao.dynamicFind3(cond);
			for (MatnrList ml : mlList) {
				RevItem it = new RevItem();
				it.setBarcode(ml.getBarcode());
				it.setDbQuantity(1D);
				it.setFactQuantity(1D);
				it.setMatnr(ml.getMatnr());
				it.setStateId(ml.getState_id());
				out.add(it);
			}
		} else {
			List<PriceList> plList = priceListDao.findAllLast();
			Map<Long, PriceList> plMap = new HashMap<>();
			for (PriceList pl : plList) {
				plMap.put(pl.getMatnr(), pl);
			}
			RevItemType parent = getRevItemTypeById(revType.getParentId());
			if (parent != null) {
				String[] ids = new String[parent.getMatnrIdList().size()];
				for (int k = 0; k < parent.getMatnrIdList().size(); k++) {
					ids[k] = parent.getMatnrIdList().get(k).toString();
				}
				cond += String.format(" AND matnr IN(SELECT sparepart_id FROM MatnrSparePart WHERE tovar_id IN(%s))",
						String.join(",", ids));
			} else {
				cond += " AND matnr IN(SELECT matnr FROM Matnr WHERE type IN(4,5,6,8))";
			}

			mlList = matnrListDao.dynamicFind(cond);
			for (MatnrList ml : mlList) {
				RevItem it = new RevItem();
				it.setBarcode(ml.getBarcode());
				it.setDbQuantity(ml.getMenge());
				it.setFactQuantity(ml.getMenge());
				it.setMatnr(ml.getMatnr());
				it.setStateId(ml.getState_id());
				PriceList pl = plMap.get(ml.getMatnr());
				if (pl != null) {
					it.setUnitPrice(pl.getPrice());
				}
				out.add(it);
			}
		}

		return out;
	}

	@Override
	public void createItemTitle(RevItemTitle revTitle, List<RevItem> items, User userData) throws DAOException {
		String error = validateRevTitle(revTitle, true, userData);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		revItemTitleDao.create(revTitle);
		for (RevItem ri : items) {
			ri.setTitleId(revTitle.getId());
			revItemDao.create(ri);
		}

		Revision revision = revDao.find(revTitle.getRevId());
		revision.setStatus(Revision.STATUS_IN_ACTION);
		revDao.update(revision);
	}

	private String validateRevTitle(RevItemTitle revTitle, boolean isNew, User userData) {
		String error = "";
		if (isNew) {
			revTitle.setCreatedAt(Calendar.getInstance().getTime());
			revTitle.setCreatedBy(userData.getUserid());
			List<RevItemTitle> temp = revItemTitleDao
					.findAll(" type_id = " + revTitle.getTypeId() + " AND rev_id = " + revTitle.getRevId());
			if (temp.size() > 0) {
				error += "Акт с таким типом материала уже создан \n";
			}
		}

		revTitle.setUpdatedAt(Calendar.getInstance().getTime());
		revTitle.setUpdatedBy(userData.getUserid());

		if (Validation.isEmptyString(revTitle.getTitle())) {
			error += "Название обязательно для заполнения \n";
		}

		if (revTitle.getTypeId() == 0) {
			error += "Выберите тип материала \n";
		}
		return error;
	}

	private String validateItems(RevItemTitle rit, List<RevItem> items) {
		String error = "";
		for (RevItem ri : items) {
			if (!Validation.isEmptyString(ri.getBarcode())) {
				ri.setBarcode(ri.getBarcode().trim());
			}
			if (ri.getDbQuantity() == 0) {
				if (Validation.isEmptyLong(ri.getMatnr())) {
					error += "Выберите вид материала \n";
				}

				if (rit.getTypeId() == 1 || rit.getTypeId() == 2) {
					if (Validation.isEmptyString(ri.getBarcode())) {
						error += "Поле Заводской номер не может быть пустым \n";
					} else {
						List<MatnrList> l = matnrListDao.findAll(" barcode='" + ri.getBarcode() + "' ", 1);
						if (l != null && l.size() > 0) {
							error += " Материал с номером \"" + ri.getBarcode() + "\" имееться на складе \n";
						}
					}
				}
			}
		}
		return error;
	}

	@Override
	public void updateItemTitle(RevItemTitle revTitle, List<RevItem> items, User userData) throws DAOException {
		String error = validateRevTitle(revTitle, false, userData);
		error += validateItems(revTitle, items);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		revItemTitleDao.update(revTitle);

		List<RevItem> oldItems = revItemDao.findAll(" title_id = " + revTitle.getId());
		Map<Long, Integer> oldItemsMap = new HashMap<>();
		for (RevItem ri : oldItems) {
			oldItemsMap.put(ri.getId(), 1);
		}

		Map<Long, Integer> newItemsMap = new HashMap<>();

		for (RevItem ri : items) {
			ri.setTitleId(revTitle.getId());
			if (Validation.isEmptyLong(ri.getId()) || !oldItemsMap.containsKey(ri.getId())) {
				ri.setId(null);
				revItemDao.create(ri);
			} else {
				revItemDao.update(ri);
			}

			newItemsMap.put(ri.getId(), 1);
		}

		for (RevItem ri : oldItems) {
			if (!newItemsMap.containsKey(ri.getId())) {
				revItemDao.delete(ri.getId());
			}
		}
	}

	@Override
	public RevItemTitle findRevTitle(Long id) {
		return revItemTitleDao.find(id);
	}

	@Override
	public List<RevItem> findItemsByTitleId(Long revTitleId) {
		return revItemDao.findAll(" title_id = " + revTitleId);
	}

	@Override
	public List<RevItemTitle> findRevTitlesByRevId(Long revId) {
		return revItemTitleDao.findAll(" rev_id = " + revId);
	}

	@Override
	public Map<RevItemTitle, List<RevItem>> findCurrentResult(Long revId) {
		Map<Long, Matnr> matnrMap = matnrDao.getMappedList("");
		Map<RevItemTitle, List<RevItem>> out = new HashMap<>();
		List<RevItemTitle> titleList = findRevTitlesByRevId(revId);
		for (RevItemTitle rit : titleList) {
			List<RevItem> tempItemList = new ArrayList<>();
			for (RevItem item : findItemsByTitleId(rit.getId())) {
				if (item.getDbQuantity().equals(item.getFactQuantity())) {
					continue;
				}

				Matnr m = matnrMap.get(item.getMatnr());
				if (m == null) {
					continue;
				}

				if (item.getFactQuantity() - item.getDbQuantity() > 0) {
					item.setOverQuantity(item.getFactQuantity() - item.getDbQuantity());
				} else {
					item.setOverQuantity(0D);
				}
				if (item.getDbQuantity() - item.getFactQuantity() > 0) {
					item.setDeficitQuantity(item.getDbQuantity() - item.getFactQuantity());
				} else {
					item.setDeficitQuantity(0D);
				}

				item.setDeficitAmount(item.getDeficitQuantity() * item.getUnitPrice());
				item.setOverAmount(item.getUnitPrice() * item.getOverQuantity());
				item.setMatnrObject(m);
				tempItemList.add(item);
			}

			if (tempItemList.size() > 0) {
				out.put(rit, tempItemList);
			}
		}

		return out;
	}

	@Override
	public void finish(Revision r, User userData) throws DAOException {
		if (!Revision.STATUS_IN_ACTION.equals(r.getStatus())) {
			throw new DAOException("Нет доступа!");
		}

		r.setStatus(Revision.STATUS_FINISHED);
		r.setUpdatedAt(Calendar.getInstance().getTime());
		r.setUpdatedBy(userData.getUserid());

		revDao.update(r);

	}

	@Override
	public void returnToAction(Revision r, User userData) throws DAOException {
		if (!Revision.STATUS_FINISHED.equals(r.getStatus())) {
			throw new DAOException("Не возможно вернуть документ!");
		}

		r.setStatus(Revision.STATUS_IN_ACTION);
		r.setUpdatedAt(Calendar.getInstance().getTime());
		r.setUpdatedBy(userData.getUserid());

		revDao.update(r);
	}

	@Override
	public List<RevItem> getPreparedPostingItems(Long revId) {
		Map<Long, Matnr> matnrMap = matnrDao.getMappedList("");
		List<RevItem> out = new ArrayList<>();
		for (RevItemTitle rit : findRevTitlesByRevId(revId)) {
			// if (RevItemType.TYPE_PART == rit.getTypeId()) {
			for (RevItem item : findItemsByTitleId(rit.getId())) {
				Double d = item.getFactQuantity() - item.getDbQuantity();
				if (d > 0) {
					Matnr m = matnrMap.get(item.getMatnr());
					if (m != null) {
						item.setOverQuantity(d);
						item.setOverAmount(item.getUnitPrice() * item.getOverQuantity());
						item.setMatnrObject(m);
						out.add(item);
					}
				}
			}
			// }
		}

		return out;
	}

	@Override
	public List<RevItem> getPreparedWriteoffItems(Long revId) {
		Map<Long, Matnr> matnrMap = matnrDao.getMappedList("");
		List<RevItem> out = new ArrayList<>();
		for (RevItemTitle rit : findRevTitlesByRevId(revId)) {
			// if (RevItemType.TYPE_PART == rit.getTypeId()) {
			for (RevItem item : findItemsByTitleId(rit.getId())) {
				Double d = item.getDbQuantity() - item.getFactQuantity();
				if (d > 0) {
					Matnr m = matnrMap.get(item.getMatnr());
					if (m != null) {
						item.setDeficitQuantity(d);
						item.setDeficitAmount(item.getUnitPrice() * d);
						item.setMatnrObject(m);
						out.add(item);
					}
				}
			}
			// }
		}

		return out;
	}

	@Override
	public void closeDoc(Revision r, Long userId, List<RevItem> postingRevItems, List<RevItem> writeoffRevItems)
			throws DAOException {
		if (!Revision.STATUS_IN_ACTION.equals(r.getStatus())) {
			throw new DAOException("Нет доступа!");
		}

		if (Revision.STATUS_CLOSED.equals(r.getStatus()) || Revision.STATUS_FINISHED.equals(r.getStatus())) {
			throw new DAOException("Нет доступа!");
		}

		List<LogFinClass> wOffList = new ArrayList<>();
		List<InvoiceItem> wOffItems = new ArrayList<>();

		for (RevItem item : writeoffRevItems) {
			LogFinClass lfc = new LogFinClass();
			lfc.setMatnr(item.getMatnr());
			lfc.setMenge(item.getDeficitQuantity());
			lfc.setTotalDmbtr(item.getDeficitAmount());

			InvoiceItem ii = new InvoiceItem();
			ii.setBarcode(item.getBarcode());
			ii.setDone_quantity(item.getDeficitQuantity());
			ii.setMatnr(item.getMatnr());
			ii.setQuantity(item.getDeficitQuantity());
			wOffItems.add(ii);
		}

		Long wOffAwkey = finServiceLogistics.removeMatnrFromWerksLost(wOffList, r.getBukrs(), userId, r.getBranchId(),
				"revision", r.getWerks());

		Invoice wOff = new Invoice();
		wOff.setAwkey(wOffAwkey);
		wOff.setBranch_id(r.getBranchId());
		wOff.setBukrs(r.getBukrs());
		wOff.setCreated_at(Calendar.getInstance().getTime());
		wOff.setCreated_by(userId);
		wOff.setFrom_werks(r.getWerks());
		wOff.setInvoice_date(Calendar.getInstance().getTime());
		wOff.setIs_system(1);
		wOff.setNote("СПИСАНЫ В РЕЗУЛЬТАТЕ РЕВИЗИИ");
		wOff.setStatus_id(Invoice.STATUS_DONE);
		wOff.setType_id(Invoice.TYPE_WRITEOFF);
		wOff.setUpdated_at(Calendar.getInstance().getTime());
		wOff.setUpdated_by(userId);

		invoiceDao.create(wOff);
		for (InvoiceItem item : wOffItems) {
			item.setInvoice_id(wOff.getId());
			invoiceItemDao.create(item);
		}

		mlService.doWriteoff(wOff, userId);

		List<LogFinClass> postList = new ArrayList<>();
		List<InvoiceItem> postItems = new ArrayList<>();
		for (RevItem item : postingRevItems) {
			LogFinClass lfc = new LogFinClass();
			lfc.setMatnr(item.getMatnr());
			lfc.setMenge(item.getOverQuantity());
			lfc.setTotalDmbtr(item.getOverAmount());

			InvoiceItem ii = new InvoiceItem();
			ii.setBarcode(item.getBarcode());
			ii.setDone_quantity(item.getOverQuantity());
			ii.setMatnr(item.getMatnr());
			ii.setQuantity(item.getOverQuantity());
			postItems.add(ii);
		}

		Long postingAwkey = finServiceLogistics.addMatnrToWerksFound(postList, r.getBukrs(), userId, r.getBranchId(),
				"revision", r.getWerks());

		Invoice post = new Invoice();
		post.setAwkey(postingAwkey);
		post.setBranch_id(r.getBranchId());
		post.setBukrs(r.getBukrs());
		post.setCreated_at(Calendar.getInstance().getTime());
		post.setCreated_by(userId);
		post.setNote("ОПРИХОДОВАНЫ ");

		r.setStatus(Revision.STATUS_FINISHED);
		r.setUpdatedAt(Calendar.getInstance().getTime());
		r.setUpdatedBy(userId);

		revDao.update(r);
	}
}
