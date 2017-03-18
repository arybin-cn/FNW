package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;

import java.util.Date;
import java.util.List;


/**
 * The Expression is an abstract concept which is used to describe something
 * For example, An Expression may simply be an English word or a Chinese phrase both of which
 * describe the same thing.
 * <p>
 * An Expression has many Expression(in different language that describe the same thing)
 * An Expression has many Translation/Pronounce(in different language)
 * An Expression has many Example that has the same language with it
 */
public class Expression extends LocalizedORM {
    public List<Expression> expressions;
    public List<Translation> translations;
    public List<Pronounce> pronounces;
    public List<Example> examples;

    @Column(defaultValue = "0")
    public int progress;
    public Date updateTime;

}
