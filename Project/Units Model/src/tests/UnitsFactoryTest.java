package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import measures.MeasureType;
import measures.units.BaseSymbolicUnit;
import measures.units.Bounded;
import measures.units.BoundedBaseNumericUnit;
import measures.units.DecomposedUnit;
import measures.units.DerivedNumericUnit;
import measures.units.DerivedSymbolicUnit;
import measures.units.Unit;
import measures.units.UnitsFactory;

/**
 * UnitsFactory Test class
 * @author davidroussel
 */
@TestMethodOrder(OrderAnnotation.class)
public class UnitsFactoryTest
{
	/**
	 * The set of units provided by the factory
	 */
	private Set<Unit<Double>> testUnitsSet = null;

	/**
	 * Map <MeaureType, Integer> mapping measure type to expected number of units
	 * @see #BeforeAll
	 */
	private static Map<MeasureType, Integer> expectedUnitsNumbers = null;

	/**
	 * Map <MeaureType, Integer> mapping measure type to expected number of
	 * {@link DerivedNumericUnit}s per units set
	 * @see #BeforeAll
	 */
	private static Map<MeasureType, Integer> expectedDerivedNumericUnitsNumbers = null;

	/**
	 * Map <MeaureType, Integer> mapping measure type to expected number of
	 * Symbolic Units ({@link BaseSymbolicUnit} or {@link DerivedSymbolicUnit})
	 * per units set
	 * @see #BeforeAll
	 */
	private static Map<MeasureType, Integer> expectedSymbolicUnitsNumbers = null;

	/**
	 * Map <MeaureType, Integer> mapping measure type to expected number of
	 * {@link DecomposedUnit}s per units set
	 * @see #BeforeAll
	 */
	private static Map<MeasureType, Integer> expectedDecomposedUnitsNumbers = null;

	/**
	 * Measure types stream provider
	 * @return a stream of all {@link MeasureType}s
	 */
	private static Stream<MeasureType> measureTypesProvider()
	{
		return MeasureType.all().stream();
	}

	/**
	 * Setup before all tests
	 * @throws java.lang.Exception if setup fails
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception
	{
		int size = MeasureType.values().length;
		expectedUnitsNumbers = new HashMap<>(size);
		expectedUnitsNumbers.put(MeasureType.LENGTH, 9);
		expectedUnitsNumbers.put(MeasureType.AREA, 8);
		expectedUnitsNumbers.put(MeasureType.VOLUME, 11);
		expectedUnitsNumbers.put(MeasureType.WEIGHT, 6);
		expectedUnitsNumbers.put(MeasureType.SPEED, 10);
		expectedUnitsNumbers.put(MeasureType.PRESSURE, 8);
		expectedUnitsNumbers.put(MeasureType.DIRECTION, 3);
		expectedUnitsNumbers.put(MeasureType.TEMPERATURE, 3);
		expectedUnitsNumbers.put(MeasureType.TIME, 5);

		expectedDerivedNumericUnitsNumbers = new HashMap<>(size);
		expectedDerivedNumericUnitsNumbers.put(MeasureType.LENGTH, 8);
		expectedDerivedNumericUnitsNumbers.put(MeasureType.AREA, 7);
		expectedDerivedNumericUnitsNumbers.put(MeasureType.VOLUME, 10);
		expectedDerivedNumericUnitsNumbers.put(MeasureType.WEIGHT, 5);
		expectedDerivedNumericUnitsNumbers.put(MeasureType.SPEED, 7);
		expectedDerivedNumericUnitsNumbers.put(MeasureType.PRESSURE, 7);
		expectedDerivedNumericUnitsNumbers.put(MeasureType.DIRECTION, 1);
		expectedDerivedNumericUnitsNumbers.put(MeasureType.TEMPERATURE, 2);
		expectedDerivedNumericUnitsNumbers.put(MeasureType.TIME, 2);

		expectedSymbolicUnitsNumbers = new HashMap<>(size);
		expectedSymbolicUnitsNumbers.put(MeasureType.LENGTH, 0);
		expectedSymbolicUnitsNumbers.put(MeasureType.AREA, 0);
		expectedSymbolicUnitsNumbers.put(MeasureType.VOLUME, 0);
		expectedSymbolicUnitsNumbers.put(MeasureType.WEIGHT, 0);
		expectedSymbolicUnitsNumbers.put(MeasureType.SPEED, 1);
		expectedSymbolicUnitsNumbers.put(MeasureType.PRESSURE, 0);
		expectedSymbolicUnitsNumbers.put(MeasureType.DIRECTION, 1);
		expectedSymbolicUnitsNumbers.put(MeasureType.TEMPERATURE, 0);
		expectedSymbolicUnitsNumbers.put(MeasureType.TIME, 0);

		expectedDecomposedUnitsNumbers = new HashMap<>(size);
		expectedDecomposedUnitsNumbers.put(MeasureType.LENGTH, 0);
		expectedDecomposedUnitsNumbers.put(MeasureType.AREA, 0);
		expectedDecomposedUnitsNumbers.put(MeasureType.VOLUME, 0);
		expectedDecomposedUnitsNumbers.put(MeasureType.WEIGHT, 0);
		expectedDecomposedUnitsNumbers.put(MeasureType.SPEED, 1);
		expectedDecomposedUnitsNumbers.put(MeasureType.PRESSURE, 0);
		expectedDecomposedUnitsNumbers.put(MeasureType.DIRECTION, 0);
		expectedDecomposedUnitsNumbers.put(MeasureType.TEMPERATURE, 0);
		expectedDecomposedUnitsNumbers.put(MeasureType.TIME, 2);

		for (MeasureType type : MeasureType.all())
		{
			int nbUnits = expectedUnitsNumbers.get(type);
			int nbNumericDerivedUnits = expectedDerivedNumericUnitsNumbers.get(type);
			int nbSymbolicUnits = expectedSymbolicUnitsNumbers.get(type);
			int nbDecomposedUnits = expectedDecomposedUnitsNumbers.get(type);
			assertEquals(nbUnits,
			             (nbNumericDerivedUnits + nbSymbolicUnits + nbDecomposedUnits + 1),
			             "setup Before Class : unexpected numbers with " + type);
		}

		System.out.println("-------------------------------------------------");
		System.out.println("UnitsFactory tests");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Tear down after all tests
	 * @throws java.lang.Exception if teardown fails
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception
	{
		System.out.println("-------------------------------------------------");
		System.out.println("UnitsFactory tests end");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Setup before each test
	 * @throws java.lang.Exception if setup fails
	 */
	@BeforeEach
	void setUp() throws Exception
	{
	}

	/**
	 * Tear down after each test
	 * @throws java.lang.Exception if teardown fails
	 */
	@AfterEach
	void tearDown() throws Exception
	{
		testUnitsSet.clear();
		testUnitsSet = null;
	}
	/**
	 * Test method for {@link measures.units.UnitsFactory#getUnits(measures.MeasureType)}.
	 * Testing the number of provided units according to type
	 * @param type the type of units tested
	 */
	@ParameterizedTest
	@MethodSource("measureTypesProvider")
	@DisplayName("Units number")
	@Order(1)
	final void testUnitsNumber(MeasureType type)
	{
		String testName = "Units number(" + type + ")";
		System.out.println(testName);

		try
		{
			testUnitsSet = UnitsFactory.getUnits(type);
		}
		catch (NullPointerException | ParseException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}
		assertNotNull(testUnitsSet,
		              testName + " unexpected null units set");
		assertFalse(testUnitsSet.isEmpty(),
		              testName + " unexpected empty units set");
		int expectedUnitsNumber = expectedUnitsNumbers.get(type);
		assertTrue(testUnitsSet.size() >= expectedUnitsNumber,
		           testName + " Not enough units for type: expected at least "
		               + expectedUnitsNumber);
	}
	/**
	 * Test method for {@link measures.units.UnitsFactory#getUnits(measures.MeasureType)}.
	 * Testing each {@link MeasureType} has a single base unit
	 * @param type the type of units tested
	 */
	@ParameterizedTest
	@MethodSource("measureTypesProvider")
	@DisplayName("Single Base Unit")
	@Order(2)
	final void testBaseUnitsNumber(MeasureType type)
	{
		String testName = "Single Base Unit(" + type + ")";
		System.out.println(testName);

		try
		{
			testUnitsSet = UnitsFactory.getUnits(type);
		}
		catch (NullPointerException | ParseException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}
		assertNotNull(testUnitsSet,
		              testName + " unexpected null units set");
		assertFalse(testUnitsSet.isEmpty(),
		              testName + " unexpected empty units set");

		/*
		 * Each unit set shall contains exactly one (and only one) instance
		 * of BoundedBaseNumericUnit
		 */
		int boundedBaseNumericUnitCount = 0;
		for (Unit<Double> unit : testUnitsSet)
		{
			if (isBase(unit))
			{
				boundedBaseNumericUnitCount++;
			}
		}

		assertEquals(1,
		             boundedBaseNumericUnitCount,
		             testName + " unexpected number of BoundedBaseNumericUnits");
	}
	/**
	 * Test method for {@link measures.units.UnitsFactory#getUnits(measures.MeasureType)}.
	 * Testing the number of derived numeric units according to type
	 * @param type the type of units tested
	 */
	@ParameterizedTest
	@MethodSource("measureTypesProvider")
	@DisplayName("Derived Numeric Units")
	@Order(3)
	final void testDerivedNumericUnits(MeasureType type)
	{
		String testName = "Derived Numeric Units(" + type + ")";
		System.out.println(testName);

		try
		{
			testUnitsSet = UnitsFactory.getUnits(type);
		}
		catch (NullPointerException | ParseException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}
		assertNotNull(testUnitsSet,
		              testName + " unexpected null units set");
		assertFalse(testUnitsSet.isEmpty(),
		              testName + " unexpected empty units set");

		int nbDerivedNumeric = 0;
		for (Unit<Double> unit : testUnitsSet)
		{
			if (isDerivedNumeric(unit))
			{
				nbDerivedNumeric++;
			}
		}

		int expectedDerivedNumeric = expectedDerivedNumericUnitsNumbers.get(type);
		assertTrue(nbDerivedNumeric >= expectedDerivedNumeric,
		             testName + " unexpected Derived Numeric Units number, "
		             	+ "expected at least " + expectedDerivedNumeric
		             	+ " but was " + nbDerivedNumeric);
	}

	/**
	 * Test method for {@link measures.units.UnitsFactory#getUnits(measures.MeasureType)}.
	 * Testing ... according to type
	 * @param type the type of units tested
	 */
	@ParameterizedTest
	@MethodSource("measureTypesProvider")
	@DisplayName("Symbolic Units")
	@Order(4)
	final void testSymbolicUnits(MeasureType type)
	{
		String testName = "Symbolic Units(" + type + ")";
		System.out.println(testName);

		try
		{
			testUnitsSet = UnitsFactory.getUnits(type);
		}
		catch (NullPointerException | ParseException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}
		assertNotNull(testUnitsSet,
		              testName + " unexpected null units set");
		assertFalse(testUnitsSet.isEmpty(),
		              testName + " unexpected empty units set");

		int nbSymbolic = 0;
		for (Unit<Double> unit : testUnitsSet)
		{
			if (isSymbolic(unit))
			{
				nbSymbolic++;
			}
		}

		int expectedSymbolic = expectedSymbolicUnitsNumbers.get(type);
		assertTrue(nbSymbolic >= expectedSymbolic,
            testName + " unexpected Symbolic Units number, "
            	+ "expected at least " + expectedSymbolic
            	+ " but was " + nbSymbolic);
	}

	/**
	 * Test method for {@link measures.units.UnitsFactory#getUnits(measures.MeasureType)}.
	 * Testing ... according to type
	 * @param type the type of units tested
	 */
	@ParameterizedTest
	@MethodSource("measureTypesProvider")
	@DisplayName("Decomposed Units")
	@Order(5)
	final void testDecomposedUnits(MeasureType type)
	{
		String testName = "Decomposed Units(" + type + ")";
		System.out.println(testName);

		try
		{
			testUnitsSet = UnitsFactory.getUnits(type);
		}
		catch (NullPointerException | ParseException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}
		assertNotNull(testUnitsSet,
		              testName + " unexpected null units set");
		assertFalse(testUnitsSet.isEmpty(),
		              testName + " unexpected empty units set");

		int nbDecomposed = 0;
		for (Unit<Double> unit : testUnitsSet)
		{
			if (isDecomposed(unit))
			{
				nbDecomposed++;
			}
		}

		int expectedDecomposed = expectedDecomposedUnitsNumbers.get(type);
		assertTrue(nbDecomposed >= expectedDecomposed,
            testName + " unexpected Decomposed Units number, "
            	+ "expected at least " + expectedDecomposed
            	+ " but was " + nbDecomposed);
	}
	/**
	 * Test method for {@link measures.units.UnitsFactory#getUnits(measures.MeasureType)}.
	 * Testing ... according to type
	 * @param type the type of units tested
	 */
	@ParameterizedTest
	@MethodSource("measureTypesProvider")
	@DisplayName("Bounds")
	@Order(6)
	final void testBounds(MeasureType type)
	{
		String testName = "Bounds(" + type + ")";
		System.out.println(testName);

		try
		{
			testUnitsSet = UnitsFactory.getUnits(type);
		}
		catch (NullPointerException | ParseException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}
		assertNotNull(testUnitsSet,
		              testName + " unexpected null units set");
		assertFalse(testUnitsSet.isEmpty(),
		              testName + " unexpected empty units set");

		/*
		 * All LENGTH, AREA, VOLUME, WEIGHT, PRESSURE and TIME units are Bounded
		 * and have
		 * 	- min = 0.0
		 * 	- max = Positive infinity
		 */
		switch (type)
		{
			case LENGTH:
			case AREA:
			case VOLUME:
			case WEIGHT:
			case PRESSURE:
			{
				for (Unit<Double> unit : testUnitsSet)
				{
					if (unit instanceof Bounded<?>)
					{
						/*
						 * All length units have:
						 * 	- min = 0.0
						 * 	- max = Potitive infinity
						 */
						@SuppressWarnings("unchecked")
						Bounded<Double> bounded = (Bounded<Double>) unit;
						assertEquals(0.0,
						             bounded.getMin(),
						             testName + " unexpected minimum on unit "
						                 + unit);
						assertEquals(Double.POSITIVE_INFINITY,
						             bounded.getMax(),
						             testName + " unexpected maximum on unit "
						                 + unit);
					}
					else
					{
						fail(testName + " unexpected non bounded unit " + unit);
					}
				}
				break;
			}
			case SPEED:
			{
				/*
				 * Numeric Beaufort should have a 12 max value
				 */
				for (Unit<Double> unit : testUnitsSet)
				{
					if (unit instanceof Bounded<?>)
					{
						@SuppressWarnings("unchecked")
						Bounded<Double> bounded = (Bounded<Double>) unit;

						if (unit.getDescription().contains("Beaufort") &&
							((unit.getClass() == DerivedNumericUnit.class) ||
							 (unit.getClass() == DerivedSymbolicUnit.class)))
						{
							assertEquals(12.0,
							             bounded.getMax(),
							             testName
							                 + " unexpected maximum value for unit "
							                 + unit);
						}
						else
						{
							assertEquals(Double.POSITIVE_INFINITY,
							             bounded.getMax(),
							             testName
							                 + " unexpected maximum value for unit "
							                 + unit);
						}

					}
					else
					{
						if (!isDecomposed(unit))
						{
							fail(testName + " unexpected non bounded unit " + unit);
						}
					}
				}
				break;
			}
			case DIRECTION:
			{
				/*
				 * All Directions units are cyclic
				 * All Direction units have a non infinite max value
				 */
				for (Unit<Double> unit : testUnitsSet)
				{
					if (unit instanceof Bounded<?>)
					{
						@SuppressWarnings("unchecked")
						Bounded<Double> bounded = (Bounded<Double>) unit;
						assertTrue(bounded.isCyclic(),
						           testName
						               + " unexpected non cyclic direction unit "
						               + unit);
						assertNotEquals(Double.POSITIVE_INFINITY,
						                bounded.getMax(),
						                testName
						                    + " unexpected max value for unit "
						                    + unit);
						String description = unit.getDescription().toLowerCase();
						if (description.contains("degr"))
						{
							assertEquals(360.0,
							             bounded.getMax(),
							             testName
							                 + " unexpected max value for unit "
							                 + unit);
						}
						else if (description.contains("rad"))
						{
							assertEquals(Math.PI * 2.0,
							             bounded.getMax(),
							             testName
							                 + " unexpected max value for unit "
							                 + unit);
						}
					}
					else
					{
						fail(testName + " unit not bounded");
					}
				}
				break;
			}
			case TIME:
			{
				for (Unit<Double> unit : testUnitsSet)
				{
					if (unit instanceof Bounded<?>)
					{
						/*
						 * All length units have:
						 * 	- min = 0.0
						 * 	- max = Potitive infinity
						 */
						@SuppressWarnings("unchecked")
						Bounded<Double> bounded = (Bounded<Double>) unit;
						assertEquals(0.0,
						             bounded.getMin(),
						             testName + " unexpected minimum on unit "
						                 + unit);
						assertEquals(Double.POSITIVE_INFINITY,
						             bounded.getMax(),
						             testName + " unexpected maximum on unit "
						                 + unit);
					}
					else
					{
						if (!isDecomposed(unit))
						{
							fail(testName + " unexpected non bounded unit " + unit);
						}
					}
				}
				break;
			}
			default:
				break;
		}
	}
	/**
	 * Test method for {@link measures.units.UnitsFactory#getUnits(measures.MeasureType)}.
	 * Testing ... according to type
	 * @param type the type of units tested
	 */
	@ParameterizedTest
	@MethodSource("measureTypesProvider")
	@DisplayName("Expected Units")
	@Order(7)
	final void testExpectedUnits(MeasureType type)
	{
		String testName = "Expected Units(" + type + ")";
		System.out.println(testName);

		try
		{
			testUnitsSet = UnitsFactory.getUnits(type);
		}
		catch (NullPointerException | ParseException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}
		assertNotNull(testUnitsSet,
		              testName + " unexpected null units set");
		assertFalse(testUnitsSet.isEmpty(),
		              testName + " unexpected empty units set");

		switch (type)
		{
			case LENGTH:
			{
				assertTrue(searchFor("m", SearchScope.Symbol, 1, BoundedBaseNumericUnit.class),
				           testName + " unit \"m\" not found");
				assertTrue(searchFor("cm", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"cm\" not found");
				assertTrue(searchFor("mm", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"mm\" not found");
				assertTrue(searchFor("yd", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"yd\" not found");
				assertTrue(searchFor("mi", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"mi\" not found");
				assertTrue(searchFor("nm", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"nm\" not found");
				break;
			}
			case AREA:
			{
				assertTrue(searchFor("m²", SearchScope.Symbol, 1, BoundedBaseNumericUnit.class),
				           testName + " unit \"m²\" not found");
				assertTrue(searchFor("cm²", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"cm²\" not found");
				assertTrue(searchFor("km²", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"km²\" not found");
				assertTrue(searchFor("ha", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"ha\" not found");
				assertTrue(searchFor("ac", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"ac\" not found");
				assertTrue(searchFor("in²", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"in²\" not found");
				assertTrue(searchFor("ft²", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"ft²\" not found");
				break;
			}
			case VOLUME:
			{
				assertTrue(searchFor("m³", SearchScope.Symbol, 1, BoundedBaseNumericUnit.class),
				           testName + " unit \"m³\" not found");
				assertTrue(searchFor("cm³", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"cm³\" not found");
				assertTrue(searchFor("l", SearchScope.Symbol, 3, DerivedNumericUnit.class),
				           testName + " unit \"l\" not found");
				assertTrue(searchFor("cl", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"cl\" not found");
				assertTrue(searchFor("ml", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"ml\" not found");
				assertTrue(searchFor("gal", SearchScope.Symbol, 2, DerivedNumericUnit.class),
				           testName + " unit \"gal\" not found twice");
				assertTrue(searchFor("oz", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"oz\" not found");
				assertTrue(searchFor("spoon", SearchScope.Description, 2, DerivedNumericUnit.class) ||
				           searchFor("cuillère", SearchScope.Description, 2, DerivedNumericUnit.class),
				           testName + " unit \"spoon\" | \"cuillère\" not found twice");
				break;
			}
			case WEIGHT:
			{
				assertTrue(searchFor("Kilogramme", SearchScope.Description, 1, BoundedBaseNumericUnit.class),
				           testName + " unit \"Kilogramme\" not found in base unit");
				assertTrue(searchFor("gramme", SearchScope.Description, 2, DerivedNumericUnit.class),
				           testName + " unit \"gramme\" not found twice in derived units");
				assertTrue(searchFor("lb", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"lb\" not found in derived units");
				assertTrue(searchFor("ton", SearchScope.Description, 2, DerivedNumericUnit.class),
				           testName + " unit \"ton\" not found in derived units");
				break;
			}
			case SPEED:
			{
				assertTrue(searchFor("m/s", SearchScope.Symbol, 1, BoundedBaseNumericUnit.class),
				           testName + " unit \"m/s\" not found in base unit");
				assertTrue(searchFor("km/h", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"km/h\" not found in derived units");
				assertTrue(searchFor("mph", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"mph\" not found in derived units");
				assertTrue(searchFor("mpm", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"mpm\" not found in derived units");
				assertTrue(searchFor("kn", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"kn\" not found in derived units");
				assertTrue(searchFor("minute", SearchScope.Description, 3, DerivedNumericUnit.class),
				           testName + " unit \"minute per ...\" not found 3 times in derived units");
				/*
				 * Beaufort should be found twice in units descriptions
				 */
				assertTrue(searchFor("Beaufort", SearchScope.Description, 1, DerivedNumericUnit.class),
				           testName + " unit \"Beaufort\" was not found in Derived Numeric Units");
				assertTrue(searchFor("Beaufort", SearchScope.Description, 1, BaseSymbolicUnit.class) ||
				           searchFor("Beaufort", SearchScope.Description, 1, DerivedSymbolicUnit.class),
				           testName + " unit \"Beaufort\" was not found in Symbolic Units");
				break;
			}
			case PRESSURE:
			{
				assertTrue(searchFor("Pascal", SearchScope.Description, 1, BoundedBaseNumericUnit.class),
				           testName + " Pascal was not found in Base unit");
				assertTrue(searchFor("Pascal", SearchScope.Description, 2, null),
				           testName + " unit \"Pascal\" was not found twice in units");
				assertTrue(searchFor("Bar", SearchScope.Description, 2, DerivedNumericUnit.class),
				           testName + " unit \"Bar\" was not found twice in derived units");
				assertTrue(searchFor("Merc", SearchScope.Description, 2, DerivedNumericUnit.class),
				           testName + " unit \"Merc\" was not found twice in derived units");
				assertTrue(searchFor("psi", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"psi\" was not found in derived units");
				break;
			}
			case DIRECTION:
			{
				assertTrue(searchFor("Rad", SearchScope.Description, 1, BoundedBaseNumericUnit.class),
				           testName + " unit \"Rad\" was not found in Base unit");
				assertTrue(searchFor("°", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"°\" was not found in derived units");
				assertTrue(searchFor("Card", SearchScope.Description, 1, DerivedSymbolicUnit.class) ||
				           searchFor("Card", SearchScope.Description, 1, BaseSymbolicUnit.class),
				           testName + " unit \"Card\" was not found in symbolic units");
				break;
			}
			case TEMPERATURE:
			{
				assertTrue(searchFor("Kelvin", SearchScope.Description, 1, BoundedBaseNumericUnit.class),
				           testName + " unit \"Kelvin\" was not found in Base unit");
				assertTrue(searchFor("Celsius", SearchScope.Description, 1, DerivedNumericUnit.class),
				           testName + " unit \"Celsius\" was not found in Derived units");
				assertTrue(searchFor("Fahrenheit", SearchScope.Description, 1, DerivedNumericUnit.class),
				           testName + " unit \"Fahrenheit\" was not found in Derived units");
				break;
			}
			case TIME:
			{
				assertTrue(searchFor("s", SearchScope.Symbol, 1, BoundedBaseNumericUnit.class),
				           testName + " unit \"s\" was not found in Base unit");
				assertTrue(searchFor("m", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"m\" was not found in Base unit");
				assertTrue(searchFor("h", SearchScope.Symbol, 1, DerivedNumericUnit.class),
				           testName + " unit \"h\" was not found in Derived units");
				assertTrue(searchFor("heure", SearchScope.Description, 1, DecomposedUnit.class),
				           testName + " unit \"*heure*\" was not found in Decomposed units");
				assertTrue(searchFor("minute", SearchScope.Description, 2, DecomposedUnit.class),
				           testName + " unit \"*minute*\" was not found twice in Decomposed units");
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected type: " + type);
		}
	}

	/**
	 * Base status of provided unit
	 * @param unit the unit to investigate
	 * @return true is the provided unit is a base unit, false otherwise
	 */
	private static boolean isBase(Unit<Double> unit)
	{
		return unit.getClass() == BoundedBaseNumericUnit.class;
	}

	/**
	 * Symbolic status of provided unit
	 * @param unit the unit to investigate
	 * @return true is the provided unit is symbolic, false otherwise
	 */
	private static boolean isSymbolic(Unit<Double> unit)
	{
		Class<?> unitClass = unit.getClass();
		return (unitClass == BaseSymbolicUnit.class) ||
			(unitClass == DerivedSymbolicUnit.class);
	}

	/**
	 * Derived Numeric status of provided unit
	 * @param unit the unit to investigate
	 * @return true is the provided unit is a derived numeric unit, false otherwise
	 */
	private static boolean isDerivedNumeric(Unit<Double> unit)
	{
		return unit.getClass() == DerivedNumericUnit.class;
	}

	/**
	 * DEcomposed status of provided unit
	 * @param unit the unit to investigate
	 * @return true is the provided unit is a decomposed unit, false otherwise
	 */
	private static boolean isDecomposed(Unit<Double> unit)
	{
		return unit.getClass() == DecomposedUnit.class;
	}

	/**
	 * Search for occurrences of searched string in {@link #testUnitsSet}.
	 * Search is performed on all units of {@link #testUnitsSet} in both
	 * {@link Unit#getDescription()} and {@link Unit#getSymbol()}.
	 * @param searched the searched string
	 * @param scope The scope of search (description, symbol or both)
	 * @param occurrences the number of times the searched string is supposed to
	 * be found
	 * @param type the expected class of units to search in. If this parameter
	 * is null, then all classes are searched. Otherwise only the provided
	 * Unit class are searched
	 * @return true if the searched string has been found at least "occurences"
	 * times, false otherwise
	 */
	private boolean searchFor(String searched,
	                          SearchScope scope,
	                          int occurrences,
	                          Class<?> type)
	{
		int occurencesCount = 0;
		String lowerCasedSearched = searched.toLowerCase();
		if (!testUnitsSet.isEmpty())
		{
			for (Unit<Double> unit : testUnitsSet)
			{
				/*
				 * If type is provided then only search in instances of classes
				 * matching the provided type type
				 */
				if ((type != null) && (unit.getClass() != type))
				{
					continue;
				}
				boolean searchDescription = false;
				boolean searchSymbol = false;
				switch (scope)
				{
					case Description:
						searchDescription = true;
						break;
					case Symbol:
						searchSymbol = true;
						break;
					case Both:
						searchDescription = true;
						searchSymbol = true;
						break;
					default:
						throw new IllegalArgumentException("Unexpected value: "
						    + scope);
				}
				if (searchDescription)
				{
					if (unit.getDescription().toLowerCase().contains(lowerCasedSearched))
					{
						occurencesCount++;
					}
				}
				if (searchSymbol)
				{
					if (unit.getSymbol().toLowerCase().contains(lowerCasedSearched))
					{
						occurencesCount++;
					}
				}
			}
			return occurencesCount >= occurrences;
		}
		return false;
	}

	/**
	 * Enum defining the search scope of #searchFor
	 */
	private static enum SearchScope
	{
		/**
		 * Search in description only
		 */
		Description,
		/**
		 * Search in Symbol only
		 */
		Symbol,
		/**
		 * Search in description and symbol
		 */
		Both;
	}
}
