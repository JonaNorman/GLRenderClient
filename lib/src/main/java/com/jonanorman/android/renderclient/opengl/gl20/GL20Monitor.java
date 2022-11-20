package com.jonanorman.android.renderclient.opengl.gl20;

import com.jonanorman.android.renderclient.opengl.GL;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface GL20Monitor extends GL.GLMonitor {


    void onGLActiveTexture(int texture);


    void onGLAttachShader(int program, int shader);


    void onGLBindAttribLocation(int program, int index, String name);


    void onGLBindBuffer(int target, int buffer);


    void onGLBindFramebuffer(int target, int framebuffer);


    void onGLBindRenderbuffer(int target, int renderbuffer);


    void onGLBindTexture(int target, int texture);


    void onGLBlendColor(float red, float green, float blue, float alpha);


    void onGLBlendEquation(int mode);


    void onGLBlendEquationSeparate(int modeRGB, int modeAlpha);


    void onGLBlendFunc(int sfactor, int dfactor);


    void onGLBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha);


    void onGLBufferData(int target, int size, Buffer data, int usage);


    void onGLBufferSubData(int target, int offset, int size, Buffer data);


    void onGLCheckFramebufferStatus(int target, int status);


    void onGLClear(int mask);


    void onGLClearColor(float red, float green, float blue, float alpha);


    void onGLClearDepthf(float depth);


    void onGLClearStencil(int s);


    void onGLColorMask(boolean red, boolean green, boolean blue, boolean alpha);


    void onGLCompileShader(int shader);


    void onGLCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border,
                                  int imageSize, Buffer data);


    void onGLCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format,
                                     int imageSize, Buffer data);


    void onGLCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border);


    void onGLCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height);


    void onGLCreateProgram(int programId);


    void onGLCreateShader(int type, int shaderId);


    void onGLCullFace(int mode);


    void onGLDeleteBuffers(int n, IntBuffer buffers);


    void onGLDeleteBuffers(int n,
                           int[] buffers,
                           int offset);


    void onGLDeleteBuffer(int buffer);


    void onGLDeleteFramebuffers(int n, IntBuffer framebuffers);


    void onGLDeleteFramebuffers(int n, int[] framebuffers, int offset);


    void onGLDeleteFramebuffer(int framebuffer);


    void onGLDeleteProgram(int program);


    void onGLDeleteRenderbuffers(int n, IntBuffer renderbuffers);


    void onGLDeleteRenderbuffers(int n, int[] renderbuffers, int offset);


    void onGLDeleteRenderbuffer(int renderbuffer);


    void onGLDeleteShader(int shader);


    void onGLDeleteTextures(int n, IntBuffer textures);


    void onGLDeleteTextures(int n, int[] textures, int offset);


    void onGLDeleteTexture(int texture);


    void onGLDepthFunc(int func);


    void onGLDepthMask(boolean flag);


    void onGLDepthRangef(float zNear, float zFar);


    void onGLDetachShader(int program, int shader);


    void onGLDisable(int cap);


    void onGLDisableVertexAttribArray(int index);


    void onGLDrawArrays(int mode, int first, int count);


    void onGLDrawElements(int mode, int count, int type, Buffer indices);


    void onGLDrawElements(int mode, int count, int type, int indices);


    void onGLEnable(int cap);


    void onGLEnableVertexAttribArray(int index);


    void onGLFinish();


    void onGLFlush();


    void onGLFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer);


    void onGLFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level);


    void onGLFrontFace(int mode);


    void onGLGenBuffers(int n, IntBuffer buffers);


    void onGLGenBuffers(int n, int[] buffers, int offset);


    void onGLGenBuffer(int bufferId);


    void onGLGenerateMipmap(int target);


    void onGLGenFramebuffers(int n, IntBuffer framebuffers);


    void onGLGenFramebuffers(int n, int[] framebuffers, int offset);


    void onGLGenFramebuffer(int frameBufferId);


    void onGLGenRenderbuffers(int n, IntBuffer renderbuffers);


    void onGLGenRenderbuffer(int renderBufferId);


    void onGLGenTextures(int n, IntBuffer textures);


    void onGLGenTextures(int n, int[] textures, int offset);


    void onGLGenTexture(int textureId);


    void onGLGetActiveAttrib(
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
            int nameOffset);


    void onGLGetActiveAttrib(
            int program,
            int index,
            int[] size,
            int sizeOffset,
            int[] type,
            int typeOffset, String attribute
    );


    void onGLGetActiveAttrib(
            int program,
            int index,
            IntBuffer size,
            IntBuffer type, String attribute);


    void onGLGetActiveAttrib(int program, int index, int[] size, int[] type, String name);


    void onGLGetActiveUniform(
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
            int nameOffset);


    void onGLGetActiveUniform(
            int program,
            int index,
            int[] size,
            int sizeOffset,
            int[] type,
            int typeOffset, String name
    );


    void onGLGetActiveUniform(
            int program,
            int index,
            IntBuffer size,
            IntBuffer type, String name);


    void onGLGetActiveUniform(int program, int index, int[] size, int[] type, String name);


    void onGLGetAttachedShaders(int program, int maxcount, IntBuffer count, IntBuffer shaders);


    void onGLGetAttribLocation(int program, String name, int location);


    void onGLGetBooleanv(int pname, IntBuffer params);


    void onGLGetBufferParameteriv(int target, int pname, IntBuffer params);


    void onGLGetFloatv(int pname, FloatBuffer params);


    void onGLGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params);


    void onGLGetInteger(int pname, int value);


    void onGLGetIntegerv(int pname, IntBuffer params);


    void onGLGetIntegerv(int pname, int[] params, int offset);


    void onGLGetProgramiv(int program, int pname, IntBuffer params);


    void onGLGetProgramiv(int program,
                          int pname,
                          int[] params,
                          int offset);


    void onGLGetProgram(int program,
                        int pname, int value);


    void onGLGetProgramInfoLog(int program, String log);


    void onGLGetRenderbufferParameteriv(int target, int pname, IntBuffer params);


    void onGLGetShaderiv(int shader, int pname, IntBuffer params);


    void onGLGetShaderiv(int shader, int pname, int[] params, int offset);


    void onGLGetShaderiv(int shader, int pname, int value);


    void onGLGetShaderInfoLog(int shader, String log);


    void onGLGetShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range, IntBuffer precision);


    void onGLGetString(int name, String value);


    void onGLGetTexParameterfv(int target, int pname, FloatBuffer params);


    void onGLGetTexParameteriv(int target, int pname, IntBuffer params);


    void onGLGetUniformfv(int program, int location, FloatBuffer params);


    void onGLGetUniformiv(int program, int location, IntBuffer params);


    void onGLGetUniformLocation(int program, String name, int location);


    void onGLGetVertexAttribfv(int index, int pname, FloatBuffer params);


    void onGLGetVertexAttribiv(int index, int pname, IntBuffer params);


    void onGLHint(int target, int mode);


    void onGLIsBuffer(int buffer, boolean isBuffer);


    void onGLIsEnabled(int cap, boolean isEnable);


    void onGLIsFramebuffer(int framebuffer, boolean isFrameBuffer);


    void onGLIsProgram(int program, boolean isProgram);


    void onGLIsRenderbuffer(int renderbuffer, boolean isRenderBuffer);


    void onGLIsShader(int shader, boolean isShader);


    void onGLIsTexture(int texture, boolean isTexture);


    void onGLLineWidth(float width);


    void onGLLinkProgram(int program);


    void onGLPixelStorei(int pname, int param);


    void onGLPolygonOffset(float factor, float units);


    void onGLReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels);


    void onGLReleaseShaderCompiler();


    void onGLRenderbufferStorage(int target, int internalformat, int width, int height);


    void onGLSampleCoverage(float value, boolean invert);


    void onGLScissor(int x, int y, int width, int height);


    void onGLShaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length);


    void onGLShaderSource(int shader, String string);


    void onGLStencilFunc(int func, int ref, int mask);


    void onGLStencilFuncSeparate(int face, int func, int ref, int mask);


    void onGLStencilMask(int mask);


    void onGLStencilMaskSeparate(int face, int mask);


    void onGLStencilOp(int fail, int zfail, int zpass);


    void onGLStencilOpSeparate(int face, int fail, int zfail, int zpass);


    void onGLTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type,
                        Buffer pixels);


    void onGLTexParameterf(int target, int pname, float param);


    void onGLTexParameterfv(int target, int pname, FloatBuffer params);


    void onGLTexParameteri(int target, int pname, int param);


    void onGLTexParameteriv(int target, int pname, IntBuffer params);


    void onGLTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type,
                           Buffer pixels);


    void onGLUniform1f(int location, float x);


    void onGLUniform1fv(int location, int count, FloatBuffer v);


    void onGLUniform1fv(int location, int count, float[] v, int offset);


    void onGLUniform1i(int location, int x);


    void onGLUniform1iv(int location, int count, IntBuffer v);


    void onGLUniform1iv(int location, int count, int[] v, int offset);


    void onGLUniform2f(int location, float x, float y);


    void onGLUniform2fv(int location, int count, FloatBuffer v);


    void onGLUniform2fv(int location, int count, float[] v, int offset);


    void onGLUniform2i(int location, int x, int y);


    void onGLUniform2iv(int location, int count, IntBuffer v);


    void onGLUniform2iv(int location, int count, int[] v, int offset);


    void onGLUniform3f(int location, float x, float y, float z);


    void onGLUniform3fv(int location, int count, FloatBuffer v);


    void onGLUniform3fv(int location, int count, float[] v, int offset);


    void onGLUniform3i(int location, int x, int y, int z);


    void onGLUniform3iv(int location, int count, IntBuffer v);


    void onGLUniform3iv(int location, int count, int[] v, int offset);


    void onGLUniform4f(int location, float x, float y, float z, float w);


    void onGLUniform4fv(int location, int count, FloatBuffer v);


    void onGLUniform4fv(int location, int count, float[] v, int offset);


    void onGLUniform4i(int location, int x, int y, int z, int w);


    void onGLUniform4iv(int location, int count, IntBuffer v);


    void onGLUniform4iv(int location, int count, int[] v, int offset);


    void onGLUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value);


    void onGLUniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset);


    void onGLUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value);


    void onGLUniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset);


    void onGLUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value);


    void onGLUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset);


    void onGLUseProgram(int program);


    void onGLValidateProgram(int program);


    void onGLVertexAttrib1f(int indx, float x);


    void onGLVertexAttrib1fv(int indx, FloatBuffer values);


    void onGLVertexAttrib2f(int indx, float x, float y);


    void onGLVertexAttrib2fv(int indx, FloatBuffer values);


    void onGLVertexAttrib3f(int indx, float x, float y, float z);


    void onGLVertexAttrib3fv(int indx, FloatBuffer values);


    void onGLVertexAttrib4f(int indx, float x, float y, float z, float w);


    void onGLVertexAttrib4fv(int indx, FloatBuffer values);


    void onGLVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr);


    void onGLVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int ptr);


    void onGLViewport(int x, int y, int width, int height);

}