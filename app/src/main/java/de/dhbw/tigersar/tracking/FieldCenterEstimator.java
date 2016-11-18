package de.dhbw.tigersar.tracking;

import android.opengl.Matrix;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lukas Schmierer on 18.11.16.
 * <p>
 * Estimate the center point of playing field.
 */
public class FieldCenterEstimator {

    private int width;
    private int height;

    private Map<MarkerPosition, Marker> markers;
    private Map<MarkerPosition, float[]> offsets;

    public FieldCenterEstimator(int width, int height) throws ARException {
        this.width = width;
        this.height = height;

        markers = new HashMap<>(6);
        markers.put(MarkerPosition.O, new SquareMarker("Data/o.patt", 200, 90));
        markers.put(MarkerPosition.OL,  new SquareMarker("Data/ol.patt", 200, 90));
        markers.put(MarkerPosition.OR,  new SquareMarker("Data/or.patt", 200));
        markers.put(MarkerPosition.U,  new SquareMarker("Data/u.patt", 200, 90));
        markers.put(MarkerPosition.UL,  new SquareMarker("Data/ul.patt", 200));
        markers.put(MarkerPosition.UR,  new SquareMarker("Data/ur.patt", 200));

        offsets = new HashMap<>(6);
    }

    public void setOffest(MarkerPosition markerPosition, float[] offset) {
        offsets.put(markerPosition, offset);
    }

    public boolean isVisible() {
        return markers.get(MarkerPosition.O).isVisible();
    }

    public float[] calculateCenterTransform() throws ARException {
        // TODO estimate center from all visible markers

        Marker markerO = markers.get(MarkerPosition.O);
        if (markerO.isVisible()) {
            float[] transform =  markerO.getTransformation();
            Matrix.translateM(transform, 0, 0, -height / 2, 0);
            if(offsets.containsKey(MarkerPosition.O)) {
                float[] offset = offsets.get(MarkerPosition.O);
                Matrix.translateM(transform, 0, -offset[0], -offset[1], 0);
            }
            return transform;
        }
        return null;
    }
}
