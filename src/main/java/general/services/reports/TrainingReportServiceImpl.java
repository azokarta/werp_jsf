package general.services.reports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.Validation;
import general.dao.DAOException;
import general.dao.DemoDao;
import general.dao.StaffDao;
import general.tables.Demonstration;
import general.tables.Staff;
import general.tables.report.TrainingReport1;

@Service("trainingReportService")
public class TrainingReportServiceImpl implements TrainingReportService {

	@Autowired
	DemoDao demoDao;

	@Autowired
	StaffDao stfDao;

	@Override
	public List<TrainingReport1> getRep1Data(String bukrs, Long branchId, Date fromDate, Date toDate) {
		if (Validation.isEmptyString(bukrs)) {
			throw new DAOException("Выберите компанию");
		}

		if (Validation.isEmptyLong(branchId)) {
			throw new DAOException("Выберите филиал");
		}

		Map<Long, Staff> stfMap = stfDao.getMappedList("");

		String cond = String.format(" bukrs = '%s' AND branch_id = %d ", bukrs, branchId);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (fromDate != null) {
			cond += " AND to_char(date_time,'yyyy-mm-dd') >= '" + sdf.format(fromDate).toString() + "' ";
		}

		if (toDate != null) {
			cond += " AND to_char(date_time,'yyyy-mm-dd') <= '" + sdf.format(toDate).toString() + "' ";
		}

		Map<Long, TrainingReport1> tempMap = new HashMap<>();
		List<Demonstration> temp = demoDao.findAll(cond);
		//System.out.println("SIZE: " + temp.size());
		for (Demonstration d : temp) {
			TrainingReport1 tr = new TrainingReport1();
			tr.setRefCount(d.getRefCount());
			tr.setSaleQuantity(d.getStatusId().equals(Demonstration.DEMO_STATUS_SOLD) ? 1D : 0D);
			tr.setShownQuantity(d.getStatusId().equals(Demonstration.DEMO_STATUS_SHOWN) ? 1D : 0D);
			tr.setCancelledQuantity(d.getStatusId().equals(Demonstration.DEMO_STATUS_CANCELLED) ? 1D : 0D);
			tr.setMovedQuantity(d.getStatusId().equals(Demonstration.DEMO_STATUS_MOVED) ? 1D : 0D);

			if (tempMap.containsKey(d.getDealerId())) {
				TrainingReport1 trTemp = tempMap.get(d.getDealerId());
				tr.setRefCount(tr.getRefCount() + trTemp.getRefCount());
				tr.setSaleQuantity(tr.getSaleQuantity() + trTemp.getSaleQuantity());
				tr.setShownQuantity(tr.getShownQuantity() + trTemp.getShownQuantity());
				tr.setCancelledQuantity(tr.getCancelledQuantity() + trTemp.getCancelledQuantity());
				tr.setMovedQuantity(tr.getMovedQuantity() + trTemp.getMovedQuantity());
			}

			tempMap.put(d.getDealerId(), tr);
		}

		List<TrainingReport1> out = new ArrayList<>();

		for (Entry<Long, TrainingReport1> e : tempMap.entrySet()) {
			Staff stf = stfMap.get(e.getKey());
			if (stf != null) {
				TrainingReport1 tr1 = e.getValue();
				if(tr1.getSaleQuantity() > 0){
					tr1.setSalePerDemoQuantity(tr1.getShownQuantity()/tr1.getSaleQuantity());
				}
				
				Double tempQ = tr1.getShownQuantity()+tr1.getCancelledQuantity()+tr1.getMovedQuantity();
				if(tempQ > 0){
					tr1.setRefPerDemoQuantity(tr1.getRefCount().doubleValue()/tempQ);
				}
				e.getValue().setDealerName(stf.getLF());
				out.add(e.getValue());
			}
		}
		//System.out.println("SIZE: " + out.size());
		return out;
	}

}