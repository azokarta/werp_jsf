package general.services;

import java.util.Calendar;
import java.util.List;

import general.Helper;
import general.Validation;
import general.dao.DAOException;
import general.dao.PyramidDao;
import general.tables.Pyramid;
import general.tables.Salary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("pyramidService")
public class PyramidServiceImpl implements PyramidService {
	@Autowired
	private PyramidDao pyrDao;

	@Override
	public List<Pyramid> dynamicSearchPyramid(Pyramid a_pyramid)
			throws DAOException {
		try {
			String dynamicWhere = "";

			a_pyramid.setBukrs(a_pyramid.getBukrs().replaceAll("\\s", ""));
			if (!(a_pyramid.getBukrs().equals(null))
					&& !(a_pyramid.getBukrs().equals("0"))) {
				dynamicWhere = dynamicWhere + "bukrs = '"
						+ a_pyramid.getBukrs() + "'";
			} else {
				throw new DAOException("Выберите компанию");
			}

			String regx = ";";
			char[] ca = regx.toCharArray();
			for (char c : ca) {
				dynamicWhere = dynamicWhere.replace("" + c, "");
			}

			return pyrDao.dynamicFindPyramid(dynamicWhere);
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	public Pyramid findRoot(String bukrs) {
		return pyrDao.findRoot(bukrs);
	}

	public List<Pyramid> getRootList(String bukrs, Long branchId) {
		return pyrDao.getRootList(bukrs, branchId);
	}

	public Pyramid findOne(String c, Long v) {
		try {
			return pyrDao.findOne(c, v);
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public void createPyramid(Pyramid p) {
		String error = validatePyramid(p, true);
		if (error.length() > 0) {
			throw new DAOException(error);
		}
		
		pyrDao.create(p);
	}

	private String validatePyramid(Pyramid p, boolean isNew) {
		String error = "";

		if (p.getStaff_id() == null || p.getStaff_id() == 0L) {
			// error += "Invalid Staff Id \n";
			p.setStaff_id(0L);
		}

		if (Validation.isEmptyString(p.getBukrs())) {
			error += "Invalid Bukrs \n";
		}

		if (p.getBranch_id() == null || p.getBranch_id().longValue() == 0L) {
			error += "Invalid Branch \n";
		}

		if (p.getBusiness_area_id() == null || p.getBusiness_area_id() == 0) {
			// error += "Invalid Business Area Id \n";
			p.setBusiness_area_id(0L);
		}

		if (p.getPosition_id() == null || p.getPosition_id() == 0) {
			// error += "Invalid Position Id \n";
			p.setPosition_id(0L);
		}

		if (isNew) {
			p.setCreated_date(Calendar.getInstance().getTime());
		}
		p.setUpdated_date(Calendar.getInstance().getTime());
		p.setMonth(Helper.getCurrentMonth());
		p.setYear(Helper.getCurrentYear());

		if (isNew) {
			List<Pyramid> pList = pyrDao.dynamicFindPyramid(" staff_id = "
					+ p.getStaff_id() + "  ");
			if (pList.size() > 0) {
				for (Pyramid pyr : pList) {
					if (pyr.getParent_pyramid_id() == p.getParent_pyramid_id()
							&& pyr.getParent_pyramid_id() == 0) {
						error += "Сотрудник уже в иерархии. \n Выберите другого сотрудника \n";
						break;
					}
				}
			}
		}

		return error;
	}

	@Override
	public void updatePyramid(Pyramid p) {
		String error = validatePyramid(p, false);
		if (error.length() > 0) {
			throw new DAOException(error);
		}
		pyrDao.update(p);
	}

	@Override
	public void deletePyramid(Long id) throws DAOException {
		List<Pyramid> l = pyrDao.dynamicFindPyramid(" parent_pyramid_id = "
				+ id);
		if (l.size() > 0) {
			throw new DAOException(
					"Вы не можете удалить эту ветку, так как под ним есть сотрудники!");
		}
		pyrDao.delete(id);
	}

	@Override
	public void createPyramid(Pyramid p, Salary s) throws DAOException {
		// TODO Auto-generated method stub

	}
}