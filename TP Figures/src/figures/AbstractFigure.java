package figures;

import points.Point2D;

/**
 * Classe abstraite Figure Contient une données concrête : le nom de la figure (
 * {@link #name} )
 * <ul>
 * <li>des méthodes d'instance</li>
 * <ul>
 * <li>concrètes
 * <ul>
 * <li>un constructeur avec un nom : {@link #AbstractFigure(String)}</li>
 * <li>un accesseur pour ce nom : {@link #getName()}</li>
 * <li>la méthode toString pour afficher ce nom {@link #toString()}</li>
 * <li>{@link #distanceToCenter(Figure)}</li>
 * </ul>
 * <li>abstraites
 * <ul>
 * <li>{@link #move(double,double)}</li>
 * <li>{@link #contains(Point2D)}</li>
 * <li>{@link #getCenter()}</li>
 * <li>{@link #area()}</li>
 * </ul>
 * </ul>
 * <li>des méthodes de classes</li>
 * <ul>
 * <li>concrètes</li>
 * <ul>
 * <li>{@link #distanceToCenter(Figure,Figure)}</li>
 * </ul>
 * </ul>
 * </ul>
 * @author David Roussel
 */
public abstract class AbstractFigure implements Figure
{
	/**
	 * Nom de la figure
	 */
	protected String name;

	/**
	 * Constructeur (protégé) par défaut.
	 * Affecte le nom de la classe comme nom de figure
	 * @see Class#getSimpleName()
	 */
	protected AbstractFigure()
	{
		name = getClass().getSimpleName();
//		this(getClass().getSimpleName()); // Impossible d'appeler une méthode d'instance sur une instance pas encore instanciée !!!
	}

	/**
	 * Constructeur (protégé) avec un nom
	 * on a fait exprès de ne pas mettre de constructeur sans arguments
	 * @param aName Chaine de caractère pour initialiser le nom de la
	 * figure
	 */
	protected AbstractFigure(String aName)
	{
		name = aName;
	}

	/**
	 * @return le nom
	 * @see figures.Figure#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see figures.Figure#deplace(double, double)
	 */
	@Override
	public abstract Figure move(double dx, double dy);

	/*
	 * (non-Javadoc)
	 * @see figures.Figure#toString()
	 * @implSpec "name : "
	 */
	@Override
	public String toString()
	{
		return (name + " : ");
	}

	/*
	 * (non-Javadoc)
	 * @see figures.Figure#contient(points.Point2D)
	 */
	@Override
	public abstract boolean contains(Point2D p);

	/*
	 * (non-Javadoc)
	 * @see figures.Figure#getCentre()
	 */
	@Override
	public abstract Point2D getCenter();

	/*
	 * (non-Javadoc)
	 * @see figures.Figure#aire()
	 */
	@Override
	public abstract double area();

	/**
	 * Comparaison de deux figures en termes de contenu
	 * @param f la figure dont on veut tester l'égalité avec soi-même
	 * @return true si f est du même types que la figure courante et qu'elles
	 * ont un contenu identique
	 */
	protected abstract boolean equals(Figure f);

	/**
	 * Comparaison de deux figures, on ne peut pas vérifier grand chose pour
	 * l'instant à part la classe et le nom
	 * @note implémentation partielle qui ne vérifie que null/this/et l'égalité
	 * de classe
	 * @see figures.Figure#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		// TODO Remplacer par l'implémentation utilisant la méthode ci-dessus...
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		else if (this.getClass() == obj.getClass()) {
			return true;
		}
		return false;
	}
}
