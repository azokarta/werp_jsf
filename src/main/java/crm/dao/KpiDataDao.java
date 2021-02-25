package crm.dao;

import general.dao.GenericDao;
import general.tables.KpiData;

import java.util.List;

/**
 * Created by admin on 29.11.2017.
 */
public interface KpiDataDao extends GenericDao<KpiData> {
    List<KpiData> findAll(int year,int month);

    List<KpiData> findAll(int year,int month,Integer indicatorId);

    //Менеджер + все кто под ним в иерархии, для текущего месяца
    List<KpiData> findAllCurrentForManager(Long managerId);

    List<KpiData> findAllByBranchId(int year,int month,Long branchId);

    List<KpiData> findAllByBranchByPositionIds(int year,int month,Long branchId,List<Long> positionIds);

    List<KpiData> findAllByStaffIdsByPositionIds(int year,int month,List<Long> staffIds,List<Long> positionIds);

    List<KpiData> findAll(int year,int month,Long branchId,Integer indicatorId,List<Long> staffIds);

    List<KpiData> findAll(int year,int month,Long branchId,Integer indicatorId,Long positionId,List<Long> staffIds);

    List<KpiData> findAllForKpiRating(List<String> bukrs,List<Long> branchIds,List<Long> positionIds,int year,int month);

    List<KpiData> findAllByBukrsAndBranches(int year,int month,String bukrs,List<Long> branchIds);

    List<Object[]> findSumForKpiReportByBukrs(String bukrs,List<Long> branchIds,List<Long> positionIds,int year,int month);

    List<Object[]> findSumGrouppedByStaff(String bukrs,List<Long> branchIds,List<Long> positionIds,int year,int month);

    List<KpiData> findByStaffIds(String bukrs,List<Long> staffIds,int year,int month);

    List<Object[]> findSumForKpiRatingReport(List<String> bukrs,List<Long> branchIds,List<Long> positionIds,int year,int month);
}
