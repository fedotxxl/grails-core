/*
 * Copyright 2011 SpringSource
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.grails.compiler;

import io.belov.grails.RecursiveDirectoryWatcher;
import io.belov.grails.SavedDirectoryWatcher;
import io.belov.grails.filters.CompositeFilter;
import io.belov.grails.filters.EndsWithFilter;

import java.io.File;
import java.util.List;

/**
 * Utility class to watch directories for changes.
 *
 * @author Graeme Rocher
 * @since 2.0
 */
public class DirectoryWatcher extends Thread {

    public static final String SVN_DIR_NAME = ".svn";
    private SavedDirectoryWatcher watcher;

    public DirectoryWatcher() {
        setDaemon(true);
        watcher = new SavedDirectoryWatcher(new RecursiveDirectoryWatcher());
    }

    /**
     * Sets whether to stop the directory watcher
     *
     * @param active False if you want to stop watching
     */
    public void setActive(boolean active) {
        watcher.setActive(active);
    }

    /**
     * Sets the amount of time to sleep between checks
     *
     * @param sleepTime The sleep time
     */
    public void setSleepTime(long sleepTime) {
        //do nothing
    }

    /**
     * Adds a file listener that can react to change events
     *
     * @param listener The file listener
     */
    public void addListener(final FileChangeListener listener) {
        watcher.addListener(new io.belov.grails.FileChangeListener() {
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
        });
    }

    /**
     * Adds a file to the watch list
     *
     * @param fileToWatch The file to watch
     */
    public void addWatchFile(File fileToWatch) {
        watcher.addWatchFile(fileToWatch.toPath());
    }

    /**
     * Adds a directory to watch for the given file and extensions.
     *
     * @param dir The directory
     * @param fileExtensions The extensions
     */
    public void addWatchDirectory(File dir, List<String> fileExtensions) {
        CompositeFilter compositeFilter = new CompositeFilter();

        for (String extension : fileExtensions) {
            compositeFilter.add(new EndsWithFilter(extension));
        }

        watcher.addWatchDirectory(dir.toPath(), compositeFilter);
    }

    /**
     * Adds a directory to watch for the given file and extensions.
     *
     * @param dir The directory
     * @param extension The extension
     */
    public void addWatchDirectory(File dir, String extension) {
        watcher.addWatchDirectory(dir.toPath(), new EndsWithFilter(extension));
    }

    /**
     * Interface for FileChangeListeners
     */
    public static interface FileChangeListener {
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

    @Override
    public void run() {
        watcher.start();
    }

}
