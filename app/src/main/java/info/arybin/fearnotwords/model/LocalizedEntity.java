package info.arybin.fearnotwords.model;


import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.arybin.fearnotwords.model.orm.Entity;
import info.arybin.fearnotwords.model.orm.EntityL;
import info.arybin.fearnotwords.model.orm.ExpressionL;
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
@SuppressWarnings("unused")
public class LocalizedEntity implements Memorable {

    private final long entityID;
    private final CharSequence body;
    private final CharSequence pronounce;
    private final CharSequence translation;
    private final List<Translatable> examples;
    private int progress;
    private Date updateTime;

    public static LocalizedEntity create(Entity entity, String language) {
        if (null != entity && null != language) {
            return new LocalizedEntity(entity, language);
        }
        return null;
    }

    public static LocalizedEntity create(String entityBody, String language) {
        Entity entity = DataSupport.where("body = ?", entityBody).findFirst(Entity.class, true);
        return create(entity, language);
    }


    private LocalizedEntity(Entity entity, String language) {
        Pronounce tmpPronounce;
        Translation tmpTranslation;

        entityID = entity.getId();
        body = entity.getBody();
        progress = entity.getProgress();
        updateTime = entity.getUpdateTime();

        tmpPronounce = entity.getExpression().getPronounce(entity.getLanguage());
        if (null != tmpPronounce) {
            pronounce = tmpPronounce.getBody();
        } else {
            pronounce = null;
        }

        tmpTranslation = entity.getExpression().getTranslation(language);
        if (null != tmpTranslation) {
            translation = tmpTranslation.getBody();
        } else {
            translation = null;
        }

        examples = new ArrayList<>();
        for (ExpressionL expressionL : entity.getExpression().getExpressionLs()) {
            CharSequence original = null;
            CharSequence translation = null;
            for (EntityL entityL : expressionL.getEntityLs()) {
                if (entity.getLanguage().equals(entityL.getLanguage())) {
                    original = entity.getBody();
                }
                if (entity.getLanguage().equals(language)) {
                    translation = entity.getBody();
                }
                if (original != null && translation != null) {
                    examples.add(new ExampleWrapper(original, translation));
                    break;
                }
            }

        }

    }


    public int getProgress() {
        return progress;
    }

    public int setProgress(int progress) {
        return setProgress(progress, true);
    }


    public int setProgress(int progress, boolean updateTime) {
        this.progress = progress;
        if (updateTime) {
            setUpdateTime(new Date());
        }
        return progress;
    }

    public void setAsNew(boolean autoSave) {
        setProgress(Entity.PROGRESS_NEW);
        if (autoSave) {
            save();
        }
    }

    public void setAsNew() {
        setAsNew(true);
    }

    public void setAsSkipped(boolean autoSave) {
        setProgress(Entity.PROGRESS_SKIPPED);
        if (autoSave) {
            save();
        }
    }


    public void setAsSkipped() {
        setAsSkipped(true);
    }

    public void setAsOld(boolean autoSave) {
        setProgress(Entity.PROGRESS_OLD);
        if (autoSave) {
            save();
        }
    }

    public void setAsOld() {
        setAsOld(true);
    }

    public int save() {
        Entity entity = new Entity();
        entity.setProgress(progress);
        entity.setUpdateTime(updateTime);
        return entity.update(entityID);
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public CharSequence getOriginal() {
        return body;
    }

    @Override
    public CharSequence getTranslation() {
        return translation;
    }

    @Override
    public List<Translatable> getExamples() {
        return examples;
    }

    @Override
    public CharSequence getPronounce() {
        return pronounce;
    }


    private class ExampleWrapper implements Translatable {
        private final CharSequence original;
        private final CharSequence translation;

        ExampleWrapper(CharSequence original, CharSequence translation) {
            this.original = original;
            this.translation = translation;
        }

        @Override
        public CharSequence getOriginal() {
            return original;
        }

        @Override
        public CharSequence getTranslation() {
            return translation;
        }

    }

}
