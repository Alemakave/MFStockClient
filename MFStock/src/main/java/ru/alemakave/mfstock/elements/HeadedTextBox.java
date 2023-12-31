package ru.alemakave.mfstock.elements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import ru.alemakave.mfstock.R;

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
        setBackgroundColor(Color.rgb(32, 32, 32));;

        header = new TextView(context);
        header.setText("Empty Header");
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
            switch (attr) {
                case R.styleable.HeadedTextBox_header:
                    header.setText(attributes.getString(attr));
                    break;
                case R.styleable.HeadedTextBox_textAlignment:
                    header.setTextAlignment(attributes.getInt(R.styleable.HeadedTextBox_textAlignment, 0));
                    break;
                case R.styleable.HeadedTextBox_inputBarText:
                    input.setText(attributes.getString(attr));
                    break;
            }
        }
    }

    public TextView getHeader() {
        return header;
    }

    public EditText getInput() {
        return input;
    }
}
