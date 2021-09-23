package com.dharma.algo


import com.dhamma.pesistence.entity.data.News
import com.dhamma.pesistence.entity.data.QNews
import com.dhamma.pesistence.entity.repo.NewsRepo
import com.dharma.algo.service.NewsService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate

@RunWith(SpringRunner::class)
@SpringBootTest
class NewsServiceTest {
    @Autowired
    lateinit var newsService: NewsService

    @Autowired
    lateinit var newsRepo: NewsRepo


    @Test
    fun contextLoads() {

//        var x = newsService.news(Optional.of("2020-10-16"),
//                true, "1", "50")
//
//        println("------NewsServiceTest--------$x------")
        val pageRequest = PageRequest.of(0, 35)
        val pageResult: Page<News> = newsRepo.findAll(QNews.news.date.eq(LocalDate.parse("2020-10-16")), pageRequest)
//        println("---RESULT---------$pageResult-")
        //      return pageResult

        var x = newsService.addStockInfo(pageResult.content, true, "0", "35")

        println("---RESULT---------$x-")


    }

}
