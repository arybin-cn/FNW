package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.List;


//An Example belongs to an Expression and has many Translation(in different language)
public class Example extends DataSupport {
    @Column(nullable = false)
    public String language;
    @Column(nullable = false)
    public String body;

    public Expression expression;
    public List<Translation> translations;
}
