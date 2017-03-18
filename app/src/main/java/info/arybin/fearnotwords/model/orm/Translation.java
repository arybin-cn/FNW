package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;


//A Translation belongs to an Expression or an Example(but no need to build relations)
public class Translation extends DataSupport {
    @Column(nullable = false)
    public String language;
    @Column(nullable = false)
    public String body;
}
