package general.dao.impl;

import general.dao.ServZFBranchMTDao;
import general.tables.ServZFBranchMT;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("servZFBranchMTDao")
public class ServZFBranchMTDaoImpl extends GenericDaoImpl<ServZFBranchMT>
		implements ServZFBranchMTDao {

	@Override
	public List<ServZFBranchMT> findAll() {
		String q = "Select c FROM ServZFBranchMT c";
		Query query = this.em.createQuery(q);
		List<ServZFBranchMT> szfbmt = query.getResultList();
		return szfbmt;
	}

	@Override
	public List<ServZFBranchMT> findAll(String cond) {
		String q = "Select c FROM ServZFBranchMT c ";
		if (cond != null && cond.length() > 1)
			q += "WHERE " + cond;
		Query query = this.em.createQuery(q);
		List<ServZFBranchMT> szfbmt = query.getResultList();
		return szfbmt;
	}

	public ServZFBranchMT findLastMTByBranch(Long a_branchId) {
		String q = "Select c FROM ServZFBranchMT c WHERE"
				+ " c.branch_id = " + a_branchId
				+ " and c.date_from = some ("
				+ "select max(cc.date_from) FROM ServZFBranchMT cc "
				+ "WHERE cc.branch_id  = c.branch_id"
				+ ")";
		Query query = this.em.createQuery(q);
		List<ServZFBranchMT> szfbmt = query.getResultList();
		if (szfbmt.size() > 0)
			return szfbmt.get(0);
		else
			return null;
	}

}
