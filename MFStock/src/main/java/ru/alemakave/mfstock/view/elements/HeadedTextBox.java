package ru.alemakave.mfstock.view.elements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;

import lombok.Getter;
import ru.alemakave.mfstock.R;

@Getter
public class HeadedTextBox extends LinearLayout {
    private TextView header;
    private EditText input;

    public HeadedTextBox(Context context) {
        this(context, null);
    }

    public HeadedTextBox(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeadedTextBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setOrientation(VERTICAL);
        setPadding(10,5,10,5);
        setBackgroundColor(Color.rgb(32, 32, 32));

        header = new TextView(context);
        header.setText(context.getText(R.string.empty_header));
        header.setTextAlignment(TEXT_ALIGNMENT_CENTER);

        input = new EditText(context);

        header.setTextColor(Color.rgb(200,200,200));
        input.setTextColor(Color.rgb(200,200,200));
        header.setBackgroundColor(Color.argb(0,0,0,0));
        input.setBackgroundColor(Color.argb(0,0,0,0));

        readLayoutAttributes(context, attrs, defStyle);

        LayoutParams imageViewLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
        addViewInLayout(header, -1, imageViewLayoutParams);
        addViewInLayout(input, -1, imageViewLayoutParams);
    }

    @SuppressLint("NonConstantResourceId")
    private void readLayoutAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HeadedTextBox, defStyleAttr, 0);

        for (int i = 0; i < attributes.getIndexCount(); i++) {
            int attr = attributes.getIndex(i);
            if (attr == R.styleable.HeadedTextBox_header) {
                header.setText(attributes.getString(attr));
            } else if (attr == R.styleable.HeadedTextBox_textAlignment) {
                header.setTextAlignment(attributes.getInt(R.styleable.HeadedTextBox_textAlignment, 0));
            } else if (attr == R.styleable.HeadedTextBox_inputBarText) {
                input.setText(attributes.getString(attr));
            }
        }

        attributes.close();
    }
}
