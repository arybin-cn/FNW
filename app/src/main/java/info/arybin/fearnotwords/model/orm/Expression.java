package info.arybin.fearnotwords.model.orm;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * The Expression is an abstract concept of which is used to describe something in different language
 * An Expression has many Expressions which is specialized in different language
 * <p>
 * An Expression has many Entities/Translation/Pronounce(in different language)
 * An Expression has many ExpressionLs
 */
public class Expression extends DataSupport {
    private long id;
    private List<Entity> entities = new ArrayList<>();
    private List<Translation> translations = new ArrayList<>();
    private List<Pronounce> pronounces = new ArrayList<>();
    private List<ExpressionL> expressionLs = new ArrayList<>();


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Entity getEntity(String language) {
        for (Entity entity : getEntities()) {
            if (language.equals(entity.getLanguage())) {
                return entity;
            }
        }
        return null;
    }

    public List<Entity> getEntities() {
        entities = DataSupport.where("expression_id == ?", String.valueOf(id)).find(Entity.class);
        return entities;
    }

    public Translation getTranslation(CharSequence language) {
        for (Translation translation : getTranslations()) {
            if (language.equals(translation.getLanguage())) {
                return translation;
            }
        }
        return null;
    }

    public List<Translation> getTranslations() {
        translations = DataSupport.where("expression_id == ?", String.valueOf(id)).find(Translation.class);
        return translations;
    }

    public Pronounce getPronounce(String language) {
        for (Pronounce pronounce : getPronounces()) {
            if (language.equals(pronounce.getLanguage())) {
                return pronounce;
            }
        }
        return null;
    }

    public List<Pronounce> getPronounces() {
        pronounces = DataSupport.where("expression_id == ?", String.valueOf(id)).find(Pronounce.class);
        return pronounces;
    }

    public List<ExpressionL> getExpressionLs() {
        expressionLs = DataSupport.where("expression_id == ?", String.valueOf(id)).find(ExpressionL.class);
        return expressionLs;
    }

}
