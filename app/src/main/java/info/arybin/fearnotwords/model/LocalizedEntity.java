package info.arybin.fearnotwords.model;


import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.arybin.fearnotwords.model.orm.Entity;
import info.arybin.fearnotwords.model.orm.EntityL;

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

    public LocalizedEntity create(Entity entity, String language) {
        if (null != entity && null != language) {
            return new LocalizedEntity(entity, language);
        }
        return null;
    }

    public LocalizedEntity create(String entityBody, String language) {
        Entity entity = DataSupport.where("body = ?", entityBody).findFirst(Entity.class);
        return create(entity, language);
    }


    private LocalizedEntity(Entity entity, String language) {
        entityID = entity.getId();
        body = entity.getBody();
        progress = entity.getProgress();
        updateTime = entity.getUpdateTime();
        pronounce = entity.getExpression().getPronounce(entity.getLanguage()).getBody();
        translation = entity.getExpression().getTranslation(language).getBody();
        examples = new ArrayList<>();
        entity.getExpression().getExpressionLs().forEach(e -> {
                    CharSequence original = null;
                    CharSequence translation = null;
                    for (EntityL entityL : e.getEntityLs()) {
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
        );
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

    public int setAsNew() {
        return setProgress(Entity.PROGRESS_NEW);
    }

    public int setAsSkipped() {
        return setProgress(Entity.PROGRESS_SKIPPED);
    }

    public int setAsOld() {
        return setProgress(Entity.PROGRESS_OLD);
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
