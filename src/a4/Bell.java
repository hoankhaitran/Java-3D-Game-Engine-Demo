package a4;

import java.nio.FloatBuffer;
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

public class Bell extends Shape3D {

	/**
	 * @return the vertex_positions
	 */
	private float count;
	private float pyramid = 0.25f;
	private float cube = 0.15f;
	int[] samplers = new int[2];
	int[] tx_location = new int[2];
	// combine shape of pyramid and cube
	float[] vertex_positions = { -pyramid, -pyramid, pyramid, 0, pyramid, 0,
			pyramid, -pyramid, pyramid, pyramid, -pyramid, pyramid, 0, pyramid,
			0, pyramid, -pyramid, -pyramid, pyramid, -pyramid, -pyramid, 0,
			pyramid, 0, -pyramid, -pyramid, -pyramid, -pyramid, -pyramid,
			-pyramid, 0, pyramid, 0, -pyramid, -pyramid, pyramid, -pyramid,
			-pyramid, pyramid, 0, -pyramid, 0, pyramid, -pyramid, pyramid,
			-pyramid, -pyramid, pyramid, pyramid, -pyramid, pyramid, -pyramid,
			-pyramid, -pyramid, pyramid, -pyramid, pyramid, pyramid, -pyramid,
			-pyramid, -pyramid, -pyramid, -pyramid,

			-cube, cube, -cube, -cube, -cube, -cube, cube, -cube, -cube, cube,
			-cube, -cube, cube, cube, -cube, -cube, cube, -cube, cube, -cube,
			-cube, cube, -cube, cube, cube, cube, -cube, cube, -cube, cube,
			cube, cube, cube, cube, cube, -cube, cube, -cube, cube, -cube,
			-cube, cube, cube, cube, cube, -cube, -cube, cube, -cube, cube,
			cube, cube, cube, cube, -cube, -cube, cube, -cube, -cube, -cube,
			-cube, cube, cube, -cube, -cube, -cube, -cube, cube, -cube, -cube,
			cube, cube, -cube, -cube, cube, cube, -cube, cube, cube, -cube,
			-cube, cube, -cube, -cube, -cube, -cube, -cube, -cube, -cube, cube,
			-cube, cube, -cube, cube, cube, -cube, cube, cube, cube, cube,
			cube, cube, -cube, cube, cube, -cube, cube, -cube

	};
	float[] texel_positions = { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
			1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
			1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
			1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
			1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 1.0f

	};
	int[] vao = new int[1];
	int[] points_vbo = new int[3];

	public void setVertex_positions(float[] input) {
		vertex_positions = input;
	}

	public float[] getVertex_positions() {
		return this.vertex_positions;
	}

	public float[] getNormals(float[] input) {
		Vector<Float> result = new Vector<Float>();
		for (int i = 0; i < input.length; i += 9) {
			Point3D a = new Point3D(input[i], input[i + 1], input[i + 2]);
			Point3D b = new Point3D(input[i + 3], input[i + 4], input[i + 5]);
			Point3D c = new Point3D(input[i + 6], input[i + 7], input[i + 8]);
			Point3D p1 = b.minus(a);
			Point3D p2 = c.minus(a);

			Vector3D e1 = new Vector3D(p1);
			Vector3D e2 = new Vector3D(p2);
			Vector3D n = e1.cross(e2).normalize();
			for (int j = 0; j < 3; j++) {
				result.add(-(float) n.getX());
				// System.out.print(n.getX());
				result.add(-(float) n.getY());
				// System.out.print(n.getY());
				result.add(-(float) n.getZ());
				// System.out.print(n.getZ());
				// System.out.println();
			}

		}
		return ArrayUtils.toPrimitive(result.toArray(new Float[0]));

	}

	public void draw(GLAutoDrawable drawable) {

		// TODO Auto-generated method stub
		GL4 gl = drawable.getGL().getGL4();
		// rotate the sun around its Y axis
		this.getTranslation().setToIdentity();
		this.getTranslation().translate(0.5, 0, 1);

		count += 0.0001;
		// reverse the rotation direction
		if (count > 5)
			count = -5;
		FloatBuffer verBuf, texBuf, norBuf;

		norBuf = FloatBuffer.wrap(this.getNormals(this.getVertex_positions()));
		verBuf = FloatBuffer.wrap(this.getVertex_positions());
		texBuf = FloatBuffer.wrap(texel_positions);
		// active the first buffer and load data for vertices into shader
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, points_vbo[0]);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, verBuf.limit() * 4, verBuf,
				GL4.GL_STATIC_DRAW);
		gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		// active the second buffer and load texture coord into shader
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, points_vbo[1]);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, norBuf.limit() * 4, norBuf,
				GL4.GL_STATIC_DRAW);
		gl.glVertexAttribPointer(1, 3, GL4.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, points_vbo[2]);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, texBuf.limit() * 4, texBuf,
				GL4.GL_STATIC_DRAW);
		gl.glVertexAttribPointer(2, 2, GL4.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glDrawArrays(GL4.GL_TRIANGLES, 0, 72);

	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		gl.glGenVertexArrays(1, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(3, points_vbo, 0);

	}

}
