package a4;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

import org.apache.commons.lang3.ArrayUtils;

import graphicslib3D.Point3D;
import graphicslib3D.Shape3D;
import graphicslib3D.Vector3D;
import graphicslib3D.Vertex3D;
import graphicslib3D.shape.Torus;

public class LightObject extends Shape3D {

	/**
	 * @return the vertex_positions
	 */

	private float cube = 0.025f;

	float[] vertex_positions = { -cube, cube, -cube, -cube, -cube, -cube, cube,
			-cube, -cube, cube, -cube, -cube, cube, cube, -cube, -cube, cube,
			-cube, cube, -cube, -cube, cube, -cube, cube, cube, cube, -cube,
			cube, -cube, cube, cube, cube, cube, cube, cube, -cube, cube,
			-cube, cube, -cube, -cube, cube, cube, cube, cube, -cube, -cube,
			cube, -cube, cube, cube, cube, cube, cube, -cube, -cube, cube,
			-cube, -cube, -cube, -cube, cube, cube, -cube, -cube, -cube, -cube,
			cube, -cube, -cube, cube, cube, -cube, -cube, cube, cube, -cube,
			cube, cube, -cube, -cube, cube, -cube, -cube, -cube, -cube, -cube,
			-cube, -cube, cube, -cube, cube, -cube, cube, cube, -cube, cube,
			cube, cube, cube, cube, cube, -cube, cube, cube, -cube, cube, -cube };
	int[] points_vbo = new int[1];
	int[] vao = new int[1];
	Point3D light_loc;

	public Point3D getLight_loc() {
		return light_loc;
	}

	public void setLight_loc(Point3D light_loc) {
		this.light_loc = light_loc;
	}

	public void setVertex_positions(float[] input) {
		vertex_positions = input;
	}

	public float[] getVertex_positions() {
		return this.vertex_positions;
	}

	public void draw(GLAutoDrawable drawable) {

		// TODO Auto-generated method stub
		GL4 gl = drawable.getGL().getGL4();
		
		this.getTranslation().setToIdentity();
		this.getTranslation().translate(light_loc.getX(), light_loc.getY(),
				light_loc.getZ());

		FloatBuffer verBuf;

		verBuf = FloatBuffer.wrap(this.getVertex_positions());
		// active the first buffer and load data for vertices into shader
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, points_vbo[0]);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, verBuf.limit() * 4, verBuf,
				GL4.GL_STATIC_DRAW);
		gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glDrawArrays(GL4.GL_TRIANGLES, 0, 36);

	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		gl.glGenVertexArrays(1, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(1, points_vbo, 0);

	}

}
