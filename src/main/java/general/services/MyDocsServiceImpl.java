package general.services;

import java.util.Calendar;
import java.util.List;

import general.dao.DAOException;
import general.dao.MyDocsDao;
import general.tables.IMdTable;
import general.tables.MyDocs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.User;

@Service("myDocsService")
public class MyDocsServiceImpl implements MyDocsService {

	@Autowired
	MyDocsDao mdDao;

	@Override
	public void create(MyDocs md, User userData) throws DAOException {
		String error = validate(md, userData);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		List<MyDocs> mdList = mdDao
				.findAll(String.format(" context = '%s' AND context_id = %d AND owner = %d AND branch_id = %d ",
						md.getContext(), md.getContext_id(), md.getOwner(), md.getBranch_id()));
		if (mdList != null && mdList.size() > 0) {
			MyDocs oldMd = mdList.get(0);
			oldMd.setStatus_id(md.getStatus_id());
			mdDao.update(oldMd);
		} else {
			mdDao.create(md);
		}
	}

	private String validate(MyDocs md, User userData) {
		String error = "";

		md.setCreated_at(Calendar.getInstance().getTime());
		md.setCreated_by(userData.getUserid());

		return error;
	}

	@Override
	public void addMd(IMdTable imd, User userData, Long owner, Integer status) {
		// System.out.println("IMPLEMENTs: " + (imd2 instanceof AbMdTable));
		List<MyDocs> mdList = mdDao.findAll(String.format(" context = '%s' AND context_id = %d AND owner = %d ",
				imd.getContext(), imd.getContextId(), owner));
		if (mdList != null && mdList.size() > 0) {
			MyDocs oldMd = mdList.get(0);
			oldMd.setStatus_id(status);
			mdDao.update(oldMd);
		} else {
			MyDocs md = new MyDocs();
			md.setBranch_id(imd.getBranchId());
			md.setBukrs(imd.getBukrs());
			md.setContext(imd.getContext());
			md.setContext_id(imd.getContextId());
			md.setCreated_at(Calendar.getInstance().getTime());
			md.setCreated_by(userData.getUserid());
			md.setOwner(owner);
			md.setStatus_id(status);
			mdDao.create(md);
		}
	}

	@Override
	public void removeMd(IMdTable imd, Long owner) throws DAOException {
		List<MyDocs> mdList = mdDao.findAll(String.format(" context = '%s' AND context_id = %d AND owner = %d ",
				imd.getContext(), imd.getContextId(), owner));
		for (MyDocs md : mdList) {
			mdDao.delete(md.getId());
		}
	}

	@Override
	public void removeAllFromMd(IMdTable imd) throws DAOException {
		List<MyDocs> mdList = mdDao
				.findAll(String.format(" context = '%s' AND context_id = %d ", imd.getContext(), imd.getContextId()));
		for (MyDocs md : mdList) {
			mdDao.delete(md.getId());
		}
	}
}