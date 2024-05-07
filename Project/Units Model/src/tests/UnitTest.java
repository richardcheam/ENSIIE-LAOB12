package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
import measures.units.BaseNumericUnit;
import measures.units.BaseSymbolicUnit;
import measures.units.Bounded;
import measures.units.BoundedBaseNumericUnit;
import measures.units.DecomposedUnit;
import measures.units.DerivedNumericUnit;
import measures.units.DerivedSymbolicUnit;
import measures.units.OperationOrder;
import measures.units.SortOrder;
import measures.units.Unit;
import measures.units.UnitsFactory;

/**
 * TestClass for all {@link Unit}s
 * @author davidroussel
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Unit<E>")
public class UnitTest
{
	/**
	 * The unit under test
	 */
	private Unit<Double> testUnit = null;

	/**
	 * A set of units to compare with {@link #testUnit}
	 * @see UnitsFactory#getUnits(measures.MeasureType)
	 */
	private Set<Unit<Double>> testUnits = null;

	/**
	 * The type of unit to test
	 */
	private Class<? extends Unit<Double>> testUnitType = null;

	/**
	 * The name of the unit type to test
	 */
	private String testUnitTypeName = null;

	/**
	 * The name of the current test in all testXXX methods
	 */
	private String testName = null;

	/**
	 * Different natures of units to test
	 */
	@SuppressWarnings("unchecked")
	private static final Class<? extends Unit<Double>>[] unitTypes =
		(Class<? extends Unit<Double>>[]) new Class<?>[] {
			BoundedBaseNumericUnit.class,
			BaseSymbolicUnit.class,
			DerivedNumericUnit.class,
			DerivedSymbolicUnit.class,
			DecomposedUnit.class
		};

	/**
	 * Measure Type for all test Units
	 */
	private static final MeasureType ExpectedMeasureType = MeasureType.SPEED;

	/**
	 * Hash Values for each unit type
	 */
	private static final int[] unitHash = new int[] {
		309292870,		// for BoundedBaseNumericUnit
		349354434,	// for BaseSymbolicUnit
		-1111454146,		// for DerivedNumericUnit
		1872746015,		// for DerivedSymbolicUnit
		-220513530		// for DecomposedUnit
	};

	/**
	 * hash map linking Unit classes to {@link #unitHash}
	 * @see #setUpBeforeClass()
	 */
	private static Map<Class<? extends Unit<Double>>, Integer> unitHashMap =
	    new HashMap<Class<? extends Unit<Double>>, Integer>();

	/**
	 * Descriptions values for each Unit class
	 */
	private static final String[] descriptions = new String[] {
		"Mètres / seconde",	// for BoundedBaseNumericUnit
		"Beaufort Symbolique",	// for BaseSymbolicUnit
		"Kilomètres / heure",	// for DerivedNumericUnit
		"Beaufort Symbolique",	// for DerivedSymbolicUnit
		"Minutes : Secondes / Kilomètre",	// for DecomposedUnit
	};

	/**
	 * Description map linking Unit classes to {@link #descriptions}
	 * @see #setUpBeforeClass()
	 */
	private static Map<Class<? extends Unit<Double>>, String> descriptionsMap =
	    new HashMap<Class<? extends Unit<Double>>, String>();

	/**
	 * Formats values for each Unit class
	 */
	private static final String[] formats = new String[] {
		BaseNumericUnit.DefaultFormat,	// for BoundedBaseNumericUnit
		"",								// for BaseSymbolicUnit
		"5.1",							// for DerivedNumericUnit
		"",								// for DerivedSymbolicUnit
		"",								// for DecomposedUnit
	};

	/**
	 * Formats map linking Unit classes to {@link #formats}
	 * @see #setUpBeforeClass()
	 */
	private static Map<Class<? extends Unit<Double>>, String> formatsMap =
	    new HashMap<Class<? extends Unit<Double>>, String>();

	/**
	 * Symbols values for each Unit class
	 */
	private static final String[] symbols = new String[] {
		"m/s",		// for BoundedBaseNumericUnit
		"",			// for BaseSymbolicUnit
		"km/h",		// for DerivedNumericUnit
		"",			// for DerivedSymbolicUnit
		"m:s/km",	// for DecomposedUnit
	};

	/**
	 * Symbols map linking Unit classes to {@link #symbols}
	 * @see #setUpBeforeClass()
	 */
	private static Map<Class<? extends Unit<Double>>, String> symbolsMap =
	    new HashMap<Class<? extends Unit<Double>>, String>();

	/**
	 * Setable flag for each Unit class
	 */
	private static final boolean[] setables = new boolean[] {
		true,	// for BoundedBaseNumericUnit
		false,	// for BaseSymbolicUnit
		true,	// for DerivedNumericUnit
		false,	// for DerivedSymbolicUnit
		false	// for DecomposedUnit
	};

	/**
	 * Setable map linking Unit classes to {@link #setables}
	 * @see #setUpBeforeClass()
	 */
	private static Map<Class<? extends Unit<Double>>, Boolean> setablesMap =
	    new HashMap<Class<? extends Unit<Double>>, Boolean>();

	/**
	 * Powers values for each Derived Unit class
	 */
	private static final Double[] powers = new Double[] {
		0.0,		// for BoundedBaseNumericUnit
		0.0,		// for BaseSymbolicUnit
		1.0,		// for DerivedNumericUnit
		3.0 / 2.0,	// for DerivedSymbolicUnit
		0.0,		// for DecomposedUnit
	};

	/**
	 * Powers map linking Unit classes to {@link #symbols}
	 * @see #setUpBeforeClass()
	 */
	private static Map<Class<? extends Unit<Double>>, Double> powersMap =
	    new HashMap<Class<? extends Unit<Double>>, Double>();

	/**
	 * Factors values for each Derived Unit class
	 */
	private static final Double[] factors = new Double[] {
		1.0,		// for BoundedBaseNumericUnit
		1.0,		// for BaseSymbolicUnit
		1.0/3.6,	// for DerivedNumericUnit
		0.83,		// for DerivedSymbolicUnit
		1.0,		// for DecomposedUnit
	};

	/**
	 * Factors map linking Unit classes to {@link #symbols}
	 * @see #setUpBeforeClass()
	 */
	private static Map<Class<? extends Unit<Double>>, Double> factorsMap =
	    new HashMap<Class<? extends Unit<Double>>, Double>();

	/**
	 * Offsets values for each Derived Unit class
	 */
	private static final Double[] offsets = new Double[] {
		0.0,	// for BoundedBaseNumericUnit
		0.0,	// for BaseSymbolicUnit
		0.0,	// for DerivedNumericUnit
		0.0,	// for DerivedSymbolicUnit
		0.0,	// for DecomposedUnit
	};

	/**
	 * Offsets map linking Unit classes to {@link #symbols}
	 * @see #setUpBeforeClass()
	 */
	private static Map<Class<? extends Unit<Double>>, Double> offsetsMap =
	    new HashMap<Class<? extends Unit<Double>>, Double>();

	/**
	 * Operation orders values for each Derived Unit class
	 */
	private static final OperationOrder[] orders = new OperationOrder[] {
		OperationOrder.NO_CHANGE,	// for BoundedBaseNumericUnit
		OperationOrder.NO_CHANGE,	// for BaseSymbolicUnit
		OperationOrder.FACTOR_ONLY,	// for DerivedNumericUnit
		OperationOrder.FACTOR_ONLY,	// for DerivedSymbolicUnit
		OperationOrder.NO_CHANGE,	// for DecomposedUnit
	};

	/**
	 * Operation orders map linking Unit classes to {@link #symbols}
	 * @see #setUpBeforeClass()
	 */
	private static Map<Class<? extends Unit<Double>>, OperationOrder> ordersMap =
	    new HashMap<Class<? extends Unit<Double>>, OperationOrder>();

	/**
	 * Minimum values for each Derived Unit class
	 */
	private static final Double[] minimums = new Double[] {
		0.0,	// for BoundedBaseNumericUnit
		0.0,	// for BaseSymbolicUnit
		0.0,	// for DerivedNumericUnit
		0.0,	// for DerivedSymbolicUnit
		0.0,	// for DecomposedUnit
	};

	/**
	 * Minimums map linking Unit classes to {@link #symbols}
	 * @see #setUpBeforeClass()
	 */
	private static Map<Class<? extends Unit<Double>>, Double> minimumsMap =
	    new HashMap<Class<? extends Unit<Double>>, Double>();

	/**
	 * Maximum values for each Derived Unit class
	 */
	private static final Double[] maximums = new Double[] {
		Double.POSITIVE_INFINITY,	// for BoundedBaseNumericUnit
		Double.POSITIVE_INFINITY,	// for BaseSymbolicUnit
		Double.POSITIVE_INFINITY,	// for DerivedNumericUnit
		12.0,						// for DerivedSymbolicUnit
		0.0							// for DecomposedUnit
	};

	/**
	 * Maximums map linking Unit classes to {@link #symbols}
	 * @see #setUpBeforeClass()
	 */
	private static Map<Class<? extends Unit<Double>>, Double> maximumsMap =
	    new HashMap<Class<? extends Unit<Double>>, Double>();


	/**
	 * Expected formated values for each Unit class
	 */
	private static final String[] formated = new String[] {
		"1.00",					// for BoundedBaseNumericUnit
		"Très légère brise",	// for BaseSymbolicUnit
		"1.0",					// for DerivedNumericUnit
		"Très légère brise",	// for DerivedSymbolicUnit
		"01m : 00s/km",			// for DecomposedUnit
	};

	/**
	 * expected formated map linking Unit classes to {@link #formated}
	 * @see #setUpBeforeClass()
	 */
	private static Map<Class<? extends Unit<Double>>, String> formatedMap =
	    new HashMap<Class<? extends Unit<Double>>, String>();

	/**
	 * Expected constructor arguments types for each Unit classes
	 */
	private static final Class<?>[][] argTypes = new Class<?>[][] {
		// for BoundedBaseNumericUnit
		new Class<?>[] {
			MeasureType.class,	// 0: type
			String.class, 		// 1: description
			String.class, 		// 2: symbol
			String.class, 		// 3: format
			Double.class, 		// 4: min
			Double.class, 		// 5: max
			boolean.class 		// 6: cyclic
		},
		// for BaseSymbolicUnit
		new Class<?>[] {
			MeasureType.class,	// 0: type
			String.class,		// 1: description
			String.class,		// 2: symbol
			double[].class,		// 3: values
			String[].class,		// 4: symbols
			boolean.class		// 5: cyclic
		},
		// for DerivedNumericUnit
		new Class<?>[] {
			MeasureType.class,		// 0: type
			String.class,			// 1: description
			String.class,			// 2: symbol
			String.class,			// 3: format
			Double.class, 			// 4: min
			Double.class,			// 5: max
			boolean.class,			// 6: cyclic
			double.class,			// 7: power
			double.class,			// 8: factor
			double.class,			// 9: offset
			OperationOrder.class	// 10: order
		},
		// for DerivedSymbolicUnit
		new Class<?>[] {
			MeasureType.class,		// 0: type
			String.class,			// 1: description
			String.class,			// 2: symbol
			String[].class,			// 3: symbols
			boolean.class,			// 4: cyclic
			double.class,			// 5: power
			double.class,			// 6: factor
			double.class,			// 7: offset
			OperationOrder.class,	// 8: order
		},
		// for DecomposedUnit
		new Class<?>[] {
			Unit.class,		// 0: unit
			String.class,	// 1: description
			String.class,	// 2: symbols
			String.class,	// 3: separator
			Double[].class	// 4: coefs
		}
	};

	/**
	 * Maximums map linking Unit classes to {@link #symbols}
	 * @see #setUpBeforeClass()
	 */
	private static Map<Class<? extends Unit<Double>>, List<Class<?>>> argTypesMap =
	    new HashMap<Class<? extends Unit<Double>>, List<Class<?>>>();

	/**
	 * Symbols for Beaufort scale
	 */
	private static final String[] beaufortSymbols = new String[] { "Calme",
	    "Très légère brise", "Légère brise", "Petite brise",
	    "Jolie brise ", "Bonne brise", "Vent frais", "Grand frais",
	    "Coup de vent", "Fort coup de vent", "Tempête",
	    "Violente tempête", "Ouragan" };

	/**
	 * Values for Beaufort scale
	 */
	private static final double[] beaufortValues =
	    new double[] { 0, 0.28, 1.53, 3.19, 5.42, 7.92, 10.69, 13.75, 17.08,
	        20.69, 24.31, 28.47, 32.64, Double.POSITIVE_INFINITY };

	/**
	 * {@link Unit} class provided for parameterized tests requiring the type
	 * of {@link Unit}
	 * @return a stream of {@link Unit} classes to use in each ParameterizedTest
	 */
	private static Stream<Class<? extends Unit<Double>>> unitClassesProvider()
	{
		return Stream.of(unitTypes);
	}

	/**
	 * check if the provided unit implements the provided interface
	 * @param unitType the unit Class to investigate
	 * @param inter the unit to check
	 * @return true if the provided unit or one of its ancestors implements
	 * the provided interface
	 */
	private static boolean implementsInterface(Class<? extends Unit<Double>> unitType, Class<?> inter)
	{
		Class<?> currentClass = unitType;
		while (currentClass != Unit.class)
		{
			Class<?>[] interfaces = currentClass.getInterfaces();
			for (Class<?> it : interfaces)
			{
				if (it == inter)
				{
					return true;
				}
			}
			currentClass = currentClass.getSuperclass();
		}
		return false;
	}

	/**
	 * {@link Unit} class provided for parameterized tests requiring the type
	 * of {@link Unit}
	 * @return a stream of {@link Unit} classes to use in each ParameterizedTest
	 */
	private static Stream<Class<? extends Unit<Double>>> boundedUnitClassesProvider()
	{
		return Stream.of(unitTypes).filter((Class<? extends Unit<Double>> c) -> {
			/*
			 * Keep only classes implementing the Bounded interface
			 */
			return implementsInterface(c, Bounded.class);
		});
	}

//	/**
//	 * Stream of SPEED {@link Unit}s
//	 * @return a stream of {@link Unit}s
//	 * @throws ParseException If some Units can't be built because format string
//	 * can't be parsed
//	 * @throws NullPointerException if some units can't be built because of
//	 * arguments are null
//	 */
//	private static Stream<Unit<Double>> unitsProvider()
//		throws NullPointerException,
//		ParseException
//	{
//		return UnitsFactory.getUnits(MeasureType.SPEED).stream();
//	}

//	/**
//	 * Stream of {@link Setable} SPEED {@link Unit}s
//	 * @return  a stream of Setable {@link Unit}s
//	 * @throws ParseException If some Units can't be built because format string
//	 * can't be parsed
//	 * @throws NullPointerException if some units can't be built because of
//	 * arguments are null
//	 */
//	private static Stream<Unit<Double>> setableUnitsProvider()
//		throws NullPointerException,
//		ParseException
//	{
//		return unitsProvider().filter((Unit<Double> unit) -> {
//			return unit.isSetable();
//		});
//	}


	/**
	 * Setup before all tests
	 * @throws java.lang.Exception if setup fails
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception
	{
		for (int i = 0; i < unitTypes.length; i++)
		{
			unitHashMap.put(unitTypes[i], unitHash[i]);
			descriptionsMap.put(unitTypes[i], descriptions[i]);
			symbolsMap.put(unitTypes[i], symbols[i]);
			formatsMap.put(unitTypes[i], formats[i]);
			setablesMap.put(unitTypes[i], setables[i]);
			powersMap.put(unitTypes[i], powers[i]);
			factorsMap.put(unitTypes[i], factors[i]);
			offsetsMap.put(unitTypes[i], offsets[i]);
			ordersMap.put(unitTypes[i], orders[i]);
			minimumsMap.put(unitTypes[i], minimums[i]);
			maximumsMap.put(unitTypes[i], maximums[i]);
			formatedMap.put(unitTypes[i], formated[i]);
			List<Class<?>> argList = new ArrayList<>();
			for (int j = 0; j < argTypes[i].length; j++)
			{
				argList.add(argTypes[i][j]);
			}
			argTypesMap.put(unitTypes[i], argList);
		}
		System.out.println("-------------------------------------------------");
		System.out.println("Units tests");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Tear down after all tests
	 * @throws java.lang.Exception if teardown fails
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception
	{
		maximumsMap.clear();
		minimumsMap.clear();
		ordersMap.clear();
		offsetsMap.clear();
		factorsMap.clear();
		powersMap.clear();
		symbolsMap.clear();
		descriptionsMap.clear();
		System.out.println("-------------------------------------------------");
		System.out.println("Units tests end");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Setup variables for a specific test.
	 * To be used in every testXXX(...) methods requiring {@code Lists<String>}
	 * instances.
	 * @param unit the {@link Unit} to test
	 * @param testName the name of the current test
	 * @throws NullPointerException if unit is null
	 */
	@SuppressWarnings("unchecked")
	void setUpTest(Unit<Double> unit, String testName)
	{
		if (unit == null)
		{
			throw new NullPointerException("null instance");
		}
		testUnit = unit;
		testUnitType = (Class<? extends Unit<Double>>) testUnit.getClass();
		testUnitTypeName = testUnitType.getSimpleName();
		this.testName = testUnitTypeName + "." + testName;
		System.out.println(this.testName);
	}

	/**
	 * Build a unit from provided unit class using the various maps
	 * ({@value #descriptionsMap}, {@value #symbolsMap}, {@link #formatsMap},
	 * {@link #powersMap}, {@link #factorsMap}, {@link #offsetsMap},
	 * {@link #ordersMap}, {@link #minimumsMap}, {@link #maximumsMap}
	 * @param type the type of unit to build
	 * @param testName The test name in which this method is used (for failure
	 * messages purposes)
	 * @return a newly created unit
	 */
	private static Unit<Double> buildUnit(Class<? extends Unit<Double>> type,
	                                      String testName)
	{
		List<Class<?>> argumentsTypesList = argTypesMap.get(type);
		final int size = argumentsTypesList.size();

		Constructor<? extends Unit<Double>> constructor = null;
		Class<?>[] argumentsTypes = argumentsTypesList.toArray(new Class<?>[size]);
		List<Object> argumentsList = new ArrayList<>();
		Object[] arguments = new Object[size];
		Object instance = null;

		/*
		 * All units have description and symbol as 2nd and 3rd arguments
		 */
		argumentsList.add(descriptionsMap.get(type));	// description
		argumentsList.add(symbolsMap.get(type));		// symbol

		if (type == DecomposedUnit.class)
		{
			//  We first need a unit to decompose
			Unit<Double> baseUnit = buildUnit(BoundedBaseNumericUnit.class, testName);
			// We don't need expected measuretype here
			argumentsList.add(0, baseUnit);					// Decomposed unit = 1st arg
			argumentsList.add(":");							// separator
			argumentsList.add( new Double[] {1.0, 60.0});	// coefficients
		}
		else // BoundedBaseNumericUnit, BaseSymbolicUnit, DerivedNumericUnit, DerivedSymbolicUnit
		{
			argumentsList.add(0, ExpectedMeasureType); // type = 1st arg

			if (type == BaseSymbolicUnit.class)
			{
				argumentsList.add(beaufortValues);	// values
				argumentsList.add(beaufortSymbols);	// symbols
				argumentsList.add(false);			// cyclic
			}
			else // BoundedBaseNumericUnit, DerivedNumericUnit, DerivedSymbolicUnit
			{
				if (type == DerivedSymbolicUnit.class)
				{
					argumentsList.add(beaufortSymbols);	// symbols
					argumentsList.add(false);			// cyclic
				}
				else // BoundedBaseNumericUnit, DerivedNumericUnit
				{
					argumentsList.add(formatsMap.get(type));	// format
					argumentsList.add(minimumsMap.get(type));	// min
					argumentsList.add(maximumsMap.get(type));	// max
					argumentsList.add(false);					// cyclic
				}

				if (type != BoundedBaseNumericUnit.class) // DerivedNumericUnit, DerivedSymbolicUnit
				{
					argumentsList.add(powersMap.get(type));		// power
					argumentsList.add(factorsMap.get(type));	// factor
					argumentsList.add(offsetsMap.get(type));	// offset
					argumentsList.add(ordersMap.get(type));		// order
				}
			}
		}

		if (argumentsList.size() != argumentsTypesList.size())
		{
			fail(testName + " unexpected argument list : " + argumentsList);
		}

		try
		{
			constructor = type.getConstructor(argumentsTypes);
		}
		catch (NoSuchMethodException e)
		{
			fail(testName + " constructor not found "
			    + e.getLocalizedMessage());
		}
		catch (SecurityException e)
		{
			fail(testName + " constructor security " + e.getLocalizedMessage());
		}

		if (constructor != null)
		{
			arguments = argumentsList.toArray();
			try
			{
				instance = constructor.newInstance(arguments);
			}
			catch (InstantiationException e)
			{
				fail(testName + " instanciation exception "
				    + e.getLocalizedMessage());
			}
			catch (IllegalAccessException e)
			{
				fail(testName + " illegal access "
				    + e.getLocalizedMessage());
			}
			catch (IllegalArgumentException e)
			{
				fail(testName + " wrong constructor arguments "
				    + e.getLocalizedMessage());
			}
			catch (InvocationTargetException e)
			{
				fail(testName + " invocation exception: " + e.getCause());
			}
		}

		@SuppressWarnings("unchecked")
		Unit<Double> unit = (Unit<Double>) instance;
		return unit;
	}

	/**
	 * Setup before each test
	 * @throws java.lang.Exception if setup fails
	 */
	@BeforeEach
	void setUp() throws Exception
	{
		if (testUnits != null)
		{
			testUnits.clear();
			testUnits = null;
		}
		testUnits = UnitsFactory.getUnits(MeasureType.SPEED);
	}

	/**
	 * Teardown after each test
	 * @throws java.lang.Exception if teardown fails
	 */
	@AfterEach
	void tearDown() throws Exception
	{
		testUnit = null;
		testUnits.clear();
		testUnit = null;
	}

	/**
	 * Test method for {@link measures.units.Unit#Unit(measures.MeasureType, java.lang.String, java.lang.String, boolean)}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("Valued Constructor")
	@Order(1)
	final void testUnit(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "Valued Constructor";
		Unit<Double> unitToTest = buildUnit(type, baseTestName);
		try
		{
			setUpTest(unitToTest, baseTestName);
		}
		catch (NullPointerException e)
		{
			fail(testName + " unexpected null instance "
			    + e.getLocalizedMessage());
		}

		assertNotNull(testUnit,
		              testName + " unexpected null instance");
	}

	/**
	 * Test method for {@link measures.units.Unit#getType()}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("getType()")
	@Order(2)
	final void testGetType(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "getType()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		assertEquals(ExpectedMeasureType,
		             testUnit.getType(),
		             testName + " unexpected Measure Type");
	}

	/**
	 * Test method for {@link measures.units.Unit#getDescription()}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("getDescription()")
	@Order(3)
	final void testGetDescription(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "getDescription()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		assertEquals(descriptionsMap.get(type),
		             testUnit.getDescription(),
		             testName + " unexpected description");
	}

	/**
	 * Test method for {@link measures.units.Unit#getSymbol()}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("getSymbol()")
	@Order(4)
	final void testGetSymbol(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "getSymbol()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		if (type != DecomposedUnit.class)
		{
			assertEquals(symbolsMap.get(type),
			             testUnit.getSymbol(),
			             testName + " unexpected symbol");
		}
		else
		{
			assertEquals("",
			             testUnit.getSymbol(),
			             testName + " unexpected symbol");
		}
	}

	/**
	 * Test method for {@link measures.units.Unit#hasValue()} before setting
	 * any values.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("hasValue() before setValue(...)")
	@Order(5)
	final void testHasValuePreSetValue(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "hasValue()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		assertFalse(testUnit.hasValue(),
		            testName + " unexpectedly has value");
	}

	/**
	 * Test method for {@link measures.units.Unit#hasValue()} after setting a
	 * value.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("hasValue() after setValue(...)")
	@Order(6)
	final void testHasValuePostSetValue(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "hasValue()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		boolean expected = false;
		if (testUnit.isSetable())
		{
			testUnit.setValue(1.0);
			expected = true;
		}

		assertEquals(expected,
		             testUnit.hasValue(),
		             testName + " unexpected has value");
	}

	/**
	 * Test method for {@link measures.units.Unit#getValue()} before setting any
	 * values.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("getValue() before setValue(...)")
	@Order(7)
	final void testGetValuePreSetValue(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "getValue()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		assertThrows(NoSuchElementException.class, () -> {
			testUnit.getValue();
		});
	}

	/**
	 * Test method for {@link measures.units.Unit#getValue()} after setting a
	 * value.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("getValue() after setValue(...)")
	@Order(8)
	final void testGetValuePostSetValue(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "getValue()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		double expectedValue = 1.0;
		if (testUnit.isSetable())
		{
			try
			{
				testUnit.setValue(expectedValue);
			}
			catch (UnsupportedOperationException e)
			{
				fail(testName + " unexpected UnsupportedOperationException");
			}
			assertEquals(expectedValue,
			             testUnit.getValue(),
			             testName + " unexpected value");
		}
		else
		{
			assertThrows(NoSuchElementException.class, () -> {
				testUnit.getValue();
			});
		}
	}

	/**
	 * Test method for {@link measures.units.Unit#getSIValue()} before setting
	 * any values.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("getSIValue() before setValue(...)")
	@Order(9)
	final void testGetSIValuePreSetValue(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "getSIValue()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		assertThrows(NoSuchElementException.class, () -> {
			testUnit.getSIValue();
		});
	}

	/**
	 * Test method for {@link measures.units.Unit#getSIValue()} after setting a
	 * value.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("getSIValue() after setValue(...)")
	@Order(10)
	final void testGetSIValuePostSetValue(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "getSIValue()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		double value = 1.0;
		if (testUnit.isSetable())
		{
			try
			{
				testUnit.setValue(value);
			}
			catch (UnsupportedOperationException e)
			{
				fail(testName + " unexpected UnsupportedOperationException");
			}
			double expectedValue = testUnit.getValue();
			if (type == DerivedNumericUnit.class)
			{
				OperationOrder order = ordersMap.get(type);
				double power = powersMap.get(type);
				double factor = factorsMap.get(type);
				double offset = offsetsMap.get(type);
				expectedValue = order.toSI(testUnit.getValue(),
				                           factor,
				                           offset,
				                           power);
			}
			assertEquals(expectedValue,
			             testUnit.getSIValue(),
			             testName + " unexpected value");
		}
		else
		{
			assertThrows(NoSuchElementException.class, () -> {
				testUnit.getValue();
			});
		}
	}

	/**
	 * Test method for {@link measures.units.Unit#isSetable()}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("isSetable()")
	@Order(11)
	final void testIsSetable(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "isSetable()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		boolean expected = true;
		if ((type != BoundedBaseNumericUnit.class) &&
			(type != DerivedNumericUnit.class))
		{
			expected = false;
		}

		assertEquals(expected,
		             testUnit.isSetable(),
		             testName + " unexpected setable status");
	}

	/**
	 * Test method for {@link measures.units.Unit#setValue(java.lang.Comparable)}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("setValue(Comparable)")
	@Order(12)
	final void testSetValue(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "setValue(Comparable)";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		List<Unit<Double>> testUnits = new ArrayList<Unit<Double>>();
		testUnits.add(testUnit);

		/*
		 * Add some cyclic units
		 */
		try
		{
			Unit<Double> angles = null;
			if (type == BoundedBaseNumericUnit.class)
			{
				angles = UnitsFactory
				    .getBoundedBaseNumericUnit(MeasureType.DIRECTION,
				                               "Radians",
				                               "",
				                               "9.5",
				                               0.0,
				                               (Math.PI * 2.0),
				                               true);
			}
			if (type == DerivedNumericUnit.class)
			{
				angles = UnitsFactory
				    .getDerivedNumericUnit(MeasureType.DIRECTION,
				                           "Degrés",
				                           "°",
				                           "4.1",
				                           0.0,
				                           360.0,
				                           true,
				                           1.0,
				                           Math.PI / 180.0,
				                           0.0,
				                           OperationOrder.FACTOR_ONLY);
			}
			if (angles != null)
			{
				testUnits.add(angles);
			}
		}
		catch (NullPointerException | ParseException e)
		{
			fail(testName + " failed to build custom unit");
		}

		for (Unit<Double> unit : testUnits)
		{
			if (unit.isSetable())
			{
				/*
				 * Set Value 1.0 (should be ok in all units)
				 */
				try
				{
					unit.setValue(1.0);
				}
				catch (UnsupportedOperationException e)
				{
					fail(testName + " unexpected UnsupportedOperationException "
					    + e.getLocalizedMessage());
				}

				if (implementsInterface(type, Bounded.class))
				{
					@SuppressWarnings("unchecked")
					Bounded<Double> testBounded = (Bounded<Double>) unit;
					double min = testBounded.getMin();
					double max = testBounded.getMax();
					if (min != Double.NEGATIVE_INFINITY)
					{
						unit.setValue(min - 1.0);
						Double expected = testBounded.isCyclic() ? max - 1.0 : min;
						assertEquals(expected,
						             unit.getValue(),
						             testName + " unexpected value");
					}
					if (max != Double.POSITIVE_INFINITY)
					{
						unit.setValue(max + 1.0);
						Double expected = testBounded.isCyclic() ? min + 1.0 : max;
						assertEquals(expected,
						             unit.getValue(),
						             testName + " unexpected value");
					}
				}
			}
			else
			{
				assertThrows(UnsupportedOperationException.class, () -> {
					unit.setValue(1.0);
				});
			}
		}
	}

	/**
	 * Test method for {@link measures.units.Unit#clearValue()}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("clearValue()")
	@Order(13)
	final void testClearValue(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "clearValue()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		if (testUnit.isSetable())
		{
			testUnit.setValue(1.0);
			assertTrue(testUnit.hasValue(),
			           testName + " unexpected has value status");
		}

		testUnit.clearValue();
		assertFalse(testUnit.hasValue(),
		            testName + " unexpected has value status");
	}

	/**
	 * Test method for {@link measures.units.Unit#formatValue()}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("formatValue() before setValue(...)")
	@Order(14)
	final void testFormatValuePreSetValue(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "formatValue()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		/*
		 * FIXME Wrong implementation in BaseNumericUnit only leads to
		 * null formatted value when there is no value to format
		 */
		if ((type == BoundedBaseNumericUnit.class) ||
			(type == DerivedNumericUnit.class) ||
			(type == BaseNumericUnit.class))
		{
			String formated = testUnit.formatValue();
			assertEquals(null,
			             formated,
			             testName + " unexpected non null formated value");
		}
		else
		{
			assertThrows(NoSuchElementException.class, () -> {
				testUnit.formatValue();
			});
		}
	}

	/**
	 * Test method for {@link measures.units.Unit#formatValue()}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("formatValue() after setValue(...)")
	@Order(15)
	final void testFormatValuePostSetValue(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "formatValue()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		if (testUnit.isSetable())
		{
			try
			{
				testUnit.setValue(1.0);
			}
			catch (UnsupportedOperationException e)
			{
				fail(testName + " unexpected UnsupportedOperationException "
				    + e.getLocalizedMessage());
			}
		}
		else
		{
			Unit<Double> baseUnit = buildUnit(BoundedBaseNumericUnit.class,
			                                  baseTestName);
			try
			{
				baseUnit.setValue(1.0);
			}
			catch (UnsupportedOperationException e)
			{
				fail(testName + " unexpected UnsupportedOperationException "
				    + e.getLocalizedMessage());
			}
			try
			{
				testUnit.convertValueFrom(baseUnit);
			}
			catch (IllegalStateException e)
			{
				fail(testName + " unexpected IllegalStateException "
				    + e.getLocalizedMessage());
			}
		}

		try
		{
			String formated = testUnit.formatValue();
			assertNotNull(formated,
			              testName + " unexpected null formated string");
			assertEquals(formatedMap.get(type),
			             formated,
			             testName + " unexpected formated string");
		}
		catch (NoSuchElementException e)
		{
			fail(testName + " unexpected NoSuchElementException "
			    + e.getLocalizedMessage());
		}
	}

	/**
	 * Test method for {@link measures.units.Unit#convertValueFrom(measures.units.Unit)}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("convertValueFrom(Unit)")
	@Order(16)
	final void testConvertValueFrom(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "convertValueFrom(Unit)";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		for (Unit<Double> unit : testUnits)
		{
			if (unit.isSetable())
			{
				unit.setValue(1.0);
				testUnit.convertValueFrom(unit);
				assertTrue(testUnit.hasValue());
				testUnit.convertValueTo(unit);
				double epsilon = 1e-6;
				assertEquals(unit.getSIValue(),
				             testUnit.getSIValue(),
				             epsilon,
				             testName + " unexpected SI value");
				if (testUnit instanceof DerivedSymbolicUnit)
				{
					/*
					 * FIXME this shall be investigated
					 */
					epsilon = 1e-1;
				}
				assertEquals(1.0,
				             unit.getValue().doubleValue(),
				             epsilon,
				             testName + " unexpected value");
				testUnit.clearValue();
				unit.clearValue();
			}
		}
	}

	/**
	 * Test method for {@link measures.units.Unit#convertValueTo(measures.units.Unit)}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("convertValueTo(Unit) before setValue(...)")
	@Order(17)
	final void testConvertValueToPreSetValue(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "convertValueTo(Unit)";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		for (Unit<Double> unit : testUnits)
		{
			assertThrows(IllegalStateException.class, () -> {
				/*
				 * No value can be converted to unit, so throw
				 * IllegalAccessException
				 */
				testUnit.convertValueTo(unit);
			});
		}
	}

	/**
	 * Test method for {@link measures.units.Unit#convertValueTo(measures.units.Unit)}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("convertValueTo(Unit) after setValue(...)")
	@Order(18)
	final void testConvertValueToPostSetvalue(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "convertValueTo(Unit)";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		for (Unit<Double> unit : testUnits)
		{
			if (testUnit.isSetable())
			{
				testUnit.setValue(1.0);
				testUnit.convertValueTo(unit);
				assertTrue(unit.hasValue());

				double epsilon = 1e-6;
				assertEquals(testUnit.getSIValue(),
				             unit.getSIValue(),
				             epsilon,
				             testName + " unexpected SI value");
				testUnit.convertValueFrom(unit);
				assertEquals(1.0,
				             testUnit.getValue().doubleValue(),
				             epsilon,
				             testName + " unexpected value");

				testUnit.clearValue();
				unit.clearValue();
			}
		}
	}

	/**
	 * Test method for {@link measures.units.Unit#compareTo(measures.units.Unit)}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("compareTo(Unit)")
	@Order(20)
	final void testCompareTo(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "compareTo(Unit)";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		for (SortOrder order : SortOrder.all())
		{
			Unit.setOrder(order);
			for (int i = 0; i < unitTypes.length; i++)
			{
				Unit<Double> unit = buildUnit(unitTypes[i], baseTestName);
				int comparison = testUnit.compareTo(unit);
				int descriptionComparison = testUnit.getDescription().compareTo(unit.getDescription());
				int symbolComparison = testUnit.getSymbol().compareTo(unit.getSymbol());
				int nameComparison = (descriptionComparison == 0 ? symbolComparison : descriptionComparison);
				int factorComparison = factorsMap.get(type).compareTo(factorsMap.get(unitTypes[i]));
				int expected = 0;
				switch (order)
				{
					case NAME_ASCENDING:
						expected = nameComparison == 0 ? factorComparison : nameComparison;
						break;
					case NAME_DESCENDING:
						expected = nameComparison == 0 ? -factorComparison : -nameComparison;
						break;
					case FACTOR_ASCENDING:
						expected = factorComparison == 0 ? nameComparison : factorComparison;
						break;
					case FACTOR_DESCENDING:
						expected = factorComparison == 0 ? -nameComparison : -factorComparison;
						break;
					default:
						fail(testName + " Unexpected order value " + order);
				}
				assertEquals(expected,
				             comparison,
				             testName + " unexpected comparison value");
			}
		}
	}

	/**
	 * Test method for {@link measures.units.Unit#hashCode()}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("hashCode()")
	@Order(21)
	final void testHashCode(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "hashCode()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

//		System.out.println("Unit: " + testUnit + " hash = " + testUnit.hashCode());

		final int prime = 31;
		int expected = 1;
		expected = (prime * expected) + type.hashCode();
		expected = (prime * expected) + testUnit.getType().hashCode();
		expected = (prime * expected) + testUnit.getDescription().hashCode();
		expected = (prime * expected) + testUnit.getSymbol().hashCode();

		final int hashValue = testUnit.hashCode();

		if (type != DecomposedUnit.class)
		{
			/*
			 * FIXME Unit#hashCode should not use attributes directly but
			 * rather use accessors (because DecomposedUnit specialized the
			 * getSymbol method)
			 */
			assertEquals(expected,
			             hashValue,
			             testName + " unexpected hash value");
		}

		assertEquals(unitHashMap.get(type),
		             hashValue,
		             testName + " unexpected hash value");
	}

	/**
	 * Test method for {@link measures.units.Unit#equals(java.lang.Object)}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("equals(Object)")
	@Order(22)
	final void testEqualsObject(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "equals(Object)";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		/*
		 * Compare with null is always false
		 */
		assertFalse(testUnit.equals(null),
		            testName + " unexpected equality with null");

		/*
		 * Compare with self is always true
		 */
		assertTrue(testUnit.equals(testUnit),
		           testName + " unexpected inequality with self");

		/*
		 * Compare with foreign oject is false
		 */
		assertFalse(testUnit.equals(new Object()),
		            testName + " unexpected equality with foreign object");

		/*
		 * For Units to be equal they should
		 * 	- Have the same comparison
		 * 	- Have the same type
		 * 	- Have the same description
		 * 	- Have the same symbol
		 */
		for (int i = 0; i < unitTypes.length; i++)
		{
			Unit<Double> unit = buildUnit(type, baseTestName);
			assertNotSame(testUnit,
			              unit,
			              testName + " unexpected same objects");
			if (testUnit.getClass() == unit.getClass())
			{
				assertEquals(0,
				             testUnit.compareTo(unit),
				             testName + " unexpected comparison value");
				assertEquals(testUnit.getType(),
				             unit.getType(),
				             testName + " unexpected type inequality");
				assertEquals(testUnit.getDescription(),
				             unit.getDescription(),
				             testName + " unexpected description inequality");
				assertEquals(testUnit.getSymbol(),
				             unit.getSymbol(),
				             testName + " unexpected description inequality");
				assertEquals(testUnit,
				             unit,
				             testName + " unexpected inequality with other unit");
			}
			else
			{
				/*
				 * FIXME inequality test is incomplete
				 */
				assertNotEquals(testUnit,
				                unit,
				                testName + " unexpected equality");

			}
		}
	}

	/**
	 * Test method for {@link measures.units.Unit#toString()}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("toString()")
	@Order(23)
	final void testToString(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "toString()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		StringBuilder builder = new StringBuilder();
		builder.append(descriptionsMap.get(type));
		String symbol = symbolsMap.get(type);
		if (!symbol.isEmpty())
		{
			builder.append(" (");
			builder.append(symbol);
			builder.append(')');
		}
		assertEquals(builder.toString(),
		             testUnit.toString(),
		             testName + " unexpected toString()");
	}

	/**
	 * Test method for {@link measures.units.Unit#getOrder()}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("unitClassesProvider")
	@DisplayName("getOrder() / setOrder(measures.units.SortOrder)")
	@Order(24)
	final void testGetAndSetOrder(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "getOrder()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		for (SortOrder order : SortOrder.all())
		{
			Unit.setOrder(order);
			assertEquals(order,
			             Unit.getOrder(),
			             testName + " unexpected order");
		}
	}

	/**
	 * Test method for {@link measures.units.Bounded#getMin()}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("boundedUnitClassesProvider")
	@DisplayName("getMin()")
	@Order(26)
	final void testGetMin(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "getMin()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		@SuppressWarnings("unchecked")
		Bounded<Double> bounded = (Bounded<Double>) testUnit;
		Double minimum = bounded.getMin();
		assertEquals(minimumsMap.get(type),
		             minimum,
		             testName + " unexpected minimum value");

		// check that if min is -infinity this unit is not cyclic
		if (minimum.equals(Double.NEGATIVE_INFINITY))
		{
			assertFalse(bounded.isCyclic(),
			            testName + " unexpected cyclic status");
		}
	}

	/**
	 * Test method for {@link measures.units.Bounded#getMax()}.
	 * @param type the type of unit to test
	 */
	@ParameterizedTest
	@MethodSource("boundedUnitClassesProvider")
	@DisplayName("getMax()")
	@Order(27)
	final void testGetMax(Class<? extends Unit<Double>> type)
	{
		String baseTestName = "getMax()";
		setUpTest(buildUnit(type, baseTestName), baseTestName);

		@SuppressWarnings("unchecked")
		Bounded<Double> bounded = (Bounded<Double>) testUnit;
		Double maximum = bounded.getMax();
		assertEquals(maximumsMap.get(type),
		             maximum,
		             testName + " unexpected maximum value");

		// check that if maw is +infinity this unit is not cyclic
		if (maximum.equals(Double.POSITIVE_INFINITY))
		{
			assertFalse(bounded.isCyclic(),
			            testName + " unexpected cyclic status");
		}
	}
}