package points;

public class Point3D extends Point2D {

	protected double z;
	
	public Point3D(double x, double y, double z) {
		super(x, y);
		this.z = z;
	}
	
	public Point3D() {
		super(0.0, 0.0);
		this.z = 0.0;
	}
	
	public Point3D(Point2D p) {
		this(p.x, p.y, 0.0);
	}
		
	public Point3D(Point3D p) {
		this(p.x, p.y, p.z);	
	}
	
	public double getZ() {
		return this.z;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
	
	public String toString() {
		return new String(super.toString() + " z = " + z);
	}
	
	public Point3D move(double dx, double dy, double dz) {
		super.move(dx, dy);
		z += dz;
		return this;
	}
	
	protected static double dz(Point3D p1, Point3D p2) {
		return (p2.z - p1.z);
	}
	
	public static double distance(Point3D p1, Point3D p2) {
//		double dx = p2.x - p1.x;
//		double dy = p2.y - p1.y;
//		double dz = p2.z - p1.z;
//		double tmp = Math.hypot(dx, dy);
		return Math.hypot(Point2D.distance(p1, p2), Point3D.dz(p1, p2));
	}
	
	public double distance(Point3D p) {
		return Point3D.distance(this, p);
	}
	
	protected boolean equals(Point3D p) {
		if (p == null) {
			return false;
		}
		if (p == this) {
			return true;
		}
		else return false;
	}
	
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(o == this) {
			return true;
		}
		if(this.getClass() == o.getClass()) {
			Point3D p = (Point3D)o;
			return distance(p) < epsilon;
		}
		else if(o instanceof Point2D && Math.abs(z) < epsilon) {
			return true;
		}
		else return false;
	}
	
}
