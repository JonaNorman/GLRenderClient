package com.byteplay.android.renderclient;

import android.opengl.GLES20;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

class GL20Command extends GL20 {
    private final int[] tempInts = new int[1];
    private final byte[] tempBuffer = new byte[512];
    private final List<GL20Monitor> monitorList = new ArrayList<>();

    public GL20Command(GLRenderClient GLRenderClient) {
        super(GLRenderClient);
    }


    @Override
    public void glActiveTexture(int texture) {
        GLES20.glActiveTexture(texture);
        for (GL20Monitor monitor : monitorList) {
            monitor.glActiveTexture(texture);
        }
    }

    @Override
    public void glAttachShader(int program, int shader) {
        GLES20.glAttachShader(program, shader);
        for (GL20Monitor monitor : monitorList) {
            monitor.glAttachShader(program, shader);
        }
    }

    @Override
    public void glBindAttribLocation(int program, int index, String name) {
        GLES20.glBindAttribLocation(program, index, name);
        for (GL20Monitor monitor : monitorList) {
            monitor.glBindAttribLocation(program, index, name);
        }
    }

    @Override
    public void glBindBuffer(int target, int buffer) {
        GLES20.glBindBuffer(target, buffer);
        for (GL20Monitor monitor : monitorList) {
            monitor.glBindBuffer(target, buffer);
        }
    }

    @Override
    public void glBindFramebuffer(int target, int framebuffer) {
        GLES20.glBindFramebuffer(target, framebuffer);
        for (GL20Monitor monitor : monitorList) {
            monitor.glBindFramebuffer(target, framebuffer);
        }
    }

    @Override
    public void glBindRenderbuffer(int target, int renderbuffer) {
        GLES20.glBindRenderbuffer(target, renderbuffer);
        for (GL20Monitor monitor : monitorList) {
            monitor.glBindRenderbuffer(target, renderbuffer);
        }
    }

    @Override
    public void glBindTexture(int target, int texture) {
        GLES20.glBindTexture(target, texture);
        for (GL20Monitor monitor : monitorList) {
            monitor.glBindTexture(target, texture);
        }
    }

    @Override
    public void glBlendColor(float red, float green, float blue, float alpha) {
        GLES20.glBlendColor(red, green, blue, alpha);
        for (GL20Monitor monitor : monitorList) {
            monitor.glBlendColor(red, green, blue, alpha);
        }
    }

    @Override
    public void glBlendEquation(int mode) {
        GLES20.glBlendEquation(mode);
        for (GL20Monitor monitor : monitorList) {
            monitor.glBlendEquation(mode);
        }
    }

    @Override
    public void glBlendEquationSeparate(int modeRGB, int modeAlpha) {
        GLES20.glBlendEquationSeparate(modeRGB, modeAlpha);
        for (GL20Monitor monitor : monitorList) {
            monitor.glBlendEquationSeparate(modeRGB, modeAlpha);
        }
    }

    @Override
    public void glBlendFunc(int sfactor, int dfactor) {
        GLES20.glBlendFunc(sfactor, dfactor);
        for (GL20Monitor monitor : monitorList) {
            monitor.glBlendFunc(sfactor, dfactor);
        }
    }

    @Override
    public void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        GLES20.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
        for (GL20Monitor monitor : monitorList) {
            monitor.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
        }
    }

    @Override
    public void glBufferData(int target, int size, Buffer data, int usage) {
        GLES20.glBufferData(target, size, data, usage);
        for (GL20Monitor monitor : monitorList) {
            monitor.glBufferData(target, size, data, usage);
        }
    }

    @Override
    public void glBufferSubData(int target, int offset, int size, Buffer data) {
        GLES20.glBufferSubData(target, offset, size, data);
        for (GL20Monitor monitor : monitorList) {
            monitor.glBufferSubData(target, offset, size, data);
        }
    }

    @Override
    public int glCheckFramebufferStatus(int target) {
        int status = GLES20.glCheckFramebufferStatus(target);
        for (GL20Monitor monitor : monitorList) {
            monitor.glCheckFramebufferStatus(target, status);
        }
        return status;
    }

    @Override
    public void glClear(int mask) {
        GLES20.glClear(mask);
        for (GL20Monitor monitor : monitorList) {
            monitor.glClear(mask);
        }
    }

    @Override
    public void glClearColor(float red, float green, float blue, float alpha) {
        GLES20.glClearColor(red, green, blue, alpha);
        for (GL20Monitor monitor : monitorList) {
            monitor.glClearColor(red, green, blue, alpha);
        }
    }

    @Override
    public void glClearDepthf(float depth) {
        GLES20.glClearDepthf(depth);
        for (GL20Monitor monitor : monitorList) {
            monitor.glClearDepthf(depth);
        }
    }

    @Override
    public void glClearStencil(int s) {
        GLES20.glClearStencil(s);
        for (GL20Monitor monitor : monitorList) {
            monitor.glClearStencil(s);
        }
    }

    @Override
    public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        GLES20.glColorMask(red, green, blue, alpha);
        for (GL20Monitor monitor : monitorList) {
            monitor.glColorMask(red, green, blue, alpha);
        }
    }

    @Override
    public void glCompileShader(int shader) {
        GLES20.glCompileShader(shader);
        for (GL20Monitor monitor : monitorList) {
            monitor.glCompileShader(shader);
        }
    }

    @Override
    public void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border,
                                       int imageSize, Buffer data) {
        GLES20.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
        for (GL20Monitor monitor : monitorList) {
            monitor.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
        }
    }

    @Override
    public void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format,
                                          int imageSize, Buffer data) {
        GLES20.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
        for (GL20Monitor monitor : monitorList) {
            monitor.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
        }
    }

    @Override
    public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border) {
        GLES20.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
        for (GL20Monitor monitor : monitorList) {
            monitor.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
        }
    }

    @Override
    public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
        GLES20.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
        for (GL20Monitor monitor : monitorList) {
            monitor.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
        }
    }

    @Override
    public int glCreateProgram() {
        int program = GLES20.glCreateProgram();
        for (GL20Monitor monitor : monitorList) {
            monitor.glCreateProgram(program);
        }
        return program;
    }

    @Override
    public int glCreateShader(int type) {
        int shader = GLES20.glCreateShader(type);
        for (GL20Monitor monitor : monitorList) {
            monitor.glCreateShader(type, shader);
        }
        return shader;
    }

    @Override
    public void glCullFace(int mode) {
        GLES20.glCullFace(mode);
        for (GL20Monitor monitor : monitorList) {
            monitor.glCullFace(mode);
        }
    }

    @Override
    public void glDeleteBuffers(int n, IntBuffer buffers) {
        GLES20.glDeleteBuffers(n, buffers);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDeleteBuffers(n, buffers);
        }
    }

    @Override
    public void glDeleteBuffers(int n,
                                int[] buffers,
                                int offset) {
        GLES20.glDeleteBuffers(n, buffers, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDeleteBuffers(n, buffers, offset);
        }
    }

    @Override
    public void glDeleteBuffer(int buffer) {
        tempInts[0] = buffer;
        GLES20.glDeleteBuffers(1, tempInts, 0);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDeleteBuffers(1, tempInts, 0);
        }
    }

    @Override
    public void glDeleteFramebuffers(int n, IntBuffer framebuffers) {
        GLES20.glDeleteFramebuffers(n, framebuffers);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDeleteFramebuffers(n, framebuffers);
        }
    }

    @Override
    public void glDeleteFramebuffers(int n, int[] framebuffers, int offset) {
        GLES20.glDeleteFramebuffers(n, framebuffers, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDeleteFramebuffers(n, framebuffers, offset);
        }
    }

    @Override
    public void glDeleteFramebuffer(int framebuffer) {
        tempInts[0] = framebuffer;
        GLES20.glDeleteFramebuffers(1, tempInts, 0);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDeleteFramebuffers(1, tempInts, 0);
        }
    }

    @Override
    public void glDeleteProgram(int program) {
        GLES20.glDeleteProgram(program);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDeleteProgram(program);
        }
    }

    @Override
    public void glDeleteRenderbuffers(int n, IntBuffer renderbuffers) {
        GLES20.glDeleteRenderbuffers(n, renderbuffers);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDeleteRenderbuffers(n, renderbuffers);
        }
    }

    @Override
    public void glDeleteRenderbuffers(int n, int[] renderbuffers, int offset) {
        GLES20.glDeleteRenderbuffers(n, renderbuffers, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDeleteRenderbuffers(n, renderbuffers, offset);
        }
    }

    @Override
    public void glDeleteRenderbuffer(int renderbuffer) {
        tempInts[0] = renderbuffer;
        GLES20.glDeleteRenderbuffers(1, tempInts, 0);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDeleteRenderbuffers(1, tempInts, 0);
        }
    }

    @Override
    public void glDeleteShader(int shader) {
        GLES20.glDeleteShader(shader);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDeleteShader(shader);
        }
    }

    @Override
    public void glDeleteTextures(int n, IntBuffer textures) {
        GLES20.glDeleteTextures(n, textures);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDeleteTextures(n, textures);
        }
    }

    @Override
    public void glDeleteTextures(int n, int[] textures, int offset) {
        GLES20.glDeleteTextures(n, textures, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDeleteTextures(n, textures, offset);
        }
    }

    @Override
    public void glDeleteTexture(int texture) {
        tempInts[0] = texture;
        GLES20.glDeleteTextures(1, tempInts, 0);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDeleteTextures(1, tempInts, 0);
        }
    }

    @Override
    public void glDepthFunc(int func) {
        GLES20.glDepthFunc(func);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDepthFunc(func);
        }
    }

    @Override
    public void glDepthMask(boolean flag) {
        GLES20.glDepthMask(flag);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDepthMask(flag);
        }
    }

    @Override
    public void glDepthRangef(float zNear, float zFar) {
        GLES20.glDepthRangef(zNear, zFar);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDepthRangef(zNear, zFar);
        }
    }

    @Override
    public void glDetachShader(int program, int shader) {
        GLES20.glDetachShader(program, shader);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDetachShader(program, shader);
        }
    }

    @Override
    public void glDisable(int cap) {
        GLES20.glDisable(cap);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDisable(cap);
        }
    }

    @Override
    public void glDisableVertexAttribArray(int index) {
        GLES20.glDisableVertexAttribArray(index);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDisableVertexAttribArray(index);
        }
    }

    @Override
    public void glDrawArrays(int mode, int first, int count) {
        GLES20.glDrawArrays(mode, first, count);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDrawArrays(mode, first, count);
        }
    }

    @Override
    public void glDrawElements(int mode, int count, int type, Buffer indices) {
        GLES20.glDrawElements(mode, count, type, indices);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDrawElements(mode, count, type, indices);
        }
    }

    @Override
    public void glDrawElements(int mode, int count, int type, int indices) {
        GLES20.glDrawElements(mode, count, type, indices);
        for (GL20Monitor monitor : monitorList) {
            monitor.glDrawElements(mode, count, type, indices);
        }
    }

    @Override
    public void glEnable(int cap) {
        GLES20.glEnable(cap);
        for (GL20Monitor monitor : monitorList) {
            monitor.glEnable(cap);
        }
    }

    @Override
    public void glEnableVertexAttribArray(int index) {
        GLES20.glEnableVertexAttribArray(index);
        for (GL20Monitor monitor : monitorList) {
            monitor.glEnableVertexAttribArray(index);
        }
    }

    @Override
    public void glFinish() {
        GLES20.glFinish();
        for (GL20Monitor monitor : monitorList) {
            monitor.glFinish();
        }
    }

    @Override
    public void glFlush() {
        GLES20.glFlush();
        for (GL20Monitor monitor : monitorList) {
            monitor.glFlush();
        }
    }

    @Override
    public void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
        GLES20.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
        for (GL20Monitor monitor : monitorList) {
            monitor.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
        }
    }

    @Override
    public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        GLES20.glFramebufferTexture2D(target, attachment, textarget, texture, level);
        for (GL20Monitor monitor : monitorList) {
            monitor.glFramebufferTexture2D(target, attachment, textarget, texture, level);
        }
    }

    @Override
    public void glFrontFace(int mode) {
        GLES20.glFrontFace(mode);
        for (GL20Monitor monitor : monitorList) {
            monitor.glFrontFace(mode);
        }
    }

    @Override
    public void glGenBuffers(int n, IntBuffer buffers) {
        GLES20.glGenBuffers(n, buffers);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGenBuffers(n, buffers);
        }
    }

    @Override
    public void glGenBuffers(int n, int[] buffers, int offset) {
        GLES20.glGenBuffers(n, buffers, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGenBuffers(n, buffers, offset);
        }
    }

    @Override
    public int glGenBuffer() {
        tempInts[0] = 0;
        GLES20.glGenBuffers(1, tempInts, 0);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGenBuffer(tempInts[0]);
        }
        return tempInts[0];
    }

    @Override
    public void glGenerateMipmap(int target) {
        GLES20.glGenerateMipmap(target);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGenerateMipmap(target);
        }
    }

    @Override
    public void glGenFramebuffers(int n, IntBuffer framebuffers) {
        GLES20.glGenFramebuffers(n, framebuffers);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGenFramebuffers(n, framebuffers);
        }
    }

    @Override
    public void glGenFramebuffers(int n, int[] framebuffers, int offset) {
        GLES20.glGenFramebuffers(n, framebuffers, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGenFramebuffers(n, framebuffers, offset);
        }
    }

    @Override
    public int glGenFramebuffer() {
        tempInts[0] = 0;
        GLES20.glGenFramebuffers(1, tempInts, 0);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGenFramebuffer(tempInts[0]);
        }
        return tempInts[0];

    }

    @Override
    public void glGenRenderbuffers(int n, IntBuffer renderbuffers) {
        GLES20.glGenRenderbuffers(n, renderbuffers);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGenRenderbuffers(n, renderbuffers);
        }
    }

    @Override
    public int glGenRenderbuffer() {
        GLES20.glGenRenderbuffers(1, tempInts, 0);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGenRenderbuffer(tempInts[0]);
        }
        return tempInts[0];
    }

    @Override
    public void glGenTextures(int n, IntBuffer textures) {
        GLES20.glGenTextures(n, textures);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGenTextures(n, textures);
        }
    }

    @Override
    public void glGenTextures(int n, int[] textures, int offset) {
        GLES20.glGenTextures(n, textures, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGenTextures(n, textures, offset);
        }
    }

    @Override
    public int glGenTexture() {
        GLES20.glGenTextures(1, tempInts, 0);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGenTexture(tempInts[0]);
        }
        return tempInts[0];
    }

    @Override
    public void glGetActiveAttrib(
            int program,
            int index,
            int bufsize,
            int[] length,
            int lengthOffset,
            int[] size,
            int sizeOffset,
            int[] type,
            int typeOffset,
            byte[] name,
            int nameOffset) {
        GLES20.glGetActiveAttrib(program, index, bufsize, length, lengthOffset, size, sizeOffset, type, typeOffset, name, nameOffset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetActiveAttrib(program, index, bufsize, length, lengthOffset, size, sizeOffset, type, typeOffset, name, nameOffset);
        }
    }

    @Override
    public String glGetActiveAttrib(
            int program,
            int index,
            int[] size,
            int sizeOffset,
            int[] type,
            int typeOffset
    ) {
        String attrib = GLES20.glGetActiveAttrib(program,
                index,
                size,
                sizeOffset,
                type,
                typeOffset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetActiveAttrib(program,
                    index,
                    size,
                    sizeOffset,
                    type,
                    typeOffset, attrib);
        }
        return attrib;

    }

    @Override
    public String glGetActiveAttrib(
            int program,
            int index,
            java.nio.IntBuffer size,
            java.nio.IntBuffer type) {

        String attrib = GLES20.glGetActiveAttrib(program,
                index,
                size,
                type);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetActiveAttrib(program,
                    index,
                    size,
                    type, attrib);
        }
        return attrib;

    }

    @Override
    public String glGetActiveAttrib(int program, int index, int[] size, int[] type) {
        GLES20.glGetActiveAttrib(program, index, tempBuffer.length, tempInts, 0, size, 0, type, 0, tempBuffer, 0);
        String attrib = new String(tempBuffer, 0, tempInts[0]);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetActiveAttrib(program, index, size, type, attrib);
        }
        return attrib;
    }


    @Override
    public void glGetActiveUniform(
            int program,
            int index,
            int bufsize,
            int[] length,
            int lengthOffset,
            int[] size,
            int sizeOffset,
            int[] type,
            int typeOffset,
            byte[] name,
            int nameOffset) {
        GLES20.glGetActiveUniform(program, index, bufsize, length, lengthOffset, size, sizeOffset, type, typeOffset, name, nameOffset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetActiveUniform(program, index, bufsize, length, lengthOffset, size, sizeOffset, type, typeOffset, name, nameOffset);
        }
    }

    @Override
    public String glGetActiveUniform(
            int program,
            int index,
            int[] size,
            int sizeOffset,
            int[] type,
            int typeOffset
    ) {
        String name = GLES20.glGetActiveUniform(program,
                index,
                size,
                sizeOffset,
                type,
                typeOffset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetActiveUniform(program,
                    index,
                    size,
                    sizeOffset,
                    type,
                    typeOffset, name);
        }
        return name;

    }

    @Override
    public String glGetActiveUniform(
            int program,
            int index,
            java.nio.IntBuffer size,
            java.nio.IntBuffer type) {
        String name = GLES20.glGetActiveUniform(program,
                index,
                size,
                type);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetActiveUniform(program,
                    index,
                    size,
                    type, name);
        }
        return name;

    }

    @Override
    public String glGetActiveUniform(int program, int index, int[] size, int[] type) {
        GLES20.glGetActiveUniform(program, index, tempBuffer.length, tempInts, 0, size, 0, type, 0, tempBuffer, 0);
        String name = new String(tempBuffer, 0, tempInts[0]);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetActiveUniform(program, index, size, type, name);
        }
        return name;

    }

    @Override
    public void glGetAttachedShaders(int program, int maxcount, IntBuffer count, IntBuffer shaders) {
        GLES20.glGetAttachedShaders(program, maxcount, count, shaders);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetAttachedShaders(program, maxcount, count, shaders);
        }
    }

    @Override
    public int glGetAttribLocation(int program, String name) {
        int location = GLES20.glGetAttribLocation(program, name);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetAttribLocation(program, name, location);
        }
        return location;

    }

    @Override
    public void glGetBooleanv(int pname, IntBuffer params) {
        GLES20.glGetBooleanv(pname, params);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetBooleanv(pname, params);
        }
    }

    @Override
    public void glGetBufferParameteriv(int target, int pname, IntBuffer params) {
        GLES20.glGetBufferParameteriv(target, pname, params);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetBufferParameteriv(target, pname, params);
        }
    }

    @Override
    public int glGetError() {
        int error = GLES20.glGetError();
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetError(error);
        }
        return error;

    }

    @Override
    public void glGetFloatv(int pname, FloatBuffer params) {
        GLES20.glGetFloatv(pname, params);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetFloatv(pname, params);
        }
    }

    @Override
    public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params) {
        GLES20.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
        }
    }

    @Override
    public int glGetInteger(int pname) {
        tempInts[0] = 0;
        GLES20.glGetIntegerv(pname, tempInts, 0);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetInteger(pname, tempInts[0]);
        }
        return tempInts[0];
    }

    @Override
    public void glGetIntegerv(int pname, IntBuffer params) {
        GLES20.glGetIntegerv(pname, params);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetIntegerv(pname, params);
        }
    }

    @Override
    public void glGetIntegerv(int pname, int[] params, int offset) {
        GLES20.glGetIntegerv(pname, params, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetIntegerv(pname, params, offset);
        }
    }

    @Override
    public void glGetProgramiv(int program, int pname, IntBuffer params) {
        GLES20.glGetProgramiv(program, pname, params);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetProgramiv(program, pname, params);
        }
    }

    @Override
    public void glGetProgramiv(int program,
                               int pname,
                               int[] params,
                               int offset) {
        GLES20.glGetProgramiv(program, pname, params, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetProgramiv(program, pname, params, offset);
        }
    }

    @Override
    public int glGetProgram(int program, int pname) {
        tempInts[0] = 0;
        GLES20.glGetProgramiv(program, pname, tempInts, 0);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetProgram(program, pname, tempInts[0]);
        }
        return tempInts[0];
    }

    @Override
    public String glGetProgramInfoLog(int program) {
        String log = GLES20.glGetProgramInfoLog(program);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetProgramInfoLog(program, log);
        }
        return log;
    }

    @Override
    public void glGetRenderbufferParameteriv(int target, int pname, IntBuffer params) {
        GLES20.glGetRenderbufferParameteriv(target, pname, params);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetRenderbufferParameteriv(target, pname, params);
        }
    }

    @Override
    public void glGetShaderiv(int shader, int pname, IntBuffer params) {
        GLES20.glGetShaderiv(shader, pname, params);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetShaderiv(shader, pname, params);
        }
    }

    @Override
    public void glGetShaderiv(int shader, int pname, int[] params, int offset) {
        GLES20.glGetShaderiv(shader, pname, params, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetShaderiv(shader, pname, params, offset);
        }
    }

    @Override
    public int glGetShaderiv(int shader, int pname) {
        tempInts[0] = 0;
        GLES20.glGetShaderiv(shader, pname, tempInts, 0);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetShaderiv(shader, pname, tempInts[0]);
        }
        return tempInts[0];
    }

    @Override
    public String glGetShaderInfoLog(int shader) {
        String log = GLES20.glGetShaderInfoLog(shader);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetShaderInfoLog(shader, log);
        }
        return log;

    }

    @Override
    public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range, IntBuffer precision) {
        GLES20.glGetShaderPrecisionFormat(shadertype, precisiontype, range, precision);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetShaderPrecisionFormat(shadertype, precisiontype, range, precision);
        }
    }

    @Override
    public String glGetString(int name) {
        String value = GLES20.glGetString(name);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetString(name, value);
        }
        return value;

    }

    @Override
    public void glGetTexParameterfv(int target, int pname, FloatBuffer params) {
        GLES20.glGetTexParameterfv(target, pname, params);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetTexParameterfv(target, pname, params);
        }
    }

    @Override
    public void glGetTexParameteriv(int target, int pname, IntBuffer params) {
        GLES20.glGetTexParameteriv(target, pname, params);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetTexParameteriv(target, pname, params);
        }
    }

    @Override
    public void glGetUniformfv(int program, int location, FloatBuffer params) {
        GLES20.glGetUniformfv(program, location, params);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetUniformfv(program, location, params);
        }
    }

    @Override
    public void glGetUniformiv(int program, int location, IntBuffer params) {
        GLES20.glGetUniformiv(program, location, params);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetUniformiv(program, location, params);
        }
    }

    @Override
    public int glGetUniformLocation(int program, String name) {
        int location = GLES20.glGetUniformLocation(program, name);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetUniformLocation(program, name, location);
        }
        return location;

    }

    @Override
    public void glGetVertexAttribfv(int index, int pname, FloatBuffer params) {
        GLES20.glGetVertexAttribfv(index, pname, params);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetVertexAttribfv(index, pname, params);
        }
    }

    @Override
    public void glGetVertexAttribiv(int index, int pname, IntBuffer params) {
        GLES20.glGetVertexAttribiv(index, pname, params);
        for (GL20Monitor monitor : monitorList) {
            monitor.glGetVertexAttribiv(index, pname, params);
        }
    }

    @Override
    public void glHint(int target, int mode) {
        GLES20.glHint(target, mode);
        for (GL20Monitor monitor : monitorList) {
            monitor.glHint(target, mode);
        }
    }

    @Override
    public boolean glIsBuffer(int buffer) {
        boolean isBuffer = GLES20.glIsBuffer(buffer);
        for (GL20Monitor monitor : monitorList) {
            monitor.glIsBuffer(buffer, isBuffer);
        }
        return isBuffer;
    }

    @Override
    public boolean glIsEnabled(int cap) {
        boolean isEnabled = GLES20.glIsEnabled(cap);
        for (GL20Monitor monitor : monitorList) {
            monitor.glIsEnabled(cap, isEnabled);
        }
        return isEnabled;
    }

    @Override
    public boolean glIsFramebuffer(int framebuffer) {
        boolean isFramebuffer = GLES20.glIsFramebuffer(framebuffer);
        for (GL20Monitor monitor : monitorList) {
            monitor.glIsFramebuffer(framebuffer, isFramebuffer);
        }
        return isFramebuffer;
    }

    @Override
    public boolean glIsProgram(int program) {
        boolean isProgram = GLES20.glIsProgram(program);
        for (GL20Monitor monitor : monitorList) {
            monitor.glIsProgram(program, isProgram);
        }
        return isProgram;

    }

    @Override
    public boolean glIsRenderbuffer(int renderbuffer) {
        boolean isRenderbuffer = GLES20.glIsRenderbuffer(renderbuffer);
        for (GL20Monitor monitor : monitorList) {
            monitor.glIsRenderbuffer(renderbuffer, isRenderbuffer);
        }
        return isRenderbuffer;

    }

    @Override
    public boolean glIsShader(int shader) {
        boolean isShader = GLES20.glIsShader(shader);
        for (GL20Monitor monitor : monitorList) {
            monitor.glIsShader(shader, isShader);
        }
        return isShader;

    }

    @Override
    public boolean glIsTexture(int texture) {
        boolean isTexture = GLES20.glIsTexture(texture);
        for (GL20Monitor monitor : monitorList) {
            monitor.glIsTexture(texture, isTexture);
        }
        return isTexture;

    }

    @Override
    public void glLineWidth(float width) {
        GLES20.glLineWidth(width);
        for (GL20Monitor monitor : monitorList) {
            monitor.glLineWidth(width);
        }
    }

    @Override
    public void glLinkProgram(int program) {
        GLES20.glLinkProgram(program);
        for (GL20Monitor monitor : monitorList) {
            monitor.glLinkProgram(program);
        }
    }

    @Override
    public void glPixelStorei(int pname, int param) {
        GLES20.glPixelStorei(pname, param);
        for (GL20Monitor monitor : monitorList) {
            monitor.glPixelStorei(pname, param);
        }
    }

    @Override
    public void glPolygonOffset(float factor, float units) {
        GLES20.glPolygonOffset(factor, units);
        for (GL20Monitor monitor : monitorList) {
            monitor.glPolygonOffset(factor, units);
        }
    }

    @Override
    public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
        GLES20.glReadPixels(x, y, width, height, format, type, pixels);
        for (GL20Monitor monitor : monitorList) {
            monitor.glReadPixels(x, y, width, height, format, type, pixels);
        }
    }

    @Override
    public void glReleaseShaderCompiler() {
        GLES20.glReleaseShaderCompiler();
        for (GL20Monitor monitor : monitorList) {
            monitor.glReleaseShaderCompiler();
        }
    }

    @Override
    public void glRenderbufferStorage(int target, int internalformat, int width, int height) {
        GLES20.glRenderbufferStorage(target, internalformat, width, height);
        for (GL20Monitor monitor : monitorList) {
            monitor.glRenderbufferStorage(target, internalformat, width, height);
        }
    }

    @Override
    public void glSampleCoverage(float value, boolean invert) {
        GLES20.glSampleCoverage(value, invert);
        for (GL20Monitor monitor : monitorList) {
            monitor.glSampleCoverage(value, invert);
        }
    }

    @Override
    public void glScissor(int x, int y, int width, int height) {
        GLES20.glScissor(x, y, width, height);
        for (GL20Monitor monitor : monitorList) {
            monitor.glScissor(x, y, width, height);
        }
    }

    @Override
    public void glShaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
        GLES20.glShaderBinary(n, shaders, binaryformat, binary, length);
        for (GL20Monitor monitor : monitorList) {
            monitor.glShaderBinary(n, shaders, binaryformat, binary, length);
        }
    }

    @Override
    public void glShaderSource(int shader, String string) {
        GLES20.glShaderSource(shader, string);
        for (GL20Monitor monitor : monitorList) {
            monitor.glShaderSource(shader, string);
        }
    }

    @Override
    public void glStencilFunc(int func, int ref, int mask) {
        GLES20.glStencilFunc(func, ref, mask);
        for (GL20Monitor monitor : monitorList) {
            monitor.glStencilFunc(func, ref, mask);
        }
    }

    @Override
    public void glStencilFuncSeparate(int face, int func, int ref, int mask) {
        GLES20.glStencilFuncSeparate(face, func, ref, mask);
        for (GL20Monitor monitor : monitorList) {
            monitor.glStencilFuncSeparate(face, func, ref, mask);
        }
    }

    @Override
    public void glStencilMask(int mask) {
        GLES20.glStencilMask(mask);
        for (GL20Monitor monitor : monitorList) {
            monitor.glStencilMask(mask);
        }
    }

    @Override
    public void glStencilMaskSeparate(int face, int mask) {
        GLES20.glStencilMaskSeparate(face, mask);
        for (GL20Monitor monitor : monitorList) {
            monitor.glStencilMaskSeparate(face, mask);
        }
    }

    @Override
    public void glStencilOp(int fail, int zfail, int zpass) {
        GLES20.glStencilOp(fail, zfail, zpass);
        for (GL20Monitor monitor : monitorList) {
            monitor.glStencilOp(fail, zfail, zpass);
        }
    }

    @Override
    public void glStencilOpSeparate(int face, int fail, int zfail, int zpass) {
        GLES20.glStencilOpSeparate(face, fail, zfail, zpass);
        for (GL20Monitor monitor : monitorList) {
            monitor.glStencilOpSeparate(face, fail, zfail, zpass);
        }
    }

    @Override
    public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type,
                             Buffer pixels) {
        GLES20.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
        for (GL20Monitor monitor : monitorList) {
            monitor.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
        }
    }

    @Override
    public void glTexParameterf(int target, int pname, float param) {
        GLES20.glTexParameterf(target, pname, param);
        for (GL20Monitor monitor : monitorList) {
            monitor.glTexParameterf(target, pname, param);
        }
    }

    @Override
    public void glTexParameterfv(int target, int pname, FloatBuffer params) {
        GLES20.glTexParameterfv(target, pname, params);
        for (GL20Monitor monitor : monitorList) {
            monitor.glTexParameterfv(target, pname, params);
        }
    }

    @Override
    public void glTexParameteri(int target, int pname, int param) {
        GLES20.glTexParameteri(target, pname, param);
        for (GL20Monitor monitor : monitorList) {
            monitor.glTexParameteri(target, pname, param);
        }
    }

    @Override
    public void glTexParameteriv(int target, int pname, IntBuffer params) {
        GLES20.glTexParameteriv(target, pname, params);
        for (GL20Monitor monitor : monitorList) {
            monitor.glTexParameteriv(target, pname, params);
        }
    }

    @Override
    public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type,
                                Buffer pixels) {
        GLES20.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
        for (GL20Monitor monitor : monitorList) {
            monitor.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
        }
    }

    @Override
    public void glUniform1f(int location, float x) {
        GLES20.glUniform1f(location, x);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform1f(location, x);
        }
    }

    @Override
    public void glUniform1fv(int location, int count, FloatBuffer v) {
        GLES20.glUniform1fv(location, count, v);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform1fv(location, count, v);
        }
    }

    @Override
    public void glUniform1fv(int location, int count, float[] v, int offset) {
        GLES20.glUniform1fv(location, count, v, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform1fv(location, count, v, offset);
        }
    }

    @Override
    public void glUniform1i(int location, int x) {
        GLES20.glUniform1i(location, x);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform1i(location, x);
        }
    }

    @Override
    public void glUniform1iv(int location, int count, IntBuffer v) {
        GLES20.glUniform1iv(location, count, v);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform1iv(location, count, v);
        }
    }

    @Override
    public void glUniform1iv(int location, int count, int[] v, int offset) {
        GLES20.glUniform1iv(location, count, v, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform1iv(location, count, v, offset);
        }
    }

    @Override
    public void glUniform2f(int location, float x, float y) {
        GLES20.glUniform2f(location, x, y);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform2f(location, x, y);
        }
    }

    @Override
    public void glUniform2fv(int location, int count, FloatBuffer v) {
        GLES20.glUniform2fv(location, count, v);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform2fv(location, count, v);
        }
    }

    @Override
    public void glUniform2fv(int location, int count, float[] v, int offset) {
        GLES20.glUniform2fv(location, count, v, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform2fv(location, count, v, offset);
        }
    }

    @Override
    public void glUniform2i(int location, int x, int y) {
        GLES20.glUniform2i(location, x, y);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform2i(location, x, y);
        }
    }

    @Override
    public void glUniform2iv(int location, int count, IntBuffer v) {
        GLES20.glUniform2iv(location, count, v);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform2iv(location, count, v);
        }
    }

    @Override
    public void glUniform2iv(int location, int count, int[] v, int offset) {
        GLES20.glUniform2iv(location, count, v, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform2iv(location, count, v, offset);
        }
    }

    @Override
    public void glUniform3f(int location, float x, float y, float z) {
        GLES20.glUniform3f(location, x, y, z);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform3f(location, x, y, z);
        }
    }

    @Override
    public void glUniform3fv(int location, int count, FloatBuffer v) {
        GLES20.glUniform3fv(location, count, v);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform3fv(location, count, v);
        }
    }

    @Override
    public void glUniform3fv(int location, int count, float[] v, int offset) {
        GLES20.glUniform3fv(location, count, v, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform3fv(location, count, v, offset);
        }
    }

    @Override
    public void glUniform3i(int location, int x, int y, int z) {
        GLES20.glUniform3i(location, x, y, z);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform3i(location, x, y, z);
        }
    }

    @Override
    public void glUniform3iv(int location, int count, IntBuffer v) {
        GLES20.glUniform3iv(location, count, v);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform3iv(location, count, v);
        }
    }

    @Override
    public void glUniform3iv(int location, int count, int[] v, int offset) {
        GLES20.glUniform3iv(location, count, v, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform3iv(location, count, v, offset);
        }
    }

    @Override
    public void glUniform4f(int location, float x, float y, float z, float w) {
        GLES20.glUniform4f(location, x, y, z, w);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform4f(location, x, y, z, w);
        }
    }

    @Override
    public void glUniform4fv(int location, int count, FloatBuffer v) {
        GLES20.glUniform4fv(location, count, v);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform4fv(location, count, v);
        }
    }

    @Override
    public void glUniform4fv(int location, int count, float[] v, int offset) {
        GLES20.glUniform4fv(location, count, v, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform4fv(location, count, v, offset);
        }
    }

    @Override
    public void glUniform4i(int location, int x, int y, int z, int w) {
        GLES20.glUniform4i(location, x, y, z, w);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform4i(location, x, y, z, w);
        }
    }

    @Override
    public void glUniform4iv(int location, int count, IntBuffer v) {
        GLES20.glUniform4iv(location, count, v);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform4iv(location, count, v);
        }
    }

    @Override
    public void glUniform4iv(int location, int count, int[] v, int offset) {
        GLES20.glUniform4iv(location, count, v, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniform4iv(location, count, v, offset);
        }
    }

    @Override
    public void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {
        GLES20.glUniformMatrix2fv(location, count, transpose, value);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniformMatrix2fv(location, count, transpose, value);
        }
    }

    @Override
    public void glUniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset) {
        GLES20.glUniformMatrix2fv(location, count, transpose, value, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniformMatrix2fv(location, count, transpose, value, offset);
        }
    }

    @Override
    public void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {
        GLES20.glUniformMatrix3fv(location, count, transpose, value);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniformMatrix3fv(location, count, transpose, value);
        }
    }

    @Override
    public void glUniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset) {
        GLES20.glUniformMatrix3fv(location, count, transpose, value, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniformMatrix3fv(location, count, transpose, value, offset);
        }
    }

    @Override
    public void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {
        GLES20.glUniformMatrix4fv(location, count, transpose, value);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniformMatrix4fv(location, count, transpose, value);
        }
    }

    @Override
    public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {
        GLES20.glUniformMatrix4fv(location, count, transpose, value, offset);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUniformMatrix4fv(location, count, transpose, value, offset);
        }
    }

    @Override
    public void glUseProgram(int program) {
        GLES20.glUseProgram(program);
        for (GL20Monitor monitor : monitorList) {
            monitor.glUseProgram(program);
        }
    }

    @Override
    public void glValidateProgram(int program) {
        GLES20.glValidateProgram(program);
        for (GL20Monitor monitor : monitorList) {
            monitor.glValidateProgram(program);
        }
    }

    @Override
    public void glVertexAttrib1f(int indx, float x) {
        GLES20.glVertexAttrib1f(indx, x);
        for (GL20Monitor monitor : monitorList) {
            monitor.glVertexAttrib1f(indx, x);
        }
    }

    @Override
    public void glVertexAttrib1fv(int indx, FloatBuffer values) {
        GLES20.glVertexAttrib1fv(indx, values);
        for (GL20Monitor monitor : monitorList) {
            monitor.glVertexAttrib1fv(indx, values);
        }
    }

    @Override
    public void glVertexAttrib2f(int indx, float x, float y) {
        GLES20.glVertexAttrib2f(indx, x, y);
        for (GL20Monitor monitor : monitorList) {
            monitor.glVertexAttrib2f(indx, x, y);
        }
    }

    @Override
    public void glVertexAttrib2fv(int indx, FloatBuffer values) {
        GLES20.glVertexAttrib2fv(indx, values);
        for (GL20Monitor monitor : monitorList) {
            monitor.glVertexAttrib2fv(indx, values);
        }
    }

    @Override
    public void glVertexAttrib3f(int indx, float x, float y, float z) {
        GLES20.glVertexAttrib3f(indx, x, y, z);
        for (GL20Monitor monitor : monitorList) {
            monitor.glVertexAttrib3f(indx, x, y, z);
        }
    }

    @Override
    public void glVertexAttrib3fv(int indx, FloatBuffer values) {
        GLES20.glVertexAttrib3fv(indx, values);
        for (GL20Monitor monitor : monitorList) {
            monitor.glVertexAttrib3fv(indx, values);
        }
    }

    @Override
    public void glVertexAttrib4f(int indx, float x, float y, float z, float w) {
        GLES20.glVertexAttrib4f(indx, x, y, z, w);
        for (GL20Monitor monitor : monitorList) {
            monitor.glVertexAttrib4f(indx, x, y, z, w);
        }
    }

    @Override
    public void glVertexAttrib4fv(int indx, FloatBuffer values) {
        GLES20.glVertexAttrib4fv(indx, values);
        for (GL20Monitor monitor : monitorList) {
            monitor.glVertexAttrib4fv(indx, values);
        }
    }

    @Override
    public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr) {
        GLES20.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
        for (GL20Monitor monitor : monitorList) {
            monitor.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
        }
    }

    @Override
    public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int ptr) {
        GLES20.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
        for (GL20Monitor monitor : monitorList) {
            monitor.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
        }
    }

    @Override
    public void glViewport(int x, int y, int width, int height) {
        GLES20.glViewport(x, y, width, height);
        for (GL20Monitor monitor : monitorList) {
            monitor.glViewport(x, y, width, height);
        }
    }

    @Override
    public void addGLMonitor(GL20Monitor monitor) {
        if (monitor == null || monitorList.contains(monitor)) {
            return;
        }
        monitorList.add(monitor);
    }

    @Override
    public void removeGLMonitor(GL20Monitor monitor) {
        if (monitor == null || !monitorList.contains(monitor)) {
            return;
        }
        monitorList.remove(monitor);
    }
}
