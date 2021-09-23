package com.dharma.algo

import com.dhamma.pesistence.entity.repo.DataRepo
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


@RunWith(SpringRunner::class)
@SpringBootTest
class toDel {


    @Autowired
    lateinit var dataRepo: DataRepo
//
//    @Test
//    fun getMetaData() {
//
//
//        var run = {
//            println("------here 1---")
//            TimeUnit.SECONDS.sleep(20)
//            println("------here 2---")
//        }
//
//
//        println("------RUN1---")
//        var t = Thread(run)
//        t.start()
//
//        println("------RUN2---")
//
//        println("------WORK 1---")
//        TimeUnit.SECONDS.sleep(10)
//        println("------WORK 2---")
//        t.join()
//        println("------All  done---")
//    }


    @Test
    fun getMetaData() {
        var exec = Executors.newCachedThreadPool();
        var future: Future<Int> = exec.submit<Int> {
            println("------here 1---")
            TimeUnit.SECONDS.sleep(10)
            println("------here 2---")
            10

        }

        println("------WORK 1---")
        TimeUnit.SECONDS.sleep(20)
        println("------WORK 2---")
        var z = future.get()

        println("------All  done---")
        println("------All  $z---")


    }

}
