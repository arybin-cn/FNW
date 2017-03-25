package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;


/**
 * The suffix "L" stands for Long, Thus EntityL is just like Entity
 * <p>
 * The EntityL is an concrete instance of an ExpressionL which is used to describe something
 * For example, An EntityL may simply be an English idiom or a Chinese idiom both of which
 * describe the same thing.
 * <p>
 * An EntityL belongs to an ExpressionL
 */
public class EntityL extends DataSupport {
    private long id;
    @Column(nullable = false)
    private String language;
    @Column(nullable = false)
    private String body;
    @Column(nullable = false)
    private ExpressionL expressionL;



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

    public ExpressionL getExpressionL() {
        return expressionL;
    }

    public void setExpressionL(ExpressionL expressionL) {
        this.expressionL = expressionL;
    }
}
