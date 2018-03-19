package com.example.dima.smarttool;

/**
 * Created by dima on 19.03.2018.
 */

public class Rule {
    long id;
    String condition;
    String act;

    public Rule(long id, String condition, String act) {
        this.id = id;
        this.condition = condition;
        this.act = act;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getAct() {
        return act;
    }

    public void setact(String act) {
        this.act = act;
    }



    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", condition='" + condition + '\'' +
                ", act='" + act + '\'' +
                '}'+"\n";
    }
}

