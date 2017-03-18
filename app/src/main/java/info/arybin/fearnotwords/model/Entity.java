package info.arybin.fearnotwords.model;


import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import info.arybin.fearnotwords.model.orm.Example;
import info.arybin.fearnotwords.model.orm.Expression;
import info.arybin.fearnotwords.model.orm.Pronounce;
import info.arybin.fearnotwords.model.orm.Translation;

/**
 *
 * An Entity is an abstract concept, it can be word or phrase or
 * any things that are Translatable, Pronounceable and Exampleable.
 *
 * The language of body of the Entity is determined by the language of Expression used to construct.
 * The language of pronounce/translation and the translation of examples are determined by the language
 * used to construct.
 */
public class Entity implements Translatable, Pronounceable, Exampleable {

    private String body;
    private String pronounce;
    private String translation;
    private List<Translatable> examples;


    public Entity(String expressionBody, String language) {
        this(DataSupport.where("body = ?", expressionBody).find(Expression.class).get(0), language);
    }

    public Entity(Expression expression, String language) {
        this.body = expression.body;
        this.pronounce = expression.pronounces.stream().
                filter(p -> language.equals(p.language)).
                findAny().orElse(new Pronounce()).body;
        this.translation = expression.translations.stream().
                filter(t -> language.equals(t.language)).
                findAny().orElse(new Translation()).body;
        this.examples = buildExamples(expression.examples, language);
    }

    private List<Translatable> buildExamples(List<Example> examples, String language) {
        //Not using Stream#collect here for compatibility.
        ArrayList<Translatable> results = new ArrayList<>(examples.size());
        examples.stream().map(i ->
                new Translatable() {
                    public CharSequence getBody() {
                        return i.body;
                    }
                    @Override
                    public CharSequence getTranslation() {
                        return i.translations.stream().
                                filter(t -> language.equals(t.language)).
                                findAny().orElse(new Translation()).body;
                    }
                }
        ).forEach(results::add);
        return results;
    }


    public CharSequence getBody() {
        return body;
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
