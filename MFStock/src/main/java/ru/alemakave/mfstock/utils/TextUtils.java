package ru.alemakave.mfstock.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class TextUtils {
    public static void appendToTextView(TextView textView, String textValue) {
        appendToTextView(textView, textValue, Color.YELLOW);
    }

    public static void appendToTextView(TextView textView, String textValue, int textColor) {
        SpannableStringBuilder text = new SpannableStringBuilder();
        if (textView.getText().length() != 0) {
            textValue = "\n" + textValue;
        }

        if (textValue.endsWith("\n")) {
            textValue = textValue.substring(0, textValue.length()-1);
        }
        text.append(textValue);
        text.setSpan(new ForegroundColorSpan(textColor), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.append(text);
    }
}
