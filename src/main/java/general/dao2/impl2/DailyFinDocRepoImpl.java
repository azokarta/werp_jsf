package general.dao2.impl2;

import general.dao.impl.GenericDaoImpl;
import general.dao2.IDailyFinDocRepo;
import general.tables2.DailyFinDoc;
import org.springframework.stereotype.Component;

@Component("iDailyFinDocRepo")
public class DailyFinDocRepoImpl extends GenericDaoImpl<DailyFinDoc> implements IDailyFinDocRepo {
}