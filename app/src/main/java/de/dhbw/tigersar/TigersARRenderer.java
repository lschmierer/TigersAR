package de.dhbw.tigersar;

import android.opengl.GLES20;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.gles20.ARRendererGLES20;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import de.dhbw.tigersar.render.Line;

/**
 * A very simple Renderer that adds a marker and draws a cube on it.
 */
public class TigersARRenderer extends ARRendererGLES20 {

    private int markerID = -1;
    private Line line;

    /**
     * This method gets called from the framework to setup the ARScene.
     * So this is the best spot to configure you assets for your AR app.
     * For example register used markers in here.
     */
    @Override
    public boolean configureARScene() {
        markerID = ARToolKit.getInstance().addMarker("single;Data/hiro.patt;80");
        if (markerID < 0) return false;

        return true;
    }

    //Shader calls should be within a GL thread that is onSurfaceChanged(), onSurfaceCreated() or onDrawFrame()
    //As the cube instantiates the shader during setShaderProgram call we need to create the cube here.
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);

        line = new Line(10);
        line.setStart(new float[]{0, 0, 0});
        line.setEnd(new float[]{0, 0, 100});
        line.setColor(new float[]{0.3f, 0.3f, 0.8f, 1f});
    }

    /**
     * Override the render function from {@link ARRendererGLES20}.
     */
    @Override
    public void draw() {
        super.draw();

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glFrontFace(GLES20.GL_CW);

        float[] projectionMatrix = ARToolKit.getInstance().getProjectionMatrix();

        // If the marker is visible, apply its transformation, and render a cube
        if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {
            line.draw(projectionMatrix, ARToolKit.getInstance().queryMarkerTransformation(markerID));
        }
    }
}