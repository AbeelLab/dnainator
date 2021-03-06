package nl.tudelft.dnainator.javafx.services;

import nl.tudelft.dnainator.graph.Graph;
import nl.tudelft.dnainator.graph.impl.Neo4jGraph;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.io.fs.FileUtils;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javafx.stage.Stage;
import static nl.tudelft.dnainator.javafx.services.LoadServiceTestUtils.registerListeners;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test class for the {@link GraphLoadService}.
 * <p>
 * See http://blog.buildpath.de/how-to-test-javafx-services/ for
 * an explanation on how to test JavaFX code.
 * </p>
 */
public class GraphLoadServiceTest extends ApplicationTest {
	private static final String DB_PATH = "target/neo4j-junit-graphload";
	private static final int DELAY = 20000;
	private GraphLoadService loadService;
	private File nodeFile;
	private File edgeFile;
	private File treeFile;
	private File drFile;
	private File gffFile;

	/**
	 * Setup the database and construct the graph.
	 */
	@BeforeClass
	public static void setUp() {
		try {
			FileUtils.deleteRecursively(new File(DB_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage arg0) throws Exception {
		// Prevent exceptions from showing during service testing.
		FxToolkit.cleanupStages();
	}

	/**
	 * Creates test node and edge files.
	 */
	@Before
	public void setup() {
		loadService = new GraphLoadService();
		try {
			nodeFile = new File(getClass().getResource("/strains/test.node.graph").toURI());
			edgeFile = new File(getClass().getResource("/strains/test.edge.graph").toURI());
			treeFile = new File(getClass().getResource("/strains/test.nwk").toURI());
			drFile = new File(getClass().getResource("/annotations/dr_test.txt").toURI());
			gffFile  = File.createTempFile("foo", ".bar");			
		} catch (URISyntaxException | IOException e) {
			fail(e.getMessage());
		}
		loadService.setNodeFile(nodeFile);
		loadService.setEdgeFile(edgeFile);
		loadService.setNewickFile(treeFile);
		loadService.setDRFile(drFile);
		loadService.setGffFile(gffFile);
	}

	/**
	 * Tests setNodeFile().
	 */
	@Test
	public void testSetNodeFile() {
		try {
			File foo = File.createTempFile("foo", ".bar");
			loadService.setNodeFile(foo);
			assertEquals(foo, loadService.getNodeFile());
			foo.delete();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Tests getNodeFile().
	 */
	@Test
	public void testGetNodeFile() {
		assertEquals(nodeFile, loadService.getNodeFile());
	}

	/**
	 * Tests nodeFileProperty().
	 */
	@Test
	public void testNodeFileProperty() {
		assertNotNull(loadService.nodeFileProperty());
	}

	/**
	 * Tests setNewickFile().
	 */
	@Test
	public void testSetNewickFile() {
		try {
			File foo = File.createTempFile("foo", ".bar");
			loadService.setNewickFile(foo);
			assertEquals(foo, loadService.getNewickFile());
			foo.delete();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Tests getNewickFile().
	 */
	@Test
	public void testGetNewickFile() {
		assertEquals(treeFile, loadService.getNewickFile());
	}

	/**
	 * Tests nodeFileProperty().
	 */
	@Test
	public void testNewickFileProperty() {
		assertNotNull(loadService.newickFileProperty());
	}

	/**
	 * Tests setEdgeFile().
	 */
	@Test
	public void testSetEdgeFile() {
		try {
			File foo = File.createTempFile("foo", ".bar");
			loadService.setEdgeFile(foo);
			assertEquals(foo, loadService.getEdgeFile());
			foo.delete();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Tests getEdgeFile().
	 */
	@Test
	public void testGetEdgeFile() {
		assertEquals(edgeFile, loadService.getEdgeFile());
	}

	/**
	 * Tests edgeFileProperty().
	 */
	@Test
	public void testEdgeFileProperty() {
		assertNotNull(loadService.edgeFileProperty());
	}

	/**
	 * Tests setDrFile().
	 */
	@Test
	public void testDrEdgeFile() {
		try {
			File foo = File.createTempFile("foo", ".bar");
			loadService.setDRFile(foo);
			assertEquals(foo, loadService.getDrFile());
			foo.delete();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Tests getDrFile().
	 */
	@Test
	public void testGetDrFile() {
		assertEquals(drFile, loadService.getDrFile());
	}

	/**
	 * Tests drFileProperty().
	 */
	@Test
	public void testDrFileProperty() {
		assertNotNull(loadService.drFileProperty());
	}
	
	/**
	 * Tests setEdgeFile().
	 */
	@Test
	public void testSetGffFile() {
		try {
			File foo = File.createTempFile("foo", ".bar");
			loadService.setNewickFile(foo);
			assertEquals(foo, loadService.getNewickFile());
			foo.delete();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Tests getEdgeFile().
	 */
	@Test
	public void testGetGffFile() {
		assertEquals(gffFile, loadService.getGffFile());
	}

	/**
	 * Tests edgeFileProperty().
	 */
	@Test
	public void testGffFileProperty() {
		assertNotNull(loadService.gffFileProperty());
	}

	/**
	 * Tests getEdgeFile().
	 */
	@Test
	public void testGetDb() {
		assertNotNull(loadService.getDatabase());
	}

	/**
	 * Tests edgeFileProperty().
	 */
	@Test
	public void testDbProperty() {
		assertNotNull(loadService.databaseProperty());
	}

	/**
	 * Performs the actual test, in that it initializes the CompletableFuture,
	 * attaches the listeners and does the assertions.
	 * @throws InterruptedException when the completablefuture was interrupted
	 * @throws ExecutionException when an error occurs in loading
	 * @throws TimeoutException  when the completablefuture does not complete
	 */
	public void doTest() throws InterruptedException, ExecutionException, TimeoutException {
		// Create a completableFuture to test the background task of the service. This
		// completableFuture blocks the test thread until its complete method was called.
		CompletableFuture<Graph> completableFuture = new CompletableFuture<>();

		// Act on the loadService's interesting states.
		registerListeners(loadService, completableFuture);

		loadService.setDatabase(DB_PATH);
		loadService.start();

		// This call blocks the test thread until the completableFuture's complete() method is
		// called. Calling get() retrieves the result (e.g., the Graph in this case). Set a
		// timeout in case the result does not appear.
		Graph graph = completableFuture.get(DELAY, TimeUnit.MILLISECONDS);

		assertNotNull(graph);
		((Neo4jGraph) graph).shutdown();
	}

	/**
	 * Tests loading a file properly.
	 * @throws InterruptedException when the completablefuture was interrupted
	 * @throws ExecutionException when an error occurs in loading
	 * @throws TimeoutException  when the completablefuture does not complete
	 */
	@Test
	public void testProperFile() throws InterruptedException, ExecutionException, TimeoutException {
		doTest();
	}

	/**
	 * Tests loading a file that is not a node file.
	 * @throws InterruptedException when the completablefuture was interrupted
	 * @throws ExecutionException when an error occurs in loading
	 * @throws TimeoutException  when the completablefuture does not complete
	 */
	@Test(expected = ExecutionException.class)
	public void notANodeFile() throws InterruptedException, ExecutionException, TimeoutException {
		try {
			File wrongFile = new File(getClass().getResource("/strains/wrong.node.graph").toURI());
			loadService.setNodeFile(wrongFile);
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}

		doTest();
	}

	/**
	 * Tests loading a file that is not an edge file.
	 * @throws InterruptedException when the completablefuture was interrupted
	 * @throws ExecutionException when an error occurs in loading
	 * @throws TimeoutException  when the completablefuture does not complete
	 */
	@Test(expected = ExecutionException.class)
	public void notAnEdgeFile() throws InterruptedException, ExecutionException, TimeoutException {
		try {
			File wrongFile = new File(getClass().getResource("/strains/wrong.edge.graph").toURI());
			loadService.setEdgeFile(wrongFile);
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}

		doTest();
	}
}
