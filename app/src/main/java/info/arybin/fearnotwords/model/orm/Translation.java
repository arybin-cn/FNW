package info.arybin.fearnotwords.model.orm;

import org.litepal.crud.DataSupport;


//A Translation belongs to an Expression or an Example(but no need to build relations)
public class Translation extends DataSupport {
    public String language;
    public String body;
}
