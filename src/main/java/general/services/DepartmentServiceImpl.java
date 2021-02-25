package general.services;
 

import general.dao.BonusArchiveDao;
import general.dao.DAOException; 
import general.dao.DepartmentDao;
import general.tables.Department;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("departmentService")
public class DepartmentServiceImpl implements DepartmentService{
	
    @Autowired
    private DepartmentDao dao;

    private String validate(Department d){
        String error = "";
        if(d.getName_ru().length() == 0){
            error += "Название (рус) обязательно для заполнение" + "\n";
        }
        
        return error;
    }
    
    @Override
    public void create(Department d) throws DAOException {
        String error = this.validate(d);
        if(error.length() > 0){
            throw new DAOException(error);
        }
        dao.create(d);
    }

    @Override
    public void update(Department d) throws DAOException {
        String error = this.validate(d);
        if(error.length() > 0){
            throw new DAOException(error);
        }
        dao.update(d);
    }
}
