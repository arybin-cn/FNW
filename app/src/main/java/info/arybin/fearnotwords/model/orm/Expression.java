package info.arybin.fearnotwords.model.orm;

import org.litepal.crud.DataSupport;

import java.util.List;


//An Expression is an abstract entity which describes things in different language.
//Eg. An Expression may simply be an English word or a Chinese phrase, both of which
//describe the same thing.

//An Expression has many Expression(in different language that describe the same thing)
//and has many Translation/Pronounce/Example(in different language)
public class Expression extends DataSupport {
    public String language;
    public String body;

    public List<Expression> expressions;
    public List<Translation> translations;
    public List<Pronounce> pronounces;
    public List<Example> examples;
}
