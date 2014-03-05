package commands;

import java.awt.event.ActionEvent;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.AbstractAction;

import a4.MyGLCanvas;

public class TickCommand extends AbstractAction {
	private MyGLCanvas mycanvas;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		mycanvas.my_gl_canvas.display();

	}

	public TickCommand(MyGLCanvas canvas) {
		mycanvas = canvas;
	}

}
