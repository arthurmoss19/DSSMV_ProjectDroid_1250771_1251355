package util;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public final class UiUtils {

    private UiUtils() {}
    
    public static void applyTopBarInsets(View bar) {
        if (bar == null) {
            return;
        }
        final int left = bar.getPaddingLeft();
        final int top = bar.getPaddingTop();
        final int right = bar.getPaddingRight();
        final int bottom = bar.getPaddingBottom();
        final int extra = Math.round(8 * bar.getResources().getDisplayMetrics().density);
        ViewCompat.setOnApplyWindowInsetsListener(bar, (view, windowInsets) -> {
            Insets statusBar = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            view.setPadding(left, top + statusBar.top + extra, right, bottom);
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(bar);
    }

    public static void toast(Context context, String message) {
        if (context == null || message == null) {
            return;
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
