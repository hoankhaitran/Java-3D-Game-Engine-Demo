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

public class Cylinder extends Shape3D {

	int[] samplers = new int[2];
	int[] tx_location = new int[2];
	FloatBuffer verBuf, texBuf, norBuf;
	float[] cylinderBuf;
	float count;
	float res=10;
	float[] texel_positions;
	int[] vao = new int[1];
	int[] points_vbo = new int[3];
	Point3D location;
	boolean animated;
	
	public boolean isAnimated() {
		return animated;
	}

	public void setAnimated(boolean animated) {
		this.animated = animated;
	}

	public Cylinder(int input,Point3D loc){
		res = input;
		location = loc;
	}

	// this function return an array of floats that holds normal vectors'
	// components of a model
	public float[] getNormals(float[] input) {
		Vector<Float> result = new Vector<Float>();
		//get the three vertices of the triangle and calculate vector normal
		for (int i = 0; i < input.length; i += 9) {
			Point3D a = new Point3D(input[i], input[i + 1], input[i + 2]);
			Point3D b = new Point3D(input[i + 3], input[i + 4], input[i + 5]);
			Point3D c = new Point3D(input[i + 6], input[i + 7], input[i + 8]);
			Point3D p1 = a.minus(b);
			Point3D p2 = a.minus(c);

			Vector3D e1 = new Vector3D(p1);
			Vector3D e2 = new Vector3D(p2);
			Vector3D n = e1.cross(e2).normalize();
			//add normal's components to the result array
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
		//this.getTranslation().rotateX(45);
		this.getTranslation().translate(location.getX(), location.getY(),location.getZ());
		
		//
		this.getTranslation().rotateY(count);
		if(animated){
			this.getTranslation().rotateZ(45);
		
		}
		count += 0.5f;

		//this.getTranslation().rotateY(count);
		count += 0.5f;

		norBuf = FloatBuffer.wrap(this.getNormals(cylinderBuf));
		verBuf = FloatBuffer.wrap(cylinderBuf);
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
		
		gl.glDrawArraysInstanced(GL4.GL_TRIANGLES, 0, cylinderBuf.length, 1);

	}

	public float[] setupCylinder(float res, float size) {
		Vector<Float> cylBuf = new Vector<Float>();
		Vector<Float> cylTexCoord = new Vector<Float>();
		float d = (float) (2 * Math.PI / res);
		double angle = 0;
		float s = 0,s1=0;
		float x,y,z;
		for (int i = 0; i <= res; i++) {
			
			 x= (float) (Math.cos(angle) * size);
			 z= (float) (Math.sin(angle) * size);
			//save this position for the other half 
			float secondx =x;
			float secondz =z;
//draw the first half of the cylinder
			cylBuf.add(x);
			cylBuf.add(0.0f);
			cylBuf.add(z);
			//add texture coord to the vertex
			cylTexCoord.add(s);
			cylTexCoord.add(0.0f);
			
			angle += d;
			//calculate new position
			x= (float) (Math.cos(angle) * size);
			z= (float) (Math.sin(angle) * size);
			
			
			cylBuf.add(x);
			cylBuf.add(1.0f);
			cylBuf.add(z);
			//add texture coord to the vertex
			cylTexCoord.add(s);
			cylTexCoord.add(1.0f);
			s1=s;
			s += 1 / res;
			cylBuf.add(x);
			cylBuf.add(0.0f);
			cylBuf.add(z);
			//add texture coord to the vertex
			cylTexCoord.add(s);
			cylTexCoord.add(0.0f);
// draw the second half of the cylinder,same algorithm but the triangle is drawn upside down
			
			
			
			
            cylBuf.add(secondx);
            cylBuf.add(1.0f);
			cylBuf.add(secondz);
			cylTexCoord.add(s1);
			cylTexCoord.add(1.0f);
			

			cylBuf.add(x);
			cylBuf.add(1.0f);
			cylBuf.add(z);
			cylTexCoord.add(s);
			cylTexCoord.add(1.0f);
			
			
			cylBuf.add(secondx);
			cylBuf.add(0.0f);
			cylBuf.add(secondz);
			cylTexCoord.add(s);
			cylTexCoord.add(0.0f);
			
		}
		
		
		texel_positions = ArrayUtils.toPrimitive(cylTexCoord
				.toArray(new Float[0]));

		return ArrayUtils.toPrimitive(cylBuf.toArray(new Float[0]));
	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		gl.glGenVertexArrays(1, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(3, points_vbo, 0);
		cylinderBuf = setupCylinder(res, 0.25f);

	}

}
