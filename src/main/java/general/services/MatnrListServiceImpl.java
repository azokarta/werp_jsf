package general.services;

import general.GeneralUtil;
import general.Validation;
import general.dao.BkpfDao;
import general.dao.BranchDao;
import general.dao.ContractDao;
import general.dao.ContractTypeDao;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.InvoiceItemDao;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.MatnrListSoldDao;
import general.dao.OrderDao;
import general.dao.OrderMatnrDao;
import general.dao.PaymentScheduleDao;
import general.dao.RelatedDocsDao;
import general.dao.SalaryDao;
import general.dao.WerksBranchDao;
import general.dao2.MatnrLimitDao;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Contract;
import general.tables.ContractStatus;
import general.tables.ContractType;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.MatnrListSold;
import general.tables.Order;
import general.tables.OrderMatnr;
import general.tables.PaymentSchedule;
import general.tables.RelatedDocs;
import general.tables.Salary;
import general.tables.Staff;
import general.tables2.MatnrLimit;
import general.tables2.MatnrLimitItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logistics.LogFinClass;

import org.primefaces.util.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.User;

@Service("matnrListService")
public class MatnrListServiceImpl implements MatnrListService {

	@Autowired
	BkpfDao bkpfDao;

	@Autowired
	InvoiceDao inDao;

	@Autowired
	InvoiceItemDao iiDao;

	@Autowired
	MatnrDao mDao;

	@Autowired
	MatnrListDao mlDao;

	@Autowired
	RelatedDocsDao relDao;

	@Autowired
	MatnrListSoldDao mlsDao;

	@Autowired
	PaymentScheduleDao scheduleDao;

	@Autowired
	FinanceServiceLogistics fServiceLogistics;

	@Autowired
	ContractService contractService;

	@Autowired
	ContractDao contractDao;

	@Autowired
	ContractTypeDao conTypeDao;

	@Autowired
	SalaryDao salDao;

	@Autowired
	OrderDao orderDao;

	@Autowired
	OrderMatnrDao orderMatnrDao;

	@Autowired
	MatnrLimitDao matnrLimitDao;
	
	@Autowired
	FinanceServiceDms2 financeServiceDms2;
	
	@Autowired
	ContractService2 contractService2;

	private Bkpf selectedGe;// Good Expence
	private Bkpf selectedTe;
	private Bkpf selectedCe;
	private List<Bkpf> selectedTeList = new ArrayList<Bkpf>();// Transport
																// Expence
	private List<Bkpf> selectedCeList = new ArrayList<Bkpf>();// Custom Expence

	public Bkpf getSelectedGe() {
		return selectedGe;
	}

	public void setSelectedGe(Bkpf selectedGe) {
		this.selectedGe = selectedGe;
	}

	public Bkpf getSelectedTe() {
		return selectedTe;
	}

	public void setSelectedTe(Bkpf selectedTe) {
		this.selectedTe = selectedTe;
	}

	public Bkpf getSelectedCe() {
		return selectedCe;
	}

	public void setSelectedCe(Bkpf selectedCe) {
		this.selectedCe = selectedCe;
	}

	public List<Bkpf> getSelectedTeList() {
		return selectedTeList;
	}

	public void setSelectedTeList(List<Bkpf> selectedTeList) {
		this.selectedTeList = selectedTeList;
	}

	public List<Bkpf> getSelectedCeList() {
		return selectedCeList;
	}

	public void setSelectedCeList(List<Bkpf> selectedCeList) {
		this.selectedCeList = selectedCeList;
	}

	private static final Long customerForByOldMatnr = 152809L;

	@Override
	public void doPosting(Invoice in, User userData) throws DAOException {

		if (userData.getStaff() == null) {
			throw new DAOException("Нет доступа");
		}
		Map<Long, Matnr> matnrMap = mDao.getMappedList("");
		List<InvoiceItem> itemList = iiDao.findAll("invoice_id = " + in.getId());
		if (in.getStatus_id().equals(Invoice.STATUS_DONE)) {
			throw new DAOException("Документ уже оприходован");
		}

		RelatedDocs orderRd = relDao.findParent(in.getId(), Invoice.CONTEXT, Order.CONTEXT);
		if (orderRd == null) {
			throw new DAOException("Заказ не найден!");
		}

		Order order = orderDao.find(orderRd.getContext_id());
		if (order == null) {
			throw new DAOException("Заказ не найден!");
		}

		if (Order.STATUS_CLOSED.equals(order.getStatus_id())) {
			throw new DAOException("Заказ уже закрыт!");
		}

		Map<Long, Double> orderQtyMap = new HashMap<>();
		Map<Long, Double> itemsQtyMap = new HashMap<>();
		List<OrderMatnr> orderAllMatnrs = orderMatnrDao.findAllByOrderId(order.getId());
		for (OrderMatnr om : orderAllMatnrs) {
			itemsQtyMap.put(om.getMatnr(), om.getPosting_quantity());
			orderQtyMap.put(om.getMatnr(), om.getQuantity());
		}

		Map<String, Integer> usedBarcodes = new HashMap<String, Integer>();
		for (InvoiceItem ii : itemList) {
			Matnr m = matnrMap.get(ii.getMatnr());
			if (m == null) {
				throw new DAOException("В справочнике не существует материал с ID: " + ii.getMatnr());
			}

			Integer q = ii.getQuantity().intValue();
			if (q == 0) {
				throw new DAOException(
						String.format("Количество материала %s должно быть больше нуля ", m.getText45()));
			}

			String barcode = "";
			if (m.getType() == 1) {
				barcode = ii.getBarcode();
				if (barcode.length() == 0) {
					throw new DAOException(
							String.format("Заводской номер обязательно для материала %s", m.getText45()));
				}

				if (usedBarcodes.containsKey(barcode)) {
					throw new DAOException("Номер дублировано " + barcode);
				}

				usedBarcodes.put(barcode, 1);

				List<Contract> conList = contractDao
						.dynamicFindContracts(String.format(" tovar_serial='%s' AND contract_status_id NOT IN(%d,%d)",
								barcode, ContractStatus.STATUS_CANCELLED, ContractStatus.STATUS_CLOSED));
				if (conList != null && conList.size() > 0) {
					throw new DAOException("Договор не выполнен и не отменен!");
				}
			}
			if (!orderQtyMap.containsKey(m.getMatnr())) {
				throw new DAOException("В заказе нет материала: " + m.getText45());
			}

			Double orderQty = orderQtyMap.get(m.getMatnr());
			Double itemTempQty = itemsQtyMap.get(m.getMatnr());
			if (itemTempQty == null) {
				itemTempQty = 0D;
			}
			itemTempQty = itemTempQty + ii.getQuantity();

			if (itemTempQty > orderQty) {
				throw new DAOException(String.format("Количество материалов %s больше чем в заказе", m.getText45()));
			}

			itemsQtyMap.put(m.getMatnr(), itemTempQty);

			for (int i = 0; i < q; i++) {
				MatnrList mlTemp = null;
				MatnrList ml = new MatnrList();
				String b = m.getType() == 1 && barcode.length() > 0 ? barcode : null;
				if (b != null) {
					if (customerForByOldMatnr.equals(in.getCustomer_id())) {
						mlTemp = mlDao.findByBarcode(b);
						if (mlTemp != null) {
							if (!m.getMatnr().equals(mlTemp.getMatnr())) {
								throw new DAOException(
										"Зав. номер уже есть в базе, но вид материала другой!!! Ожидалась: "
												+ matnrMap.get(m.getMatnr()).getText45() + ", фактически: "
												+ matnrMap.get(mlTemp.getMatnr()).getText45() + " ("
												+ mlTemp.getBarcode() + ")");
							}

							if (!MatnrList.STATUS_SOLD.equals(mlTemp.getStatus())
									&& !MatnrList.STATUS_RESOLD.equals(mlTemp.getStatus())) {
								throw new DAOException("Зав. номер уже есть в базе. Статус: " + mlTemp.getStatusName());
							}
						}
					} else {
						List<MatnrList> temp = mlDao.findAll(" barcode = '" + b + "' ");
						if (temp.size() > 0) {
							throw new DAOException("На складах уже имеется материал с номером " + b);
						}
					}
				}

				if (mlTemp == null) {
					ml.setBarcode(b);
					ml.setBukrs(in.getBukrs());
					ml.setCreated_by(userData.getUserid());
					ml.setCreated_date(Calendar.getInstance().getTime());
					ml.setCustomer_id(in.getCustomer_id());
					ml.setDmbtr(0D);
					ml.setGjahr(Calendar.getInstance().get(Calendar.YEAR));
					ml.setInvoice(in.getId());
					ml.setMatnr(m.getMatnr());
					ml.setMenge(1D);
					ml.setPosting_id(in.getId());
					ml.setStaff_id(0L);
					ml.setStatus(MatnrList.STATUS_RECEIVED);
					ml.setWerks(in.getTo_werks());
					ml.setMatnr_list_id(null);
					mlDao.create(ml);
				} else {

					mlTemp.setBukrs(in.getBukrs());
					mlTemp.setCustomer_id(0L);
					mlTemp.setDmbtr(0D);
					mlTemp.setGjahr(Calendar.getInstance().get(Calendar.YEAR));
					mlTemp.setInvoice(in.getId());
					// mlTemp.setMatnr(m.getMatnr());
					mlTemp.setMenge(1D);
					mlTemp.setPosting_id(in.getId());
					mlTemp.setStaff_id(0L);
					mlTemp.setStatus(MatnrList.STATUS_RECEIVED);
					mlTemp.setWerks(in.getTo_werks());
					mlTemp.setAwkey(null);
					mlDao.update(mlTemp);
				}
			}
		}
		in.setStatus_id(Invoice.STATUS_DONE);
		inDao.update(in);

		List<OrderMatnr> orderMatnrs = orderMatnrDao.findAllNotPostingByOrderId(order.getId());
		for (OrderMatnr om : orderMatnrs) {
			if (itemsQtyMap.containsKey(om.getMatnr())) {
				om.setPosting_quantity(om.getPosting_quantity() + itemsQtyMap.get(om.getMatnr()));
				orderMatnrDao.update(om);
			}
		}

		List<OrderMatnr> orderMatnrs2 = orderMatnrDao.findAllNotPostingByOrderId(order.getId());
		if (Validation.isEmptyCollection(orderMatnrs2)) {
			order.setStatus_id(Order.STATUS_CLOSED);
			orderDao.update(order);
		}

	}

	@Override
	public void doWriteoff(Invoice in, Long userId) throws DAOException {
		checkForType(in, Invoice.TYPE_WRITEOFF);

		RelatedDocs rd = relDao.find(in.getId(), Invoice.CONTEXT);
		if (rd == null) {
			throw new DAOException("Родительский документ не найден");
		}

		RelatedDocs parentRd = relDao.find(rd.getParent_id());
		if (parentRd == null) {
			throw new DAOException("Родительский документ не найден");
		}

		Invoice parentInvoice = inDao.find(parentRd.getContext_id());
		if (parentInvoice == null) {
			throw new DAOException("Родительский документ не найден");
		}

		if (!isPaidFirstPayment(parentInvoice.getBukrs(), parentInvoice.getAwkey())) {
			throw new DAOException("Первоначальная сумма не оплачена");
		}

		if (parentInvoice.getStatus_id().equals(Invoice.STATUS_DONE)) {
			throw new DAOException("По данному документы материалы уже списаны");
		}

		String tovarSN = "";
		List<MatnrList> wrOffMlList = new ArrayList<MatnrList>();
		List<LogFinClass> lfcList = new ArrayList<LogFinClass>();
		Map<Long, Matnr> matnrMap = mDao.getMappedList("");
		List<InvoiceItem> parentInvoiceItems = iiDao.findAll(" invoice_id = " + parentInvoice.getId());
		Map<Long, InvoiceItem> parentInvoiceItemMap = new HashMap<Long, InvoiceItem>();
		for (InvoiceItem ii : parentInvoiceItems) {
			parentInvoiceItemMap.put(ii.getMatnr(), ii);
		}

		List<InvoiceItem> wItemList = iiDao.findAll(" invoice_id = " + in.getId() + " AND quantity > done_quantity ");// Будут
																														// списаны
		List<Salary> currSal = new ArrayList<>();
		if (!Validation.isEmptyLong(in.getResponsible_id())) {
			currSal = salDao.findAllCurrent(" staff_id = " + in.getResponsible_id());
		}

		for (InvoiceItem ii : wItemList) {
			Matnr m = matnrMap.get(ii.getMatnr());
			if (m == null) {
				throw new DAOException("В справочнике не существует материал с ID: " + ii.getMatnr());
			}

			InvoiceItem parentInvoiceItemTemp = parentInvoiceItemMap.get(m.getMatnr());
			if (parentInvoiceItemTemp == null) {
				throw new DAOException("Такой материал не имеется в документе для списания");
			}

			InvoiceItem newItemForParent = new InvoiceItem();
			BeanUtils.copyProperties(parentInvoiceItemTemp, newItemForParent);

			Integer q = (new Double(ii.getQuantity() - ii.getDone_quantity())).intValue();
			if (q == 0) {
				throw new DAOException(
						String.format("Количество материала %s должно быть больше нуля ", m.getText45()));
			}

			if (q > parentInvoiceItemTemp.getQuantity().intValue()) {
				throw new DAOException("Количество материала больше чем в род. документе, материал: " + m.getText45() + " " + parentInvoiceItemTemp.getQuantity());
			}

			String cond = String.format(" werks = %d AND matnr = %d AND ( awkey IS NOT NULL AND awkey != 0 ) ",
					in.getFrom_werks(), ii.getMatnr());
			if (Validation.isEmptyLong(in.getResponsible_id())) {
				cond += String.format(" AND status IN('%s','%s') AND ( staff_id IS NULL OR staff_id=0 ) ",
						MatnrList.STATUS_RECEIVED, MatnrList.STATUS_RESERVED);
			} else {
				// if (currSal.size() > 0) {
				//
				if (Validation.isEmptyLong(parentInvoice.getFrom_werks())) {
					cond += String.format(" AND status IN('%s','%s','%s') AND staff_id = %d ",
							MatnrList.STATUS_RECEIVED, MatnrList.STATUS_ACCOUNTABILITY, MatnrList.STATUS_RESERVED,
							in.getResponsible_id());
				} else {
					cond += String.format(" AND status = '%s' AND staff_id = %d ", MatnrList.STATUS_RESERVED,
							in.getResponsible_id());
				}
				// } else {
				// cond += String.format(" AND status IN('%s','%s','%s') AND
				// (staff_id = %d OR staff_id = 0) ",
				// MatnrList.STATUS_RECEIVED, MatnrList.STATUS_ACCOUNTABILITY,
				// MatnrList.STATUS_RESERVED,
				// in.getResponsible_id());
				// }
			}

			
			String barcode = "";
			if (m.getType() == 1) {
				barcode = ii.getBarcode();
				if (Validation.isEmptyString(barcode)) {
					throw new DAOException(String.format("Номер обязательно для материала %s", m.getText45()));
				}

				cond += String.format(" AND barcode = '%s' ", barcode);
				tovarSN = barcode;
				parentInvoiceItemTemp.setDone_quantity(1D);
				iiDao.update(parentInvoiceItemTemp);

			} else {
				parentInvoiceItemTemp.setDone_quantity(parentInvoiceItemTemp.getDone_quantity() + ii.getQuantity());
				iiDao.update(parentInvoiceItemTemp);
			}

			cond += " ORDER BY matnr_list_id ";

			List<MatnrList> mlList = mlDao.findAll(cond, q);
			if (q != mlList.size()) {
				if (m.getType() == 1) {
					if (Validation.isEmptyLong(in.getResponsible_id())) {
						throw new DAOException("На складе не найден " + m.getText45() + ", по номеру " + barcode);
					} else {
						throw new DAOException("У подотчетника не имеется " + m.getText45() + ", по номеру " + barcode);
					}

				} else {
					if (Validation.isEmptyLong(in.getResponsible_id()) || currSal.size() == 0) {
						throw new DAOException(
								"На складе не достаточно материала " + m.getText45() + ", максимум " + mlList.size());
					} else {
						throw new DAOException("У подотчетника не достаточно материала " + m.getText45() + ", максимум "
								+ mlList.size());
					}
				}

			}

			List<String> mlIds = new ArrayList<>();
			Double totalDmbtr = 0D;
			for (MatnrList ml : mlList) {
				ml.setStatus(MatnrList.STATUS_SOLD);
				ml.setStaff_id(0L);
				ml.setCustomer_id(in.getCustomer_id());
				mlDao.update(ml);

				MatnrListSold mls = new MatnrListSold(ml.getMatnr_list_id(), ml.getBukrs(),
						Calendar.getInstance().get(Calendar.YEAR), ml.getMatnr(), ml.getWerks(), ml.getBarcode(),
						ml.getDmbtr(), 0L, in.getId(), null, 1D, ml.getStatus(), ml.getCustomer_id(),
						Calendar.getInstance().getTime(), userId, null);
				mlsDao.create(mls);
				totalDmbtr += ml.getDmbtr();
				wrOffMlList.add(ml);
				mlIds.add(ml.getMatnr_list_id().toString());
			}
			ii.setMl_ids(String.join(",", mlIds));
			LogFinClass lfc = new LogFinClass(ii.getMatnr(), ii.getQuantity(), totalDmbtr);
			lfcList.add(lfc);
			
		}

		Long docAwkey;
		if (Validation.isEmptyLong(in.getContract_number())) {
			docAwkey = in.getAwkey();
		} else {
			Contract contr = contractDao.findByContractNumber(in.getContract_number());
			if (contr == null) {
				throw new DAOException("Не найден договор с номером №" + in.getContract_number());
			}

			docAwkey = contr.getAwkey();
			// parentInvoice.setAwkey(docAwkey);
		}

		Long awkey = fServiceLogistics.removeMatnrFromWerks(lfcList, parentInvoice.getAwkey(), in.getBukrs(), userId,
				in.getBranch_id(), "LWRT", in.getFrom_werks());

		in.setContract_number(parentInvoice.getContract_number());
		in.setService_number(parentInvoice.getService_number());
		in.setAwkey(awkey);
		in.setStatus_id(Invoice.STATUS_DONE);
		in.setUpdated_at(Calendar.getInstance().getTime());
		in.setUpdated_by(userId);
		inDao.update(in);

		if (tovarSN.length() > 0) {
			Contract cnt = contractDao.findByTovarSN(tovarSN);
			if (cnt != null) {
				//contractService.forWriteOffAssets(cnt, wrOffMlList, in.getCreated_by());
			} else {
				// throw new DAOException("CONTRACT FOR ASSET EXCEPTION");
			}
		} else {
			
			// throw new DAOException("CONTRACT FOR ASSET EXCEPTION");
		}

		// List<InvoiceItem> parentInItems = iiDao.findAll("invoice_id = "
		// + parentInvoice.getId());

		List<InvoiceItem> tempList = iiDao
				.findAll(" invoice_id = " + parentInvoice.getId() + " AND quantity > done_quantity ");

		if (tempList.size() == 0) {
			parentInvoice.setStatus_id(Invoice.STATUS_DONE);
			parentInvoice.setUpdated_at(Calendar.getInstance().getTime());
			parentInvoice.setUpdated_by(userId);
			// deleteNotClosetWriteoffInvoices(parentInvoice);
		}
		inDao.update(parentInvoice);
		

		// contractService.forWriteOffAssets(contract, matnrList_L, userId);
	}

	private void deleteNotClosetWriteoffInvoices(Invoice closedParentInvoice) throws DAOException {

		// List<Invoice> childsList = inDao.findAll(String.format(
		// " id IN(SELECT context_id FROM RelatedDocs rd WHERE rd.parent_id
		// IN(SELECT id FROM RelatedDocs rd2 WHERE rd2.context_id=%d AND
		// context='%s') AND rd.context='%s') AND type_id = %d AND status_id =
		// %d",
		// closedParentInvoice.getId(), Invoice.CONTEXT, Invoice.CONTEXT,
		// Invoice.TYPE_WRITEOFF,
		// Invoice.STATUS_NEW));
		// String[] ids =
		// relDao.findChildContextIds(closedParentInvoice.getId(),
		// Invoice.CONTEXT, Invoice.CONTEXT);
		// if (ids != null && ids.length > 0) {
		// for (Invoice in : childsList) {
		// RelatedDocs rd = relDao.find(in.getId(), Invoice.CONTEXT);
		// if (rd != null) {
		// relDao.delete(rd.getId());
		// }
		//
		// // List<InvoiceItem> iiList = iiDao.findAll(" invoice_id = " +
		// // in.getId());
		// // for (InvoiceItem ii : iiList) {
		// // iiDao.delete(ii.getId());
		// // }
		//
		// in.setStatus_id(Invoice.STATUS_DELETED);
		// inDao.update(in);
		// }
		// }

	}

	@Override
	public void doSend(Invoice in, User userData) throws DAOException {
		checkForType(in, Invoice.TYPE_SEND);

		if (!in.getStatus_id().equals(Invoice.STATUS_NEW)) {
			throw new DAOException("Документ не подлежит к отправке");
		}

		if (Validation.isEmptyLong(in.getResponsible_id())) {
			throw new DAOException("Не выбран ответственный");
		}

		Map<Long, Matnr> matnrMap = mDao.getMappedList("");
		List<InvoiceItem> itemList = iiDao.findAll("invoice_id = " + in.getId());

		List<LogFinClass> lfcList = new ArrayList<LogFinClass>();

		for (InvoiceItem ii : itemList) {
			Matnr m = matnrMap.get(ii.getMatnr());
			if (m == null) {
				throw new DAOException("В справочнике не существует материал с ID: " + ii.getMatnr());
			}

			Integer q = ii.getQuantity().intValue();
			if (q == 0) {
				throw new DAOException(
						String.format("Количество материала %s должно быть больше нуля ", m.getText45()));
			}

			String cond = String.format(
					" werks = %d AND status = '%s' AND matnr = %d  AND ( awkey IS NOT NULL AND awkey != 0 ) ",
					in.getFrom_werks(), MatnrList.STATUS_RECEIVED, ii.getMatnr());

			Double totalDmbtr = 0D;
			Double totalMenge = 0D;
			String barcode = "";
			if (m.getType() == 1) {
				barcode = ii.getBarcode();
				if (Validation.isEmptyString(barcode)) {
					throw new DAOException("Заводской номер обязательно для материала: " + m.getText45());
				}

				cond += String.format(" AND barcode = '%s' ", barcode);
			}

			List<MatnrList> mlList = mlDao.findAll(cond, q);
			if (q != mlList.size()) {
				if (m.getType() == 1) {
					throw new DAOException("На складе не найден " + m.getText45() + ", с номером: " + barcode);
				} else {
					throw new DAOException(
							"На складе не достаточно материала " + m.getText45() + ", максимум " + mlList.size());
				}

			}

			boolean temp = false; // Временно

			for (MatnrList ml : mlList) {
				ml.setStatus(MatnrList.STATUS_MOVING);
				ml.setStaff_id(in.getResponsible_id());
				mlDao.update(ml);
				if (!ml.getAwkey().equals(999L)) {
					temp = true;
				}
				totalDmbtr += ml.getDmbtr();
				totalMenge++;
			}

			if (temp) {

				LogFinClass lfc = new LogFinClass();
				lfc.setMatnr(m.getMatnr());
				lfc.setMenge(totalMenge);
				lfc.setTotalDmbtr(totalDmbtr);

				lfcList.add(lfc);
			}
		}

		Long awkey = 0L;
		if (lfcList.size() > 0) {
			awkey = fServiceLogistics.moveFromWerks(lfcList, in.getBukrs(), userData.getUserid(), in.getBranch_id(), "",
					in.getFrom_werks());
		}

		in.setStatus_id(Invoice.STATUS_MOVING);
		in.setAwkey(awkey);
		inDao.update(in);

		createPostInFromSend(in, userData);
	}

	@Autowired
	WerksBranchDao wbDao;
	@Autowired
	BranchDao branchDao;
	@Autowired
	InvoiceService inService;
	@Autowired
	InvoiceItemService inItemService;
	@Autowired
	RelatedDocsService relDocService;

	private void createPostInFromSend(Invoice in, User userData) {
		if (!in.getType_id().equals(Invoice.TYPE_SEND)) {
			throw new DAOException("Тип документа должно быть ОТПРАВКА");
		}

		List<Branch> bukrsBranches = branchDao.findByBukrs(in.getBukrs());
		Map<Long, Branch> brMap = new HashMap<Long, Branch>();
		for (Branch br : bukrsBranches) {
			brMap.put(br.getBranch_id(), br);
		}
		List<Branch> brList = wbDao.findAllBranchesByWerks(in.getTo_werks());
		if(Validation.isEmptyCollection(brList)){
			throw new DAOException("Исправьте связку Werks<->Branch");
		}
		
		Branch selectedBr = null;
		for (Branch br : brList) {
			Branch currBr = brMap.get(br.getBranch_id());
			if (currBr != null) {
				if (currBr.getType() == 3) {
					if(!Validation.isEmptyLong(currBr.getTovarCategory())){
						if(Long.valueOf(1).equals(currBr.getTovarCategory())){
							selectedBr = currBr;
							break;
						}else if(Long.valueOf(2).equals(currBr.getTovarCategory())){
							selectedBr = currBr;
						}
					}
				}
			}
		}

		if (selectedBr == null) {
			for (Branch br : brList) {
				Branch currBr = brMap.get(br.getBranch_id());
				if (currBr != null) {
					if (currBr.getType() == 3) {
						selectedBr = currBr;
						break;
					}
				}
			}
		}

		Long centerWerksId = 2L;
		Long mainBranchId = 60L;
		if (selectedBr == null && in.getTo_werks().equals(centerWerksId)) {
			selectedBr = brMap.get(mainBranchId);
		}

		if (selectedBr == null) {
			throw new DAOException("Branch Not Found!");
		}

		Invoice postIn = new Invoice();
		postIn.setBranch_id(selectedBr.getBranch_id());
		postIn.setBukrs(in.getBukrs());
		postIn.setCreated_at(Calendar.getInstance().getTime());
		postIn.setCreated_by(userData.getUserid());
		postIn.setDepartment_id(in.getDepartment_id());
		postIn.setFrom_werks(in.getFrom_werks());
		postIn.setIs_system(1);
		postIn.setNote("");
		postIn.setTo_werks(in.getTo_werks());
		postIn.setResponsible_id(in.getResponsible_id());
		postIn.setStatus_id(Invoice.STATUS_NEW);
		postIn.setUpdated_at(Calendar.getInstance().getTime());
		postIn.setUpdated_by(userData.getUserid());
		postIn.setType_id(Invoice.TYPE_POSTING_IN);
		postIn.setInvoice_date(Calendar.getInstance().getTime());

		inDao.create(postIn);

		Map<Long, Matnr> matnrMap = mDao.getMappedList("");
		List<InvoiceItem> itemList = iiDao.findAll("invoice_id = " + in.getId());
		List<InvoiceItem> postInItems = new ArrayList<InvoiceItem>();
		for (InvoiceItem ii : itemList) {
			Matnr m = matnrMap.get(ii.getMatnr());
			if (m == null) {
				throw new DAOException("В справочнике не существует материал с ID: " + ii.getMatnr());
			}

			InvoiceItem postItem = new InvoiceItem();
			postItem.setBarcode(ii.getBarcode());
			postItem.setInvoice_id(postIn.getId());
			postItem.setMatnr(ii.getMatnr());
			postItem.setMatnrObject(m);
			postItem.setQuantity(ii.getQuantity());
			postInItems.add(postItem);
		}

		inItemService.create(postInItems);
		Map<String, List<Long>> parentDocs = new HashMap<String, List<Long>>();
		List<Long> l = new ArrayList<Long>();
		l.add(in.getId());
		parentDocs.put(Invoice.CONTEXT, l);
		relDocService.addChildToParents(postIn.getId(), Invoice.CONTEXT, parentDocs);
	}

	@Override
	public void doReceive(Invoice in, User userData) throws DAOException {
		checkForType(in, Invoice.TYPE_POSTING_IN);

		if (!in.getStatus_id().equals(Invoice.STATUS_NEW)) {
			throw new DAOException("Документ уже оприходован");
		}

		Invoice parentInvoice = inDao.findParentInvoice(in.getId());
		// List<InvoiceItem> parentInvoiceItems = new ArrayList<InvoiceItem>();
		// if (parentInvoice != null) {
		// parentInvoiceItems = iiDao.findAll(" invoice_id = "
		// + parentInvoice.getId());
		// }

		Map<Long, Matnr> matnrMap = mDao.getMappedList("");
		List<InvoiceItem> itemList = iiDao.findAll("invoice_id = " + in.getId());

		List<LogFinClass> lfcList = new ArrayList<LogFinClass>();

		for (InvoiceItem ii : itemList) {
			Matnr m = matnrMap.get(ii.getMatnr());
			if (m == null) {
				throw new DAOException("В справочнике не существует материал с ID: " + ii.getMatnr());
			}

			Integer q = ii.getQuantity().intValue();
			if (q == 0) {
				throw new DAOException(
						String.format("Количество материала %s должно быть больше нуля ", m.getText45()));
			}

			String cond = String.format(" werks = %d AND status = '%s' AND matnr = %d ", in.getFrom_werks(),
					MatnrList.STATUS_MOVING, ii.getMatnr());

			String barcode = "";
			if (m.getType() == 1) {
				barcode = ii.getBarcode();
				if (Validation.isEmptyString(barcode)) {
					throw new DAOException(String.format("Номер обязательно для материала %s", m.getText45()));
				}

				cond += String.format(" AND barcode = '%s' ", barcode);
			} else {
				cond += " AND staff_id = " + in.getResponsible_id();
			}

			List<MatnrList> mlList = mlDao.findAll(cond, q);
			if (q != mlList.size()) {
				throw new DAOException(getPreparedErrorMsgNotFoundInWerks(m, barcode));
			}

			Double totalMenge = 0D;
			Double totalDmbtr = 0D;
			boolean temp = false; // vremenno
			for (MatnrList ml : mlList) {
				ml.setStatus(MatnrList.STATUS_RECEIVED);
				ml.setStaff_id(0L);
				ml.setWerks(in.getTo_werks());
				mlDao.update(ml);

				if (!ml.getAwkey().equals(999L)) {
					temp = true;
				}

				totalDmbtr += ml.getDmbtr();
				totalMenge++;
			}

			if (temp) {

				LogFinClass lfc = new LogFinClass();
				lfc.setMatnr(m.getMatnr());
				lfc.setMenge(totalMenge);
				lfc.setTotalDmbtr(totalDmbtr);

				lfcList.add(lfc);
			}
		}

		Long awkey = 0L;
		if (lfcList.size() > 0) {
			awkey = fServiceLogistics.movetoWerks(lfcList, in.getBukrs(), userData.getUserid(), in.getBranch_id(), "",
					in.getTo_werks());
		}

		in.setAwkey(awkey);
		in.setStatus_id(Invoice.STATUS_DONE);
		inDao.update(in);

		if (parentInvoice != null) {
			parentInvoice.setStatus_id(Invoice.STATUS_DONE);
			inDao.update(parentInvoice);
		}
	}

	public String convertWithIteration(Map<?, ?> map) {
		StringBuilder mapAsString = new StringBuilder("{");
		for (Object key : map.keySet()) {
			mapAsString.append(key + "=" + map.get(key) + ", ");
		}
		mapAsString.delete(mapAsString.length()-2, mapAsString.length()).append("}");
		return mapAsString.toString();
	}

	@Override
	public void doAccountability(Invoice in, User userData) throws DAOException {
		if (!in.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY)) {
			throw new DAOException("Wrong type of document");
		}

		if (!in.getStatus_id().equals(Invoice.STATUS_NEW)) {
			throw new DAOException("Документ не подлежит к сдаче");
		}

		Map<Long, Matnr> matnrMap = mDao.getMappedList("");
		List<InvoiceItem> itemList = iiDao.findAll("invoice_id = " + in.getId());

		//String stfBukrs = "";
		//Long stfBranchId = null;
		Map<Long, Salary> salPositionsMap = new HashMap<>();
		for (Salary sal : salDao.findAllCurrent("staff_id = " + in.getResponsible_id())) {
			salPositionsMap.put(sal.getPosition_id(), sal);
		}

		List<MatnrLimit> limitList = matnrLimitDao.findAllWithItems(in.getBukrs(), in.getBranch_id(), null);
		Map<Long, Double> matnrLimitMap = new HashMap<>();
		for (MatnrLimit ml : limitList) {
			if (Validation.isEmptyLong(ml.getPositionId())) {
				for (MatnrLimitItem mli : ml.getMatnrLimitItems()) {
					matnrLimitMap.put(mli.getMatnr(), mli.getAccountLimit());
					// matnrMap.get(mli.getMatnr()).setAccount_limit(mli.getAccountLimit());
				}
			} else {
				/*
				 * 1. Берется самый большой лимит среди должностей - 22.11.2019
				 * */
				if (salPositionsMap.containsKey(ml.getPositionId())) {
					for (MatnrLimitItem mli : ml.getMatnrLimitItems()) {
						if(matnrLimitMap.containsKey(mli.getMatnr())){
							if(matnrLimitMap.get(mli.getMatnr()) < mli.getAccountLimit())
								matnrLimitMap.put(mli.getMatnr(), mli.getAccountLimit());
						}else
							matnrLimitMap.put(mli.getMatnr(), mli.getAccountLimit());
						// matnrMap.get(mli.getMatnr()).setAccount_limit(mli.getAccountLimit());
					}
				}
			}
		}

		String tempCond = String.format(" staff_id = %d ", in.getResponsible_id());
		List<MatnrList> stfMatnrsList = mlDao.findAllAccountability(tempCond);
		Map<Long, Double> stfMatnrsMap = new HashMap<>();
		if (stfMatnrsList != null && stfMatnrsList.size() > 0) {
			for (MatnrList ml : stfMatnrsList) {
				stfMatnrsMap.put(ml.getMatnr(), ml.getMenge());
			}
		}

		for (InvoiceItem ii : itemList) {
			Matnr m = matnrMap.get(ii.getMatnr());
			if (m == null) {
				throw new DAOException("В справочнике не существует материал с ID: " + ii.getMatnr());
			}

			//if (stfMatnrsMap.containsKey(m.getMatnr())) {
				Double tempStaffMatnrQty = ii.getQuantity();
				if(stfMatnrsMap.containsKey(m.getMatnr())){
					tempStaffMatnrQty += stfMatnrsMap.get(m.getMatnr());
				}
				
				stfMatnrsMap.put(m.getMatnr(),tempStaffMatnrQty);
				
				Double tempMatnrAccountLimit = m.getAccount_limit();

				if (matnrLimitMap.containsKey(m.getMatnr())) {
						tempMatnrAccountLimit = matnrLimitMap.get(m.getMatnr());
				}

				if (tempStaffMatnrQty > tempMatnrAccountLimit) {
					throw new DAOException(
							"У сотрудника уже имеется в подотчете достаточно материалов:\n " + m.getText45());
				}
			//}

			Integer q = ii.getQuantity().intValue();
			if (q == 0) {
				throw new DAOException(
						String.format("Количество материала %s должно быть больше нуля ", m.getText45()));
			}

			String cond = String.format(
					" (staff_id IS NULL OR staff_id = 0) AND werks = %d AND status = '%s' AND matnr = %d AND ( awkey IS NOT NULL AND awkey != 0 ) ",
					in.getFrom_werks(), MatnrList.STATUS_RECEIVED, ii.getMatnr());

			String barcode = "";
			if (m.getType() == 1) {
				barcode = ii.getBarcode();
				if (Validation.isEmptyString(barcode)) {
					throw new DAOException(String.format("Номер обязательно для материала %s", m.getText45()));
				}

				cond += String.format(" AND barcode = '%s' ", barcode);
			}

			List<MatnrList> mlList = mlDao.findAll(cond, q);

			if (q != mlList.size()) {
				throw new DAOException("На складе отправителя не достаточно материала " + m.getText45() + ", максимум "
						+ mlList.size());
			}

			for (MatnrList ml : mlList) {
				ml.setStatus(MatnrList.STATUS_ACCOUNTABILITY);
				ml.setStaff_id(in.getResponsible_id());
				mlDao.update(ml);
			}
		}

		in.setStatus_id(Invoice.STATUS_DONE);
		inDao.update(in);
	}

	private boolean isPaidFirstPayment(String bukrs, Long awkey) {
		Long belnr = GeneralUtil.getPreparedBelnr(awkey);
		int year = GeneralUtil.getPreparedGjahr(awkey);
		Bkpf b = bkpfDao.findOriginalSingleBkpf(belnr, year, bukrs);
		if (b == null) {
			throw new DAOException("Bkpf Not Found Awkey: " + awkey);
		}
		if (b.getBlart().equals("GC")) {
			List<PaymentSchedule> psList = scheduleDao
					.findAllByAwkeyOrderById(GeneralUtil.getPreparedAwkey(b.getBelnr(), b.getGjahr()), b.getBukrs());
			if (psList.size() == 0) {
				return true;
			}
			PaymentSchedule ps = psList.get(0);
			return ps.getPaid() == ps.getSum();
		} else if (b.getBlart().equals("AK") || "G2".equals(b.getBlart())) {
			return true;
			// Bkpf bkpf2 = bkpfDao.dynamicFindSingleBkpf(String.format(
			// " contract_number = %d AND blart = '%s' AND closed=0 AND
			// storno=0", b.getContract_number(), "GC"));
			// if (bkpf2 != null) {
			// List<PaymentSchedule> psList =
			// scheduleDao.findAllByAwkeyOrderByPaymentDate(
			// GeneralUtil.getPreparedAwkey(bkpf2.getBelnr(),
			// bkpf2.getGjahr()));
			// if (psList.size() == 0) {
			// return true;
			// }
			// PaymentSchedule ps = psList.get(0);
			// return ps.getPaid() == ps.getSum();
			// }
		} else if (b.getBlart().equals("GS")) {
			if (b.getWaers().equals("USD")) {
				return b.getDmbtr_paid() == b.getDmbtr();
			}
			return b.getWrbtr_paid() == b.getWrbtr();
		}
		return false;
	}

	private void checkForType(Invoice in, Integer typeId) {
		if (!in.getType_id().equals(typeId)) {
			throw new DAOException("Wrong type of document");
		}
	}

	@Override
	public void doReturn(Invoice in, User userData) throws DAOException {

		checkForType(in, Invoice.TYPE_RETURN);

		if (!in.getStatus_id().equals(Invoice.STATUS_NEW)) {
			throw new DAOException("Документ не подлежит к возврату");
		}

		if (Validation.isEmptyLong(in.getTo_werks())) {
			throw new DAOException("Склад получатель не указан!");
		}

		Invoice parentInvoice = inDao.findParentInvoice(in.getId());
		if (parentInvoice == null) {
			throw new DAOException("Не найден род. документ!");
		}

		if (!Invoice.STATUS_DONE.equals(parentInvoice.getStatus_id())) {
			throw new DAOException("По род. документу невозможно сделать возврат!");
		}

		if (!Validation.isEmptyLong(parentInvoice.getService_number())) {
			throw new DAOException("Возврат сервиса!");
		}
		
		List<InvoiceItem> parentInvoiceItems = iiDao.findAll("invoice_id = " + parentInvoice.getId());
		//matnr_list_id-шники списанных материалов
		Map<Long, String[]> writtenOffMLIds = new HashMap<>();
		for(InvoiceItem invoiceItem: parentInvoiceItems){
			if(!Validation.isEmptyString(invoiceItem.getMl_ids())){
				if(writtenOffMLIds.containsKey(invoiceItem.getMatnr())){
					writtenOffMLIds.put(invoiceItem.getMatnr(),ArrayUtils.concat(writtenOffMLIds.get(invoiceItem.getMatnr()),invoiceItem.getMl_ids().split(",")));
				} else {
					writtenOffMLIds.put(invoiceItem.getMatnr(),invoiceItem.getMl_ids().split(","));
				}
			}
		}

		Map<Long, Matnr> matnrMap = mDao.getMappedList("");
		List<InvoiceItem> itemList = iiDao.findAll("invoice_id = " + in.getId());

		List<LogFinClass> lfcList = new ArrayList<LogFinClass>();
		List<Long> matnrIdList = new ArrayList<Long>();
		boolean hasApparat = false;
		Long apparatMatnr = null;
		for (InvoiceItem ii : itemList) {
			Matnr m = matnrMap.get(ii.getMatnr());
			if (m == null) {
				throw new DAOException("В справочнике не существует материал с ID: " + ii.getMatnr());
			}

			Integer q = ii.getQuantity().intValue();
			if (q == 0) {
				throw new DAOException(
						String.format("Количество материала %s должно быть больше нуля ", m.getText45()));
			}
			if (!matnrIdList.contains(m.getMatnr())) {
				matnrIdList.add(m.getMatnr());
			}

			String cond = String.format(" matnr = %d  ", ii.getMatnr());

			String barcode = "";
			if (m.getType() == 1) {
				apparatMatnr = m.getMatnr();
				hasApparat = true;
				barcode = ii.getBarcode();
				if (Validation.isEmptyString(barcode)) {
					throw new DAOException(String.format("Номер обязательно для материала %s", m.getText45()));
				}

				cond += String.format(" AND barcode = '%s' ", barcode);
			} else if(writtenOffMLIds.containsKey(ii.getMatnr())) {
				cond += " AND matnr_list_id IN(" + String.join(",", writtenOffMLIds.get(ii.getMatnr())) + ") ";
			}

			List<MatnrList> mlList = mlDao.findAllSold(cond, q);
			Double totalMenge = 0D;
			Double totalDmbtr = 0D;
			for (MatnrList ml : mlList) {
				ml.setStatus(MatnrList.STATUS_RECEIVED);
				ml.setStaff_id(0L);
				ml.setWerks(in.getTo_werks());
				mlDao.update(ml);
				totalMenge += 1D;
				totalDmbtr += ml.getDmbtr();
			}

			LogFinClass lfc = new LogFinClass();
			lfc.setMatnr(m.getMatnr());
			lfc.setMenge(totalMenge);
			lfc.setTotalDmbtr(totalDmbtr);
			lfcList.add(lfc);
		}

		/*********************/
		Bkpf gwBkpf = null; // Списание

		Bkpf bkpf = bkpfDao.findOriginalSingleBkpf(GeneralUtil.getPreparedBelnr(parentInvoice.getAwkey()),
				GeneralUtil.getPreparedGjahr(parentInvoice.getAwkey()), parentInvoice.getBukrs());
		if (bkpf != null) {
			if (bkpf.getBlart().equals("GW")) {
				// документ
				// списание
				gwBkpf = bkpf;

			}

			// else if (bkpf.getBlart().equals("GC")) {// документ
			// // договора
			//
			// } else if (bkpf.getBlart().equals("GS")) {// Документ
			// // сервиса
			//
			// } else if ("AK".equals(bkpf.getBlart())) {
			// // gwBkpf = bkpf;
			// if (!Validation.isEmptyLong(parentInvoice.getContract_number()))
			// {
			// contractService.forContractReturnMatnr(parentInvoice.getContract_number(),
			// matnrIdList, "",
			// userData.getUserid());
			// }
			// }
		}

		if (!Validation.isEmptyLong(parentInvoice.getContract_number())) {
//			contractService.forContractReturnMatnr(parentInvoice.getContract_number(), matnrIdList, "",
//					userData.getUserid());
			//contractService2
			if(hasApparat){
				contractService2.onRemoveContractHistoryMatnr(parentInvoice.getContract_number(), userData.getUserid());
				contractService2.onCancelWriteOffAndReturnContractMatnr(parentInvoice.getContract_number(), userData.getUserid());
			} else {
				contractService2.onRemoveContractHistoryPromo(parentInvoice.getContract_number(),userData.getUserid());
				contractService2.onCancelWriteOffAndReturnContractPromo(parentInvoice.getContract_number());
			}
		}
		


		/*********************/

		if (gwBkpf != null) {
			Long awkey = fServiceLogistics.returnMatnrToWerks(lfcList, gwBkpf, in.getBukrs(), userData.getUserid(),
					in.getBranch_id(), "", in.getTo_werks());
			in.setAwkey(awkey);
		}

		if (parentInvoice != null) {
			parentInvoice.setStatus_id(Invoice.STATUS_ARCHIVE);
			inDao.update(parentInvoice);

			Invoice parentParentInvoice = inDao.findParentInvoice(parentInvoice.getId());
			if (parentParentInvoice != null) {
				parentParentInvoice.setStatus_id(Invoice.STATUS_ARCHIVE);
				inDao.update(parentParentInvoice);
			}
			// @TODO
		}
		
		if(!hasApparat){
			financeServiceDms2.cancelPromoFinanceDoc(in.getContract_number(), userData.getUserid(), "LG_WOFF_DOC");
			financeServiceDms2.cancelHeldFromDealerAccountForPromo(in.getBukrs(), in.getContract_number(), userData.getUserid(), "LG_WOFF_DOC");
		}

		in.setStatus_id(Invoice.STATUS_DONE);
		inDao.update(in);
	}

	// ВОЗВРАТ С ПОДОТЧЕТА
	@Override
	public void doReturnAccountability(Invoice in, User userData) throws DAOException {

		checkForType(in, Invoice.TYPE_ACCOUNTABILITY_RETURN);

		if (!in.getStatus_id().equals(Invoice.STATUS_NEW)) {
			throw new DAOException("Документ не подлежит к возврату");
		}

		Map<Long, Matnr> matnrMap = mDao.getMappedList("");
		List<InvoiceItem> itemList = iiDao.findAll("invoice_id = " + in.getId());

		List<Long> matnrIdList = new ArrayList<Long>();
		for (InvoiceItem ii : itemList) {
			Matnr m = matnrMap.get(ii.getMatnr());
			if (m == null) {
				throw new DAOException("В справочнике не существует материал с ID: " + ii.getMatnr());
			}

			Integer q = ii.getQuantity().intValue();
			if (q == 0) {
				throw new DAOException(
						String.format("Количество материала %s должно быть больше нуля ", m.getText45()));
			}

			if (m.getType() == 1) {
				if (Validation.isEmptyString(ii.getBarcode())) {
					throw new DAOException("Заводской номер обязательно для материала " + m.getText45());
				}
			}

			matnrIdList.add(m.getMatnr());

			String cond = String.format(" matnr = %d AND staff_id = %d AND status = '%s' ", ii.getMatnr(),
					in.getResponsible_id(), MatnrList.STATUS_ACCOUNTABILITY);

			if (m.getType() == 1) {
				cond += String.format(" AND barcode = '%s' ", ii.getBarcode());
			}

			List<MatnrList> mlList = mlDao.findAll(cond, q);
			if (mlList.size() != q) {
				throw new DAOException("У сотрудника в подотчете не достаточно материала " + m.getText45() + " ("
						+ ii.getBarcode() + ") " + ". Максимум: " + mlList.size());
			}

			for (MatnrList ml : mlList) {
				ml.setStatus(MatnrList.STATUS_RECEIVED);
				ml.setStaff_id(0L);
				ml.setWerks(in.getTo_werks());
				mlDao.update(ml);
			}
		}

		/*********************/

		in.setStatus_id(Invoice.STATUS_DONE);
		inDao.update(in);
	}

	@Override
	public void doWriteoffLoss(Invoice in, User userData) throws DAOException {
		checkForType(in, Invoice.TYPE_WRITEOFF_LOSS);

		List<MatnrList> wrOffMlList = new ArrayList<MatnrList>();
		List<LogFinClass> lfcList = new ArrayList<LogFinClass>();
		Map<Long, Matnr> matnrMap = mDao.getMappedList("");

		List<InvoiceItem> wItemList = iiDao.findAll(" invoice_id = " + in.getId());// Будут
																					// списаны
		for (InvoiceItem ii : wItemList) {
			Matnr m = matnrMap.get(ii.getMatnr());
			if (m == null) {
				throw new DAOException(getPreparedErrorMsgNotFoundMatnr(ii.getMatnr()));
			}

			Integer q = ii.getQuantity().intValue();
			if(q > 70){
				throw new DAOException("Максимальное количество для списания 70");
			}
			if (q == 0) {
				throw new DAOException(getPreparedErrorMsgQuantityMustBeMoreThanZero(m));
			}

			String cond = String.format(
					" werks = %d AND status IN('%s') AND matnr = %d AND ( awkey IS NOT NULL AND awkey != 0 ) ",
					in.getFrom_werks(), MatnrList.STATUS_RECEIVED, ii.getMatnr());

			String barcode = "";
			if (m.getType() == 1) {
				barcode = ii.getBarcode();
				if (Validation.isEmptyString(barcode)) {
					throw new DAOException(String.format("Номер обязательно для материала %s", m.getText45()));
				}

				cond += String.format(" AND barcode = '%s' ", barcode);

			} else {

			}

			List<MatnrList> mlList = mlDao.findAll(cond, q);
			if (q != mlList.size()) {
				throw new DAOException(getPreparedErrorMsgNotFoundInWerks(m, barcode));
			}

			List<String> mlIds = new ArrayList<>();
			Double totalDmbtr = 0D;
			for (MatnrList ml : mlList) {
				ml.setStatus(MatnrList.STATUS_LOSS);
				ml.setStaff_id(0L);
				ml.setCustomer_id(in.getCustomer_id());
				mlDao.update(ml);
				totalDmbtr += ml.getDmbtr();
				wrOffMlList.add(ml);
				mlIds.add(ml.getMatnr_list_id().toString());
			}
			LogFinClass lfc = new LogFinClass(ii.getMatnr(), ii.getQuantity(), totalDmbtr);
			lfcList.add(lfc);
			
			ii.setMl_ids(String.join(",", mlIds));
			iiDao.update(ii);
		}

		Long awkey = fServiceLogistics.removeMatnrFromWerksLost(lfcList, in.getBukrs(), userData.getUserid(),
				in.getBranch_id(), "LG_WOFF_LOSS", in.getFrom_werks());

		in.setAwkey(awkey);
		in.setStatus_id(Invoice.STATUS_DONE);
		in.setUpdated_at(Calendar.getInstance().getTime());
		in.setUpdated_by(userData.getUserid());
		inDao.update(in);
	}

	private String getPreparedErrorMsgNotFoundMatnr(Long id) {
		return "В справочнике не существует материал с ID: " + id;
	}

	private String getPreparedErrorMsgQuantityMustBeMoreThanZero(Matnr m) {
		return "Количество материала %s должно быть больше нуля " + m.getText45();
	}

	private String getPreparedErrorMsgNotFoundInWerks(Matnr m, String barcode) {
		if (m.getType() == 1) {
			return "На складе не найден " + m.getText45() + ", по номеру " + barcode;
		} else {
			return "На складе не достаточно материала " + m.getText45();
		}
	}

	@Override
	public void createMlForFix(MatnrList ml) throws DAOException {
		mlDao.create(ml);
	}

	@Override
	public List<MatnrList> findStaffMatnrList(Long staffId) {
		return mlDao.dynamicFind3(String.format("staff_id = %d AND status IN('%s','%s')", staffId,
				MatnrList.STATUS_ACCOUNTABILITY, MatnrList.STATUS_RESERVED));
	}

	@Override
	public void doResold(Contract contract, User userData) throws DAOException {
		// List<MatnrList> matnrLists = mlDao.dynamicFind3(String.format("
		// barcode = '%s' ", contract.getTovar_serial()));
		MatnrList ml = mlDao.findByBarcode(contract.getTovar_serial());
		if (ml != null) {
			if (!MatnrList.STATUS_SOLD.equals(ml.getStatus())) {
				throw new DAOException("Материал не продан!");
			}

			ml.setStatus(MatnrList.STATUS_RESOLD);
			ml.setCustomer_id(0L);
			mlDao.update(ml);
		} else {
			ContractType ct = conTypeDao.find(contract.getContract_type_id());
			if (ct == null) {
				throw new DAOException("Contract Type Not Found!");
			}

			ml = new MatnrList();
			ml.setAwkey(111L);
			ml.setBarcode(contract.getTovar_serial());
			ml.setBukrs(contract.getBukrs());
			ml.setCreated_by(userData.getUserid());
			ml.setCreated_date(Calendar.getInstance().getTime());
			ml.setCustomer_id(0L);
			ml.setDmbtr(0D);
			ml.setGjahr(Calendar.getInstance().get(Calendar.YEAR));
			ml.setInvoice(0L);
			ml.setMatnr(ct.getMatnr());
			ml.setWerks(0L);
			ml.setStaff_id(0L);
			ml.setStatus(MatnrList.STATUS_RESOLD);
			// ml.setPosting_id(-2L);
			mlDao.create(ml);
		}
	}

	@Override
	public void restoreSoldMatnr(Long contractNumber, Long werks, User userData) throws DAOException {
		Contract contract = contractDao.findByContractNumber(contractNumber);
		if (contract == null) {
			throw new DAOException("Контракт не найден!");
		}

		ContractType ct = conTypeDao.find(contract.getContract_type_id());
		if (ct == null) {
			throw new DAOException("Contract Type Not Found!");
		}

		List<Invoice> inList = inDao.findAll(String.format(" contract_number = %d AND type_id IN(%d,%d) ",
				contract.getContract_number(), Invoice.TYPE_WRITEOFF_DOC, Invoice.TYPE_WRITEOFF));
		boolean hasDoc = false;
		if (inList.size() > 0) {
			for (Invoice in : inList) {
				List<InvoiceItem> tempItem = iiDao
						.findAll(" invoice_id = " + in.getId() + " AND barcode='" + contract.getTovar_serial() + "' ");
				if (tempItem.size() > 0) {
					hasDoc = true;
					break;
				}
			}
		}
		if (!hasDoc) {
			List<MatnrList> mlList = mlDao.findAll(" barcode = '" + contract.getTovar_serial() + "' ", 1);
			MatnrList ml = null;
			if (mlList.size() == 0) {
				ml = new MatnrList();
				ml.setAwkey(999999L);
				ml.setBarcode(contract.getTovar_serial());
				ml.setBukrs(contract.getBukrs());
				ml.setCreated_by(userData.getUserid());
				ml.setCreated_date(Calendar.getInstance().getTime());
				ml.setCustomer_id(contract.getCustomer_id());
				ml.setDmbtr(0D);
				ml.setGjahr(Calendar.getInstance().get(Calendar.YEAR));
				ml.setMatnr(ct.getMatnr());
				ml.setMenge(1D);
				ml.setStaff_id(0L);
				ml.setStatus(MatnrList.STATUS_SOLD);
				ml.setWerks(werks);
				mlDao.create(ml);
			} else {
				ml = mlList.get(0);
			}

			Invoice inv1 = new Invoice();
			inv1.setAwkey(contract.getAwkey());
			inv1.setBranch_id(contract.getBranch_id());
			inv1.setBukrs(contract.getBukrs());
			inv1.setContract_number(contract.getContract_number());
			inv1.setCreated_at(Calendar.getInstance().getTime());
			inv1.setCreated_by(userData.getUserid());
			inv1.setCustomer_id(contract.getCustomer_id());
			inv1.setDepartment_id(0L);
			inv1.setInvoice_date(Calendar.getInstance().getTime());
			inv1.setIs_system(1);
			inv1.setNote("СОЗДАНО СИСТЕМОЙ");
			inv1.setStatus_id(Invoice.STATUS_DONE);
			inv1.setType_id(Invoice.TYPE_WRITEOFF_DOC);
			inv1.setUpdated_at(inv1.getCreated_at());
			inv1.setUpdated_by(inv1.getCreated_by());
			inDao.create(inv1);

			List<InvoiceItem> iiList1 = new ArrayList<>();
			InvoiceItem ii1 = new InvoiceItem(inv1.getId(), ct.getMatnr(), 1D, contract.getTovar_serial());
			ii1.setMatnrObject(new Matnr());
			iiList1.add(ii1);
			inItemService.create(iiList1);

			Invoice inv = new Invoice();
			inv.setAwkey(contract.getAwkey());
			inv.setBranch_id(contract.getBranch_id());
			inv.setBukrs(contract.getBukrs());
			inv.setContract_number(contract.getContract_number());
			inv.setCreated_at(Calendar.getInstance().getTime());
			inv.setCreated_by(userData.getUserid());
			inv.setCustomer_id(contract.getCustomer_id());
			inv.setDepartment_id(0L);
			inv.setInvoice_date(Calendar.getInstance().getTime());
			inv.setIs_system(1);
			inv.setNote("СОЗДАНО СИСТЕМОЙ");
			inv.setStatus_id(Invoice.STATUS_DONE);
			inv.setType_id(Invoice.TYPE_WRITEOFF);
			inv.setUpdated_at(inv.getCreated_at());
			inv.setUpdated_by(inv.getCreated_by());
			inv.setFrom_werks(werks);
			inDao.create(inv);

			List<InvoiceItem> iiList = new ArrayList<>();
			InvoiceItem ii = new InvoiceItem(inv.getId(), ct.getMatnr(), 1D, contract.getTovar_serial());
			ii.setMatnrObject(new Matnr());
			iiList.add(ii);
			inItemService.create(iiList);

			Map<String, List<Long>> parentDocs = new HashMap<>();
			List<Long> temp = new ArrayList<>();
			temp.add(inv1.getId());
			parentDocs.put(Invoice.CONTEXT, temp);

			relDocService.addChildToParents(inv.getId(), Invoice.CONTEXT, parentDocs);
			// rdService.addChildToParents(invoiceId, Invoice.CONTEXT,
			// parentDocs);
		} else {
			throw new DAOException("Документы списания по данному контракты уже имееться");
		}
	}

	@Override
	public void onCreateServiceContract(Contract c, User userData) throws DAOException {
		if (Contract.MT_SERVICE.equals(c.getMarkedType())) {
			if (!Validation.isEmptyString(c.getTovar_serial())) {
				MatnrList ml = mlDao.findByBarcode(c.getTovar_serial());
				if (ml == null) {
					return;
					// throw new DAOException(String.format("Материал с зав.
					// номером не %s найден", c.getTovar_serial()));
				}

				if (!MatnrList.STATUS_RESOLD.equals(ml.getStatus()) && !MatnrList.STATUS_LOSS.equals(ml.getStatus())) {
					throw new DAOException("Не правильный статус материала");
				}

				ml.setStatus(MatnrList.STATUS_RESOLD_SERVICE);
				mlDao.update(ml);

			}
		} else {

		}
	}

	@Override
	public void doReserve(Invoice in, User userData) throws DAOException {
		List<InvoiceItem> itemList = iiDao.findAll(" invoice_id = " + in.getId());
		for (InvoiceItem item : itemList) {
			List<MatnrList> l = mlDao
					.findAll(String.format(" werks = %d AND status = '%s' AND staff_id = %d AND barcode='%s' ",
							in.getFrom_werks(), MatnrList.STATUS_ACCOUNTABILITY, in.getResponsible_id(),
							item.getBarcode()), item.getQuantity().intValue());
			if (l == null || l.size() == 0) {
				throw new DAOException("У подотчетника не найден материал с зав. номером: " + item.getBarcode());
			}

			MatnrList ml = l.get(0);
			ml.setStatus(MatnrList.STATUS_MINI_CONTRACT);
			mlDao.update(ml);
		}

		in.setStatus_id(Invoice.STATUS_DONE);
		inDao.update(in);
	}

	@Override
	public void doCancelReserve(Invoice in, Long userId) throws DAOException {
		checkForType(in, Invoice.TYPE_MINI_CONTRACT);
		if (Validation.isEmptyLong(in.getResponsible_id())) {
			throw new DAOException("Подотчетник не выбран");
		}
		List<InvoiceItem> itemList = iiDao.findAll(" invoice_id = " + in.getId());
		if (itemList != null && itemList.size() > 0) {
			InvoiceItem item = itemList.get(0);
			MatnrList ml = mlDao.findByBarcode(item.getBarcode());
			if (ml == null || !in.getFrom_werks().equals(ml.getWerks())) {
				throw new DAOException("Материал не найден на складе");
			}

			if (MatnrList.STATUS_MINI_CONTRACT.equals(ml.getStatus())) {
				if (!in.getResponsible_id().equals(ml.getStaff_id())) {
					throw new DAOException("Невозможно отменить, так как материал у дргугого в подотчете");
				}

				ml.setStatus(MatnrList.STATUS_ACCOUNTABILITY);
				ml.setStaff_id(in.getResponsible_id());
				mlDao.update(ml);

				in.setStatus_id(Invoice.STATUS_ARCHIVE);
				in.setUpdated_at(Calendar.getInstance().getTime());
				in.setUpdated_by(userId);
				inDao.update(in);
			} else {
				throw new DAOException("Невозможно отменить документ, так как у материала изменился СТАТУС");
			}
		}
	}

	@Override
	public void doCancelWriteoffDoc(Invoice in, Long userId) throws DAOException {
		checkForType(in, Invoice.TYPE_WRITEOFF_DOC);
		if (!Invoice.STATUS_NEW.equals(in.getStatus_id())) {
			throw new DAOException("Невозможно отменить документ");
		}

		String ids[] = relDao.findChildContextIds(in.getId(), Invoice.CONTEXT, Invoice.CONTEXT);
		if (ids != null && ids.length > 0) {
			List<Invoice> childInvoices = inDao.findAll(String.format(" id IN(%s) ", String.join(", ", ids)));
			for (Invoice childIn : childInvoices) {
				if (Invoice.STATUS_DONE.equals(childIn.getStatus_id())) {
					throw new DAOException("Не возможно отменить, так как дочерние документы выполнены");
				}

				childIn.setStatus_id(Invoice.STATUS_DELETED);
				inDao.update(childIn);
			}
		}
		List<String> barcodes = new ArrayList<>();
		List<Long> matnrIdList = new ArrayList<>();
		List<InvoiceItem> itemList = iiDao.findAll(" invoice_id = " + in.getId());
		for(InvoiceItem item: itemList){
			matnrIdList.add(item.getMatnr());
			if(!Validation.isEmptyString(item.getBarcode())){
				barcodes.add(item.getBarcode());
			}
		}
		
		if(!Validation.isEmptyLong(in.getContract_number())){
			//Обратно в подотчет
			if(barcodes.size() > 0){
				List<MatnrList> mlList = mlDao.findAll(" barcode in('" + String.join("','", barcodes) + "') " );
				for(MatnrList ml: mlList){
					if(!Validation.isEmptyLong(ml.getStaff_id())){
						ml.setStatus(MatnrList.STATUS_ACCOUNTABILITY);
						mlDao.update(ml);
					}
				}
			} else {
				//Значет Промо
				//throw new DAOException("RR");
				financeServiceDms2.cancelPromoFinanceDoc(in.getContract_number(), userId, "LG_WOFF_DOC");
				financeServiceDms2.cancelHeldFromDealerAccountForPromo(in.getBukrs(), in.getContract_number(), userId, "LG_WOFF_DOC");
			}
			
			//Contract
			contractService.forContractReturnMatnr(in.getContract_number(),matnrIdList,"",userId);
		}
		in.setStatus_id(Invoice.STATUS_ARCHIVE);
		in.setUpdated_at(Calendar.getInstance().getTime());
		in.setUpdated_by(userId);
		inDao.update(in);
	}
}