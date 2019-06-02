/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package code.name.monkey.retromusic.dialogs

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.app.ShareCompat
import androidx.fragment.app.DialogFragment
import code.name.monkey.appthemehelper.ThemeStore
import code.name.monkey.retromusic.App
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.activities.MainActivity
import code.name.monkey.retromusic.activities.bugreport.BugReportActivity
import code.name.monkey.retromusic.util.NavigationUtil
import code.name.monkey.retromusic.util.PreferenceUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView

class OptionsSheetDialogFragment : DialogFragment(), View.OnClickListener {

    override fun onClick(view: View) {
        val mainActivity = activity as MainActivity? ?: return
        when (view.id) {
            R.id.actionFolders -> mainActivity.selectedFragment(R.id.action_folder)
            R.id.actionLibrary -> mainActivity.selectedFragment(PreferenceUtil.getInstance().lastPage)
            R.id.actionSettings -> NavigationUtil.goToSettings(mainActivity)
            R.id.actionSleepTimer -> if (fragmentManager != null) {
                SleepTimerDialog().show(fragmentManager!!, TAG)
            }
            R.id.actionRate -> NavigationUtil.goToPlayStore(mainActivity)
            R.id.actionShare -> shareApp()
            R.id.actionBugReport -> prepareBugReport()
            R.id.actionEqualizer -> NavigationUtil.openEqualizer(mainActivity)

        }
        dismiss()
    }

    private fun prepareBugReport() {
        startActivity(Intent(activity, BugReportActivity::class.java))
    }

    private fun shareApp() {
        val shareIntent = ShareCompat.IntentBuilder.from(activity)
                .setType("songText/plain")
                .setText(String.format(getString(R.string.app_share), activity!!.packageName))
                .intent
        if (shareIntent.resolveActivity(activity!!.packageManager) != null) {
            startActivity(
                    Intent.createChooser(shareIntent, resources.getText(R.string.action_share)))
        }
    }

    private lateinit var actionSettings: View
    private lateinit var actionSleepTimer: View
    private lateinit var actionLibrary: View
    private lateinit var actionEqualizer: View
    private lateinit var actionFolders: View
    private lateinit var actionRate: View
    private lateinit var actionShare: View
    private lateinit var actionBugReport: View
    private lateinit var buyProContainer: CardView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layout = LayoutInflater.from(context).inflate(R.layout.fragment_main_options, null)
        actionSettings = layout.findViewById(R.id.actionSettings)
        actionSleepTimer = layout.findViewById(R.id.actionSleepTimer)
        actionLibrary = layout.findViewById(R.id.actionLibrary)
        actionEqualizer = layout.findViewById(R.id.actionEqualizer)
        actionFolders = layout.findViewById(R.id.actionFolders)
        actionRate = layout.findViewById(R.id.actionRate)
        actionShare = layout.findViewById(R.id.actionShare)
        actionBugReport = layout.findViewById(R.id.actionBugReport)
        buyProContainer = layout.findViewById(R.id.buyProContainer)

        actionSettings.setOnClickListener(this)
        actionSleepTimer.setOnClickListener(this)
        actionLibrary.setOnClickListener(this)
        actionEqualizer.setOnClickListener(this)
        actionFolders.setOnClickListener(this)
        actionRate.setOnClickListener(this)
        actionShare.setOnClickListener(this)
        actionBugReport.setOnClickListener(this)

        buyProContainer.apply {
            setCardBackgroundColor(ThemeStore.accentColor(context!!))
            visibility = if (!App.isProVersion) View.VISIBLE else View.GONE
            setOnClickListener {
                NavigationUtil.goToProVersion(context)
            }
        }

        return MaterialDialog(activity!!, BottomSheet())
                .show {
                    customView(view = layout, scrollable = true)
                }
    }

    companion object {

        private const val TAG: String = "MainOptionsBottomSheetD"

        fun newInstance(selected_id: Int): OptionsSheetDialogFragment {
            val bundle = Bundle()
            bundle.putInt("selected_id", selected_id)
            val fragment = OptionsSheetDialogFragment()
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(): OptionsSheetDialogFragment {
            return OptionsSheetDialogFragment()
        }
    }
}
