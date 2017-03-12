package info.arybin.fearnotwords.model;

public interface Translatable {
    CharSequence getEntity();
    CharSequence getPronounce();
    CharSequence getTranslation();
    CharSequence getExample();
}
