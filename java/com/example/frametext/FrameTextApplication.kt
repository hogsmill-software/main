/*
 * Copyright (C) 2021 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified by Hogsmill Software Ltd, May 2022 renamed NewFeaturesApplication from TrivialDriveApplication
 */

package com.example.frametext

import android.app.Application
import com.example.frametext.billing.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

@DelicateCoroutinesApi
class FrameTextApplication : Application() {
    lateinit var appContainer: AppContainer
    inner class AppContainer {
        private val applicationScope = GlobalScope
        val storeManager = StoreManager.getInstance(
            this@FrameTextApplication,
            applicationScope,
            arrayOf(SKU_MORE_EMOJIS, SKU_MORE_SYMBOLS)
        )
    }

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer()
    }
}