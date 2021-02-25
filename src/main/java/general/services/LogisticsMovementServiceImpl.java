package general.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logistics.LogToFinance;
import general.GeneralUtil;
import general.MessageProvider;
import general.Validation;
import general.dao.DAOException;
import general.dao.MatnrListDao;
import general.dao.MatnrMovementDao;
import general.dao.MatnrMovementItemDao;
import general.dao.OrderDao;
import general.tables.Bkpf;
import general.tables.MatnrList;
import general.tables.MatnrMovement;
import general.tables.MatnrMovementItem;
import general.tables.Order;
import general.tables.OrderStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("logisticsMovementService")
public class LogisticsMovementServiceImpl implements LogisticsMovementService {

	@Autowired
	MatnrMovementDao mmDao;

	@Autowired
	MatnrListDao mlDao;

	@Autowired
	FinanceServiceLogistics fServiceLogistics;

	@Autowired
	private MatnrMovementItemDao mItemDao;

	@Autowired
	OrderDao orderDao;

	// Создание ПЕРЕМЕЩЕНИЕ-Отправка
	@Override
	public void create(MatnrMovement mm, List<MatnrList> mlList, String tCode,
			Long branchId, List<Bkpf> findocList,
			Map<Long, List<String>> barcodeMap) throws DAOException {
		mm.setMm_type(MatnrMovement.TYPE_MOVEMENT_SEND);
		mm.setStatus(MatnrMovement.STATUS_MOVING);

		Map<String, List<Long>> matnrListIdsMap = new HashMap<String, List<Long>>(); // status
																						// =>
																						// List<ids>
		List<LogToFinance> listForFinance = new ArrayList<LogToFinance>();
		matnrListIdsMap.put(MatnrList.STATUS_MOVING, new ArrayList<Long>());
		matnrListIdsMap
				.put(MatnrList.STATUS_MOVING_TEMP, new ArrayList<Long>());
		List<MatnrMovementItem> mmiList = getPreparedItems(mm, mlList,
				findocList, listForFinance, matnrListIdsMap, barcodeMap);
		validate(mm, mmiList, true);

//		Long awkey = fServiceLogistics.moveFromWerks(listForFinance,
//				findocList, mm.getBukrs(), mm.getCreated_by(), branchId, tCode,
//				mm.getFrom_werks());
		//mm.setAwkey(awkey);
		mmDao.create(mm);

		// for(MatnrList )

		for (MatnrMovementItem item : mmiList) {
			item.setMm_id(mm.getMm_id());
			item.setStatus(MatnrMovement.STATUS_MOVING);
			mItemDao.create(item);
		}

		for (Entry<String, List<Long>> e : matnrListIdsMap.entrySet()) {
			String[] ids = new String[e.getValue().size()];
			int i = 0;
			for (Long l : e.getValue()) {
				ids[i] = l.toString();
				i++;
			}
			System.out.println("SSSIZE: " + e.getValue().size());
			String s = String
					.format(" SET status = '%s', staff_id = %d WHERE matnr_list_id IN (%s) ",
							e.getKey(), mm.getStaff_id(),
							"'" + String.join("','", ids) + "'");
			mlDao.updateMatnrs(s);
		}

		String cond = String
				.format(" in_status = %d AND werks_from = %d AND werks_to = %d ",
						OrderStatus.STATUS_IN_NEW, mm.getTo_werks(),
						mm.getFrom_werks());
		List<Order> oList = orderDao.findAll(cond);
		for (Order o : oList) {
//			o.setIn_status(OrderStatus.STATUS_IN_PROCESSED);
//			o.setOut_status(OrderStatus.STATUS_OUT_PROCESSED);
//			o.setProcessed_date(Calendar.getInstance().getTime());
			orderDao.update(o);
		}
	}

	private List<MatnrMovementItem> getPreparedItems(MatnrMovement mm,
			List<MatnrList> mlList, List<Bkpf> bkpfList,
			List<LogToFinance> listForFinance,
			Map<String, List<Long>> matnrListIdsMap,
			Map<Long, List<String>> barcodeMap) {
		Map<Long, Double> matnrOldDmbtrMap = new HashMap<Long, Double>();
		Double allTeDmbtr = 0D;
		Double allMatnrDmbtr = 0D;
		for (Bkpf b : bkpfList) {
			allTeDmbtr += b.getDmbtr();
		}

		List<MatnrMovementItem> out = new ArrayList<MatnrMovementItem>();
		Map<Long, List<MatnrList>> mlListMap = new HashMap<Long, List<MatnrList>>();
		Map<Long, Double> mlDmbtrMap = new HashMap<Long, Double>();

		for (MatnrList ml : mlList) {
			List<String> barcodes = barcodeMap.get(ml.getMatnr());
			if (barcodes == null) {
				barcodes = new ArrayList<String>();
			}
			int count = new Double(ml.getMenge()).intValue();
			String cond = String.format(
					" werks = %d AND status = '%s' AND matnr = %d ",
					mm.getFrom_werks(), MatnrList.STATUS_RECEIVED,
					ml.getMatnr());
			if (barcodes.size() > 0) {
				cond += String.format(
						" AND barcode IN(%s) ",
						"'"
								+ String.join("','", barcodes
										.toArray(new String[barcodes.size()]))
								+ "'");
			}
			List<MatnrList> tempMlList = mlDao.findAll(cond, count);
			if (tempMlList.size() < count) {
				throw new DAOException("На складе не достаточно материалов: "
						+ ml.getMatnrObject().getText45() + "("
						+ tempMlList.size() + ")" + " \n");
			}
			Double tempDmbtr = 0D;
			List<Long> tempMatnrIds = new ArrayList<Long>();
			for (int i = 0; i < tempMlList.size(); i++) {
				matnrOldDmbtrMap.put(ml.getMatnr(), tempMlList.get(i)
						.getDmbtr());
				tempDmbtr += tempMlList.get(i).getDmbtr();
				tempMatnrIds.add(tempMlList.get(i).getMatnr_list_id());
			}

			if (barcodes.size() == 0
					&& !Validation
							.isEmptyString(tempMlList.get(0).getBarcode())) {
				matnrListIdsMap.get(MatnrList.STATUS_MOVING_TEMP).addAll(
						tempMatnrIds);
			} else {
				matnrListIdsMap.get(MatnrList.STATUS_MOVING).addAll(
						tempMatnrIds);
			}

			allMatnrDmbtr += tempDmbtr;
			mlDmbtrMap.put(ml.getMatnr(), tempDmbtr);
			mlListMap.put(ml.getMatnr(), tempMlList);
		}

		Map<Long, Double> matnrTeDmbtrMap = new HashMap<Long, Double>();
		Map<Long, Double> newMlDmbtrMap = new HashMap<Long, Double>();
		for (Entry<Long, List<MatnrList>> e : mlListMap.entrySet()) {
			Double mlDmbtr = mlDmbtrMap.get(e.getKey());
			Double matnrTe = GeneralUtil.round((mlDmbtr * allTeDmbtr)
					/ allMatnrDmbtr, 2);
			Double tempTotalDmbtr = 0D;
			Double firstMlDmbtr = 0D;
			Double matnrTotalDmbtr = 0D;
			for (MatnrList ml : e.getValue()) {
				Double addedTeDmbtr = GeneralUtil.round(
						(matnrTe * ml.getDmbtr()) / mlDmbtr, 2);
				ml.setDmbtr(ml.getDmbtr() + addedTeDmbtr);
				tempTotalDmbtr += addedTeDmbtr;
				if (firstMlDmbtr == 0D) {
					firstMlDmbtr = ml.getDmbtr();
				}

				matnrTotalDmbtr += ml.getDmbtr();
			}

			e.getValue()
					.get(0)
					.setDmbtr(
							GeneralUtil.round(firstMlDmbtr
									+ (matnrTe - tempTotalDmbtr), 2));
			matnrTotalDmbtr = GeneralUtil.round(matnrTotalDmbtr
					+ (matnrTe - tempTotalDmbtr), 2);
			newMlDmbtrMap.put(e.getKey(), matnrTotalDmbtr);
			matnrTeDmbtrMap.put(e.getKey(), matnrTe);
		}

		for (Entry<Long, List<MatnrList>> e : mlListMap.entrySet()) {
			LogToFinance tempLTF = new LogToFinance();
			tempLTF.setMenge(new Double(e.getValue().size()));
			tempLTF.setMatnr(e.getKey());
			tempLTF.setDmbtr(newMlDmbtrMap.get(e.getKey()));
			tempLTF.setTeDmbtr(matnrTeDmbtrMap.get(e.getKey()));
			listForFinance.add(tempLTF);

			for (MatnrList ml : e.getValue()) {
				MatnrMovementItem mmi = new MatnrMovementItem();
				mmi.setMatnr(ml.getMatnr());
				mmi.setMatnr_list_id(ml.getMatnr_list_id());
				mmi.setNew_dmbtr(ml.getDmbtr());
				mmi.setOld_dmbtr(matnrOldDmbtrMap.get(ml.getMatnr()));
				out.add(mmi);
			}
		}

		return out;
	}

	// private List<MatnrMovementItem> getPreparedItems2(MatnrMovement mm,
	// List<OutputListClass> outputList, Map<String, List<Long>> mListMap) {
	// List<MatnrMovementItem> out = new ArrayList<MatnrMovementItem>();
	// for (OutputListClass olc : outputList) {
	// int count = olc.getMenge().intValue();
	// Double newDmbtr = getRounded(olc.getDmbtr() / olc.getMenge());
	// List<Long> matnrListIds = olc.getMatnrListIds();
	// String status = MatnrList.STATUS_MOVING;
	// if (matnrListIds.size() == 0) {
	// status = MatnrList.STATUS_MOVING_TEMP;
	// String cond = String.format(
	// "werks = %d AND status = '%s' AND matnr = %d ",
	// mm.getFrom_werks(), MatnrList.STATUS_RECEIVED,
	// olc.getMatnr());
	// List<MatnrList> randomMatnrList = mlDao.findAll(cond, olc
	// .getMenge().intValue());
	// if (randomMatnrList.size() < olc.getMenge().intValue()) {
	// throw new DAOException(
	// "На складе не достаточно материалов \n");
	// }
	//
	// matnrListIds = new ArrayList<Long>();
	// for (MatnrList ml : randomMatnrList) {
	// matnrListIds.add(ml.getMatnr_list_id());
	// }
	// }
	//
	// mListMap.put(status, matnrListIds);
	// for (int k = 0; k < count; k++) {
	// MatnrMovementItem temp = new MatnrMovementItem();
	// temp.setMatnr(olc.getMatnr());
	// temp.setOld_dmbtr(olc.getOldDmbtr());
	// temp.setNew_dmbtr(olc.getDmbtr());
	// temp.setStatus(MatnrMovement.STATUS_MOVING);
	// temp.setMatnr_list_id(matnrListIds.get(k));
	// out.add(temp);
	// }
	//
	// }
	// return out;
	// }

	private void validate(MatnrMovement mm, List<MatnrMovementItem> mmiList,
			boolean isNew) throws DAOException {
		String error = "";
		MessageProvider messageProvider = new MessageProvider();
		if (Validation.isEmptyString(mm.getBukrs())) {
			error += MessageFormat.format(
					messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("logistics.movement.bukrs"))
					+ "\n";
		}

		if (Validation.isEmptyLong(mm.getFrom_werks())) {
			error += MessageFormat.format(
					messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("logistics.movement.from_werks"))
					+ "\n";
		}

		if (Validation.isEmptyLong(mm.getTo_werks())) {
			error += MessageFormat.format(
					messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("logistics.movement.to_werks"))
					+ "\n";
		}

		if (mmiList.size() == 0) {
			error += messageProvider
					.getValue("logistics.movement.empty_materials") + "\n";
		}

		if (mm.getFrom_werks().equals(mm.getTo_werks())) {
			error += messageProvider
					.getValue("logistics.movement.to_werks_is_same") + "\n";
		}

		if (Validation.isEmptyLong(mm.getStaff_id())) {
			error += MessageFormat.format(
					messageProvider.getErrorValue("field_is_required"),
					messageProvider.getValue("logistics.movement.staff_id"))
					+ "\n";
		}

		if (mm.getMm_date() == null) {
			error += " Поле дата обязательно для заполнения" + "\n";
		}

		if (isNew) {
			mm.setCreated_date(Calendar.getInstance().getTime());
		}

		if (error.length() > 0) {
			throw new DAOException(error);
		}
	}

	Double getRounded(Double d) {
		return new BigDecimal(d).setScale(2, RoundingMode.DOWN).doubleValue();
	}

	// Перемещения - приход
	@Override
	public void receive(MatnrMovement mm, MatnrMovement receivedMovement,
			List<MatnrList> mlList, String tCode, Long branchId,
			Map<Long, List<String>> barcodeMap) throws DAOException {

		if (receivedMovement.getMm_date() == null) {
			throw new DAOException("Введите дату");
		}

		List<MatnrMovementItem> mmiList = mItemDao
				.findAllWithMatnrListByMmId(mm.getMm_id());
		Map<Long, List<MatnrMovementItem>> mmiListMap = new HashMap<Long, List<MatnrMovementItem>>();
		for (MatnrMovementItem mmi : mmiList) {
			if (!mmiListMap.containsKey(mmi.getMatnr())) {
				mmiListMap.put(mmi.getMatnr(),
						new ArrayList<MatnrMovementItem>());
			}
			mmiListMap.get(mmi.getMatnr()).add(mmi);
		}
		Map<Long, Integer> mengeMap = new HashMap<Long, Integer>();
		for (MatnrList ml : mlList) {
			if (mmiListMap.containsKey(ml.getMatnr())) {
				if (new Double(ml.getMenge()).intValue() > mmiListMap.get(
						ml.getMatnr()).size()) {
					throw new DAOException(
							"Количество принято не может быть больше количества отправлено!");
				}
			}

			mengeMap.put(ml.getMatnr(), new Double(ml.getMenge()).intValue());

		}

		for (Entry<Long, List<MatnrMovementItem>> e : mmiListMap.entrySet()) {
			List<String> barcodes = barcodeMap.get(e.getKey());
			if (barcodes != null) {
				for (String s : barcodes) {
					boolean isFound = false;
					for (MatnrMovementItem mmi : e.getValue()) {
						if (!Validation.isEmptyString(mmi.getMatnrList()
								.getBarcode()) && !Validation.isEmptyString(s)) {
							if (s.equals(mmi.getMatnrList().getBarcode())) {
								isFound = true;
								break;
							}
						}
					}

					if (!isFound) {
						throw new DAOException(
								"Не найден материал с баркодом: " + s);
					}
				}
			}
		}

		Map<Long, Double> dmbtrMap = new HashMap<Long, Double>();

		List<MatnrMovementItem> preparedMmiList = new ArrayList<MatnrMovementItem>();
		for (Entry<Long, List<MatnrMovementItem>> e : mmiListMap.entrySet()) {
			Double matnrDmbtrTotal = 0D;
			List<String> barcodes = barcodeMap.get(e.getKey());
			if (barcodes == null) {
				int temp = 0;
				for (MatnrMovementItem mmi : e.getValue()) {
					if (temp == mengeMap.get(e.getKey())) {
						break;
					}
					preparedMmiList.add(mmi);
					matnrDmbtrTotal += mmi.getNew_dmbtr();
					temp++;
				}
			} else {
				for (MatnrMovementItem mmi : e.getValue()) {
					if (!Validation.isEmptyString(mmi.getMatnrList()
							.getBarcode())
							&& barcodes.contains(mmi.getMatnrList()
									.getBarcode())) {
						preparedMmiList.add(mmi);
						matnrDmbtrTotal += mmi.getNew_dmbtr();
					}
				}
			}

			dmbtrMap.put(e.getKey(), matnrDmbtrTotal);
		}

		List<LogToFinance> listForFinance = new ArrayList<LogToFinance>();
		for (Entry<Long, List<MatnrMovementItem>> e : mmiListMap.entrySet()) {
			LogToFinance ltf = new LogToFinance();
			ltf.setDmbtr(dmbtrMap.get(e.getKey()));
			ltf.setMatnr(e.getKey());
			ltf.setMenge(new Double(mengeMap.get(e.getKey())));
			ltf.setTeDmbtr(0D);
			listForFinance.add(ltf);
		}

		Long awkey = null;
//		fServiceLogistics.movetoWerks(listForFinance,
//				mm.getBukrs(), receivedMovement.getCreated_by(), branchId,
//				tCode, mm.getTo_werks());

		receivedMovement.setRelated_id(mm.getMm_id());
		receivedMovement.setAwkey(awkey);
		receivedMovement.setFrom_werks(mm.getFrom_werks());
		receivedMovement.setTo_werks(mm.getTo_werks());
		receivedMovement.setStatus(MatnrMovement.STATUS_RECEIVED);
		receivedMovement.setMm_type(MatnrMovement.TYPE_MOVEMENT_RECEIVE);
		mmDao.create(receivedMovement);

		mm.setStatus(MatnrMovement.STATUS_RECEIVED);
		mm.setRelated_id(receivedMovement.getMm_id());
		mmDao.update(mm);

		for (MatnrMovementItem mmi : preparedMmiList) {
			MatnrMovementItem newMMi = new MatnrMovementItem();
			newMMi.setMatnr(mmi.getMatnr());
			newMMi.setMatnr_list_id(mmi.getMatnr_list_id());
			newMMi.setMm_id(receivedMovement.getMm_id());
			newMMi.setNew_dmbtr(mmi.getNew_dmbtr());
			newMMi.setOld_dmbtr(mmi.getOld_dmbtr());
			newMMi.setReceived_date(receivedMovement.getMm_date());
			newMMi.setStatus(MatnrMovement.STATUS_RECEIVED);
			mItemDao.create(newMMi);

			mmi.setStatus(MatnrMovement.STATUS_RECEIVED);
			mItemDao.update(mmi);

			MatnrList ml = mmi.getMatnrList();
			ml.setDmbtr(mmi.getNew_dmbtr());
			ml.setStaff_id(0L);
			ml.setWerks(mm.getTo_werks());
			ml.setStatus(MatnrList.STATUS_RECEIVED);
			mlDao.update(ml);

		}

		// mm.setStatus(MatnrMovement.STATUS_RECEIVED);
		// mm.setRelated_id(receivedMovement);

		// for()

		// List<MatnrMovementItem> itemList = mItemDao.findAll("mm_id = "
		// + mm.getMm_id());
		// Map<Long, List<MatnrMovementItem>> itemListMap = new HashMap<Long,
		// List<MatnrMovementItem>>();
		// for (MatnrMovementItem mmi : itemList) {
		// if (!itemListMap.containsKey(mmi.getMatnr())) {
		// itemListMap.put(mmi.getMatnr(),
		// new ArrayList<MatnrMovementItem>());
		// }
		// itemListMap.get(mmi.getMatnr()).add(mmi);
		// }
		//
		// for (OutputListClass olc : outputList) {
		// if (olc.hasBarcode()) {
		// List<MatnrMovementItem> tempMmiList = itemListMap.get(olc
		// .getMatnr());
		// String[] matnrListIds = new String[olc.getMatnrListIds().size()];
		// String[] matnrListIds2 = new String[tempMmiList.size()];
		// for (int k = 0; k < tempMmiList.size(); k++) {
		// matnrListIds2[k] = tempMmiList.get(k).getMatnr_list_id()
		// .toString();
		// }
		//
		// String query = String
		// .format(" SET staff_id = 0, status = '%s'  WHERE matnr_list_id IN(%s)",
		// MatnrList.STATUS_RECEIVED,
		// "'" + String.join("','", matnrListIds2) + "'");
		// mlDao.updateMatnrs(query);
		// for (int k = 0; k < olc.getMovementItemIds().size(); k++) {
		// query = String
		// .format("SET matnr_list_id = %d,status='%s' WHERE item_id = %d",
		// olc.getMatnrListIds().get(k),
		// MatnrMovement.STATUS_RECEIVED, olc
		// .getMovementItemIds().get(k));
		// mItemDao.updateByQuery(query);
		// }
		// for (int k = 0; k < olc.getMatnrListIds().size(); k++) {
		// matnrListIds[k] = olc.getMatnrListIds().get(k).toString();
		// }
		// query = String
		// .format(" SET staff_id = 0, status = '%s',werks = %d WHERE matnr_list_id IN(%s)",
		// MatnrList.STATUS_RECEIVED, mm.getTo_werks(),
		// "'" + String.join("','", matnrListIds) + "'");
		// mlDao.updateMatnrs(query);
		//
		// } else {
		// String[] movementItemIds = new String[olc.getMovementItemIds()
		// .size()];
		// String[] matnrListIds = new String[olc.getMatnrListIds().size()];
		// for (int i = 0; i < olc.getMatnrListIds().size(); i++) {
		// matnrListIds[i] = olc.getMatnrListIds().get(i).toString();
		// movementItemIds[i] = olc.getMovementItemIds().get(i)
		// .toString();
		// }
		//
		// String query1 = String.format(
		// " SET status = '%s' WHERE item_id IN (%s) ",
		// MatnrMovement.STATUS_RECEIVED,
		// "'" + String.join("','", movementItemIds) + "'");
		// String query2 = String
		// .format(" SET status = '%s', staff_id = %d, werks = %d WHERE matnr_list_id IN (%s) ",
		// MatnrList.STATUS_RECEIVED, 0, mm.getTo_werks(),
		// "'" + String.join("','", matnrListIds) + "'");
		// mItemDao.updateByQuery(query1);
		// mlDao.updateMatnrs(query2);
		//
		// }
		// }
		// mm.setStatus(MatnrMovement.STATUS_RECEIVED);
		// mmDao.update(mm);
		//
		// // TODO Finance
		// fServiceLogistics.movetoWerks(outputList, mm.getBukrs(),
		// mm.getCreated_by(), branchId, tCode, mm.getTo_werks());
	}
}