package info.arybin.fearnotwords.model;


import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import info.arybin.fearnotwords.model.orm.Example;
import info.arybin.fearnotwords.model.orm.Expression;
import info.arybin.fearnotwords.model.orm.Pronounce;
import info.arybin.fearnotwords.model.orm.Translation;

/**
 * An LocalizedEntity is an abstract concept, it can be word or phrase or
 * any things that are Translatable, Pronounceable and Exampleable with specific language.
 * <p>
 * The language of body of the LocalizedEntity is determined by the language of Expression used to construct.
 * The language of pronounce/translation and the translation of examples are determined by the language
 * used to construct.
 */
public class LocalizedEntity implements Translatable, Pronounceable, Exampleable {

    private final CharSequence body;
    private final CharSequence pronounce;
    private final CharSequence translation;
    private final List<Translatable> examples;


    public LocalizedEntity(String expressionBody, CharSequence language) {
        this(DataSupport.where("body = ?", expressionBody).find(Expression.class).get(0), language);
    }

    public LocalizedEntity(Expression expression, CharSequence language) {
        this.body = expression.body;
        this.pronounce = expression.pronounces.stream().
                filter(p -> language.equals(p.language)).
                findAny().orElse(new Pronounce()).body;
        this.translation = expression.translations.stream().
                filter(t -> language.equals(t.language)).
                findAny().orElse(new Translation()).body;
        this.examples = buildExamples(expression.examples, language);

    }

    private List<Translatable> buildExamples(List<Example> examples, CharSequence language) {
        //Not using Stream#collect here for compatibility.
        ArrayList<Translatable> results = new ArrayList<>(examples.size());
        examples.stream().
                map(i -> new ExampleWrapper(i, language)).
                forEach(results::add);
        return results;
    }


    public CharSequence getOriginal() {
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


    private class ExampleWrapper implements Translatable {
        private final Example example;
        private final CharSequence language;

        ExampleWrapper(Example example, CharSequence language) {
            this.example = example;
            this.language = language;
        }

        @Override
        public CharSequence getOriginal() {
            return example.body;
        }

        @Override
        public CharSequence getTranslation() {
            return example.translations.stream().
                    filter(t -> language.equals(t.language)).
                    findAny().orElse(new Example()).body;
        }

    }


}
