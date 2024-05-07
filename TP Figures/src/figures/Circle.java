package figures;

import points.Point2D;

/**
 * Classe cercle héritière de la classe abstraite Figure doit donc implémenter
 * (entre-autres) les méthodes abstraites suivantes
 * @see AbstractFigure#move
 * @see AbstractFigure#contains
 * @see AbstractFigure#getCenter
 * @see AbstractFigure#area
 */
public class Circle extends AbstractFigure
{
	/**
	 * Centre
	 */
	protected Point2D center;

	/**
	 * Rayon
	 */
	protected double radius;

	// Constructeurs --------------------------------------------------------
	/**
	 * Constructeur par défaut.
	 * Crée un cercle de centre (0, 0) et de rayon 1.
	 */
	public Circle()
	{
		// super() est implicite
		// TODO Compléter ...
		this(0, 0, 1);
	}

	/**
	 * Constructeur valué : position + rayon
	 * @param x abcisse
	 * @param y ordonnée
	 * @param r rayon
	 */
	public Circle(double x, double y, double r)
	{
		// TODO Compléter ...
		center = new Point2D(x, y);
		radius = r;
	}

	/**
	 * constructeur valué : position + rayon
	 * @param p Point central
	 * @param r rayon
	 */
	public Circle(Point2D p, double r)
	{
		// TODO Compléter ...
		this(p.getX(), p.getY(), r);
	}

	/**
	 * Contructeur de copie à partir d'un autre cercle
	 * @param c le Cercel à copier
	 */
	public Circle(Circle c)
	{
		// TODO Compléter ...
		this(c.getCenter(), c.getRadius());
	}

	// Accesseurs -----------------------------------------------------------
	/**
	 * Accesseur en lecture pour le centre
	 * @return le point central du cercle
	 */
	@Override
	public Point2D getCenter()
	{
		// TODO Remplacer par l'implémentation ...
		if (this.center.getX() != 0 && this.center.getY() != 0) {
			return this.center;
		}
		return new Point2D(0.0, 0.0);
	}

	/**
	 * Accesseur en lecture du centre de la boite englobante
	 * @return le point central du cercle
	 * @see figures.Figure#getBoundingBoxCenter()
	 */
	@Override
	public Point2D getBoundingBoxCenter()
	{
		// TODO Remplacer par l'implémentation ...
//		if (this.center.getX() != 0 && this.center.getY() != 0) {
//			return this.center;
//		}
//		return new Point2D(0.0, 0.0);
		return new Point2D(getCenter());
	}

	/**
	 * Accesseur en lecture pour le rayon
	 * @return le rayon du cercle
	 */
	public double getRadius()
	{
		return radius;
	}

	/**
	 * Accesseur en écriture pour le rayon
	 * @param r rayon du cercle : si r est négatif le rayon est alors mis à 0.0
	 */
	public void setRadius(double r)
	{
		if (r < 0.0)
		{
			radius = 0.0;
		}
		else
		{
			radius = r;
		}
	}

	/**
	 * Déplacement du cercle = deplacement du centre
	 * @param dx déplacement suivant x
	 * @param dy déplacement suivant y
	 * @return une référence vers la figure déplacée
	 */
	@Override
	public Figure move(double dx, double dy)
	{
		// TODO Compléter ...
		center.setX(center.getX() + dx);
		center.setY(center.getY() + dy);
		return this;
	}

	/**
	 * Affichage contenu
	 * @return une chaine représentant l'objet (centre + rayon)
	 * @implSpec "name : centerpoint, r = radius"
	 */
	@Override
	public String toString()
	{	
		//TODO
		return new String(getName() + " : " + center + ", r = " + radius);
	}

	/**
	 * Test de contenu : teste si le point passé en argument est contenu à
	 * l'intérieur du cercle
	 * @param p point à tester
	 * @return une valeur booléenne indiquent si le point est contenu ou pas à
	 * l'intérieur du cercle
	 */
	@Override
	public boolean contains(Point2D p)
	{
		// TODO Remplacer par l'implémentation
//		if (p.getX() < radius && p.getY() < radius) {
//			return true;
//		}
		if (p.distance(center) < radius) {
			return true;
		}
		return false;
	}

	/**
	 * Largeur du cercle
	 * @return le rayon du cercle
	 */
	@Override
	public double width()
	{
		// TODO Remplacer par l'implémentation
		return (2 * radius);
	}

	/**
	 * Hauteur du cercle
	 * @return le rayon du cercle
	 */
	@Override
	public double height()
	{
		// TODO Remplacer par l'implémentation
		return (2 * radius);
	}

	/**
	 * Aire
	 * @return renvoie l'aire couverte par le cercle
	 */
	@Override
	public double area()
	{
		// TODO Remplacer par l'implémentation
		if (radius > 0) {
			return Math.PI * Math.pow(radius, 2);
		}
		return 0.0;
	}

	@Override
	public boolean equals(Figure figure)
	{
		// TODO Remplacer par l'implémentation
		if (figure.area() == this.area()) {
			return true;
		}
		return false;
	}
	
    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((center == null) ? 0 : center.hashCode());
        long temp;
        temp = Double.doubleToLongBits(radius);
        result = (prime * result) + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
