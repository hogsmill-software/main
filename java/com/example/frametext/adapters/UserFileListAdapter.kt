package com.example.frametext.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.frametext.MainActivity
import com.example.frametext.R
import com.example.frametext.fragments.UserFilesFragment
import com.example.frametext.viewModels.TextInputViewModel
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class UserFileListAdapter internal constructor(
    var context: Context,
    private var userFileList: ArrayList<String>?,
    private val textInputViewModel: TextInputViewModel,
    private var userFile: UserFilesFragment
) :
    RecyclerView.Adapter<UserFileListAdapter.ViewHolder>() {
    // This userFileFragment needed so text input tab is set after loading

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var userFileNameView: TextView =
            view.findViewById<View>(R.id.fileName) as TextView

        var loadButton: AppCompatButton =
            view.findViewById<View>(R.id.loadButton) as AppCompatButton

        val deleteButton: AppCompatButton =
            view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.list_user_files_items, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.userFileNameView.text = userFileList?.get(position)

        holder.loadButton.setOnClickListener { loadFile(position) }

        holder.deleteButton.setOnClickListener { deleteFile(position) }
    }

    override fun getItemCount(): Int {
        userFileList?.let{
            return it.size
        }
        return  0
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun findRelativeAdapterPositionIn(
        adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
        viewHolder: RecyclerView.ViewHolder,
        localPosition: Int
    ): Int {
        return super.findRelativeAdapterPositionIn(adapter, viewHolder, localPosition)
    }

    private fun loadFile(i: Int) {
        MainActivity.getUserFileFolder(true, context).let { uffIt ->
            userFileList?.let { uflIt ->
                val usrFile = File(uffIt + uflIt[i])
                if (usrFile.exists()) {
                    val fileContentBuilder = StringBuilder()
                    try {
                        BufferedReader(FileReader(usrFile)).use { br ->
                            br.forEachLine { currentLine ->
                                fileContentBuilder.append(currentLine).append("\n")
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val fileContent = fileContentBuilder.toString()
                    textInputViewModel.selectItem(fileContent)
                    userFile.setToTextInputFragment()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteFile(i: Int) {
        MainActivity.getUserFileFolder(true, context)?.let { uff ->
            userFileList?.let {
                val usrFile = File(uff + it[i])
                if (usrFile.exists()) {
                    if (usrFile.delete()) {
                        it.removeAt(i)
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }
}
