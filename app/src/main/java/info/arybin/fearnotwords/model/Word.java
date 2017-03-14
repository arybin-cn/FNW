package info.arybin.fearnotwords.model;


public class Word implements Translatable {

    private final CharSequence mFromLang;
    private final CharSequence mToLang;

    public Word(CharSequence fromLang, CharSequence toLang, CharSequence entity) {
        mFromLang = fromLang;
        mToLang = toLang;
    }


    @Override
    public CharSequence getEntity() {
        return null;
    }

    @Override
    public CharSequence getPronounce() {
        return null;
    }

    @Override
    public CharSequence getTranslation() {
        return null;
    }

    @Override
    public CharSequence getExample() {
        return null;
    }
}
