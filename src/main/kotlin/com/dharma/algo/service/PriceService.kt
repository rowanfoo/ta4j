package com.dharma.algo.service

//@Component
class PriceService {
//    @Autowired
//    lateinit var coreDataIgniteService: CoreDataIgniteService
//
//    @Autowired
//    lateinit var coreDataIgniteService: CoreDataIgniteService
//
//    @Autowired
//    lateinit var allStocks: Map<String, CoreStock>
//
//    lateinit var addStockR: (techstr) -> techstr
//
//
//    fun pricedatajson(coreDataIgniteService: CoreDataIgniteService, date: String, code: String): ObjectNode {
//        var mapper = ObjectMapper()
//        var rootNode = mapper.createObjectNode()
//        (rootNode as ObjectNode).put("code", code)
//
//        var data = coreDataIgniteService.dateeq(code, date)
//        (rootNode as ObjectNode).put("price", data.close)
//        (rootNode as ObjectNode).put("change", data.changepercent)
//        (rootNode as ObjectNode).put("volume", data.volume)
//        (rootNode as ObjectNode).put("date", data.date.toString())
//        return rootNode
//    }
//
//
//    fun perioddatajson(coreDataIgniteService: CoreDataIgniteService, code: String): ObjectNode {
//        var mapper = ObjectMapper()
//
//        var rootNode = mapper.createObjectNode()
//        (rootNode as ObjectNode).put("code", code)
//        (rootNode as ObjectNode).put("weeky", Maths.percent( coreDataIgniteService.priceperiodprecent(code, "week")  ) )
//        (rootNode as ObjectNode).put("month", coreDataIgniteService.priceperiodprecent(code, "month"))
//        (rootNode as ObjectNode).put("3month", coreDataIgniteService.priceperiodprecent(code, "3month"))
//        return rootNode
//    }


}
