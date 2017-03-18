package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

//A Pronounce belongs to an Expression
public class Pronounce extends DataSupport {
    @Column(nullable = false)
    public String language;
    @Column(nullable = false)
    public String body;
    @Column(nullable = false)
    public Expression expression;
}
