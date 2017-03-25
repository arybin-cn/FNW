package info.arybin.fearnotwords.model;


import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import info.arybin.fearnotwords.model.orm.Entity;
import info.arybin.fearnotwords.model.orm.Plan;

public class LocalizedPlan {
    public final Plan plan;

    private LocalizedPlan(Plan plan) {
        this.plan = plan;
    }


    public static LocalizedPlan create(String planName) {
        return create(DataSupport.where("name == ?", planName).findFirst(Plan.class, true));
    }


    public static LocalizedPlan create(Plan plan) {
        if (plan != null) {
            return new LocalizedPlan(plan);
        }
        return null;
    }


    private List<Entity> queryByProgress(int progress) {
        ArrayList<Entity> entities = new ArrayList<>();
        plan.getEntities().stream().filter(i -> i.getProgress() == progress).
                forEach(e -> entities.add(e));
        return entities;
    }

    public List<Entity> getAll() {
        return plan.getEntities();
    }

    public List<Entity> getNew() {
        return queryByProgress(Entity.PROGRESS_NEW);
    }

    public List<Entity> getSkipped() {
        return queryByProgress(Entity.PROGRESS_SKIPPED);
    }

    public List<Entity> getOld() {
        return queryByProgress(Entity.PROGRESS_OLD);
    }


}
