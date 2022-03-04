package com.jonanorman.android.renderclient;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface GL20Monitor {


    default void glActiveTexture(int texture) {

    }

    default void glAttachShader(int program, int shader) {

    }

    default void glBindAttribLocation(int program, int index, String name) {

    }

    default void glBindBuffer(int target, int buffer) {

    }

    default void glBindFramebuffer(int target, int framebuffer) {

    }

    default void glBindRenderbuffer(int target, int renderbuffer) {

    }

    default void glBindTexture(int target, int texture) {

    }

    default void glBlendColor(float red, float green, float blue, float alpha) {

    }

    default void glBlendEquation(int mode) {

    }

    default void glBlendEquationSeparate(int modeRGB, int modeAlpha) {

    }

    default void glBlendFunc(int sfactor, int dfactor) {

    }

    default void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {

    }

    default void glBufferData(int target, int size, Buffer data, int usage) {

    }

    default void glBufferSubData(int target, int offset, int size, Buffer data) {

    }

    default void glCheckFramebufferStatus(int target, int status) {

    }

    default void glClear(int mask) {

    }

    default void glClearColor(float red, float green, float blue, float alpha) {

    }

    default void glClearDepthf(float depth) {

    }

    default void glClearStencil(int s) {

    }

    default void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {

    }

    default void glCompileShader(int shader) {

    }

    default void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border,
                                        int imageSize, Buffer data) {

    }

    default void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format,
                                           int imageSize, Buffer data) {

    }

    default void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border) {

    }

    default void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {

    }

    default void glCreateProgram(int programId) {

    }

    default void glCreateShader(int type, int shaderId) {

    }

    default void glCullFace(int mode) {

    }

    default void glDeleteBuffers(int n, IntBuffer buffers) {

    }

    default void glDeleteBuffers(int n,
                                 int[] buffers,
                                 int offset) {

    }

    default void glDeleteBuffer(int buffer) {

    }

    default void glDeleteFramebuffers(int n, IntBuffer framebuffers) {

    }

    default void glDeleteFramebuffers(int n, int[] framebuffers, int offset) {

    }

    default void glDeleteFramebuffer(int framebuffer) {

    }

    default void glDeleteProgram(int program) {

    }

    default void glDeleteRenderbuffers(int n, IntBuffer renderbuffers) {

    }

    default void glDeleteRenderbuffers(int n, int[] renderbuffers, int offset) {

    }

    default void glDeleteRenderbuffer(int renderbuffer) {

    }

    default void glDeleteShader(int shader) {

    }

    default void glDeleteTextures(int n, IntBuffer textures) {

    }

    default void glDeleteTextures(int n, int[] textures, int offset) {

    }

    default void glDeleteTexture(int texture) {

    }

    default void glDepthFunc(int func) {

    }

    default void glDepthMask(boolean flag) {

    }

    default void glDepthRangef(float zNear, float zFar) {

    }

    default void glDetachShader(int program, int shader) {

    }

    default void glDisable(int cap) {

    }

    default void glDisableVertexAttribArray(int index) {

    }

    default void glDrawArrays(int mode, int first, int count) {

    }

    default void glDrawElements(int mode, int count, int type, Buffer indices) {

    }

    default void glDrawElements(int mode, int count, int type, int indices) {

    }

    default void glEnable(int cap) {

    }

    default void glEnableVertexAttribArray(int index) {

    }

    default void glFinish() {

    }

    default void glFlush() {

    }

    default void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {

    }

    default void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {

    }

    default void glFrontFace(int mode) {

    }

    default void glGenBuffers(int n, IntBuffer buffers) {

    }

    default void glGenBuffers(int n, int[] buffers, int offset) {

    }

    default void glGenBuffer(int bufferId) {

    }

    default void glGenerateMipmap(int target) {

    }

    default void glGenFramebuffers(int n, IntBuffer framebuffers) {

    }

    default void glGenFramebuffers(int n, int[] framebuffers, int offset) {

    }

    default void glGenFramebuffer(int frameBufferId) {

    }

    default void glGenRenderbuffers(int n, IntBuffer renderbuffers) {

    }

    default void glGenRenderbuffer(int renderBufferId) {

    }

    default void glGenTextures(int n, IntBuffer textures) {

    }

    default void glGenTextures(int n, int[] textures, int offset) {

    }

    default void glGenTexture(int textureId) {

    }

    default void glGetActiveAttrib(
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

    }

    default void glGetActiveAttrib(
            int program,
            int index,
            int[] size,
            int sizeOffset,
            int[] type,
            int typeOffset, String attribute
    ) {

    }

    default void glGetActiveAttrib(
            int program,
            int index,
            IntBuffer size,
            IntBuffer type, String attribute) {

    }

    default void glGetActiveAttrib(int program, int index, int[] size, int[] type, String name) {

    }

    default void glGetActiveUniform(
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

    }

    default void glGetActiveUniform(
            int program,
            int index,
            int[] size,
            int sizeOffset,
            int[] type,
            int typeOffset, String name
    ) {

    }

    default void glGetActiveUniform(
            int program,
            int index,
            IntBuffer size,
            IntBuffer type, String name) {

    }

    default void glGetActiveUniform(int program, int index, int[] size, int[] type, String name) {

    }

    default void glGetAttachedShaders(int program, int maxcount, IntBuffer count, IntBuffer shaders) {

    }

    default void glGetAttribLocation(int program, String name, int location) {

    }

    default void glGetBooleanv(int pname, IntBuffer params) {

    }

    default void glGetBufferParameteriv(int target, int pname, IntBuffer params) {

    }

    default void glGetError(int error) {

    }

    default void glGetFloatv(int pname, FloatBuffer params) {

    }

    default void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params) {

    }

    default void glGetInteger(int pname, int value) {

    }
    default void glGetIntegerv(int pname, IntBuffer params) {

    }

    default void glGetIntegerv(int pname, int[] params, int offset) {

    }

    default void glGetProgramiv(int program, int pname, IntBuffer params) {

    }

    default void glGetProgramiv(int program,
                                int pname,
                                int[] params,
                                int offset) {

    }

    default void glGetProgram(int program,
                              int pname, int value) {

    }

    default void glGetProgramInfoLog(int program, String log) {

    }

    default void glGetRenderbufferParameteriv(int target, int pname, IntBuffer params) {

    }

    default void glGetShaderiv(int shader, int pname, IntBuffer params) {

    }

    default void glGetShaderiv(int shader, int pname, int[] params, int offset) {

    }

    default void glGetShaderiv(int shader, int pname, int value) {

    }

    default void glGetShaderInfoLog(int shader, String log) {

    }

    default void glGetShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range, IntBuffer precision) {

    }

    default void glGetString(int name, String value) {

    }

    default void glGetTexParameterfv(int target, int pname, FloatBuffer params) {

    }

    default void glGetTexParameteriv(int target, int pname, IntBuffer params) {

    }

    default void glGetUniformfv(int program, int location, FloatBuffer params) {

    }

    default void glGetUniformiv(int program, int location, IntBuffer params) {

    }

    default void glGetUniformLocation(int program, String name, int location) {

    }

    default void glGetVertexAttribfv(int index, int pname, FloatBuffer params) {

    }

    default void glGetVertexAttribiv(int index, int pname, IntBuffer params) {

    }

    default void glHint(int target, int mode) {

    }

    default void glIsBuffer(int buffer, boolean isBuffer) {

    }

    default void glIsEnabled(int cap, boolean isEnable) {

    }

    default void glIsFramebuffer(int framebuffer, boolean isFrameBuffer) {

    }

    default void glIsProgram(int program, boolean isProgram) {

    }

    default void glIsRenderbuffer(int renderbuffer, boolean isRenderBuffer) {

    }

    default void glIsShader(int shader, boolean isShader) {

    }

    default void glIsTexture(int texture, boolean isTexture) {

    }

    default void glLineWidth(float width) {

    }

    default void glLinkProgram(int program) {

    }

    default void glPixelStorei(int pname, int param) {

    }

    default void glPolygonOffset(float factor, float units) {

    }

    default void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {

    }

    default void glReleaseShaderCompiler() {

    }

    default void glRenderbufferStorage(int target, int internalformat, int width, int height) {

    }

    default void glSampleCoverage(float value, boolean invert) {

    }

    default void glScissor(int x, int y, int width, int height) {

    }

    default void glShaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {

    }

    default void glShaderSource(int shader, String string) {

    }

    default void glStencilFunc(int func, int ref, int mask) {

    }

    default void glStencilFuncSeparate(int face, int func, int ref, int mask) {

    }

    default void glStencilMask(int mask) {

    }

    default void glStencilMaskSeparate(int face, int mask) {

    }

    default void glStencilOp(int fail, int zfail, int zpass) {

    }

    default void glStencilOpSeparate(int face, int fail, int zfail, int zpass) {

    }

    default void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type,
                              Buffer pixels) {

    }

    default void glTexParameterf(int target, int pname, float param) {

    }

    default void glTexParameterfv(int target, int pname, FloatBuffer params) {

    }

    default void glTexParameteri(int target, int pname, int param) {

    }

    default void glTexParameteriv(int target, int pname, IntBuffer params) {

    }

    default void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type,
                                 Buffer pixels) {

    }

    default void glUniform1f(int location, float x) {

    }

    default void glUniform1fv(int location, int count, FloatBuffer v) {

    }

    default void glUniform1fv(int location, int count, float[] v, int offset) {

    }

    default void glUniform1i(int location, int x) {

    }

    default void glUniform1iv(int location, int count, IntBuffer v) {

    }

    default void glUniform1iv(int location, int count, int[] v, int offset) {

    }

    default void glUniform2f(int location, float x, float y) {

    }

    default void glUniform2fv(int location, int count, FloatBuffer v) {

    }

    default void glUniform2fv(int location, int count, float[] v, int offset) {

    }

    default void glUniform2i(int location, int x, int y) {

    }

    default void glUniform2iv(int location, int count, IntBuffer v) {

    }

    default void glUniform2iv(int location, int count, int[] v, int offset) {

    }

    default void glUniform3f(int location, float x, float y, float z) {

    }

    default void glUniform3fv(int location, int count, FloatBuffer v) {

    }

    default void glUniform3fv(int location, int count, float[] v, int offset) {

    }

    default void glUniform3i(int location, int x, int y, int z) {

    }

    default void glUniform3iv(int location, int count, IntBuffer v) {

    }

    default void glUniform3iv(int location, int count, int[] v, int offset) {

    }

    default void glUniform4f(int location, float x, float y, float z, float w) {

    }

    default void glUniform4fv(int location, int count, FloatBuffer v) {

    }

    default void glUniform4fv(int location, int count, float[] v, int offset) {

    }

    default void glUniform4i(int location, int x, int y, int z, int w) {

    }

    default void glUniform4iv(int location, int count, IntBuffer v) {

    }

    default void glUniform4iv(int location, int count, int[] v, int offset) {

    }

    default void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {

    }

    default void glUniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset) {

    }

    default void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {

    }

    default void glUniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset) {

    }

    default void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {

    }

    default void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {

    }

    default void glUseProgram(int program) {

    }

    default void glValidateProgram(int program) {

    }

    default void glVertexAttrib1f(int indx, float x) {

    }

    default void glVertexAttrib1fv(int indx, FloatBuffer values) {

    }

    default void glVertexAttrib2f(int indx, float x, float y) {

    }

    default void glVertexAttrib2fv(int indx, FloatBuffer values) {

    }

    default void glVertexAttrib3f(int indx, float x, float y, float z) {

    }

    default void glVertexAttrib3fv(int indx, FloatBuffer values) {

    }

    default void glVertexAttrib4f(int indx, float x, float y, float z, float w) {

    }

    default void glVertexAttrib4fv(int indx, FloatBuffer values) {

    }

    default void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr) {

    }

    default void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int ptr) {

    }

    default void glViewport(int x, int y, int width, int height) {

    }

}