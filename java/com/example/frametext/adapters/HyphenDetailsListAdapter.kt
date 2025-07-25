package com.example.frametext.adapters

//https://www.techyourchance.com/asynctask-deprecated/
import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.frametext.MainActivity.Companion.getHyphenFileFolder
import com.example.frametext.R
import com.example.frametext.globalObjects.HyphenDetails
import com.example.frametext.helpers.Utilities
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.net.URLConnection

class HyphenDetailsListAdapter internal constructor(
    private var context: Context,
    private var hyphenDetails: Array<HyphenDetails>,
    private var hyphenFilesList: ArrayList<String>
) : RecyclerView.Adapter<HyphenDetailsListAdapter.ViewHolder>() {
    private var success: Boolean = false

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var textView: TextView =
            view.findViewById<View>(R.id.textView) as TextView
        var button: AppCompatButton =
            view.findViewById<View>(R.id.button) as AppCompatButton
    }

    fun doInBackground(idx: Int, hyphenFileNameView: TextView) {
        try {
            var url = URL(hyphenDetails[idx].downloadLink)
            var conn: URLConnection = url.openConnection()
            var contentLength: Int = conn.contentLength
            if (contentLength == -1)
                return
            var downloaded = false
            var stream = DataInputStream(url.openStream())
            var buffer = ByteArray(contentLength)
            stream.readFully(buffer)
            stream.close()
            val content = String(buffer)
            if (isHTML(content)) {
                val hRef = "href=\""
                var idx = content.indexOf(hRef)
                if (idx != 1) {
                    idx += hRef.length
                    val idx2 = content.indexOf("\"", idx)
                    val newLink = content.substring(idx, idx2)
                    url = URL(newLink)
                    conn = url.openConnection()
                    contentLength = conn.contentLength
                    //  conn.wait();
                    stream = DataInputStream(url.openStream())
                    buffer = ByteArray(contentLength)
                    stream.readFully(buffer)
                    stream.close()
                    downloaded = true
                }
            } else {
                downloaded = matches(content, "%")
            }
            if (downloaded) {
                val hyphenFileFolder = getHyphenFileFolder(
                    hyphenFileNameView.context
                )
                success = hyphenFileFolder?.let { hyphenFileFolderIt ->
                    val hyphenFileName = hyphenFileFolderIt + hyphenDetails[idx].fileName
                    FileOutputStream(hyphenFileName).use { fos -> fos.write(buffer) }
                    true
                } == true
            }
        } catch (e: IOException) {
            e.printStackTrace()
            success = false
        }
    }

    fun updateFrontEnd(idx: Int,  downloadDeleteButton: AppCompatButton, hyphenFileNameView: TextView) {
        if (success) {
            hyphenDetails[idx].downLoaded = true
            setButtonToDeleteStatus(downloadDeleteButton, hyphenFileNameView)
            hyphenFilesList.add(hyphenDetails[idx].hyphenatePatternLanguage)
        } else {
            setButtonToDownloadStatus(downloadDeleteButton, hyphenFileNameView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.list_hyphen_items, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
       // holder.bind(hyphenDetails[position], hyphenFilesFragment)
        holder.textView.text = hyphenDetails[position].hyphenatePatternLanguage

        if (hyphenDetails[position].downLoaded) {
            setButtonToDeleteStatus(holder.button, holder.textView)
        } else {
            setButtonToDownloadStatus(holder.button, holder.textView)
        }

        holder.button.setOnClickListener {
            onDownloadDeleteButton(
                position,
                holder.button,
                holder.textView
            )
        }

    }

    override fun getItemCount(): Int {
        return  hyphenDetails.size
    }

    override fun getItemId(position: Int): Long {
        return hyphenDetails[position].id.toLong()
    }

    override fun findRelativeAdapterPositionIn(
        adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
        viewHolder: RecyclerView.ViewHolder,
        localPosition: Int
    ): Int {
        return super.findRelativeAdapterPositionIn(adapter, viewHolder, localPosition)
    }

    private fun onDownloadDeleteButton(
        i: Int,
        downloadDeleteButton: AppCompatButton,
        hyphenFileNameView: TextView
    ) {
        if (hyphenDetails[i].downLoaded) {
            val hyphenFileFolder = getHyphenFileFolder(hyphenFileNameView.context)
            hyphenFileFolder?.let {
                val hyphenFileName = it + hyphenDetails[i].fileName
                val hyphenFile = File(hyphenFileName)
                if (hyphenFile.delete()) {
                    hyphenDetails[i].downLoaded = false
                    hyphenFilesList.remove(hyphenDetails[i].hyphenatePatternLanguage)
                }
                setButtonToDownloadStatus(downloadDeleteButton, hyphenFileNameView)
            }
        } else {
            val frameTextExecutor = Executors.newSingleThreadExecutor()
            val frameTextHandler = Handler(Looper.getMainLooper())

            frameTextExecutor.execute {
                doInBackground(i, hyphenFileNameView)
            }

            frameTextHandler.post {
                updateFrontEnd(i,  downloadDeleteButton, hyphenFileNameView)
            }

            // so change to button immediately. If it fails, reverses back.
            setButtonToDeleteStatus(downloadDeleteButton, hyphenFileNameView)
        }
    }

    fun setButtonToDownloadStatus(
        downloadDeleteButton: AppCompatButton,
        hyphenFileNameView: TextView
    ) {
        downloadDeleteButton.setBackgroundColor(
            ContextCompat.getColor(context, Utilities.getPinkMagentaColorId(context))
        )
        downloadDeleteButton.text = context.resources.getString(R.string.download)
        hyphenFileNameView.setTextColor(ContextCompat.getColor(context, R.color.fog))
        hyphenFileNameView.setTypeface(null, Typeface.NORMAL)
    }

    fun setButtonToDeleteStatus(
        downloadDeleteButton: AppCompatButton,
        hyphenFileNameView: TextView
    ) {
        downloadDeleteButton.setBackgroundColor(ContextCompat.getColor(context, R.color.navyBlue))
        downloadDeleteButton.text = context.resources.getString(R.string.remove)
        hyphenFileNameView.setTextColor(ContextCompat.getColor(context, Utilities.getTextColorId(context)))
        hyphenFileNameView.setTypeface(null, Typeface.BOLD)
    }

    companion object {
        fun isHTML(str: String): Boolean {
            return matches(str, "<!DOCTYPE HTML") || matches(str, "<!DOCTYPE HTML")
        }

        // Checks if string str1 starts with string str2
        fun matches(str1: String, str2: String): Boolean {
            return str1.regionMatches(0, str2, 0, str2.length)
        }
    }
}