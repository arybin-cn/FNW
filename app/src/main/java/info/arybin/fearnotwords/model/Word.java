package info.arybin.fearnotwords.model;


import org.litepal.crud.DataSupport;

import java.util.List;

import info.arybin.fearnotwords.model.orm.Expression;

public class Word implements Translatable {

    private String expression;
    private String pronounce;
    private String translation;
    private List<Translatable> examples;


    public Word(String expressionBody, String language) {
        this(DataSupport.where("body = ?", expressionBody).find(Expression.class).get(0), language);
    }

    //The language of Pronounce and Translation and the Translation of Example
    public Word(Expression expression, String language) {
        this.expression = expression.body;

        //initialization here


    }


    @Override
    public CharSequence getExpression() {
        return expression;
    }

    @Override
    public CharSequence getPronounce() {
        return pronounce;
    }

    @Override
    public CharSequence getTranslation() {
        return translation;
    }

    @Override
    public List<Translatable> getExamples() {
        return examples;
    }
}
