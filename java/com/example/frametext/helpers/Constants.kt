package com.example.frametext.helpers

internal object Constants {
    const val FRAME_TXT_FOLDER = "/frameText"
    const val USER_FILE_FOLDER = "/userFiles"
    const val SETTINGS_FOLDER = "/settings"
    const val HYPHENATION = "/Hyphenation"

    // Put json column names in one place here so no mismatch between read and write...
    const val HYPHEN_FILE_NAME = "hyphenFileName"
    const val OPTIMIZE_SPACING = "optimizeSpacing"
    const val HYPHENATE_TEXT = "hyphenateText"
    const val TEXT_TO_SYMBOLS_MARGIN = "txtToSymbolsMargin"
    const val OUTER_MARGIN = "outerMargin"
    const val TEXT_COLOR = "textColor"
    const val SYMBOLS_COLOR = "symbolsColor"
    const val BACKGROUND_COLOR = "backgroundColor"
    const val USE_EMOJI = "useEmoji"
    const val EMOJI = "emoji"
    const val SYMBOL_SHAPE_TYPE = "symbolShapeType"
    const val MAIN_SHAPE_TYPE = "mainShapeType"
    const val SYMBOL = "symbol"
    const val FONT_FAMILY = "fontFamily"
    const val TYPEFACE_ID = "typefaceId"
    const val TYPEFACE = "typeface"
    const val MIN_DIST_EDGE_SHAPE = "minDistEdgeShape"

    // Font family names.
    // these work
    // https://reactnative-examples.com/font-family-available-in-react-native-for-android/
    // these font names don't work:
    // https://fonts.google.com/
    // codes:
    const val FF_MONOSPACE = "monospace"
    const val FF_NORMAL = "normal"
    const val FF_NOTOSERIF = "notoserif"
    const val FF_ROBOTO = "Roboto"
    const val FF_SANS_SERIF = "sans-serif"
    const val FF_SANS_SERIF_LIGHT = "sans-serif-light"
    const val FF_SANS_SERIF_THIN = "sans-serif-thin"
    const val FF_SANS_SERIF_CONDENSED = "sans-serif-condensed"
    const val FF_SANS_SERIF_MEDIUM = "sans-serif-medium"
    const val FF_SERIF = "serif"

    // user friendly names:
    const val UFFF_MONOSPACE = "Monospace"
    const val UFFF_NORMAL = "Normal"
    const val UFFF_NOTOSERIF = "Notoserif"
    const val UFFF_ROBOTO = "Roboto"
    const val UFFF_SANS_SERIF = "Sans Serif"
    const val UFFF_SANS_SERIF_LIGHT = "Sans Serif Light"
    const val UFFF_SANS_SERIF_THIN= "Sans Serif Thin"
    const val UFFF_SANS_SERIF_CONDENSED = "Sans Serif Condensed"
    const val UFFF_SANS_SERIF_MEDIUM = "Sans Serif Medium"
    const val UFFF_SERIF = "Serif"
    const val UFFF_AKRONIM = "Akronim"
    const val UFFF_BUTTERFLY_KIDS = "Butterfly Kids"
    const val UFFF_DANCING_SCRIPT = "Dancing Script"
    const val UFFF_EUPHORIA_SCRIPT = "Euphoria Script"
    const val UFFF_FASTER_ONE = "Faster One"
    const val UFFF_FRIJOLE = "Frijole"
    const val UFFF_JACQUES_FRANCOIS_SHADOW = "Jacques François Shadow"
    const val UFFF_LOBSTER = "Lobster"
    const val UFFF_LOVERS_QUARREL = "Lovers Quarrel"
    const val UFFF_MISS_FAJARDOSE = "Miss Fajardose"
    const val UFFF_MONOFETT = "Monofett"
    const val UFFF_MONSIEUR_LA_DOULAISE = "Monsieur La Doulaise"
    const val UFFF_MONTE_CARLO = "Monte Carlo"
    const val UFFF_NOSIFER = "Nosifer"
    const val UFFF_OLD_LONDON = "Old London"
    const val UFFF_PUPPIES_PLAY = "Puppies Play"
    const val UFFF_RAMPART_ONE = "Rampart One"
    const val UFFF_SHADOWS_INTO_LIGHT = "Shadows Into Light"
    const val UFFF_UNIFRAKTUR_COOK = "Unifraktur Cook"
    const val UFFF_UNIFRAKTUR_MAGUNTIA = "Unifraktur Maguntia"
    const val UFFF_VAST_SHADOW = "Vast Shadow"
}
