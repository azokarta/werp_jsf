package general.tables.search;

import general.tables.Department;

public class DepartmentSearch extends Department {

    public String getCondition() {
        String condition = "";
        if (this.getName_ru().length() > 0) {
            condition += String.format(" name_ru LIKE '%s'", "%"
                    + this.getName_ru() + "%");
        }

        if (this.getName_en().length() > 0) {
            condition += String.format(" name_en LIKE '%s'", "%"
                    + this.getName_en() + "%");
        }

        if (this.getName_tr().length() > 0) {
            condition += String.format(" name_tr LIKE '%s'", "%"
                    + this.getName_tr() + "%");
        }

        return condition;
    }
}
