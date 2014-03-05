package a4;

import java.nio.FloatBuffer;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

import graphicslib3D.Shape3D;

public class Terrain extends Shape3D {


	@Override
	public void draw(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL4 gl = drawable.getGL().getGL4();
		gl.glUseProgram(this.getShaderID());

		gl.glPatchParameteri(GL4.GL_PATCH_VERTICES,4); 
		gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_FILL); //FILL or LINE 
		gl.glDrawArraysInstanced(GL4.GL_PATCHES, 0, 4, 64*64);
			
		

	}
	public void init(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		
	}

}
