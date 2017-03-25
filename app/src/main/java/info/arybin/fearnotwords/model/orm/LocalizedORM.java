package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;


public abstract class LocalizedORM extends DataSupport {
    @Column(nullable = false)
    protected String language;
    @Column(nullable = false)
    protected String body;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
