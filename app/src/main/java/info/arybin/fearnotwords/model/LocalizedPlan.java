package info.arybin.fearnotwords.model;


import android.provider.ContactsContract;

import org.litepal.crud.ClusterQuery;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import info.arybin.fearnotwords.model.orm.Entity;
import info.arybin.fearnotwords.model.orm.Expression;
import info.arybin.fearnotwords.model.orm.Plan;

public class LocalizedPlan {
    public final Plan plan;

    private LocalizedPlan(Plan plan) {
        this.plan = plan;
    }


    public static LocalizedPlan create(String planName) {
        if (planName != null) {
            List<Plan> plans = DataSupport.where("body = ?", planName).find(Plan.class);
            if (plans.size() > 0) {
                return new LocalizedPlan(plans.get(0));
            }
        }

        return null;
    }


    public static LocalizedPlan create(Plan plan) {
        if (plan != null) {
            return new LocalizedPlan(plan);
        }
        return null;
    }


    public List<Entity> getAll() {
        return plan.getAll();
    }

    public List<Entity> getNew() {

        return plan.getNew();
    }

    public List<Entity> getSkipped() {
        return plan.getSkipped();
    }

    public List<Entity> getOld() {
        return plan.getOld();
    }


}
