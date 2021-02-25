package general.services.reports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import general.Helper;
import general.Validation;
import general.dao.ContractDao;
import general.dao.ContractPromosDao;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.InvoiceItemDao;
import general.dao.MatnrDao;
import general.dao.PromotionDao;
import general.dao.StaffDao;
import general.services.ContractService;
import general.tables.Contract;
import general.tables.ContractPromos;
import general.tables.ContractStatus;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Matnr;
import general.tables.Promotion;
import general.tables.Staff;
import general.tables.report.LogReport3;
import general.tables.report.LogReport4;
import general.tables.report.LogReport5;
import general.tables.report.LogReport6;
import reports.logistics.RepLog2;
import reports.logistics.RepLog2.OutputTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("logisticsReportService")
public class LogisticsReportServiceImpl implements LogisticsReportService {

	@Autowired
	InvoiceDao invoiceDao;
	@Autowired
	InvoiceItemDao invoiceItemDao;

	@Autowired
	MatnrDao matnrDao;

	@Autowired
	StaffDao stfDao;

	@Autowired
	ContractDao contractDao;

	@Autowired
	ContractPromosDao contractPromosDao;

	@Autowired
	PromotionDao promotionDao;

	@Override
	public List<OutputTable> getRep2Data(Long branchId, Long werks, Long staffId, Date fromDate, Date toDate,
			String code) {
		Matnr m = matnrDao.findByCode(code);
		if (m == null) {
			throw new DAOException("Не найден материал с кодом: " + code);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String cond1 = " matnr = " + m.getMatnr() + " AND invoice_id IN ";
		cond1 += " ( SELECT id FROM Invoice WHERE type_id = %d AND status_id = %d AND responsible_id = %d AND invoice_date >= '%s' AND from_werks = %d  ";
		if (toDate != null) {
			cond1 += " AND invoice_date <= '" + sdf.format(toDate).toString() + "' ";
		}

		cond1 += ") ";

		List<InvoiceItem> items1 = invoiceItemDao.findAll(String.format(cond1, Invoice.TYPE_ACCOUNTABILITY,
				Invoice.STATUS_DONE, staffId, sdf.format(fromDate).toString(), werks));
		Double qAccount = 0D;
		// System.out.println("COND:: " + items1.size());
		for (InvoiceItem ii : items1) {
			qAccount += ii.getQuantity();
		}

		String cond2 = " matnr = " + m.getMatnr() + " AND invoice_id IN ";
		cond2 += " ( SELECT id FROM Invoice WHERE type_id = %d AND status_id = %d AND responsible_id = %d AND invoice_date >= '%s' AND to_werks = %d ";
		if (toDate != null) {
			cond2 += " AND invoice_date <= '" + sdf.format(toDate).toString() + "' ";
		}

		cond2 += ") ";

		List<InvoiceItem> items2 = invoiceItemDao.findAll(String.format(cond2, Invoice.TYPE_ACCOUNTABILITY_RETURN,
				Invoice.STATUS_DONE, staffId, sdf.format(fromDate).toString(), werks));
		Double qReturned = 0D;
		System.out.println("DD: " + items2.size());
		for (InvoiceItem ii : items2) {
			qReturned += ii.getQuantity();
		}

		String cond3 = " matnr = " + m.getMatnr() + " AND invoice_id IN ";
		cond3 += " ( SELECT id FROM Invoice WHERE type_id = %d AND ( status_id = %d OR status_id = %d ) AND responsible_id = %d AND invoice_date >= '%s' ";
		if (toDate != null) {
			cond3 += " AND invoice_date <= '" + sdf.format(toDate).toString() + "' ";
		}

		cond3 += ") ";

		List<InvoiceItem> items3 = invoiceItemDao.findAll(String.format(cond3, Invoice.TYPE_WRITEOFF_DOC,
				Invoice.STATUS_DONE, Invoice.STATUS_NEW, staffId, sdf.format(fromDate).toString()));
		Double qToWriteoff = 0D;
		System.out.println("DD: " + items3.size());
		for (InvoiceItem ii : items3) {
			qToWriteoff += ii.getQuantity();
		}

		String cond4 = " matnr = " + m.getMatnr() + " AND invoice_id IN ";
		cond4 += " ( SELECT id FROM Invoice WHERE type_id = %d AND status_id = %d AND responsible_id = %d AND invoice_date >= '%s' AND from_werks = %d ";
		if (toDate != null) {
			cond4 += " AND invoice_date <= '" + sdf.format(toDate).toString() + "' ";
		}

		cond4 += ") ";

		List<InvoiceItem> items4 = invoiceItemDao.findAll(String.format(cond4, Invoice.TYPE_WRITEOFF,
				Invoice.STATUS_DONE, staffId, sdf.format(fromDate).toString(), werks));
		Double qWrittenoff = 0D;
		for (InvoiceItem ii : items4) {
			qWrittenoff += ii.getQuantity();
		}

		RepLog2 rp = new RepLog2();

		List<OutputTable> out = new ArrayList<>();
		OutputTable ot = rp.new OutputTable();
		ot.setqAccountability(qAccount);
		ot.setqReturned(qReturned);
		ot.setqToWriteoff(qToWriteoff);
		ot.setqWrittenoff(qWrittenoff);
		out.add(ot);

		return out;
	}

	private Date getPreparedFromDate(Long staffId, Long werks, Date fromDate, Long matnr) {

		// String cond = " responsible_id = %d AND from_werks = %d AND
		// invoice_date >= '%s' "
		// + " AND id IN(SELECT invoice_id FROM InvoiceItem WHERE matnr = %d) "
		// + " ORDER BY invoice_date ASC ";
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//
		// List<Invoice> temp = invoiceDao
		// .findAllLazy(String.format(cond, staffId, werks,
		// sdf.format(fromDate).toString(), matnr), 0, 1);
		// if (temp.size() > 0) {
		// return temp.get(0).getInvoice_date();
		// }
		return fromDate;
	}

	@Override
	public List<LogReport3> getRep3Data(Long werks, Long staffId, Date fromDate, Date toDate, String code) {
		Matnr m = matnrDao.findByCode(code);
		if (m == null) {
			throw new DAOException("В справочнике не найден материал с кодом '" + code + "' ");
		}

		// if (Validation.isEmptyLong(werks)) {
		// throw new DAOException("Выберите склад");
		// }

		if (fromDate == null && toDate == null) {
			throw new DAOException("Выберите Дату с или Дату по");
		}

		if (Validation.isEmptyLong(staffId)) {
			throw new DAOException("Выберите сотрудника");
		}

		if (fromDate != null) {
			fromDate = getPreparedFromDate(staffId, werks, fromDate, m.getMatnr());
		}

		List<LogReport3> out = new ArrayList<>();

		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		cal.add(Calendar.DATE, -1);
		Date toDateForOld = cal.getTime();
		List<InvoiceItem> oldItems = invoiceDao.findLogReport3Data2(werks, staffId, null, toDateForOld, m.getMatnr());
		Double balance = 0D;
		Double accQty = 0D;
		Double returnQty = 0D;
		Double wrOffDocQty = 0D;
		Double wrOffQty = 0D;
		for (InvoiceItem item : oldItems) {
			Invoice invoice = item.getInvoiceObject();
			if (Invoice.STATUS_DONE.equals(invoice.getStatus_id())) {
				if (Invoice.TYPE_ACCOUNTABILITY.equals(invoice.getType_id())) {
					balance += item.getQuantity();
					accQty += item.getQuantity();
				} else if (Invoice.TYPE_WRITEOFF_DOC.equals(invoice.getType_id())) {
					balance -= item.getQuantity();
					wrOffDocQty += item.getQuantity();
				} else if (Invoice.TYPE_ACCOUNTABILITY_RETURN.equals(invoice.getType_id())) {
					balance -= item.getQuantity();
					returnQty += item.getQuantity();
				} else if (Invoice.TYPE_WRITEOFF.equals(invoice.getType_id())) {
					wrOffQty += item.getQuantity();
				}
			} else if (Invoice.STATUS_NEW.equals(invoice.getStatus_id())) {
				if (invoice.getType_id().equals(Invoice.TYPE_WRITEOFF_DOC)) {
					balance -= item.getQuantity();
					wrOffDocQty += item.getQuantity();
				}
			}
		}

		LogReport3 lr3 = new LogReport3();
		lr3.setAccountQuantity(accQty);
		lr3.setReturnFromAccountQuantity(returnQty);
		lr3.setWrOffDocQuantity(wrOffDocQty);
		lr3.setWrOffQuantity(wrOffQty);
		lr3.setBalance(accQty - (returnQty + wrOffDocQty));
		System.out.println("ACC: " + accQty + "; RTRN: " + returnQty + "; WOFF: " + wrOffDocQty);
		out.add(lr3);

		List<InvoiceItem> items = invoiceDao.findLogReport3Data2(werks, staffId, fromDate, toDate, m.getMatnr());
		int counter = 0;
		for (InvoiceItem item : items) {
			Double tempBalance = 0D;
			Invoice invoice = item.getInvoiceObject();
			LogReport3 lr = new LogReport3();
			lr.setId(invoice.getId());
			lr.setInvoice_date(invoice.getInvoice_date());
			lr.setType_id(invoice.getType_id());
			lr.setStatus_id(invoice.getStatus_id());
			lr.setService_number(invoice.getService_number());
			lr.setContract_number(invoice.getContract_number());
			boolean b = false;
			if (Invoice.STATUS_DONE.equals(invoice.getStatus_id())) {
				if (invoice.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY)) {
					lr.setAccountQuantity(item.getQuantity());
					tempBalance += item.getQuantity();
				} else if (lr.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY_RETURN)) {
					lr.setReturnFromAccountQuantity(item.getQuantity());
					tempBalance -= item.getQuantity();
				} else if (lr.getType_id().equals(Invoice.TYPE_WRITEOFF_DOC)) {
					lr.setWrOffDocQuantity(item.getQuantity());
					tempBalance -= item.getQuantity();
				} else if (lr.getType_id().equals(Invoice.TYPE_WRITEOFF)) {
					lr.setWrOffQuantity(item.getQuantity());
				}
				out.add(lr);
				b = true;
			} else if (Invoice.STATUS_NEW.equals(invoice.getStatus_id())) {
				if (lr.getType_id().equals(Invoice.TYPE_WRITEOFF_DOC)) {
					lr.setWrOffDocQuantity(item.getQuantity());
					out.add(lr);
					tempBalance -= item.getQuantity();
					b = true;
				}

			} else if (Invoice.STATUS_ARCHIVE.equals(invoice.getStatus_id())) {
				if(Invoice.TYPE_WRITEOFF.equals(lr.getType_id())){
					lr.setWrOffQuantity(item.getQuantity());
					out.add(lr);
					b = true;
				}else if(Invoice.TYPE_WRITEOFF_DOC.equals(lr.getType_id())){
					lr.setWrOffDocQuantity(item.getQuantity());
					out.add(lr);
					tempBalance -= item.getQuantity();
					b = true;
					//System.out.println("ARCHIVE: ID: " + lr.getId() + "; Count: " + item.getQuantity() );
				}
				// if (lr.getType_id().equals(Invoice.TYPE_WRITEOFF_DOC)) {
				// lr.setWrOffDocQuantity(item.getDone_quantity());
				// } else if (lr.getType_id().equals(Invoice.TYPE_WRITEOFF)) {
				// lr.setWrOffQuantity(item.getQuantity());
				// }
			} else {
				// System.out.println(invoice.getTypeName() + " => ");
			}

			if (b) {
				lr.setBalance(tempBalance + out.get(counter).getBalance());
				counter++;
			}
		}

		return out;

		// List<Object[]> result = invoiceDao.findLogReport3Data(werks, staffId,
		// fromDate, toDate, m.getMatnr());
		// for (Object[] o : result) {
		// LogReport3 lr = new LogReport3();
		// lr.setId(Long.parseLong(String.valueOf(o[0])));
		// lr.setInvoice_date((Date) o[1]);
		// lr.setType_id(Integer.parseInt(String.valueOf(o[2])));
		// lr.setStatus_id(Integer.parseInt(String.valueOf(o[3])));
		// lr.setService_number(Long.parseLong(String.valueOf(o[4])));
		// lr.setContract_number(Long.parseLong(String.valueOf(o[5])));
		// if (lr.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY)) {
		// lr.setAccountQuantity(Double.parseDouble(String.valueOf(o[6])));
		// } else if
		// (lr.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY_RETURN)) {
		// lr.setReturnFromAccountQuantity(Double.parseDouble(String.valueOf(o[6])));
		// } else if (lr.getType_id().equals(Invoice.TYPE_WRITEOFF_DOC)) {
		// lr.setWrOffDocQuantity(Double.parseDouble(String.valueOf(o[6])));
		// } else if (lr.getType_id().equals(Invoice.TYPE_WRITEOFF)) {
		// lr.setWrOffQuantity(Double.parseDouble(String.valueOf(o[6])));
		// }
		// // lr.setItemsQuantity(Double.parseDouble(String.valueOf(o[6])));
		// out.add(lr);
		// }
		//
		// return out;
		// String s = " SELECT id, invoice_date, (SELECT SUM(quantity) FROM
		// invoice_table_item1 WHERE invoice_id= i.id) AS q FROM invoice_table i
		// "
		// + " WHERE id IN(SELECT invoice_id FROM invoice_table_item1 WHERE
		// matnr=%d)" + " AND ( "
		// + " (type_id = %d AND (status_id = %d OR status_id = %d) )" + " OR "
		// + " (type_id = %d AND status_id = %d )" + ")";
		// s = String.format(s, m.getMatnr(), Invoice.TYPE_WRITEOFF_DOC,
		// Invoice.STATUS_DONE, Invoice.STATUS_NEW,
		// Invoice.TYPE_WRITEOFF, Invoice.STATUS_DONE);
		//
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// if (fromDate != null) {
		// s += String.format(" AND invoice_date >= '%s' ",
		// sdf.format(fromDate).toString());
		// }
		//
		// if (toDate != null) {
		// s += String.format(" AND invoice_date <= '%s' ",
		// sdf.format(toDate).toString());
		// }

		// return null;
	}

	@Override
	public Map<Long, HashMap<Long, LogReport4>> getRep4Data(Long werks, Long staffId, Date fromDate, Date toDate,
			String code) {
		Map<Long, Staff> stfMap = stfDao.getMappedList("");
		Map<Long, Matnr> matnrMap = matnrDao.getMappedList("");
		Long matnr = 0L;
		if (!Validation.isEmptyString(code)) {
			Matnr m = matnrDao.findByCode(code);
			if (m == null) {
				throw new DAOException("Не найден материал с кодом: " + code + " \n");
			}

			matnr = m.getMatnr();
		}

		List<Object[]> oList = invoiceDao.findLogReport4Data(werks, staffId, fromDate, toDate, matnr);

		Map<Long, HashMap<Long, LogReport4>> out = new HashMap<>();

		Map<Long, HashMap<Long, LogReport4>> plusMap = new HashMap<>();
		Map<Long, HashMap<Long, LogReport4>> minusMap = new HashMap<>();

		for (Object[] o : oList) {
			Long tempStaffId = Long.parseLong(String.valueOf(o[0]));
			Long tempMatnrId = Long.parseLong(String.valueOf(o[1]));
			String tempBarcode = String.valueOf(o[2]);
			Double tempMenge = Double.parseDouble(String.valueOf(o[3]));
			Integer tempTypeId = Integer.parseInt(String.valueOf(o[4]));
			Matnr matnrObject = matnrMap.get(tempMatnrId);

			HashMap<Long, LogReport4> tempMap = new HashMap<>();

			LogReport4 lr = null;

			if (tempTypeId.equals(Invoice.TYPE_ACCOUNTABILITY)) {
				if (plusMap.containsKey(tempStaffId)) {
					tempMap = plusMap.get(tempStaffId);
					if (tempMap.containsKey(tempMatnrId)) {
						lr = tempMap.get(tempMatnrId);
					}
				}

				if (lr == null) {
					lr = new LogReport4();
				}
				lr.setMenge(lr.getMenge() + tempMenge);
				if (matnrObject.getType() == 1) {
					lr.getBarcodes().add(tempBarcode);
				}

				lr.setMatnrName(matnrObject.getText45());
				lr.setMatnrCode(matnrObject.getCode());
				tempMap.put(tempMatnrId, lr);
				plusMap.put(tempStaffId, tempMap);

			} else {
				if (minusMap.containsKey(tempStaffId)) {
					tempMap = minusMap.get(tempStaffId);
					if (tempMap.containsKey(tempMatnrId)) {
						lr = tempMap.get(tempMatnrId);
					}
				}

				if (lr == null) {
					lr = new LogReport4();
				}
				lr.setMenge(lr.getMenge() + tempMenge);
				if (matnrObject.getType() == 1) {
					lr.getBarcodes().add(tempBarcode);
				}
				lr.setMatnrName(matnrObject.getText45());
				lr.setMatnrCode(matnrObject.getCode());
				tempMap.put(tempMatnrId, lr);
				minusMap.put(tempStaffId, tempMap);
			}
		}

		for (Entry<Long, HashMap<Long, LogReport4>> e1 : plusMap.entrySet()) {
			for (Entry<Long, LogReport4> e2 : e1.getValue().entrySet()) {
				LogReport4 plusLr = e2.getValue();
				LogReport4 minusLr = null;
				if (minusMap.containsKey(e1.getKey())) {
					if (minusMap.get(e1.getKey()).containsKey(e2.getKey())) {
						minusLr = minusMap.get(e1.getKey()).get(e2.getKey());
					}
				}

				if (minusLr != null) {
					plusLr.setMenge(plusLr.getMenge() - minusLr.getMenge());
					for (String s : minusLr.getBarcodes()) {
						plusLr.getBarcodes().remove(s);
					}
				}

				if (plusLr.getMenge() > 0) {

					HashMap<Long, LogReport4> tempMap = new HashMap<>();
					if (out.containsKey(e1.getKey())) {
						tempMap = out.get(e1.getKey());
					}

					tempMap.put(e2.getKey(), plusLr);
					out.put(e1.getKey(), tempMap);
				}
			}
		}

		return out;
	}

	@Override
	public List<LogReport5> getRep5Data(Long contractNumber) {
		Map<Long, Matnr> matnrMap = matnrDao.getMappedList("");

		LogReport5 lp = new LogReport5();
		Contract contract = contractDao.findByContractNumber(contractNumber);
		if (contract == null) {
			throw new DAOException("Договор не найден!");
		}

		lp.setContract(contract);

		List<ContractPromos> conPromosList = contractPromosDao.findAllByContrID(contract.getContract_id());
		if (conPromosList != null && conPromosList.size() > 0) {
			String[] promoIds = new String[conPromosList.size()];
			for (int k = 0; k < conPromosList.size(); k++) {
				promoIds[k] = conPromosList.get(k).getPromo_id().toString();
			}

			List<Promotion> promoList = promotionDao.dynamicFindAll(" id IN(" + String.join(",", promoIds) + ") ");
			lp.setPromotions(new ArrayList<>());
			for (Promotion p : promoList) {
				lp.getPromotions().add(matnrMap.get(p.getMatnr()));
			}
		}

		List<Invoice> inList = invoiceDao.findAll(" contract_number = " + contractNumber + " ORDER BY id ASC ");
		System.out.println("IN LIST SIZE: " + inList.size());
		lp.setInvoiceDocs(inList);
		Map<Long, List<InvoiceItem>> tempMap = new HashMap<>();
		for (Invoice in : inList) {
			List<InvoiceItem> iiList = invoiceItemDao.findAll(" invoice_id = " + in.getId());
			if (iiList.size() > 0) {
				for (InvoiceItem ii : iiList) {
					ii.setMatnrObject(matnrMap.get(ii.getMatnr()));
				}
				tempMap.put(in.getId(), iiList);
			}
		}

		lp.setInvoiceItemsMap(tempMap);
		List<LogReport5> out = new ArrayList<>();
		out.add(lp);
		return out;
	}

	@Override
	public List<LogReport5> getRep6Data(String bukrs, Long branchId, Date fromDate, Date toDate, Long contractNumber) {
		if (!Validation.isEmptyLong(contractNumber)) {
			return getRep5Data(contractNumber);
		}

		Map<Long, Matnr> matnrMap = matnrDao.getMappedList("");
		String cond = "";
		if (!Validation.isEmptyString(bukrs)) {
			cond = " bukrs = '" + bukrs + "' ";
		}

		if (!Validation.isEmptyLong(branchId)) {
			cond += (cond.length() > 0 ? " AND " : " ") + " branch_id = " + branchId;
		}

		if (fromDate == null || toDate == null) {
			throw new DAOException("Выберите все даты");
		}

		if (fromDate != null) {
			cond += (cond.length() > 0 ? " AND " : " ") + " contract_date >= '" + Helper.getFormattedDateForDb(fromDate)
					+ "' ";
		}

		if (toDate != null) {
			cond += (cond.length() > 0 ? " AND " : " ") + " contract_date <= '" + Helper.getFormattedDateForDb(toDate)
					+ "' ";
		}

		cond += String.format(" AND contract_status_id=%d ", ContractStatus.STATUS_STANDARD);
		cond += " ORDER BY contract_date ASC ";

		List<Contract> conList = contractDao.dynamicFindContracts(cond);
		List<LogReport5> out = new ArrayList<>();
		for (Contract contract : conList) {
			LogReport5 lp = new LogReport5();
			lp.setContract(contract);
			List<ContractPromos> conPromosList = contractPromosDao.findAllByContrID(contract.getContract_id());

			if (conPromosList != null && conPromosList.size() > 0) {
				String[] promoIds = new String[conPromosList.size()];
				for (int k = 0; k < conPromosList.size(); k++) {
					promoIds[k] = conPromosList.get(k).getPromo_id().toString();
				}

				List<Promotion> promoList = promotionDao.dynamicFindAll(" id IN(" + String.join(",", promoIds) + ") ");
				lp.setPromotions(new ArrayList<>());
				for (Promotion p : promoList) {
					lp.getPromotions().add(matnrMap.get(p.getMatnr()));
				}

				System.out.println("LP_PROMOTION_SIZE: " + lp.getPromotions().size());
			}

			List<Invoice> inList = invoiceDao.findAll(String.format(
					" contract_number = %d AND ( type_id = %d OR type_id=%d ) AND (status_id=%d OR status_id=%d OR status_id=%d) ORDER BY id ASC",
					contract.getContract_number(), Invoice.TYPE_WRITEOFF, Invoice.TYPE_RETURN, Invoice.STATUS_DONE,
					Invoice.STATUS_CANCELLED, Invoice.STATUS_ARCHIVE));
			lp.setInvoiceDocs(inList);

			Map<Long, List<InvoiceItem>> tempMap = new HashMap<>();
			for (Invoice in : inList) {
				List<InvoiceItem> iiList = invoiceItemDao.findAll(" invoice_id = " + in.getId());
				if (iiList.size() > 0) {
					for (InvoiceItem ii : iiList) {
						ii.setMatnrObject(matnrMap.get(ii.getMatnr()));
					}
					tempMap.put(in.getId(), iiList);
				}
			}
			lp.setInvoiceItemsMap(tempMap);

			out.add(lp);
		}

		return out;
	}

	@Override
	public List<LogReport6> getRep6Data(String bukrs, Long branchId, Date fromDate, Date toDate) {
		Map<Long, Matnr> matnrMap = matnrDao.getMappedList("");
		if (Validation.isEmptyString(bukrs)) {
			throw new DAOException("Выберите компанию");
		}

		if (Validation.isEmptyLong(branchId)) {
			throw new DAOException("Выберите филиал");
		}

		String cond = String.format(" bukrs = '%s' AND branch_id = %d ", bukrs, branchId);

		if (fromDate != null) {
			cond += " AND contract_date >= '" + Helper.getFormattedDateForDb(fromDate) + "' ";
		}

		if (toDate != null) {
			cond += " AND contract_date <= '" + Helper.getFormattedDateForDb(toDate) + "' ";
		}

		cond += String.format(" AND contract_status_id=%d ", ContractStatus.STATUS_STANDARD);
		cond += " ORDER BY contract_date ASC ";

		List<Contract> conList = contractDao.dynamicFindContracts(cond);
		List<LogReport6> out = new ArrayList<>();
		for (Contract contract : conList) {
			LogReport6 lp = new LogReport6();
			lp.setContract(contract);
			List<ContractPromos> conPromosList = contractPromosDao.findAllByContrID(contract.getContract_id());

			if (conPromosList != null && conPromosList.size() > 0) {
				String[] promoIds = new String[conPromosList.size()];
				for (int k = 0; k < conPromosList.size(); k++) {
					promoIds[k] = conPromosList.get(k).getPromo_id().toString();
				}

				List<Promotion> promoList = promotionDao.dynamicFindAll(" id IN(" + String.join(",", promoIds) + ") ");
				lp.setPromotions(new ArrayList<>());
				for (Promotion p : promoList) {
					lp.getPromotions().add(matnrMap.get(p.getMatnr()));
				}
			}

			List<Invoice> inList = invoiceDao.findAll(String.format(
					" contract_number = %d AND ( type_id = %d OR type_id=%d ) AND (status_id=%d OR status_id=%d OR status_id=%d) ORDER BY id ASC",
					contract.getContract_number(), Invoice.TYPE_WRITEOFF, Invoice.TYPE_RETURN, Invoice.STATUS_DONE,
					Invoice.STATUS_CANCELLED, Invoice.STATUS_ARCHIVE));
			lp.setInvoiceDocs(inList);

			Map<Long, List<InvoiceItem>> tempMap = new HashMap<>();
			for (Invoice in : inList) {
				boolean isML = false;
				List<InvoiceItem> iiList = invoiceItemDao.findAll(" invoice_id = " + in.getId());
				for (InvoiceItem ii : iiList) {
					ii.setMatnrObject(matnrMap.get(ii.getMatnr()));
					if (!Validation.isEmptyString(ii.getBarcode())) {
						isML = true;
					}
				}
				tempMap.put(in.getId(), iiList);
				if (isML) {
					lp.getMatnrList().add(in);
				} else {
					lp.getPromotionDocs().add(in);
				}
			}
			lp.setInvoiceItemsMap(tempMap);

			out.add(lp);
		}
		return out;
	}

}