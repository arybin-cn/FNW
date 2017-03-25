package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

//A Pronounce belongs to an Entity
public class Pronounce extends DataSupport {
    private long id;
    @Column(nullable = false)
    private String body;
    @Column(nullable = false)
    private String language;
    @Column(nullable = false)
    private Expression expression;
    private long expression_id;


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

    public Expression getExpression() {
        if (null == expression) {
            expression = DataSupport.find(Expression.class, expression_id);
        }
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
