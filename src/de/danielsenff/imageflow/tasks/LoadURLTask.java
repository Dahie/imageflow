package de.danielsenff.imageflow.tasks;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jdesktop.application.Task;

import de.danielsenff.imageflow.ImageFlow;
import de.danielsenff.imageflow.ImageFlowView;
import de.danielsenff.imageflow.controller.GraphController;



/**
 * Abstract Task for loading files.
 * @author tomka
 *
 * @param <T>
 * @param <V>
 */
public abstract class LoadURLTask<T, V> extends Task<T, V> {

	protected boolean modified = false;
	private static final Logger logger = Logger.getLogger(ImageFlowView.class.getName());

	/**
	 * Url that is loaded.
	 */
	protected final URL url;

	protected ImageFlowView view;

	/**
	 * Construct the LoadFileTask object.  The constructor
	 * will run on the EDT, so we capture a reference to the
	 * File to be loaded here.  To keep things simple, the
	 * resources for this Task are specified to be in the same
	 * ResourceMap as the DocumentEditorView class's resources.
	 * They're defined in resources/DocumentEditorView.properties.
	 */
	public LoadURLTask(final URL url) {
		super(ImageFlow.getApplication());
		this.url = url;
		this.view = (ImageFlowView) ImageFlow.getApplication().getMainView();
	}

	/**
	 * Called on the EDT if doInBackground completes without
	 * error and this Task isn't cancelled.  We update the
	 * GUI as well as the file and modified properties here.
	 * @param fileContents
	 */
	@Override
	protected void succeeded(final T fileContents) {
		view.setFile(new File(this.url.getFile()));
		view.setGraphController((GraphController)fileContents);
		ImageFlowView.getProgressBar().setIndeterminate(false);
		ImageFlowView.getProgressBar().setVisible(false);
		//textArea.setText(fileContents);
		view.setModified(false);
	}

	/**
	 * Url this task is handling.
	 * @return
	 */
	public URL getURL() {
		return url;
	}

	@Override
	protected void cancelled() {
		ImageFlowView.getProgressBar().setIndeterminate(false);
		ImageFlowView.getProgressBar().setVisible(false);
		super.cancelled();
	}

	/* Called on the EDT if doInBackground fails because
	 * an uncaught exception is thrown.  We show an error
	 * dialog here.  The dialog is configured with resources
	 * loaded from this Tasks's ResourceMap.
	 */
	@Override
	protected void failed(final Throwable e) {
		logger.log(Level.WARNING, "couldn't load " + getURL(), e);
		final String msg = getResourceMap().getString("loadFailedMessage", getURL());
		final String title = getResourceMap().getString("loadFailedTitle");
		final int type = JOptionPane.ERROR_MESSAGE;
		ImageFlowView.getProgressBar().setIndeterminate(false);
		ImageFlowView.getProgressBar().setVisible(false);
		JOptionPane.showMessageDialog(ImageFlow.getApplication().getMainFrame(), msg, title, type);
	}

}
