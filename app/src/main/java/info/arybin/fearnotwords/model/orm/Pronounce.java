package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;

//A Pronounce belongs to an Entity
public class Pronounce extends LocalizedORM {
    private long id;
    @Column(nullable = false)
    private Expression expression;



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
