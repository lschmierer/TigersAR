package de.dhbw.tigersar.render;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 17.11.16.
 * <p>
 * Render a playing field.
 */

public class Field implements Renderable {

    private List<Line> lines;
    private Circle centerCircle;

    public Field(int width, int height) {
        lines = new ArrayList<>(5);
        // oben
        lines.add(new Line(8, new float[]{-width / 2, height / 2, 0}, new float[]{width / 2, height / 2, 0}));
        // unten
        lines.add(new Line(8, new float[]{-width / 2, -height / 2, 0}, new float[]{width / 2, -height / 2, 0}));
        // mitte
        lines.add(new Line(8, new float[]{0, -height / 2, 0}, new float[]{0, height / 2, 0}));
        // links
        lines.add(new Line(8, new float[]{-width / 2, height / 2, 0}, new float[]{-width / 2, -height / 2, 0}));
        // rechts
        lines.add(new Line(8, new float[]{width / 2, height / 2, 0}, new float[]{width / 2, -height / 2, 0}));

        for (Line line : lines) {
            line.setColor(new float[]{1f, 1f, 1f, 1f});
        }

        centerCircle = new Circle(new float[]{0, 0, 0}, 25, true);
        centerCircle.setColor(new float[]{1f, 1f, 1f, 1f});
    }

    @Override
    public void draw(float[] projectionMatrix, float[] modelViewMatrix) {
        for (Line line : lines) {
            line.draw(projectionMatrix, modelViewMatrix);
        }
        centerCircle.draw(projectionMatrix, modelViewMatrix);
    }
}
