package knbit.rsintegration.bc.amqp

import java.util

import akka.actor.{ActorRef, ActorSystem}
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.DefaultConsumer
import com.thenewmotion.akka.rabbitmq._
import knbit.rsintegration.bc.common.Reservation
import knbit.rsintegration.bc.scheduling.ActorFactory

object AMQP {

  val exchange = "amq.direct"
  val queue_in = "rs.integration.in"
  val queue_out = "rs.integration.out"
  
  def setupSubscriber(factory: ActorFactory)(channel: Channel, self: ActorRef) {
    channel.queueDeclare(queue_in, false, false, false, null)
    channel.queueBind(queue_in, exchange, queue_in)
    val consumer = new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]) {
        val reservation = parse[Reservation](body)
        factory.createRequest(reservation)
      }
    }
    channel.basicConsume(queue_in, true, consumer)
  }

  def setupPublisher(channel: Channel, self: ActorRef) {
    channel.queueDeclare(queue_out, false, false, false, null)
    channel.queueBind(queue_out, exchange, queue_out)
  }

  def publish(message: AnyRef, messageType: String)(channel: Channel) {
    channel.basicPublish(exchange, queue_out, propsWithHeader("type", messageType), bytes(message))
  }

  private[this] def propsWithHeader(key: String, value: String): BasicProperties = {
    val map = new util.HashMap[String, Object]()
    map.put(key, value)
    new BasicProperties()
      .builder.headers(map).build
  }

}
