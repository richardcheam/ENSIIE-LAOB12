package figures;

import points.Point2D;

public class Rectangle extends AbstractFigure {
	
	protected Point2D pMin;
	protected Point2D pMax;
	protected String name;
	
	/* ------ Constructor ------ */
	
	public Rectangle() {
		this(0, 0, 0, 0);
	}
	
	public Rectangle(double x1, double y1, double x2, double y2) {
		if (x1 > x2 && y1 > y2) {
			pMax = new Point2D(x1, y1);
			pMin = new Point2D(x2, y2);
		}
		else {
			pMax = new Point2D(x2, y2);
			pMin = new Point2D(x1, y1);
		}
	}
	
	public Rectangle(Point2D p1, Point2D p2) {
//		this(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		if (p1.getX() > p2.getX() && p1.getY() > p2.getY()) {
			this.pMin = new Point2D(p2);
			this.pMax = new Point2D(p1);
		}
		else {
			this.pMin = new Point2D(p1);
			this.pMax = new Point2D(p2);
		}
	}
	
	public Rectangle(Rectangle r) {
		this(r.bottomLeft(), r.topRight());
	}
	
	/* ------ Fin Constructor ------ */
	
	/* ------ Methods ------ */
	
	public Point2D bottomLeft() {
		return this.pMin;
	}
	
	public Point2D topRight() {
		return this.pMax;
	}
	
	@Override
	public double width() {
		// TODO Auto-generated method stub
		return (pMax.getX() - pMin.getX());
	}

	@Override
	public double height() {
		// TODO Auto-generated method stub
		return (pMax.getY() - pMin.getY());
	}
	
	public void setWidth(double width) {
		if (width >= 0) {
			double centerX = this.getCenter().getX();
			pMin.setX(centerX - width/2);
			pMax.setX(centerX + width/2);
		}
//		else {
//			pMin.setX(centerX + width/2);
//			pMax.setX(centerX - width/2);
//		}
//		if (width >= 0) {
//			pMax.setX(pMin.getX() + width);
//		} 
//		else if (width < 0) {
//			pMin.setX(pMax.getX() + width);
//		}
//		else {
//			pMin.setX(0);
//			pMax.setX(0);
//		}
	}
	
	public void setHeight(double height) {
		if (height >= 0) {
			double centerY = this.getCenter().getY();
			pMin.setY(centerY - height/2);
			pMax.setY(centerY + height/2);
		}
	}
	
	@Override
	public Point2D getCenter() {
		// TODO Auto-generated method stub\
		double center_x = pMin.getX() + (this.width()/2);
		double center_y = pMin.getY() + (this.height()/2);
		Point2D center = new Point2D(center_x, center_y);
		return center;
	}


	@Override
	public Point2D getBoundingBoxCenter() {
		// TODO Auto-generated method stub
		return new Point2D(getCenter());
	}


	@Override
	public Figure move(double dx, double dy) {
		// TODO Auto-generated method stub
		pMin.setX(pMin.getX() + dx);
		pMin.setY(pMin.getY() + dy);
		pMax.setX(pMax.getX() + dx);
		pMax.setY(pMax.getY() + dy);
		return this;
	}

	@Override
	public boolean contains(Point2D p) {
		// TODO Auto-generated method stub
		if ( (p.getX() < pMax.getX() && p.getY() < pMax.getY()) && (p.getX() > pMin.getX() && p.getY() > pMin.getY()) ) {
			return true;
		}
		return false;
	}
	
	@Override
	public double area() {
		// TODO Auto-generated method stub
		return (height() * width());
	}

	@Override
	protected boolean equals(Figure f) {
		// TODO Auto-generated method stub
		if (f.area() == this.area()) {
			return true;
		}
		return false;
	}
	
	

}
