package live.security.ru.app.util;

import android.graphics.Matrix;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
/**
 * @author sardor
 */
public class SurfaceHelper {

  public static void clearSurface(Surface surface) {
    EGL10 egl = (EGL10) EGLContext.getEGL();
    EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
    egl.eglInitialize(display, null);

    int[] attribList = {
        EGL10.EGL_RED_SIZE, 8,
        EGL10.EGL_GREEN_SIZE, 8,
        EGL10.EGL_BLUE_SIZE, 8,
        EGL10.EGL_ALPHA_SIZE, 8,
        EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
        EGL10.EGL_NONE, 0,      // placeholder for recordable [@-3]
        EGL10.EGL_NONE
    };
    EGLConfig[] configs = new EGLConfig[1];
    int[] numConfigs = new int[1];
    egl.eglChooseConfig(display, attribList, configs, configs.length, numConfigs);
    EGLConfig config = configs[0];
    EGLContext context = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, new int[]{
        EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
        EGL10.EGL_NONE
    });
    EGLSurface eglSurface = egl.eglCreateWindowSurface(display, config, surface,
        new int[]{
            EGL14.EGL_NONE
        });

    egl.eglMakeCurrent(display, eglSurface, eglSurface, context);
    GLES20.glClearColor(0, 0, 0, 1);
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    egl.eglSwapBuffers(display, eglSurface);
    egl.eglDestroySurface(display, eglSurface);
    egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
        EGL10.EGL_NO_CONTEXT);
    egl.eglDestroyContext(display, context);
    egl.eglTerminate(display);
  }

  public static void adjustAspectRatio(int videoWidth, int videoHeight, TextureView mTextureView, View view) {
    int viewWidth = mTextureView.getWidth();
    int viewHeight = mTextureView.getHeight();
    double aspectRatio = (double) videoHeight / videoWidth;

    int newWidth, newHeight;
    if (viewHeight > (int) (viewWidth * aspectRatio)) {
      // limited by narrow width; restrict height
      newWidth = viewWidth;
      newHeight = (int) (viewWidth * aspectRatio);
    } else {
      // limited by short height; restrict width
      newWidth = (int) (viewHeight / aspectRatio);
      newHeight = viewHeight;
    }
    int xoff = (viewWidth - newWidth) / 2;
    int yoff = (viewHeight - newHeight) / 2;
    LoggingTool.Companion.log("video=" + videoWidth + "x" + videoHeight +
        " view=" + viewWidth + "x" + viewHeight +
        " newView=" + newWidth + "x" + newHeight +
        " off=" + xoff + "," + yoff);


    Matrix txform = new Matrix();
    mTextureView.getTransform(txform);
    txform.setScale((float) newWidth / viewWidth, (float) newHeight / viewHeight);
    //txform.postRotate(10);          // just for fun
    txform.postTranslate(xoff, yoff);
    mTextureView.setTransform(txform);

    // This view does not belong here, take this view to other place, it contradicts SOLID principle

    if (view != null) {
      ViewGroup.LayoutParams params = view.getLayoutParams();
      params.width = mTextureView.getWidth();
      params.height = mTextureView.getHeight();
      view.invalidate();
    }
  }
}
