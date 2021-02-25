package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.Validation;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.WriteoffRepairDao;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.WriteoffRepair;
import general.tables.WriteoffRepairItem;
import logistics.LogFinClass;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("writeoffRepairService")
public class WriteoffRepairServiceImpl implements WriteoffRepairService {

	@Autowired
	WriteoffRepairDao wrDao;
	
	@Autowired
	FinanceServiceLogistics finServiceLogistics;

	@Override
	public void create(WriteoffRepair wr, Long userId) throws DAOException {
		String er = validate(wr, userId, true);
		if (er.length() > 0) {
			throw new DAOException(er);
		}

		wrDao.create(wr);
	}

	private String validate(WriteoffRepair wr, Long userId, boolean isNew) {
		String error = "";
		if (isNew) {
			wr.setCreatedAt(Calendar.getInstance().getTime());
			wr.setCreatedBy(userId);
		}

		if (Validation.isEmptyString(wr.getBukrs())) {
			error += "Выберите компанию \n";
		}

		if (Validation.isEmptyLong(wr.getBranchId())) {
			error += "Выберите филиал \n";
		}

		if (Validation.isEmptyLong(wr.getMasterId())) {
			error += "Выберите мастера \n";
		}

		if (Validation.isEmptyLong(wr.getFromWerks())) {
			error += "Выберите склад \n";
		}

		if (Validation.isEmptyLong(wr.getMatnrListId())) {
			error += "Выберите материал \n";
		}

		if (wr.getWriteoffRepairItems() == null || wr.getWriteoffRepairItems().size() == 0) {
			error += "Выберите зап. часть \n";
		} else {
			for (WriteoffRepairItem item : wr.getWriteoffRepairItems()) {
				if (item.getStateId() == 0) {
					error += "Выберите состояния для всех зап. частей \n";
					break;
				}
			}
		}

		wr.setUpdatedAt(Calendar.getInstance().getTime());
		wr.setUpdatedBy(userId);
		return error;
	}

	@Override
	public void update(WriteoffRepair wr, Long userId) throws DAOException {
		String er = validate(wr, userId, false);
		if (er.length() > 0) {
			throw new DAOException(er);
		}

		wrDao.update(wr);
	}

	@Autowired
	InvoiceService inService;

	@Autowired
	MatnrDao mDao;

	@Autowired
	MatnrListDao mlDao;

	@Override
	public void writeoff(WriteoffRepair wr, Long userId) throws DAOException {
		if (wr.getStatusId() != WriteoffRepair.STATUS_NEW) {
			throw new DAOException("Документ не подлежит к списанию");
		}
		Map<Long, Matnr> matnrMap = mDao.getMappedList("");
		createWriteoffPosting(wr, userId, matnrMap);

		wr.setStatusId(WriteoffRepair.STATUS_DONE);
		wrDao.update(wr);
	}

	@Autowired
	InvoiceDao inDao;

	private void createWriteoffPosting(WriteoffRepair wr, Long userId, Map<Long, Matnr> matnrMap) {
		if (Validation.isEmptyLong(wr.getMasterId())) {
			throw new DAOException("Выберите мастера");
		}
		Invoice in = new Invoice();
		in.setBranch_id(wr.getBranchId());
		in.setBukrs(wr.getBukrs());
		in.setCreated_at(Calendar.getInstance().getTime());
		in.setCreated_by(userId);
		in.setFrom_werks(wr.getFromWerks());
		in.setInvoice_date(wr.getDate());
		in.setIs_system(1);
		in.setNote("ОБНОВЛЕНИЕ МАТЕРИАЛА: (" + wr.getMatnrObject().getCode() + ") " + wr.getMatnrObject().getText45()
				+ ". Зав номер: " + wr.getBarcode());
		in.setStatus_id(Invoice.STATUS_DONE);
		in.setType_id(Invoice.TYPE_WRITEOFF);
		in.setUpdated_at(Calendar.getInstance().getTime());
		in.setUpdated_by(userId);
		in.setDepartment_id(6L);

		Invoice inPosting = new Invoice();
		BeanUtils.copyProperties(in, inPosting);
		inPosting.setType_id(Invoice.TYPE_POSTING);
		inPosting.setFrom_werks(0L);
		inPosting.setTo_werks(wr.getFromWerks());
		
		List<LogFinClass> finItems = new ArrayList<>();

		List<InvoiceItem> inItems = new ArrayList<InvoiceItem>();
		List<InvoiceItem> inPostingItems = new ArrayList<InvoiceItem>();
		for (WriteoffRepairItem wri : wr.getWriteoffRepairItems()) {

			Matnr m = matnrMap.get(wri.getMatnr());
			if (m == null) {
				throw new DAOException("В справочнике не существует материал с ID: " + wri.getMatnr());
			}

			Integer q = wri.getQuantity().intValue();
			if (q == 0) {
				throw new DAOException(
						String.format("Количество материала %s должно быть больше нуля ", m.getText45()));
			}

			String cond = String.format(
					" werks = %d AND status = '%s' AND matnr = %d AND ( awkey IS NOT NULL AND awkey != 0 ) ",
					wr.getFromWerks(), MatnrList.STATUS_ACCOUNTABILITY, wri.getMatnr());

			// cond += " AND (staff_id IS NULL OR staff_id = 0) ";
			cond += " AND staff_id = " + wr.getMasterId();

			List<MatnrList> mlList = mlDao.findAll(cond, q);
			if (q != mlList.size()) {
				throw new DAOException("У мастера не достаточно материала " + m.getText45() + " (" + m.getCode() + ") "
						+ ", максимум " + mlList.size());
			}

			if (wri.getStateId() == MatnrList.STATE_OFF) {
				Double totalDmbtr = 0D;
				for (MatnrList ml : mlList) {
					ml.setStatus(MatnrList.STATUS_SPENT_FOR_REPAIR);
					ml.setStaff_id(0L);
					ml.setCustomer_id(0L);
					// ml.setWerks(0L);
					mlDao.update(ml);
					totalDmbtr += ml.getDmbtr();
				}
				LogFinClass finItem = new LogFinClass(wri.getMatnr(), wri.getQuantity(), totalDmbtr);
				finItems.add(finItem);
			} else {
				for (MatnrList ml : mlList) {
					ml.setState_id(wri.getStateId());
					ml.setBu(1);
					ml.setStaff_id(0L);
					ml.setCustomer_id(0L);
					ml.setStatus(MatnrList.STATUS_RECEIVED);
					mlDao.update(ml);
				}
			}

			InvoiceItem ii = new InvoiceItem(null, wri.getMatnr(), wri.getQuantity(), null);
			ii.setMatnrObject(new Matnr());
			inItems.add(ii);

			if (wri.getStateId() != MatnrList.STATE_OFF) {
				InvoiceItem iiPosting = new InvoiceItem(null, wri.getMatnr(), wri.getQuantity(), null);
				iiPosting.setMatnrObject(new Matnr());
				inPostingItems.add(iiPosting);
			}
		}
		Map<String, List<Long>> parentDocs = new HashMap<String, List<Long>>();
		List<Long> l = new ArrayList<Long>();
		l.add(wr.getId());
		parentDocs.put(WriteoffRepair.CONTEXT, l);
		inService.create(in, inItems, parentDocs, new HashMap<Long, List<String>>(), userId);
		in.setStatus_id(Invoice.STATUS_DONE);
		inDao.update(in);

		if (inPostingItems.size() > 0) {
			inService.create(inPosting, inPostingItems, parentDocs, new HashMap<Long, List<String>>(), userId);
			inPosting.setStatus_id(Invoice.STATUS_DONE);
			inDao.update(inPosting);
		}
		
		if(finItems.size() > 0){
			finServiceLogistics.removeMatnrFromWerksRemont(finItems, wr.getMatnrObject(), wr.getBukrs(), userId, wr.getBranchId(), "LG_WR", wr.getFromWerks(), wr.getBarcode());
		}
		
	}
}
