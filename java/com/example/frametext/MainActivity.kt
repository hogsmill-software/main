package com.example.frametext

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.frametext.adapters.FrameTextAdapter
import com.example.frametext.enums.MainShapeType
import com.example.frametext.enums.SymbolShapeType
import com.example.frametext.globalObjects.FrameTextParameters
import com.example.frametext.globalObjects.HyphenDetails
import com.example.frametext.helpers.Constants
import com.example.frametext.helpers.Utilities
import com.example.frametext.viewModels.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager2? = null
    private val hyphenDetailsList = ArrayList<HyphenDetails>()
    private val hyphenFilesList = ArrayList<String>()
    private var tabSetting: TabLayout.Tab? = null
    private var hplFileNameMap = HashMap<String, String>()
    private var fileNameHplMap = HashMap<String, String>()
    private var userFriendlyFontFamilyFontFamilyMap = HashMap<String, String>()
    private var fontFamilyUserFriendlyFontFamilyMap = HashMap<String, String>()
    private var userFriendlyFontFamilyToTypeFaceIdMap = HashMap<String, Int>()
    private var typeFaceIdToUserFriendlyFontFamilyMap = HashMap<Int, String>()
    private var ftp: FrameTextParameters = FrameTextParameters()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val hyphenDetailsArray = JSONArray(loadJSONFromAsset())
            var hyphenFileSet = false
            for (idx in 0 until hyphenDetailsArray.length()) {
                val hyphenDetail: JSONObject = hyphenDetailsArray.getJSONObject(idx)
                val hd = HyphenDetails(
                    hyphenDetail.getInt("id"),
                    hyphenDetail.getString("hpl"),
                    hyphenDetail.getBoolean("downLoaded"),
                    hyphenDetail.getString("fileName"),
                    hyphenDetail.getString("downloadLink")
                )
                val hyphenFileFolder = getHyphenFileFolder(applicationContext)
                if (hyphenFileFolder != null) {
                    val hyphenFile = File(hyphenFileFolder + hyphenDetail.getString("fileName"))
                    if (hyphenFile.exists()) {
                        hd.downLoaded = true
                        hyphenFilesList.add(hyphenDetail.getString("hpl"))
                        ftp.hyphenateText = true
                        if (!hyphenFileSet) {
                            ftp.hyphenFileName = hyphenDetail.getString("fileName")
                            hyphenFileSet = true
                        }
                    }
                }
                hyphenDetailsList.add(hd)
                hplFileNameMap[hyphenDetail.getString("hpl")] = hyphenDetail.getString("fileName")
                fileNameHplMap[hyphenDetail.getString("fileName")] = hyphenDetail.getString("hpl")
            }
            loadSavedSettings()
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Tabs set up below:
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.pager)
        tabLayout?.newTab()?.setText(resources.getString(R.string.text_input))
            ?.let { tabLayout?.addTab(it) }
        tabSetting = tabLayout?.newTab()?.setText(resources.getString(R.string.settings))
        tabSetting?.let { tabLayout?.addTab(it) }
        tabLayout?.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = FrameTextAdapter(this, this, 3)
        viewPager?.adapter = adapter

        viewPager?.registerOnPageChangeCallback(object : OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val tab = tabLayout?.getTabAt(position)
                tab?.select()
            }

        })

        tabLayout?.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab === tabSetting) {
                    adapter.updateHyphenDropdown()
                }
                viewPager?.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                if (tab === tabSetting) {
                    adapter.saveSelectedItem()
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Data shared between fragments below
        // Text input
        val textInputViewModel: TextInputViewModel =
            ViewModelProvider(this).get(TextInputViewModel::class.java)
        textInputViewModel.getSelectedItem().observe(this) { }
        // User files
        val userFilesViewModel: UserFilesViewModel =
            ViewModelProvider(this).get(UserFilesViewModel::class.java)
        val userFileList: java.util.ArrayList<String> = loadUserFile()
        userFilesViewModel.selectItems(userFileList)
        userFilesViewModel.selectedItem.observe(this) { }

        // FrameText parameters
        val frameTextParametersViewModel: FrameTextParametersViewModel =
            ViewModelProvider(this).get(
                FrameTextParametersViewModel::class.java
            )
        frameTextParametersViewModel.selectItem(ftp)
        frameTextParametersViewModel.getSelectedItem().observe(this) { }

        //  Hyphen Details List
        val hyphenDetailsListViewModel: HyphenDetailsListViewModel = ViewModelProvider(this).get(
            HyphenDetailsListViewModel::class.java
        )
        hyphenDetailsListViewModel.selectItem(hyphenDetailsList)
        hyphenDetailsListViewModel.selectedItem.observe(this) { }

        // Hyphen file List
        val hyphenFilesListViewModel: HyphenFilesListViewModel = ViewModelProvider(this).get(
            HyphenFilesListViewModel::class.java
        )
        hyphenFilesListViewModel.selectItems(hyphenFilesList)
        hyphenFilesListViewModel.selectedItem.observe(this) { }

        // FrameText bitmap image
        val frameTextBitmapViewModel: FrameTextBitmapViewModel = ViewModelProvider(this).get(
            FrameTextBitmapViewModel::class.java
        )
        frameTextBitmapViewModel.getSelectedItem().observe(this) { }

        // TabLayout. When generate image, 1st tab sets 3rd tab active, so text input fragment needs access to tabs.
        val tabLayoutViewModel = ViewModelProvider(this)[TabLayoutViewModel::class.java]

        val tabLayoutCopy = tabLayout

        if (tabLayoutCopy != null) {
            tabLayoutViewModel.selectItem(tabLayoutCopy)
        }
        tabLayoutViewModel.getSelectedItem().observe(this) { }

        // Hyphen pattern language to file name map
        val hplFileNameMapViewModel: HplFileNameMapViewModel = ViewModelProvider(this).get(
            HplFileNameMapViewModel::class.java
        )
        hplFileNameMapViewModel.selectItem(hplFileNameMap)
        hplFileNameMapViewModel.getSelectedItem().observe(this) { }

        // File name to hyphen pattern language map
        val fileNameHplMapViewModel: FileNameHplMapViewModel = ViewModelProvider(this).get(
            FileNameHplMapViewModel::class.java
        )
        fileNameHplMapViewModel.selectItem(fileNameHplMap)
        fileNameHplMapViewModel.getSelectedItem().observe(this) { }

        // Font family maps - only need to initialize once...
        userFriendlyFontFamilyFontFamilyMap = Utilities.userFriendlyFontFamilyToFontFamilyHashMap()
        val userFriendlyFontFamilyFontFamilyMapViewModel: UserFriendlyFontFamilyFontFamilyMapViewModel =
            ViewModelProvider(this).get(
                UserFriendlyFontFamilyFontFamilyMapViewModel::class.java
            )
        userFriendlyFontFamilyFontFamilyMapViewModel.selectItem(userFriendlyFontFamilyFontFamilyMap)
        userFriendlyFontFamilyFontFamilyMapViewModel.getSelectedItem().observe(this) { }

        fontFamilyUserFriendlyFontFamilyMap = Utilities.fontFamilyToUserFriendlyFontFamilyHashMap()

        val fontFamilyUserFriendlyFontFamilyMapViewModel: FontFamilyUserFriendlyFontFamilyMapViewModel =
            ViewModelProvider(this).get(
                FontFamilyUserFriendlyFontFamilyMapViewModel::class.java
            )
        fontFamilyUserFriendlyFontFamilyMapViewModel.selectItem(fontFamilyUserFriendlyFontFamilyMap)
        fontFamilyUserFriendlyFontFamilyMapViewModel.getSelectedItem().observe(this) { }

        userFriendlyFontFamilyToTypeFaceIdMap = Utilities.userFriendlyFontFamilyToTypeFaceId()
        val userFriendlyFontFamilyTypesetIdMapViewModel: UserFriendlyFontFamilyTypesetIdMapViewModel =
            ViewModelProvider(this).get(
                UserFriendlyFontFamilyTypesetIdMapViewModel::class.java
            )
        userFriendlyFontFamilyTypesetIdMapViewModel.selectItem(userFriendlyFontFamilyToTypeFaceIdMap)
        userFriendlyFontFamilyTypesetIdMapViewModel.getSelectedItem().observe(this) { }

        typeFaceIdToUserFriendlyFontFamilyMap = Utilities.typeFaceIdToUserFriendlyFontFamily()
        val typesetIdUserFriendlyFontFamilyMapViewModel: TypesetIdUserFriendlyFontFamilyMapViewModel =
            ViewModelProvider(this).get(
                TypesetIdUserFriendlyFontFamilyMapViewModel::class.java
            )
        typesetIdUserFriendlyFontFamilyMapViewModel.selectItem(typeFaceIdToUserFriendlyFontFamilyMap)
        typesetIdUserFriendlyFontFamilyMapViewModel.getSelectedItem().observe(this) { }

        // if ftp.minDistEdgeShape is 0, it has never been saved and therefore needs to be recomputed
        // from symbol/emoji set as default.
        if (ftp.minDistEdgeShape == 0) {
            ftp.minDistEdgeShape = Utilities.closestDistance(
                ftp.useEmoji,
                ftp.emoji,
                ftp.symbol,
                ftp.symbolShapeType
            )
        }
    }

    private fun loadUserFile(): java.util.ArrayList<String> {
        val userFileList = java.util.ArrayList<String>()
        val usrFilePath = getUserFileFolder(false, applicationContext)
        if (usrFilePath != null) {
            val userFilesFolder = File(usrFilePath)
            val files = userFilesFolder.listFiles()
            if (files != null) {
                for (usrFile in files) {
                    if (usrFile.isFile) {
                        userFileList.add(usrFile.name)
                    }
                }
            }
        }
        return userFileList
    }

    @Throws(IOException::class, JSONException::class)
    private fun loadSavedSettings() {
        val settingsFileName = getSettingsFileName(applicationContext)
        if (settingsFileName != null) {
            val settingsFile = File(settingsFileName)
            if (settingsFile.exists()) {
                val settings: String? = readSavedSettingsFomFile()
                if (settings != null) {
                    val jsonObject = JSONObject(settings)
                    ftp.hyphenFileName = jsonObject[Constants.HYPHEN_FILE_NAME] as String
                    ftp.optimizeSpacing = jsonObject.getBoolean(Constants.OPTIMIZE_SPACING)
                    ftp.hyphenateText = jsonObject.getBoolean(Constants.HYPHENATE_TEXT)
                    ftp.setTxtSymbolsMargin(jsonObject.getInt(Constants.TEXT_TO_SYMBOLS_MARGIN))
                    ftp.outerMargin = jsonObject.getInt(Constants.OUTER_MARGIN)
                    ftp.textColor = jsonObject.getInt(Constants.TEXT_COLOR)
                    ftp.symbolsColor = jsonObject.getInt(Constants.SYMBOLS_COLOR)
                    ftp.backgroundColor = jsonObject.getInt(Constants.BACKGROUND_COLOR)
                    ftp.useEmoji = jsonObject.getBoolean(Constants.USE_EMOJI)
                    ftp.emoji = jsonObject[Constants.EMOJI] as String
                    val shapeType = jsonObject[Constants.SYMBOL_SHAPE_TYPE] as String
                    ftp.symbolShapeType = SymbolShapeType.valueOf(shapeType)
                    val mainShape = jsonObject[Constants.MAIN_SHAPE_TYPE] as String
                    ftp.mainShapeType = MainShapeType.valueOf(mainShape)
                    if (shapeType == "None") {
                        ftp.symbol = jsonObject[Constants.SYMBOL] as String
                    }
                    ftp.fontFamily = jsonObject[Constants.FONT_FAMILY] as String
                    ftp.fontStyle = jsonObject.getInt(Constants.TYPEFACE)
                    ftp.minDistEdgeShape = jsonObject.getInt(Constants.MIN_DIST_EDGE_SHAPE)
                    ftp.typefaceId = jsonObject.getInt(Constants.TYPEFACE_ID)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun readSavedSettingsFomFile(): String? {
        val settingsFilePath: String? = getSettingsFileName(applicationContext)
        if (settingsFilePath != null) {
            val file = File(settingsFilePath)
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)
            val stringBuilder = StringBuilder()
            var line = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }
            bufferedReader.close()
            return stringBuilder.toString()
        }
        return null
    }

    private fun loadJSONFromAsset(): String? {
        val json: String = try {
            val `is` = assets.open("hyphenDetails.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    companion object {

        @JvmStatic
        fun getSettingsFileName(context: Context): String? {
            var folderCreated = true
            val docDir = context.filesDir
            val frameTextFolder = docDir.path + Constants.FRAME_TXT_FOLDER
            val frameTextDir = File(frameTextFolder)
            if (!frameTextDir.exists()) {
                folderCreated = frameTextDir.mkdir()
            }
            if (folderCreated) {
                val settingsFolder = frameTextFolder + Constants.SETTINGS_FOLDER
                val settingsDir = File(settingsFolder)
                if (!settingsDir.exists()) {
                    folderCreated = settingsDir.mkdir()
                }
                return if (folderCreated) "$settingsFolder/settings.json" else null
            }
            return null
        }

        @JvmStatic
        fun getHyphenFileFolder(context: Context): String? {
            var folderCreated = true
            val docDir = context.filesDir
            val frameTextFolder = docDir.path + Constants.FRAME_TXT_FOLDER
            val frameTextDir = File(frameTextFolder)
            if (!frameTextDir.exists()) {
                folderCreated = frameTextDir.mkdir()
            }
            if (folderCreated) {
                val hyphenFileFolder = frameTextFolder + Constants.HYPHENATION
                val hyphenFileDir = File(hyphenFileFolder)
                if (!hyphenFileDir.exists()) {
                    folderCreated = hyphenFileDir.mkdir()
                }
                return if (folderCreated) "$hyphenFileFolder/" else null
            }
            return null
        }

        @JvmStatic
        fun getUserFileFolder(appendBackslash: Boolean, context: Context): String? {
            var folderCreated = true
            val docDir = context.filesDir
            val frameTextFolder = docDir.path + Constants.FRAME_TXT_FOLDER
            val frameTextDir = File(frameTextFolder)
            if (!frameTextDir.exists()) {
                folderCreated = frameTextDir.mkdir()
            }
            if (folderCreated) {
                val userFileFolder = frameTextFolder + Constants.USER_FILE_FOLDER
                val userFileDir = File(userFileFolder)
                if (!userFileDir.exists()) {
                    folderCreated = userFileDir.mkdir()
                }
                return if (folderCreated) userFileFolder + (if (appendBackslash) '/' else "") else null
            }
            return null
        }

        @Throws(JSONException::class)
        fun getJSonObjectFromFramesTextParameters(hvp:FrameTextParameters): JSONObject {
            val jsonObject = JSONObject()
            jsonObject.put(Constants.HYPHEN_FILE_NAME, hvp.hyphenFileName)
            jsonObject.put(Constants.OPTIMIZE_SPACING, hvp.optimizeSpacing)
            jsonObject.put(Constants.HYPHENATE_TEXT, hvp.hyphenateText)
            jsonObject.put(Constants.TEXT_TO_SYMBOLS_MARGIN, hvp.getTxtSymbolsMargin())
            jsonObject.put(Constants.OUTER_MARGIN, hvp.outerMargin)
            jsonObject.put(Constants.TEXT_COLOR, hvp.textColor)
            jsonObject.put(Constants.SYMBOLS_COLOR, hvp.symbolsColor)
            jsonObject.put(Constants.BACKGROUND_COLOR, hvp.backgroundColor)
            jsonObject.put(Constants.USE_EMOJI, hvp.useEmoji)
            jsonObject.put(Constants.EMOJI, hvp.emoji)
            jsonObject.put(Constants.SYMBOL_SHAPE_TYPE, hvp.symbolShapeType)
            jsonObject.put(Constants.MAIN_SHAPE_TYPE, hvp.mainShapeType)
            jsonObject.put(Constants.SYMBOL, hvp.symbol)
            jsonObject.put(Constants.FONT_FAMILY, hvp.fontFamily)
            jsonObject.put(Constants.TYPEFACE, hvp.fontStyle)
            jsonObject.put(Constants.MIN_DIST_EDGE_SHAPE, hvp.minDistEdgeShape)
            jsonObject.put(Constants.TYPEFACE_ID, hvp.typefaceId)

            return jsonObject
        }
    }
}