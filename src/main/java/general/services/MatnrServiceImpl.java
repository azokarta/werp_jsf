package general.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.Helper;
import general.Validation;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.MatnrSparePartDao;
import general.dao.MatnrWarDao;
import general.dao.UserDao;
import general.tables.EventLog;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.MatnrSparePart;
import general.tables.MatnrWar;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("matnrService")
public class MatnrServiceImpl implements MatnrService {

	@Autowired
	MatnrDao mDao;
	
	@Autowired
	MatnrListDao mlDao;
	
	@Autowired
	MatnrSparePartDao mspDao;
	
	@Autowired
	MatnrWarDao mwDao;
	

	
	@Autowired
	UserDao userDao;
	


	@Autowired
	private EventLogService eventLogService;
	
	
	@Override
	public void create(Matnr m,List<Long> catIds, MatnrWar mw) throws DAOException {
		String error = this.validate(m, true);
		error += validateMw(m, mw, true);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		mDao.create(m);
		
		for(Long l:catIds){
			MatnrSparePart msp = new MatnrSparePart();
			msp.setSparepart_id(m.getMatnr());
			msp.setTovar_id(l);
			mspDao.create(msp);
		}
		
		mw.setMatnr_id(m.getMatnr());
		mwDao.create(mw);
	}

	private String validate(Matnr m, boolean isNew){
		String error = "";
//		if(Validation.isEmptyString(m.getBukrs())){
//			error += "Выберите компанию" + "\n";
//		}
		
		if(Validation.isEmptyString(m.getText45())){
			error += "Заполните поле Название" + "\n";
		}
		
		if(m.getType() == 0){
			error += "Выберите тип материала" + "\n";
		}
		
		if(Validation.isEmptyString(m.getCode())){
			error += "Заполните поле Код" + "\n";
		}
		
		if(isNew && !Validation.isEmptyString(m.getCode())){
			List<Matnr> l = mDao.findAll("code = '" + m.getCode() + "'");
			if(l.size() > 0){
				error += "В справочнике имеется материал с таким кодом" + "\n";
			}
		}
		
		return error;
	}
	
	private String validateMw(Matnr m, MatnrWar mw, boolean isNew){
		String error = "";
		System.out.println(mw.getWar_months() + " WAR MONTJS");
		if(mw.getWar_months() > 0){
			if(mw.getFrom_date() == null){
				error += "Введите дату (в гарант.)" + "\n";
			}
		}
		return error;
	}

	@Override
	public void update(Matnr m,List<Long> catIds, MatnrWar mw,MatnrWar oldMatnr, Long userId) throws DAOException {
		String error = this.validate(m, false);
		error += validateMw(m, mw, false);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		mDao.update(m);
		

        //method 2 - via Date
        Date date = new Date();
        
		EventLog el = new EventLog();
		el.setBukrs("1000");
		el.setDatetime(new Timestamp(date.getTime()));
		el.setMsg("Matnr ID:"+m.getMatnr()+" New accLim:"+m.getAccount_limit()+" new IsMem:"+m.getIs_m());
		el.setStaff_id(userDao.find(userId).getStaff_id());
		el.setTransaction_id(114L);
		el.setType(1);		
		eventLogService.create(el);
		
		
		
		List<MatnrSparePart> mspList = mspDao.findAll(" sparepart_id = " + m.getMatnr());
		List<Long> existsCatIds = new ArrayList<Long>();
		for(MatnrSparePart msp:mspList){
			if(catIds.contains(msp.getTovar_id())){
				existsCatIds.add(msp.getTovar_id());
			}else{
				mspDao.delete(msp.getId());
			}
		}
		
		
		for(Long l:catIds){
			if(existsCatIds.contains(l)){
				continue;
			}
			MatnrSparePart msp = new MatnrSparePart();
			msp.setSparepart_id(m.getMatnr());
			msp.setTovar_id(l);
			mspDao.create(msp);
		}
		
		if(mw.getWar_months() > 0){
			List<MatnrWar> mwList = mwDao.findAll(" matnr_id = " + m.getMatnr() + " AND from_date = '" + Helper.getFormattedDateForDb(mw.getFrom_date()) + "' ");
			if(mwList.size() > 0){
				throw new DAOException("Данные гарантии существует!");
			}
			
			mw.setMatnr_id(m.getMatnr());
			mwDao.create(mw);
			if(oldMatnr != null){
				mwDao.update(oldMatnr);
			}
		}
	}

	@Override
	public List<MatnrList> getMatnrListWithMatnr(String cond, String bukrs) {
		List<MatnrList> mlList = mlDao.dynamicFind(cond);
		Map<Long, MatnrList> mlListMap = new HashMap<Long, MatnrList>();
		for(MatnrList ml:mlList){
			mlListMap.put(ml.getMatnr(), ml);
		}
		/*int matnrSize = out.size();
		String[] matnrIds = new String[matnrSize];
		int temp = 0;
		for(MatnrList ml:out){
			matnrIds[temp] = ml.getMatnr().toString();
			bukrs = ml.getBukrs();
			temp++;
		}
		*/
		String condition = "";
		
		/*if(matnrIds.length > 1000){
			int chunkSize = (int)Math.ceil(matnrIds.length/1000D);
			String[] conditions = new String[chunkSize];
			int counter = 0;
			for(int k = 0; k < matnrIds.length; k += 1000){
				conditions[counter] = String.format(" matnr NOT IN (%s) ", "'" + String.join("','", Arrays.copyOfRange(matnrIds, k, (k+1000 < matnrIds.length ? k+1000 : matnrIds.length))) + "'");
				counter++;
			}
			condition = "( " + String.join(" AND ", conditions) + " ) ";
		}else{
			condition = String.format("matnr NOT IN(%s) ", "'" + String.join("','", matnrIds) + "'");
		}*/
		
		if(bukrs != null && bukrs.length() > 0){
			//condition += (condition.length() > 0 ? " AND " : " ") + " bukrs = '" + bukrs + "' ";
			condition = " bukrs = '" + bukrs + "' ";
		}
		
		List<Matnr> tempList = mDao.findAll(condition);
		for(Matnr m:tempList){
			if(mlListMap.containsKey(m.getMatnr())){
				mlListMap.get(m.getMatnr()).setMatnrCode(m.getCode());
				mlListMap.get(m.getMatnr()).setMatnrName(m.getText45());
			}else{
				MatnrList ml = new MatnrList();
				ml.setMatnrCode(m.getCode());
				ml.setMatnr(m.getMatnr());
				ml.setMatnrName(m.getText45());
				ml.setMenge(0);
				mlList.add(ml);
			}
			
			//out.add(ml);
		}
		
		return mlList;
	}
	
}