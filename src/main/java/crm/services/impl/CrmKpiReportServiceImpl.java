package crm.services.impl;

import crm.services.CrmKpiReportService;
import general.Validation;
import crm.dao.CrmDocDemoDao;
import crm.dao.KpiDataDao;
import crm.dao.KpiItemDao;
import crm.dto.CrmKpiFinanceDto;
import general.dao.BranchDao;
import general.dao.PyramidDao;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.tables.Branch;
import general.tables.KpiData;
import general.tables.Position;
import general.tables.Pyramid;
import general.tables.Staff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;


/**
 * Created by admin on 22.11.2017.
 */
@Service("marketingKpiReportService")
public class CrmKpiReportServiceImpl implements CrmKpiReportService {

    @Autowired
    KpiItemDao kpiItemDao;


    @Autowired
    CrmDocDemoDao demoDao;

    @Autowired
    PyramidDao pyramidDao;


    @Autowired
    SalaryDao salaryDao;

    @Autowired
    KpiDataDao kpiDataDao;

    @Autowired
    StaffDao staffDao;
    
    @Autowired
    BranchDao branchDao;


    /**
     * Id-шники должности, которые участвует в KPI отчете поумолчанию
     */
    private List<Long> defaultPositionIds = Arrays.asList(Position.DEALER_POSITION_ID,Position.MANAGER_POSITION_ID,Position.STAZHER_DEALER_POSITION_ID);
    //private final static int CURRENT_YEAR = 2018;
    //private final static int CURRENT_MONTH = 7;
    private final static List<Long> excludeBranchIds = Arrays.asList(210L,212L);


    private List<CrmKpiFinanceDto> prepareForDirectors(String bukrs,List<KpiData> dataList,List<Long> branchIds,int year, int month){
        Map<Long,Pyramid> byParent = new HashMap<>();
        Map<Long,Pyramid> byStaff = new HashMap<>();
        Map<Long, Staff> staffMap = staffDao.getMappedList("");
        Map<Long,Staff> directorsMapByBranch = new HashMap<>();
        preparePyramidsMap(bukrs,byParent,byStaff);
        for(Entry<Long, Pyramid> e: byStaff.entrySet()){
        	Pyramid p = e.getValue();
        	if(p != null && !Validation.isEmptyLong(p.getStaff_id()) && !Validation.isEmptyLong(p.getPosition_id())
        			&& staffMap.containsKey(p.getStaff_id())
        			){
        		if(Position.DIRECTOR_POSITION_ID.equals(p.getPosition_id())){
        			directorsMapByBranch.put(p.getBranch_id(), staffMap.get(p.getStaff_id()));
        		}
        	}
        }

        

        Map<Long,CrmKpiFinanceDto> dataMap = new HashMap<>();
        for(KpiData data:dataList){
        	if(!directorsMapByBranch.containsKey(data.getBranchId())){
        		continue;
        	}
        	
        	if(!isApplied(data.getStaffId(),byParent,byStaff)){
        		continue;
        	}
        	
        	CrmKpiFinanceDto dto = new CrmKpiFinanceDto();
            if(dataMap.containsKey(data.getBranchId())){
                dto = dataMap.get(data.getBranchId());
            }else{
            	dto.setBukrs(data.getBukrs());
            	dto.setBranchId(data.getBranchId());
            	dto.setPositionId(Position.DIRECTOR_POSITION_ID);
                dto.setStaffName(directorsMapByBranch.get(data.getBranchId()).getFullFIO());
                dto.setStaffId(directorsMapByBranch.get(data.getBranchId()).getStaff_id());
                dto.setCustomerId(directorsMapByBranch.get(data.getBranchId()).getCustomer_id());
            }

            if(isApplied(data.getStaffId(),byParent,byStaff)){
                dto.addItem(data.getIndicatorId(),data.getValue(),data.getPoint(),data.getDoneValue(),data.getScore());
            }
            dataMap.put(data.getBranchId(),dto);
        }

        return new ArrayList<>(dataMap.values());
    }

    //<parentPyramidId,Pyramid>
    private void preparePyramidsMap(String bukrs,Map<Long,Pyramid> byParentMap, Map<Long,Pyramid> byStaff){
    	List<Pyramid> pyramids = pyramidDao.dynamicFindPyramid(" bukrs = '" + bukrs + "' and position_id != 9  ");
        for(Pyramid p:pyramids){
            byParentMap.put(p.getParent_pyramid_id(),p);
            if(byStaff.containsKey(p.getStaff_id())){
            	//byStaff.put(Long.valueOf(100000+p.getStaff_id()),p);
            	if(!Validation.isEmptyLong(p.getStaff_id())){
            		byStaff.put(Long.valueOf(100000+p.getStaff_id()),p);
            	}
            	//System.out.println("DD: " + p.getStaff_id());
            }else{
            	byStaff.put(p.getStaff_id(),p);
            }
        }
    }

    private boolean isApplied(Long staffId,Map<Long,Pyramid> byParentMap, Map<Long,Pyramid> byStaff){
        Pyramid pyramid = byStaff.get(staffId);
        if(pyramid == null){
            return false;
        }else{
            Pyramid parent = byParentMap.get(pyramid.getParent_pyramid_id());
            if(parent == null || Validation.isEmptyLong(parent.getStaff_id())){
                if(!Position.MANAGER_POSITION_ID.equals(pyramid.getPosition_id())){
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public List<CrmKpiFinanceDto> getCurrentKpiForBranches(String bukrs, int year, int month) {
        List<Long> branchIds = new ArrayList<>();
        for(Branch br:branchDao.findByBukrs(bukrs)){
        	if(!excludeBranchIds.contains(br.getBranch_id())){
        		branchIds.add(br.getBranch_id());
        	}
        }
        
        List<KpiData> list = kpiDataDao.findAllByBukrsAndBranches(year,month,bukrs,branchIds);
        return prepareForDirectors(bukrs,list,branchIds,year,month);
    }

    @Override
    public List<CrmKpiFinanceDto> getCurrentKpiForGroups(Long branchId, int year, int month) {
    	String[] posIds = {String.valueOf(Position.DEALER_POSITION_ID),String.valueOf(Position.MANAGER_POSITION_ID),String.valueOf(Position.STAZHER_DEALER_POSITION_ID)};
    	String pyrCond = String.format(" branch_id = %d AND position_id IN(" + String.join(",", posIds) + ") ", branchId);

        Map<Long,Staff> managersMap = new HashMap<>();
        List<Pyramid> pyramids = pyramidDao.dynamicFindPyramid(pyrCond);
        
        Map<Long,Pyramid> pyramidMap = new HashMap<>();
        for(Pyramid p:pyramids){
            if(!Validation.isEmptyLong(p.getStaff_id())){
                pyramidMap.put(p.getPyramid_id(),p);
            }
        }

        for(Pyramid p:pyramids){
            if(Position.MANAGER_POSITION_ID.equals(p.getPosition_id())){
                managersMap.put(p.getStaff_id(),p.getStaff());
            }else{
                Pyramid parent = pyramidMap.get(p.getParent_pyramid_id());
                if(parent != null && Position.MANAGER_POSITION_ID.equals(parent.getPosition_id())){
                    managersMap.put(p.getStaff_id(),parent.getStaff());
                }
            }
        }

        Staff fakeManager = new Staff();
        fakeManager.setStaff_id(0L);
        fakeManager.setLastname("Без менеджера");

        List<KpiData> dataList = kpiDataDao.findAllByBranchId(year,month,branchId);

        Map<Long,CrmKpiFinanceDto> dataMap = new HashMap<>();
        for(KpiData data:dataList){

            Staff manager = managersMap.get(data.getStaffId());
            if(manager == null){
            	continue;
            }

            Long id = manager.getStaff_id();
            CrmKpiFinanceDto dto = new CrmKpiFinanceDto();
            if(dataMap.containsKey(id)){
                dto = dataMap.get(id);
            }else{
            	dto.setStaffId(id);
                dto.setStaffName(manager.getFullFIO());
                dto.setBranchId(branchId);
                dto.setBukrs(data.getBukrs());
                dto.setCustomerId(manager.getCustomer_id());
                dto.setPositionId(Position.MANAGER_POSITION_ID);
            }

            dto.addItem(data.getIndicatorId(),data.getValue(),data.getPoint(),data.getDoneValue(),data.getScore());
            dataMap.put(id,dto);
        }

        return new ArrayList<>(dataMap.values());
    }

	@Override
	public List<CrmKpiFinanceDto> getDataForFinance(String bukrs, int year, int month) {
		List<CrmKpiFinanceDto> out = getCurrentKpiForBranches(bukrs, year, month);
		List<CrmKpiFinanceDto> managersData = new ArrayList<>();
		for(Branch br:branchDao.findByBukrs(bukrs)){
        	if(!excludeBranchIds.contains(br.getBranch_id())){
        		managersData.addAll(getCurrentKpiForGroups(br.getBranch_id(), year, month));
        	}
//			if(Long.valueOf(54).equals(dto.getBranchId())){
//				dto.resetItems();
//			}
			//managersData.addAll(getCurrentKpiForGroups(dto.getBranchId(), year, month));
		}
		out.addAll(managersData);
		return out;
	}
    
    
}
