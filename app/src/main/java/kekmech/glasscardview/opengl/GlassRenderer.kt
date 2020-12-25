package kekmech.glasscardview.opengl

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GlassRenderer(
    private val context: Context
) : GLSurfaceView.Renderer {

    private lateinit var assets: Assets

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
        glDrawFan(0, 4)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 1f)
        assets = Assets(context)
        glUseProgram(assets.program.id)
        bindData()
    }

    private fun bindData() {
        val uColorLocation = glGetUniformLocation(assets.program.id, "u_Color")
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f)

        val aPositionLocation = glGetAttribLocation(assets.program.id, "a_Position")
        assets.vertexData.position(0)

        glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 0, assets.vertexData)
        glEnableVertexAttribArray(aPositionLocation)
    }
}