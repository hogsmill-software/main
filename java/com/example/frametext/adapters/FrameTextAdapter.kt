package com.example.frametext.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.frametext.fragments.FrameTextImageFragment
import com.example.frametext.fragments.SettingsFragment
import com.example.frametext.fragments.TextInputFragment

class FrameTextAdapter(c: Context, fa: FragmentActivity, totalTabs: Int) :
    FragmentStateAdapter(fa) {
    private var context: Context
    private var totalTabs: Int
    private var settingsFragment: SettingsFragment? = null

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TextInputFragment()
            1 -> SettingsFragment()
            2 -> FrameTextImageFragment()
            else -> FrameTextImageFragment()
        }
    }

    override fun getItemCount(): Int {
        return totalTabs
    }

    fun saveSelectedItem() {
        settingsFragment?.saveSelectedItem()
    }

    fun updateHyphenDropdown() {
        settingsFragment?.updateHyphenDropdown()
    }

    init {
        context = c
        this.totalTabs = totalTabs
    }
}