/*
 * JavaDirectoryWatcherAdapter
 * Copyright (c) 2012 Cybervision. All rights reserved.
 */
package org.codehaus.groovy.grails.compiler.watchers
import io.belov.grails.watchers.DirectoryWatcher as JavaDirectoryWatcher
import io.belov.grails.FileChangeListener as JavaChangeListener
import io.belov.grails.filters.CompositeFilter
import io.belov.grails.filters.EndsWithFilter

class JavaDirectoryWatcherAdapter implements DirectoryWatcher {

    private JavaDirectoryWatcher watcher;

    public JavaDirectoryWatcherAdapter(JavaDirectoryWatcher watcher) {
        this.watcher = watcher
    }

    /**
     * Sets whether to stop the directory watcher
     *
     * @param active False if you want to stop watching
     */
    @Override
    public void setActive(boolean active) {
        if (!active) {
            watcher.stop();
        }
    }

    /**
     * Adds a file listener that can react to change events
     *
     * @param listener The file listener
     */
    @Override
    public void addListener(final FileChangeListener listener) {
        JavaChangeListener l = new JavaChangeListener() {
            @Override
            public void onChange(File file) {
                listener.onChange(file);
            }

            @Override
            public void onDelete(File file) {
                //do nothing
            }

            @Override
            public void onCreate(File file) {
                listener.onNew(file);
            }
        };

        watcher.addListener(l);
    }

    /**
     * Adds a file to the watch list
     *
     * @param fileToWatch The file to watch
     */
    @Override
    public void addWatchFile(File fileToWatch) {
        watcher.addWatchFile(fileToWatch);
    }

    /**
     * Adds a directory to watch for the given file and extensions.
     *
     * @param dir The directory
     * @param fileExtensions The extensions
     */
    @Override
    public void addWatchDirectory(File dir, List<String> fileExtensions) {
        CompositeFilter compositeFilter = new CompositeFilter();

        for (String extension : fileExtensions) {
            compositeFilter.add(new EndsWithFilter(extension));
        }

        watcher.addWatchDirectory(dir, compositeFilter);
    }

    /**
     * Adds a directory to watch for the given file and extensions.
     *
     * @param dir The directory
     * @param extension The extension
     */
    @Override
    public void addWatchDirectory(File dir, String extension) {
        watcher.addWatchDirectory(dir, new EndsWithFilter(extension));
    }

    @Override
    public void run() {
        watcher.startAsync();
    }

}