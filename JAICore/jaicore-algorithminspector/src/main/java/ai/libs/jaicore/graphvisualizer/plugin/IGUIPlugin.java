package ai.libs.jaicore.graphvisualizer.plugin;

import ai.libs.jaicore.basic.algorithm.IAlgorithm;
import ai.libs.jaicore.basic.algorithm.events.AlgorithmEvent;
import ai.libs.jaicore.basic.algorithm.events.serializable.PropertyProcessedAlgorithmEvent;
import ai.libs.jaicore.graphvisualizer.events.gui.GUIEvent;
import ai.libs.jaicore.graphvisualizer.events.gui.GUIEventSource;
import ai.libs.jaicore.graphvisualizer.events.recorder.property.AlgorithmEventPropertyComputer;
import ai.libs.jaicore.graphvisualizer.events.recorder.property.PropertyProcessedAlgorithmEventSource;
import ai.libs.jaicore.graphvisualizer.window.AlgorithmVisualizationWindow;

/**
 * {@link IGUIPlugin}s can be used to display information about an {@link IAlgorithm} or its behavior in an {@link AlgorithmVisualizationWindow}. Such a plugin consists of an {@link IGUIPluginController} responsible for handling
 * {@link GUIEvent}s provided by the underlying window and {@link PropertyProcessedAlgorithmEvent}s which are constructed from the {@link AlgorithmEvent}s provided by the underlying {@link IAlgorithm} based on the information computed by
 * {@link AlgorithmEventPropertyComputer}s. Furthermore, an {@link IGUIPlugin} has an {@link IGUIPluginModel} which serves as the model for the actual view, i.e. the {@link IGUIPluginView}, which is the last part of such a plugin. The
 * {@link IGUIPluginView} is responsible for creating the actually displayable content.
 * 
 * @author atornede
 *
 */
public interface IGUIPlugin {

	/**
	 * Returns the underlying {@link IGUIPluginController}.
	 * 
	 * @return The underlying {@link IGUIPluginController}.
	 */
	public IGUIPluginController getController();

	/**
	 * Returns the underlying {@link IGUIPluginModel}.
	 * 
	 * @return The underlying {@link IGUIPluginModel}.
	 */
	public IGUIPluginModel getModel();

	/**
	 * Returns the underlying {@link IGUIPluginView}.
	 * 
	 * @return The underlying {@link IGUIPluginView}.
	 */
	public IGUIPluginView getView();

	/**
	 * Sets the {@link PropertyProcessedAlgorithmEventSource} yielding {@link PropertyProcessedAlgorithmEvent}s which are handled by the {@link IGUIPluginController}.
	 * 
	 * @param propertyProcessedAlgorithmEventSource The {@link PropertyProcessedAlgorithmEventSource} yielding {@link PropertyProcessedAlgorithmEvent}s which are handled by the {@link IGUIPluginController}.
	 */
	public void setAlgorithmEventSource(PropertyProcessedAlgorithmEventSource propertyProcessedAlgorithmEventSource);

	/**
	 * Sets the {@link GUIEventSource} yielding {@link GUIEvent}s which are handled by the {@link IGUIPluginController}.
	 * 
	 * @param guiEventSource The {@link GUIEventSource} yielding {@link GUIEvent}s which are handled by the {@link IGUIPluginController}.
	 */
	public void setGUIEventSource(GUIEventSource guiEventSource);

}
