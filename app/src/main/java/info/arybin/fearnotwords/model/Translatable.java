package info.arybin.fearnotwords.model;

import java.util.List;

public interface Translatable {
    CharSequence getExpression();
    CharSequence getPronounce();
    CharSequence getTranslation();
    List<Translatable> getExamples();
}
