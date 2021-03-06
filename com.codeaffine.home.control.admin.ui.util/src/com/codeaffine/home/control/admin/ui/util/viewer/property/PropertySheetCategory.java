/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codeaffine.home.control.admin.ui.util.viewer.property;

import java.util.ArrayList;
import java.util.List;

/**
 * A category in a PropertySheet used to group <code>IPropertySheetEntry</code>
 * entries so they are displayed together.
 */
@SuppressWarnings("rawtypes")
/*package*/class PropertySheetCategory {
    private final String categoryName;

    private List entries = new ArrayList();

    private boolean shouldAutoExpand = true;

    /**
     * Create a PropertySheet category with name.
     * @param name
     */
    public PropertySheetCategory(String name) {
        categoryName = name;
    }

    /**
     * Add an <code>IPropertySheetEntry</code> to the list
     * of entries in this category.
     * @param entry
     */
    @SuppressWarnings("unchecked")
    public void addEntry(IPropertySheetEntry entry) {
        entries.add(entry);
    }

    /**
     * Return the category name.
     * @return the category name
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Returns <code>true</code> if this category should be automatically
     * expanded. The default value is <code>true</code>.
     *
     * @return <code>true</code> if this category should be automatically
     * expanded, <code>false</code> otherwise
     */
    public boolean getAutoExpand() {
        return shouldAutoExpand;
    }

    /**
     * Sets if this category should be automatically
     * expanded.
     * @param autoExpand
     */
    public void setAutoExpand(boolean autoExpand) {
        shouldAutoExpand = autoExpand;
    }

    /**
     * Returns the entries in this category.
     *
     * @return the entries in this category
     */
    @SuppressWarnings("unchecked")
    public IPropertySheetEntry[] getChildEntries() {
        return (IPropertySheetEntry[]) entries
                .toArray(new IPropertySheetEntry[entries.size()]);
    }

    /**
     * Removes all of the entries in this category.
     * Doing so allows us to reuse this category entry.
     */
    public void removeAllEntries() {
        entries = new ArrayList();
    }
}
