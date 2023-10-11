package com.phntechnolab.sales.di

import android.content.ClipDescription
import android.content.Context

interface Downloader {
    fun downloadFile(url: String,filename : String): Long
}