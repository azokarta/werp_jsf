package general.services; 

import general.dao.DAOException;
import general.dao.MainPageFoldersDao;
import general.tables.MainPageFolders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("mainPageFoldersService")
public class MainPageFoldersServiceImpl implements MainPageFoldersService{
	
	@Autowired
    private MainPageFoldersDao dao;

	private String validate(MainPageFolders mpf){
		if(mpf.getName_ru() == null || mpf.getName_ru().length() == 0){
			return "Заполните поле Название";
		}
		
		return "";
	}
	
	@Override
	public void update(MainPageFolders mpf) throws DAOException {
		String error = validate(mpf);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		dao.update(mpf);
	}

	@Override
	public void create(MainPageFolders mpf) throws DAOException {
		String error = validate(mpf);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		dao.create(mpf);
	}
	
}