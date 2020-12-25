package kekmech.glasscardview

import android.content.Context
import android.opengl.GLES20.*
import androidx.annotation.RawRes
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


data class Program constructor(
    val id: Int,
    val shaders: List<Shader>
) {

    companion object {

        fun create(vararg shaders: Shader) = create(shaders.asList())

        fun create(shaders: List<Shader>): Program {
            val programId = glCreateProgram()
            if (programId == 0) throw RuntimeException("Не удалось создать программу")
            shaders.forEach { shader ->
                glAttachShader(programId, shader.id)
            }
            glLinkProgram(programId)
            val linkStatus = IntArray(1)
            glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                glDeleteProgram(programId)
                throw RuntimeException("Не удалось создать программу")
            }
            return Program(programId, shaders)
        }
    }
}

data class Shader(
    val id: Int,
    val type: ShaderType
) {

    companion object {

        fun create(context: Context, type: ShaderType, @RawRes rawId: Int): Shader {
            val source = context.resources.openRawResource(rawId).readBytes().decodeToString()
            return compile(type, source) ?: throw RuntimeException("Не удалось создать шейдер")
        }

        private fun compile(type: ShaderType, shaderText: String): Shader? {
            val shaderId = glCreateShader(type.glType)
            if (shaderId == 0) return null

            glShaderSource(shaderId, shaderText)
            glCompileShader(shaderId)
            val compileStatus = IntArray(1)
            glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus, 0)
            if (compileStatus[0] == 0) {
                glDeleteShader(shaderId)
                return null
            }
            return Shader(shaderId, type)
        }
    }
}

enum class ShaderType(val glType: Int) {
    VERTEX(GL_VERTEX_SHADER),
    FRAGMENT(GL_FRAGMENT_SHADER)
}

data class Vertex(
    val u: Float,
    val v: Float
)

fun List<Vertex>.asFloatBuffer(): FloatBuffer = this
    .flatMap { listOf(it.u, it.v) }
    .toFloatArray()
    .let { ByteBuffer
        .allocateDirect(it.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(it)
    }


