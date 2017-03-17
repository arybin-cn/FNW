package info.arybin.fearnotwords.model.orm;

import org.litepal.crud.DataSupport;

//A Pronounce belongs to an Expression
public class Pronounce extends DataSupport {
    public String language;
    public String body;

    public Expression expression;
}
