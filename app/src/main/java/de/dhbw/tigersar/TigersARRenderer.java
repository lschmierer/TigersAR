package de.dhbw.tigersar;

import android.opengl.GLES20;
import android.opengl.Matrix;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.gles20.ARRendererGLES20;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import de.dhbw.tigersar.render.Line;
import de.dhbw.tigersar.tracking.ARException;
import de.dhbw.tigersar.tracking.SquareMarker;

/**
 * A very simple Renderer that adds a marker and draws a cube on it.
 */
public class TigersARRenderer extends ARRendererGLES20 {

    private SquareMarker markerO;
    private SquareMarker markerOL;
    private SquareMarker markerOR;
    private SquareMarker markerU;
    private SquareMarker markerUL;
    private SquareMarker markerUR;
    private Line line;

    /**
     * This method gets called from the framework to setup the ARScene.
     * So this is the best spot to configure you assets for your AR app.
     * For example register used markers in here.
     */
    @Override
    public boolean configureARScene() {
        try {
            markerO = new SquareMarker("Data/o.patt", 200, 90);
            markerOL = new SquareMarker("Data/ol.patt", 200, 90);
            markerOR = new SquareMarker("Data/or.patt", 200);
            markerU = new SquareMarker("Data/u.patt", 200, 90);
            markerUL = new SquareMarker("Data/ul.patt", 200);
            markerUR = new SquareMarker("Data/ur.patt", 200);
        } catch (ARException e) {
            e.printStackTrace();
        }

        return true;
    }

    //Shader calls should be within a GL thread that is onSurfaceChanged(), onSurfaceCreated() or onDrawFrame()
    //As the cube instantiates the shader during setShaderProgram call we need to create the cube here.
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);

        line = new Line(10);
        line.setStart(new float[]{0, 0, 0});
        line.setEnd(new float[]{1000, 0, 0});
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
        try {
            if (markerO.isVisible()) {
                line.draw(projectionMatrix, markerO.getTransformation());
            }
            if (markerOL.isVisible()) {
                line.draw(projectionMatrix, markerOL.getTransformation());
            }
            if (markerOR.isVisible()) {
                line.draw(projectionMatrix, markerOR.getTransformation());
            }
            if (markerU.isVisible()) {
                line.draw(projectionMatrix, markerU.getTransformation());
            }
            if (markerUL.isVisible()) {
                line.draw(projectionMatrix, markerUL.getTransformation());
            }
            if (markerUR.isVisible()) {
                line.draw(projectionMatrix, markerUR.getTransformation());
            }
        } catch (ARException e) {
            e.printStackTrace();
        }
    }
}