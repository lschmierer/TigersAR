package de.dhbw.tigersar;

import android.opengl.GLES20;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.gles20.ARRendererGLES20;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import de.dhbw.tigersar.render.Field;
import de.dhbw.tigersar.tracking.ARException;
import de.dhbw.tigersar.tracking.FieldCenterEstimator;
import de.dhbw.tigersar.tracking.MarkerPosition;

/**
 * A very simple Renderer that adds a marker and draws a cube on it.
 */
public class TigersARRenderer extends ARRendererGLES20 {

    private Field field;
    private FieldCenterEstimator fieldCenterEstimator;

    /**
     * This method gets called from the framework to setup the ARScene.
     * So this is the best spot to configure you assets for your AR app.
     * For example register used markers in here.
     */
    @Override
    public boolean configureARScene() {
        try {
            fieldCenterEstimator = new FieldCenterEstimator(1200, 600);
            fieldCenterEstimator.setOffest(MarkerPosition.O, new float[]{0, 150});
            fieldCenterEstimator.setOffest(MarkerPosition.OL, new float[]{-150, 150});
            fieldCenterEstimator.setOffest(MarkerPosition.OR, new float[]{150, 150});
            fieldCenterEstimator.setOffest(MarkerPosition.U, new float[]{0, -150});
            fieldCenterEstimator.setOffest(MarkerPosition.UL, new float[]{-150, -150});
            fieldCenterEstimator.setOffest(MarkerPosition.UR, new float[]{150, -150});
        } catch (ARException e) {
            return false;
        }
        return true;
    }

    //Shader calls should be within a GL thread that is onSurfaceChanged(), onSurfaceCreated() or onDrawFrame()
    //As the cube instantiates the shader during setShaderProgram call we need to create the cube here.
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);

        field = new Field(1200, 600);
    }

    /**
     * Override the render function from {@link ARRendererGLES20}.
     */
    @Override
    public void draw() {
        super.draw();

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glFrontFace(GLES20.GL_CCW);

        float[] projectionMatrix = ARToolKit.getInstance().getProjectionMatrix();

        try {
            if (fieldCenterEstimator.isVisible()) {
                field.draw(projectionMatrix, fieldCenterEstimator.calculateCenterTransform());
            }
        } catch (ARException e) {
            e.printStackTrace();
        }
    }
}