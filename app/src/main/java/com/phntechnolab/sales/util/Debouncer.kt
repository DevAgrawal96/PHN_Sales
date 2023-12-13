package com.phntechnolab.sales.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class Debouncer @Inject constructor() {
    private var debounceJob: Job? = null

    fun debounce(timeMillis: Long, action: () -> Unit) {
        debounceJob?.cancel()
        debounceJob = CoroutineScope(Dispatchers.Main).launch {
            delay(timeMillis)
            action.invoke()
        }
    }
}
