package de.dhbw.tigersar.tracking;

/**
 * Created by lukas on 18.11.16.
 * <p>
 * Interface for all markers.
 */
interface Marker {

    boolean isVisible();

    float[] getTransformation() throws ARException;
}
