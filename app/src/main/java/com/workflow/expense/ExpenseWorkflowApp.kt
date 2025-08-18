package com.workflow.expense

import android.app.Application
import com.workflow.expense.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ExpenseWorkflowApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ExpenseWorkflowApp)
            modules(appModules)
        }
    }
}


