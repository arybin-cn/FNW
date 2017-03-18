package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;

//A Pronounce belongs to an Expression
public class Pronounce extends LocalizedORM {
    @Column(nullable = false)
    public Expression expression;
}
