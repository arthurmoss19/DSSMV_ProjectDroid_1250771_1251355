package util;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public final class UiUtils {

    private UiUtils() {}

    public static void toast(Context context, String message) {
        if (context == null || message == null) {
            return;
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void snack(View root, String message, String actionLabel, Runnable action) {
        if (root == null || message == null) {
            return;
        }
        Snackbar bar = Snackbar.make(root, message, Snackbar.LENGTH_LONG);
        if (actionLabel != null && action != null) {
            bar.setAction(actionLabel, v -> action.run());
        }
        bar.show();
    }

    public static void confirm(Context context, String title, String message,
                               String positiveLabel, DialogInterface.OnClickListener onConfirm) {
        new MaterialAlertDialogBuilder(context).setTitle(title).setMessage(message).setNegativeButton(android.R.string.cancel, null).setPositiveButton(positiveLabel, onConfirm).show();
    }

    public static String errorMessage(Throwable throwable) {
        if (throwable == null) {
            return "Ocorreu um erro inesperado.";
        }
        String message = throwable.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = throwable.getClass().getSimpleName();
        }
        return message;
    }
}
