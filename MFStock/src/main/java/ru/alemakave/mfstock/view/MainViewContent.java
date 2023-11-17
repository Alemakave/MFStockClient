package ru.alemakave.mfstock.view;

import android.graphics.Color;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ru.alemakave.mfstock.MainActivity;
import ru.alemakave.mfstock.R;
import ru.alemakave.mfstock.commands.Action;

import static ru.alemakave.mfstock.utils.TextUtils.appendToTextView;
import static ru.alemakave.mfstock.commands.Actions.*;

public class MainViewContent implements IViewContent {
    @Override
    public void draw(AppCompatActivity activity) {
        MainActivity mainActivity = (MainActivity) activity;

        TextView appInfoView = mainActivity.findViewById(R.id.applicationInfoTextView);
        EditText inputBox = mainActivity.findViewById(R.id.inputBox);
        inputBox.requestFocus();

        appInfoView.setTextSize(mainActivity.getSettings().getFontSize());
        inputBox.setTextSize(mainActivity.getSettings().getFontSize());

        if (((LinearLayout)mainActivity.findViewById(R.id.commandView)).getChildCount() == 0) {
            drawCommandsMenu(activity);
        }

        inputBox.setOnKeyListener((v, keyCode, event) -> {
            String inputData = inputBox.getText().toString().trim().replaceAll("\n", "");

            if(keyCode == KeyEvent.KEYCODE_ENTER) {
                if (inputData.isEmpty()) {
                    return true;
                }

                if (inputData.matches("^[0-9]*$") && callCommand(activity, Integer.parseInt(inputData))) {
                    inputBox.setText("");
                    inputBox.requestFocus();
                    drawCommandsMenu(activity);

                    return true;
                }

                if (inputData.startsWith("!") && inputData.endsWith("?")) {
                    mainActivity.onScan(inputData);

                    drawCommandsMenu(mainActivity);

                    callCommand(mainActivity, UPDATE_CACHE.getId());
                }
                else {
                    mainActivity.clearInfoTexView();
                    appendToTextView(appInfoView, "Сканируйте штрихкод!\nРучной ввод запрещен!", Color.RED); //TODO: Localize
                }
                inputBox.setText("");
                inputBox.requestFocus();
                return true;
            }
            return false;
        });
    }

    private boolean callCommand(AppCompatActivity activity, int actionId) {
        Action actionFromId = null;

        for (Action action : values()) {
            if (action.getId() == actionId) {
                actionFromId = action;
                break;
            }
        }

        return callCommand(activity, actionFromId);
    }

    private boolean callCommand(AppCompatActivity activity, Action action) {
        if (action == null || !action.isPossibleCall(activity)) {
            return false;
        }

        action.call(activity);

        return true;
    }

    private void drawCommandsMenu(AppCompatActivity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        LinearLayout commandView = mainActivity.findViewById(R.id.commandView);
        commandView.removeAllViews();

        for (Action action : values()) {
            if (action == UPDATE_CACHE) {
                continue;
            }

            addActionView(activity, action);
        }
    }

    private void addActionView(AppCompatActivity activity, Action action) {
        if (!action.isPossibleCall(activity)) {
            return;
        }

        MainActivity mainActivity = (MainActivity) activity;
        LinearLayout commandView = mainActivity.findViewById(R.id.commandView);

        TextView commandsTextView = new TextView(activity);
        commandsTextView.setText(String.format("%d %s", action.getId(), action.getDescription(activity)));

        commandView.addView(commandsTextView);
    }
}
