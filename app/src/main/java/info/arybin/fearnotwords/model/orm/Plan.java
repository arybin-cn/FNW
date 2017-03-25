package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A Plan is an aggregation for many Entity(eg. "GRE")
 * <p>
 * A Plan has many Expressions(that have the same language with it)
 */
public class Plan extends LocalizedORM {
    private long id;
    private List<Entity> entities = new ArrayList<>();


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
