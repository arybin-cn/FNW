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
        body = "body-" + salt;
        if (random.nextInt(2) > 0) {
            pronounce = "['bɒdɪ]";
        } else {
            pronounce = null;
        }

        translation = "n. 身体；主体；大量；团体；主要部分\nvt. 赋以形体\n[复数bodies,过去式bodied,过去分词bodied,现在分词bodying,第三人称单数bodies]";

        int exampleCount = 1 + random.nextInt(3);
        examples = new String[exampleCount];
        exampleTranslations = new String[exampleCount];
        for (int i = 0; i < examples.length; i++) {
            examples[i] = "This is a usage example for " + body + ".";
            exampleTranslations[i] = "这是一个仅用于测试的例句示例";
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
        return String.format("%s\n%s\n%s\n", body, pronounce, translation);
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
