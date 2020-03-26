package com.revolut.finance.ui

import androidx.annotation.StringRes

sealed class UIResult<out T> {
    data class Success<out T>(val data: T) : UIResult<T>()
    data class Error<out T>(@StringRes val errorMessage: Int) : UIResult<T>()
}