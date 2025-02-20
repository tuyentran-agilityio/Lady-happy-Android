package com.egoriku.ladyhappy.tools

import android.content.Context
import com.egoriku.ladyhappy.BuildConfig
import com.egoriku.ladyhappy.R
import com.egoriku.ladyhappy.core.IStringResource

class StringResource(private val context: Context) : IStringResource {

    override val currentVersion: String
        get() = context.getString(R.string.settings_app_version, BuildConfig.VERSION_NAME)

    override val emptyDescription: String
        get() = context.getString(R.string.edit_empty_description)

    override val notEdited: String
        get() = context.getString(R.string.edit_not_edited)
}