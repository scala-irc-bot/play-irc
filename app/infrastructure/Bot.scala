package net.mtgto.infrastructure

import java.util.UUID

case class Bot(id: UUID, name: String, filename: String, config: Option[String], enabled: Boolean)
