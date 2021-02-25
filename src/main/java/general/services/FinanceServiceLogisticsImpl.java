package general.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import general.GeneralUtil;
import general.dao.BkpfDao;
import general.dao.BranchDao;
import general.dao.BsegDao;
import general.dao.DAOException;
import general.dao.InvoiceItemDao;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.OrderDao;
import general.dao.OrderMatnrDao;
import general.dao.RelatedDocsDao;
import general.output.tables.InvoiceItemWithMatnr;
import general.output.tables.InvoiceListItemFkage;
import general.tables.Bkpf;
import general.tables.Branch;
import general.tables.Bseg;
import general.tables.Bsik;
import general.tables.Invoice;
import general.tables.Matnr;














import logistics.LogFinClass;

import general.tables.Order;
import general.tables.OrderMatnr;
import general.tables.RelatedDocs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service("financeServiceLogistics")
public class FinanceServiceLogisticsImpl implements FinanceServiceLogistics{
	@Autowired
    private BkpfDao bkpfDao;
		
	@Autowired
    private BsegDao bsegDao;
	
	@Autowired
    private MatnrDao matnrDao;
	
	@Autowired
    private FinanceService financeService;
	
	@Autowired
    private OrderMatnrDao ordMatDao;
	
	@Autowired
    private OrderDao ordDao;
	
	@Autowired
    private RelatedDocsDao relDocsDao;
	
	@Autowired
    private InvoiceItemDao invItDao;
	
	
	@Autowired
    private MatnrListDao matnrListDao;
	
	@Autowired
    private InvoiceService invoiceService;

	@Autowired
	private BranchDao branchDao;
	
	
	//GS dogovor
	//GC service
	//GW spisanie
	//RG return
	//PE peremeshenie
	public Long removeMatnrFromWerks (List<LogFinClass> l_mls,Long  GC_GS_AK_awkey, String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks) throws DAOException{
		try{
			Bkpf a_bkpf = new Bkpf();
			a_bkpf = bkpfDao.findOriginalSingleBkpf(GeneralUtil.getPreparedBelnr(GC_GS_AK_awkey), GeneralUtil.getPreparedGjahr(GC_GS_AK_awkey),a_bukrs);
			
			if (a_bkpf==null)
			{
				throw new DAOException("Финансовый документ не найден.");
			}
			
			if (a_bkpf.getStorno()==1)
			{
				throw new DAOException("Финансовый документ отменен, к списанию не подлежит.");
			}
			if (!(a_bkpf.getBlart().equals("GS") || a_bkpf.getBlart().equals("GC") || a_bkpf.getBlart().equals("AK") || a_bkpf.getBlart().equals("G2")))
			{
				throw new DAOException("Финансовый документ не относится к продаже.");
			}
			
			if(werks==null || werks==0)
			{
				throw new DAOException("Склад не указен.");
			}
			
			List<Bkpf> l_bkpf_GW = new ArrayList<Bkpf>();
			List<Bseg> l_bseg_GW = new ArrayList<Bseg>();
			l_bkpf_GW = bkpfDao.dynamicFindBkpf(" blart='GW' and awkey2=null and storno = 0 and awkey = "+GeneralUtil.getPreparedAwkey(a_bkpf.getBelnr(), a_bkpf.getGjahr()));
			if (l_bkpf_GW.size()>0)
			{
				Map<Long,Double> l_matnr_map = new HashMap<Long,Double>();
				for (Bkpf wa_bkpf:l_bkpf_GW)
				{
					l_bseg_GW.addAll(bsegDao.dynamicFindBseg(" belnr = "+wa_bkpf.getBelnr()+" and gjahr = "+wa_bkpf.getGjahr()));
				}
				
				for (Bseg wa_bseg:l_bseg_GW)
				{
					if (wa_bseg.getHkont().startsWith("1330"))
					{
						double count = 0;
						if (l_matnr_map.get(wa_bseg.getMatnr())!=null)
						{
							count = l_matnr_map.get(wa_bseg.getMatnr());
							l_matnr_map.put(wa_bseg.getMatnr(), wa_bseg.getMenge()+count);
						}
						else
						{
							l_matnr_map.put(wa_bseg.getMatnr(), wa_bseg.getMenge());
						}
					}
				}
				
				for (LogFinClass wa_mls:l_mls)
				{
					if (wa_mls.getMenge()==0)
					{
						throw new DAOException("Количество материалов равно 0, Программа не может списать материал");
					}
					double count = 0;
					if (l_matnr_map.get(wa_mls.getMatnr())!=null)
					{
						count = l_matnr_map.get(wa_mls.getMatnr());
						
						count = count - wa_mls.getMenge();
						if (count<0)
						{
							Matnr wa_matnr = matnrDao.find(wa_mls.getMatnr());
							throw new DAOException("Все материалы уже списаны ("+wa_matnr.getText45()+")");
						}
						else
						{
							l_matnr_map.put(wa_mls.getMatnr(), count);
						}
					}
					
					
				}
			}
			
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_bsik = new ArrayList<Bsik>();
			
			Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis());
			p_bkpf.setBukrs(a_bukrs);
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setBlart("GW");
			p_bkpf.setBudat(curDate.getTime());
			p_bkpf.setBldat(curDate.getTime());
            p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
            p_bkpf.setCpudt(curDate.getTime());
            p_bkpf.setUsnam(a_userID);
            p_bkpf.setTcode(a_tcode);
            p_bkpf.setWaers("USD");
            p_bkpf.setKursf(1); 
            p_bkpf.setBrnch(a_branch_id);
            p_bkpf.setContract_number(a_bkpf.getContract_number());
            p_bkpf.setCustomer_id(a_bkpf.getCustomer_id());
            p_bkpf.setAwkey(GeneralUtil.getPreparedAwkey(a_bkpf.getBelnr(), a_bkpf.getGjahr()));
            p_bkpf.setAwtyp(1);
			
			
			for (LogFinClass wa_mls:l_mls)
			{
				//815	RAINBOW 13300001
				//816	REXWAT ATLAS 13300003
				//817	REXWAT ECO 13300002

				
				
				Bseg wa_bseg = new Bseg();
				wa_bseg.setHkont(get1330MatnrHkont(a_bukrs,wa_mls.getMatnr()," FinanceServiceLogistics -> removeMatnrFromWerks"));				
				if (wa_mls.getMenge()==0)
				{
					throw new DAOException("Количество материалов равно 0, Программа не может списать материал");
				}
				
				wa_bseg.setMatnr(wa_mls.getMatnr());
				wa_bseg.setMenge(wa_mls.getMenge());
				wa_bseg.setBschl("50");
				wa_bseg.setShkzg("H");
				wa_bseg.setDmbtr(wa_mls.getTotalDmbtr());
				wa_bseg.setBukrs(p_bkpf.getBukrs());
				wa_bseg.setGjahr(p_bkpf.getGjahr());
				wa_bseg.setLifnr(p_bkpf.getCustomer_id());
				wa_bseg.setWerks(werks);
				pl_bseg.add(wa_bseg);

				Bseg wa_bsegDebet = new Bseg();
				wa_bsegDebet.setHkont(get7010MatnrHkont(a_bukrs,wa_mls.getMatnr()," FinanceServiceLogistics -> removeMatnrFromWerks"));				
				wa_bsegDebet.setBschl("40");
				wa_bsegDebet.setShkzg("S");
				wa_bsegDebet.setDmbtr(wa_mls.getTotalDmbtr());
				wa_bsegDebet.setBukrs(p_bkpf.getBukrs());
				wa_bsegDebet.setGjahr(p_bkpf.getGjahr());
				wa_bsegDebet.setMatnr(wa_mls.getMatnr());
				wa_bsegDebet.setWerks(werks);
				pl_bseg.add(wa_bsegDebet);
				p_bkpf.setDmbtr(GeneralUtil.round(p_bkpf.getDmbtr()+wa_mls.getTotalDmbtr(), 2));
			}
			

			
			
			
			
			int wa_buzei=0;
			for(Bseg wa_bseg:pl_bseg)
			{
				wa_buzei++;
				wa_bseg.setBuzei(wa_buzei);
				System.out.print(wa_buzei+" ");
				System.out.print(wa_bseg.getShkzg()+" ");
				System.out.print(wa_bseg.getHkont()+" ");
				System.out.print(wa_bseg.getMatnr()+" ");
				System.out.println(wa_bseg.getDmbtr());
			}

		    financeService.check_empty_fields(p_bkpf, pl_bseg);
            Long wa_belnr =financeService.insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);            
            
            int gjahr = p_bkpf.getGjahr();
            Long wa_awkey = Long.parseLong( String.valueOf(wa_belnr)+String.valueOf(gjahr));            
			return wa_awkey;
            
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage()+" FinanceServiceLogistics -> removeMatnrFromWerks");
		}
	}
	

	
	public Long returnMatnrToWerks(List<LogFinClass> l_mls,Bkpf a_bkpf_GW, String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks) throws DAOException
	{//vozvrat posle prodazhi
		try{
			
			
			if (a_bkpf_GW.getStorno()==1)
			{
				throw new DAOException("Финансовый документ отменен, к возврату не подлежит.");
			}
			if (a_bkpf_GW.getBlart()==null || !a_bkpf_GW.getBlart().equals("GW"))
			{
				throw new DAOException("Выберите фин. документ по списанию");
			}
			
			if (a_bkpf_GW.getAwkey2()!=null)
			{
				throw new DAOException("Возврат состоялся по данному фин. документу");
			}
			
			
			if(werks==null || werks==0)
			{
				throw new DAOException("Склад не указен.");
			}
			//Bkpf p_bkpf_GC_or_GS = new Bkpf();
			//p_bkpf_GC_or_GS = bkpfDao.dynamicFindSingleBkpf(" belnr = "+GeneralUtil.getPreparedBelnr(a_bkpf_GW.getAwkey())+" and gjahr = "+GeneralUtil.getPreparedGjahr(a_bkpf_GW.getAwkey()));
			//if (p_bkpf_GC_or_GS==null || (!(p_bkpf_GC_or_GS.getBlart().equals("GS") || p_bkpf_GC_or_GS.getBlart().equals("GC") || p_bkpf_GC_or_GS.getBlart().equals("AK"))))
			//{
			//	throw new DAOException("Финансовый документ не относится к продаже.");
			//}
			
			
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_bsik = new ArrayList<Bsik>();
			
			Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis());
			p_bkpf.setBukrs(a_bukrs);
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setBlart("RG");
			p_bkpf.setBudat(curDate.getTime());
			p_bkpf.setBldat(curDate.getTime());
            p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
            p_bkpf.setCpudt(curDate.getTime());
            p_bkpf.setUsnam(a_userID);
            p_bkpf.setTcode(a_tcode);
            p_bkpf.setWaers("USD");
            p_bkpf.setKursf(1); 
            p_bkpf.setBrnch(a_branch_id);
            p_bkpf.setContract_number(a_bkpf_GW.getContract_number());
            p_bkpf.setCustomer_id(a_bkpf_GW.getCustomer_id());
            p_bkpf.setAwtyp(1);
            p_bkpf.setAwkey(GeneralUtil.getPreparedAwkey(a_bkpf_GW.getBelnr(), a_bkpf_GW.getGjahr()));
           // Map<Long,MatnrDmbtr> l_matnr_dmbtr = new HashMap<Long,MatnrDmbtr>();
			
			
			for (LogFinClass wa_mls:l_mls)
			{
				//815	RAINBOW 13300001
				//816	REXWAT ATLAS 13300003
				//817	REXWAT ECO 13300002

				
				Bseg wa_bseg = new Bseg();				
				wa_bseg.setHkont(get7010MatnrHkont(a_bukrs,wa_mls.getMatnr()," FinanceServiceLogistics -> returnMatnrToWerks"));		
				wa_bseg.setMatnr(wa_mls.getMatnr());
				wa_bseg.setMenge(wa_mls.getMenge());
				wa_bseg.setBschl("50");
				wa_bseg.setShkzg("H");
				wa_bseg.setDmbtr(wa_mls.getTotalDmbtr());
				wa_bseg.setBukrs(p_bkpf.getBukrs());
				wa_bseg.setGjahr(p_bkpf.getGjahr());
				wa_bseg.setWerks(werks);
				pl_bseg.add(wa_bseg);
				
				
				Bseg wa_bsegDebet = new Bseg();
				
				wa_bsegDebet.setHkont(get1330MatnrHkont(a_bukrs,wa_mls.getMatnr()," FinanceServiceLogistics -> returnMatnrToWerks"));		
				wa_bsegDebet.setBschl("40");
				wa_bsegDebet.setShkzg("S");
				wa_bsegDebet.setDmbtr(wa_mls.getTotalDmbtr());
				wa_bsegDebet.setBukrs(p_bkpf.getBukrs());
				wa_bsegDebet.setGjahr(p_bkpf.getGjahr());
				wa_bsegDebet.setMatnr(wa_mls.getMatnr());
				wa_bsegDebet.setWerks(werks);
				wa_bsegDebet.setLifnr(p_bkpf.getCustomer_id());
				pl_bseg.add(wa_bsegDebet);
				p_bkpf.setDmbtr(GeneralUtil.round(p_bkpf.getDmbtr()+wa_mls.getTotalDmbtr(), 2));
				/*MatnrDmbtr wa_md = l_matnr_dmbtr.get(wa_mls.getMatnr());
				if (wa_md==null)
				{
					wa_md = new MatnrDmbtr();
					wa_md.setMatnr(wa_mls.getMatnr());
					wa_md.setDmbtr(wa_mls.getTotalDmbtr());
					l_matnr_dmbtr.put(wa_mls.getMatnr(), wa_md);
				}
				else
				{
					wa_md.setDmbtr(wa_md.getDmbtr()+wa_mls.getDmbtr());
				}*/
				
			}
			
			
			
			
			
			int wa_buzei=0;
			for(Bseg wa_bseg:pl_bseg)
			{
				wa_buzei++;
				wa_bseg.setBuzei(wa_buzei);
				System.out.print(wa_buzei+" ");
				System.out.print(wa_bseg.getShkzg()+" ");
				System.out.print(wa_bseg.getHkont()+" ");
				System.out.print(wa_bseg.getMatnr()+" ");
				System.out.println(wa_bseg.getDmbtr());
			}
			
		    financeService.check_empty_fields(p_bkpf, pl_bseg);
            Long wa_belnr =financeService.insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);            
            
            int gjahr = p_bkpf.getGjahr();
            Long wa_awkey = Long.parseLong( String.valueOf(wa_belnr)+String.valueOf(gjahr));
            a_bkpf_GW.setAwkey2(wa_awkey);
            financeService.updateFiDoc(a_bkpf_GW, null);
			return wa_awkey;
			
			
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage()+" FinanceServiceLogistics -> returnMatnrToWerks");
		}
	}
	
	

	public Long removeMatnrFromWerksGuarantee (List<LogFinClass> l_mls,Bkpf a_bkpf, String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks, String a_barcode ) throws DAOException{
		try{//spsianie po garantii
			
			if (a_bkpf.getStorno()==1)
			{
				throw new DAOException("Финансовый документ отменен, к списанию не подлежит.");
			}
			
			if (!(a_bkpf.getBlart().equals("GC") ))
			{
				throw new DAOException("Финансовый документ не относится к продаже.");
			}
			if(werks==null || werks==0)
			{
				throw new DAOException("Склад не указен.");
			}
			
			List<Bkpf> l_bkpf_GW = new ArrayList<Bkpf>();
			List<Bseg> l_bseg_GW = new ArrayList<Bseg>();
			l_bkpf_GW = bkpfDao.dynamicFindBkpf(" blart='GW' and awkey2=null and storno = 0 and awkey = "+GeneralUtil.getPreparedAwkey(a_bkpf.getBelnr(), a_bkpf.getGjahr()));
			if (l_bkpf_GW.size()>0)
			{
				Map<Long,Double> l_matnr_map = new HashMap<Long,Double>();
				for (Bkpf wa_bkpf:l_bkpf_GW)
				{
					l_bseg_GW.addAll(bsegDao.dynamicFindBseg(" belnr = "+wa_bkpf.getBelnr()+" and gjahr = "+wa_bkpf.getGjahr()));
				}
				
				for (Bseg wa_bseg:l_bseg_GW)
				{
					if (wa_bseg.getHkont().startsWith("1330"))
					{
						double count = l_matnr_map.get(wa_bseg.getMatnr());
						l_matnr_map.put(wa_bseg.getMatnr(), wa_bseg.getMenge()+count);
					}
				}
				
				for (LogFinClass wa_mls:l_mls)
				{
					if (wa_mls.getMenge()==0)
					{
						throw new DAOException("Количество материалов равно 0, Программа не может списать материал");
					}
					double count = l_matnr_map.get(wa_mls.getMatnr());
					count = count - wa_mls.getMenge();
					if (count<0)
					{
						Matnr wa_matnr = matnrDao.find(wa_mls.getMatnr());
						throw new DAOException("Все материалы уже списаны ("+wa_matnr.getText45()+")");
					}
					else
					{
						l_matnr_map.put(wa_mls.getMatnr(), count);
					}
				}
			}
			
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_bsik = new ArrayList<Bsik>();
			
			Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis());
			p_bkpf.setBukrs(a_bukrs);
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setBlart("GW");
			p_bkpf.setBudat(curDate.getTime());
			p_bkpf.setBldat(curDate.getTime());
            p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
            p_bkpf.setCpudt(curDate.getTime());
            p_bkpf.setUsnam(a_userID);
            p_bkpf.setTcode(a_tcode);
            p_bkpf.setWaers("USD");
            p_bkpf.setKursf(1); 
            p_bkpf.setBrnch(a_branch_id);
            p_bkpf.setContract_number(a_bkpf.getContract_number());
            p_bkpf.setCustomer_id(a_bkpf.getCustomer_id());
            p_bkpf.setAwkey(GeneralUtil.getPreparedAwkey(a_bkpf.getBelnr(), a_bkpf.getGjahr()));
            p_bkpf.setBktxt(a_barcode);
            p_bkpf.setAwtyp(1);
            //Map<Long,MatnrDmbtr> l_matnr_dmbtr = new HashMap<Long,MatnrDmbtr>();
			
			
			for (LogFinClass wa_mls:l_mls)
			{
				//815	RAINBOW 13300001
				//816	REXWAT ATLAS 13300003
				//817	REXWAT ECO 13300002

				
				Bseg wa_bseg = new Bseg();
				wa_bseg.setHkont(get1330MatnrHkont(a_bukrs,wa_mls.getMatnr()," FinanceServiceLogistics -> removeMatnrFromWerksGuarantee"));
			
				if (wa_mls.getMenge()==0)
				{
					throw new DAOException("Количество материалов равно 0, Программа не может списать материал");
				}
				
				wa_bseg.setMatnr(wa_mls.getMatnr());
				wa_bseg.setMenge(wa_mls.getMenge());
				wa_bseg.setBschl("50");
				wa_bseg.setShkzg("H");
				wa_bseg.setDmbtr(wa_mls.getTotalDmbtr());
				wa_bseg.setBukrs(p_bkpf.getBukrs());
				wa_bseg.setGjahr(p_bkpf.getGjahr());
				wa_bseg.setLifnr(p_bkpf.getCustomer_id());
				wa_bseg.setWerks(werks);
				pl_bseg.add(wa_bseg);

				Bseg wa_bsegDebet = new Bseg();				
				wa_bsegDebet.setHkont(get7010MatnrHkont(a_bukrs,wa_mls.getMatnr()," FinanceServiceLogistics -> removeMatnrFromWerksGuarantee"));
				wa_bsegDebet.setBschl("40");
				wa_bsegDebet.setShkzg("S");
				wa_bsegDebet.setDmbtr(wa_mls.getTotalDmbtr());
				wa_bsegDebet.setBukrs(p_bkpf.getBukrs());
				wa_bsegDebet.setGjahr(p_bkpf.getGjahr());
				wa_bsegDebet.setMatnr(wa_mls.getMatnr());
				wa_bsegDebet.setWerks(werks);
				pl_bseg.add(wa_bsegDebet);
				p_bkpf.setDmbtr(GeneralUtil.round(p_bkpf.getDmbtr()+wa_mls.getTotalDmbtr(), 2));
			}
			
			
			
			
			int wa_buzei=0;
			for(Bseg wa_bseg:pl_bseg)
			{
				wa_buzei++;
				wa_bseg.setBuzei(wa_buzei);
				System.out.print(wa_buzei+" ");
				System.out.print(wa_bseg.getShkzg()+" ");
				System.out.print(wa_bseg.getHkont()+" ");
				System.out.print(wa_bseg.getMatnr()+" ");
				System.out.println(wa_bseg.getDmbtr());
			}

		    financeService.check_empty_fields(p_bkpf, pl_bseg);
            Long wa_belnr =financeService.insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);            
            
            int gjahr = p_bkpf.getGjahr();
            Long wa_awkey = Long.parseLong( String.valueOf(wa_belnr)+String.valueOf(gjahr));            
			return wa_awkey;
            
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage()+" FinanceServiceLogistics -> removeMatnrFromWerksGuarantee");
		}
	}
	public Long removeMatnrFromWerksRemont (List<LogFinClass> l_mls,Matnr a_remont_matnr, String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks, String a_barcode) throws DAOException{
		try{//spsianie na remont
			
			if(werks==null || werks==0)
			{
				throw new DAOException("Склад не указен.");
			}
			
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_bsik = new ArrayList<Bsik>();
			
			Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis());
			p_bkpf.setBukrs(a_bukrs);
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setBlart("GW");
			p_bkpf.setBudat(curDate.getTime());
			p_bkpf.setBldat(curDate.getTime());
            p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
            p_bkpf.setCpudt(curDate.getTime());
            p_bkpf.setUsnam(a_userID);
            p_bkpf.setTcode(a_tcode);
            p_bkpf.setWaers("USD");
            p_bkpf.setKursf(1); 
            p_bkpf.setBrnch(a_branch_id);
            p_bkpf.setBktxt(a_barcode);
            p_bkpf.setAwtyp(0);
            //Map<Long,MatnrDmbtr> l_matnr_dmbtr = new HashMap<Long,MatnrDmbtr>();
			
			
			for (LogFinClass wa_mls:l_mls)
			{
				//815	RAINBOW 13300001
				//816	REXWAT ATLAS 13300003
				//817	REXWAT ECO 13300002

				
				Bseg wa_bseg = new Bseg();
				wa_bseg.setHkont(get1330MatnrHkont(a_bukrs,wa_mls.getMatnr()," FinanceServiceLogistics -> removeMatnrFromWerksRemont"));
				
				if (wa_mls.getMenge()==0)
				{
					throw new DAOException("Количество материалов равно 0, Программа не может списать материал");
				}
				
				wa_bseg.setMatnr(wa_mls.getMatnr());
				wa_bseg.setMenge(wa_mls.getMenge());
				wa_bseg.setBschl("50");
				wa_bseg.setShkzg("H");
				wa_bseg.setDmbtr(wa_mls.getTotalDmbtr());
				wa_bseg.setBukrs(p_bkpf.getBukrs());
				wa_bseg.setGjahr(p_bkpf.getGjahr());
				wa_bseg.setWerks(werks);
				pl_bseg.add(wa_bseg);
				
				Bseg wa_bsegDebet = new Bseg();
				wa_bsegDebet.setBukrs(p_bkpf.getBukrs());
				wa_bsegDebet.setGjahr(p_bkpf.getGjahr());
				wa_bsegDebet.setMatnr(a_remont_matnr.getMatnr());
				wa_bsegDebet.setHkont(get1330MatnrHkont(a_bukrs,a_remont_matnr.getMatnr()," FinanceServiceLogistics -> removeMatnrFromWerksRemont"));	
				wa_bsegDebet.setBschl("40");
				wa_bsegDebet.setShkzg("S");
				wa_bsegDebet.setWerks(werks);
				wa_bsegDebet.setDmbtr(wa_mls.getTotalDmbtr());
				pl_bseg.add(wa_bsegDebet);
				p_bkpf.setDmbtr(GeneralUtil.round(p_bkpf.getDmbtr()+wa_mls.getTotalDmbtr(), 2));

				
			}

			
			
			
			int wa_buzei=0;
			for(Bseg wa_bseg:pl_bseg)
			{
				wa_buzei++;
				wa_bseg.setBuzei(wa_buzei);
				System.out.print(wa_buzei+" ");
				System.out.print(wa_bseg.getShkzg()+" ");
				System.out.print(wa_bseg.getHkont()+" ");
				System.out.print(wa_bseg.getMatnr()+" ");
				System.out.println(wa_bseg.getDmbtr());
			}

		    financeService.check_empty_fields(p_bkpf, pl_bseg);
            Long wa_belnr =financeService.insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);            
            
            int gjahr = p_bkpf.getGjahr();
            Long wa_awkey = Long.parseLong( String.valueOf(wa_belnr)+String.valueOf(gjahr));            
			return wa_awkey;
            
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage()+" FinanceServiceLogistics -> removeMatnrFromWerksRemont");
		}
	}
	
	public Long removeMatnrFromWerksLost (List<LogFinClass> l_mls, String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks) throws DAOException{
		try{//spisnanie uterie
			
			if(werks==null || werks==0)
			{
				throw new DAOException("Склад не указен.");
			}
			
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>(); 
			List<Bsik> pl_bsik = new ArrayList<Bsik>();
			
			Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis());
			p_bkpf.setBukrs(a_bukrs);
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setBlart("GW");
			p_bkpf.setBudat(curDate.getTime());
			p_bkpf.setBldat(curDate.getTime());
            p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
            p_bkpf.setCpudt(curDate.getTime());
            p_bkpf.setUsnam(a_userID);
            p_bkpf.setTcode(a_tcode);
            p_bkpf.setWaers("USD");
            p_bkpf.setKursf(1); 
            p_bkpf.setBrnch(a_branch_id);
            p_bkpf.setAwtyp(0);
			
			for (LogFinClass wa_mls:l_mls)
			{
				//815	RAINBOW 13300001
				//816	REXWAT ATLAS 13300003
				//817	REXWAT ECO 13300002

				
				Bseg wa_bseg = new Bseg();
				wa_bseg.setHkont(get1330MatnrHkont(a_bukrs,wa_mls.getMatnr()," FinanceServiceLogistics -> removeMatnrFromWerks"));	
				
				if (wa_mls.getMenge()==0)
				{
					throw new DAOException("Количество материалов равно 0, Программа не может списать материал");
				}
				
				wa_bseg.setMatnr(wa_mls.getMatnr());
				wa_bseg.setMenge(wa_mls.getMenge());
				wa_bseg.setBschl("50");
				wa_bseg.setShkzg("H");
				wa_bseg.setDmbtr(wa_mls.getTotalDmbtr());
				wa_bseg.setBukrs(p_bkpf.getBukrs());
				wa_bseg.setGjahr(p_bkpf.getGjahr());
				wa_bseg.setWerks(werks);
				pl_bseg.add(wa_bseg);

				Bseg wa_bsegDebet = new Bseg();				
				wa_bsegDebet.setHkont(get7010MatnrHkont(a_bukrs,wa_mls.getMatnr()," FinanceServiceLogistics -> removeMatnrFromWerks"));
				wa_bsegDebet.setBschl("40");
				wa_bsegDebet.setShkzg("S");
				wa_bsegDebet.setDmbtr(wa_mls.getTotalDmbtr());
				wa_bsegDebet.setBukrs(p_bkpf.getBukrs());
				wa_bsegDebet.setGjahr(p_bkpf.getGjahr());
				wa_bsegDebet.setMatnr(wa_mls.getMatnr());
				wa_bsegDebet.setWerks(werks);
				pl_bseg.add(wa_bsegDebet);
				p_bkpf.setDmbtr(GeneralUtil.round(p_bkpf.getDmbtr()+wa_mls.getTotalDmbtr(), 2));
				
			}
			

			
			
			
			int wa_buzei=0;
			for(Bseg wa_bseg:pl_bseg)
			{
				wa_buzei++;
				wa_bseg.setBuzei(wa_buzei);
				System.out.print(wa_buzei+" ");
				System.out.print(wa_bseg.getShkzg()+" ");
				System.out.print(wa_bseg.getHkont()+" ");
				System.out.print(wa_bseg.getMatnr()+" ");
				System.out.println(wa_bseg.getDmbtr());
			}

		    financeService.check_empty_fields(p_bkpf, pl_bseg);
            Long wa_belnr =financeService.insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);            
            
            int gjahr = p_bkpf.getGjahr();
            Long wa_awkey = Long.parseLong( String.valueOf(wa_belnr)+String.valueOf(gjahr));            
			return wa_awkey;
            
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage()+" FinanceServiceLogistics -> removeMatnrFromWerks");
		}
	}
	
	public Long returnIncorrectRemovedMatnr (Bkpf a_bkpf_GW,String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks) throws DAOException{
		try{//otmenit spisanni tovar
			
			if(werks==null || werks==0)
			{
				throw new DAOException("Склад не указен.");
			}
			
			if (a_bkpf_GW.getStorno()==1)
			{
				throw new DAOException("Финансовый документ отменен, к возврату не подлежит.");
			}
			
			List<Bseg> l_bseg_GW = new ArrayList<Bseg>();
			l_bseg_GW = bsegDao.dynamicFindBseg(" belnr = "+a_bkpf_GW.getBelnr()+" and gjahr = "+a_bkpf_GW.getGjahr());
			
		    
            financeService.cancelFiDoc(a_bkpf_GW, l_bseg_GW, a_userID,a_tcode);         
            
            Bkpf p_bkpf_ST = new Bkpf();
            p_bkpf_ST = bkpfDao.dynamicFindSingleBkpf(" stblg = "+a_bkpf_GW.getBelnr()+" and stjah="+a_bkpf_GW.getGjahr()+" and blart = 'ST'",a_bkpf_GW.getBukrs());

            
            Long wa_awkey = GeneralUtil.getPreparedAwkey(p_bkpf_ST.getBelnr(), p_bkpf_ST.getGjahr());        
			return wa_awkey;
            
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage()+" FinanceServiceLogistics -> removeMatnrFromWerks");
		}
	}
	
	public List<InvoiceListItemFkage> getInvoiceListToFkage(Invoice a_invoice) throws DAOException
	{
		try{
			RelatedDocs p_related_docs = new RelatedDocs();
			Order p_order = new Order();
			List<OrderMatnr> l_order_matnr = new ArrayList<OrderMatnr>();
			List<InvoiceItemWithMatnr> l_invoice_item = new ArrayList<InvoiceItemWithMatnr>();
			l_invoice_item = invItDao.findWithMatnr(" ml.invoice = "+a_invoice.getId());
			if (l_invoice_item==null || l_invoice_item.size()==0)
			{
				throw new DAOException("Материалы в накладной не найдены.");
			}
			
			p_related_docs = relDocsDao.findParent(a_invoice.getId(), Invoice.CONTEXT, Order.CONTEXT);
			
			if (p_related_docs==null || p_related_docs.getContext_id()==null)
			{
				throw new DAOException("Заказ не найден в таблице RelatedDocs");
			}
			else
			{
				p_order = ordDao.find(p_related_docs.getContext_id());
				if (p_order==null || p_order.getId()==null)
				{
					throw new DAOException("Заказ не найден в таблице Order");
				}
			}
			
			l_order_matnr = ordMatDao.findAll(" order_id = "+p_order.getId());
			if (l_order_matnr==null || l_order_matnr.size()==0)
			{
				throw new DAOException("Материалы в заказе на найдены.");
			}
			
			Map<Long,OrderMatnr> l_order_matnr_map = new HashMap<Long,OrderMatnr>();
			
			for(OrderMatnr wa_order_matnr:l_order_matnr)
			{
				l_order_matnr_map.put(wa_order_matnr.getMatnr(), wa_order_matnr);
			}
			
			
			List<InvoiceListItemFkage> rt_list = new ArrayList<InvoiceListItemFkage>();
			
			
			
			for(InvoiceItemWithMatnr wa_invoice_item:l_invoice_item)
			{
				OrderMatnr wa_order_matnr = l_order_matnr_map.get(wa_invoice_item.getMatnr());
				if (wa_order_matnr==null || wa_order_matnr.getMatnr()==null)
				{
					throw new DAOException(matnrDao.find(wa_invoice_item.getMatnr()).getText45()+ " в заказе отсутсвует.");
				}
				
				if (wa_order_matnr.getUnit_price()==null || wa_order_matnr.getUnit_price()==0)
				{
					throw new DAOException(matnrDao.find(wa_invoice_item.getMatnr()).getText45()+ " цена в заказе 0 или не указана.");
				}
				InvoiceListItemFkage wa_rt = new InvoiceListItemFkage();
				
				if (p_order.getCustomer_id()==null || p_order.getCustomer_id()<1)
				{
					throw new DAOException("Контрагент не указан в заказе.");
				}
				
				wa_rt.setCustomer_id(p_order.getCustomer_id());
				wa_rt.setInvoice_id(a_invoice.getId());
				wa_rt.setInvoice_item_id(wa_invoice_item.getInvoice_item_id());
				
				if (wa_invoice_item.getMatnr()==null || wa_invoice_item.getMatnr()<1)
				{
					throw new DAOException("Материал не указан в накладной.");
				}
				wa_rt.setMatnr(wa_invoice_item.getMatnr());
				
				if (wa_invoice_item.getMenge()==0 || wa_invoice_item.getMenge()<1)
				{
					throw new DAOException("Количество материалов в накладной 0 или меньше.");
				}
				wa_rt.setMenge(wa_invoice_item.getMenge());
				
				wa_rt.setMatnr_name(wa_invoice_item.getMatnr_name());
				
				if (wa_invoice_item.getMatnr_type()==0 || wa_invoice_item.getMatnr_type()<1)
				{
					throw new DAOException("Тип материала не найден в справочнике материалов. ID материала: "+wa_invoice_item.getMatnr());
				}
				wa_rt.setMatnr_type(wa_invoice_item.getMatnr_type());				
				
				
				wa_rt.setOrder_id(p_order.getId());
				
				if (wa_order_matnr.getUnit_price()==0)
				{
					throw new DAOException("Заводская цена в заказе не указана.ID материала: "+wa_invoice_item.getMatnr());
				}
				wa_rt.setUnit_price(wa_order_matnr.getUnit_price());
				
				
				wa_rt.setTotal_price(wa_rt.getMenge()*wa_rt.getUnit_price());
				
				if (p_order.getCurrency()==null || p_order.getCurrency().length()<3)
				{
					throw new DAOException("Материал не указан в накладной.");
				}
				wa_rt.setWaers(p_order.getCurrency());
				
				if (a_invoice.getBukrs()==null || a_invoice.getBukrs().length()<4)
				{
					throw new DAOException("Компания не указана в накладной.");
				}
				wa_rt.setBukrs(a_invoice.getBukrs());
				
				wa_rt.setParent_matnr(wa_invoice_item.getParent_matnr());
				
				rt_list.add(wa_rt);
				
				
			}
			
			
			return rt_list;
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	public Long createFkage(Bkpf a_bkpf, List<Bseg> l_bseg, Branch a_branch) throws DAOException
	{
		try{
			List<Long> l_invoice_id = new ArrayList<Long>();
			Long belnr = financeService.createAccountPayableDocs(a_bkpf, l_bseg);
			Long awkey = GeneralUtil.getPreparedAwkey(belnr, a_bkpf.getGjahr());
			for(Bseg wa_bseg:l_bseg)
			{
				if (wa_bseg.getMatnr()!=null && wa_bseg.getMenge()>0)
				{
					matnrListDao.updateMatnrListFkage(wa_bseg.getMatnr(),GeneralUtil.round(wa_bseg.getDmbtr()/wa_bseg.getMenge(), 2),awkey,wa_bseg.getInvoice_id(),(int)wa_bseg.getMenge());
					l_invoice_id.add(wa_bseg.getInvoice_id());
				}
				
			}
			// removing duplicates
			Set<Long> hs_invoice_id = new HashSet<>();
			hs_invoice_id.addAll(l_invoice_id);
			l_invoice_id.clear();
			l_invoice_id.addAll(hs_invoice_id);
			
			invoiceService.checkInvoicesStatus(awkey, l_invoice_id);
			
			return belnr;
		}  
		catch (Exception ex)
		{	    		
			throw new DAOException(ex.getMessage());
		}
	}
	
	public Long moveFromWerks(List<LogFinClass> l_olc, String a_bukrs,
			Long a_userID, Long a_branch_id, String a_tcode, Long werks) throws DAOException
	{
		try{//otpravka tovara
			//invoice table
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>();
			List<Bsik> pl_bsik = new ArrayList<Bsik>();
			
			if(werks==null || werks==0)
			{
				throw new DAOException("Склад не указен.");
			}
			
			Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis());
			p_bkpf.setBukrs(a_bukrs);
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setBlart("PE");
			p_bkpf.setBudat(curDate.getTime());
			p_bkpf.setBldat(curDate.getTime());
            p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
            p_bkpf.setCpudt(curDate.getTime());
            p_bkpf.setUsnam(a_userID);
            p_bkpf.setTcode(a_tcode);
            p_bkpf.setWaers("USD");
            p_bkpf.setKursf(1); 
            p_bkpf.setBrnch(a_branch_id);
            p_bkpf.setAwtyp(0);
			
			for (LogFinClass wa_olc:l_olc)
			{
				//815	RAINBOW 13300001
				//816	REXWAT ATLAS 13300003
				//817	REXWAT ECO 13300002

				
				Bseg wa_bseg = new Bseg();
				wa_bseg.setHkont(get1330MatnrHkont(a_bukrs,wa_olc.getMatnr()," FinanceServiceLogistics -> moveFromWerks"));					
				wa_bseg.setMatnr(wa_olc.getMatnr());
				wa_bseg.setMenge(wa_olc.getMenge());
				wa_bseg.setBschl("50");
				wa_bseg.setShkzg("H");
				wa_bseg.setDmbtr(wa_olc.getTotalDmbtr());
				wa_bseg.setBukrs(p_bkpf.getBukrs());
				wa_bseg.setGjahr(p_bkpf.getGjahr());
				wa_bseg.setWerks(werks);
				pl_bseg.add(wa_bseg);
				
				Bseg wa_bseg2 = new Bseg();
				wa_bseg2.setDmbtr(wa_olc.getTotalDmbtr());
				wa_bseg2.setHkont("13300029");
				wa_bseg2.setBschl("40");
				wa_bseg2.setShkzg("S");
				wa_bseg2.setGjahr(p_bkpf.getGjahr());
				wa_bseg2.setBukrs(p_bkpf.getBukrs());
				wa_bseg2.setWerks(werks);
				pl_bseg.add(wa_bseg2);
				p_bkpf.setDmbtr(GeneralUtil.round(p_bkpf.getDmbtr()+wa_olc.getTotalDmbtr(), 2));
				
			}
		
			
			

			
			int wa_buzei=0;
			for(Bseg wa_bseg:pl_bseg)
			{
				wa_buzei++;
				wa_bseg.setBuzei(wa_buzei);
				System.out.print(wa_buzei+" ");
				System.out.print(wa_bseg.getShkzg()+" ");
				System.out.print(wa_bseg.getHkont()+" ");
				System.out.print(wa_bseg.getMatnr()+" ");
				System.out.println(wa_bseg.getDmbtr());
			}
			
		    financeService.check_empty_fields(p_bkpf, pl_bseg);
            Long wa_belnr =financeService.insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);
       

            int gjahr = p_bkpf.getGjahr();
            Long wa_awkey = Long.parseLong( String.valueOf(wa_belnr)+String.valueOf(gjahr));            
			return wa_awkey;
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage()+" FinanceServiceLogistics -> moveFromWerks");
		}
	}
	public Long movetoWerks(List<LogFinClass> l_orlc, String a_bukrs,
			Long a_userID, Long a_branch_id, String a_tcode, Long werks) throws DAOException
	{
		try{
			//invoice table
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>();
			List<Bsik> pl_bsik = new ArrayList<Bsik>();
			
			Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis());
			p_bkpf.setBukrs(a_bukrs);
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setBlart("PE");
			p_bkpf.setBudat(curDate.getTime());
			p_bkpf.setBldat(curDate.getTime());
            p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
            p_bkpf.setCpudt(curDate.getTime());
            p_bkpf.setUsnam(a_userID);
            p_bkpf.setTcode(a_tcode);
            p_bkpf.setWaers("USD");
            p_bkpf.setKursf(1); 
            p_bkpf.setBrnch(a_branch_id);
            p_bkpf.setAwtyp(0);
			
			for (LogFinClass wa_orlc:l_orlc)
			{
				//815	RAINBOW 13300001
				//816	REXWAT ATLAS 13300003
				//817	REXWAT ECO 13300002

				
				Bseg wa_bseg = new Bseg();
				wa_bseg.setHkont(get1330MatnrHkont(a_bukrs,wa_orlc.getMatnr()," FinanceServiceLogistics -> movetoWerks"));	
				wa_bseg.setMatnr(wa_orlc.getMatnr());
				wa_bseg.setMenge(wa_orlc.getMenge());
				wa_bseg.setBschl("40");
				wa_bseg.setShkzg("S");
				wa_bseg.setDmbtr(wa_orlc.getTotalDmbtr());
				wa_bseg.setBukrs(p_bkpf.getBukrs());
				wa_bseg.setGjahr(p_bkpf.getGjahr());
				wa_bseg.setWerks(werks);
				pl_bseg.add(wa_bseg);
				
				Bseg wa_bseg2 = new Bseg();
				wa_bseg2.setHkont("13300029");
				wa_bseg2.setBschl("50");
				wa_bseg2.setShkzg("H");
				wa_bseg2.setMatnr(wa_bseg.getMatnr());
				wa_bseg2.setDmbtr(wa_bseg.getDmbtr());
				wa_bseg2.setBukrs(p_bkpf.getBukrs());
				wa_bseg2.setGjahr(p_bkpf.getGjahr());
				wa_bseg2.setWerks(werks);
				pl_bseg.add(wa_bseg2);
				p_bkpf.setDmbtr(GeneralUtil.round(p_bkpf.getDmbtr()+wa_orlc.getTotalDmbtr(),2));
				
			}
			//System.out.println(pl_bseg.size());

			
			int wa_buzei=0;
			for(Bseg wa_bseg:pl_bseg)
			{
				wa_buzei++;
				wa_bseg.setBuzei(wa_buzei);
				System.out.print(wa_buzei+" ");
				System.out.print(wa_bseg.getShkzg()+" ");
				System.out.print(wa_bseg.getHkont()+" ");
				System.out.print(wa_bseg.getMatnr()+" ");
				System.out.println(wa_bseg.getDmbtr());
			}
			
		    financeService.check_empty_fields(p_bkpf, pl_bseg);
            Long wa_belnr =financeService.insertNewFiDoc(p_bkpf, pl_bseg, pl_bsik);

            
            
            int gjahr = p_bkpf.getGjahr();
            Long wa_awkey = Long.parseLong( String.valueOf(wa_belnr)+String.valueOf(gjahr));

            return wa_awkey;
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage()+" FinanceServiceLogistics -> movetoWerks");
		}
	}
	
	public String get1330MatnrHkont(String a_bukrs,Long a_matnr,String a_method)
	{
		try
		{
			//************************************
			//Aura-1000
			//ceb-812,2,3,256 "13300002"
			//rob-1,4 "13300001"
			//Rexwat Atlas-816,910 "13300004"
			//Rexwat ECO-817,913 "13300005"
			
			//GreenLight
			//Rain-815 "13300001"
			//Rexwat Atlas-816,910 "13300003"
			//Rexwat ECO-817,913 "13300002"
			//***********************************
			
			if (a_bukrs.equals("1000"))
			{
				if ((a_matnr==1L) || (a_matnr==4L)) return "13300001"; //Roboclean
				if ((a_matnr==2L) || (a_matnr==3L) || (a_matnr==256L) || (a_matnr==812L)) return "13300002"; //Cebilon
				if ((a_matnr==816L) || (a_matnr==910L)) return "13300004";//Rexwat Atlas
				if ((a_matnr==817L) || (a_matnr==913L)) return "13300005";//Rexwat ECO
				
				return "13300030";
				
			}
			else if (a_bukrs.equals("2000"))
			{
				if (a_matnr==815L) return "13300001"; //Rainbow
				if ((a_matnr==816L) || (a_matnr==910L)) return "13300003"; //Rexwat Atlas
				if ((a_matnr==817L) || (a_matnr==913L)) return "13300002"; //Rexwat ECO
				return "13300030";
			}
			else if (a_bukrs.equals("3000"))
			{
				return "13300001";
			}
			else if (a_bukrs.equals("6000"))
			{
				return "13300001";
			}
			
			else throw new DAOException("Счет материала не существует.");
			
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage()+" "+a_method);
		}
	}
	public String get7010MatnrHkont(String a_bukrs,Long a_matnr,String a_method)
	{
		try
		{
			//************************************
			//Aura-1000
			//ceb-812,2,3,256 "70100002"
			//rob-1,4 "70100001"
			//Rexwat Atlas-816,910 "70100004"
			//Rexwat ECO-817,913 "70100005"
			
			//GreenLight
			//Rain-815 "70100001"
			//Rexwat Atlas-816,910 "70100003"
			//Rexwat ECO-817,913 "70100002"
			//***********************************			

			
			if (a_bukrs.equals("1000"))
			{
				if ((a_matnr==1L) || (a_matnr==4L)) return "70100001"; //Roboclean
				if ((a_matnr==2L) || (a_matnr==3L) || (a_matnr==256L) || (a_matnr==812L)) return "70100002"; //Cebilon
				if ((a_matnr==816L) || (a_matnr==910L)) return "70100004";//Rexwat Atlas
				if ((a_matnr==817L) || (a_matnr==913L)) return "70100005";//Rexwat ECO				
				return "70100030";
				
			}
			else if (a_bukrs.equals("2000"))
			{
				if (a_matnr==815L) return "70100001"; //Rainbow
				if ((a_matnr==816L) || (a_matnr==910L)) return "70100003"; //Rexwat Atlas
				if ((a_matnr==817L) || (a_matnr==913L)) return "70100002"; //Rexwat ECO
				if (a_matnr==2930L) return "70100001"; //Rainbow
				return "70100030";
			}
			else if (a_bukrs.equals("3000"))
			{
				return "70100001";
			}
			else if (a_bukrs.equals("6000"))
			{
				return "70100001";
			}
			
			
			else throw new DAOException("Счет материала не существует.");
			
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage()+" "+a_method);
		}
	}
	public String get6010MatnrHkont(String a_bukrs,Long a_matnr,String a_method)
	{
		try
		{
			//************************************
			//Aura-1000
			//ceb-812,2,3,256 "60100002"
			//rob-1,4 "60100001"
			//Rexwat Atlas-816,910 "60100004"
			//Rexwat ECO-817,913 "60100005"
			
			//GreenLight
			//Rain-815 "60100001"
			//Rexwat Atlas-816,910 "60100003"
			//Rexwat ECO-817,913 "60100002"
			//***********************************			

			
			if (a_bukrs.equals("1000"))
			{
				if ((a_matnr==1L) || (a_matnr==4L)) return "60100001"; //Roboclean
				if ((a_matnr==2L) || (a_matnr==3L) || (a_matnr==256L) || (a_matnr==812L)) return "60100002"; //Cebilon
				if ((a_matnr==816L) || (a_matnr==910L)) return "60100004";//Rexwat Atlas
				if ((a_matnr==817L) || (a_matnr==913L)) return "60100005";//Rexwat ECO		
				
				
				return "60100030";
				
			}
			else if (a_bukrs.equals("2000"))
			{
				if (a_matnr==815L) return "60100001"; //Rainbow
				if ((a_matnr==816L) || (a_matnr==910L)) return "60100003"; //Rexwat Atlas
				if ((a_matnr==817L) || (a_matnr==913L)) return "60100002"; //Rexwat ECO
				if (a_matnr==2930L) return "60100001"; //Rainbow
				return "60100030";
			}
			else if (a_bukrs.equals("3000"))
			{
				return "60100001";
			}
			else if (a_bukrs.equals("6000"))
			{
				return "60100001";
			}
			
			else throw new DAOException("Счет материала не существует.");
			
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage()+" "+a_method);
		}
	}
	
	public Long addMatnrToWerksFound (List<LogFinClass> l_mls, String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks) throws DAOException{
		try{//spisnanie uterie
			
			if(werks==null || werks==0)
			{
				throw new DAOException("Склад не указен.");
			}
			
			Bkpf p_bkpf = new Bkpf();
			List<Bseg> pl_bseg = new ArrayList<Bseg>();
			
			Calendar curDate = Calendar.getInstance(); 
			//Time cputm = new Time(curDate.getTimeInMillis());
			p_bkpf.setBukrs(a_bukrs);
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR)); 
			p_bkpf.setBlart("GW");
			p_bkpf.setBudat(curDate.getTime());
			p_bkpf.setBldat(curDate.getTime());
            p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
            p_bkpf.setCpudt(curDate.getTime());
            p_bkpf.setUsnam(a_userID);
            p_bkpf.setTcode(a_tcode);
            p_bkpf.setWaers("USD");
            p_bkpf.setKursf(1); 
            p_bkpf.setBrnch(a_branch_id);
            p_bkpf.setAwtyp(2);
            p_bkpf.setBktxt("Недостача");
            p_bkpf.setCustomer_id(148517L);
			
			for (LogFinClass wa_mls:l_mls)
			{
				//815	RAINBOW 13300001
				//816	REXWAT ATLAS 13300003
				//817	REXWAT ECO 13300002

				
				Bseg wa_bseg = new Bseg();
				
				
				if (wa_mls.getMenge()==0)
				{
					throw new DAOException("Количество материалов равно 0, Программа не может списать материал");
				}
				
				wa_bseg.setMatnr(wa_mls.getMatnr());
				wa_bseg.setMenge(wa_mls.getMenge());
				wa_bseg.setBschl("31");
				wa_bseg.setShkzg("H");
				wa_bseg.setDmbtr(wa_mls.getTotalDmbtr());
				wa_bseg.setBukrs(p_bkpf.getBukrs());
				wa_bseg.setGjahr(p_bkpf.getGjahr());
				wa_bseg.setWerks(werks);
				wa_bseg.setHkont("33100001");
				wa_bseg.setLifnr(148517L); //Ambar Eksi Fazla iin 8888
				pl_bseg.add(wa_bseg);
				
				

				Bseg wa_bsegDebet = new Bseg();				
				wa_bsegDebet.setHkont(get1330MatnrHkont(a_bukrs,wa_mls.getMatnr()," FinanceServiceLogistics -> removeMatnrFromWerks"));
				wa_bsegDebet.setBschl("40");
				wa_bsegDebet.setShkzg("S");
				wa_bsegDebet.setDmbtr(wa_mls.getTotalDmbtr());
				wa_bsegDebet.setBukrs(p_bkpf.getBukrs());
				wa_bsegDebet.setGjahr(p_bkpf.getGjahr());
				wa_bsegDebet.setMatnr(wa_mls.getMatnr());
				wa_bsegDebet.setWerks(werks);
				pl_bseg.add(wa_bsegDebet);
				p_bkpf.setDmbtr(GeneralUtil.round(p_bkpf.getDmbtr()+wa_mls.getTotalDmbtr(), 2));
			}
			

			
			
			
			int wa_buzei=0;
			for(Bseg wa_bseg:pl_bseg)
			{
				wa_buzei++;
				wa_bseg.setBuzei(wa_buzei);
				System.out.print(wa_buzei+" ");
				System.out.print(wa_bseg.getShkzg()+" ");
				System.out.print(wa_bseg.getHkont()+" ");
				System.out.print(wa_bseg.getMatnr()+" ");
				System.out.println(wa_bseg.getDmbtr());
			}

		    financeService.check_empty_fields(p_bkpf, pl_bseg);
            Long wa_belnr =financeService.createAccountPayableDocs(p_bkpf, pl_bseg);            
            
            int gjahr = p_bkpf.getGjahr();
            Long wa_awkey = Long.parseLong( String.valueOf(wa_belnr)+String.valueOf(gjahr));            
			return wa_awkey;
            
		}
		catch (DAOException ex)
		{	    		
			throw new DAOException(ex.getMessage()+" FinanceServiceLogistics -> removeMatnrFromWerks");
		}
	}
	
	public Long tradeInGoodsIn (List<LogFinClass> l_mls, String a_bukrs, Long a_userID, Long a_branch_id, String a_tcode, Long werks, Long a_customerId) throws DAOException{
		try
		{
			double summaForAllSame = 1;

			Branch selectedBranch = branchDao.find(a_branch_id);
			Calendar curDate = Calendar.getInstance();
			Bkpf p_bkpf = new Bkpf();
			p_bkpf.setBukrs(a_bukrs);
			p_bkpf.setBldat(curDate.getTime());
			p_bkpf.setBudat(curDate.getTime());
			p_bkpf.setUsnam(a_userID);
			p_bkpf.setTcode(a_tcode);
			p_bkpf.setGjahr(curDate.get(Calendar.YEAR));
			p_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
			p_bkpf.setBlart("GE");
			p_bkpf.setWaers("USD");
			p_bkpf.setKursf(1);
			p_bkpf.setCustomer_id(a_customerId);


			if (p_bkpf.getWaers() == null || p_bkpf.getWaers().isEmpty()){
				throw new DAOException("Select currency");
			}
			//System.out.println(p_bkpf.getBukrs());


			if (p_bkpf.getKursf() == 0 || p_bkpf.getKursf() < 0 ){
				throw new DAOException("Курсе равен 0 или меньше.");
			}

			//FinanceService financeService = (FinanceService) appContext.getContext().getBean("financeService");
			List<Bseg> l_bsegFinal = new ArrayList<Bseg>();
			Calendar cal = Calendar.getInstance();
			cal.setTime(p_bkpf.getBudat());

			p_bkpf.setBrnch(selectedBranch.getBranch_id());
			p_bkpf.setBusiness_area_id(selectedBranch.getBusiness_area_id());
			p_bkpf.setCpudt(cal.getTime());
			p_bkpf.setAwtyp(2);
			if (p_bkpf.getCustomer_id() == null)
			{
				throw new DAOException("Choose Creditor");
			}



			double totalDmbtr = 0;

			for(LogFinClass wa_mls:l_mls)
			{
				Bseg wa_bseg = new Bseg();
				wa_bseg.setBukrs(p_bkpf.getBukrs());
				wa_bseg.setGjahr(p_bkpf.getGjahr());
				wa_bseg.setBschl("40");
				wa_bseg.setShkzg("S");
				wa_bseg.setLifnr(p_bkpf.getCustomer_id());
				wa_bseg.setWerks(werks);
				wa_bseg.setMenge(wa_mls.getMenge());
				wa_bseg.setMatnr(wa_mls.getMatnr());
				wa_bseg.setDmbtr(summaForAllSame);
				wa_bseg.setHkont(get1330MatnrHkont(a_bukrs,wa_mls.getMatnr()," FinanceServiceLogistics -> removeMatnrFromWerks"));
				if (wa_bseg.getMenge() <= 0)
				{
					throw new DAOException("Enter material amount");
				}
				if (wa_bseg.getWerks() == null || wa_bseg.getWerks() == 0)
				{
					throw new DAOException("Select werks");
				}
				if (wa_bseg.getMatnr() == null || wa_bseg.getMatnr() == 0)
				{
					throw new DAOException("Select matnr");
				}
				l_bsegFinal.add(wa_bseg);
				totalDmbtr = totalDmbtr + wa_bseg.getDmbtr();
			}


			p_bkpf.setDmbtr(GeneralUtil.round(totalDmbtr,2));

			for(LogFinClass wa_mls:l_mls)
			{
				Bseg wa_bseg = new Bseg();
				wa_bseg.setBukrs(p_bkpf.getBukrs());
				wa_bseg.setGjahr(p_bkpf.getGjahr());
				wa_bseg.setBschl("31");
				wa_bseg.setShkzg("H");
				wa_bseg.setHkont("33100001");
				wa_bseg.setLifnr(p_bkpf.getCustomer_id());
				wa_bseg.setWerks(werks);
				wa_bseg.setMenge(wa_mls.getMenge());
				wa_bseg.setMatnr(wa_mls.getMatnr());
				wa_bseg.setDmbtr(summaForAllSame);
				l_bsegFinal.add(wa_bseg);
			}




			int wa_buzei = 0;

			for(Bseg wa_bseg:l_bsegFinal)
			{
				//System.out.println(111);
				wa_buzei = wa_buzei + 1;
				wa_bseg.setBuzei(wa_buzei);
				l_bsegFinal.add(wa_bseg);
			}



			Long belnr = financeService.createAccountPayableDocs(p_bkpf, l_bsegFinal);
			Long awkey = GeneralUtil.getPreparedAwkey(belnr, p_bkpf.getGjahr());

			for(LogFinClass wa_mls:l_mls)
			{
				if (wa_mls.getInvoiceId()==null|| wa_mls.getInvoiceId().equals(0L)){
					throw new DAOException("Invoice ID is null or 0");
				}
				if (awkey==null|| awkey.equals(0L)){
					throw new DAOException("Invoice ID is null or 0");
				}
				matnrListDao.updateMatnrListFkage(wa_mls.getMatnr(),summaForAllSame,awkey,wa_mls.getInvoiceId(),(int)(double)wa_mls.getMenge());
			}

			return awkey;


		}
		catch (DAOException ex)
		{
			throw new DAOException(ex.getMessage()+" FinanceServiceLogistics -> tradeInGoodsIn");
		}
	}
}
