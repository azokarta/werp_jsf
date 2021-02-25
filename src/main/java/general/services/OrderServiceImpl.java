package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.OrderDao;
import general.dao.RelatedDocsDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.User;
import general.tables.Order;
import general.tables.OrderMatnr;
import general.tables.RelatedDocs;
import general.tables.RequestOut;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
	@Autowired
	private OrderDao oDao;

	@Autowired
	RelatedDocsDao relDDao;

	@Autowired
	private OrderMatnrService omService;
	
	@Autowired RelatedDocsService rdService;

	@Override
	public void create(Order o, List<OrderMatnr> omList,
			List<RequestOut> parentDocs, User userData) throws DAOException {
		String error = validate(o, userData, true);
		if (omList == null || omList.size() == 0) {
			error += "Список материалов пуст";
		}

		if (error.length() > 0) {
			throw new DAOException(error);
		}
		o.setId(null);
		oDao.create(o);
		Double totalAmount = 0D;
		for (OrderMatnr om : omList) {
			om.setOrder_id(o.getId());
			totalAmount += om.getAmount();
		}
		
		o.setTotal_amount(GeneralUtil.round(totalAmount, 2));
		oDao.update(o);

		omService.create(omList);
		addToParentDocs(o.getId(), parentDocs);
	}

	private String validate(Order o, User userData, boolean isNew) {
		String error = "";

		if (userData.getStaff() == null) {
			error += "Вы не можете добавить запись, обратитесь админу" + "\n";
		} else {
			if (isNew) {
				o.setCreated_by(userData.getUserid());
				o.setCreated_at(Calendar.getInstance().getTime());
				o.setStatus_id(Order.STATUS_OPENED);
			}

			o.setUpdated_at(Calendar.getInstance().getTime());
			o.setUpdated_by(userData.getUserid());
		}

		if (Validation.isEmptyString(o.getBukrs())) {
			error += "Выберите компанию" + "\n";
		}

		if (Validation.isEmptyLong(o.getCountry_id())) {
			error += "Выберите страну" + "\n";
		}

		if (Validation.isEmptyString(o.getCurrency())) {
			error += "Выберите валюту" + "\n";
		}

		if (Validation.isEmptyLong(o.getDepartment_id())) {
			error += "Выберите департамент" + "\n";
		}

		if (Validation.isEmptyLong(o.getCustomer_id())) {
			error += "Выберите поставщика" + "\n";
		}

		return error;
	}

	private void addToParentDocs(Long orderId, List<RequestOut> parentDocs) {
		Map<String, List<Long>> m = new HashMap<String, List<Long>>();
		if(parentDocs != null && parentDocs.size() > 0){
			List<Long> l = new ArrayList<Long>();
			for(RequestOut ro:parentDocs){
				l.add(ro.getId());
			}
			
			if(l.size() > 0){
				m.put(RequestOut.CONTEXT, l);
			}
		}
		
		rdService.addChildToParents(orderId, Order.CONTEXT, m);
		
//		List<RelatedDocs> orderRelList = relDDao.findAll(String.format(
//				" context = '%s' AND context_id = %d AND parent_id > 0 ",
//				Order.CONTEXT, orderId));
//		List<RelatedDocs> relatedParentDocs = new ArrayList<RelatedDocs>();
//		if (parentDocs != null && parentDocs.size() > 0) {
//			String[] ids = new String[parentDocs.size()];
//			for (int i = 0; i < parentDocs.size(); i++) {
//				ids[i] = parentDocs.get(i).getId().toString();
//			}
//
//			relatedParentDocs = relDDao.findAll(String.format(
//					" context = '%s' AND context_id IN (%s) ",
//					RequestOut.CONTEXT, "'" + String.join("','", ids) + "' "));
//
//		}
//
//		Map<Long, Integer> tempMap = new HashMap<Long, Integer>();
//		List<RelatedDocs> existsParentDocs = new ArrayList<RelatedDocs>();
//		if (orderRelList.size() > 0) {
//			String[] ids = new String[orderRelList.size()];
//			for (int i = 0; i < orderRelList.size(); i++) {
//				ids[i] = orderRelList.get(i).getParent_id().toString();
//			}
//
//			existsParentDocs = relDDao.findAll(String.format(" id IN(%s) ", "'"
//					+ String.join("','", ids) + "'"));
//		}
//		//System.out.println("Related: " + relatedParentDocs.size());
//		for (RelatedDocs rd1 : relatedParentDocs) {
//			boolean isExisted = false;
//			for (RelatedDocs rd2 : existsParentDocs) {
//				if (rd1.getId().equals(rd2.getId())) {
//					isExisted = true;
//					tempMap.put(rd1.getId(), 1);
//					break;
//				}
//			}
//
//			if (!isExisted) {
//				RelatedDocs newRD = new RelatedDocs();
//				newRD.setContext(Order.CONTEXT);
//				newRD.setContext_id(orderId);
//				newRD.setParent_id(rd1.getId());
//				newRD.setTree_id(rd1.getTree_id());
//				relDDao.create(newRD);
//			}
//		}
//
//		for (RelatedDocs rd : existsParentDocs) {
//			if (!tempMap.containsKey(rd.getId())) {
//				relDDao.delete(rd.getId(), Order.CONTEXT, orderId);
//			}
//		}

	}

	@Override
	public void update(Order o, List<OrderMatnr> omList,
			List<RequestOut> parentDocs, User userData) throws DAOException {
		String error = validate(o, userData, false);
		if (omList == null || omList.size() == 0) {
			error += "Список материалов пуст";
		}

		if (error.length() > 0) {
			throw new DAOException(error);
		}

		Double totalAmount = 0D;
		for (OrderMatnr om : omList) {
			om.setOrder_id(o.getId());
			totalAmount += om.getAmount();
		}

		omService.create(omList);
		o.setTotal_amount(GeneralUtil.round(totalAmount, 2));
		oDao.update(o);
		//addToParentDocs(o.getId(), parentDocs);
	}

}
