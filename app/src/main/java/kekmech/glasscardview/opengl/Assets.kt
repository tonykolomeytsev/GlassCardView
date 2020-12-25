package kekmech.glasscardview.opengl

import android.content.Context
import kekmech.glasscardview.*

class Assets(context: Context) {
    private val vertexShader = Shader.create(context, ShaderType.VERTEX, R.raw.vertex_shader)
    private val fragmentShader = Shader.create(context, ShaderType.FRAGMENT, R.raw.fragment_shader)

    val program = Program.create(vertexShader, fragmentShader)

    val vertexData = listOf(
        Vertex(-0.5f, 0.5f),
        Vertex(0.5f, 0.5f),
        Vertex(0.5f, -0.5f),
        Vertex(-0.5f, -0.5f)
    ).asFloatBuffer()
}