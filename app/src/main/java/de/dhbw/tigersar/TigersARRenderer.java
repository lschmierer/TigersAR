package de.dhbw.tigersar;

import android.opengl.GLES20;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.gles20.ARRendererGLES20;

import java.util.ArrayList;
import java.util.List;

import de.dhbw.tigersar.render.Circle;
import de.dhbw.tigersar.render.Field;
import de.dhbw.tigersar.render.Line;
import de.dhbw.tigersar.tracking.ARException;
import de.dhbw.tigersar.tracking.FieldCenterEstimator;
import de.dhbw.tigersar.tracking.MarkerPosition;
import de.dhwb.tigersar.TigersARProtos;

/**
 * A very simple Renderer that adds a marker and draws a cube on it.
 */
public class TigersARRenderer extends ARRendererGLES20 {

    TigersARProtos.ARMessage arMessage;
    private FieldCenterEstimator fieldCenterEstimator;
    private Field field;
    private List<Line> lines;
    private List<Circle> circles;

    /**
     * This method gets called from the framework to setup the ARScene.
     * So this is the best spot to configure you assets for your AR app.
     * For example register used markers in here.
     */
    @Override
    public boolean configureARScene() {
        lines = new ArrayList<>();
        circles = new ArrayList<>();
        try {
            fieldCenterEstimator = new FieldCenterEstimator();
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

        createGLObjects();

        try {
            if (fieldCenterEstimator.isVisible()) {
                if(field != null) {
                    field.draw(projectionMatrix, fieldCenterEstimator.calculateCenterTransform());
                }
                for (Line line : lines) {
                    line.draw(projectionMatrix, fieldCenterEstimator.calculateCenterTransform());
                }
                for (Circle circle : circles) {
                    circle.draw(projectionMatrix, fieldCenterEstimator.calculateCenterTransform());
                }
            }
        } catch (ARException e) {
            e.printStackTrace();
        }
    }

    private void createGLObjects() {
        if (arMessage == null)
            return;

        if (arMessage.hasField()) {
            TigersARProtos.Field arField = arMessage.getField();
            fieldCenterEstimator.setWidth(arField.getWidth());
            fieldCenterEstimator.setHeight(arField.getHeight());
            field = new Field(arField.getWidth(), arField.getHeight());
        }

        lines.clear();
        for (TigersARProtos.Line arLine : arMessage.getLinesList()) {
            lines.add(new Line(8,
                    new float[]{arLine.getStart().getX(), arLine.getStart().getY()},
                    new float[]{arLine.getEnd().getX(), arLine.getEnd().getY()}));
        }

        circles.clear();
        for (TigersARProtos.Circle arCircle : arMessage.getCirclesList()) {
            float[] position = new float[]{arCircle.getPosition().getX(), arCircle.getPosition().getY()};
            if (arCircle.hasFillColor()) {
                Circle circle = new Circle(position, arCircle.getRadius());
                circle.setColor(new float[]{
                        arCircle.getFillColor().getR(),
                        arCircle.getFillColor().getG(),
                        arCircle.getFillColor().getB()});
                circles.add(circle);
            }
            Circle circle = new Circle(position, arCircle.getRadius(), 8);
            circle.setColor(new float[]{
                    arCircle.getColor().getR(),
                    arCircle.getColor().getG(),
                    arCircle.getColor().getB()});
            circles.add(circle);
        }
    }

    public void renderFromARMessage(TigersARProtos.ARMessage arMessage) {
        this.arMessage = arMessage;
    }
}