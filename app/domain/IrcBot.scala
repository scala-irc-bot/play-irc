package net.mtgto.domain

import net.mtgto.irc.{Client => IrcClient, DefaultClient => DefaultIrcClient, Config => IrcConfig}
import net.mtgto.irc.config.BotConfig

trait IrcBot {
  def connect(client: Client, channels: Seq[Channel], bots: Seq[Bot])

  def disconnect

  def isConnected: Boolean
}

object IrcBot {
  def apply(): IrcBot = new IrcBot {
    private var client: Option[IrcClient] = None

    override def connect(domainClient: Client, domainChannels: Seq[Channel], domainBots: Seq[Bot]) = {
      client.map(_.disconnect)
      client = Some(new DefaultIrcClient(
        new IrcConfig {
          val hostname = domainClient.hostname
          val port = domainClient.port
          val password = domainClient.password
          val encoding = domainClient.encoding
          val delay = domainClient.delay
          val nickname = domainClient.nickname
          val username = domainClient.username
          val realname = domainClient.realname
          val channels = domainChannels.map(_.name).toArray
          val bots = domainBots.map(bot => (bot.name -> Option.empty[BotConfig])).toArray
        }
      ))
      client.map(_.connect)
    }

    override def disconnect = {
      client.map(_.disconnect)
    }

    override def isConnected = {
      client.map(_.isConnected).getOrElse(false)
    }
  }
}
