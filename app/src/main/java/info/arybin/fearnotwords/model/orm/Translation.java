package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;

//A Translation belongs to an Expression
public class Translation extends LocalizedORM {
    @Column(nullable = false)
    public Expression expression;
}
