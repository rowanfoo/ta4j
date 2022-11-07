package com.dharma.algo.service

import com.dhamma.ignitedata.manager.MAManager
import com.dhamma.ignitedata.manager.RSIManager
import com.dhamma.ignitedata.service.CoreDataIgniteService
import com.dhamma.ignitedata.service.NewsIgniteService
import com.dhamma.ignitedata.service.PeriodService
import com.dhamma.pesistence.entity.data.CoreStock
import com.dhamma.pesistence.entity.data.Fundamental
import com.dhamma.pesistence.entity.data.HistoryIndicators
import com.dhamma.pesistence.service.FundamentalService
import com.dhamma.pesistence.service.NewsServices
import com.dharma.algo.data.pojo.Stock
import com.dharma.algo.data.pojo.techstr
import com.dharma.algo.utility.Json
import com.dharma.algo.utility.Maths
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.gson.JsonObject
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json





fun historyIndicatorstoTechstrs(list: List<HistoryIndicators>): List<techstr> {
    return list.map {
        techstr(it.code, it.date, it.type.toString(), it.message)
    }.toList()
}


fun bind(toRun: Boolean, fn: (techstr) -> techstr, data: techstr): techstr {
    return when (toRun) {
        true -> fn(data)
        false -> data
    }
}


fun addNews(newsIgniteService: NewsIgniteService, tech: techstr): techstr {
    tech.news = newsIgniteService.newsToday(tech.code)
    return tech
}

fun addFundamental(fundamentalService: FundamentalService, tech: techstr): techstr {
    tech.fundamental = fundamentalService.code(tech.code)
    return tech;
}

fun addStock(stockservice: Map<String, CoreStock>, tech: techstr): techstr {
    var stk = stockservice.get(tech.code)
    tech.stock = Stock(stk!!.code, stk.category ?: "", stk!!.name)
    return tech;
}


fun stockinfo(stockservice: Map<String, CoreStock>, code: String): JsonNode {
    val mapper = ObjectMapper()
    var stk = stockservice.get(code)
    return if (stk == null) mapper.createObjectNode() else Json.toJson(stk!!);
}

var mapper = ObjectMapper()


fun stocktojson(
    stockservice: Map<String, CoreStock>, allFundamental: Map<String, Fundamental>, code: String
): JsonNode {
    var stk = stockservice.get(code)

    var rootNode = mapper.createObjectNode()
    (rootNode as ObjectNode).put("code", stk?.code)
    (rootNode as ObjectNode).put("name", stk?.name)
    (rootNode as ObjectNode).put("shares",allFundamental[stk?.code]?.shares)
    (rootNode as ObjectNode).put("marketcap", allFundamental[stk?.code]?.martketcapAsString)
    (rootNode as ObjectNode).put("category", stk?.category)
    (rootNode as ObjectNode).put("subcategory", stk?.subcategory)
    (rootNode as ObjectNode).put("tags", stk?.tags)
    return rootNode
}

fun pricedatajson(coreDataIgniteService: CoreDataIgniteService, date: String, code: String): ObjectNode {
    var rootNode = mapper.createObjectNode()
    (rootNode as ObjectNode).put("code", code)
    //println("====================pricedatajson==================$code===++++++$date==")
    var data = coreDataIgniteService.dateeq(code, date)
    println("----pricedatajson data------$data")

    (rootNode as ObjectNode).put("price", data.close)
    (rootNode as ObjectNode).put("change", data.changepercent)
    (rootNode as ObjectNode).put("volume", data.volume)
    (rootNode as ObjectNode).put("date", data.date.toString())
    return rootNode
}

fun perioddatamonthjson(periodService: PeriodService, code: String): ObjectNode {
   // println("----perioddatamonthjson data-----")
    var data = periodService.getMonth(code)
  //  println("----perioddatajson data------$data")
    return data
}

fun perioddataweekjson(periodService: PeriodService, code: String): ObjectNode {
   // println("----perioddataweekjson data-----")
    var data = periodService.getWeek(code)
 //   println("----perioddataweekjson data------$data")
    return data
}

fun perioddata3monthjson(periodService: PeriodService, code: String): ObjectNode {
   // println("----perioddatamonthjson data-----")
    var data = periodService.get3Month(code)
  //  println("----perioddatajson data------$data")
    return data
}



fun perioddatajson(coreDataIgniteService: CoreDataIgniteService, code: String): ObjectNode {

    var rootNode = mapper.createObjectNode()
    (rootNode as ObjectNode).put("code", code)
    (rootNode as ObjectNode).put(
        "weeky", Maths.percentformat(coreDataIgniteService.priceperiodprecent(code, "week") * 100)
    )
    (rootNode as ObjectNode).put(
        "month", Maths.percentformat(coreDataIgniteService.priceperiodprecent(code, "month") * 100)
    )
    (rootNode as ObjectNode).put(
        "month3", Maths.percentformat(coreDataIgniteService.priceperiodprecent(code, "3month") * 100)
    )
    return rootNode
}


fun madatajson(ma: MAManager, obj: JsonObject, stocks: List<String>): List<ObjectNode> {

    var rootNode = mapper.createObjectNode()
    var time = obj.get("time").asString

    var z = ma.ma(obj, stocks)

    var t = z.map {
        (rootNode as ObjectNode).put("maprice$time", it["maprice"].asString)
        (rootNode as ObjectNode).put("mapercentage$time", Maths.percentformat(it["percentage"].asDouble * 100))
    }.toList() as List<ObjectNode>
    return t
}

fun rsidatajson(rsi: RSIManager, stocks: String): ObjectNode {
    var rootNode = mapper.createObjectNode()
    var z = rsi.getRSI(stocks)
    var t = (rootNode as ObjectNode).put("rsi", z.toString())
    return rootNode
}

fun fundamentaljson(fundamentalService: FundamentalService, code: String): ObjectNode {
    var fundamental = fundamentalService.code(code)
    var rootNode = mapper.createObjectNode()

    (rootNode as ObjectNode).put("code", code)
    (rootNode as ObjectNode).put("annualyield", fundamental.annualYied)
    (rootNode as ObjectNode).put("eps", fundamental.eps)
    (rootNode as ObjectNode).put("marketcap", fundamental.martketcapAsStringAsM)
    (rootNode as ObjectNode).put("pe", fundamental.pe)
    (rootNode as ObjectNode).put("shares", fundamental.shares)
    (rootNode as ObjectNode).put("yearhighprice", fundamental.yearHighPrice)
    (rootNode as ObjectNode).put("yearlowprice", fundamental.yearLowPrice)
    (rootNode as ObjectNode).put("yearchange", fundamental.yearchange ?: 0.0)
    return rootNode
}





fun newstodayjson(newsService: NewsServices, code: String): ObjectNode {
    var fundamental = newsService.threeDays(code)
    var rootNode = mapper.createObjectNode()

    (rootNode as ObjectNode).put("code", code)
    val info: ObjectNode = (rootNode as ObjectNode).putObject("news")
   var arraynode=  info.putArray("news")
    fundamental.forEach{
        arraynode .add(it.title )
    }
    return rootNode
}

