package com.namii.app.core.network

interface TokenProvider {
    fun getToken(): String?
}
