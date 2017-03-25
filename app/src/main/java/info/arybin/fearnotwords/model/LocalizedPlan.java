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


    private List<LocalizedEntity> queryByProgress(int progress) {
        ArrayList<LocalizedEntity> result = new ArrayList<>();
        for (Entity entity : plan.getEntities()) {
            if (entity.getProgress() == progress) {
                result.add(LocalizedEntity.create(entity, plan.getToLanguage()));
            }

        }
        return result;
    }

    public List<LocalizedEntity> getAll() {
        ArrayList<LocalizedEntity> result = new ArrayList<>();
        for (Entity entity : plan.getEntities()) {
            result.add(LocalizedEntity.create(entity, plan.getToLanguage()));
        }
        return result;
    }

    public List<LocalizedEntity> getNew() {
        return queryByProgress(Entity.PROGRESS_NEW);
    }

    public List<LocalizedEntity> getSkipped() {
        return queryByProgress(Entity.PROGRESS_SKIPPED);
    }

    public List<LocalizedEntity> getOld() {
        return queryByProgress(Entity.PROGRESS_OLD);
    }


}
