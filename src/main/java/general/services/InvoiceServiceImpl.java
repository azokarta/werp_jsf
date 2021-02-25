package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import logistics.LogFinClass;
import general.GeneralUtil;
import general.Validation;
import general.dao.AccountMatnrStateDao;
import general.dao.BkpfDao;
import general.dao.BranchDao;
import general.dao.ContractDao;
import general.dao.ContractPromosDao;
import general.dao.ContractTypeDao;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.InvoiceItemDao;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.OrderDao;
import general.dao.OrderMatnrDao;
import general.dao.PromotionDao;
import general.dao.RelatedDocsDao;
import general.dao.SalaryDao;
import general.dao.ServiceDao;
import general.dao.WerksBranchDao;
import general.tables.AccountMatnrState;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Contract;
import general.tables.ContractPromos;
import general.tables.ContractType;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.Promotion;
import general.tables.RelatedDocs;
import general.tables.Salary;
import general.tables.ServicePos;
import general.tables.ServiceTable;
import general.tables.Werks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.User;

@Service("invoiceService")
public class InvoiceServiceImpl implements InvoiceService {

	@Autowired
	InvoiceDao inDao;

	@Autowired
	InvoiceItemService iiService;

	@Autowired
	RelatedDocsDao relDDao;

	@Autowired
	MatnrListDao mlsDao;

	@Autowired
	RelatedDocsService rdService;

	@Autowired
	MatnrDao matnrDao;

	@Autowired
	OrderDao orderDao;

	@Autowired
	InvoiceItemDao iiDao;

	@Autowired
	ContractPromosDao cpDao;

	@Autowired
	PromotionDao promDao;

	@Autowired
	BkpfDao bkpfDao;

	@Autowired
	FinanceServiceLogistics fServiceLogistics;

	@Autowired
	WerksBranchDao wbDao;

	@Autowired
	SalaryDao salDao;

	@Autowired
	ServiceDao serDao;

	@Autowired
	ContractDao conDao;

	@Autowired
	MatnrListService matnrListService;

	@Autowired
	OrderMatnrDao orderMatnrDao;
	
	@Autowired
	BranchDao brDao;

	@Override
	public void create(Invoice in, List<InvoiceItem> items, Map<String, List<Long>> parentDocs,
			Map<Long, List<String>> barcodesMap, Long userId) throws DAOException {
		String error = validate(in, userId, true);
		if (items == null || items.size() == 0) {
			error += "Список материалов пуст";
		}

		if (error.length() > 0) {
			throw new DAOException(error);
		}
		in.setId(null);
		setBranchIdForReturnDoc(in);
		setBranchForSendInvoice(in);
		inDao.create(in);
		for (InvoiceItem ii : items) {
			ii.setInvoice_id(in.getId());
		}

		List<InvoiceItem> newItems = new ArrayList<InvoiceItem>();
		prepareItems(items, newItems, barcodesMap);

		iiService.create(newItems);
		if (parentDocs != null) {
			addToParentDocs(in.getId(), parentDocs);
		}
	}
	
	private void setBranchForSendInvoice(Invoice invoice){
		if(!Invoice.TYPE_SEND.equals(invoice.getType_id())){
			return;
		}
		
		List<Branch> bukrsBranches = brDao.findByBukrs(invoice.getBukrs());
		Map<Long, Branch> brMap = new HashMap<Long, Branch>();
		for (Branch br : bukrsBranches) {
			brMap.put(br.getBranch_id(), br);
		}
		List<Branch> brList = wbDao.findAllBranchesByWerks(invoice.getFrom_werks());
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
		if (selectedBr == null && invoice.getFrom_werks().equals(centerWerksId)) {
			selectedBr = brMap.get(mainBranchId);
		}
		
		if(selectedBr == null){
			throw new DAOException("Филиал не найден! Проверьте связку Werks<->Branch");
		}
		
		invoice.setBranch_id(selectedBr.getBranch_id());
	}
	
	private void setBranchIdForReturnDoc(Invoice invoice){
		if(!Invoice.TYPE_RETURN.equals(invoice.getType_id())){
			return;
		}
		
		if(!Validation.isEmptyLong(invoice.getContract_number())){
			Contract contract = conDao.findByContractNumber(invoice.getContract_number());
			if(contract != null){
				invoice.setBukrs(contract.getBukrs());
				invoice.setBranch_id(contract.getBranch_id());
			}
		}
		
	}

	private void prepareItems(List<InvoiceItem> sourceItems, List<InvoiceItem> targetItems,
			Map<Long, List<String>> barcodesMap) {

		for (InvoiceItem ii : sourceItems) {
			List<String> bcodes = null;
			if (barcodesMap != null) {
				bcodes = barcodesMap.get(ii.getMatnr());
			}
			if (bcodes != null && bcodes.size() > 0) {
				for (String bc : bcodes) {
					InvoiceItem iiTemp = new InvoiceItem();
					iiTemp.setInvoice_id(ii.getInvoice_id());
					iiTemp.setMatnr(ii.getMatnr());
					iiTemp.setQuantity(1D);
					iiTemp.setMatnrObject(ii.getMatnrObject());
					iiTemp.setBarcode(bc);
					targetItems.add(iiTemp);
				}

			} else {
				targetItems.add(ii);
			}
		}
	}

	private void addToParentDocs(Long invoiceId, Map<String, List<Long>> parentDocs) {
		rdService.addChildToParents(invoiceId, Invoice.CONTEXT, parentDocs);
	}

	private String validate(Invoice in, Long userId, boolean isNew) {
		String error = "";
		if (Validation.isEmptyString(in.getBukrs())) {
			error += "Выберите компанию" + "\n";
		}

		if (isNew) {
			in.setCreated_at(Calendar.getInstance().getTime());
			in.setCreated_by(userId);
			in.setStatus_id(Invoice.STATUS_NEW);
		}

		in.setUpdated_at(Calendar.getInstance().getTime());
		in.setUpdated_by(userId);

		if (Validation.isEmptyLong(in.getDepartment_id())) {
			error += "Выберите департамент" + "\n";
		}

		if (Validation.isEmptyLong(in.getCustomer_id())) {
			// error += "Выберите поставщика" + "\n";
			in.setCustomer_id(0L);
		}

		if (in.getInvoice_date() == null) {
			error += "Введите дату" + "\n";
		}

		if (in.getType_id().equals(Invoice.TYPE_POSTING)) {
			if (Validation.isEmptyLong(in.getTo_werks())) {
				error += "Выберите на склад" + "\n";
			}

			if (Validation.isEmptyLong(in.getFrom_werks())) {
				in.setFrom_werks(0L);
			}

			if (Validation.isEmptyLong(in.getResponsible_id())) {
				in.setResponsible_id(0L);
			}
		} else if (in.getType_id().equals(Invoice.TYPE_SEND)) {
			if (Validation.isEmptyLong(in.getFrom_werks())) {
				error += "Выберите Склад отправитель" + "\n";
			}

			if (Validation.isEmptyLong(in.getTo_werks())) {
				error += "Выберите Склад получатель" + "\n";
			}

			if (!Validation.isEmptyLong(in.getFrom_werks()) && !Validation.isEmptyLong(in.getTo_werks())) {
				if (in.getFrom_werks().equals(in.getTo_werks())) {
					error += "Склад отправитель и склад получатель не может быть одинаковыми" + "\n";
				}
			}

			if (Validation.isEmptyLong(in.getResponsible_id())) {
				error += "Выберите ответственного сотрудника" + "\n";
			}
		} else if (in.getType_id().equals(Invoice.TYPE_POSTING_IN)) {
			if (Validation.isEmptyLong(in.getTo_werks())) {
				error += "Выберите Склад получатель" + "\n";
			}
		} else if (in.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY)) {
			if (Validation.isEmptyLong(in.getFrom_werks())) {
				error += "Выберите Склад отправитель" + "\n";
			}

			if (Validation.isEmptyLong(in.getBranch_id())) {
				error += "Выберите Филиал" + "\n";
			}

			if (Validation.isEmptyLong(in.getResponsible_id())) {
				error += "Выберите Подотчетника" + "\n";
			}
		} else if (in.getType_id().equals(Invoice.TYPE_WRITEOFF)) {
			if (Validation.isEmptyLong(in.getFrom_werks())) {
				error += "Выберите Склад отправитель" + "\n";
			}
		} else if (in.getType_id().equals(Invoice.TYPE_RETURN)) {
			if (Validation.isEmptyLong(in.getTo_werks())) {
				error += "Выберите Склад получатель" + "\n";
			}
		} else if (in.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY_RETURN)) {
			if (Validation.isEmptyLong(in.getTo_werks())) {
				error += "Выберите Склад получатель" + "\n";
			}
		} else if (Invoice.TYPE_MINI_CONTRACT.equals(in.getType_id())) {
			// if (in.getAmount() == null || in.getAmount() == 0) {
			// error += "Введите сумму договора \n";
			// }

			if (Validation.isEmptyLong(in.getResponsible_id())) {
				error += "Выберите подотчетника \n";
			}

			if (Validation.isEmptyLong(in.getBranch_id())) {
				error += "Выберите филиал \n";
			}
		}

		return error;
	}

	@Override
	public void update(Invoice in, List<InvoiceItem> items, Map<String, List<Long>> parentDocs,
			Map<Long, List<String>> barcodesMap, Long userId) throws DAOException {

		String error = validate(in, userId, false);
		if (items == null || items.size() == 0) {
			error += "Список материалов пуст";
		}

		if (error.length() > 0) {
			throw new DAOException(error);
		}
		setBranchIdForReturnDoc(in);
		setBranchForSendInvoice(in);
		inDao.update(in);
		for (InvoiceItem ii : items) {
			ii.setInvoice_id(in.getId());
		}

		List<InvoiceItem> newItems = new ArrayList<InvoiceItem>();
		prepareItems(items, newItems, barcodesMap);

		iiService.create(newItems);
		if (parentDocs != null) {
			addToParentDocs(in.getId(), parentDocs);
		}
	}

	private Long getPromoAwkey(Long contractNumber) {
		List<Bkpf> l = bkpfDao.dynamicFindBkpf(
				String.format(" contract_number = %d AND blart = 'AK' AND storno = 0 ", contractNumber));
		if (l.size() > 0) {
			return GeneralUtil.getPreparedAwkey(l.get(0).getBelnr(), l.get(0).getGjahr());
		}

		return null;
	}

	

	private String employeeHasMatnrError(Contract contract, String[] wIds, String barcode, List<Promotion> promoList) {
		String error = "";
		Long dealerId = contract.getDealer();
		Map<Long, Double> mengeMap = new HashMap<>();
		Map<Long, Double> writtenOffMenge = new HashMap<>();
		List<Salary> currSalList = salDao.findAllCurrent(" staff_id = " + dealerId);
		// Проверка промоАкции только если сотрудник работает
		if (promoList != null && promoList.size() > 0 && currSalList.size() > 0) {
			for (Promotion p : promoList) {
				Double menge = 0D;
				if (mengeMap.containsKey(p.getMatnr())) {
					menge = mengeMap.get(p.getMatnr());
				} else {
					writtenOffMenge.put(p.getMatnr(),
							inDao.findCountWrittenOffMatnrsFromContract(contract.getContract_number(), p.getMatnr()));
				}
				menge++;
				mengeMap.put(p.getMatnr(), menge);
			}

			for (Entry<Long, Double> e : mengeMap.entrySet()) {
				Double wOffMenge = writtenOffMenge.get(e.getKey());
				if (wOffMenge.equals(e.getValue())) {
					continue;
				} else if (wOffMenge > e.getValue()) {
					// throw
				} else {
					Double d = e.getValue() - wOffMenge;
					String cond = String.format(" staff_id = %d AND matnr = %d AND status='%s' AND werks IN(%s)",
							dealerId, e.getKey(), MatnrList.STATUS_ACCOUNTABILITY, String.join(",", wIds));
					List<MatnrList> temp = mlsDao.findAll(cond, 0);
					if (temp.size() < d) {
						error += " У дилера в подотчете не достаточно материалов с ПромоАкции \n";
					}
				}
			}
		}

		if (!Validation.isEmptyString(barcode)) {
			String cond = String.format(" barcode = '%s' AND staff_id = %d AND status IN('%s','%s') AND werks IN(%s) ",
					barcode, dealerId, MatnrList.STATUS_ACCOUNTABILITY, MatnrList.STATUS_MINI_CONTRACT,
					String.join(",", wIds));
			List<MatnrList> temp = mlsDao.findAll(cond, 1);
			if (temp.size() < 1) {
				error += "У дилера в подотчете не имеется материал с номером " + barcode + " \n";
			}
		}

		return error;
	}

	@Override
	public void createWriteoffDocFromContract(Contract contract, List<Promotion> promList) throws DAOException {

		if (Validation.isEmptyString(contract.getTovar_serial())) {
			throw new DAOException("Номер материала пуст");
		}
		Branch br = brDao.find(contract.getBranch_id());
		if (br == null) {
			throw new DAOException("Филиал не найден!");
		}
		List<Werks> wList = wbDao.findAllWerksByBranch2(br.getParent_branch_id());
		if (wList.size() == 0) {
			throw new DAOException("Не найден склад для филиала");
		}

		String[] wIds = new String[wList.size()];
		for (int k = 0; k < wList.size(); k++) {
			wIds[k] = wList.get(k).getWerks().toString();
		}

		List<MatnrList> tempList = mlsDao.findAll(
				"werks IN(" + String.join(",", wIds) + ") " + " AND barcode = '" + contract.getTovar_serial() + "' ",
				1);
		if (tempList.size() == 0) {
			throw new DAOException(
					String.format("На складах не найден материал с сер. номером %s ", contract.getTovar_serial()));
		}

		if (Contract.MT_STANDARD_PRICE.equals(contract.getMarkedType())) {
			String hasMatnrError = employeeHasMatnrError(contract, wIds, contract.getTovar_serial(), promList);
			if (!Validation.isEmptyString(hasMatnrError)) {
				throw new DAOException(hasMatnrError);
			}
		}

		MatnrList ml = tempList.get(0);
		List<InvoiceItem> iiList = new ArrayList<InvoiceItem>();
		InvoiceItem ii = new InvoiceItem(0L, ml.getMatnr(), 1D, ml.getBarcode());
		iiList.add(ii);

		onChangeContractPromo(contract, promList, contract.getCreated_by());

		Invoice in = new Invoice();
		in.setAwkey(contract.getAwkey());
		in.setBranch_id(contract.getBranch_id());
		in.setBukrs(contract.getBukrs());
		in.setCreated_at(Calendar.getInstance().getTime());
		in.setCreated_by(contract.getCreated_by() == null ? 0L : contract.getCreated_by());
		in.setCustomer_id(contract.getCustomer_id());
		in.setDepartment_id(0L);
		in.setFrom_werks(ml.getWerks());
		in.setInvoice_date(contract.getContract_date());
		in.setResponsible_id(contract.getDealer());
		in.setStatus_id(Invoice.STATUS_NEW);
		in.setTo_werks(0L);
		in.setIs_system(1);
		in.setType_id(Invoice.TYPE_WRITEOFF_DOC);
		in.setUpdated_at(Calendar.getInstance().getTime());
		in.setUpdated_by(contract.getCreated_by() == null ? 0L : contract.getCreated_by());
		in.setNote("ДОГОВОР, №" + contract.getContract_number());
		in.setContract_number(contract.getContract_number());
		inDao.create(in);

		for (InvoiceItem item : iiList) {
			item.setInvoice_id(in.getId());
			item.setMatnrObject(new Matnr());
		}

		iiService.create(iiList);

		List<RelatedDocs> rdList = relDDao
				.findAll(String.format(" context = '%s' AND context_id = %d ", "bkpf", contract.getAwkey()));

		RelatedDocs rd;
		if (rdList.size() > 0) {
			rd = rdList.get(0);
		} else {
			rd = new RelatedDocs();
			rd.setContext("bkpf");
			rd.setContext_id(contract.getAwkey());
			rd.setParent_id(0L);
			rd.setTree_id(contract.getAwkey());
			relDDao.create(rd);
		}
		ml.setStatus(MatnrList.STATUS_RESERVED);
		mlsDao.update(ml);
		rdService.addChild(rd, in.getId(), Invoice.CONTEXT);
	}

	private List<Promotion> getContractPromoList(Long contractId) {
		List<Promotion> contractPromoList = new ArrayList<Promotion>();
		List<ContractPromos> cpList = cpDao.findAllByContrID(contractId);
		if (cpList.size() > 0) {
			String[] promoIds = new String[cpList.size()];
			for (int k = 0; k < cpList.size(); k++) {
				promoIds[k] = cpList.get(k).getPromo_id().toString();
			}

			contractPromoList = promDao
					.dynamicFindAll(String.format(" id IN(%s) ", "'" + String.join("','", promoIds) + "'"));
		}

		return contractPromoList;
	}

	// Аппаратты өзгерткен жағдайда
	@Override
	public void updateWriteoffDocFromContract(Contract contract, List<Promotion> promList) throws DAOException {
		// System.out.println("UPDAATING CONTRACT " +
		// contract.getContract_number());
		if (Validation.isEmptyString(contract.getTovar_serial())) {
			throw new DAOException("Номер материала пуст");
		}

		if (contract.getLast_state() == Contract.LASTSTATE_RETURNED) {
			createWriteoffDocFromContract(contract,
					promList == null ? getContractPromoList(contract.getContract_id()) : promList);
			return;
		} else if (contract.getLast_state() == Contract.LASTSTATE_SIGNED) {
			/*********** Для аппарата **********************/
			List<Invoice> tempInList = inDao.findAll(String.format(" type_id = %d AND status_id = %d AND awkey = %d ",
					Invoice.TYPE_WRITEOFF_DOC, Invoice.STATUS_NEW, contract.getAwkey()));
			if (tempInList.size() == 0) {
				throw new DAOException("Не найден документ списания аппарата!");
			}

			Invoice apparatInvoice = tempInList.get(0);
			List<InvoiceItem> oldItems = iiDao.findAll("invoice_id = " + apparatInvoice.getId());
			InvoiceItem oldItem = null;
			for (InvoiceItem ii : oldItems) {
				if (!Validation.isEmptyString(ii.getBarcode())) {
					oldItem = ii;
					break;
				}
			}

			if (oldItem != null && !contract.getTovar_serial().equals(oldItem.getBarcode())) {
				String cond = String.format(" matnr = %d AND barcode = '%s' AND status = '%s' ", oldItem.getMatnr(),
						oldItem.getBarcode(), MatnrList.STATUS_RESERVED);

				List<MatnrList> mlsList = mlsDao.findAll(cond, 1);
				if (mlsList.size() == 0) {
					throw new DAOException("Не достаточно зарезервированных материалов ");
				}

				for (MatnrList ml : mlsList) {
					ml.setStatus(MatnrList.STATUS_RECEIVED);
					mlsDao.update(ml);
				}

				List<MatnrList> tempList = mlsDao.findAll(" barcode = '" + contract.getTovar_serial() + "' ", 1);
				if (tempList.size() == 0) {
					throw new DAOException(String.format("На складах не найден материал с сер. номером %s ",
							contract.getTovar_serial()));
				}

				MatnrList ml = tempList.get(0);
				List<InvoiceItem> apparatItemList = new ArrayList<InvoiceItem>();
				InvoiceItem iiApparat = new InvoiceItem(apparatInvoice.getId(), ml.getMatnr(), 1D, ml.getBarcode());
				iiApparat.setMatnrObject(new Matnr());
				apparatItemList.add(iiApparat);

				iiService.create(apparatItemList);

				apparatInvoice.setUpdated_at(Calendar.getInstance().getTime());
				apparatInvoice.setUpdated_by(contract.getUpdated_by());
				apparatInvoice.setResponsible_id(contract.getDealer());

				inDao.update(apparatInvoice);
			}

			/********** Для Акции **********************/

			// TODO

		} else if (contract.getLast_state() == Contract.LASTSTATE_GIVEN) {
			return;
		} else {
			throw new DAOException("Не удается создать документ списании");
		}
	}

	@Override
	public void checkInvoicesStatus(Long awkey, List<Long> ids) throws DAOException {
		// for (Long l : ids) {
		// // System.out.println("LLLLL: " + l);
		// }
		if (ids != null && ids.size() > 0) {
			String[] ids2 = new String[ids.size()];
			for (int i = 0; i < ids.size(); i++) {
				ids2[i] = ids.get(i).toString();
			}

			List<RelatedDocs> inRelDocs = relDDao.findAll(String.format(" context = '%s' AND context_id IN(%s) ",
					Invoice.CONTEXT, "'" + String.join("','", ids2) + "'"));
			String[] parentIds = new String[inRelDocs.size()];
			for (int i = 0; i < inRelDocs.size(); i++) {
				parentIds[i] = inRelDocs.get(i).getParent_id().toString();
			}

			if (parentIds.length > 0) {
				Map<Long, Long> tempMap = new HashMap<Long, Long>();
				List<RelatedDocs> orderRlDocs = new ArrayList<RelatedDocs>();
				for (RelatedDocs rd : inRelDocs) {
					RelatedDocs rdTemp = relDDao.find(rd.getParent_id());
					if (rdTemp != null) {
						orderRlDocs.add(rdTemp);
						tempMap.put(rd.getContext_id(), rdTemp.getContext_id());
					}
				}

				// for (Entry<Long, Long> e : tempMap.entrySet()) {
				// if (isPaymentComplete(e.getKey())) {
				// Order o = orderDao.find(e.getValue());
				// if (o != null) {
				// o.setStatus_id(Order.STATUS_CLOSED);
				// orderDao.update(o);
				// }
				// }
				// }

				for (RelatedDocs rd : orderRlDocs) {
					rdService.addChild(rd, awkey, "bkpf");
				}

			}

		}
	}

	private boolean isPaymentComplete(Long invoiceId) {
		List<MatnrList> mlsList = mlsDao
				.findAll(String.format(" invoice = %d AND awkey IS NULL OR awkey = 0 ", invoiceId), 1);

		return mlsList.size() == 0;
	}

	@Override
	public Map<Date, Map<Long, List<InvoiceItem>>> getGeneratedWerksLogData(Long werks, Date fromDate, Date toDate,
			Integer typeId, Long staffId) {
		Map<Date, Map<Long, List<InvoiceItem>>> out = new TreeMap<Date, Map<Long, List<InvoiceItem>>>();
		List<Object[]> tempList = inDao.findWerksLogData(werks, fromDate, toDate, typeId, staffId);
		List<InvoiceItem> itemList = new ArrayList<>();
		for (Object[] o : tempList) {
			InvoiceItem item = (InvoiceItem) o[0];
			item.setInvoiceObject((Invoice) o[1]);
			itemList.add(item);
		}

		if (itemList.size() == 0) {
			return out;
		}

		for (InvoiceItem ii : itemList) {

			Map<Long, List<InvoiceItem>> tempMap = new HashMap<Long, List<InvoiceItem>>();
			List<InvoiceItem> tempItem = new ArrayList<>();
			if (out.containsKey(ii.getInvoiceObject().getInvoice_date())) {
				tempMap = out.get(ii.getInvoiceObject().getInvoice_date());
			}

			if (tempMap.containsKey(ii.getInvoice_id())) {
				tempItem = tempMap.get(ii.getInvoice_id());
			}

			tempItem.add(ii);
			tempMap.put(ii.getInvoice_id(), tempItem);
			out.put(ii.getInvoiceObject().getInvoice_date(), tempMap);
		}

		return out;
	}

	@Override
	public void createWriteoffDocFromService(ServiceTable st, List<ServicePos> spList) throws DAOException {
		if (st.getServ_type() == 2) {
			return;
		}
		List<InvoiceItem> iiList = new ArrayList<InvoiceItem>();
		if (spList != null && spList.size() > 0) {

			Map<Long, Double> tempMap = new HashMap<Long, Double>();
			for (ServicePos sp : spList) {
				if (Validation.isEmptyLong(sp.getMatnr_id())) {
					continue;
				}
				Double menge = 0D;
				if (tempMap.containsKey(sp.getMatnr_id())) {
					menge = tempMap.get(sp.getMatnr_id());
				}
				// menge++;
				menge += sp.getQuantity();
				tempMap.put(sp.getMatnr_id(), menge);
			}

			for (Entry<Long, Double> e : tempMap.entrySet()) {
				InvoiceItem ii2 = new InvoiceItem(0L, e.getKey(), e.getValue(), null);
				ii2.setMatnrObject(new Matnr());
				iiList.add(ii2);
			}
		}

		if (iiList.size() == 0) {
			return;
		}

		Branch br = brDao.find(st.getBranch_id());
		if (br == null) {
			throw new DAOException("Филиал не найден!");
		}
		List<Werks> wList = wbDao.findAllWerksByBranch2(br.getParent_branch_id());
		if (wList.size() == 0) {
			throw new DAOException("Не найден склад для филиала");
		}

		Long fromWerks = wList.get(0).getWerks();

		Invoice in = new Invoice();
		in.setAwkey(st.getAwkey());
		in.setBranch_id(st.getBranch_id());
		in.setBukrs(st.getBukrs());
		in.setCustomer_id(st.getCustomer_id());
		in.setDepartment_id(3L);
		in.setFrom_werks(fromWerks);
		in.setInvoice_date(st.getDate_open());
		in.setResponsible_id(st.getMaster_id());
		in.setStatus_id(Invoice.STATUS_NEW);
		in.setTo_werks(0L);
		in.setIs_system(1);
		in.setType_id(Invoice.TYPE_WRITEOFF_DOC);
		in.setNote(String.format("СЕРВИС КАРТА, №%d", st.getServ_num()));
		in.setService_number(st.getServ_num());

		Map<String, List<Long>> parentDocs = new HashMap<>();
		List<Long> l = new ArrayList<>();
		l.add(st.getAwkey());
		parentDocs.put("bkpf", l);

		create(in, iiList, parentDocs, null, st.getCreated_by());

		Map<Long, Matnr> matnrMap = matnrDao.getMappedList("");

		for (InvoiceItem item : iiList) {
			Matnr m = matnrMap.get(item.getMatnr());
			if (m == null) {
				throw new DAOException("В справочнике не существует материал с ID: " + item.getMatnr());
			}

			Integer q = item.getQuantity().intValue();
			if (q == 0) {
				throw new DAOException(
						String.format("Количество материала %s должно быть больше нуля ", m.getText45()));
			}

			String cond = String.format(
					" werks = %d AND matnr = %d AND ( awkey IS NOT NULL AND awkey != 0 ) AND staff_id=%d AND status='%s' ",
					in.getFrom_werks(), item.getMatnr(), in.getResponsible_id(), MatnrList.STATUS_ACCOUNTABILITY);

			if (m.getType() == 1) {
				throw new DAOException(String.format("Не верный тип материала %s", m.getText45()));
			}

			List<MatnrList> mlList = mlsDao.findAll(cond, q);
			if (q != mlList.size()) {
				throw new DAOException("У мастера не достаточно материала в подотчете " + m.getText45() + ", максимум "
						+ mlList.size());
			}

			for (MatnrList ml : mlList) {
				ml.setStatus(MatnrList.STATUS_RESERVED);
				mlsDao.update(ml);
			}
		}
	}

	private Long getBranchParentWerks(Long branchId) {
		Branch br = brDao.find(branchId);
		if (br == null) {
			throw new DAOException("Филиал не найден!");
		}
		List<Werks> wList = wbDao.findAllWerksByBranch2(br.getParent_branch_id());
		if (wList.size() == 0) {
			throw new DAOException("Не найден склад для филиала");
		}

		return wList.get(0).getWerks();
	}

	@Override
	public void returnWriteoffFromService(ServiceTable oldService, User userData) throws DAOException {
		if (oldService.getServ_type() == 2) {
			return;
		}

		Long userId = userData.getUserid();

		List<Invoice> inWriteOffDocList = inDao.findAll(
				" service_number = " + oldService.getServ_num() + " AND type_id = " + Invoice.TYPE_WRITEOFF_DOC);
		if (inWriteOffDocList.size() == 0) {
			return;
		}
		Invoice inWriteOffDoc = inWriteOffDocList.get(0);
		if (Invoice.STATUS_NEW.equals(inWriteOffDoc.getStatus_id())) {
			if (!Validation.isEmptyLong(inWriteOffDoc.getFrom_werks())) {
				List<InvoiceItem> itemList = iiDao.findAll(String.format(" invoice_id = %d ", inWriteOffDoc.getId()));
				for (InvoiceItem item : itemList) {
					int itemQty = item.getQuantity().intValue();
					String cond = String.format(" matnr = %d AND werks = %d AND status = '%s' AND staff_id = %d ",
							item.getMatnr(), inWriteOffDoc.getFrom_werks(), MatnrList.STATUS_RESERVED,
							inWriteOffDoc.getResponsible_id());

					List<MatnrList> mlList = mlsDao.findAll(cond, itemQty);
					if (mlList.size() < itemQty) {
						throw new DAOException("Не достаточно материалов для возврата. Обратитесь к администратору");
					}

					for (MatnrList ml : mlList) {
						ml.setStatus(MatnrList.STATUS_ACCOUNTABILITY);
						mlsDao.update(ml);
					}
				}
			}

			List<Invoice> inTempList = inDao
					.findAll(String.format(" status_id = %d AND type_id = %d AND service_number = %d ",
							Invoice.STATUS_NEW, Invoice.TYPE_WRITEOFF, oldService.getServ_num()));
			for (Invoice in : inTempList) {
				cancelInvoice(in, userId);
			}

		} else if (Invoice.STATUS_DONE.equals(inWriteOffDoc.getStatus_id())) {
			List<Invoice> inTempList = inDao
					.findAll(String.format(" status_id = %d AND type_id = %d AND service_number = %d ",
							Invoice.STATUS_DONE, Invoice.TYPE_WRITEOFF, oldService.getServ_num()));
			for (Invoice in : inTempList) {
				List<LogFinClass> lfcList = new ArrayList<LogFinClass>();
				List<InvoiceItem> itemList = iiDao.findAll(String.format(" invoice_id = %d ", in.getId()));
				for (InvoiceItem item : itemList) {
					int itemQty = item.getQuantity().intValue();
					String cond = String.format(" matnr = %d AND werks = %d ", item.getMatnr(), in.getFrom_werks());
					if(!Validation.isEmptyString(item.getMl_ids())){
						cond += " AND matnr_list_id IN(" + String.join(",", item.getMlIdsAsArray()) + ") ";
					}

					List<MatnrList> mlList = mlsDao.findAllSold(cond, itemQty);
					if (mlList.size() < itemQty) {
						throw new DAOException("Не достаточно материалов для возврата. Обратитесь к администратору");
					}

					Double totalMenge = 0D;
					Double totalDmbtr = 0D;

					for (MatnrList ml : mlList) {
						ml.setStatus(MatnrList.STATUS_ACCOUNTABILITY);
						ml.setStaff_id(in.getResponsible_id());
						mlsDao.update(ml);

						totalMenge += ml.getMenge();
						totalDmbtr += ml.getDmbtr();
					}

					LogFinClass lfc = new LogFinClass();
					lfc.setMatnr(item.getMatnr());
					lfc.setMenge(totalMenge);
					lfc.setTotalDmbtr(totalDmbtr);
					lfcList.add(lfc);
				}

				Bkpf bkpf = bkpfDao.findOriginalSingleBkpf(GeneralUtil.getPreparedBelnr(in.getAwkey()),
						GeneralUtil.getPreparedGjahr(in.getAwkey()), in.getBukrs());
				if (bkpf != null) {
					fServiceLogistics.returnMatnrToWerks(lfcList, bkpf, in.getBukrs(), userData.getUserid(),
							in.getBranch_id(), "", in.getFrom_werks());
				}

				cancelInvoice(in, userId);
			}
		}

		cancelInvoice(inWriteOffDoc, userId);
	}

	private void cancelInvoice(Invoice in, Long userId) {
		in.setUpdated_at(Calendar.getInstance().getTime());
		in.setUpdated_by(userId);
		in.setStatus_id(Invoice.STATUS_CANCELLED);
		inDao.update(in);
	}

	@Autowired
	AccountMatnrStateDao amsDao;

	@Override
	public void saveAms(Invoice in, List<AccountMatnrState> amsList, User userData) throws DAOException {
		String error = "";
		if (amsList.size() == 0) {
			error += "Список зап. частей пусты";
		}
		for (AccountMatnrState ams : amsList) {
			System.out.println("DD: " + ams.getNote());
			if (Validation.isEmptyString(ams.getNote())) {
				error += "Введтие примечание";
			}

			if (Validation.isEmptyLong(ams.getMatnr())) {
				error += "Не выбран материал справочник";
			}

			if (Validation.isEmptyLong(ams.getMlsMatnrId())) {
				error += "Не выбран материал";
			}

			ams.setInvoiceId(in.getId());
			ams.setId(null);
		}

		if (!Validation.isEmptyString(error)) {
			throw new DAOException(error);
		}

		List<AccountMatnrState> oldList = amsDao.findAll(" invoice_id = " + in.getId());
		for (AccountMatnrState ams : oldList) {
			amsDao.delete(ams.getId());
		}

		for (AccountMatnrState ams : amsList) {
			ams.setCreatedAt(Calendar.getInstance().getTime());
			ams.setUpdatedAt(Calendar.getInstance().getTime());
			ams.setCreatedBy(userData.getUserid());
			ams.setUpdatedBy(userData.getUserid());
			amsDao.create(ams);
		}
	}

	@Override
	public void updateInvoicesForFix() throws DAOException {
		List<Invoice> l = inDao.findAll(String.format(
				" type_id = %d AND status_id = %d AND contract_number IS NOT NULL AND contract_number > 0 AND bukrs = '1000' ",
				Invoice.TYPE_WRITEOFF_DOC, Invoice.STATUS_NEW));
		for (Invoice in : l) {
			List<InvoiceItem> items = iiDao.findAll(" (barcode IS NULL) AND invoice_id = " + in.getId());
			if (items.size() > 0) {
				Long promoAwkey = getPromoAwkey(in.getContract_number());
				Invoice inPromo = new Invoice();
				inPromo.setAwkey(promoAwkey);
				inPromo.setBranch_id(in.getBranch_id());
				inPromo.setBukrs(in.getBukrs());
				inPromo.setCreated_at(in.getCreated_at());
				inPromo.setCreated_by(in.getCreated_by());
				inPromo.setCustomer_id(in.getCustomer_id());
				inPromo.setDepartment_id(0L);
				inPromo.setFrom_werks(in.getFrom_werks());
				inPromo.setInvoice_date(in.getInvoice_date());
				inPromo.setResponsible_id(in.getResponsible_id());
				inPromo.setStatus_id(Invoice.STATUS_NEW);
				inPromo.setTo_werks(0L);
				inPromo.setIs_system(1);
				inPromo.setType_id(Invoice.TYPE_WRITEOFF_DOC);
				inPromo.setUpdated_at(Calendar.getInstance().getTime());
				inPromo.setUpdated_by(1L);
				inPromo.setNote("ДОГОВОР, №" + in.getContract_number());
				inPromo.setContract_number(in.getContract_number());
				inDao.create(inPromo);

				for (InvoiceItem item : items) {
					item.setInvoice_id(inPromo.getId());
					iiDao.update(item);
				}

				if (promoAwkey != null) {
					List<RelatedDocs> rdList = relDDao
							.findAll(String.format(" context = '%s' AND context_id = %d ", "bkpf", promoAwkey));

					RelatedDocs rd;
					if (rdList.size() > 0) {
						rd = rdList.get(0);
					} else {
						rd = new RelatedDocs();
						rd.setContext("bkpf");
						rd.setContext_id(promoAwkey);
						rd.setParent_id(0L);
						rd.setTree_id(promoAwkey);
						relDDao.create(rd);
					}

					rdService.addChild(rd, inPromo.getId(), Invoice.CONTEXT);
				}
				// System.out.println("DDD: " + in.getId() + " => " +
				// items.size());
			}
		}

		// System.out.println("SIZE: " + l.size());
	}

	// @Autowired
	// ContractDao cDao;
	//
	// @Override
	// public void updateContractForFix(Contract c) throws DAOException {
	// cDao.update(c);
	// }
	//

	@Override
	public void updateInvoicesForFix2() throws DAOException {
		// List<Invoice> inList = inDao
		// .findAll("( responsible_id = 0 OR responsible_id IS NULL ) AND
		// (contract_number > 0 OR service_number > 0) AND bukrs=1000 AND
		// type_id = "
		// + Invoice.TYPE_WRITEOFF_DOC
		// + " AND status_id = "
		// + Invoice.STATUS_NEW);
		// System.out.println("SIZE: " + inList.size());
		// int i = 0;
		// for (Invoice in : inList) {
		// if (!Validation.isEmptyLong(in.getContract_number())) {
		// Contract con = conDao.findByContractNumber(in
		// .getContract_number());
		// if (con != null) {
		// in.setResponsible_id(con.getDealer());
		// inDao.update(in);
		// System.out.println("UPDATED: " + i);
		// i++;
		// }
		// } else if (!Validation.isEmptyLong(in.getService_number())) {
		// ServiceTable st = serDao.findByNumber(in.getService_number());
		// if (st != null) {
		// in.setResponsible_id(st.getMaster_id());
		// inDao.update(in);
		// System.out.println("UPDATED: " + i);
		// i++;
		// }
		// }
		// }
	}

	@Autowired
	ContractTypeDao conTypeDao;

	@Override
	public void createReturnDocFromContract(Contract con, User userData) throws DAOException {
		List<Invoice> parentInList = new ArrayList<>();
		boolean createReturnDoc = false;
		if (con.getTovar_serial() == null)
			return;
		MatnrList ml = mlsDao.findByBarcode(con.getTovar_serial());
		if (ml == null) {
			ContractType ct = conTypeDao.find(con.getContract_type_id());
			ml = new MatnrList();
			ml.setAwkey(999L);
			ml.setBarcode(con.getTovar_serial());
			ml.setBu(0);
			ml.setBukrs(con.getBukrs());
			ml.setCreated_by(1L);
			ml.setCreated_date(Calendar.getInstance().getTime());
			ml.setDmbtr(0D);
			ml.setGjahr(Calendar.getInstance().get(Calendar.YEAR));
			ml.setMatnr(ct.getMatnr());
			ml.setMenge(1D);
			ml.setPosting_id(-1L);
			ml.setStatus(MatnrList.STATUS_SOLD);
			ml.setCustomer_id(con.getCustomer_id());
			ml.setWerks(0L);
			ml.setStaff_id(0L);
			mlsDao.create(ml);

			Invoice inWriteoff = new Invoice();
			inWriteoff.setAwkey(con.getAwkey());
			inWriteoff.setBranch_id(con.getBranch_id());
			inWriteoff.setBukrs(con.getBukrs());
			inWriteoff.setContract_number(con.getContract_number());
			inWriteoff.setCreated_at(Calendar.getInstance().getTime());
			inWriteoff.setCreated_by(1L);
			inWriteoff.setUpdated_at(Calendar.getInstance().getTime());
			inWriteoff.setUpdated_by(1L);
			inWriteoff.setDepartment_id(0L);
			inWriteoff.setTo_werks(0L);
			inWriteoff.setFrom_werks(0L);
			inWriteoff.setInvoice_date(Calendar.getInstance().getTime());
			inWriteoff.setIs_system(1);
			inWriteoff.setType_id(Invoice.TYPE_WRITEOFF);
			inWriteoff.setStatus_id(Invoice.STATUS_DONE);
			inWriteoff.setNote("СПИСАНИЕ СОЗДАНО СИСТЕМОЙ ДЛЯ СТАРЫХ ПРОДАЖ");
			inDao.create(inWriteoff);

			List<InvoiceItem> iiWrList = new ArrayList<InvoiceItem>();
			InvoiceItem iiWr = new InvoiceItem(inWriteoff.getId(), ml.getMatnr(), 1D, con.getTovar_serial());
			iiWr.setDone_quantity(1D);
			iiWr.setMatnrObject(new Matnr());
			iiWrList.add(iiWr);
			iiService.create(iiWrList);

			parentInList.add(inWriteoff);
			createReturnDoc = true;
		} else {
			List<Invoice> tempInList = inDao
					.findAll(String.format(" type_id = %d AND status_id = %d AND contract_number = %d",
							Invoice.TYPE_WRITEOFF_DOC, Invoice.STATUS_NEW, con.getContract_number()));
			// @TODO удалить созданные списания
			for (Invoice inTemp : tempInList) {
				inTemp.setStatus_id(Invoice.STATUS_CANCELLED);
				inDao.update(inTemp);
			}

			if (ml.getStatus().equals(MatnrList.STATUS_RESERVED)) {
				if (Validation.isEmptyLong(ml.getStaff_id())) {
					ml.setStatus(MatnrList.STATUS_RECEIVED);
				} else {
					ml.setStatus(MatnrList.STATUS_ACCOUNTABILITY);
				}

				mlsDao.update(ml);
				createReturnDoc = false;
			} else if (ml.getStatus().equals(MatnrList.STATUS_SOLD)) {
				List<Invoice> tempInList2 = inDao.findAll(String.format(
						" id IN( SELECT invoice_id FROM InvoiceItem WHERE barcode='%s') AND type_id = %d AND status_id = %d",
						ml.getBarcode(), Invoice.TYPE_WRITEOFF_DOC, Invoice.STATUS_DONE));
				if (tempInList2.size() > 0) {
					Invoice inTemp = tempInList2.get(0);
					inTemp.setStatus_id(Invoice.STATUS_CANCELLED);
					inDao.update(inTemp);
					// parentInList.add(inTemp);
				}

				List<Invoice> tempInList3 = inDao.findAll(String.format(
						" id IN( SELECT invoice_id FROM InvoiceItem WHERE barcode='%s') AND type_id = %d AND status_id = %d",
						ml.getBarcode(), Invoice.TYPE_WRITEOFF, Invoice.STATUS_DONE));
				if (tempInList3.size() > 0) {
					parentInList.add(tempInList3.get(0));
				}

				createReturnDoc = true;
			} else {
				// throw new DAOException("Unkown status of matnr!!!");
				return;
			}
		}

		if (createReturnDoc) {
			Invoice in = new Invoice();
			in.setAwkey(con.getAwkey());
			in.setBranch_id(con.getBranch_id());
			in.setBukrs(con.getBukrs());
			in.setContract_number(con.getContract_number());
			in.setCreated_at(Calendar.getInstance().getTime());
			in.setCreated_by(userData.getUserid());
			in.setCustomer_id(con.getCustomer_id());
			in.setNote("ВОЗВРАТ ДОГОВОРА №" + con.getContract_number());
			in.setStatus_id(Invoice.STATUS_NEW);
			in.setType_id(Invoice.TYPE_RETURN);
			in.setUpdated_at(in.getCreated_at());
			in.setUpdated_by(in.getCreated_by());
			in.setInvoice_date(Calendar.getInstance().getTime());
			in.setDepartment_id(0L);
			in.setFrom_werks(0L);
			in.setTo_werks(0L);
			inDao.create(in);

			InvoiceItem ii = new InvoiceItem(in.getId(), ml.getMatnr(), 1D, con.getTovar_serial());
			ii.setMatnrObject(new Matnr());
			List<InvoiceItem> items = new ArrayList<InvoiceItem>();
			items.add(ii);
			iiService.create(items);

			if (parentInList.size() > 0) {
				Map<String, List<Long>> parentDocs = new HashMap<String, List<Long>>();
				List<Long> temp = new ArrayList<Long>();
				temp.add(parentInList.get(0).getId());
				parentDocs.put(Invoice.CONTEXT, temp);
				addToParentDocs(in.getId(), parentDocs);
			}
		}

		System.out.println("... DONE createReturnDocFromContract ....");
	}

	@Override
	public List<InvoiceItem> findStaffItems(Long staffId) {
		return iiDao.findStaffItems(staffId);
	}

	@Override
	public void changedContractAwkey(Contract con) {
		List<Invoice> inList = inDao.findAll(String.format(
				" id IN(SELECT invoice_id FROM InvoiceItem WHERE barcode='%s') AND type_id = %d AND status_id = %d AND contract_number=%d ",
				con.getTovar_serial(), Invoice.TYPE_WRITEOFF_DOC, Invoice.STATUS_NEW, con.getContract_number()));
		if (inList.size() == 1) {
			inList.get(0).setAwkey(con.getAwkey());
			inDao.update(inList.get(0));
		}

		List<Invoice> inList2 = inDao.findAll(String.format(
				" id NOT IN(SELECT invoice_id FROM InvoiceItem WHERE barcode='%s') AND type_id = %d AND status_id = %d AND contract_number=%d ",
				con.getTovar_serial(), Invoice.TYPE_WRITEOFF_DOC, Invoice.STATUS_NEW, con.getContract_number()));
		if (inList2.size() > 0) {
			inList2.get(0).setAwkey(getPromoAwkey(con.getContract_number()));
			inDao.update(inList2.get(0));
		}
	}

	@Override
	public void onChangeContractPromo(Contract contract, List<Promotion> promoList, Long currUserId)
			throws DAOException {
		// Map<Long, Matnr> matnrMap = matnrDao.getMappedList("");

		// Списанные материалы
		List<InvoiceItem> writtenOffItems = inDao
				.getContractWrittenOffItemsByContractNumber(contract.getContract_number());

		// Документы на списание акции, которые еще не списаны
		List<Invoice> wrDocItems = inDao.findAll(String.format(
				" id NOT IN(SELECT invoice_id FROM InvoiceItem WHERE barcode='%s') AND type_id = %d AND status_id = %d AND contract_number=%d ",
				contract.getTovar_serial(), Invoice.TYPE_WRITEOFF_DOC, Invoice.STATUS_NEW,
				contract.getContract_number()));

		for (Invoice in : wrDocItems) {
			in.setStatus_id(Invoice.STATUS_ARCHIVE);
			in.setUpdated_at(Calendar.getInstance().getTime());
			in.setUpdated_by(currUserId);
			in.setNote("Отменен послен изменении акцию договора");
			inDao.update(in);
		}

		Map<Long, Double> writtenPromoMengeMap = new HashMap<>();
		for (InvoiceItem ii : writtenOffItems) {
			if (Validation.isEmptyString(ii.getBarcode()) && ii.getQuantity() > 0) {
				Double menge = 0D;
				if (writtenPromoMengeMap.containsKey(ii.getMatnr())) {
					menge = writtenPromoMengeMap.get(ii.getMatnr());
				}
				menge += ii.getQuantity();
				writtenPromoMengeMap.put(ii.getMatnr(), menge);
			}
		}

		if (promoList == null || promoList.size() == 0) {
			if (writtenPromoMengeMap.size() > 0) {
				throw new DAOException("Верните на склад списанные акции");
			}
		}

		Branch br = brDao.find(contract.getBranch_id());
		if (br == null) {
			throw new DAOException("Филиал не найден!");
		}
		List<Werks> wList = wbDao.findAllWerksByBranch2(br.getParent_branch_id());
		if (wList.size() == 0) {
			throw new DAOException("Не найден склад для филиала");
		}

		String[] wIds = new String[wList.size()];
		for (int k = 0; k < wList.size(); k++) {
			wIds[k] = wList.get(k).getWerks().toString();
		}

		Map<Long, Double> promoMap = new HashMap<>();
		if (promoList != null) {
			for (Promotion promo : promoList) {
				Double menge = 0D;
				if (promoMap.containsKey(promo.getMatnr())) {
					menge = promoMap.get(promo.getMatnr());
				}
				menge++;
				promoMap.put(promo.getMatnr(), menge);
			}
		}

		List<InvoiceItem> itemsForWriteOff = new ArrayList<>();
		for (Entry<Long, Double> e : promoMap.entrySet()) {
			if (writtenPromoMengeMap.containsKey(e.getKey())) {
				if (!e.getValue().equals(writtenPromoMengeMap.get(e.getKey()))) {
					throw new DAOException("Верните на склад списанные акции");
				}
				writtenPromoMengeMap.remove(e.getKey());
			} else {
				InvoiceItem itemNew = new InvoiceItem(null, e.getKey(), e.getValue(), null);
				itemsForWriteOff.add(itemNew);
			}
		}

		if (writtenPromoMengeMap.size() > 0) {
			throw new DAOException("Верните на склад списанные акции");
		}

		if (itemsForWriteOff.size() > 0) {
			Invoice wrInvoice = new Invoice();
			wrInvoice.setAwkey(getPromoAwkey(contract.getContract_number()));
			wrInvoice.setBranch_id(contract.getBranch_id());
			wrInvoice.setBukrs(contract.getBukrs());
			wrInvoice.setContract_number(contract.getContract_number());
			wrInvoice.setCreated_at(Calendar.getInstance().getTime());
			wrInvoice.setUpdated_at(wrInvoice.getCreated_at());
			wrInvoice.setCreated_by(currUserId);
			wrInvoice.setUpdated_by(currUserId);
			wrInvoice.setCustomer_id(contract.getCustomer_id());
			wrInvoice.setDepartment_id(0L);
			wrInvoice.setInvoice_date(Calendar.getInstance().getTime());
			wrInvoice.setNote("СПИСАНИЕ ПОСЛЕ ИЗМЕНЕНИЕ АКЦИИ В ДОГОВОРЕ №" + contract.getContract_number());
			wrInvoice.setStatus_id(Invoice.STATUS_NEW);
			wrInvoice.setType_id(Invoice.TYPE_WRITEOFF_DOC);
			wrInvoice.setIs_system(1);
			wrInvoice.setResponsible_id(contract.getDealer());
			inDao.create(wrInvoice);

			for (InvoiceItem ii : itemsForWriteOff) {
				ii.setInvoice_id(wrInvoice.getId());
				ii.setMatnrObject(new Matnr());
			}

			iiService.create(itemsForWriteOff);
		}
	}

	@Override
	public Double getWerksBalance(Long werks, Long matnr, Date toDate) {
		Double out = 0D;
		Calendar cal = Calendar.getInstance();
		cal.setTime(toDate);
		cal.add(Calendar.DATE, -1);
		List<InvoiceItem> items = iiDao.getWerksItemsWithInvoices(werks, matnr, null, cal.getTime());
		for (InvoiceItem item : items) {
			Invoice invoice = item.getInvoiceObject();

			if (Invoice.STATUS_DONE.equals(invoice.getStatus_id())
					|| Invoice.STATUS_MOVING.equals(invoice.getStatus_id())) {
				if (Invoice.TYPE_ACCOUNTABILITY.equals(invoice.getType_id())) {
					if (invoice.getFrom_werks().equals(werks)) {
						out -= item.getQuantity();
					}
				} else if (Invoice.TYPE_ACCOUNTABILITY_RETURN.equals(invoice.getType_id())) {
					if (werks.equals(invoice.getTo_werks())) {
						out += item.getQuantity();
					}
				} else if (Invoice.TYPE_POSTING.equals(invoice.getType_id())) {
					if (werks.equals(invoice.getTo_werks())) {
						out += item.getQuantity();
					}
				} else if (Invoice.TYPE_POSTING_IN.equals(invoice.getType_id())) {
					if (werks.equals(invoice.getTo_werks())) {
						out += item.getQuantity();
					}
				} else if (Invoice.TYPE_RETURN.equals(invoice.getType_id())) {
					if (werks.equals(invoice.getTo_werks())) {
						out += item.getQuantity();
					}
				} else if (Invoice.TYPE_SEND.equals(invoice.getType_id())) {
					if (werks.equals(invoice.getFrom_werks())) {
						out -= item.getQuantity();
					}
				} else if (Invoice.TYPE_WRITEOFF.equals(invoice.getType_id())) {
					if (werks.equals(invoice.getFrom_werks()) && Validation.isEmptyLong(invoice.getResponsible_id())) {
						out -= item.getQuantity();
					}
				} else if (Invoice.TYPE_WRITEOFF_LOSS.equals(invoice.getType_id())) {
					if (werks.equals(invoice.getFrom_werks())) {
						out -= item.getQuantity();
					}
				}
			} else if (Invoice.STATUS_ARCHIVE.equals(invoice.getStatus_id())
					|| Invoice.STATUS_CANCELLED.equals(invoice.getStatus_id())) {
				// if (Invoice.TYPE_WRITEOFF.equals(invoice.getType_id())) {
				// if (werks.equals(invoice.getFrom_werks()) &&
				// Validation.isEmptyLong(invoice.getResponsible_id())) {
				// out -= item.getQuantity();
				// }
				// }
			}
		}

		return out;
	}

	@Override
	public void onChangeContractAwkey(Long oldAwkey, Long newAwkey, User userData) throws DAOException {
		List<Invoice> inList = inDao.findAll(String.format(" awkey = %d AND type_id = %d AND status_id = %d ", oldAwkey,
				Invoice.TYPE_WRITEOFF_DOC, Invoice.STATUS_NEW));
		for (Invoice in : inList) {
			in.setAwkey(newAwkey);
			inDao.update(in);
		}
	}

	@Override
	public void delete(Invoice in, User userData) throws DAOException {
		if (!Invoice.STATUS_NEW.equals(in.getStatus_id())) {
			throw new DAOException("Не возможно удалить документ, обратитесь администратору!");
		}
		in.setStatus_id(Invoice.STATUS_DELETED);
		in.setUpdated_at(Calendar.getInstance().getTime());
		in.setUpdated_by(userData.getUserid());
		inDao.update(in);
	}

	@Override
	public void createWriteoffDocFromBkpf(Bkpf bkpf, List<Bseg> bsegList, Long userId, Long dealerId, Long awkey)
			throws DAOException {
		if (Validation.isEmptyLong(dealerId)) {
			throw new DAOException("Выберите подотчетника");
		}
		Map<Long, Matnr> matnrMap = matnrDao.getMappedList("");
		List<InvoiceItem> iiList = new ArrayList<InvoiceItem>();
		Long fromWerks = 0L;
		if (bsegList != null && bsegList.size() > 0) {

			Map<Long, Double> tempMap = new HashMap<Long, Double>();
			for (Bseg bseg : bsegList) {
				if (Validation.isEmptyLong(bseg.getMatnr())) {
					continue;
				}
				Double menge = 0D;
				if (tempMap.containsKey(bseg.getMatnr())) {
					menge = tempMap.get(bseg.getMatnr());
				}
				// menge++;
				menge += bseg.getMenge();
				tempMap.put(bseg.getMatnr(), menge);
				if (Validation.isEmptyLong(fromWerks)) {
					fromWerks = bseg.getWerks();
				}
			}

			for (Entry<Long, Double> e : tempMap.entrySet()) {
				InvoiceItem ii2 = new InvoiceItem(0L, e.getKey(), e.getValue(), null);
				ii2.setMatnrObject(new Matnr());
				iiList.add(ii2);
			}
		}

		if (iiList.size() == 0) {
			return;
		}

		if (Validation.isEmptyLong(fromWerks)) {
			throw new DAOException("Невозможно списание - склад не выбран!");
		}

		Invoice in = new Invoice();
		in.setAwkey(awkey);
		in.setBranch_id(bkpf.getBrnch());
		in.setBukrs(bkpf.getBukrs());
		in.setCreated_at(Calendar.getInstance().getTime());
		in.setCreated_by(userId);
		in.setCustomer_id(bkpf.getCustomer_id());
		in.setDepartment_id(bkpf.getDep());
		in.setFrom_werks(fromWerks);
		in.setInvoice_date(bkpf.getBldat());
		in.setResponsible_id(dealerId);
		in.setStatus_id(Invoice.STATUS_NEW);
		in.setTo_werks(0L);
		in.setIs_system(1);
		in.setType_id(Invoice.TYPE_WRITEOFF_DOC);
		in.setUpdated_at(Calendar.getInstance().getTime());
		in.setUpdated_by(userId);
		in.setNote("ПРОДАЖА ЗАП. ЧАСТЕЙ. ФИН ОТДЕЛ");

		Map<String, List<Long>> parentDocs1 = new HashMap<>();
		List<Long> l1 = new ArrayList<>();
		l1.add(in.getAwkey());
		parentDocs1.put("bkpf", l1);
		create(in, iiList, parentDocs1, new HashMap<>(), userId);

		List<InvoiceItem> iiList2 = new ArrayList<InvoiceItem>();
		for (InvoiceItem item : iiList) {
			Matnr m = matnrMap.get(item.getMatnr());
			if (m == null) {
				throw new DAOException("Не найден материал в справочнике: " + item.getMatnr());
			}
			InvoiceItem ii2 = new InvoiceItem(null, item.getMatnr(), item.getQuantity(), item.getBarcode());
			ii2.setMatnrObject(new Matnr());
			iiList2.add(ii2);
			int qty = item.getQuantity().intValue();
			String cond = String.format(" werks = %d AND matnr = %d AND staff_id = %d AND status='%s' ", fromWerks,
					item.getMatnr(), in.getResponsible_id(), MatnrList.STATUS_ACCOUNTABILITY);
			List<MatnrList> mlsList = mlsDao.findAll(cond, qty);
			if (mlsList.size() < qty) {
				throw new DAOException("Не достаточно материала для резервирования, код: " + m.getCode());
			}

			for (MatnrList ml : mlsList) {
				ml.setStatus(MatnrList.STATUS_RESERVED);
				mlsDao.update(ml);
			}
		}

		// Документ списания
		Invoice writeOffInvoice = new Invoice();
		writeOffInvoice.setAwkey(in.getAwkey());
		writeOffInvoice.setBranch_id(in.getBranch_id());
		writeOffInvoice.setBukrs(in.getBukrs());
		writeOffInvoice.setCustomer_id(in.getCustomer_id());
		writeOffInvoice.setDepartment_id(1L);// Otdel Marketinga
		writeOffInvoice.setFrom_werks(fromWerks);
		writeOffInvoice.setInvoice_date(new Date());
		writeOffInvoice.setResponsible_id(in.getResponsible_id());
		writeOffInvoice.setStatus_id(Invoice.STATUS_NEW);
		writeOffInvoice.setTo_werks(0L);
		writeOffInvoice.setIs_system(1);
		writeOffInvoice.setType_id(Invoice.TYPE_WRITEOFF);
		writeOffInvoice.setNote(in.getNote());

		Map<String, List<Long>> parentDocs = new HashMap<>();
		List<Long> l = new ArrayList<>();
		l.add(in.getId());
		parentDocs.put(Invoice.CONTEXT, l);

		create(writeOffInvoice, iiList2, parentDocs, new HashMap<>(), userId);

		matnrListService.doWriteoff(writeOffInvoice, userId);
	}

	@Override
	public void onCancelBkpf(Long awkey, User userData) throws DAOException {
		List<Invoice> inList = inDao.findAll(" awkey = " + awkey);
		List<Invoice> forCancelList = new ArrayList<>();

		for (Invoice in : inList) {
			if (Invoice.STATUS_DONE.equals(in.getStatus_id())) {

				String[] childIds = relDDao.findChildContextIds(in.getId(), Invoice.CONTEXT, Invoice.CONTEXT);
				if (childIds != null && childIds.length > 0) {
					List<Invoice> childInvoices = inDao
							.findAll(String.format(" id IN(%s) AND type_id = %d AND status_id = %d",
									String.join(",", childIds), Invoice.TYPE_WRITEOFF, Invoice.STATUS_DONE));
					for (Invoice childIn : childInvoices) {
						List<LogFinClass> lfcList = new ArrayList<LogFinClass>();
						List<InvoiceItem> childInItems = iiDao.findAll(" invoice_id = " + childIn.getId());
						for (InvoiceItem childItem : childInItems) {
							int qty = childItem.getQuantity().intValue();
							String cond = String.format(" werks = %d AND matnr = %d ", childIn.getFrom_werks(),
									childItem.getMatnr());
							if(!Validation.isEmptyString(childItem.getMl_ids())){
								cond += " AND matnr_list_id IN(" + String.join(",", childItem.getMlIdsAsArray()) + ") ";
							}
							List<MatnrList> mlsList = mlsDao.findAllSold(cond, qty);
							if (mlsList.size() < qty) {
								throw new DAOException("Не достаточно материалов для возврата");
							}

							Double totalMenge = 0D;
							Double totalDmbtr = 0D;

							for (MatnrList mls : mlsList) {
								if(Validation.isEmptyLong(childIn.getResponsible_id())){
									mls.setStaff_id(childIn.getResponsible_id());
									mls.setStatus(MatnrList.STATUS_RECEIVED);
								} else {
									mls.setStaff_id(childIn.getResponsible_id());
									mls.setStatus(MatnrList.STATUS_ACCOUNTABILITY);
								}
								
								mlsDao.update(mls);

								totalMenge += mls.getMenge();
								totalDmbtr += mls.getDmbtr();
							}

							LogFinClass lfc = new LogFinClass();
							lfc.setMatnr(childItem.getMatnr());
							lfc.setMenge(totalMenge);
							lfc.setTotalDmbtr(totalDmbtr);
							lfcList.add(lfc);
						}

						Bkpf bkpf = bkpfDao.findOriginalSingleBkpf(GeneralUtil.getPreparedBelnr(childIn.getAwkey()),
								GeneralUtil.getPreparedGjahr(childIn.getAwkey()), childIn.getBukrs());
						if (bkpf != null) {
							fServiceLogistics.returnMatnrToWerks(lfcList, bkpf, childIn.getBukrs(),
									userData.getUserid(), childIn.getBranch_id(), "", childIn.getFrom_werks());
						}

						forCancelList.add(childIn);
					}
				}

				forCancelList.add(in);

				// throw new DAOException("Верните на склад списанные
				// материалы");
			} else if (Invoice.STATUS_NEW.equals(in.getStatus_id())) {
				forCancelList.add(in);
			}
		}

		for (Invoice in : forCancelList) {
			cancelInvoice(in, userData.getUserid());
		}
	}

	@Override
	public void writeoffFromService(Long serviceAwkey, Long userId, String a_bukrs) throws DAOException {
		List<Invoice> inList = inDao
				.findAll(String.format(" type_id = %d AND status_id = %d AND service_number > 0 AND awkey=%d and bukrs='"+a_bukrs+"'",
						Invoice.TYPE_WRITEOFF_DOC, Invoice.STATUS_NEW, serviceAwkey));
		if (inList.size() > 0) {
			Invoice in = inList.get(0);
			List<InvoiceItem> itemList = iiDao.findAll(" invoice_id = " + in.getId());
			if (itemList == null || itemList.size() == 0) {
				return;
			}
			Long fromWerks = in.getFrom_werks();
			if (Validation.isEmptyLong(fromWerks)) {
				fromWerks = getBranchParentWerks(in.getBranch_id());
			}

			List<InvoiceItem> writeOffItems = new ArrayList<>();
			for (InvoiceItem item : itemList) {
				InvoiceItem ii = new InvoiceItem(null, item.getMatnr(), item.getQuantity(), item.getBarcode());
				ii.setMatnrObject(new Matnr());
				writeOffItems.add(ii);
			}

			Invoice writeOffInvoice = new Invoice();
			writeOffInvoice.setAwkey(in.getAwkey());
			writeOffInvoice.setBranch_id(in.getBranch_id());
			writeOffInvoice.setBukrs(in.getBukrs());
			writeOffInvoice.setCustomer_id(in.getCustomer_id());
			writeOffInvoice.setDepartment_id(3L);
			writeOffInvoice.setFrom_werks(fromWerks);
			writeOffInvoice.setInvoice_date(new Date());
			writeOffInvoice.setResponsible_id(in.getResponsible_id());
			writeOffInvoice.setStatus_id(Invoice.STATUS_NEW);
			writeOffInvoice.setTo_werks(0L);
			writeOffInvoice.setIs_system(1);
			writeOffInvoice.setType_id(Invoice.TYPE_WRITEOFF);
			writeOffInvoice
					.setNote(String.format("СЕРВИС КАРТА, №%d. СПИСАНИЕ СОЗДАНО СИСТЕМОЙ", in.getService_number()));
			writeOffInvoice.setService_number(in.getService_number());

			Map<String, List<Long>> parentDocs = new HashMap<>();
			List<Long> l = new ArrayList<>();
			l.add(in.getId());
			parentDocs.put(Invoice.CONTEXT, l);

			create(writeOffInvoice, writeOffItems, parentDocs, new HashMap<>(), userId);

			matnrListService.doWriteoff(writeOffInvoice, userId);
		}
	}

	@Override
	public void checkStaffAccountabilityMatnr(Long staffId, Long werks, Map<Long, Double> matnrMengeMap)
			throws DAOException {
		Map<Long, Matnr> matnrMap = matnrDao.getMappedList("");
		for (Entry<Long, Double> e : matnrMengeMap.entrySet()) {
			Matnr m = matnrMap.get(e.getKey());
			if (m == null) {
				throw new DAOException("Не найден материал в справочник с ID: " + e.getKey());
			}

			int qty = e.getValue().intValue();
			String cond = String.format(" werks = %d AND matnr = %d AND staff_id = %d AND status='%s' ", werks,
					m.getMatnr(), staffId, MatnrList.STATUS_ACCOUNTABILITY);
			List<MatnrList> mlsList = mlsDao.findAll(cond, qty);
			if (mlsList.size() < qty) {
				throw new DAOException("У подотчетника не достаточно материала, код: " + m.getCode());
			}
		}
	}
}