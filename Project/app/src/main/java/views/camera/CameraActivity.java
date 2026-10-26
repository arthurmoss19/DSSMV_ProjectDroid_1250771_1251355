package views.camera;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.triptrail.R;

import java.io.File;

import util.Extras;
import util.ImageUtils;
import util.PermissionHelper;
import util.UiUtils;

public class CameraActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ImageCapture imageCapture;
    private boolean capturing;

    public static Intent intent(Context context) {
        return new Intent(context, CameraActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildContentView());

        if (!PermissionHelper.hasCameraPermission(this)) {
            PermissionHelper.requestCamera(this);
        } else {
            startCamera();
        }
    }

    private View buildContentView() {
        int margin = getResources().getDimensionPixelSize(R.dimen.margin);
        int marginSmall = getResources().getDimensionPixelSize(R.dimen.margin_small);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        previewView = new PreviewView(this);
        previewView.setContentDescription(getString(R.string.cd_viewfinder));
        previewView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        root.addView(previewView);

        TextView hint = new TextView(this);
        hint.setText(R.string.camera_capture);
        hint.setTextColor(Color.WHITE);
        hint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        hint.setBackgroundColor(0x99000000);
        hint.setPadding(marginSmall, marginSmall, marginSmall, marginSmall);
        FrameLayout.LayoutParams hintParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        hintParams.topMargin = margin;
        root.addView(hint, hintParams);

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER);
        bar.setPadding(margin, margin, margin, margin);
        root.addView(bar, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM));

        ImageButton close = new ImageButton(this);
        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(
                dp(56), dp(56));
        closeParams.setMarginEnd(margin);
        close.setLayoutParams(closeParams);
        close.setImageResource(R.drawable.icon_close);
        close.setContentDescription(getString(R.string.action_close));
        close.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        TypedValue borderless = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless,
                borderless, true);
        close.setBackgroundResource(borderless.resourceId);
        close.setOnClickListener(view -> finish());
        bar.addView(close);

        Button capture = new Button(this);
        capture.setText(R.string.camera_capture);
        capture.setOnClickListener(view -> capture());
        bar.addView(capture);

        return root;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(this);
        future.addListener(() -> {
            try {
                ProcessCameraProvider provider = future.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                provider.unbindAll();
                provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA,
                        preview, imageCapture);
            } catch (Exception e) {
                UiUtils.toast(this, "Não foi possível abrir a câmara.");
                finish();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void capture() {
        if (imageCapture == null || capturing) {
            return;
        }
        capturing = true;

        File raw = ImageUtils.newPhotoFile(this);
        ImageCapture.OutputFileOptions options =
                new ImageCapture.OutputFileOptions.Builder(raw).build();

        imageCapture.takePicture(options, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        File compressed = ImageUtils.compress(raw,
                                ImageUtils.MAX_DIMENSION, ImageUtils.JPEG_QUALITY);
                        Intent data = new Intent();
                        data.putExtra(Extras.PHOTO_FILE, compressed.getAbsolutePath());
                        setResult(RESULT_OK, data);
                        finish();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        capturing = false;
                        UiUtils.toast(CameraActivity.this, "Não foi possível tirar a fotografia.");
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.REQUEST_CAMERA) {
            if (PermissionHelper.hasCameraPermission(this)) {
                startCamera();
            } else {
                UiUtils.toast(this, "Permissão de câmara recusada.");
                finish();
            }
        }
    }
}
