package nl.tudelft.dnainator.javafx.drawables.strains;

import nl.tudelft.dnainator.core.SequenceNode;
import nl.tudelft.dnainator.core.impl.Cluster;
import nl.tudelft.dnainator.javafx.ColorServer;
import nl.tudelft.dnainator.javafx.drawables.Drawable;
import nl.tudelft.dnainator.javafx.views.AbstractView;
import nl.tudelft.dnainator.javafx.widgets.PropertyType;
import nl.tudelft.dnainator.javafx.widgets.Propertyable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.MapChangeListener;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 * This enum represents all properties a Cluster can have.
 */
enum ClusterPropertyTypes implements PropertyType {
	ID("NodeID"),
	SEQUENCE("Sequence"),
	STARTREF("Startref"),
	ENDREF("Endref"),
	SOURCES("Sources"),
	STARTRANK("Start rank");

	private String description;
	private ClusterPropertyTypes(String description) {
		this.description = description;
	}

	@Override
	public String description() {
		return description;
	}
}

/**
 * The {@link ClusterDrawable} class represents the mid level object in the viewable model.
 */
public class ClusterDrawable extends Group implements Drawable, Propertyable {
	private static final String TITLE = "Cluster";

	protected static final int SINGLE = 1;
	protected static final int SMALL = 3;
	protected static final int MEDIUM = 10;
	protected static final double SINGLE_RADIUS = 2;
	protected static final double SMALL_RADIUS = 4;
	protected static final double MEDIUM_RADIUS = 5;
	protected static final double LARGE_RADIUS = 6;
	protected static final int PIETHRESHOLD = 20;
	private Cluster cluster;
	private Set<String> sources;
	private Text label;
	private Pie pie;
	private Map<PropertyType, String> properties;

	/**
	 * Construct a new mid level {@link ClusterDrawable} using the default graph.
	 * @param colorServer the {@link ColorServer} to bind to.
	 * @param cluster	the {@link Cluster} containing a list of {@link SequenceNode}s.
	 */
	public ClusterDrawable(ColorServer colorServer, Cluster cluster) {
		this.cluster = cluster;
		this.properties = new HashMap<>();
		this.sources = cluster.getNodes().stream()
				.flatMap(e -> e.getSources().stream())
				.collect(Collectors.toSet());
		label = new Text(Integer.toString(cluster.getNodes().size()));
		initProperties();
		setOnMouseClicked(e -> AbstractView.setLastClicked(this));
		draw(colorServer);
	}

	private void initProperties() {
		if (cluster.getNodes().size() > 1) {
			properties.put(ClusterPropertyTypes.ID, cluster.getNodes().toString());
			properties.put(ClusterPropertyTypes.STARTRANK,
					Integer.toString(cluster.getStartRank()));
		} else if (cluster.getNodes().size() == 1) {
			SequenceNode sn = cluster.getNodes().iterator().next();
			properties.put(ClusterPropertyTypes.ID, sn.getId());
			properties.put(ClusterPropertyTypes.SEQUENCE, sn.getSequence());
			properties.put(ClusterPropertyTypes.STARTREF, Integer.toString(sn.getStartRef()));
			properties.put(ClusterPropertyTypes.ENDREF, Integer.toString(sn.getEndRef()));
			properties.put(ClusterPropertyTypes.SOURCES, sn.getSources().toString());
		}
	}

	private void draw(ColorServer colorServer) {
		double radius = getRadius();

		if (sources.size() > PIETHRESHOLD) {
			getChildren().add(new Circle(radius));
		} else {
			colorServer.addListener(this::onColorServerChanged);

			List<String> collect = sources.stream()
					.map(e -> colorServer.getColor(e))
					.filter(e -> e != null)
					.collect(Collectors.toList());
			pie = new Pie(radius, collect);
			getChildren().add(pie);
		}
		getChildren().add(label);
	}

	/**
	 * Return the radius of this cluster drawable.
	 * @return	the radius
	 */
	protected double getRadius() {
		int nChildren = cluster.getNodes().size();

		if (nChildren == SINGLE) {
			return SINGLE_RADIUS;
		} else if (nChildren <= SMALL) {
			return SMALL_RADIUS;
		} else if (nChildren <= MEDIUM) {
			return MEDIUM_RADIUS;
		} else {
			return LARGE_RADIUS;
		}
	}

	private void onColorServerChanged(
			MapChangeListener.Change<? extends String, ? extends String> change) {
		if (!sources.contains(change.getKey())) {
			return;
		} else if (change.wasAdded()) {
			addStyle(change.getValueAdded());
		} else if (change.wasRemoved()) {
			removeStyle(change.getValueRemoved());
		}
	}

	@Override
	public void addStyle(String style) {
		pie.getStyles().add(style);
	}

	@Override
	public void removeStyle(String style) {
		pie.getStyles().remove(style);
	}

	/**
	 * @return the {@link Cluster}s in this drawable.
	 */
	public Cluster getCluster() {
		return cluster;
	}

	@Override
	public Map<PropertyType, String> getPropertyMap() {
		return properties;
	}

	@Override
	public PropertyType getTitle() {
		return new PropertyType() {
			@Override
			public String description() {
				return TITLE;
			}
		};
	}
}
