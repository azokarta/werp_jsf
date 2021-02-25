package general.dao2.impl2;

import org.springframework.stereotype.Component;

import general.dao.impl.GenericDaoImpl;
import general.dao2.PrikazLogDao;
import general.tables2.PrikazLog;



@Component("prikazLogDao")
public class PrikazLogDaoImpl extends GenericDaoImpl<PrikazLog> implements PrikazLogDao{

}
