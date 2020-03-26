package com.revolut.finance

import com.revolut.finance.extension.InstantExecutorExtension
import com.revolut.finance.extension.RxSchedulerExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit

@ExtendWith(InstantExecutorExtension::class, RxSchedulerExtension::class)
abstract class BaseTest {

    @Throws(Exception::class)
    open fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    private fun advanceScheduler(seconds: Long = 1) = RxSchedulerExtension.testScheduler.advanceTimeBy(seconds, TimeUnit.SECONDS)

    protected fun triggerActions() = RxSchedulerExtension.testScheduler.triggerActions()

    protected fun advanceSchedulerAndTriggerActions(seconds: Long = 1) {
        advanceScheduler(seconds)
        triggerActions()
    }
}