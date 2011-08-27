package com.ushahidi.j2me.forms;

import com.ushahidi.j2me.App;

import ushahidi.core.I18N;

/**
 * Details Form
 * @author dalezak
 */
public class Details extends Base {

    public Details(final App app, com.ushahidi.j2me.models.Report report) {
        super(I18N.s("details"));
    }
}
