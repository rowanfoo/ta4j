package com.dharma.algo.Controller

import com.dhamma.ignitedata.service.CoreDataIgniteService
import com.dhamma.pesistence.entity.data.CoreData
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URL
import java.time.LocalDate
import java.util.*
import java.util.function.Consumer


@CrossOrigin
@RestController
class DataController {
//
//    @Autowired
//    lateinit var dataRepo: DataRepo
//
//    @Autowired
//    lateinit var ignitecache: IgniteRepo<CoreData>

    @Autowired
    lateinit var coreDataIgniteService: CoreDataIgniteService


//    @GetMapping("/dategt")
//    fun dategt(@RequestParam date: String , code:String ): Iterable<CoreData> = dataRepo.findAll(QCoreData.coreData.date.gt(LocalDate.parse(date)).and(QCoreData.coreData.code.eq(code) )   )

//    @GetMapping("/dategt")
//    fun dategt(@RequestParam date: String, code: String): Iterable<CoreData> = ignitecache.values(" where code=?  and  date > ?  ", arrayOf(code, date))

    @GetMapping("/dategt")
    fun dategt(@RequestParam date: String, code: String): Iterable<CoreData> = coreDataIgniteService.dategt(code, date)

    @GetMapping("/week/dategt")
    fun dateweekgt(@RequestParam date: String, code: String): List<CoreData> {
        println("-----------dateweekgt------------")
        val uri =
            "https://eodhistoricaldata.com/api/eod/$code.au?api_token=61d5ab86451db3.27602867&fmt=json&period=w&from=$date&to=${
                LocalDate.now().toString()
            }"
        println("-----------dateweekgt-1123-----$uri------")

        val mapper = ObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        //System.out.println("----ASX import RUN !!!insertdata!!  --:" + uri);
        //System.out.println("----ASX import RUN !!!insertdata!!  --:" + uri);
        //System.out.println("----ASX import RUN !!!insertdata!!  --:" + uri);
//        CoreData[] node = null;
//        node = mapper.readValue(new URL(uri), CoreData[].class);
//        System.out.println(node);
//        Arrays.stream(node).forEach(coreData -> {
//        });
//
//        Arrays.stream(node).forEach(coreData -> {
//            System.out.println(coreData);
//        });

//        MyClass[] myObjects = mapper.readValue(json, MyClass[].class);
        var nodes = mapper.readValue(
            URL(uri),
            Array<JsonNode>::class.java
        )
        var list = mutableListOf<CoreData>()
        nodes.iterator().forEachRemaining(Consumer {
            var c = CoreData()
            var d = it.get("date").toString().replace("\"", "")
            c.setDate(LocalDate.parse(d))
            c.code = "$code.AX"
            c.setClose(it.get("close").toString().toDouble())
            c.setOpen(it.get("open").toString().toDouble())
            c.setLow(it.get("low").toString().toDouble())
            c.setHigh(it.get("high").toString().toDouble())
            c.setVolume(it.get("volume").toString().toLong())
            c.setChanges(0.0)
            c.setChangepercent(0.0)
            c.setPreviousclose("")
            list.add(c)
        })
        return list
    }

}
