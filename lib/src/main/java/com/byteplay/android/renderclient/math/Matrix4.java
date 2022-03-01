package com.byteplay.android.renderclient.math;

import android.opengl.Matrix;

import java.util.Arrays;
import java.util.Stack;

public class Matrix4 implements Cloneable {
    private static final float[] MULTIPLY_MM_TEMP = new float[16];
    private static final float[] POINT_TEMP = new float[4];
    private final Stack<float[]> matrixStack = new Stack<>();
    private float[] val = new float[16];
    private final float[] mTemp = new float[16];

    public Matrix4(float[] matrix) {
        set(matrix);
    }


    public Matrix4(Matrix4 matrix) {
        set(matrix.get());
        matrixStack.addAll(matrix.matrixStack);
    }


    public Matrix4() {
        setIdentity();
    }


    public Matrix4 save() {
        matrixStack.push(Arrays.copyOf(val, 16));
        return this;
    }

    public Matrix4 restore() {
        if (matrixStack.empty())
            return this;
        val = matrixStack.pop();
        return this;
    }

    public Matrix4 lookAt(Vector3 eye, Vector3 center, Vector3 up) {
        return lookAt(eye.getX(), eye.getY(), eye.getZ(), center.getX(), center.getY(), center.getZ(), up.getX(), up.getY(), up.getZ());
    }

    public Matrix4 lookAt(float eyeX, float eyeY, float eyeZ,
                          float centerX, float centerY, float centerZ, float upX, float upY,
                          float upZ) {
        Matrix.setLookAtM(mTemp, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
        postMul(mTemp);
        return this;
    }

    public Matrix4 frustum(float left, float right, float bottom, float top, float near, float far) {
        Matrix.frustumM(mTemp, 0, left, right, bottom, top, near, far);
        postMul(mTemp);
        return this;
    }

    public Matrix4 perspective(float fovy, float aspect, float zNear, float zFar) {
        Matrix.perspectiveM(mTemp, 0, fovy, aspect, zNear, zFar);
        postMul(mTemp);
        return this;
    }

    public Matrix4 ortho(float left, float right, float bottom, float top, float near, float far) {
        Matrix.orthoM(mTemp, 0, left, right, bottom, top, near, far);
        postMul(mTemp);
        return this;

    }

    public Matrix4 rotate(float angle, float x, float y, float z) {
        while (angle >= 360.0f) {
            angle -= 360.0f;
        }
        while (angle <= -360.0f) {
            angle += 360.0f;
        }
        Matrix.setRotateM(mTemp, 0, angle, x, y, z);
        postMul(mTemp);
        return this;
    }

    public Matrix4 rotate(float angle, Vector3 rotate) {
        return rotate(angle, rotate.getX(), rotate.getY(), rotate.getZ());
    }

    public Matrix4 translate(float x, float y, float z) {
        Matrix.setIdentityM(mTemp, 0);
        Matrix.translateM(mTemp, 0, x, y, z);
        postMul(mTemp);
        return this;
    }

    public Matrix4 translate(Vector3 translate) {
        return translate(translate.getX(), translate.getY(), translate.getZ());
    }

    public Matrix4 translateX(float x) {
        translate(x, 0, 0);
        return this;
    }

    public Matrix4 translateY(float y) {
        translate(0, y, 0);
        return this;
    }

    public Matrix4 translateZ(float z) {
        scale(0, 0, z);
        return this;
    }

    public Matrix4 scale(Vector3 scale) {
        return scale(scale.getX(), scale.getY(), scale.getZ());
    }

    public Matrix4 scale(float x, float y, float z) {
        Matrix.setIdentityM(mTemp, 0);
        Matrix.scaleM(mTemp, 0, x, y, z);
        postMul(mTemp);
        return this;
    }

    public Matrix4 flipX() {
        scale(-1, 1, 1);
        return this;
    }


    public Matrix4 flipY() {
        scale(1, -1, 1);
        return this;
    }

    public Matrix4 flipZ() {
        scale(1, 1, -1);
        return this;
    }


    public Matrix4 setIdentity() {
        Matrix.setIdentityM(val, 0);
        return this;
    }

    public Matrix4 preMul(Matrix4 matrix4) {
        multiplyMM(this.val, val, matrix4.get());
        return this;
    }

    public Matrix4 preMul(float[] matrix) {
        multiplyMM(this.val, this.val, matrix);
        return this;
    }

    public Matrix4 postMul(Matrix4 matrix) {
        multiplyMM(this.val, matrix.get(), this.val);
        return this;
    }


    public Matrix4 postMul(float[] matrix) {
        multiplyMM(this.val, matrix, this.val);
        return this;
    }

    public float[] get() {
        return val;
    }

    public Matrix4 set(float[] matrix) {
        System.arraycopy(matrix, 0, val, 0, val.length);
        return this;
    }

    public Matrix4 set(Matrix4 matrix4) {
        System.arraycopy(matrix4.get(), 0, val, 0, val.length);
        return this;
    }

    public void getInvertMatrix(Matrix4 matrix4) {
        boolean success = Matrix.invertM(matrix4.get(), 0, val, 0);
        if (!success) {
            matrix4.setIdentity();
        }
    }

    public void mapPoints(float[] point) {
        multiplyMV(point, val, point);
    }

    public void mapPoints(float[] resultPoint, float[] point) {
        multiplyMV(resultPoint, val, point);
    }

    @Override
    public Matrix4 clone() {
        Matrix4 matrix4 = new Matrix4(val);
        return matrix4;
    }

    static void multiplyMM(float[] result, float[] left, float[] right) {
        boolean useTemp = result == left || result == right;
        if (!useTemp) {
            Matrix.multiplyMM(result, 0, left, 0, right, 0);
            return;
        }
        synchronized (MULTIPLY_MM_TEMP) {
            Matrix.multiplyMM(MULTIPLY_MM_TEMP, 0, left, 0, right, 0);
            System.arraycopy(MULTIPLY_MM_TEMP, 0, result, 0, 16);
        }
    }

    static void multiplyMV(float[] resultPoint, float[] matrix, float[] point) {
        boolean useTemp = resultPoint == point;
        if (!useTemp) {
            Matrix.multiplyMV(resultPoint, 0, matrix, 0, point, 0);
            return;
        }
        synchronized (POINT_TEMP) {
            Matrix.multiplyMV(POINT_TEMP, 0, matrix, 0, point, 0);
            System.arraycopy(POINT_TEMP, 0, resultPoint, 0, 4);
        }
    }

}
