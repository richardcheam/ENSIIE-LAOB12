package tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
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

import collections.CollectionFactory;
import collections.MyArrayCollection;
import collections.MyDummyCollection;
import collections.MyLinkedCollection;
import collections.utils.Capacity;

/**
 * A Class to test all kinds of {@link Collection}s
 * @author davidroussel
 *
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Collection<E>")
public class CollectionTest
{
	/**
	 * The collection under test
	 */
	private Collection<String> testCollection;

	/**
	 * The type of collection to test
	 */
	private Class<? extends Collection<String>> testCollectionType;

	/**
	 * The name of the type of collection to test
	 */
	private String testCollectionTypeName;

	/**
	 * The name of the current test in all testXXX methods
	 */
	private String testName;

	/**
	 * Elements to fill collections :
	 * "Lorem ipsum dolor sit amet"
	 */
	private static final String[] elements1 = new String[] {
		"Lorem",
		"ipsum",
		"sit",
		"dolor",
		"amet"
	};

	/**
	 * Other elements to fill collections :
	 * "dolor amet consectetur adipisicing elit"
	 */
	private static final String[] elements2 = new String[] {
    	"consectetur",
    	"adipisicing",
    	"elit",
    	"Donec",
    	"ut",
    	"urna",
    	"nisl"
    };

	/**
	 * Complementary elements not present in either {@link #elements1}
	 * nor {@link #elements2}
	 */
	private static final String[] elements3 = new String[] {
		"Curabitur",
		"ut",
		"libero",
		"vestibulum",
		"elit",
		"sagittis",
		"mattis",
		"Mauris",
		"lacinia",
		"elit",
		"eget",
		"eros"
	};

	/**
	 * Elements to fill a collection with duplicates.
	 */
	private static final String[] elements = new String[elements1.length + elements2.length];

	/**
	 * List to hold elements to fill Collections.
	 */
	private List<String> listElements;

	/**
	 * Different natures of collections to test
	 */
	@SuppressWarnings("unchecked")
	private static final Class<? extends Collection<?>>[] collectionTypes =
		(Class<? extends Collection<?>>[]) new Class<?>[] {
			LinkedList.class,
 			MyLinkedCollection.class,
			ArrayList.class,
 			MyArrayCollection.class,
			MyDummyCollection.class,
		};

	/**
	 * Collections class provider used for parameterized tests requiring the type of
	 * collection
	 * @return a stream of List Classes to use in each ParameterizedTest
	 */
	private static Stream<Class<? extends Collection<?>>> collectionClassesProvider()
	{
		return Stream.of(collectionTypes);
	}

	/**
	 * Utiliy method indicating if the the provided class has been implemented
	 * in the "collections" package
	 * @param <E> the type of collection content
	 * @param type the type of collection we're invsetigating
	 * @return true if the provided collection class is located in the
	 * "collections" package
	 */
	private static <E> boolean isCustom(Class<? extends Collection<E>> type)
	{
		return type.getPackage().getName().equals("collections");
	}

//	/**
//	 * List class provider used for parameterized tests requiring the type of
//	 * collection (restricted to personnaly implemented classes)
//	 * @return a stream of List Classes to use in each ParameterizedTest
//	 */
//	private static Stream<Class<? extends Collection<?>>> personnalCollectionsClassesProvider()
//	{
//		return collectionClassesProvider().filter((Class<? extends Collection<?>> c) -> {
//			// Keep only classes from "collections" package
//			return isCustom(c);
//		});
//	}

	/**
	 * List class provider used for parameterized tests requiring the type of
	 * collections (restricted to classes implementing the {@link Capacity}
	 * interface)
	 * @return a stream of Collections Classes to use in each ParameterizedTest
	 */
	private static Stream<Class<? extends Collection<?>>> capacityClassesProvider()
	{
		return collectionClassesProvider().filter((Class<? extends Collection<?>> c) -> {
			/*
			 * Keep only classes that uses internal arrays and feature
			 * a constructor with a int argument for initial capacity
			 */
			Class<?>[] interfaces = c.getInterfaces();
			for (Class<?> i : interfaces)
			{
				if (i == Capacity.class)
				{
					return true;
				}
			}
			return false;
		});
	}

	/**
	 * List class provider used for parameterized tests requiring the type of
	 * collection (restricted to classes implementing a capacity constructor)
	 * @return a stream of List Classes to use in each ParameterizedTest
	 */
	private static Stream<Class<? extends Collection<?>>> capacityConstructorClassesProvider()
	{
		return collectionClassesProvider().filter((Class<? extends Collection<?>> c) -> {
			/*
			 * Keep only classes that feature a xxx(int) constructor
			 */
			Constructor<? extends Collection<?>> constructor = null;
			try
			{
				constructor = c.getConstructor(int.class);
			}
			catch (NoSuchMethodException e)
			{
				return false;
			}
			catch (SecurityException e)
			{
				return false;
			}
			return constructor != null;
		});
	}

	/**
	 * Creates an instance of a {@code Collection<String>} according to the type of
	 * collection to create and eventually a content to set.
	 * @param testName the message to repeat in each assertion based on the test
	 * this method is used in
	 * @param type the type of collection to create
	 * @param content the content to setup in the created collection
	 * @return a new collection with the required type filled with the required
	 * content (if provided)
	 */
	private static Collection<String>
	    constructCollection(String testName,
	                        Class<? extends Collection<String>> type,
	                        Collection<String> content)
	{
		Collection<String> col = null;

		try
		{
			col = CollectionFactory.<String> getCollection(type, content);
		}
		catch (SecurityException e)
		{
			fail(testName + " constructor security exception");
		}
		catch (NoSuchMethodException e)
		{
			fail(testName + " constructor not found");
		}
		catch (IllegalArgumentException e)
		{
			fail(testName + " wrong constructor arguments");
		}
		catch (InstantiationException e)
		{
			fail(testName + " instanciation exception");
		}
		catch (IllegalAccessException e)
		{
			fail(testName + " illegal access");
		}
		catch (InvocationTargetException e)
		{
			fail(testName + " invocation exception");
		}

		return col;
	}

	/**
	 * Shuffle elements from the provided elements array
	 * @param elements the array containing elements to shuffle
	 * @return an new array containing the same elements as the provided array
	 * with a different order
	 */
	private static String[] shuffleArray(String[] elements)
	{
		/*
		 * CAUTION elements needs to be shuffled in a new collection in order to
		 * preserve "elements" order
		 */
		List<String> listElements = new ArrayList<>(Arrays.asList(elements));

		Collections.shuffle(listElements);

		String[] result = new String[elements.length];
		int i = 0;
		for (String elt : listElements)
		{
			result[i++] = elt;
		}

		return result;
	}

	/**
	 * Setup before all tests
	 * @throws java.lang.Exception if setup fails
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception
	{
		int j = 0;
		for (int i = 0; i < elements1.length; i++)
		{
			elements[j++] = elements1[i];
		}
		for (int i = 0; i < elements2.length; i++)
		{
			elements[j++] = elements2[i];
		}

		System.out.println("-------------------------------------------------");
		System.out.println("Collections tests");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Tear down after al tests
	 * @throws java.lang.Exception if teardown fails
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception
	{
		System.out.println("-------------------------------------------------");
		System.out.println("Collections test end");
		System.out.println("-------------------------------------------------");
	}
	/**
	 * Setup variables for a specific test.
	 * To be used in every testXXX(...) methods requiring {@code Collection<String>}
	 * instances.
	 * @param col the collection to test
	 * @param testName the name of the current test
	 */
	@SuppressWarnings("unchecked")
	void setUpTest(Collection<String> col, String testName)
	{
		testCollection = col;
		testCollectionType = (Class<? extends Collection<String>>) testCollection.getClass();
		testCollectionTypeName = testCollectionType.getSimpleName();
		this.testName = testCollectionTypeName + "." + testName;
		System.out.println(this.testName);
	}

	/**
	 * Setup before each test
	 * @throws java.lang.Exception if setup fails
	 */
	@BeforeEach
	void setUp() throws Exception
	{
		listElements = new ArrayList<String>();
		for (String elt : elements)
		{
			listElements.add(elt);
		}
	}

	/**
	 * Teardown after each test
	 * @throws java.lang.Exception if teardown fails
	 */
	@AfterEach
	void tearDown() throws Exception
	{
		testCollection = null;
		listElements.clear();
		listElements = null;
	}

	/**
	 * Test method for all collections constructors such as
	 * {@link collections.MyLinkedCollection#MyLinkedCollection()}
	 * @param type the type of collection provided by {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("...Collection()")
	@Order(1)
	final void testDefaultConstructor(Class<? extends Collection<String>> type)
	{
		String testName = new String(type.getSimpleName() + "()");
		System.out.println(testName);

		Constructor<? extends Collection<String>> defaultConstructor = null;
		Class<?>[] constructorsArgs = new Class<?>[0];

		try
		{
			defaultConstructor = type.getConstructor(constructorsArgs);
		}
		catch (NoSuchMethodException e)
		{
			fail(testName + " constructor not found");
		}
		catch (SecurityException e)
		{
			fail(testName + " constructor security exception");
		}

		if (defaultConstructor != null)
		{
			Object instance = null;
			Object[] args = new Object[0];
			try
			{
				instance = defaultConstructor.newInstance(args);
			}
			catch (InstantiationException e)
			{
				fail(testName + " instanciation exception : Abstract class");
			}
			catch (IllegalAccessException e)
			{
				fail(testName + " constructor is inaccessible");
			}
			catch (IllegalArgumentException e)
			{
				fail(testName + " illegal argument");
			}
			catch (InvocationTargetException e)
			{
				fail(testName + " invoked constructor throwed an exception");
			}

			assertNotNull(instance, testName + " null instance");
			assertEquals(type,
			             instance.getClass(),
			             testName + " unexpected instance class");
			@SuppressWarnings("unchecked")
			Collection<String> col = (Collection<String>) instance;
			assertTrue(col.isEmpty(),
			           testName + " unexpected non empty on default instance");
			assertEquals(0,
			             col.size(),
			             testName
			                 + " unexpected non 0 size on default instance");
		}
		else
		{
			fail(testName + " null constructor");
		}
	}
	/**
	 * Test method for all collections constructors such as
	 * {@link collections.MyLinkedCollection#MyLinkedCollection(Collection)}
	 * @param type the type of collection provided by {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("...Collection(Collection)")
	@Order(2)
	final void testCopyConstructor(Class<? extends Collection<String>> type)
	{
		String testName = new String(type.getSimpleName() + "(Collection)");
		System.out.println(testName);

		Constructor<? extends Collection<String>> copyConstructor = null;
		Class<?>[] constructorsArgs = new Class<?>[1];
		constructorsArgs[0] = Collection.class;

		try
		{
			copyConstructor = type.getConstructor(constructorsArgs);
		}
		catch (NoSuchMethodException e)
		{
			fail(testName + " copy constructor not found");
		}
		catch (SecurityException e)
		{
			fail(testName + " copy constructor security exception");
		}

		if (copyConstructor != null)
		{
			Object instance = null;
			Object[] args = new Object[1];
			args[0] = listElements;
			try
			{
				instance = copyConstructor.newInstance(args);
			}
			catch (InstantiationException e)
			{
				fail(testName
				    + " copy instanciation exception : Abstract class");
			}
			catch (IllegalAccessException e)
			{
				fail(testName + " copy constructor is inaccessible");
			}
			catch (IllegalArgumentException e)
			{
				fail(testName + " copy constructor illegal argument");
			}
			catch (InvocationTargetException e)
			{
				fail(testName
				    + " invoked copy constructor throwed an exception");
			}
			catch (Exception e)
			{
				fail(testName + " copy constructor exception");
			}

			assertNotNull(instance, testName + " null instance");
			assertEquals(type,
			             instance.getClass(),
			             testName + " unexpected instance class");
			@SuppressWarnings("unchecked")
			Collection<String> col = (Collection<String>) instance;
			assertFalse(col.isEmpty(),
			            testName + " unexpected empty on copy instance");
			Iterator<String> it = col.iterator();
			assertNotNull(it, testName + " unexpected null iterator");
			// assert expected elements
			for (int i = 0; i < elements.length; i++)
			{
				assertEquals(elements[i],
				             it.next(),
				             testName + " unexpected collection value at index " + i);
			}
		}
		else
		{
			fail(testName + " null constructor");
		}
	}
	/**
	 * Test method for all lists constructors such as
	 * {@link collections.lists.MyArrayCollection#MyArrayCollection(int)}
	 * @param type the type of list provided by {@link #capacityConstructorClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("capacityConstructorClassesProvider")
	@DisplayName("...Collection(int)")
	@Order(3)
	final void testCapacityConstructor(Class<? extends List<String>> type)
	{
		String testName = new String(type.getSimpleName() + "(int)");
		System.out.println(testName);

		Constructor<? extends List<String>> capacityConstructor = null;
		Class<?>[] constructorsArgs = new Class<?>[1];
		constructorsArgs[0] = int.class;

		try
		{
			capacityConstructor = type.getConstructor(constructorsArgs);
		}
		catch (NoSuchMethodException e)
		{
			fail(testName + " capacity constructor not found");
		}
		catch (SecurityException e)
		{
			fail(testName + " capacity constructor security exception");
		}

		if (capacityConstructor != null)
		{
			Object instance = null;
			Object[] args = new Object[1];
			int expectedCapacity = 15;
			args[0] = expectedCapacity;
			try
			{
				instance = capacityConstructor.newInstance(args);
			}
			catch (InstantiationException e)
			{
				fail(testName
				    + " capacity instanciation exception : Abstract class");
			}
			catch (IllegalAccessException e)
			{
				fail(testName + " capacity constructor is inaccessible");
			}
			catch (IllegalArgumentException e)
			{
				fail(testName + " capacity constructor illegal argument");
			}
			catch (InvocationTargetException e)
			{
				fail(testName
				    + " invoked capacity constructor throwed an exception");
			}
			catch (Exception e)
			{
				fail(testName + " setup failed");
			}

			assertNotNull(instance, testName + " unexpected null instance");
			assertEquals(type,
			             instance.getClass(),
			             testName + " unexpected instance class");
			@SuppressWarnings("unchecked")
			Collection<String> col = (Collection<String>) instance;
			assertTrue(col.isEmpty(),
			           testName + " unexpected non empty on capacity instance");
			// assert expected capacity (if available)
			if (Capacity.class.isInstance(col))
			{
				@SuppressWarnings("unchecked")
				Capacity<String> cap = (Capacity<String>) col;
				assertEquals(expectedCapacity,
				             cap.getCapacity(),
				             testName + " unexpected capacity");
			}
		}
		else
		{
			fail(testName + " null constructor");
		}
	}

	/**
	 * Test method for {@link Capacity#getCapacity()}.
	 * @param type the type of list to test provided by
	 * {@link #capacityClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("capacityClassesProvider")
	@DisplayName("getCapacity()")
	@Order(4)
	final void testGetCapacity(Class<? extends List<String>> type)
	{
		String baseTestName = "getCapacity()";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);

		if (Capacity.class.isInstance(testCollection))
		{
			@SuppressWarnings("unchecked")
			Capacity<String> myCap = (Capacity<String>) testCollection;

			assertEquals(Capacity.DEFAULT_CAPACITY,
			             myCap.getCapacity(),
			             testName + " unexpected capacity");
		}
		else
		{
			fail(testName + " unexpected type: " + type.getSimpleName());
		}
	}

	/**
	 * Test method for {@link Capacity#getCapacityIncrement()}.
	 * @param type the type of list to test provided by
	 * {@link #capacityClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("capacityClassesProvider")
	@DisplayName("getCapacityIncrement()")
	@Order(5)
	final void testGetCapacityIncrement(Class<? extends List<String>> type)
	{
		String baseTestName = "getCapacityIncrement()";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);
		if (Capacity.class.isInstance(testCollection))
		{
			@SuppressWarnings("unchecked")
			Capacity<String> myCap = (Capacity<String>) testCollection;

			assertEquals(Capacity.DEFAULT_CAPACITY_INCREMENT,
			             myCap.getCapacityIncrement(),
			             testName + " unexpected capacity");
		}
		else
		{
			fail(testName + " unexpected type: " + type.getSimpleName());
		}
	}

	/**
	 * Test method for {@link Capacity#grow(int)}.
	 * @param type the type of list to test provided by
	 * {@link #capacityClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("capacityClassesProvider")
	@DisplayName("grow(int)")
	@Order(6)
	final void testGrow(Class<? extends List<String>> type)
	{
		String baseTestName = "grow(int)";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);
		if (Capacity.class.isInstance(testCollection))
		{
			@SuppressWarnings("unchecked")
			Capacity<String> myCap = (Capacity<String>) testCollection;
			/*
			 * We can't test grow directly but we can assert size has
			 * grown by capacity increment when capacity is exceeded
			 */
			int expectedCapacity = Capacity.DEFAULT_CAPACITY;
			assertEquals(expectedCapacity,
			             myCap.getCapacity(),
			             testName + " unexpected initial capacity");
			int i = 0;
			while (i < (elements.length * 3))
			{
				if (i >= myCap.getCapacity())
				{
					expectedCapacity += Capacity.DEFAULT_CAPACITY_INCREMENT;
				}
				testCollection.add(elements[i % elements.length]);
				assertEquals(expectedCapacity,
				             myCap.getCapacity(),
				             testName + " unexpected capacity");
				i++;
			}
		}
		else
		{
			fail(testName + " unexpected type: " + type.getSimpleName());
		}
	}

	/**
	 * Test method for {@link Collection#size()}.
	 * @param type the type of collection to test provided by
	 * {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("size()")
	@Order(7)
	final void testSize(Class<? extends Collection<String>> type)
	{
		String baseTestName = "size()";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);

		assertEquals(0,
		             testCollection.size(),
		             testName + " size 0 on empty collection failed");

		testCollection = constructCollection(testName, testCollectionType, listElements);
		assertNotNull(testCollection, testName + " unexpected null collection instance");

		assertEquals(elements.length,
		             testCollection.size(),
		             testName + " size " + elements.length
		                 + " on filled collection failed");
	}

	/**
	 * Test method for {@link Collection#iterator()}.
	 * @param type the type of collection to test provided by
	 * {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("iterator()")
	@Order(8)
	final void testIterator(Class<? extends Collection<String>> type)
	{
		String baseTestName = "iterator()";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);

		assertNotNull(testCollection, testName + " unexpected null collection instance");
		assertEquals(0,
		             testCollection.size(),
		             testName + " initial empty collection doesn't have 0 size");

		Iterator<String> it = testCollection.iterator();
		assertNotNull(it, testName + " unexpected null iterator");
		assertFalse(it.hasNext(), testName + " unexpected iterator state");

		testCollection = constructCollection(testName, type, listElements);
		it = testCollection.iterator();
		assertNotNull(it, testName + " unexpected null iterator");
		assertTrue(it.hasNext(), testName + " unexpected iterator state");

		int i = 0;
		while (i < elements.length)
		{
			try
			{
				assertEquals(elements[i],
				             it.next(),
				             testName + " unexpected value from iterator at index "
				                 + i);
			}
			catch (NoSuchElementException e)
			{
				fail(testName + " unexpected NoSuchElementException at index " + i);
			}
			i++;
			assertEquals(i < elements.length,
			             it.hasNext(),
			             testName + " unexpected hasNext state at index " + i);
		}

		assertThrows(NoSuchElementException.class,
		             () -> {
		            	 Iterator<String> ite = testCollection.iterator();
		            	 while (ite.hasNext())
		            	 {
		            		 ite.next();
		            	 }
		            	 // last call to next should trigger exception
		            	 ite.next();
		             },
		             testName + " unexpected next result on terminated iterator");
		it = testCollection.iterator();
		i = 0;
		int expectedSize = testCollection.size();
		while (it.hasNext())
		{
			assertEquals(elements[i],
			             it.next(),
			             testName + " unexpected iterator value at index " + i);
			try
			{
				it.remove();
				assertEquals(--expectedSize,
				             testCollection.size(),
				             testName + " unexpected collection size after remove at step " + i);
			}
			catch (IllegalStateException e)
			{
				fail(testName + " unexpected IllegalStatException during remove of index " + i);
			}
			catch (UnsupportedOperationException o)
			{
				fail(testName + " " + type + " internal iterator should implement the remove() method");
			}

			try
			{
				it.remove();
				fail(testName + " unexpected second remove success");
			}
			catch (IllegalStateException e)
			{
				// Nothing : Expected
			}
			i++;
		}

		for (i = 0; i < elements.length; i++)
		{
			testCollection = constructCollection(testName, type, listElements);
			expectedSize = testCollection.size() - 1;
			it = testCollection.iterator();
			String element = null;
			int j = 0;
			for (; j <= i; j++)
			{
				element = it.next();
			}
			try
			{
				it.remove();
				assertEquals(expectedSize,
				             testCollection.size(),
				             testName
				                 + " unexpected collection size after remove of "
				                 + element + " at step " + j);
			}
			catch (IllegalStateException e)
			{
				fail(testName + " unexpected IllegalStatException during remove of index " + j);
			}
		}
	}

	/**
	 * Get Ith element of a collection
	 * @param <E> The type of elements in the collection
	 * @param testName the name of the test this method is used in
	 * @param col the collection to iterate upon
	 * @param index the index of the required element in the collection
	 * @return the i^th element of provided collection
	 * @throws NullPointerException if the provided collection is null
	 * @throws NoSuchElementException if there is no such element
	 */
	private static <E> E getElement(String testName, Collection<E> col, int index)
		throws NullPointerException,
	    NoSuchElementException
	{
		try
		{
			Objects.requireNonNull(col);
		}
		catch (NullPointerException e)
		{
			fail(testName + " unexpected null collection");
			throw e;
		}

		try
		{
			Objects.checkIndex(index, col.size());
		}
		catch (IndexOutOfBoundsException e)
		{
			fail(testName + " invalid index " + index);
			throw new NoSuchElementException("invalid index " + String.valueOf(index));
		}

		Iterator<E> it = col.iterator();
		E element = null;
		for (int i = 0; i <= index; i++)
		{
			element = it.next();
		}
		return element;
	}

	/**
	 * Test method for {@link Collection#add(Object)}.
	 * @param type the type of collection to test provided by
	 * {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("add(E)")
	@Order(9)
	final void testAddE(Class<? extends Collection<String>> type)
	{
		String baseTestName = "add(E)";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);

		assertNotNull(testCollection, testName + " unexpected null collection instance");
		assertEquals(0,
		             testCollection.size(),
		             testName + " initial empty collection doesn't have 0 size");

		int expectedSize;
		/*
		 * If we implemented the "type" class we expect a NullPointerException
		 * when trying to add a null element
		 */
		if (isCustom(type))
		{
			expectedSize = 0;
			assertThrows(NullPointerException.class,
			             () -> {
			            	 testCollection.add(null);
			             },
			             testName + "adding null element didn't thtow NullPointerException");
			assertEquals(expectedSize, testCollection.size(), testName + " unexpected size");
		}

		for (int i = 0; i < elements3.length; i++)
		{
			expectedSize = testCollection.size() + 1;
			boolean result = false;
			try
			{
				result = testCollection.add(elements3[i]);
			}
			catch (UnsupportedOperationException e)
			{
				fail(testName + type.getSimpleName() + " must override add(E) operation");
			}
			assertTrue(result, testName + " unexpected add(E) result");
			int providedSize = testCollection.size();
			assertEquals(expectedSize,
			             providedSize,
			             testName + " unexpected size after add(E)");
			assertEquals(elements3[i],
			             getElement(testName, testCollection, providedSize - 1),
			             testName + " unexpected element value after add(E)");
		}
	}

	/**
	 * Test method for {@link Collection#toString()}.
	 * @param type the type of collection to test provided by
	 * {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("toString()")
	@Order(10)
	final void testToString(Class<? extends Collection<String>> type)
	{
		String baseTestName = "toString()";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);

		assertNotNull(testCollection, testName + " unexpected null collection instance");
		assertEquals(0,
		             testCollection.size(),
		             testName + " initial empty collection doesn't have 0 size");
		assertEquals("[]",
		             testCollection.toString(),
		             testName + " unexpected toString value");

		testCollection = constructCollection(testName, type, listElements);
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0; i < elements.length; i++)
		{
			sb.append(elements[i]);
			if (i < (elements.length - 1))
			{
				sb.append(", ");
			}
		}
		sb.append(']');
		String expected = sb.toString();
		assertEquals(expected,
		             testCollection.toString(),
		             testName + " unexpected toString value");
	}

	/**
	 * Test method for {@link Collection#isEmpty()}.
	 * @param type the type of collection to test provided by
	 * {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("isEmpty()")
	@Order(11)
	final void testIsEmpty(Class<? extends Collection<String>> type)
	{
		String baseTestName = "isEmpty()";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);

		assertNotNull(testCollection, testName + " unexpected null collection instance");
		assertEquals(0,
		             testCollection.size(),
		             testName + " initial empty collection doesn't have 0 size");

		assertTrue(testCollection.isEmpty(), testName + " unexpected non empty collection");

		testCollection = constructCollection(testName, type, listElements);
		assertFalse(testCollection.isEmpty(), testName + " unexpected empty collection");
	}

	/**
	 * Test method for {@link Collection#contains(Object)}.
	 * @param type the type of collection to test provided by
	 * {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("contains(Object)")
	@Order(12)
	final void testContains(Class<? extends Collection<String>> type)
	{
		String baseTestName = "contains(Object)";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);

		assertNotNull(testCollection, testName + " unexpected null collection instance");
		assertEquals(0,
		             testCollection.size(),
		             testName + " initial empty collection doesn't have 0 size");

		assertFalse(testCollection.contains(null),
		            testName + " unexpected null contained in empty collection");

		for (int i = 0; i < elements.length; i++)
		{
			assertFalse(testCollection.contains(elements[i]),
			            testName + " unexpected contained object: "
			                + elements[i]);
		}

		testCollection = constructCollection(testName, type, listElements);
		assertFalse(testCollection.contains(null),
		            testName + " unexpected null contained in non empty collection");
		String[] containedElements = shuffleArray(elements);
		for (int i = 0; i < containedElements.length; i++)
		{
			assertTrue(testCollection.contains(containedElements[i]),
			           testName + " unexpected not contained object: "
			               + containedElements[i]);
		}
		List<String> uncontainedElements = Arrays.asList(elements3);
		for (Iterator<String> it = uncontainedElements.iterator(); it.hasNext();)
		{
			final String elt = it.next();
			if (listElements.contains(elt))
			{
				uncontainedElements.replaceAll((String s) -> {
					if (s.equals(elt))
					{
						s = new String("replaced");
					}
					return s;
				});
			}
		}

		for (int i = 0; i < uncontainedElements.size(); i++)
		{
			String searched = uncontainedElements.get(i);
			assertFalse(testCollection.contains(searched),
			            testName + " unexpected contained object: "
			                + searched);
		}
	}

	/**
	 * Test method for {@link Collection#toArray()}.
	 * @param type the type of collection to test provided by
	 * {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("toArray()")
	@Order(13)
	final void testToArray(Class<? extends Collection<String>> type)
	{
		String baseTestName = "toArray()";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);

		assertNotNull(testCollection, testName + " unexpected null collection instance");
		assertEquals(0,
		             testCollection.size(),
		             testName + " initial empty collection doesn't have 0 size");

		Object[] providedArray = testCollection.toArray();
		assertNotNull(providedArray, testName + " unexpected null array");
		assertEquals(0,
		             providedArray.length,
		             testName + " unexpected array length");

		testCollection = constructCollection(testName, type, listElements);
		providedArray = testCollection.toArray();
		assertNotNull(providedArray, testName + " unexpected null array");
		assertEquals(testCollection.size(),
		             providedArray.length,
		             testName + " unexpected array length");
		assertArrayEquals(elements,
		                  providedArray,
		                  testName + " unexpected array inequality");
	}

	/**
	 * Test method for {@link Collection#toArray(Object[])}.
	 * @param type the type of collection to test provided by
	 * {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("toArray(T[])")
	@Order(14)
	final void testToArrayTArray(Class<? extends Collection<String>> type)
	{
		String baseTestName = "toArray(T[])";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);

		assertNotNull(testCollection, testName + " unexpected null collection instance");
		assertEquals(0,
		             testCollection.size(),
		             testName + " initial empty collection doesn't have 0 size");

		String[] providedArray = testCollection.toArray(new String[0]);
		assertNotNull(providedArray, testName + " unexpected null array");
		assertEquals(0,
		             providedArray.length,
		             testName + " unexpected array length");

		testCollection = constructCollection(testName, type, listElements);

		providedArray = testCollection.toArray(new String[0]);
		assertNotNull(providedArray, testName + " unexpected null array");
		assertEquals(testCollection.size(),
		             providedArray.length,
		             testName + " unexpected array length");
		assertArrayEquals(elements,
		                  providedArray,
		                  testName + " unexpected array inequality");

		providedArray = testCollection.toArray(new String[elements.length]);
		assertNotNull(providedArray, testName + " unexpected null array");
		assertEquals(testCollection.size(),
		             providedArray.length,
		             testName + " unexpected array length");
		assertArrayEquals(elements,
		                  providedArray,
		                  testName + " unexpected array inequality");
	}

	/**
	 * Test method for {@link Collection#remove(Object)}.
	 * @param type the type of collection to test provided by
	 * {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("remove(Object)")
	@Order(15)
	final void testRemove(Class<? extends Collection<String>> type)
	{
		String baseTestName = "remove(Object)";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);

		assertNotNull(testCollection, testName + " unexpected null collection instance");
		assertEquals(0,
		             testCollection.size(),
		             testName + " initial empty collection doesn't have 0 size");
		assertFalse(testCollection.remove(null),
		            testName + " unexpected null removed in empty collection");

		for (int i = 0; i < elements.length; i++)
		{
			assertFalse(testCollection.remove(elements[i]),
			            testName + " unexpected removing of object "
			                + elements[i] + " on empty collection");
		}

		testCollection = constructCollection(testName, type, listElements);
		assertFalse(testCollection.remove(null),
		            testName + " unexpected null removed in non empty collection");
		String[] containedElements = shuffleArray(elements);
		int expectedSize = testCollection.size();
		for (int i = 0; i < containedElements.length; i++)
		{
			boolean removed = false;
			try
			{
				removed = testCollection.remove(containedElements[i]);
			}
			catch (UnsupportedOperationException o)
			{
				fail(testName + " " + type + " internal iterator should implement the remove() method");
			}
			assertTrue(removed,
			           testName + " unexpected not removed object: "
			               + containedElements[i]);
			assertEquals(--expectedSize,
			             testCollection.size(),
			             testName + " unexpected collection size after remove");
		}
		testCollection = constructCollection(testName, type, listElements);
		List<String> uncontainedElements = Arrays.asList(elements3);
		for (Iterator<String> it = uncontainedElements.iterator(); it.hasNext(); )
		{
			final String elt = it.next();
			if (listElements.contains(elt))
			{
				uncontainedElements.replaceAll((String s) -> {
					if (s.equals(elt))
					{
						s = new String("replaced");
					}
					return s;
				});
			}
		}

		for (int i = 0; i < uncontainedElements.size(); i++)
		{
			String searched = uncontainedElements.get(i);
			assertFalse(testCollection.remove(searched),
			            testName + " unexpected removed object: "
			                + searched);
		}
	}

	/**
	 * Test method for {@link Collection#containsAll(Collection)}.
	 * @param type the type of collection to test provided by
	 * {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("containsAll(Collection)")
	@Order(16)
	final void testContainsAll(Class<? extends Collection<String>> type)
	{
		String baseTestName = "containsAll(Collection)";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);

		assertNotNull(testCollection, testName + " unexpected null collection instance");
		assertEquals(0,
		             testCollection.size(),
		             testName + " initial empty collection doesn't have 0 size");
		assertThrows(NullPointerException.class,
		             () -> {
		            	 testCollection.containsAll(null);
		             },
		             testName +  " missing NullPointerexception");
		assertFalse(testCollection.containsAll(listElements),
		            testName + " unexpected contained collection in empty collection");
		testCollection = constructCollection(testName, type, listElements);
		assertThrows(NullPointerException.class,
		             () -> {
		            	 testCollection.containsAll(null);
		             },
		             testName +  " missing NullPointerexception");
		String[] shuffledElements = shuffleArray(elements);
		assertTrue(testCollection.containsAll(Arrays.asList(shuffledElements)),
		           testName + " unexpected not contained collection");
		listElements.add("lacinia");
		assertFalse(testCollection.containsAll(listElements),
		            testName + " unexpected contained collection");
		listElements.remove("lacinia");
		while (listElements.size() > 0)
		{
			assertTrue(testCollection.containsAll(listElements),
			           testName + " unexpected not contained collection");
			listElements.remove(0);
		}
		assertFalse(testCollection.containsAll(Arrays.asList(elements3)),
		            testName + " unexpected contained collection");
	}

	/**
	 * Test method for {@link Collection#addAll(Collection)}.
	 * @param type the type of collection to test provided by
	 * {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("addAll(Collection)")
	@Order(17)
	final void testAddAll(Class<? extends Collection<String>> type)
	{
		String baseTestName = "addAll(Collection)";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);

		assertNotNull(testCollection, testName + " unexpected null collection instance");
		assertEquals(0,
		             testCollection.size(),
		             testName + " initial empty collection doesn't have 0 size");
		assertThrows(NullPointerException.class,
		             () -> {
		            	 testCollection.addAll(null);
		             },
		             testName + " unexpected added null collection without throwing NullPointerException");
		int initialSize = testCollection.size();
		assertTrue(testCollection.addAll(listElements),
		           testName + " unexpected addAll failure");
		assertNotEquals(initialSize,
		                testCollection.size(),
		                testName + " unexpected resulting collection unchanged size");
		assertTrue(testCollection.containsAll(listElements),
		           testName + " unexpected collection not contained");

		initialSize = testCollection.size();
		assertTrue(testCollection.addAll(listElements),
		           testName + " unexpected addAll failure");
		assertNotEquals(initialSize,
		                testCollection.size(),
		                testName + " unexpected resulting collection unchanged size");

		Map<String, Integer> wordCounts = new HashMap<String, Integer>();
		for (Iterator<String> it = testCollection.iterator(); it.hasNext();)
		{
			String word = it.next();
			Integer value = wordCounts.get(word);
			wordCounts.put(word, (value == null ? 1 : value + 1));
		}
		for (Iterator<String> it = wordCounts.keySet().iterator(); it.hasNext(); )
		{
			String word = it.next();
			assertEquals(2,
			             wordCounts.get(word),
			             testName + " unexpected count for word \"" + word + "\"");
		}
	}

	/**
	 * Test method for {@link Collection#removeAll(Collection)}.
	 * @param type the type of collection to test provided by
	 * {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("removeAll(Collection)")
	@Order(18)
	final void testRemoveAll(Class<? extends Collection<String>> type)
	{
		String baseTestName = "removeAll(Collection)";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);

		assertNotNull(testCollection, testName + " unexpected null collection instance");
		assertEquals(0,
		             testCollection.size(),
		             testName + " initial empty collection doesn't have 0 size");
		assertThrows(NullPointerException.class,
		             () -> {
		            	 testCollection.removeAll(null);
		             },
		             testName + " unexpected removeAll of null collection");

		testCollection = constructCollection(testName, type, listElements);
		assertThrows(NullPointerException.class,
		             () -> {
		            	 testCollection.removeAll(null);
		             },
		             testName + " unexpected removeAll of null collection");
		int expectedSize = testCollection.size() - elements2.length;
		assertTrue(testCollection.removeAll(Arrays.asList(elements2)),
		           testName + " unexpected removeAll failure of elements1");
		assertEquals(expectedSize,
		             testCollection.size(),
		             testName + " unexpected size after removeAll");
		for (int i = 0; i < elements2.length; i++)
		{
			assertFalse(testCollection.contains(elements2[i]),
			            testName + " unexpected contained element "
			                + elements2[i]);
		}
		for (int i = 0; i < elements1.length; i++)
		{
			assertTrue(testCollection.contains(elements1[i]),
			            testName + " unexpected not contained element "
			                + elements1[i]);
		}
		assertFalse(testCollection.removeAll(Arrays.asList(elements3)),
		            testName
		                + " unexpected removeAll result of uncontained collection");

		listElements.add("lacinia");
		assertTrue(testCollection.removeAll(listElements),
		            testName + " unexpected non removal of collection");
		for (Iterator<String> it = listElements.iterator(); it.hasNext();)
		{
			String word = it.next();
			assertFalse(testCollection.contains(word),
			            testName + " unexpected contained element " + word);
		}
		assertEquals(0,
		             testCollection.size(),
		             testName + " unexpected resulting collection size");
	}

	/**
	 * Test method for {@link Collection#retainAll(Collection)}.
	 * @param type the type of collection to test provided by
	 * {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("retainAll(Collection)")
	@Order(19)
	final void testRetainAll(Class<? extends Collection<String>> type)
	{
		String baseTestName = "retainAll(Collection)";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);

		assertNotNull(testCollection, testName + " unexpected null collection instance");
		assertEquals(0,
		             testCollection.size(),
		             testName + " initial empty collection doesn't have 0 size");

		assertThrows(NullPointerException.class,
		             () -> {
		            	 testCollection.retainAll(null);
		             },
		             testName
		                 + " didn't throw NullPointerException on null collection");

		testCollection = constructCollection(testName, type, listElements);
		assertTrue(testCollection.retainAll(Arrays.asList(elements1)),
		           testName + " unexpected retain result");
		for (int i = 0; i < elements1.length; i++)
		{
			assertTrue(testCollection.contains(elements1[i]),
			            testName + " unexpected not contained object "
			                + elements1[i]);
		}
		for (int i = 0; i < elements2.length; i++)
		{
			assertFalse(testCollection.contains(elements2[i]),
			            testName + " unexpected contained object "
			                + elements2[i]);
		}

		int expectedSize = testCollection.size();
		assertFalse(testCollection.retainAll(Arrays.asList(elements1)),
		            testName + " unexpected retain result");
		assertEquals(expectedSize,
		             testCollection.size(),
		             testName
		                 + " unexpected collection size after retain of already contained collection");

		assertTrue(testCollection.retainAll(Arrays.asList(elements3)),
		           testName + " unexpected retain result");
		assertEquals(0,
		             testCollection.size(),
		             testName
		                 + " unexpected collection size after retain of uncontained collection");
	}

	/**
	 * Test method for {@link Collection#clear()}.
	 * @param type the type of collection to test provided by
	 * {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("clear()")
	@Order(20)
	final void testClear(Class<? extends Collection<String>> type)
	{
		String baseTestName = "clear()";
		setUpTest(constructCollection(baseTestName, type, listElements), baseTestName);
		assertNotNull(testCollection, testName + " unexpected null filled instance");
		assertEquals(listElements.size(),
		             testCollection.size(),
		             testName + " unexpected collection size");

		try
		{
			testCollection.clear();
		}
		catch (UnsupportedOperationException o)
		{
			fail(testName + " " + type + " internal iterator should implement the remove() method");
		}

		assertEquals(0,
		             testCollection.size(),
		             testName + " unexpected collection size after clear");
	}

	/**
	 * Test method for {@link Collection#hashCode()}.
	 * @param type the type of collection to test provided by
	 * {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("hashCode()")
	@Order(21)
	final void testHashCode(Class<? extends Collection<String>> type)
	{
		String baseTestName = "hashCode()";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);

		assertNotNull(testCollection, testName + " unexpected null collection instance");
		assertEquals(0,
		             testCollection.size(),
		             testName + " initial empty collection doesn't have 0 size");

	    int expectedHash = 1;
		assertEquals(expectedHash,
		             testCollection.hashCode(),
		             testName + " unexpected hashcode on empty collection");

		testCollection = constructCollection(testName, type, listElements);
	    final int prime = 31;
	    for (String elt : testCollection)
	    {
	    	expectedHash = (prime * expectedHash) + (elt == null ? 0 : elt.hashCode());
	    }
		assertEquals(expectedHash,
		             testCollection.hashCode(),
		             testName + " unexpected hash code on full collection");
	}

	/**
	 * Test method for {@link Collection#equals(Object)}.
	 * @param type the type of collection to test provided by
	 * {@link #collectionClassesProvider()}
	 */
	@ParameterizedTest
	@MethodSource("collectionClassesProvider")
	@DisplayName("equals(java.lang.Object)")
	@Order(22)
	final void testEquals(Class<? extends Collection<String>> type)
	{
		String baseTestName = "equals(Object)";
		setUpTest(constructCollection(baseTestName, type, null), baseTestName);

		assertNotNull(testCollection, testName + " unexpected null collection instance");
		assertEquals(0,
		             testCollection.size(),
		             testName + " initial empty collection doesn't have 0 size");

		/*
		 * Inequality with null (always)
		 */
		assertFalse(testCollection.equals(null),
		            testName + "unexpected equality with null");
		/*
		 * Equality with self
		 */
		assertTrue(testCollection.equals(testCollection),
		            testName + "unexpected inequality with self");

		/*
		 * Equality with another collection
		 */
		testCollection = constructCollection(testName, type, listElements);
		assertIterableEquals(testCollection,
		                     listElements,
		                     testName + " unexpected not same content with listElements");
		assertTrue(testCollection.equals(listElements),
		           testName + " unexpected inequality with listElements");

		/*
		 * Inequality with another collection which is not a collection
		 * (even with same content)
		 */
		LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<>(listElements);
		assertIterableEquals(testCollection,
		                     lbq,
		                     testName + " unexpected not same content with Queue");

		/*
		 * Inequality with another collection featuring same content + 1 elt
		 */
		listElements.add(elements[0]);
		assertFalse(testCollection.equals(listElements),
		            testName +  " unexpected equality with different content (+1 elt)");

		/*
		 * Inequality with another collection featuring same content - 1 elt
		 */
		listElements.remove(listElements.size() - 1);
		assertTrue(testCollection.equals(listElements),
		            testName +  " unexpected inequality with same content");

		Collection<String> colElements = listElements;
		assertTrue(testCollection.equals(colElements),
		            testName +  " unexpected inequality with same content collection");

		Iterable<String> itElements = listElements;
		assertTrue(testCollection.equals(itElements),
		            testName +  " unexpected inequality with same content iterable");

		listElements.remove(listElements.size() - 1);
		assertFalse(testCollection.equals(listElements),
		            testName +  " unexpected equality with different content (-1 elt)");
	}
}
