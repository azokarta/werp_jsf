/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.tables;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author onlasyn
 */
@Entity
@javax.persistence.SequenceGenerator(name="seq_department_id",sequenceName="seq_department_id", allocationSize = 1)
public class Department implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name="dep_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_department_id")
    private Long dep_id;

    public Long getId() {
        return getDep_id();
    }
    
    
    @Column(name="name_ru")
    private String name_ru;
    
    @Column(name="name_en")
    private String name_en;
    
    @Column(name="name_tr")
    private String name_tr;

    public Department() {
        this.name_en = "";
        this.name_ru = "";
        this.name_tr = "";
    }

    public Department(Long dep_id, String name_ru, String name_en, String name_tr) {
        this.dep_id = dep_id;
        this.name_ru = name_ru;
        this.name_en = name_en;
        this.name_tr = name_tr;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getDep_id() != null ? getDep_id().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Department)) {
            return false;
        }
        Department other = (Department) object;
        if ((this.getDep_id() == null && other.getDep_id() != null) || (this.getDep_id() != null && !this.dep_id.equals(other.dep_id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "general.tables.Department[ id=" + getDep_id() + " ]";
    }

    /**
     * @return the dep_id
     */
    public Long getDep_id() {
        return dep_id;
    }

    /**
     * @param dep_id the dep_id to set
     */
    public void setDep_id(Long dep_id) {
        this.dep_id = dep_id;
    }

    /**
     * @return the name_ru
     */
    public String getName_ru() {
        return name_ru;
    }

    /**
     * @param name_ru the name_ru to set
     */
    public void setName_ru(String name_ru) {
        this.name_ru = name_ru;
    }

    /**
     * @return the name_en
     */
    public String getName_en() {
        return name_en;
    }

    /**
     * @param name_en the name_en to set
     */
    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    /**
     * @return the name_tr
     */
    public String getName_tr() {
        return name_tr;
    }

    /**
     * @param name_tr the name_tr to set
     */
    public void setName_tr(String name_tr) {
        this.name_tr = name_tr;
    }
    
    
    public String getName(String lang){
        switch (lang) {
            case "en":
                return this.name_en;
            case "tr":
                return this.name_tr;
            
            default:
                return this.name_ru;
        }
    }
}
