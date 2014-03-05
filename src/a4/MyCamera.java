package a4;

import graphicslib3D.Camera;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

public class MyCamera extends Camera {
	public Matrix3D perspective(float fovy, float aspect, float n, float f) {
		float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
		float A = q / aspect;
		float B = (n + f) / (n - f);
		float C = (2.0f * n * f) / (n - f);
		Matrix3D r = new Matrix3D();
		r.setElementAt(0, 0, A);
		r.setElementAt(0, 1, 0.0f);
		r.setElementAt(0, 2, 0.0f);
		r.setElementAt(0, 3, 0.0f);
		r.setElementAt(1, 0, 0.0f);
		r.setElementAt(1, 1, q);
		r.setElementAt(1, 2, 0.0f);
		r.setElementAt(1, 3, 0.0f);
		r.setElementAt(2, 0, 0.0f);
		r.setElementAt(2, 1, 0.0f);
		r.setElementAt(2, 2, B);
		r.setElementAt(2, 3, -1.0f);
		r.setElementAt(3, 0, 0.0f);
		r.setElementAt(3, 1, 0.0f);
		r.setElementAt(3, 2, C);
		r.setElementAt(3, 3, 0.0f);
		r = r.transpose();
		return r;
	}

	public Matrix3D lookAt(graphicslib3D.Point3D eyeP,
			graphicslib3D.Point3D centerP, Vector3D upV) {
		Vector3D eyeV = new Vector3D(eyeP);
		Vector3D cenV = new Vector3D(centerP);
		Vector3D f = (cenV.minus(eyeV)).normalize();
		Vector3D sV = (f.cross(upV)).normalize();
		Vector3D nU = (sV.cross(f)).normalize();
		Matrix3D l = new Matrix3D();
		l.setElementAt(0, 0, sV.getX());
		l.setElementAt(0, 1, nU.getX());
		l.setElementAt(0, 2, -f.getX());
		l.setElementAt(0, 3, 0.0f);
		l.setElementAt(1, 0, sV.getY());
		l.setElementAt(1, 1, nU.getY());
		l.setElementAt(1, 2, -f.getY());
		l.setElementAt(1, 3, 0.0f);
		l.setElementAt(2, 0, sV.getZ());
		l.setElementAt(2, 1, nU.getZ());
		l.setElementAt(2, 2, -f.getZ());
		l.setElementAt(2, 3, 0.0f);
		l.setElementAt(3, 0, sV.dot(eyeV.mult(-1)));
		l.setElementAt(3, 1, nU.dot(eyeV.mult(-1)));
		l.setElementAt(3, 2, (f.mult(-1)).dot(eyeV.mult(-1)));
		l.setElementAt(3, 3, 1.0f);
		return (l.transpose());
	}

}
