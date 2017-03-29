package info.arybin.fearnotwords.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class TextViewNonAscii extends android.support.v7.widget.AppCompatTextView {
    public TextViewNonAscii(Context context) {
        super(context);
    }

    public TextViewNonAscii(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewNonAscii(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
