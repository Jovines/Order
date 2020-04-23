package com.jovines.order.base

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.jovines.order.base.viewmodel.BaseViewModel

/**
 * @author jon
 * @create 2020-04-23 3:23 PM
 *
 * 描述:
 *
 */
abstract class BaseViewModelActivity<T : BaseViewModel> : BaseActivity() {

    lateinit var viewModel: T

    abstract val viewModelClass: Class<T>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[viewModelClass]
    }

}