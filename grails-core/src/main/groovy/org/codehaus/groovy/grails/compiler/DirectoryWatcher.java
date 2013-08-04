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

import io.belov.grails.FileUtils;
import io.belov.grails.RecursiveDirectoryWatcher;
import io.belov.grails.SavedDirectoryWatcher;
import io.belov.grails.filters.CompositeFilter;
import io.belov.grails.filters.EndsWithFilter;
import io.belov.grails.win.WindowsBaseDirectoryWatcher;
import org.apache.commons.lang.SystemUtils;

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
    private WindowsBaseDirectoryWatcher windowsBaseDirectoryWatcher;
    private File base;

    public DirectoryWatcher() {
        setDaemon(true);

        watcher = new SavedDirectoryWatcher(new RecursiveDirectoryWatcher());
        windowsBaseDirectoryWatcher = new WindowsBaseDirectoryWatcher(getBase());
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
        io.belov.grails.FileChangeListener l = new io.belov.grails.FileChangeListener() {
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
        windowsBaseDirectoryWatcher.addListener(l);
    }

    /**
     * Adds a file to the watch list
     *
     * @param fileToWatch The file to watch
     */
    public void addWatchFile(File fileToWatch) {
        getDirectoryWatcherForFile(fileToWatch).addWatchFile(fileToWatch.toPath());
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

        getDirectoryWatcherForFile(dir).addWatchDirectory(dir.toPath(), compositeFilter);
    }

    /**
     * Adds a directory to watch for the given file and extensions.
     *
     * @param dir The directory
     * @param extension The extension
     */
    public void addWatchDirectory(File dir, String extension) {
        getDirectoryWatcherForFile(dir).addWatchDirectory(dir.toPath(), new EndsWithFilter(extension));
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
        windowsBaseDirectoryWatcher.start();
        watcher.start();
    }

    private File getBase() {
        if (this.base == null) {
            this.base = (File) FileUtils.getNormalizedFile(new File("."));
        }

        return this.base;
    }

    private io.belov.grails.DirectoryWatcher getDirectoryWatcherForFile(File file) {
        return (isBaseFile(file)) ? windowsBaseDirectoryWatcher : watcher;
    }

    private boolean isBaseFile(File file) {
        return (SystemUtils.IS_OS_WINDOWS && FileUtils.isParentOf(getBase(), file));
    }

}
