package de.dhbw.tigersar.render;

/**
 * Created by lukas on 17.11.16.
 */

public interface Renderable {

    void draw(float[] projectionMatrix, float[] modelViewMatrix);
}
