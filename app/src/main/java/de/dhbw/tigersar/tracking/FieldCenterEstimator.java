package de.dhbw.tigersar.tracking;

import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        markers.put(MarkerPosition.OL, new SquareMarker("Data/ol.patt", 200, 90));
        markers.put(MarkerPosition.OR, new SquareMarker("Data/or.patt", 200));
        markers.put(MarkerPosition.U, new SquareMarker("Data/u.patt", 200, 90));
        markers.put(MarkerPosition.UL, new SquareMarker("Data/ul.patt", 200));
        markers.put(MarkerPosition.UR, new SquareMarker("Data/ur.patt", 200));

        offsets = new HashMap<>(6);
    }

    public void setOffest(MarkerPosition markerPosition, float[] offset) {
        offsets.put(markerPosition, offset);
    }

    public boolean isVisible() {
        for (Marker marker : markers.values()) {
            if (marker.isVisible()) {
                return true;
            }
        }
        return false;
    }

    public float[] calculateCenterTransform() throws ARException {
        List<float[]> markerCenterTransformations = calculateAllMarkersCenterTransforms();
        if (!markerCenterTransformations.isEmpty()) {
            float[] finalTransform = markerCenterTransformations.get(0);

            int count = markerCenterTransformations.size();

            for (int i = 1; i < count; i++) {
                float[] transform = markerCenterTransformations.get(i);
                finalTransform[12] += transform[12];
                finalTransform[13] += transform[13];
                finalTransform[14] += transform[14];
            }

            finalTransform[12] /= count;
            finalTransform[13] /= count;
            finalTransform[14] /= count;

            return finalTransform;
        }
        return null;
    }

    private List<float[]> calculateAllMarkersCenterTransforms() throws ARException {
        List<float[]> markerCenterTransforms = new ArrayList<>(markers.size());

        for (Map.Entry<MarkerPosition, Marker> markerEntry : markers.entrySet()) {
            MarkerPosition markerPosition = markerEntry.getKey();
            Marker marker = markerEntry.getValue();
            if (marker.isVisible()) {
                markerCenterTransforms.add(calculateMarkerCenterTransform(markerPosition, marker));
            }
        }
        return markerCenterTransforms;
    }

    private float[] calculateMarkerCenterTransform(MarkerPosition markerPosition, Marker marker) throws ARException {
        float[] transform = marker.getTransformation();

        float[] offset;
        if (offsets.containsKey(markerPosition)) {
            offset = offsets.get(markerPosition).clone();
        } else {
            offset = new float[3];
        }

        switch (markerPosition) {
            case OL:
            case UL:
                offset[0] -= width / 2;
                break;
            case OR:
            case UR:
                offset[0] += width / 2;
        }

        switch (markerPosition) {
            case O:
            case OL:
            case OR:
                offset[1] += height / 2;
                break;
            default:
                offset[1] -= height / 2;
        }

        Matrix.translateM(transform, 0, -offset[0], -offset[1], offset.length >= 3 ? -offset[2] : 0);
        return transform;
    }
}
