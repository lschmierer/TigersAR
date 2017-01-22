package de.dhbw.tigersar;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.camera.CameraPreferencesActivity;
import org.artoolkit.ar.base.rendering.ARRenderer;

public class MainActivity extends ARActivity {

    private static final float MAIN_LAYOUT_ASPECT_RATIO = 4 / 3f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = supplyFrameLayout();

        Point mainLayoutSize = calculateMainLayoutSize();
        ViewGroup.LayoutParams layoutParams = mainLayout.getLayoutParams();
        layoutParams.width = mainLayoutSize.x;
        layoutParams.height = mainLayoutSize.y;
        mainLayout.setLayoutParams(layoutParams);

    }

    @Override
    protected ARRenderer supplyRenderer() {
        return new TigersARRenderer();
    }

    @Override
    protected FrameLayout supplyFrameLayout() {
        return (FrameLayout) findViewById(R.id.aRFramleLayout);
    }

    public void startPreferencesActivity(View view) {
        startActivity(new Intent(this, CameraPreferencesActivity.class));
    }

    private Point calculateMainLayoutSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        int width = displaySize.x;
        int height = displaySize.y;

        if (height * MAIN_LAYOUT_ASPECT_RATIO < width) {
            return new Point(width, Math.round(width / MAIN_LAYOUT_ASPECT_RATIO));
        } else {
            return new Point(Math.round(MAIN_LAYOUT_ASPECT_RATIO * height), height);
        }
    }
}
