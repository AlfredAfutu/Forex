package com.revolut.finance.domain.usecase

import com.revolut.currencies.R
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.domain.repository.ICurrenciesRepository
import com.revolut.finance.mapper.Mapper
import com.revolut.finance.ui.UIResult
import com.revolut.finance.ui.model.CurrenciesViewData
import com.revolut.finance.ui.model.RateViewData
import com.revolut.finance.ui.model.UpdateRatesData
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal
import java.math.RoundingMode

class UpdateRatesUseCase(
    private val repository: ICurrenciesRepository,
    private val mapper: Mapper<Currencies, CurrenciesViewData>
) : UseCase<UpdateRatesData, UIResult<List<RateViewData>>>(Schedulers.computation()) {

    override fun run(input: UpdateRatesData): Observable<UIResult<List<RateViewData>>> =
        Observable.fromCallable { updateRates(input) }
            .onErrorReturnItem(UIResult.Error(R.string.rate_update_error_message))

    private fun updateRates(data: UpdateRatesData): UIResult<List<RateViewData>> {
        val currencies = repository.getCurrenciesFromCache()
        if (data.newRate.isBlank() || BigDecimal(data.newRate).compareTo(BigDecimal.ZERO)  == 0) {
            val currenciesViewData = mapper.mapTo(Currencies(currencies.baseCurrency, currencies.rates.map { it.copy(amount = BigDecimal("0")) }))
            return UIResult.Success(currenciesViewData.rates)
        }

        val rate = currencies.rates.first { it.currency == data.currency }
        val newBaseRateAmount = calculateNewBaseRate(BigDecimal(data.newRate), rate.amount)

        val updatedCurrencies = Currencies(
            currencies.baseCurrency,
            currencies.rates.map { it.copy(amount = (newBaseRateAmount.multiply(it.amount).setScale(2, RoundingMode.HALF_UP))) }
        )
        repository.setCacheCurrencies(updatedCurrencies)

        return UIResult.Success(mapper.mapTo(repository.getCurrenciesFromCache()).rates)
    }

    private fun calculateNewBaseRate(newRateAmount: BigDecimal, rateAmount: BigDecimal): BigDecimal =
        newRateAmount.divide(rateAmount, 2, RoundingMode.HALF_UP).stripTrailingZeros()
}