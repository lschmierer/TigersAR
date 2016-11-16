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

    private int markerO = -1;
    private int markerOL = -1;
    private int markerOR = -1;
    private int markerU = -1;
    private int markerUL = -1;
    private int markerUR = -1;
    private Line line;

    /**
     * This method gets called from the framework to setup the ARScene.
     * So this is the best spot to configure you assets for your AR app.
     * For example register used markers in here.
     */
    @Override
    public boolean configureARScene() {
        markerO = ARToolKit.getInstance().addMarker("single;Data/o.patt;80");
        if (markerO < 0) return false;

        markerOL = ARToolKit.getInstance().addMarker("single;Data/ol.patt;80");
        if (markerOL < 0) return false;

        markerOR = ARToolKit.getInstance().addMarker("single;Data/or.patt;80");
        if (markerOR < 0) return false;

        markerU = ARToolKit.getInstance().addMarker("single;Data/u.patt;80");
        if (markerU < 0) return false;

        markerUL = ARToolKit.getInstance().addMarker("single;Data/ul.patt;80");
        if (markerUL < 0) return false;

        markerUR = ARToolKit.getInstance().addMarker("single;Data/ur.patt;80");
        if (markerUR < 0) return false;

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
        if (ARToolKit.getInstance().queryMarkerVisible(markerO)) {
            line.draw(projectionMatrix, ARToolKit.getInstance().queryMarkerTransformation(markerO));
        }

        if (ARToolKit.getInstance().queryMarkerVisible(markerOL)) {
            line.draw(projectionMatrix, ARToolKit.getInstance().queryMarkerTransformation(markerOL));
        }

        if (ARToolKit.getInstance().queryMarkerVisible(markerOR)) {
            line.draw(projectionMatrix, ARToolKit.getInstance().queryMarkerTransformation(markerOR));
        }

        if (ARToolKit.getInstance().queryMarkerVisible(markerU)) {
            line.draw(projectionMatrix, ARToolKit.getInstance().queryMarkerTransformation(markerU));
        }

        if (ARToolKit.getInstance().queryMarkerVisible(markerUL)) {
            line.draw(projectionMatrix, ARToolKit.getInstance().queryMarkerTransformation(markerUL));
        }

        if (ARToolKit.getInstance().queryMarkerVisible(markerUR)) {
            line.draw(projectionMatrix, ARToolKit.getInstance().queryMarkerTransformation(markerUR));
        }
    }
}