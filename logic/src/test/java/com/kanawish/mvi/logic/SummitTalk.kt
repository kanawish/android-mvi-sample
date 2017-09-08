package com.kanawish.mvi.logic

import java.net.ResponseCache
import kotlin.properties.Delegates

/**
 *
 */


// Delegation

fun localDelegatedProps() {

}

class Veto {
    var value: String by Delegates.vetoable("String") {
        prop, old, new -> new.startsWith("S")
    }
}

class CustomDelegate { // ...
}

class FooClass {
    var name : String = ""
    operator fun invoke(name:String) {
        println("Hello $name")
    }
}

/*
fun main(args:Array<String>) {
    val foo = FooClass()
    foo ("Something")
    println(foo.name)
}
*/

class Something() {

}

fun get(path:String, f: RouteHandler.()->Unit) {

}

class Req(val accept:String, val isSecure:Boolean)
class Resp (var content:String, var contentType:String) {
    operator fun invoke(function: () -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
class RouteHandler(val req:Req, val resp:Resp)

fun main(args: Array<String>) {
    get("/customer") {
        if(req.isSecure) {
            resp { "PASSWORD!" } // err... ??
        }
    }
}

sealed class Result
class Success(val content:String):Result()
class Error(val msg:String):Result()

fun getPage(url:String):Result {
    if(url != "") {
        return Success("woo!")
    } else {
        return Error("boo!")
    }
}

