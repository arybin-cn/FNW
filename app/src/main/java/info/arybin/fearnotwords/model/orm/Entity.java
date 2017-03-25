package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;

import java.util.List;


/**
 * The Entity is an concrete instance of an Expression which is used to describe something
 * For example, An Entity may simply be an English word or a Chinese word both of which
 * describe the same thing.
 * <p>
 * An Entity has many Plan(eg. "GRE"/"IELTS")
 */
public class Entity extends LocalizedORM {
    private long id;
    @Column(nullable = false)
    private Expression expression;
    private List<Plan> plans;


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

}
