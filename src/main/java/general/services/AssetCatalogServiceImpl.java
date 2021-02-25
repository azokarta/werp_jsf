package general.services;
 
import java.util.Calendar;

import general.dao.AssetCatalogDao;
import general.dao.DAOException; 
import general.tables.AssetCatalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("assetCatalogService")
public class AssetCatalogServiceImpl implements AssetCatalogService{
	@Autowired
    private AssetCatalogDao dao;
	

	private String validate(AssetCatalog c, boolean isNew)
	{
		String error = "";
		if(c.getName_ru().isEmpty())
		{
			error += "Please enter catalog name ru" + "\n";
		}
		
		if(isNew){
			c.setCreated_date(Calendar.getInstance().getTime());
		}
		
		c.setUpdated_date(Calendar.getInstance().getTime());
		
		return error;
	}
	
	@Override
	public void createCatalog(AssetCatalog c) throws DAOException {
		
		String error = validate(c, true);
		if(error.length() > 0)
		{
			throw new DAOException(error);
		}
		
		dao.create(c);
	}
	
	@Override
	public void updateCatalog(AssetCatalog c) throws DAOException {
		String error = validate(c, false);
		if(error.length() > 0)
		{
			throw new DAOException(error);
		}
		
		dao.update(c);
	}	
}
