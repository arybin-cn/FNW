package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * The ExpressionL is an abstract concept of EntityL in different language
 * An ExpressionL has many EntityL which is specialized in different language
 */
public class ExpressionL extends DataSupport {
    private long id;
    @Column(nullable = false)
    private Expression expression;
    private List<EntityL> entityLs = new ArrayList<>();



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public List<EntityL> getEntityLs() {
        entityLs = DataSupport.where("expressionl_id = ?", String.valueOf(id)).find(EntityL.class);
        return entityLs;
    }

}
