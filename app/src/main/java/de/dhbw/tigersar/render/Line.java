package de.dhbw.tigersar.render;

import android.opengl.GLES20;
import android.util.Log;

import org.artoolkit.ar.base.rendering.gles20.BaseFragmentShader;
import org.artoolkit.ar.base.rendering.gles20.BaseShaderProgram;
import org.artoolkit.ar.base.rendering.gles20.BaseVertexShader;
import org.artoolkit.ar.base.rendering.gles20.LineGLES20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by lukas on 14.11.16.
 */
public class Line extends LineGLES20 {

    private LineShaderProgram shaderProgram;

    public Line(float width) {
        super(width);
        shaderProgram = new LineShaderProgram((int) width);
        setShaderProgram(shaderProgram);
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        if(shaderProgram != null)
            shaderProgram.setLineWidth((int) width);
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
