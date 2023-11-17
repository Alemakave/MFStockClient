package ru.alemakave.mfstock.commands;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.alemakave.android.utils.Logger;
import ru.alemakave.mfstock.MainActivity;
import ru.alemakave.mfstock.R;
import ru.alemakave.mfstock.model.json.CachedData;
import ru.alemakave.xlsx_parser.SheetData;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static ru.alemakave.mfstock.utils.TextUtils.appendToTextView;

public class Actions {
    // При сканировании предлагать сохранить по коду материала информацию в кэш
    public static final Action SAVE_TO_CACHE = new SaveCacheAction();
    public static final Action CLEAR_CACHE = new ClearCacheAction();
    // При сканировании того-же кода товара, при соединении с БД обновлять данные, если дата сохранения изменилась
    public static final Action UPDATE_CACHE = new UpdateCacheAction();
    public static final Action GET_CACHED_NOMS = new GetCachedNoms();
    private static List<Action> values = null;

    private Actions() {}

    public static List<Action> values() {
        if (values == null) {
            values = new ArrayList<>();
        } else {
            return values;
        }

        for (Field field : Actions.class.getFields()) {
            boolean isValidField = true;

            int modifier = field.getModifiers();
            isValidField &= Modifier.isPublic(modifier);
            isValidField &= Modifier.isStatic(modifier);
            isValidField &= Modifier.isFinal(modifier);
            isValidField &= field.getType().getName().equals(Action.class.getName());

            if (isValidField) {
                try {
                    Object action = field.get(new Object());
                    values.add((Action) action);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return values;
    }

    private static class SaveCacheAction implements Action {
        @Override
        public int getId() {
            return 1;
        }

        @Override
        public void call(Context context) {
            if (!(context instanceof MainActivity)) {
                throw new ClassCastException(String.format("Context parameter \"%s\" is not MainActivity", context.getClass().getName()));
            }

            String nomCode = ((MainActivity) context).getNomCodeScannedBarcode();
            ObjectMapper mapper = new ObjectMapper();
            try {
                SheetData cacheSheetData = ((MainActivity) context).getDataFromScan(nomCode);
                CachedData cacheData = new CachedData(((MainActivity) context).getDBData().getDateTimeString(), cacheSheetData);
                mapper.writeValue(context.openFileOutput(nomCode + ".json", Context.MODE_PRIVATE), cacheData);

                ((MainActivity) context).getCachedData().put(nomCode, cacheData);
            } catch (Exception e) {
                Logger.e(context, e.getClass().getName(), e.getMessage());
            }
        }

        @Override
        public String getDescription(Context context) {
            String nomCode = ((MainActivity) context).getNomCodeScannedBarcode();
            return "Save to cache by nom code: " + nomCode;
        }

        @Override
        public boolean isPossibleCall(Context context) {
            if (!(context instanceof MainActivity)) {
                throw new ClassCastException(String.format("Context parameter \"%s\" is not MainActivity", context.getClass().getName()));
            }

            MainActivity mainActivity = (MainActivity)context;
            LinearLayout infoView = mainActivity.findViewById(R.id.infoView);

            boolean isPossible = infoView.getChildCount() > 0;
            isPossible &= !mainActivity.getNomCodeScannedBarcode().isEmpty();
            isPossible &= !mainActivity.getCachedData().containsKey(mainActivity.getNomCodeScannedBarcode());

            return isPossible;
        }
    }

    private static class ClearCacheAction implements Action {
        @Override
        public int getId() {
            return 2;
        }

        @Override
        public void call(Context context) {
            if (!(context instanceof MainActivity)) {
                throw new ClassCastException(String.format("Context parameter \"%s\" is not MainActivity", context.getClass().getName()));
            }

            String nomCode = ((MainActivity) context).getNomCodeScannedBarcode();
            File cacheFile = new File(context.getFilesDir(), nomCode + ".json");
            cacheFile.delete();
            ((MainActivity) context).getCachedData().remove(nomCode);
        }

        @Override
        public String getDescription(Context context) {
            String nomCode = ((MainActivity) context).getNomCodeScannedBarcode();
            return "Clear cache for code: " + nomCode;
        }

        @Override
        public boolean isPossibleCall(Context context) {
            if (!(context instanceof MainActivity)) {
                throw new ClassCastException(String.format("Context parameter \"%s\" is not MainActivity", context.getClass().getName()));
            }

            MainActivity mainActivity = (MainActivity)context;
            LinearLayout infoView = mainActivity.findViewById(R.id.infoView);

            boolean isPossible = infoView.getChildCount() > 0;
            isPossible &= !mainActivity.getNomCodeScannedBarcode().isEmpty();
            isPossible &= mainActivity.getCachedData().containsKey(mainActivity.getNomCodeScannedBarcode());

            return isPossible;
        }
    }

    private static class UpdateCacheAction implements Action {
        @Override
        public int getId() {
            return -1;
        }

        @Override
        public void call(Context context) {
            if (!(context instanceof MainActivity)) {
                throw new ClassCastException(String.format("Context parameter \"%s\" is not MainActivity", context.getClass().getName()));
            }

            CLEAR_CACHE.call(context);
            SAVE_TO_CACHE.call(context);
        }

        @Override
        public String getDescription(Context context) {
            return "Update cache";
        }

        @Override
        public boolean isPossibleCall(Context context) {
            try {
                if (!(context instanceof MainActivity)) {
                    throw new ClassCastException(String.format("Context parameter \"%s\" is not MainActivity", context.getClass().getName()));
                }

                MainActivity mainActivity = ((MainActivity) context);

                boolean isPossible = mainActivity.checkConnection();
                isPossible &= !mainActivity.getNomCodeScannedBarcode().isEmpty();
                isPossible &= mainActivity.getCachedData().containsKey(mainActivity.getNomCodeScannedBarcode());

                return isPossible;
            } catch (Exception e) {
                Logger.e(context, e.getClass().getName(), e.getMessage());
                return false;
            }
        }
    }

    private static class GetCachedNoms implements Action {
        @Override
        public int getId() {
            return 9;
        }

        @Override
        public void call(Context context) {
            if (!(context instanceof MainActivity)) {
                throw new ClassCastException(String.format("Context parameter \"%s\" is not MainActivity", context.getClass().getName()));
            }

            MainActivity mainActivity = ((MainActivity) context);
            LinearLayout foundedInfoView = mainActivity.findViewById(R.id.infoView);
//            foundedInfoView.removeAllViews();
            mainActivity.clearInfoTexView();
            mainActivity.getNomCodeScannedBarcode();

            for (File file : mainActivity.getFilesDir().listFiles()) {
                TextView infoTextView = new TextView(context);
                foundedInfoView.addView(infoTextView);
                Space spacer = new Space(context);
                spacer.setMinimumHeight(30);
                foundedInfoView.addView(spacer);

                appendToTextView(infoTextView, file.getName());
            }
        }

        @Override
        public String getDescription(Context context) {
            return "Get cached noms";
        }

        @Override
        public boolean isPossibleCall(Context context) {
            return true;
        }
    }
}
