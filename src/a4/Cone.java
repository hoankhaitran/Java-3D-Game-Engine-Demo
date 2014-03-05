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
import graphicslib3D.shape.Teapot;
import graphicslib3D.shape.Torus;

@SuppressWarnings("serial")
public class Cone extends Shape3D {

	int[] samplers = new int[2];
	int[] tx_location = new int[2];
	FloatBuffer verBuf, texBuf, norBuf;
	float[] coneBuf;
	float count;

	float[] texel_positions;
	int[] vao = new int[1];
	int[] points_vbo = new int[3];


	// this function return an array of floats that holds normal vectors'
	// components of a model
	public float[] getNormals(float[] input) {
		Vector<Float> result = new Vector<Float>();
		// get the three vertices of the triangle and calculate vector normal
		for (int i = 0; i < input.length; i += 9) {
			Point3D a = new Point3D(input[i], input[i + 1], input[i + 2]);
			Point3D b = new Point3D(input[i + 3], input[i + 4], input[i + 5]);
			Point3D c = new Point3D(input[i + 6], input[i + 7], input[i + 8]);
			Point3D p1 = a.minus(b);
			Point3D p2 = a.minus(c);

			Vector3D e1 = new Vector3D(p1);
			Vector3D e2 = new Vector3D(p2);
			Vector3D n = e1.cross(e2).normalize();
			// add normal's components to the result array
			for (int j = 0; j < 3; j++) {
				result.add((float) n.getX());
				// System.out.print(n.getX());
				result.add((float) n.getY());
				// System.out.print(n.getY());
				result.add((float) n.getZ());
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
		// this.getTranslation().rotateX(count);
		// this.getTranslation().rotateY(count);
		// this.getTranslation().rotateZ(-count);
		//this.getTranslation().translate(0.5, 0.0f, 1.5f);
		//System.out.println(this.getTranslation().getFloatValues().length);
		count += 0.5f;

		// sending normals, vertices, and texture coordinates to buffer
		norBuf = FloatBuffer.wrap(this.getNormals(coneBuf));
		verBuf = FloatBuffer.wrap(coneBuf);
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
		//
		gl.glDrawArraysInstanced(GL4.GL_TRIANGLES, 0, coneBuf.length, 1);

	}

	public float[] setupConeBase(float res, float size) {
		Vector<Float> coneBuf = new Vector<Float>();
		Vector<Float> coneTexCoord = new Vector<Float>();
		float d = (float) (2 * Math.PI / res);
		float t = 0, s = 0;
		double a = 0;
		for (int i = 0; i <= res; i++) {
			// each loop will draw a single triangle and calculate the texture
			// coord for each vertex
			coneBuf.add((float) (Math.cos(a) * size));
			coneBuf.add(0.0f);
			coneBuf.add((float) (Math.sin(a) * size));

			coneTexCoord.add(1.0f);
			coneTexCoord.add(t);
			t += 1 / res;
			a += d;

			coneBuf.add(0.0f);
			coneBuf.add(1.0f);
			coneBuf.add(0.0f);
			coneTexCoord.add(0.0f);
			coneTexCoord.add(0.5f);

			coneBuf.add((float) (Math.cos(a) * size));
			coneBuf.add(0.0f);
			coneBuf.add((float) (Math.sin(a) * size));

			coneTexCoord.add(1.0f);
			coneTexCoord.add(t);

		}
		// convert vector to array
		texel_positions = ArrayUtils.toPrimitive(coneTexCoord
				.toArray(new Float[0]));

		return ArrayUtils.toPrimitive(coneBuf.toArray(new Float[0]));
	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		gl.glGenVertexArrays(1, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(3, points_vbo, 0);
		// gl.glEnable(GL4.GL_CLIP_DISTANCE0);
		// first argument is the resolution of the cone, second is size
		coneBuf = setupConeBase(300, 0.5f);
		


	}

}
