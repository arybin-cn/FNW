package info.arybin.fearnotwords.model.orm;

import org.litepal.crud.DataSupport;

import java.util.List;


//An Example belongs to an Expression and has many Translation(in different language)
public class Example extends DataSupport {
    public String language;
    public String body;

    public Expression expression;
    public List<Translation> translations;
}
