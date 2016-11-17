package de.dhbw.tigersar.tracking;

import android.opengl.Matrix;

import org.artoolkit.ar.base.ARToolKit;

/**
 * Created by Lukas Schmierer on 17.11.16.
 * <p>
 * Class representing a square marker that can be loaded from file.
 */
public class SquareMarker {

    private int markerId = -1;
    private float rotDeg;

    public SquareMarker(String filePath, int markerSize, int rotDeg) throws ARException {
        markerId = ARToolKit.getInstance().addMarker("single;" + filePath + ";" + markerSize);
        if (markerId < 0) {
            throw new ARException("Error loading marker");
        }
        this.rotDeg = rotDeg;
    }

    public SquareMarker(String filePath, int markerSize) throws ARException {
        this(filePath, markerSize, 0);
    }

    public boolean isVisible() {
        return ARToolKit.getInstance().queryMarkerVisible(markerId);
    }

    public float[] getTransformation() throws ARException {
        if (!isVisible()) {
            throw new ARException("Marker not visible");
        }
        float[] transformation = ARToolKit.getInstance().queryMarkerTransformation(markerId);
        if (rotDeg != 0) {
            Matrix.rotateM(transformation, 0, 90f, 0f, 0f, 1f);
        }
        return transformation;
    }
}
