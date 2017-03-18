package info.arybin.fearnotwords.model;


import org.litepal.crud.ClusterQuery;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import info.arybin.fearnotwords.model.orm.Expression;
import info.arybin.fearnotwords.model.orm.Plan;

public class LocalizedPlan {
    public final String name;
    public final String language;

    public LocalizedPlan(String name, String language) {
        this.name = name;
        this.language = language;
    }

    public List<Expression> getAll() {
        return queryPlans(DataSupport.where("body = ? and language = ?", name, language));
    }

    public List<Expression> getNew() {
        return queryPlans(DataSupport.where("body = ? and language = ? and progress = ?", name, language, "0"));
    }

    public List<Expression> getOld() {
        return queryPlans(DataSupport.where("body = ? and language = ? and progress != ?", name, language, "0"));
    }

    private List<Expression> queryPlans(ClusterQuery query) {
        List<Plan> plans = query.find(Plan.class);
        ArrayList<Expression> results = new ArrayList<>(plans.size());
        plans.stream().
                map(i -> i.expression).
                forEach(results::add);
        return results;
    }

}
