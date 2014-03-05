package a4;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

public class GLSLRenderer {
	private String fragmentShaderSource, fragShader;
	private String vertexShaderSource, vertexShader;
	private String geoShaderSource, geoShader;
	private String TCShaderSource, TCShader;
	private String TEShaderSource, TEShader;
	int[] vertCompiled = new int[1];
	int[] fragCompiled = new int[1];
	int[] geoCompiled = new int[1];
	int[] tcCompiled = new int[1];
	int[] teCompiled = new int[1];
	int[] linked = new int[1];
	private int rendering_program;
	public String getTCShader() {
		return TCShader;
	}

	public void setTCShader(String tCShader) {
		TCShader = tCShader;
	}

	public String getTEShader() {
		return TEShader;
	}

	public void setTEShader(String tEShader) {
		TEShader = tEShader;
	}

	public String getGeoShader() {
		return geoShader;
	}

	public void setGeoShader(String geoShader) {
		this.geoShader = geoShader;
	}

	GLU glu = new GLU();

	public String getFragShader() {
		return fragShader;
	}

	public void setFragShader(String fragShader) {
		this.fragShader = fragShader;
	}

	public String getVertexShader() {
		return vertexShader;
	}

	public void setVertexShader(String vertexShader) {
		this.vertexShader = vertexShader;
	}

	public GLSLRenderer() {
		super();

	}

	private byte[] getRGBAPixelData(BufferedImage img) {
		byte[] imgRGBA;
		int height = img.getHeight(null);
		int width = img.getWidth(null);
		// create an (empty) BufferedImage with Raster and ColorModel
		WritableRaster raster = Raster.createInterleavedRaster(
				DataBuffer.TYPE_BYTE, width, height, 4, null);
		ComponentColorModel colorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
						8, 8 }, // bits
				true, // hasAlpha
				false, // isAlphaPreMultiplied
				ComponentColorModel.TRANSLUCENT, // transparency
				DataBuffer.TYPE_BYTE); // data transfertype
		BufferedImage newImage = new BufferedImage(colorModel, raster, false, // isRasterPremultiplied
				null); // properties
		// Since Java expects images to have their origin at the upper left
		// while OpenGL expects the origin at the lower left,
		// we must flip the image upside down.
		// We create an AffineTransform to perform the flipping,
		// and use the Graphics object for the new image to draw
		// the old image into the new one, applying the AffineTransform
		// as it draws (i.e. "upside down" in the Java sense, which will
		// make it rightside up in the OpenGL sense).
		AffineTransform gt = new AffineTransform();
		gt.translate(0, height);
		gt.scale(1, -1d);
		Graphics2D g = newImage.createGraphics();
		g.transform(gt);
		g.drawImage(img, null, null); // draw into new image
		g.dispose();
		// retrieve the underlying byte array from the raster data buffer
		DataBufferByte dataBuf = (DataBufferByte) raster.getDataBuffer();
		imgRGBA = dataBuf.getData();
		return imgRGBA;
	}

	private BufferedImage getBufferedImage(String fileName) {
		BufferedImage img;
		try {
			File temp = new File(fileName);

			System.out.println(temp.getCanonicalFile());
			img = ImageIO.read(temp);

		} catch (IOException e) {
			System.err.println("Error reading '" + fileName + '"');
			throw new RuntimeException(e);
		}
		return img;
	}

	public int loadTexture(GLAutoDrawable drawable, String texFile) {
		GL gl = drawable.getGL();
		String fullName = "." + File.separator + texFile;
		BufferedImage textureImage = getBufferedImage(fullName);
		System.out.println("Texture file: '" + fullName + "', " + "size = "
				+ textureImage.getWidth() + " x " + textureImage.getHeight());
		byte[] imgRGBA = getRGBAPixelData(textureImage);
		ByteBuffer wrappedRGBA = ByteBuffer.wrap(imgRGBA);
		// reserve an integer texture ID (name)
		int[] textureIDs = new int[1];
		gl.glGenTextures(1, textureIDs, 0);
		int textureID = textureIDs[0];
		// make the textureID the "current texture"
		// gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, textureID);
		// attach image texture to the currently active OpenGL texture ID
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, // MIPMAP Level
				GL.GL_RGBA, // number of color components
				textureImage.getWidth(), // image size
				textureImage.getHeight(), 0, // border size in pixels
				GL.GL_RGBA, // pixel format
				GL.GL_UNSIGNED_BYTE, // pixel data type
				wrappedRGBA // buffer holding texture data
		);
		gl.glGenerateMipmap(GL.GL_TEXTURE_2D);
		// enable linear filtering for minification
		// (or else default is MUST use MIPMaps...)
		// gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
		// GL.GL_LINEAR);
		return textureID;
	}

	public int createShaderProgram(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		// read GLSL source from files and save to String
		vertexShaderSource = loadShaderSource(vertexShader);
		fragmentShaderSource = loadShaderSource(fragShader);
		int gShader,tcShader,teShader;
		
		rendering_program = gl.glCreateProgram();
		if(geoShader!=null){
			geoShaderSource = loadShaderSource(geoShader);
			gShader = gl.glCreateShader(GL4.GL_GEOMETRY_SHADER);
			// String array with one element contains all the GLSL vertex shader
			// code
			gl.glShaderSource(gShader, 1, new String[] { geoShaderSource }, null);
			gl.glCompileShader(gShader);
			// Check for compilation errors
			checkGlErrors(drawable);
			gl.glGetShaderiv(gShader, GL4.GL_COMPILE_STATUS, geoCompiled, 0);
			if (geoCompiled[0] == 1) {
				System.out.println("GEO compilation succeeded");
			} else {
				System.out.println("GEO compilation failed");
				printShaderLog(drawable, gShader);
			}
			gl.glAttachShader(rendering_program, gShader);
		}
		
		if(TCShader!=null){
			TCShaderSource = loadShaderSource(TCShader);
			tcShader = gl.glCreateShader(GL4.GL_TESS_CONTROL_SHADER);
			// String array with one element contains all the GLSL vertex shader
			// code
			gl.glShaderSource(tcShader, 1, new String[] { TCShaderSource }, null);
			gl.glCompileShader(tcShader);
			// Check for compilation errors
			checkGlErrors(drawable);
			gl.glGetShaderiv(tcShader, GL4.GL_COMPILE_STATUS, tcCompiled, 0);
			if (tcCompiled[0] == 1) {
				System.out.println("TCS compilation succeeded");
			} else {
				System.out.println("TCS compilation failed");
				printShaderLog(drawable, tcShader);
			}
			gl.glAttachShader(rendering_program, tcShader);
		}
		
		if(TEShader!=null){
			TEShaderSource = loadShaderSource(TEShader);
			teShader = gl.glCreateShader(GL4.GL_TESS_EVALUATION_SHADER);
			// String array with one element contains all the GLSL vertex shader
			// code
			gl.glShaderSource(teShader, 1, new String[] { TEShaderSource }, null);
			gl.glCompileShader(teShader);
			// Check for compilation errors
			checkGlErrors(drawable);
			gl.glGetShaderiv(teShader, GL4.GL_COMPILE_STATUS, teCompiled, 0);
			if (teCompiled[0] == 1) {
				System.out.println("TES compilation succeeded");
			} else {
				System.out.println("TES compilation failed");
				printShaderLog(drawable, teShader);
			}
			gl.glAttachShader(rendering_program, teShader);
		}

		int vShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
		// String array with one element contains all the GLSL vertex shader
		// code
		gl.glShaderSource(vShader, 1, new String[] { vertexShaderSource }, null);
		gl.glCompileShader(vShader);
		// Check for compilation errors
		checkGlErrors(drawable);
		gl.glGetShaderiv(vShader, GL4.GL_COMPILE_STATUS, vertCompiled, 0);
		if (vertCompiled[0] == 1) {
			System.out.println("Vertex compilation succeeded");
		} else {
			System.out.println("Vertex compilation failed");
			printShaderLog(drawable, vShader);
		}
		
		

		int fShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, 1, new String[] { fragmentShaderSource },
				null);
		gl.glCompileShader(fShader);
		checkGlErrors(drawable);
		gl.glGetShaderiv(fShader, GL4.GL_COMPILE_STATUS, fragCompiled, 0);
		if (fragCompiled[0] == 1) {
			System.out.println("Frag compilation succeeded");
		} else {
			System.out.println("Frag compilation failed");
			printShaderLog(drawable, fShader);
		}
		if ((vertCompiled[0] != 1) || (fragCompiled[0] != 1)) {
			System.out.println("\n Error in compilation;return-flags:");
			System.out.println("vertComplied=" + vertCompiled[0]
					+ ";fragCompiled=" + fragCompiled[0]);
		} else {
			System.out.println("Successful compilation");
		}
		// create a render program
		
		// Attach the shaders to the render program
		gl.glAttachShader(rendering_program, vShader);
		gl.glAttachShader(rendering_program, fShader);
		gl.glLinkProgram(rendering_program);
		// Checking for linking errors
		checkGlErrors(drawable);
		gl.glGetProgramiv(rendering_program, GL3.GL_LINK_STATUS, linked, 0);
		if (linked[0] == 1) {
			System.out.println("Linking succeeded");
		} else {
			System.out.println("Linking failed");
			printProgramLog(drawable, rendering_program);
		}
		gl.glDeleteShader(fShader);
		gl.glDeleteShader(vShader);

		return rendering_program;
	}

	private boolean checkGlErrors(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		boolean foundErr = false;
		int glErr = gl.glGetError();
		while (glErr != GL.GL_NO_ERROR) {
			System.err.println("glError: " + glu.gluErrorString(glErr));
			foundErr = true;
			glErr = gl.glGetError();

		}
		return foundErr;
	}

	private void printShaderLog(GLAutoDrawable d, int shader) {
		GL4 gl = d.getGL().getGL4();
		int[] len = new int[1];
		int[] charsWritten = new int[1];
		byte[] log = null;
		gl.glGetShaderiv(shader, GL4.GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0) {
			log = new byte[len[0]];
			gl.glGetShaderInfoLog(shader, len[0], charsWritten, 0, log, 0);
			System.out.println("Shader Info Log:");
			for (int i = 0; i < log.length; i++) {
				System.out.print((char) log[i]);
			}
		}
	}

	private void printProgramLog(GLAutoDrawable d, int prog) {
		GL4 gl = d.getGL().getGL4();
		int[] len = new int[1];
		int[] charsWritten = new int[1];
		byte[] log = null;
		gl.glGetProgramiv(prog, GL4.GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0) {
			log = new byte[len[0]];
			gl.glGetProgramInfoLog(prog, len[0], charsWritten, 0, log, 0);
			System.out.println("Shader Info Log:");
			for (int i = 0; i < log.length; i++) {
				System.out.print((char) log[i]);
			}
		}
	}

	public int getRendering_program() {
		return rendering_program;
	}

	// read shader source from a file
	private String loadShaderSource(String fileName) {
		Vector<String> lines = new Vector<String>();

		String currLine;
		int size = 0;

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			try {
				while ((currLine = br.readLine()) != null) {
					lines.addElement(currLine);
					size++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder program = new StringBuilder();
		for (int i = 0; i < size; i++) {
			program.append(lines.elementAt(i) + "\n");

		}
		return program.toString();
	}

}
