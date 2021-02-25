package general.services.reports;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.dao.ContractTypeDao;
import general.dao.CustomerDao;
import general.tables.ContractType;
import general.tables.report.CustomerReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("analyticsReportService")
public class AnalyticsReportServiceImpl implements AnalyticsReportService {

	@Autowired
	CustomerDao customerDao;

	@Autowired
	ContractTypeDao conTypeDao;

	@Override
	public List<CustomerReport> getRep1Data(String bukrs, Long branchId, int day, int month) {
		List<Object[]> result = customerDao.findAnalyticsRep1Data(bukrs, branchId, day, month);
		List<ContractType> conTypeList = conTypeDao.findAll();
		Map<Long, ContractType> conTypeMap = new HashMap<>();
		for (ContractType ct : conTypeList) {
			conTypeMap.put(ct.getContract_type_id(), ct);
		}

		List<CustomerReport> out = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		int currYear = cal.get(Calendar.YEAR);
		for (Object[] r : result) {
			CustomerReport cr = new CustomerReport();
			cr.setId(Long.parseLong(String.valueOf(r[0])));
			cr.setFirstname(String.valueOf(r[1]));
			cr.setMiddlename(String.valueOf(r[2]));
			cr.setLastname(String.valueOf(r[3]));
			cr.setBirthday((Date) r[4]);
			cr.setContractId(Long.parseLong(String.valueOf(r[5])));
			String temp = "";
			if (conTypeMap.containsKey(Long.parseLong(String.valueOf(r[6])))) {
				temp = conTypeMap.get(Long.parseLong(String.valueOf(r[6]))).getName();
			}
			cr.setPurchasedGoods(temp);

			cal.setTime(cr.getBirthday());
			int birthYear = cal.get(Calendar.YEAR);
			cr.setAge(currYear - birthYear);
			cr.setPurchasedDate((Date) r[7]);
			cr.setTelephone(String.valueOf(r[8]));
			out.add(cr);
		}

		return out;
	}

}