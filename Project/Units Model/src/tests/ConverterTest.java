package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import application.Converter;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import measures.MeasureType;
import measures.units.BaseSymbolicUnit;
import measures.units.DecomposedUnit;
import measures.units.DerivedSymbolicUnit;
import measures.units.OperationOrder;
import measures.units.SortOrder;
import measures.units.Unit;
import measures.units.UnitsFactory;

/**
 * Test class for {@link Converter}
 * @author davidroussel
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Converter")
public class ConverterTest
{
	/**
	 * The converter to test
	 */
	private Converter testConverter;

	/**
	 * The name of the current test in all testXXX methods
	 */
	private String testName = null;

	/**
	 * Swing Frame to contain {@link #panel}
	 */
	private static JFrame frame = null;

	/**
	 * JFX Panel to initialize JavaFX runtime
	 */
	private static JFXPanel panel = null;

	/**
	 * Flag indicating {@link #frame} has been constructed
	 */
	private static Condition frameReady = new Condition(false);

	/**
	 * Flag indicating {@link #panel} has been constructed
	 */
	private static Condition panelReady = new Condition(false);

	/**
	 * Flag indicating current test operations have been executed on
	 * JavaFX Thread
	 */
	private static Condition executed = new Condition(false);


	/**
	 * ComboBox to choose the measures
	 */
	private static ComboBox<MeasureType> measuresComboBox = null;

	/**
	 * The Source Unit combobox
	 */
	private static ComboBox<Unit<Double>> sourceUnitComboBox = null;

	/**
	 * The Destination Unit combobox
	 */
	private static ComboBox<Unit<Double>> destinationUnitComboBox = null;

	/**
	 * The Source Unit Sorting Method combobox
	 */
	private static ComboBox<SortOrder> sourceUnitSortingComboBox = null;

	/**
	 * The Destination Unit Sorting Method combobox
	 */
	private static ComboBox<SortOrder> destinationUnitSortingComboBox = null;

	/**
	 * "Switch" Button to switch between source and destination units (iff possible)
	 */
	private static Button switchButton = null;

	/**
	 * Source input text field
	 */
	private static TextField sourceTextField = null;

//	/**
//	 * Source Unit Label
//	 */
//	private static Label sourceUnitLabel = null;

	/**
	 * Destination Label
	 */
	private static Label destinationLabel = null;

//	/**
//	 * Destination Unit Label
//	 */
//	private static Label destinationUnitLabel = null;

	/**
	 * Flag indicating {@link #testConverter}'s properties are bound
	 * @see #bindConverterProperties(String)
	 * @see #unBindConverterProperties(String)
	 */
	private static boolean propertiesBound = false;

	/**
	 * Initial measure type to set
	 */
	private final static MeasureType initialMeasureType = MeasureType.SPEED;

	/**
	 * Initial Sort order for source and destination units
	 */
	private final static SortOrder initialSortOrder = SortOrder.FACTOR_ASCENDING;

	/**
	 * Launch the UI in A Swing Frame
	 */
	private static void initSWINGUI()
	{
        // This method is invoked on Swing thread
        frame = new JFrame("FX");
        panel = new JFXPanel();
        frame.setBounds(0, 0, 400, 200);
        frame.add(panel);
        frame.setVisible(true);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initJavaFXUI(panel);
                synchronized (panelReady)
				{
                	panelReady.setValue(true);
                	panelReady.notify();
				}
            }
        });
		/*
		 * wait to be notified of JavaFX thread completed setup
		 */
        synchronized (panelReady)
		{
			while (!panelReady.getValue())
			{
				try
				{
					panelReady.wait();
				}
				catch (IllegalMonitorStateException e)
				{
					fail(e.getLocalizedMessage());
				}
				catch (InterruptedException e)
				{
					fail(e.getLocalizedMessage());
				}
			}
		}
		assertNotNull(panel, "null JFXPanel");
	}
	/**
	 * Builds the JavaFX Panel containg the UI
	 * @param panel the pane containing the UI
	 */
	private static void initJavaFXUI(JFXPanel panel)
	{
		measuresComboBox = new ComboBox<>();
		Collection<MeasureType> measureTypes = MeasureType.all();
		assertTrue(measureTypes.contains(initialMeasureType),
		           "initJavaFXUI measureTypes does not contain "
		               + initialMeasureType);
		measuresComboBox.getItems().addAll(measureTypes);
		measuresComboBox.setValue(initialMeasureType);

		/*
		 * Source and destination units combobox content will be set
		 * later with converter's content
		 */
		sourceUnitComboBox = new ComboBox<>();
		destinationUnitComboBox = new ComboBox<>();

		sourceUnitSortingComboBox = new ComboBox<>();
		destinationUnitSortingComboBox = new ComboBox<>();
		Collection<SortOrder> sortOrders = SortOrder.all();
		sourceUnitSortingComboBox.getItems().addAll(sortOrders);
		destinationUnitSortingComboBox.getItems().addAll(sortOrders);

		HBox sourceUnitBox = new HBox(sourceUnitComboBox,
		                              sourceUnitSortingComboBox);

		switchButton = new Button("switch");
		HBox switchBox = new HBox(switchButton);

		sourceTextField = new TextField("Source Value");
//		sourceUnitLabel = new Label("unit");
//		HBox sourceTextBox = new HBox(sourceTextField, sourceUnitLabel);
		HBox sourceTextBox = new HBox(sourceTextField);

		HBox destinationUnitBox = new HBox(destinationUnitComboBox,
		                                   destinationUnitSortingComboBox);

		destinationLabel = new Label("destination value");
//		destinationUnitLabel = new Label("unit");
//		HBox destinationTextBox = new HBox(destinationLabel,
//		                                   destinationUnitLabel);
		HBox destinationTextBox = new HBox(destinationLabel);

		VBox vbox = new VBox(measuresComboBox,
		                     sourceUnitBox,
		                     sourceTextBox,
		                     switchBox,
		                     destinationUnitBox,
		                     destinationTextBox);
		Scene scene = new Scene(vbox, 400, 200, true, SceneAntialiasing.BALANCED);
		panel.setScene(scene);
	}

	/**
	 * Setup before all tests
	 * @throws java.lang.Exception if setup fails
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception
	{
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
            	initSWINGUI();
            	synchronized (frameReady)
				{
            		frameReady.setValue(true);
                	frameReady.notify();
				}
            }
        });

        /*
		 * wait to be notified of Swing thread complete setup
		 */
        synchronized (frameReady)
		{
        	while (!frameReady.getValue())
        	{
                try
        		{
                	frameReady.wait();
        		}
        		catch (IllegalMonitorStateException e)
        		{
        			fail(e.getLocalizedMessage());
        		}
        		catch (InterruptedException e)
        		{
        			fail(e.getLocalizedMessage());
        		}
        	}
		}
		assertNotNull(frame, "frame is null");

		System.out.println("-------------------------------------------------");
		System.out.println("Converter tests");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Teardown after all tests
	 * @throws java.lang.Exception if teardown fails
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception
	{
		if (frame != null)
		{
			frame.dispose();
		}
//		destinationUnitLabel = null;
		destinationLabel = null;
//		sourceUnitLabel = null;
		sourceTextField = null;
		switchButton = null;
		destinationUnitSortingComboBox = null;
		sourceUnitSortingComboBox = null;
		destinationUnitComboBox = null;
		sourceUnitComboBox = null;
		panel = null;
		frame = null;
		panelReady.setValue(false);
		frameReady.setValue(false);

		System.out.println("-------------------------------------------------");
		System.out.println("Converter tests end");
		System.out.println("-------------------------------------------------");
	}

	/**
	 * Setup before each test
	 * @throws java.lang.Exception if setup fails
	 */
	@BeforeEach
	void setUp() throws Exception
	{
		testConverter = new Converter(measuresComboBox.getValue());

		executed.setValue(false);
		Platform.runLater(() -> {
			sourceUnitComboBox.setItems(testConverter.getSourceUnits());
			sourceUnitComboBox.setValue(testConverter.getSourceUnit());
			destinationUnitComboBox.setItems(testConverter.getdestinationUnits());
			destinationUnitComboBox.setValue(testConverter.getDestinationUnit());
//			sourceUnitLabel.setText(testConverter.getSourceUnit().getSymbol());
//			destinationUnitLabel.setText(testConverter.getDestinationUnit().getSymbol());
			sourceUnitSortingComboBox.setValue(testConverter.getSourceSortOrder());
			destinationUnitSortingComboBox.setValue(testConverter.getDestinationSortOrder());
			notifyCondition(executed);
		});
		waitOnCondition(executed, "setUp");
		/*
		 * bindConverterProperties shall be performed on request in tests
		 */
	}

	/**
	 * Bind {@link #testConverter}'s properties to test properties just like
	 * a controlller would.
	 * @param testName Name of the test to perform these bindings
	 */
	private void bindConverterProperties(String testName)
	{
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		executed.setValue(false);
		Platform.runLater(() -> {
			if (!propertiesBound)
			{
				testConverter.measureTypeProperty().bind(measuresComboBox.valueProperty());
				switchButton.disableProperty().bind(testConverter.unexchangeableUnitsProperty());
				testConverter.sourceUnitProperty().bindBidirectional(sourceUnitComboBox.valueProperty());
				testConverter.destinationUnitProperty().bindBidirectional(destinationUnitComboBox.valueProperty());
				testConverter.sourceOrderProperty().bind(sourceUnitSortingComboBox.valueProperty());
				testConverter.destinationOrderProperty().bind(destinationUnitSortingComboBox.valueProperty());
				testConverter.inputTextProperty().bindBidirectional(sourceTextField.textProperty());
				destinationLabel.textProperty().bind(testConverter.outputTextProperty());
				propertiesBound = true;
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
	}

	/**
	 * Teardown after each test
	 * @throws java.lang.Exception if teardown fails
	 */
	@AfterEach
	void tearDown() throws Exception
	{
		if (propertiesBound)
		{
			unBindConverterProperties("tearDown");
		}
		testConverter.clear();
		testConverter = null;
		System.gc();

		executed.setValue(false);
		Platform.runLater(() -> {
			measuresComboBox.setValue(initialMeasureType);
			sourceTextField.setText(null);
//			sourceUnitLabel.setText(null);
			destinationLabel.setText(null);
//			destinationUnitLabel.setText(null);
			ObservableList<Unit<Double>> destinationUnitsItems = destinationUnitComboBox.getItems();
			if (!destinationUnitsItems.isEmpty())
			{
				destinationUnitsItems.clear();
			}
			ObservableList<Unit<Double>> sourceUnitsItems = sourceUnitComboBox.getItems();
			if (!sourceUnitsItems.isEmpty())
			{
				sourceUnitsItems.clear();
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, "tearDown");
	}

	/**
	 * Unbind {@link #testConverter}'s properties from or to test properties
	 * @param testName Name of the test to perform these bindings
	 */
	private void unBindConverterProperties(String testName)
	{
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");
		executed.setValue(false);
		Platform.runLater(() -> {
			if (propertiesBound)
			{
				destinationLabel.textProperty().unbind();
				sourceTextField.textProperty().unbindBidirectional(testConverter.inputTextProperty());
				testConverter.destinationOrderProperty().unbind();
				testConverter.sourceOrderProperty().unbind();
				destinationUnitComboBox.valueProperty().unbindBidirectional(testConverter.destinationUnitProperty());
				sourceUnitComboBox.valueProperty().unbindBidirectional(testConverter.sourceUnitProperty());
				switchButton.disableProperty().unbind();
				testConverter.measureTypeProperty().unbind();

				propertiesBound = false;
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		/*
		 * Template to run code on FX thread
		 */
//		executed.setValue(false);
//		Platform.runLater(() -> {
//			// ...
//			notifyCondition(executed);
//		});
//		waitOnCondition(executed, testName);
	}

	/**
	 * Test method for {@link application.Converter#Converter(measures.MeasureType)}.
	 */
	@Test
	@DisplayName("Converter(MeasureType)")
	@Order(1)
	final void testConverter()
	{
		testName = "Converter(MeasureType)";
		System.out.println(testName);

		for (MeasureType type : MeasureType.all())
		{
			try
			{
				testConverter = new Converter(type);
			}
			catch (ParseException e)
			{
				fail(testName + " unexpected ParseException "
				    + e.getLocalizedMessage());
			}
			assertNotNull(testConverter,
			              testName + " unexpected null converter instance");
			ObservableList<Unit<Double>> sourceUnits = testConverter.getSourceUnits();

			Set<Unit<Double>> unitSet = null;
			try
			{
				unitSet = UnitsFactory.getUnits(type);
			}
			catch (NullPointerException e)
			{
				fail(testName + " unexected null arguments in built units"
				    + e.getLocalizedMessage());
			}
			catch (ParseException e)
			{
				fail(testName + " unexpected ParseException "
				    + e.getLocalizedMessage());
			}

			/*
			 * Source Units may contain least elements than destination units
			 * since all source units must be setable
			 */
			ObservableList<Unit<Double>> destinationUnits = testConverter.getdestinationUnits();
			assertTrue(sourceUnits.size() <= destinationUnits.size(),
			           testName + " unexpected source units size");

			/*
			 * Assert source units are all setable
			 */
			for (Unit<Double> unit : sourceUnits)
			{
				assertTrue(unitSet.contains(unit),
				           testName + " unexpected uncontained unit");
				assertTrue(unit.isSetable(),
				           testName + " unexected not setable unit");
			}
			for (Unit<Double> unit : destinationUnits)
			{
				assertTrue(unitSet.contains(unit),
				           testName + " unexpected uncontained unit");
			}
		}
	}

	/**
	 * Test method for {@link application.Converter#measureTypeProperty()}.
	 */
	@Test
	@DisplayName("measureTypeProperty()")
	@Order(2)
	final void testMeasureTypeProperty()
	{
		testName = "measureTypeProperty()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		ObjectProperty<MeasureType> converterProperty = testConverter.measureTypeProperty();
		ObjectProperty<MeasureType> uiProperty = measuresComboBox.valueProperty();

		assertFalse(uiProperty.isBound(),
		            testName + " unexpected ui bound property");
		assertFalse(converterProperty.isBound(),
		            testName + " unexpected converter bound property");

		executed.setValue(false);
		Platform.runLater(() -> {
			uiProperty.set(MeasureType.LENGTH);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertNotEquals(uiProperty.get(),
		                converterProperty.get(),
		                testName + " unexpected same value");

		converterProperty.set(MeasureType.LENGTH);
		assertEquals(uiProperty.get(),
		             converterProperty.get(),
		             testName + " unexpected not same value");

		/*
		 * converter's measureType shall be bound to #measuresComboBox value
		 * property
		 */
		bindConverterProperties(testName);
		assertFalse(uiProperty.isBound(),
		            testName + " unexpected ui bound property");
		assertTrue(converterProperty.isBound(),
		           testName + " unexpected not bound converter property");
		assertEquals(uiProperty.get(),
		             converterProperty.get(),
		             testName + " unexpected property value");
		MeasureType expected = MeasureType.DIRECTION;

		executed.setValue(false);
		Platform.runLater(() -> {
			uiProperty.set(expected);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertEquals(expected,
		             converterProperty.get(),
		             testName + " unexpected property value");

		/*
		 * Can't set value on bound property : RuntimeException
		 */
		assertThrows(RuntimeException.class, () -> {
			converterProperty.set(MeasureType.PRESSURE);
		});
	}

	/**
	 * Test method for {@link application.Converter#getMeasureType()}.
	 */
	@Test
	@DisplayName("getMeasureType()")
	@Order(3)
	final void testGetMeasureType()
	{
		testName = "getMeasureType()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		/*
		 * Initial measure type shall be #initialMeasureType
		 */
		assertEquals(initialMeasureType,
		             testConverter.getMeasureType(),
		             testName + " unexpected initial measure type");

		ObservableList<MeasureType> measuresList = measuresComboBox.getItems();

		executed.setValue(false);
		Platform.runLater(() -> {
			for (MeasureType type : measuresList)
			{
				measuresComboBox.setValue(type);
				/*
				 * Not bound property won't change converter's property
				 */
				assertEquals(initialMeasureType,
				             testConverter.getMeasureType(),
				             testName + " unexpected measure type value");
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		bindConverterProperties(testName);

		executed.setValue(false);
		Platform.runLater(() -> {
			for (MeasureType type : measuresList)
			{
				measuresComboBox.setValue(type);
				/*
				 * Bound property will change converter's property
				 */
				assertEquals(measuresComboBox.getValue(),
				             testConverter.getMeasureType(),
				             testName + " unexpected measure type value");
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
	}

	/**
	 * Test method for {@link application.Converter#setMeasureType(measures.MeasureType)}.
	 */
	@Test
	@DisplayName("setMeasureType(measures.MeasureType)")
	@Order(4)
	final void testSetMeasureType()
	{
		testName = "setMeasureType(measures.MeasureType)";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		ObservableList<MeasureType> measuresList = measuresComboBox.getItems();
		executed.setValue(false);
		Platform.runLater(() -> {
			for (MeasureType type : measuresList)
			{
				try
				{
					/*
					 * Caution : setting measureType will trigger
					 * source and destination units lists clear which might
					 * affect UI, so this operation shall be perfomed on the
					 * JavaFX Thread
					 */
					testConverter.setMeasureType(type);
					assertEquals(type,
					             testConverter.getMeasureType(),
					             testName + " unexpected measure type");
				}
				catch (RuntimeException | ParseException e)
				{
					fail(testName + " unexpected " + e.getLocalizedMessage());
				}
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		bindConverterProperties(testName);

		final MeasureType expected = measuresComboBox.getValue();
		executed.setValue(false);
		Platform.runLater(() -> {
			for (MeasureType type : measuresList)
			{
				try
				{
					/*
					 * Now that converter's measure type property is bound
					 * it shall never change through setMeasureType
					 */
					testConverter.setMeasureType(type);
					assertEquals(expected,
					             testConverter.getMeasureType(),
					             testName + " unexpected measure type");
				}
				catch (RuntimeException | ParseException e)
				{
					fail(testName + " unexpected " + e.getLocalizedMessage());
				}
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
	}

	/**
	 * Test method for {@link application.Converter#applyMeasureType()}.
	 */
	@Test
	@DisplayName("applyMeasureType()")
	@Order(5)
	final void testApplyMeasureType()
	{
		testName = "applyMeasureType()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		bindConverterProperties(testName);

		for (MeasureType type : MeasureType.all())
		{
			/*
			 * Get converter's copy of
			 * 	- source units
			 * 	- destination units
			 */
			List<Unit<Double>> previousSourceUnits =
			    new ArrayList<>(testConverter.getSourceUnits());
			List<Unit<Double>> previousDestinationUnits =
			    new ArrayList<>(testConverter.getdestinationUnits());
			assertNotNull(previousSourceUnits,
			              testName + " unexpected null previous source units");
			assertFalse(previousSourceUnits.isEmpty(),
			            testName + " unexpectedl empty previous source units");
			assertNotNull(previousDestinationUnits,
			              testName + " unexpected null previous destination units");
			assertFalse(previousDestinationUnits.isEmpty(),
			            testName + " unexpectedl empty previous destination units");

			executed.setValue(false);
			Platform.runLater(() -> {
				measuresComboBox.setValue(type);
				assertEquals(type,
				             testConverter.getMeasureType(),
				             testName + " unexpected measure type");
				try
				{
					testConverter.applyMeasureType();
				}
				catch (ParseException e)
				{
					fail(testName + " unexpected " + e.getLocalizedMessage());
				}
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			List<Unit<Double>> newSourceUnits = testConverter.getSourceUnits();
			List<Unit<Double>> newDestinationUnits = testConverter.getdestinationUnits();
			assertNotNull(newSourceUnits,
			              testName + " unexpected null new source units");
			assertFalse(newSourceUnits.isEmpty(),
			            testName + " unexpectedl empty new source units");
			assertNotNull(newDestinationUnits,
			              testName + " unexpected null new destination units");
			assertFalse(newDestinationUnits.isEmpty(),
			            testName + " unexpectedl empty new destination units");
			assertNotSame(previousSourceUnits,
			              newSourceUnits,
			              testName + " unexpected same source lists");
			assertNotSame(previousDestinationUnits,
			              newDestinationUnits,
			              testName + " unexpected same destination lists");

			/*
			 * Applying (evt new) MeasureType should
			 * 	- create new source units
			 * 	- create new destination units
			 * 	- source units should contain only setable units
			 * 	- source units shall be sorted occording to sourcOrder
			 * 	- destination units shall be sorted according to destinationOrder
			 */
			assertTrue(noIntersection(previousSourceUnits, newSourceUnits),
			           testName + " unexpected intersection in source units");
			assertTrue(noIntersection(previousDestinationUnits, newDestinationUnits),
			           testName + " unexpected intersection in destination units");
			for (Unit<Double> unit : newSourceUnits)
			{
				assertTrue(unit.isSetable(),
				           testName + " unexected not setable unit");
			}

			Unit.setOrder(testConverter.getSourceSortOrder());
			assertTrue(isSorted(newSourceUnits),
			           testName + " unexpected not sorted source units");
			Unit.setOrder(testConverter.getDestinationSortOrder());
			assertTrue(isSorted(newDestinationUnits),
			           testName + " unexpected not sorted destination units");
		}
	}

	/**
	 * Test method for {@link application.Converter#getSourceUnits()}.
	 */
	@Test
	@DisplayName("getSourceUnits()")
	@Order(6)
	final void testGetSourceUnits()
	{
		testName = "getSourceUnits()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		bindConverterProperties(testName);

		/*
		 * Source Units shall be the same as the ones provided by
		 * UnitsFactory, evt sorted according to source order
		 */
		for (MeasureType type : measuresComboBox.getItems())
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				measuresComboBox.setValue(type);
				try
				{
					testConverter.applyMeasureType();
				}
				catch (ParseException e)
				{
					fail(testName + " unexpected " + e.getLocalizedMessage());
				}
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			List<Unit<Double>> sourceUnits = testConverter.getSourceUnits();
			Unit.setOrder(testConverter.getSourceSortOrder());
			SortedSet<Unit<Double>> expectedUnits = new TreeSet<>();
			try
			{
				for (Unit<Double> unit : UnitsFactory.getUnits(type))
				{
					if (unit.isSetable())
					{
						expectedUnits.add(unit);
					}
				}
			}
			catch (NullPointerException | ParseException e)
			{
				fail(testName + " unexpected " + e.getLocalizedMessage());
			}
			/*
			 * sourceUnits and expectedUnits shall have the exact same content
			 */
			assertIterableEquals(expectedUnits,
			                     sourceUnits,
			                     testName + " unexpected lists not same content");
		}
	}

	/**
	 * Test method for {@link application.Converter#sourceUnitProperty()}.
	 */
	@Test
	@DisplayName("sourceUnitProperty()")
	@Order(7)
	final void testSourceUnitProperty()
	{
		testName = "sourceUnitProperty()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		ObjectProperty<Unit<Double>> converterProperty = testConverter.sourceUnitProperty();
		ObjectProperty<Unit<Double>> uiProperty = sourceUnitComboBox.valueProperty();

		ObservableList<Unit<Double>> units = sourceUnitComboBox.getItems();
		assertNotNull(units,
		              testName + " unexpected null units list in sourceUnitComboBox");
		assertFalse(units.size() < 2,
		            testName + " can't perform without at least 2 units in "
		            	+ "sourceUnitComboBox");

		final Unit<Double> sampleUnit = units.get(units.size() / 2);

		assertFalse(uiProperty.isBound(),
		            testName + " unexpected ui bound property");
		assertFalse(converterProperty.isBound(),
		            testName + " unexpected converter bound property");

		executed.setValue(false);
		Platform.runLater(() -> {
			uiProperty.set(sampleUnit);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertNotEquals(uiProperty.get(),
		                converterProperty.get(),
		                testName + " unexpected same value in properties");

		converterProperty.set(sampleUnit);
		assertEquals(uiProperty.get(),
		             converterProperty.get(),
		             testName + " unexpected not same value in properties");

		/*
		 * Converter#sourceUnit property shall be bound bidirectionnally to
		 * #sourceUnitComboBox's valueProperty : Changing one should change the
		 * other
		 */
		bindConverterProperties(testName);
		assertEquals(uiProperty.get(),
		             converterProperty.get(),
		             testName + " unexpected converter property value");

		for (Unit<Double> unit : units)
		{
			final Unit<Double> expected = unit;

			executed.setValue(false);
			Platform.runLater(() -> {
				uiProperty.set(expected);
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertEquals(expected,
			             converterProperty.get(),
			             testName + " unexpected converter property value");
		}

		for (Unit<Double> unit : units)
		{
			final Unit<Double> expected2 = unit;

			executed.setValue(false);
			Platform.runLater(() -> {
				converterProperty.set(expected2);
				assertEquals(expected2,
				             uiProperty.get(),
				             testName + " unexpected ui property value");
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);
		}
	}

	/**
	 * Test method for {@link application.Converter#getSourceUnit()}.
	 */
	@Test
	@DisplayName("getSourceUnit()")
	@Order(8)
	final void testGetSourceUnit()
	{
		testName = "getSourceUnit()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		/*
		 * Default source unit should be first setable unit from
		 * UnitsFactory
		 */
		Set<Unit<Double>> unitSet = null;
		try
		{
			unitSet = UnitsFactory.getUnits(initialMeasureType);
		}
		catch (NullPointerException | ParseException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}
		Unit<Double> expectedUnit = null;
		for (Unit<Double> unit : unitSet)
		{
			if (unit.isSetable())
			{
				expectedUnit = unit;
				break;
			}
		}

		assertEquals(expectedUnit,
		             testConverter.getSourceUnit(),
		             testName + " unexpected initial Source unit");

		bindConverterProperties(testName);
		executed.setValue(false);
		Platform.runLater(() -> {
			for (Unit<Double> unit : sourceUnitComboBox.getItems())
			{
				sourceUnitComboBox.setValue(unit);
				assertEquals(unit,
				             testConverter.getSourceUnit(),
				             testName + " unexpected Source unit");
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
	}

	/**
	 * Test method for {@link application.Converter#setSourceUnit(measures.units.Unit)}.
	 */
	@Test
	@DisplayName("setSourceUnit(measures.units.Unit)")
	@Order(9)
	final void testSetSourceUnit()
	{
		testName = "setSourceUnit(measures.units.Unit)";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		List<Unit<Double>> sourceUnitsList = testConverter.getSourceUnits();
		List<Unit<Double>> destinationUnitList = testConverter.getdestinationUnits();
		Unit<Double> searchUnit = null;
		for (Unit<Double> unit : destinationUnitList)
		{
			if (!sourceUnitsList.contains(unit))
			{
				searchUnit = unit;
				break;
			}
		}
		final Unit<Double> unavailableUnit = searchUnit;

		/*
		 * Setting a value outside of source unit shall trigger an
		 * IllegalArgumentException
		 */
		assertThrows(IllegalArgumentException.class, () -> {
			testConverter.setSourceUnit(unavailableUnit);
		});

		for (Unit<Double> unit : sourceUnitsList)
		{
			testConverter.setSourceUnit(unit);
			assertEquals(unit,
			             testConverter.getSourceUnit(),
			             testName + " unexpected source unit");
		}

		bindConverterProperties(testName);

		/*
		 * Converter's source unit property is now bidirectionnaly bound
		 * to #sourceUnitComboBox's value property
		 * Setting a value shall also change #sourceUnitComboBox
		 * Note : setSourceUnit will trigger applySourceUnit(); which might
		 * change inputText which is bidirectionnaly bound so this
		 * should be executed on a JavaFX thread
		 */
		for (Unit<Double> unit : sourceUnitsList)
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				testConverter.setSourceUnit(unit);
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);
			assertEquals(unit,
			             testConverter.getSourceUnit(),
			             testName + " unexpected source unit in converter");
			assertEquals(unit,
			             sourceUnitComboBox.getValue(),
			             testName + " unexpected source unit in combobox");
		}
		for (Unit<Double> unit : sourceUnitComboBox.getItems())
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				sourceUnitComboBox.setValue(unit);
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);
			assertEquals(unit,
			             testConverter.getSourceUnit(),
			             testName + " unexpected source unit in converter");
			assertEquals(unit,
			             sourceUnitComboBox.getValue(),
			             testName + " unexpected source unit in combobox");
		}
	}

	/**
	 * Test method for {@link application.Converter#applySourceUnit()}.
	 */
	@Test
	@DisplayName("applySourceUnit()")
	@Order(10)
	final void testApplySourceUnit()
	{
		testName = "applySourceUnit()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		/*
		 * Apply Source unit does nothing if input is null or empty
		 */
		assertNull(testConverter.getInputText(),
		           testName + " unexpected non null input text");
		testConverter.applySourceUnit();
		assertNull(testConverter.getInputText(),
		           testName + " unexpected non null input text");

		/*
		 * Apply Source unit triggers conversion if input text is not empty
		 */
		StringProperty inputTextProperty = testConverter.inputTextProperty();
		String unexpected = "Not a number";
		inputTextProperty.set(unexpected);
		assertEquals(unexpected,
		             inputTextProperty.get(),
		             testName + " unexpected intput text value");
		/*
		 * Parsing invalid value shall lead to null input text
		 */
		testConverter.applySourceUnit();
		assertNull(testConverter.getInputText(),
		           testName + " unexpected non null input text");

		/*
		 * Parsing valid value should lead to source unit value formatted in
		 * input text
		 */
		String expected = "1.0";
		inputTextProperty.set(expected);
		assertEquals(expected,
		             inputTextProperty.get(),
		             testName + " unexpected intput text value");

		testConverter.applySourceUnit();
		Unit<Double> sourceUnit = testConverter.getSourceUnit();
		assertNotNull(sourceUnit,
		              testName + " unexpected null source unit");
		assertEquals(sourceUnit.formatValue(),
		             inputTextProperty.get(),
		             testName + " unexpected input text content");
	}

	/**
	 * Test method for {@link application.Converter#sourceOrderProperty()}.
	 */
	@Test
	@DisplayName("sourceOrderProperty()")
	@Order(11)
	final void testSourceOrderProperty()
	{
		testName = "sourceOrderProperty()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");
		ObjectProperty<SortOrder> converterProperty = testConverter.sourceOrderProperty();
		ObjectProperty<SortOrder> uiProperty = sourceUnitSortingComboBox.valueProperty();

		assertFalse(uiProperty.isBound(),
		            testName + " unexpected ui bound property");
		assertFalse(converterProperty.isBound(),
		            testName + " unexpected converter bound property");

		executed.setValue(false);
		Platform.runLater(() -> {
			uiProperty.set(SortOrder.FACTOR_DESCENDING);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertNotEquals(uiProperty.get(),
		                converterProperty.get(),
		                testName + " unexpected same value");

		converterProperty.set(SortOrder.FACTOR_DESCENDING);
		assertEquals(uiProperty.get(),
		             converterProperty.get(),
		             testName + " unexpected not same value");

		/*
		 * converter's source sort order shall be bound to #sourceUnitSortingComboBox
		 * value property
		 */
		bindConverterProperties(testName);

		assertFalse(uiProperty.isBound(),
		            testName + " unexpected ui bound property");
		assertTrue(converterProperty.isBound(),
		           testName + " unexpected not bound converter property");
		assertEquals(uiProperty.get(),
		             converterProperty.get(),
		             testName + " unexpected property value");

		List<SortOrder> sortOrders = sourceUnitSortingComboBox.getItems();
		for (SortOrder order : sortOrders)
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				uiProperty.set(order);
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertEquals(order,
			             converterProperty.get(),
			             testName + " unexpected property value");
		}

		/*
		 * Can't set value on bound property : RuntimeException
		 */
		assertThrows(RuntimeException.class, () -> {
			converterProperty.set(SortOrder.NAME_DESCENDING);
		});
	}

	/**
	 * Test method for {@link application.Converter#getSourceSortOrder()}.
	 */
	@Test
	@DisplayName("getSourceSortOrder()")
	@Order(12)
	final void testGetSourceSortOrder()
	{
		testName = "getSourceSortOrder()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		/*
		 * Initial source sort order should be #initialSortOrder
		 */
		assertEquals(initialSortOrder,
		             testConverter.getSourceSortOrder(),
		             testName + " unexpected initial source unit sort order");

		ObservableList<SortOrder> sortOrderList = sourceUnitSortingComboBox.getItems();

		executed.setValue(false);
		Platform.runLater(() -> {
			for (SortOrder order : sortOrderList)
			{
				sourceUnitSortingComboBox.setValue(order);
				/*
				 * Not bound property won't change converter's property
				 */
				assertEquals(initialSortOrder,
				             testConverter.getSourceSortOrder(),
				             testName + " unexpected source sort order");
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		bindConverterProperties(testName);

		executed.setValue(false);
		Platform.runLater(() -> {
			for (SortOrder order : sortOrderList)
			{
				sourceUnitSortingComboBox.setValue(order);
				/*
				 * Bound property will change converter's property
				 */
				assertEquals(sourceUnitSortingComboBox.getValue(),
				             testConverter.getSourceSortOrder(),
				             testName + " unexpected source sort order");
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
	}

	/**
	 * Test method for {@link application.Converter#setSourceSortOrder(measures.units.SortOrder)}.
	 */
	@Test
	@DisplayName("setSourceSortOrder(measures.units.SortOrder)")
	@Order(13)
	final void testSetSourceSortOrder()
	{
		testName = "setSourceSortOrder(measures.units.SortOrder)";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		ObservableList<SortOrder> orderList = sourceUnitSortingComboBox.getItems();
		executed.setValue(false);
		Platform.runLater(() -> {
			for (SortOrder order : orderList)
			{
				/*
				 * Caution : setting sort order will trigger
				 * sorce units resorting which might affect UI, so this
				 * operation shall be perfomed on the JavaFX Thread
				 */
				testConverter.setSourceSortOrder(order);
				assertEquals(order,
				             testConverter.getSourceSortOrder(),
				             testName + " unexpected sort order");
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		bindConverterProperties(testName);

		final SortOrder expected = sourceUnitSortingComboBox.getValue();
		executed.setValue(false);
		Platform.runLater(() -> {
			for (SortOrder order : orderList)
			{
				/*
				 * Now that converter's source sort order property is bound
				 * it shall never change through setSourceSortOrder
				 */
				testConverter.setSourceSortOrder(order);
				assertEquals(expected,
				             testConverter.getSourceSortOrder(),
				             testName + " unexpected measure type");
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
	}

	/**
	 * Test method for {@link application.Converter#applySourceSortOrder()}.
	 */
	@Test
	@DisplayName("applySourceSortOrder()")
	@Order(14)
	final void testApplySourceSortOrder()
	{
		testName = "applySourceSortOrder()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		/*
		 * Apply source sort order should apply the currently selected
		 * order to the source units.
		 * So we should check source units are sorted according to the
		 * current order
		 */
		for (SortOrder order : SortOrder.all())
		{
			ObjectProperty<SortOrder> orderProperty = testConverter.sourceOrderProperty();
			orderProperty.set(order);
			assertEquals(order,
			             testConverter.getSourceSortOrder(),
			             testName +  " unexpected sources sort order");

			executed.setValue(false);
			Platform.runLater(() -> {
				testConverter.applySourceSortOrder();
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertEquals(order,
			             Unit.getOrder(),
			             testName + " unexpected Units orderding");
			assertTrue(isSorted(testConverter.getSourceUnits()),
			           testName + " sources are not sorted according to " + order);
		}

		bindConverterProperties(testName);

		for (SortOrder order : sourceUnitSortingComboBox.getItems())
		{
			ObjectProperty<SortOrder> orderProperty = sourceUnitSortingComboBox.valueProperty();
			executed.setValue(false);
			Platform.runLater(() -> {
				orderProperty.set(order);
				testConverter.applySourceSortOrder();
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertEquals(order,
			             testConverter.getSourceSortOrder(),
			             testName +  " unexpected sources sort order");
			assertEquals(order,
			             Unit.getOrder(),
			             testName + " unexpected Units orderding");
			assertTrue(isSorted(testConverter.getSourceUnits()),
			           testName + " sources are not sorted according to " + order);
		}

//		executed.setValue(false);
//		Platform.runLater(() -> {
//			notifyCondition(executed);
//		});
//		waitOnCondition(executed, testName);
	}

	/**
	 * Test method for {@link application.Converter#getdestinationUnits()}.
	 */
	@Test
	@DisplayName("getdestinationUnits()")
	@Order(15)
	final void testGetdestinationUnits()
	{
		testName = "getdestinationUnits()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");
		bindConverterProperties(testName);

		/*
		 * Source Units shall be the same as the ones provided by
		 * UnitsFactory, evt sorted according to source order
		 */
		for (MeasureType type : measuresComboBox.getItems())
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				measuresComboBox.setValue(type);
				try
				{
					testConverter.applyMeasureType();
				}
				catch (ParseException e)
				{
					fail(testName + " unexpected " + e.getLocalizedMessage());
				}
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			List<Unit<Double>> destinationUnits = testConverter.getdestinationUnits();
			Unit.setOrder(testConverter.getSourceSortOrder());
			SortedSet<Unit<Double>> expectedUnits = null;
			try
			{
				expectedUnits = new TreeSet<>(UnitsFactory.getUnits(type));
			}
			catch (NullPointerException | ParseException e)
			{
				fail(testName + " unexpected " + e.getLocalizedMessage());
			}
			/*
			 * sourceUnits and expectedUnits shall have the exact same content
			 */
			assertIterableEquals(expectedUnits,
			                     destinationUnits,
			                     testName + " unexpected lists not same content");
		}
	}

	/**
	 * Test method for {@link application.Converter#destinationUnitProperty()}.
	 */
	@Test
	@DisplayName("destinationUnitProperty()")
	@Order(16)
	final void testDestinationUnitProperty()
	{
		testName = "destinationUnitProperty()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		ObjectProperty<Unit<Double>> converterProperty = testConverter.destinationUnitProperty();
		ObjectProperty<Unit<Double>> uiProperty = destinationUnitComboBox.valueProperty();

		ObservableList<Unit<Double>> units = destinationUnitComboBox.getItems();
		assertNotNull(units,
		              testName + " can't perform with null units list from "
		              	+ "destinationUnitComboBox");
		assertTrue(units.size() > 1,
		           testName + " can't perform with at least 2 units in "
		           	+ "destinationUnitComboBox");
		final Unit<Double> sampleUnit = units.get(units.size() / 2);

		assertFalse(uiProperty.isBound(),
		            testName + " unexpected ui bound property");
		assertFalse(converterProperty.isBound(),
		            testName + " unexpected converter bound property");

		executed.setValue(false);
		Platform.runLater(() -> {
			uiProperty.set(sampleUnit);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertNotEquals(uiProperty.get(),
		                converterProperty.get(),
		                testName + " unexpected same value on properties");

		converterProperty.set(sampleUnit);
		assertEquals(uiProperty.get(),
		             converterProperty.get(),
		             testName + " unexpected not same value on properties");

		/*
		 * Converter#destinationUnit property shall be bound bidirectionnally to
		 * #destinationUnitComboBox's valueProperty : Changing one should change the
		 * other
		 */
		bindConverterProperties(testName);
		assertEquals(uiProperty.get(),
		             converterProperty.get(),
		             testName + " unexpected converter property value");

		for (Unit<Double> unit : units)
		{
			final Unit<Double> expected = unit;

			executed.setValue(false);
			Platform.runLater(() -> {
				uiProperty.set(expected);
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertEquals(expected,
			             converterProperty.get(),
			             testName + " unexpected converter property value");
		}

		for (Unit<Double> unit : units)
		{
			final Unit<Double> expected = unit;

			executed.setValue(false);
			Platform.runLater(() -> {
				converterProperty.set(expected);
				assertEquals(expected,
				             uiProperty.get(),
				             testName + " unexpected ui property value");
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);
		}
	}

	/**
	 * Test method for {@link application.Converter#getDestinationUnit()}.
	 */
	@Test
	@DisplayName("getDestinationUnit()")
	@Order(17)
	final void testGetDestinationUnit()
	{
		testName = "getDestinationUnit()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		/*
		 * Default destination unit should be first unit from UnitsFactory
		 * different from source unit
		 */
		Set<Unit<Double>> unitSet = null;
		try
		{
			unitSet = UnitsFactory.getUnits(initialMeasureType);
		}
		catch (NullPointerException | ParseException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}
		Unit<Double> sourceUnit = testConverter.getSourceUnit();
		Unit<Double> expectedUnit = null;

		for (Unit<Double> unit : unitSet)
		{
			if (!unit.equals(sourceUnit))
			{
				expectedUnit = unit;
				break;
			}
		}

		assertEquals(expectedUnit,
		             testConverter.getDestinationUnit(),
		             testName + " unexpected initial Destination unit");

		bindConverterProperties(testName);

		executed.setValue(false);
		Platform.runLater(() -> {
			for (Unit<Double> unit : destinationUnitComboBox.getItems())
			{
				destinationUnitComboBox.setValue(unit);
				assertEquals(unit,
				             testConverter.getDestinationUnit(),
				             testName + " unexpected Destination unit");
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
	}

	/**
	 * Test method for {@link application.Converter#setDestinationUnit(measures.units.Unit)}.
	 */
	@Test
	@DisplayName("setDestinationUnit(measures.units.Unit)")
	@Order(18)
	final void testSetDestinationUnit()
	{
		testName = "setDestinationUnit(measures.units.Unit)";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		List<Unit<Double>> destinationUnitList = testConverter.getdestinationUnits();
		Unit<Double> otherUnit = null;
		try
		{
			otherUnit =
			    UnitsFactory.getDerivedNumericUnit(initialMeasureType,
			                                       "furlong",
			                                       "f",
			                                       "4.1",
			                                       1.0,
			                                       5.0,
			                                       0.0,
			                                       OperationOrder.FACTOR_ONLY);
		}
		catch (NullPointerException | ParseException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}

		/*
		 * Setting a value outside of source unit shall trigger an
		 * IllegalArgumentException
		 */
		final Unit<Double> unavailableUnit = otherUnit;
		assertThrows(IllegalArgumentException.class, () -> {
			testConverter.setDestinationUnit(unavailableUnit);
		});

		for (Unit<Double> unit : destinationUnitList)
		{
			testConverter.setDestinationUnit(unit);
			assertEquals(unit,
			             testConverter.getDestinationUnit(),
			             testName + " unexpected destination unit");
		}

		bindConverterProperties(testName);

		/*
		 * Converter's destination unit property is now bidirectionnaly bound
		 * to #destinationUnitComboBox's value property
		 * Setting a value shall also change #destinationUnitComboBox
		 * Note : setDestinationUnit will trigger applyDestinationUnit(); which might
		 * trigger changes in UI so this should be executed on a JavaFX thread
		 */
		for (Unit<Double> unit : destinationUnitList)
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				testConverter.setDestinationUnit(unit);
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);
			assertEquals(unit,
			             testConverter.getDestinationUnit(),
			             testName + " unexpected destination unit in converter");
			assertEquals(unit,
			             destinationUnitComboBox.getValue(),
			             testName + " unexpected destination unit in combobox");
		}
		for (Unit<Double> unit : sourceUnitComboBox.getItems())
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				destinationUnitComboBox.setValue(unit);
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);
			assertEquals(unit,
			             testConverter.getDestinationUnit(),
			             testName + " unexpected destination unit in converter");
			assertEquals(unit,
			             destinationUnitComboBox.getValue(),
			             testName + " unexpected destination unit in combobox");
		}
	}

	/**
	 * Test method for {@link application.Converter#applyDestinationUnit()}.
	 */
	@Test
	@DisplayName("applyDestinationUnit()")
	@Order(19)
	final void testApplyDestinationUnit()
	{
		testName = "applyDestinationUnit()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		/*
		 * Initial state shall not allow conversion
		 */
		String initialOutput = testConverter.getOutputText();
		testConverter.applyDestinationUnit();
		assertEquals(initialOutput,
		             testConverter.getOutputText(),
		             testName + " unexpected output text");

		for (MeasureType type : MeasureType.all())
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				try
				{
					testConverter.setMeasureType(type);
				}
				catch (ParseException e)
				{
					fail(testName + " unexpected " + e.getLocalizedMessage());
				}
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);
			testConverter.setInputText("1.0");
			Unit<Double> sourceUnit = testConverter.getSourceUnit();
			assertNotNull(sourceUnit,
			              testName + " unexpected null source unit");
			assertTrue(sourceUnit.hasValue(),
			           testName + " source unit has no value");
			ObjectProperty<Unit<Double>> destinationUnitProperty = testConverter.destinationUnitProperty();
			for (Unit<Double> unit : testConverter.getdestinationUnits())
			{
				/*
				 * If destination unit is not null then
				 *	- Converter#unexchangeableUnitsProperty shall be updated according
				 *	to destination unit nature
				 *	- If source unit is not null and source has a value then
				 * 	conversion shall be performed
				 */
				destinationUnitProperty.set(unit);
				assertEquals(unit,
				             testConverter.getDestinationUnit(),
				             testName + " unexpected destination unit");

				testConverter.applyDestinationUnit();

				assertEquals(!unit.isSetable(),
				             testConverter.unexchangeableUnitsProperty().get(),
				             testName + " unexpected unexchangeable property");

				String outputText = testConverter.getOutputText();
				assertNotNull(outputText,
				              testName + " unexpected null output text");
				assertFalse(outputText.isEmpty(),
				            testName + " unexpected empty output text");
				assertEquals(testConverter.getSourceUnit().getSIValue(),
				             testConverter.getDestinationUnit().getSIValue(),
				             1e-6,
				             testName + " unexpected destination unit SI value");
			}
		}

		bindConverterProperties(testName);

		for (MeasureType type : measuresComboBox.getItems())
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				try
				{
					testConverter.setMeasureType(type);
					sourceTextField.setText("1.0");
					testConverter.applyInputText();
				}
				catch (ParseException e)
				{
					fail(testName + " unexpected " + e.getLocalizedMessage());
				}
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);
			assertTrue(testConverter.getSourceUnit().hasValue(),
			           testName + " source unit has no value");
			ObjectProperty<Unit<Double>> destinationUnitProperty = destinationUnitComboBox.valueProperty();
			for (Unit<Double> unit : testConverter.getdestinationUnits())
			{
				/*
				 * If destination unit is not null then
				 *	- Converter#unexchangeableUnitsProperty shall be updated according
				 *	to destination unit nature
				 *	- If source unit is not null and source has a value then
				 * 	conversion shall be performed
				 */
				executed.setValue(false);
				Platform.runLater(() -> {
					destinationUnitProperty.set(unit);
					notifyCondition(executed);
				});
				waitOnCondition(executed, testName);

				assertEquals(unit,
				             testConverter.getDestinationUnit(),
				             testName + " unexpected destination unit");

				executed.setValue(false);
				Platform.runLater(() -> {
					testConverter.applyDestinationUnit();
					notifyCondition(executed);
				});
				waitOnCondition(executed, testName);

				assertEquals(!unit.isSetable(),
				             testConverter.unexchangeableUnitsProperty().get(),
				             testName + " unexpected unexchangeable property");

				String outputText = destinationLabel.getText();
				assertNotNull(outputText,
				              testName + " unexpected null output text");
				assertFalse(outputText.isEmpty(),
				            testName + " unexpected empty output text");
				assertEquals(testConverter.getSourceUnit().getSIValue(),
				             testConverter.getDestinationUnit().getSIValue(),
				             1e-6,
				             testName + " unexpected destination unit SI value");
			}
		}

//		executed.setValue(false);
//		Platform.runLater(() -> {
//			notifyCondition(executed);
//		});
//		waitOnCondition(executed, testName);
	}

	/**
	 * Test method for {@link application.Converter#unexchangeableUnitsProperty()}.
	 */
	@Test
	@DisplayName("unexchangeableUnitsProperty()")
	@Order(20)
	final void testUnexchangeableUnitsProperty()
	{
		testName = "unexchangeableUnitsProperty()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		/*
		 * When properties are bound, setting an unsetable unit in
		 * destination units shall render Converter#unexchangeableUnitsProperty()
		 * true which could disable the #switchButton
		 */

		bindConverterProperties(testName);
//		executed.setValue(false);
//		Platform.runLater(() -> {
//			notifyCondition(executed);
//		});
//		waitOnCondition(executed, testName);

		for (MeasureType type : measuresComboBox.getItems())
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				measuresComboBox.setValue(type);
				try
				{
					testConverter.applyMeasureType();
				}
				catch (ParseException e)
				{
					fail(testName + " unexpected " + e.getLocalizedMessage());
				}
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			for (Unit<Double> sourceUnit : sourceUnitComboBox.getItems())
			{
				executed.setValue(false);
				Platform.runLater(() -> {
					sourceUnitComboBox.setValue(sourceUnit);
					testConverter.applySourceUnit();
					notifyCondition(executed);
				});
				waitOnCondition(executed, testName);
				assertNotNull(testConverter.getSourceUnit(),
				              testName + " unexpected null source unit");
				assertEquals(sourceUnit,
				             testConverter.getSourceUnit(),
				             testName + " unexpected source unit");
				for (Unit<Double> destinationUnit : destinationUnitComboBox.getItems())
				{
					executed.setValue(false);
					Platform.runLater(() -> {
						destinationUnitComboBox.setValue(destinationUnit);
						assertEquals(destinationUnit,
						             testConverter.getDestinationUnit(),
						             testName + " unexpected destination unit");
						testConverter.applyDestinationUnit();
						notifyCondition(executed);
					});
					waitOnCondition(executed, testName);
					if (!destinationUnit.isSetable())
					{
						assertTrue(testConverter.unexchangeableUnitsProperty().get(),
						           testName
						               + " unexpected unexchangeable units property value");
						assertTrue(switchButton.disableProperty().get(),
						           testName
						               + " unexpected disable property value on switch button");
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link application.Converter#destinationOrderProperty()}.
	 */
	@Test
	@DisplayName("destinationOrderProperty()")
	@Order(21)
	final void testDestinationOrderProperty()
	{
		testName = "destinationOrderProperty()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		ObjectProperty<SortOrder> converterProperty = testConverter.destinationOrderProperty();
		ObjectProperty<SortOrder> uiProperty = destinationUnitSortingComboBox.valueProperty();

		assertFalse(uiProperty.isBound(),
		            testName + " unexpected ui bound property");
		assertFalse(converterProperty.isBound(),
		            testName + " unexpected converter bound property");

		executed.setValue(false);
		Platform.runLater(() -> {
			uiProperty.set(SortOrder.FACTOR_DESCENDING);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertNotEquals(uiProperty.get(),
		                converterProperty.get(),
		                testName + " unexpected same value");

		converterProperty.set(SortOrder.FACTOR_DESCENDING);
		assertEquals(uiProperty.get(),
		             converterProperty.get(),
		             testName + " unexpected not same value");

		/*
		 * converter's destination sort order shall be bound to
		 * #destinationUnitSortingComboBox value property
		 */
		bindConverterProperties(testName);

		assertFalse(uiProperty.isBound(),
		            testName + " unexpected ui bound property");
		assertTrue(converterProperty.isBound(),
		           testName + " unexpected not bound converter property");
		assertEquals(uiProperty.get(),
		             converterProperty.get(),
		             testName + " unexpected property value");

		List<SortOrder> sortOrders = destinationUnitSortingComboBox.getItems();
		for (SortOrder order : sortOrders)
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				uiProperty.set(order);
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertEquals(order,
			             converterProperty.get(),
			             testName + " unexpected property value");
		}

		/*
		 * Can't set value on bound property : RuntimeException
		 */
		assertThrows(RuntimeException.class, () -> {
			converterProperty.set(SortOrder.NAME_DESCENDING);
		});
	}

	/**
	 * Test method for {@link application.Converter#getDestinationSortOrder()}.
	 */
	@Test
	@DisplayName("getDestinationSortOrder()")
	@Order(22)
	final void testGetDestinationSortOrder()
	{
		testName = "getDestinationSortOrder()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		/*
		 * Initial source sort order should be #initialSortOrder
		 */
		assertEquals(initialSortOrder,
		             testConverter.getDestinationSortOrder(),
		             testName + " unexpected initial source unit sort order");

		ObservableList<SortOrder> sortOrderList = destinationUnitSortingComboBox.getItems();

		executed.setValue(false);
		Platform.runLater(() -> {
			for (SortOrder order : sortOrderList)
			{
				destinationUnitSortingComboBox.setValue(order);
				/*
				 * Not bound property won't change converter's property
				 */
				assertEquals(initialSortOrder,
				             testConverter.getDestinationSortOrder(),
				             testName + " unexpected source sort order");
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		bindConverterProperties(testName);

		executed.setValue(false);
		Platform.runLater(() -> {
			for (SortOrder order : sortOrderList)
			{
				destinationUnitSortingComboBox.setValue(order);
				/*
				 * Bound property will change converter's property
				 */
				assertEquals(destinationUnitSortingComboBox.getValue(),
				             testConverter.getDestinationSortOrder(),
				             testName + " unexpected source sort order");
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
	}

	/**
	 * Test method for {@link application.Converter#setDestinationSortOrder(measures.units.SortOrder)}.
	 */
	@Test
	@DisplayName("setDestinationSortOrder(measures.units.SortOrder)")
	@Order(23)
	final void testSetDestinationSortOrder()
	{
		testName = "setDestinationSortOrder(measures.units.SortOrder)";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		ObservableList<SortOrder> orderList = destinationUnitSortingComboBox.getItems();
		executed.setValue(false);
		Platform.runLater(() -> {
			for (SortOrder order : orderList)
			{
				/*
				 * Caution : setting sort order will trigger
				 * destination units resorting which might affect UI, so this
				 * operation shall be perfomed on the JavaFX Thread
				 */
				testConverter.setDestinationSortOrder(order);
				assertEquals(order,
				             testConverter.getDestinationSortOrder(),
				             testName + " unexpected sort order");
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		bindConverterProperties(testName);

		final SortOrder expected = destinationUnitSortingComboBox.getValue();
		executed.setValue(false);
		Platform.runLater(() -> {
			for (SortOrder order : orderList)
			{
				/*
				 * Now that converter's destination sort order property is bound
				 * it shall never change through setDestinationSortOrder
				 */
				testConverter.setDestinationSortOrder(order);
				assertEquals(expected,
				             testConverter.getDestinationSortOrder(),
				             testName + " unexpected measure type");
			}
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
	}

	/**
	 * Test method for {@link application.Converter#applyDestinationSortOrder()}.
	 */
	@Test
	@DisplayName("applyDestinationSortOrder()")
	@Order(24)
	final void testApplyDestinationSortOrder()
	{
		testName = "applyDestinationSortOrder()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");
		/*
		 * Apply destination sort order should apply the currently selected
		 * order to the destination units.
		 * So we should check destination units are sorted according to the
		 * current order
		 */
		for (SortOrder order : SortOrder.all())
		{
			ObjectProperty<SortOrder> orderProperty = testConverter.destinationOrderProperty();
			orderProperty.set(order);
			assertEquals(order,
			             testConverter.getDestinationSortOrder(),
			             testName +  " unexpected destinations sort order");

			executed.setValue(false);
			Platform.runLater(() -> {
				testConverter.applyDestinationSortOrder();
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertEquals(order,
			             Unit.getOrder(),
			             testName + " unexpected Units orderding");
			assertTrue(isSorted(testConverter.getdestinationUnits()),
			           testName + " destinations are not sorted according to " + order);
		}

		bindConverterProperties(testName);

		for (SortOrder order : destinationUnitSortingComboBox.getItems())
		{
			ObjectProperty<SortOrder> orderProperty = destinationUnitSortingComboBox.valueProperty();
			executed.setValue(false);
			Platform.runLater(() -> {
				orderProperty.set(order);
				testConverter.applyDestinationSortOrder();
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertEquals(order,
			             testConverter.getDestinationSortOrder(),
			             testName +  " unexpected destinations sort order");
			assertEquals(order,
			             Unit.getOrder(),
			             testName + " unexpected Units orderding");
			assertTrue(isSorted(testConverter.getdestinationUnits()),
			           testName + " destinations are not sorted according to " + order);
		}

//		executed.setValue(false);
//		Platform.runLater(() -> {
//			notifyCondition(executed);
//		});
//		waitOnCondition(executed, testName);
	}

	/**
	 * Test method for {@link application.Converter#inputTextProperty()}.
	 */
	@Test
	@DisplayName("inputTextProperty()")
	@Order(25)
	final void testInputTextProperty()
	{
		testName = "inputTextProperty()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		StringProperty converterProperty = testConverter.inputTextProperty();
		StringProperty uiProperty = sourceTextField.textProperty();

		assertFalse(uiProperty.isBound(),
		            testName + " unexpected ui bound property");
		assertFalse(converterProperty.isBound(),
		            testName + " unexpected converter bound property");

		final String sampleText = "Sample Text";
		executed.setValue(false);
		Platform.runLater(() -> {
			uiProperty.set(sampleText);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertNotEquals(uiProperty.get(),
		                converterProperty.get(),
		                testName + " unexpected same value in properties");

		converterProperty.set(sampleText);
		assertEquals(uiProperty.get(),
		             converterProperty.get(),
		             testName + " unexpected not same value in properties");

		/*
		 * Converter#inputTextProperty shall be bound bidirectionnaly to
		 * #sourceTextField's TextProperty : Changing one should change the
		 * other
		 */
		bindConverterProperties(testName);
		assertEquals(uiProperty.get(),
		             converterProperty.get(),
		             testName + " unexpected converter property value");

		/*
		 * Setting an unparsable value shall lead to a null content since
		 * Converter#setInputText will trigger applyInputText which will
		 * trigger parseInputText(String text)
		 */
		final String unexpected = "Unexpected Sample";
		executed.setValue(false);
		Platform.runLater(() -> {
			testConverter.setInputText(unexpected);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		assertNull(converterProperty.get(),
		           testName + " unexpected non null property on unparsable text");
		assertNull(testConverter.getInputText(),
		           testName + " unexpected non null input text on unparsable text");
		assertNull(uiProperty.get(),
		           testName + " unexpected non null text field property content");
		assertNull(sourceTextField.getText(),
		           testName + " unexpected non null text field content");

		/*
		 * Setting a parsable value
		 */
		Double expected = 1.0;
		final String cexpected = Double.toString(expected);
		executed.setValue(false);
		Platform.runLater(() -> {
			sourceTextField.setText(cexpected);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		String uiPropertyContent = uiProperty.get();
		assertNotNull(uiPropertyContent,
		              testName + " unexpected ui property null content");
		String cPropertyContent = converterProperty.get();
		assertNotNull(cPropertyContent,
		              testName + " unexpected converter property null content");
		assertEquals(converterProperty.get(),
		             uiProperty.get(),
		             testName + " unexpected difference between properties");
		assertEquals(testConverter.getInputText(),
		             sourceTextField.getText(),
		             testName + " unexpected difference betweeen converter input text and text field");

		Double uiValue = null;
		Double cValue = null;
		try
		{
			uiValue = Double.parseDouble(uiPropertyContent);
			cValue = Double.parseDouble(cPropertyContent);
		}
		catch (NumberFormatException | NullPointerException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}
		assertEquals(expected,
		             uiValue,
		             testName + " unexpected value from ui text property");
		assertEquals(expected,
		             cValue,
		             testName + " unexpected value from converter text property");

		expected++;
		final String uexpected = Double.toString(expected);
		executed.setValue(false);
		Platform.runLater(() -> {
			sourceTextField.setText(uexpected);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		cPropertyContent = converterProperty.get();
		assertNotNull(cPropertyContent,
		              testName + " unexpected converter property null content");
		uiPropertyContent = uiProperty.get();
		assertNotNull(uiPropertyContent,
		              testName + " unexpected ui property null content");
		assertEquals(converterProperty.get(),
		             uiProperty.get(),
		             testName + " unexpected difference between properties");
		assertEquals(testConverter.getInputText(),
		             sourceTextField.getText(),
		             testName + " unexpected difference betweeen converter input text and text field");
		uiValue = null;
		cValue = null;
		try
		{
			uiValue = Double.parseDouble(uiPropertyContent);
			cValue = Double.parseDouble(cPropertyContent);
		}
		catch (NumberFormatException | NullPointerException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}
		assertEquals(expected,
		             uiValue,
		             testName + " unexpected value from ui text property");
		assertEquals(expected,
		             cValue,
		             testName + " unexpected value from converter text property");
	}

	/**
	 * Test method for {@link application.Converter#getInputText()}.
	 */
	@Test
	@DisplayName("getInputText()")
	@Order(26)
	final void testGetInputText()
	{
		testName = "getInputText()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		/*
		 * Initial input text should be null
		 */
		assertNull(testConverter.getInputText(),
		           testName + " unexpected non null initialinput text");

		StringProperty converterProperty = testConverter.inputTextProperty();
		assertFalse(converterProperty.isBound(),
		            testName + " unexpected bound property");

		/*
		 * Parsable text should lead to double value
		 */
		final Double expectedValue = 1.0;
		final String valueString = expectedValue.toString();
		testConverter.setInputText(valueString);
		String inputText = testConverter.getInputText();
		assertNotNull(inputText,
		              testName + " unexpected null input text");
		try
		{
			final Double value = Double.parseDouble(inputText);
			assertEquals(expectedValue,
			             value,
			             testName + " unexpected input value");
		}
		catch (NumberFormatException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}

		/*
		 * Unparsable text should lead to null text unless the property is
		 * set directly
		 */
		final String expected = "Test";
		converterProperty.set(expected);
		assertEquals(expected,
		             testConverter.getInputText(),
		             testName + " unexpected input text value");
		testConverter.setInputText(expected);
		assertNull(testConverter.getInputText(),
		           testName + " unexpected non null input text after unparsable text");

		bindConverterProperties(testName);
		final Double expectedValueB = 2.0;
		final String valueStringB = expectedValueB.toString();

		executed.setValue(false);
		Platform.runLater(() -> {
			sourceTextField.setText(valueStringB);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		inputText = testConverter.getInputText();
		assertNotNull(inputText,
		              testName + " unexpected null input text");
		try
		{
			final Double value = Double.parseDouble(inputText);
			assertEquals(expectedValueB,
			             value,
			             testName + " unexpected input value");
		}
		catch (NumberFormatException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}
	}

	/**
	 * Test method for {@link application.Converter#setInputText(java.lang.String)}.
	 */
	@Test
	@DisplayName("setInputText(java.lang.String)")
	@Order(27)
	final void testSetInputTextString()
	{
		testName = "setInputText(java.lang.String)";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		// Setting unparsable value will lead to null text content
		testConverter.setInputText("Not a Number");
		assertNull(testConverter.getInputText(),
		           testName + " unexpected non null input text content after ");

		/*
		 * Setting input text when properties are not bound will always change
		 * input text
		 */
		Double expectedValue = 1.0;
		String valueString = expectedValue.toString();
		testConverter.setInputText(valueString);
		String computedString = testConverter.getInputText();
		assertNotNull(computedString,
		              testName + " unexpected null input text content");
		assertFalse(testConverter.getInputText().isEmpty(),
		            testName + " unexpected empty input text");
		Double computedValue = null;
		try
		{
			computedValue = Double.parseDouble(computedString);
		}
		catch (NumberFormatException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}
		assertEquals(expectedValue,
		             computedValue,
		             testName + " unexpected computed value");


		/*
		 * Input text propety is bound bidirectionnally so changes in either
		 * property will change the other
		 */
		StringProperty converterProperty = testConverter.inputTextProperty();
		StringProperty uiProperty = sourceTextField.textProperty();

		bindConverterProperties(testName);

		executed.setValue(false);
		Platform.runLater(() -> {
			testConverter.setInputText(valueString);
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);

		computedString = uiProperty.get();
		assertNotNull(computedString, testName + " unexpected null content");
		assertFalse(computedString.isEmpty(), testName + " unexpected empty content");
		assertEquals(converterProperty.get(),
		             computedString,
		             testName + " unexpected difference in input text properties");
	}

	/**
	 * Test method for {@link application.Converter#applyInputText()}.
	 */
	@Test
	@DisplayName("applyInputText()")
	@Order(28)
	final void testApplyInputText()
	{
		testName = "applyInputText()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		bindConverterProperties(testName);
		StringProperty converterProperty = testConverter.inputTextProperty();
		StringProperty uiProperty = sourceTextField.textProperty();

		/*
		 * Setting an unparsable text will lead to null content
		 */
		String unparsable = "Not a Number";
		executed.setValue(false);
		Platform.runLater(() -> {
			uiProperty.set(unparsable);
			testConverter.applyInputText();
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
		assertNull(uiProperty.get(),
		           testName + " unexpected non null content");
		assertEquals(converterProperty.get(),
		             uiProperty.get(),
		             testName + " unexpected difference in properties content");

		Double expectedValue = 1.0;
		String valueString = expectedValue.toString();

		executed.setValue(false);
		Platform.runLater(() -> {
			uiProperty.set(valueString);
			testConverter.applyInputText();
			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
		String computedString = uiProperty.get();
		assertNotNull(computedString,
		              testName + " unexpected null content");
		assertFalse(computedString.isEmpty(),
		            testName + " unexpected empty content");
		assertEquals(computedString,
		             uiProperty.get(),
		             testName + " unexpected difference in properties content");
		Double computedValue = null;
		try
		{
			computedValue = Double.parseDouble(computedString);
		}
		catch (NumberFormatException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}
		assertEquals(expectedValue,
		             computedValue,
		             testName + " unexpected value");
	}

	/**
	 * Test method for {@link application.Converter#convert()}.
	 */
	@Test
	@DisplayName("convert()")
	@Order(29)
	final void testConvert()
	{
		testName = "convert()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		/*
		 * source unit has no value yet which will trigger an
		 * IllegalStateException if we try to convert
		 */
		assertThrows(IllegalStateException.class, () -> {
			testConverter.convert();
		});

		Double sourceValue = 1.0;
		String sourceString = sourceValue.toString();
		final double epsilon = 1e-6;

		for (MeasureType type : MeasureType.all())
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				try
				{
					testConverter.setMeasureType(type);
				}
				catch (ParseException e)
				{
					fail(testName + " unexpected " + e.getLocalizedMessage());
				}
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			testConverter.setInputText(sourceString);

			for (Unit<Double> unit : testConverter.getdestinationUnits())
			{
				testConverter.setDestinationUnit(unit);

				/*
				 * destination unit shall have a value and
				 * output text shall be filled
				 */
				Unit<Double> sourceUnit = testConverter.getSourceUnit();
				Unit<Double> destUnit = testConverter.getDestinationUnit();
				assertEquals(sourceUnit.getSIValue(),
				             destUnit.getSIValue(),
				             epsilon,
				             testName + " unexpected converted value");

				String output = testConverter.getOutputText();
				assertNotNull(output,
				              testName + " unexpected null output text");
				assertEquals(destUnit.formatValue(),
				             output,
				             testName + " unexpected output");

				/*
				 * No conversion shall lead to 0.0 value :
				 * The format you'll use shall always show what conversion
				 * from any sourceUnit with a value of "1.0" will provide in
				 * any destinationUnit
				 */
				if (!(destUnit instanceof BaseSymbolicUnit) &&
					!(destUnit instanceof DerivedSymbolicUnit) &&
					!(destUnit instanceof DecomposedUnit))
				{
					Double outputNumber = null;
					try
					{
						outputNumber = Double.parseDouble(output);
						assertNotEquals(0.0,
						                outputNumber.doubleValue(),
						                testName
						                    + " unexpected 0.0 value while converting "
						                    + sourceUnit + "="
						                    + sourceUnit.getValue() + " to "
						                    + destUnit + "="
						                    + destUnit.getValue()
						                    + " ==> destination unit format has not enough decimal digits");
					}
					catch (NumberFormatException e)
					{
						fail(testName + " unexpected not number output "
						    + e.getLocalizedMessage());
					}
				}
			}
		}

		bindConverterProperties(testName);

		for (MeasureType type : measuresComboBox.getItems())
		{
			executed.setValue(false);
			Platform.runLater(() -> {

				measuresComboBox.setValue(type);
				try
				{
					testConverter.applyMeasureType();
				}
				catch (ParseException e)
				{
					fail(testName + " unexpected " + e.getLocalizedMessage());
				}

				List<Unit<Double>> sourceUnits = sourceUnitComboBox.getItems();
				if (sourceUnits != null)
				{
					if (!sourceUnits.isEmpty())
					{
						sourceUnitComboBox.setValue(sourceUnitComboBox.getItems().get(0));
						testConverter.applySourceUnit();
					}
				}

				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			assertNotNull(sourceUnitComboBox.getItems(),
			              testName + " can't perform with null source units");
			assertFalse(sourceUnitComboBox.getItems().isEmpty(),
			            testName + " can't perform with empty source units");

			executed.setValue(false);
			Platform.runLater(() -> {
				sourceTextField.setText(sourceString);
				testConverter.applyInputText();
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			for (Unit<Double> unit : testConverter.getdestinationUnits())
			{
				assertNotNull(testConverter.getSourceUnit(),
				              testName + " unexpected null source unit");
				executed.setValue(false);
				Platform.runLater(() -> {
					destinationUnitComboBox.setValue(unit);
					/*
					 * apply destination unit shall trigger convsrsion iff
					 * source unit is not null and have a value
					 */
					testConverter.applyDestinationUnit();
					notifyCondition(executed);
				});
				waitOnCondition(executed, testName);

				/*
				 * destination unit shall have a value and
				 * output text shall be filled
				 */
				Unit<Double> sourceUnit = testConverter.getSourceUnit();
				Unit<Double> destUnit = testConverter.getDestinationUnit();
				assertEquals(sourceUnit.getSIValue(),
				             destUnit.getSIValue(),
				             epsilon,
				             testName + " unexpected converted value");

				String output = testConverter.getOutputText();
				assertNotNull(output,
				              testName + " unexpected null output text");
				assertFalse(output.isEmpty(),
				            testName + " unexpected empty output text");
				assertEquals(destUnit.formatValue(),
				             output,
				             testName + " unexpected output");
			}
		}
	}

	/**
	 * Test method for {@link application.Converter#outputTextProperty()}.
	 */
	@Test
	@DisplayName("outputTextProperty()")
	@Order(30)
	final void testOutputTextProperty()
	{
		testName = "outputTextProperty()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		StringProperty converterProperty = testConverter.outputTextProperty();
		StringProperty uiProperty = destinationLabel.textProperty();

		assertFalse(converterProperty.isBound(),
		            testName + " unexpected bound converter property");
		assertFalse(uiProperty.isBound(),
		            testName + " unexpected bound ui property");

		/*
		 * Initial value of output text property shall be null until a
		 * conversion occurs
		 */
		assertNull(converterProperty.get(),
		           testName + " unexpected property non null initial value");

		/*
		 * Value after conversion
		 */
		Double inputValue = 1.0;
		String inputValueString = inputValue.toString();
		testConverter.setInputText(inputValueString);

		for (Unit<Double> unit : testConverter.getdestinationUnits())
		{
			testConverter.setDestinationUnit(unit);

			String output = converterProperty.get();
			assertNotNull(output,
			              testName + " unexpected null output value");
			assertFalse(output.isEmpty(),
			            testName + " unexpected empty output value");
			assertEquals(testConverter.getDestinationUnit().formatValue(),
			             output,
			             testName + " unexpected output text value");
		}

		bindConverterProperties(testName);
		assertTrue(uiProperty.isBound(),
		           testName + " unexpected ui property not bound");

		for (Unit<Double> unit : testConverter.getdestinationUnits())
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				destinationUnitComboBox.setValue(unit);
				testConverter.applyDestinationUnit();
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			String output = uiProperty.get();
			assertNotNull(output,
			              testName + " unexpected null output value");
			assertFalse(output.isEmpty(),
			            testName + " unexpected empty output value");
			assertEquals(testConverter.getDestinationUnit().formatValue(),
			             output,
			             testName + " unexpected output text value");
		}
	}

	/**
	 * Test method for {@link application.Converter#getOutputText()}.
	 */
	@Test
	@DisplayName("getOutputText()")
	@Order(31)
	final void testGetOutputText()
	{
		testName = "getOutputText()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		/*
		 * Initial output text (prior to any conversion) should be null
		 */
		String output = testConverter.getOutputText();
		assertNull(output, testName + " unexpected non null output text");

		/*
		 * Any successful conversion shall produce some output text
		 */
		for (MeasureType type : MeasureType.all())
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				try
				{
					testConverter.setMeasureType(type);
				}
				catch (ParseException e)
				{
					fail(testName + " unexpected " + e.getLocalizedMessage());
				}
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			testConverter.setInputText("1.0");
			for (Unit<Double> unit : testConverter.getdestinationUnits())
			{
				testConverter.setDestinationUnit(unit);
				output = testConverter.getOutputText();
				assertNotNull(output,
				              testName + " unexpected null output text");
				assertFalse(output.isEmpty(),
				            testName + " unexpected empty output text");
				assertEquals(testConverter.getDestinationUnit().formatValue(),
				             output,
				             testName + " unexpected output");
			}
		}

		bindConverterProperties(testName);

		for (MeasureType type : measuresComboBox.getItems())
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				measuresComboBox.setValue(type);
				try
				{
					testConverter.applyMeasureType();
				}
				catch (ParseException e)
				{
					fail(testName + " unexpected " + e.getLocalizedMessage());
				}
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);
			executed.setValue(false);
			Platform.runLater(() -> {
				testConverter.setInputText("1.0");
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			for (Unit<Double> unit : destinationUnitComboBox.getItems())
			{
				executed.setValue(false);
				Platform.runLater(() -> {
					testConverter.setDestinationUnit(unit);
					notifyCondition(executed);
				});
				waitOnCondition(executed, testName);
				output = testConverter.getOutputText();
				assertNotNull(output,
				              testName + " unexpected null output text");
				assertFalse(output.isEmpty(),
				            testName + " unexpected empty output text");
				assertEquals(testConverter.getDestinationUnit().formatValue(),
				             output,
				             testName + " unexpected output");
			}
		}

//		executed.setValue(false);
//		Platform.runLater(() -> {
//			notifyCondition(executed);
//		});
//		waitOnCondition(executed, testName);
	}

	/**
	 * Test method for {@link application.Converter#switchUnits()}.
	 */
	@Test
	@DisplayName("switchUnits()")
	@Order(32)
	final void testSwitchUnits()
	{
		testName = "switchUnits()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		/*
		 * Switching source and destination unit shall be possible iff
		 * both are setable
		 */
		for (MeasureType type : MeasureType.all())
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				try
				{
					testConverter.setMeasureType(type);
				}
				catch (ParseException e)
				{
					fail(testName + " unexpected " + e.getLocalizedMessage());
				}
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			List<Unit<Double>> sourceUnits = testConverter.getSourceUnits();
			assertNotNull(sourceUnits,
			              testName + " unexpected null source units");
			assertFalse(sourceUnits.isEmpty(),
			            testName + " unexpected empty source units");

			for (Unit<Double> sourceUnit : sourceUnits)
			{
				List<Unit<Double>> destinationUnits = testConverter.getdestinationUnits();
				assertNotNull(destinationUnits,
				              testName + " unexpected null destination units");
				assertFalse(destinationUnits.isEmpty(),
				            testName + " unexpected empty destination units");
			for (Unit<Double> destinationUnit : destinationUnits)
				{
					/*
					 * Source unit might have changed with a switch so reset it
					 */
					testConverter.setSourceUnit(sourceUnit);
					assertNotNull(testConverter.getSourceUnit(),
					              testName + " unexpected null source unit");
					assertEquals(sourceUnit,
					             testConverter.getSourceUnit(),
					             testName + " unexpected source unit");
					testConverter.setDestinationUnit(destinationUnit);
					assertNotNull(testConverter.getDestinationUnit(),
					              testName + " unexpected null destination unit");
					assertEquals(destinationUnit,
					             testConverter.getDestinationUnit(),
					             testName + " unexpected destination unit");

					boolean isSetable = destinationUnit.isSetable();

					/*
					 * try to switch source and destination units
					 */
					try
					{
						testConverter.switchUnits();
					}
					catch (NullPointerException e)
					{
						fail(testName + " unexpected " + e.getLocalizedMessage());
					}

					Unit<Double> expectedSourceUnit = isSetable ? destinationUnit : sourceUnit;
					Unit<Double> expectedDestinationUnit = isSetable ? sourceUnit : destinationUnit;
					assertEquals(expectedSourceUnit,
					             testConverter.getSourceUnit(),
					             testName + " unexpected source unit");
					assertEquals(expectedDestinationUnit,
					             testConverter.getDestinationUnit(),
					             testName + " unexpected destination unit");
				}
			}
		}

		bindConverterProperties(testName);

		for (MeasureType type : measuresComboBox.getItems())
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				measuresComboBox.setValue(type);
				try
				{
					testConverter.applyMeasureType();
				}
				catch (ParseException e)
				{
					fail(testName + " unexpected " + e.getLocalizedMessage());
				}
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			for (Unit<Double> sourceUnit : sourceUnitComboBox.getItems())
			{
				for (Unit<Double> destinationUnit : destinationUnitComboBox.getItems())
				{
					executed.setValue(false);
					Platform.runLater(() -> {
						/*
						 * Source unit might have changed with a switch so reset it
						 */
						sourceUnitComboBox.setValue(sourceUnit);
						testConverter.applySourceUnit();
						destinationUnitComboBox.setValue(destinationUnit);
						testConverter.applyDestinationUnit();
						notifyCondition(executed);
					});
					waitOnCondition(executed, testName);

					assertNotNull(testConverter.getSourceUnit(),
					              testName + " unexpected null source unit");
					assertEquals(sourceUnit,
					             testConverter.getSourceUnit(),
					             testName + " unexpected source unit");
					assertNotNull(testConverter.getDestinationUnit(),
					              testName + " unexpected null destination unit");
					assertEquals(destinationUnit,
					             testConverter.getDestinationUnit(),
					             testName + " unexpected destination unit");

					boolean isSetable = destinationUnit.isSetable();

					/*
					 * try to switch source and destination units
					 */
					executed.setValue(false);
					Platform.runLater(() -> {
						try
						{
							testConverter.switchUnits();
							testConverter.applySourceUnit();
							testConverter.applyDestinationUnit();
						}
						catch (NullPointerException e)
						{
							fail(testName + " unexpected " + e.getLocalizedMessage());
						}
						notifyCondition(executed);
					});
					waitOnCondition(executed, testName);

					Unit<Double> expectedSourceUnit = isSetable ? destinationUnit : sourceUnit;
					Unit<Double> expectedDestinationUnit = isSetable ? sourceUnit : destinationUnit;
					assertEquals(expectedSourceUnit,
					             testConverter.getSourceUnit(),
					             testName + " unexpected source unit");
					assertEquals(expectedDestinationUnit,
					             testConverter.getDestinationUnit(),
					             testName + " unexpected destination unit");
				}
			}
		}
//		executed.setValue(false);
//		Platform.runLater(() -> {
//			notifyCondition(executed);
//		});
//		waitOnCondition(executed, testName);
	}

	/**
	 * Test method for {@link application.Converter#clear()}.
	 */
	@Test
	@DisplayName("clear()")
	@Order(33)
	final void testClear()
	{
		testName = "clear()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		/*
		 * Set inputText, souceUnit value, outputText and destination unit value
		 * so it can be cleared
		 */
		assertFalse(testConverter.inputTextProperty().isBound(),
		            testName + " input text property unexpectedly bound");
		testConverter.setInputText("1.0");
		testConverter.applyInputText();

		try
		{
			testConverter.convert();
		}
		catch (IllegalStateException e)
		{
			fail(testName + " unexpected " + e.getLocalizedMessage());
		}

		assertFalse(testConverter.getInputText().isEmpty(),
		            testName + " unexpected empty input text");
		assertTrue(testConverter.getSourceUnit().hasValue(),
		           testName + " unexpected empty source unit");
		assertTrue(testConverter.getDestinationUnit().hasValue(),
		           testName + " unexpected empty destination unit");
		assertFalse(testConverter.getOutputText().isEmpty(),
		            testName + " unexpected empty output text");

		/*
		 * Clear
		 * 	- clears source unit's value
		 *	- clears destination unit's value
		 *	- set both input and output text properties values to null
		 */
		testConverter.clear();

		assertNull(testConverter.getInputText(),
		           testName + " unexpected not null input text");
		assertFalse(testConverter.getSourceUnit().hasValue(),
		            testName + " unexpected not empty source unit");
		assertFalse(testConverter.getDestinationUnit().hasValue(),
		            testName + " unexpected not empty destination unit");
		assertNull(testConverter.getOutputText(),
		           testName + " unexpected not null output text");

		bindConverterProperties(testName);

		executed.setValue(false);
		Platform.runLater(() -> {
			sourceTextField.setText("1.0");
			assertFalse(testConverter.getInputText().isEmpty(),
			            testName + " unexpected empty input text");
			testConverter.applyInputText();
			testConverter.convert();
			assertTrue(testConverter.getSourceUnit().hasValue(),
			           testName + " unexpected empty source unit");
			assertTrue(testConverter.getDestinationUnit().hasValue(),
			           testName + " unexpected empty destination unit");
			assertFalse(testConverter.getOutputText().isEmpty(),
			            testName + " unexpected empty output text");

			testConverter.clear();

			assertNull(sourceTextField.getText(),
			           testName + " unexpected not null text field");
			assertNull(testConverter.getInputText(),
			           testName + " unexpected not null input text");
			assertFalse(testConverter.getSourceUnit().hasValue(),
			            testName + " unexpected not empty source unit");
			assertFalse(testConverter.getDestinationUnit().hasValue(),
			            testName + " unexpected not empty destination unit");
			assertNull(testConverter.getOutputText(),
			           testName + " unexpected not null output text");
			assertNull(destinationLabel.getText(),
			           testName + " unexpected not null output Label text");

			notifyCondition(executed);
		});
		waitOnCondition(executed, testName);
	}

	/**
	 * Test method for {@link application.Converter#toString()}.
	 */
	@Test
	@DisplayName("toString()")
	@Order(34)
	final void testToString()
	{
		testName = "toString()";
		System.out.println(testName);
		assertNotNull(testConverter,
		              testName + " unexpected null converter instance");

		for (MeasureType type : MeasureType.all())
		{
			executed.setValue(false);
			Platform.runLater(() -> {
				try
				{
					testConverter.setMeasureType(type);
				}
				catch (ParseException e)
				{
					fail(testName + " unexpected " + e.getLocalizedMessage());
				}
				notifyCondition(executed);
			});
			waitOnCondition(executed, testName);

			String expected = null;
			StringBuilder builder = new StringBuilder();
			builder.append("Source Units:\n");
			for (Unit<Double> unit : testConverter.getSourceUnits())
			{
				builder.append("[");
				builder.append(unit.toString());
				builder.append("]\n");
			}
			Unit<Double> source = testConverter.getSourceUnit();
			builder.append("source: ");
			if (source != null)
			{
				builder.append(source.toString());
			}

			builder.append("\nDestination Units:\n");
			for (Unit<Double> unit : testConverter.getdestinationUnits())
			{
				builder.append("[");
				builder.append(unit.toString());
				builder.append("]\n");
			}

			Unit<Double> destination = testConverter.getDestinationUnit();
			builder.append("destination: ");
			if (destination != null)
			{
				builder.append(destination);
			}

			expected = builder.toString();
			assertEquals(expected,
			             testConverter.toString(),
			             testName + " unexpected toString");
		}
	}

	/**
	 * Sets condition's value to true and notifies any waiting thread on
	 * condition
	 * @param condition the condition to update and notify on
	 */
	private void notifyCondition(Condition condition)
	{
		synchronized (condition)
		{
			condition.setValue(true);
			condition.notify();
		}
	}

	/**
	 * Wait on provided condition to be notified (only if this condition is
	 * false). And reset the condition value to false before returning
	 * @param condition the condition to wait on
	 * @param testName the name of the test this method is used in
	 */
	private void waitOnCondition(Condition condition, String testName)
	{
		synchronized (condition)
		{
//			try
//			{
//				Thread.sleep(100);
//			}
//			catch (InterruptedException e)
//			{
//				fail(testName + " unexpected " + e.getLocalizedMessage());
//			}
			if (!condition.getValue())
			{
				try
				{
					condition.wait();
				}
				catch (IllegalMonitorStateException e)
				{
					fail(testName + " " + e.getLocalizedMessage());
				}
				catch (InterruptedException e)
				{
					fail(testName + " " + e.getLocalizedMessage());
				}
			}
		}
		condition.setValue(false);
	}

	/**
	 * Checks if a list of comparable elements is sorted
	 * @param list the list to check
	 * @return true if the provided list is sorted, false otherwise
	 */
	private static boolean isSorted(List<Unit<Double>> list)
	{
		if (list.isEmpty() || (list.size() == 1))
		{
			return true;
		}

		Iterator<Unit<Double>> it = list.iterator();
		Unit<Double> current, previous = it.next();
		while (it.hasNext())
		{
			current = it.next();
			if (previous.compareTo(current) > 0)
			{
				return false;
			}
			previous = current;
		}
		return true;
	}

	/**
	 * Check if two lists are completely different : list1 does not contain any
	 * element of list2 and list2 does not contain any element of list1
	 * @param <E> the type of elements in lists
	 * @param list1 the first list
	 * @param list2 the second list
	 * @return true if lists are different
	 */
	private static <E> boolean noIntersection(List<E> list1, List<E> list2)
	{
		for (E elt : list2)
		{
			if (list1.contains(elt))
			{
				return false;
			}
		}
		for (E elt : list1)
		{
			if (list2.contains(elt))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Internal condition used to wait and notify on either
	 * <ul>
	 * <li>{@link #frame} to be built on SWING thread</li>
	 * <li>{@link #panel} to be built on JavaFX thread</li>
	 * <li>Or any JavaFX operation that need to be performed on the JavaFX
	 * Thread</li>
	 * </ul>
	 * @author davidroussel
	 */
	private static class Condition
	{
		/**
		 * condition value
		 */
		private boolean value;

		/**
		 * Valued constructor
		 * @param value the initial value of the condition
		 */
		public Condition(boolean value)
		{
			this.value = value;
		}

		/**
		 * Value setter
		 * @param value the value to set
		 */
		synchronized public void setValue(boolean value)
		{
			this.value = value;
		}

		/**
		 * Value getter
		 * @return the current value
		 */
		public boolean getValue()
		{
			return value;
		}
	}
}