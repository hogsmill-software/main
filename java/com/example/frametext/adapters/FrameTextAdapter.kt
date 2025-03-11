package com.example.frametext.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.frametext.fragments.FrameTextImageFragment
import com.example.frametext.fragments.SettingsFragment
import com.example.frametext.fragments.TextInputFragment

class FrameTextAdapter(fa: FragmentActivity, private var totalTabs: Int) :
    FragmentStateAdapter(fa) {
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
}