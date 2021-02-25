package general.dao.impl;

import general.dao.ReqEventLogDao;
import general.tables.ReqEventLog;
import general.tables.Staff;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;
@Component("reqEventLogDao")
public class ReqEventLogDaoImpl extends GenericDaoImpl<ReqEventLog> implements ReqEventLogDao{

	@Override
	public List<ReqEventLog> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ReqEventLog> findRequestEvents(Long reqId) {
		Query query = this.em
				.createQuery("SELECT e,s FROM ReqEventLog e LEFT JOIN e.staff s WHERE e.request_id= :request_id ORDER BY e.datetime DESC");
		query.setParameter("request_id", reqId);
		List<Object[]> result = query.getResultList();
		List<ReqEventLog> out = new ArrayList<ReqEventLog>();
		for (Object[] o : result) {
			ReqEventLog e = (ReqEventLog) o[0];
			Staff s = (Staff) o[1];
			if (s == null) {
				e.setStaff(new Staff());
			} else {
				e.setStaff(s);
			}

			out.add(e);
		}
		return out;
	}

}
