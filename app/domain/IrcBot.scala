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
      client.map {
        client => if (client.isConnected) client.disconnect
      }
      client = Some(new DefaultIrcClient(
        new IrcConfig {
          override val hostname = domainClient.hostname
          override val port = domainClient.port
          override val password = domainClient.password
          override val encoding = domainClient.encoding
          override val delay = domainClient.delay
          override val nickname = domainClient.nickname
          override val username = domainClient.username
          override val realname = domainClient.realname
          override val channels = domainChannels.map(_.name).toArray
          override val bots = domainBots.map(bot => (bot.name -> Option.empty[BotConfig])).toArray
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
