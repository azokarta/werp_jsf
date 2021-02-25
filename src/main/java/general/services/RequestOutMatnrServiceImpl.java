package general.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.Validation;
import general.dao.DAOException;
import general.dao.RequestOutMatnrDao;
import general.tables.RequestOutMatnr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("requestOutMatnrService")
public class RequestOutMatnrServiceImpl implements RequestOutMatnrService {
	
	@Autowired RequestOutMatnrDao romDao;

	@Override
	public void create(RequestOutMatnr rom) throws DAOException {
		
		
	}
	
	private String validate(List<RequestOutMatnr> romList){
		String error = "";
		for(RequestOutMatnr rom:romList){
			if(Validation.isEmptyLong(rom.getMatnr())){
				error += "Выберите материал" + "\n";
			}
			
			if(rom.getQuantity() < 1D){
				error += "Заполните поле Количество" + "\n";
			}
			
			if(Validation.isEmptyLong(rom.getRequest_id())){
				error += "Выберите заявку" + "\n";
			}
			
			if(error.length() > 0){
				return error;
			}
		}
		return error;
	}

	@Override
	public void create(List<RequestOutMatnr> romList) throws DAOException {
		String error = validate(romList);
		
		if(romList == null || romList.size() == 0){
			error += "Список материалов пуст";
		}
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		Long reqId = romList.get(0).getRequest_id();
		
		Map<Long, RequestOutMatnr> romMap = new HashMap<Long, RequestOutMatnr>();
		List<RequestOutMatnr> oldRomList = romDao.findAll(" request_id = " + reqId);

		for (RequestOutMatnr rom : romList) {
			if(!Validation.isEmptyLong(rom.getId())){
				romDao.update(rom);
				romMap.put(rom.getId(), rom);
				continue;
			}
			boolean isExisted = false;
			if (oldRomList != null) {
				for (int i = 0; i < oldRomList.size(); i++) {
					RequestOutMatnr oldRom = oldRomList.get(i);
					if (oldRom.getMatnr().equals(rom.getMatnr())) {
						rom.setId(oldRom.getId());
						romDao.update(rom);
						isExisted = true;
						romMap.put(rom.getId(), rom);
						break;
					}
				}
			}

			if (!isExisted) {
				romDao.create(rom);
				romMap.put(rom.getId(), rom);
			}
		}
		
		if (oldRomList != null) {
			for(RequestOutMatnr rom:oldRomList){
				if(!romMap.containsKey(rom.getId())){
					romDao.delete(rom.getId());
				}
			}
		}
	}
	
}