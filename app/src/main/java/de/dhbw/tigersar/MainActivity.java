package de.dhbw.tigersar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.camera.CameraPreferencesActivity;
import org.artoolkit.ar.base.rendering.ARRenderer;

import java.io.IOException;
import java.net.InetAddress;

import de.dhbw.tigersar.net.TigersARMulticastClient;
import de.dhwb.tigersar.TigersARProtos;

public class MainActivity extends ARActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final float MAIN_LAYOUT_ASPECT_RATIO = 4 / 3f;
    private TigersARRenderer mARRenderer;
    private TigersARMulticastClient mARMulticastClient;
    private WifiManager.MulticastLock mMulticastLock;

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

        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            mMulticastLock = wifi.createMulticastLock("TigersARLock");
        }

        try {
            mARMulticastClient = new TigersARMulticastClient(InetAddress.getByName("225.225.125.225"), 25225);

            mARMulticastClient.setCallback(new TigersARMulticastClient.OnNewMessageCallback() {
                @Override
                public void onNewMessage(TigersARProtos.ARMessage message) {
                    Log.d(TAG, "new ARMessage received");
                    if(mARRenderer != null) {
                        mARRenderer.renderFromARMessage(message);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mMulticastLock != null) {
            mMulticastLock.acquire();

            Thread clientThread = new Thread(mARMulticastClient);
            clientThread.start();
        }
    }

    @Override
    protected ARRenderer supplyRenderer() {
        mARRenderer = new TigersARRenderer();
        return mARRenderer;
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

    @Override
    protected void onPause() {
        super.onPause();
        mARMulticastClient.stop();
        if (mMulticastLock != null) {
            mMulticastLock.release();
        }
    }
}
