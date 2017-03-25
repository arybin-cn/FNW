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


    private <S> S localizedAssoc(Class<S> klass, String language) {
        S s = DataSupport.where("expression_id = ? and language = ?", String.valueOf(id), language).find(klass).get(0);
        return s;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Entity getEntity(String language) {
        return localizedAssoc(Entity.class, language);
    }

    public List<Entity> getEntities() {
        entities = DataSupport.where("expression_id == ?", String.valueOf(id)).find(Entity.class);
        return entities;
    }

    public Translation getTranslation(String language) {
        return localizedAssoc(Translation.class, language);
    }

    public List<Translation> getTranslations() {
        translations = DataSupport.where("expression_id == ?", String.valueOf(id)).find(Translation.class);
        return translations;
    }

    public Pronounce getPronounce(String language) {
        return localizedAssoc(Pronounce.class, language);
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
