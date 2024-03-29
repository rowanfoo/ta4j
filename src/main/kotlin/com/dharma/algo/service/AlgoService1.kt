package com.dharma.algo.service

import arrow.syntax.function.curried
import com.dhamma.ignitedata.service.HistoryIndicatorService
import com.dhamma.ignitedata.service.NewsIgniteService
import com.dhamma.pesistence.entity.data.CoreStock
import com.dhamma.pesistence.entity.data.HistoryIndicators
import com.dhamma.pesistence.service.FundamentalService
import com.dharma.algo.data.pojo.techstr
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct

/*
used by controller , organize some simple logic and also prepare data ,  get params

 */
@Component
class AlgoService1 {

    @Autowired
    lateinit var historyIndicatorService: HistoryIndicatorService

    @Autowired
    lateinit var newsIgniteService: NewsIgniteService

    @Autowired
    lateinit var fundamentalService: FundamentalService

    @Autowired
    lateinit var allStocks: Map<String, CoreStock>


    lateinit var addStockR: (techstr) -> techstr
    lateinit var setDateR: (techstr) -> techstr
    lateinit var addNewsR: (techstr) -> techstr
    lateinit var addFundamentalR: (techstr) -> techstr

    lateinit var setNewsC: (techstr) -> techstr
    lateinit var setFundC: (techstr) -> techstr
    lateinit var setStockC: (techstr) -> techstr

    @PostConstruct
    fun init() {
        addNewsR = ::addNews.curried()(newsIgniteService)
        addFundamentalR = ::addFundamental.curried()(fundamentalService)
        addStockR = ::addStock.curried()(allStocks)
    }

    fun setFunction(addDate: Boolean, addNews: Boolean, addFund: Boolean, addStock: Boolean) {
        setNewsC = ::bind.curried()(addNews)(addNewsR)
        setFundC = ::bind.curried()(addFund)(addFundamentalR)
        setStockC = ::bind.curried()(addStock)(addStockR)
    }

    fun process(
        id: String,
        date: Optional<String>,
        addDate: Boolean,
        addNews: Boolean,
        addFund: Boolean,
        addStock: Boolean,
        page: Pair<Int, Int>
    ): Page<techstr> {
        setFunction(addDate, addNews, addFund, addStock)

        var myddate = fngetdatebetween(date, historyIndicatorService)
        println("----myddate--$myddate-----$id-")
        var mypage = historyIndicatorService.datebetweentypeid(id, myddate.first, myddate.second!!, page)
        println("----1---")
        var data = mypage.content
        println("----2---")

        var content = historyIndicatorstoTechstrs(data)
            //.asSequence()
            .onEach { setNewsC(it) }
            .onEach { setFundC(it) }
            .onEach { setStockC(it) }
            .toList()

        var z = PageImpl<techstr>(content, PageRequest.of(page.first, page.second), mypage.totalElements)
        return z
    }


    fun processmessage(
        id: String
    ): List<techstr> {
        setFunction(false, true , true, false)

        var xx = mutableListOf<HistoryIndicators>()
        id.split(",").forEach{
            var data = historyIndicatorService.todaycodemsg(it)
            if (data != null) xx.add(data)
        }

        var content = historyIndicatorstoTechstrs(xx)
                      .onEach { setNewsC(it) }
            .onEach { setFundC(it) }
            .toList()
       return content
    }


}
