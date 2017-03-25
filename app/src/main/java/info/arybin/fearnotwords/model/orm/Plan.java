package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


/**
 * A Plan is an aggregation for many Entity(eg. "GRE")
 * <p>
 * A Plan has many Expressions(that have the same language with it)
 */
public class Plan extends DataSupport {
    private long id;
    @Column(nullable = false)
    private String body;
    @Column(nullable = false)
    private String language;
    private List<Entity> entities = new ArrayList<>();


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<Entity> getEntities() {
        return entities;
    }


    public List<Entity> getAll() {
        return DataSupport.where("plan_id == ?",
                String.valueOf(id)).find(Entity.class);
    }

    public List<Entity> getSkipped() {
        return DataSupport.where("plan_id == ? and progress == ?",
                String.valueOf(id),
                String.valueOf(Entity.PROGRESS_SKIPPED)).find(Entity.class);
    }


    public List<Entity> getNew() {
        return DataSupport.where("plan_id == ? and progress == ?",
                String.valueOf(id),
                String.valueOf(Entity.PROGRESS_NEW)).find(Entity.class);
    }

    public List<Entity> getOld() {
        return DataSupport.where("plan_id == ? and progress == ?",
                String.valueOf(id),
                String.valueOf(Entity.PROGRESS_OLD)).find(Entity.class);
    }
}
