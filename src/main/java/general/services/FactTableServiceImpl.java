package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import general.dao.DAOException;
import general.dao.FactTableDao;
import general.dao.PyramidDao;
import general.tables.FactTable;
import general.tables.Pyramid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("factTableService")
public class FactTableServiceImpl implements FactTableService {
	@Autowired
	FactTableDao dao;

	@Autowired
	PyramidDao pDao;

	private static final Long POSITION_CORDINATOR = 5L;
	private static final Long POSITION_DIRECTOR = 10L;
	private static final Long POSITION_MANAGER = 3L;
	private static final Long POSITION_DEALER = 4L;
	private static final Long POSITION_CHIEF_EDUCATOR = 15L;
	private static final Long POSITION_EDUCATOR = 12L;

	List<Pyramid> pList = new ArrayList<Pyramid>();
	private int year = Calendar.getInstance().get(Calendar.YEAR);
	private int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
	private List<FactTable> fList = new ArrayList<FactTable>();
	private Double sum = 0D;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	private Long fitterId = 0L; // ID установщика
	private Long secretaryId = 0L; // ID Секретаря
	private Long sellerId = 0L; // ID сотрудника который продал
								// (Дилер|Менеджер|Директор)

	@Override
	public void incrementCounter(Long dealerId, Long fitterId, Long secretaryId)
			throws DAOException {
		this.fitterId = fitterId;
		this.secretaryId = secretaryId;
		this.sellerId = dealerId;

		this.fList = new ArrayList<FactTable>();
		if (fitterId > 0L) {
			this.fList.add(this.getBlank(fitterId));
		}
		if (secretaryId > 0) {
			this.fList.add(this.getBlank(secretaryId));
		}
		this.loadPyramidList(true);
		this.change(true);
	}

	public void change(boolean isIncrement) {
		if (this.sellerId == 0) {
			return;
		}
		for (FactTable f : fList) {
			boolean isExisted = false;
			FactTable fNew = new FactTable();
			List<FactTable> l = dao
					.findAll(String
							.format("staff_id = %d AND year = %d AND month = %d AND pyramid_id = %d",
									f.getStaff_id(), this.getYear(),
									this.getMonth(), f.getPyramid_id()));
			if (l.size() > 0) {
				fNew = l.get(0);
				fNew.setGroup_count(f.getGroup_count() + fNew.getGroup_count());
				isExisted = true;
			} else {
				fNew = f;
			}

			if (isIncrement) {
				if (sellerId == fNew.getStaff_id().longValue()
						|| fNew.getStaff_id().longValue() == fitterId
						|| fNew.getStaff_id().longValue() == secretaryId) {
					fNew.setSale_count(fNew.getSale_count() + 1);
				}
			} else {
				if (fNew.getStaff_id().longValue() == sellerId) {
					// System.out.println("ddd: " + fNew.getCancel_count() +
					// " => " + fNew.getStaff_id());
					fNew.setCancel_count(fNew.getCancel_count() + 1);
				}
			}

			fNew.setSumm(fNew.getSumm()+this.sum);

			if (isExisted) {
				dao.update(fNew);
			} else {
				dao.create(fNew);
			}
		}
	}

	private Pyramid sellerPyramid;

	private void loadPyramidList(boolean isIncrement) {
		if (this.sellerId == 0) {
			return;
		}
		// System.out.println(this.sellerId);
		Long tempId = this.sellerId;
		int i = 0;
		Pyramid p = null;
		do {
			if (i == 0) {
				p = pDao.findOne("staff_id", tempId);
			} else {
				p = pDao.find(tempId);
			}

			if (p == null) {
				break;
			}
			if (i == 0) {
				this.sellerPyramid = p;
			}
			if (p.getStaff_id() != null) {
				FactTable f = this.getBlank(i == 0 ? tempId : p.getStaff_id());
				if (i != 0) {
					f.setGroup_count(isIncrement ? 1 : -1);
				}

				this.fList.add(f);
			}

			tempId = p.getParent_pyramid_id();
			if (p.getPosition_id().longValue() == POSITION_CORDINATOR
					|| p.getParent_pyramid_id().intValue() == 0) {
				break;
			}

			if (i > 10) {
				break;
			}
			i++;
		} while (true);
	}

	private FactTable getBlank(Long staffId) {
		Pyramid p = pDao.findOne("staff_id", staffId);
		Long pyramidId = p == null ? 0L : p.getPyramid_id();
		FactTable f = new FactTable();
		f.setStaff_id(staffId);
		f.setYear(this.year);
		f.setMonth(this.month);
		f.setPyramid_id(pyramidId);
		if(p != null){
			f.setBranch_id(p.getBranch_id());
			f.setBusiness_area_id(p.getBusiness_area_id());
			f.setPosition_id(p.getPosition_id());
		}
		return f;
	}

	@Override
	public void decrementCounter(Long dealerId) throws DAOException {
		this.fList = new ArrayList<FactTable>();
		this.sellerId = dealerId;
		this.loadPyramidList(false);
		this.change(false);
	}

	/** Для взонсщиков ***/
	@Override
	public void incrementCounter(Long staffId, Double sum) throws DAOException {

		List<FactTable> l = dao.findAll(String.format(
				"staff_id = %d AND year = %d AND month = %d", staffId,
				this.getYear(), this.getMonth()));
		if (l.size() > 0) {
			FactTable f = l.get(0);
			f.setSumm(f.getSumm() + sum);
			dao.update(f);
		} else {
			FactTable f = this.getBlank(staffId);
			f.setSumm(f.getSumm() + sum);
			dao.create(f);
		}
	}

	private void loadEducators(boolean isIncrement) {
		if (this.sellerPyramid == null) {
			return;
		}
		for (Pyramid p : pDao
				.dynamicFindPyramid(String
						.format("(position_id = %d OR position_id = %d ) AND business_area_id = %d",
								POSITION_CHIEF_EDUCATOR, POSITION_EDUCATOR,
								sellerPyramid.getBusiness_area_id()))) {
			boolean add = false;
			if (p.getPosition_id().longValue() == POSITION_CHIEF_EDUCATOR) {
				add = true;
			} else if (p.getBranch_id().longValue() == sellerPyramid
					.getPosition_id()) {
				add = true;
			}

			if (add) {
				FactTable fTemp = this.getBlank(p.getStaff_id());
				fTemp.setGroup_count(isIncrement ? 1 : -1);
				this.fList.add(fTemp);
			}
		}
	}

	@Override
	public void addSale(Long sellerId, Long secretaryId) throws DAOException {
		this.sum = 0D;
		this.sellerId = sellerId;
		this.secretaryId = secretaryId;
		this.fList = new ArrayList<FactTable>();
		this.fList.add(this.getBlank(secretaryId));
		//System.out.println("SEC: " + secretaryId + " => " + sellerId);
		this.loadPyramidList(true);
		this.loadEducators(true);
		this.change(true);
	}

	@Override
	public void addService(Long staffId, Double sum) throws DAOException {
		this.sum = sum;
		this.sellerId = staffId;
		this.fList = new ArrayList<FactTable>();
		this.fList.add(this.getBlank(this.sellerId));
		this.loadPyramidList(true);
		this.change(true);
	}

	@Override
	public void addPayment(Long staffId, Long branchId, Long positionId,
			Long businessAreaId, String waers, Double sum, boolean b) throws DAOException {
		this.sum = 0D;
		List<FactTable> ftList = dao
				.findAll(String
						.format("staff_id = %d AND branch_id = %d AND position_id = %d AND business_area_id = %d AND waers = '%s' AND year=%d AND month = %d",
								staffId, branchId, positionId, businessAreaId,
								waers,this.year,this.month));
		FactTable ft = ftList.size() > 0 ? ftList.get(0) : null;
		if(ft == null){
			ft = this.getBlank(staffId);
			ft.setBranch_id(branchId);
			ft.setBusiness_area_id(businessAreaId);
			ft.setWaers(waers);
			ft.setPosition_id(positionId);
		}
		
		if(b){
			ft.setComplete_summ(ft.getComplete_summ()+sum);
		}else{
			ft.setSumm(ft.getSumm()+sum);
		}
	}
}