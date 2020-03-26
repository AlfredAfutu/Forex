package com.revolut.finance.domain.usecase

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

abstract class UseCase<Input, Output>(private val scheduler: Scheduler = Schedulers.io()) {

    protected abstract fun run(input: Input): Observable<Output>

    operator fun invoke(input: Input): Observable<Output> =
        run(input)
            .subscribeOn(scheduler)
}