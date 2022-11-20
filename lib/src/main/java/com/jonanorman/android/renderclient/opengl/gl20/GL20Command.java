package com.jonanorman.android.renderclient.opengl.gl20;

import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLU;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class GL20Command implements GL20 {
    private final int[] tempInts;
    private final byte[] tempBuffer;
    private final List<GLMonitor> monitorList;
    private final List<GL20Monitor> monitor20List;

    public GL20Command() {
        super();
        tempInts = new int[1];
        tempBuffer = new byte[512];
        monitorList = new ArrayList<>();
        monitor20List = new ArrayList<>();
    }


    @Override
    public void glActiveTexture(int texture) {
        GLES20.glActiveTexture(texture);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLActiveTexture(texture);
        }
    }

    @Override
    public void glAttachShader(int program, int shader) {
        GLES20.glAttachShader(program, shader);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLAttachShader(program, shader);
        }
    }

    @Override
    public void glBindAttribLocation(int program, int index, String name) {
        GLES20.glBindAttribLocation(program, index, name);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLBindAttribLocation(program, index, name);
        }
    }

    @Override
    public void glBindBuffer(int target, int buffer) {
        GLES20.glBindBuffer(target, buffer);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLBindBuffer(target, buffer);
        }
    }

    @Override
    public void glBindFramebuffer(int target, int framebuffer) {
        GLES20.glBindFramebuffer(target, framebuffer);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLBindFramebuffer(target, framebuffer);
        }
    }

    @Override
    public void glBindRenderbuffer(int target, int renderbuffer) {
        GLES20.glBindRenderbuffer(target, renderbuffer);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLBindRenderbuffer(target, renderbuffer);
        }
    }

    @Override
    public void glBindTexture(int target, int texture) {
        GLES20.glBindTexture(target, texture);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLBindTexture(target, texture);
        }
    }

    @Override
    public void glBlendColor(float red, float green, float blue, float alpha) {
        GLES20.glBlendColor(red, green, blue, alpha);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLBlendColor(red, green, blue, alpha);
        }
    }

    @Override
    public void glBlendEquation(int mode) {
        GLES20.glBlendEquation(mode);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLBlendEquation(mode);
        }
    }

    @Override
    public void glBlendEquationSeparate(int modeRGB, int modeAlpha) {
        GLES20.glBlendEquationSeparate(modeRGB, modeAlpha);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLBlendEquationSeparate(modeRGB, modeAlpha);
        }
    }

    @Override
    public void glBlendFunc(int sfactor, int dfactor) {
        GLES20.glBlendFunc(sfactor, dfactor);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLBlendFunc(sfactor, dfactor);
        }
    }

    @Override
    public void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        GLES20.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
        }
    }

    @Override
    public void glBufferData(int target, int size, Buffer data, int usage) {
        GLES20.glBufferData(target, size, data, usage);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLBufferData(target, size, data, usage);
        }
    }

    @Override
    public void glBufferSubData(int target, int offset, int size, Buffer data) {
        GLES20.glBufferSubData(target, offset, size, data);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLBufferSubData(target, offset, size, data);
        }
    }

    @Override
    public int glCheckFramebufferStatus(int target) {
        int status = GLES20.glCheckFramebufferStatus(target);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLCheckFramebufferStatus(target, status);
        }
        return status;
    }

    @Override
    public void glClear(int mask) {
        GLES20.glClear(mask);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLClear(mask);
        }
    }

    @Override
    public void glClearColor(float red, float green, float blue, float alpha) {
        GLES20.glClearColor(red, green, blue, alpha);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLClearColor(red, green, blue, alpha);
        }
    }

    @Override
    public void glClearDepthf(float depth) {
        GLES20.glClearDepthf(depth);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLClearDepthf(depth);
        }
    }

    @Override
    public void glClearStencil(int s) {
        GLES20.glClearStencil(s);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLClearStencil(s);
        }
    }

    @Override
    public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        GLES20.glColorMask(red, green, blue, alpha);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLColorMask(red, green, blue, alpha);
        }
    }

    @Override
    public void glCompileShader(int shader) {
        GLES20.glCompileShader(shader);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLCompileShader(shader);
        }
    }

    @Override
    public void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border,
                                       int imageSize, Buffer data) {
        GLES20.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
        }
    }

    @Override
    public void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format,
                                          int imageSize, Buffer data) {
        GLES20.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
        }
    }

    @Override
    public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border) {
        GLES20.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
        }
    }

    @Override
    public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
        GLES20.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
        }
    }

    @Override
    public int glCreateProgram() {
        int program = GLES20.glCreateProgram();
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLCreateProgram(program);
        }
        return program;
    }

    @Override
    public int glCreateShader(int type) {
        int shader = GLES20.glCreateShader(type);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLCreateShader(type, shader);
        }
        return shader;
    }

    @Override
    public void glCullFace(int mode) {
        GLES20.glCullFace(mode);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLCullFace(mode);
        }
    }

    @Override
    public void glDeleteBuffers(int n, IntBuffer buffers) {
        GLES20.glDeleteBuffers(n, buffers);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDeleteBuffers(n, buffers);
        }
    }

    @Override
    public void glDeleteBuffers(int n,
                                int[] buffers,
                                int offset) {
        GLES20.glDeleteBuffers(n, buffers, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDeleteBuffers(n, buffers, offset);
        }
    }

    @Override
    public void glDeleteBuffer(int buffer) {
        tempInts[0] = buffer;
        GLES20.glDeleteBuffers(1, tempInts, 0);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDeleteBuffer(buffer);
        }
    }

    @Override
    public void glDeleteFramebuffers(int n, IntBuffer framebuffers) {
        GLES20.glDeleteFramebuffers(n, framebuffers);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDeleteFramebuffers(n, framebuffers);
        }
    }

    @Override
    public void glDeleteFramebuffers(int n, int[] framebuffers, int offset) {
        GLES20.glDeleteFramebuffers(n, framebuffers, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDeleteFramebuffers(n, framebuffers, offset);
        }
    }

    @Override
    public void glDeleteFramebuffer(int framebuffer) {
        tempInts[0] = framebuffer;
        GLES20.glDeleteFramebuffers(1, tempInts, 0);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDeleteFramebuffer(framebuffer);
        }
    }

    @Override
    public void glDeleteProgram(int program) {
        GLES20.glDeleteProgram(program);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDeleteProgram(program);
        }
    }

    @Override
    public void glDeleteRenderbuffers(int n, IntBuffer renderbuffers) {
        GLES20.glDeleteRenderbuffers(n, renderbuffers);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDeleteRenderbuffers(n, renderbuffers);
        }
    }

    @Override
    public void glDeleteRenderbuffers(int n, int[] renderbuffers, int offset) {
        GLES20.glDeleteRenderbuffers(n, renderbuffers, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDeleteRenderbuffers(n, renderbuffers, offset);
        }
    }

    @Override
    public void glDeleteRenderbuffer(int renderbuffer) {
        tempInts[0] = renderbuffer;
        GLES20.glDeleteRenderbuffers(1, tempInts, 0);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDeleteRenderbuffer(renderbuffer);
        }
    }

    @Override
    public void glDeleteShader(int shader) {
        GLES20.glDeleteShader(shader);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDeleteShader(shader);
        }
    }

    @Override
    public void glDeleteTextures(int n, IntBuffer textures) {
        GLES20.glDeleteTextures(n, textures);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDeleteTextures(n, textures);
        }
    }

    @Override
    public void glDeleteTextures(int n, int[] textures, int offset) {
        GLES20.glDeleteTextures(n, textures, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDeleteTextures(n, textures, offset);
        }
    }

    @Override
    public void glDeleteTexture(int texture) {
        tempInts[0] = texture;
        GLES20.glDeleteTextures(1, tempInts, 0);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDeleteTexture(texture);
        }
    }

    @Override
    public void glDepthFunc(int func) {
        GLES20.glDepthFunc(func);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDepthFunc(func);
        }
    }

    @Override
    public void glDepthMask(boolean flag) {
        GLES20.glDepthMask(flag);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDepthMask(flag);
        }
    }

    @Override
    public void glDepthRangef(float zNear, float zFar) {
        GLES20.glDepthRangef(zNear, zFar);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDepthRangef(zNear, zFar);
        }
    }

    @Override
    public void glDetachShader(int program, int shader) {
        GLES20.glDetachShader(program, shader);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDetachShader(program, shader);
        }
    }

    @Override
    public void glDisable(int cap) {
        GLES20.glDisable(cap);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDisable(cap);
        }
    }

    @Override
    public void glDisableVertexAttribArray(int index) {
        GLES20.glDisableVertexAttribArray(index);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDisableVertexAttribArray(index);
        }
    }

    @Override
    public void glDrawArrays(int mode, int first, int count) {
        GLES20.glDrawArrays(mode, first, count);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDrawArrays(mode, first, count);
        }
    }

    @Override
    public void glDrawElements(int mode, int count, int type, Buffer indices) {
        GLES20.glDrawElements(mode, count, type, indices);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDrawElements(mode, count, type, indices);
        }
    }

    @Override
    public void glDrawElements(int mode, int count, int type, int indices) {
        GLES20.glDrawElements(mode, count, type, indices);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLDrawElements(mode, count, type, indices);
        }
    }

    @Override
    public void glEnable(int cap) {
        GLES20.glEnable(cap);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLEnable(cap);
        }
    }

    @Override
    public void glEnableVertexAttribArray(int index) {
        GLES20.glEnableVertexAttribArray(index);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLEnableVertexAttribArray(index);
        }
    }

    @Override
    public void glFinish() {
        GLES20.glFinish();
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLFinish();
        }
    }

    @Override
    public void glFlush() {
        GLES20.glFlush();
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLFlush();
        }
    }

    @Override
    public void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
        GLES20.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
        }
    }

    @Override
    public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        GLES20.glFramebufferTexture2D(target, attachment, textarget, texture, level);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLFramebufferTexture2D(target, attachment, textarget, texture, level);
        }
    }

    @Override
    public void glFrontFace(int mode) {
        GLES20.glFrontFace(mode);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLFrontFace(mode);
        }
    }

    @Override
    public void glGenBuffers(int n, IntBuffer buffers) {
        GLES20.glGenBuffers(n, buffers);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGenBuffers(n, buffers);
        }
    }

    @Override
    public void glGenBuffers(int n, int[] buffers, int offset) {
        GLES20.glGenBuffers(n, buffers, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGenBuffers(n, buffers, offset);
        }
    }

    @Override
    public int glGenBuffer() {
        tempInts[0] = 0;
        GLES20.glGenBuffers(1, tempInts, 0);
        int id = tempInts[0];
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGenBuffer(id);
        }
        return id;
    }

    @Override
    public void glGenerateMipmap(int target) {
        GLES20.glGenerateMipmap(target);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGenerateMipmap(target);
        }
    }

    @Override
    public void glGenFramebuffers(int n, IntBuffer framebuffers) {
        GLES20.glGenFramebuffers(n, framebuffers);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGenFramebuffers(n, framebuffers);
        }
    }

    @Override
    public void glGenFramebuffers(int n, int[] framebuffers, int offset) {
        GLES20.glGenFramebuffers(n, framebuffers, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGenFramebuffers(n, framebuffers, offset);
        }
    }

    @Override
    public int glGenFramebuffer() {
        tempInts[0] = 0;
        GLES20.glGenFramebuffers(1, tempInts, 0);
        int id = tempInts[0];
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGenFramebuffer(id);
        }
        return id;

    }

    @Override
    public void glGenRenderbuffers(int n, IntBuffer renderbuffers) {
        GLES20.glGenRenderbuffers(n, renderbuffers);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGenRenderbuffers(n, renderbuffers);
        }
    }

    @Override
    public int glGenRenderbuffer() {
        tempInts[0] = 0;
        GLES20.glGenRenderbuffers(1, tempInts, 0);
        int id = tempInts[0];
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGenRenderbuffer(id);
        }
        return id;
    }

    @Override
    public void glGenTextures(int n, IntBuffer textures) {
        GLES20.glGenTextures(n, textures);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGenTextures(n, textures);
        }
    }

    @Override
    public void glGenTextures(int n, int[] textures, int offset) {
        GLES20.glGenTextures(n, textures, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGenTextures(n, textures, offset);
        }
    }

    @Override
    public int glGenTexture() {
        tempInts[0] = 0;
        GLES20.glGenTextures(1, tempInts, 0);
        int id = tempInts[0];
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGenTexture(id);
        }
        return id;
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
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetActiveAttrib(program, index, bufsize, length, lengthOffset, size, sizeOffset, type, typeOffset, name, nameOffset);
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
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetActiveAttrib(program,
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
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetActiveAttrib(program,
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
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetActiveAttrib(program, index, size, type, attrib);
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
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetActiveUniform(program, index, bufsize, length, lengthOffset, size, sizeOffset, type, typeOffset, name, nameOffset);
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
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetActiveUniform(program,
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
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetActiveUniform(program,
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
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetActiveUniform(program, index, size, type, name);
        }
        return name;

    }

    @Override
    public void glGetAttachedShaders(int program, int maxcount, IntBuffer count, IntBuffer shaders) {
        GLES20.glGetAttachedShaders(program, maxcount, count, shaders);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetAttachedShaders(program, maxcount, count, shaders);
        }
    }

    @Override
    public int glGetAttribLocation(int program, String name) {
        int location = GLES20.glGetAttribLocation(program, name);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetAttribLocation(program, name, location);
        }
        return location;

    }

    @Override
    public void glGetBooleanv(int pname, IntBuffer params) {
        GLES20.glGetBooleanv(pname, params);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetBooleanv(pname, params);
        }
    }

    @Override
    public void glGetBufferParameteriv(int target, int pname, IntBuffer params) {
        GLES20.glGetBufferParameteriv(target, pname, params);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetBufferParameteriv(target, pname, params);
        }
    }

    @Override
    public int glGetError() {
        int error = GLES20.glGetError();
        for (GLMonitor monitor : monitorList) {
            monitor.onGLCall();
            if (error != GL20.GL_NO_ERROR) {
                GLException exception = new GLException(error, getErrorString(error));
                monitor.onGLError(exception);
            }
        }
        return error;
    }

    private String getErrorString(int error) {
        String errorString = GLU.gluErrorString(error);
        if (errorString == null) {
            errorString = "unknown error";
        }
        return errorString = "gl " + errorString + " 0x" + Integer.toHexString(error);
    }


    @Override
    public void glGetFloatv(int pname, FloatBuffer params) {
        GLES20.glGetFloatv(pname, params);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetFloatv(pname, params);
        }
    }

    @Override
    public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params) {
        GLES20.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
        }
    }

    @Override
    public int glGetInteger(int pname) {
        tempInts[0] = 0;
        GLES20.glGetIntegerv(pname, tempInts, 0);
        int value = tempInts[0];
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetInteger(pname, value);
        }
        return value;
    }

    @Override
    public void glGetIntegerv(int pname, IntBuffer params) {
        GLES20.glGetIntegerv(pname, params);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetIntegerv(pname, params);
        }
    }

    @Override
    public void glGetIntegerv(int pname, int[] params, int offset) {
        GLES20.glGetIntegerv(pname, params, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetIntegerv(pname, params, offset);
        }
    }

    @Override
    public void glGetProgramiv(int program, int pname, IntBuffer params) {
        GLES20.glGetProgramiv(program, pname, params);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetProgramiv(program, pname, params);
        }
    }

    @Override
    public void glGetProgramiv(int program,
                               int pname,
                               int[] params,
                               int offset) {
        GLES20.glGetProgramiv(program, pname, params, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetProgramiv(program, pname, params, offset);
        }
    }

    @Override
    public int glGetProgram(int program, int pname) {
        tempInts[0] = 0;
        GLES20.glGetProgramiv(program, pname, tempInts, 0);
        int value = tempInts[0];
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetProgram(program, pname, value);
        }
        return value;
    }

    @Override
    public String glGetProgramInfoLog(int program) {
        String log = GLES20.glGetProgramInfoLog(program);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetProgramInfoLog(program, log);
        }
        return log;
    }

    @Override
    public void glGetRenderbufferParameteriv(int target, int pname, IntBuffer params) {
        GLES20.glGetRenderbufferParameteriv(target, pname, params);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetRenderbufferParameteriv(target, pname, params);
        }
    }

    @Override
    public void glGetShaderiv(int shader, int pname, IntBuffer params) {
        GLES20.glGetShaderiv(shader, pname, params);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetShaderiv(shader, pname, params);
        }
    }

    @Override
    public void glGetShaderiv(int shader, int pname, int[] params, int offset) {
        GLES20.glGetShaderiv(shader, pname, params, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetShaderiv(shader, pname, params, offset);
        }
    }

    @Override
    public int glGetShaderiv(int shader, int pname) {
        tempInts[0] = 0;
        GLES20.glGetShaderiv(shader, pname, tempInts, 0);
        int value = tempInts[0];
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetShaderiv(shader, pname, value);
        }
        return value;
    }

    @Override
    public String glGetShaderInfoLog(int shader) {
        String log = GLES20.glGetShaderInfoLog(shader);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetShaderInfoLog(shader, log);
        }
        return log;

    }

    @Override
    public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range, IntBuffer precision) {
        GLES20.glGetShaderPrecisionFormat(shadertype, precisiontype, range, precision);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetShaderPrecisionFormat(shadertype, precisiontype, range, precision);
        }
    }

    @Override
    public String glGetString(int name) {
        String value = GLES20.glGetString(name);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetString(name, value);
        }
        return value;

    }

    @Override
    public void glGetTexParameterfv(int target, int pname, FloatBuffer params) {
        GLES20.glGetTexParameterfv(target, pname, params);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetTexParameterfv(target, pname, params);
        }
    }

    @Override
    public void glGetTexParameteriv(int target, int pname, IntBuffer params) {
        GLES20.glGetTexParameteriv(target, pname, params);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetTexParameteriv(target, pname, params);
        }
    }

    @Override
    public void glGetUniformfv(int program, int location, FloatBuffer params) {
        GLES20.glGetUniformfv(program, location, params);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetUniformfv(program, location, params);
        }
    }

    @Override
    public void glGetUniformiv(int program, int location, IntBuffer params) {
        GLES20.glGetUniformiv(program, location, params);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetUniformiv(program, location, params);
        }
    }

    @Override
    public int glGetUniformLocation(int program, String name) {
        int location = GLES20.glGetUniformLocation(program, name);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetUniformLocation(program, name, location);
        }
        return location;

    }

    @Override
    public void glGetVertexAttribfv(int index, int pname, FloatBuffer params) {
        GLES20.glGetVertexAttribfv(index, pname, params);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetVertexAttribfv(index, pname, params);
        }
    }

    @Override
    public void glGetVertexAttribiv(int index, int pname, IntBuffer params) {
        GLES20.glGetVertexAttribiv(index, pname, params);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLGetVertexAttribiv(index, pname, params);
        }
    }

    @Override
    public void glHint(int target, int mode) {
        GLES20.glHint(target, mode);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLHint(target, mode);
        }
    }

    @Override
    public boolean glIsBuffer(int buffer) {
        boolean isBuffer = GLES20.glIsBuffer(buffer);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLIsBuffer(buffer, isBuffer);
        }
        return isBuffer;
    }

    @Override
    public boolean glIsEnabled(int cap) {
        boolean isEnabled = GLES20.glIsEnabled(cap);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLIsEnabled(cap, isEnabled);
        }
        return isEnabled;
    }

    @Override
    public boolean glIsFramebuffer(int framebuffer) {
        boolean isFramebuffer = GLES20.glIsFramebuffer(framebuffer);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLIsFramebuffer(framebuffer, isFramebuffer);
        }
        return isFramebuffer;
    }

    @Override
    public boolean glIsProgram(int program) {
        boolean isProgram = GLES20.glIsProgram(program);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLIsProgram(program, isProgram);
        }
        return isProgram;

    }

    @Override
    public boolean glIsRenderbuffer(int renderbuffer) {
        boolean isRenderbuffer = GLES20.glIsRenderbuffer(renderbuffer);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLIsRenderbuffer(renderbuffer, isRenderbuffer);
        }
        return isRenderbuffer;

    }

    @Override
    public boolean glIsShader(int shader) {
        boolean isShader = GLES20.glIsShader(shader);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLIsShader(shader, isShader);
        }
        return isShader;

    }

    @Override
    public boolean glIsTexture(int texture) {
        boolean isTexture = GLES20.glIsTexture(texture);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLIsTexture(texture, isTexture);
        }
        return isTexture;

    }

    @Override
    public void glLineWidth(float width) {
        GLES20.glLineWidth(width);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLLineWidth(width);
        }
    }

    @Override
    public void glLinkProgram(int program) {
        GLES20.glLinkProgram(program);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLLinkProgram(program);
        }
    }

    @Override
    public void glPixelStorei(int pname, int param) {
        GLES20.glPixelStorei(pname, param);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLPixelStorei(pname, param);
        }
    }

    @Override
    public void glPolygonOffset(float factor, float units) {
        GLES20.glPolygonOffset(factor, units);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLPolygonOffset(factor, units);
        }
    }

    @Override
    public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
        GLES20.glReadPixels(x, y, width, height, format, type, pixels);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLReadPixels(x, y, width, height, format, type, pixels);
        }
    }

    @Override
    public void glReleaseShaderCompiler() {
        GLES20.glReleaseShaderCompiler();
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLReleaseShaderCompiler();
        }
    }

    @Override
    public void glRenderbufferStorage(int target, int internalformat, int width, int height) {
        GLES20.glRenderbufferStorage(target, internalformat, width, height);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLRenderbufferStorage(target, internalformat, width, height);
        }
    }

    @Override
    public void glSampleCoverage(float value, boolean invert) {
        GLES20.glSampleCoverage(value, invert);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLSampleCoverage(value, invert);
        }
    }

    @Override
    public void glScissor(int x, int y, int width, int height) {
        GLES20.glScissor(x, y, width, height);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLScissor(x, y, width, height);
        }
    }

    @Override
    public void glShaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
        GLES20.glShaderBinary(n, shaders, binaryformat, binary, length);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLShaderBinary(n, shaders, binaryformat, binary, length);
        }
    }

    @Override
    public void glShaderSource(int shader, String string) {
        GLES20.glShaderSource(shader, string);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLShaderSource(shader, string);
        }
    }

    @Override
    public void glStencilFunc(int func, int ref, int mask) {
        GLES20.glStencilFunc(func, ref, mask);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLStencilFunc(func, ref, mask);
        }
    }

    @Override
    public void glStencilFuncSeparate(int face, int func, int ref, int mask) {
        GLES20.glStencilFuncSeparate(face, func, ref, mask);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLStencilFuncSeparate(face, func, ref, mask);
        }
    }

    @Override
    public void glStencilMask(int mask) {
        GLES20.glStencilMask(mask);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLStencilMask(mask);
        }
    }

    @Override
    public void glStencilMaskSeparate(int face, int mask) {
        GLES20.glStencilMaskSeparate(face, mask);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLStencilMaskSeparate(face, mask);
        }
    }

    @Override
    public void glStencilOp(int fail, int zfail, int zpass) {
        GLES20.glStencilOp(fail, zfail, zpass);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLStencilOp(fail, zfail, zpass);
        }
    }

    @Override
    public void glStencilOpSeparate(int face, int fail, int zfail, int zpass) {
        GLES20.glStencilOpSeparate(face, fail, zfail, zpass);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLStencilOpSeparate(face, fail, zfail, zpass);
        }
    }

    @Override
    public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type,
                             Buffer pixels) {
        GLES20.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
        }
    }

    @Override
    public void glTexParameterf(int target, int pname, float param) {
        GLES20.glTexParameterf(target, pname, param);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLTexParameterf(target, pname, param);
        }
    }

    @Override
    public void glTexParameterfv(int target, int pname, FloatBuffer params) {
        GLES20.glTexParameterfv(target, pname, params);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLTexParameterfv(target, pname, params);
        }
    }

    @Override
    public void glTexParameteri(int target, int pname, int param) {
        GLES20.glTexParameteri(target, pname, param);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLTexParameteri(target, pname, param);
        }
    }

    @Override
    public void glTexParameteriv(int target, int pname, IntBuffer params) {
        GLES20.glTexParameteriv(target, pname, params);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLTexParameteriv(target, pname, params);
        }
    }

    @Override
    public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type,
                                Buffer pixels) {
        GLES20.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
        }
    }

    @Override
    public void glUniform1f(int location, float x) {
        GLES20.glUniform1f(location, x);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform1f(location, x);
        }
    }

    @Override
    public void glUniform1fv(int location, int count, FloatBuffer v) {
        GLES20.glUniform1fv(location, count, v);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform1fv(location, count, v);
        }
    }

    @Override
    public void glUniform1fv(int location, int count, float[] v, int offset) {
        GLES20.glUniform1fv(location, count, v, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform1fv(location, count, v, offset);
        }
    }

    @Override
    public void glUniform1i(int location, int x) {
        GLES20.glUniform1i(location, x);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform1i(location, x);
        }
    }

    @Override
    public void glUniform1iv(int location, int count, IntBuffer v) {
        GLES20.glUniform1iv(location, count, v);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform1iv(location, count, v);
        }
    }

    @Override
    public void glUniform1iv(int location, int count, int[] v, int offset) {
        GLES20.glUniform1iv(location, count, v, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform1iv(location, count, v, offset);
        }
    }

    @Override
    public void glUniform2f(int location, float x, float y) {
        GLES20.glUniform2f(location, x, y);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform2f(location, x, y);
        }
    }

    @Override
    public void glUniform2fv(int location, int count, FloatBuffer v) {
        GLES20.glUniform2fv(location, count, v);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform2fv(location, count, v);
        }
    }

    @Override
    public void glUniform2fv(int location, int count, float[] v, int offset) {
        GLES20.glUniform2fv(location, count, v, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform2fv(location, count, v, offset);
        }
    }

    @Override
    public void glUniform2i(int location, int x, int y) {
        GLES20.glUniform2i(location, x, y);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform2i(location, x, y);
        }
    }

    @Override
    public void glUniform2iv(int location, int count, IntBuffer v) {
        GLES20.glUniform2iv(location, count, v);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform2iv(location, count, v);
        }
    }

    @Override
    public void glUniform2iv(int location, int count, int[] v, int offset) {
        GLES20.glUniform2iv(location, count, v, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform2iv(location, count, v, offset);
        }
    }

    @Override
    public void glUniform3f(int location, float x, float y, float z) {
        GLES20.glUniform3f(location, x, y, z);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform3f(location, x, y, z);
        }
    }

    @Override
    public void glUniform3fv(int location, int count, FloatBuffer v) {
        GLES20.glUniform3fv(location, count, v);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform3fv(location, count, v);
        }
    }

    @Override
    public void glUniform3fv(int location, int count, float[] v, int offset) {
        GLES20.glUniform3fv(location, count, v, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform3fv(location, count, v, offset);
        }
    }

    @Override
    public void glUniform3i(int location, int x, int y, int z) {
        GLES20.glUniform3i(location, x, y, z);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform3i(location, x, y, z);
        }
    }

    @Override
    public void glUniform3iv(int location, int count, IntBuffer v) {
        GLES20.glUniform3iv(location, count, v);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform3iv(location, count, v);
        }
    }

    @Override
    public void glUniform3iv(int location, int count, int[] v, int offset) {
        GLES20.glUniform3iv(location, count, v, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform3iv(location, count, v, offset);
        }
    }

    @Override
    public void glUniform4f(int location, float x, float y, float z, float w) {
        GLES20.glUniform4f(location, x, y, z, w);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform4f(location, x, y, z, w);
        }
    }

    @Override
    public void glUniform4fv(int location, int count, FloatBuffer v) {
        GLES20.glUniform4fv(location, count, v);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform4fv(location, count, v);
        }
    }

    @Override
    public void glUniform4fv(int location, int count, float[] v, int offset) {
        GLES20.glUniform4fv(location, count, v, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform4fv(location, count, v, offset);
        }
    }

    @Override
    public void glUniform4i(int location, int x, int y, int z, int w) {
        GLES20.glUniform4i(location, x, y, z, w);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform4i(location, x, y, z, w);
        }
    }

    @Override
    public void glUniform4iv(int location, int count, IntBuffer v) {
        GLES20.glUniform4iv(location, count, v);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform4iv(location, count, v);
        }
    }

    @Override
    public void glUniform4iv(int location, int count, int[] v, int offset) {
        GLES20.glUniform4iv(location, count, v, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniform4iv(location, count, v, offset);
        }
    }

    @Override
    public void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {
        GLES20.glUniformMatrix2fv(location, count, transpose, value);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniformMatrix2fv(location, count, transpose, value);
        }
    }

    @Override
    public void glUniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset) {
        GLES20.glUniformMatrix2fv(location, count, transpose, value, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniformMatrix2fv(location, count, transpose, value, offset);
        }
    }

    @Override
    public void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {
        GLES20.glUniformMatrix3fv(location, count, transpose, value);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniformMatrix3fv(location, count, transpose, value);
        }
    }

    @Override
    public void glUniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset) {
        GLES20.glUniformMatrix3fv(location, count, transpose, value, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniformMatrix3fv(location, count, transpose, value, offset);
        }
    }

    @Override
    public void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {
        GLES20.glUniformMatrix4fv(location, count, transpose, value);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniformMatrix4fv(location, count, transpose, value);
        }
    }

    @Override
    public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {
        GLES20.glUniformMatrix4fv(location, count, transpose, value, offset);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUniformMatrix4fv(location, count, transpose, value, offset);
        }
    }

    @Override
    public void glUseProgram(int program) {
        GLES20.glUseProgram(program);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLUseProgram(program);
        }
    }

    @Override
    public void glValidateProgram(int program) {
        GLES20.glValidateProgram(program);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLValidateProgram(program);
        }
    }

    @Override
    public void glVertexAttrib1f(int indx, float x) {
        GLES20.glVertexAttrib1f(indx, x);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLVertexAttrib1f(indx, x);
        }
    }

    @Override
    public void glVertexAttrib1fv(int indx, FloatBuffer values) {
        GLES20.glVertexAttrib1fv(indx, values);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLVertexAttrib1fv(indx, values);
        }
    }

    @Override
    public void glVertexAttrib2f(int indx, float x, float y) {
        GLES20.glVertexAttrib2f(indx, x, y);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLVertexAttrib2f(indx, x, y);
        }
    }

    @Override
    public void glVertexAttrib2fv(int indx, FloatBuffer values) {
        GLES20.glVertexAttrib2fv(indx, values);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLVertexAttrib2fv(indx, values);
        }
    }

    @Override
    public void glVertexAttrib3f(int indx, float x, float y, float z) {
        GLES20.glVertexAttrib3f(indx, x, y, z);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLVertexAttrib3f(indx, x, y, z);
        }
    }

    @Override
    public void glVertexAttrib3fv(int indx, FloatBuffer values) {
        GLES20.glVertexAttrib3fv(indx, values);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLVertexAttrib3fv(indx, values);
        }
    }

    @Override
    public void glVertexAttrib4f(int indx, float x, float y, float z, float w) {
        GLES20.glVertexAttrib4f(indx, x, y, z, w);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLVertexAttrib4f(indx, x, y, z, w);
        }
    }

    @Override
    public void glVertexAttrib4fv(int indx, FloatBuffer values) {
        GLES20.glVertexAttrib4fv(indx, values);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLVertexAttrib4fv(indx, values);
        }
    }

    @Override
    public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr) {
        GLES20.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLVertexAttribPointer(indx, size, type, normalized, stride, ptr);
        }
    }

    @Override
    public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int ptr) {
        GLES20.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLVertexAttribPointer(indx, size, type, normalized, stride, ptr);
        }
    }

    @Override
    public void glViewport(int x, int y, int width, int height) {
        GLES20.glViewport(x, y, width, height);
        onGLCallback();
        for (GL20Monitor gl20Monitor : monitor20List) {
            gl20Monitor.onGLViewport(x, y, width, height);
        }
    }

    @Override
    public void addMonitor(GLMonitor monitor) {
        if (monitor == null || monitorList.contains(monitor)) {
            return;
        }
        monitorList.add(monitor);
        if (monitor instanceof GL20Monitor) {
            monitor20List.add((GL20Monitor) monitor);
        }
    }


    @Override
    public void removeMonitor(GLMonitor monitor) {
        if (monitor == null || !monitorList.contains(monitor)) {
            return;
        }
        monitorList.remove(monitor);
        if (monitor instanceof GL20Monitor) {
            monitor20List.remove(monitor);
        }
    }

    private void onGLCallback() {
        for (GLMonitor glMonitor : monitorList) {
            glMonitor.onGLCall();
        }
    }
}
