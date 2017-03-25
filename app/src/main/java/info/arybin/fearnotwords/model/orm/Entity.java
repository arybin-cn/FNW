package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * The Entity is an concrete instance of an Expression which is used to describe something
 * For example, An Entity may simply be an English word or a Chinese word both of which
 * describe the same thing.
 * <p>
 * An Entity has many Plan(eg. "GRE"/"IELTS")
 */
public class Entity extends DataSupport {
    private long id;
    @Column(nullable = false)
    private String language;
    @Column(nullable = false)
    private String body;
    @Column(defaultValue = "0")
    private int progress;
    private Date updateTime;
    @Column(nullable = false)
    private Expression expression;
    private List<Plan> plans = new ArrayList<>();
    private List<EntityL> examples = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public List<EntityL> getExamples() {
        return examples;
    }

}
