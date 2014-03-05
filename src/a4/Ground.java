package a4;

import java.nio.FloatBuffer;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

import graphicslib3D.Shape3D;

public class Ground extends Shape3D {
	int size=4;
	float[] vertex_positions = { 
			-size,0.0f,-size,-size,0.0f,size,size,0.0f,size,
			size,0.0f,size,size,0.0f,-size,-size,0.0f,-size
			
	};
	int[] vao = new int[1];
	int[] points_vbo = new int[1];

	public float[] getVertex_positions() {
		return vertex_positions;
	}

	public void setVertex_positions(float[] vertex_positions) {
		this.vertex_positions = vertex_positions;
	}

	@Override
	public void draw(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL4 gl = drawable.getGL().getGL4();
		gl.glUseProgram(this.getShaderID());

		FloatBuffer verBuf, colorBuf;
		verBuf = FloatBuffer.wrap(this.getVertex_positions());
		
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, points_vbo[0]);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, verBuf.limit() * 4, verBuf,
				GL4.GL_STATIC_DRAW);
		gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		int color_loc = gl.glGetUniformLocation(this.getShaderID(), "in_color");
		colorBuf = FloatBuffer.wrap(new float[] { 0.4f, 0.4f, 0.4f, 1 });
		gl.glUniform4fv(color_loc, 1, colorBuf);
		gl.glDrawArrays(GL4.GL_TRIANGLES,0,vertex_positions.length);
		

	}
	public void init(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		gl.glGenVertexArrays(1, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(1, points_vbo, 0);

	}

}
