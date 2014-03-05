package a4;

import graphicslib3D.Material;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Shape3D;
import graphicslib3D.Vector3D;
import graphicslib3D.Vertex3D;
import graphicslib3D.light.PositionalLight;
import graphicslib3D.light.SpotLight;
import graphicslib3D.shape.Teapot;
import graphicslib3D.shape.Torus;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.jogamp.opengl.util.FPSAnimator;

import commands.TickCommand;

/**
 * @author FXSSD
 * 
 */
@SuppressWarnings("serial")
public class MyGLCanvas extends JFrame implements GLEventListener {
	private JPanel my_main_panel, my_control_panel, my_canvas_panel;
	private GLSLRenderer glsl_axes_renderer = new GLSLRenderer();
	private GLSLRenderer glsl_discarded_cylinder_renderer = new GLSLRenderer();
	private GLSLRenderer glsl_cylinder_renderer = new GLSLRenderer();
	private GLSLRenderer glsl_lo_renderer = new GLSLRenderer();
	private GLSLRenderer glsl_shadow_renderer = new GLSLRenderer();
	private GLSLRenderer glsl_cone_renderer = new GLSLRenderer();
	private GLSLRenderer glsl_ground_renderer = new GLSLRenderer();
	private GLSLRenderer glsl_terrain_renderer = new GLSLRenderer();
	private Timer timer;
	public GLCanvas my_gl_canvas = new GLCanvas();
	private Button pause_play_button, back_color_button, axis_toggle_button;
	final int FRAME_WIDTH = 800;
	final int FRAME_HEIGHT = 800;
	final int DELAY_TIME_IN_MS = 10;
	FPSAnimator animator = new FPSAnimator(60);
	Point savedPoint = new Point();
	String OPENGL_VERSION;
	String GLSL_VERSION;
	final String JOGL_VERSION = Package.getPackage("javax.media.opengl")
			.getImplementationVersion();

	boolean toggle = true;
	boolean light_toggle = true;
	private FloatBuffer colors = FloatBuffer.allocate(4);

	int[] vao = new int[1];
	int[] points_vbo = new int[2];
	int[] samplers = new int[2];
	int[] tx_location = new int[7];
	int[] shadow_tex = new int[1];
	int[] shadow_buffer = new int[1];
	float SPEED = 0.05f;
	float CAM_SPEED = 1;
	float texture_flag = 1;
	Matrix3D m_matrix = new Matrix3D();
	Matrix3D v_matrix = new Matrix3D();
	Matrix3D mv_matrix = new Matrix3D();
	Matrix3D proj_matrix = new Matrix3D();
	Matrix3D lightV_matrix = new Matrix3D();
	Matrix3D lightP_matrix = new Matrix3D();
	Matrix3D shadowMVP_matrix = new Matrix3D();
	Matrix3D b = new Matrix3D();

	// FPSAnimator animator = new FPSAnimator(24);
	int count;
	Axes axes;
	Ground ground = new Ground();
	Terrain terrain = new Terrain();
	Cone cone = new Cone();
	Cone cone2 = new Cone();
	Cylinder cylinder = new Cylinder(500, new Point3D(0,0,2.5));
	Cylinder cylinder2 = new Cylinder(500,new Point3D(0,0,2.5));
	Cylinder lr_cylinder = new Cylinder(50,new Point3D(1.0,0.5,3.5));
	Cylinder lr_cylinder2 = new Cylinder(50,new Point3D(1.0,0.5,3.5));

	LightObject light_obj = new LightObject();
	MyCamera gl_camera = new MyCamera();
	MyCamera gl_light_camera = new MyCamera();
	Point3D new_cam_loc;
	Point3D new_light_loc;

	float[] globalAmbient = new float[] { 2.9f, 2.9f, 2.9f, 1 };
	PositionalLight currentLight = new PositionalLight();
	float x = 0.5f, y = 3.5f, z = 10;

	private void installLights(int shaderProgID, Matrix3D v_matrix,
			GLAutoDrawable drawable, Material currentMaterial) {
		GL4 gl = (GL4) drawable.getGL();

		Point3D lightP = currentLight.getPosition();

		light_obj.setLight_loc(currentLight.getPosition());
		Point3D lightPv = lightP.mult(v_matrix);

		float[] currLightPos = new float[] { (float) lightPv.getX(),
				(float) lightPv.getY(), (float) lightPv.getZ() };

		// get the location of the global ambient light field in the shader
		int globalAmbLoc = gl.glGetUniformLocation(shaderProgID,
				"globalAmbient");

		// set the current globalAmbient settings
		gl.glProgramUniform4fv(shaderProgID, globalAmbLoc, 1, globalAmbient, 0);
		// enable light if light toggle is true
		if (light_toggle) {
			currentLight.setPosition(new Point3D(x, y, z));
			currentLight.setSpecular(new float[] { 1.0f, 1.0f, 1.0f });
			currentLight.setDiffuse(new float[] { 0.7f, 0.7f, 0.7f });
			currentLight.setAmbient(new float[] { 0.2f, 0.2f, 0.2f, 1 });
			// get the locations of the light and material fields in the shader
			int lightLoc = gl.glGetUniformLocation(shaderProgID,
					"light.ambient");

			// set the uniform light and material values in the shader
			gl.glProgramUniform4fv(shaderProgID, lightLoc, 1,
					currentLight.getAmbient(), 0);
			// same procedure for light diffuse, specular and location
			lightLoc = gl.glGetUniformLocation(shaderProgID, "light.diffuse");
			gl.glProgramUniform4fv(shaderProgID, lightLoc, 1,
					currentLight.getDiffuse(), 0);

			lightLoc = gl.glGetUniformLocation(shaderProgID, "light.specular");
			gl.glProgramUniform4fv(shaderProgID, lightLoc, 1,
					currentLight.getSpecular(), 0);

			lightLoc = gl.glGetUniformLocation(shaderProgID, "light.position");
			gl.glProgramUniform3fv(shaderProgID, lightLoc, 1, currLightPos, 0);
		} else {// disable light source by zeroing all its values
			// set the uniform light and material values in the shader
			currentLight.setPosition(new Point3D(x, y, z));
			currentLight.setSpecular(new float[] { 0.0f, 0.0f, 0.0f });
			currentLight.setDiffuse(new float[] { 0.0f, 0.0f, 0.0f });
			currentLight.setAmbient(new float[] { 0.0f, 0.0f, 0.0f, 1 });
			int lightLoc = gl.glGetUniformLocation(shaderProgID,
					"light.ambient");

			gl.glProgramUniform4fv(shaderProgID, lightLoc, 1,
					currentLight.getAmbient(), 0);

			lightLoc = gl.glGetUniformLocation(shaderProgID, "light.diffuse");
			gl.glProgramUniform4fv(shaderProgID, lightLoc, 1,
					currentLight.getDiffuse(), 0);

			lightLoc = gl.glGetUniformLocation(shaderProgID, "light.specular");
			gl.glProgramUniform4fv(shaderProgID, lightLoc, 1,
					currentLight.getSpecular(), 0);

			lightLoc = gl.glGetUniformLocation(shaderProgID, "light.position");
			gl.glProgramUniform3fv(shaderProgID, lightLoc, 1, currLightPos, 0);
		}
		// sending material properties to the shaders
		int materialLoc = gl.glGetUniformLocation(shaderProgID,
				"material.ambient");
		gl.glProgramUniform4fv(shaderProgID, materialLoc, 1,
				currentMaterial.getAmbient(), 0);
		materialLoc = gl.glGetUniformLocation(shaderProgID, "material.diffuse");
		gl.glProgramUniform4fv(shaderProgID, materialLoc, 1,
				currentMaterial.getDiffuse(), 0);
		materialLoc = gl
				.glGetUniformLocation(shaderProgID, "material.specular");
		gl.glProgramUniform4fv(shaderProgID, materialLoc, 1,
				currentMaterial.getSpecular(), 0);
		materialLoc = gl.glGetUniformLocation(shaderProgID,
				"material.shininess");
		gl.glProgramUniform1f(shaderProgID, materialLoc,
				currentMaterial.getShininess());

	}

	@Override
	public void init(GLAutoDrawable drawable) {

		// Initialization all the planets,camera,and sun

		gl_camera.setLocation(new Point3D(0.5, 1.5, 7d));

		new_cam_loc = new Point3D(0, 0, 0);
		GL4 gl = drawable.getGL().getGL4();

		OPENGL_VERSION = gl.glGetString(GL4.GL_VERSION);
		GLSL_VERSION = gl.glGetString(GL4.GL_SHADING_LANGUAGE_VERSION);
		System.out.println("OPENGL VER: " + OPENGL_VERSION);
		System.out.println("JOGL version: " + JOGL_VERSION);
		System.out.println("GLSL version: " + GLSL_VERSION);
		colors.put(0, 0.0f);
		colors.put(1, 0.0f);
		colors.put(2, 0.0f);
		colors.put(3, 1.0f);
		// create shader program for each 3D shape
		axes.setShaderID(glsl_axes_renderer.createShaderProgram(drawable));
		light_obj.setShaderID(glsl_lo_renderer.createShaderProgram(drawable));
		cone.setShaderID(glsl_shadow_renderer.createShaderProgram(drawable));
		cone.init(drawable);
		cylinder.setShaderID(glsl_shadow_renderer.createShaderProgram(drawable));
		cylinder.init(drawable);

		lr_cylinder.setShaderID(glsl_shadow_renderer
				.createShaderProgram(drawable));
		lr_cylinder.init(drawable);

		cone2.setShaderID(glsl_cone_renderer.createShaderProgram(drawable));
		cone2.init(drawable);

		ground.setShaderID(glsl_ground_renderer.createShaderProgram(drawable));
		cylinder2.setShaderID(glsl_cylinder_renderer
				.createShaderProgram(drawable));
		cylinder2.init(drawable);
		lr_cylinder2.setShaderID(glsl_discarded_cylinder_renderer
				.createShaderProgram(drawable));
		lr_cylinder2.init(drawable);

		light_obj.init(drawable);
		ground.init(drawable);

		terrain.setShaderID(glsl_terrain_renderer.createShaderProgram(drawable));
		terrain.init(drawable);
		// load the textures into a buffer
		tx_location[0] = glsl_cone_renderer.loadTexture(drawable, "cubes.jpg");
		tx_location[1] = glsl_cone_renderer.loadTexture(drawable, "brick.png");
		tx_location[2] = glsl_discarded_cylinder_renderer.loadTexture(drawable,
				"warning.jpg");
		tx_location[3] = glsl_discarded_cylinder_renderer.loadTexture(drawable,
				"wood.jpg");
		tx_location[4] = glsl_discarded_cylinder_renderer.loadTexture(drawable,
				"brick.png");
		tx_location[5] = glsl_discarded_cylinder_renderer.loadTexture(drawable,
				"normalbrick.jpg");
		tx_location[6] = glsl_discarded_cylinder_renderer.loadTexture(drawable,
				"heightmap.jpg");

		my_gl_canvas.requestFocus();
		// set up frambuffer to hold the depth texture
		gl.glGenFramebuffers(1, shadow_buffer, 0);

		gl.glGenTextures(1, shadow_tex, 0);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, shadow_tex[0]);
		// Misc opengl settings
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_DEPTH_COMPONENT32,
				my_gl_canvas.getWidth(), my_gl_canvas.getHeight(), 0,
				GL4.GL_DEPTH_COMPONENT, GL4.GL_FLOAT, null);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER,
				GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER,
				GL4.GL_LINEAR);
		// prevent shadow acne
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S,
				GL4.GL_CLAMP_TO_EDGE);

		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T,
				GL4.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_COMPARE_MODE,
				GL4.GL_COMPARE_REF_TO_TEXTURE);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_COMPARE_FUNC,
				GL4.GL_LEQUAL);

	}

	public void shadowMapPass1(GLAutoDrawable drawable) {

		GL4 gl = drawable.getGL().getGL4();
		//bind the framebuffer
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, shadow_buffer[0]);
		//attach the depth of the objects to the framebuffer's texture
		gl.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT,
				shadow_tex[0], 0);
		int buffs[] = { GL.GL_COLOR_ATTACHMENT0 };
		gl.glDrawBuffers(1, buffs, 0);
		gl.glDrawBuffer(GL.GL_NONE);
		gl.glViewport(0, 0, my_gl_canvas.getWidth(), my_gl_canvas.getHeight());
		gl.glClear(GL4.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL.GL_DEPTH_TEST);
		//prevent shadow acne by using offset
		gl.glEnable(GL4.GL_POLYGON_OFFSET_FILL);
		gl.glPolygonOffset(2.0f, 4.0f);
		//prevent self shadowing
		gl.glEnable(GL4.GL_CULL_FACE);
		gl.glCullFace(GL4.GL_FRONT);
		//draw the scene from light perspective
		if (light_toggle) {
			gl.glUseProgram(cone.getShaderID());

			drawTheObjectLightPerspective(drawable, cone);

			gl.glUseProgram(cylinder.getShaderID());

			drawTheObjectLightPerspective(drawable, cylinder);

			gl.glUseProgram(lr_cylinder.getShaderID());
			lr_cylinder.setAnimated(true);
			drawTheObjectLightPerspective(drawable, lr_cylinder);
		}
		gl.glDisable(GL4.GL_CULL_FACE);

	}

	public void shadowMapPass2(GLAutoDrawable drawable) {

		GL4 gl = drawable.getGL().getGL4();
		int samplerloc = 0;
		
		gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_FILL);
		//unbind the framebuffer
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		gl.glDisable(GL4.GL_POLYGON_OFFSET_FILL);
		//prevent shadow acne
		gl.glDrawBuffer(GL4.GL_FRONT);
		gl.glEnable(GL4.GL_CULL_FACE);
		gl.glCullFace(GL4.GL_BACK);
		//bind shadow texture to texture unit 0
		gl.glActiveTexture(gl.GL_TEXTURE0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, shadow_tex[0]);

		gl.glUseProgram(cone2.getShaderID());
		samplerloc = gl.glGetUniformLocation(cone2.getShaderID(), "shadow_tex");
		gl.glUniform1i(samplerloc, 0);

		samplerloc = gl.glGetUniformLocation(cone2.getShaderID(), "s");
		gl.glUniform1i(samplerloc, 1);
		gl.glActiveTexture(GL.GL_TEXTURE1);
		// bind texture to texture unit
		gl.glBindTexture(GL.GL_TEXTURE_2D, tx_location[2]);

		samplerloc = gl.glGetUniformLocation(cone2.getShaderID(), "s1");
		gl.glUniform1i(samplerloc, 2);
		gl.glActiveTexture(GL.GL_TEXTURE2);
		// bind another texture to texture unit
		gl.glBindTexture(GL.GL_TEXTURE_2D, tx_location[3]);

		installLights(cone2.getShaderID(), v_matrix, drawable,
				graphicslib3D.Material.BRONZE);
		
		drawTheObject(drawable, cone2);

		gl.glUseProgram(light_obj.getShaderID());
		drawTheObject(drawable, light_obj);

		gl.glUseProgram(axes.getShaderID());

		// toggle for axes
		if (toggle)
			drawTheObject(drawable, axes);
		gl.glUseProgram(ground.getShaderID());

		installLights(ground.getShaderID(), v_matrix, drawable,
				graphicslib3D.Material.SILVER);

		drawTheObject(drawable, ground);
		gl.glDisable(GL4.GL_CULL_FACE);
		gl.glUseProgram(cylinder2.getShaderID());
		// let shader know the location of its sampler
		samplerloc = gl.glGetUniformLocation(cylinder2.getShaderID(), "s");
		gl.glUniform1i(samplerloc, 1);
		gl.glActiveTexture(GL.GL_TEXTURE1);
		// bind texture to texture unit
		gl.glBindTexture(GL.GL_TEXTURE_2D, tx_location[4]);

		samplerloc = gl.glGetUniformLocation(cylinder2.getShaderID(),
				"normalSampler");
		gl.glUniform1i(samplerloc, 2);
		gl.glActiveTexture(GL.GL_TEXTURE2);
		// bind normal map texture to texture unit 2
		gl.glBindTexture(GL.GL_TEXTURE_2D, tx_location[5]);

		samplerloc = gl.glGetUniformLocation(cylinder2.getShaderID(), "s1");
		gl.glUniform1i(samplerloc, 3);
		gl.glActiveTexture(GL.GL_TEXTURE3);
		// bind texture to texture unit
		gl.glBindTexture(GL.GL_TEXTURE_2D, tx_location[0]);

		installLights(cylinder2.getShaderID(), v_matrix, drawable,
				graphicslib3D.Material.GOLD);

		drawTheObject(drawable, cylinder2);

		gl.glUseProgram(lr_cylinder2.getShaderID());
		// let shader know the location of its sampler
		samplerloc = gl.glGetUniformLocation(lr_cylinder2.getShaderID(), "s");
		gl.glUniform1i(samplerloc, 1);
		gl.glActiveTexture(GL.GL_TEXTURE1);
		// bind texture to texture unit
		gl.glBindTexture(GL.GL_TEXTURE_2D, tx_location[0]);

		samplerloc = gl.glGetUniformLocation(lr_cylinder2.getShaderID(), "s1");
		gl.glUniform1i(samplerloc, 2);
		gl.glActiveTexture(GL.GL_TEXTURE2);
		// bind texture to texture unit
		gl.glBindTexture(GL.GL_TEXTURE_2D, tx_location[2]);

		samplerloc = gl.glGetUniformLocation(lr_cylinder2.getShaderID(),
				"heightmap");
		gl.glUniform1i(samplerloc, 3);
		gl.glActiveTexture(GL.GL_TEXTURE3);
		// bind height map texture to texture unit
		gl.glBindTexture(GL.GL_TEXTURE_2D, tx_location[6]);

		installLights(lr_cylinder2.getShaderID(), v_matrix, drawable,
				graphicslib3D.Material.GOLD);
		lr_cylinder2.setAnimated(true);
		drawTheObject(drawable, lr_cylinder2);

		gl.glUseProgram(terrain.getShaderID());
		samplerloc = gl.glGetUniformLocation(terrain.getShaderID(), "s");
		gl.glUniform1i(samplerloc, 0);
		gl.glActiveTexture(GL.GL_TEXTURE0);
		// bind height map texture to texture unit 0
		gl.glBindTexture(GL.GL_TEXTURE_2D, tx_location[6]);

		samplerloc = gl.glGetUniformLocation(terrain.getShaderID(), "s1");
		gl.glUniform1i(samplerloc, 1);
		gl.glActiveTexture(GL.GL_TEXTURE1);
		// bind color texture to texture unit 1
		gl.glBindTexture(GL.GL_TEXTURE_2D, tx_location[1]);

		installLights(terrain.getShaderID(), v_matrix, drawable,
				graphicslib3D.Material.GOLD);
		terrain.getTranslation().setToIdentity();
		terrain.getTranslation().translate(0, -8, 0);

		

		gl.glUseProgram(terrain.getShaderID());
		

		drawTheObject(drawable, terrain);

		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

	
		gl.glEnable(GL4.GL_DEPTH_RENDERABLE);
		gl.glFrontFace(GL4.GL_CCW);
		gl.glDepthFunc(GL4.GL_LEQUAL);

	}

	@Override
	public void display(GLAutoDrawable drawable) {

		this.setTitle("JOGL " + JOGL_VERSION + " Light loc= "
				+ currentLight.getPosition());
		GL4 gl = drawable.getGL().getGL4();
		gl.glClearBufferfv(GL4.GL_COLOR, 0, colors);

		// PASS1

		shadowMapPass1(drawable);

		// PASS2
		shadowMapPass2(drawable);

		

	}

	public void drawTheObject(GLAutoDrawable drawable, Shape3D shape) {
		GL4 gl = drawable.getGL().getGL4();
		// Calculate new camera location based on its current location and
		// direction vector
		shape.draw(drawable);
		new_cam_loc = gl_camera.getLocation().add(
				new Point3D(gl_camera.getViewDirection().getX(), gl_camera
						.getViewDirection().getY(), gl_camera
						.getViewDirection().getZ()));

		int m_matrix_p, mv_matrix_p, v_matrix_p, proj_matrix_p, shadow_location;
		mv_matrix_p = gl.glGetUniformLocation(shape.getShaderID(), "mv_matrix");
		v_matrix_p = gl.glGetUniformLocation(shape.getShaderID(), "v_matrix");
		m_matrix_p = gl.glGetUniformLocation(shape.getShaderID(), "m_matrix");

		proj_matrix_p = gl.glGetUniformLocation(shape.getShaderID(),
				"proj_matrix");

		shadow_location = gl.glGetUniformLocation(shape.getShaderID(),
				"shadowMVP_matrix");

		// make call to Shape's draw method

		// setup view matrix

		v_matrix = gl_camera.lookAt(gl_camera.getLocation(), new_cam_loc,
				new Vector3D(0, 1, 0));

		mv_matrix.setToIdentity();

		mv_matrix.concatenate(v_matrix);

		// add shape's transform to modelview matrix
		mv_matrix.concatenate(shape.getTransform());

		float aspect = my_gl_canvas.getWidth() / my_gl_canvas.getHeight();
		proj_matrix.setToIdentity();
		proj_matrix = gl_camera.perspective(50.0f, aspect, 0.1f, 1000.0f);

		shadowMVP_matrix.setToIdentity();
		shadowMVP_matrix.concatenate(b);
		shadowMVP_matrix.concatenate(lightP_matrix);
		shadowMVP_matrix.concatenate(lightV_matrix);
		shadowMVP_matrix.concatenate(shape.getTransform());

		gl.glUniformMatrix4fv(shadow_location, 1, false,
				shadowMVP_matrix.getFloatValues(), 0);

		gl.glUniformMatrix4fv(proj_matrix_p, 1, false,
				proj_matrix.getFloatValues(), 0);

		gl.glUniformMatrix4fv(mv_matrix_p, 1, false,
				mv_matrix.getFloatValues(), 0);
		gl.glUniformMatrix4fv(v_matrix_p, 1, false, v_matrix.getFloatValues(),
				0);
		int n_matrix_p = gl.glGetUniformLocation(shape.getShaderID(),
				"n_matrix");
		gl.glUniformMatrix4fv(n_matrix_p, 1, false, mv_matrix.inverse()
				.transpose().getFloatValues(), 0);
		gl.glUniformMatrix4fv(m_matrix_p, 1, false, shape.getTransform()
				.getFloatValues(), 0);

	}

	public void drawTheObjectLightPerspective(GLAutoDrawable drawable,
			Shape3D shape) {
		GL4 gl = drawable.getGL().getGL4();

		int shadow_location;

		// make call to Shape's draw method
		shape.draw(drawable);

		float aspect = my_gl_canvas.getWidth() / my_gl_canvas.getHeight();
		//setup the shadow MVP matrix
		lightV_matrix = gl_camera.lookAt(currentLight.getPosition(),
				new Point3D(0, 0, 0), new Vector3D(0, 1, 0));

		lightP_matrix.setToIdentity();
		lightP_matrix = gl_camera.perspective(50.0f, aspect, 0.1f, 1000.0f);
		//Create the B matrix to convert coordinate range from -1,1 to 0,1 
		b.setElementAt(0, 0, 0.5);
		b.setElementAt(0, 1, 0.0);
		b.setElementAt(0, 2, 0.0);
		b.setElementAt(0, 3, 0.0f);
		b.setElementAt(1, 0, 0.0);
		b.setElementAt(1, 1, 0.5);
		b.setElementAt(1, 2, 0.0);
		b.setElementAt(1, 3, 0.0);
		b.setElementAt(2, 0, 0.0);
		b.setElementAt(2, 1, 0.0);
		b.setElementAt(2, 2, 0.5);
		b.setElementAt(2, 3, 0.0);
		b.setElementAt(3, 0, 0.5);
		b.setElementAt(3, 1, 0.5);
		b.setElementAt(3, 2, 0.5);
		b.setElementAt(3, 3, 1.0);
		b = b.transpose();

		shadowMVP_matrix.setToIdentity();
		shadowMVP_matrix.concatenate(lightP_matrix);
		shadowMVP_matrix.concatenate(lightV_matrix);
		shadowMVP_matrix.concatenate(shape.getTransform());
		//send shadow matrix to shaders
		shadow_location = gl.glGetUniformLocation(shape.getShaderID(),
				"shadowMVP_matrix");

		gl.glUniformMatrix4fv(shadow_location, 1, false,
				shadowMVP_matrix.getFloatValues(), 0);

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	public MyGLCanvas() {
		setupFrame();
		axes = new Axes();
		glsl_discarded_cylinder_renderer
				.setFragShader("DiscardedFShaderCYL.glsl");
		glsl_discarded_cylinder_renderer
				.setVertexShader("DiscardedVShaderCYL.glsl");
		glsl_discarded_cylinder_renderer
				.setGeoShader("DiscardedGShaderCYL.glsl");
		glsl_cylinder_renderer.setFragShader("FShaderCYL.glsl");
		glsl_cylinder_renderer.setVertexShader("VShaderCYL.glsl");

		glsl_shadow_renderer.setFragShader("shadowFShader.glsl");
		glsl_shadow_renderer.setVertexShader("shadowVShader.glsl");
		// glsl_shadow_renderer.setGeoShader("shadowGShader.glsl");

		glsl_cone_renderer.setFragShader("FShaderCONE.glsl");
		glsl_cone_renderer.setVertexShader("VShaderCONE.glsl");
		glsl_lo_renderer.setFragShader("fragShaderLO.glsl");
		glsl_lo_renderer.setVertexShader("vertexShaderLO.glsl");
		glsl_axes_renderer.setFragShader("fragShaderAxis.glsl");
		glsl_axes_renderer.setVertexShader("vertexShaderAxis.glsl");
		glsl_ground_renderer.setFragShader("fragShaderGround.glsl");
		glsl_ground_renderer.setVertexShader("vertexShaderGround.glsl");
		glsl_terrain_renderer.setVertexShader("vertexShaderTerrain.glsl");
		glsl_terrain_renderer.setFragShader("fragShaderTerrain.glsl");
		glsl_terrain_renderer.setTCShader("TCSShaderTerrain.glsl");
		glsl_terrain_renderer.setTEShader("TESShaderTerrain.glsl");
	}

	// method to setup Frame
	private void setupFrame() {
		my_main_panel = new JPanel();
		my_control_panel = new JPanel();
		my_canvas_panel = new JPanel();

		pause_play_button = new Button("Play");
		back_color_button = new Button("Background Color Toggle");
		back_color_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				colors.put(0, (float) Math.sin(6) * 0.5f + 0.5f);
				colors.put(1, (float) Math.cos(30) * 0.5f + 0.5f);
				colors.put(2, 0.0f);
				colors.put(3, 1.0f);

			}
		});
		axis_toggle_button = new Button("Light Switch");
		axis_toggle_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (light_toggle == false) {

					light_toggle = true;

				} else {

					light_toggle = false;

				}

			}

		});
		// setup main panel

		my_main_panel.setLayout(new BorderLayout());
		my_main_panel.add(my_control_panel, BorderLayout.SOUTH);
		// setup control panel

		my_control_panel.add(pause_play_button);
		pause_play_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (pause_play_button.getLabel().equals("Play")) {
					pause_play_button.setLabel("Pause");
					timer.start();
					// animator.start();
				} else {
					pause_play_button.setLabel("Play");
					timer.stop();
					// animator.stop();
				}

			}
		});
		my_control_panel.add(back_color_button);
		back_color_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		my_control_panel.add(axis_toggle_button);

		// setup canvas panel

		my_gl_canvas.addGLEventListener(this);
		my_gl_canvas.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				int sensitivity = 2;// view port only moves if mouse moves over
									// a certain speed
				if (e.getX() < savedPoint.x - sensitivity) // pan left
				{
					x -= SPEED;
				}
				if (e.getX() > savedPoint.x + sensitivity) // pan right
				{
					x += SPEED;
				}
				if (e.getY() < savedPoint.y + sensitivity) // pan down
				{
					y += SPEED;
				}
				if (e.getY() > savedPoint.y - sensitivity) // pan up
				{
					y -= SPEED;
				}

				savedPoint = e.getPoint();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		my_gl_canvas.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				// TODO Auto-generated method stub
				if (e.getWheelRotation() > 0) {
					z += 0.1f;
				} else {
					z -= 0.1f;
				}

			}
		});
		my_gl_canvas.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				// define action for each key
				if (e.getKeyCode() == KeyEvent.VK_S) {
					gl_camera.moveForward(-SPEED);
				}
				if (e.getKeyCode() == KeyEvent.VK_W) {
					gl_camera.moveForward(SPEED);
				}
				if (e.getKeyCode() == KeyEvent.VK_D) {
					gl_camera.strafe(SPEED);
				}
				if (e.getKeyCode() == KeyEvent.VK_A) {
					gl_camera.strafe(-SPEED);

				}
				if (e.getKeyCode() == KeyEvent.VK_E) {
					gl_camera.moveUp(-SPEED);
				}
				if (e.getKeyCode() == KeyEvent.VK_Q) {
					gl_camera.moveUp(SPEED);
				}
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					gl_camera.pitch(CAM_SPEED);

				}
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					gl_camera.pitch(-CAM_SPEED);
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					gl_camera.yaw(-CAM_SPEED);

				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					gl_camera.yaw(CAM_SPEED);
				}
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					if (toggle == false) {

						toggle = true;
						texture_flag = 0;

					} else {

						toggle = false;
						texture_flag = 1;

					}
				}

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});

		my_canvas_panel.add(my_gl_canvas);

		this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		this.setLayout(new BorderLayout());
		// NOTE: add GLCanvas to JPanel will not show but to JFrame is visible
		this.getContentPane().add(my_gl_canvas, BorderLayout.CENTER);
		this.getContentPane().add(my_main_panel, BorderLayout.SOUTH);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		timer = new Timer(DELAY_TIME_IN_MS, new TickCommand(this));
		// animator.add(my_gl_canvas);
		// animator.start();
		timer.start();

	}

}
