package info.arybin.fearnotwords.model;

import android.os.AsyncTask;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

import info.arybin.fearnotwords.model.orm.Entity;
import info.arybin.fearnotwords.model.orm.Plan;


public class LoadPlanTask extends AsyncTask<LoadPlanTask.Type, Float, ArrayList<LocalizedEntity>> {
    public enum Type {
        NEW(Entity.PROGRESS_NEW),
        SKIPPED(Entity.PROGRESS_SKIPPED),
        OLD(Entity.PROGRESS_OLD);
        private int progress;

        Type(int progress) {
            this.progress = progress;
        }

        public int getProgress() {
            return progress;
        }
    }

    public interface OnProgressListener {
        void onProgressUpdated(float percentage);

        void onProgressCompleted(ArrayList<LocalizedEntity> result);
    }


    private String planName;
    private OnProgressListener listener;
    private int updateProgressThreshold;


    private LoadPlanTask(String planName, OnProgressListener listener, int updateProgressThreshold) {
        this.planName = planName;
        this.listener = listener;
        this.updateProgressThreshold = updateProgressThreshold;
    }

    public static LoadPlanTask setupFor(String planName, OnProgressListener listener) {
        if (null != listener && null != DataSupport.where("name == ?", planName).findFirst(Plan.class)) {
            return new LoadPlanTask(planName, listener, 30);
        }
        return null;
    }

    public static LoadPlanTask setupFor(String planName, OnProgressListener listener, int updateProgressThreshold) {
        if (null != listener
                && updateProgressThreshold > 0
                && null != DataSupport.where("name == ?", planName).findFirst(Plan.class)) {
            return new LoadPlanTask(planName, listener, updateProgressThreshold);
        }
        return null;
    }

    @Override
    protected ArrayList<LocalizedEntity> doInBackground(Type... types) {
        ArrayList<LocalizedEntity> result = new ArrayList<>();
        Plan plan = DataSupport.where("name == ?", planName).findFirst(Plan.class, true);
        int total = 0;
        for (Entity entity : plan.getEntities()) {
            boolean wanted = false;
            for (Type type : types) {
                if (entity.getProgress() == type.getProgress()) {
                    wanted = true;
                    break;
                }
            }
            if (wanted) {
                total += 1;
            }
        }
        for (Entity entity : plan.getEntities()) {
            boolean wanted = false;
            for (Type type : types) {
                if (entity.getProgress() == type.getProgress()) {
                    wanted = true;
                    break;
                }
            }
            if (wanted) {
                result.add(LocalizedEntity.create(entity, plan.getToLanguage()));
                if (result.size() % updateProgressThreshold == 0) {
                    publishProgress(1f * result.size() / total);
                }
            }
        }

        return result;
    }


    @Override
    protected void onProgressUpdate(Float... values) {
        super.onProgressUpdate(values);
        listener.onProgressUpdated(values[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<LocalizedEntity> localizedEntities) {
        super.onPostExecute(localizedEntities);
        listener.onProgressCompleted(localizedEntities);
    }
}
