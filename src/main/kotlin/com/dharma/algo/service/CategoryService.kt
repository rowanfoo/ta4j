package com.dharma.algo.service

import arrow.syntax.function.curried
import com.dhamma.ignitedata.manager.MAManager
import com.dhamma.ignitedata.manager.RSIManager
import com.dhamma.ignitedata.service.CoreDataIgniteService
import com.dhamma.ignitedata.service.HistoryIndicatorService
import com.dhamma.ignitedata.service.PeriodService
import com.dhamma.pesistence.entity.data.*
import com.dhamma.pesistence.entity.repo.FundamentalRepo
import com.dhamma.pesistence.entity.repo.PortfolioRepo
import com.dhamma.pesistence.service.NewsServices
import com.dhamma.pesistence.entity.repo.StockRepo
import com.dhamma.pesistence.service.FundamentalService
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class CategoryService {
    @Autowired
    lateinit var stockrepo: StockRepo


    @Autowired
    lateinit var allStocks: Map<String, CoreStock>

    @Autowired
    lateinit var coreDataIgniteService: CoreDataIgniteService

    @Autowired
    lateinit var ma: MAManager

    @Autowired
    lateinit var rsi: RSIManager


    @Autowired
    lateinit var fundamentalservice: FundamentalService

    @Autowired
    lateinit var newsService: NewsServices

    @Autowired
    lateinit var fundamentalRepo: FundamentalRepo

    lateinit var addPrice: (String) -> JsonNode

    lateinit var addfundamentalR: (String) -> JsonNode
    lateinit var addNewsR: (String) -> JsonNode
    lateinit var addStockR: (String) -> JsonNode


    @Autowired
    lateinit var historyIndicatorService: HistoryIndicatorService

    @Autowired
    lateinit var periodService: PeriodService


    /// is this latest version
    @Autowired
    lateinit var allFundamental: Map<String, Fundamental>

    @Autowired
    lateinit var portfolioRepo : PortfolioRepo

    @PostConstruct
    fun init() {
        addStockR = ::stocktojson.curried()(allStocks)(allFundamental)
    }

    fun category(name: String): List<JsonNode> {
        var z = stockrepo.findAll(QCoreStock.coreStock.category.like("%$name%")).map { it.code }. toList()
        println("----------category---${z.size}-----------")
        return addAdditionInfo(z)
    }

    fun subcategory(name: String): List<JsonNode> {
        var z = stockrepo.findAll(QCoreStock.coreStock.subcategory.like("%$name%")).map { it.code }.toList()
        println("----------subcategory---${z.size}-----------")
        return addAdditionInfo(z)
    }

    fun wishlist (): List<JsonNode> {
        var z = stockrepo.findAll(QCoreStock.coreStock.wishlist.eq(true )).map { it.code }.toList()
        println("----------subcategory---${z.size}-----------")
        return addAdditionInfo(z)
    }

    fun portfolio(): List<JsonNode> {
        var z = portfolioRepo.findAll().map { it.code }.distinct() .toList()
        println("----------portfolio---${z}-----------")
        return addAdditionInfo(z)
    }

    fun tag(name: String): List<JsonNode> {
        var z = stockrepo.findAll(QCoreStock.coreStock.tags.like("%$name%")).map { it.code }. toList()
        println("----------tag---${z.size}-----------")
        return addAdditionInfo(z)
    }

    fun mktCapGreater1b(name: String): List<JsonNode> {
        var codes = fundamentalRepo.findAll(QFundamental.fundamental.marketcap.gt(1000000000)).map { it.code }.toList()
        var z = stockrepo.findAll(QCoreStock.coreStock.code.`in`(codes).and(QCoreStock.coreStock.category.like(name)  )   ).map { it.code }.toList()
        println("----------mktCapGreater1b---${z.size}-----------")
        return addAdditionInfo(z)
    }

    fun addAdditionInfo(ls: List<String>): List<JsonNode> {
           addPrice = ::pricedatajson.curried()(coreDataIgniteService)(historyIndicatorService.today().toString())
        //var perioddatajson = ::perioddatajson.curried()(coreDataIgniteService)

        addfundamentalR = ::fundamentaljson.curried()(fundamentalservice)
        addNewsR = ::newstodayjson.curried()(newsService)




        var MA50 = JsonObject()
        MA50.addProperty("mode", "price")
        MA50.addProperty("time", "50")

        var MA200 = JsonObject()
        MA200.addProperty("mode", "price")
        MA200.addProperty("time", "200")


        var majson = ::madatajson.curried()(ma)(MA50)
        var majson200 = ::madatajson.curried()(ma)(MA200)

        var rsijson = ::rsidatajson.curried()(rsi)
        var perioddatamonth  = ::perioddatamonthjson.curried()(periodService)
        var perioddataweekjson  = ::perioddataweekjson.curried()(periodService)
        var perioddata3monthjson  = ::perioddata3monthjson.curried()(periodService)



//        fun madatajson(ma: MAManager, obj: JsonObject, stocks: List<String>): List<ObjectNode> {
        println("-----------start------------")

        var t =
            ls.map(addStockR).map {
            var z = addPrice(it["code"].asText())
            //        println("----------xxxx1---${z}-----------")
            (it as ObjectNode).setAll(z as ObjectNode)
//                            println("----------yyyyy2---${it}-----------")
            it
        }.map {
                println("----------MONTH---${it}-----------")
            var z = perioddatamonth(it["code"].asText())
                println("----------MONTH--OK----------")


            //   println("----------xxxx2---${z}-----------")
            (it as ObjectNode).setAll(z as ObjectNode)
//                            println("----------yyyyy4---${it}-----------")
            it
        }
//            .map {
//                var z = perioddatajson(it["code"].asText())
//                //   println("----------xxxx2---${z}-----------")
//                (it as ObjectNode).setAll(z as ObjectNode)
////                            println("----------yyyyy4---${it}-----------")
//                it
//            }

            .map {
                var z = perioddataweekjson(it["code"].asText())
                //   println("----------xxxx2---${z}-----------")
                (it as ObjectNode).setAll(z as ObjectNode)
//                            println("----------yyyyy4---${it}-----------")
                it
            }

            .map {
                var z = perioddata3monthjson(it["code"].asText())
                (it as ObjectNode).setAll(z as ObjectNode)
                it
            }


            .map {

            var z = majson200(listOf(it["code"].asText()))
            //  println("---------zzzzz3---${z}-----------")
            (it as ObjectNode).setAll(z[0] as ObjectNode)
//                            println("----------yyyyy5---${it}-----------")
            it
        }
            .map {

                var z = majson(listOf(it["code"].asText()))
                //  println("---------zzzzz3---${z}-----------")
                (it as ObjectNode).setAll(z[0] as ObjectNode)
//                            println("----------yyyyy5---${it}-----------")
                it
            }
            .map {
            //  println("----------zzzzz4---${it}-----------")
            var z = addfundamentalR(it["code"].asText())
            (it as ObjectNode).setAll(z as ObjectNode)
            //    println("----------yyyyy6---${it}-----------")
            it
        }.map {
            //    println("----------zzzzz5---${it}-----------")
                var z = rsijson(it["code"].asText())
                (it as ObjectNode).setAll(z as ObjectNode)
                //println("----------yyyyy6---${it}-----------")
                it
            }
            .map {
               // println("----------NEWS 1---${it}-----------")
                var z = addNewsR(it["code"].asText())
                (it as ObjectNode).setAll(z as ObjectNode)
             //   println("----------NEWS 2---${it}-----------")
                it
            }.sortedByDescending {  it["marketcap"].asText().toLong() }
        .toList()
        return t

        //is there ghost
        //  return emptyList()
    }
}



