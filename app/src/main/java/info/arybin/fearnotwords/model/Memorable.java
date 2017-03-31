package info.arybin.fearnotwords.model;


interface Memorable extends Translatable, Pronounceable, Exampleable {
    void setMemoryProgress(int progress);

    int getMemoryProgress();
}
