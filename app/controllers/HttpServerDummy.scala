package controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.io.StdIn


object HttpServerDummy extends App {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val route = post {
        entity(as[String]) { request => {
            println(s"request received with $request")
            complete(HttpResponse(200, entity = request));
        }}
    }


    val interface = "localhost"
    val port = 8080

    val binding = Http().bindAndHandle(route, interface, port)
    println(s"Server is now online at http://$interface:$port\nPress RETURN to stop...")
    StdIn.readLine()

    binding.flatMap(_.unbind()).onComplete(_ => system.terminate())
    println("Server is down...")
}
