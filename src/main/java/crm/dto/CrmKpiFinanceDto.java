package crm.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import antlr.collections.List;
import general.GeneralUtil;

public class CrmKpiFinanceDto {

	private Long staffId;
	private String bukrs;
	private Long branchId;	
	private Long positionId;
	private String staffName;
	private Set<Item> items = new HashSet<Item>();
	private Map<Integer,Item> itemsMap = new HashMap<>();
	private Long customerId;
	
	public CrmKpiFinanceDto() {
	}
	
	

	public Long getCustomerId() {
		return customerId;
	}



	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}



	public Long getPositionId() {
		return positionId;
	}




	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}




	public String getStaffName() {
		return staffName;
	}




	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}




	public Long getStaffId() {
		return staffId;
	}




	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}
	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	 public Double getTotalScore(){
	        Double d = 0D;
	        for(Item i:getItems()){
	            d += i.getScore();
	        }

	        return GeneralUtil.round(d,2);
	    }

	    public Double getTotalAverageScore(){
	        Double d = 0D;
	        for(Item i:getItems()){
	            d += i.getAverageScore();
	        }

	        return GeneralUtil.round(d,2);
	    }


	public Long getBranchId() {
		return branchId;
	}

	public Set<Item> getItems(){
		return items;
	}


	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public void addItem(Integer indicatorId, Double value, Double point, Double doneValue,Double score){
//      if(doneValue > value){
//          doneValue = value;
//      }

      if(score > point){
          score = point;
      }
      Item item =new Item(indicatorId,value,point,doneValue,score);
      if(itemsMap.containsKey(indicatorId)){
          Item oldItem = itemsMap.get(indicatorId);
          item.setDoneValue(item.getDoneValue()+oldItem.getDoneValue());
          item.setValue(item.getValue()+oldItem.getValue());
          item.setScore(item.getScore() + oldItem.getScore());
          item.setPoint(item.getPoint()+oldItem.getPoint());
          item.setCount(oldItem.getCount()+1);
      }
      itemsMap.put(indicatorId,item);
      items = new HashSet<>(itemsMap.values());
  }

	public void resetItems(){
		this.items = new HashSet<>();
	}


	public class Item{
        private Integer indicatorId;
        private Double value;
        private Double point;
        private Double doneValue;
        private Double score;
        private int count = 0;

        public Item(Integer indicatorId, Double value, Double point, Double doneValue,Double score) {
            this.indicatorId = indicatorId;
            this.value = value;
            this.point = point;
            this.doneValue = doneValue;
            this.score = score;
            count++;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public Double getPercentage(){
            if(point == 0){
                return 0D;
            }
            return GeneralUtil.round((score*100)/point,2);
        }

        public Double getPerformancePercentage(){
            if(value == 0){
                return 0D;
            }

            return GeneralUtil.round((doneValue*100)/value,2);
        }

        public Double getScore() {
            return score;
            //return GeneralUtil.round(score,2);
        }

        public Double getAverageScore(){
            if(count > 0){
                return GeneralUtil.round(getScore()/count,2);
            }

            return 0D;
        }

        public void setScore(Double score) {
            this.score = score;
        }

        public Integer getIndicatorId() {
            return indicatorId;
        }

        public void setIndicatorId(Integer indicatorId) {
            this.indicatorId = indicatorId;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public Double getPoint() {
            return point;
        }

        public void setPoint(Double point) {
            this.point = point;
        }

        public Double getDoneValue() {
            return doneValue;
        }

        public void setDoneValue(Double doneValue) {
            this.doneValue = doneValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item item = (Item) o;

            return indicatorId.equals(item.indicatorId);
        }

        @Override
        public int hashCode() {
            return indicatorId.hashCode();
        }
    }
}
