package info.arybin.fearnotwords.model;


public interface Memorable extends Translatable, Pronounceable, Exampleable {
    void setMemoryProgress(int progress);

    int getMemoryProgress();
}
