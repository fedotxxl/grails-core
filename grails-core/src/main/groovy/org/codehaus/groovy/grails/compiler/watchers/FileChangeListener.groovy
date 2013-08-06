package org.codehaus.groovy.grails.compiler.watchers

/**
 * Interface for FileChangeListeners
 */
public interface FileChangeListener {
    /**
     * Fired when a file changes
     *
     * @param file The file that changed
     */
    void onChange(File file);

    /**
     * Fired when a new file is created
     *
     * @param file The file that was created
     */
    void onNew(File file);
}
