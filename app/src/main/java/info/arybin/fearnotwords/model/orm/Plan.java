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
    private String name;
    @Column(nullable = false)
    private String from_language;
    @Column(nullable = false)
    private String to_language;

    private List<Entity> entities = new ArrayList<>();


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getFromLanguage() {
        return from_language;
    }

    public void setFromLanguage(String from_language) {
        this.from_language = from_language;
    }

    public String getToLanguage() {
        return to_language;
    }

    public void setToLanguage(String to_language) {
        this.to_language = to_language;
    }

    public List<Entity> getEntities() {
        return entities;
    }

}
