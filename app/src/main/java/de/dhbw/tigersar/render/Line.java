package de.dhbw.tigersar.render;

import android.opengl.GLES20;

import org.artoolkit.ar.base.rendering.RenderUtils;
import org.artoolkit.ar.base.rendering.gles20.BaseFragmentShader;
import org.artoolkit.ar.base.rendering.gles20.BaseShaderProgram;
import org.artoolkit.ar.base.rendering.gles20.BaseVertexShader;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by Lukas Schmierer on 14.11.16.
 */
public class Line implements Renderable {

    private float[] start = new float[3];
    private float[] end = new float[3];
    private float width;
    private float[] color = new float[]{1.0f, 0.0f, 0.0f, 1.0f};

    private LineShaderProgram mShaderProgram;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;

    public Line(float width) {
        setWidth(width);
        mShaderProgram = new LineShaderProgram((int) width);
    }

    public Line(float width, float[] start, float[] end) {
        this(width);
        setStart(start);
        setEnd(end);
    }

    public float[] getStart() {
        return start;
    }

    public void setStart(float[] start) {
        if (start.length == 2) {
            this.start[0] = start[0];
            this.start[1] = start[1];
        } else if (start.length == 3) {
            this.start = start;
        }
    }

    public float[] getEnd() {
        return end;
    }

    public void setEnd(float[] end) {
        if (end.length == 2) {
            this.end[0] = end[0];
            this.end[1] = end[1];
        } else if (end.length == 3) {
            this.end = end;
        }
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
        if (mShaderProgram != null)
            mShaderProgram.setLineWidth((int) width);
    }

    public float[] getColor() {
        return this.color;
    }

    public void setColor(float[] color) {
        this.color = color;
    }

    private void setArrays() {
        float[] vertices = new float[6];

        for (int i = 0; i < 3; i++) {
            vertices[i] = getStart()[i];
            vertices[i + 3] = getEnd()[i];
        }
        mVertexBuffer = RenderUtils.buildFloatBuffer(vertices);
        mColorBuffer = RenderUtils.buildFloatBuffer(this.color);

    }

    public void draw(float[] projectionMatrix, float[] modelViewMatrix) {
        mShaderProgram.setProjectionMatrix(projectionMatrix);
        mShaderProgram.setModelViewMatrix(modelViewMatrix);
        setArrays();
        mShaderProgram.render(mVertexBuffer, mColorBuffer, null);
    }

    private static class LineFragmentShader extends BaseFragmentShader {
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

    private static class LineShaderProgram extends BaseShaderProgram {

        private int lineWidth;

        LineShaderProgram(int lineWidth) {
            super(new BaseVertexShader(), new LineFragmentShader());
            this.lineWidth = lineWidth;
        }

        private void setLineWidth(int lineWidth) {
            this.lineWidth = lineWidth;
        }

        private int getColorHandle() {
            return GLES20.glGetUniformLocation(shaderProgramHandle, LineFragmentShader.colorVectorString);
        }

        @Override
        public void render(FloatBuffer vertexBuffer, FloatBuffer colorBuffer, ByteBuffer indexBuffer) {
            setupShaderUsage();

            GLES20.glVertexAttribPointer(getPositionHandle(), positionDataSize, GLES20.GL_FLOAT, false,
                    positionStrideBytes, vertexBuffer);
            GLES20.glEnableVertexAttribArray(getPositionHandle());

            GLES20.glUniform4fv(getColorHandle(), 1, colorBuffer);
            GLES20.glLineWidth(lineWidth);

            // render 2 vertices
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);
        }
    }
}
