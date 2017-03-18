package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;



public class LocalizedORM extends DataSupport {
    @Column(nullable = false)
    public String language;
    @Column(nullable = false)
    public String body;
}
