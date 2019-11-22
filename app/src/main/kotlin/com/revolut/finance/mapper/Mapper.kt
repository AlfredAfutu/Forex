package com.revolut.finance.mapper

interface Mapper<I, O> {
    fun mapTo(input: I?): O
}