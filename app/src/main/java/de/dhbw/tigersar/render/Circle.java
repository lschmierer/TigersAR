package de.dhbw.tigersar.render;

import android.opengl.GLES20;

import org.artoolkit.ar.base.rendering.RenderUtils;
import org.artoolkit.ar.base.rendering.gles20.BaseFragmentShader;
import org.artoolkit.ar.base.rendering.gles20.BaseShaderProgram;
import org.artoolkit.ar.base.rendering.gles20.BaseVertexShader;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by Lukas Schmierer on 01.03.17.
 */
public class Circle implements Renderable {

    private static final int VERTEX_COUNT = 60;

    private float[] position = new float[3];
    private float radius;
    private int lineWidth;
    private float[] color = new float[]{1f, 0, 0, 1f};
    private boolean filled;

    private static CircleShaderProgram mShaderProgram;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;

    public Circle(float[] position, float radius, int lineWidth, boolean filled) {
        if(mShaderProgram == null)
            mShaderProgram = new CircleShaderProgram(lineWidth);
        setPosition(position);
        setRadius(radius);
        setLineWidth(lineWidth);
        setFilled(filled);
    }

    public Circle(float[] position, float radius, int lineWidth) {
        this(position, radius, lineWidth, false);
    }

    public Circle(float[] position, float radius) {
        this(position, radius, 0, true);
    }

    public float[] getPosition() {
        return position;
    }

    public void setPosition(float[] position) {
        if (position.length == 2) {
            this.position[0] = position[0];
            this.position[1] = position[1];
        } else {
            this.position = position;
        }
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public boolean getFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public float[] getColor() {
        return this.color;
    }

    public void setColor(float[] color) {
        this.color = color;
    }

    private void setArrays() {
        float[] vertices = new float[VERTEX_COUNT * 3];

        int idx = 0;

        for(int i = 0; i < VERTEX_COUNT; i++) {
            float rad = (float) ((i / (float) VERTEX_COUNT) * 2 * Math.PI);

            vertices[idx++] = (float) (position[0] + radius * Math.cos(rad));
            vertices[idx++] = (float) (position[1] + radius * Math.sin(rad));
            vertices[idx++] = position[2];
        }

        mVertexBuffer = RenderUtils.buildFloatBuffer(vertices);
        mColorBuffer = RenderUtils.buildFloatBuffer(this.color);

    }

    public void draw(float[] projectionMatrix, float[] modelViewMatrix) {
        mShaderProgram.setLineWidth(lineWidth);
        mShaderProgram.setFilled(filled);

        mShaderProgram.setProjectionMatrix(projectionMatrix);
        mShaderProgram.setModelViewMatrix(modelViewMatrix);
        setArrays();
        mShaderProgram.render(mVertexBuffer, mColorBuffer, null);
    }

    private static class CircleFragmentShader extends BaseFragmentShader {
        private static String colorVectorString = "a_Color";

        private String fragmentShader =
                "precision lowp float;" +
                        "uniform vec4 " + colorVectorString + ";\n" +
                        "void main() {\n" +
                        "   gl_FragColor = " + colorVectorString + ";\n" +
                        "}\n";

        @Override
        public int configureShader() {
            setShaderSource(fragmentShader);
            return super.configureShader();
        }
    }

    private static class CircleShaderProgram extends BaseShaderProgram {

        private int lineWidth;
        private boolean filled;

        CircleShaderProgram(int lineWidth) {
            super(new BaseVertexShader(), new CircleFragmentShader());
            this.lineWidth = lineWidth;
        }

        private void setLineWidth(int lineWidth) {
            this.lineWidth = lineWidth;
        }

        public void setFilled(boolean filled) {
            this.filled = filled;
        }

        private int getColorHandle() {
            return GLES20.glGetUniformLocation(shaderProgramHandle, CircleFragmentShader.colorVectorString);
        }

        @Override
        public void render(FloatBuffer vertexBuffer, FloatBuffer colorBuffer, ByteBuffer indexBuffer) {
            setupShaderUsage();

            GLES20.glVertexAttribPointer(getPositionHandle(), positionDataSize, GLES20.GL_FLOAT, false,
                    positionStrideBytes, vertexBuffer);
            GLES20.glEnableVertexAttribArray(getPositionHandle());

            GLES20.glUniform4fv(getColorHandle(), 1, colorBuffer);
            GLES20.glLineWidth(lineWidth);

            if(filled) {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, VERTEX_COUNT);
            } else {
                GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, VERTEX_COUNT);
            }
        }
    }
}
