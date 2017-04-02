package info.arybin.fearnotwords.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Random;

/**
 * For Test, just like LocalizedEntity
 */
public class FakeEntity implements Memorable, Parcelable {

    private static long count = 0;
    private static Random random = new Random();

    private final long entityID;
    private final String body;
    private final String pronounce;
    private final String translation;
    private final String[] examples;
    private final String[] exampleTranslations;

    private int progress;
    private Date updateTime;


    public FakeEntity(int salt) {
        entityID = ++count;
        body = "admire" + salt;
        if (random.nextInt(20) > 0) {
            pronounce = "[əd'maɪə]";
        } else {
            pronounce = null;
        }

        translation = "vt. 钦佩；赞美\n" +
                "vi. 钦佩；称赞；爱慕";

        int exampleCount = 1 + random.nextInt(3);
        examples = new String[exampleCount];
        exampleTranslations = new String[exampleCount];
        for (int i = 0; i < examples.length; i++) {
            examples[i] = "We stopped halfway to admire the view-" + salt + "-" + i;
            exampleTranslations[i] = "我们中途停下来观赏风景-" + salt + "-" + i;
        }

        updateTime = new Date();
        progress = 0;
    }


    protected FakeEntity(Parcel in) {
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
    public int describeContents() {
        return 0;
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


    public static final Creator<FakeEntity> CREATOR = new Creator<FakeEntity>() {
        @Override
        public FakeEntity createFromParcel(Parcel in) {
            return new FakeEntity(in);
        }

        @Override
        public FakeEntity[] newArray(int size) {
            return new FakeEntity[size];
        }
    };

    @Override
    public String toString() {
        return body;
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
    public void setMemoryProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public int getMemoryProgress() {
        return progress;
    }
}
