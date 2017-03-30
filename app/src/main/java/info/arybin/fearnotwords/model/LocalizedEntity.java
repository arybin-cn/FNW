package info.arybin.fearnotwords.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.Pair;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;

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
public class LocalizedEntity implements Memorable, Parcelable {

    private final long entityID;
    private final String body;
    private final String pronounce;
    private final String translation;
    private final String[] examples;
    private final String[] exampleTranslations;

    private int progress;
    private Date updateTime;


    protected LocalizedEntity(Parcel in) {
        entityID = in.readLong();
        body = in.readString();
        pronounce = in.readString();
        translation = in.readString();
        int exampleCount = in.readInt();
        examples = new String[exampleCount];
        exampleTranslations = new String[exampleCount];
        in.readStringArray(examples);
        in.readStringArray(exampleTranslations);
        progress = in.readInt();
        long updateTimeLong = in.readLong();
        updateTime = new Date(updateTimeLong);

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(entityID);
        dest.writeString(body);
        dest.writeString(pronounce);
        dest.writeString(translation);
        dest.writeInt(examples.length);
        dest.writeStringArray(examples);
        dest.writeStringArray(exampleTranslations);
        dest.writeInt(progress);
        dest.writeLong(updateTime.getTime());
    }


    public static final Creator<LocalizedEntity> CREATOR = new Creator<LocalizedEntity>() {
        @Override
        public LocalizedEntity createFromParcel(Parcel in) {
            return new LocalizedEntity(in);
        }

        @Override
        public LocalizedEntity[] newArray(int size) {
            return new LocalizedEntity[size];
        }
    };

    public static LocalizedEntity create(Entity entity, String language) {
        if (null != entity && null != language) {
            return new LocalizedEntity(entity, language);
        }
        return null;
    }

    public static LocalizedEntity create(String entityBody, String language) {
        Entity entity = DataSupport.where("body = ?", entityBody).findFirst(Entity.class);
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

        ArrayList<Pair<CharSequence, CharSequence>> tmpExamples = new ArrayList<>();
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
                    tmpExamples.add(new Pair<>(original, translation));
                    break;
                }
            }
        }
        int length = tmpExamples.size();
        examples = new String[length];
        exampleTranslations = new String[length];
        for (int i = 0; i < length; i++) {
            examples[i] = tmpExamples.get(i).first.toString();
            exampleTranslations[i] = tmpExamples.get(i).second.toString();
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
    public CharSequence getPronounce() {
        return pronounce;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public Translatable getExampleAt(final int index) {
        return new Translatable() {
            @Override
            public CharSequence getOriginal() {
                return examples[index];
            }

            @Override
            public CharSequence getTranslation() {
                return exampleTranslations[index];
            }
        };
    }

    @Override
    public int getExampleCount() {
        return examples.length;
    }


}
