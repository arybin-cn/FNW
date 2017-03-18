package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;

import java.util.List;


/**
 * An Example belongs to an Expression
 * <p>
 * An Example has many translations which in fact are the Examples in different languages
 */
public class Example extends LocalizedORM {
    public List<Example> translations;
    @Column(nullable = false)
    public Expression expression;
}
