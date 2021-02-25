//package general.services;
//
//import java.util.Calendar;
//
//import general.dao.DAOException; 
//import general.dao.FixedAssetDao;
//import general.tables.FixedAsset;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//
//@Service("fixedAssetService")
//public class FixedAssetServiceImpl implements FixedAssetService{
//	@Autowired
//    private FixedAssetDao dao;
//
//	@Override
//	public void createAsset(FixedAsset f) throws DAOException {
//		String error = validateAsset(f, true);
//		
//		if(error.length() > 0){
//			throw new DAOException(error);
//		}
//		dao.create(f);
//	}
//	
//	private String validateAsset(FixedAsset f,boolean isNew){
//		String error = "";
//		if(f.getBranch_id() == 0L)
//		{
//			error += "Поле Филиал обязательно для заполнение" + "\n";
//		}
//		
//		if(f.getFa_name() == null || f.getFa_name().isEmpty()){
//			error += "Поле Наименование обязательно для заполнение" + "\n";
//		}
//		
//		if(f.getInit_amount() == 0D)
//		{
//			error += "Поле Первоначальная стоимость обязательно для заполнение" + "\n";
//		}
//		
//		if(f.getAsset_catalog_id() == 0L)
//		{
//			error += "Поле Группа учета ОС обязательно для заполнение" + "\n";
//		}
//		
//		if(f.getAsset_life() == 0)
//		{
//			error += "Поле Срок полезного использования обязательно для заполнение" + "\n";
//		}
//		
//		if(f.getStaff_id() == null || f.getStaff_id() == 0L)
//		{
//			error += "Поле Ответственный обязательно для заполнение" + "\n";
//		}
//		
//		if(f.getOperation_date() == null)
//		{
//			error += "Поле Дата эксплуатации обязательно для заполнение" + "\n";
//		}
//		
//		if(isNew)
//		{
//			f.setCreated_date(Calendar.getInstance().getTime());
//		}
//		f.setUpdated_date(Calendar.getInstance().getTime());
//		
//		if(error.length() == 0){
//			if(isNew){
//				Integer maxCounter = dao.findMaxCounter(f.getBranch_id());
//				if(maxCounter == null){
//					maxCounter = 0;
//				}
//				f.setCounter(++maxCounter);
//				//System.out.println(f.getFa_sn().length());
//				f.setFa_sn(this.getPerparedSN(f));
//				
//			}else{
//				FixedAsset temp = dao.findOne(String.format("fa.fa_sn = '%s' AND fa.branch_id = %s", f.getFa_sn(),f.getBranch_id()));
//				if(temp != null && temp.getFa_id() != f.getFa_id()){
//					error += "Материал с таким номером уже существует";
//				}
//			}
//		}
//		return error;
//	}
//
//	@Override
//	public void updateAsset(FixedAsset f) throws DAOException {
//		String error = validateAsset(f, false);
//		
//		if(error.length() > 0){
//			throw new DAOException(error);
//		}
//		dao.update(f);
//		//System.out.println("TTTEST");
//	}
//	
//	private String getPerparedSN(FixedAsset f){
//		return f.getBranch_id() + "-" + f.getCounter();
//	}
//}
