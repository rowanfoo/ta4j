package com.dharma.algo.utility

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.ignitedata.service.NewsIgniteService
import com.dhamma.pesistence.entity.data.CoreStock
import com.dharma.algo.data.pojo.Stock
import com.dharma.algo.data.pojo.techstr
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
public class TechStrBuilderUtility {
    @Autowired
    lateinit var ignitecachestock: IgniteRepo<CoreStock>
    @Autowired
    lateinit var newsIgniteService: NewsIgniteService


    public fun addtechstrs(data: Map<String, Any>, predicate: (params: Map<String, Any>) -> Boolean, addmesesage: (Map<String, Any>) -> String, getvaluemsg: (Map<String, Any>) -> String, news: Boolean): Option<techstr> {

        if (predicate(data)) {
            return Some(techstrs(data, addmesesage(data), getvaluemsg(data), (news)))
        }
        return None
    }

    public fun techstrs(data: Map<String, Any>, addmesesage: String, getvaluemsg: String, news: Boolean): techstr {
        var code = data.get("code") as String
        var date = data.get("date") as LocalDate


        var tech = techstr(code, date, addmesesage, getvaluemsg)
        var stk = ignitecachestock.get(code)
        var sector = stk.top ?: ""
        tech.stock = Stock(stk.code, sector, stk.name)

        if (news) {
            var a = GJson.toGson(mapOf("code" to code, "date" to date.toString()))
            tech.news = newsIgniteService.getCode(a)

        }


        return tech
    }

//    public fun techstrsNews(data: Map<String, Any>, addmesesage: String, getvaluemsg: String): techstr {
//        var tech = techstrs(data, addmesesage, getvaluemsg,false)
//        var code = data.get("code") as String
//        var date = data.get("date") as LocalDate
//        var a = GJson.toGson(mapOf("code" to code, "date" to date.toString()))
//        tech.news = newsIgniteService.getCode(a)
//        return tech
//    }

}