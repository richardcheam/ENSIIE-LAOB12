import figures.Polygon;
import points.Point2D;

/**
 * Test des polygones
 * @author davidroussel
 *
 */
public class TestPoly
{

	/**
	 * Programme principal
	 * @param args arguments du programme (non utilisés)
	 */
	public static void main(String[] args)
	{
//		Polygone poly = new Polygone(new Point2D(3,2), new Point2D(7,3));
//		poly.ajouter(new Point2D(4,6));

		Polygon poly = new Polygon(new Point2D(5,1),
		                           new Point2D(8,2),
		                           new Point2D(7,5),
		                           new Point2D(2,4),
		                           new Point2D(2,3));

		Point2D centreM = poly.getCenter();
		System.out.println("centre de masse = " +  centreM);

		Point2D innerPoint = new Point2D(6,3);
		Point2D outerPoint = new Point2D(6,5);

		boolean in = poly.contains(innerPoint);
		boolean out = poly.contains(outerPoint);

		System.out.println("Le point " + innerPoint
				+ (in ? " est contenu" : " n'est pas contenu")
				+ " dans le polygone");

		System.out.println("Le point " + outerPoint
				+ (out ? " est contenu" : " n'est pas contenu")
				+ " dans le polygone");

	}

}
