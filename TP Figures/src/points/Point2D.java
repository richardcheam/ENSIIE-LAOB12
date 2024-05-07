package points;

/**
 * Classe définissant un point du plan 2D
 * @author David Roussel
 */
public class Point2D
{
	// attributs d'instance --------------------------------------------------
	/**
	 * l'abcisse du point
	 */
	protected double x;
	/**
	 * l'ordonnée du point
	 */
	protected double y;

	// attributs de classe ---------------------------------------------------
	/**
	 * Compteur d'instances : le nombre de points actuellement instanciés
	 */
	protected static int nbPoints = 0;

	/**
	 * Constante servant à comparer deux points entre eux (à {@value} près). On
	 * comparera alors la distance entre deux points.
	 * @see #distance(Point2D)
	 * @see #distance(Point2D, Point2D)
	 */
	protected static final double epsilon = 1e-6;

	/*
	 * Constructeurs
	 */
	/**
	 * TODO Constructeur par défaut. Initialise un point à l'origine du repère [0.0,
	 * 0.0]
	 */
	public Point2D() {
		x = 0.0;
		y = 0.0;
		nbPoints++;
	}

	/**
	 * TODO Constructeur valué
	 * @param x l'abcisse du point à créer
	 * @param y l'ordonnée du point à créer
	 */
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
		nbPoints++;
	}

	/**
	 * TODO Constructeur de copie
	 * @param p le point dont il faut copier les coordonnées Il s'agit ici d'une
	 * copie profonde de manière à créer une nouvelle instance
	 * possédant les même caractéristiques que celle dont on copie
	 * les coordonnées.
	 */
	public Point2D(Point2D p) {
		this.x = p.x;
		this.y = p.y;
		nbPoints++;
	}

	/**
	 * Nettoyeur avant destruction Permet de décrémenter le compteur d'instances
	 */
	@Override
	protected void finalize()
	{
		nbPoints--;
	}

	/*
	 * TODO Accesseurs
	 * 	- get[X|Y]
	 * 	- set[X|Y]
	 * 	- getEpsilon
	 * 	- getNbPoints
	 * 	- Source > Generate Getters and Setters ...
	 */
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public void setX(double x) {
		this.x = x;
	}
	public void setY(double y) {
		this.y = y;
	}
	public static double getEpsilon(){
		return epsilon;
	}
	public static int getNbPoints(){
		return nbPoints;
	}

	/**
	 * Opérations sur un point
	 * @param dx le déplacement en x
	 * @param dy le déplacement en y
	 * @return renvoie la référence vers l'instance courante (this) de manière à
	 * pouvoir enchainer les traitements du style :
	 * unObjet.uneMéthode(monPoint.deplace(dx,dy))
	 */
	public Point2D move(double dx, double dy)
	{
		x += dx;
		y += dy;
		return this;
	}

	/*
	 * Méthodes de classe : opérations sur les points
	 */
	/**
	 * Calcul de l'écart en abcsisse entre deux points. Cet écart ne concerne
	 * pas plus le premier que le second point c'est pourquoi on en fait une
	 * méthode de classe.
	 * @param p1 le premier point
	 * @param p2 le second point
	 * @return l'écart en x entre les deux points
	 */
	protected static double dx(Point2D p1, Point2D p2)
	{
		return (p2.x - p1.x);
	}

	/**
	 * Calcul de l'écart en ordonnée entre deux points. Cet écart ne concerne
	 * pas plus le premier que le second point c'est pourquoi on en fait une
	 * méthode de classe.
	 * @param p1 le premier point
	 * @param p2 le second point
	 * @return l'écart en y entre les deux points
	 */
	protected static double dy(Point2D p1, Point2D p2)
	{
		return (p2.y - p1.y);
	}

	/**
	 * TODO Calcul de la distance 2D entre deux points. Cette distance ne concerne
	 * pas plus un point que l'autre c'est pourquoi on en fait une méthode de
	 * classe. Cette méthode utilise les méthodes {@link #dx(Point2D, Point2D)}
	 * et {@link #dy(Point2D, Point2D)} pour calculer la distance entre les
	 * points.
	 * @param p1 le premier point
	 * @param p2 le seconde point
	 * @return la distance entre les points p1 et p2
	 * @see #dx(Point2D, Point2D)
	 * @see #dy(Point2D, Point2D)
	 */
	public static double distance(Point2D p1, Point2D p2)
	{
		return Math.sqrt( (dy(p1, p2) * dy(p1, p2)) + (dx(p1, p2) * dx(p1, p2)) );
		//return Math.hypot(dx(p1, p2), dy(p1, p2));
	}
	
	

	/**
	 * TODO Calcul de distance 2D par rapport au point courant
	 * @param p l'autre point dont on veut calculer la distance
	 * @return la distance entre le point courant et le point p
	 * @see #distance(Point2D, Point2D)
	 */
	public double distance(Point2D p)
	{
		return Point2D.distance(this, p);
	}
	


	/*
	 * TODO Affichage contenu et test d'égalité
	 * 	- toString
	 * 	- equals avec Point2D puis avec Object
	 * 	- Source > Override/Implements Methods ...
	 */
	// toString est une méthode classique en Java, elle est présente
	// dans les objets de type Object, on pourra donc ainsi l'utiliser
	// dans une éventuelle Liste de points.
	
	public String toString(){
		return new String("x = " + this.x + " y = " + this.y);
	}
	public boolean equals(Point2D p){
		if (p == null) {
			return false;
		}
		if (this == p) {
			return true;
		}
		else return false;
	}
	public boolean equals(Object o){
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (getClass() == o.getClass()) {
			Point2D p = (Point2D)o;
			return distance(p) < epsilon;
		}
		else return false;
	}
}


